package com.bcnetech.bcnetchhttp.bean.request;

/**
 * Created by yhf on 2018/9/19.
 */

public class SendEmailBody {

    public SendEmailBody(String toEmail) {
        this.toEmail = toEmail;
    }

    private String  toEmail;

    public String getToEmail() {
        return toEmail;
    }

    public void setToEmail(String toEmail) {
        this.toEmail = toEmail;
    }
}
