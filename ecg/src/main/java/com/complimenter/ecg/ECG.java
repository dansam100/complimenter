package com.complimenter.ecg;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.complimenter.ecg.layout.ImageFlipper;
import com.complimenter.ecg.layout.ImageFlipperEventProvider;

import java.io.File;
import java.io.FileOutputStream;

public class ECG extends Activity implements ImageFlipper.ImageFlipperListener
{
    private File mFavoritesFolder;
    private ImageFlipper mFlipper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ecg);
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

        //setup folders
        this.mFavoritesFolder = new File(Environment.getExternalStoragePublicDirectory(getString(R.string.app_name)), getString(R.string.favorites_folder));

        //initiate load
        ImageFlipperEventProvider shareProvider = mFlipper;
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
        if(!mFavoritesFolder.exists()){
            if(!mFavoritesFolder.mkdirs()){
                Log.d("FAVORITES:", "Unable to create favorites folder");
            }
        }
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
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_subject));
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
            File favoritedImage = new File(mFavoritesFolder, fileName);
            if (favoritedImage.exists()) {
                if(favoritedImage.delete()) {
                    Log.d("FAVORITE", "Removing favorite: " + Uri.fromFile(favoritedImage));
                }
                else{ Log.d("FAVORITE", "Failed to remove favorite: " + Uri.fromFile(favoritedImage)); }
                //start media scannner
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.fromFile(favoritedImage)));
                Toast.makeText(this, getResources().getString(R.string.unfavorited), Toast.LENGTH_SHORT).show();
            }
            else {
                try {
                    FileOutputStream stream = new FileOutputStream(favoritedImage);
                    image.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    stream.flush();
                    stream.close();
                }
                catch (Exception e) { Log.e("FAILED", "Failed to create file"); }
                Log.d("FAVORITE", "Favoriting: " + Uri.fromFile(favoritedImage));
                //start media scannner
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.fromFile(favoritedImage)));
                Toast.makeText(this, getResources().getString(R.string.favorited), Toast.LENGTH_SHORT).show();
                //display favorited string
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ECG.this, getResources().getString(R.string.favorited_image_scanned), Toast.LENGTH_SHORT).show();
                    }
                }, Toast.LENGTH_SHORT);
            }
            //favorite/de-favorite file
            mFlipper.setFavorite(favoritedImage.exists());
        }
    }

    @Override
    public void onNavigate(View view, String imageName, String text){
        String fileName = imageName + text.hashCode() + ".jpg";
        File favoritedImage = new File(mFavoritesFolder, fileName);
        mFlipper.setFavorite(favoritedImage.exists());
    }
}
