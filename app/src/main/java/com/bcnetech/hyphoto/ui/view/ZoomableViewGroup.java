package com.bcnetech.hyphoto.ui.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PathEffect;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.RelativeLayout;

/**
 * Created by yhf on 2017/10/9.
 */

public class ZoomableViewGroup extends RelativeLayout {

    private int bitWidth, bitHight;
    private Rect rect;
    private ImgUpData listener;
    private float mImageWidth, mImageHeight;//初始宽高
    /**
     * 图片缩放动画时间
     */
    public static final int SCALE_ANIMATOR_DURATION = 200;

    /**
     * 惯性动画衰减参数
     */
    public static final float FLING_DAMPING_FACTOR = 0.9f;

    // States.
    private static final byte NONE = 0;
    private static final byte DRAG = 1; // 拖动操作
    private static final byte ZOOM = 2; // 放大缩小操作

    private byte mode = NONE;  // 当前模式


    private boolean isZoom = false;
    // Matrices used to move and zoom image.
    private Matrix mMatrix = new Matrix();
    private Matrix matrix = new Matrix();
    private Matrix matrixInverse = new Matrix();
    private Matrix savedMatrix = new Matrix();

    // Parameters for zooming.
    private PointF start = new PointF();
    private PointF mid = new PointF();
    private float oldDist = 1f;
    private float[] lastEvent = null;
    private long lastDownTime = 0l;
    private float mvalues[];
    private float dx, dy;

    private float[] mDispatchTouchEventWorkingArray = new float[2];
    private float[] mOnTouchEventWorkingArray = new float[2];
    private boolean checkBoundedX = false;
    private boolean checkBoundedY = false;


    private boolean isShow = false;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        mDispatchTouchEventWorkingArray[0] = ev.getX();
        mDispatchTouchEventWorkingArray[1] = ev.getY();
        mDispatchTouchEventWorkingArray = screenPointsToScaledPoints(mDispatchTouchEventWorkingArray);
        ev.setLocation(mDispatchTouchEventWorkingArray[0], mDispatchTouchEventWorkingArray[1]);
        return super.dispatchTouchEvent(ev);
    }

    public ZoomableViewGroup(Context context) {
        super(context);
        init(context);
    }

    public ZoomableViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ZoomableViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    private int mTouchSlop;

    private void init(Context context) {
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    public int getBitHight() {
        return bitHight;
    }

    public void setBitHight(int bitHight) {
        this.bitHight = bitHight;
    }

    public int getBitWidth() {
        return bitWidth;
    }

    public void setBitWidth(int bitWidth) {
        this.bitWidth = bitWidth;
    }

    public Rect getRect() {
        return rect;
    }

    public void setRect(Rect rect) {
        this.rect = rect;
    }

    public boolean isShow() {
        return isShow;
    }

    public void setShow(boolean show) {
        isShow = show;
    }

    /**
     * Determine the space between the first two fingers
     */
    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    /**
     * Calculate the mid point of the first two fingers
     */
    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    private float[] scaledPointsToScreenPoints(float[] a) {
        matrix.mapPoints(a);
        return a;
    }

    private float[] screenPointsToScaledPoints(float[] a) {
        matrixInverse.mapPoints(a);

        return a;
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                child.layout(child.getLeft(), child.getTop(), child.getLeft() + child.getMeasuredWidth(), child.getTop() + child.getMeasuredHeight());
//                child.layout(left, top, left + child.getMeasuredWidth(), top + child.getMeasuredHeight());
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                measureChild(child, widthMeasureSpec, heightMeasureSpec);
            }
        }
    }


    public void setListener(ImgUpData listener) {
        this.listener = listener;
    }

    public interface ImgUpData {
        void imgUpdata(Matrix matrix);
    }

    int MAX_ZOOM = 8;
    float scale;

    @Override
    protected void dispatchDraw(Canvas canvas) {
        float[] values = new float[9];
        matrix.getValues(values);
        mvalues = values;
        //
        canvas.save();
        canvas.translate(values[Matrix.MTRANS_X], values[Matrix.MTRANS_Y]);
        canvas.scale(values[Matrix.MSCALE_X], values[Matrix.MSCALE_Y]);
        if (listener != null) {
            listener.imgUpdata(matrix);
        }
        super.dispatchDraw(canvas);
        canvas.restore();


        if (isZoom && isShow) {
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(Color.WHITE);
            PathEffect effects = new DashPathEffect(new float[]{50, 50, 50, 50}, 1);
            paint.setPathEffect(effects);

            int drawY = getMeasuredHeight() / 2;
            int drawX = getMeasuredWidth() / 2;
            canvas.drawCircle(drawX, drawY, 100 / getScale(), paint);
        }


    }

    public boolean touchEvent(MotionEvent event) {
        // handle touch events here
        mOnTouchEventWorkingArray[0] = event.getX();
        mOnTouchEventWorkingArray[1] = event.getY();
        int nCnt = event.getPointerCount();
        mOnTouchEventWorkingArray = scaledPointsToScreenPoints(mOnTouchEventWorkingArray);

        event.setLocation(mOnTouchEventWorkingArray[0], mOnTouchEventWorkingArray[1]);

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:

                start.set(event.getRawX(), event.getRawY());
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                isZoom = true;
                if (nCnt == 2) {
                    oldDist = spacing(event);
                    if (oldDist > 10f) {
                        savedMatrix.set(matrix);
                        midPoint(mid, event);
                        mode = ZOOM;

                    }
                    lastEvent = new float[4];
                    lastEvent[0] = event.getX(0);
                    lastEvent[1] = event.getX(1);
                    lastEvent[2] = event.getY(0);
                    lastEvent[3] = event.getY(1);
                }

                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:

                dx = event.getRawX() - start.x; // 得到x轴的移动距离
                dy = event.getRawY() - start.y; // 得到x轴的移动距离
                reSetMatrix();
                break;
            case MotionEvent.ACTION_POINTER_UP:
                isZoom = false;
                mode = NONE;
                lastEvent = null;
                break;
            case MotionEvent.ACTION_MOVE:
                final float density = getResources().getDisplayMetrics().density;
                if (mode == ZOOM) {
                    if (event.getPointerCount() > 1) {
                        float newDist = spacing(event);
                        if (newDist > 30f * density) {
                            float dx = event.getRawX() - start.x; // 得到x轴的移动距离
                            float dy = event.getRawY() - start.y; // 得到x轴的移动距离
                            matrix.set(savedMatrix);
                            scale = (newDist / oldDist);

                            float[] values = new float[9];
                            matrix.getValues(values);
                            if (scale * values[Matrix.MSCALE_X] >= MAX_ZOOM) {
                                scale = MAX_ZOOM / values[Matrix.MSCALE_X];
                            }
                            mvalues = values;
                            matrix.postScale(scale, scale, mid.x, mid.y);
                            matrix.postTranslate(dx, dy);
                            matrix.invert(matrixInverse);
                            invalidate();

                        }
                    } /*else {
                        matrix.set(savedMatrix);
                        float scale = event.getY() / start.y;
                        matrix.postScale(scale, scale, mid.x, mid.y);
                        matrix.invert(matrixInverse);
                        invalidate();
                    }*/
                }
                break;
        }
        invalidate();
        return true;
    }

    /**
     * 和当前矩阵对比，检验dx，使图像移动后不会超出ImageView边界
     *
     * @param values
     * @param dx
     * @return
     */

    private float checkDxBound(float[] values, float dx) {
        if (rect == null)
            return 0;
        float width = getWidth();//view宽高
        if (rect.left > 0 && rect.right > this.getWidth()) {//超出左边界
            checkBoundedX = true;
            return -rect.left;

        } else if (rect.right < getWidth() && rect.left < 0) {//超出右边界
            checkBoundedX = true;
            return this.getWidth() - rect.right;
        } else {
            checkBoundedX = false;
            return 0;
        }
        /*float width = getWidth();//view宽高
        if (mImageWidth * values[Matrix.MSCALE_X] < width) {//边界内
            checkBoundedX = false;
            return 0;
        }
        if (values[Matrix.MTRANS_X] + dx > 0) {//左边界
            checkBoundedX = true;
            dx =  -values[Matrix.MTRANS_X];
            Log.d("xxxx","x1");
        }else if (values[Matrix.MTRANS_X] + dx < -(mImageWidth * values[Matrix.MSCALE_X] - width)) {//右边界
            dx =  -(mImageWidth * values[Matrix.MSCALE_X] - width) - values[Matrix.MTRANS_X];
            Log.d("xxxx","x2"+" "+dx);
            checkBoundedX = true;
        }*/
    }


    /**
     * 和当前矩阵对比，检验dy，使图像移动后不会超出ImageView边界
     *
     * @param values
     * @param dy
     * @return
     */
    private float checkDyBound(float[] values, float dy) {
        if (rect.top < 0 && rect.bottom < this.getHeight()) {//超出下边界
            checkBoundedY = true;
            return this.getHeight() - rect.bottom;

        } else if (rect.bottom > this.getHeight() && rect.top > 0) {//超出上边界
            checkBoundedY = true;
            return -rect.top;
        } else {
            checkBoundedY = false;
            return 0;
        }
       /* float height = getHeight();
        if (mImageHeight * values[Matrix.MSCALE_Y] < height) {
            checkBoundedY = false;
            return 0;
        }
        if (values[Matrix.MTRANS_Y] + dy > 0) {//图片当前在Y轴的位移量(values[Matrix.MTRANS_Y]值)
            Log.d("xxxx","y1");
            checkBoundedY = true;
            dy = -values[Matrix.MTRANS_Y];
        }else if (values[Matrix.MTRANS_Y] + dy < -(mImageHeight * values[Matrix.MSCALE_Y] - height)) {
            checkBoundedY = true;
            Log.d("xxxx","y2"+" "+mImageHeight);
            dy = -(mImageHeight * values[Matrix.MSCALE_Y] - height) - values[Matrix.MTRANS_Y];
        }
        return dy;*/
    }


    /**
     * 重置Matrix
     */
    private void reSetMatrix() {
        checkBoundedX = false;
        checkBoundedY = false;
        float cx = checkDxBound(mvalues, dx);
        float cy = checkDyBound(mvalues, dy);
        if (checkRest()) {
            // matrix.set(mMatrix);
            mMatrix.invert(matrixInverse);
            mScaleAnimator = new ScaleAnimator(matrix, mMatrix);
            mScaleAnimator.start();
        } else if (checkBoundedX || checkBoundedY) {
            Matrix m = new Matrix(matrix);
            matrix.postTranslate(cx, cy);
            matrix.invert(matrixInverse);
            mScaleAnimator = new ScaleAnimator(m, matrix);
            mScaleAnimator.start();
        }
        //invalidate();
    }

    public boolean checkRest() {
        // TODO Auto-generated method stub
        float[] values = new float[9];
        matrix.getValues(values);
        //获取当前X轴缩放级别
        float scale = values[Matrix.MSCALE_X];
        //获取模板的X轴缩放级别，两者做比较
        mMatrix.getValues(values);
        return scale < values[Matrix.MSCALE_X];
    }

    /**
     * 用于存放矩阵的9个值
     */
    private final float[] matrixValues = new float[9];

    /**
     * 获得当前的缩放比例
     *
     * @return
     */
    public final float getScale() {
        matrix.getValues(matrixValues);
        return matrixValues[Matrix.MSCALE_X];
    }

    public void getPicWH(int w, int h) {
        this.mImageWidth = w;
        this.mImageHeight = h;
    }

    private ScaleAnimator mScaleAnimator;
    /////

    /**
     * 缩放动画
     * <p>
     * 在给定时间内从一个矩阵的变化逐渐动画到另一个矩阵的变化
     */
    private class ScaleAnimator extends ValueAnimator implements ValueAnimator.AnimatorUpdateListener {

        /**
         * 开始矩阵
         */
        private float[] mStart = new float[9];

        /**
         * 结束矩阵
         */
        private float[] mEnd = new float[9];

        /**
         * 中间结果矩阵
         */
        private float[] mResult = new float[9];

        /**
         * 构建一个缩放动画
         * <p>
         * 从一个矩阵变换到另外一个矩阵
         *
         * @param start 开始矩阵
         * @param end   结束矩阵
         */
        public ScaleAnimator(Matrix start, Matrix end) {
            this(start, end, SCALE_ANIMATOR_DURATION);
        }

        /**
         * 构建一个缩放动画
         * <p>
         * 从一个矩阵变换到另外一个矩阵
         *
         * @param start    开始矩阵
         * @param end      结束矩阵
         * @param duration 动画时间
         */
        public ScaleAnimator(Matrix start, Matrix end, long duration) {
            super();
            setFloatValues(0, 1f);
            setDuration(duration);
            addUpdateListener(this);
            start.getValues(mStart);
            end.getValues(mEnd);
        }

        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            //获取动画进度
            float value = (Float) animation.getAnimatedValue();
            //根据动画进度计算矩阵中间插值
            for (int i = 0; i < 9; i++) {
                mResult[i] = mStart[i] + (mEnd[i] - mStart[i]) * value;
            }
            //设置矩阵并重绘
            matrix.setValues(mResult);
            //dispatchOuterMatrixChanged();
            invalidate();
        }
    }

    /**
     * 重置到初始状态
     */
    public void resetMatrix(){
        mMatrix.invert(matrixInverse);
        mScaleAnimator = new ScaleAnimator(matrix, mMatrix);
        mScaleAnimator.start();
    }

}