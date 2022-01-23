package com.bcnetech.hyphoto.data;

import java.io.Serializable;

/**
 * Created by a1234 on 17/2/15.
 *
 *   {
 "data": "1111",
 "created": 1487662397821,
 "fromUserId": 5111,
 "title": "美食2",
 "fromId": 11,
 "version": 1,
 "tags": "123123",
 "cover": "1111",
 "catalogId": 1,
 "price": 111,
 "intro": "介绍",
 "pagesize": 10,
 "typeId": 1099511,
 "id": 1099511627882,
 "page": 1,
 "updated": null
 },
 */

public class MarketTemplateData implements Serializable{
    private String fromUserId;//作者id
    private String title;//标题
    private String fromId;//模版id
    private String cover;//封面img
    private String intro;//简介
    //需要传入的数据
    //{"title":"12312", "catalogId":111, "typeId":12312, "groupId":234234, "shareId":343, "shareAt":23423, "intro":"12312", "data":"2342423423"}
    private String catalogId;
    private String typeId;
    private String tags;//
    private String created;
    private String data;
    private int cover_width;
    private int cover_height;

    public String getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(String fromUserId) {
        this.fromUserId = fromUserId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFromId() {
        return fromId;
    }

    public void setFromId(String fromId) {
        this.fromId = fromId;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
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

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
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
        return "MarketTemplateData{" +
                "fromUserId='" + fromUserId + '\'' +
                ", title='" + title + '\'' +
                ", fromId='" + fromId + '\'' +
                ", cover='" + cover + '\'' +
                ", intro='" + intro + '\'' +
                ", catalogId='" + catalogId + '\'' +
                ", typeId='" + typeId + '\'' +
                ", tags='" + tags + '\'' +
                ", created='" + created + '\'' +
                ", data='" + data + '\'' +
                ", cover_width=" + cover_width +
                ", cover_height=" + cover_height +
                '}';
    }
}
