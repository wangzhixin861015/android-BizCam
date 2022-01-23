package com.bcnetech.bcnetchhttp.bean.request;

/**
 * Created by yhf on 2018/9/18.
 */

public class ResetNameBody {

    public ResetNameBody(String nickname) {
        this.nickname = nickname;
    }

    private String nickname;

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
