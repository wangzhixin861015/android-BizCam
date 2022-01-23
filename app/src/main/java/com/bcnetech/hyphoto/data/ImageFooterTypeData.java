package com.bcnetech.hyphoto.data;

import java.io.Serializable;

/**
 * Created by wenbin on 16/8/23.
 */

public class ImageFooterTypeData implements Serializable{
    /**
     * 按钮事件类型
     */
    public final static int SCOURE=1;
    public final static int FORPARMS=2;
    public final static int PARMSCHANG=3;
    public final static int RECTCHANG=4;
    /**
     * 按钮状态类型
     */
    public final static int UNCLICK=1;
    public final static int CLICK=2;
    public final static int AFTERCLICK=3;
    private int type;
    private String name;
    private int isClickType;
    public ImageFooterTypeData(){


    }

    public ImageFooterTypeData(int type,String name,int isClickType){
        this.type=type;
        this.name=name;
        this.isClickType=isClickType;

    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIsClickType() {
        return isClickType;
    }

    public void setIsClickType(int isClickType) {
        this.isClickType = isClickType;
    }
}
