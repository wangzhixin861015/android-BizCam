package com.bcnetech.hyphoto.popwindow;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bcnetech.bcnetchhttp.bean.response.LoginedUser;
import com.bcnetech.bcnetechlibrary.blurkit.BlurCallable;
import com.bcnetech.bcnetechlibrary.blurkit.BlurUtil;
import com.bcnetech.bcnetechlibrary.popwindow.BasePopWindow;
import com.bcnetech.bcnetechlibrary.util.ThreadPoolUtil;
import com.bcnetech.hyphoto.R;

import java.lang.reflect.Method;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

/**
 * Created by yhf on 2017/2/22.
 */
public class ResetPswEngPop extends BasePopWindow {
    private RelativeLayout content;
    private ImageView pop_dismiss;
    private TextView retrieve_message_top;
    private TextView retrieve_message_mail;
    private TextView retrieve_message_bottom;
   // private TextView tv_resending;
    private ImageView image;

    private ResetPswEngPopListener resetPswPopListener;

    private Activity activity;

    public ResetPswEngPop(Activity activity) {
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
        View view = inflater.inflate(R.layout.pop_reset_psw_eng, null);
        content = (RelativeLayout) view.findViewById(R.id.content);
        pop_dismiss = (ImageView)view.findViewById(R.id.pop_dismiss);
        retrieve_message_top = (TextView) view.findViewById(R.id.retrieve_message_top);
        retrieve_message_mail = (TextView) view.findViewById(R.id.retrieve_message_mail);
        retrieve_message_bottom = (TextView) view.findViewById(R.id.retrieve_message_bottom);
       // tv_resending = (TextView)view.findViewById(R.id.tv_resending);
        image = (ImageView)view.findViewById(R.id.image);
        setContentView(view);
    }

    private void initData() {
        setApplyBlur();
        retrieve_message_top.setText("We have send you a Email，\n"+
                "Please check ");
        retrieve_message_mail.setText(LoginedUser.getLastLoginedUserInfo().getUser_name());
        retrieve_message_mail.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        retrieve_message_bottom.setText("To confirm your email address to retrieve your password.");

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

     /*   image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissPop();
            }
        });
*/
       /* tv_resending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (resetPswPopListener != null) {
                    resetPswPopListener.onResending();
                }
            }
        });*/
        pop_dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (resetPswPopListener != null) {
                    resetPswPopListener.onCancel();
                }
            }
        });

    }

   /* public void setMessage(String message) {
        this.message.setText(Html.fromHtml(message));
    }
*/


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


    public interface ResetPswEngPopListener {
        void onResending();

        void onCancel();

        void onClickMail();

    }

    public void setResetPswPopListener(Activity activity,ResetPswEngPopListener resetPswPopListener) {

        this.activity = activity;
        this.resetPswPopListener = resetPswPopListener;
    }


    /**获取虚拟功能键高度 */
    public int getVirtualBarHeigh() {
        int vh = 0;
        WindowManager windowManager = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        try {
            @SuppressWarnings("rawtypes")
            Class c = Class.forName("android.view.Display");
            @SuppressWarnings("unchecked")
            Method method = c.getMethod("getRealMetrics", DisplayMetrics.class);
            method.invoke(display, dm);
            vh = dm.heightPixels - windowManager.getDefaultDisplay().getHeight();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vh;
    }
}
