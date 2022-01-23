package com.bcnetech.hyphoto.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.bcnetech.hyphoto.R;


/**
 * author: wsBai
 * date: 2018/12/14
 */
public class FilterCoverView extends View {
    private Paint paint;

    public FilterCoverView(Context context) {
        super(context);
    }

    public FilterCoverView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public FilterCoverView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(getResources().getColor(R.color.sing_in_color));
        paint.setStrokeWidth(5);
        canvas.drawLine(getWidth() / 2, 0, getWidth() / 2, getHeight(), paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }
}
