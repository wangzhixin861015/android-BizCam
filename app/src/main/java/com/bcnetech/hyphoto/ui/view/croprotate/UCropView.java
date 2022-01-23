package com.bcnetech.hyphoto.ui.view.croprotate;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.bcnetech.hyphoto.R;
import com.bcnetech.hyphoto.ui.view.RotateRularView;
import com.yalantis.ucrop.callback.CropBoundsChangeListener;
import com.yalantis.ucrop.callback.OverlayViewChangeListener;


public class UCropView extends FrameLayout {

    private GestureCropImageView mGestureCropImageView;
    private final OverlayView mViewOverlay;
    private RotateRularView crop_rotates2;
    private int deviceW;

    public UCropView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public UCropView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.ucrop_view, this, true);
        mGestureCropImageView = (GestureCropImageView) findViewById(R.id.image_view_crop);
        mViewOverlay = (OverlayView) findViewById(R.id.view_overlay);
        crop_rotates2 = (RotateRularView) findViewById(R.id.crop_rotates2);
        WindowManager wm = (WindowManager) getContext()
                .getSystemService(Context.WINDOW_SERVICE);
        deviceW = wm.getDefaultDisplay().getWidth();
        TypedArray a = context.obtainStyledAttributes(attrs, com.bcnetech.bcnetechlibrary.R.styleable.ucrop_UCropView);
        mViewOverlay.processStyledAttributes(a);
        mGestureCropImageView.processStyledAttributes(a);
        a.recycle();


        setListenersToViews();
    }

    private void setListenersToViews() {
        mGestureCropImageView.setCropBoundsChangeListener(new CropBoundsChangeListener() {
            @Override
            public void onCropAspectRatioChanged(float cropRatio, boolean isCanAutoScale) {
                mViewOverlay.setTargetAspectRatio(cropRatio, isCanAutoScale);
            }
        });
        mViewOverlay.setOverlayViewChangeListener(new OverlayViewChangeListener() {
            @Override
            public void onCropRectUpdated(RectF cropRect, boolean isCanAutoScale, PointF moveoffset) {
                mGestureCropImageView.setCropRect(cropRect, isCanAutoScale, moveoffset);
             /*   double r = (cropRect.right - cropRect.left) / 2 / Math.sin(Math.toRadians(45));
                double h = (cropRect.right - cropRect.left) / 2 / Math.tan(Math.toRadians(45));
                Point circle = new Point((int) (cropRect.left) + (int) ((cropRect.right - cropRect.left) / 2), (int) (cropRect.bottom - h));//圆心坐标*/
                double r = (deviceW) / 4 / Math.sin(Math.toRadians(45));
                double h = (deviceW) / 4 / Math.tan(Math.toRadians(45));
                Point circle = new Point((int) (cropRect.left) + (int) ((cropRect.right - cropRect.left) / 2), (int) (cropRect.bottom - h));//圆心坐标
                crop_rotates2.getRoundParm(circle, r, cropRect.bottom);
            }

            @Override
            public void onTouchMoveCrop(boolean isTouchMoving, RectF smallRect,RectF maxRect) {
                if (isTouchMoving) {
                    crop_rotates2.setVisibility(INVISIBLE);
                } else {
                    crop_rotates2.setVisibility(VISIBLE);
                        //mGestureCropImageView.zoomAndTranslateImage(smallRect,maxRect);
                }
            }
        });
    }

    @Override
    public boolean shouldDelayChildPressedState() {
        return false;
    }

    @NonNull
    public GestureCropImageView getCropImageView() {
        return mGestureCropImageView;
    }

    @NonNull
    public OverlayView getOverlayView() {
        return mViewOverlay;
    }

    @NonNull
    public RotateRularView getRotateRularView() {
        return crop_rotates2;
    }

    /**
     * Method for reset state for UCropImageView such as rotation, scale, translation.
     * Be careful: this method recreate UCropImageView instance and reattach it to layout.
     */
    public void resetCropImageView() {
        removeView(mGestureCropImageView);
        mGestureCropImageView = new GestureCropImageView(getContext());
        setListenersToViews();
        mGestureCropImageView.setCropRect(getOverlayView().getCropViewRect(), true, null);
        addView(mGestureCropImageView, 0);
    }
}