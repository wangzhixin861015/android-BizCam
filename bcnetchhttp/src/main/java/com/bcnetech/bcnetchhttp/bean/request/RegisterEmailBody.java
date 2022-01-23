package com.bcnetech.bcnetchhttp.bean.request;

/**
 * Created by yhf on 2018/9/14.
 */

public class RegisterEmailBody {

    public RegisterEmailBody(String email, String ciphertext, String nickname,String regionCode,String appCode) {
        this.email = email;
        this.ciphertext = ciphertext;
        this.login=email;
        this.password=ciphertext;
        this.nickname = nickname;
        this.regionCode=regionCode;
        this.appCode=appCode;
    }

    private String email;
    private String ciphertext;
    private String nickname;
    private String regionCode;

    private String appCode;

    //国际化用户名
    private String login;
    //国际化密码
    private String password;

    public String getPhone() {
        return email;
    }

    public void setPhone(String phone) {
        this.email = phone;
    }

    public String getCiphertext() {
        return ciphertext;
    }

    public void setCiphertext(String ciphertext) {
        this.ciphertext = ciphertext;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
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
}
