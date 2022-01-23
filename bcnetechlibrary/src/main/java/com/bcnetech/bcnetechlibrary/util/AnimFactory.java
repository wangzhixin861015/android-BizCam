package com.bcnetech.bcnetechlibrary.util;

import android.animation.ValueAnimator;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * Created by wenbin on 16/5/5.
 */
public class AnimFactory {

    public static ValueAnimator BottomInAnim(final View view) {
        ValueAnimator inAnimation = ValueAnimator.ofFloat(0, 1);
        inAnimation.setDuration(250);
        inAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float f = (Float) animation.getAnimatedValue();
                view.setAlpha(f);
                view.setTranslationY(((1 - f) * (400)));
            }
        });
        return inAnimation;
    }

    public static ValueAnimator BottomOutAnim(final View view) {
        ValueAnimator outAnimation = ValueAnimator.ofFloat(1, 0);
        outAnimation.setDuration(250);
        outAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float f = (Float) animation.getAnimatedValue();
                view.setAlpha(f);
                view.setTranslationY(400 * (1 - f));
            }
        });
        return outAnimation;
    }

    public static ValueAnimator BottomInAnim(final View view, final int y) {
        ValueAnimator inAnimation = ValueAnimator.ofFloat(0, 1);
        inAnimation.setDuration(250);
        inAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float f = (Float) animation.getAnimatedValue();
                view.setAlpha(f);
                view.setTranslationY(((1 - f) * (y)));
            }
        });
        return inAnimation;
    }

    public static ValueAnimator BottomOutAnim(final View view, final int y) {
        ValueAnimator outAnimation = ValueAnimator.ofFloat(1, 0);
        outAnimation.setDuration(250);
        outAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float f = (Float) animation.getAnimatedValue();
                view.setAlpha(f);
                view.setTranslationY(y * (1 - f));
            }
        });
        return outAnimation;
    }


    public static ValueAnimator ImgAlphaAnim(final View view) {
        ValueAnimator outAnimation = ValueAnimator.ofFloat(0, 1);
        outAnimation.setDuration(250);
        outAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float f = (Float) animation.getAnimatedValue();
                view.setAlpha(f);
            }
        });
        return outAnimation;
    }

    public static ValueAnimator ImgAlphaAnimOut(final View view) {
        ValueAnimator outAnimation = ValueAnimator.ofFloat(1, 0);
        outAnimation.setDuration(250);
        outAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float f = (Float) animation.getAnimatedValue();
                view.setAlpha(f);
            }
        });
        return outAnimation;

    }

    public static ValueAnimator TitleInAnim(final View view, final View view2) {
        ValueAnimator inAnimation = ValueAnimator.ofFloat(1, 0);
        inAnimation.setDuration(250);
        inAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float f = (Float) animation.getAnimatedValue();
                view.setTranslationY(view.getY() * f);
                view2.setTranslationY(view.getY() * f + view.getHeight());
                view2.setLayoutParams(new RelativeLayout.LayoutParams(ContentUtil.getScreenWidth(view.getContext()), (int) (ContentUtil.getScreenHeight(view.getContext()) - (view.getY() * f + view.getHeight()))));
            }
        });
        return inAnimation;
    }

    public static ValueAnimator TitleOutAnim(final View view, final View view2) {
        ValueAnimator inAnimation = ValueAnimator.ofFloat(1, 0);
        inAnimation.setDuration(250);
        inAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float f = (Float) animation.getAnimatedValue();
                view.setTranslationY(-view.getHeight() + ((view.getHeight() + view.getY()) * f));
                view2.setTranslationY((view.getHeight() + view.getY()) * f);
                view2.setLayoutParams(new RelativeLayout.LayoutParams(ContentUtil.getScreenWidth(view.getContext()), (int) (ContentUtil.getScreenHeight(view.getContext()) - ((view.getHeight() + view.getY()) * f))));
            }
        });
        return inAnimation;
    }

    public static ValueAnimator BigImgShow(final View view, final float x, final float y) {
        ValueAnimator inAnimation = ValueAnimator.ofFloat(0, 1);
        inAnimation.setDuration(150);
        inAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float f = (Float) animation.getAnimatedValue();
                view.setAlpha(f);
                view.setTranslationY(y - y * f);
                view.setTranslationX(x - x * f);
                view.setLayoutParams(new RelativeLayout.LayoutParams((int) (ContentUtil.getScreenWidth(view.getContext()) * f), (int) (ContentUtil.getScreenHeight2(view.getContext()) * f)));

            }
        });
        return inAnimation;
    }

    public static ValueAnimator BigImgClose(final View view, final float x, final float y) {
        ValueAnimator inAnimation = ValueAnimator.ofFloat(1, 0);
        inAnimation.setDuration(150);
        inAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float f = (Float) animation.getAnimatedValue();
                view.setAlpha(f);
                view.setTranslationY(y - y * f);
                view.setTranslationX(x - x * f);
                view.setLayoutParams(new RelativeLayout.LayoutParams((int) (ContentUtil.getScreenWidth(view.getContext()) * f), (int) (ContentUtil.getScreenHeight(view.getContext()) * f)));
                //

            }
        });
        return inAnimation;
    }

    public static ValueAnimator logoTrRec(final View view, final float startx, final float endx, final float starty, final float endy,
                                          final float startW, final float endW, final float startH, final float endH) {
        ValueAnimator inAnimation = ValueAnimator.ofFloat(0, 1);
        inAnimation.setDuration(250);
        inAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float f = (Float) animation.getAnimatedValue();
                view.setTranslationX(startx + (endx - startx) * f);
                view.setTranslationY(starty + (endy - starty) * f);
                RelativeLayout.LayoutParams parms = new RelativeLayout.LayoutParams((int) (startW + (endW - startW) * f), (int) (startH + (endH - startH) * f));
                view.setLayoutParams(parms);
            }
        });
        return inAnimation;
    }

    public static ValueAnimator transY(final View view, final float starty, final float endy) {
        ValueAnimator inAnimation = ValueAnimator.ofFloat(0, 1);
        inAnimation.setDuration(250);
        inAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float f = (Float) animation.getAnimatedValue();
                view.setTranslationY((endy - starty) * f);
            }
        });
        return inAnimation;
    }

    public static ValueAnimator anticlockwiseAnim(final View view, float x) {
        ValueAnimator inAnimation = ValueAnimator.ofFloat(0, -x);
        inAnimation.setDuration(250);
        inAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float f = (Float) animation.getAnimatedValue();
                view.setRotation(f);
            }
        });
        return inAnimation;
    }

    public static ValueAnimator rotationClockwiseAnim(final View view, float x) {
        ValueAnimator inAnimation = ValueAnimator.ofFloat(0, x);
        inAnimation.setDuration(250);
        inAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float f = (Float) animation.getAnimatedValue();
                view.setRotation(f);
            }
        });
        return inAnimation;
    }

    public static ValueAnimator tranXLeftAnim(final View view, final float x) {
        ValueAnimator inAnimation = ValueAnimator.ofFloat(1, 0);
        inAnimation.setDuration(150);
        inAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float f = (Float) animation.getAnimatedValue();
                view.setAlpha(f);
                view.setTranslationX(x - x * f);

            }
        });
        return inAnimation;
    }

    public static ValueAnimator tranXRightAnim(final View view, final float x) {
        ValueAnimator inAnimation = ValueAnimator.ofFloat(0, 1);
        inAnimation.setDuration(150);
        inAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float f = (Float) animation.getAnimatedValue();
                view.setAlpha(f);
                view.setTranslationX(x - x * f);

            }
        });
        return inAnimation;
    }

    public static ValueAnimator tranYTopInAnim(final View view, final float y) {
        ValueAnimator inAnimation = ValueAnimator.ofFloat(1, 0);
        inAnimation.setDuration(250);
        inAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float f = (Float) animation.getAnimatedValue();
                view.setTranslationY(-y * f);

            }
        });
        return inAnimation;
    }

    public static ValueAnimator tranYTopOutAnim(final View view, final float y) {
        ValueAnimator inAnimation = ValueAnimator.ofFloat(0, 1);
        inAnimation.setDuration(250);
        inAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float f = (Float) animation.getAnimatedValue();
                view.setTranslationY(0 - y * f);

            }
        });
        return inAnimation;
    }

    public static ValueAnimator tranYBottomInAnim(final View view, final float y) {
        ValueAnimator inAnimation = ValueAnimator.ofFloat(1, 0);
        inAnimation.setDuration(500);
        inAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float f = (Float) animation.getAnimatedValue();
                view.setTranslationY(y * f);

            }
        });
        return inAnimation;
    }

    public static ValueAnimator tranYBottomOutAnim(final View view, final float y) {
        ValueAnimator inAnimation = ValueAnimator.ofFloat(0, 1);
        inAnimation.setDuration(500);
        inAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float f = (Float) animation.getAnimatedValue();
                view.setTranslationY(y * f);

            }
        });
        return inAnimation;
    }

    public static ValueAnimator rotationAnim(final FloatListener floatListener) {
        ValueAnimator rotationAnimation = ValueAnimator.ofFloat(0, 1);
        rotationAnimation.setDuration(300);
        rotationAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float f = (Float) animation.getAnimatedValue();
                if (floatListener != null) {
                    floatListener.floatValueChang(f);
                }
            }
        });
        return rotationAnimation;
    }

    public static ValueAnimator rotationCricleAnim(final FloatListener floatListener, long time) {
        ValueAnimator rotationAnimation = ValueAnimator.ofFloat(0, 1);
        rotationAnimation.setDuration(time);
        rotationAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float f = (Float) animation.getAnimatedValue();
                if (floatListener != null) {
                    floatListener.floatValueChang(f);
                }
            }
        });
        return rotationAnimation;
    }

    public static ValueAnimator cameraAnim(final FloatListener floatListener, long time) {
        ValueAnimator rotationAnimation = ValueAnimator.ofFloat(1, 0,1);
        rotationAnimation.setDuration(time);
        rotationAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float f = (Float) animation.getAnimatedValue();
                if (floatListener != null) {
                    floatListener.floatValueChang(f);
                }
            }
        });
        return rotationAnimation;
    }

    public interface FloatListener {
        void floatValueChang(float f);
    }
}
