package com.bcnetech.hyphoto.data;

import java.io.Serializable;

/**
 * Created by yhf on 2017/5/12.
 */
public class ShareOwnerParmsData implements Serializable{

    public String catalogId;

    public String fileId;

    public String ownerId;

    public String coverId;

    public int code;

    public String getCatalogId() {
        return catalogId;
    }

    public void setCatalogId(String catalogId) {
        this.catalogId = catalogId;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getCoverId() {
        return coverId;
    }

    public void setCoverId(String coverId) {
        this.coverId = coverId;
    }
}
