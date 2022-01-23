package com.bcnetech.hyphoto.listener;

import android.view.MotionEvent;


/**
 * Created by wenbin on 2016/11/25.
 */

public class BaseGestureListener{
    private final static int TYPE_INIT = 1; //初始类型
    private final static int ONE_MOVE=2;//单只手指移动

    private final static int DOUBLE_ZOOM_MOVE = 5;//两只手指放大缩小移动
    private final static int NULL=7;


    private final static int TIME_LIMIT=500;//时间阀值  （毫秒）

    private int type=TYPE_INIT;
    private float downY, downX;
    private int count = 0;//点击次数
    private long firClick, secClick;//点击事件
    private GestureDetector gestureDetector;


    public boolean onTouchEvent(MotionEvent event){
        DoubleClickListener(event);
        OnePaintListener(event);
        ZoomMoveListener(event);
        return true;
    }





    /**
     * 双击事件
     *
     * @param event
     */
    private void DoubleClickListener(MotionEvent event) {
        if (type == TYPE_INIT) {
            int action = event.getAction() & MotionEvent.ACTION_MASK;
            int nCnt = event.getPointerCount();
            if (nCnt > 1) {
                count = 0;
                firClick = 0;
                secClick = 0;
            } else if (action == MotionEvent.ACTION_DOWN) {
                count++;
                if (count == 1) {
                    firClick = System.currentTimeMillis();
                }
            } else if (action == MotionEvent.ACTION_UP) {
                if (count == 2) {
                    secClick = System.currentTimeMillis();
                    if (secClick - firClick < TIME_LIMIT) {
                        //双击事件
                        if (gestureDetector != null) {
                            gestureDetector.onDoubleClick();
                        }
                    }
                    count = 0;
                    firClick = 0;
                    secClick = 0;
                }
            }
        }
    }

    /**
     * 两只手指 放大缩小移动
     *
     * @param event
     */
    private void ZoomMoveListener(MotionEvent event) {
        int action = event.getAction() & MotionEvent.ACTION_MASK;
        int nCnt = event.getPointerCount();
        //最后一个点抬起或者取消，结束所有模式
        gestureDetector.touch(event);
        if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
            if (type == DOUBLE_ZOOM_MOVE) {
                if (gestureDetector != null) {
                    gestureDetector.onEndZoomMove();
                }
            }
            type = TYPE_INIT;
        }//多个手指情况下抬起一个手指,此时需要是缩放模式才触发
        else if (action == MotionEvent.ACTION_POINTER_UP) {
            if (nCnt == 2 && type == DOUBLE_ZOOM_MOVE) {
                type = NULL;
                if (gestureDetector != null) {
                    gestureDetector.onEndZoomMove();
                }
            }
        } //非第一个点按下，关闭滚动模式，开启缩放模式，记录缩放模式的一些初始数据
        else if (action == MotionEvent.ACTION_POINTER_DOWN) {
            if (nCnt == 2) {
                type = DOUBLE_ZOOM_MOVE;
                if (gestureDetector != null) {
                    gestureDetector.onStartZoomMove(event.getX(0), event.getY(0), event.getX(1), event.getY(1));
                }
            }
            if (gestureDetector != null) {
                gestureDetector.onEndOneMove(event);
            }
        } else if (action == MotionEvent.ACTION_MOVE) {
            if (nCnt >= 2 && type == DOUBLE_ZOOM_MOVE) {
                if (gestureDetector != null) {
                    gestureDetector.onZoomMove(event.getX(0), event.getY(0), event.getX(1), event.getY(1));
                }
            }
        }
    }


    /**
     * 单只手指事件
     * @param event
     */
    private void OnePaintListener(MotionEvent event){
        int action = event.getAction() & MotionEvent.ACTION_MASK;
        int nCnt = event.getPointerCount();
        if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
            if (type==ONE_MOVE){
                if (gestureDetector != null) {
                    gestureDetector.onEndOneMove(event);
                }
            }
            type = TYPE_INIT;
        }//第一个点按下，开启滚动模式，记录开始滚动的点
        else if (action == MotionEvent.ACTION_DOWN) {
            if(type == TYPE_INIT){
                downY = event.getY();
                downX = event.getX();
                if (gestureDetector != null) {
                    gestureDetector.onStartOneMove(downX,downY,event);
                    type=ONE_MOVE;
                }
            }
        }
        else if (action == MotionEvent.ACTION_MOVE) {
            if(nCnt == 1 && type == ONE_MOVE){
                if (gestureDetector != null) {
                    gestureDetector.onOneMove(event.getX(),event.getY(),event);
                }
            }
        }


    }


    public void setGestureDetector(GestureDetector gestureDetector) {
        this.gestureDetector = gestureDetector;
    }




    public interface GestureDetector {
        /**
         * 双击操作
         */
        void onDoubleClick();

        /**
         * 开始两只手指缩放移动
         * @param pointOneX
         * @param pointOneY
         * @param pointTwoX
         * @param pointTwoY
         */
        void onStartZoomMove(float pointOneX, float pointOneY, float pointTwoX, float pointTwoY);

        /**
         * 两只手指缩放移动
         * @param pointOneX
         * @param pointOneY
         * @param pointTwoX
         * @param pointTwoY
         */
        void onZoomMove(float pointOneX, float pointOneY, float pointTwoX, float pointTwoY);

        /**
         * 两只手指缩放移动结束
         */
        void onEndZoomMove();


        void onStartOneMove(float initX,float initY,MotionEvent e);
        void onOneMove(float x,float y,MotionEvent e);
        void onEndOneMove(MotionEvent e);

        void touch(MotionEvent event);


    }


}
