package com.bcnetech.bcnetchhttp.bean.response;

import java.io.Serializable;

/**
 * Created by wenbin on 16/6/30.
 */
public class CloudPicData implements Serializable{


    /**
     * id : 298
     * url : http://ebiztop-tst.oss-cn-hangzhou.aliyuncs.com/school_1/teacher3/pics/146734142371736.png
     */
    private String catalogId;
    private String name;
    private String fileId;
    private boolean isClick;
    private int width;
    private int height;
    private String type="1";
    private String format;
    //video：
    private String coverId;
    private String coverWidth;
    private String coverHeight;

    private String sha1;



    public String getCatalogId() {
        return catalogId;
    }

    public void setCatalogId(String catalogId) {
        this.catalogId = catalogId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public boolean isClick() {
        return isClick;
    }

    public void setClick(boolean click) {
        isClick = click;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getCoverId() {
        return coverId;
    }

    public void setCoverId(String coverId) {
        this.coverId = coverId;
    }

    public String getCoverWidth() {
        return coverWidth;
    }

    public void setCoverWidth(String coverWidth) {
        this.coverWidth = coverWidth;
    }

    public String getCoverHeight() {
        return coverHeight;
    }

    public void setCoverHeight(String coverHeight) {
        this.coverHeight = coverHeight;
    }

    public String getSha1() {
        return sha1;
    }

    public void setSha1(String sha1) {
        this.sha1 = sha1;
    }

    @Override
    public String toString() {
        return "CloudPicData{" +
                "catalogId='" + catalogId + '\'' +
                ", name='" + name + '\'' +
                ", fileId='" + fileId + '\'' +
                ", isClick=" + isClick +
                ", width=" + width +
                ", height=" + height +
                ", type='" + type + '\'' +
                ", format='" + format + '\'' +
                ", coverId='" + coverId + '\'' +
                ", coverWidth='" + coverWidth + '\'' +
                ", coverHeight='" + coverHeight + '\'' +
                '}';
    }
}
