package com.ecg.complimenter;

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
import com.ecg.complimenter.layout.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;

public class ECG extends Activity implements ImageFlipper.ImageFlipperListener
{
    private File mRootFolder;
    private File mLogsFolder;
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

        Thread.setDefaultUncaughtExceptionHandler(
            new Thread.UncaughtExceptionHandler() {
                @Override
                public void uncaughtException(Thread thread, Throwable throwable) {
                logToFile("UnCaught", "Exception on thread: " + thread + throwable);
                }
            }
        );

        //setup folders
        this.mRootFolder = Environment.getExternalStoragePublicDirectory(getString(R.string.app_name));
        this.mFavoritesFolder = new File(mRootFolder, getString(R.string.favorites_folder));
        this.mLogsFolder = new File(mRootFolder, getString(R.string.logs_folder));

        //initiate load
        ImageFlipperEventProvider shareProvider = mFlipper;
        if(shareProvider != null) {
            shareProvider.setOnShareClickedEventListener(this);
            shareProvider.setOnFavoriteClickedEventListener(this);
            shareProvider.setOnSelectionChangedEventListener(this);
        }
        mFlipper.setupFlipper();
    }

    public void logToFile(String tag, String exception){
        try {
            File logFile = new File(mLogsFolder, "log.txt");
            if (!logFile.exists()) {
                if (!logFile.mkdirs()) {
                    Log.d("LOGS", "Unable to create log folder");
                }
            }
            new PrintWriter(logFile).print(String.format("%s: %s", tag, exception));
        }
        catch(Exception e){
            Log.d("LOGS", "Unable to log to file");
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if(!mRootFolder.exists()){
            if(!mRootFolder.mkdirs()){
                Log.d("ROOT_FOLDER:", "Unable to create root folder");
            }
        }
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
                try {
                    if (favoritedImage.delete()) {
                        Log.d("FAVORITE", "Removing favorite: " + Uri.fromFile(favoritedImage));
                    } else {
                        Log.d("FAVORITE", "Failed to remove favorite: " + Uri.fromFile(favoritedImage));
                    }
                    //start media scanner
                    sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.fromFile(favoritedImage)));
                    Toast.makeText(this, getResources().getString(R.string.unfavorited), Toast.LENGTH_SHORT).show();
                }
                catch (Exception e){
                    Log.d("FAVORITE", "Unable to remove favorites: " + Uri.fromFile(favoritedImage) + e);
                }
            }
            else {
                try {
                    FileOutputStream stream = new FileOutputStream(favoritedImage);
                    image.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    stream.flush();
                    stream.close();
                    sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.fromFile(favoritedImage)));
                }
                catch (Exception e) { Log.e("FAILED", "Failed to create file" + e); }
                Log.d("FAVORITE", "Favoriting: " + Uri.fromFile(favoritedImage));
                //start media scannner
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
