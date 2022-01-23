package com.bcnetech.bcnetechlibrary.drawable;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;

/**
 * Created by wenbin on 16/8/31.
 */

public class CircleDefultDrawable extends Drawable {
    private Paint mPaint;
    private int mWidth;
    private Bitmap mBitmap ;
    private String text;


    public CircleDefultDrawable(int color,String text)
    {
        this.text=text;

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(color);
        mWidth = 200;
    }

    @Override
    public void draw(Canvas canvas)
    {
        canvas.drawCircle(mWidth / 2, mWidth / 2, mWidth / 2, mPaint);
        drawTextS(canvas);
    }


    private void drawTextS(Canvas canvas){
        Paint countPaint = new Paint(Paint.ANTI_ALIAS_FLAG
                | Paint.DEV_KERN_TEXT_FLAG);
        countPaint.setColor(Color.WHITE);
        countPaint.setTextSize(50f);
        countPaint.setTypeface(Typeface.DEFAULT_BOLD);
        countPaint.setTextAlign(Paint.Align.CENTER);
        Rect textBounds = new Rect();
        String numberStr = String.valueOf(text);
        countPaint.getTextBounds(numberStr, 0, numberStr.length(), textBounds);//get text bounds, that can get the text width and height
        int textHeight = textBounds.bottom - textBounds.top;
        canvas.drawText(numberStr, mWidth / 2, mWidth / 2+ textHeight/2,
                countPaint);

    }
    @Override
    public int getIntrinsicWidth()
    {
        return mWidth;
    }

    @Override
    public int getIntrinsicHeight()
    {
        return mWidth;
    }

    @Override
    public void setAlpha(int alpha)
    {
        mPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter cf)
    {
        mPaint.setColorFilter(cf);
    }

    @Override
    public int getOpacity()
    {
        return PixelFormat.TRANSLUCENT;
    }
}
