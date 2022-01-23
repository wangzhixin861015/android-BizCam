package com.bcnetech.bizcamerlibrary.camera.dao;

import android.util.Size;

import com.bcnetech.bizcamerlibrary.camera.data.CameraStatus;

/**
 * Created by a1234 on 2018/7/24.
 */

public class CameraSizeBean {
    private CameraSizeBase CameraSize;
    private CameraSizeBase RealCameraSize;

    private Size sizeLarge;
    private Size realsizeLarge;
    private Size sizeMiddle;
    private Size realsizeMiddle;
    private Size sizeSmall;
    private Size realsizeSmall;
    private Size selectSize;


    public Size getSizeLarge() {
        return sizeLarge;
    }

    public void setSizeLarge(Size sizeLarge) {
        this.sizeLarge = sizeLarge;
    }

    public Size getSizeMiddle() {
        return sizeMiddle;
    }

    public void setSizeMiddle(Size sizeMiddle) {
        this.sizeMiddle = sizeMiddle;
    }

    public Size getSizeSmall() {
        return sizeSmall;
    }

    public void setSizeSmall(Size sizeSmall) {
        this.sizeSmall = sizeSmall;
    }

    public Size getSelectSize() {
        return selectSize;
    }

    public void setSelectSize(Size selectSize) {
        this.selectSize = selectSize;
    }

    public Size getRealsizeLarge() {
        return realsizeLarge;
    }

    public void setRealsizeLarge(Size realsizeLarge) {
        this.realsizeLarge = realsizeLarge;
    }

    public Size getRealsizeMiddle() {
        return realsizeMiddle;
    }

    public void setRealsizeMiddle(Size realsizeMiddle) {
        this.realsizeMiddle = realsizeMiddle;
    }

    public Size getRealsizeSmall() {
        return realsizeSmall;
    }

    public void setRealsizeSmall(Size realsizeSmall) {
        this.realsizeSmall = realsizeSmall;
    }

    public Size getselectSize(boolean isRealSize, CameraStatus.Size size) {
        Size selectSize;
        if (isRealSize) {
            switch (size) {
                case LARGE:
                    selectSize = realsizeLarge;
                    break;
                case MIDDLE:
                    selectSize = realsizeMiddle;
                    break;
                default:
                    selectSize = realsizeSmall;
                    break;
            }

        } else {
            switch (size) {
                case LARGE:
                    selectSize = sizeLarge;
                    break;
                case MIDDLE:
                    selectSize = sizeMiddle;
                    break;
                default:
                    selectSize = sizeSmall;
                    break;
            }
        }
        return selectSize;
    }


    @Override
    public String toString() {
        return "CameraSizeBean{" +
                "sizeLarge=" + sizeLarge +
                ", realsizeLarge=" + realsizeLarge +
                ", sizeMiddle=" + sizeMiddle +
                ", realsizeMiddle=" + realsizeMiddle +
                ", sizeSmall=" + sizeSmall +
                ", realsizeSmall=" + realsizeSmall +
                ", selectSize=" + selectSize +
                '}';
    }
}
