package com.bcnetech.hyphoto.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import java.util.ArrayList;


public class FocusAlphaview extends AppCompatImageView {
    //变化圈状态
    public static final int STATUS_DETECTING = 0;//检测中
    public static final int STATUS_SHOT = 1;//可拍照
    public static final int STATUS_FAIL = 2;//不可拍照
    private Paint paintColor;
    private static float STROKEWIDTH = 3;
    //变化圆环总圈数
    private static int TOTAL_CIRCLE = 25;
    private int inner_radius;
    private ArrayList<PaintAlpa> paintAlpas;

    public FocusAlphaview(Context context) {
        super(context);
        init();
    }

    public FocusAlphaview(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FocusAlphaview(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paintColor = new Paint();
        paintColor.setAntiAlias(true);
        paintColor.setStyle(Paint.Style.STROKE);
        paintColor.setStrokeWidth(STROKEWIDTH);
        paintAlpas = new ArrayList<>();
        for (int i = 0; i < TOTAL_CIRCLE; i++) {
            PaintAlpa paintAlpa = new PaintAlpa(i, 180 - 180 / TOTAL_CIRCLE * i);
            paintAlpas.add(paintAlpa);
        }
    }

    public void setRadius(int inner_radius,int status) {
        this.inner_radius = inner_radius;
        switch (status) {
            case STATUS_DETECTING:
                paintColor.setColor(Color.TRANSPARENT);

                break;
            case STATUS_SHOT:
                paintColor.setColor(Color.BLUE);

                break;
            case STATUS_FAIL:
                paintColor.setColor(Color.RED);
                break;
        }
        invalidate();
    }

    public void setPaintColor(int status){
        switch (status) {
            case STATUS_DETECTING:
                paintColor.setColor(Color.TRANSPARENT);

                break;
            case STATUS_SHOT:
                paintColor.setColor(Color.BLUE);

                break;
            case STATUS_FAIL:
                paintColor.setColor(Color.RED);
                break;
        }
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawGradientRing(canvas);
    }

    /**
     * 渐变色环
     */
    private void drawGradientRing(Canvas canvas) {
        if (paintColor.getColor() != Color.TRANSPARENT && inner_radius != 0) {
            for (int i = 0; i < paintAlpas.size(); i++) {
                Paint paint = new Paint();
                paint.setAntiAlias(true);
                paint.setStyle(Paint.Style.STROKE);
                paint.setColor(paintColor.getColor());
                paint.setAlpha((int) (paintAlpas.get(i).getAlpha()));
                canvas.drawCircle(getWidth() / 2, getHeight() / 2, (float) inner_radius + i, paint);
            }
        }
    }


    private class PaintAlpa {
        int serial;
        float alpha;

        public PaintAlpa(int serial, float alpha) {
            this.serial = serial;
            this.alpha = alpha;
        }

        public int getSerial() {
            return serial;
        }

        public void setSerial(int serial) {
            this.serial = serial;
        }

        public float getAlpha() {
            return alpha;
        }

        public void setAlpha(float alpha) {
            this.alpha = alpha;
        }

        @Override
        public String toString() {
            return "PaintAlpa{" +
                    "serial=" + serial +
                    ", alpha=" + alpha +
                    '}';
        }
    }
}
