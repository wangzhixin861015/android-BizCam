package com.bcnetech.bcnetechlibrary.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by wenbin on 16/6/15.
 */
public class SublineImageView extends ImageView {
//    public final static int RECTANGLE=1;
//    public final static int SQUARE=2;
//    private Paint paintLine;
//    private int type=RECTANGLE;
//    private Paint paintLineBlue;
//
//    public boolean rotation=false;

    public SublineImageView(Context context) {
        super(context);
//        init();
    }

    public SublineImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
//        init();
    }

    public SublineImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
//        init();
    }

//    private void init(){
//        setBlueLine();
//        paintLine=new Paint();
//        paintLine.setColor(Color.WHITE);
//        paintLine.setStrokeWidth(ContentUtil.dip2px(BcnetechAppInstance.getApplicationContext(),2));
//        paintLine.setStyle(Paint.Style.STROKE);
//
//    }
//    @Override
//    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
//        if (rotation) {
//            canvas.drawLine(0,getHeight()/2,getWidth(),getHeight()/2,paintLineBlue);
//            canvas.drawCircle(getWidth()/2,getHeight()/2,getHeight()/2-3,paintLineBlue);
//        }else {
//            canvas.drawLine(0,getHeight()/2,getWidth(),getHeight()/2,paintLine);
//            canvas.drawCircle(getWidth()/2,getHeight()/2,getHeight()/2-3,paintLine);
//        }
//    }
//
//    public void setBlueLine() {
//        paintLineBlue = new Paint();
//        paintLineBlue.setColor(getResources().getColor(R.color.sing_in_color));
//        paintLineBlue.setStrokeWidth(ContentUtil.dip2px(BcnetechAppInstance.getApplicationContext(), 2));
//        paintLineBlue.setStyle(Paint.Style.STROKE);
//    }
}
