package com.bcnetech.hyphoto.data;

import java.io.Serializable;

/**
 * Created by wenbin on 2017/1/17.
 */

public class KeyValue implements Serializable{

    private String key;
    private String value;

    public KeyValue(){}

    public KeyValue(String key,String value){
        this.key=key;
        this.value=value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
