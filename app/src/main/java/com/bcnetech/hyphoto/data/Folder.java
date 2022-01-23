package com.bcnetech.hyphoto.data;


import com.bcnetech.hyphoto.utils.StringUtils;

import java.util.ArrayList;

/**
 * 图片文件夹实体类
 */
public class Folder {

    private String name;
    private ArrayList<SDCardMedia> SDCardMedias;

    public Folder(String name) {
        this.name = name;
    }

    public Folder(String name, ArrayList<SDCardMedia> SDCardMedias) {
        this.name = name;
        this.SDCardMedias = SDCardMedias;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<SDCardMedia> getSDCardMedias() {
        return SDCardMedias;
    }

    public void setSDCardMedias(ArrayList<SDCardMedia> SDCardMedias) {
        this.SDCardMedias = SDCardMedias;
    }

    public void addImage(SDCardMedia SDCardMedia) {
        if (SDCardMedia != null && StringUtils.isNotEmptyString(SDCardMedia.getPath())) {
            if (SDCardMedias == null) {
                SDCardMedias = new ArrayList<>();
            }
            SDCardMedias.add(SDCardMedia);
        }
    }

    @Override
    public String toString() {
        return "Folder{" +
                "name='" + name + '\'' +
                ", SDCardMedias=" + SDCardMedias +
                '}';
    }
}
