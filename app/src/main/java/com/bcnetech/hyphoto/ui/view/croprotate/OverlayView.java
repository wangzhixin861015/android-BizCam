package com.bcnetech.hyphoto.ui.view.croprotate;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Region;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.IntDef;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.bcnetech.bcnetechlibrary.util.ContentUtil;
import com.bcnetech.hyphoto.R;
import com.yalantis.ucrop.callback.OverlayViewChangeListener;
import com.yalantis.ucrop.util.CubicEasing;
import com.yalantis.ucrop.util.RectUtils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;

/**
 * Created by Oleksii Shliama (https://github.com/shliama).
 * 绘制裁剪框
 * <p/>
 * This view is used for drawing the overlay on top of the image. It may have frame, crop guidelines and dimmed area.
 * This must have LAYER_TYPE_SOFTWARE to draw itself properly.
 */
public class OverlayView extends View {

    public static final int FREESTYLE_CROP_MODE_DISABLE = 0;//不可移动裁剪框
    public static final int FREESTYLE_CROP_MODE_ENABLE = 1;//可移动裁剪框
    public static final int FREESTYLE_CROP_MODE_ENABLE_WITH_PASS_THROUGH = 2;//按比例移动裁剪框

    public static final boolean DEFAULT_SHOW_CROP_FRAME = true;
    public static final boolean DEFAULT_SHOW_CROP_GRID = true;
    public static final boolean DEFAULT_CIRCLE_DIMMED_LAYER = false;
    public static final boolean DEFAULT_SHOW_TRIANGEL = true;
    public static final int DEFAULT_FREESTYLE_CROP_MODE = FREESTYLE_CROP_MODE_DISABLE;
    public static final int DEFAULT_CROP_GRID_ROW_COUNT = 5;
    public static final int DEFAULT_CROP_GRID_COLUMN_COUNT = 5;

    private final RectF mCropViewRect = new RectF();
    private final RectF mTempRect = new RectF();
    private RectF saveCropRect = new RectF();

    protected int mThisWidth, mThisHeight;
    protected float[] mCropGridCorners;
    protected float[] mCropGridCenter;

    private int mCropGridRowCount, mCropGridColumnCount;
    private float mTargetAspectRatio;
    private float[] mGridPoints = null;
    private boolean mShowCropFrame, mShowCropGrid, mShowTriangel;
    private boolean mCircleDimmedLayer;
    private int mDimmedColor;
    private Path mCircularPath = new Path();
    private Paint mDimmedStrokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mCropGridPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mCropFramePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mCropFrameCornersPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    @FreestyleMode
    private int mFreestyleCropMode = DEFAULT_FREESTYLE_CROP_MODE;
    private float mPreviousTouchX = -1, mPreviousTouchY = -1;
    private int mCurrentTouchCornerIndex = -1;//当前触摸裁剪框顶点
    private int mTouchPointThreshold;//触摸到顶点的可取误差值
    private int mCropRectMinSize;//最小裁剪框大小
    private int mCropRectCornerTouchAreaLineLength;
    private boolean isCanAutoScale = true;
    private Bitmap triangel;

    private OverlayViewChangeListener mCallback;

    private boolean mShouldSetupCropBounds;

    {
        mTouchPointThreshold = getResources().getDimensionPixelSize(com.bcnetech.bcnetechlibrary.R.dimen.ucrop_default_crop_rect_corner_touch_threshold);
        mCropRectMinSize = getResources().getDimensionPixelSize(com.bcnetech.bcnetechlibrary.R.dimen.ucrop_default_crop_rect_min_size);
        mCropRectCornerTouchAreaLineLength = getResources().getDimensionPixelSize(com.bcnetech.bcnetechlibrary.R.dimen.ucrop_default_crop_rect_corner_touch_area_line_length);
    }

    public OverlayView(Context context) {
        this(context, null);
    }

    public OverlayView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public OverlayView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public OverlayViewChangeListener getOverlayViewChangeListener() {
        return mCallback;
    }

    public void setOverlayViewChangeListener(OverlayViewChangeListener callback) {
        mCallback = callback;
    }

    @NonNull
    public RectF getCropViewRect() {
        return mCropViewRect;
    }

    @Deprecated
    /***
     * Please use the new method {@link #getFreestyleCropMode() getFreestyleCropMode} method as we have more than 1 freestyle crop mode.
     */
    public boolean isFreestyleCropEnabled() {
        return mFreestyleCropMode == FREESTYLE_CROP_MODE_ENABLE;
    }

    @Deprecated
    /***
     * Please use the new method {@link #setFreestyleCropMode setFreestyleCropMode} method as we have more than 1 freestyle crop mode.
     */
    public void setFreestyleCropEnabled(boolean freestyleCropEnabled) {
        mFreestyleCropMode = freestyleCropEnabled ? FREESTYLE_CROP_MODE_ENABLE : FREESTYLE_CROP_MODE_DISABLE;
    }

    @FreestyleMode
    public int getFreestyleCropMode() {
        return mFreestyleCropMode;
    }

    public void setFreestyleCropMode(@FreestyleMode int mFreestyleCropMode) {
        this.mFreestyleCropMode = mFreestyleCropMode;
        postInvalidate();
    }

    /**
     * Setter for {@link #mCircleDimmedLayer} variable.
     *
     * @param circleDimmedLayer - set it to true if you want dimmed layer to be an circle
     */
    public void setCircleDimmedLayer(boolean circleDimmedLayer) {
        mCircleDimmedLayer = circleDimmedLayer;
    }

    /**
     * Setter for crop grid rows count.
     * Resets {@link #mGridPoints} variable because it is not valid anymore.
     */
    public void setCropGridRowCount(@IntRange(from = 0) int cropGridRowCount) {
        mCropGridRowCount = cropGridRowCount;
        mGridPoints = null;
    }

    /**
     * Setter for crop grid columns count.
     * Resets {@link #mGridPoints} variable because it is not valid anymore.
     */
    public void setCropGridColumnCount(@IntRange(from = 0) int cropGridColumnCount) {
        mCropGridColumnCount = cropGridColumnCount;
        mGridPoints = null;
    }

    /**
     * Setter for {@link #mShowCropFrame} variable.
     *
     * @param showCropFrame - set to true if you want to see a crop frame rectangle on top of an image
     */
    public void setShowCropFrame(boolean showCropFrame) {
        mShowCropFrame = showCropFrame;
    }

    public void setShowTriangel(boolean ShowTriangel) {
        mShowTriangel = ShowTriangel;
    }

    /**
     * Setter for {@link #mShowCropGrid} variable.
     *
     * @param showCropGrid - set to true if you want to see a crop grid on top of an image
     */
    public void setShowCropGrid(boolean showCropGrid) {
        mShowCropGrid = showCropGrid;
    }

    /**
     * Setter for {@link #mDimmedColor} variable.
     *
     * @param dimmedColor - desired color of dimmed area around the crop bounds
     */
    public void setDimmedColor(@ColorInt int dimmedColor) {
        mDimmedColor = dimmedColor;
    }

    /**
     * Setter for crop frame stroke width
     */
    public void setCropFrameStrokeWidth(@IntRange(from = 0) int width) {
        mCropFramePaint.setStrokeWidth(width);
    }

    /**
     * Setter for crop grid stroke width
     */
    public void setCropGridStrokeWidth(@IntRange(from = 0) int width) {
        mCropGridPaint.setStrokeWidth(width);
    }

    /**
     * Setter for crop frame color
     */
    public void setCropFrameColor(@ColorInt int color) {
        mCropFramePaint.setColor(color);
    }

    /**
     * Setter for crop grid color
     */
    public void setCropGridColor(@ColorInt int color) {
        mCropGridPaint.setColor(color);
    }

    /**
     * This method sets aspect ratio for crop bounds.
     *
     * @param targetAspectRatio - aspect ratio for image crop (e.g. 1.77(7) for 16:9)
     */
    public void setTargetAspectRatio(final float targetAspectRatio, boolean isCanAutoScale) {
        mTargetAspectRatio = targetAspectRatio;
        if (mThisWidth > 0) {
            setupCropBounds(isCanAutoScale);
            postInvalidate();
        } else {
            mShouldSetupCropBounds = true;
        }
    }

    /**
     * This method setups crop bounds rectangles for given aspect ratio and view size.
     * {@link #mCropViewRect} is used to draw crop bounds - uses padding.
     */
    public void setupCropBounds(boolean isCanAutoScale) {
        int height = (int) (mThisWidth / mTargetAspectRatio);
        if (height > mThisHeight) {
            int width = (int) (mThisHeight * mTargetAspectRatio);
            int halfDiff = (mThisWidth - width) / 2;
            mCropViewRect.set(getPaddingLeft() + halfDiff, getPaddingTop(),
                    getPaddingLeft() + width + halfDiff, getPaddingTop() + mThisHeight);
        } else {
            int halfDiff = (mThisHeight - height) / 2;
            mCropViewRect.set(getPaddingLeft(), getPaddingTop() + halfDiff,
                    getPaddingLeft() + mThisWidth, getPaddingTop() + height + halfDiff);
        }

        if (mCallback != null) {
            mCallback.onCropRectUpdated(mCropViewRect, isCanAutoScale, null);
        }
        saveCropRect = mCropViewRect;
        updateGridPoints();
    }

    private void updateGridPoints() {
        mCropGridCorners = RectUtils.getCornersFromRect(mCropViewRect);
        mCropGridCenter = RectUtils.getCenterFromRect(mCropViewRect);

        mGridPoints = null;
        mCircularPath.reset();
        mCircularPath.addCircle(mCropViewRect.centerX(), mCropViewRect.centerY(),
                Math.min(mCropViewRect.width(), mCropViewRect.height()) / 2.f, Path.Direction.CW);
    }

    protected void init() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2 &&
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            setLayerType(LAYER_TYPE_SOFTWARE, null);
        }
        triangel = BitmapFactory.decodeResource(getResources(), R.drawable.popup_window_arrow_down);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            left = getPaddingLeft();
            top = getPaddingTop();
            right = getWidth() - getPaddingRight();
            bottom = getHeight() - getPaddingBottom();
            mThisWidth = right - left;
            mThisHeight = bottom - top;

            if (mShouldSetupCropBounds) {
                mShouldSetupCropBounds = false;
                setTargetAspectRatio(mTargetAspectRatio, true);
            }
        }
    }

    /**
     * Along with image there are dimmed layer, crop bounds and crop guidelines that must be drawn.
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawDimmedLayer(canvas);
        drawCropGrid(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mCropViewRect.isEmpty() || mFreestyleCropMode == FREESTYLE_CROP_MODE_DISABLE) {
            return false;
        }

        if (event.getPointerCount() > 1)
            return false;

        float x = event.getX();
        float y = event.getY();

        if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_DOWN) {
            mCurrentTouchCornerIndex = getCurrentTouchIndex(x, y);
            boolean shouldHandle = mCurrentTouchCornerIndex != -1;
            if (!shouldHandle) {
                mPreviousTouchX = -1;
                mPreviousTouchY = -1;
            } else if (mPreviousTouchX < 0) {
                mPreviousTouchX = x;
                mPreviousTouchY = y;
                mShowTriangel = false;
            }
            return shouldHandle;
        }

        if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_MOVE) {
            isCanAutoScale = true;
            if (event.getPointerCount() == 1 && mCurrentTouchCornerIndex != -1) {
                x = Math.min(Math.max(x, getPaddingLeft()), getWidth() - getPaddingRight());
                y = Math.min(Math.max(y, getPaddingTop()), getHeight() - getPaddingBottom());
                updateCropViewRect(x, y);

                mPreviousTouchX = x;
                mPreviousTouchY = y;
                return true;

            }
        }

        if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {
            mPreviousTouchX = -1;
            mPreviousTouchY = -1;
            mCurrentTouchCornerIndex = -1;

            if (mCallback != null) {
                PointF moveoffset = setCropCenter();
                mCallback.onCropRectUpdated(mCropViewRect, true, moveoffset);
                mCallback.onTouchMoveCrop(false, mCropViewRect, calculateMaxRect());
                //setCropAnim();
            }
            mShowTriangel = true;
        }

        return false;
    }

    /**
     * 根据当前宽高比计算最大裁剪框大小
     *
     * @return
     */
    private RectF calculateMaxRect() {
        RectF rectF = new RectF();
        int height = (int) (mThisWidth / mTargetAspectRatio);
        if (height > mThisHeight) {
            int width = (int) (mThisHeight * mTargetAspectRatio);
            int halfDiff = (mThisWidth - width) / 2;
            rectF.set(getPaddingLeft() + halfDiff, getPaddingTop(),
                    getPaddingLeft() + width + halfDiff, getPaddingTop() + mThisHeight);
        } else {
            int halfDiff = (mThisHeight - height) / 2;
            rectF.set(getPaddingLeft(), getPaddingTop() + halfDiff,
                    getPaddingLeft() + mThisWidth, getPaddingTop() + height + halfDiff);
        }
        return rectF;
    }



    /**
     * * The order of the corners is:
     * 0------->1
     * ^        |
     * |   4    |
     * |        v
     * 3<-------2
     */
    private void updateCropViewRect(float touchX, float touchY) {
        mTempRect.set(mCropViewRect);
        //随意拖动
        if (mFreestyleCropMode == FREESTYLE_CROP_MODE_ENABLE) {
            switch (mCurrentTouchCornerIndex) {
                // resize rectangle
                case 0:
                    mTempRect.set(touchX, touchY, mCropViewRect.right, mCropViewRect.bottom);
                    break;
                case 1:
                    mTempRect.set(mCropViewRect.left, touchY, touchX, mCropViewRect.bottom);
                    break;
                case 2:
                    mTempRect.set(mCropViewRect.left, mCropViewRect.top, touchX, touchY);
                    break;
                case 3:
                    mTempRect.set(touchX, mCropViewRect.top, mCropViewRect.right, touchY);
                    break;
                // move rectangle
                case 4:
                    mTempRect.offset(touchX - mPreviousTouchX, touchY - mPreviousTouchY);
                    if (mTempRect.left > getLeft() && mTempRect.top > getTop()
                            && mTempRect.right < getRight() && mTempRect.bottom < getBottom()) {
                        mCropViewRect.set(mTempRect);
                        updateGridPoints();
                        postInvalidate();
                    }
                    return;
            }
            //按比例放大缩小
        } else if (mFreestyleCropMode == FREESTYLE_CROP_MODE_ENABLE_WITH_PASS_THROUGH) {
            float y = saveCropRect.bottom - ((saveCropRect.right - touchX) / mTargetAspectRatio);//上
            float y1 = saveCropRect.bottom - ((touchX - saveCropRect.left) / mTargetAspectRatio);//下
            switch (mCurrentTouchCornerIndex) {
                // resize rectangle
                case 0://
                    if (y >= getPaddingTop())
                        mTempRect.set(touchX, y, mCropViewRect.right, mCropViewRect.bottom);
                    break;
                case 1:
                    if (y1 >= getPaddingTop())
                        mTempRect.set(mCropViewRect.left, y1, touchX, mCropViewRect.bottom);
                    break;
                case 2://
                    if (y <= getHeight() - getPaddingBottom())
                        mTempRect.set(mCropViewRect.left, mCropViewRect.top, touchX, y);
                    break;
                case 3:
                    if (y1 <= getHeight() - getPaddingBottom())
                        mTempRect.set(touchX, mCropViewRect.top, mCropViewRect.right, y1);
                    break;
                // move rectangle
                case 4:
                    mTempRect.offset(touchX - mPreviousTouchX, touchY - mPreviousTouchY);
                    if (mTempRect.left > getLeft() && mTempRect.top > getTop()
                            && mTempRect.right < getRight() && mTempRect.bottom < getBottom()) {
                        mCropViewRect.set(mTempRect);
                        updateGridPoints();
                        postInvalidate();
                    }
                    return;
            }
        }
        float minY = mCropRectMinSize / mTargetAspectRatio;
        boolean changeHeight = mTempRect.height() >= minY;
        boolean changeWidth = mTempRect.width() >= mCropRectMinSize;
        mCropViewRect.set(
                changeWidth ? mTempRect.left : mCropViewRect.left,
                changeHeight ? mTempRect.top : mCropViewRect.top,
                changeWidth ? mTempRect.right : mCropViewRect.right,
                changeHeight ? mTempRect.bottom : mCropViewRect.bottom);

        if (changeHeight || changeWidth) {
            if (mCallback != null) {
                mCallback.onTouchMoveCrop(true, null,null);
            }
            updateGridPoints();
            postInvalidate();
        }
    }

    /**
     * * The order of the corners in the float array is:
     * 0------->1
     * ^        |
     * |   4    |
     * |        v
     * 3<-------2
     *
     * @return - index of corner that is being dragged
     * 判断是否点击到四个角位置
     */
    private int getCurrentTouchIndex(float touchX, float touchY) {
        int closestPointIndex = -1;
        double closestPointDistance = mTouchPointThreshold;
        for (int i = 0; i < 8; i += 2) {
            double distanceToCorner = Math.sqrt(Math.pow(touchX - mCropGridCorners[i], 2)
                    + Math.pow(touchY - mCropGridCorners[i + 1], 2));
            if (distanceToCorner < closestPointDistance) {
                closestPointDistance = distanceToCorner;
                closestPointIndex = i / 2;
            }
        }

       /* if (mFreestyleCropMode == FREESTYLE_CROP_MODE_ENABLE && closestPointIndex < 0 && mCropViewRect.contains(touchX, touchY)) {
            return 4;
        }*/

//        for (int i = 0; i <= 8; i += 2) {
//
//            double distanceToCorner;
//            if (i < 8) { // corners
//                distanceToCorner = Math.sqrt(Math.pow(touchX - mCropGridCorners[i], 2)
//                        + Math.pow(touchY - mCropGridCorners[i + 1], 2));
//            } else { // center
//                distanceToCorner = Math.sqrt(Math.pow(touchX - mCropGridCenter[0], 2)
//                        + Math.pow(touchY - mCropGridCenter[1], 2));
//            }
//            if (distanceToCorner < closestPointDistance) {
//                closestPointDistance = distanceToCorner;
//                closestPointIndex = i / 2;
//            }
//        }
        return closestPointIndex;
    }

    /**
     * 绘制遮罩
     * This method draws dimmed area around the crop bounds.
     *
     * @param canvas - valid canvas object
     */
    protected void drawDimmedLayer(@NonNull Canvas canvas) {
        canvas.save();
        if (mCircleDimmedLayer) {
            canvas.clipPath(mCircularPath, Region.Op.DIFFERENCE);
        } else {
            canvas.clipRect(mCropViewRect, Region.Op.DIFFERENCE);
        }
        canvas.drawColor(mDimmedColor);//绘制前景色
        canvas.restore();

        if (mCircleDimmedLayer) { // Draw 1px stroke to fix antialias
            canvas.drawCircle(mCropViewRect.centerX(), mCropViewRect.centerY(),
                    Math.min(mCropViewRect.width(), mCropViewRect.height()) / 2.f, mDimmedStrokePaint);
        }
    }

    /**
     * 绘制裁剪框
     * This method draws crop bounds (empty rectangle)
     * and crop guidelines (vertical and horizontal lines inside the crop bounds) if needed.
     *
     * @param canvas - valid canvas object
     */
    protected void drawCropGrid(@NonNull Canvas canvas) {
        if (mShowCropGrid) {
            if (mGridPoints == null && !mCropViewRect.isEmpty()) {

                mGridPoints = new float[(mCropGridRowCount) * 4 + (mCropGridColumnCount) * 4];

                int index = 0;
                //横线
                for (int i = 0; i < mCropGridRowCount; i++) {
                    mGridPoints[index++] = mCropViewRect.left;
                    mGridPoints[index++] = (mCropViewRect.height() * (((float) i + 1.0f) / (float) (mCropGridRowCount + 1))) + mCropViewRect.top;
                    mGridPoints[index++] = mCropViewRect.right;
                    mGridPoints[index++] = (mCropViewRect.height() * (((float) i + 1.0f) / (float) (mCropGridRowCount + 1))) + mCropViewRect.top;
                }
                //纵线
                for (int i = 0; i < mCropGridColumnCount; i++) {
                    mGridPoints[index++] = (mCropViewRect.width() * (((float) i + 1.0f) / (float) (mCropGridColumnCount + 1))) + mCropViewRect.left;
                    mGridPoints[index++] = mCropViewRect.top;
                    mGridPoints[index++] = (mCropViewRect.width() * (((float) i + 1.0f) / (float) (mCropGridColumnCount + 1))) + mCropViewRect.left;
                    mGridPoints[index++] = mCropViewRect.bottom;
                }
            }

            if (mGridPoints != null) {
                canvas.drawLines(mGridPoints, mCropGridPaint);
            }
        }

        if (mShowCropFrame) {
            //绘制外框
            canvas.drawRect(mCropViewRect, mCropFramePaint);

        }
        float starX = (mCropViewRect.left) + (mCropViewRect.right - mCropViewRect.left) / 2 - triangel.getWidth() / 2;
        float starY = mCropViewRect.bottom + ContentUtil.dip2px(getContext(), 12);
        //下表指针
        if (mShowTriangel) {
            canvas.drawBitmap(triangel, starX, starY, mCropFramePaint);
        }

        //绘制角标
        if (mFreestyleCropMode != FREESTYLE_CROP_MODE_DISABLE) {
            canvas.save();

            mTempRect.set(mCropViewRect);
            mTempRect.inset(mCropRectCornerTouchAreaLineLength, -mCropRectCornerTouchAreaLineLength);
            canvas.clipRect(mTempRect, Region.Op.DIFFERENCE);

            mTempRect.set(mCropViewRect);
            mTempRect.inset(-mCropRectCornerTouchAreaLineLength, mCropRectCornerTouchAreaLineLength);
            canvas.clipRect(mTempRect, Region.Op.DIFFERENCE);

            canvas.drawRect(mCropViewRect, mCropFrameCornersPaint);

            canvas.restore();
        }
    }

    /**
     * This method extracts all needed values from the styled attributes.
     * Those are used to configure the view.
     */
    @SuppressWarnings("deprecation")
    protected void processStyledAttributes(@NonNull TypedArray a) {
        mCircleDimmedLayer = a.getBoolean(com.bcnetech.bcnetechlibrary.R.styleable.ucrop_UCropView_ucrop_circle_dimmed_layer, DEFAULT_CIRCLE_DIMMED_LAYER);
        mDimmedColor = a.getColor(com.bcnetech.bcnetechlibrary.R.styleable.ucrop_UCropView_ucrop_dimmed_color,
                getResources().getColor(com.bcnetech.bcnetechlibrary.R.color.ucrop_color_default_dimmed));
        mDimmedStrokePaint.setColor(mDimmedColor);
        mDimmedStrokePaint.setStyle(Paint.Style.STROKE);
        mDimmedStrokePaint.setStrokeWidth(1);

        initCropFrameStyle(a);
        mShowCropFrame = a.getBoolean(com.bcnetech.bcnetechlibrary.R.styleable.ucrop_UCropView_ucrop_show_frame, DEFAULT_SHOW_CROP_FRAME);

        initCropGridStyle(a);
        mShowCropGrid = a.getBoolean(com.bcnetech.bcnetechlibrary.R.styleable.ucrop_UCropView_ucrop_show_grid, DEFAULT_SHOW_CROP_GRID);
    }

    /**
     * This method setups Paint object for the crop bounds.
     */
    @SuppressWarnings("deprecation")
    private void initCropFrameStyle(@NonNull TypedArray a) {
        int cropFrameStrokeSize = a.getDimensionPixelSize(com.bcnetech.bcnetechlibrary.R.styleable.ucrop_UCropView_ucrop_frame_stroke_size,
                getResources().getDimensionPixelSize(com.bcnetech.bcnetechlibrary.R.dimen.ucrop_default_crop_frame_stoke_width));
        int cropFrameColor = a.getColor(com.bcnetech.bcnetechlibrary.R.styleable.ucrop_UCropView_ucrop_frame_color,
                getResources().getColor(com.bcnetech.bcnetechlibrary.R.color.ucrop_color_default_crop_frame));
        mCropFramePaint.setStrokeWidth(cropFrameStrokeSize);
        mCropFramePaint.setColor(cropFrameColor);
        mCropFramePaint.setStyle(Paint.Style.STROKE);

        mCropFrameCornersPaint.setStrokeWidth(cropFrameStrokeSize * 3);
        mCropFrameCornersPaint.setColor(cropFrameColor);
        mCropFrameCornersPaint.setStyle(Paint.Style.STROKE);
    }

    /**
     * This method setups Paint object for the crop guidelines.
     */
    @SuppressWarnings("deprecation")
    private void initCropGridStyle(@NonNull TypedArray a) {
        int cropGridStrokeSize = a.getDimensionPixelSize(com.bcnetech.bcnetechlibrary.R.styleable.ucrop_UCropView_ucrop_grid_stroke_size,
                getResources().getDimensionPixelSize(com.bcnetech.bcnetechlibrary.R.dimen.ucrop_default_crop_grid_stoke_width));
        int cropGridColor = a.getColor(com.bcnetech.bcnetechlibrary.R.styleable.ucrop_UCropView_ucrop_grid_color,
                getResources().getColor(com.bcnetech.bcnetechlibrary.R.color.ucrop_color_default_crop_grid));
        mCropGridPaint.setStrokeWidth(cropGridStrokeSize);
        mCropGridPaint.setColor(cropGridColor);

        mCropGridRowCount = a.getInt(com.bcnetech.bcnetechlibrary.R.styleable.ucrop_UCropView_ucrop_grid_row_count, DEFAULT_CROP_GRID_ROW_COUNT);
        mCropGridColumnCount = a.getInt(com.bcnetech.bcnetechlibrary.R.styleable.ucrop_UCropView_ucrop_grid_column_count, DEFAULT_CROP_GRID_COLUMN_COUNT);
    }


    @Retention(RetentionPolicy.SOURCE)
    @IntDef({FREESTYLE_CROP_MODE_DISABLE, FREESTYLE_CROP_MODE_ENABLE, FREESTYLE_CROP_MODE_ENABLE_WITH_PASS_THROUGH})
    public @interface FreestyleMode {
    }

    public PointF setCropCenter() {
       /* mTempRect.set(mCropViewRect);
        mTempRect.offset(getWidth() / 2 - mCropViewRect.centerX(), getHeight() / 2 - mCropViewRect.centerY());
        PointF moveoffset = new PointF(mCropViewRect.centerX(), mCropViewRect.centerY());
        if (mTempRect.left > getLeft() && mTempRect.top > getTop()
                && mTempRect.right < getRight() && mTempRect.bottom < getBottom()) {
            mCropViewRect.set(mTempRect);
            updateGridPoints();
            postInvalidate();
            return moveoffset;
        }*/
        return null;
    }

    private static class CropScaleRunnable implements Runnable {

        private final WeakReference<OverlayView> moverlayView;

        private final long mDurationMs, mStartTime;
        private final float mOldX, mOldY;
        private final float mCenterDiffX, mCenterDiffY;
        private final float mOldScale;
        private final float mDeltaScale;

        public CropScaleRunnable(OverlayView overlayView,
                                 long durationMs,
                                 float oldX, float oldY,
                                 float centerDiffX, float centerDiffY,
                                 float oldScale, float deltaScale
        ) {

            moverlayView = new WeakReference<>(overlayView);

            mDurationMs = durationMs;
            mStartTime = System.currentTimeMillis();
            mOldX = oldX;
            mOldY = oldY;
            mCenterDiffX = centerDiffX;
            mCenterDiffY = centerDiffY;
            mOldScale = oldScale;
            mDeltaScale = deltaScale;
        }

        @Override
        public void run() {
            OverlayView overlayView = moverlayView.get();
            if (overlayView == null) {
                return;
            }

            long now = System.currentTimeMillis();
            float currentMs = Math.min(mDurationMs, now - mStartTime);

            float newX = CubicEasing.easeOut(currentMs, 0, mCenterDiffX, mDurationMs);
            float newY = CubicEasing.easeOut(currentMs, 0, mCenterDiffY, mDurationMs);
            float newScale = CubicEasing.easeInOut(currentMs, 0, mDeltaScale, mDurationMs);
           /* overlayView.postTranslate(newX - (cropImageView.mCurrentImageCenter[0] - mOldX), newY - (cropImageView.mCurrentImageCenter[1] - mOldY));
            overlayView.zoomInImage(mOldScale + newScale, cropImageView.mCropRect.centerX(), cropImageView.mCropRect.centerY());
            overlayView.post(this);*/
        }
    }

    public void setCropAnim() {
        CropValueAnim cropValueAnim = new CropValueAnim(mCropViewRect, calculateMaxRect(), 3000);
        cropValueAnim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

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
        cropValueAnim.start();
    }

    private class CropValueAnim extends ValueAnimator implements ValueAnimator.AnimatorUpdateListener {
        private RectF mStart;
        private RectF mEnd;
        private float left, top, right, bottom;

        public CropValueAnim(RectF start, RectF end, long duration) {
            super();
            setFloatValues(0, 1f);
            setDuration(duration);
            addUpdateListener(this);
            mStart = start;
            mEnd = end;
        }

        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            float value = (float) animation.getAnimatedValue();
            left = mStart.left + (mEnd.left - mStart.left) * value;
            right = mStart.right + (mEnd.right - mStart.right) * value;
            top = mStart.top + (mEnd.top - mStart.top) * value;
            bottom = mStart.bottom + (mEnd.bottom - mStart.bottom) * value;
            mCropViewRect.set(left, top, right, bottom);
            mGridPoints = null;
            postInvalidate();
        }
    }
}
