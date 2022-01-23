package com.bcnetech.hyphoto.ui.view;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.bcnetech.bcnetechlibrary.util.AnimFactory;
import com.bcnetech.bcnetechlibrary.util.ContentUtil;
import com.bcnetech.bcnetechlibrary.view.BaseRelativeLayout;
import com.bcnetech.hyphoto.R;


/**
 * Created by wenbin on 16/5/5.
 */
public class BottomToolView extends BaseRelativeLayout {
    public static final int ABLUM = 1;
    public static final int PHOTO_EDIT = 2;
    public static final int CLOUD_INFO = 3;
    private BottomToolItemView item1;
    private BottomToolItemView item2;
    private BottomToolItemView item3;
    private BottomToolItemView item4;
    private ChoiceView cloud_choice_ablum;
    /*  private BottomToolItemView item5;*/
    private LinearLayout ll_ablum;
    private RelativeLayout bottom_tool_in_layout;
    private ValueAnimator animIn, animOut;
    private AnimEndListener animEndListener;
    private int w, h;

    private int currentType=ABLUM;

    public BottomToolView(Context context) {
        super(context);
    }

    public BottomToolView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BottomToolView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void initView() {
        super.initView();
        inflate(getContext(), R.layout.bottom_tool_layout, this);
        item1 = (BottomToolItemView) findViewById(R.id.item1);
        item2 = (BottomToolItemView) findViewById(R.id.item2);
        item3 = (BottomToolItemView) findViewById(R.id.item3);
        item4 = (BottomToolItemView) findViewById(R.id.item4);
       /* item5=(BottomToolItemView) findViewById(item5);*/
        cloud_choice_ablum = (ChoiceView) findViewById(R.id.cloud_choice_ablum);
        bottom_tool_in_layout = (RelativeLayout) findViewById(R.id.bottom_tool_in_layout);
        ll_ablum = (LinearLayout) findViewById(R.id.ll_ablum);
    }

    @Override
    protected void initData() {
        super.initData();


        bottom_tool_in_layout.setBackground(getContext().getResources().getDrawable(R.color.maintitlebg));
        w = ContentUtil.getScreenWidth(getContext());
        h = ContentUtil.dip2px(getContext(), 56);
    }


    public void setType(int type) {
        switch (type) {
//            case ABLUM:
//                item1.setImgText(R.drawable.btncancel, getResources().getString(R.string.cancel));
//                item2.setImgText(R.drawable.btnafter, getResources().getString(R.string.pic_after));
//                item3.setImgText(R.drawable.btnpickup, getResources().getString(R.string.matting_us));
//                item4.setImgText(R.drawable.btnmore, getResources().getString(R.string.more));
//                break;
//            case PHOTO_EDIT:
//                item1.setImgText(R.drawable.btncancel,getResources().getString(R.string.cancel));
//                item2.setImgText(R.drawable.btnupload, getResources().getString(R.string.intonative));
//                item3.setImgText(R.drawable.btnshape, getResources().getString(R.string.share));
//                item4.setImgText(R.drawable.btndelete,getResources().getString(R.string.delete));
//                break;
//            case CLOUD_INFO:
//                bottom_tool_in_layout.setVisibility(GONE);
//                cloud_choice_ablum.setVisibility(VISIBLE);
//                break;

        }
    }

    /***
     * @param listener
     * @param type     item的按钮种类  0表示默认
     * @return
     */
    public BottomToolView setFirstItem(OnClickListener listener, int type) {
        if (type != 0) {
            item1.setTouchType(type);
        }
        item1.setOnClickListener(listener);
        return this;
    }

    public BottomToolView setSecondItem(OnClickListener listener, int type) {
        if (type != 0) {
            item2.setTouchType(type);
        }
        item2.setOnClickListener(listener);
        return this;
    }

    public BottomToolView setThreeItem(OnClickListener listener, int type) {
        if (type != 0) {
            item3.setTouchType(type);
        }
        item3.setOnClickListener(listener);
        return this;
    }

    public BottomToolView setFourItem(OnClickListener listener) {
        item4.setOnClickListener(listener);
        return this;
    }
   /* public BottomToolView setFiveItem(OnClickListener listener,int type){
        if (type != 0) {
            item5.setTouchType(type);
        }
        item5.setOnClickListener(listener);
        return this;
    }*/

    public void initAnim(Animator.AnimatorListener animInListener, Animator.AnimatorListener animOutListener) {
        animIn = AnimFactory.BottomInAnim(this);
        animOut = AnimFactory.BottomOutAnim(this);

        if (animInListener != null) {
            animIn.addListener(animInListener);
        }

        if (animOutListener != null) {
            animOut.addListener(animOutListener);
        } else {
            animOut.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    setVisibility(GONE);
                    if (animEndListener != null)
                        animEndListener.AnimEnd();
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

    public void startIn() {
        setVisibility(VISIBLE);
        if (animIn != null)
            animIn.start();
    }

    public void startOut(AnimEndListener listener) {
        if (animOut != null)
            animOut.start();

        if (listener != null)
            animEndListener = listener;
    }


    public interface AnimEndListener {
        void AnimEnd();
    }


    @TargetApi(19)
    public void setDoubleSelect(int type) {
        if(currentType!=type) {
            currentType=type;
        }
        switch (type) {
            //单选
            case PHOTO_EDIT:
                bottomchangeAnim(PHOTO_EDIT);
                break;
            //多选
            case ABLUM:
                bottomchangeAnim(ABLUM);
                break;
        }
    }

    private void bottomchangeAnim(final int type) {
        final ValueAnimator animators = AnimFactory.tranXRightAnim(ll_ablum, bottom_tool_in_layout.getWidth());
        final ValueAnimator animatore = AnimFactory.tranXLeftAnim(ll_ablum, bottom_tool_in_layout.getWidth());
        animators.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                if (animatore != null) {
                    animatore.cancel();
                }
                BottomToolView.this.setType(type);
            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        AnimatorSet set = new AnimatorSet();
        set.play(animators).after(animatore);
        set.start();
    }
}
