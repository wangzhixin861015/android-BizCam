package com.bcnetech.hyphoto.ui.popwindow;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bcnetech.bcnetechlibrary.blurkit.BlurCallable;
import com.bcnetech.bcnetechlibrary.blurkit.BlurUtil;
import com.bcnetech.bcnetechlibrary.popwindow.BasePopWindow;
import com.bcnetech.bcnetechlibrary.util.ThreadPoolUtil;
import com.bcnetech.hyphoto.data.SqlControl.CameraSettingData;
import com.bcnetech.hyphoto.ui.adapter.CameraSettingAdapter;
import com.bcnetech.hyphoto.ui.view.RecordSelectLayout;
import com.bcnetech.hyphoto.R;

import java.util.ArrayList;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

/**
 * Created by a1234 on 2017/11/30.
 */

public class CameraSettingPop extends BasePopWindow {
    private LinearLayout content;
    private ImageView image;
    private RecyclerView recycle;
    private Activity activity;
    private ArrayList<CameraSettingData> list;
    private CameraSettingAdapter cameraSettingAdapter;
    private RecordSelectLayout rec_select;
    private int customTime = -1;


    public CameraSettingPop(Activity activity, ArrayList<CameraSettingData> list) {
        super(activity);
        this.activity = activity;
        this.list = list;
        initView(activity);
        initData();
        onViewClick();
        initAlpAnim(content);
        initBottomAnim(content);
    }

    public void setList(ArrayList<CameraSettingData> list) {
        if (list.get(list.size() - 1).getDetailSize() != null) {
            if (list.get(list.size() - 1).getDetailSize().getWidth() == list.get(list.size() - 2).getDetailSize().getWidth() && list.get(list.size() - 1).getDetailSize().getHeight() == (list.get(list.size() - 2).getDetailSize().getHeight())) {
                list.remove(list.size() - 1);
            }
        }
        this.list = list;
        cameraSettingAdapter.setList(list);
    }


    @Override
    public void showPop(final View view) {
        initData();
        setApplyBlur();
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
        View view = inflater.inflate(R.layout.camera_setting_pop_layout, null);
        image = (ImageView) view.findViewById(R.id.image);
        content = (LinearLayout) view.findViewById(R.id.content);
        recycle = (RecyclerView) view.findViewById(R.id.recycle);
        rec_select = (RecordSelectLayout) view.findViewById(R.id.rec_select);
        setContentView(view);
    }

    private void initData() {
        cameraSettingAdapter = new CameraSettingAdapter(list, activity, new CameraSettingAdapter.ClickInterFace() {
            @Override
            public void onClick(int position) {
                rec_select.DisSelect();
                CameraSettingPop.this.dismissPop();
            }
        });
        recycle.setAdapter(cameraSettingAdapter);
        recycle.setLayoutManager(new LinearLayoutManager(activity));
        rec_select.setSelectListener(new RecordSelectLayout.SelectListener() {
            @Override
            public void onSeek(int time) {
                customTime = time;
            }

            @Override
            public void onSelect(boolean isSelect) {
                if (isSelect) {
                    CameraSettingPop.this.dismissPop();
                    cameraSettingAdapter.setNoSelect();
                }
            }
        });
    }

    public void setApplyBlur() {
        //applyBlur(blueToothListNewPopBinding.ivContent);
        BlurCallable blurCallable = new BlurCallable(BlurUtil.getViewCache(activity));
        FutureTask<Drawable> futureTask = new FutureTask(blurCallable);
        ThreadPoolUtil.execute(futureTask);
        try {
            image.setBackground(futureTask.get(3000, TimeUnit.MILLISECONDS));
            if (futureTask.isDone()) {
                futureTask.cancel(false);
            }
        } catch (Exception e) {
        }

    }


    public void setCostom() {
        rec_select.setCostom();
    }

    public void onCoustomSelect() {
        rec_select.onClickEd();
    }

    public void isShowSelect(boolean isShow) {
        if (isShow) {
            rec_select.setVisibility(View.VISIBLE);
        } else {
            rec_select.setVisibility(View.GONE);
        }
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
        return cameraSettingAdapter.getSelect();
    }

    public int getRecordTime_Custom() {
        return customTime;
    }
}

