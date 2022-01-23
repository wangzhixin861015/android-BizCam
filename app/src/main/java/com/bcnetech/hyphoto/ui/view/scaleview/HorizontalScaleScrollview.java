package com.bcnetech.hyphoto.ui.view.scaleview;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.bcnetech.bcnetechlibrary.util.ContentUtil;
import com.bcnetech.hyphoto.utils.ImageUtil;
import com.bcnetech.hyphoto.R;

/**
 * Created by a1234 on 2017/11/21.
 */

public class HorizontalScaleScrollview extends BaseScaleView {

    private int default_inde;
    //顶部临界值
    private int leftCount;
    //底部临界值
    private int rightCount;

    private int startY = 0;
    private int startX = 0;

    private int mMiddleHeight = 0;

    private Bitmap twoOfThree;
    private Bitmap oneOfThree;


    Paint paintText16;


    Paint paintText8;





    public HorizontalScaleScrollview(Context context) {
        super(context);
    }

    public HorizontalScaleScrollview(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HorizontalScaleScrollview(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public HorizontalScaleScrollview(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void initVar() {
        mMax = 9;
        mMin = -9;
        mRectWidth = (mMax - mMin) * mScaleMargin;
        mRectHeight = (int) (mScaleHeight * 1.1);
        mScaleMaxHeight = (int) (mRectHeight * 1.5);

        paintText16=new Paint();
        paintText16.setColor(Color.parseColor("#888888"));
        paintText16.setTextSize(ContentUtil.dip2px(getContext(), 16));
        // 抗锯齿
        paintText16.setAntiAlias(true);
        // 设定是否使用图像抖动处理，会使绘制出来的图片颜色更加平滑和饱满，图像更加清晰
        paintText16.setDither(true);
        // 空心
        //paint.setStyle(Paint.Style.STROKE);
        // 文字居中
        paintText16.setTextAlign(Paint.Align.CENTER);

        paintText8=new Paint();
        paintText8.setColor(Color.parseColor("#888888"));
        paintText8.setTextSize(ContentUtil.dip2px(getContext(), 10));
        // 抗锯齿
        paintText8.setAntiAlias(true);
        // 设定是否使用图像抖动处理，会使绘制出来的图片颜色更加平滑和饱满，图像更加清晰
        paintText8.setDither(true);
        // 空心
        //paint.setStyle(Paint.Style.STROKE);
        // 文字居中
        paintText8.setTextAlign(Paint.Align.CENTER);

        oneOfThree = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.one_of_three);
        twoOfThree = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.two_of_three);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mScaleScrollViewRange = getMeasuredWidth();
        mTempScale = mMax - (mScaleScrollViewRange / mScaleMargin / 2);
        mMidCountScale = mMax - (mScaleScrollViewRange / mScaleMargin / 2);

        leftCount = (mMidCountScale - 9) * mScaleMargin;
        rightCount = (mMidCountScale - (-9)) * mScaleMargin;

        if(default_inde==0){
            //初始化指针位置
            int finalX = (mMidCountScale + default_inde) * mScaleMargin;
            mScroller.setFinalX(finalX);
        }

        startY = getMeasuredHeight() / 2;
        startX = getMeasuredWidth() / 2;
        postInvalidate();
    }

    @Override
    protected void onDrawLine(Canvas canvas, Paint paint) {
        paint.setColor(Color.WHITE);
        //canvas.drawLine(0, 0, mRectWidth, 0, paint);
    }

    @Override
    protected void onDrawScale(Canvas canvas, Paint paint) {
        mMiddleHeight = (int) (mRectHeight * 1.3);
        paint.setColor(Color.WHITE);
        paint.setTextSize(ContentUtil.dip2px(getContext(), 16));
        paint.setStrokeWidth(ContentUtil.dip2px(getContext(), 1));


        for (int i = 0; i <= mMax - mMin; i++) {

            if (i == 9) {

                canvas.drawLine(i * mScaleMargin- ImageUtil.Dp2Px(getContext(),5), startY, i * mScaleMargin- ImageUtil.Dp2Px(getContext(),5), startY + ContentUtil.dip2px(getContext(), 4), paint);
                canvas.drawLine(i * mScaleMargin- ImageUtil.Dp2Px(getContext(),5), startY, i * mScaleMargin- ImageUtil.Dp2Px(getContext(),5), startY - ContentUtil.dip2px(getContext(), 4), paint);
                //整值文字
                canvas.drawText(String.valueOf(0), (i * mScaleMargin)+ContentUtil.dip2px(getContext(), 20), startY+ ContentUtil.dip2px(getContext(), 4), paintText16);


            }else if(i==18){
                canvas.drawLine(i * mScaleMargin- ImageUtil.Dp2Px(getContext(),5), startY, i * mScaleMargin- ImageUtil.Dp2Px(getContext(),5), startY + ContentUtil.dip2px(getContext(), 4), paint);
                canvas.drawLine(i * mScaleMargin- ImageUtil.Dp2Px(getContext(),5), startY, i * mScaleMargin- ImageUtil.Dp2Px(getContext(),5), startY - ContentUtil.dip2px(getContext(), 4), paint);

                canvas.drawLine((i+1) * mScaleMargin- ImageUtil.Dp2Px(getContext(),5), startY, (i+1) * mScaleMargin- ImageUtil.Dp2Px(getContext(),5), startY + ContentUtil.dip2px(getContext(), 4), paint);
                canvas.drawLine((i+1) * mScaleMargin- ImageUtil.Dp2Px(getContext(),5), startY, (i+1) * mScaleMargin- ImageUtil.Dp2Px(getContext(),5), startY - ContentUtil.dip2px(getContext(), 4), paint);

                //整值文字
                canvas.drawText(String.valueOf(3), (i * mScaleMargin)+ContentUtil.dip2px(getContext(), 20), startY + ContentUtil.dip2px(getContext(), 5), paintText16);

                canvas.drawText("+", (i * mScaleMargin)+ContentUtil.dip2px(getContext(), 20)-paintText8.getTextSize(), startY + ContentUtil.dip2px(getContext(), 4), paintText8);

            }else if(i==0){
                canvas.drawLine(i * mScaleMargin- ImageUtil.Dp2Px(getContext(),5), startY, i * mScaleMargin- ImageUtil.Dp2Px(getContext(),5), startY + ContentUtil.dip2px(getContext(), 4), paint);
                canvas.drawLine(i * mScaleMargin- ImageUtil.Dp2Px(getContext(),5), startY, i * mScaleMargin- ImageUtil.Dp2Px(getContext(),5), startY - ContentUtil.dip2px(getContext(), 4), paint);
                //整值文字
                canvas.drawText(String.valueOf(3), (i * mScaleMargin)+ContentUtil.dip2px(getContext(), 20), startY + ContentUtil.dip2px(getContext(), 5), paintText16);
                canvas.drawText("—", (i * mScaleMargin)+ContentUtil.dip2px(getContext(), 20)-paintText8.getTextSize(), startY + ContentUtil.dip2px(getContext(), 4), paintText8);

            } else {
                if (i % 3 == 0) { //整值
                    canvas.drawLine(i * mScaleMargin- ImageUtil.Dp2Px(getContext(),5), startY, i * mScaleMargin- ImageUtil.Dp2Px(getContext(),5), startY + ContentUtil.dip2px(getContext(), 4), paint);
                    canvas.drawLine(i * mScaleMargin- ImageUtil.Dp2Px(getContext(),5), startY, i * mScaleMargin- ImageUtil.Dp2Px(getContext(),5), startY - ContentUtil.dip2px(getContext(), 4), paint);
                    //整值文字
                    // canvas.drawText(String.valueOf(k), mScaleMaxHeight + 20, i * mScaleMargin + paint.getTextSize() / 3, paint);
                    canvas.drawText(i<9?"—":"+", (i * mScaleMargin)+ContentUtil.dip2px(getContext(), 20)-paintText8.getTextSize(), startY + ContentUtil.dip2px(getContext(), 4), paintText8);
                    if(i==3){
                        canvas.drawText(String.valueOf(2), (i * mScaleMargin)+ContentUtil.dip2px(getContext(), 20), startY+ ContentUtil.dip2px(getContext(), 5), paintText16);
                    }else if(i==6){
                        canvas.drawText(String.valueOf(1), (i * mScaleMargin)+ContentUtil.dip2px(getContext(), 20), startY+ ContentUtil.dip2px(getContext(), 5), paintText16);

                    }else if(i==12){
                        canvas.drawText(String.valueOf(1), (i * mScaleMargin)+ContentUtil.dip2px(getContext(), 20), startY+ ContentUtil.dip2px(getContext(), 5), paintText16);

                    }else if(i==15){
                        canvas.drawText(String.valueOf(2), (i * mScaleMargin)+ContentUtil.dip2px(getContext(), 20), startY+ ContentUtil.dip2px(getContext(), 5), paintText16);
                    }

                } else {
                    canvas.drawLine(i * mScaleMargin- ImageUtil.Dp2Px(getContext(),5), startY, i * mScaleMargin- ImageUtil.Dp2Px(getContext(),5), startY + ContentUtil.dip2px(getContext(), 4), paint);
                    canvas.drawLine(i * mScaleMargin- ImageUtil.Dp2Px(getContext(),5), startY, i * mScaleMargin- ImageUtil.Dp2Px(getContext(),5), startY - ContentUtil.dip2px(getContext(), 4), paint);

                    canvas.drawText(i<9?"—":"+", (i * mScaleMargin)+ContentUtil.dip2px(getContext(), 20)-(i>6&&i<9||i>9&&i<12?oneOfThree.getWidth()/2:oneOfThree.getWidth()/2+paintText8.getTextSize()), startY + ContentUtil.dip2px(getContext(), 4), paintText8);

                    if(i<3&&i>0){
                        canvas.drawText(String.valueOf(2), (i * mScaleMargin)+ContentUtil.dip2px(getContext(), 20)-oneOfThree.getWidth()/2, startY+ ContentUtil.dip2px(getContext(), 5), paintText16);
                    }else if(i<6&&i>3){
                        canvas.drawText(String.valueOf(1), (i * mScaleMargin)+ContentUtil.dip2px(getContext(), 20)-oneOfThree.getWidth()/2, startY+ ContentUtil.dip2px(getContext(), 5), paintText16);
                    }else if(i<15&&i>12){
                        canvas.drawText(String.valueOf(1), (i * mScaleMargin)+ContentUtil.dip2px(getContext(), 20)-oneOfThree.getWidth()/2, startY+ ContentUtil.dip2px(getContext(), 5), paintText16);
                    }else if(i<18&&i>15){
                        canvas.drawText(String.valueOf(2), (i * mScaleMargin)+ContentUtil.dip2px(getContext(), 20)-oneOfThree.getWidth()/2, startY+ ContentUtil.dip2px(getContext(), 5), paintText16);
                    }
                    if(i<9){
                        canvas.drawBitmap((18-i)%3==1?oneOfThree:twoOfThree,(i * mScaleMargin)+ContentUtil.dip2px(getContext(), 20), startY-(oneOfThree.getHeight()/2) ,paint);
//                        canvas.drawText((18-i) % 3+"/3", (i * mScaleMargin)+ContentUtil.dip2px(getContext(), 20), startY + ContentUtil.dip2px(getContext(), 5), paintText8);
                    }else {
                        canvas.drawBitmap((i%3)==1?oneOfThree:twoOfThree,(i * mScaleMargin)+ContentUtil.dip2px(getContext(), 20), startY-(oneOfThree.getHeight()/2) ,paint);
//                        canvas.drawText(i % 3+"/3", (i * mScaleMargin)+ContentUtil.dip2px(getContext(), 20), startY + ContentUtil.dip2px(getContext(), 5), paintText8);
                    }
                }
            }
        }
    }

    @Override
    protected void onDrawPointer(Canvas canvas, Paint paint) {

        //每一屏幕刻度的个数/2
        int countScale = mScaleScrollViewRange / mScaleMargin / 2;

        //根据滑动的距离，计算指针的位置【指针始终位于屏幕中间】
        int finalX = mScroller.getFinalX();

        //滑动的刻度数
        int tmpCountScale = (int) Math.rint((double) finalX / (double) mScaleMargin); //四舍五入取整
        //总刻度
        mCountScale = mMax - (tmpCountScale + countScale);
        if (mCountScale > 9) {
            //纠正指针位置
            mCountScale = 9;

        } else if (mCountScale < -9) {
            //纠正指针位置
            mCountScale = -9;
        }

        if (mScrollListener != null) { //回调方法
            mScrollListener.onScaleScroll(-mCountScale);
            default_inde=mCountScale;

        }
    }


    @Override
    public void scrollToScale(int val) {
        if (val < mMin || val > mMax) {
            return;
        }
        int finalX = mScroller.getFinalX();

        int dy = (val+mCountScale ) * mScaleMargin;
        default_inde=val;
        smoothScrollBy(dy, 0);
    }



    public void setFinalX(int var) {
        this.default_inde = var;
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
                if (mTempScale - mCountScale < 0) { //向下边滑动
//                    if (mCountScale == mMax && dataX <= 0) //禁止继续向下滑动
//                        return super.onTouchEvent(event);
                } else if (mTempScale - mCountScale > 0) { //向上边滑动
//                    if (mCountScale == mMin && dataX >= 0) //禁止继续向上滑动
//                        return super.onTouchEvent(event);
                }

                if (mScroller.getFinalX() + dataX <= leftCount) {
                    return super.onTouchEvent(event);
                }
                if (mScroller.getFinalX() + dataX >= rightCount) {
                    return super.onTouchEvent(event);
                }
                smoothScrollBy(dataX, 0);
                mScrollLastX = x;
                postInvalidate();
                mTempScale = mCountScale;
                return true;
            case MotionEvent.ACTION_UP:
                if (mCountScale < mMin) mCountScale = mMin;
                if (mCountScale > mMax) mCountScale = mMax;
                int finalX = (mMidCountScale - mCountScale) * mScaleMargin;
                mScroller.setFinalX(finalX); //纠正指针位置
                postInvalidate();
                return true;
        }
        return super.onTouchEvent(event);
    }

    public int getMax(){
        return this.mMax;
    }

    public int getMin(){
        return this.mMin;
    }

    public void reset(){
        scrollToScale(0);
    }
}
