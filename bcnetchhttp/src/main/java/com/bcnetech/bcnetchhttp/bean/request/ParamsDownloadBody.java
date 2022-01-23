package com.bcnetech.bcnetchhttp.bean.request;

/**
 * Created by yhf on 2018/9/14.
 */

public class ParamsDownloadBody {

    public ParamsDownloadBody(String fileId, String catalogId) {
        this.fileId = fileId;
        this.catalogId = catalogId;
    }

    private String fileId;
    private String catalogId;

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
}
