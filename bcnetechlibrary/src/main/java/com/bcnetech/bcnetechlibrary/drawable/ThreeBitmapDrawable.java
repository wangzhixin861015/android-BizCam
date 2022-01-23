package com.bcnetech.bcnetechlibrary.drawable;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

import com.bcnetech.bcnetechlibrary.BcnetechAppInstance;
import com.bcnetech.bcnetechlibrary.util.ContentUtil;

/**
 * Created by wenbin on 16/5/13.
 */
public class ThreeBitmapDrawable extends Drawable {


    private Path[] path;
    private Rect[] rect;
    private Paint mPaint,mPaint1,mPaint2;
    private Bitmap mBitmap,mBitmap1,mBitmap2,mBitmap3;

    private int x,y,w;

    private RectF rectF;
    private PorterDuffXfermode mMode ;

    private float f=0.3f;
    public ThreeBitmapDrawable(Bitmap bitmap,Bitmap bitmap1,Bitmap bitmap2,Bitmap bitmap3)
    {
        mBitmap=bitmap;
        mBitmap1=bitmap1;
        mBitmap2=bitmap2;
        mBitmap3=bitmap3;
        //BitmapShader bitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);

        mPaint1=new Paint();
        mPaint1.setAntiAlias(true);

        mPaint2 = new Paint();
        mPaint2.setAntiAlias(true);
        //mPaint.setShader(bitmapShader);
    }
    public void setBitmapBG(Bitmap bitmap){
        this.mBitmap=bitmap;
        invalidateSelf();
    }

    public void setmBitmap1(Bitmap bitmap1){
        this.mBitmap1=bitmap1;
        invalidateSelf();
    }

    public void setmBitmap2(Bitmap bitmap2){
        this.mBitmap2=bitmap2;
        invalidateSelf();
    }
    public void setmBitmap3(Bitmap bitmap3){
        this.mBitmap3=bitmap3;
        invalidateSelf();
    }
    @Override
    public void setBounds(int left, int top, int right, int bottom)
    {
        super.setBounds(left, top, right, bottom);
        rectF = new RectF(left, top, right, bottom);
    }

    @Override
    public void setAlpha(int alpha) {

    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {

    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    @Override
    public void draw(Canvas canvas)
    {
        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG));
        drawImg(-10f,canvas,mBitmap1,mPaint);
        drawImg(0f,canvas,mBitmap2,mPaint1);
        drawImg(10f,canvas,mBitmap3, mPaint2);
    }


/*    private void initPath(){

        x=ContentUtil.dip2px(App.getInstance(),20.706f);
        y=ContentUtil.dip2px(App.getInstance(),77.274f);
        w=ContentUtil.dip2px(App.getInstance(),80f);

        path=new Path[3];

        path[0]=new Path();
        path[0].moveTo(x,x+y);
        path[0].lineTo(x,x+y-80);
        path[0].lineTo(x+80,x+y+80);
        path[0].lineTo(x+80,x+y);
        path[0].close();

        path[1]=new Path();
        path[1].moveTo(x,x+y);
        path[1].lineTo(x,x+y-80);
        path[1].lineTo(x+80,x+y+80);
        path[1].lineTo(x+80,x+y);
        path[1].close();

        path[0]=new Path();
        path[0].moveTo(x,x+y);
        path[0].lineTo(x,x+y-80);
        path[0].lineTo(x+80,x+y+80);
        path[0].lineTo(x+80,x+y);
        path[0].close();

        for(int i=0;i<3;i++){

        }
    }*/

    private int dp2px(float x){
        return ContentUtil.dip2px(BcnetechAppInstance.getApplicationContext(),x);
    }

    private void drawImg(float x,Canvas canvas,Bitmap bitmap,Paint paint){
        canvas.save();
        canvas.rotate(x,dp2px(20),dp2px(100));
        //paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OVER));
        canvas.drawBitmap(mBitmap, new Rect(0,0,mBitmap.getWidth(),mBitmap.getHeight()),new Rect(dp2px(15),dp2px(15),dp2px(110),dp2px(110)), paint);
        if(bitmap!=null) {
            canvas.drawBitmap(bitmap, new Rect(0, 0, bitmap.getWidth(), bitmap.getWidth()), new Rect(dp2px(19), dp2px(18), dp2px(107), dp2px(106)), paint);
        }

        canvas.restore();
    }
}
