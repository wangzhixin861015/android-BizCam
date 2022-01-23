package com.bcnetech.hyphoto.ui.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bcnetech.bcnetchhttp.RetrofitFactory;
import com.bcnetech.bcnetchhttp.base.BaseResult;
import com.bcnetech.bcnetchhttp.bean.response.LoginedUser;
import com.bcnetech.bcnetechlibrary.dialog.ProgressDialog;
import com.bcnetech.bcnetechlibrary.util.AnimFactory;
import com.bcnetech.bcnetechlibrary.util.ContentUtil;
import com.bcnetech.bcnetechlibrary.util.LogUtil;
import com.bcnetech.bcnetechlibrary.view.BaseRelativeLayout;
import com.bcnetech.hyphoto.data.BizCamUToData;
import com.bcnetech.hyphoto.task.MBaseObserver;
import com.bcnetech.hyphoto.ui.activity.personCenter.BizCamUToActivity;
import com.bcnetech.hyphoto.utils.JsonUtil;
import com.bcnetech.hyphoto.utils.StringUtil;
import com.bcnetech.hyphoto.R;

import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by yhf on 2018/3/12.
 */
public class BizCamUToView extends BaseRelativeLayout {

    private ImageView iv_logo;
    private TextView tv_hintOne;
    private TextView tv_hintTwo;
    private LinearLayout ll_input;
    private EditText ed_name;
    private EditText ed_pwd;
    private TextView tv_ok;
    private TextView tv_cancel;
    private WebView wv_join;
    private TitleView titleView;
    private RelativeLayout tl_top;

    private ValueAnimator outAnim, inAnim;
    private BizCamUToData bizCamUToData;
    private ProgressDialog login_dialog;

    public BizCamUToView(Context context) {
        super(context);
    }

    public BizCamUToView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BizCamUToView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void initData() {
        super.initData();
        initAnim();

        titleView.setType(TitleView.BIZCAM_U_TO_WV);
        titleView.setTitleText("申请加入");
        wv_join.getSettings().setJavaScriptEnabled(true);
        wv_join.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        wv_join.setWebChromeClient(new WebChromeClient());

    }

    @Override
    protected void initView() {
        super.initView();
        View view = inflate(getContext(), R.layout.bizcam_u_to_layout, this);
        iv_logo = view.findViewById(R.id.iv_logo);
        tv_hintOne = view.findViewById(R.id.tv_hintOne);
        tv_hintTwo = view.findViewById(R.id.tv_hintTwo);
        ll_input = view.findViewById(R.id.ll_input);
        ed_name = view.findViewById(R.id.ed_name);
        ed_pwd = view.findViewById(R.id.ed_pwd);
        tv_ok = view.findViewById(R.id.tv_ok);
        tv_cancel = view.findViewById(R.id.tv_cancel);
        wv_join = view.findViewById(R.id.wv_join);
        titleView = view.findViewById(R.id.titleView);
        tl_top=view.findViewById(R.id.tl_top);
    }

    @Override
    protected void onViewClick() {
        super.onViewClick();
    }

    public void show(String result) {
//        inAnim.start();
        setVisibility(VISIBLE);
        if (!StringUtil.isBlank(result)) {
            bizCamUToData= JsonUtil.Json2T(result, BizCamUToData.class);
            if (null != bizCamUToData && "bcyun".equals(bizCamUToData.getCode())) {
                showType(bizCamUToData.getAction());
            } else {
                showType("codeError");
            }
        } else {
            showType("codeError");
        }
    }

    private void initAnim() {
        int hight = ContentUtil.getScreenHeight2(getContext());
        outAnim = AnimFactory.tranYBottomOutAnim(this, hight);
        outAnim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                setVisibility(GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        inAnim = AnimFactory.tranYBottomInAnim(this, hight);
        inAnim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                setVisibility(VISIBLE);

            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    private void showType(String action) {
        if ("bangding".equals(action)) {
            tl_top.setVisibility(VISIBLE);
            wv_join.setVisibility(VISIBLE);
            LogUtil.d(encodeUrl(bizCamUToData.getUrl() + "?tag=" + bizCamUToData.getTag() + "&params=" + bizCamUToData.getParams() + "&token=" + LoginedUser.getLoginedUser().getTokenid())+"&user_id="+LoginedUser.getLoginedUser().getLoginData().getUserid());
            wv_join.loadUrl(encodeUrl(bizCamUToData.getUrl() + "?tag=" + bizCamUToData.getTag() + "&params=" + bizCamUToData.getParams() + "&token=" + LoginedUser.getLoginedUser().getTokenid()+"&user_id="+LoginedUser.getLoginedUser().getLoginData().getUserid()));
        } else if ("login".equals(action)) {

            iv_logo.setVisibility(VISIBLE);
            iv_logo.setImageResource(R.drawable.bizcam_u_to_login);

            tv_hintOne.setVisibility(VISIBLE);
            tv_hintOne.setText(getResources().getString(R.string.bizcam_ut_hint3));

            tv_ok.setVisibility(VISIBLE);
            tv_ok.setText(getResources().getString(R.string.bizcam_ut_hint4));

            tv_cancel.setVisibility(VISIBLE);
            tv_cancel.setText(getResources().getString(R.string.cancel));

        } else if ("codeError".equals(action)) {
            iv_logo.setVisibility(VISIBLE);
            iv_logo.setImageResource(R.drawable.bizcam_u_to_unknow);

            tv_hintTwo.setVisibility(VISIBLE);
            tv_hintTwo.setText(getResources().getString(R.string.bizcam_ut_hint2));

            tv_ok.setVisibility(VISIBLE);
            tv_ok.setText(getResources().getString(R.string.bizcam_ut_hint5));

            tv_cancel.setVisibility(VISIBLE);
            tv_cancel.setText(getResources().getString(R.string.cancel));
        }
    }

    private  String encodeUrl(String url) {
        return Uri.encode(url, "-![.:/,%?&=]");
    }

    public void cancelClickListener(OnClickListener listener){
        tv_cancel.setOnClickListener(listener);
    }

    public void okClickListener(OnClickListener listener){
        tv_ok.setOnClickListener(listener);
    }

    public void loginClick(){
        if (null != bizCamUToData && "bcyun".equals(bizCamUToData.getCode())) {
            RetrofitFactory.getInstence().API().bizCamUToLogin(bizCamUToData.getUrl())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new MBaseObserver<Object>((BizCamUToActivity)getContext(),true) {
                        @Override
                        protected void onSuccees(BaseResult<Object> t) throws Exception {
                            showSuccessDialog();

                            Timer timer = new Timer();
                            TimerTask task = new TimerTask() {
                                public void run() {
                                    Message message = new Message();
                                    message.what = 1;

                                    myHandler.sendMessage(message);
                                }
                            };
                            timer.schedule(task, 2000);
                        }

                        @Override
                        protected void onCodeError(BaseResult<Object> t) throws Exception {
                            showFailDialog();
                            Timer timer = new Timer();
                            TimerTask task = new TimerTask() {
                                public void run() {
                                    Message message = new Message();
                                    message.what = 2;

                                    myHandler.sendMessage(message);
                                }
                            };
                            timer.schedule(task, 2000);
                        }


                        @Override
                        protected void onFailure(Throwable e, boolean isNetWorkError) throws Exception {
                            showFailDialog();
                            Timer timer = new Timer();
                            TimerTask task = new TimerTask() {
                                public void run() {
                                    Message message = new Message();
                                    message.what = 2;

                                    myHandler.sendMessage(message);
                                }
                            };
                            timer.schedule(task, 2000);

                        }
                    });
        }else {
            setVisibility(GONE);
//            outAnim.start();
            iv_logo.setVisibility(GONE);
            tv_hintTwo.setVisibility(GONE);
            tv_ok.setVisibility(GONE);
            tv_cancel.setVisibility(GONE);
        }
    }

    private BizCamUtoListener bizCamUtoListener;

    public BizCamUtoListener getBizCamUtoListener() {
        return bizCamUtoListener;
    }

    public void setBizCamUtoListener(BizCamUtoListener bizCamUtoListener) {
        loginClick();
        this.bizCamUtoListener = bizCamUtoListener;
    }

    public interface BizCamUtoListener{
        void loginSuccess();
    }

    private void showFailDialog(){
        if(null==login_dialog){
            login_dialog = new ProgressDialog(getContext(), new ProgressDialog.DGProgressListener() {
                @Override
                public void onSuccessAnimed() {

                }

                @Override
                public void onFailAnimed() {

                }

                @Override
                public void onAnim() {

                }
            }, true);
        }
        login_dialog.setType(ProgressDialog.TYPE_UT_LOGIN_FAIL);
        login_dialog.setText(getContext().getResources().getString(R.string.logging_fail),
                ProgressDialog.TYPE_UT_LOGIN_FAIL);
        login_dialog.show();
    }

    private void showSuccessDialog(){
        if(null==login_dialog){
            login_dialog = new ProgressDialog(getContext(), new ProgressDialog.DGProgressListener() {
                @Override
                public void onSuccessAnimed() {

                }

                @Override
                public void onFailAnimed() {

                }

                @Override
                public void onAnim() {

                }
            }, true);
        }
        login_dialog.setType(ProgressDialog.TYPE_UT_LOGIN_SUCCESS);
        login_dialog.setText(getContext().getResources().getString(R.string.logging_success),
                ProgressDialog.TYPE_UT_LOGIN_SUCCESS);
        login_dialog.show();
    }

    Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    login_dialog.dismiss();
                    bizCamUtoListener.loginSuccess();
                    break;
                case 2:
                    login_dialog.dismiss();
                    break;
            }
            super.handleMessage(msg);
        }
    };

}
