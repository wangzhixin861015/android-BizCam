package com.bcnetech.hyphoto.data;

import java.io.Serializable;

/**
 * Created by dd on 16/1/13.
 * 坐标实体
 */
public class Coordinates implements Serializable {

    private float x;
    private float y;

    public Coordinates(float x, float y){
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }
}
