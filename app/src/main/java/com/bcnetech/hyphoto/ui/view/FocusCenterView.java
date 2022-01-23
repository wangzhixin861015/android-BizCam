package com.bcnetech.hyphoto.ui.view;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import com.bcnetech.bcnetechlibrary.util.ContentUtil;
import com.bcnetech.hyphoto.R;

/**
 * @author wsbai
 * @date 2018/9/24
 */
public class FocusCenterView extends AppCompatImageView {
    private Paint paint, paintRadiate;
    //单个圆环的高度
    private static float STROKEWIDTH = 3;
    private static int STROKEPADDING = 5;
    //识别圆环的高度
    private static int STROKEWIDTH_COLOR = 6;
    //圆环组与左右屏幕的间距
    private static int RADIUS_PADDING = 25;
    //初始透明度
    private static int INIT_ALPHA = 180;
    //拍照动画时长
    private static int TAKEPHOTO_DURATION = 8000;
    //放射动画时长
    private static int RADIATE_DURATION = 1000;
    //固定圆半径
    private int inner_radius;
    //与固定圆偏移角度
    private float biasAngle = 0;
    //偏移量
    private float biasLevel = 0;
    //动画圆环半径
    private int radiateRadius = inner_radius/2;
    //固定圆环圆心点
    private PointF pointCenter;
    //多个圆环内切点
    private PointF pointCrisis;
    //放射动画圆环圆心
    private PointF pointRadiate;
    private Bitmap mSrcRect, mDstCircle;
    private Paint maskPaint;
    //点击拍照绘制进度
    private float takePhotoProgress = -90;
    private ValueAnimator radiateAnimator, takePhotoAnimator;
    private boolean isTakePhoto = false;
    //用于画圆弧动画
    private RectF centRectF;

    public FocusCenterView(Context context) {
        super(context);
        initView();
    }

    public FocusCenterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public FocusCenterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        paint = new Paint();
        paint.setColor(getResources().getColor(R.color.focusfade_color));
        paint.setAntiAlias(true);
        paint.setStrokeWidth(STROKEWIDTH);
        paint.setStyle(Paint.Style.STROKE);
        inner_radius = ContentUtil.getScreenWidth(getContext()) / 2 - ContentUtil.dip2px(getContext(), RADIUS_PADDING);
        paintRadiate = new Paint();
        paintRadiate.setAntiAlias(true);
        paintRadiate.setStrokeWidth(STROKEWIDTH);
        paintRadiate.setStyle(Paint.Style.STROKE);
        paintRadiate.setColor(getResources().getColor(R.color.focusanim_color));
        setRadiateAnimator();
    }

    /**
     * 识别图片算法偏差方向返回值
     */
    public void getIdentfyDirect(double angle, int biasLevel) {
        this.biasAngle = (float) angle;
        this.biasLevel = biasLevel;
        if (biasLevel == 0)
            this.biasAngle = 0;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawMask(canvas);
        if (pointCenter == null) {
            pointCenter = new PointF(getWidth() / 2, getHeight() / 2);
            /**中心点的x坐标*/
            centRectF = new RectF(pointCenter.x - inner_radius - RADIUS_PADDING / 2, pointCenter.y - inner_radius - RADIUS_PADDING / 2, pointCenter.x + inner_radius + RADIUS_PADDING / 2, pointCenter.y + inner_radius + RADIUS_PADDING / 2);

        }
        paint.setAlpha(INIT_ALPHA);
        calculatCrisisPoint((float) Math.PI / 2 - biasAngle);
        for (int i = 0; i < 18; i++) {
            paint.setAlpha(paint.getAlpha() - i * paint.getAlpha() / (INIT_ALPHA / 5));
            canvas.drawCircle(getBiasCircleCenter(i).x, getBiasCircleCenter(i).y, inner_radius + i * STROKEPADDING, paint);
        }
        if (pointRadiate == null) {
            pointRadiate = pointCenter;
        }

        if (pointRadiate != null)
            canvas.drawCircle(pointRadiate.x, pointRadiate.y, radiateRadius, paintRadiate);
        if (isTakePhoto) {
            Paint paint = new Paint();
            paint.setColor(Color.WHITE);
            paint.setStrokeWidth(STROKEWIDTH);
            paint.setStyle(Paint.Style.STROKE);
            paint.setAntiAlias(true);
            for (int i = -1; i < 2; i++) {
                centRectF = new RectF(pointCenter.x -(inner_radius + i* STROKEPADDING) - RADIUS_PADDING / 2, pointCenter.y - (inner_radius + i* STROKEPADDING )- RADIUS_PADDING / 2, pointCenter.x + (inner_radius + i* STROKEPADDING) + RADIUS_PADDING / 2, pointCenter.y + (inner_radius + i * STROKEPADDING)+ RADIUS_PADDING / 2);
                canvas.drawArc(centRectF, -90, takePhotoProgress, false, paint);
            }
        }

    }

    /**
     * 画偏移圆
     * 已知两点坐标，求直线方程、距离其中一点距离为L的某点
     */
    private PointF getBiasCircleCenter(int m) {
        PointF mPoint;
        if (pointCenter == null)
            return null;
        // 第一步：求得直线方程相关参数y=kx+b
        double k = (pointCenter.y - pointCrisis.y) * 1.0
                / (pointCenter.x - pointCrisis.x);// 坐标直线斜率k
        double b = pointCenter.y - k * pointCenter.x;// 坐标直线b
        // 第二步：求得在直线y=kx+b上，距离当前坐标距离为L的某点
        // 一元二次方程Ax^2+Bx+C=0中,
        // 一元二次方程求根公式：
        // 两根x1,x2= [-B±√(B^2-4AC)]/2A
        // ①(y-y0)^2+(x-x0)^2=L^2;
        // ②y=kx+b;
        // 由①②表达式得到:(k^2+1)x^2+2[(b-y0)k-x0]x+[(b-y0)^2+x0^2-L^2]=0
        double A = Math.pow(k, 2) + 1;// A=k^2+1;
        double B = 2 * ((b - pointCenter.y) * k - pointCenter.x);// B=2[(b-y0)k-x0];
        // int m = 1;
        double L = m * STROKEPADDING;//距离圆心点偏移距离
        // C=(b-y0)^2+x0^2-L^2
        double C = Math.pow(b - pointCenter.y, 2) + Math.pow(pointCenter.x, 2)
                - Math.pow(L, 2);
        // 两根x1,x2= [-B±√(B^2-4AC)]/2A
        double x1 = (-B + Math.sqrt(Math.pow(B, 2) - 4 * A * C)) / (2 * A);
        double x2 = (-B - Math.sqrt(Math.pow(B, 2) - 4 * A * C)) / (2 * A);
        double x = 0;// 最后确定是在已知两点之间的某点
        if (x1 == x2) {
            x = x1;
        } else if (pointCenter.x <= x1 && x1 <= pointCrisis.x || pointCrisis.x <= x1
                && x1 <= pointCenter.x) {
            x = x1;
        } else if (pointCenter.x <= x2 && x2 <= pointCrisis.x || pointCrisis.x <= x2
                && x2 <= pointCenter.x) {
            x = x2;
        }
        double y = k * x + b;

        if (x != 0 && y != 0) {
            mPoint = new PointF((float) x, (float) y);
        } else {
            mPoint = pointCenter;
        }
        return mPoint;
    }

    public PointF getFocusCenter() {
        return pointCenter;
    }


    /**
     * 创建遮罩层形状
     *
     * @return
     */
    private Bitmap makeSrcRect() {
        Bitmap bm = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvcs = new Canvas(bm);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(getResources().getColor(R.color.trans_backgroud));
        canvcs.drawRect(new RectF(0, 0, getWidth(), getHeight()), paint);
        return bm;
    }

    /**
     * 创建镂空层圆形形状
     *
     * @return
     */
    private Bitmap makeDstCircle() {
        Bitmap bm = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvcs = new Canvas(bm);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.WHITE);
        canvcs.drawCircle(getWidth() / 2, getHeight() / 2, inner_radius, paint);
        return bm;
    }

    /**
     * 绘制镂空遮罩
     *
     * @param canvas
     */
    @SuppressWarnings("WrongConstant")
    private void drawMask(Canvas canvas) {
        if (mSrcRect == null)
            mSrcRect = makeSrcRect();
        if (mDstCircle == null)
            mDstCircle = makeDstCircle();
        if (maskPaint == null)
            maskPaint = new Paint();
        maskPaint.setFilterBitmap(false);
        canvas.saveLayer(0, 0, getWidth(), getHeight(), null, Canvas.ALL_SAVE_FLAG);
        canvas.drawBitmap(mDstCircle, 0, 0, maskPaint);


        maskPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT));
        canvas.drawBitmap(mSrcRect, 0, 0, maskPaint);
    }

    public int getRadiusLength() {
        return inner_radius;
    }

    public int getPadding() {
        return ContentUtil.dip2px(getContext(), RADIUS_PADDING);
    }


    /**
     * 点击拍照动画
     */
    public void startTakePhotoAnim() {
        if (takePhotoAnimator == null) {
            takePhotoAnimator = ValueAnimator.ofInt(0, 360);
            takePhotoAnimator.setDuration(TAKEPHOTO_DURATION);
            takePhotoAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    takePhotoProgress = (int) animation.getAnimatedValue();
                    if (takePhotoProgress == 630)
                        isTakePhoto = false;
                }
            });
        }
        isTakePhoto = true;
        takePhotoAnimator.start();
    }

    public void stopTakePhotoAnim() {
        if (takePhotoAnimator != null)
            takePhotoAnimator.cancel();
        isTakePhoto = false;
    }


    /**
     * 提示放射动画
     */
    private void setRadiateAnimator() {
        if (radiateAnimator == null) {
            radiateAnimator = ValueAnimator.ofInt(0, 9);
            radiateAnimator.setRepeatCount(ValueAnimator.INFINITE);
            radiateAnimator.setRepeatMode(ValueAnimator.REVERSE);
            radiateAnimator.setDuration(RADIATE_DURATION);
            radiateAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    radiateRadius = inner_radius + (int) animation.getAnimatedValue() * STROKEPADDING;
                    pointRadiate = getBiasCircleCenter((int) animation.getAnimatedValue());

                    invalidate();
                }
            });
        }
        if (!radiateAnimator.isRunning())
            radiateAnimator.start();
    }

    /**
     * 计算共切圆的切点
     *
     * @param biasAngle
     */
    private void calculatCrisisPoint(float biasAngle) {
        pointCrisis = new PointF((getWidth() / 2 + inner_radius * transformCoordinates(biasAngle)), getHeight() / 2 - inner_radius * transformCoordinates((float) Math.PI / 2 - biasAngle));
    }

    /**
     * 直角坐标系转换成屏幕坐标系
     *
     * @param biasAngle
     * @return
     */
    private float transformCoordinates(float biasAngle) {
        if (biasAngle != (float) Math.PI / 2) {
            return ((float) Math.sin(biasAngle) > 0 ? Math.abs((float) Math.sin(biasAngle)) : -Math.abs((float) Math.sin(biasAngle)));
        } else {
            return 0;
        }

    }
}
