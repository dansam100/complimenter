package com.complimenter.ecg;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import com.complimenter.ecg.fragment.SharingFragment;
import com.complimenter.ecg.layout.ImageFlipper;

public class ECG extends Activity implements SharingFragment.OnSharingFragmentActivatedListener
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

    /** Defines a default (dummy) share intent to initialize the action provider.
     * However, as soon as the actual content to be used in the intent
     * is known or changes, you must update the share intent by again calling
     * mShareActionProvider.setShareIntent()
     */
    private Intent getDefaultShareIntent() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/*");
        intent.putExtra(intent.EXTRA_TEXT, getResources().getString(R.string.share_text));
        return intent;
    }

    public void beginSharing()
    {
        Intent share = this.getDefaultShareIntent();
        //share.putExtra(share.EXTRA_STREAM, "");
        startActivity(Intent.createChooser(share, "Share Image"));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        return super.onTouchEvent(event);
    }
}
