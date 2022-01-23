package com.bcnetech.bcnetchhttp.bean.response;

import java.util.List;

/**
 * Created by yhf on 2017/3/28.
 */
public class PresetDownManageData {

    private int page;
    private int pagesize;
    private int total;

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

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<ParamList> paramList;

    public List<ParamList> getParamList() {
        return paramList;
    }

    public void setParamList(List<ParamList> paramList) {
        this.paramList = paramList;
    }

    public class ParamList{
        private String userId;
        private String fileId;
        private String name;
        private String submitterId;
        private String submitterName;
        private String imageHeight;
        private String imageWidth;
        private String id;
        private String categoryId;
        private String autherUrl;
        private String coverId;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getSubmitterId() {
            return submitterId;
        }

        public void setSubmitterId(String submitterId) {
            this.submitterId = submitterId;
        }

        public String getSubmitterName() {
            return submitterName;
        }

        public void setSubmitterName(String submitterName) {
            this.submitterName = submitterName;
        }

        public String getFileId() {
            return fileId;
        }

        public void setFileId(String fileId) {
            this.fileId = fileId;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
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

        public String getAutherUrl() {
            return autherUrl;
        }

        public void setAutherUrl(String autherUrl) {
            this.autherUrl = autherUrl;
        }

        public String getCoverId() {
            return coverId;
        }

        public void setCoverId(String coverId) {
            this.coverId = coverId;
        }
    }




}
