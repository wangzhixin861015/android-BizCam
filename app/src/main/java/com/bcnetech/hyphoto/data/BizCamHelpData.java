package com.bcnetech.hyphoto.data;

import java.io.Serializable;

/**
 * Created by yhf on 2018/12/11.
 */

public class BizCamHelpData implements Serializable{

    public BizCamHelpData() {
    }

    public BizCamHelpData(int type) {
        this.type = type;
    }

    private int type;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
