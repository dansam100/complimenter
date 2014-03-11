package com.complimenter.ecg;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.complimenter.ecg.layout.ImageFlipper;
import com.complimenter.ecg.layout.OnShareClickEventProvider;

import java.io.File;
import java.io.FileOutputStream;

public class ECG extends Activity implements ImageFlipper.ImageFlipperListener
{
    private boolean mShareMode = false;
    private Vibrator mVibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ecg);
        mVibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
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
        OnShareClickEventProvider shareProvider = (OnShareClickEventProvider)flipper;
        if(shareProvider != null) {
            flipper.setOnShareClickedEventListener(this);
        }
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

    @Override
    public void onShareActivated(View view) {
        vibrate();
        this.mShareMode = true;
    }

    @Override
    public void onShareClicked(View view, Bitmap image){
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.share_text));
        File sharedImage = new File(this.getExternalCacheDir(), "shared_img.jpg");
        try{
            FileOutputStream stream = new FileOutputStream(sharedImage);
            image.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            stream.flush();
            stream.close();
        }
        catch(Exception e){
            Log.e("FAILED", "Failed to create file");
        }
        Log.d("SHARING", "Sharing as: " + Uri.fromFile(sharedImage));
        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(sharedImage));
        shareIntent.setType("image/jpeg");
        startActivity(Intent.createChooser(shareIntent, getResources().getString(R.string.share_via)));
    }

    @Override
    public void onBackPressed()
    {
        if(this.mShareMode){
            vibrate();
            this.mShareMode = false;
            ImageFlipper imageFlipper = (ImageFlipper)findViewById(R.id.container);
            imageFlipper.onShareDeactivated(this);
        }
        else{
            super.onBackPressed();
        }
    }

    public void vibrate(){
        long[] pattern = {0, 100};
        mVibrator.vibrate(pattern, -1);
    }
}
