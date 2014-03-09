package com.complimenter.ecg;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

public class ECGLoader extends Activity
{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);
        //find text view
        final TextView textView = (TextView)findViewById(R.id.text_load_msg);
        AnimatorSet zoomAnimator = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.ecg_zoom_text);
        zoomAnimator.addListener(
            new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    Intent intent = new Intent(ECGLoader.this, ECG.class);
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            }

        );
        zoomAnimator.setTarget(textView);
        zoomAnimator.start();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        return super.onTouchEvent(event);
    }
}
