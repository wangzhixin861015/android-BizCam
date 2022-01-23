package com.bcnetech.bcnetechlibrary.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.bcnetech.bcnetechlibrary.util.ImageUtil;


/**
 * Created by ergashev on 11.04.17.
 * This view displays a clipped drawable on the right over another drawable
 * Sliding the delimiter to the right reveals more of the left view
 */
public class MyImageView extends ImageView {



    private Paint paint;



    private int delimiterPosition=20;
    private int width;
    private int height;
    private int delimiterColor= Color.WHITE;
    private int delimiterWidth=3;


    private Drawable drawableLeft;
    private Drawable drawableRight;



    private boolean isShow=false;
    public MyImageView(Context context) {
        super(context);
        init();
    }

    public MyImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();

    }

    private void init() {
        paint = new Paint();

    }


    public boolean isShow() {
        return isShow;
    }

    public void setShow(boolean show) {
        isShow = show;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        width = View.MeasureSpec.getSize(widthMeasureSpec);
        height = View.MeasureSpec.getSize(heightMeasureSpec);
        delimiterPosition = width-80;

        if (drawableLeft != null) {
            drawableLeft = ImageUtil.resizeDrawable(drawableLeft, width, height);
        }

        if (drawableRight != null) {
            drawableRight = ImageUtil.resizeDrawable(drawableRight, width, height);
        }

        setMeasuredDimension(width, height);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                getParent().requestDisallowInterceptTouchEvent(true);
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                getParent().requestDisallowInterceptTouchEvent(false);
                break;
        }
        delimiterPosition = (int) (x / 1);
        invalidate();
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (delimiterPosition > 0 && drawableLeft != null) {
            if (width - delimiterPosition < 0) {
                delimiterPosition = width;
            }
            drawableLeft.draw(canvas);
        }

        if(isShow){
            paint.setColor(delimiterColor);
            paint.setStrokeWidth(delimiterWidth);
            paint.setStyle(Paint.Style.STROKE);
            canvas.drawLine(delimiterPosition, 0, delimiterPosition, height, paint);
        }


        if (width - delimiterPosition > 0 && drawableRight != null) {
            if (delimiterPosition < 0) {
                delimiterPosition = 0;
            }
            canvas.clipRect(delimiterPosition + delimiterWidth / 2, 0, width, height);
            drawableRight.draw(canvas);
        }



    }



    public void setDrawableLeft(Drawable drawableLeft) {
        if (width > 0 && height > 0) {
            this.drawableLeft = ImageUtil.resizeDrawable(drawableLeft, width, height);
        } else {
            this.drawableLeft = drawableLeft;
        }
        invalidate();
    }

    public void setDrawableRight(Drawable drawableRight) {
        if (width > 0 && height > 0) {
            this.drawableRight = ImageUtil.resizeDrawable(drawableRight, width, height);
        } else {
            this.drawableRight = drawableRight;
        }
        invalidate();
    }

public void remove(){
    this.drawableLeft=null;
    this.drawableRight=null;
}

}
