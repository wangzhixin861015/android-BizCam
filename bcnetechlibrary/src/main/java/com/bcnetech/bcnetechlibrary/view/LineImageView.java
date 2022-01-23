package com.bcnetech.bcnetechlibrary.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.bcnetech.bcnetechlibrary.BcnetechAppInstance;
import com.bcnetech.bcnetechlibrary.util.ContentUtil;

/**
 * Created by wenbin on 16/6/15.
 */
public class LineImageView extends ImageView {
    public final static int RECTANGLE=1;
    public final static int SQUARE=2;
    private Paint paintLine;
    private int type=RECTANGLE;
    public LineImageView(Context context) {
        super(context);
        init();
    }

    public LineImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LineImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        paintLine=new Paint();
        paintLine.setColor(Color.WHITE);
        paintLine.setStrokeWidth(ContentUtil.dip2px(BcnetechAppInstance.getApplicationContext(),1));


    }
    @Override
    protected void onDraw(Canvas canvas) {
        if(type==RECTANGLE) {
            drawLayout(canvas);
        }
        else if(type==SQUARE){
            drawSquare(canvas);
        }
    }

    private void drawLayout(Canvas canvas){

        //画水平线
        float lineh=getHeight()/3;
        float linew=getWidth()/3;
        canvas.drawLine(0,0,getWidth(),0,paintLine);
        canvas.drawLine(0,lineh,getWidth(),lineh,paintLine);
        canvas.drawLine(0,2*lineh,getWidth(),2*lineh,paintLine);
        canvas.drawLine(0,getHeight(),getWidth(),getHeight(),paintLine);
        //画垂直线
        canvas.drawLine(0,0,0,getHeight(),paintLine);
        canvas.drawLine(linew,0,linew,getHeight(),paintLine);
        canvas.drawLine(2*linew,0,2*linew,getHeight(),paintLine);
        canvas.drawLine(getWidth(),0,getWidth(),getHeight(),paintLine);

    }
    private void drawSquare(Canvas canvas){
        float w=Math.min(getWidth(),getHeight());
        float startx=(getWidth()-w)/2;
        float starty=(getHeight()-w)/2;
        float lineh=w/3;
        float linew=w/3;
        canvas.drawLine(startx,starty,startx+w,starty,paintLine);
        canvas.drawLine(startx,starty+lineh,startx+w,starty+lineh,paintLine);
        canvas.drawLine(startx,starty+lineh*2,startx+w,starty+lineh*2,paintLine);
        canvas.drawLine(startx,starty+lineh*3,startx+w,starty+lineh*3,paintLine);

        canvas.drawLine(startx,starty,startx,starty+w,paintLine);
        canvas.drawLine(startx+linew,starty,startx+linew,starty+w,paintLine);
        canvas.drawLine(startx+linew*2,starty,startx+linew*2,starty+w,paintLine);
        canvas.drawLine(startx+linew*3,starty,startx+linew*3,starty+w,paintLine);
    }

    public void setType(int type){
        this.type=type;
    }

}
