package com.bcnetech.hyphoto.data;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/2/22.
 */
public class TemplaetPuzzlePicData implements Serializable {

    public String imageData;

    public String mineType;

    public String getImageData() {
        return imageData;
    }

    public void setImageData(String imageData) {
        this.imageData = imageData;
    }

    public String getMineType() {
        return mineType;
    }

    public void setMineType(String mineType) {
        this.mineType = mineType;
    }

}
