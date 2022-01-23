package com.bcnetech.hyphoto.ui.view.croprotate;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

import com.bcnetech.hyphoto.imageinterface.BizImageMangage;
import com.bcnetech.hyphoto.sql.dao.PictureProcessingData;
import com.yalantis.ucrop.callback.BitmapLoadCallback;
import com.yalantis.ucrop.model.ExifInfo;
import com.yalantis.ucrop.util.BitmapLoadUtils;
import com.yalantis.ucrop.util.FastBitmapDrawable;
import com.yalantis.ucrop.util.RectUtils;

import java.util.ArrayList;
import java.util.List;

import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.GPUImageFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageFilterGroup;
import jp.co.cyberagent.android.gpuimage.gpumanage.GPUImageFilterTools;


/**
 * Created by Oleksii Shliama (https://github.com/shliama).
 * <p/>
 * This class provides base logic to setup the image, transform it with matrix (move, scale, rotate),
 * and methods to get current matrix state.
 */
public class TransformImageView extends ImageView {

    private static final String TAG = "TransformImageView";

    private static final int RECT_CORNER_POINTS_COORDS = 8;
    private static final int RECT_CENTER_POINT_COORDS = 2;
    private static final int MATRIX_VALUES_COUNT = 9;

    protected final float[] mCurrentImageCorners = new float[RECT_CORNER_POINTS_COORDS];
    protected final float[] mCurrentImageCenter = new float[RECT_CENTER_POINT_COORDS];

    private final float[] mMatrixValues = new float[MATRIX_VALUES_COUNT];

    protected Matrix mCurrentImageMatrix = new Matrix();
    public Matrix baseMatrix = new Matrix();
    protected int mThisWidth, mThisHeight;
    public float cw, ch;//初始图片加载的宽高（未压缩80%）

    protected TransformImageListener mTransformImageListener;

    private float[] mInitialImageCorners;
    private float[] mInitialImageCenter;

    protected boolean mBitmapDecoded = false;
    protected boolean mBitmapLaidOut = false;

    private int mMaxBitmapSize = 0;

    private String mImageInputPath, mImageOutputPath;
    private ExifInfo mExifInfo;
    private Bitmap currentBitmap;
    private Bitmap orginBitmap;
    public float rotatePlus = 0;
    public float rotateAngel = 0;
    public float initialMinScale = 1.0f;//初始缩放比

    private GPUImage gpuImage;
    private GPUImageFilter srcFilterGroup;
    private List<PictureProcessingData> imageTools;


    /**
     * Interface for rotation and scale change notifying.
     */
    public interface TransformImageListener {

        void onLoadComplete();

        void onLoadFailure(Exception fail);

        void onRotate(float currentAngle);

        void onScale(float currentScale);

    }

    public TransformImageView(Context context) {
        this(context, null);
    }

    public TransformImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TransformImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public void setTransformImageListener(TransformImageListener transformImageListener) {
        mTransformImageListener = transformImageListener;
    }

    @Override
    public void setScaleType(ScaleType scaleType) {
        if (scaleType == ScaleType.MATRIX) {
            super.setScaleType(scaleType);
        } else {
            Log.w(TAG, "Invalid ScaleType. Only ScaleType.MATRIX can be used");
        }
    }

    /**
     * Setter for {@link #mMaxBitmapSize} value.
     * Be sure to call it before {@link #setImageURI(Uri)} or other image setters.
     *
     * @param maxBitmapSize - max size for both width and height of bitmap that will be used in the view.
     */
    public void setMaxBitmapSize(int maxBitmapSize) {
        mMaxBitmapSize = maxBitmapSize;
    }

    public int getMaxBitmapSize() {
        if (mMaxBitmapSize <= 0) {
            mMaxBitmapSize = BitmapLoadUtils.calculateMaxBitmapSize(getContext());
        }
        return mMaxBitmapSize;
    }

    @Override
    public void setImageBitmap(final Bitmap bitmap) {
        setImageDrawable(new FastBitmapDrawable(bitmap));
    }

    private void reSetImageBitmap(boolean isLr) {
        if (orginBitmap != null) {
            Matrix mirror_M = new Matrix();
            if (isLr) {
                mirror_M.postScale(-1, 1); //镜像水平翻转
            } else {
                mirror_M.postScale(1, -1); //镜像上下翻转
            }
            orginBitmap = Bitmap.createBitmap(orginBitmap, 0, 0, orginBitmap.getWidth(), orginBitmap.getHeight(), mirror_M, true);
            currentBitmap = gpuImage.getBitmapWithFilterApplied(orginBitmap);
            setImageBitmap(currentBitmap);
            setImageMatrix(mCurrentImageMatrix);
        }
    }

    public void setmCurrentImageMatrix() {
        setImageMatrix(mCurrentImageMatrix);
    }


    private void setGpuImage() {
        gpuImage = new GPUImage(getContext());
        List list2 = new ArrayList();
        for (int i = 0; imageTools != null && i < imageTools.size(); i++) {
            GPUImageFilter mFilter;
            GPUImageFilterTools.FilterAdjuster mFilterAdjuster;
            mFilter = BizImageMangage.getInstance().getGPUFilterforType(getContext(), imageTools.get(i).getType());
            list2.add(mFilter);
            mFilterAdjuster = new GPUImageFilterTools.FilterAdjuster(mFilter);
            if (imageTools.get(i).getType() == BizImageMangage.WHITE_BALANCE) {
                mFilterAdjuster.adjust(imageTools.get(i).getNum(), imageTools.get(i).getTintNum());
            } else {
                mFilterAdjuster.adjust(imageTools.get(i).getNum());
            }
        }
        if (list2.size() != 0) {
            srcFilterGroup = new GPUImageFilterGroup(list2);
        } else {
            srcFilterGroup = new GPUImageFilter();
        }
        gpuImage.setFilter(srcFilterGroup);
    }

    public Bitmap getCurrentBitmap() {
        return this.orginBitmap;
    }

    public String getImageInputPath() {
        return mImageInputPath;
    }

    public void setmImageInputPath(String mImageInputPath) {
        this.mImageInputPath = mImageInputPath;
    }


    public String getImageOutputPath() {
        return mImageOutputPath;
    }

    public ExifInfo getExifInfo() {
        return mExifInfo;
    }

    /**
     * This method takes an Uri as a parameter, then calls method to decode it into Bitmap with specified size.
     *
     * @throws Exception - can throw exception if having problems with decoding Uri or OOM.
     */
    public void setImageUri(@Nullable Uri inputUri, @Nullable Uri outputUri, final boolean isPng, List<PictureProcessingData> imageTools) {
        this.imageTools = imageTools;
        int maxBitmapSize = getMaxBitmapSize();
        BitmapLoadUtils.decodeBitmapInBackground(getContext(), inputUri, outputUri, maxBitmapSize, maxBitmapSize,
                new BitmapLoadCallback() {

                    @Override
                    public void onBitmapLoaded(@NonNull Bitmap bitmap, @NonNull ExifInfo exifInfo, @NonNull String imageInputPath, @Nullable String imageOutputPath) {
                        mImageInputPath = imageInputPath;
                        mImageOutputPath = imageOutputPath;
                        mExifInfo = exifInfo;

                        mBitmapDecoded = true;
                        orginBitmap = bitmap;
                        setGpuImage();
                        currentBitmap = gpuImage.getBitmapWithFilterApplied(bitmap);
                        setImageBitmap(currentBitmap);
                    }

                    @Override
                    public void onFailure(@NonNull Exception bitmapWorkerException) {
                        Log.e(TAG, "onFailure: setImageUri", bitmapWorkerException);
                        if (mTransformImageListener != null) {
                            mTransformImageListener.onLoadFailure(bitmapWorkerException);
                        }
                    }
                });
    }


    /**
     * @return - current image scale value.
     * [1.0f - for original image, 2.0f - for 200% scaled image, etc.]
     */
    public float getCurrentScale() {
        return getMatrixScale(mCurrentImageMatrix);
    }

    /**
     * This method calculates scale value for given Matrix object.
     */
    public float getMatrixScale(@NonNull Matrix matrix) {
        return (float) Math.sqrt(Math.pow(getMatrixValue(matrix, Matrix.MSCALE_X), 2)
                + Math.pow(getMatrixValue(matrix, Matrix.MSKEW_Y), 2));
    }

    /**
     * @return - current image rotation angle.
     */
    public float getCurrentAngle() {
        return getMatrixAngle(mCurrentImageMatrix);
    }

    /**
     * This method calculates rotation angle for given Matrix object.
     */
    public float getMatrixAngle(@NonNull Matrix matrix) {
        return (float) -(Math.atan2(getMatrixValue(matrix, Matrix.MSKEW_X),
                getMatrixValue(matrix, Matrix.MSCALE_X)) * (180 / Math.PI));
    }

    @Override
    public void setImageMatrix(Matrix matrix) {
        super.setImageMatrix(matrix);
        mCurrentImageMatrix.set(matrix);
        updateCurrentImagePoints();
    }

    @Nullable
    public Bitmap getViewBitmap() {
        if (getDrawable() == null || !(getDrawable() instanceof FastBitmapDrawable)) {
            return null;
        } else {
            return ((FastBitmapDrawable) getDrawable()).getBitmap();
        }
    }

    /**
     * This method translates current image.
     *
     * @param deltaX - horizontal shift
     * @param deltaY - vertical shift
     */
    public void postTranslate(float deltaX, float deltaY) {
        if (deltaX != 0 || deltaY != 0) {
            mCurrentImageMatrix.postTranslate(deltaX, deltaY);
            setImageMatrix(mCurrentImageMatrix);
        }
    }

    /**
     * This method scales current image.
     *
     * @param deltaScale - scale value
     * @param px         - scale center X
     * @param py         - scale center Y
     */
    public void postScale(float deltaScale, float px, float py) {
        if (deltaScale != 0) {
            mCurrentImageMatrix.postScale(deltaScale, deltaScale, px, py);
            setImageMatrix(mCurrentImageMatrix);
            if (mTransformImageListener != null) {
                mTransformImageListener.onScale(getMatrixScale(mCurrentImageMatrix));
            }
        }
    }

    /**
     * This method rotates current image.
     *
     * @param rotateAngle - rotation angle
     * @param px          - rotation center X
     * @param py          - rotation center Y
     */
    public void postRotate(float rotateAngle, float px, float py) {
        rotatePlus += rotateAngle;
        mCurrentImageMatrix.postRotate(rotateAngle, px, py);
        setImageMatrix(mCurrentImageMatrix);
        if (mTransformImageListener != null) {
            mTransformImageListener.onRotate(getMatrixAngle(mCurrentImageMatrix));
        }
    }

    /**
     * 镜像图片
     *
     * @param isLr
     */
    public void setMirror(boolean isLr) {
        reSetImageBitmap(isLr);

    }


    /**
     * 旋转放大
     *
     * @param rotateAngle - rotation angle
     * @param px          - rotation center X
     * @param py          - rotation center Y
     * @param initRation  - 初始缩放值
     */
   /* public void rotateSacle(float rotateAngle, float px, float py, float initRation) {
        // calculationInitScale();
        //此角度为每次应转角度
        float scale = 1;//相对于原始角度需要放大的值
        calculationInitScale();
        if (rotateAngle != 0) {
            this.rotateAngel = rotateAngle;
            this.initialMinScale = initRation;
            float oldScale = getCurrentScale();
            float oldRotate = getCurrentAngle();
             //oldScale /= initRation;
            if (rotateAngle >= -45 && rotateAngle <= 45) {
                float resultw = (float) (cw * Math.abs(Math.cos(Math.toRadians(rotateAngle))) + ch * Math.abs(Math.sin(Math.toRadians(rotateAngle)))) ;
                float resulty = (float) (cw * Math.abs(Math.sin(Math.toRadians(rotateAngle))) + ch * Math.abs(Math.cos(Math.toRadians(rotateAngle)))) ;
                float scaleX = resultw/cw;
                float scaleY = resulty/ch;
                Log.d("xxxx3",cw+" "+ch+" "+resultw+" "+resulty);
                scale = Math.max(scaleX, scaleY);
                // float deltaScale = scale / oldScale *initRation;
                float deltaScale = scale / oldScale;
                deltaScale = deltaScale/oldScale;
                float deltaRotate = rotateAngle + rotatePlus - oldRotate;
                mCurrentImageMatrix.postRotate(deltaRotate, px, py);
                Log.d("xxxx2",scale+" "+deltaScale+" "+oldScale);
                mCurrentImageMatrix.postScale(deltaScale, deltaScale, px, py);
                setImageMatrix(mCurrentImageMatrix);
                if (mTransformImageListener != null) {
                    mTransformImageListener.onRotate(getMatrixAngle(mCurrentImageMatrix));
                    mTransformImageListener.onScale(getMatrixScale(mCurrentImageMatrix));
                }
            }
        }
    }*/


    /**
     * 通过目标放大值反推初始放大倍数
     */
  /*  public void calculationInitScale() {
        final float[] currentImageSides = RectUtils.getRectSidesFromCorners(mCurrentImageCorners);
        cw = currentImageSides[0];
        ch = currentImageSides[1];
    }*/
    protected void init() {
        setScaleType(ScaleType.MATRIX);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed || (mBitmapDecoded && !mBitmapLaidOut)) {
            left = getPaddingLeft();
            top = getPaddingTop();
            right = getWidth() - getPaddingRight();
            bottom = getHeight() - getPaddingBottom();
            mThisWidth = right - left;
            mThisHeight = bottom - top;
            onImageLaidOut();
        }
    }

    /**
     * When image is laid out {@link #mInitialImageCenter} and {@link #mInitialImageCenter}
     * must be set.
     */
    protected void onImageLaidOut() {
        final Drawable drawable = getDrawable();
        if (drawable == null) {
            return;
        }

        float w = drawable.getIntrinsicWidth();
        float h = drawable.getIntrinsicHeight();


        RectF initialImageRect = new RectF(0, 0, w, h);
        mInitialImageCorners = RectUtils.getCornersFromRect(initialImageRect);
        mInitialImageCenter = RectUtils.getCenterFromRect(initialImageRect);

        mBitmapLaidOut = true;

        if (mTransformImageListener != null) {
            mTransformImageListener.onLoadComplete();
        }
    }

    /**
     * This method returns Matrix value for given index.
     *
     * @param matrix     - valid Matrix object
     * @param valueIndex - index of needed value. See {@link Matrix#MSCALE_X} and others.
     * @return - matrix value for index
     */
    protected float getMatrixValue(@NonNull Matrix matrix, @IntRange(from = 0, to = MATRIX_VALUES_COUNT) int valueIndex) {
        matrix.getValues(mMatrixValues);
        return mMatrixValues[valueIndex];
    }

    /**
     * This method logs given matrix X, Y, scale, and angle values.
     * Can be used for debug.
     */
    @SuppressWarnings("unused")
    protected void printMatrix(@NonNull String logPrefix, @NonNull Matrix matrix) {
        float x = getMatrixValue(matrix, Matrix.MTRANS_X);
        float y = getMatrixValue(matrix, Matrix.MTRANS_Y);
        float rScale = getMatrixScale(matrix);
        float rAngle = getMatrixAngle(matrix);
        Log.d(TAG, logPrefix + ": matrix: { x: " + x + ", y: " + y + ", scale: " + rScale + ", angle: " + rAngle + " }");
    }

    /**
     * 更新当前图片的顶点和中心坐标
     * This method updates current image corners and center points that are stored in
     * {@link #mCurrentImageCorners} and {@link #mCurrentImageCenter} arrays.
     * Those are used for several calculations.
     */
    private void updateCurrentImagePoints() {
        if (mInitialImageCorners != null)
            mCurrentImageMatrix.mapPoints(mCurrentImageCorners, mInitialImageCorners);
        if (mInitialImageCenter != null)
            mCurrentImageMatrix.mapPoints(mCurrentImageCenter, mInitialImageCenter);
    }

}
