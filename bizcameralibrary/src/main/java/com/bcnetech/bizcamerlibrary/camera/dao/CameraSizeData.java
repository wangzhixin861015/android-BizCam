package com.bcnetech.bizcamerlibrary.camera.dao;


import com.bcnetech.bcnetchhttp.config.Flag;
import com.bcnetech.bizcamerlibrary.camera.data.CameraStatus;

/**
 * Created by a1234 on 2018/7/24.
 */

public class CameraSizeData {
    private CameraStatus.Size SizeRatio;
    private CameraSizeBean pictureSizeBean = new CameraSizeBean();
    private CameraSizeBean videoSizeBean = new CameraSizeBean();

    public CameraSizeData(CameraStatus.Size sizeRatio) {
        SizeRatio = sizeRatio;
    }

    public CameraStatus.Size getSizeRatio() {
        return SizeRatio;
    }

    public void setSizeRatio(CameraStatus.Size sizeRatio) {
        SizeRatio = sizeRatio;
    }

    public CameraSizeBean getPictureSizeBean() {
        return pictureSizeBean;
    }

    public void setPictureSizeBean(CameraSizeBean pictureSizeBean) {
        this.pictureSizeBean = pictureSizeBean;
    }

    public CameraSizeBean getVideoSizeBean() {
        return videoSizeBean;
    }

    public void setVideoSizeBean(CameraSizeBean videoSizeBean) {
        this.videoSizeBean = videoSizeBean;
    }

    public CameraSizeBean getCameraSizeBean(int type){
        if (type == Flag.TYPE_PIC){
            return pictureSizeBean;
        }else{
            return videoSizeBean;
        }
    }

    @Override
    public String toString() {
        return "CameraSizeData{" +
                "SizeRatio=" + SizeRatio +
                ", pictureSizeBean=" + pictureSizeBean +
                ", videoSizeBean=" + videoSizeBean +
                '}';
    }
}
