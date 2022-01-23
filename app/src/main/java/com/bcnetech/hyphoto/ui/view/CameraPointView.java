package com.bcnetech.hyphoto.ui.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.bcnetech.bcnetechlibrary.util.ContentUtil;
import com.bcnetech.hyphoto.R;

/**
 * Created by a1234 on 2017/11/22.
 */

public class CameraPointView extends ImageView {
    private Bitmap middleBitmap;
    private int startY = 0;
    private int startX = 0;
    private Paint paint;
    private String text="0.0";
    public CameraPointView(Context context) {
        super(context);
        init();
    }

    public CameraPointView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CameraPointView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    private void init(){
        paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(ContentUtil.dip2px(getContext(),14));
        paint.setStrokeWidth(ContentUtil.dip2px(getContext(),2));
        middleBitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.camera_oval);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        startY = getMeasuredHeight() / 2;
        startX = getMeasuredWidth() / 2;
        postInvalidate();
    }

    @Override
    public void draw(Canvas canvas) {
        DrawPointer(canvas,paint);
        super.draw(canvas);
    }

    private void DrawPointer(Canvas canvas, Paint paint){
//        canvas.drawBitmap(middleBitmap, startX-middleBitmap.getWidth()/2, startY - ContentUtil.dip2px(getContext(), 30), paint);
//        canvas.drawText(text,startX - ContentUtil.dip2px(getContext(),10f) , startY+ ContentUtil.dip2px(getContext(), 30), paint);

        canvas.drawLine(startX,0,startX,ContentUtil.dip2px(getContext(),6),paint);
        canvas.drawLine(startX,getMeasuredHeight(),startX,getMeasuredHeight()-ContentUtil.dip2px(getContext(),6),paint);
    }

    public void setText(String text){
        this.text = text;
        invalidate();
    }
}
