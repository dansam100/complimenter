package com.complimenter.ecg.layout;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.GestureDetector;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.content.Context;
import android.widget.TextView;

import com.complimenter.ecg.R;

import java.io.ByteArrayOutputStream;

/**
 * Created by sam.jr on 3/2/14.
 */
public class ImageFlipper extends RelativeLayout implements ImageFlipperEventProvider {
    private GestureDetectorCompat mDetector;
    private Animation mSlideLeft;
    private Animation mSlideRight;
    private Animation mIntegrate;
    private Animation mDisintegrate;
    private Bitmap mImageContext;

    private boolean mMenuVisible = false;

    private ImageFlipperListener mOnShareClickedListener;
    private ImageFlipperListener mOnFavoriteClickedListener;
    private ImageFlipperListener mOnSelectionChangedListener;

    private int index = 0;
    public ImageFlipper(Context context, AttributeSet attrs){
        super(context, attrs);

        this.mSlideLeft = AnimationUtils.loadAnimation(this.getContext(), R.anim.ecg_slide_left_right);
        this.mSlideRight = AnimationUtils.loadAnimation(this.getContext(), R.anim.ecg_slide_right_left);
        this.mSlideLeft.setAnimationListener(animationListener);
        this.mSlideRight.setAnimationListener(animationListener);

        mDetector = new GestureDetectorCompat(this.getContext(), new ImageFlipperGestureListener());
    }

    public void setupFlipper(){
        this.loadNext();
        this.toggleMenu(false);
    }

    public void toggleMenu(boolean show){
        if(show){
            this.hideView(findViewById(R.id.flipper_menu));
        }
        else{

        }
    }

    public void hideView(View view){

    }

    public void showView(View view){

    }

    private String getCurrentImageName(){
        return getResources().getStringArray(R.array.images)[index];
    }

    private String getCurrentText(){
        return getResources().getStringArray(R.array.texts)[index];
    }

    public void loadNext(){
        this.loadImage(getCurrentImageName());
        this.loadText(getCurrentText());
        this.mOnSelectionChangedListener.onSelectionChanged(this, getCurrentImageName(), getCurrentText());
    }

    private void animateFling(boolean left)
    {
        Animation slide = left ? mSlideLeft : mSlideRight;
        this.startAnimation(slide);
    }

    private void loadImage(String imageName){
        setBackgroundResource(getResources().getIdentifier(imageName, "drawable", getContext().getPackageName()));
    }

    private void loadText(String text){
        TextView motivation = (TextView)findViewById(R.id.text_compliment);
        motivation.setText(text);
    }

    public void setFavorite(boolean favorite){
        findViewById(R.id.image_favorite).setSelected(favorite);
        findViewById(R.id.image_favorite_small).setSelected(favorite);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        mDetector.onTouchEvent(event);
        super.onTouchEvent(event);
        return true;
    }

    Animation.AnimationListener animationListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) { loadNext(); }
        @Override
        public void onAnimationEnd(Animation animation) {}
        @Override
        public void onAnimationRepeat(Animation animation) {}
    };

    Animation.AnimationListener mIntegrateListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) { loadNext(); }
        @Override
        public void onAnimationEnd(Animation animation) {}
        @Override
        public void onAnimationRepeat(Animation animation) {}
    };

    class ImageFlipperGestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final String DEBUG_TAG = "Swipe";
        @Override
        public boolean onDown(MotionEvent event){
            Log.d(DEBUG_TAG, "onDown: " + event.toString());
            return true;
        }

        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY) {
            Log.d(DEBUG_TAG, "onFling: " + event1.toString() + event2.toString());
            if(velocityX < 0){
                index++;
                if(index >= getResources().getInteger(R.integer.max_images)) { index = 0; }
            }
            else{
                index--;
                if(index < 0) { index = getResources().getInteger(R.integer.max_images)-1; }
            }
            if(getResources().getBoolean(R.bool.animation)){ animateFling(velocityX > 0); }
            else{ loadNext(); }
            return true;
        }

        @Override
        public void onLongPress(MotionEvent event) {
            Log.d(DEBUG_TAG, "onLongPress: " + event.toString());
            onSelectionActivated(ImageFlipper.this);
        }

        @Override
        public boolean onDoubleTap(MotionEvent event) {
            Log.d(DEBUG_TAG, "onDoubleTap: " + event.toString());
            return true;
        }
    }

    @Override
    public void setOnShareClickedEventListener(ImageFlipperListener listener){
        this.mOnShareClickedListener = listener;
        this.findViewById(R.id.image_share).setOnClickListener(
            new OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOnShareClickedListener.onShareClicked(view, mImageContext);
                }
            }
        );
        this.findViewById(R.id.image_share_small).setOnClickListener(
            new OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOnShareClickedListener.onShareClicked(view, mImageContext);
                }
            }
        );
    }

    @Override
    public void setOnFavoriteClickedEventListener(ImageFlipperListener listener){
        this.mOnFavoriteClickedListener = listener;
        this.findViewById(R.id.image_favorite).setOnClickListener(
            new OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOnFavoriteClickedListener.onFavoriteClicked(view, mImageContext, getCurrentImageName(), getCurrentText());
                }
            }
        );
        this.findViewById(R.id.image_favorite_small).setOnClickListener(
            new OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOnFavoriteClickedListener.onFavoriteClicked(view, mImageContext, getCurrentImageName(), getCurrentText());
                }
            }
        );
    }

    @Override
    public void setOnSelectionChangedEventListener(ImageFlipperListener listener) {
        this.mOnSelectionChangedListener = listener;
    }

    @Override
    public void onSelectionActivated(View view){
        //hide the button
        View button = findViewById(R.id.ok_button);
        button.setVisibility(View.GONE);
        //get a screen shot
        try {
            if (mOnShareClickedListener != null) {
                View rootView = getRootView();
                rootView.setDrawingCacheEnabled(true);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                if (rootView.getDrawingCache().compress(Bitmap.CompressFormat.JPEG, 100, stream)) {
                    mImageContext = BitmapFactory.decodeByteArray(stream.toByteArray(), 0, stream.size());
                }
                mOnShareClickedListener.onSelectionActivated(ImageFlipper.this);
            }
        }
        catch(Exception e){
            Log.d("FLIPPER", "Unable to activate share due to error:" + e);
        }
        //show the share icon
        View shareImg = findViewById(R.id.flipper_options);
        shareImg.setVisibility(View.VISIBLE);
    }

    @Override
    public void onSelectionDeactivated(Context context){
        //hide the share icon
        View shareImg = findViewById(R.id.flipper_options);
        shareImg.setVisibility(View.GONE);

        //show the button
        View button = findViewById(R.id.ok_button);
        button.setVisibility(View.VISIBLE);

        //dispose of the screen shot
        mImageContext = null;
    }

    public interface ImageFlipperListener{
        public void onSelectionActivated(View view);
        public void onShareClicked(View view, Bitmap bitmap);
        public void onFavoriteClicked(View view, Bitmap bitmap, String name, String text);
        public void onSelectionChanged(View view, String imageName, String text);
    }
}