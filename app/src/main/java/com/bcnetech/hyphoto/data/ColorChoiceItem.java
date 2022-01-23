package com.bcnetech.hyphoto.data;


import java.io.Serializable;

/**
 * Created by yhf on 2017/3/6.
 */
public class ColorChoiceItem implements Serializable {

    //颜色
    private String color;
    //是否选中
    private boolean isCheck;

    private String path;

    public ColorChoiceItem(String color, boolean isCheck,String path) {
        this.color = color;
        this.isCheck = isCheck;
        this.path=path;
    }

    public ColorChoiceItem(String color, boolean isCheck) {
        this.color = color;
        this.isCheck = isCheck;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }
}
