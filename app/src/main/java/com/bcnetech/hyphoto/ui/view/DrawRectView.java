package com.bcnetech.hyphoto.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

/**
 * author: wsBai
 * date: 2018/11/23
 */
public class DrawRectView extends AppCompatImageView {
    private RectF rect;
    private Paint paint;

    public DrawRectView(Context context) {
        super(context);
        init();
    }

    public DrawRectView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DrawRectView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void setDrawRect(RectF rect) {
        this.rect = rect;
        invalidate();
    }

    private void init() {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStrokeWidth(3);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.WHITE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (rect != null)
            canvas.drawRect(rect, paint);
    }
}
