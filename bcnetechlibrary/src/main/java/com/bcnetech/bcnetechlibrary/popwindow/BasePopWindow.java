package com.bcnetech.bcnetechlibrary.popwindow;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;

import com.bcnetech.bcnetechlibrary.util.AnimFactory;


/**
 * Created by wenbin on 16/5/25.
 */
public abstract  class BasePopWindow extends PopupWindow {

    protected ValueAnimator inAnimation,outAnimation,alpInAnim,alpOutAnim;
    protected View content;
    protected int height=0;

    protected BasePopWindow(Activity activity){
        super(activity);
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setFocusable(true);
        this.setOutsideTouchable(true);
    height = getStatusBarHeight(activity);

        ColorDrawable dw = new ColorDrawable(0x00000000);
        this.setBackgroundDrawable(dw);
        this.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        this.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

    }
    /***
     * 底部弹出动画
     * @param view  全区上下文
     */
    protected void initBottomAnim(View view){

        inAnimation= AnimFactory.BottomInAnim(view);
        outAnimation=AnimFactory.BottomOutAnim(view);

        outAnimation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                if(inAnimation!=null){
                    inAnimation.cancel();
                }
                if(alpInAnim!=null){
                    alpInAnim.cancel();
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                dismiss();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    /***
     * 底部弹出动画
     * @param view  全区上下文
     */
    protected void initBottomAnim(View view,int y){

        inAnimation= AnimFactory.BottomInAnim(view,y);
        outAnimation=AnimFactory.BottomOutAnim(view,y);

        outAnimation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                if(inAnimation!=null){
                    inAnimation.cancel();
                }
                if(alpInAnim!=null){
                    alpInAnim.cancel();
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                dismiss();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }
    protected void initAlpAnim(View view){
        alpInAnim=AnimFactory.ImgAlphaAnim(view);
        alpOutAnim=AnimFactory.ImgAlphaAnimOut(view);
    }

    @Override
    public void showAtLocation(View parent, int gravity, int x, int y) {
        super.showAtLocation(parent, gravity, x, y-height);
    }

    public  int getStatusBarHeight(Activity activity) {
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        return frame.top;
    }

    public abstract void showPop(View view);
    public abstract void dismissPop();
}
