package com.bcnetech.bcnetechlibrary.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.bcnetech.bcnetechlibrary.BcnetechAppInstance;
import com.bcnetech.bcnetechlibrary.R;
import com.bcnetech.bcnetechlibrary.util.ContentUtil;

/**
 * Created by wenbin on 16/6/15.
 */
public class LineImageView2 extends ImageView {
    public final static int RECTANGLE = 1;
    public final static int SQUARE = 2;
    private Paint paintLine;
    private Paint paintLine2;
    private Paint paintLineBlue;
    private int type = RECTANGLE;

    public int blueLineWidth = -1;
    public int blueLineHight = -1;

    public boolean rotation=false;

    public LineImageView2(Context context) {
        super(context);
        init();
    }

    public LineImageView2(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LineImageView2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setBlueLine();
        paintLine = new Paint();
        paintLine.setColor(getResources().getColor(R.color.color_white40));
        paintLine2 = new Paint();
        paintLine2.setColor(Color.WHITE);
        paintLine.setStrokeWidth(ContentUtil.dip2px(BcnetechAppInstance.getApplicationContext(), 7));
        paintLine2.setStrokeWidth(ContentUtil.dip2px(BcnetechAppInstance.getApplicationContext(), 7));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

//        if(rotation&&Math.abs(Math.abs(blueLineHight)-getHeight()/2)<=5){
//            canvas.drawLine(0, 0, getWidth(), 0, paintLineBlue);
//            canvas.drawLine(0, getHeight(), getWidth(), getHeight(), paintLineBlue);
//            //画垂直线
//            canvas.drawLine(0, 0, 0, getHeight(), paintLineBlue);
//            canvas.drawLine(getWidth(), 0, getWidth(), getHeight(), paintLineBlue);
//        }else {
//            canvas.drawLine(0, 0, getWidth(), 0, paintLine);
//            canvas.drawLine(0, getHeight(), getWidth(), getHeight(), paintLine);
//            //画垂直线
//            canvas.drawLine(0, 0, 0, getHeight(), paintLine);
//            canvas.drawLine(getWidth(), 0, getWidth(), getHeight(), paintLine);
//        }
//
//        //中间白线
//        if(Math.abs(Math.abs(blueLineHight)-getHeight()/2)<=5){
//            canvas.drawLine(0, getHeight() / 2, getWidth() / 4, getHeight() / 2, paintLineBlue);
//            canvas.drawLine(3 * getWidth() / 4, getHeight() / 2, getWidth(), getHeight() / 2, paintLineBlue);
//        }else {
//            canvas.drawLine(0, getHeight() / 2, getWidth() / 4, getHeight() / 2, paintLine2);
//            canvas.drawLine(3 * getWidth() / 4, getHeight() / 2, getWidth(), getHeight() / 2, paintLine2);
//        }
//
//        //移动的蓝线
//        if (blueLineHight < 0) {
//            if(Math.abs(Math.abs(blueLineHight)-getHeight()/2)<=5){
//                canvas.drawLine(1, getHeight() / 2, getWidth() / 4, getHeight() / 2, paintLineBlue);
//                canvas.drawLine(3 * getWidth() / 4, getHeight() / 2, getWidth(), getHeight() / 2, paintLineBlue);
//            }else {
//                canvas.drawLine(1, -blueLineHight, getWidth() / 4, -blueLineHight, paintLineBlue);
//                canvas.drawLine(3 * getWidth() / 4 - 1, -blueLineHight, getWidth(), -blueLineHight, paintLineBlue);
//            }
//
//        } else {
//            if(Math.abs(Math.abs(blueLineHight)-getHeight()/2)<=5){
//                canvas.drawLine(1, getHeight() / 2, getWidth() / 4, getHeight() / 2, paintLineBlue);
//                canvas.drawLine(3 * getWidth() / 4, getHeight() / 2, getWidth(), getHeight() / 2, paintLineBlue);
//            }else {
//                canvas.drawLine(1, blueLineHight, getWidth() / 4, blueLineHight, paintLineBlue);
//                canvas.drawLine(3 * getWidth() / 4 - 1, blueLineHight, getWidth(), blueLineHight, paintLineBlue);
//            }
//        }

        canvas.drawLine(0, getHeight() / 2, ContentUtil.dip2px(BcnetechAppInstance.getApplicationContext(), 7), getHeight() / 2, paintLine2);
        canvas.drawLine(getWidth() - ContentUtil.dip2px(BcnetechAppInstance.getApplicationContext(), 7), getHeight() / 2, getWidth(), getHeight() / 2, paintLine2);


        //移动的蓝线
        if (blueLineHight < 0) {
            if (Math.abs(Math.abs(blueLineHight) - getHeight() / 2) <= 3) {
                canvas.drawLine(0, getHeight() / 2, ContentUtil.dip2px(BcnetechAppInstance.getApplicationContext(), 20), getHeight() / 2, paintLineBlue);
                canvas.drawLine(getWidth() - ContentUtil.dip2px(BcnetechAppInstance.getApplicationContext(), 20), getHeight() / 2, getWidth(), getHeight() / 2, paintLineBlue);

//                canvas.drawLine(1, getHeight() / 2, getWidth() / 4, getHeight() / 2, paintLineBlue);
//                canvas.drawLine(3 * getWidth() / 4, getHeight() / 2, getWidth(), getHeight() / 2, paintLineBlue);
            } else {
                canvas.drawLine(0, -blueLineHight, ContentUtil.dip2px(BcnetechAppInstance.getApplicationContext(), 20), -blueLineHight, paintLine);
                canvas.drawLine(getWidth() - ContentUtil.dip2px(BcnetechAppInstance.getApplicationContext(), 20), -blueLineHight, getWidth(), -blueLineHight, paintLine);
            }

        } else {
            if (Math.abs(Math.abs(blueLineHight) - getHeight() / 2) <= 3) {

                canvas.drawLine(0, getHeight() / 2, ContentUtil.dip2px(BcnetechAppInstance.getApplicationContext(), 20), getHeight() / 2, paintLineBlue);
                canvas.drawLine(getWidth() - ContentUtil.dip2px(BcnetechAppInstance.getApplicationContext(), 20), getHeight() / 2, getWidth(), getHeight() / 2, paintLineBlue);

//                canvas.drawLine(0, getHeight() / 2, getWidth() / 4, getHeight() / 2, paintLineBlue);
//                canvas.drawLine(3 * getWidth() / 4, getHeight() / 2, getWidth(), getHeight() / 2, paintLineBlue);
            } else {

                canvas.drawLine(0, blueLineHight, ContentUtil.dip2px(BcnetechAppInstance.getApplicationContext(), 20), blueLineHight, paintLine);
                canvas.drawLine(getWidth() - ContentUtil.dip2px(BcnetechAppInstance.getApplicationContext(), 20), blueLineHight, getWidth(), blueLineHight, paintLine);
            }
        }
    }

    public void setBlueLine() {
        paintLineBlue = new Paint();
        paintLineBlue.setColor(getResources().getColor(R.color.sing_in_color));
        paintLineBlue.setStrokeWidth(ContentUtil.dip2px(BcnetechAppInstance.getApplicationContext(), 7));
    }
}
