package com.bcnetech.bcnetchhttp.bean.request;

/**
 * Created by yhf on 2018/9/17.
 */

public class ResetHeadBody {

    public ResetHeadBody(String avatar) {
        this.avatar = avatar;
    }


    private String avatar;


    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
