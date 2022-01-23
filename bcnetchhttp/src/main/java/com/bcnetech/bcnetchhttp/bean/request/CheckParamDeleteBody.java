package com.bcnetech.bcnetchhttp.bean.request;

/**
 * Created by yhf on 2018/9/14.
 */

public class CheckParamDeleteBody {

    public CheckParamDeleteBody(String files) {
        this.files = files;
    }

    private String files;

    public String getFiles() {
        return files;
    }

    public void setFiles(String files) {
        this.files = files;
    }
}
