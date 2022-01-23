package com.bcnetech.hyphoto.task.manage;

import com.bcnetech.bcnetchhttp.bean.response.CloudPicData;

import java.util.List;

/**
 * Created by yhf on 2020-02-20
 */
public class DownloadListHolder {

    private static DownloadListHolder downloadListHolder;

    private List<CloudPicData> cloudPicData;

    public List<CloudPicData> getCloudPicData() {
        return cloudPicData;
    }

    public void saveCloudPicData(List<CloudPicData> cloudPicData) {
        this.cloudPicData = cloudPicData;
    }


    private DownloadListHolder() {
    }

    public  static DownloadListHolder getInstance(){
        if(downloadListHolder==null){

            downloadListHolder=new DownloadListHolder();
        }
        return  downloadListHolder;
    }
}
