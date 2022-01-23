package com.bcnetech.hyphoto.ui.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;

import com.bcnetech.bcnetechlibrary.util.AnimFactory;
import com.bcnetech.bcnetechlibrary.util.ContentUtil;
import com.bcnetech.bcnetechlibrary.util.ThreadPoolUtil;
import com.bcnetech.bcnetechlibrary.view.BaseRelativeLayout;
import com.bcnetech.hyphoto.R;
import com.bcnetech.hyphoto.databinding.FilterwaitViewBinding;


import java.io.IOException;
import java.io.InputStream;

/**
 * author: wsBai
 * date: 2018/12/14
 */
public class FilterWaitView extends BaseRelativeLayout {
    private ValueAnimator inAnim, outAnim;
    private FilterwaitViewBinding filterwaitViewBinding;
    private FilterWaitViewListener listener;
    private AnimationDrawable anim;

    public FilterWaitView(Context context) {
        super(context);
    }

    public FilterWaitView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FilterWaitView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void initView() {
        filterwaitViewBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.filterwait_view, this, true);
    }

    @Override
    protected void onViewClick() {
        //filterwaitViewBinding.ivProcess.setGifResource(R.mipmap.filter_wait);
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        if (visibility == VISIBLE) {
            anim.start();
        }
    }

    public void init() {
        anim = new AnimationDrawable();
        ThreadPoolUtil.execute(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i <= 200; i++) {
                    try {
                        InputStream in = getContext().getAssets().open("frame_anim/" + "circle_" + i + ".jpg");
                        Drawable drawable = Drawable.createFromStream(in, "src");
                        anim.addFrame(drawable, 1);
                        anim.setOneShot(false);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        filterwaitViewBinding.ivProcess.setImageDrawable(anim);
    }


    private void initAnim() {
        outAnim = AnimFactory.tranYBottomOutAnim(this, ContentUtil.getScreenHeight(getContext()));
        outAnim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                FilterWaitView.this.setVisibility(GONE);
                if (listener != null)
                    listener.onClose();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                FilterWaitView.this.setVisibility(GONE);
                if (listener != null)
                    listener.onClose();
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        inAnim = AnimFactory.tranYBottomInAnim(this, ContentUtil.getScreenHeight(getContext()));
        inAnim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                FilterWaitView.this.setVisibility(VISIBLE);

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (listener != null)
                    listener.onShow();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                FilterWaitView.this.setVisibility(GONE);
                if (listener != null)
                    listener.onShow();
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    public void show(boolean isShow) {
        initAnim();
        if (isShow) {
            inAnim.start();
        } else {
            outAnim.start();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }

    public interface FilterWaitViewListener {
        void onClose();

        void onShow();
    }

    public void setFilterWaitViewListener(FilterWaitViewListener listener) {
        this.listener = listener;
    }
}
