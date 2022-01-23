package com.bcnetech.bcnetechlibrary.dialog;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.CardView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.bcnetech.bcnetechlibrary.blurkit.BlurCallable;
import com.bcnetech.bcnetechlibrary.blurkit.BlurUtil;
import com.bcnetech.bcnetechlibrary.util.ThreadPoolUtil;

import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

/**
 * 带高斯模糊背景的弹出框
 * Created by a1234 on 2018/8/27.
 */

public abstract class BaseBlurDialog implements View.OnClickListener {
    protected CardDialogCallback cardDialogCallback;
    protected Context context;
    private WindowManager windowManager;
    protected ViewGroup background;
    protected ValueAnimator valueAnimator;
    protected ViewGroup dialog_viewgroup;
    protected CardView cardView;
    protected TextView ok, cancel, dialog_title, dialog_message;
    private boolean isNeedBlur = true;
    public boolean isDismissed = false;

    public BaseBlurDialog(Context context, CardDialogCallback cardDialogCallback) {
        this.cardDialogCallback = cardDialogCallback;
        init(context);
    }

    public BaseBlurDialog(Context context) {
        init(context);
    }


    protected void init(Context context) {
        this.context = context;
        initView(context);
    }

    protected abstract void initView(Context context);

    protected abstract void setOk(String text);

    protected abstract void setCancel(String text);

    protected void setupWindowmanage(ViewGroup viewGroup) {
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        // 类型
        params.type = WindowManager.LayoutParams.TYPE_APPLICATION;
        // 设置flag
        int flags = WindowManager.LayoutParams.FLAG_FULLSCREEN;
        params.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN;
        // | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        // 如果设置了WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE，弹出的View收不到Back键的事件
        params.flags = flags;
        // 不设置这个弹出框的透明遮罩显示为黑色
        params.format = PixelFormat.TRANSLUCENT;
        // FLAG_NOT_TOUCH_MODAL不阻塞事件传递到后面的窗口
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        params.gravity = Gravity.CENTER;

        try {
            windowManager.addView(viewGroup, params);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (ok != null)
            ok.setOnClickListener(this);
        if (cancel != null)
            cancel.setOnClickListener(this);

    }

    public void show() {
        setupWindowmanage(dialog_viewgroup);
        if (isNeedBlur)
            setApplyBlur();
        startAnim(true);
    }

    public void setCardRadius(int radius) {
        cardView.setRadius(radius);
    }

    public void dismiss() {
        if (!isDismissed)
            startAnim(false);
    }

    public void close(){
        if (!isDismissed){
            windowManager.removeView(dialog_viewgroup);
        }

    }

    public void setTitle(String title) {
        this.dialog_title.setText(title);
    }

    public void setMessage(String phone) {
        this.dialog_message.setText(phone);
    }


    private void setApplyBlur() {
        BlurCallable blurCallable = new BlurCallable(BlurUtil.getViewCache((Activity) context));
        FutureTask<Drawable> futureTask = new FutureTask(blurCallable);
        ThreadPoolUtil.execute(futureTask);
        try {
            background.setBackground(futureTask.get(3000, TimeUnit.MILLISECONDS));
            if (futureTask.isDone())
                futureTask.cancel(false);
        } catch (Exception e) {
        }
    }

    private void startAnim(final boolean isShow) {
        int startTime, endTime;
        if (isShow) {
            startTime = 0;
            endTime = 1;
        } else {
            startTime = 1;
            endTime = 0;
        }
        valueAnimator = ValueAnimator.ofFloat(startTime, endTime);
        valueAnimator.setDuration(300);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                cardView.setAlpha((float) animation.getAnimatedValue());
            }
        });
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (!isShow) {
                    /*ChoiceDialog.this.dialog_message.setText("");
                    ChoiceDialog.this.dialog_title.setText("");
                    ChoiceDialog.this.cancel.setText("");
                    ChoiceDialog.this.ok.setText("");*/
                    try {
                        windowManager.removeView(dialog_viewgroup);
                    } catch (Exception e) {

                    }
                    if (cardDialogCallback != null)
                        cardDialogCallback.onDismiss();
                    isDismissed = true;
                } else {
                    isDismissed = false;
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        valueAnimator.start();
    }

    public void isNeedBlur(boolean needBlur) {
        isNeedBlur = needBlur;
    }

    protected void setCanceledOnTouchOutside(final boolean isCanCancel) {
        dialog_viewgroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isCanCancel) {
                    dismiss();
                }
            }
        });
    }


    public interface CardDialogCallback {
        void onOKClick();

        void onCancelClick();

        void onDismiss();
    }

    public void setChoiceCallback(CardDialogCallback cardDialogCallback) {
        this.cardDialogCallback = cardDialogCallback;
    }



}
