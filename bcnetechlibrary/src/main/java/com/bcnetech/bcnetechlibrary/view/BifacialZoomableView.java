package com.bcnetech.bcnetechlibrary.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.bcnetech.bcnetechlibrary.util.ImageUtil;



/**
 * Created by ergashev on 11.04.17.
 * This view displays a clipped drawable on the right over another drawable
 * Sliding the delimiter to the right reveals more of the left view
 */
public class BifacialZoomableView extends View {



    private Paint paint;



    private int delimiterPosition;
    private int width;
    private int height;
    private int delimiterColor= Color.WHITE;
    private int delimiterWidth=3;
    private Drawable drawableLeft;
    private Drawable drawableRight;
    private Bitmap bitmapLeft;
    //private Bitmap bitmapRight;
    private Rect canveRect, bitmapRect;



    public BifacialZoomableView(Context context) {
        super(context);
        init();
    }

    public BifacialZoomableView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();

    }


    public Rect getCanveRect() {
        return canveRect;
    }

    public void setCanveRect(Rect canveRect) {
        this.canveRect = canveRect;
    }

    public Rect getBitmapRect() {
        return bitmapRect;
    }

    public void setBitmapRect(Rect bitmapRect) {
        this.bitmapRect = bitmapRect;
    }

    public BifacialZoomableView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();

    }

    private void init() {
        paint = new Paint();
        canveRect = new Rect();
        bitmapRect = new Rect();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        width = MeasureSpec.getSize(widthMeasureSpec);
        height = MeasureSpec.getSize(heightMeasureSpec);
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
                // for ViewPager and RecyclerView
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
        //bitmapRect.set(0, 0 ,4800, 2700);

        if (delimiterPosition > 0 && drawableLeft != null) {
            if (width - delimiterPosition < 0) {
                delimiterPosition = width;
            }
            //canveRect.set(-894,-436 ,2041, 1214);

            canvas.drawBitmap(bitmapLeft, null, canveRect, paint);
        }

     /*   paint.setColor(delimiterColor);
        paint.setStrokeWidth(delimiterWidth);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawLine(delimiterPosition, 0, delimiterPosition, height, paint);*/




    /*    if (width - delimiterPosition > 0 && drawableRight != null) {
            if (delimiterPosition < 0) {
                delimiterPosition = 0;
            }

            //canvas.drawRect(canveRect,paint);
            canvas.clipRect(delimiterPosition + delimiterWidth / 2, 0, width, height);
            drawableRight.draw(canvas);
        }*/


    }


    public void setDrawableLeft(Drawable drawableLeft,Bitmap bitmapLeft) {
        if (width > 0 && height > 0) {
            this.drawableLeft = ImageUtil.resizeDrawable(drawableLeft, width, height);
        } else {
            this.drawableLeft = drawableLeft;
        }
        this.bitmapLeft=bitmapLeft;
        invalidate();
    }

    public void setDrawableRight(Drawable drawableRight) {
        if (width > 0 && height > 0) {
            this.drawableRight = ImageUtil.resizeDrawable(drawableRight, width, height);
        } else {
            this.drawableRight = drawableRight;
        }
          //this.bitmapRight=bitmapRight;
        invalidate();
    }


    public static Bitmap drawableToBitmap(Drawable drawable) {

        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();
        System.out.println("Drawable转Bitmap");
        Bitmap.Config config =Bitmap.Config.ARGB_8888;
        Bitmap bitmap = Bitmap.createBitmap(w, h, config);
        //注意，下面三行代码要用到，否则在View或者SurfaceView里的canvas.drawBitmap会看不到图
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        drawable.draw(canvas);

        return bitmap;
    }

}
