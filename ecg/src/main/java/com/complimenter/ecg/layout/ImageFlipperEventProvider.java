package com.complimenter.ecg.layout;

import android.content.Context;
import android.view.View;

public interface ImageFlipperEventProvider {
    public void onSelectionDeactivated(Context context);
    public void setOnShareClickedEventListener(ImageFlipper.ImageFlipperListener listener);
    public void setOnFavoriteClickedEventListener(ImageFlipper.ImageFlipperListener listener);
    public void setOnSelectionChangedEventListener(ImageFlipper.ImageFlipperListener listener);
    public void onSelectionActivated(View view);
}
