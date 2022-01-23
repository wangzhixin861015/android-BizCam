package com.bcnetech.hyphoto.data;

import com.bcnetech.hyphoto.sql.dao.PresetParm;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/3/6.
 */
public class PresetItem implements Serializable {

    private String presetId;

    //示例图片地址
    private String path;
    //名字
    private String name;
    //是否选中
    private boolean ischeck;

    private PresetParm presetParm;

    public PresetItem(String name, String path,String presetId) {
        this.name = name;
        this.path = path;
        this.presetId = presetId;
    }

    public boolean ischeck() {
        return ischeck;
    }

    public void setIscheck(boolean ischeck) {
        this.ischeck = ischeck;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PresetParm getPresetParm() {
        return presetParm;
    }

    public void setPresetParm(PresetParm presetParm) {
        this.presetParm = presetParm;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPresetId() {
        return presetId;
    }

    public void setPresetId(String presetId) {
        this.presetId = presetId;
    }
}
