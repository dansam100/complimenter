package com.complimenter.ecg.layout;

import android.content.Context;
import android.view.View;

public interface OnShareClickEventProvider{
    public void onShareDeactivated(Context context);
    public void setOnShareClickedEventListener(ImageFlipper.ImageFlipperListener listener);
    public void onShareActivated(View view);
}
