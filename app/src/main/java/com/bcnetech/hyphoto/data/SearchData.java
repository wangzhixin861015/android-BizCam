package com.bcnetech.hyphoto.data;

import java.io.Serializable;

/**
 * Created by yhf on 2018/3/12.
 */

public class SearchData implements Serializable {

    private float[] featureVec;
    private String cobox;
    private String cellphone;
    private Long filesize;
    private String version;

    public String getCellphone() {
        return cellphone;
    }

    public void setCellphone(String cellphone) {
        this.cellphone = cellphone;
    }

    public String getCobox() {
        return cobox;
    }

    public void setCobox(String cobox) {
        this.cobox = cobox;
    }

    public float[] getFeatureVec() {
        return featureVec;
    }

    public void setFeatureVec(float[] featureVec) {
        this.featureVec = featureVec;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Long getFilesize() {
        return filesize;
    }

    public void setFilesize(Long filesize) {
        this.filesize = filesize;
    }
}
