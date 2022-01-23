package com.bcnetech.hyphoto.ui.popwindow;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bcnetech.bcnetechlibrary.popwindow.BasePopWindow;
import com.bcnetech.hyphoto.R;
import com.bcnetech.hyphoto.data.PopSelectData;
import com.bcnetech.hyphoto.ui.adapter.SelectAdapter;

import java.util.ArrayList;

/**
 * Created by a1234 on 2018/6/4.
 */

public class MarketFilterPop extends BasePopWindow {
    private LinearLayout content;
    private ImageView image;
    private RecyclerView recycle;
    private Activity activity;
    private ArrayList<PopSelectData> list;
    private SelectAdapter selectAdapter;
    private int customTime = -1;


    public MarketFilterPop(Activity activity, ArrayList<PopSelectData> list) {
        super(activity);
        this.activity = activity;
        this.list = list;
        initView(activity);
        initData();
        onViewClick();
        initAlpAnim(content);
        initBottomAnim(content);
    }

    public void setList(ArrayList<PopSelectData> list) {
        this.list = list;
        selectAdapter.setList(list);
    }


    @Override
    public void showPop(final View view) {
        initData();
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

    @Override
    protected void initBottomAnim(View view) {
        super.initBottomAnim(view);
    }

    @Override
    public void dismissPop() {
        outAnimation.start();
        alpOutAnim.start();
    }

    @Override
    protected void initAlpAnim(View view) {
        super.initAlpAnim(view);
    }

    private void initView(Activity activity) {
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.market_filter_pop_layout, null);
        image = (ImageView) view.findViewById(R.id.image);
        content = (LinearLayout) view.findViewById(R.id.content);
        recycle = (RecyclerView) view.findViewById(R.id.recycle);
        setContentView(view);
    }

    private void initData() {
        selectAdapter = new SelectAdapter(list, activity, new SelectAdapter.ClickInterFace(){
            @Override
            public void onClick(int position) {
            }
        });
        recycle.setAdapter(selectAdapter);
        recycle.setLayoutManager(new LinearLayoutManager(activity));
    }

    private void onViewClick() {
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissPop();
            }
        });
    }

    public int getSelectType() {
        return selectAdapter.getSelect();
    }

    public int getRecordTime_Custom() {
        return customTime;
    }
}

