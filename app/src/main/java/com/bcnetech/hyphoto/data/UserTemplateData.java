package com.bcnetech.hyphoto.data;

import java.io.Serializable;

/**
 * Created by a1234 on 17/2/15.
 *
 "list":[{"shareAt":1099511627879,"catalogId":1099511,"created":1487660012807,"groupId":1099511627879,"intro":"介绍","shareId":1099511627879,"typeId":1099511,"id":1168231104515,"ownerId":1168231104714,"title":"美食2","version":1}]
 */

public class UserTemplateData implements Serializable{
    private String shareAt;//作者id
    private String title;//标题
    private String shareId;
    private String id;
    private String ownerId;
    private String catalogId;
    private String typeId;
    private String groupId;//
    private String created;
    private String intro;
    private String version;
    private String cover;
    private int cover_width;
    private int cover_height;

    public String getShareAt() {
        return shareAt;
    }

    public void setShareAt(String shareAt) {
        this.shareAt = shareAt;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getShareId() {
        return shareId;
    }

    public void setShareId(String shareId) {
        this.shareId = shareId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getCatalogId() {
        return catalogId;
    }

    public void setCatalogId(String catalogId) {
        this.catalogId = catalogId;
    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public int getCover_width() {
        return cover_width;
    }

    public void setCover_width(int cover_width) {
        this.cover_width = cover_width;
    }

    public int getCover_height() {
        return cover_height;
    }

    public void setCover_height(int cover_height) {
        this.cover_height = cover_height;
    }

    @Override
    public String toString() {
        return "UserTemplateData{" +
                "shareAt='" + shareAt + '\'' +
                ", title='" + title + '\'' +
                ", shareId='" + shareId + '\'' +
                ", id='" + id + '\'' +
                ", ownerId='" + ownerId + '\'' +
                ", catalogId='" + catalogId + '\'' +
                ", typeId='" + typeId + '\'' +
                ", groupId='" + groupId + '\'' +
                ", created='" + created + '\'' +
                ", intro='" + intro + '\'' +
                ", version='" + version + '\'' +
                ", cover='" + cover + '\'' +
                ", cover_width='" + cover_width + '\'' +
                ", cover_height='" + cover_height + '\'' +
                '}';
    }
}
