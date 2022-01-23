package com.bcnetech.hyphoto.ui.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;


/**
 * 形状变换动画
 * Created by a1234 on 2018/8/2.
 */

public class LoadingAnimView extends View {
    private PointF startA, startB, startC, AnimA, AnimB, AnimC;
    private float centX, centY;
    private int LENGTH = 0;//view边长
    private int LengthAnim = 0;
    private int AngelA, AngelB, AngelC, AngelA2, AngelB2, AngelC2;
    private Paint drawPaint, circlePaint;
    private Path pathA, pathB, pathC;
    private float radius;
    private static int STROKEWIDTH = 12;
    private static int DURATION = 1000;
    private ValueAnimator valueAnimator;
    private long currentAnimCount = 0;
    private long startTime = 0;

    public LoadingAnimView(Context context) {
        super(context);
        getSize1();
    }

    public LoadingAnimView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        getSize1();

    }

    public LoadingAnimView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getSize1();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        startDraw();
        canvas.drawPath(pathA, drawPaint);
        canvas.drawPath(pathB, drawPaint);
        canvas.drawPath(pathC, drawPaint);
        // canvas.drawCircle(centX, centY, centY, circlePaint);
        // canvas.drawCircle(centX, centY, radius, circlePaint);
    }

    /**
     * 会执行多次
     */
    private void getSize1() {
        ViewTreeObserver vto = this.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                if (LENGTH == 0) {
                    int height = LoadingAnimView.this.getMeasuredHeight();
                    int width = LoadingAnimView.this.getMeasuredWidth();
                    LENGTH = width;
                    initDraw();
                    initValueAnim();
                }
                return true;
            }

        });

    }

    private void initDraw() {
        drawPaint = new Paint();
        drawPaint.setColor(Color.BLUE);
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(STROKEWIDTH);
        drawPaint.setStyle(Paint.Style.STROKE);

        // circlePaint = new Paint();
        //circlePaint.setColor(Color.parseColor("#00000000"));
        //circlePaint.setColor(Color.CYAN);
        // circlePaint.setStyle(Paint.Style.STROKE);
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(STROKEWIDTH);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);
        drawPaint.setStyle(Paint.Style.STROKE);

        centX = LENGTH / 2;
        centY = (float) ((LENGTH / 2) / (Math.cos(Math.toRadians(30))));
        LENGTH = LengthAnim = (int) centY+100;
        AngelA = -150;
        AngelB = 90;
        AngelC = -30;
        AngelA2 = -90;
        AngelB2 = 150;
        AngelC2 = 30;

        startA = getPoint(AngelA2, true);
        startB = getPoint(AngelB2, true);
        startC = getPoint(AngelC2, true);
    }

    private void startDraw() {
        if (currentAnimCount % 2 == 0) {
            AngelA = -150;
            AngelB = 90;
            AngelC = -30;
            AngelA2 = -90;
            AngelB2 = 150;
            AngelC2 = 30;

        } else {
            AngelA2 = -30;
            AngelB2 = -150;
            AngelC2 = 90;
            AngelA = -90;
            AngelB = 150;
            AngelC = 30;
        }
        AnimA = getPoint(AngelA, false);
        AnimB = getPoint(AngelB, false);
        AnimC = getPoint(AngelC, false);
        startA = getPoint(AngelA2, true);
        startB = getPoint(AngelB2, true);
        startC = getPoint(AngelC2, true);

        pathA = new Path();
        pathA.moveTo(startA.x, startA.y);
        pathA.lineTo(AnimA.x, AnimA.y);
        pathA.moveTo(startA.x, startA.y);
        pathA.lineTo(AnimC.x, AnimC.y);

        pathB = new Path();
        pathB.moveTo(startB.x, startB.y);
        pathB.lineTo(AnimA.x, AnimA.y);
        pathB.moveTo(startB.x, startB.y);
        pathB.lineTo(AnimB.x, AnimB.y);

        pathC = new Path();
        pathC.moveTo(startC.x, startC.y);
        pathC.lineTo(AnimC.x, AnimC.y);
        pathC.moveTo(startC.x, startC.y);
        pathC.lineTo(AnimB.x, AnimB.y);
    }

    private void initValueAnim() {
        valueAnimator = ValueAnimator.ofInt(LENGTH, LENGTH / 2 + 1);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                currentAnimCount = (System.currentTimeMillis() - startTime) / (DURATION * 2);
                //  currentAnimCount = valueAnimator.getCurrentPlayTime() / (DURATION * 2);
                LengthAnim = (int) valueAnimator.getAnimatedValue();
                LoadingAnimView.this.invalidate();
            }
        });
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                startTime = System.currentTimeMillis();
            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        valueAnimator.setDuration(DURATION);
        valueAnimator.start();
        valueAnimator.setRepeatMode(ValueAnimator.REVERSE);
        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
    }

    public void DestroyAnim() {
        valueAnimator.cancel();
        valueAnimator = null;
        LENGTH = 0;
    }

    private PointF getPoint(int angle, boolean isStable) {
        int length = isStable ? LENGTH : LengthAnim;
        //圆点坐标：(x0,y0)
        //半径：r
        //角度：a0
        //
        //则圆上任一点为：（x1,y1）
        //x1   =   x0   +   r   *   cos(ao   *   3.14   /180   )
        //y1   =   y0   +   r   *   sin(ao   *   3.14   /180   )
        float radius_Anim = (float) ((length / 2) / (Math.cos(Math.toRadians(30))));
        double x = centX + radius_Anim / 2 * Math.cos(Math.toRadians(angle));
        double y = centY + radius_Anim / 2 * Math.sin(Math.toRadians(angle));
        return new PointF((float) x, (float) y);
    }

}

