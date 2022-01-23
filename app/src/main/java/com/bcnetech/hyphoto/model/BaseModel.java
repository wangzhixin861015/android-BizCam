package com.bcnetech.hyphoto.model;

import android.app.Activity;

/**
 * Created by wenbin on 2017/2/22.
 */

public abstract class BaseModel<T> {
    public T mModel;

    public Activity activity;

    public void attach(T mModel,Activity activity) {
        this.mModel = mModel;
        this.activity=activity;
    }

    public void dettach() {
        mModel = null;
        activity=null;
    }


}
