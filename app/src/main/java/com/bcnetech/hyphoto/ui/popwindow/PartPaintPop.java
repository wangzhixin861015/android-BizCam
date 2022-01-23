package com.bcnetech.hyphoto.ui.popwindow;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.bcnetech.bcnetechlibrary.popwindow.BasePopWindow;
import com.bcnetech.bcnetechlibrary.popwindow.interfaces.PopWindowInterface;
import com.bcnetech.bcnetechlibrary.util.ContentUtil;
import com.bcnetech.hyphoto.imageinterface.BizImageMangage;
import com.bcnetech.hyphoto.ui.adapter.PartPaintPopAdapter;
import com.bcnetech.hyphoto.R;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by wenbin on 16/10/28.
 */

public class PartPaintPop extends BasePopWindow {
    private RecyclerView listview;
    private ImageView paint_cencel_pop;
    private ImageView paint_ok_pop;
    private View view;

    private List<ItemData> datas;
    private Activity activity;
    private PopWindowInterface popWindowInterface;
    private PartPaintPopAdapter adapter;

    public PartPaintPop(Activity activity,PopWindowInterface popWindowInterface) {
        super(activity);
        this.activity=activity;
        this.popWindowInterface=popWindowInterface;
        initView();
        initData();
        onViewClick();
    }
    private void initView(){
        LayoutInflater inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.part_paint_pop,null);
        listview=(RecyclerView) view.findViewById(R.id.listview);
        paint_cencel_pop=(ImageView) view.findViewById(R.id.paint_cencel_pop);
        paint_ok_pop=(ImageView) view.findViewById(R.id.paint_ok_pop);
        setContentView(view);
    }

    private void initData(){
        newDatas();
        initBottomAnim(view, ContentUtil.dip2px(activity,56));
        initAlpAnim(view);
        listview.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
        listview.setHasFixedSize(true);
        adapter=new PartPaintPopAdapter(activity,datas);
        listview.setAdapter(adapter);
    }

    private void onViewClick(){
        adapter.setClickInterFace(new PartPaintPopAdapter.ClickInterFace() {
            @Override
            public void onClickView(int poistion) {
                if(popWindowInterface!=null) {
                    popWindowInterface.OnWBClickListener(datas.get(poistion).getType());
                    dismissPop();
                }
            }
        });
        paint_ok_pop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        paint_cencel_pop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissPop();
            }
        });

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissPop();
            }
        });
        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                dismissPop();
                if(popWindowInterface!=null) {
                    popWindowInterface.OnWBDismissListener();
                }
            }
        });
    }

    private void newDatas(){
        datas=new ArrayList<>();
        datas.add(new ItemData(BizImageMangage.PART_PAINT_EXPOSURE,false));
        datas.add(new ItemData(BizImageMangage.PART_PAINT_BRIGHTNESS,false));
        datas.add(new ItemData(BizImageMangage.PART_PAINT_SATURATION,false));
    }

    @Override
    public void showPop(View view) {
        showAtLocation(view, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        inAnimation.start();
        alpInAnim.start();
    }

    @Override
    public void dismissPop() {
        outAnimation.start();
        alpOutAnim.start();
    }


    public  static  class ItemData{
        private int type;
        private boolean isClick;

        public ItemData(){
        }

        public ItemData(int type,boolean isClick){
            this.isClick=isClick;
            this.type=type;
        }
        public boolean isClick() {
            return isClick;
        }

        public void setClick(boolean click) {
            isClick = click;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }
    }
}
