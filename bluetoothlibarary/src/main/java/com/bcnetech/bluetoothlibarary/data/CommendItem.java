package com.bcnetech.bluetoothlibarary.data;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Created by wenbin on 16/7/19.
 */
public class CommendItem implements Serializable {
    private int type;
    private int passageway;
    private int num;
    private byte[] respons;


    public CommendItem() {
    }

    public CommendItem(int type, int num) {
        this.type = type;
        this.num = num;
    }

    public CommendItem(int type, int passageway, int num) {
        this.type = type;
        this.passageway = passageway;
        this.num = num;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public byte[] getRespons() {
        return respons;
    }

    public void setRespons(byte[] respons) {
        this.respons = respons;
    }

    public int getPassageway() {
        return passageway;
    }

    public void setPassageway(int passageway) {
        this.passageway = passageway;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    @Override
    public String toString() {
        return "CommendItem{" +
                "type=" + type +
                ", passageway=" + passageway +
                ", num=" + num +
                ", respons=" + Arrays.toString(respons) +
                '}';
    }
}
