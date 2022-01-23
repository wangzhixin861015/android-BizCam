package com.bcnetech.bcnetechlibrary.blurkit;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;

import java.util.concurrent.Callable;

/**
 * Created by a1234 on 2018/8/3.
 */

public class BlurCallable implements Callable<Drawable> {
    private Bitmap bitmap;
    private static final int TARGETMIN = 300;//图片短边缩放至长度
    private int radius = 6;//模糊程度;

    public BlurCallable(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    @Override
    public Drawable call() throws Exception {
        return applyBlur();
    }

    /***
     * 高斯模糊背景
     */
    private Drawable applyBlur() throws Exception{
        return blur(bitmap);
    }

    public void setRadius(int radius){
        this.radius = radius;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private Drawable blur(Bitmap bkg) {
        int newWidth, newHeight;
        int min = Math.min(bkg.getWidth(), bkg.getHeight());
        if (min == bkg.getWidth()) {
            newWidth = TARGETMIN;
            newHeight =(int) ((float)(TARGETMIN / (float)bkg.getWidth()) * (float) bkg.getHeight());
        } else if (min == bkg.getHeight()) {
            newHeight = TARGETMIN;
            newWidth = (int)((float)(TARGETMIN / (float)bkg.getHeight()) *(float) bkg.getWidth());
        } else {
            newWidth = newHeight = TARGETMIN;
        }
        Bitmap inputBitmap = Bitmap.createScaledBitmap(bkg, newWidth, newHeight, false);
        bkg.recycle();
        inputBitmap = BlurKit.getInstance().blur(inputBitmap, (int) radius);
        Drawable drawable = new BitmapDrawable(inputBitmap);
        return drawable;
    }
}
