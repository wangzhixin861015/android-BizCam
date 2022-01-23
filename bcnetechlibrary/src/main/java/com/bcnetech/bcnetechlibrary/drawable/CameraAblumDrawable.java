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


public class CameraAblumDrawable extends Drawable {
    private Paint mPaint;
    private Bitmap mBitmap;

    public CameraAblumDrawable(Bitmap bitmap) {
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
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(dp2px(2));
        canvas.drawLine(dp2px(6), dp2px(3), dp2px(50), dp2px(3), paint);
        canvas.drawLine(dp2px(49), dp2px(3), dp2px(49), dp2px(48), paint);

        canvas.save();
        //paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OVER));
        if (mBitmap!=null) {
            canvas.drawBitmap(mBitmap, new Rect(0, 0, mBitmap.getWidth(), mBitmap.getHeight()), new Rect(dp2px(0), dp2px(7), dp2px(45), dp2px(52)), paint);
        }
            canvas.restore();
    }
}
