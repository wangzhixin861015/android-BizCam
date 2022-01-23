package com.bcnetech.hyphoto.data;

/**
 * Created by a1234 on 2018/6/5.
 */

public class BizCamExif {
    int bCBoxPicture;
    int bRestore;
    int topLight;
    int bottomLight;
    int leftLight;
    int rightLight;
    int forwardLight;
    int backLight;
    int picScore;
    char res[];

    public BizCamExif(int bCBoxPicture, int bRestore, int topLight, int bottomLight, int leftLight, int rightLight, int forwardLight, int backLight, int picScore) {
        this.bCBoxPicture = bCBoxPicture;
        this.bRestore = bRestore;
        this.topLight = topLight;
        this.bottomLight = bottomLight;
        this.leftLight = leftLight;
        this.rightLight = rightLight;
        this.forwardLight = forwardLight;
        this.backLight = backLight;
        this.picScore = picScore;
    }

    public int getbCBoxPicture() {
        return bCBoxPicture;
    }

    public void setbCBoxPicture(int bCBoxPicture) {
        this.bCBoxPicture = bCBoxPicture;
    }

    public int getbRestore() {
        return bRestore;
    }

    public void setbRestore(int bRestore) {
        this.bRestore = bRestore;
    }

    public int getTopLight() {
        return topLight;
    }

    public void setTopLight(int topLight) {
        this.topLight = topLight;
    }

    public int getBottomLight() {
        return bottomLight;
    }

    public void setBottomLight(int bottomLight) {
        this.bottomLight = bottomLight;
    }

    public int getLeftLight() {
        return leftLight;
    }

    public void setLeftLight(int leftLight) {
        this.leftLight = leftLight;
    }

    public int getRightLight() {
        return rightLight;
    }

    public void setRightLight(int rightLight) {
        this.rightLight = rightLight;
    }

    public int getForwardLight() {
        return forwardLight;
    }

    public void setForwardLight(int forwardLight) {
        this.forwardLight = forwardLight;
    }

    public int getBackLight() {
        return backLight;
    }

    public void setBackLight(int backLight) {
        this.backLight = backLight;
    }

    public int getPicScore() {
        return picScore;
    }

    public void setPicScore(int picScore) {
        this.picScore = picScore;
    }

    public char[] getRes() {
        return res;
    }

    public void setRes(char[] res) {
        this.res = res;
    }
}
