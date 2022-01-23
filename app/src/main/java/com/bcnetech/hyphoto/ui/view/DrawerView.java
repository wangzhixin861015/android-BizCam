package com.bcnetech.hyphoto.ui.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;


/**
 * Created by a1234 on 2018/8/17.
 */
public class DrawerView extends ViewGroup {
    private int HandlerHeight;//把手高度
    private Rect childRect;//完全展开子view的rect大小
    private View childView;//包含的子view，仅允许有一个
    private DrawerStatus drawerStatus = DrawerStatus.DRAWERCLOSE;
    private ValueAnimator drawerAnim;
    private DrawerLister drawerLister;
    private OvershootInterpolator overshootInterpolator = new OvershootInterpolator(1.1f);
    private boolean isCanClick = true;

    public DrawerView(Context context) {
        super(context);
    }

    public DrawerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DrawerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    /**
     * 计算所有ChildView的宽度和高度 然后根据ChildView的计算结果，设置自己的宽和高
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int maxHeight = 0;
        int maxWidth = 0;
        int mLeftHeight = 0;//当前行已有子View中最高的那个的高度
        int mLeftWidth = 0;// 当前行中所有子View已经占有的宽度

        final int count = getChildCount();
        final int widthSize = MeasureSpec.getSize(widthMeasureSpec);


        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);

            if (child.getVisibility() == GONE)
                continue;

            measureChild(child, widthMeasureSpec, heightMeasureSpec);

            maxWidth += child.getMeasuredWidth();
            mLeftWidth += child.getMeasuredWidth();

            if (mLeftWidth > widthSize) {
                maxHeight += mLeftHeight;
                mLeftWidth = child.getMeasuredWidth();
                mLeftHeight = child.getMeasuredHeight();
            } else {
                mLeftHeight = Math.max(mLeftHeight, child.getMeasuredHeight());
            }

        }
        maxHeight += mLeftHeight;
        maxHeight = Math.max(maxHeight, getSuggestedMinimumHeight());
        maxWidth = Math.max(maxWidth, getSuggestedMinimumWidth());
        setMeasuredDimension(resolveSizeAndState(maxWidth, widthMeasureSpec, 0),
                resolveSizeAndState(maxHeight, heightMeasureSpec, 0));
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int count = getChildCount();
        //当前viewgroup只允许包含一个view
        if (count > 1)
            throw new IllegalArgumentException("DrawerView Only can contain one view");
        //左上角的顶点的坐标。
        final int childLeft = getPaddingLeft();
        final int childTop = getPaddingTop();
        //右边那条边的坐标。
        final int childRight = r - l - getPaddingRight();
        int curLeft, curTop, maxHeight;//准备用来layout子View的起点坐标, maxHeight代表当前行中最高的子View的高度，当需要换行时，curTop要加上该值

        maxHeight = 0;
        curLeft = childLeft;
        curTop = childTop;
        View child = getChildAt(0);
        if (child.getVisibility() == GONE)
            return;

        int curWidth, curHeight;
        curWidth = child.getMeasuredWidth();
        curHeight = child.getMeasuredHeight();
        if (curLeft + curWidth >= childRight) {
                    /*
                    需要移到下一行时，更新curLeft和curTop的值，使它们指向下一行的起点
                    同时将maxHeight清零。
                     */
            curLeft = childLeft;
            curTop += maxHeight;
            maxHeight = 0;
        }
        this.childView = child;
        childRect = new Rect(curLeft, curTop, curLeft + curWidth, curTop + curHeight);
        child.layout(curLeft, curTop + curHeight - HandlerHeight, curLeft + curWidth, childRect.bottom);
        //更新maxHeight和curLeft
           /* if (maxHeight < curHeight)
                maxHeight = curHeight;
            curLeft += curWidth;*/
    }

    public void changeStatus() {
        if (isCanClick) {
            isCanClick = false;
            if (drawerStatus == DrawerStatus.DRAWERCLOSE) {
                drawerStatus = DrawerStatus.DRAWEROPEN;
            } else {
                drawerStatus = DrawerStatus.DRAWERCLOSE;
            }
            initAnim();
        }
    }

    public void closeDrawer(boolean isAnim) {
        if (childView == null || childRect == null)
            return;
        drawerStatus = DrawerStatus.DRAWERCLOSE;
        if (isAnim) {
            initAnim();
        } else {
            DrawerView.this.childView.layout(childRect.left, childRect.bottom - HandlerHeight, childRect.right, childRect.bottom);
            drawerLister.onDrawStatus(false);
        }
    }

    public void OpenDrawer(boolean isAnim) {
        if (childView == null || childRect == null)
            return;
        drawerStatus = DrawerStatus.DRAWEROPEN;
        if (isAnim) {
            initAnim();
        } else {
            DrawerView.this.childView.layout(childRect.left, childRect.top, childRect.right, childRect.bottom);
            drawerLister.onDrawStatus(true);
        }
    }


    public void setHandlerHeight(int height) {
        this.HandlerHeight = height;
    }

    private void initAnim() {
        if (drawerAnim != null && drawerAnim.isRunning())
            return;
        switch (drawerStatus) {
            case DRAWEROPEN:
                drawerAnim = ValueAnimator.ofInt(childRect.bottom - HandlerHeight, childRect.top);
                break;
            case DRAWERCLOSE:
                drawerAnim = ValueAnimator.ofInt(childRect.top, childRect.bottom - HandlerHeight);
                break;
        }
        drawerAnim.setDuration(300);
        drawerAnim.setInterpolator(overshootInterpolator);
        drawerAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                DrawerView.this.childView.layout(childRect.left, (int) animation.getAnimatedValue(), childRect.right, childRect.bottom);
            }
        });
        drawerAnim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                if (drawerLister != null) {
                    drawerLister.onDrawStatus(drawerStatus == DrawerStatus.DRAWEROPEN ? true : false);
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                isCanClick = true;
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        drawerAnim.start();
    }

    public enum DrawerStatus {
        DRAWEROPEN, DRAWERCLOSE;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;
    }

    public interface DrawerLister {
        void onDrawStatus(boolean isOpen);
    }

    public boolean isDrawerOpen() {
        return this.drawerStatus == DrawerStatus.DRAWEROPEN;
    }

    public void setDrawerLister(DrawerLister drawerLister) {
        this.drawerLister = drawerLister;
    }
}
