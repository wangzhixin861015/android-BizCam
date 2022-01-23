package com.bcnetech.hyphoto.data;

import com.bcnetech.bcnetchhttp.bean.data.DownloadInfoData;
import com.bcnetech.bcnetchhttp.bean.response.Preinstail;
import com.bcnetech.hyphoto.sql.dao.ImageData;

import java.io.Serializable;

/**
 * Created by a1234 on 17/1/15.
 */

public class UploadCloudData implements Serializable{
    private ImageData imageData;
    private String catalogId;
    private DownloadInfoData downloadInfoData;
    private Preinstail preinstail;

    public UploadCloudData(){}

    public UploadCloudData(ImageData imageData, String catalogId){
        this.imageData=imageData;
        this.catalogId=catalogId;
    }

    public DownloadInfoData getDownloadInfoData() {
        return downloadInfoData;
    }

    public void setDownloadInfoData(DownloadInfoData downloadInfoData) {
        this.downloadInfoData = downloadInfoData;
    }

    public ImageData getImageData() {
        return imageData;
    }

    public void setImageData(ImageData imageData) {
        this.imageData = imageData;
    }

    public String getCatalogId() {
        return catalogId;
    }

    public void setCatalogId(String catalogId) {
        this.catalogId = catalogId;
    }

    public Preinstail getPreinstail() {
        return preinstail;
    }

    public void setPreinstail(Preinstail preinstail) {
        this.preinstail = preinstail;
    }
}
