package com.bcnetech.hyphoto.ui.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.bcnetech.bcnetechlibrary.util.LogUtil;
import com.bcnetech.hyphoto.App;
import com.bcnetech.hyphoto.R;

/**
 * Created by wenbin on 16/7/28.
 */
public class BGPaintView extends ImageView {
    public static final int LIMIT = 20;
    public static final int FORGROUD = Color.BLUE;
    public static final int BACKGROUD = Color.RED;
    private float PAINT_SIZE = 80* App.getInstance().getRatio();

    private int typeColor;
    private Paint brush, mBitmapPaint, brush2;
    private Path path;
    private OnDrawListener listener;
    private int width, height;
    private Bitmap baseBitmap;
    private Canvas bitCanvas;
    private boolean canPaint, canPath;
    private float pointX, pointY;
    private float pax;

    private Rect canvesRect, bitmapRect;

    public BGPaintView(Context context) {
        super(context);
        init();
    }

    public BGPaintView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BGPaintView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    private void init() {
        setAlpha(0.6f);
        mBitmapPaint = new Paint(Paint.DITHER_FLAG);
        brush = new Paint();
        path = new Path();
        brush2 = new Paint();

        typeColor = getResources().getColor(R.color.translucent);
        brush.setAntiAlias(true);
        brush2.setAntiAlias(true);
        brush.setAlpha(10);
        brush2.setAlpha(10);
        brush.setColor(typeColor);
        brush2.setColor(typeColor);
        brush.setStrokeCap(Paint.Cap.ROUND);// 形状
        brush2.setStrokeCap(Paint.Cap.ROUND);// 形状
        brush.setStyle(Paint.Style.STROKE);
        brush.setStrokeJoin(Paint.Join.ROUND);
        brush.setStrokeWidth(PAINT_SIZE);

        pax = PAINT_SIZE;
        //brush.setMaskFilter(new BlurMaskFilter(10, BlurMaskFilter.Blur.NORMAL));
        //brush.setPathEffect(new CornerPathEffect(10));
    }

    // dp转ps
    public static int Dp2Px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
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
          /*  brush.setShadowLayer(30, 5, 2, typeColor);
            brush2.setShadowLayer(30, 5, 2, typeColor);*/
        }
    }

    public void setImgRect(Rect canvesRect) {
        this.canvesRect = canvesRect;
        if (this.width != 0) {
            float size= (float) (this.width * 1.0 / (canvesRect.right - canvesRect.left));
            pax =  PAINT_SIZE * size;
        } else {
            pax = PAINT_SIZE;
        }


        brush.setStrokeWidth(pax);
        //updatePaintSize(pax);
        invalidate();
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
        LogUtil.d(width + "  " + height);
        return false;
    }

    private void startDraw(int width, int height) {

        baseBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitCanvas = new Canvas(baseBitmap);
        bitmapRect = new Rect(0, 0, width, height);
    }

       public void reStartDraw() {
        if (width > 0 && height > 0) {
            baseBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bitCanvas.setBitmap(baseBitmap);
            path.reset();
        }
        invalidate();

    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Checks for the event that occurs
        float currentPaintX, currentPaintY;
        listener.touchEven(event);


            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    if(event.getY()>canvesRect.top&&event.getY()<canvesRect.bottom&&event.getX()>canvesRect.left&&event.getX()<canvesRect.right&&event.getPointerCount() == 1){
                        if (canvesRect != null) {
                            pointX = (event.getX() - canvesRect.left) / (canvesRect.right - canvesRect.left) * width;
                            pointY = (event.getY() - canvesRect.top) / (canvesRect.bottom - canvesRect.top) * height;
                        } else {
                            pointX = event.getX();
                            pointY = event.getY();
                        }
                        path.moveTo(pointX, pointY);
                        canPath = true;
                        canPaint = false;
                    }
                    return true;
                case MotionEvent.ACTION_MOVE:
                    if(event.getY()>canvesRect.top&&event.getY()<canvesRect.bottom&&event.getX()>canvesRect.left&&event.getX()<canvesRect.right&&event.getPointerCount() == 1){

                        if(canPath==false){
                            if (canvesRect != null) {
                                pointX = (event.getX() - canvesRect.left) / (canvesRect.right - canvesRect.left) * width;
                                pointY = (event.getY() - canvesRect.top) / (canvesRect.bottom - canvesRect.top) * height;
                            } else {
                                pointX = event.getX();
                                pointY = event.getY();
                            }
                            path.moveTo(pointX, pointY);
                            canPath = true;
                            canPaint = false;
                        }

                        if (canvesRect != null) {
                            currentPaintX = (event.getX() - canvesRect.left) / (canvesRect.right - canvesRect.left) * width;
                            currentPaintY = (event.getY() - canvesRect.top) / (canvesRect.bottom - canvesRect.top) * height;
                        } else {
                            currentPaintX = event.getX();
                            currentPaintY = event.getY();
                        }
                        if (Math.abs(currentPaintX - pointX) + Math.abs(currentPaintY - pointY) > LIMIT && canPath) {
                            canPaint = true;
                            path.lineTo(currentPaintX, currentPaintY);
                            bitCanvas.drawPath(path, brush);
                            bitCanvas.drawCircle(currentPaintX, currentPaintY, pax / 2, brush2);
                        }
                    }

                    break;
                case MotionEvent.ACTION_POINTER_DOWN:
                    if (event.getPointerCount() != 1) {
                        canPath = false;
                        return false;
                    }
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    if (listener != null && canPaint) {
                        listener.beforDrow(baseBitmap);
                    }
                    canPaint=false;
                    canPath = false;
                    break;
                default:
                    return false;
            }
            invalidate();
        // Force a view to draw again
        return false;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(0x00000000);
        if (baseBitmap != null) {
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

    }

}





