package com.bcnetech.hyphoto.data.SqlControl;

import android.util.Size;

import com.bcnetech.bizcamerlibrary.camera.data.CameraStatus;

/**
 * Created by a1234 on 2017/12/1.
 */

public class CameraSettingData {
    boolean isSelect;
    CameraStatus.Size size;
    Size detailSize;
    int type;

    public CameraSettingData(boolean isSelect,CameraStatus.Size size,int type, Size detailSize) {
        this.isSelect = isSelect;
        this.size = size;
        this.type=type;
        this.detailSize = detailSize;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public CameraStatus.Size getSize() {
        return size;
    }

    public void setSize(CameraStatus.Size size) {
        this.size = size;
    }

    public Size getDetailSize() {
        return detailSize;
    }

    public void setDetailSize(Size detailSize) {
        this.detailSize = detailSize;
    }
}
