package com.bcnetech.bcnetchhttp.bean.request;

/**
 * Created by yhf on 2018/9/19.
 */

public class RefindPwdBody {

    public RefindPwdBody(String email) {
        this.email = email;
    }

    private String  email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
