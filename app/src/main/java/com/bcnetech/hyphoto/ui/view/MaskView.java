package com.bcnetech.hyphoto.ui.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by wenbin on 2017/5/2.
 */

public class MaskView extends ImageView {
    private Bitmap maskBit;
    private Paint paint;
    private Rect bitRect,canveRect;
    public MaskView(Context context) {
        super(context);
    }

    public MaskView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MaskView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    public void initData(){
        paint=new Paint(Paint.DITHER_FLAG);
    }

    public void setCanveRect(Rect rect){
        canveRect=rect;
        invalidate();

    }
    public void setMaskBit(Bitmap bit){
        if(bit==null){
            return;
        }
        this.maskBit=bit;
        bitRect=new Rect(0,0,maskBit.getWidth(),maskBit.getHeight());
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {

        if(maskBit==null){
            canvas.drawColor(0x00000000);
        }
        else{
            canvas.drawBitmap(maskBit,bitRect,canveRect,paint);
        }
    }


}
