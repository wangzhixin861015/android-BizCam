package com.bcnetech.hyphoto.sql.dao;

import java.io.Serializable;

/**
 * Created by wenbin on 16/8/4.
 */

public class LightRatioData implements Serializable {

    private String name;
    private int leftLight;
    private int rightLight;
    private int bottomLight;
    private int backgroudLight;
    private int moveLight;
    private int topLight;//move2
    private int light1;//motor
    private int light2;
    private int light3;
    private int light4;
    private int light5;
    private int num;
    private String lightId;
    private String version;
    private String id;


    public LightRatioData() {

    }

    public void initData() {
        name = "";
        leftLight = -1;
        rightLight = -1;
        bottomLight = -1;
        backgroudLight = -1;
        moveLight = -1;
        topLight = -1;
        light1 = -1;
        light2 = -1;
        light3 = -1;
        light4 = -1;
        light5 = -1;
        num = -1;
        version="";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }




    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
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

    public int getBottomLight() {
        return bottomLight;
    }

    public void setBottomLight(int bottomLight) {
        this.bottomLight = bottomLight;
    }

    public int getBackgroudLight() {
        return backgroudLight;
    }

    public void setBackgroudLight(int backgroudLight) {
        this.backgroudLight = backgroudLight;
    }

    public int getMoveLight() {
        return moveLight;
    }

    public void setMoveLight(int moveLight) {
        this.moveLight = moveLight;
    }

    public int getTopLight() {
        return topLight;
    }

    public void setTopLight(int topLight) {
        this.topLight = topLight;
    }

    public int getLight1() {
        return light1;
    }

    public void setLight1(int light1) {
        this.light1 = light1;
    }

    public int getLight2() {
        return light2;
    }

    public void setLight2(int light2) {
        this.light2 = light2;
    }

    public int getLight3() {
        return light3;
    }

    public void setLight3(int light3) {
        this.light3 = light3;
    }

    public int getLight4() {
        return light4;
    }

    public void setLight4(int light4) {
        this.light4 = light4;
    }

    public int getLight5() {
        return light5;
    }

    public void setLight5(int light5) {
        this.light5 = light5;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getLightId() {
        return lightId;
    }

    public void setLightId(String lightId) {
        this.lightId = lightId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "LightRatioData{" +
                "name='" + name + '\'' +
                ", leftLight=" + leftLight +
                ", rightLight=" + rightLight +
                ", bottomLight=" + bottomLight +
                ", backgroudLight=" + backgroudLight +
                ", moveLight=" + moveLight +
                ", topLight=" + topLight +
                ", light1=" + light1 +
                ", light2=" + light2 +
                ", light3=" + light3 +
                ", light4=" + light4 +
                ", light5=" + light5 +
                ", num=" + num +
                ", lightId='" + lightId + '\'' +
                ", version='" + version + '\'' +
                ", id='" + id + '\'' +
                '}';
    }
}
