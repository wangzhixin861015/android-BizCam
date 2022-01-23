package com.bcnetech.bcnetchhttp.bean.request;

/**
 * Created by yhf on 2018/9/14.
 */

public class ForgetResetBody {

    public ForgetResetBody(String login, String password, String code) {
        this.login = login;
        this.password = password;
        this.code = code;
    }

    private String login;
    private String password;
    private String code;

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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
