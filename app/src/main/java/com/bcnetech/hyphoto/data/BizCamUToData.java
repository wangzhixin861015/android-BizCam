package com.bcnetech.hyphoto.data;

/**
 * Created by yhf on 2018/10/29.
 */

public class BizCamUToData {

    private String action;
    private String code;
    private String url;
    private String params;
    private class Params{

        private String provider;
        private String provider_name;
        private String user_name;
        private String phone;

    }

    private String tag;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
