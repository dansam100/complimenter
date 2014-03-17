package com.complimenter.ecg;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
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
        //fade out the current view
        final View container = this.findViewById(R.id.container);
        final AnimatorSet fadeOut = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.ecg_fade_out);
        fadeOut.setTarget(container);
        Handler startActivity = new Handler();
        fadeOut.addListener(
            new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    Intent intent = new Intent(ECGLoader.this, ECG.class);
                    startActivity(intent);
                    finish();
                }
            }
        );
        //launch the main activity
        startActivity.postDelayed(
            new Runnable() {
                @Override
                public void run() {
                    fadeOut.start();
                }
            }, 1500
        );
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        return super.onTouchEvent(event);
    }
}
