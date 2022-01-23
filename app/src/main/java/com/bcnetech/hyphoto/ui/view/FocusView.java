package com.bcnetech.hyphoto.ui.view;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.bcnetech.hyphoto.R;
import com.bcnetech.hyphoto.listener.BizFocusViewGestureListener;

/**
 * Created by a1234 on 17/4/6.
 */

public class FocusView extends AppCompatImageView {
    public static int FOCUSROUNDSIZE = 120;
    /**
     * 最外层圆弧的宽度
     */
    private float borderWidth;
    /**
     * 开始绘制圆弧的角度
     */
    private float startAngle = 0;
    /**
     * 所要绘制的当前步数的红色圆弧终点到起点的夹角
     */
    private float currentAngleLength = 360;
    /**
     * 动画时长
     */
    private int animationLength = 400;
    /**
     * 终点对应的角度和起始点对应的角度的夹角
     */
    private float angleLength = 360;
    /**
     * 最外层圆弧和宽圆弧的间距
     */
    private float marginWidth = 10f;
    private float marginHeight = 10f;
    private float centerX = 0;
    private Paint paintCurrent;
    private boolean canrect = false;
    private float roundangel = 0;
    private boolean hasAnimed = false;//已经进行过长按动画(长按不再进行动画)
    private boolean isAniming = false;//正在进行动画
    private boolean isLongPress = false;//正在长按
    private boolean showLock = false;//是否展示锁
    private RoundArcListener roundArcListener;
    private int saveW, saveH;
    private int circleColor;
    ValueAnimator scaleAnim0;
    ValueAnimator scaleAnim1;
    ValueAnimator vanishAnim;
    AnimatorSet animatorSet;
    AnimatorSet animatorSetLongClick;

    private float pointX, pointY;
    private float currentPaintX, currentPaintY;
    private Rect rect;

    private FouceListener fouceListener;
    private BizFocusViewGestureListener bizFocusViewGestureListener;

    private final static int CLIKC_LIMIT = 40;//按钮范围

    public FocusView(Context context) {
        super(context);
        init();
        onViewClick();
    }

    public FocusView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        onViewClick();
    }

    public void setFouceWH(Rect rect){
        this.rect = rect;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (bizFocusViewGestureListener != null) {
            return bizFocusViewGestureListener.onTouchEvent(event);
        }
        return false;
    }

    /**
     * dp 转换成px
     *
     * @param dip
     * @return
     */

    private int dpToPx(float dip) {
        float density = getContext().getResources().getDisplayMetrics().density;
        return (int) (dip * density + 0.5f * (dip >= 0 ? 1 : -1));
    }

    private void init() {
        setBorderWidth(dpToPx(1));
        initAnim();

        bizFocusViewGestureListener = new BizFocusViewGestureListener(getContext());
        //setMarginWidth(3);
    }

    public void setRoundangel() {
        this.roundangel = 0;
    }


    public void setBorderWidth(float borderWidth) {
        this.borderWidth = borderWidth;
    }

    public void setMarginWidth(float marginWidth) {
        this.marginWidth = marginWidth;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        /**中心点的x坐标*/
       /* centerX = dpToPx(FOCUSROUNDSIZE);*/
        RectF rectF = new RectF(marginWidth, marginHeight, marginWidth + centerX, marginHeight + centerX);
        if (canrect) {//画方形
            drawRect(canvas, rectF);
        } else {
            if (isLongPress) {
                //长按画弧形
                drawRound(canvas, rectF);
            } else {
                //非长按画圆形
                drawCircle(canvas, (int) (marginWidth), (int) (marginHeight));
            }

        }
    }

    public void setShowLock(boolean showLock) {
        this.showLock = showLock;
    }

    /**
     * 点击生成方形
     *
     * @param canrect
     */
    public void setCanrect(boolean canrect) {
        this.canrect = canrect;
        //  invalidate();
        hasAnimed = false;
    }

    /**
     * 点击不消失
     *
     * @param isLongPress
     */
    public void setLongPress(boolean isLongPress) {
        this.isLongPress = isLongPress;
    }

    public void setCenterX(int marginW, int marginH, boolean isLongPress) {
        this.isLongPress = isLongPress;
        boolean saveWOK = false, saveHOK = false;
        if (marginW + dpToPx(FOCUSROUNDSIZE + 40) / 2 <= (saveW + 100) && marginW + dpToPx(FOCUSROUNDSIZE) / 2 >= (saveW - 100)) {
            saveWOK = true;
        }
        if (marginH + dpToPx(FOCUSROUNDSIZE + 40) / 2 <= (saveH + 100) && marginH + dpToPx(FOCUSROUNDSIZE) / 2 >= (saveH - 100)) {
            saveHOK = true;
        }
        if (saveWOK && saveHOK && canrect && !isAniming) {
            setRectAnim();
            this.marginWidth = marginW;
        } else {
            this.marginWidth = marginW;
            this.marginHeight = marginH;
            centerX = dpToPx(FOCUSROUNDSIZE);
            invalidate();
        }
        saveW = marginW;
        saveH = marginH;
    }

    public void initAnim(){
        scaleAnim0 = ValueAnimator.ofFloat(dpToPx(FOCUSROUNDSIZE), dpToPx(FOCUSROUNDSIZE * 1.1f));
        scaleAnim1 = ValueAnimator.ofFloat(dpToPx(FOCUSROUNDSIZE * 1.1f), dpToPx(FOCUSROUNDSIZE));
        vanishAnim = ValueAnimator.ofFloat(getResources().getColor(R.color.white), 0);
        //scaleAnim0.setDuration(400);
        scaleAnim0.setTarget(centerX);
        scaleAnim1.setTarget(centerX);
        scaleAnim1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                centerX = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        scaleAnim0.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                centerX = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        vanishAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                circleColor = (int) animation.getAnimatedValue();
            }
        });
        animatorSet = new AnimatorSet();
        animatorSet.playSequentially(scaleAnim0, scaleAnim1);
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                if (roundArcListener != null) {
                    roundArcListener.onAnimStart();
                }
                isAniming = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                // vanishAnim.start();
                if (roundArcListener != null) {
                    roundArcListener.onAnimEnd();
                }
                isAniming = false;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                if (roundArcListener != null) {
                    roundArcListener.onAnimEnd();
                }
                isAniming = false;
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        animatorSetLongClick = new AnimatorSet();
        animatorSetLongClick.playSequentially(scaleAnim0, scaleAnim1);
        animatorSetLongClick.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                if (roundArcListener != null) {
                    roundArcListener.onAnimStart();
                }
                isAniming = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                // vanishAnim.start();
                if (roundArcListener != null) {
                    roundArcListener.onAnimEnd();
                }
                isAniming = true;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                if (roundArcListener != null) {
                    roundArcListener.onAnimEnd();
                }
                isAniming = true;
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    /**
     * 点击缩小动画
     */
    public void setScaleAnim() {
        cancleAllAnim();
        animatorSet.start();
    }

    /**
     * 长按缩小动画
     */
    public void setLongClickScaleAnim() {
        cancleAllAnim();
        animatorSetLongClick.start();
    }

    private void cancleAllAnim() {
        if (animatorSet != null) {
            if (animatorSet.isRunning() || animatorSet.isStarted()) {
                animatorSet.cancel();
            }
        }
        if (vanishAnim != null) {
            if (vanishAnim.isRunning() || vanishAnim.isStarted()) {
                vanishAnim.cancel();
            }
        }
    }


    /**
     * 2.绘制圆弧
     */
    private void drawRound(Canvas canvas, RectF rectF) {
        paintCurrent = new Paint();
        paintCurrent.setStrokeJoin(Paint.Join.ROUND);
        paintCurrent.setStyle(Paint.Style.STROKE);//设置填充样式
        paintCurrent.setAntiAlias(true);//抗锯齿功能
        paintCurrent.setStrokeWidth(borderWidth);
        paintCurrent.setColor(getResources().getColor(R.color.white));
        canvas.drawArc(rectF, startAngle, currentAngleLength, false, paintCurrent);
    }

    /**
     * 3.绘制圆环
     */
    private void drawCircle(Canvas canvas, int x, int y) {
        paintCurrent = new Paint();
        paintCurrent.setStrokeJoin(Paint.Join.ROUND);
        paintCurrent.setStyle(Paint.Style.STROKE);//设置填充样式
        paintCurrent.setAntiAlias(true);//抗锯齿功能
        paintCurrent.setStrokeWidth(borderWidth);
        circleColor = getResources().getColor(R.color.white);
        paintCurrent.setColor(circleColor);
        if (isAniming) {
            canvas.drawCircle(x, y, centerX / 2, paintCurrent);
        }
    }

    private void drawRect(Canvas canvas, RectF rectF) {
        if (paintCurrent != null) {
          /*  canvas.drawRect(rectF,paintCurrent);*/
            //drawRoundRect:绘制圆角矩形
            canvas.drawRoundRect(rectF, roundangel, roundangel, paintCurrent);
        }
    }

    /**
     * 绘制圆环动画
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void setAnimation() {
        if (!hasAnimed) {
            ValueAnimator progressAnimator = ValueAnimator.ofFloat(0, 360);
            progressAnimator.setDuration(animationLength);
            progressAnimator.setTarget(currentAngleLength);
            progressAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    /**每次要绘制的圆弧角度**/
                    currentAngleLength = (float) animation.getAnimatedValue();

                    invalidate();
                }
            });
            progressAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    isAniming = true;
                    if (roundArcListener != null) {
                        roundArcListener.onAnimStart();
                    }
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    canrect = true;
               /* gradientDrawable = createDrawable(Color.WHITE);
                morphToProgress(dpToPx(240),0);*/
                    setRoundAnimation();
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            if (!isAniming) {
                progressAnimator.start();
            }
        }
    }

    /**
     * 圆变方动画
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void setRoundAnimation() {
        ValueAnimator roundAnim = ValueAnimator.ofFloat(2 * centerX - borderWidth - marginWidth, 0);
        roundAnim.setDuration(animationLength);
        roundAnim.setTarget(roundangel);
        roundAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                /**每次要绘制的圆弧角度**/
                roundangel = (float) animation.getAnimatedValue();

                invalidate();
            }
        });
        roundAnim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                hasAnimed = true;
                isAniming = false;
                showLock = true;
                if (roundArcListener != null) {
                    roundArcListener.onAnimEnd();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        roundAnim.start();
    }

    /**
     * 方变圆动画
     */
    public void setRectAnim() {
        ValueAnimator rectAnim = ValueAnimator.ofFloat(0, 2 * centerX - borderWidth - marginWidth);
        rectAnim.setDuration(250);
        rectAnim.setTarget(roundangel);
        rectAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                /**每次要绘制的圆弧角度**/

                roundangel = (float) animation.getAnimatedValue();

                invalidate();
            }
        });
        rectAnim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                canrect = false;
                hasAnimed = false;
                if (roundArcListener != null) {
                    roundArcListener.onRectAnimEnd();
                }
                setScaleAnim();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        rectAnim.start();
    }

    public interface RoundArcListener {
        void onAnimEnd();

        void onAnimStart();

        void onRectAnimEnd();

    }

    public void setRoundArcListener(RoundArcListener roundArcListener) {
        this.roundArcListener = roundArcListener;
    }


    private void onViewClick(){
        bizFocusViewGestureListener.setBizFoucusViewGestureDetector(new BizFocusViewGestureListener.BizFoucusViewGestureDetector() {
            @Override
            public boolean startLongClickDown(MotionEvent motionEvent,float startX, float startY) {
                if (inCricle(startX, startY)) {
                    pointX = startX;
                    pointY = startY;
                    return true;
                }
                if (fouceListener != null) {
                    fouceListener.onDown(motionEvent);
                }
                return false;
            }

            @Override
            public void move(float x, float y) {
                currentPaintX = x;
                currentPaintY = y;

                float xx=marginWidth +(currentPaintX-pointX);
                float yy=marginHeight+(currentPaintY-pointY);

                pointY=currentPaintY;
                pointX=currentPaintX;


                moveCricleLimit(xx, yy);
            }


            @Override
            public void startClickDown(MotionEvent motionEvent) {
                if (fouceListener != null) {
                    fouceListener.onDown(motionEvent);
                }
            }

            @Override
            public void onStartUpDown(MotionEvent event) {
                if (fouceListener != null) {
                    fouceListener.onStartUpDown(event);
                }
            }

            @Override
            public void onUpDown(MotionEvent event,float addNum) {
                if (fouceListener != null) {
                    fouceListener.onUpDown(event,addNum);
                }
            }

            @Override
            public void onEndUpDown(MotionEvent event) {
                if (fouceListener != null) {
                    fouceListener.onEndUpDown(event);
                }
            }

            @Override
            public void onLongClick(MotionEvent event) {

                if (fouceListener != null) {
                    fouceListener.onLongClick(event);
                }
            }



            @Override
            public void onClick(MotionEvent event) {
                if (fouceListener != null) {
                    fouceListener.onClick(event);

                }
            }

            @Override
            public void endMove(MotionEvent event) {
                if (fouceListener != null) {
                    fouceListener.endMove(event);

                }
            }

            @Override
            public void onLongClickCancel() {
                if (fouceListener != null) {
                    fouceListener.onLongClickCancel();
                }
            }
        });
    }

    /**
     * 判断是否在外圈内
     * @param x
     * @param y
     * @return
     */
    private boolean inCricle(float x, float y) {
        if (Math.abs(x - marginWidth) < FOCUSROUNDSIZE && Math.abs(y - marginHeight) < FOCUSROUNDSIZE) {
            return true;
        }
        return false;
    }

    public void setAniming(boolean animing) {
        isAniming = animing;
        invalidate();
    }

    /**
     * 移动外圈
     * @param x
     * @param y
     */
    private void moveCricleLimit(float x, float y) {
        marginWidth = x;
        marginHeight = y;

        if (x > rect.right) {
            marginWidth = rect.right ;
        }
        if (x  < rect.left) {
            marginWidth = rect.left ;
        }


        if (y  < rect.top) {
            marginHeight = rect.top;
        }

        if (y  > rect.bottom) {
            marginHeight = rect.bottom ;
        }


//        LogUtil.d("r=" + r + ",g=" + g + ",b=" + b);

        invalidate();
    }

    public interface FouceListener {



        void endMove(MotionEvent event);

        void onDown(MotionEvent motionEvent);

        void onStartUpDown(MotionEvent motionEvent);

        void onEndUpDown(MotionEvent motionEvent);

        void onUpDown(MotionEvent motionEvent,float addNum);

        void onClick(MotionEvent event);

        void onLongClick(MotionEvent event);

        void onLongClickCancel();

    }

    public void setFouceListener(FouceListener fouceListener){
        this.fouceListener=fouceListener;
        Log.d("sadas",fouceListener.toString());
    }

}
