package com.bcnetech.hyphoto.ui.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bcnetech.bcnetchhttp.config.Flag;
import com.bcnetech.bcnetechlibrary.util.PreferenceUtil;
import com.bcnetech.hyphoto.App;
import com.bcnetech.hyphoto.R;
import com.bcnetech.hyphoto.presenter.MainPresenter;
import com.bcnetech.hyphoto.presenter.iview.IMainView;
import com.bcnetech.hyphoto.ui.activity.personCenter.SettingDetailActivity;
import com.bcnetech.hyphoto.utils.SPUtils;

/**
 * Created by yhf on 2017/11/20.
 */

public class MainActivity extends BaseMvpActivity<IMainView, MainPresenter> implements IMainView {

    private TextView tv_register;
    private TextView tv_login;
    private RelativeLayout rl_mian;
    private PreferenceUtil preferenceUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_new);
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
//        LangugeUtil.switchLanguage(this, Flag.CHINAE);
//        preferenceUtil = new PreferenceUtil(this);
//        preferenceUtil.commitString(Flag.LANGUAGE_KEY, Flag.CHINAE);
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
    public MainPresenter initPresenter() {
        return new MainPresenter();
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
