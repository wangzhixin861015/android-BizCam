package com.bcnetech.bcnetchhttp.bean.response;

import java.io.Serializable;
import java.util.List;

/**
 * Created by a1234 on 17/2/28.
 */

public class MarketParamsListData implements Serializable{
    private int total;
    private int page;
    private int pagesize;
    private List<PresetParmManageItem> paramList;

    public class PresetParmManageItem implements Serializable{
        //"autherId":"5041","name":"1484013962323","id":"7781","url":"7781","autherName":""
        String id;   //预设参数ID
        String name;  //预设参数名字
        String url;       //预设参数图片url(老版本)
        String autherName;  //作者名字
        String autherId;   //作者id
        String autherUrl;  //作者头像
        int downloadCount; //下载量
        String deviceType; //设备
        String imageHeight; //高
        String imageWidth; //宽
        String coboxName; //设备名称
        String categoryId; //文件ID
        String isDownload;//是否下载
        //以下用于对比缓存数据
        String catalogId;
        String fileId;  //参数Id
        String coverId; //预设参数图片

        public String getIsDownload() {
            return isDownload;
        }

        public void setIsDownload(String isDownload) {
            this.isDownload = isDownload;
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



        public String getAutherName() {
            return autherName;
        }

        public void setAutherName(String autherName) {
            this.autherName = autherName;
        }

        public String getAutherId() {
            return autherId;
        }

        public void setAutherId(String autherId) {
            this.autherId = autherId;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getAutherUrl() {
            return autherUrl;
        }

        public void setAutherUrl(String autherUrl) {
            this.autherUrl = autherUrl;
        }

        public int getDownloadCount() {
            return downloadCount;
        }

        public void setDownloadCount(int downloadCount) {
            this.downloadCount = downloadCount;
        }

        public String getDeviceType() {
            return deviceType;
        }

        public void setDeviceType(String deviceType) {
            this.deviceType = deviceType;
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

        public String getCoboxName() {
            return coboxName;
        }

        public void setCoboxName(String coboxName) {
            this.coboxName = coboxName;
        }

        public String getCategoryId() {
            return categoryId;
        }

        public void setCategoryId(String categoryId) {
            this.categoryId = categoryId;
        }

        public String getCatalogId() {
            return catalogId;
        }

        public void setCatalogId(String catalogId) {
            this.catalogId = catalogId;
        }

        public String getFileId() {
            return fileId;
        }

        public void setFileId(String fileId) {
            this.fileId = fileId;
        }

        public String getCoverId() {
            return coverId;
        }

        public void setCoverId(String coverId) {
            this.coverId = coverId;
        }

        @Override
        public String toString() {
            return "PresetParmManageItem{" +
                    "id='" + id + '\'' +
                    ", name='" + name + '\'' +
                    ", url='" + url + '\'' +
                    ", autherName='" + autherName + '\'' +
                    ", autherId='" + autherId + '\'' +
                    ", autherUrl='" + autherUrl + '\'' +
                    ", downloadCount=" + downloadCount +
                    ", deviceType='" + deviceType + '\'' +
                    ", imageHeight='" + imageHeight + '\'' +
                    ", imageWidth='" + imageWidth + '\'' +
                    ", coboxName='" + coboxName + '\'' +
                    ", categoryId='" + categoryId + '\'' +
                    ", isDownload='" + isDownload + '\'' +
                    ", catalogId='" + catalogId + '\'' +
                    ", fileId='" + fileId + '\'' +
                    '}';
        }
    }




    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPagesize() {
        return pagesize;
    }

    public void setPagesize(int pagesize) {
        this.pagesize = pagesize;
    }

    public List<PresetParmManageItem> getList() {
        return paramList;
    }

    public void setList(List<PresetParmManageItem> list) {
        this.paramList = list;
    }

    @Override
    public String toString() {
        return "MarketParamsListData{" +
                "total=" + total +
                ", page=" + page +
                ", pagesize=" + pagesize +
                ", paramList=" + paramList +
                '}';
    }
}
