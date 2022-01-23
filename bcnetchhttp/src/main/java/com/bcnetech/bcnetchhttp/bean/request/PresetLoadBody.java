package com.bcnetech.bcnetchhttp.bean.request;

import com.bcnetech.bcnetchhttp.bean.response.LoginedUser;

/**
 * Created by yhf on 2018/9/13.
 */

public class PresetLoadBody {

    public PresetLoadBody(String page) {
        this.page = page;
    }

    private String page;
    private String pagesize="8";
    private String device= LoginedUser.getLoginedUser().isSupportCamera2()?
            android.os.Build.MODEL:"android";


    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public String getPagesize() {
        return pagesize;
    }

    public void setPagesize(String pagesize) {
        this.pagesize = pagesize;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }
}
