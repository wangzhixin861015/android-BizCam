package com.bcnetech.hyphoto.ui.view.scaleview;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;

/**
 * @author LichFaker on 16/3/12.
 * @Email lichfaker@gmail.com
 */
public class VerticalScaleScrollView extends BaseScaleView {

    private int default_inde;
    //顶部临界值
    private int topCount;
    //底部临界值
    private int bottomCount;

    private float size=1;

    private int rangeSize=1;

    public VerticalScaleScrollView(Context context) {
        super(context);
    }

    public VerticalScaleScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VerticalScaleScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public VerticalScaleScrollView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void initVar() {
        mRectHeight = (mMax - mMin) * mScaleMargin;
        mRectWidth = mScaleHeight * 8;
        mScaleMaxHeight = mScaleHeight * 2;

        // 设置layoutParams
        ViewGroup.MarginLayoutParams lp = new ViewGroup.MarginLayoutParams(mRectWidth, mRectHeight);
        this.setLayoutParams(lp);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mScaleScrollViewRange = getMeasuredHeight();
        mTempScale = mMax - (mScaleScrollViewRange / mScaleMargin / 2);
        mMidCountScale = mMax - (mScaleScrollViewRange / mScaleMargin / 2);

        topCount = (mMidCountScale - mMax) * mScaleMargin;
        bottomCount = (mMidCountScale - mMin) * mScaleMargin;

        //初始化指针位置
        int finalY = (mMidCountScale - default_inde) * mScaleMargin;
        mScroller.setFinalY(finalY);
        postInvalidate();
    }

    @Override
    protected void onDrawLine(Canvas canvas, Paint paint) {
        canvas.drawLine(0, 0, 0, mRectHeight, paint);
    }

    @Override
    protected void onDrawScale(Canvas canvas, Paint paint) {
        paint.setTextSize(mRectWidth / 4);

        for (int i = 0, k = mMax; i <= mMax - mMin; i++) {
            if (i % 10 == 0) { //整值
                canvas.drawLine(0, i * mScaleMargin, mScaleMaxHeight, i * mScaleMargin, paint);
                //整值文字
                canvas.drawText(String.valueOf(k*rangeSize), mScaleMaxHeight + 40, i * mScaleMargin + paint.getTextSize() / 3, paint);
                k -= 10;
            } else {
                canvas.drawLine(0, i * mScaleMargin, mScaleHeight, i * mScaleMargin, paint);
            }
        }
    }

    public void resize(float size){
        this.size=size;
//        invalidate();
    }

    public int getRangeSize() {
        return rangeSize;
    }

    public void setRangeSize(int rangeSize) {
        this.rangeSize = rangeSize;
    }

    @Override
    protected void onDrawPointer(Canvas canvas, Paint paint) {

        //每一屏幕刻度的个数/2
        int countScale = mScaleScrollViewRange / mScaleMargin / 2;

        //根据滑动的距离，计算指针的位置【指针始终位于屏幕中间】
        int finalY = mScroller.getFinalY();

        //滑动的刻度数
        int tmpCountScale = (int) Math.rint((double) finalY / (double) mScaleMargin); //四舍五入取整
        //总刻度
        mCountScale = mMax - (tmpCountScale + countScale);
        if (mCountScale > mMax) {
            //纠正指针位置
            mCountScale = mMax;

        } else if (mCountScale < mMin) {
            //纠正指针位置
            mCountScale = mMin;
        }
        if (mScrollListener != null) { //回调方法
            mScrollListener.onScaleScroll(mCountScale*rangeSize);
        }
    }


    @Override
    public void scrollToScale(int val) {
        if (val < mMin || val > mMax) {
            return;
        }
        int finalY = mScroller.getFinalY();

        int dy = (mCountScale - val) * mScaleMargin;
        smoothScrollBy(0, dy);
        mCountScale=val;
    }

    public void setFinalY(int var) {
        this.default_inde = var;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int y = (int) (event.getY()*size);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (mScroller != null && !mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
                mScrollLastX = y;
                return true;
            case MotionEvent.ACTION_MOVE:
                int dataY = mScrollLastX - y;
//                if (mTempScale - mCountScale <= 0) { //向上边滑动
//                    if (mCountScale == mMax && dataY > 0) //禁止继续向上滑动
//                        return super.onTouchEvent(event);
//                } else if (mTempScale - mCountScale >= 0) { //向下边滑动
//                    if (mCountScale == mMin && dataY < 0) //禁止继续向下滑动
//                        return super.onTouchEvent(event);
//                }


                if (mScroller.getFinalY() - dataY <= topCount) {
                    scrollToScale(mMax);
                    mCountScale = mMax;
                    return super.onTouchEvent(event);
                }
                if (mScroller.getFinalY() - dataY >= bottomCount) {
                    scrollToScale(mMin);
                    mCountScale = mMin;
                    return super.onTouchEvent(event);
                }
                smoothScrollBy(0, -dataY);
                mScrollLastX = y;
                postInvalidate();
                mTempScale = mCountScale;
                return true;
            case MotionEvent.ACTION_UP:
                if (mCountScale < mMin) mCountScale = mMin;
                if (mCountScale > mMax) mCountScale = mMax;
                int finalY = (mMidCountScale - mCountScale) * mScaleMargin;
                mScroller.setFinalY(finalY); //纠正指针位置
                postInvalidate();
                return true;
        }
        return super.onTouchEvent(event);
    }
}