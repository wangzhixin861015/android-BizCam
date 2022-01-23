package com.bcnetech.bcnetchhttp.bean.request;

import android.os.Build;

/**
 * Created by yhf on 2018/9/14.
 */

public class FeedBackBody {


    public FeedBackBody(String content, String appVer,String username,String mobile,String images) {
        this.content = content;
        this.appVer = appVer;
        this.username=username;
        this.mobile=mobile;
        this.images=images;
    }

    //app系统版本
    private String appVer;
    //已经反馈内容
    private String content;
    //手机号
    private String mobile;
    //意见反馈人姓名
    private String username;
    //已经反馈图片
    private String images;


    //系统版本
    private String osVer = android.os.Build.VERSION.SDK_INT + "";
    //手机型号
    private String model= Build.MODEL;
    // 1 android
    private String appType = "1";
    //应用
    private String appId="100";



    public String getAppVer() {
        return appVer;
    }

    public void setAppVer(String appVer) {
        this.appVer = appVer;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAppType() {
        return appType;
    }

    public void setAppType(String appType) {
        this.appType = appType;
    }

    public String getOsVer() {
        return osVer;
    }

    public void setOsVer(String osVer) {
        this.osVer = osVer;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }
}
