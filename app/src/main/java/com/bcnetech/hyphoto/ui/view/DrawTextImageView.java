package com.bcnetech.hyphoto.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.bcnetech.hyphoto.utils.ImageUtil;
import com.bcnetech.hyphoto.R;

/**
 * Created by a1234 on 16/11/11.
 */

public class DrawTextImageView extends ImageView {

    private Context mContext = null;

    private Paint paintText = null;
    private Paint paintBg = null;

    private Rect bgRect = null;
    private int width = 0;
    private int height = 0;

    private int BgHeight = 80;

    private String text = getResources().getString(R.string.edit_headimg);
    private int textLen = 0;
    private int textSize = 40;
    private int textTop = 0;
    private int textLeft = 0;
    private boolean isshow = true;

    private Paint.FontMetrics fm = null;

    public DrawTextImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        this.mContext = context;
        textSize = ImageUtil.Dp2Px(mContext, 14);

        initPaint();

    }

    public void setMiddleText(String text) {
        textLen = 0;
        this.text = text;
        this.invalidate();
        initPaint();
    }

    public void setTextSize(int size) {
        this.textSize = size;
    }

    public DrawTextImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DrawTextImageView(Context context) {
        this(context, null);
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
                            int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        height = getHeight();
        width = getWidth();

        if (width > 0 && height > 0) {
            if (bgRect == null) {
                bgRect = new Rect(0, height - width, width, height - width + BgHeight);
                textLeft = (width - textLen) / 2;
                textTop = (int) (height - (height - Math.abs(fm.ascent)) / 2);
                //textTop = (int) (height - width + BgHeight  + Math.abs(fm.ascent) );
            }
        }
    }

    private void initPaint() {

        paintBg = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintBg.setColor(getResources().getColor(R.color.translucent));

       /*根据背景颜色深浅判断字体的颜色*/
        paintText = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintText.setColor(Color.WHITE);
        paintText.setTextSize(textSize);

        textLen = (int) paintText.measureText(text);
        fm = paintText.getFontMetrics();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRect(bgRect, paintBg);
        if (isshow) {
            canvas.drawText(text, textLeft, textTop, paintText);
        }
    }

    public void showText(boolean isshow){
        this.isshow = isshow;
        this.invalidate();
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
       // drawableToBitamp(drawable);
    }

   /* private void drawableToBitamp(Drawable drawable) {
        Bitmap bitmap;
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();
        Bitmap.Config config =
                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                        : Bitmap.Config.RGB_565;
        bitmap = Bitmap.createBitmap(w, h, config);
        //注意，下面三行代码要用到，否在在View或者surfaceview里的canvas.drawBitmap会看不到图
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        drawable.draw(canvas);
    }*/

}

