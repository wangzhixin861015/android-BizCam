package com.bcnetech.hyphoto.data;

/**
 * Created by wangzhixin on 2018/3/27.
 */

public class CameraParamType {
    private int type;
    private Long max;
    private Long min;
    private float focusMin;


    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Long getMax() {
        return max;
    }

    public void setMax(Long max) {
        this.max = max;
    }

    public Long getMin() {
        return min;
    }

    public void setMin(Long min) {
        this.min = min;
    }

    public float getFocusMin() {
        return focusMin;
    }

    public void setFocusMin(float focusMin) {
        this.focusMin = focusMin;
    }
}
