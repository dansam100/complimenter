package com.complimenter.ecg;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import com.complimenter.ecg.layout.ImageFlipper;

public class ECG extends Activity
{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ecg);
        //find views
        final View controlsView = findViewById(R.id.ok_button);
        final ImageFlipper flipper = (ImageFlipper)findViewById(R.id.container);

        //set listener for close event
        controlsView.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ECG.this.finish();
                }
            }
        );

        //initiate load
        flipper.setupFlipper();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        //TODO: Set up the image and compliment loaders
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        return super.onTouchEvent(event);
    }
}
