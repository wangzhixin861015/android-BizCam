package com.bcnetech.hyphoto.listener;

import android.app.Activity;
import android.view.MotionEvent;

import com.bcnetech.bcnetechlibrary.util.ContentUtil;
import com.bcnetech.bcnetechlibrary.view.PickerViewV;

/**
 * Created by wenbin on 2016/11/25.
 */

public class BizGestureListener {
    private final static int INIT = 1;//初始状态
    private final static int UP_DOWN = 2;//上下改变数值类型
    private final static int LEFT_RIGHT = 3;//左右改变图片处理类型

    private final static int PROPAR = 100;//最大比例
    private final static int IMG_TYPE_LIMIT = 90;//左右滑动判断限制临界值
    private final static int TYPE_LIMIT = 15;//类型限制临界值
    public BaseGestureListener baseGestureListener;
    public GestureZoomMoveDetector gestureZoomMoveDetector;
    public GestureUpDownDetector gestureUpDownDetector;
    public GestureLeftRightDetector gestureLeftRightDetector;
    public GestureOnTouchDetector gestureOnTouchDetector;

    private float downY, addNum, downX;
    private int srceenH;
    private Activity activity;
    private int type;

    long firClick;

    public BizGestureListener(Activity activity) {
        this.activity = activity;
        srceenH = ContentUtil.getScreenHeight(activity);
        initData();
    }

    private void initData() {
        type = INIT;
        baseGestureListener = new BaseGestureListener();
        baseGestureListener.setGestureDetector(new BaseGestureListener.GestureDetector() {
            @Override
            public void onDoubleClick() {
                if (gestureZoomMoveDetector != null) {
                    gestureZoomMoveDetector.onDoubleClick();
                }
            }

            @Override
            public void onStartZoomMove(float pointOneX, float pointOneY, float pointTwoX, float pointTwoY) {
                if (gestureZoomMoveDetector != null) {
                    gestureZoomMoveDetector.onStartZoomMove(pointOneX, pointOneY, pointTwoX, pointTwoY);
                }
            }

            @Override
            public void onZoomMove(float pointOneX, float pointOneY, float pointTwoX, float pointTwoY) {
                if (gestureZoomMoveDetector != null) {
                    gestureZoomMoveDetector.onZoomMove(pointOneX, pointOneY, pointTwoX, pointTwoY);
                }
            }

            @Override
            public void onEndZoomMove() {
                if (gestureZoomMoveDetector != null) {
                    gestureZoomMoveDetector.onEndZoomMove();
                }
            }

            @Override
            public void onStartOneMove(float initX, float initY,MotionEvent event) {
                downX = initX;
                downY = initY;
                type = INIT;
                firClick = System.currentTimeMillis();
                if (gestureUpDownDetector != null) {
                    gestureUpDownDetector.onDown(event);
                }


            }


            @Override
            public void onOneMove(float x, float y,MotionEvent event) {
                if (type == INIT && getSqrt(downX, downY, x, y) > TYPE_LIMIT * TYPE_LIMIT) {
                    if (Math.abs(downX - x) > Math.abs(downY - y)) {
                        if (gestureLeftRightDetector != null) {
                            gestureLeftRightDetector.onStartLeftRight();
                        }
                        downX=x;
                        type = LEFT_RIGHT;
                    } else {
                        if (gestureUpDownDetector != null) {
                            gestureUpDownDetector.onStartUpDown();
                            gestureUpDownDetector.onStartUpDown(event);
                        }
                        downY=y;
                        type = UP_DOWN;
                    }
                } else if (type == LEFT_RIGHT) {
                    if (downX - x > IMG_TYPE_LIMIT) {
                        if (gestureLeftRightDetector != null) {
                            gestureLeftRightDetector.onLeftRight(PickerViewV.LEFT_FLING);
                        }
                    } else if (downX - x < -IMG_TYPE_LIMIT) {
                        if (gestureLeftRightDetector != null) {
                            gestureLeftRightDetector.onLeftRight(PickerViewV.RIGHT_FLING);
                        }
                    }
                } else if (type == UP_DOWN) {
                    addNum =  ( downY-y) / srceenH * PROPAR;

//                    LogUtil.d("downY downY"+downY+"yyyyyyy"+y+"addNum"+addNum);
                    if (gestureUpDownDetector != null) {
                        gestureUpDownDetector.onUpDown(addNum);
                        gestureUpDownDetector.onUpDown(event);
                    }
                }else {
                    if(System.currentTimeMillis()-firClick>500){
                        if(null!=gestureLeftRightDetector){
                            gestureLeftRightDetector.onLongClick(event);
                        }

                    }
                }
            }

            @Override
            public void onEndOneMove(MotionEvent event) {
                if (type == LEFT_RIGHT) {
                    if (gestureLeftRightDetector != null) {
                        gestureLeftRightDetector.onEndLeftRight();
                    }
                } else if (type == UP_DOWN) {
                    if (gestureUpDownDetector != null) {
                        gestureUpDownDetector.onEndUpDown();
                        gestureUpDownDetector.onEndUpDown(event);
                    }
                }else {
                    if (gestureLeftRightDetector != null) {
                        if(System.currentTimeMillis()-firClick<500){
                            gestureLeftRightDetector.onClick(event);
                        }
                    }
                }
            }

            @Override
            public void touch(MotionEvent event) {
                if(gestureOnTouchDetector!=null){
                    gestureOnTouchDetector.touch(event);
                }
            }
        });
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (baseGestureListener == null) {
            initData();
        }
        return baseGestureListener.onTouchEvent(event);
    }

    private double getSqrt(double startx, double starty, double endx, double endy) {
        return (startx - endx) * (startx - endx) + (starty - endy) * (starty - endy);
    }

    public void setGestureZoomMoveDetector(GestureZoomMoveDetector gestureZoomMoveDetector) {
        this.gestureZoomMoveDetector = gestureZoomMoveDetector;
    }


    public void setGestureUpDownDetector(GestureUpDownDetector gestureUpDownDetector) {
        this.gestureUpDownDetector = gestureUpDownDetector;
    }


    public void setGestureLeftRightDetector(GestureLeftRightDetector gestureLeftRightDetector) {
        this.gestureLeftRightDetector = gestureLeftRightDetector;
    }

    public GestureOnTouchDetector getGestureOnTouchDetector() {
        return gestureOnTouchDetector;
    }

    public void setGestureOnTouchDetector(GestureOnTouchDetector gestureOnTouchDetector) {
        this.gestureOnTouchDetector = gestureOnTouchDetector;
    }

    public interface GestureZoomMoveDetector {
        void onDoubleClick();

        void onStartZoomMove(float pointOneX, float pointOneY, float pointTwoX, float pointTwoY);

        void onZoomMove(float pointOneX, float pointOneY, float pointTwoX, float pointTwoY);

        void onEndZoomMove();
    }


    public interface GestureUpDownDetector {

        void onDown(MotionEvent motionEvent);

        void onStartUpDown();
        void onStartUpDown(MotionEvent motionEvent);

        void onUpDown(float addNum);

        void onUpDown(MotionEvent motionEvent);

        void onEndUpDown();
        void onEndUpDown(MotionEvent motionEvent);
    }

    public interface GestureLeftRightDetector {

        void onStartLeftRight();

        void onLeftRight(int pickerViewVType);

        void onEndLeftRight();

        void onClick(MotionEvent event);

        void onLongClick(MotionEvent event);


    }

    public interface GestureOnTouchDetector{
        void touch(MotionEvent event);
    }
}
