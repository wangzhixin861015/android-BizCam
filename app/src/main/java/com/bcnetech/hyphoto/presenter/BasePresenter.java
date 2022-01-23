package com.bcnetech.hyphoto.presenter;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by wenbin on 2017/2/17.
 */

public abstract class BasePresenter<T> {

    public T mView;

    public Activity activity;

    public void attach(T mView, Activity activity) {
        this.mView = mView;
        this.activity=activity;
    }

    public void dettach() {
        mView = null;
        activity=null;
    }

    public abstract void onCreate(Bundle bundle);

    public void onStart(){}

    public void onResume(){}

    public void onWindowFocusChanged(boolean hasFocus){}

    public void onPause() {}

    public void onStop(){}

    public void onRestart() {}

    public abstract void onDestroy();

    public void onSaveInstanceState(Bundle outState) {}

    public void onRestoreInstanceState(Bundle savedInstanceState) {}


}