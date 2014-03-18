package com.complimenter.ecg.layout;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.os.Handler;
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

    private boolean mMenuVisible = false;

    private ImageFlipperListener mOnShareClickedListener;
    private ImageFlipperListener mOnFavoriteClickedListener;
    private ImageFlipperListener mOnNavigateEventListener;

    private Handler mMenuHandler;
    private Runnable mMenuCallback;

    private int index = 0;
    public ImageFlipper(Context context, AttributeSet attrs){
        super(context, attrs);

        this.mSlideLeft = AnimationUtils.loadAnimation(this.getContext(), R.anim.ecg_slide_left_right);
        this.mSlideRight = AnimationUtils.loadAnimation(this.getContext(), R.anim.ecg_slide_right_left);
        this.mSlideLeft.setAnimationListener(mSlideAnimationListener);
        this.mSlideRight.setAnimationListener(mSlideAnimationListener);

        this.mMenuHandler = new Handler();

        mDetector = new GestureDetectorCompat(this.getContext(), new ImageFlipperGestureListener());
    }

    public void setupFlipper(){
        this.loadNext();
        this.toggleMenu(false);
    }

    public void toggleMenu(boolean show){
        if(show){
            this.showView(findViewById(R.id.flipper_menu), true);
        }
        else{
            this.hideView(findViewById(R.id.flipper_menu), true);
        }
    }

    private void toggleMenuQuick(boolean show) {
        if(show){
            this.showView(findViewById(R.id.flipper_menu), false);
        }
        else{
            this.hideView(findViewById(R.id.flipper_menu), false);
        }
    }

    public void hideView(final View view, boolean animate){
        if(animate) {
            final AnimatorSet fadeOut = (AnimatorSet) AnimatorInflater.loadAnimator(this.getContext(), R.animator.ecg_hide_share);
            fadeOut.setTarget(view);
            fadeOut.addListener(
                    new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animator) {
                            view.setVisibility(View.GONE);
                        }
                    }
            );
            fadeOut.start();
        }
        else{
            view.setVisibility(View.GONE);
        }
        mMenuVisible = false;
    }

    public void showView(final View view, boolean animate){
        if(mMenuCallback != null){
            mMenuHandler.removeCallbacks(mMenuCallback);
        }
        if (mMenuVisible) {
            mMenuHandler.postDelayed(mMenuCallback = new Runnable() {
                @Override
                public void run() {
                    hideView(view, true);
                }
            }, 1000);
        } else{
            if(animate) {
                final AnimatorSet fadeIn = (AnimatorSet) AnimatorInflater.loadAnimator(this.getContext(), R.animator.ecg_show_share);
                fadeIn.setTarget(view);
                fadeIn.addListener(
                        new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animator) {
                                mMenuHandler.postDelayed(mMenuCallback = new Runnable() {
                                    @Override
                                    public void run() {
                                        hideView(view, true);
                                    }
                                }, 1000);
                            }
                        }
                );
                view.setVisibility(View.VISIBLE);
                fadeIn.start();
            }
            else{
                view.setVisibility(View.VISIBLE);
            }
            mMenuVisible = true;
        }
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
        if(this.mOnNavigateEventListener != null) {
            this.mOnNavigateEventListener.onNavigate(this, getCurrentImageName(), getCurrentText());
        }
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
        findViewById(R.id.image_favorite_small).setSelected(favorite);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        mDetector.onTouchEvent(event);
        super.onTouchEvent(event);
        return true;
    }

    private Animation.AnimationListener mSlideAnimationListener = new Animation.AnimationListener() {
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
        public boolean onSingleTapConfirmed(MotionEvent event){
            toggleMenu(true);
            return true;
        }

        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY) {
            Log.d(DEBUG_TAG, "onFling: " + event1.toString() + event2.toString());
            if(Math.abs(velocityX) > Math.abs(velocityY)) {
                if (velocityX < 0) {
                    index++;
                    if (index >= getResources().getInteger(R.integer.max_images)) {
                        index = 0;
                    }
                } else {
                    index--;
                    if (index < 0) {
                        index = getResources().getInteger(R.integer.max_images) - 1;
                    }
                }
                if (getResources().getBoolean(R.bool.animation)) {
                    animateFling(velocityX > 0);
                } else {
                    loadNext();
                }
            }
            return true;
        }
    }

    public Bitmap capture(){
        //get a screen shot
        Bitmap capture = null;
        //hide the button and top menu
        View button = findViewById(R.id.ok_button);
        button.setVisibility(View.GONE);    //hide views that are not required
        toggleMenuQuick(false);             //hide views that are not required
        try {
            View rootView = getRootView();
            rootView.setDrawingCacheEnabled(true);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            if (rootView.getDrawingCache().compress(Bitmap.CompressFormat.JPEG, 100, stream)) {
                capture = BitmapFactory.decodeByteArray(stream.toByteArray(), 0, stream.size());
            }
        }
        catch(Exception e){
            Log.d("FLIPPER", "Unable to activate share due to error:" + e);
        }
        button.setVisibility(View.VISIBLE);
        toggleMenuQuick(true);
        return capture;
    }

    @Override
    public void setOnShareClickedEventListener(ImageFlipperListener listener){
        this.mOnShareClickedListener = listener;
        this.findViewById(R.id.image_share_small).setOnClickListener(
            new OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOnShareClickedListener.onShareClicked(view, capture());
                }
            }
        );
    }

    @Override
    public void setOnFavoriteClickedEventListener(ImageFlipperListener listener){
        this.mOnFavoriteClickedListener = listener;
        this.findViewById(R.id.image_favorite_small).setOnClickListener(
            new OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOnFavoriteClickedListener.onFavoriteClicked(view, capture(), getCurrentImageName(), getCurrentText());
                }
            }
        );
    }

    @Override
    public void setOnSelectionChangedEventListener(ImageFlipperListener listener) {
        this.mOnNavigateEventListener = listener;
    }

    public interface ImageFlipperListener{
        public void onShareClicked(View view, Bitmap bitmap);
        public void onFavoriteClicked(View view, Bitmap bitmap, String name, String text);
        public void onNavigate(View view, String imageName, String text);
    }
}