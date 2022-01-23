package com.bcnetech.hyphoto.ui.popwindow;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bcnetech.bcnetechlibrary.popwindow.BasePopWindow;
import com.bcnetech.hyphoto.R;

/**
 * Created by yhf on 17/10/17.
 */
public class IntoPop extends BasePopWindow {

    private ImageView image;
    private Activity activity;
    private IntoClickListener intoClickListener;
    private LinearLayout ll_native,ll_cloud;
    private TextView tv_phone,tv_album;





    public IntoPop(Activity activity) {
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
        View view = inflater.inflate(R.layout.into_view_layout, null);


        image = (ImageView)view.findViewById(R.id.image);
        content = (RelativeLayout)view.findViewById(R.id.content);
        ll_native= (LinearLayout) view.findViewById(R.id.ll_native);
        ll_cloud= (LinearLayout) view.findViewById(R.id.ll_cloud);
        tv_phone=view.findViewById(R.id.tv_phone);
        tv_album=view.findViewById(R.id.tv_album);

        setContentView(view);
    }

    private void initData() {

    }

    public void setText(){
        tv_phone.setText(activity.getResources().getString(R.string.take_photo));
        tv_album.setText(activity.getResources().getString(R.string.photo_album));
    }

    private void onViewClick() {
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissPop();
            }
        });

        ll_native.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intoClickListener.nativeFile();
            }
        });

        ll_cloud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intoClickListener.cloud();
            }
        });
    }

    public interface IntoClickListener{
        void nativeFile();
        void cloud();
    }



    public void setIntoClickListener(Activity activity,IntoClickListener intoClickListener){
        this.activity = activity;
        this.intoClickListener = intoClickListener;
    }
}