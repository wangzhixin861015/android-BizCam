package com.yalantis.ucrop.callback;

import android.graphics.PointF;
import android.graphics.RectF;

/**
 * Created by Oleksii Shliama.
 */
public interface OverlayViewChangeListener {

    void onCropRectUpdated(RectF cropRect, boolean isCanAutoScale, PointF moveoffset);

    void onTouchMoveCrop(boolean isTouchMoving,RectF smallRect,RectF maxRect);

}