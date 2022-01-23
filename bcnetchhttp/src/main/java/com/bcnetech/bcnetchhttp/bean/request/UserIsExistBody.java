package com.bcnetech.bcnetchhttp.bean.request;

/**
 * Created by yhf on 2018/11/7.
 */

public class UserIsExistBody {

    private String login;
    private String appCode;
    private String regionCode;

    public UserIsExistBody(String login, String appCode, String regionCode) {
        this.login = login;
        this.appCode = appCode;
        this.regionCode = regionCode;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getAppCode() {
        return appCode;
    }

    public void setAppCode(String appCode) {
        this.appCode = appCode;
    }

    public String getRegionCode() {
        return regionCode;
    }

    public void setRegionCode(String regionCode) {
        this.regionCode = regionCode;
    }
}
