package com.bcnetech.hyphoto.ui.view;

import android.content.Context;
import android.graphics.Bitmap;
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
 * Created by yhf on 17/10/30.
 */
public class ImageParmsCircleView extends ImageView {

    private final static int ZOOM = 1;
    private final static int MOVE = 2;
    private final static int NULL = 4;

    private final static int CLIKC_LIMIT = 40;//按钮范围
    private final static int INIT_XY=-1;
    private final static int BLUE_PAINT_SIZE=4;//中心点蓝圈画笔宽度
    private final static int PAINT_SIZE=5;//外圈画笔宽度
    private final static int PAINT_CENTER_SIZE=10;//外圈画笔宽度


    private int type;
    private float drawX;//圆环的位置
    private float drawY;
    private float drawR,mDrawR;
    private double proportion;//实际压缩比
    private double proX;//实际压缩比
    private double proY;//实际压缩比
    private float startDrawR;
    private int bitmapw, bitmaph;
    private Paint bluePaint, paint,centerPaint;
    private ChangListener changListener;
    private BizAtmosphereGestureListener bizAtmosphereGestureListener;
    private Rect rect;
    private Bitmap bitmap;
    private float pointX, pointY;
    private float currentPaintX, currentPaintY;
    private ZoomableViewGroup zoomableViewGroup;

    public ZoomableViewGroup getZoomableViewGroup() {
        return zoomableViewGroup;
    }

    public void setZoomableViewGroup(ZoomableViewGroup zoomableViewGroup) {
        this.zoomableViewGroup = zoomableViewGroup;
    }

    public ImageParmsCircleView(Context context) {
        super(context);
        initData();
        onViewClick();
    }

    public ImageParmsCircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initData();
        onViewClick();
    }

    public ImageParmsCircleView(Context context, AttributeSet attrs, int defStyleAttr) {
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
                drawR = drawX / 3;
                mDrawR=drawX/3;
            }
            else{
                drawR = drawY / 3;
                mDrawR=drawX/3;
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

        canvas.drawLine(drawX, drawY, drawX-drawR/5,drawY, bluePaint);//中心点
        canvas.drawLine(drawX, drawY, drawX+drawR/5,drawY, bluePaint);//中心点
        canvas.drawLine(drawX, drawY, drawX,drawY-drawR/5, bluePaint);//中心点
        canvas.drawLine(drawX, drawY, drawX,drawY+drawR/5, bluePaint);//中心点
        canvas.drawCircle(drawX, drawY, drawR, paint);// 白圈
        canvas.drawCircle(drawX, drawY, drawR-paint.getStrokeWidth(), centerPaint);// 白圈

    }

    private void initData() {
        initXYSize();

        bluePaint = new Paint();
        bluePaint.setAntiAlias(true);
        bluePaint.setStrokeWidth(BLUE_PAINT_SIZE);
        bluePaint.setColor(getResources().getColor(R.color.sing_in_color));

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStrokeWidth(PAINT_SIZE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.parseColor("#ADB5C2"));

        centerPaint=new Paint();
        centerPaint.setAntiAlias(true);
        centerPaint.setStrokeWidth(PAINT_CENTER_SIZE);
        centerPaint.setStyle(Paint.Style.STROKE);

        bizAtmosphereGestureListener = new BizAtmosphereGestureListener(getContext());

    }

    public void resize(float size){
        drawR =mDrawR/size;
        bluePaint.setStrokeWidth(BLUE_PAINT_SIZE/size);
        centerPaint.setStrokeWidth(PAINT_CENTER_SIZE/size);
        paint.setStrokeWidth(PAINT_SIZE/size);
        invalidate();
    }

    private void onViewClick(){
        bizAtmosphereGestureListener.setBizAtmosphereGestureDetector(new BizAtmosphereGestureListener.BizAtmosphereGestureDetector() {

            @Override
            public void startZoom(float distance) {
//                ImageParmsCircleView.this.distance = (int) distance;//
//                startDrawR = drawR;
            }

            @Override
            public void zoom(float distance) {
//                type = ZOOM;
//                distance1 = (int) distance;
//                zoomCricleLimit();
//                if (changListener != null) {
//                    changListener.returnXYR((float) (drawX / proX), (float) (drawY / proY), (float) (drawR / proportion));
//                }
            }

            @Override
            public void endZoom() {
//                type = NULL;
//                invalidate();
            }

            @Override
            public boolean startMove(float startX, float startY) {

//
//                if (rect != null) {
//                    pointX = (startX - rect.left) / (rect.right - rect.left) * bitmapw;
//                    pointY = (startY - rect.top) / (rect.bottom - rect.top) * bitmaph;
//                } else {
                    pointX = startX;
                    pointY = startY;
//


//                if (inCricle(startX, startY)) {
//                    type = MOVE;
                return true;
//                }
//                if (changListener != null) {
//                    changListener.startUpDown();
//                }
//                return false;
            }

            @Override
            public void move(float x, float y) {


//                if (rect != null) {
//                    currentPaintX = (x - rect.left) / (rect.right - rect.left) * bitmapw;
//                    currentPaintY = (y - rect.top) / (rect.bottom - rect.top) * bitmaph;
//                } else {
                    currentPaintX = x;
                    currentPaintY = y;
//                }

                float xx=drawX+(currentPaintX-pointX);
                float yy=drawY+(currentPaintY-pointY);

                pointY=currentPaintY;
                pointX=currentPaintX;



                moveCricleLimit(xx, yy);
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
//                if (changListener != null) {
//                    changListener.upDown((int) addNum);
//                }

            }

            @Override
            public void onTouch(MotionEvent event) {
                zoomableViewGroup.touchEvent(event);
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

//        initXYSize();
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
        if (Math.abs(x - drawX) < CLIKC_LIMIT*2 && Math.abs(y - drawY) < CLIKC_LIMIT*2) {
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
//        if (x < CLIKC_LIMIT*2) {
//            drawX = CLIKC_LIMIT*2;
//        }

        if (x > rect.right) {
            drawX = rect.right ;
        }
        if (x  < rect.left) {
            drawX = rect.left ;
        }
//
//
//        if (y  < CLIKC_LIMIT*2) {
//            drawY = CLIKC_LIMIT*2;
//        }

        if (y  < rect.top) {
            drawY = rect.top;
        }

        if (y  > rect.bottom) {
            drawY = rect.bottom ;
        }


        int color = bitmap.getPixel((int)(drawX-rect.left-0.01), (int)(drawY-rect.top-0.01));
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);
        centerPaint.setColor(Color.rgb(r,g,b));
        changListener.returnRGB(color);

//        LogUtil.d("r=" + r + ",g=" + g + ",b=" + b);

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

        void returnRGB(int color);
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
        if (drawY == INIT_XY) {
            drawY = getMeasuredHeight() / 2;
            drawX = getMeasuredWidth() / 2;
        }
        int color = bitmap.getPixel((int)(drawX-rect.left-0.01), (int)(drawY-rect.top-0.01));
        centerPaint.setColor(Color.rgb(Color.red(color),Color.green(color),Color.blue(color)));
        changListener.returnRGB(color);
        invalidate();
    }
}
