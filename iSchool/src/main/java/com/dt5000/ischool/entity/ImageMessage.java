package com.dt5000.ischool.entity;

import android.graphics.Bitmap;

/**
 * Created by weimy on 2017/11/30.
 */

public class ImageMessage {
    private Bitmap bitmap;

    public ImageMessage(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}
