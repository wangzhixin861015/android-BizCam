package com.bcnetech.hyphoto.sql.dao;

import com.bcnetech.hyphoto.data.HttpBaseData;

import java.io.Serializable;

/**
 * Created by wenbin on 2017/1/16.
 */

public class DownloadInfoData extends HttpBaseData implements Serializable {

    private int id;
    private int type;
    private int fileType;
    private String url;
    private String getParms;
    private String postParms;
    private String postFileParms;
    private Long uploadTime; //上传时间
    private String localUrl; //本地小图路径
    private String fileId;
    private String catalogId;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getGetParms() {
        return getParms;
    }

    public void setGetParms(String getParms) {
        this.getParms = getParms;
    }

    public String getPostParms() {
        return postParms;
    }

    public void setPostParms(String postParms) {
        this.postParms = postParms;
    }

    public String getPostFileParms() {
        return postFileParms;
    }

    public void setPostFileParms(String postFileParms) {
        this.postFileParms = postFileParms;
    }

    public Long getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(Long uploadTime) {
        this.uploadTime = uploadTime;
    }

    public String getLocalUrl() {
        return localUrl;
    }

    public void setLocalUrl(String localUrl) {
        this.localUrl = localUrl;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public int getFileType() {
        return fileType;
    }

    public void setFileType(int fileType) {
        this.fileType = fileType;
    }

    public String getCatalogId() {
        return catalogId;
    }

    public void setCatalogId(String catalogId) {
        this.catalogId = catalogId;
    }

    @Override
    public String toString() {
        return "DownloadInfoData{" +
                "id=" + id +
                ", type=" + type +
                ", url='" + url + '\'' +
                ", getParms='" + getParms + '\'' +
                ", postParms='" + postParms + '\'' +
                ", postFileParms='" + postFileParms + '\'' +
                ", uploadTime=" + uploadTime +
                ", localUrl='" + localUrl + '\'' +
                ", fileId='" + fileId + '\'' +
                '}';
    }
}
