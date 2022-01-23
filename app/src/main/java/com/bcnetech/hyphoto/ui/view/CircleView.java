package com.bcnetech.hyphoto.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.bcnetech.hyphoto.R;
import com.bcnetech.hyphoto.listener.BizAtmosphereGestureListener;

/**
 * Created by a1234 on 16/10/27.
 */

public class CircleView extends ImageView {

    private final static int ZOOM = 1;
    private final static int MOVE = 2;
    private final static int NULL = 4;

    private final static int CLIKC_LIMIT = 40;//按钮范围
    private final static int INIT_XY=-1;
    private final static int WITHE_PAINT_SIZE=8;//中心点白圈画笔宽度
    private final static int BLUE_PAINT_SIZE=8;//中心点蓝圈画笔宽度
    private final static int PAINT_SIZE=5;//外圈画笔宽度
    private final static int WITHE_PAINT_R=28;//中心点白圈半径
    private final static int BLUE_PAINT_R=24;//中心点蓝圈半径


    private int type;
    private float drawX;//圆环的位置
    private float drawY;
    private float drawR;
    private double proportion;//实际压缩比
    private double proX;//实际压缩比
    private double proY;//实际压缩比
    private int distance, distance1;//双指放大的距离
    private float startDrawR;
    private int bitmapw, bitmaph;
    private Paint withePaint, bluePaint, paint;
    private ChangListener changListener;
    private BizAtmosphereGestureListener bizAtmosphereGestureListener;
    private Rect rect;

    public CircleView(Context context) {
        super(context);
        initData();
        onViewClick();
    }

    public CircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initData();
        onViewClick();
    }

    public CircleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initData();
        onViewClick();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (drawY == INIT_XY) {
            drawY = getMeasuredHeight() / 2;
            drawX = getMeasuredWidth() / 2;
            if(drawX<drawY){
                drawR = drawX / 2;
            }
            else{
                drawR = drawY / 2;
            }
            proportion = getMeasuredWidth() * 1.0 / bitmapw;
             proX = getMeasuredWidth() * 1.0 / bitmapw;
             proY = getMeasuredHeight() * 1.0 / bitmaph;
            proportion = Math.max(proX,proY);
            if (changListener != null) {
                changListener.returnXYR((float) (drawX / proX), (float) (drawY / proY), (float) (drawR / proportion));
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {

        canvas.drawCircle(drawX, drawY, WITHE_PAINT_R, withePaint);//中心点
        canvas.drawCircle(drawX, drawY, BLUE_PAINT_R, bluePaint);//中心点
        if (type != NULL) {
            canvas.drawCircle(drawX, drawY, drawR, paint);// 白圈
        }
    }

    private void initData() {
        initXYSize();
        withePaint = new Paint();
        withePaint.setAntiAlias(true);
        withePaint.setStrokeWidth(WITHE_PAINT_SIZE);
        withePaint.setColor(Color.WHITE);

        bluePaint = new Paint();
        bluePaint.setAntiAlias(true);
        bluePaint.setStrokeWidth(BLUE_PAINT_SIZE);
        bluePaint.setColor(getResources().getColor(R.color.little_blue));

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStrokeWidth(PAINT_SIZE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.WHITE);

        bizAtmosphereGestureListener = new BizAtmosphereGestureListener(getContext());

    }

    private void onViewClick(){
        bizAtmosphereGestureListener.setBizAtmosphereGestureDetector(new BizAtmosphereGestureListener.BizAtmosphereGestureDetector() {

            @Override
            public void startZoom(float distance) {
                CircleView.this.distance = (int) distance;//
                startDrawR = drawR;
            }

            @Override
            public void zoom(float distance) {
                type = ZOOM;
                distance1 = (int) distance;
                zoomCricleLimit();
                if (changListener != null) {
                    changListener.returnXYR((float) (drawX / proX), (float) (drawY / proY), (float) (drawR / proportion));
                }
            }

            @Override
            public void endZoom() {
                type = NULL;
                invalidate();
            }

            @Override
            public boolean startMove(float startX, float startY) {
                if (inCricle(startX, startY)) {
                    type = MOVE;
                    return true;
                }
                if (changListener != null) {
                    changListener.startUpDown();
                }
                return false;
            }

            @Override
            public void move(float x, float y) {

                moveCricleLimit(x, y);
                if (changListener != null) {
                    changListener.returnXYR((float) (drawX / proX), (float) (drawY / proY), (float) (drawR / proportion));
                }
            }

            @Override
            public void endMove() {
                type = NULL;
                invalidate();
                if (changListener != null) {
                    changListener.endMove();
                }
            }

            @Override
            public void upDowm(float addNum) {
                if (changListener != null) {
                    changListener.upDown((int) addNum);
                }

            }

            @Override
            public void onTouch(MotionEvent event) {

            }
        });
    }

    private void initXYSize(){
        drawX = INIT_XY;
        drawY = INIT_XY;
        type = NULL;
    }

    /**
     * 传入展示图片实际长宽
     *
     * @param bitmapw
     * @param bitmaph
     */
    public void getpicSize(int bitmapw, int bitmaph) {
        this.bitmapw = bitmapw;
        this.bitmaph = bitmaph;

        initXYSize();
        invalidate();
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (bizAtmosphereGestureListener != null) {
            return bizAtmosphereGestureListener.onTouchEvent(event);
        }
        return false;
    }


    /**
     * 判断是否在外圈内
     * @param x
     * @param y
     * @return
     */
    private boolean inCricle(float x, float y) {
        if (Math.abs(x - drawX) < CLIKC_LIMIT && Math.abs(y - drawY) < CLIKC_LIMIT) {
            return true;
        }
        return false;
    }

    /**
     * 移动外圈
     * @param x
     * @param y
     */
    private void moveCricleLimit(float x, float y) {
        drawX = x;
        drawY = y;
        if (x < CLIKC_LIMIT*2) {
            drawX = CLIKC_LIMIT*2;
        }

        if (x + CLIKC_LIMIT*2 > rect.right) {
            drawX = rect.right - CLIKC_LIMIT*2;
        }
        if (x + CLIKC_LIMIT*2 < rect.left) {
            drawX = rect.left - CLIKC_LIMIT*2;
        }


        if (y  < CLIKC_LIMIT*2) {
            drawY = CLIKC_LIMIT*2;
        }

        if (y + CLIKC_LIMIT*2 < rect.top) {
            drawY = rect.top - CLIKC_LIMIT*2;
        }

        if (y + CLIKC_LIMIT*2 > rect.bottom) {
            drawY = rect.bottom - CLIKC_LIMIT*2;
        }


        invalidate();
    }


    /**
     * 放大外圈
     */
    private void zoomCricleLimit() {
        float r = startDrawR * distance1 / distance;
        if (drawX - r < 0) {
            r = drawX;
        }
        if (drawX + r > getMeasuredWidth()) {
            r = getMeasuredWidth() - drawX;
        }
        if (drawY - r < 0) {
            r = drawY;
        }
        if (drawY + r > getMeasuredHeight()) {
            r = getMeasuredHeight() - drawY;
        }
        if (r < CLIKC_LIMIT) {
            r = CLIKC_LIMIT;
        }
        drawR = r;
        invalidate();
    }

    public float getDrawX() {
        return drawX;
    }

    public void setDrawX(float drawX) {
        this.drawX = drawX;
    }

    public float getDrawY() {
        return drawY;
    }

    public void setDrawY(float drawY) {
        this.drawY = drawY;
    }

    public float getDrawR() {
        return drawR;
    }

    public void setDrawR(float drawR) {
        this.drawR = drawR;
    }

    public double getProportion() {
        return proportion;
    }

    public void setProportion(double proportion) {
        this.proportion = proportion;
    }

    public void setChangListener(ChangListener changListener) {
        this.changListener = changListener;
    }

    public void getGpuImageWH(Rect rect){
       this.rect = rect;
    }

    public interface ChangListener {

        void returnXYR(float x, float y, float r);

        void startUpDown();

        void upDown(int number);

        void endMove();
    }

}
