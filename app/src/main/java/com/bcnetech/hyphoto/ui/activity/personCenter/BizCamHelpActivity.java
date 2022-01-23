package com.bcnetech.hyphoto.ui.activity.personCenter;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.bcnetech.bcnetchhttp.config.Flag;
import com.bcnetech.hyphoto.R;
import com.bcnetech.hyphoto.data.BizCamHelpData;
import com.bcnetech.hyphoto.presenter.BizCamHelpPresenter;
import com.bcnetech.hyphoto.presenter.iview.IBizCamHelpView;
import com.bcnetech.hyphoto.ui.activity.BaseMvpActivity;
import com.bcnetech.hyphoto.ui.adapter.BizCamHelpAdapter;
import com.bcnetech.hyphoto.ui.view.TitleView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by yhf on 2018/12/11.
 */

public class BizCamHelpActivity extends BaseMvpActivity<IBizCamHelpView,BizCamHelpPresenter>{

    private TitleView titleView;
    private RecyclerView rv_help;
    private List<BizCamHelpData> bizCamHelpDatas;
    private BizCamHelpAdapter  bizCamHelpAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bizcam_help);
        initView();
        initData();
        onViewClick();
    }

    @Override
    protected void initView() {
        titleView=findViewById(R.id.titleView);
        rv_help=findViewById(R.id.rv_help);
    }

    @Override
    protected void initData() {
        titleView.setType(TitleView.BIZCAM_HELP);
        titleView.setTitleText("提示和帮助");

        if(bizCamHelpDatas==null){
            bizCamHelpDatas=new ArrayList<>();
        }

        addHelpData();

        bizCamHelpAdapter=new BizCamHelpAdapter(this,bizCamHelpDatas);
        bizCamHelpAdapter.setInterFace(new BizCamHelpAdapter.BizCamHelpHolderInterFace() {
            @Override
            public void itemClick(int position, BizCamHelpAdapter.ViewHolder viewHolder) {
                Intent intent=new Intent(BizCamHelpActivity.this,BizCamHelpDetailActivity.class);
                intent.putExtra("bizCamHelpDatas", (Serializable)bizCamHelpDatas);
                intent.putExtra("position",position);
                startActivity(intent);
            }
        });
        //设置布局管理器
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rv_help.setLayoutManager(linearLayoutManager);
        rv_help.setAdapter(bizCamHelpAdapter);
    }

    @Override
    protected void onViewClick() {
        titleView.setLeftListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public BizCamHelpPresenter initPresenter() {
        return new BizCamHelpPresenter();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void addHelpData(){
        BizCamHelpData bizCamHelpAiCobox=new BizCamHelpData(Flag.BIZCAM_HELP_AI_COBOX);
        BizCamHelpData bizCamHelpAiCoLink=new BizCamHelpData(Flag.BIZCAM_HELP_AI_COLINK);
        BizCamHelpData bizCamHelpPaint=new BizCamHelpData(Flag.BIZCAM_HELP_PAINT);
        BizCamHelpData bizCamHelpMatting=new BizCamHelpData(Flag.BIZCAM_HELP_MATTING);
        BizCamHelpData bizCamHelpRepair=new BizCamHelpData(Flag.BIZCAM_HELP_REPAIR);

        bizCamHelpDatas.add(bizCamHelpAiCobox);
        bizCamHelpDatas.add(bizCamHelpAiCoLink);
        bizCamHelpDatas.add(bizCamHelpPaint);
        bizCamHelpDatas.add(bizCamHelpMatting);
        bizCamHelpDatas.add(bizCamHelpRepair);

    }


}
