package com.bcnetech.hyphoto.ui.activity.personCenter;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import com.bcnetech.bcnetechlibrary.activity.BaseActivity;
import com.bcnetech.bcnetechlibrary.util.ToastUtil;
import com.bcnetech.bcnetechlibrary.view.help.OnStartDragListener;
import com.bcnetech.hyphoto.R;
import com.bcnetech.hyphoto.data.SqlControl.BaseSqlControl;
import com.bcnetech.hyphoto.data.SqlControl.LightRatioSqlControl;
import com.bcnetech.hyphoto.help.LithgItemTouchHelper;
import com.bcnetech.hyphoto.sql.dao.LightRatioData;
import com.bcnetech.hyphoto.ui.adapter.LightRatioAdapter;
import com.bcnetech.hyphoto.ui.view.TitleView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by wenbin on 16/8/16.
 */

public class LightRatioActivity extends BaseActivity {
    private TitleView title_layout;
    private RecyclerView lightratioList;

    private List<LightRatioData> list;

    private ItemTouchHelper itemTouchHelper;
    private LightRatioAdapter adapter;
    private LightRatioSqlControl queryHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.light_ratio_layout);
        initView();
        initData();
        onViewClick();
    }

    @Override
    protected void onStart() {
        super.onStart();
        startQuery();
    }

    protected void initView(){
        title_layout=(TitleView) findViewById(R.id.title_layout);
        lightratioList=(RecyclerView) findViewById(R.id.lightratioList);
    }


    protected void initData(){
        list=new ArrayList<>();
        queryHandler=new LightRatioSqlControl(this);

        title_layout.setType(TitleView.LIGHT_RATIO);
        title_layout.setRightText(getResources().getString(R.string.edits));
        lightratioList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        lightratioList.setHasFixedSize(true);
        adapter=new LightRatioAdapter(this, null, new OnStartDragListener() {
            @Override
            public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
                itemTouchHelper.startDrag(viewHolder);
            }

            @Override
            public void onStartSwip(RecyclerView.ViewHolder viewHolder) {
                itemTouchHelper.startSwipe(viewHolder);
            }

            @Override
            public void onEndDrag(int fromPosition, int toPosition) {

            }
        });
        lightratioList.setAdapter(adapter);
        itemTouchHelper = new ItemTouchHelper(new LithgItemTouchHelper(adapter));
        itemTouchHelper.attachToRecyclerView(lightratioList);
    }

    protected void onViewClick(){

        queryHandler.setListener(new BaseSqlControl.SqlControlListener() {
            @Override
            public void queryListener(Object... parms) {
                List list=(ArrayList)parms[0];
                Collections.sort(list, new Comparator<LightRatioData>() {
                    @Override
                    public int compare(LightRatioData lhs, LightRatioData rhs) {
                        if(lhs.getNum()<rhs.getNum()){
                            return 1;

                        }
                        else if(lhs.getNum()==rhs.getNum()){
                            return 0;
                        }

                        return -1;
                    }
                });
                adapter.setData(list);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void insertListener(Object... parms) {
                ToastUtil.toast(getResources().getString(R.string.save_ok));
            }

            @Override
            public void deletListener(Object... parms) {
                dissmissDialog();
            }

            @Override
            public void upDataListener(Object... parms) {

            }
        });
        title_layout.setLeftListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }


    //查询数据
    private void startQuery() {
        queryHandler.startQuery();
    }
    private void startDel(String id){

        queryHandler.startDel(new String[]{id});
    }



}
