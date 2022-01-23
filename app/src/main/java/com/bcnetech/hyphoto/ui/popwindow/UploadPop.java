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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.bcnetech.bcnetechlibrary.blurkit.BlurCallable;
import com.bcnetech.bcnetechlibrary.blurkit.BlurUtil;
import com.bcnetech.bcnetechlibrary.popwindow.BasePopWindow;
import com.bcnetech.bcnetechlibrary.util.ThreadPoolUtil;
import com.bcnetech.hyphoto.R;

import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

/**
 * Created by yhf on 17/10/17.
 */
public class UploadPop extends BasePopWindow {

    private ImageView image;
    private Activity activity;
    private UploadClickListener uploadClickListener;
    private LinearLayout ll_flashDisk, ll_upload, ll_newFile, ll_share;


    public UploadPop(Activity activity) {
        super(activity);
        this.activity = activity;
        initView(activity);
        initData();
        onViewClick();
        initAlpAnim(content);
        initBottomAnim(content);
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
        View view = inflater.inflate(R.layout.upload_view_layout, null);


        image = (ImageView) view.findViewById(R.id.image);
        content = (RelativeLayout) view.findViewById(R.id.content);
        ll_flashDisk = (LinearLayout) view.findViewById(R.id.ll_flashDisk);
        ll_upload = (LinearLayout) view.findViewById(R.id.ll_upload);
        ll_newFile = (LinearLayout) view.findViewById(R.id.ll_newFile);
        ll_share = (LinearLayout) view.findViewById(R.id.ll_share);

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
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissPop();
            }
        });

        ll_newFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadClickListener.newFileUpload();
            }
        });

        ll_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadClickListener.upload();
            }
        });

        ll_flashDisk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadClickListener.flashDisk();
            }
        });

        ll_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadClickListener.share();
            }
        });
    }

    public interface UploadClickListener {
        void newFileUpload();

        void upload();

        void flashDisk();

        void share();
    }


    public void setUploadClickListener(Activity activity, UploadClickListener uploadClickListener) {
        this.activity = activity;
        this.uploadClickListener = uploadClickListener;
    }
}