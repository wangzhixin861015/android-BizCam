package com.bcnetech.bcnetchhttp.bean.data;

/**
 * num = 57;
 * type = 2;
 */
public class ShareGlobalParms {
    int num;
    int tint;
    int type;

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getTint() {
        return tint;
    }

    public void setTint(int tint) {
        this.tint = tint;
    }

    @Override
    public String toString() {
        return "ShareGlobalParms{" +
                "num=" + num +
                ", type=" + type +
                ", tint=" + tint +
                '}';
    }
}