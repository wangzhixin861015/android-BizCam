
package com.bcnetech.bcnetechlibrary.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.SeekBar;

import static android.R.attr.bottom;
import static android.R.attr.left;
import static android.R.attr.right;
import static android.R.attr.top;


/**
 * @类名: NumberSeekBar
 * @描述: TODO(带有数字的水平拖动条)
 * @作者: wang.fb
 * @日期: 2014-8-11 下午2:01:14
 * @修改人:
 * @修改时间: 2014-8-11 下午2:01:14
 * @修改内容:
 * @版本: V1.0
 */
public class NumberSeekBar extends SeekBar {
    
    private int oldPaddingTop;
    
    private int oldPaddingLeft;
    
    private int oldPaddingRight;
    
    private int oldPaddingBottom;
    
    private boolean isMysetPadding = true;
    
    private String mText;
    
    private float mTextWidth;
    
    private float mImgWidth;
    
    private float mImgHei;
    
    private Paint mPaint;
    
    private Resources res;
    
    private Bitmap bm;
    
    private int textsize = 32;
    
    private int textpaddingleft;
    
    private int textpaddingtop;
    
    private int imagepaddingleft;
    
    private int imagepaddingtop;


    public NumberSeekBar(Context context) {
        super(context);
        init();
    }
    
    public NumberSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    
    public NumberSeekBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }
    
    // 屏蔽滑动
    // @Override
    // public boolean onTouchEvent(MotionEvent event) {
    // return false;
    // }
    /**
     * (非 Javadoc)
     * 
     * @方法名: onTouchEvent
     * @描述: 不屏蔽屏蔽滑动
     * @日期: 2014-8-11 下午2:03:15
     * @param event
     * @return
     * @see android.widget.AbsSeekBar#onTouchEvent(android.view.MotionEvent)
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }
    
    // 修改setpadding 使其在外部调用的时候无效
    @Override
    public void setPadding(int left, int top, int right, int bottom) {
        if (isMysetPadding) {
            super.setPadding(left, top, right, bottom);
        }
    }
    
    // 初始化
    private void init() {
        res = getResources();
        initBitmap();
        initDraw();
        setPadding();
        NumberSeekBar.this.setEnabled(false);
        setMyPadding(0,0,0,0);
    }
    
    private void initDraw() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setTypeface(Typeface.DEFAULT);
        textsize = Dp2Px(getContext(),11);
        mPaint.setTextSize(textsize);
        mPaint.setColor(Color.WHITE);
    }
    
    private void initBitmap() {
        Paint paint = new Paint();
        Rect  spaceRect;
        spaceRect = new Rect();
        spaceRect.set(left, top, right, bottom);
        paint.setStyle(Paint.Style.FILL);
        paint.setAlpha(0);

        bm=Bitmap.createBitmap(Dp2Px(getContext(),20),Dp2Px(getContext(),12),Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bm);
        c.drawRect(spaceRect, paint);
        if (bm != null) {
            mImgWidth = bm.getWidth();
            mImgHei = bm.getHeight();
        } else {
            mImgWidth = 0;
            mImgHei = 0;
        }
    }
private double progress;
    public  void setMyProgress(double progress) {
        this.progress = progress;
        this.setProgress((int)progress);
    }

    protected synchronized void onDraw(Canvas canvas) {
        try {
            super.onDraw(canvas);
            mText = (int)(2*progress-100)+"";
            mTextWidth = mPaint.measureText(mText);
            Rect bounds = this.getProgressDrawable().getBounds();
            float xImg =
                bounds.width() * getProgress() / getMax() + imagepaddingleft
                    + oldPaddingLeft;
            float yImg = imagepaddingtop + oldPaddingTop;
            float xText =
                bounds.width() * getProgress() / getMax() + mImgWidth / 2+1
                    - mTextWidth / 2 + textpaddingleft + oldPaddingLeft;
            float yText =
                yImg + textpaddingtop + mImgHei / 2 + getTextHei() / 4-2;
            canvas.drawBitmap(bm, xImg, yImg, mPaint);
            canvas.drawText(mText, xText, yText, mPaint);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // 初始化padding 使其左右上 留下位置用于展示进度图片
    private void setPadding() {
        int top = getBitmapHeigh() + oldPaddingTop;
        int left = getBitmapWidth() / 2 + oldPaddingLeft;
        int right = getBitmapWidth() / 2 + oldPaddingRight;
        int bottom = oldPaddingBottom;
        isMysetPadding = true;
        setPadding(left, top, right, bottom);
        isMysetPadding = false;
    }
    
    /**
     * 设置展示进度背景图片
     * 
     * @param resid
     */
    public void setBitmap(int resid) {
        bm = BitmapFactory.decodeResource(res, resid);
        if (bm != null) {
            mImgWidth = bm.getWidth();
            mImgHei = bm.getHeight();
        } else {
            mImgWidth = 0;
            mImgHei = 0;
        }
        setPadding();
    }

    @Override
    public void setThumbOffset(int thumbOffset) {
        super.setThumbOffset(thumbOffset);
    }

    /**
     * 替代setpadding
     * 
     * @param left
     * @param top
     * @param right
     * @param bottom
     */
    public void setMyPadding(int left, int top, int right, int bottom) {
        oldPaddingTop = top;
        oldPaddingLeft = left;
        oldPaddingRight = right;
        oldPaddingBottom = bottom;
        isMysetPadding = true;
        setPadding(left + getBitmapWidth() / 2, top + getBitmapHeigh(), right
            + getBitmapWidth(), bottom);
        isMysetPadding = false;
    }
    
    /**
     * 设置进度字体大小
     * 
     * @param textsize
     */
    public void setTextSize(int textsize) {
        this.textsize = textsize;
        mPaint.setTextSize(textsize);
    }
    
    /**
     * 设置进度字体颜色
     * 
     * @param color
     */
    public void setTextColor(int color) {
        mPaint.setColor(color);
    }
    
    /**
     * 调整进度字体的位置 初始位置为图片的正中央
     * 
     * @param top
     * @param left
     */
    public void setTextPadding(int top, int left) {
        this.textpaddingleft = left;
        this.textpaddingtop = top;
    }
    
    /**
     * 调整进图背景图的位置 初始位置为进度条正上方、偏左一半
     * 
     * @param top
     * @param left
     */
    public void setImagePadding(int top, int left) {
        this.imagepaddingleft = left;
        this.imagepaddingtop = top;
    }
    
    private int getBitmapWidth() {
        return (int) Math.ceil(mImgWidth);
    }
    
    private int getBitmapHeigh() {
        return (int) Math.ceil(mImgHei);
    }
    
    private float getTextHei() {
        FontMetrics fm = mPaint.getFontMetrics();
        return (float) Math.ceil(fm.descent - fm.top) + 2;
    }
    
    public int getTextpaddingleft() {
        return textpaddingleft;
    }
    
    public int getTextpaddingtop() {
        return textpaddingtop;
    }
    
    public int getImagepaddingleft() {
        return imagepaddingleft;
    }
    
    public int getImagepaddingtop() {
        return imagepaddingtop;
    }
    
    public int getTextsize() {
        return textsize;
    }

    // dp转ps
    public static int Dp2Px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }
    
}
