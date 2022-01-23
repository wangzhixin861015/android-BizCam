package com.bcnetech.hyphoto.ui.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.bcnetech.hyphoto.App;
import com.bcnetech.hyphoto.listener.BaseGestureListener;

/**
 * Created by wenbin on 16/7/28.
 */
public class PaintView extends ImageView {
    public static final int LIGHT = 1;
    public static final int SAT = 2;
    public static final int LIMIT = 1;

    public static final int FORGROUD = Color.BLUE;
    public static final int BACKGROUD = Color.RED;

    private float PAINT_SIZE=80*App.getInstance().getRatio() ;
    private float BLUR_R = 8f;

    private int type = LIGHT;
    private int typeColor = BACKGROUD;
    private Paint brush, mBitmapPaint, brush2;
    private Path path;
    private OnDrawListener listener;
    private int width, height;
    private Bitmap baseBitmap;
    private Bitmap baseBitmap2;
    private Canvas bitCanvas;
    private Canvas bitCanvas2;
    private boolean canPaint, canPath;
    private float pointX, pointY;
    private float pax;
    private boolean isShow;
    private BaseGestureListener baseGestureListener;
    private float currentPaintX, currentPaintY;

    private Rect canvesRect,bitmapRect;
    private float dx, dy, scale;
    private  DisplayMetrics displayMetrics;
    public PaintView(Context context) {
        super(context);
        init();
    }

    public PaintView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PaintView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    private void init() {
        baseGestureListener = new BaseGestureListener();
        mBitmapPaint = new Paint(Paint.DITHER_FLAG);
        brush = new Paint();
        path = new Path();
        brush2 = new Paint();

        brush.setAntiAlias(true);
        brush2.setAntiAlias(true);
        brush.setColor(typeColor);
        brush2.setColor(typeColor);
        brush.setStrokeCap(Paint.Cap.ROUND);
        brush.setStrokeJoin(Paint.Join.ROUND);
        brush2.setStrokeCap(Paint.Cap.ROUND);
        brush2.setStrokeJoin(Paint.Join.ROUND);
        brush.setStrokeWidth(PAINT_SIZE);
        pax = PAINT_SIZE;
        brush.setMaskFilter(new BlurMaskFilter(pax, BlurMaskFilter.Blur.NORMAL));
        //brush.setPathEffect(new CornerPathEffect(5));
        brush2.setMaskFilter(new BlurMaskFilter(pax, BlurMaskFilter.Blur.NORMAL));
        //brush2.setPathEffect(new DiscretePathEffect(10.0F, 2.0F));

        baseGestureListener.setGestureDetector(new BaseGestureListener.GestureDetector() {
            @Override
            public void onDoubleClick() {

            }

            @Override
            public void onStartZoomMove(float pointOneX, float pointOneY, float pointTwoX, float pointTwoY) {
                if (listener != null) {
                    listener.onStartZoomMove(pointOneX, pointOneY, pointTwoX, pointTwoY);
                }
            }

            @Override
            public void onZoomMove(float pointOneX, float pointOneY, float pointTwoX, float pointTwoY) {
                if (listener != null) {
                    listener.onZoomMove(pointOneX, pointOneY, pointTwoX, pointTwoY);
                }
            }

            @Override
            public void onEndZoomMove() {
                if (listener != null) {
                    listener.onEndZoomMove();
                }
            }

            @Override
            public void onStartOneMove(float initX, float initY,MotionEvent e) {
                if (canvesRect != null) {
                    pointX = (initX - canvesRect.left) / (canvesRect.right - canvesRect.left) * width;
                    pointY = (initY - canvesRect.top) / (canvesRect.bottom - canvesRect.top) * height;
                } else {
                    pointX = initX;
                    pointY = initY;
                }
                //  path.moveTo(pointX, pointY);
                canPaint = false;
                canPath = true;
            }

            @Override
            public void onOneMove(float x, float y, MotionEvent event) {
                if (canvesRect != null) {
                    currentPaintX = (x - canvesRect.left) / (canvesRect.right - canvesRect.left) * width;
                    currentPaintY = (y - canvesRect.top) / (canvesRect.bottom - canvesRect.top) * height;
                } else {
                    currentPaintX = x;
                    currentPaintY = y;
                }
                int limit = (int) (Math.abs(currentPaintX - pointX) + Math.abs(currentPaintY - pointY));
                if ( limit > LIMIT &&canPath) {
                    canPaint = true;
                    if (type == LIGHT) {
                        bitCanvas.drawLine(pointX, height - pointY, currentPaintX, height - currentPaintY, brush2);
                        // bitCanvas.drawCircle(currentPaintX,height - currentPaintY,pax/2,brush2);
                        if (listener != null) {
                            listener.moveDrow(baseBitmap);
                        }

                    } else {
                        bitCanvas2.drawLine(pointX, pointY, currentPaintX, currentPaintY, brush2);
                        //bitCanvas2.drawCircle(currentPaintX, currentPaintY,pax/2,brush2);
                        if (listener != null) {
                            listener.moveDrow(baseBitmap2);
                        }

                    }


                    pointX = currentPaintX;
                    pointY = currentPaintY;
                }
            }

            @Override
            public void onEndOneMove(MotionEvent event) {
                canPath = false;
                if (listener != null && canPaint) {
                    if (type == LIGHT) {
                        listener.moveDrow(baseBitmap);
                    } else {
                        listener.moveDrow(baseBitmap2);
                    }
                }
                canPath = false;
            }

            @Override
            public void touch(MotionEvent event) {

            }
        });
    }

    /**
     * 设置画笔颜色
     *
     * @param Color
     */
    public void setPaintColor(int Color) {
        typeColor = Color;
        if (brush != null) {
            brush.setColor(typeColor);
            brush2.setColor(typeColor);
        }
    }

    /**
     * 设置当前类型
     *
     * @param type
     */
    public void setType(int type) {
        this.type = type;
    }

    /**
     * 判断是否显示
     */
    public void setIsShow(boolean isShow) {
        this.isShow = isShow;
        invalidate();
    }

    /**
     * 判断是否叠加
     *
     * @param isAccumulation
     */
    public void setPaintType(boolean isAccumulation) {
        if (isAccumulation) {
            PAINT_SIZE = 20* App.getInstance().getRatio();
            BLUR_R = 110f;
        } else {
            PAINT_SIZE = 80*App.getInstance().getRatio();
            BLUR_R = 80f;
        }
        brush.setStrokeWidth(PAINT_SIZE);
        brush2.setStrokeWidth(PAINT_SIZE);
        pax = PAINT_SIZE;
        //brush2.setMaskFilter(new BlurMaskFilter(BLUR_R, BlurMaskFilter.Blur.NORMAL));
    }


    /**
     * @param number
     */
    public void setPaintColor(float number) {
        if (number > 1 || number < -1) {
            number /= 10;
        }
        if (number == 0) {
            typeColor = Color.argb(255, 128, 128, 128);
        } else {
            typeColor = Color.argb(255, (int) (128 + 127 * number), (int) (128 + 127 * number), (int) (128 + 127 * number));
        }
        setPaintColor(typeColor);
    }


    public void setImgRect(Rect canvesRect) {
        this.canvesRect = canvesRect;
        if (this.width != 0) {
            float size= (float) (this.width * 1.0 / (canvesRect.right - canvesRect.left));
            pax =  PAINT_SIZE * size;
        } else {
            pax = PAINT_SIZE;
        }

        brush.setMaskFilter(new BlurMaskFilter(pax, BlurMaskFilter.Blur.NORMAL));
        //brush.setPathEffect(new CornerPathEffect(5));
        brush2.setMaskFilter(new BlurMaskFilter(pax, BlurMaskFilter.Blur.NORMAL));
        brush.setStrokeWidth(pax);
        brush2.setStrokeWidth(pax);
        invalidate();
    }



    public void setScale(float scale, float dx, float dy) {
        if (scale != 1) {
            this.dx = dx;
            this.dy = dy;
            this.scale = scale;
            this.brush.setStrokeWidth(pax / scale);
            this.brush2.setStrokeWidth(pax / scale);
            invalidate();
        }
    }


    public Bitmap getBaseBitmap() {
        return baseBitmap;
    }

    public Bitmap getBaseBitmap2() {
        return baseBitmap2;
    }

    /**
     * 设置交互属性
     *
     * @param listener
     * @param width
     * @param height
     * @return
     */
    public boolean setCanveLayoutParms(OnDrawListener listener, int width, int height) {
        this.listener = listener;
        this.width = width;
        this.height = height;
        startDraw(width, height);
        return false;
    }

    private void startDraw(int width, int height) {

        baseBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitCanvas = new Canvas(baseBitmap);
        bitCanvas.drawARGB(255, 128, 128, 128);
        bitmapRect = new Rect(0, 0, width, height);

        baseBitmap2 = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitCanvas2 = new Canvas(baseBitmap2);
        bitCanvas2.drawARGB(255, 128, 128, 128);
    }

    public void reStartDraw() {
        if (width > 0 && height > 0) {
            baseBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bitCanvas.setBitmap(baseBitmap);
            baseBitmap2 = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bitCanvas2.setBitmap(baseBitmap2);
            path.reset();
        }
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //float currentPaintX,currentPaintY;

        if (listener != null) {
            listener.touchEven(event);
        }
        invalidate();
        return baseGestureListener.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (isShow && baseBitmap != null) {
            if (canvesRect != null) {
                canvas.drawBitmap(baseBitmap, bitmapRect, canvesRect, mBitmapPaint);
            } else {
                canvas.drawBitmap(baseBitmap, 0, 0, mBitmapPaint);
            }

        }
    }

    public interface OnDrawListener {
        void beforDrow(Bitmap bit);

        void touchEven(MotionEvent event);

        void moveDrow(Bitmap bit);

        public void onStartZoomMove(float pointOneX, float pointOneY, float pointTwoX, float pointTwoY);

        public void onZoomMove(float pointOneX, float pointOneY, float pointTwoX, float pointTwoY);

        public void onEndZoomMove();
    }

    public DisplayMetrics getDisplayMetrics() {
        return displayMetrics;
    }

    public void setDisplayMetrics(DisplayMetrics displayMetrics) {
        this.displayMetrics = displayMetrics;
    }


}