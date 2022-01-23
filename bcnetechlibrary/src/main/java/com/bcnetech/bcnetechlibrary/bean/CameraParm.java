package com.bcnetech.bcnetechlibrary.bean;

import java.io.Serializable;

public class CameraParm implements Serializable {
    private String whiteBalance;    //白平衡
    private String iso;                //补光
    private String sec;            //快门
    private String focalLength;        //焦距
    private String sceneMode;         //场景模式

    private String red;
    private String greenEven;
    private String greenOdd;
    private String blue;

    public CameraParm(String whiteBalance, String iso, String sec, String focalLength,String mRed,String mGreenEven,String mGreenOdd,String mBlue) {
        this.whiteBalance = whiteBalance;
        this.iso = iso;
        this.sec = sec;
        this.focalLength = focalLength;
        this.red=mRed;
        this.greenEven=mGreenEven;
        this.greenOdd=mGreenOdd;
        this.blue=mBlue;
    }

    public CameraParm() {
    }

    public String getWhiteBalance() {
        return whiteBalance;
    }

    public void setWhiteBalance(String whiteBalance) {
        this.whiteBalance = whiteBalance;
    }

    public String getIso() {
        return iso;
    }

    public void setIso(String iso) {
        this.iso = iso;
    }

    public String getSec() {
        return sec;
    }

    public void setSec(String sec) {
        this.sec = sec;
    }

    public String getFocalLength() {
        return focalLength;
    }

    public void setFocalLength(String focalLength) {
        this.focalLength = focalLength;
    }

    public String getSceneMode() {
        return sceneMode;
    }

    public void setSceneMode(String sceneMode) {
        this.sceneMode = sceneMode;
    }

    public String getRed() {
        return red;
    }

    public void setRed(String red) {
        this.red = red;
    }

    public String getGreenEven() {
        return greenEven;
    }

    public void setGreenEven(String greenEven) {
        this.greenEven = greenEven;
    }

    public String getGreenOdd() {
        return greenOdd;
    }

    public void setGreenOdd(String greenOdd) {
        this.greenOdd = greenOdd;
    }

    public String getBlue() {
        return blue;
    }

    public void setBlue(String blue) {
        this.blue = blue;
    }

    @Override
    public String toString() {
        return "CameraParm{" +
                "whiteBalance='" + whiteBalance + '\'' +
                ", iso='" + iso + '\'' +
                ", sec='" + sec + '\'' +
                ", focalLength='" + focalLength + '\'' +
                ", sceneMode='" + sceneMode + '\'' +
                '}';
    }
}
