package com.bcnetech.hyphoto.ui.activity;

import android.os.Bundle;

import com.bcnetech.bcnetechlibrary.activity.BaseActivity;
import com.bcnetech.hyphoto.presenter.BasePresenter;

/**
 * Created by wenbin on 2017/2/17.
 */

public abstract class BaseMvpActivity<V,T extends BasePresenter<V>> extends BaseActivity {

    public T presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = initPresenter();
        presenter.attach((V)this,this);
        presenter.onCreate(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        presenter.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        presenter.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.onResume();

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        presenter.onWindowFocusChanged(hasFocus);
    }

    @Override
    protected void onPause() {
        super.onPause();
        presenter.onPause();

    }

    @Override
    protected void onStop() {
        super.onStop();
        presenter.onStop();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        presenter.onRestart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.dettach();
    }

    // 实例化presenter
    public abstract T initPresenter();

}