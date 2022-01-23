package com.bcnetech.hyphoto.ui.popwindow;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bcnetech.bcnetechlibrary.popwindow.BasePopWindow;
import com.bcnetech.hyphoto.R;
import com.bcnetech.hyphoto.sql.dao.LightRatioData;
import com.bcnetech.hyphoto.ui.adapter.LightAdapter;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by yhf on 2017/4/28.
 */

public class LightPenPop extends BasePopWindow {

    private Activity activity;
    private TextView tv_save;
    private ImageView image;
    private RelativeLayout content;
    private LightPenInterface lightPenInterface;
    private RecyclerView light_recycler;
    private List<LightRatioData> list;
    private LightAdapter adapter;



    public LightPenPop(Activity activity,LightPenInterface lightPenInterface) {
        super(activity);
        this.activity = activity;
        this.lightPenInterface=lightPenInterface;
        initView(activity);
        initData();
        onViewClick();
        initAlpAnim(content);
        initBottomAnim(content);
    }

    @Override
    public void dismissPop() {
        outAnimation.start();
        alpOutAnim.start();
    }

    @Override
    public void showPop(final View view) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(image, "alpha", 1f, 0f, 1f);
        animator.setDuration(100);
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                inAnimation.start();
                alpInAnim.start();
                showAtLocation(view, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator.start();
    }

    private void initView(Activity activity) {
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.light_pen_view_layout, null);
        content= (RelativeLayout) view.findViewById(R.id.content);
        tv_save= (TextView) view.findViewById(R.id.tv_save);
        image= (ImageView) view.findViewById(R.id.image);
        light_recycler= (RecyclerView) view.findViewById(R.id.light_recycler);
        setContentView(view);
    }

    private void initData() {
//        (new Handler()).post(new Runnable() {
//            @Override
//            public void run() {
//                applyBlur(image);
//            }
//        });
        list=new ArrayList<>();
        adapter=new LightAdapter(activity,list);
        light_recycler.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
        light_recycler.setItemAnimator(new DefaultItemAnimator());
        light_recycler.setAdapter(adapter);
        adapter.setLightInterface(new LightAdapter.LightInterface() {
            @Override
            public void itemClick(int position, LightAdapter.ViewHolder viewHolder) {
                lightPenInterface.onClick(position);
            }
        });
    }

    private void onViewClick(){
        tv_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lightPenInterface.onSave();
            }
        });
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissPop();
            }
        });
    }

    public void setData(List<LightRatioData> lightRatioDatas){
        list.clear();
        list.addAll(lightRatioDatas);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void initBottomAnim(View view) {
        super.initBottomAnim(view);
    }

    @Override
    protected void initAlpAnim(View view) {
        super.initAlpAnim(view);
    }

    public interface LightPenInterface{
        void onSave();
        void onClick(int position);
    }

    public void setLightPenInterface(LightPenInterface lightPenInterface) {
        this.lightPenInterface = lightPenInterface;
    }

}
