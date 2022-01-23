package com.bcnetech.hyphoto.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.style.ImageSpan;

/**
 * 居中的ImageSpan
 */
public class CentImageSpan extends ImageSpan {
    public CentImageSpan(Context arg0, int arg1) {
        super(arg0, arg1);
    }

    @Override
    public void draw(Canvas canvas, CharSequence text, int start, int end,
                     float x, int top, int y, int bottom, Paint paint) {
        Drawable b = getDrawable();
        Paint.FontMetricsInt fm = paint.getFontMetricsInt();
        int transY = (y + fm.descent + y + fm.ascent) / 2 - b.getBounds().bottom / 2;//计算y方向的位移
        canvas.save();
        canvas.translate(x, transY);//绘制图片位移一段距离
        b.draw(canvas);
        canvas.restore();
    /*	Drawable b = getDrawable();
        canvas.save();
		int transY = 0;
		transY = ((bottom - top) - b.getBounds().bottom) / 2 + top;
		canvas.translate(x, transY);
		b.draw(canvas);
		canvas.restore();*/
    }
}
