package com.bcnetech.hyphoto.ui.view.verticalscrollview;

/**
 * Created by a1234 on 2017/7/5.
 */

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

import com.bcnetech.hyphoto.ui.view.TwoWaysSeekBar;

/**
 * Created by wenbin on 16/6/21.
 */
public class VerticalSeekBar extends TwoWaysSeekBar {

    public VerticalSeekBar(Context context) {
        super(context);
    }

    public VerticalSeekBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public VerticalSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(h, w, oldh, oldw);
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(heightMeasureSpec, widthMeasureSpec);
        setMeasuredDimension(getMeasuredHeight(), getMeasuredWidth());
    }

    protected void onDraw(Canvas c) {
        //将SeekBar转转90度
        c.rotate(-90);
        //将旋转后的视图移动回来
        c.translate(-getHeight(),0);
        super.onDraw(c);
    }
    public void setProgresssee(int i){
        setProgress(i);
        onSizeChanged(getWidth(), getHeight(), 0, 0);
    }
}
