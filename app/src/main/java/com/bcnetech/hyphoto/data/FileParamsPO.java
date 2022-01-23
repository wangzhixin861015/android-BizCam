package com.bcnetech.hyphoto.data;

import java.io.Serializable;

/**
 * Created by a1234 on 17/3/2.
 */

public class FileParamsPO implements Serializable{
    private String created;
    private String params;
    private String fileId;

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }
}
