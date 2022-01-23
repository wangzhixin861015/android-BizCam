package com.bcnetech.hyphoto.ui.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.LinearLayout;

import com.bcnetech.hyphoto.R;
import com.bcnetech.hyphoto.ui.view.ChangePswView;
import com.bcnetech.hyphoto.ui.view.SettingAdviseView;
import com.bcnetech.hyphoto.ui.view.SettingUserAgreementView;
import com.bcnetech.hyphoto.ui.view.TitleView;

public class AgreeActivity extends FragmentActivity {
    private TitleView titleView;

    private SettingUserAgreementView settingagreeview;//用户协议

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.agree_layout);
        initView();
    }


    protected void initView() {
        titleView = (TitleView) findViewById(R.id.sett_title_view);
        settingagreeview = (SettingUserAgreementView) findViewById(R.id.sett_agree_view);

        titleView.setSettingTitle(getResources().getString(R.string.user_contract));
        titleView.setType(TitleView.SETTING);
        titleView.setLeftImg(R.drawable.arrow_back);
        settingagreeview.setVisibility(View.VISIBLE);
        settingagreeview.loadPage();
        titleView.setLeftListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}
