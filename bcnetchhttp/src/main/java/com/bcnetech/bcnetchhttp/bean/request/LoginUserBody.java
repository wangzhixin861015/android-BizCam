package com.bcnetech.bcnetchhttp.bean.request;

/**
 * Created by yhf on 2018/9/13.
 */

public class LoginUserBody {


    public LoginUserBody() {
    }

    public LoginUserBody(String login, String password, String deviceName, String appType,String regionCode) {
        this.login = login;
        this.password = password;
        this.deviceName = deviceName;
        this.appType = appType;
        this.regionCode=regionCode;
    }
    //账号
    private String login;
    //密码
    private String password;
    //手机型号
    private String deviceName;
    //
    private String appType;

    private String appCode;
    //注册区域
    private String regionCode;


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

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getAppType() {
        return appType;
    }

    public void setAppType(String appType) {
        this.appType = appType;
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
