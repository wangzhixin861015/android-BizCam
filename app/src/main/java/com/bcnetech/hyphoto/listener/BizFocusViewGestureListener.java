package com.bcnetech.hyphoto.listener;

import android.content.Context;
import android.view.MotionEvent;

import com.bcnetech.bcnetechlibrary.util.ContentUtil;

/**
 * Created by wenbin on 2016/12/16.
 */

public class BizFocusViewGestureListener {

    private final static int INIT = 1;//初始状态
    private final static int UP_DOWN = 2;//上下改变数值类型


    public BaseGestureListener baseGestureListener;
    private BizFoucusViewGestureDetector bizFoucusViewGestureDetector;

    private final static int PROPAR = 100;//最大比例
    private final static int TYPE_LIMIT = 15;//类型限制临界值
    private Context context;
    private boolean inCricle;
    private float downY, addNum, downX;
    private int srceenH;


    private int type;

    long firClick;
    private boolean isLongClick = false;


    public BizFocusViewGestureListener(Context context) {

        this.context = context;
        srceenH = ContentUtil.getScreenHeight(context);

        initData();
    }


    private void initData() {
        baseGestureListener = new BaseGestureListener();
        baseGestureListener.setGestureDetector(new BaseGestureListener.GestureDetector() {
            @Override
            public void onDoubleClick() {

            }

            @Override
            public void onStartZoomMove(float pointOneX, float pointOneY, float pointTwoX, float pointTwoY) {
            }

            @Override
            public void onZoomMove(float pointOneX, float pointOneY, float pointTwoX, float pointTwoY) {
            }

            @Override
            public void onEndZoomMove() {
//
            }

            @Override
            public void onStartOneMove(float initX, float initY, MotionEvent event) {
                downX = initX;
                downY = initY;
                type = INIT;
                firClick = System.currentTimeMillis();

                //拖动对焦模式下
                if (isLongClick) {
                    if (bizFoucusViewGestureDetector != null) {
                        //判断点击位置是否在圈内
                        inCricle = bizFoucusViewGestureDetector.startLongClickDown(event, initX, initY);
                    }
                } else {
                    bizFoucusViewGestureDetector.startClickDown(event);
                }
            }


            @Override
            public void onOneMove(float x, float y, MotionEvent event) {

                //拖动对焦模式下
                if (isLongClick) {
                    if (!inCricle) {
                        if (type == INIT && getSqrt(downX, downY, x, y) > TYPE_LIMIT * TYPE_LIMIT) {
                            if (Math.abs(downX - x) < Math.abs(downY - y)) {
                                if (bizFoucusViewGestureDetector != null) {
                                    bizFoucusViewGestureDetector.onStartUpDown(event);
                                }
                                downY = y;
                                type = UP_DOWN;
                            }
                        } else if (type == UP_DOWN) {
                            addNum =  ( downY - y) / srceenH * PROPAR;
                            if (bizFoucusViewGestureDetector != null) {
                                bizFoucusViewGestureDetector.onUpDown(event,addNum);
                            }
                        } else {
                            if (System.currentTimeMillis() - firClick > 300) {
                                if (null != bizFoucusViewGestureDetector) {
                                    bizFoucusViewGestureDetector.onLongClickCancel();
                                }
                            }
                        }
                    } else {
                        if (bizFoucusViewGestureDetector != null) {
                            bizFoucusViewGestureDetector.move(x, y);
                        }
                    }
                } else {
                    if (type == INIT && getSqrt(downX, downY, x, y) > TYPE_LIMIT * TYPE_LIMIT) {
                        if (Math.abs(downX - x) < Math.abs(downY - y)) {
                            if (bizFoucusViewGestureDetector != null) {
                                bizFoucusViewGestureDetector.onStartUpDown(event);
                            }
                            downY = y;
                            type = UP_DOWN;
                        }
                    } else if (type == UP_DOWN) {
                        addNum =  ( downY - y) / srceenH * PROPAR;
                        if (bizFoucusViewGestureDetector != null) {
                            bizFoucusViewGestureDetector.onUpDown(event,addNum);
                        }
                    } else {
                        if (System.currentTimeMillis() - firClick > 300) {
                            if (null != bizFoucusViewGestureDetector) {
                                bizFoucusViewGestureDetector.onLongClick(event);
                            }
                        }
                    }
                }

            }

            @Override
            public void onEndOneMove(MotionEvent event) {
                //拖动对焦模式下
                if(isLongClick){
                    //上下滑动
                    if (type == UP_DOWN) {
                        if (bizFoucusViewGestureDetector != null) {
                            bizFoucusViewGestureDetector.onEndUpDown(event);
                        }
                    } else {
                        //在对焦圈内
                        if(inCricle){
                            if (bizFoucusViewGestureDetector != null) {
                                bizFoucusViewGestureDetector.endMove(event);
                            }
                        }else {
                            if (bizFoucusViewGestureDetector != null) {
                                if (System.currentTimeMillis() - firClick < 300) {
                                    bizFoucusViewGestureDetector.onLongClick(event);
                                }else {
                                    isLongClick=false;
                                }
                            }
                        }
                    }
                }else {
                    if (type == UP_DOWN) {
                        if (bizFoucusViewGestureDetector != null) {
                            bizFoucusViewGestureDetector.onEndUpDown(event);
                        }
                    } else {
                        if (bizFoucusViewGestureDetector != null) {
                            if (System.currentTimeMillis() - firClick < 300) {
                                bizFoucusViewGestureDetector.onClick(event);
                            }else {
                                isLongClick=true;
                            }
                        }
                    }
                }


            }

            @Override
            public void touch(MotionEvent event) {
            }
        });


    }

    private double getSqrt(double startx, double starty, double endx, double endy) {
        return (startx - endx) * (startx - endx) + (starty - endy) * (starty - endy);
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

    public boolean onTouchEvent(MotionEvent event) {
        if (baseGestureListener == null) {
            initData();
        }
        return baseGestureListener.onTouchEvent(event);
    }

    public void setBizFoucusViewGestureDetector(BizFoucusViewGestureDetector bizAtmosphereGestureDetector) {
        this.bizFoucusViewGestureDetector = bizAtmosphereGestureDetector;
    }

    public interface BizFoucusViewGestureDetector {


        boolean startLongClickDown(MotionEvent motionEvent, float initX, float initY);

        void move(float x, float y);

        void startClickDown(MotionEvent motionEvent);

        void onStartUpDown(MotionEvent event);

        void onUpDown(MotionEvent event,float addNum);

        void onEndUpDown(MotionEvent event);

        void onClick(MotionEvent event);

        void onLongClick(MotionEvent event);

        void endMove(MotionEvent event);

        void onLongClickCancel();

    }

}
