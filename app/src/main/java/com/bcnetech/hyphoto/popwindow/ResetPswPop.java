package com.bcnetech.hyphoto.popwindow;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Html;
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
 * Created by yhf on 2017/2/22.
 */
public class ResetPswPop extends BasePopWindow {
    private TextView title;
    private TextView message;
    private TextView phone;
    private TextView ok;
    private TextView cencel;
    private ImageView image;
    private RelativeLayout content;

    private ResetPswPopListener resetPswPopListener;

    private Activity activity;

    public ResetPswPop(Activity activity) {
        super(activity);
        this.activity = activity;
        initView(activity);
        initData();
        onViewClick();
        initAlpAnim(content);
        initBottomAnim(content);
    }

    private void initView(Activity activity) {
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.pop_reset_psw, null);
        content = (RelativeLayout) view.findViewById(R.id.content);
        image = (ImageView) view.findViewById(R.id.image);
        title = (TextView) view.findViewById(R.id.dialog_title);
        message = (TextView) view.findViewById(R.id.dialog_message);
        ok = (TextView) view.findViewById(R.id.dialog_ok);
        cencel = (TextView) view.findViewById(R.id.dialog_cencel);
        phone = (TextView) view.findViewById(R.id.dialog_phone);
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

       /* image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissPop();
            }
        });*/

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (resetPswPopListener != null) {
                    resetPswPopListener.onConfirm();
                }
            }
        });
        cencel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (resetPswPopListener != null) {
                    resetPswPopListener.onCancel();
                }
            }
        });

    }

    public void setMessage(String message) {
        this.message.setText(Html.fromHtml(message));
    }



    @Override
    public void dismissPop() {
        outAnimation.start();
        alpOutAnim.start();
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
    protected void initAlpAnim(View view) {
        super.initAlpAnim(view);
    }

    public interface ResetPswPopListener {
        void onConfirm();

        void onCancel();

    }

    public void setResetPswPopListener(Activity activity,ResetPswPopListener resetPswPopListener) {

        this.activity = activity;
        this.resetPswPopListener = resetPswPopListener;
    }

    public void setTitle(String title){
        this.title.setText(title);
    }
    public void setPhone(String phone){
        this.phone.setText(phone);
    }



    public void setOk(String text){
        ok.setText(text);
    }
    public void setCancel(String text){
        cencel.setText(text);
    }

}
