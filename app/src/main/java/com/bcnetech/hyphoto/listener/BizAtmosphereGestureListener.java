package com.bcnetech.hyphoto.listener;

import android.content.Context;
import android.view.MotionEvent;

import com.bcnetech.bcnetechlibrary.util.ContentUtil;

/**
 * Created by wenbin on 2016/12/16.
 */

public class BizAtmosphereGestureListener {
    public BaseGestureListener baseGestureListener;
    private BizAtmosphereGestureDetector bizAtmosphereGestureDetector;

    private final static int PROPAR=100;//最大比例
    private Context context;
    private boolean canUpDow;
    private float downY, addNum, downX;
    private int srceenH;
    public BizAtmosphereGestureListener(Context context){

        this.context=context;
        srceenH = ContentUtil.getScreenHeight(context);

        initData();
    }


    private void initData(){
        baseGestureListener=new BaseGestureListener();
        baseGestureListener.setGestureDetector(new BaseGestureListener.GestureDetector() {
            @Override
            public void onDoubleClick() {

            }

            @Override
            public void onStartZoomMove(float pointOneX, float pointOneY, float pointTwoX, float pointTwoY) {
                if(bizAtmosphereGestureDetector!=null) {
                    bizAtmosphereGestureDetector.startZoom(getDistance(pointOneX, pointOneY, pointTwoX, pointTwoY));
                }
            }

            @Override
            public void onZoomMove(float pointOneX, float pointOneY, float pointTwoX, float pointTwoY) {
                if(bizAtmosphereGestureDetector!=null) {
                    bizAtmosphereGestureDetector.zoom(getDistance(pointOneX, pointOneY, pointTwoX, pointTwoY));
                }
            }

            @Override
            public void onEndZoomMove() {
                if(bizAtmosphereGestureDetector!=null) {
                    bizAtmosphereGestureDetector.endZoom();
                }
            }

            @Override
            public void onStartOneMove(float initX, float initY,MotionEvent event) {
                downX=initX;
                downY=initY;
                if(bizAtmosphereGestureDetector!=null) {
                    canUpDow=bizAtmosphereGestureDetector.startMove(initX,initY);
                }
            }


            @Override
            public void onOneMove(float x, float y,MotionEvent event) {

                if(canUpDow) {
                    if (bizAtmosphereGestureDetector != null) {
                        bizAtmosphereGestureDetector.move(x, y);
                    }
                }
                else{
                    addNum =  (downY - y) / srceenH * PROPAR;
                    if (bizAtmosphereGestureDetector != null) {
                        bizAtmosphereGestureDetector.upDowm(addNum);
                    }
                }
            }

            @Override
            public void onEndOneMove(MotionEvent event) {
                if(bizAtmosphereGestureDetector!=null) {
                    bizAtmosphereGestureDetector.endMove();
                }
            }

            @Override
            public void touch(MotionEvent event) {
                if(bizAtmosphereGestureDetector!=null) {
                    bizAtmosphereGestureDetector.onTouch(event);
                }
            }
        });


    }



    /**
     * 获取两点之间距离
     *
     * @param x1 点1
     * @param y1 点1
     * @param x2 点2
     * @param y2 点2
     * @return 距离
     */
    private float getDistance(float x1, float y1, float x2, float y2) {
        float x = x1 - x2;
        float y = y1 - y2;
        return (float) Math.sqrt(x * x + y * y);
    }
    public boolean onTouchEvent(MotionEvent event){
        if(baseGestureListener==null) {
            initData();
        }
        return baseGestureListener.onTouchEvent(event);
    }
    public void setBizAtmosphereGestureDetector(BizAtmosphereGestureDetector bizAtmosphereGestureDetector) {
        this.bizAtmosphereGestureDetector = bizAtmosphereGestureDetector;
    }

    public interface BizAtmosphereGestureDetector{
        void startZoom(float distance);
        void zoom(float distance);
        void endZoom();

        boolean startMove(float startX, float startY);
        void move(float x, float y);
        void endMove();
        void upDowm(float addNum);

        void onTouch(MotionEvent event);
    }

}
