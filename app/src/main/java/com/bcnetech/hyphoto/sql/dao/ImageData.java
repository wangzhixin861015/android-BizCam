package com.bcnetech.hyphoto.sql.dao;

import com.bcnetech.bcnetechlibrary.bean.CameraParm;

import java.io.Serializable;
import java.util.List;

/**
 * Created by wenbin on 16/8/23.
 */

public class ImageData implements Serializable{

    private String localUrl;//保存视频不加"file:/"，图片反之
    private String smallLocalUrl;

    private int type;
    private int recoderTime;    //视频时间
    private String size;

    private int currentPosition;//当前所处的步骤
    private long timeStamp; //时间戳
    private boolean isMatting;//判断是否时抠图
    private LightRatioData lightRatioData;

    private List<PictureProcessingData> imageTools;//后期修改(全局)
    private List<PictureProcessingData> imageParts;//部分修改
    private PresetParm presetParms;//预设参数
    private CameraParm cameraParm;	//相机参数

    private String value1;
    private String value2; //全局调整小图
    private String value3; //上传后fileId
    private String value4; //上传后hash值
    private String value5;//25d的标题
    private String value6;
    private String value7;
    private String value8;
    private String value9;
    private String value10;


    public ImageData(){

    }

    public String getLocalUrl() {
        return localUrl;
    }

    public void setLocalUrl(String localUrl) {
        this.localUrl = localUrl;
    }

    public String getSmallLocalUrl() {
        return smallLocalUrl;
    }

    public void setSmallLocalUrl(String smallLocalUrl) {
        this.smallLocalUrl = smallLocalUrl;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public LightRatioData getLightRatioData() {
        return lightRatioData;
    }

    public void setLightRatioData(LightRatioData lightRatioData) {
        this.lightRatioData = lightRatioData;
    }

    public PresetParm getPresetParms() {
        return presetParms;
    }

    public void setPresetParms(PresetParm presetParms) {
        this.presetParms = presetParms;
    }

    public List<PictureProcessingData> getImageTools() {
        return imageTools;
    }

    public void setImageTools(List<PictureProcessingData> imageTools) {
        this.imageTools = imageTools;
    }

    public List<PictureProcessingData> getImageParts() {
        return imageParts;
    }

    public void setImageParts(List<PictureProcessingData> imageParts) {
        this.imageParts = imageParts;
    }

    public boolean isMatting() {
        return isMatting;
    }

    public void setMatting(boolean matting) {
        isMatting = matting;
    }

    public int getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(int currentPosition) {
        this.currentPosition = currentPosition;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getRecoderTime() {
        return recoderTime;
    }

    public void setRecoderTime(int recoderTime) {
        this.recoderTime = recoderTime;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public CameraParm getCameraParm() {
        return cameraParm;
    }

    public void setCameraParm(CameraParm cameraParm) {
        this.cameraParm = cameraParm;
    }

    public String getValue1() {
        return value1;
    }

    public void setValue1(String value1) {
        this.value1 = value1;
    }

    public String getValue2() {
        return value2;
    }

    public void setValue2(String value2) {
        this.value2 = value2;
    }

    public String getValue3() {
        return value3;
    }

    public void setValue3(String value3) {
        this.value3 = value3;
    }

    public String getValue4() {
        return value4;
    }

    public void setValue4(String value4) {
        this.value4 = value4;
    }

    public String getValue5() {
        return value5;
    }

    public void setValue5(String value5) {
        this.value5 = value5;
    }

    public String getValue6() {
        return value6;
    }

    public void setValue6(String value6) {
        this.value6 = value6;
    }

    public String getValue7() {
        return value7;
    }

    public void setValue7(String value7) {
        this.value7 = value7;
    }

    public String getValue8() {
        return value8;
    }

    public void setValue8(String value8) {
        this.value8 = value8;
    }

    public String getValue9() {
        return value9;
    }

    public void setValue9(String value9) {
        this.value9 = value9;
    }

    public String getValue10() {
        return value10;
    }

    public void setValue10(String value10) {
        this.value10 = value10;
    }


    @Override
    public String toString() {
        return "ImageData{" +
                "localUrl='" + localUrl + '\'' +
                ", smallLocalUrl='" + smallLocalUrl + '\'' +
                ", type=" + type +
                ", recoderTime=" + recoderTime +
                ", size='" + size + '\'' +
                ", currentPosition=" + currentPosition +
                ", timeStamp=" + timeStamp +
                ", isMatting=" + isMatting +
                ", lightRatioData=" + lightRatioData +
                ", imageTools=" + imageTools +
                ", imageParts=" + imageParts +
                ", presetParms=" + presetParms +
                ", cameraParm=" + cameraParm +
                ", value1='" + value1 + '\'' +
                ", value2='" + value2 + '\'' +
                ", value3='" + value3 + '\'' +
                ", value4='" + value4 + '\'' +
                ", value5='" + value5 + '\'' +
                ", value6='" + value6 + '\'' +
                ", value7='" + value7 + '\'' +
                ", value8='" + value8 + '\'' +
                ", value9='" + value9 + '\'' +
                ", value10='" + value10 + '\'' +
                '}';
    }
}
