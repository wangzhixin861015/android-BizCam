package com.bcnetech.hyphoto.ui.view;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import com.bcnetech.bcnetechlibrary.view.BaseRelativeLayout;
import com.bcnetech.hyphoto.databinding.CameraWelcomeLayoutBinding;
import com.bcnetech.hyphoto.R;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by a1234 on 2018/8/3.
 */

public class CameraWelcomeView extends BaseRelativeLayout {
    CameraWelcomeLayoutBinding cameraWelcomeLayoutBinding;
    private Animation animation;
    private boolean isFirst = true;
    public CameraWelcomeView(Context context) {
        super(context);
    }

    public CameraWelcomeView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CameraWelcomeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void initView() {
        cameraWelcomeLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.camera_welcome_layout,this,true);
        super.initView();
    }

    @Override
    protected void initData() {
        initAnim();
        super.initData();

    }

    public void startAnim(){
        if (isFirst) {
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                public void run() {
                    animation.start();
                }
            }, 1000L);
        }
    }

    @Override
    protected void onViewClick() {
        super.onViewClick();
    }
    private void initAnim(){
        animation = new AlphaAnimation(1,0);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                CameraWelcomeView.this.setVisibility(GONE);
                isFirst = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        animation.setDuration(1500);
        CameraWelcomeView.this.setAnimation(animation);

    }
}
