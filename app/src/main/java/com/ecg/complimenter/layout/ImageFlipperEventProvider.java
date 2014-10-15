package com.ecg.complimenter.layout;

public interface ImageFlipperEventProvider {
    public void setOnShareClickedEventListener(ImageFlipper.ImageFlipperListener listener);
    public void setOnFavoriteClickedEventListener(ImageFlipper.ImageFlipperListener listener);
    public void setOnSelectionChangedEventListener(ImageFlipper.ImageFlipperListener listener);
}
