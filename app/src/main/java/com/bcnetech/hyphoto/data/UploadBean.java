package com.bcnetech.hyphoto.data;


/**
 * Created by yhf on 2018/6/15.
 */

public class UploadBean{



    public UploadBean(int count,int uploadStatus,int httpType) {
        this.count = count;
        this.httpType=httpType;
        this.uploadStatus=uploadStatus;
    }

    private int count;

    private int httpType;

    //
    private int uploadStatus;


    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getHttpType() {
        return httpType;
    }

    public void setHttpType(int httpType) {
        this.httpType = httpType;
    }

    public int getUploadStatus() {
        return uploadStatus;
    }

    public void setUploadStatus(int uploadStatus) {
        this.uploadStatus = uploadStatus;
    }
}
