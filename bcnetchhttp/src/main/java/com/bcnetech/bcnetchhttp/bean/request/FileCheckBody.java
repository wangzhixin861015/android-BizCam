package com.bcnetech.bcnetchhttp.bean.request;

import java.util.List;

/**
 * Created by yhf on 2018/9/17.
 */

public class FileCheckBody {

    private String size;
    private String sha1;

    private List<Chunks> chunks;

    public class  Chunks{

        private String chunkSize;
        private String chunkSha1;

        public String getChunkSize() {
            return chunkSize;
        }

        public void setChunkSize(String chunkSize) {
            this.chunkSize = chunkSize;
        }

        public String getChunkSha1() {
            return chunkSha1;
        }

        public void setChunkSha1(String chunkSha1) {
            this.chunkSha1 = chunkSha1;
        }
    }

    private String name;

    private String fileName;
    private String format;
    private String contentType;
    private String scope;
    private String code;
    private String fileId;
    private String replaceId;

    public String getReplaceId() {
        return replaceId;
    }

    public void setReplaceId(String replaceId) {
        this.replaceId = replaceId;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getSha1() {
        return sha1;
    }

    public void setSha1(String sha1) {
        this.sha1 = sha1;
    }

    public List<Chunks> getChunks() {
        return chunks;
    }

    public void setChunks(List<Chunks> chunks) {
        this.chunks = chunks;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }
}
