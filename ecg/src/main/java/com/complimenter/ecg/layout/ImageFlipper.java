package com.complimenter.ecg.layout;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
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
public class ImageFlipper extends RelativeLayout implements OnShareClickEventProvider {
    private GestureDetectorCompat mDetector;
    private Animation mSlideLeft;
    private Animation mSlideRight;
    private Bitmap mImageShare;

    private ImageFlipperListener mOnShareClickedListener;

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
    }

    public void loadNext(){
        this.loadImage(getResources().getStringArray(R.array.images)[index]);
        this.loadText(getResources().getStringArray(R.array.texts)[index]);
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
                if(index >= getResources().getInteger(R.integer.max_images)) {
                    index = 0;
                }
            }
            else{
                index--;
                if(index < 0) {
                    index = getResources().getInteger(R.integer.max_images)-1;
                }
            }
            if(getResources().getBoolean(R.bool.animation)){
                animateFling(velocityX > 0);
            }
            else{
                loadNext();
            }
            return true;
        }

        @Override
        public void onLongPress(MotionEvent event) {
            Log.d(DEBUG_TAG, "onLongPress: " + event.toString());
            onShareActivated(ImageFlipper.this);
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
                    mOnShareClickedListener.onShareClicked(view, mImageShare);
                }
            }
        );
    }

    @Override
    public void onShareActivated(View view){
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
                    mImageShare = BitmapFactory.decodeByteArray(stream.toByteArray(), 0, stream.size());
                }
                mOnShareClickedListener.onShareActivated(ImageFlipper.this);
            }
        }
        catch(Exception e){}
        //show the share icon
        View shareImg = findViewById(R.id.image_share);
        shareImg.setVisibility(View.VISIBLE);
    }

    public void onShareDeactivated(Context context){
        //hide the share icon
        View shareImg = findViewById(R.id.image_share);
        shareImg.setVisibility(View.GONE);

        //show the button
        View button = findViewById(R.id.ok_button);
        button.setVisibility(View.VISIBLE);

        //dispose of the screen shot
        mImageShare = null;
    }

    public interface ImageFlipperListener{
        public void onShareActivated(View view);
        public void onShareClicked(View view, Bitmap bitmap);
    }
}