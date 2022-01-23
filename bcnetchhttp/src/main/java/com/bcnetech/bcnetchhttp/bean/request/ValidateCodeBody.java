package com.bcnetech.bcnetchhttp.bean.request;

/**
 * Created by yhf on 2018/9/14.
 */

public class ValidateCodeBody {

    public ValidateCodeBody(String key, String value) {
        this.key = key;
        this.value = value;
        this.login=key;
        this.code=value;
    }

    //手机号
    private String key;
    private String value;

    //国际化 用户名
    private String login;
    //国际化 验证码
    private String code;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
