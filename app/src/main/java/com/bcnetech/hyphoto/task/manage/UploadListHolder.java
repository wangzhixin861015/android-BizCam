package com.bcnetech.hyphoto.task.manage;

import com.bcnetech.hyphoto.data.UploadCloudData;

import java.util.List;

/**
 * Created by yhf on 2020-02-20
 */
public class UploadListHolder {

    private static UploadListHolder uploadListHolder;

    private List<UploadCloudData> uploadCloudData;

    public List<UploadCloudData> getUploadCloudData() {
        return uploadCloudData;
    }

    public void saveUploadCloudData(List<UploadCloudData> uploadCloudData) {
        this.uploadCloudData = uploadCloudData;
    }


    private UploadListHolder() {
    }

    public  static  UploadListHolder getInstance(){
        if(uploadListHolder==null){

            uploadListHolder=new UploadListHolder();
        }
        return  uploadListHolder;
    }
}
