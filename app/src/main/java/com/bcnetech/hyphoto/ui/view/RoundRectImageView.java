package com.bcnetech.hyphoto.ui.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.bcnetech.hyphoto.utils.ImageUtil;

/**
 * 自定义的圆角矩形ImageView，可以直接当组件在布局中使用。
 */
public class RoundRectImageView extends ImageView {
    public static final int DETAIL_TEMPLATE_MODEL = 1;
    public static final int MARKET_MODEL = 2;
    public static final int VIDEO_MODEL = 3;
    public static final int NORMAL_MODEL = 4;
    private int type;
    private Bitmap bitmap;
    /*圆角的半径，依次为左上角xy半径，右上角，右下角，左下角*/
    float rount = ImageUtil.Dp2Px(getContext(),16);
    private float[] rids = {rount, rount, rount, rount, 0.0f, 0.0f, 0.0f, 0.0f,};

    private Paint paint;

    public RoundRectImageView(Context context) {
        this(context, null);
    }

    public RoundRectImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundRectImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        paint = new Paint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Path clipPath = new Path();
        int w = this.getWidth();
        int h = this.getHeight();
        if (type == DETAIL_TEMPLATE_MODEL) {
            clipPath.addRoundRect(new RectF(0, 0, w, h), 60.0f, 60.0f, Path.Direction.CW);
        } else if (type == MARKET_MODEL) {
            clipPath.addRoundRect(new RectF(0, 0, w, h), rids, Path.Direction.CW);
        } else if (type == VIDEO_MODEL) {
            float[] videorids = {30.0f, 30.0f, 30.0f, 30.0f, 30.0f, 30.0f, 30.0f, 30.0f,};
            clipPath.addRoundRect(new RectF(0, 0, w, h), videorids, Path.Direction.CW);
        } else if (type == NORMAL_MODEL) {
            clipPath.addRoundRect(new RectF(0, 0, w, h), 0, 0, Path.Direction.CW);
        }
        canvas.clipPath(clipPath);
        super.onDraw(canvas);
    }

    public void setType(int type) {
        switch (type) {
            case MARKET_MODEL:
                this.type = MARKET_MODEL;
                break;
            case DETAIL_TEMPLATE_MODEL:
                this.type = DETAIL_TEMPLATE_MODEL;
                break;
            case VIDEO_MODEL:
                this.type = VIDEO_MODEL;
                break;
            case NORMAL_MODEL:
                this.type = NORMAL_MODEL;
                break;
        }
    }

}
