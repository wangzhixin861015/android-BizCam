package com.bcnetech.hyphoto.ui.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;

import com.bcnetech.bcnetchhttp.config.Flag;
import com.bcnetech.bcnetechlibrary.util.AnimFactory;
import com.bcnetech.bcnetechlibrary.util.ContentUtil;
import com.bcnetech.bcnetechlibrary.view.BaseRelativeLayout;
import com.bcnetech.hyphoto.R;
import com.bcnetech.hyphoto.databinding.AihintLayoutBinding;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;


public class AIHintView extends BaseRelativeLayout {
    private int Count = 0;
    private AihintLayoutBinding aihintLayoutBinding;
    private ValueAnimator outAnim, inAnim;
    private AIHintCallback aiHintCallback;

    public static final int AICAMERA = 0;

    public static final int AI360_SUCCESS = 1;

    public static final int AI360_ERROR = 2;

    private int currentType = AICAMERA;


    public AIHintView(Context context) {
        super(context);
    }

    public AIHintView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AIHintView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void initData() {
        super.initData();
        initAnim();
    }

    @Override
    protected void initView() {
        super.initView();
        aihintLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.aihint_layout, this, true);
    }

    @Override
    protected void onViewClick() {
        super.onViewClick();
        aihintLayoutBinding.ivClose.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                aiHintCallback.onClose();
            }
        });
        aihintLayoutBinding.rlConfirm.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                aiHintCallback.onClick();
            }
        });
        aihintLayoutBinding.ivCobox.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                aiHintCallback.onCobox();
            }
        });
    }

    public void show() {
        inAnim.start();
    }

    public void dismiss() {
        outAnim.start();
    }

    public void setType(int type) {
        this.currentType = type;
        if (type == AI360_SUCCESS) {
            aihintLayoutBinding.tvHint1.setText(getResources().getString(R.string.ai_360_hint1));
            aihintLayoutBinding.tvHint2.setText(getResources().getString(R.string.ai_360_hint2));
            aihintLayoutBinding.ivBitmap.setImageResource(R.drawable.aihint_turntable);
            aihintLayoutBinding.tvButton.setText(getResources().getString(R.string.ai_360_hint3));
            aihintLayoutBinding.rlConfirm.setVisibility(VISIBLE);
            aihintLayoutBinding.ivHint.setVisibility(GONE);
            aihintLayoutBinding.ivCobox.setVisibility(GONE);

        } else if (type == AI360_ERROR) {
            aihintLayoutBinding.ivCobox.setVisibility(VISIBLE);
            aihintLayoutBinding.tvHint1.setText(getResources().getString(R.string.ai_360_hint4));
            aihintLayoutBinding.tvHint2.setText(getResources().getString(R.string.ai_360_hint5));
            aihintLayoutBinding.ivBitmap.setImageResource(R.drawable.aihint_turntable);
            aihintLayoutBinding.ivHint.setImageResource(R.drawable.aihint_error);
            aihintLayoutBinding.ivHint.setVisibility(VISIBLE);

            if (Flag.isEnglish) {
                aihintLayoutBinding.rlConfirm.setVisibility(INVISIBLE);
            } else {
                aihintLayoutBinding.rlConfirm.setVisibility(VISIBLE);
            }
            aihintLayoutBinding.tvButton.setText(getResources().getString(R.string.ai_360_hint6));
        } else if (type == AICAMERA) {
            Glide.with(getContext())
                    .load(R.drawable.aiphoto)
                    .asGif()
                    .dontAnimate()
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(aihintLayoutBinding.ivBitmap);
            aihintLayoutBinding.rlConfirm.setVisibility(VISIBLE);
            aihintLayoutBinding.ivCobox.setVisibility(GONE);
            aihintLayoutBinding.tvButton.setText(getContext().getString(R.string.ai_start));
        }
    }

    private void initAnim() {
        int hight = ContentUtil.getScreenHeight2(getContext());
        outAnim = AnimFactory.tranYBottomOutAnim(this, hight);
        outAnim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                setVisibility(GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        inAnim = AnimFactory.tranYBottomInAnim(this, hight);
        inAnim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                setVisibility(VISIBLE);

            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    public int getCurrentType() {
        return currentType;
    }


    public interface AIHintCallback {
        void onClose();

        void onClick();

        void onCobox();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }

    public void setInterface(AIHintCallback aiHintCallback) {
        this.aiHintCallback = aiHintCallback;
    }
}
