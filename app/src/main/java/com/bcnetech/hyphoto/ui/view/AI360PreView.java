package com.bcnetech.hyphoto.ui.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.bcnetech.bcnetechlibrary.util.ContentUtil;
import com.bcnetech.hyphoto.R;


public class AI360PreView extends View {
    private Paint maskPaint;
    private int padding = 0;


    public AI360PreView(Context context) {
        super(context);
        init(context);
    }

    public AI360PreView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public AI360PreView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        maskPaint = new Paint();
        padding = ContentUtil.dip2px(context, 27);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawMask(canvas);
    }

    /**
     * 创建遮罩层形状
     *
     * @return
     */
    private Bitmap makeSrcRect() {
        Bitmap bm = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvcs = new Canvas(bm);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(getResources().getColor(R.color.trans_backgroud));
        canvcs.drawRect(new RectF(0, 0, getWidth(), getHeight()), paint);
        return bm;
    }

    /**
     * 创建镂空层矩形形状
     *
     * @return
     */
    private Bitmap makeDstCircle() {
        Bitmap bm = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvcs = new Canvas(bm);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.WHITE);
        canvcs.drawRect(padding, 0, getWidth() - padding, getWidth(), paint);
        return bm;
    }


    /**
     * 绘制镂空遮罩
     *
     * @param canvas
     */
    @SuppressWarnings("WrongConstant")
    private void drawMask(Canvas canvas) {
        maskPaint.setFilterBitmap(false);
        canvas.saveLayer(0, 0, getWidth(), getHeight(), null, Canvas.ALL_SAVE_FLAG);
        canvas.drawBitmap(makeDstCircle(), 0, 0, maskPaint);

        maskPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT));
        canvas.drawBitmap(makeSrcRect(), 0, 0, maskPaint);
    }
}
