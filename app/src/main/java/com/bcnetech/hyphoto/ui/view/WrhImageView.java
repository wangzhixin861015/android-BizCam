package com.bcnetech.hyphoto.ui.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ViewTreeObserver;

import com.bcnetech.bcnetechlibrary.util.ContentUtil;
import com.bcnetech.hyphoto.utils.BitmapUtils;
import com.bcnetech.hyphoto.R;

/**
 * Created by Administrator on 2015/9/7 0007.
 */
public class WrhImageView extends AppCompatImageView implements ViewTreeObserver.OnGlobalLayoutListener, ScaleGestureDetector.OnScaleGestureListener {

    private int mRadius = 200;
    public static final float SCALE_MAX = 3.0f;

    private Paint mPaint;
    private RectF shelterR;
    private RectF circleR;

    private Bitmap mBitmap;
    private float initScale = 1.0f;

    private float mLastX;
    private float mLastY;

    private int lastPointerCount;

    // private int mRadius;
    /**
     * 缩放的手势检测
     */
    private ScaleGestureDetector mScaleGestureDetector = null;

    private GestureDetector mGestureDetector;

    private boolean isAutoScale;

    private WrhLoadingInter WrhLoadingInter;

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            measureSize(mBitmap);
            setImageBitmap(mBitmap);
            WrhLoadingInter.onLoading();
            return false;
        }
    });

    /**
     * 用于存放矩阵的9个值
     */
    private final float[] matrixValues = new float[9];
    private final Matrix mScaleMatrix = new Matrix();
    private boolean once = true;

    public WrhImageView(Context context) {
        this(context, null);
    }

    public WrhImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WrhImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        super.setScaleType(ScaleType.MATRIX);
        mScaleGestureDetector = new ScaleGestureDetector(context, this);
        mGestureDetector = new GestureDetector(context,
                new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onDoubleTap(MotionEvent e) {
                        if (isAutoScale == true)
                            return true;
                        float x = e.getX();
                        float y = e.getY();
                        if (getScale() < SCALE_MAX) {
                            WrhImageView.this.postDelayed(
                                    new AutoScaleRunnable(SCALE_MAX, x, y), 16);
                            isAutoScale = true;
                        } else {
                            WrhImageView.this.postDelayed(
                                    new AutoScaleRunnable(initScale, x, y), 16);
                            isAutoScale = true;
                        }

                        return true;
                    }
                });
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeWidth(2);
        mPaint.setAntiAlias(true);
        mPaint.setColor(getResources().getColor(R.color.dg_color_translucent));
    }

    private float getScale() {
        mScaleMatrix.getValues(matrixValues);
        return matrixValues[Matrix.MSCALE_X];
    }

    private float getTranslateX() {
        mScaleMatrix.getValues(matrixValues);
        return matrixValues[Matrix.MTRANS_X];
    }

    private float getTranslateY() {
        mScaleMatrix.getValues(matrixValues);
        return matrixValues[Matrix.MTRANS_Y];
    }

    /**
     * 用来保证阴影的大小足以遮住图片
     *
     * @return
     */
    private RectF getShelterRectF() {
        float x = (int) getTranslateX();
        float y = (int) getTranslateY();
        float width = getDrawable().getIntrinsicWidth() * getScale();
        float height = getDrawable().getIntrinsicHeight() * getScale();
        if (shelterR == null) {
            shelterR = new RectF(x, y, width + x, height + y);
        } else {
            shelterR.set(x, y, width + x, height + y);
        }
        return shelterR;
    }

    public void setImageResource(int resourceId) {
        super.setImageResource(resourceId);
        mBitmap = BitmapFactory.decodeResource(getResources(), resourceId);
    }

    public void setImageBitmap(final Bitmap bitmap, WrhLoadingInter wrhLoadingInter) {
        this.WrhLoadingInter = wrhLoadingInter;
        new Thread(new Runnable() {
            @Override
            public void run() {
                mBitmap = BitmapUtils.compress2(bitmap, getContext());
                handler.sendEmptyMessage(1);
            }
        }).start();
    }

    private void measureSize(Bitmap bitmap) {
       /* int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        if (width > height) {
            this.mRadius = (height - ImageUtil.Dp2Px(getContext(), 62)) / 2;
        } else {
            this.mRadius = (width - ImageUtil.Dp2Px(getContext(), 62)) / 2;
        }

        if (this.mRadius <= 0)
            this.mRadius = ImageUtil.Dp2Px(getContext(), 62);

        int screenWidth = ContentUtil.getScreenWidth(getContext());
        if (this.mRadius * 2 >= screenWidth) {
            this.mRadius = screenWidth / 2 - ContentUtil.dip2px(getContext(), 30);
        }*/
        int screenWidth = ContentUtil.getScreenWidth(getContext());
        this.mRadius = screenWidth / 2 - ContentUtil.dip2px(getContext(), 30);
    }

    /**
     * 剪裁头像
     *
     * @return
     */
    public Bitmap clipBitmap() {
        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        int dw = getDrawable().getIntrinsicWidth();
        int dh = getDrawable().getIntrinsicHeight();
        float x = getTranslateX() - (getWidth() - dw) / 2;
        float y = getTranslateY() - (getHeight() - dh) / 2;
        mBitmap = zoomBitmap(mBitmap);
        Bitmap target = Bitmap.createBitmap(mRadius * 2, mRadius * 2, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(target);
        canvas.drawCircle(mRadius, mRadius, mRadius, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        canvas.drawBitmap(mBitmap, -(dw / 2 - mRadius) + x, -(dh / 2 - mRadius) + y, paint);
        Bitmap mbit = setScale(target);
        return mbit;
    }

    private Bitmap zoomBitmap(Bitmap bitmap) {
        int imageWidth = bitmap.getWidth();
        int imageHeight = bitmap.getHeight();
        Matrix matrix = new Matrix();
        matrix.postScale(getScale(), getScale());
        Bitmap imageBitmap = Bitmap.createBitmap(bitmap, 0, 0, imageWidth,
                imageHeight, matrix, true);
        return imageBitmap;
    }

    private Bitmap setScale(Bitmap bgimage) {
        // 获取这个图片的宽和高
        float width = bgimage.getWidth();
        float height = bgimage.getHeight();
        // 创建操作图片用的matrix对象
        Matrix matrix = new Matrix();
        if (width >= 200 && height >= 200) {
            // 计算宽高缩放率
            float scaleWidth = ((float) 200) / width;
            float scaleHeight = ((float) 200) / height;
            // 缩放图片动作
            matrix.postScale(scaleWidth, scaleHeight);
            Bitmap bitmap = Bitmap.createBitmap(bgimage, 0, 0, (int) width, (int) height, matrix, true);
            return bitmap;
        } else {
            return bgimage;
        }

    }


    private class AutoScaleRunnable implements Runnable {
        static final float BIGGER = 1.07f;
        static final float SMALLER = 0.93f;
        private float mTargetScale;
        private float tmpScale;

        /**
         * 缩放的中心
         */
        private float x;
        private float y;

        /**
         * 传入目标缩放值，根据目标值与当前值，判断应该放大还是缩小
         *
         * @param targetScale
         */
        public AutoScaleRunnable(float targetScale, float x, float y) {
            this.mTargetScale = targetScale;
            this.x = x;
            this.y = y;
            if (getScale() < mTargetScale) {
                tmpScale = BIGGER;
            } else {
                tmpScale = SMALLER;
            }

        }

        @Override
        public void run() {
            // 进行缩放
            mScaleMatrix.postScale(tmpScale, tmpScale, x, y);
            checkBorderAndCenterWhenScale();
            setImageMatrix(mScaleMatrix);

            final float currentScale = getScale();
            // 如果值在合法范围内，继续缩放
            if (((tmpScale > 1f) && (currentScale < mTargetScale))
                    || ((tmpScale < 1f) && (mTargetScale < currentScale))) {
                WrhImageView.this.postDelayed(this, 16);
            } else
            // 设置为目标的缩放比例
            {
                final float deltaScale = mTargetScale / currentScale;
                mScaleMatrix.postScale(deltaScale, deltaScale, x, y);
                checkBorderAndCenterWhenScale();
                setImageMatrix(mScaleMatrix);
                isAutoScale = false;
            }

        }
    }

    /**
     * 在缩放时，进行图片显示范围的控制
     */
    private void checkBorderAndCenterWhenScale() {

        RectF rect = getMatrixRectF();
        float deltaX = 0;
        float deltaY = 0;

        int width = getWidth();
        int height = getHeight();

        // 如果宽或高大于屏幕，则控制范围
        if (rect.width() >= width) {
            if (rect.left > 0) {
                deltaX = -rect.left;
            }
            if (rect.right < width) {
                deltaX = width - rect.right;
            }
        }
        if (rect.height() >= height) {
            if (rect.top > 0) {
                deltaY = -rect.top;
            }
            if (rect.bottom < height) {
                deltaY = height - rect.bottom;
            }
        }
        // 如果宽或高小于屏幕，则让其居中
        if (rect.width() < width) {
            deltaX = width * 0.5f - rect.right + 0.5f * rect.width();
        }
        if (rect.height() < height) {
            deltaY = height * 0.5f - rect.bottom + 0.5f * rect.height();
        }
        mScaleMatrix.postTranslate(deltaX, deltaY);
    }

    /**
     * 根据当前图片的Matrix获得图片的范围
     *
     * @return
     */
    private RectF getMatrixRectF() {
        Matrix matrix = mScaleMatrix;
        RectF rect = new RectF();
        Drawable d = getDrawable();
        if (null != d) {
            rect.set(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
            matrix.mapRect(rect);
        }
        return rect;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (getDrawable() == null) {
            return;
        }
        getShelterRectF();
        // 画入前景圆形蒙板层
        int sc = canvas.saveLayer(shelterR, null,  Canvas.ALL_SAVE_FLAG);
        canvas.drawRect(shelterR, mPaint);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(5);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, mRadius, paint);
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, mRadius, mPaint);
        if (circleR == null) {
            circleR = new RectF(getWidth() / 2 - mRadius, getHeight() / 2 - mRadius, getWidth() / 2 + mRadius, getHeight() / 2 + mRadius);
        }
        canvas.restoreToCount(sc);
        mPaint.setXfermode(null);
        mPaint.setColor(getResources().getColor(R.color.dg_color_translucent));
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getViewTreeObserver().removeGlobalOnLayoutListener(this);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mGestureDetector.onTouchEvent(event))
            return true;
        mScaleGestureDetector.onTouchEvent(event);
        float x = 0, y = 0;
        // 拿到触摸点的个数
        final int pointerCount = event.getPointerCount();
        // 得到多个触摸点的x与y均值
        for (int i = 0; i < pointerCount; i++) {
            x += event.getX(i);
            y += event.getY(i);
        }
        x = x / pointerCount;
        y = y / pointerCount;
        /**
         * 每当触摸点发生变化时，重置mLasX , mLastY
         */
        if (pointerCount != lastPointerCount) {
            mLastX = x;
            mLastY = y;
        }
        lastPointerCount = pointerCount;
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                float dx = x - mLastX;
                float dy = y - mLastY;

                if (shelterR.left + dx >= circleR.left || shelterR.right + dx <= circleR.right) {
                    dx = 0;
                }
                if (shelterR.top + dy > circleR.top || shelterR.bottom + dy <= circleR.bottom) {
                    dy = 0;
                }
                mScaleMatrix.postTranslate(dx, dy);
                setImageMatrix(mScaleMatrix);
                mLastX = x;
                mLastY = y;
                postInvalidate();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                lastPointerCount = 0;
                break;
        }
        return true;
    }

    @Override
    public void onGlobalLayout() {
        if (once) {
            Drawable d = getDrawable();
            if (d == null)
                return;
            int width = getWidth();
            int height = getHeight();
            int diameter = mRadius * 2;
            int dw = d.getIntrinsicWidth();
            int dh = d.getIntrinsicHeight();
            float scale = 1.0f;
            if (dw < diameter && dh > diameter) {
                scale = diameter * 1.0f / dw;
            }
            if (dh < diameter && dw > diameter) {
                scale = diameter * 1.0f / dh;
            }
            if (dh < diameter && dw < diameter) {
                scale = Math.max(diameter * 1.0f / dw, diameter * 1.0f / dh);
            }
            if (dw > diameter && dh > diameter) {
                scale = (float) diameter / (float) Math.min(dw, dh);
            }
            initScale = scale;
            // 图片移动至屏幕中心
            mScaleMatrix.postTranslate((width - dw) / 2, (height - dh) / 2);
            mScaleMatrix
                    .postScale(scale, scale, getWidth() / 2, getHeight() / 2);
            setImageMatrix(mScaleMatrix);
            once = false;
        }
    }

    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        float scale = getScale();
        float scaleFactor = detector.getScaleFactor();

        if (getDrawable() == null)
            return true;

        /**
         * 缩放的范围控制
         */
        if ((scale < SCALE_MAX && scaleFactor > 1.0f)
                || (scale > initScale && scaleFactor < 1.0f)) {
            /**
             * 最大值最小值判断
             */
            if (scaleFactor * scale < initScale) {
                scaleFactor = initScale / scale;
            }
            if (scaleFactor * scale > SCALE_MAX) {
                scaleFactor = SCALE_MAX / scale;
            }
            /**
             * 设置缩放比例
             */
            mScaleMatrix.postScale(scaleFactor, scaleFactor,
                    detector.getFocusX(), detector.getFocusX());
            checkBorderAndCenterWhenScale();
            setImageMatrix(mScaleMatrix);
        }
        return true;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {

    }

    public interface WrhLoadingInter {
        void onLoading();
    }

}
