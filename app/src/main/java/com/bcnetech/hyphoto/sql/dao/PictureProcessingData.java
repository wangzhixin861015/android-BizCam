package com.bcnetech.hyphoto.sql.dao;

import com.bcnetech.hyphoto.imageinterface.BizImageMangage;

import java.io.Serializable;

/**
 *
 * 图片处理类
 * Created by wenbin on 16/10/10.
 */

public class PictureProcessingData implements Serializable{
    private int type;//图片处理类型
    private String imageUrl;
    private int num;//参数处理值
    private int tintNum;//色调
    private String imageData;
    private String smallUrl;

    public PictureProcessingData(){}

    public PictureProcessingData(int type){
        this.type=type;
        this.imageUrl="";
//        if(type==BizImageMangage.WHITE_LEVELS){
//            this.num= BizImageMangage.INIT_DATA*2;
//        }else if(type==BizImageMangage.BLACK_LEVELS){
//            this.num= BizImageMangage.INIT_DATA*2;
//        }else {
            this.num= BizImageMangage.INIT_DATA;
//        }
        this.tintNum=BizImageMangage.INIT_DATA;
    }
    public PictureProcessingData(int type,String imageUrl){
        this.type=type;
        this.imageUrl=imageUrl;
        this.num= BizImageMangage.INIT_DATA;
        this.tintNum=BizImageMangage.INIT_DATA;
    }

    public PictureProcessingData(int type,String imageUrl,String smallUrl,String diff){
        this.type=type;
        this.imageUrl=imageUrl;
        this.smallUrl=smallUrl;
        this.num= BizImageMangage.INIT_DATA;
        this.tintNum=BizImageMangage.INIT_DATA;
    }

    public PictureProcessingData(int type,String imageUrl,String imageData){
        this.type=type;
        this.imageUrl=imageUrl;
        this.num= BizImageMangage.INIT_DATA;
        this.tintNum=BizImageMangage.INIT_DATA;
        this.imageData=imageData;
    }

    public PictureProcessingData(int type,int num){
        this.type=type;
        this.imageUrl="";
        this.num= num;
        this.tintNum=BizImageMangage.INIT_DATA;
    }

    public PictureProcessingData(PictureProcessingData processingData){
        this.type=processingData.type;
        this.imageUrl=processingData.imageUrl;
        this.num=processingData.num;
        this.tintNum=processingData.tintNum;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public int getTintNum() {
        return tintNum;
    }

    public void setTintNum(int tintNum) {
        this.tintNum = tintNum;
    }

    public String getImageData() {
        return imageData;
    }

    public void setImageData(String imageData) {
        this.imageData = imageData;
    }

    public String getSmallUrl() {
        return smallUrl;
    }

    public void setSmallUrl(String smallUrl) {
        this.smallUrl = smallUrl;
    }

    @Override
    public String toString() {
        return "PictureProcessingData{" +
                "imageData='" + imageData + '\'' +
                ", type=" + type +
                ", imageUrl='" + imageUrl + '\'' +
                ", num=" + num +
                ", tintNum=" + tintNum +
                ", smallUrl='" + smallUrl + '\'' +
                '}';
    }
}
