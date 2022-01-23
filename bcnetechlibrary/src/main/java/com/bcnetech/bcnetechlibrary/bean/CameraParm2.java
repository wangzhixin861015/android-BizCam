package com.bcnetech.bcnetechlibrary.bean;

import android.hardware.camera2.params.RggbChannelVector;

public class CameraParm2 {
    private CameraParm cameraParm;
    private RggbChannelVector vector;
    public RggbChannelVector getVector() {
        return vector;
    }

    public void setVector(RggbChannelVector vector) {
        this.vector = vector;
    }

    public CameraParm getCameraParm() {
        return cameraParm;
    }

    public void setCameraParm(CameraParm cameraParm) {
        this.cameraParm = cameraParm;
    }
}
