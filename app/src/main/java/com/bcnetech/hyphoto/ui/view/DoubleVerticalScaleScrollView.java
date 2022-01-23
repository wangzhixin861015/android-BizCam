package com.bcnetech.hyphoto.ui.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;

import com.bcnetech.bcnetechlibrary.util.ContentUtil;
import com.bcnetech.hyphoto.ui.view.scaleview.BaseScaleView;
import com.bcnetech.hyphoto.R;

/**
 * @author LichFaker on 16/3/12.
 * @Email lichfaker@gmail.com
 */
public class DoubleVerticalScaleScrollView extends BaseScaleView {

    private int default_inde=0;
    public DoubleVerticalScaleScrollView(Context context) {
        super(context);
    }

    public DoubleVerticalScaleScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DoubleVerticalScaleScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public DoubleVerticalScaleScrollView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public int getDefault_inde() {
        return default_inde;
    }

    public void setDefault_inde(int default_inde) {
        this.default_inde = default_inde;
        int finalY = (default_inde-mMidCountScale ) * mScaleMargin;
        mScroller.setFinalY(finalY);
        postInvalidate();
    }

    @Override
    protected void initVar() {
        mRectHeight = ContentUtil.dip2px(getContext(),240);
        mRectWidth = ContentUtil.dip2px(getContext(),80);
        mScaleMaxHeight = mScaleHeight * 2;

        // 设置layoutParams
        ViewGroup.MarginLayoutParams lp = new ViewGroup.MarginLayoutParams(mRectWidth, mRectHeight);
        this.setLayoutParams(lp);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        int width = MeasureSpec.makeMeasureSpec(mRectWidth, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mScaleScrollViewRange = getMeasuredHeight();
        mTempScale = mScaleScrollViewRange / mScaleMargin / 2 + mMin;
        mMidCountScale = mScaleScrollViewRange / mScaleMargin / 2 + mMin;
        //初始化指针位置

    }

    @Override
    protected void onDrawLine(Canvas canvas, Paint paint) {
        //paint.setStrokeWidth(4);
        //canvas.drawLine(0, 0, 0, mRectHeight, paint);
        //canvas.drawLine(mRectWidth, 0, mRectWidth, mRectHeight, paint);
    }

    @Override
    protected void onDrawScale(Canvas canvas, Paint paint) {
        paint.setTextSize(mRectWidth / 7);

        for (int i = 0, k = mMax; i <= mMax - mMin; i++) {
            if (i % 10 == 0) { //整值
                paint.setStrokeWidth(7);
                canvas.drawLine(0, i * mScaleMargin, mScaleHeight+10, i * mScaleMargin, paint);
                canvas.drawLine(mRectWidth-mScaleHeight-10, i * mScaleMargin, mRectWidth, i * mScaleMargin, paint);
                //整值文字
                paint.setStrokeWidth(2);
                canvas.drawText(String.valueOf(k/10), mScaleMaxHeight + 80, i * mScaleMargin + paint.getTextSize() / 3, paint);
                k -= 10;
            } else {
                paint.setStrokeWidth(4);
                canvas.drawLine(0, i * mScaleMargin, mScaleHeight, i * mScaleMargin, paint);
                canvas.drawLine(mRectWidth-mScaleHeight, i * mScaleMargin, mRectWidth, i * mScaleMargin, paint);
            }
        }
    }

    @Override
    protected void onDrawPointer(Canvas canvas, Paint paint) {

        paint.setColor(getResources().getColor(R.color.part_color));
        paint.setStrokeWidth(5);
        //每一屏幕刻度的个数/2
        int countScale = mScaleScrollViewRange / mScaleMargin / 2;
        //根据滑动的距离，计算指针的位置【指针始终位于屏幕中间】
        int finalY = mScroller.getFinalY();
        //滑动的刻度
        int tmpCountScale = (int) Math.rint((double) finalY / (double) mScaleMargin); //四舍五入取整
        //总刻度
        mCountScale = tmpCountScale + countScale + mMin;
        if (mScrollListener != null) { //回调方法
            mScrollListener.onScaleScroll(mCountScale);
        }
        //canvas.drawLine(0, countScale * mScaleMargin + finalY, mScaleMaxHeight + mScaleHeight, countScale * mScaleMargin + finalY, paint);

    }

    @Override
    public void scrollToScale(int val) {
        if (val < mMin || val > mMax) {
            return;
        }
        int dy = (val - mCountScale) * mScaleMargin;
        smoothScrollBy(0, dy);
    }
    public void setFinalY(int var){
        this.default_inde=var;
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int y = (int) event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (mScroller != null && !mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
                mScrollLastX = y;
                return true;
            case MotionEvent.ACTION_MOVE:
                int dataY = mScrollLastX - y;
                if (mCountScale - mTempScale < 0) { //向下边滑动
                    if (mCountScale <= mMin && dataY <= 0) //禁止继续向下滑动
                        return super.onTouchEvent(event);
                } else if (mCountScale - mTempScale > 0) { //向上边滑动
                    if (mCountScale >= mMax && dataY >= 0) //禁止继续向上滑动
                        return super.onTouchEvent(event);
                }
                smoothScrollBy(0, dataY);
                mScrollLastX = y;
                postInvalidate();
                mTempScale = mCountScale;
                return true;
            case MotionEvent.ACTION_UP:
                if (mCountScale < mMin) mCountScale = mMin;
                if (mCountScale > mMax) mCountScale = mMax;
                int finalY = (mCountScale - mMidCountScale) * mScaleMargin;
                mScroller.setFinalY(finalY); //纠正指针位置
                postInvalidate();
                return true;
        }
        return super.onTouchEvent(event);
    }
}
