package com.bcnetech.bcnetchhttp.bean.request;

/**
 * Created by yhf on 2018/9/17.
 */

public class ChangePasswordBody {

    public ChangePasswordBody() {
    }

    public ChangePasswordBody(String login, String password, String newPassword) {
        this.login = login;
        this.password = password;
        this.newPassword = newPassword;
        this.regionCode=regionCode;
        this.appCode=appCode;
    }

    private String login;

    private String password;

    private String newPassword;

    private String regionCode;

    private String appCode;

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getRegionCode() {
        return regionCode;
    }

    public void setRegionCode(String regionCode) {
        this.regionCode = regionCode;
    }

    public String getAppCode() {
        return appCode;
    }

    public void setAppCode(String appCode) {
        this.appCode = appCode;
    }
}
