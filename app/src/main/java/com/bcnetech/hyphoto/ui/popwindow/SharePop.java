package com.bcnetech.hyphoto.ui.popwindow;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bcnetech.bcnetchhttp.config.Flag;
import com.bcnetech.bcnetechlibrary.blurkit.BlurCallable;
import com.bcnetech.bcnetechlibrary.blurkit.BlurUtil;
import com.bcnetech.bcnetechlibrary.popwindow.BasePopWindow;
import com.bcnetech.bcnetechlibrary.util.ThreadPoolUtil;
import com.bcnetech.hyphoto.R;

import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

/**
 * Created by a1234 on 16/8/23.
 */

public class SharePop extends BasePopWindow {

    private ImageView image;
    private Activity activity;
    private MorePopSelectListener morePopSelectListener;
    private LinearLayout ll_qq;
    private LinearLayout ll_wechat;
    private LinearLayout ll_wechat_firend;
    private LinearLayout ll_sina;
    private LinearLayout ll_qq_zone;
    private LinearLayout ll_other;

    private LinearLayout ll_twitter;
    private LinearLayout ll_facebook;


    private ImageView iv_qq;
    private ImageView iv_wechat;
    private ImageView iv_wechatFriend;
    private ImageView iv_sina;
    private ImageView iv_qqZone;
    private ImageView iv_other;

    private TextView tv_qq;
    private TextView tv_wechat;
    private TextView tv_wechatFriend;
    private TextView tv_sina;
    private TextView tv_qqZone;
    private TextView tv_other;

    private LinearLayout ll_cnOne;
    private LinearLayout ll_cnTwo;
    private LinearLayout ll_us;

    private LinearLayout ll_us_other;

    private TextView tv_facebook;
    private TextView tv_twitter;

    private ImageView iv_twitter;
    private ImageView iv_facebook;
    private boolean isCanClick = true;

    //图片
    public static final int PHOTO=1;

    //视频
    public static final int CAMERA=2;

    //多个视频
    public static final int CAMERAS=3;

    //视频和图片
    public static final int PHOTO_CAMERA=4;

    //视频和图片
    public static final int PHOTOS=5;

    //单张
    public static final int PHOTO_ONE=0;

    private int shareType;


    //private ChoiceDialog5 choiceDialog5;





    public SharePop(Activity activity) {
        super(activity);
        this.activity = activity;
        initView(activity);
        initData();
        onViewClick();
        initAlpAnim(content);
        initBottomAnim(content);
    }

    @Override
    public void showPop(final View view) {
        initData();
        ObjectAnimator animator = ObjectAnimator.ofFloat(image, "alpha", 1f, 0f, 1f);
        animator.setDuration(100);
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                inAnimation.start();
                alpInAnim.start();
                showAtLocation(view, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator.start();

    }

    @Override
    protected void initBottomAnim(View view) {
        super.initBottomAnim(view);
    }

    @Override
    public void dismissPop() {
        outAnimation.start();
        alpOutAnim.start();
    }

    @Override
    protected void initAlpAnim(View view) {
        super.initAlpAnim(view);
    }

    private void initView(Activity activity) {
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.share_view_layout, null);

        ll_other= (LinearLayout) view.findViewById(R.id.ll_other);
        ll_qq_zone= (LinearLayout) view.findViewById(R.id.ll_qq_zone);
        ll_qq= (LinearLayout) view.findViewById(R.id.ll_qq);
        ll_sina= (LinearLayout) view.findViewById(R.id.ll_sina);
        ll_wechat= (LinearLayout) view.findViewById(R.id.ll_wechat);
        ll_wechat_firend= (LinearLayout) view.findViewById(R.id.ll_wechat_firend);

        image = (ImageView)view.findViewById(R.id.image);
        content = (RelativeLayout)view.findViewById(R.id.content);
        iv_qq= (ImageView) view.findViewById(R.id.iv_qq);
        iv_wechat= (ImageView) view.findViewById(R.id.iv_wechat);
        iv_wechatFriend= (ImageView) view.findViewById(R.id.iv_wechatFriend);
        iv_sina= (ImageView) view.findViewById(R.id.iv_sina);
        iv_qqZone= (ImageView) view.findViewById(R.id.iv_qqZone);
        iv_other= (ImageView) view.findViewById(R.id.iv_other);

        tv_qq= (TextView) view.findViewById(R.id.tv_qq);
        tv_wechat= (TextView) view.findViewById(R.id.tv_wechat);
        tv_wechatFriend= (TextView) view.findViewById(R.id.tv_wechatFriend);
        tv_sina= (TextView) view.findViewById(R.id.tv_sina);
        tv_qqZone= (TextView) view.findViewById(R.id.tv_qqZone);
        tv_other= (TextView) view.findViewById(R.id.tv_other);

        ll_cnOne= (LinearLayout) view.findViewById(R.id.ll_cnOne);
        ll_cnTwo= (LinearLayout) view.findViewById(R.id.ll_cnTwo);
        ll_us= (LinearLayout) view.findViewById(R.id.ll_us);
        ll_us_other= (LinearLayout) view.findViewById(R.id.ll_us_other);
        ll_twitter= (LinearLayout) view.findViewById(R.id.ll_twitter);
        ll_facebook= (LinearLayout) view.findViewById(R.id.ll_facebook);

        tv_facebook= (TextView) view.findViewById(R.id.tv_facebook);
        tv_twitter= (TextView) view.findViewById(R.id.tv_twitter);

        iv_facebook= (ImageView) view.findViewById(R.id.iv_facebook);
        iv_twitter= (ImageView) view.findViewById(R.id.iv_twitter);


        setContentView(view);
    }

    private void initData() {
        setApplyBlur();

        if(Flag.isEnglish){
            ll_us.setVisibility(View.VISIBLE);


        }else {
            ll_cnOne.setVisibility(View.VISIBLE);
            ll_cnTwo.setVisibility(View.VISIBLE);
        }

    }

    public void setApplyBlur() {
        //applyBlur(blueToothListNewPopBinding.ivContent);
        BlurCallable blurCallable = new BlurCallable(BlurUtil.getViewCache(activity));
        FutureTask<Drawable> futureTask = new FutureTask(blurCallable);
        ThreadPoolUtil.execute(futureTask);
        try {
           image.setBackground(futureTask.get(3000, TimeUnit.MILLISECONDS));
            if (futureTask.isDone()) {
                futureTask.cancel(false);
            }
        } catch (Exception e) {
        }

    }

   private void onViewClick(){

       /* ll_qq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(shareType==PHOTO_CAMERA||shareType==CAMERAS){
                    if (choiceDialog5 == null) {
                        choiceDialog5 = ChoiceDialog5.createInstance(activity);
                        choiceDialog5.setChoiceInterface(new ChoiceDialog5.ChoiceInterface() {
                            @Override
                            public void choiceOk() {
                                choiceDialog5.dismiss();
                            }

                            @Override
                            public void choiceCencel() {

                            }
                        });
                    }
                    choiceDialog5.show();
                    choiceDialog5.setCancel("");
                    choiceDialog5.setTitle(activity.getString(R.string.qq));
                    choiceDialog5.setHint(activity.getString(R.string.qq_shareHint));

                    return;
                }
                morePopSelectListener.onShareQQ();
            }
        });
        ll_wechat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(shareType==PHOTO_CAMERA||shareType==CAMERAS){
                    if (choiceDialog5 == null) {
                        choiceDialog5 = ChoiceDialog5.createInstance(activity);
                        choiceDialog5.setChoiceInterface(new ChoiceDialog5.ChoiceInterface() {
                            @Override
                            public void choiceOk() {
                                choiceDialog5.dismiss();
                            }

                            @Override
                            public void choiceCencel() {

                            }
                        });
                    }
                    choiceDialog5.show();
                    choiceDialog5.setCancel("");
                    choiceDialog5.setTitle(activity.getString(R.string.we_chat));
                    choiceDialog5.setHint(activity.getString(R.string.weChat_shareHint));

                    return;
                }
                morePopSelectListener.onShareWeChat();
            }
        });
        ll_wechat_firend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(shareType==PHOTO_CAMERA||shareType==CAMERAS||shareType==PHOTOS){
                    if (choiceDialog5 == null) {
                        choiceDialog5 = ChoiceDialog5.createInstance(activity);
                        choiceDialog5.setChoiceInterface(new ChoiceDialog5.ChoiceInterface() {
                            @Override
                            public void choiceOk() {
                                choiceDialog5.dismiss();
                            }

                            @Override
                            public void choiceCencel() {

                            }
                        });
                    }
                    choiceDialog5.show();
                    choiceDialog5.setCancel("");
                    choiceDialog5.setTitle(activity.getString(R.string.moments));
                    choiceDialog5.setHint(activity.getString(R.string.moments_shareHint));

                    return;
                }
                morePopSelectListener.onShareWeChatFriend();
            }
        });
        ll_sina.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(shareType==PHOTO_CAMERA||shareType==CAMERAS||shareType==PHOTOS){
                    if (choiceDialog5 == null) {
                        choiceDialog5 = ChoiceDialog5.createInstance(activity);
                        choiceDialog5.setChoiceInterface(new ChoiceDialog5.ChoiceInterface() {
                            @Override
                            public void choiceOk() {
                                choiceDialog5.dismiss();
                            }

                            @Override
                            public void choiceCencel() {

                            }
                        });
                    }
                    choiceDialog5.show();
                    choiceDialog5.setCancel("");
                    choiceDialog5.setTitle(activity.getString(R.string.sina));
                    choiceDialog5.setHint(activity.getString(R.string.sina_shareHint));

                    return;
                }
                morePopSelectListener.onShareSina();
            }
        });
        ll_qq_zone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(shareType==PHOTO_CAMERA||shareType==CAMERAS){
                    if (choiceDialog5 == null) {
                        choiceDialog5 = ChoiceDialog5.createInstance(activity);
                        choiceDialog5.setChoiceInterface(new ChoiceDialog5.ChoiceInterface() {
                            @Override
                            public void choiceOk() {
                                choiceDialog5.dismiss();
                            }

                            @Override
                            public void choiceCencel() {

                            }
                        });
                    }
                    choiceDialog5.show();
                    choiceDialog5.setCancel("");
                    choiceDialog5.setTitle(activity.getString(R.string.qq_zone));
                    choiceDialog5.setHint(activity.getString(R.string.qqZone_shareHint));

                    return;
                }
                morePopSelectListener.onShareQQZone();
            }
        });
        ll_other.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                morePopSelectListener.onShareOther();
            }
        });

        ll_facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(shareType==PHOTO_CAMERA||shareType==CAMERAS){
                    if (choiceDialog5 == null) {
                        choiceDialog5 = ChoiceDialog5.createInstance(activity);
                        choiceDialog5.setChoiceInterface(new ChoiceDialog5.ChoiceInterface() {
                            @Override
                            public void choiceOk() {
                                choiceDialog5.dismiss();
                            }

                            @Override
                            public void choiceCencel() {

                            }
                        });
                    }
                    choiceDialog5.show();
                    choiceDialog5.setCancel("");
                    choiceDialog5.setTitle(activity.getString(R.string.facebook));
                    choiceDialog5.setHint(activity.getString(R.string.facebook_shareHint));

                    return;
                }
                morePopSelectListener.onShareFacebook();
            }
        });

        ll_twitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(shareType==PHOTO_CAMERA||shareType==CAMERAS||shareType==PHOTO||shareType==PHOTOS){
                    if (choiceDialog5 == null) {
                        choiceDialog5 = ChoiceDialog5.createInstance(activity);
                        choiceDialog5.setChoiceInterface(new ChoiceDialog5.ChoiceInterface() {
                            @Override
                            public void choiceOk() {
                                choiceDialog5.dismiss();
                            }

                            @Override
                            public void choiceCencel() {

                            }
                        });
                    }
                    choiceDialog5.show();
                    choiceDialog5.setCancel("");
                    choiceDialog5.setTitle(activity.getString(R.string.twitter));
                    choiceDialog5.setHint(activity.getString(R.string.twitter_shareHint));

                    return;
                }
                morePopSelectListener.onShareTwitter();
            }
        });*/

        ll_us_other.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                morePopSelectListener.onShareUsOther();
            }
        });

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissPop();
            }
        });
    }

    public interface MorePopSelectListener{
        void onShareQQ();
        void onShareWeChat();
        void onShareWeChatFriend();
        void onShareSina();
        void onShareQQZone();
        void onShareOther();
        void onShareTwitter();
        void onShareFacebook();
        void onShareUsOther();
    }

    public void setType(int type){
        this.shareType=type;
//        switch (type){
//            case PHOTO:
//                iv_qq.setImageResource(R.drawable.qq);
//                iv_wechat.setImageResource(R.drawable.wechat);
//                iv_wechatFriend.setImageResource(R.drawable.wechat_friend);
//                iv_sina.setImageResource(R.drawable.sina);
//                iv_qqZone.setImageResource(R.drawable.qq_zone);
//
//                tv_qq.setTextColor(activity.getResources().getColor(R.color.text_item_child));
//                tv_wechat.setTextColor(activity.getResources().getColor(R.color.text_item_child));
//                tv_wechatFriend.setTextColor(activity.getResources().getColor(R.color.text_item_child));
//                tv_sina.setTextColor(activity.getResources().getColor(R.color.text_item_child));
//                tv_qqZone.setTextColor(activity.getResources().getColor(R.color.text_item_child));
//
//
//                iv_facebook.setImageResource(R.drawable.sina);
//                iv_twitter.setImageResource(R.drawable.qq_unzone);
//
//                tv_facebook.setTextColor(activity.getResources().getColor(R.color.text_item_child));
//                tv_twitter.setTextColor(activity.getResources().getColor(R.color.text_item_child52));
//
//                break;
//            case PHOTOS:
//                iv_qq.setImageResource(R.drawable.qq);
//                iv_wechat.setImageResource(R.drawable.wechat);
//                iv_wechatFriend.setImageResource(R.drawable.wechat_unfriend);
//                iv_sina.setImageResource(R.drawable.sina_unclick);
//                iv_qqZone.setImageResource(R.drawable.qq_zone);
//
//
//                tv_qq.setTextColor(activity.getResources().getColor(R.color.text_item_child));
//                tv_wechat.setTextColor(activity.getResources().getColor(R.color.text_item_child));
//                tv_wechatFriend.setTextColor(activity.getResources().getColor(R.color.text_item_child52));
//                tv_sina.setTextColor(activity.getResources().getColor(R.color.text_item_child52));
//                tv_qqZone.setTextColor(activity.getResources().getColor(R.color.text_item_child));
//
//
//                iv_facebook.setImageResource(R.drawable.sina);
//                iv_twitter.setImageResource(R.drawable.qq_unzone);
//
//                tv_facebook.setTextColor(activity.getResources().getColor(R.color.text_item_child));
//                tv_twitter.setTextColor(activity.getResources().getColor(R.color.text_item_child52));
//
//                break;
//            case CAMERA:
//                iv_qq.setImageResource(R.drawable.qq);
//                iv_wechat.setImageResource(R.drawable.wechat);
//                iv_wechatFriend.setImageResource(R.drawable.wechat_unfriend);
//                iv_sina.setImageResource(R.drawable.sina);
//                iv_qqZone.setImageResource(R.drawable.qq_zone);
//
//                tv_qq.setTextColor(activity.getResources().getColor(R.color.text_item_child));
//                tv_wechat.setTextColor(activity.getResources().getColor(R.color.text_item_child));
//                tv_wechatFriend.setTextColor(activity.getResources().getColor(R.color.text_item_child52));
//                tv_sina.setTextColor(activity.getResources().getColor(R.color.text_item_child));
//                tv_qqZone.setTextColor(activity.getResources().getColor(R.color.text_item_child));
//
//                iv_facebook.setImageResource(R.drawable.sina);
//                iv_twitter.setImageResource(R.drawable.qq_zone);
//
//                tv_facebook.setTextColor(activity.getResources().getColor(R.color.text_item_child));
//                tv_twitter.setTextColor(activity.getResources().getColor(R.color.text_item_child));
//
//                break;
//            case CAMERAS:
//                iv_qq.setImageResource(R.drawable.qq_unclick);
//                iv_wechat.setImageResource(R.drawable.wechat_unclick);
//                iv_wechatFriend.setImageResource(R.drawable.wechat_unfriend);
//                iv_sina.setImageResource(R.drawable.sina_unclick);
//                iv_qqZone.setImageResource(R.drawable.qq_unzone);
//
//                tv_qq.setTextColor(activity.getResources().getColor(R.color.text_item_child52));
//                tv_wechat.setTextColor(activity.getResources().getColor(R.color.text_item_child52));
//                tv_wechatFriend.setTextColor(activity.getResources().getColor(R.color.text_item_child52));
//                tv_sina.setTextColor(activity.getResources().getColor(R.color.text_item_child52));
//                tv_qqZone.setTextColor(activity.getResources().getColor(R.color.text_item_child52));
//
//                iv_facebook.setImageResource(R.drawable.sina_unclick);
//                iv_twitter.setImageResource(R.drawable.qq_unzone);
//
//                tv_facebook.setTextColor(activity.getResources().getColor(R.color.text_item_child52));
//                tv_twitter.setTextColor(activity.getResources().getColor(R.color.text_item_child52));
//
//                break;
//            case PHOTO_CAMERA:
//                iv_qq.setImageResource(R.drawable.qq_unclick);
//                iv_wechat.setImageResource(R.drawable.wechat_unclick);
//                iv_wechatFriend.setImageResource(R.drawable.wechat_unfriend);
//                iv_sina.setImageResource(R.drawable.sina_unclick);
//                iv_qqZone.setImageResource(R.drawable.qq_unzone);
//
//                tv_qq.setTextColor(activity.getResources().getColor(R.color.text_item_child52));
//                tv_wechat.setTextColor(activity.getResources().getColor(R.color.text_item_child52));
//                tv_wechatFriend.setTextColor(activity.getResources().getColor(R.color.text_item_child52));
//                tv_sina.setTextColor(activity.getResources().getColor(R.color.text_item_child52));
//                tv_qqZone.setTextColor(activity.getResources().getColor(R.color.text_item_child52));
//
//
//                iv_facebook.setImageResource(R.drawable.sina_unclick);
//                iv_twitter.setImageResource(R.drawable.qq_unzone);
//
//                tv_facebook.setTextColor(activity.getResources().getColor(R.color.text_item_child52));
//                tv_twitter.setTextColor(activity.getResources().getColor(R.color.text_item_child52));
//
//                break;
//
//            case PHOTO_ONE:
//
//                iv_facebook.setImageResource(R.drawable.facebook);
//                iv_twitter.setImageResource(R.drawable.twitter);
//
//                tv_facebook.setTextColor(activity.getResources().getColor(R.color.text_item_child));
//                tv_twitter.setTextColor(activity.getResources().getColor(R.color.text_item_child));
//
//
//                break;
//        }
    }



    public void setMorePopSelectListener(Activity activity,MorePopSelectListener morePopSelectListener){
        this.activity = activity;
        this.morePopSelectListener = morePopSelectListener;
    }
}