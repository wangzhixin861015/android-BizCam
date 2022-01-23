package com.bcnetech.hyphoto.sql.dao;

import com.bcnetech.bcnetechlibrary.bean.CameraParm;

import java.io.Serializable;
import java.util.List;

/**
 * Created by wenbin on 16/9/10.
 */

public class PresetParm implements Serializable{

    private String name;//名字
    private String id;
    private long timeStamp; //时间戳
    private String textSrc;//示例图片地址
    private String auther;//作者名字
    private String autherID;//作者id
    private String autherUrl;//作者头像URL
    private String describe;//描述
    private String equipment;//设备
    private String presetId; //预设参数id
    //0 显示 1隐藏
    private String showType;//显示类型
    private int position; //排序
    private int downloadCount;
    private String imageHeight;
    private String imageWidth;
    private String system; //系统
    private String categoryId;
    private List<String> labels;
    private LightRatioData lightRatioData;
    private List<PictureProcessingData> parmlists;//参数调整
    private List<PictureProcessingData> partParmlists;//局部调整
    private CameraParm cameraParm;	//相机参数
    private String size;
    private String coverId;



    public PresetParm(){}
    public PresetParm(PresetParm presetParm){
        this.name=presetParm.getName();
        this.id=presetParm.getId();
        this.timeStamp=presetParm.getTimeStamp();
        this.textSrc=presetParm.getTextSrc();
        this.auther=presetParm.getAuther();
        this.autherID=presetParm.getAutherID();
        this.describe=presetParm.getDescribe();
        this.equipment=presetParm.getEquipment();
        this.labels=presetParm.getLabels();
        this.showType=presetParm.getShowType();
        this.lightRatioData=presetParm.getLightRatioData();
        this.parmlists=presetParm.getParmlists();
        this.partParmlists=presetParm.getPartParmlists();
        this.cameraParm=presetParm.getCameraParm();

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LightRatioData getLightRatioData() {
        return lightRatioData;
    }

    public void setLightRatioData(LightRatioData lightRatioData) {
        this.lightRatioData = lightRatioData;
    }


    public String getShowType() {
        return showType;
    }

    public void setShowType(String showType) {
        this.showType = showType;
    }

    public List<String> getLabels() {
        return labels;
    }

    public void setLabels(List<String> labels) {
        this.labels = labels;
    }

    public String getTextSrc() {
        return textSrc;
    }

    public void setTextSrc(String textSrc) {
        this.textSrc = textSrc;
    }

    public String getAutherID() {
        return autherID;
    }

    public void setAutherID(String autherID) {
        this.autherID = autherID;
    }

    public String getAuther() {
        return auther;
    }

    public void setAuther(String auther) {
        this.auther = auther;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public String getEquipment() {
        return equipment;
    }

    public void setEquipment(String equipment) {
        this.equipment = equipment;
    }

    public List<PictureProcessingData> getParmlists() {
        return parmlists;
    }

    public void setParmlists(List<PictureProcessingData> parmlists) {
        this.parmlists = parmlists;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public List<PictureProcessingData> getPartParmlists() {
        return partParmlists;
    }

    public void setPartParmlists(List<PictureProcessingData> partParmlists) {
        this.partParmlists = partParmlists;
    }

    public String getAutherUrl() {
        return autherUrl;
    }

    public void setAutherUrl(String autherUrl) {
        this.autherUrl = autherUrl;
    }

    public CameraParm getCameraParm() {
        return cameraParm;
    }

    public void setCameraParm(CameraParm cameraParm) {
        this.cameraParm = cameraParm;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getPresetId() {
        return presetId;
    }

    public void setPresetId(String presetId) {
        this.presetId = presetId;
    }

    public int getDownloadCount() {
        return downloadCount;
    }

    public void setDownloadCount(int downloadCount) {
        this.downloadCount = downloadCount;
    }

    public String getImageHeight() {
        return imageHeight;
    }

    public void setImageHeight(String imageHeight) {
        this.imageHeight = imageHeight;
    }

    public String getImageWidth() {
        return imageWidth;
    }

    public void setImageWidth(String imageWidth) {
        this.imageWidth = imageWidth;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getCoverId() {
        return coverId;
    }

    public void setCoverId(String coverId) {
        this.coverId = coverId;
    }

    @Override
    public String toString() {
        return "PresetParm{" +
                "auther='" + auther + '\'' +
                ", name='" + name + '\'' +
                ", id='" + id + '\'' +
                ", timeStamp=" + timeStamp +
                ", textSrc='" + textSrc + '\'' +
                ", autherID='" + autherID + '\'' +
                ", autherUrl='" + autherUrl + '\'' +
                ", describe='" + describe + '\'' +
                ", equipment='" + equipment + '\'' +
                ", presetId='" + presetId + '\'' +
                ", showType='" + showType + '\'' +
                ", position=" + position +
                ", downloadCount=" + downloadCount +
                ", imageHeight='" + imageHeight + '\'' +
                ", imageWidth='" + imageWidth + '\'' +
                ", system='" + system + '\'' +
                ", categoryId='" + categoryId + '\'' +
                ", labels=" + labels +
                ", lightRatioData=" + lightRatioData +
                ", parmlists=" + parmlists +
                ", partParmlists=" + partParmlists +
                ", cameraParm=" + cameraParm +
                ", size='" + size + '\'' +
                '}';
    }
}
