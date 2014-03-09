package com.complimenter.ecg.layout;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
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

/**
 * Created by sam.jr on 3/2/14.
 */
public class ImageFlipper extends RelativeLayout {
    private GestureDetectorCompat mDetector;
    private View view;

    private int index = 0;
    public ImageFlipper(Context context, AttributeSet attrs){
        super(context, attrs);
        this.view = this;
        mDetector = new GestureDetectorCompat(this.getContext(), new ImageFlipperGestureListener());
    }

    public void load(){
        this.loadImage(getResources().getStringArray(R.array.images)[index]);
        this.loadText(getResources().getStringArray(R.array.texts)[index]);
    }

    private void animateFling(boolean left)
    {
        if(left){
            Log.d("TEST", "fling: left");
        }
        else{
            Log.d("TEST", "fling: right");
        }
        Animation slide = (Animation) AnimationUtils.loadAnimation(this.getContext(), left ? R.anim.ecg_slide_left_right : R.anim.ecg_slide_right_left);
        slide.setAnimationListener(
            new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    loadImage(getResources().getStringArray(R.array.images)[index]);
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    loadText(getResources().getStringArray(R.array.texts)[index]);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            }
        );
        this.setAnimation(slide);
        slide.start();
    }

    private void loadImage(String imageName){
        view.setBackgroundResource(getResources().getIdentifier(imageName, "drawable", getContext().getPackageName()));
    }

    private void loadText(String text){
        TextView motivation = (TextView)view.findViewById(R.id.text_compliment);
        motivation.setText(text);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        mDetector.onTouchEvent(event);
        super.onTouchEvent(event);
        return true;
    }

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
                load();
            }
            return true;
        }

        @Override
        public void onLongPress(MotionEvent event) {
            Log.d(DEBUG_TAG, "onLongPress: " + event.toString());
        }

        @Override
        public boolean onDoubleTap(MotionEvent event) {
            Log.d(DEBUG_TAG, "onDoubleTap: " + event.toString());
            return true;
        }
    }
}
