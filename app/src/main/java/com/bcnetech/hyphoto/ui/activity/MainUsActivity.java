package com.bcnetech.hyphoto.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bcnetech.bcnetchhttp.config.Flag;
import com.bcnetech.bcnetechlibrary.util.PreferenceUtil;
import com.bcnetech.hyphoto.presenter.MainUsPresenter;
import com.bcnetech.hyphoto.presenter.iview.IMainUsView;
import com.bcnetech.hyphoto.R;

/**
 * Created by yhf on 2017/11/20.
 */

public class MainUsActivity extends BaseMvpActivity<IMainUsView, MainUsPresenter> implements IMainUsView {

    private TextView tv_register;
    private TextView tv_login;
    private RelativeLayout rl_mian;
    private PreferenceUtil preferenceUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_us_new);
        initView();
        initData();
        presenter.initData();
        onViewClick();
    }

    @Override
    protected void initView() {
        tv_register= (TextView) findViewById(R.id.tv_register);
        tv_login= (TextView) findViewById(R.id.tv_login);
        rl_mian= (RelativeLayout) findViewById(R.id.rl_main);
    }

    @Override
    protected void initData() {
//        LangugeUtil.switchLanguage(this, Flag.ENGLISH_US);
//        preferenceUtil = new PreferenceUtil(this);
//        preferenceUtil.commitString(Flag.LANGUAGE_KEY, Flag.ENGLISH_US);
    }

    @Override
    protected void onViewClick() {
        tv_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.showPop(Flag.TYPE_REGIST);
            }
        });

        tv_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.showPop(Flag.TYPE_LOGIN);
            }
        });
    }

    @Override
    protected void onDestroy() {
        presenter.onDestroy();
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return false;
    }

    @Override
    public MainUsPresenter initPresenter() {
        return new MainUsPresenter();
    }



    @Override
    public void finishView(int resultCode, Intent intent) {

    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }
}
