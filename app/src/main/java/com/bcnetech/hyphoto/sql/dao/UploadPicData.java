package com.bcnetech.hyphoto.sql.dao;

import java.io.Serializable;

/**
 * Created by a1234 on 2017/8/17.
 */

public class UploadPicData implements Serializable {
    private ImageData imagedata;
    private Long uploadTime; //上传时间
    private String localUrl; //本地小图路径
    private String value1;
    private String value2;

    public ImageData getImagedata() {
        return imagedata;
    }

    public void setImagedata(ImageData imagedata) {
        this.imagedata = imagedata;
    }

    public Long getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(Long uploadTime) {
        this.uploadTime = uploadTime;
    }

    public String getLocalUrl() {
        return localUrl;
    }

    public void setLocalUrl(String localUrl) {
        this.localUrl = localUrl;
    }

    public String getValue1() {
        return value1;
    }

    public void setValue1(String value1) {
        this.value1 = value1;
    }

    public String getValue2() {
        return value2;
    }

    public void setValue2(String value2) {
        this.value2 = value2;
    }

    @Override
    public String toString() {
        return "UploadPicData{" +
                "imagedata=" + imagedata +
                ", uploadTime=" + uploadTime +
                ", localUrl='" + localUrl + '\'' +
                ", value1='" + value1 + '\'' +
                ", value2='" + value2 + '\'' +
                '}';
    }
}
