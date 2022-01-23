package com.bcnetech.hyphoto.data;

import android.graphics.drawable.Drawable;

/**
 * Created by a1234 on 2017/12/4.
 */

public class WaterMarkData {
    public String watermarkurl;
    public Drawable drawable;

    public WaterMarkData(Drawable drawable, String watermarkurl) {
        this.drawable = drawable;
        this.watermarkurl = watermarkurl;
    }

    public String getWatermarkurl() {
        return watermarkurl;
    }

    public void setWatermarkurl(String watermarkurl) {
        this.watermarkurl = watermarkurl;
    }

    public Drawable getDrawable() {
        return drawable;
    }

    public void setDrawable(Drawable drawable) {
        this.drawable = drawable;
    }
}
