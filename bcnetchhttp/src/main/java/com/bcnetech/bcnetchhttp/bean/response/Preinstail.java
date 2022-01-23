package com.bcnetech.bcnetchhttp.bean.response;


import com.bcnetech.bcnetchhttp.bean.data.LightThan;
import com.bcnetech.bcnetchhttp.bean.data.ShareGlobalParms;
import com.bcnetech.bcnetchhttp.bean.data.SharePartParms;
import com.bcnetech.bcnetechlibrary.bean.CameraParm;

import java.util.ArrayList;

/**
 * 分享参数data
 * Created by a1234 on 17/1/16.
 */

public class Preinstail {
    private String ID;
    private String autherName;
    private String des;
    private String fileName;
    private String label;
    private String device;
    private long imageDate;
    private String name;
    private ArrayList<ShareGlobalParms> globalArray;
    private ArrayList<SharePartParms> partArray;
    private LightThan lightThan;
    private CameraParm cameraParm;            //相机参数
    private String fileId;
    private String dataSize;
    private String size;
    private String system;
    private String paramVersion;////商拍光影版本
    private String coverId;



    private String imageData1;
    private String ImageData2;

    public String getParamVersion() {
        return paramVersion;
    }

    public void setParamVersion(String paramVersion) {
        this.paramVersion = paramVersion;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public CameraParm getCameraParm() {
        return cameraParm;
    }

    public void setCameraParm(CameraParm cameraParm) {
        this.cameraParm = cameraParm;
    }

    public ArrayList<ShareGlobalParms> getGlobalArray() {
        return globalArray;
    }

    public void setGlobalArray(ArrayList<ShareGlobalParms> globalArray) {
        this.globalArray = globalArray;
    }

    public ArrayList<SharePartParms> getPartArray() {
        return partArray;
    }

    public void setPartArray(ArrayList<SharePartParms> partArray) {
        this.partArray = partArray;
    }


    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public long getImageDate() {
        return imageDate;
    }

    public void setImageDate(long imageDate) {
        this.imageDate = imageDate;
    }

    public LightThan getLightThan() {
        return lightThan;
    }

    public void setLightThan(LightThan lightThan) {
        this.lightThan = lightThan;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getAutherName() {
        return autherName;
    }

    public void setAutherName(String autherName) {
        this.autherName = autherName;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
    }

    public String getDataSize() {
        return dataSize;
    }

    public void setDataSize(String dataSize) {
        this.dataSize = dataSize;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }


    public String getImageData1() {
        return imageData1;
    }

    public void setImageData1(String imageData1) {
        this.imageData1 = imageData1;
    }

    public String getImageData2() {
        return ImageData2;
    }

    public void setImageData2(String imageData2) {
        ImageData2 = imageData2;
    }

    public String getCoverId() {
        return coverId;
    }

    public void setCoverId(String coverId) {
        this.coverId = coverId;
    }

    @Override
    public String toString() {
        return "Preinstail{" +
                "ID='" + ID + '\'' +
                ", autherName='" + autherName + '\'' +
                ", des='" + des + '\'' +
                ", fileName='" + fileName + '\'' +
                ", label='" + label + '\'' +
                ", device='" + device + '\'' +
                ", imageDate=" + imageDate +
                ", name='" + name + '\'' +
                ", globalArray=" + globalArray +
                ", partArray=" + partArray +
                ", lightThan=" + lightThan +
                ", cameraParm=" + cameraParm +
                ", fileId='" + fileId + '\'' +
                '}';
    }
}
