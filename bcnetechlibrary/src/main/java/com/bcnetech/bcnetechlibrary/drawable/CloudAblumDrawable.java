package com.bcnetech.bcnetechlibrary.drawable;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

import com.bcnetech.bcnetechlibrary.BcnetechAppInstance;
import com.bcnetech.bcnetechlibrary.util.ContentUtil;


public class CloudAblumDrawable extends Drawable {
    private Paint mPaint;
    private Bitmap mBitmap;

    public CloudAblumDrawable(Bitmap bitmap) {
        mBitmap = bitmap;
        mPaint = new Paint();
        mPaint.setAntiAlias(true);

    }

    @Override
    public void setBounds(int left, int top, int right, int bottom) {
        super.setBounds(left, top, right, bottom);
    }

    @Override
    public void setAlpha(int alpha) {

    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {

    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        drawImg(canvas, mPaint);
    }

    private int dp2px(float x) {
        return ContentUtil.dip2px(BcnetechAppInstance.getApplicationContext(), x);
    }

    private void drawImg(Canvas canvas, Paint paint) {
        //239,241,244
        paint.setColor(Color.parseColor("#f2f4f7"));
        paint.setStrokeWidth(dp2px(2));
        canvas.drawLine(dp2px(3), dp2px(7), dp2px(95), dp2px(7), paint);
        canvas.drawLine(dp2px(5), dp2px(3), dp2px(93), dp2px(3), paint);

        canvas.save();
        //paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OVER));
        if (mBitmap!=null) {
            paint.setColor(Color.WHITE);
            canvas.drawBitmap(mBitmap, new Rect(0, 0, mBitmap.getWidth(), mBitmap.getHeight()), new Rect(dp2px(0), dp2px(10), dp2px(98), dp2px(108)), paint);
        }
            canvas.restore();
    }
}
