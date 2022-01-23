package com.bcnetech.hyphoto.ui.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.bcnetech.bcnetechlibrary.util.LogUtil;
import com.bcnetech.hyphoto.data.PathData;
import com.bcnetech.hyphoto.R;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by wenbin on 16/7/28.
 */
public class MattingPaintView extends AppCompatImageView {
    public static final int LIMIT = 20;
    public static final int BACKGROUND = Color.RED;
    public static final int MAIN = Color.BLUE;
    private float PAINT_SIZE = 60f;

    private int typeColor;

    private Path path;

    private Paint brush_red, mBitmapPaint, brush2_red;
    private Paint brush_blue, brush2_blue;

    private OnDrawListener listener;
    private int width, height;
    private Bitmap baseBitmap;
    private Canvas bitCanvas;


    private boolean canPath, canPaint, canDraw;
    private float pointX, pointY;
    private float pax;
    //画图path
    private List<PathData> savePathDatas;
    private PathData pathData;
    private Paint pathPaint;
    private Paint pathPaint2;
    private List<PathData.PaintCircle> paintCircleList;

    private Rect canvesRect, bitmapRect;
    private int type;

    private int countBackground = 0;
    private int countMain = 0;

    public MattingPaintView(Context context) {
        super(context);
        init();
    }

    public MattingPaintView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MattingPaintView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public boolean isCanDraw() {
        return canDraw;
    }

    public void setCanDraw(boolean canDraw) {
        this.canDraw = canDraw;
    }

    private void init() {
        setAlpha(0.6f);
        savePathDatas = new ArrayList<>();

        mBitmapPaint = new Paint(Paint.DITHER_FLAG);

        brush_red = new Paint();
        brush2_red = new Paint();

        typeColor = getResources().getColor(R.color.translucent);
        brush_red.setAntiAlias(true);
        brush2_red.setAntiAlias(true);
        brush_red.setAlpha(10);
        brush2_red.setAlpha(10);
        brush_red.setColor(BACKGROUND);
        brush2_red.setColor(BACKGROUND);
        brush_red.setStrokeCap(Paint.Cap.ROUND);// 形状
        brush2_red.setStrokeCap(Paint.Cap.ROUND);// 形状
        brush_red.setStyle(Paint.Style.STROKE);
        brush_red.setStrokeJoin(Paint.Join.ROUND);
        brush_red.setStrokeWidth(PAINT_SIZE);

        brush_blue = new Paint();
        brush2_blue = new Paint();

        brush_blue.setAntiAlias(true);
        brush2_blue.setAntiAlias(true);
        brush_blue.setAlpha(10);
        brush2_blue.setAlpha(10);
        brush_blue.setColor(MAIN);
        brush2_blue.setColor(MAIN);
        brush_blue.setStrokeCap(Paint.Cap.ROUND);// 形状
        brush2_blue.setStrokeCap(Paint.Cap.ROUND);// 形状
        brush_blue.setStyle(Paint.Style.STROKE);
        brush_blue.setStrokeJoin(Paint.Join.ROUND);
        brush_blue.setStrokeWidth(PAINT_SIZE);


        pax = PAINT_SIZE;

        pathPaint = new Paint();
        pathPaint.setAntiAlias(true);
        pathPaint.setAlpha(10);
        pathPaint.setStrokeCap(Paint.Cap.ROUND);// 形状
        pathPaint.setStyle(Paint.Style.STROKE);
        pathPaint.setStrokeJoin(Paint.Join.ROUND);
        pathPaint.setStrokeWidth(PAINT_SIZE);

        pathPaint2 = new Paint();
        pathPaint2.setAntiAlias(true);
        pathPaint2.setAlpha(10);
        pathPaint2.setStrokeCap(Paint.Cap.ROUND);// 形状
        //brush.setMaskFilter(new BlurMaskFilter(10, BlurMaskFilter.Blur.NORMAL));
        //brush.setPatEffect(new CornerPathEffect(10));

    }

    // dp转ps
    public static int Dp2Px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }


    /**
     * 设置画笔颜色
     *
     * @param
     */
    public void setPaintColor(int Color) {
        typeColor = Color;
//        if (brush != null) {
//            brush.setColor(typeColor);
//            brush2.setColor(typeColor);
//          /*  brush.setShadowLayer(30, 5, 2, typeColor);
//            brush2.setShadowLayer(30, 5, 2, typeColor);*/
//        }
    }


    public void setPaintType(int type) {
        this.type = type;

    }


    public void setImgRect(Rect canvesRect) {
        this.canvesRect = canvesRect;
        if (this.width != 0) {
            pax = (float) (PAINT_SIZE * (this.width * 1.0 / (canvesRect.right - canvesRect.left)));
            if (pax < 5) {
                pax = 5;
            }
        } else {
            pax = PAINT_SIZE;
        }

        brush_red.setStrokeWidth(pax);
        brush_blue.setStrokeWidth(pax);
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

        this.width = width;
        this.height = height;
        startDraw(width, height);
        LogUtil.d(width + "  " + height);
        return false;
    }

    /**
     * 设置交互属性
     *
     * @param width
     * @param height
     * @return
     */
    public boolean setCanveLayoutParms(int width, int height) {

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
        if (width > 0 && height > 0 && bitCanvas == null) {
            baseBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bitCanvas.setBitmap(baseBitmap);
        }

        Paint paint = new Paint();
        paint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
        bitCanvas.drawPaint(paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC));

        invalidate();

    }

    public void setBaseCanves(Bitmap bitmap) {
        if (this.baseBitmap != null) {
            baseBitmap.recycle();
        }
        this.baseBitmap = bitmap;
        bitCanvas.setBitmap(baseBitmap);
        reDrawPath(bitCanvas);
        invalidate();// 刷新
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Checks for the event that occurs
        float currentPaintX, currentPaintY;
        listener.touchEven(event);

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                if (canvesRect != null && event.getY() > canvesRect.top && event.getY() < canvesRect.bottom && event.getX() > canvesRect.left && event.getX() < canvesRect.right && event.getPointerCount() == 1) {
                    if (canvesRect != null) {
                        pointX = (event.getX() - canvesRect.left) / (canvesRect.right - canvesRect.left) * width;
                        pointY = (event.getY() - canvesRect.top) / (canvesRect.bottom - canvesRect.top) * height;
                    } else {
                        pointX = event.getX();
                        pointY = event.getY();
                    }
                    path = new Path();
                    pathData = new PathData();
                    paintCircleList = new ArrayList<>();
                    path.moveTo(pointX, pointY);

                    canPaint = false;
                    canPath = true;
                }

                return true;
            case MotionEvent.ACTION_MOVE:
                if (canvesRect != null && event.getY() > canvesRect.top && event.getY() < canvesRect.bottom && event.getX() > canvesRect.left && event.getX() < canvesRect.right && event.getPointerCount() == 1) {

                    if (canPath == false) {
                        if (canvesRect != null) {
                            pointX = (event.getX() - canvesRect.left) / (canvesRect.right - canvesRect.left) * width;
                            pointY = (event.getY() - canvesRect.top) / (canvesRect.bottom - canvesRect.top) * height;
                        } else {
                            pointX = event.getX();
                            pointY = event.getY();
                        }
                        path = new Path();
                        pathData = new PathData();
                        paintCircleList = new ArrayList<>();
                        path.moveTo(pointX, pointY);

                        canPaint = false;
                        canPath = true;
                    }

                    if (canvesRect != null) {
                        currentPaintX = (event.getX() - canvesRect.left) / (canvesRect.right - canvesRect.left) * width;
                        currentPaintY = (event.getY() - canvesRect.top) / (canvesRect.bottom - canvesRect.top) * height;
                    } else {
                        currentPaintX = event.getX();
                        currentPaintY = event.getY();
                    }

                    if (Math.abs(currentPaintX - pointX) + Math.abs(currentPaintY - pointY) > LIMIT && canPath && canDraw) {

                        canPaint = true;
                        path.lineTo(currentPaintX, currentPaintY);

                        PathData.PaintCircle paintCircle = new PathData().new PaintCircle();
                        if (type == 1) {
//                        path_red.lineTo(currentPaintX, currentPaintY);
                            bitCanvas.drawPath(path, brush_red);
                            bitCanvas.drawCircle(currentPaintX, currentPaintY, pax / 2, brush2_red);

                            paintCircle.setCurrentPaintX(currentPaintX);
                            paintCircle.setCurrentPaintY(currentPaintY);

                        } else if (type == 2) {
//                        path_blue.lineTo(currentPaintX, currentPaintY);
                            bitCanvas.drawPath(path, brush_blue);
                            bitCanvas.drawCircle(currentPaintX, currentPaintY, pax / 2, brush2_blue);

                            paintCircle.setCurrentPaintX(currentPaintX);
                            paintCircle.setCurrentPaintY(currentPaintY);
                        }
                        paintCircleList.add(paintCircle);
                        pathData.setPaintCircle(paintCircleList);

                    }
                }

                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                if (event.getPointerCount() != 1) {
                    canPath = false;
                    canPaint = false;
                    return false;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (canPaint) {
                    pathData.setPath(path);
                    pathData.setPaintSize(pax);
                    pathData.setPaint(pathPaint);
                    pathData.setPaint2(pathPaint2);
                    if (type == 1) {
                        countBackground = countBackground + 1;
                        pathData.setColor(BACKGROUND);
                    } else if (type == 2) {
                        countMain = countMain + 1;
                        pathData.setColor(MAIN);
                    }
                    pathData.setCountBackground(countBackground);
                    pathData.setCountMain(countMain);

                    if (countBackground >= 1 && countMain >= 1) {
                        pathData.setDo(true);
                        beforDrow(savePathDatas.size() + 1);
                    } else {
                        pathData.setDo(false);
                    }
                    savePathDatas.add(pathData);


                }
                canPath = false;
                canPaint = false;
                break;
            default:
                return false;
        }
        invalidate();
        // Force a view to draw again
        return false;
    }


    public void undo() {


        if (savePathDatas != null && savePathDatas.size() > 0) {
            boolean isDo = savePathDatas.get(savePathDatas.size() - 1).isDo();
            reStartDraw();
            savePathDatas.remove(savePathDatas.size() - 1);
            if (savePathDatas.size() >= 1) {
                countBackground = savePathDatas.get(savePathDatas.size() - 1).getCountBackground();
                countMain = savePathDatas.get(savePathDatas.size() - 1).getCountMain();
            } else {
                countBackground = 0;
                countMain = 0;
            }
            reDrawPath(bitCanvas);
            if (countBackground >= 1 && countMain >= 1) {
                pathData.setDo(true);
                beforDrow(savePathDatas.size() + 1);
            } else {
                pathData.setDo(false);
            }
            if (isDo) {
                beforDrow(savePathDatas.size());
            }

            invalidate();// 刷新
        }

    }

    public void undo(Bitmap bitmap) {
        baseBitmap = bitmap;
        invalidate();

    }

    public void reDrawPath(Canvas bitCanvas) {

        if (savePathDatas != null && savePathDatas.size() > 0) {

            //将路径保存列表中的路径重绘在画布上
            Iterator<PathData> iter = savePathDatas.iterator();
            while (iter.hasNext()) {
                PathData dp = iter.next();
                dp.getPaint().setStrokeWidth(dp.getPaintSize());
                dp.getPaint().setColor(dp.getColor());

                dp.getPaint2().setColor(dp.getColor());
                Iterator<PathData.PaintCircle> paintCircleIterator = dp.getPaintCircle().iterator();
                while (paintCircleIterator.hasNext()) {
                    PathData.PaintCircle paintCircle = paintCircleIterator.next();
                    bitCanvas.drawCircle(paintCircle.getCurrentPaintX(), paintCircle.getCurrentPaintY(), dp.getPaintSize() / 2, dp.getPaint2());

                }
                bitCanvas.drawPath(dp.getPath(), dp.getPaint());
            }

        }
    }

    private void beforDrow(int size) {
        if (listener != null) {
            listener.beforDrow(baseBitmap, size);
        }

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

    public void setListener(OnDrawListener listener) {
        this.listener = listener;
    }

    public OnDrawListener getListener() {
        return listener;
    }

    public interface OnDrawListener {
        void beforDrow(Bitmap bit, int savePathDatas);

        void touchEven(MotionEvent event);

    }


}
