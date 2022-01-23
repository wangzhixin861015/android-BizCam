package com.bcnetech.hyphoto.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.bcnetech.bcnetechlibrary.util.ContentUtil;
import com.bcnetech.hyphoto.R;

/**
 * Created by a1234 on 16/9/30.
 * 用于旋转标尺
 */

public class RotateRularView extends ImageView {
    //长刻度线
    private static final float DEFAULT_LONG_DEGREE_LENGTH = 25f;
    //短刻度线
    private static final float DEFAULT_SHORT_DEGREE_LENGTH = 3f;
    //指针反向超过圆点的长度
    private static final float DEFAULT_POINT_BACK_LENGTH = 25f;
    //遮罩的rect
    private RectF shelterR;

    private Point circle = new Point();//圆心
    private double r;//半径
    private float y2;//图片下方y轴坐标
    private float rotateAngle = 0;
    private Paint mpaint;

    public RotateRularView(Context context) {
        super(context);
    }

    public RotateRularView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RotateRularView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }


    @Override
    protected void onDraw(Canvas canvas) {
        drawLayout(canvas);
    }

    public void setRotate(float rotate) {
        this.rotateAngle = rotate;
        invalidate();
    }

    public void getRoundParm(Point circle, double r, float y2) {
        this.circle = circle;
        this.r = r;
        this.y2 = y2;
        getShelterRectF();
        invalidate();
    }

    private void drawLayout(Canvas canvas) {
        mpaint = new Paint();
        mpaint.setColor(getContext().getResources().getColor(R.color.translucent));
        // 画入前景圆形蒙板层
        if (shelterR != null) {
            int sc = canvas.saveLayer(shelterR, null,  Canvas.ALL_SAVE_FLAG);
            canvas.drawRect(shelterR, mpaint);
            mpaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OVER));
            canvas.rotate(rotateAngle, circle.x, circle.y);
            //画刻度线
            float degreeLength = 0f;
            //Paint paintDegree = new Paint();
            mpaint.setAntiAlias(true);
            mpaint.setColor(Color.WHITE);
            //
            int degressNumberSize = ContentUtil.dip2px(getContext(),10);
            int startPointY = ContentUtil.dip2px(getContext(),14);
            for (int i = 0; i < 180; i++) {
                mpaint.setColor(Color.WHITE);
                if (i % 5 == 0) {
                    mpaint.setStrokeWidth(3);
                    degreeLength = DEFAULT_LONG_DEGREE_LENGTH;
                    canvas.drawCircle(circle.x, (float) (circle.y - r + startPointY), 3, mpaint);

                } else {
                    mpaint.setStrokeWidth(1.5f);
                    degreeLength = DEFAULT_SHORT_DEGREE_LENGTH;
                    canvas.drawLine(circle.x, (float) (circle.y - r + startPointY), circle.x + degreeLength, (float) (circle.y - r + startPointY), mpaint);
                }
                //第一个点为圆弧最高点y-r
                canvas.save();
                if (degreeLength == DEFAULT_LONG_DEGREE_LENGTH) {
                    if (i > 0 && i < 180) {
                        mpaint.setTextAlign(Paint.Align.CENTER);
                        mpaint.setTextSize(degressNumberSize);
                        mpaint.setFakeBoldText(true);//使用TextPaint的仿“粗体”
                        mpaint.setColor(Color.GRAY);
                      /*  if (Math.abs(rotateAngle)-(-2 * (i - 90))==0){
                            mpaint.setColor(Color.WHITE);
                        }*/
                        canvas.rotate(180, circle.x, (float) (circle.y - r + startPointY) + degreeLength - DEFAULT_POINT_BACK_LENGTH);
                        canvas.drawText(-2 * (i - 90) + "", circle.x, (float) (circle.y - r + startPointY) + degreeLength + DEFAULT_POINT_BACK_LENGTH, mpaint);

                    }

                }
                canvas.restore();
                canvas.rotate(360 / 180, circle.x, circle.y);//每2度一刻度标示
            }
        }
    }

    /**
     * 用来保证阴影的大小足以遮住图片
     *
     * @return
     */
    private RectF getShelterRectF() {
        if (shelterR == null) {
            shelterR = new RectF();
        }
        shelterR.set(0, y2, getWidth(), getHeight());

        return shelterR;
    }
}
