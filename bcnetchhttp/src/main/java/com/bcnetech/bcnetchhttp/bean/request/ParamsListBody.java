package com.bcnetech.bcnetchhttp.bean.request;

import com.bcnetech.bcnetchhttp.bean.response.LoginedUser;

/**
 * Created by yhf on 2018/9/14.
 */

public class ParamsListBody {

    public ParamsListBody(String page,String pagesize,  String catalogId) {
        this.page = page;
        this.pagesize=pagesize;
        this.catalogId = catalogId;
    }

    private String page;
    private String pagesize;
    private String device= LoginedUser.getLoginedUser().isSupportCamera2()?
            android.os.Build.MODEL:"android";

    private String boxStyle;
    private String catalogId;


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

    public String getBoxStyle() {
        return boxStyle;
    }

    public void setBoxStyle(String boxStyle) {
        this.boxStyle = boxStyle;
    }

    public String getCatalogId() {
        return catalogId;
    }

    public void setCatalogId(String catalogId) {
        this.catalogId = catalogId;
    }
}
