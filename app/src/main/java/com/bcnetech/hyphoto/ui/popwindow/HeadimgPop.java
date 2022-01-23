package com.bcnetech.hyphoto.ui.popwindow;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bcnetech.bcnetechlibrary.blurkit.BlurCallable;
import com.bcnetech.bcnetechlibrary.blurkit.BlurUtil;
import com.bcnetech.bcnetechlibrary.popwindow.BasePopWindow;
import com.bcnetech.bcnetechlibrary.util.ThreadPoolUtil;
import com.bcnetech.hyphoto.R;

import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

/**
 * Created by a1234 on 16/8/23.
 */

public class HeadimgPop extends BasePopWindow {
    private TextView take_photo;
    private TextView system_ablum;
    private RelativeLayout content_layout;
    private ImageView image;


    private Activity activity;
    private HeadimgPopListener headimgPopListener;

    public HeadimgPop(Activity activity) {
        super(activity);
        this.activity = activity;
        initView(activity);
        initData();
        onViewClick();
        initAlpAnim(content_layout);
        initBottomAnim(content_layout);
    }

    public void SetbuttonName(String name1, String name2) {
        take_photo.setText(name1);
        system_ablum.setText(name2);
    }

    @Override
    public void showPop(final View view) {
        initData();
        ObjectAnimator animator = ObjectAnimator.ofFloat(image, "alpha", 1f, 0f, 1f);
        animator.setDuration(100);
        animator.start();
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
        View view = inflater.inflate(R.layout.headimg_view_layout, null);
        content_layout = (RelativeLayout) view.findViewById(R.id.content_layouts);
        take_photo = (TextView) view.findViewById(R.id.take_photo);
        system_ablum = (TextView) view.findViewById(R.id.system_ablum);
        image = (ImageView) view.findViewById(R.id.image);
        setContentView(view);
    }

    private void initData() {
        setApplyBlur();

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

    private void onViewClick() {
        take_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                headimgPopListener.onTake_photo();
            }
        });

        system_ablum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                headimgPopListener.onSystem_ablum();
            }
        });
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissPop();
            }
        });

    }

    public interface HeadimgPopListener {
        void onTake_photo();

        void onSystem_ablum();
    }

    public void setHeadimgPopListener(Activity activity, HeadimgPopListener headimgPopListener) {
        this.activity = activity;
        this.headimgPopListener = headimgPopListener;
    }
}