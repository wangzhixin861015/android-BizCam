package com.bcnetech.hyphoto.data;

import java.io.Serializable;
import java.util.List;

/**
 * Created by wenbin on 16/6/22.
 */
public class CloudPicture implements Serializable{


    /**
     * "code":"koutu","moduleType":1,"created":1482979117916,"ownerId":3541,"layer":1,"parentId":1822,"deleted":null,"children":null,"intro":"抠图相册","letter":null,"scope":1,"name":"抠图相册","id":1841,"updated":null,"fileId":"5481,5491,5801"
     */
    /**
     * {"code":"product","moduleType":1,"created":1488886053890,"ownerId":1168231104524,"layer":1,"parentId":1923,"deleted":0,"children":0,"intro":"产品图片","letter":null,"scope":1,"name":"产品图片","pagesize":10,"id":1168231106439,"page":1,"updated":null,"fileId":null}
     *
     * {
     "__filters": null,
     "pagesize": 10,
     "page": 1,
     "id": 1168231110963,
     "parentId": 1168231110963,
     "children": 0,
     "layer": 1,
     "moduleType": 1,
     "submitterId": null,
     "ownerId": 1168231105919,
     "scope": 1,
     "code": null,
     "name": "temp",
     "intro": "temp",
     "updated": null,
     "created": 1512719570151,
     "fileId": "1168231110983,1168231110982,1168231110981",
     "letter": "t",
     "deleted": 0,
     "sort": null,
     "size": null
     },
     */

    private String code;
    private String id;
    private String name;
    private int deleted;
    private String children;
    private boolean isselected = false;
    private String fileId;
    private String letter;
    private int count;
   // private List<String>  fileIds;

    public int getDeleted() {
        return deleted;
    }

    public boolean isselected() {
        return isselected;
    }

    public void setIsselected(boolean isselected) {
        this.isselected = isselected;
    }

    /**
     * id : 298
     * url : http://ebiztop-tst.oss-cn-hangzhou.aliyuncs.com/school_1/teacher3/pics/146734142371736.png
     */

    private List<PhotosBean> photos;

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

    public List<PhotosBean> getPhotos() {
        return photos;
    }

    public void setPhotos(List<PhotosBean> photos) {
        this.photos = photos;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    /* public List<String> getFileIds() {
        return fileIds;
    }

    public void setFileIds(List<String> fileIds) {
        this.fileIds = fileIds;
    }*/

    public String getLetter() {
        return letter;
    }

    public void setLetter(String letter) {
        this.letter = letter;
    }

    public static class PhotosBean implements Serializable{
        private int id;
        private String url;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

    public int isDeleted() {
        return deleted;
    }

    public void setDeleted(int deleted) {
        this.deleted = deleted;
    }

    public String getChildren() {
        return children;
    }

    public void setChildren(String children) {
        this.children = children;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "CloudPicture{" +
                "code='" + code + '\'' +
                ", id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", deleted=" + deleted +
                ", children='" + children + '\'' +
                ", isselected=" + isselected +
                ", fileId='" + fileId + '\'' +
                ", photos=" + photos +
                '}';
    }
}
