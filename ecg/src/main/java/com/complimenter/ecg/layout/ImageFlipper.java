package com.complimenter.ecg.layout;

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

import com.complimenter.ecg.ECG;
import com.complimenter.ecg.R;

/**
 * Created by sam.jr on 3/2/14.
 */
public class ImageFlipper extends RelativeLayout {
    private GestureDetectorCompat mDetector;
    private Animation slideLeft;
    private Animation slideRight;

    private int index = 0;
    public ImageFlipper(Context context, AttributeSet attrs){
        super(context, attrs);

        this.slideLeft = AnimationUtils.loadAnimation(this.getContext(), R.anim.ecg_slide_left_right);
        this.slideRight = AnimationUtils.loadAnimation(this.getContext(), R.anim.ecg_slide_right_left);
        this.slideLeft.setAnimationListener(animationListener);
        this.slideRight.setAnimationListener(animationListener);

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
        Animation slide = left ? slideLeft : slideRight;
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
        public void onAnimationStart(Animation animation) {
            loadImage(getResources().getStringArray(R.array.images)[index]);
            loadText(getResources().getStringArray(R.array.texts)[index]);
        }

        @Override
        public void onAnimationEnd(Animation animation) {
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
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
            final ECG context = (ECG)getContext();
            /*final ImageView imageView = (ImageView)findViewById(R.id.image_share);
            imageView.setVisibility(View.VISIBLE);
            AnimatorSet shareAnimator = (AnimatorSet) AnimatorInflater.loadAnimator(context, R.animator.ecg_show_share);
            shareAnimator.setTarget(imageView);
            shareAnimator.start();*/

            //context.getFragmentManager().beginTransaction().add(new ShareFragment(), "share").addToBackStack(null).commit();
        }

        @Override
        public boolean onDoubleTap(MotionEvent event) {
            Log.d(DEBUG_TAG, "onDoubleTap: " + event.toString());
            return true;
        }
    }
}
