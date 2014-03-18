package com.complimenter.ecg;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.complimenter.ecg.layout.ImageFlipper;
import com.complimenter.ecg.layout.ImageFlipperEventProvider;

import java.io.File;
import java.io.FileOutputStream;

public class ECG extends Activity implements ImageFlipper.ImageFlipperListener, MediaScannerConnection.MediaScannerConnectionClient
{
    private MediaScannerConnection mMediaScanner;
    private File mFavoritedImageFile;
    private ImageFlipper mFlipper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ecg);
        mFavoritedImageFile = null;
        //find views
        final View controlsView = findViewById(R.id.ok_button);
        mFlipper = (ImageFlipper)findViewById(R.id.container);

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
        ImageFlipperEventProvider shareProvider = (ImageFlipperEventProvider)mFlipper;
        if(shareProvider != null) {
            mFlipper.setOnShareClickedEventListener(this);
            mFlipper.setOnFavoriteClickedEventListener(this);
            mFlipper.setOnSelectionChangedEventListener(this);
        }
        mFlipper.setupFlipper();
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
    public void onShareClicked(View view, Bitmap image){
        if(image != null) {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.share_text));
            File sharedImage = new File(this.getExternalCacheDir(), "shared_img.jpg");
            try {
                FileOutputStream stream = new FileOutputStream(sharedImage);
                image.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                stream.flush();
                stream.close();
            } catch (Exception e) {
                Log.e("FAILED", "Failed to create file");
            }
            Log.d("SHARING", "Sharing as: " + Uri.fromFile(sharedImage));
            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(sharedImage));
            shareIntent.setType("image/jpeg");
            startActivity(Intent.createChooser(shareIntent, getResources().getString(R.string.share_via)));
        }
    }

    @Override
    public void onFavoriteClicked(View view, Bitmap image, String imageName, String text){
        if(image != null) {
            String fileName = imageName + text.hashCode() + ".jpg";
            mFavoritedImageFile = new File(this.getFilesDir(), fileName);
            if (mFavoritedImageFile.exists()) {
                mFavoritedImageFile.delete();
                Log.d("FAVORITE", "Removing favorite: " + Uri.fromFile(mFavoritedImageFile));
                Toast.makeText(this, getResources().getString(R.string.unfavorited), 1000).show();
            } else {
                try {
                    FileOutputStream stream = new FileOutputStream(mFavoritedImageFile);
                    image.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    stream.flush();
                    stream.close();
                } catch (Exception e) {
                    Log.e("FAILED", "Failed to create file");
                }
                Log.d("FAVORITE", "Favoriting: " + Uri.fromFile(mFavoritedImageFile));
                Toast.makeText(this, getResources().getString(R.string.favorited), 1000).show();
            }
            //start media scannner
            mMediaScanner = new MediaScannerConnection(this, this);
            mMediaScanner.connect();
            //favorite/de-favorite file
            mFlipper.setFavorite(mFavoritedImageFile.exists());
        }
    }

    @Override
    public void onNavigate(View view, String imageName, String text){
        String fileName = imageName + text.hashCode() + ".jpg";
        File favoritedImage = new File(this.getFilesDir(), fileName);
        mFlipper.setFavorite(favoritedImage.exists());
    }

    @Override
    public void onMediaScannerConnected() {
        if(mFavoritedImageFile != null) {
            try {
                mMediaScanner.scanFile(mFavoritedImageFile.getPath(), "image/jpeg");
            }
            catch(Exception e){
                Log.d("SCANNER", "Media scanned failed" + e);
            }
        }
    }

    @Override
    public void onScanCompleted(String s, Uri uri) {
        mMediaScanner.disconnect();
        Toast.makeText(this, getResources().getString(R.string.favorited_image_scanned), 2000).show();
    }
}
