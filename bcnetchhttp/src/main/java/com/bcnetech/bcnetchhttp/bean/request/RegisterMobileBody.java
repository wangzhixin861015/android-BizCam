package com.bcnetech.bcnetchhttp.bean.request;

/**
 * Created by yhf on 2018/9/14.
 */

public class RegisterMobileBody {
    public RegisterMobileBody(String phone, String ciphertext, String nickname, String code, String regionCode, String appCode) {
        this.phone = phone;
        this.ciphertext = ciphertext;
        this.nickname = nickname;
        this.code = code;
        this.login = phone;
        this.password = ciphertext;
        this.regionCode = regionCode;
        this.appCode = appCode;
    }

    private String phone;
    private String ciphertext;
    private String nickname;
    private String code;

    //国际化用户名
    private String login;
    //国际化密码
    private String password;

    private String regionCode;

    private String appCode;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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
