package com.ecg.complimenter;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.io.File;

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
        Thread loader = new Thread(
            new Runnable() {
                @Override
                public void run() {
                    performLoad();
                }
            }
        );
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
        //load stuff
        loader.run();
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

    private void performLoad()
    {
        File favesFolder = new File(Environment.getExternalStoragePublicDirectory(getString(R.string.app_name)),
                getString(R.string.favorites_folder));
        if(!favesFolder.exists()){
            if(!favesFolder.mkdirs()){
                Log.d("FAVORITES:", "Unable to create favorites folder");
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        return super.onTouchEvent(event);
    }
}
