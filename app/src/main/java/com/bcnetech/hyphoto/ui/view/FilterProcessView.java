package com.bcnetech.hyphoto.ui.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Scroller;

import com.bcnetech.bcnetechlibrary.util.ContentUtil;
import com.bcnetech.bcnetechlibrary.util.StringUtil;
import com.bcnetech.hyphoto.ui.view.scaleview.BaseScaleView;
import com.bcnetech.hyphoto.R;

import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * author: wsBai
 * date: 2018/12/13
 */
public class FilterProcessView extends BaseScaleView {
    public static int FILTERPROCESSCOUNT = 6;
    private ArrayList<Bitmap> list;
    private int SCROLLOVERRANGE = 50;//移动到临界点时的坐标偏移值
    private int initScale = 0;//中点值
    private Bitmap ProcessInit;//下三角箭头

    public FilterProcessView(Context context) {
        super(context);
    }

    public FilterProcessView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FilterProcessView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public FilterProcessView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void initVar() {
        mRectWidth = (mMax - mMin) * mScaleMargin;
        mRectHeight = (int) (mScaleHeight * 1.1);
        mScaleMaxHeight = (int) (mRectHeight * 1.5);
    }

    public void setBitmapList(ArrayList<Bitmap> list) {
        this.list = list;
        //总长度
        mScaleScrollViewRange = list.get(0).getWidth() * FILTERPROCESSCOUNT;
        //单位刻度长度
        mScaleMargin = list.get(0).getWidth() / 10;
        SCROLLOVERRANGE = list.get(0).getWidth() / 2;
        //中点坐标
        mMidCountScale = mMax - (mScaleScrollViewRange / mScaleMargin / 2);
        this.list = list;

        //初始化指针位置（初始位置在中点处）
        int finalX = mScaleScrollViewRange / 2;
        initScale = -(ContentUtil.getScreenWidth(getContext()) / 2 - (int) (FILTERPROCESSCOUNT/2 * list.get(0).getWidth()));
        mScroller.startScroll(mScroller.getFinalX(), mScroller.getFinalY(), initScale, 0);
    }


    @Override
    protected void onDrawLine(Canvas canvas, Paint paint) {
        //绘制中点箭头
        Drawable d = getResources().getDrawable(R.drawable.filter_init);
        BitmapDrawable bitmapDrawable = (BitmapDrawable) d;
        ProcessInit = bitmapDrawable.getBitmap();
        canvas.drawBitmap(ProcessInit, (int) (FILTERPROCESSCOUNT/2 * list.get(0).getWidth()) - ProcessInit.getWidth() / 2, 0, paint);
    }

    @Override
    protected void onDrawScale(Canvas canvas, Paint paint) {
        //绘制图片process
        for (int i = 0; i < list.size(); i++) {
            canvas.drawBitmap(list.get(i), i * list.get(i).getWidth(), ProcessInit.getHeight() + 10, paint);
        }
    }

    @Override
    protected void onDrawPointer(Canvas canvas, Paint paint) {
        //根据滑动的距离，计算指针的位置【指针始终位于屏幕中间】
        int finalX = mScroller.getFinalX();

        BigDecimal countPercent;
        if (finalX < 0) {
            countPercent = StringUtil.perToDecimal(StringUtil.division((ContentUtil.getScreenWidth(getContext()) / 2 - Math.abs(finalX)), mScaleScrollViewRange));
        } else {
            countPercent = StringUtil.perToDecimal(StringUtil.division((ContentUtil.getScreenWidth(getContext()) / 2 + Math.abs(finalX)), mScaleScrollViewRange));
        }
        int percent = countPercent.intValue();
        if (percent < 0) {
            percent = 0;
        } else if (percent > 100) {
            percent = 100;
        }
        if (mScrollListener != null) { //回调方法
            mScrollListener.onScaleScroll(percent);

        }
    }


    @Override
    public void scrollToScale(int val) {
        if (val < mMin || val > mMax) {
            return;
        }
        int finalX = mScroller.getFinalX();

        int dy = (val + mCountScale) * mScaleMargin;
        smoothScrollBy(dy, 0);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (mScroller != null && !mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
                mScrollLastX = x;
                return true;
            case MotionEvent.ACTION_MOVE:
                int dataX = mScrollLastX - x;
                if (mScroller.getFinalX() < -ContentUtil.getScreenWidth(getContext()) / 2 - SCROLLOVERRANGE) {
                    return super.onTouchEvent(event);
                } else if (mScroller.getFinalX() > 0) {
                    if ((mScroller.getFinalX() + ContentUtil.getScreenWidth(getContext()) / 2 > mScaleScrollViewRange + SCROLLOVERRANGE)) {
                        return super.onTouchEvent(event);
                    }
                }
                smoothScrollBy(dataX, 0);
                mScrollLastX = x;
                postInvalidate();
                return true;
            case MotionEvent.ACTION_UP:
                //右边临界点
                if (mScroller.getFinalX() < -ContentUtil.getScreenWidth(getContext()) / 2) {
                    mScroller.setFinalX(-ContentUtil.getScreenWidth(getContext()) / 2); //纠正指针位置
                    //左边临界点
                } else if (mScroller.getFinalX() > 0) {
                    if ((mScroller.getFinalX() + ContentUtil.getScreenWidth(getContext()) / 2 > mScaleScrollViewRange)) {
                        mScroller.setFinalX(Math.abs(ContentUtil.getScreenWidth(getContext()) / 2 - mScaleScrollViewRange)); //纠正指针位置
                    }
                }
                postInvalidate();
                return true;
        }
        return super.onTouchEvent(event);
    }

    public int getMax() {
        return this.mMax;
    }

    public int getMin() {
        return this.mMin;
    }

    /**
     * 重置为中点坐标
     */
    public void reset() {
        mScroller = new Scroller(getContext());
    }

}
