package com.bcnetech.hyphoto.ui.view.croprotate;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.yalantis.ucrop.callback.BitmapCropCallback;
import com.yalantis.ucrop.callback.CropBoundsChangeListener;
import com.yalantis.ucrop.model.CropParameters;
import com.yalantis.ucrop.model.ImageState;
import com.yalantis.ucrop.task.BitmapCropTask;
import com.yalantis.ucrop.util.CubicEasing;
import com.yalantis.ucrop.util.RectUtils;

import java.lang.ref.WeakReference;
import java.util.Arrays;

/**
 * Created by Oleksii Shliama (https://github.com/shliama).
 * <p/>
 * This class adds crop feature, methods to draw crop guidelines, and keep image in correct state.
 * Also it extends parent class methods to add checks for scale; animating zoom in/out.
 */
public class CropImageView extends TransformImageView {

    public static final int DEFAULT_MAX_BITMAP_SIZE = 6000;
    public static final int DEFAULT_IMAGE_TO_CROP_BOUNDS_ANIM_DURATION = 500;
    public static final float DEFAULT_MAX_SCALE_MULTIPLIER = 10.0f;
    public static final float SOURCE_IMAGE_ASPECT_RATIO = 0f;
    public static final float DEFAULT_ASPECT_RATIO = SOURCE_IMAGE_ASPECT_RATIO;


    public final RectF mCropRect = new RectF();
    public RectF CropRect = new RectF();//展示的裁剪框

    private final Matrix mTempMatrix = new Matrix();

    private float mTargetAspectRatio;
    private float mMaxScaleMultiplier = DEFAULT_MAX_SCALE_MULTIPLIER;

    private CropBoundsChangeListener mCropBoundsChangeListener;

    private Runnable mWrapCropBoundsRunnable, mWrapCropBoundsRunnable2, mZoomImageToPositionRunnable = null;

    private float mMaxScale, mMinScale;
    private int mMaxResultImageSizeX = 0, mMaxResultImageSizeY = 0;
    private long mImageToWrapCropBoundsAnimDuration = DEFAULT_IMAGE_TO_CROP_BOUNDS_ANIM_DURATION;

    public CropImageView(Context context) {
        this(context, null);
    }

    public CropImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CropImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * Cancels all current animations and sets image to fill crop area (without animation).
     */
    public void cropAndSaveImage(@NonNull Bitmap.CompressFormat compressFormat, int compressQuality,
                                 @Nullable BitmapCropCallback cropCallback) {
        cancelAllAnimations();
        setImageToWrapCropBounds(false);

        final ImageState imageState = new ImageState(
                mCropRect, RectUtils.trapToRect(mCurrentImageCorners),
                getCurrentScale(), getCurrentAngle());

        final CropParameters cropParameters = new CropParameters(
                mMaxResultImageSizeX, mMaxResultImageSizeY,
                compressFormat, compressQuality,
                getImageInputPath(), getImageOutputPath(), getExifInfo());

        new BitmapCropTask(getViewBitmap(), imageState, cropParameters, cropCallback).execute();
    }

    /**
     * @return - maximum scale value for current image and crop ratio
     */
    public float getMaxScale() {
        return mMaxScale;
    }

    /**
     * @return - minimum scale value for current image and crop ratio
     */
    public float getMinScale() {
        return mMinScale;
    }

    /**
     * @return - aspect ratio for crop bounds
     */
    public float getTargetAspectRatio() {
        return mTargetAspectRatio;
    }

    /**
     * Updates current crop rectangle with given. Also recalculates image properties and position
     * to fit new crop rectangle.
     *
     * @param cropRect - new crop rectangle
     */
    public void setCropRect(RectF cropRect, boolean isCanAutoScale, PointF moveoffset) {
        CropRect = cropRect;
        // mTargetAspectRatio = cropRect.width() / cropRect.height();
        mCropRect.set(cropRect.left - getPaddingLeft(), cropRect.top - getPaddingTop(),
                cropRect.right - getPaddingRight(), cropRect.bottom - getPaddingBottom());
     /*   cw = mCropRect.width();
        ch = mCropRect.height();*/
       /* if (moveoffset!=null) {
            postTranslate(moveoffset.x, moveoffset.y);
        }*/
        calculateImageScaleBounds();
        if (isCanAutoScale)
            setImageToWrapCropBounds();
    }

    /**
     * This method sets aspect ratio for crop bounds.
     * 设置裁剪边框
     * If {@link #SOURCE_IMAGE_ASPECT_RATIO} value is passed - aspect ratio is calculated
     * based on current image width and height.
     *
     * @param targetAspectRatio - aspect ratio for image crop (e.g. 1.77(7) for 16:9)
     */
    public void setTargetAspectRatio(float targetAspectRatio, boolean isCanAutoScale) {
        final Drawable drawable = getDrawable();
        if (drawable == null) {
            mTargetAspectRatio = targetAspectRatio;
            return;
        }

        if (targetAspectRatio == SOURCE_IMAGE_ASPECT_RATIO) {
            mTargetAspectRatio = drawable.getIntrinsicWidth() / (float) drawable.getIntrinsicHeight();
        } else {
            mTargetAspectRatio = targetAspectRatio;
        }

        if (mCropBoundsChangeListener != null) {
            mCropBoundsChangeListener.onCropAspectRatioChanged(mTargetAspectRatio, isCanAutoScale);
        }
    }

    @Nullable
    public CropBoundsChangeListener getCropBoundsChangeListener() {
        return mCropBoundsChangeListener;
    }

    public void setCropBoundsChangeListener(@Nullable CropBoundsChangeListener cropBoundsChangeListener) {
        mCropBoundsChangeListener = cropBoundsChangeListener;
    }

    /**
     * This method sets maximum width for resulting cropped image
     *
     * @param maxResultImageSizeX - size in pixels
     */
    public void setMaxResultImageSizeX(@IntRange(from = 10) int maxResultImageSizeX) {
        mMaxResultImageSizeX = maxResultImageSizeX;
    }

    /**
     * This method sets maximum width for resulting cropped image
     *
     * @param maxResultImageSizeY - size in pixels
     */
    public void setMaxResultImageSizeY(@IntRange(from = 10) int maxResultImageSizeY) {
        mMaxResultImageSizeY = maxResultImageSizeY;
    }

    /**
     * This method sets animation duration for image to wrap the crop bounds
     *
     * @param imageToWrapCropBoundsAnimDuration - duration in milliseconds
     */
    public void setImageToWrapCropBoundsAnimDuration(@IntRange(from = 100) long imageToWrapCropBoundsAnimDuration) {
        if (imageToWrapCropBoundsAnimDuration > 0) {
            mImageToWrapCropBoundsAnimDuration = imageToWrapCropBoundsAnimDuration;
        } else {
            throw new IllegalArgumentException("Animation duration cannot be negative value.");
        }
    }

    /**
     * This method sets multiplier that is used to calculate max image scale from min image scale.
     *
     * @param maxScaleMultiplier - (minScale * maxScaleMultiplier) = maxScale
     */
    public void setMaxScaleMultiplier(float maxScaleMultiplier) {
        mMaxScaleMultiplier = maxScaleMultiplier;
    }

    /**
     * This method scales image down for given value related to image center.
     */
    public void zoomOutImage(float deltaScale) {
        zoomOutImage(deltaScale, mCropRect.centerX(), mCropRect.centerY());
    }

    /**
     * This method scales image down for given value related given coords (x, y).
     */
    public void zoomOutImage(float scale, float centerX, float centerY) {
        if (scale >= getMinScale()) {
            postScale(scale / getCurrentScale(), centerX, centerY);
        }
    }

    /**
     * 将当前图片设置为符合裁剪框大小
     */
    public void setScaleMin() {
        // postScale(mMinScale/getCurrentScale(),mCropRect.centerX(),mCropRect.centerY());
        resetImageToWrapCropBounds(false);
    }

    /**
     * This method scales image up for given value related to image center.
     */
    public void zoomInImage(float deltaScale) {
        zoomInImage(deltaScale, mCropRect.centerX(), mCropRect.centerY());
    }

    /**
     * This method scales image up for given value related to given coords (x, y).
     */
    public void zoomInImage(float scale, float centerX, float centerY) {
        if (scale <= getMaxScale()) {
            postScale(scale / getCurrentScale(), centerX, centerY);
        }
    }

    /**
     * This method changes image scale for given value related to point (px, py) but only if
     * resulting scale is in min/max bounds.
     *
     * @param deltaScale - scale value
     * @param px         - scale center X
     * @param py         - scale center Y
     */
    public void postScale(float deltaScale, float px, float py) {
        if (deltaScale > 1 && getCurrentScale() * deltaScale <= getMaxScale()) {
            super.postScale(deltaScale, px, py);
        } else if (deltaScale < 1 && getCurrentScale() * deltaScale >= getMinScale()) {
            super.postScale(deltaScale, px, py);
        }
    }

    public float getCurrentRatio() {
        return this.mTargetAspectRatio;
    }

    /**
     * This method rotates image for given angle related to the image center.
     * 旋转图片
     *
     * @param deltaAngle - angle to rotate
     */
    public void postRotate(float deltaAngle) {
        postRotate(deltaAngle, mCropRect.centerX(), mCropRect.centerY());
    }

    public void rotateScale(float deltaAngle) {
        rotateSacle(deltaAngle, mCropRect.centerX(), mCropRect.centerY());
    }

    public void setMirror_L() {
        setMirror(true);
    }

    public void setMirror_T() {
        setMirror(false);
    }


    /**
     * This method cancels all current Runnable objects that represent animations.
     */
    public void cancelAllAnimations() {
        removeCallbacks(mWrapCropBoundsRunnable);
        removeCallbacks(mZoomImageToPositionRunnable);
        removeCallbacks(mWrapCropBoundsRunnable2);
    }

    public void setImageToWrapCropBounds() {
        setImageToWrapCropBounds(true);
    }

    /**
     * 裁剪框超出图片时修正
     * If image doesn't fill the crop bounds it must be translated and scaled properly to fill those.
     * <p/>
     * Therefore this method calculates delta X, Y and scale values and passes them to the
     * {@link WrapCropBoundsRunnable} which animates image.
     * Scale value must be calculated only if image won't fill the crop bounds after it's translated to the
     * crop bounds rectangle center. Using temporary variables this method checks this case.
     */
    public void setImageToWrapCropBounds(boolean animate) {
        if (mBitmapLaidOut && !isImageWrapCropBounds()) {
            float currentX = mCurrentImageCenter[0];
            float currentY = mCurrentImageCenter[1];
            float currentScale = getCurrentScale();
            float deltaX = mCropRect.centerX() - currentX;
            float deltaY = mCropRect.centerY() - currentY;
            float deltaScale = 0;

            mTempMatrix.reset();
            mTempMatrix.setTranslate(deltaX, deltaY);

            final float[] tempCurrentImageCorners = Arrays.copyOf(mCurrentImageCorners, mCurrentImageCorners.length);
            mTempMatrix.mapPoints(tempCurrentImageCorners);

            boolean willImageWrapCropBoundsAfterTranslate = isImageWrapCropBounds(tempCurrentImageCorners);
            if (willImageWrapCropBoundsAfterTranslate) {
                final float[] imageIndents = calculateImageIndents();
                deltaX = -(imageIndents[0] + imageIndents[2]);
                deltaY = -(imageIndents[1] + imageIndents[3]);
            } else {
                RectF tempCropRect = new RectF(mCropRect);
                mTempMatrix.reset();
                mTempMatrix.setRotate(getCurrentAngle());
                mTempMatrix.mapRect(tempCropRect);

                final float[] currentImageSides = RectUtils.getRectSidesFromCorners(mCurrentImageCorners);

                deltaScale = Math.max(tempCropRect.width() / currentImageSides[0],
                        tempCropRect.height() / currentImageSides[1]);
                deltaScale = deltaScale * currentScale - currentScale;
            }
            if (animate) {
                post(mWrapCropBoundsRunnable = new WrapCropBoundsRunnable(
                        CropImageView.this, mImageToWrapCropBoundsAnimDuration, currentX, currentY, deltaX, deltaY,
                        currentScale, deltaScale, willImageWrapCropBoundsAfterTranslate));
            } else {
                postTranslate(deltaX, deltaY);
                if (!willImageWrapCropBoundsAfterTranslate) {
                    zoomInImage(currentScale + deltaScale, mCropRect.centerX(), mCropRect.centerY());
                }
            }
        }
    }

    /**
     * 缩放至与裁剪框同尺寸
     *
     * @param animate
     */
    public void resetImageToWrapCropBounds(boolean animate) {
        //裁剪框宽高
        float currentX = mCurrentImageCenter[0];
        float currentY = mCurrentImageCenter[1];
        float currentScale = getCurrentScale();

        float deltaX = mCropRect.centerX() - currentX;
        float deltaY = mCropRect.centerY() - currentY;
        float deltaScale = 0;

        mTempMatrix.reset();
        mTempMatrix.setTranslate(deltaX, deltaY);

        final float[] tempCurrentImageCorners = Arrays.copyOf(mCurrentImageCorners, mCurrentImageCorners.length);
        mTempMatrix.mapPoints(tempCurrentImageCorners);
        RectF tempCropRect = new RectF(mCropRect);
        mTempMatrix.reset();
        mTempMatrix.setRotate(getCurrentAngle());
        mTempMatrix.mapRect(tempCropRect);

        final float[] currentImageSides = RectUtils.getRectSidesFromCorners(mCurrentImageCorners);

        deltaScale = Math.max(tempCropRect.width() / currentImageSides[0],
                tempCropRect.height() / currentImageSides[1]);
        deltaScale = deltaScale * currentScale - currentScale;
        if (animate) {
            post(mWrapCropBoundsRunnable2 = new WrapCropBoundsRunnable2(
                    CropImageView.this, mImageToWrapCropBoundsAnimDuration, currentX, currentY, deltaX, deltaY,
                    currentScale, deltaScale));
        } else {
            postTranslate(deltaX, deltaY);
            zoomInImage(currentScale + deltaScale, mCropRect.centerX(), mCropRect.centerY());
        }
    }

    public float calculateScaleSize(float rotateAngle) {
        float currentX = mCurrentImageCenter[0];
        float currentY = mCurrentImageCenter[1];
        float currentScale = getCurrentScale();

        float deltaX = mCropRect.centerX() - currentX;
        float deltaY = mCropRect.centerY() - currentY;
        float deltaScale = 0;

        mTempMatrix.reset();
        mTempMatrix.setTranslate(deltaX, deltaY);

        final float[] tempCurrentImageCorners = Arrays.copyOf(mCurrentImageCorners, mCurrentImageCorners.length);
        mTempMatrix.mapPoints(tempCurrentImageCorners);
        rotateAngle += rotatePlus;
        //判断移动后裁剪框四个顶点是否在图像内部
        boolean iswrap = isImageWrapCropContain(tempCurrentImageCorners);
        // boolean willImageWrapCropBoundsAfterTranslate = isImageWrapCropBounds(tempCurrentImageCorners);
        // willImageWrapCropBoundsAfterTranslate = false;
        if (!iswrap) {
            final float[] imageIndents = calculateImageIndents();
            deltaX = -(imageIndents[0] + imageIndents[2]);
            deltaY = -(imageIndents[1] + imageIndents[3]);
        } else {
            RectF tempCropRect = new RectF(mCropRect);
            mTempMatrix.reset();
            mTempMatrix.setRotate(rotateAngle);
            mTempMatrix.mapRect(tempCropRect);

            final float[] currentImageSides = RectUtils.getRectSidesFromCorners(mCurrentImageCorners);

            deltaScale = Math.max(tempCropRect.width() / currentImageSides[0],
                    tempCropRect.height() / currentImageSides[1]);
            deltaScale = deltaScale * currentScale - currentScale;
        }
        return deltaScale;
    }

    /**
     * 旋转放大
     *
     * @param rotateAngle - rotation angle
     * @param px          - rotation center X
     * @param py          - rotation center Y
     */
    public void rotateSacle(float rotateAngle, float px, float py) {
        //此角度为每次应转角度
        float scale = 1;//相对于原始角度需要放大的值
        if (rotateAngle != 0) {
            this.rotateAngel = rotateAngle;
            float oldScale = getCurrentScale();
            float oldRotate = getCurrentAngle();
            if (rotateAngle >= -45 && rotateAngle <= 45) {
                scale = calculateScaleSize(rotateAngle);
                float deltaRotate = rotateAngle + rotatePlus - oldRotate;
                mCurrentImageMatrix.postRotate(deltaRotate, px, py);
                mCurrentImageMatrix.postScale((oldScale + scale) / getCurrentScale(), (oldScale + scale) / getCurrentScale(), px, py);
                setImageMatrix(mCurrentImageMatrix);
                if (mTransformImageListener != null) {
                    mTransformImageListener.onRotate(getMatrixAngle(mCurrentImageMatrix));
                    mTransformImageListener.onScale(getMatrixScale(mCurrentImageMatrix));
                }
            }
        }
    }


    /**
     * First, un-rotate image and crop rectangles (make image rectangle axis-aligned).
     * Second, calculate deltas between those rectangles sides.
     * Third, depending on delta (its sign) put them or zero inside an array.
     * Fourth, using Matrix, rotate back those points (indents).
     *
     * @return - the float array of image indents (4 floats) - in this order [left, top, right, bottom]
     */
    private float[] calculateImageIndents() {
        mTempMatrix.reset();
        mTempMatrix.setRotate(-getCurrentAngle());

        float[] unrotatedImageCorners = Arrays.copyOf(mCurrentImageCorners, mCurrentImageCorners.length);
        float[] unrotatedCropBoundsCorners = RectUtils.getCornersFromRect(mCropRect);

        mTempMatrix.mapPoints(unrotatedImageCorners);
        mTempMatrix.mapPoints(unrotatedCropBoundsCorners);

        RectF unrotatedImageRect = RectUtils.trapToRect(unrotatedImageCorners);
        RectF unrotatedCropRect = RectUtils.trapToRect(unrotatedCropBoundsCorners);

        float deltaLeft = unrotatedImageRect.left - unrotatedCropRect.left;
        float deltaTop = unrotatedImageRect.top - unrotatedCropRect.top;
        float deltaRight = unrotatedImageRect.right - unrotatedCropRect.right;
        float deltaBottom = unrotatedImageRect.bottom - unrotatedCropRect.bottom;

        float indents[] = new float[4];
        indents[0] = (deltaLeft > 0) ? deltaLeft : 0;
        indents[1] = (deltaTop > 0) ? deltaTop : 0;
        indents[2] = (deltaRight < 0) ? deltaRight : 0;
        indents[3] = (deltaBottom < 0) ? deltaBottom : 0;

        mTempMatrix.reset();
        mTempMatrix.setRotate(getCurrentAngle());
        mTempMatrix.mapPoints(indents);

        return indents;
    }

    /**
     * When image is laid out it must be centered properly to fit current crop bounds.
     */
    @Override
    protected void onImageLaidOut() {
        super.onImageLaidOut();
        final Drawable drawable = getDrawable();
        if (drawable == null) {
            return;
        }
        float drawableWidth = drawable.getIntrinsicWidth();
        float drawableHeight = drawable.getIntrinsicHeight();

        if (mTargetAspectRatio == SOURCE_IMAGE_ASPECT_RATIO) {
            mTargetAspectRatio = drawableWidth / drawableHeight;
        }
        int height = (int) (mThisWidth / mTargetAspectRatio);
        if (height > mThisHeight) {
            int width = (int) (mThisHeight * mTargetAspectRatio);
            int halfDiff = (mThisWidth - width) / 2;
            mCropRect.set(halfDiff, 0, width + halfDiff, mThisHeight);
        } else {
            int halfDiff = (mThisHeight - height) / 2;
            mCropRect.set(0, halfDiff, mThisWidth, height + halfDiff);
        }

        calculateImageScaleBounds(drawableWidth, drawableHeight);
        setupInitialImagePosition(drawableWidth, drawableHeight);

        if (mCropBoundsChangeListener != null) {
            mCropBoundsChangeListener.onCropAspectRatioChanged(mTargetAspectRatio, true);
        }
        if (mTransformImageListener != null) {
            mTransformImageListener.onScale(getCurrentScale());
            mTransformImageListener.onRotate(getCurrentAngle());
        }
    }

    /**
     * This method checks whether current image fills the crop bounds.
     */
    protected boolean isImageWrapCropBounds() {
        return isImageWrapCropBounds(mCurrentImageCorners);
    }


    /**
     * This methods checks whether a rectangle that is represented as 4 corner points (8 floats)
     * fills the crop bounds rectangle.
     * 判断是否裁剪框四个顶点均在图像内
     *
     * @param imageCorners - corners of a rectangle
     * @return - true if it wraps crop bounds, false - otherwise
     */
    protected boolean isImageWrapCropBounds(float[] imageCorners) {
        mTempMatrix.reset();
        mTempMatrix.setRotate(-getCurrentAngle());

        float[] unrotatedImageCorners = Arrays.copyOf(imageCorners, imageCorners.length);
        mTempMatrix.mapPoints(unrotatedImageCorners);

        float[] unrotatedCropBoundsCorners = RectUtils.getCornersFromRect(mCropRect);
        mTempMatrix.mapPoints(unrotatedCropBoundsCorners);

        return RectUtils.trapToRect(unrotatedImageCorners).contains(RectUtils.trapToRect(unrotatedCropBoundsCorners));
    }

    /**
     * 当全部包含在图像内部时，旋转以图像内部为圆心
     * 当有对角两个点在边上时，旋转并放大
     *
     * @param imageCorners
     * @return
     */
    protected boolean isImageWrapCropContain(float[] imageCorners) {
        boolean isLeftOn = false, isRightOn = false, isTopOn = false, isBottomOn = false;
        mTempMatrix.reset();
        mTempMatrix.setRotate(-getCurrentAngle());

        float[] unrotatedImageCorners = Arrays.copyOf(imageCorners, imageCorners.length);
        mTempMatrix.mapPoints(unrotatedImageCorners);

        float[] unrotatedCropBoundsCorners = RectUtils.getCornersFromRect(mCropRect);
        mTempMatrix.mapPoints(unrotatedCropBoundsCorners);
        RectF unrotatedRect = RectUtils.trapToRect(unrotatedImageCorners);//图片rect
        RectF unrotatedCropBoundsRect = RectUtils.trapToRect(unrotatedCropBoundsCorners);//裁剪框rect
        if (unrotatedRect.contains(unrotatedCropBoundsRect)) {
            //在边界内
            if (unrotatedRect.left == unrotatedCropBoundsRect.left) {
                isLeftOn = true;
            }
            if (unrotatedRect.right == unrotatedCropBoundsRect.right) {
                isRightOn = true;
            }
            if (unrotatedRect.top == unrotatedCropBoundsRect.top) {
                isTopOn = true;
            }
            if (unrotatedRect.bottom == unrotatedCropBoundsRect.bottom) {
                isBottomOn = true;
            }
            if (!isLeftOn && !isRightOn && !isTopOn && !isBottomOn) {
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    /**
     * This method changes image scale (animating zoom for given duration), related to given center (x,y).
     *
     * @param scale      - target scale
     * @param centerX    - scale center X
     * @param centerY    - scale center Y
     * @param durationMs - zoom animation duration
     */
    public void zoomImageToPosition(float scale, float centerX, float centerY, long durationMs) {
        if (scale > getMaxScale()) {
            scale = getMaxScale();
        }

        final float oldScale = getCurrentScale();
        final float deltaScale = scale - oldScale;

        post(mZoomImageToPositionRunnable = new ZoomImageToPosition(CropImageView.this,
                durationMs, oldScale, deltaScale, centerX, centerY));
    }

    public void zoomAndTranslateImage(RectF smallRect, RectF maxRect) {
        //判断移动距离
        float currentX = smallRect.centerX();
        float currentY = smallRect.centerY();
        float deltaX =maxRect.centerX() - currentX;
        float deltaY = maxRect.centerY() - currentY;
        //判断缩放大小
        float currentScale = getCurrentScale();

        final float[] currentImageSides = RectUtils.getRectSidesFromCorners(mCurrentImageCorners);

       /* deltaScale = Math.max(tempCropRect.width() / currentImageSides[0],
                tempCropRect.height() / currentImageSides[1]);*/
        float deltaScale = Math.max(maxRect.width() / smallRect.width(), maxRect.height() / smallRect.height());
        // deltaScale = deltaScale * currentScale - currentScale;
        postTranslate(deltaX/deltaScale, deltaY/deltaScale);
        postScale(deltaScale, smallRect.centerX(), smallRect.centerY());
       /* post(mWrapCropBoundsRunnable = new WrapCropBoundsRunnable(
                CropImageView.this, mImageToWrapCropBoundsAnimDuration, currentX, currentY, deltaX, deltaY,
                currentScale, deltaScale, false));*/
    }

    private void calculateImageScaleBounds() {
        final Drawable drawable = getDrawable();
        if (drawable == null) {
            return;
        }
        calculateImageScaleBounds(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
    }

    /**
     * 计算最小和最大放大范围
     * This method calculates image minimum and maximum scale values for current {@link #mCropRect}.
     *
     * @param drawableWidth  - image width
     * @param drawableHeight - image height
     */
    private void calculateImageScaleBounds(float drawableWidth, float drawableHeight) {
        float widthScale = Math.min(mCropRect.width() / drawableWidth, mCropRect.width() / drawableHeight);
        float heightScale = Math.min(mCropRect.height() / drawableHeight, mCropRect.height() / drawableWidth);

        mMinScale = Math.min(widthScale, heightScale);
        mMaxScale = mMinScale * mMaxScaleMultiplier;
    }

    /**
     * This method calculates initial image position so it is positioned properly.
     * Then it sets those values to the current image matrix.
     *
     * @param drawableWidth  - image width
     * @param drawableHeight - image height
     */
    private void setupInitialImagePosition(float drawableWidth, float drawableHeight) {
        float cropRectWidth = mCropRect.width();
        float cropRectHeight = mCropRect.height();

        float widthScale = mCropRect.width() / drawableWidth;
        float heightScale = mCropRect.height() / drawableHeight;

        float initialMinScale = Math.max(widthScale, heightScale);
        if (this.initialMinScale == 1.0f) {
            this.initialMinScale = initialMinScale;
        }

        float tw = (cropRectWidth - drawableWidth * initialMinScale) / 2.0f + mCropRect.left;
        float th = (cropRectHeight - drawableHeight * initialMinScale) / 2.0f + mCropRect.top;
        mCurrentImageMatrix.reset();
        mCurrentImageMatrix.postScale(initialMinScale, initialMinScale);
        mCurrentImageMatrix.postTranslate(tw, th);
        baseMatrix = mCurrentImageMatrix;
        setImageMatrix(mCurrentImageMatrix);
    }

    public void resetMatrix() {
        mCurrentImageMatrix.set(baseMatrix);
    }

    /**
     * This method extracts all needed values from the styled attributes.
     * Those are used to configure the view.
     */
    @SuppressWarnings("deprecation")
    protected void processStyledAttributes(@NonNull TypedArray a) {
        float targetAspectRatioX = Math.abs(a.getFloat(com.bcnetech.bcnetechlibrary.R.styleable.ucrop_UCropView_ucrop_aspect_ratio_x, DEFAULT_ASPECT_RATIO));
        float targetAspectRatioY = Math.abs(a.getFloat(com.bcnetech.bcnetechlibrary.R.styleable.ucrop_UCropView_ucrop_aspect_ratio_y, DEFAULT_ASPECT_RATIO));

        if (targetAspectRatioX == SOURCE_IMAGE_ASPECT_RATIO || targetAspectRatioY == SOURCE_IMAGE_ASPECT_RATIO) {
            mTargetAspectRatio = SOURCE_IMAGE_ASPECT_RATIO;
        } else {
            mTargetAspectRatio = targetAspectRatioX / targetAspectRatioY;
        }
    }

    /**
     * This Runnable is used to animate an image so it fills the crop bounds entirely.
     * Given values are interpolated during the animation time.
     * Runnable can be terminated either vie {@link #cancelAllAnimations()} method
     * or when certain conditions inside {@link WrapCropBoundsRunnable#run()} method are triggered.
     */
    private static class WrapCropBoundsRunnable implements Runnable {

        private final WeakReference<CropImageView> mCropImageView;

        private final long mDurationMs, mStartTime;
        private final float mOldX, mOldY;
        private final float mCenterDiffX, mCenterDiffY;
        private final float mOldScale;
        private final float mDeltaScale;
        private final boolean mWillBeImageInBoundsAfterTranslate;

        public WrapCropBoundsRunnable(CropImageView cropImageView,
                                      long durationMs,
                                      float oldX, float oldY,
                                      float centerDiffX, float centerDiffY,
                                      float oldScale, float deltaScale,
                                      boolean willBeImageInBoundsAfterTranslate
        ) {

            mCropImageView = new WeakReference<>(cropImageView);

            mDurationMs = durationMs;
            mStartTime = System.currentTimeMillis();
            mOldX = oldX;//当前中点
            mOldY = oldY;
            mCenterDiffX = centerDiffX;//缩放中点
            mCenterDiffY = centerDiffY;
            mOldScale = oldScale;
            mDeltaScale = deltaScale;
            mWillBeImageInBoundsAfterTranslate = willBeImageInBoundsAfterTranslate;
        }

        @Override
        public void run() {
            CropImageView cropImageView = mCropImageView.get();
            if (cropImageView == null) {
                return;
            }

            long now = System.currentTimeMillis();
            float currentMs = Math.min(mDurationMs, now - mStartTime);

            float newX = CubicEasing.easeOut(currentMs, 0, mCenterDiffX, mDurationMs);
            float newY = CubicEasing.easeOut(currentMs, 0, mCenterDiffY, mDurationMs);
            float newScale = CubicEasing.easeInOut(currentMs, 0, mDeltaScale, mDurationMs);
            if (currentMs < mDurationMs) {
                cropImageView.postTranslate(newX - (cropImageView.mCurrentImageCenter[0] - mOldX), newY - (cropImageView.mCurrentImageCenter[1] - mOldY));
                if (!mWillBeImageInBoundsAfterTranslate) {
                    cropImageView.zoomInImage(mOldScale + newScale, cropImageView.mCropRect.centerX(), cropImageView.mCropRect.centerY());
                }
                if (!cropImageView.isImageWrapCropBounds()) {
                    cropImageView.post(this);
                }
            }
        }
    }

    private static class WrapCropBoundsRunnable2 implements Runnable {

        private final WeakReference<CropImageView> mCropImageView;

        private final long mDurationMs, mStartTime;
        private final float mOldX, mOldY;
        private final float mCenterDiffX, mCenterDiffY;
        private final float mOldScale;
        private final float mDeltaScale;

        public WrapCropBoundsRunnable2(CropImageView cropImageView,
                                       long durationMs,
                                       float oldX, float oldY,
                                       float centerDiffX, float centerDiffY,
                                       float oldScale, float deltaScale
        ) {

            mCropImageView = new WeakReference<>(cropImageView);

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
            CropImageView cropImageView = mCropImageView.get();
            if (cropImageView == null) {
                return;
            }

            long now = System.currentTimeMillis();
            float currentMs = Math.min(mDurationMs, now - mStartTime);

            float newX = CubicEasing.easeOut(currentMs, 0, mCenterDiffX, mDurationMs);
            float newY = CubicEasing.easeOut(currentMs, 0, mCenterDiffY, mDurationMs);
            float newScale = CubicEasing.easeInOut(currentMs, 0, mDeltaScale, mDurationMs);
            cropImageView.postTranslate(newX - (cropImageView.mCurrentImageCenter[0] - mOldX), newY - (cropImageView.mCurrentImageCenter[1] - mOldY));
            cropImageView.zoomInImage(mOldScale + newScale, cropImageView.mCropRect.centerX(), cropImageView.mCropRect.centerY());
            cropImageView.post(this);
        }
    }

    /**
     * This Runnable is used to animate an image zoom.
     * Given values are interpolated during the animation time.
     * Runnable can be terminated either vie {@link #cancelAllAnimations()} method
     * or when certain conditions inside {@link ZoomImageToPosition#run()} method are triggered.
     */
    private static class ZoomImageToPosition implements Runnable {

        private final WeakReference<CropImageView> mCropImageView;

        private final long mDurationMs, mStartTime;
        private final float mOldScale;
        private final float mDeltaScale;
        private final float mDestX;
        private final float mDestY;

        public ZoomImageToPosition(CropImageView cropImageView,
                                   long durationMs,
                                   float oldScale, float deltaScale,
                                   float destX, float destY) {

            mCropImageView = new WeakReference<>(cropImageView);

            mStartTime = System.currentTimeMillis();
            mDurationMs = durationMs;
            mOldScale = oldScale;
            mDeltaScale = deltaScale;
            mDestX = destX;
            mDestY = destY;
        }

        @Override
        public void run() {
            CropImageView cropImageView = mCropImageView.get();
            if (cropImageView == null) {
                return;
            }

            long now = System.currentTimeMillis();
            float currentMs = Math.min(mDurationMs, now - mStartTime);
            float newScale = CubicEasing.easeInOut(currentMs, 0, mDeltaScale, mDurationMs);
            if (currentMs < mDurationMs) {
                cropImageView.zoomInImage(mOldScale + newScale, mDestX, mDestY);
                cropImageView.post(this);
            } else {
                cropImageView.setImageToWrapCropBounds();
            }
        }

    }

}
