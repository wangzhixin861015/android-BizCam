package com.bcnetech.bcnetchhttp.bean.response;

import java.util.List;

/**
 * Created by wangzhixin on 2018/4/17.
 */

public class BimageUploadingChunk {

    private String fileId;

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    private String catalogId;

    public String getCatalogId() {
        return catalogId;
    }

    public void setCatalogId(String catalogId) {
        this.catalogId = catalogId;
    }

    private List<BimageUploadingChunkList> list;

    public List<BimageUploadingChunkList> getList() {
        return list;
    }

    public void setList(List<BimageUploadingChunkList> list) {
        this.list = list;
    }

    public class BimageUploadingChunkList{

        private String chunkSha1;
        private String chunkIndex;
        private String chunkSize;


        public String getChunkSha1() {
            return chunkSha1;
        }

        public void setChunkSha1(String chunkSha1) {
            this.chunkSha1 = chunkSha1;
        }

        public String getChunkIndex() {
            return chunkIndex;
        }

        public void setChunkIndex(String chunkIndex) {
            this.chunkIndex = chunkIndex;
        }

        public String getChunkSize() {
            return chunkSize;
        }

        public void setChunkSize(String chunkSize) {
            this.chunkSize = chunkSize;
        }
    }





}
