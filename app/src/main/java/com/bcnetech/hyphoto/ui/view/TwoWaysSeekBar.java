package com.bcnetech.hyphoto.ui.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import com.bcnetech.hyphoto.utils.ImageUtil;
import com.bcnetech.hyphoto.R;

/**
 * Created by a1234 on 2017/7/5.
 */

public class TwoWaysSeekBar extends View {

    private static final int CLICK_ON_PRESS = 1;    //点击在滑块上
    private static final int CLICK_INVAILD = 0;

    private static final int[] STATE_NORMAL = {};
    private static final int[] STATE_PRESSED = {android.R.attr.state_pressed, android.R.attr.state_window_focused};
    private static final String tag = "TwoWaysSeekBar";

    private Drawable notScrollBarBg;    //滑动条背景图
    private Drawable hasScrollBarBg;    //滑动条滑动时背景图
    private Drawable mThumb;    //滑块
    private Drawable mCenterCircle;     //中间分隔小圆点
    private Drawable bg;

    private int mSeekBarWidth;  //控件宽度
    private int mSeekBarHeight; //滑动条高度
    private int mCenterCircleWidth; //中间分割点宽度
    private int mCenterCircleHeight;    //中间分割点高度
    private int mThumbWidth;    //滑块宽度
    private int mThumbHeight;   //滑块高度
    private int mhasScrollBarW; //滑动线宽度
    private int mhasScrollBarH; //滑动线高度

    private double mThumbOffset = 0;    //滑块中心坐标
    private double ThumbOffset = 0;
    private double mDefaultThumbOffSet = 100;   //默认滑块位置百分比
    //    private int mThumbMarginTop = 0;   //滑动块顶部距离上边框距离，也就是距离字体顶部的距离
    private int mDistance = 0;  //滑动的总距离，固定值
    private int mFlag = CLICK_INVAILD;
    private OnSeekBarChangeListener mSeekBarChangeListener;

    private Paint whiteCircle;
    private Paint blueCircle;

    private float defaultPoint = 100f;


    public TwoWaysSeekBar(Context context) {
        this(context, null);
    }

    public TwoWaysSeekBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TwoWaysSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init() {
        Resources resources = getResources();
        notScrollBarBg = resources.getDrawable(R.drawable.seekbar_bg);
        hasScrollBarBg = resources.getDrawable(R.drawable.seek_progress_line);
        mThumb = resources.getDrawable(R.drawable.seekbar_thumb);
        mCenterCircle = resources.getDrawable(R.drawable.seekbar_thumb_center);

        bg = resources.getDrawable(R.drawable.seekbar_bg_new);

        mThumbWidth = mThumb.getIntrinsicWidth();
        mThumbHeight = mThumb.getIntrinsicHeight();
        mCenterCircleWidth = mCenterCircle.getIntrinsicWidth();
        mCenterCircleHeight = mCenterCircle.getIntrinsicHeight();
        mhasScrollBarW = hasScrollBarBg.getIntrinsicWidth();

        whiteCircle = new Paint();
        whiteCircle.setColor(Color.parseColor("#FFFFFF"));
        whiteCircle.setAntiAlias(true);  //抗锯齿
        whiteCircle.setStrokeWidth(1);
        whiteCircle.setDither(true);
        whiteCircle.setStyle(Paint.Style.FILL);

        blueCircle = new Paint();
        blueCircle.setColor(Color.parseColor("#0057FF"));
        blueCircle.setAntiAlias(true);  //抗锯齿
        blueCircle.setStrokeWidth(1);
        blueCircle.setDither(true);
        blueCircle.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = measureWidth(widthMeasureSpec);
        mSeekBarWidth = width;
        mSeekBarHeight = notScrollBarBg.getIntrinsicHeight() + ImageUtil.Dp2Px(getContext(), 7);
        mDistance = notScrollBarBg.getIntrinsicWidth() - mThumbWidth;
    /*   mThumbOffset = width/2;
         mThumbOffset = formatDouble(mDefaultThumbOffSet/200*(mDistance) + mThumbWidth/2);*/
        setMeasuredDimension(width, mSeekBarHeight);
    }

    private int measureWidth(int measureSpec) {
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        if (specMode == MeasureSpec.AT_MOST) {

        } else if (specMode == MeasureSpec.EXACTLY) {

        }
        return specSize;
    }

    @SuppressWarnings("unused")
    private int measureHeight(int measureSpec) {
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        int defaultHeight = 100;
        //wrap_content
        if (specMode == MeasureSpec.AT_MOST) {
        }
        //fill_parent
        else if (specMode == MeasureSpec.EXACTLY) {
            defaultHeight = specSize;
        }

        return defaultHeight;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


        int aaa = mSeekBarHeight / 2 - notScrollBarBg.getIntrinsicHeight() / 2;
        int bbb = aaa + notScrollBarBg.getIntrinsicHeight();

        int ccc = mThumbHeight / 2 - mCenterCircleHeight / 2;
        int ddd = ccc + mCenterCircleHeight;


        int eee = mSeekBarHeight / 2 - mhasScrollBarW / 2;
        int fff = eee + mhasScrollBarW;


        //距离底部的差值
        int offSet = mSeekBarWidth / 2 - notScrollBarBg.getIntrinsicWidth();

        notScrollBarBg.setBounds(mSeekBarWidth-offSet-notScrollBarBg.getIntrinsicWidth()-(int)mThumbOffset-ImageUtil.Dp2Px(getContext(),2), aaa, mSeekBarWidth-offSet-(int)mThumbOffset+ImageUtil.Dp2Px(getContext(),2), bbb);
        notScrollBarBg.draw(canvas);

        //滑动条中心点
        float pointCenter = mSeekBarWidth-(defaultPoint / 200) * notScrollBarBg.getIntrinsicWidth() -offSet - (int) mThumbOffset;

        canvas.drawCircle(pointCenter, mSeekBarHeight / 2, ImageUtil.Dp2Px(getContext(), 7), whiteCircle);


        if (mThumbOffset < notScrollBarBg.getIntrinsicWidth() - (notScrollBarBg.getIntrinsicWidth() * (defaultPoint / 200))) {
            hasScrollBarBg.setBounds(mSeekBarWidth / 2, eee, (int) pointCenter, fff);
        } else {
            hasScrollBarBg.setBounds((int) pointCenter, eee, mSeekBarWidth / 2, fff);
        }


        hasScrollBarBg.draw(canvas);

        canvas.drawCircle(pointCenter, mSeekBarHeight / 2, ImageUtil.Dp2Px(getContext(), 5), blueCircle);


//        mCenterCircle.setBounds(mSeekBarWidth/2 - mCenterCircleWidth/2, ccc, mSeekBarWidth/2 + mCenterCircleWidth/2, ddd);
//        mCenterCircle.draw(canvas);

//        mThumb.setBounds((int)mThumbOffset - mThumbWidth/2,mThumbMarginTop,(int)mThumbOffset + mThumbWidth/2,mThumbMarginTop+mThumbHeight);
//        mThumb.draw(canvas);


        double progress = formatDouble((mThumbOffset - mThumbWidth / 2) * 200 / mDistance);   //progress初始值为100

        if ((int) progress == 100) {
            progress = 0;
        } else if ((int) progress > 100) {
            progress -= 100;
        } else if ((int) progress < 100) {
            progress -= 100;
        }
//        Log.d(tag, "thumb:"+mThumbOffset);
        //canvas.drawText((int)progress+"", (int) mThumbOffset - 2 - 2, 50, text_Paint);
        if (mSeekBarChangeListener != null) {
            mSeekBarChangeListener.onProgressChanged(this, progress);
        }

    }

    private void refresh() {
        invalidate();
    }

    //设置进度
    public void setProgress(double progress) {
        progress -= 100;
        //this.mDefaultThumbOffSet = progress;
        /*if(progress == 0){
            mThumbOffset = formatDouble(100/200*(mDistance))+mThumbWidth/2;
        }else *//*if(progress >= 0){
            mThumbOffset = formatDouble((100 + progress)/200*(mDistance))+mThumbWidth/2;
        }else if(progress < 0){
            mThumbOffset = formatDouble((100 + progress)/200*(mDistance))+mThumbWidth/2;
        }*/
        mThumbOffset = formatDouble((100 + progress) / 200 * (mDistance)) + mThumbWidth / 2;
        refresh();
    }

    public void setProgressAndDefaultPoint(float defaultPoint,double progress){
        this.defaultPoint=defaultPoint;
        progress -= 100;

        mThumbOffset = formatDouble((100 + progress) / 200 * (notScrollBarBg.getIntrinsicWidth() - mThumbWidth
        )) + mThumbWidth / 2;
        refresh();
    }



    public void setOnSeekBarChangeListener(OnSeekBarChangeListener mListener) {
        this.mSeekBarChangeListener = mListener;
    }

    public interface OnSeekBarChangeListener {
        //滑动前
        public void onProgressBefore();

        //滑动中
        public void onProgressChanged(TwoWaysSeekBar seekBar, double progress);

        //滑动后
        public void onProgressAfter();
    }

    public static double formatDouble(double mDouble) {
        java.math.BigDecimal b = new java.math.BigDecimal(mDouble);
        java.math.BigDecimal bd1 = b.setScale(2, java.math.BigDecimal.ROUND_HALF_UP);
        mDouble = bd1.doubleValue();
        return mDouble;
    }
}