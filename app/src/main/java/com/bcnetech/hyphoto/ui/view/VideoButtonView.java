package com.bcnetech.hyphoto.ui.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import com.bcnetech.bcnetechlibrary.util.AnimFactory;
import com.bcnetech.bcnetechlibrary.util.ContentUtil;

/**
 * Created by a1234 on 2017/11/24.
 */
public class VideoButtonView extends AppCompatImageView {
    private final static float CRICLE = 360;
    private final static int TEXT_TIME = 4;
    private final static float STROKE_WIDTH = 10;
    private final static float START_ARC = -90;
    private final static float END_ARC = -360;

    private int textTime;
    private Paint paint;
    private Handler handler;
    private Runnable runnable;
    private boolean isRunning;
    private long time;
    private float startArc, endArc;
    private float w, h, r;
    private RectF rectF;

    private ValueAnimator animatorArc;
    private Paint.FontMetricsInt fontMetrics;
    private Paint paintText, paintCricleWithe;
    private VideoButtonInter videoButtonInter;

    public VideoButtonView(Context context) {
        super(context);
        initView();
        initData();
        onViewClick();
    }

    public VideoButtonView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
        initData();
        onViewClick();
    }

    public VideoButtonView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
        initData();
        onViewClick();
    }

    public void initView() {
        paint = new Paint();
    }

    public void initData() {
        isRunning = false;
        handler = new Handler();
        initCricleWithe();
        initText();
    }


    public void onViewClick() {

    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (isRunning) {
            drawCricleWithe(canvas);
        } else {
            if (textTime != 0) {
                drawText(canvas);
            }
        }
        super.onDraw(canvas);
    }

    private void drawText(Canvas canvas) {
        canvas.drawText(textTime == TEXT_TIME ? "3" : textTime + "", w / 2, (h - fontMetrics.top - fontMetrics.bottom) / 2, paintText);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (w != getMeasuredWidth() || h != getMeasuredHeight()) {
            w = getMeasuredWidth();
            h = getMeasuredHeight();
            if (w < h) {
                r = w / 2 - STROKE_WIDTH / 2;
            } else {
                r = h / 2 - STROKE_WIDTH / 2;
            }
            rectF = new RectF(w / 2 - r + STROKE_WIDTH, h / 2 - r + STROKE_WIDTH, w / 2 + r - STROKE_WIDTH, h / 2 + r - STROKE_WIDTH);
        }
    }

    private void initText() {
        this.paintText = new Paint();
        this.paintText.setAntiAlias(true);
        this.paintText.setTextSize(ContentUtil.dip2px(getContext(), 40));
        this.paintText.setColor(Color.WHITE);
        paintText.setAntiAlias(true);
        paintText.setTextAlign(Paint.Align.CENTER);
        fontMetrics = paintText.getFontMetricsInt();
    }

    private void initCricleWithe() {
        this.paintCricleWithe = new Paint();
        this.paintCricleWithe.setAntiAlias(true);
        this.paintCricleWithe.setColor(Color.WHITE);
        this.paintCricleWithe.setAntiAlias(true);
        startArc = START_ARC;
        endArc = END_ARC;
    }

    private void drawCricleWithe(Canvas canvas) {
        canvas.drawArc(rectF, startArc, endArc, true, paintCricleWithe);
    }

    private void startTextAnim() {
        textTime = TEXT_TIME;
        if (runnable == null || textTime != 0) {
            videoButtonInter.startVideo();
            runnable = new Runnable() {
                @Override
                public void run() {
                    textTime--;
                    if (textTime <= 0) {
                        isRunning = true;
                        animatorArc.start();
                        videoButtonInter.CountdownFin();
                        handler.removeCallbacks(runnable);
                        return;
                    }
                    invalidate();
                    handler.postDelayed(this, sToms(1));
                }
            };
        }
        handler.removeCallbacks(runnable);
        handler.post(runnable);
    }

    private void initArcAnim(long timeType) {
        if (animatorArc == null || timeType != this.time) {
            this.time = timeType;
            animatorArc = AnimFactory.rotationCricleAnim(new AnimFactory.FloatListener() {
                @Override
                public void floatValueChang(float f) {
                    endArc = f * CRICLE + END_ARC;
                    invalidate();
                }
            }, sToms(timeType) + 100);
            animatorArc.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    cencelAnim();
                    videoButtonInter.videoFin();

                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        }
    }

    /**
     * 秒转毫秒
     *
     * @return
     */
    private long sToms(long time) {
        return time * 1000;
    }

    public void startArcAnim(long timeType) {
        cencelAnim();
        initArcAnim(timeType);
        startTextAnim();
    }

    public void resetArcAnim() {
        handler.removeCallbacks(runnable);
        if (animatorArc != null && animatorArc.isRunning()) {
            animatorArc.removeAllListeners();
            animatorArc.cancel();
        }
        isRunning = false;
    }

    public void cencelAnim() {
        if (animatorArc != null && animatorArc.isRunning()) {
            animatorArc.cancel();
        }
        isRunning = false;
        invalidate();
    }

    public interface VideoButtonInter {
        void startVideo();

        void CountdownFin();

        void videoFin();

    }

    public void setVideoButtonInter(VideoButtonInter videoButtonInter) {
        this.videoButtonInter = videoButtonInter;
    }

    public void Pause() {
        if (animatorArc != null && animatorArc.isRunning()) {
            animatorArc.pause();
        }
    }

    public void Resume() {
        if (animatorArc != null && animatorArc.isPaused()) {
            animatorArc.resume();
        }
    }

}
