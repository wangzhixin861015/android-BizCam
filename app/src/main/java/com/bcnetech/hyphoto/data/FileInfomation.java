package com.bcnetech.hyphoto.data;

import java.io.Serializable;

/**
 * Created by a1234 on 17/3/2.
 */

public class FileInfomation implements Serializable{
    private String created;
    private String catalogId;
    private String fileId;

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

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
