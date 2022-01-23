package com.bcnetech.hyphoto.ui.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bcnetech.bcnetchhttp.RetrofitFactory;
import com.bcnetech.bcnetchhttp.RetrofitInternationalFactory;
import com.bcnetech.bcnetchhttp.base.BaseResult;
import com.bcnetech.bcnetchhttp.bean.request.LoginUserBody;
import com.bcnetech.bcnetchhttp.bean.request.RegisterEmailBody;
import com.bcnetech.bcnetchhttp.bean.request.SendEmailBody;
import com.bcnetech.bcnetchhttp.bean.request.UserIsExistBody;
import com.bcnetech.bcnetchhttp.bean.response.LoginReceiveData;
import com.bcnetech.bcnetchhttp.bean.response.LoginedUser;
import com.bcnetech.bcnetchhttp.config.Flag;
import com.bcnetech.bcnetchhttp.config.UrlConstants;
import com.bcnetech.bcnetechlibrary.dialog.DGProgressDialog3;
import com.bcnetech.bcnetechlibrary.util.AnimFactory;
import com.bcnetech.bcnetechlibrary.util.LogUtil;
import com.bcnetech.bcnetechlibrary.util.StringUtil;
import com.bcnetech.bcnetechlibrary.util.ToastUtil;
import com.bcnetech.bcnetechlibrary.util.sharepreference.SharePreferences;
import com.bcnetech.bcnetechlibrary.view.LimitBackEditText;
import com.bcnetech.bcnetechlibrary.view.LimitEditText;
import com.bcnetech.hyphoto.task.MBaseObserver;
import com.bcnetech.hyphoto.ui.activity.AlbumNewActivity;
import com.bcnetech.hyphoto.ui.dialog.RegisterUsDialog;
import com.bcnetech.hyphoto.utils.FontImageUtil;
import com.bcnetech.hyphoto.utils.JsonUtil;
import com.bcnetech.hyphoto.utils.KeyBoardUtil;
import com.bcnetech.hyphoto.R;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


/**
 * Created by yhf on 17/11/20.
 */

public class MLoginNewUsView extends RelativeLayout {
    private RelativeLayout rl_content;


    public MLoginInter mLoginInter;

    private ValueAnimator valueAnimator;

    private RelativeLayout rl_number_content;
    private TitleView titleView;
    private LimitBackEditText input_number;
    private ImageView num_clear;
    private TextView tv_num_next;
    //    private TextView tv_message;
//    private TextView tv_num;
    private LinearLayout ll_number;
    private TextView tv_input_number;
    private ProgressBar pb_num;


    private RelativeLayout rl_pwd_content;
    private TitleView title_pwd;
    private LimitEditText input_pwd;
    private TextView tv_pwd_next;
    private ImageView pwd_clear;
    private LinearLayout ll_pwd;
    private TextView tv_input_pwd;


    private LimitBackEditText input_verify;
    private TextView tv_input_verify;
    private ImageView verify_clear;
    private TextView tv_verify_next;

    private int type;
    private RelativeLayout rl_verify_content;
    private TitleView title_verify;
    private LinearLayout input_verify_ll;
    private LinearLayout ll_verify;
    private TextView tv_verifyMessage;


    private RelativeLayout rl_rePwd_content;
    private TitleView title_rePwd;
    private EditText input_rePwd;
    private ImageView rePwd_clear;
    private TextView tv_rePwd_next;
    private TextView tv_rePwd;
    private TextView tv_input_rePwd;
    private LinearLayout ll_rePwd;
    private TextView tv_rePwdMessage;

    private RelativeLayout rl_login_content;
    private ProgressBar pb_progressbar;

    private RegisterUsDialog registerUsDialog;
    String email;
    private boolean isEmail;


    private ClassCut classCut;
    private ClassEditCut editCut;
    private Handler mHandler = new Handler();//全局handler
    private int i = 60;//倒计时的整个时间数
    private int errorTime = 1;//错误倒计时
    private int asynTime = 1500;
    private ClassAsyncCut asyncCut;

    //电话
    private String mPhone;
    //密码
    private String mPwd;
    //验证码
    private String mCode;
    //昵称
    private String mNicekName;

    private int nextType;
    private boolean hint = true;

    private final static int NUMBER_NEXT = 1;
    private final static int NUMBER_NEXT_ISEXIT = 5;
    private final static int PWD_NEXT = 2;
    private final static int REPWD_NEXT = 3;
    private final static int VERIFY_NEXT = 4;

    private final static int LOG_IN = 1;
    private final static int REGISTER = 2;
    private final static int FORGET_PWD = 3;
    private final static int RELOG_IN = 4;


    MLoginNewView.ClickInter clickInter;


    public MLoginNewUsView(Context context) {
        super(context);
        initView();
        initData();
        onViewClick();
        initEditText();
    }

    public MLoginNewUsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
        initData();
        onViewClick();
        initEditText();
    }

    public MLoginNewUsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
        initData();
        onViewClick();
        initEditText();
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
        if (this.type == Flag.TYPE_LOGIN) {
            title_rePwd.setType(TitleView.MAIN_LOGIN_REPWD_US);
//            tv_message.setText("手机号码/子账号");
//            tv_num_next.setText("下一步");
//            tv_rePwd.setText("新密码");
            if (null != LoginedUser.getLoginedUser()) {
                if (!StringUtil.isBlank(LoginedUser.getLoginedUser().getUser_name())) {
                    input_number.setText(LoginedUser.getLoginedUser().getUser_name());
                    input_number.setSelection(LoginedUser.getLoginedUser().getUser_name().length());
                }
            }
        } else {
//            tv_message.setText("账号");
//            tv_num_next.setText("验证码");
//            tv_rePwd.setText("设置密码");
            input_rePwd.setHint(getResources().getString(R.string.enter_a_valid_password));
            title_rePwd.setType(TitleView.MAIN_LOGIN_REPWD_US);
        }
    }

    protected void initData() {
        titleView.setType(TitleView.MAIN_LOGIN_US);
        title_pwd.setType(TitleView.MAIN_LOGIN_PWD_US);
        title_verify.setType(TitleView.MAIN_LOGIN_VERIFY_US);
        KeyBoardUtil.openKeybord(input_number, getContext());

    }

    public void setMLoginInter(MLoginInter mLoginInter) {
        this.mLoginInter = mLoginInter;
    }

    protected void initView() {
        inflate(getContext(), R.layout.mlogin_new_us_layout, this);
        rl_content = (RelativeLayout) findViewById(R.id.rl_content);
        rl_number_content = (RelativeLayout) findViewById(R.id.rl_number_content);
        titleView = (TitleView) findViewById(R.id.title_layout);
        input_number = (LimitBackEditText) findViewById(R.id.input_number);
        num_clear = (ImageView) findViewById(R.id.num_clear);
        tv_num_next = (TextView) findViewById(R.id.tv_num_next);
//        tv_num = (TextView) findViewById(R.id.tv_num);
//        tv_message = (TextView) findViewById(R.id.tv_message);
        ll_number = (LinearLayout) findViewById(R.id.ll_number);
        tv_input_number = (TextView) findViewById(R.id.tv_input_number);
        pb_num = (ProgressBar) findViewById(R.id.pb_num);

        rl_pwd_content = (RelativeLayout) findViewById(R.id.rl_pwd_content);
        title_pwd = (TitleView) findViewById(R.id.title_pwd);
        input_pwd = (LimitEditText) findViewById(R.id.input_pwd);
        tv_pwd_next = (TextView) findViewById(R.id.tv_pwd_next);
        pwd_clear = (ImageView) findViewById(R.id.pwd_clear);
        ll_pwd = (LinearLayout) findViewById(R.id.ll_pwd);
        tv_input_pwd = (TextView) findViewById(R.id.tv_input_pwd);

        tv_verify_next = (TextView) findViewById(R.id.tv_verify_next);
        input_verify = (LimitBackEditText) findViewById(R.id.input_verify);
        tv_input_verify = (TextView) findViewById(R.id.tv_input_verify);
        verify_clear = (ImageView) findViewById(R.id.verify_clear);
        rl_verify_content = (RelativeLayout) findViewById(R.id.rl_verify_content);
        title_verify = (TitleView) findViewById(R.id.title_verify);
        input_verify_ll = (LinearLayout) findViewById(R.id.input_verify_ll);
        ll_verify = (LinearLayout) findViewById(R.id.ll_verify);
        tv_verifyMessage = (TextView) findViewById(R.id.tv_verifyMessage);

        rl_rePwd_content = (RelativeLayout) findViewById(R.id.rl_rePwd_content);
        title_rePwd = (TitleView) findViewById(R.id.title_rePwd);
        input_rePwd = (EditText) findViewById(R.id.input_rePwd);
        rePwd_clear = (ImageView) findViewById(R.id.rePwd_clear);
        tv_rePwd_next = (TextView) findViewById(R.id.tv_rePwd_next);
        tv_rePwd = (TextView) findViewById(R.id.tv_rePwd);
        tv_input_rePwd = (TextView) findViewById(R.id.tv_input_rePwd);
        ll_rePwd = (LinearLayout) findViewById(R.id.ll_rePwd);
        tv_rePwdMessage = (TextView) findViewById(R.id.tv_rePwdMessage);

        rl_login_content = (RelativeLayout) findViewById(R.id.rl_login_content);
        pb_progressbar = (ProgressBar) findViewById(R.id.pb_progressbar);
    }

    public void openNumKeyBoard() {
        KeyBoardUtil.openKeybord(input_number, getContext());
    }

    public void openPwdKeyBoard() {
        KeyBoardUtil.openKeybord(input_pwd, getContext());
    }

    public void openVerifyKeyBoard() {
        KeyBoardUtil.openKeybord(input_verify, getContext());
    }

    public void openRePwdKeyBoard() {
        KeyBoardUtil.openKeybord(input_rePwd, getContext());
    }

    public void closeRePwdKeyBoard() {
        KeyBoardUtil.closeKeybord(input_rePwd, getContext());
    }

    public void closeNumKeyBoard() {
        KeyBoardUtil.closeKeybord(input_number, getContext());
    }

    public void closePwdBoard() {
        KeyBoardUtil.closeKeybord(input_pwd, getContext());
    }

    public void closeVerifyBoard() {
        KeyBoardUtil.closeKeybord(input_verify, getContext());
    }


    public void closeKeyBoard() {
        KeyBoardUtil.closeKeybord(input_number, getContext());
        KeyBoardUtil.closeKeybord(input_pwd, getContext());
        KeyBoardUtil.closeKeybord(input_verify, getContext());
        KeyBoardUtil.closeKeybord(input_rePwd, getContext());
    }

    public void openKeyBoard() {
        if (rl_login_content.getVisibility() != VISIBLE) {
            KeyBoardUtil.openKeybord(input_number, getContext());
            KeyBoardUtil.openKeybord(input_pwd, getContext());
            KeyBoardUtil.openKeybord(input_verify, getContext());
            KeyBoardUtil.openKeybord(input_rePwd, getContext());
        }
    }


    protected void onViewClick() {

        //关闭
        titleView.setLeftListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                KeyBoardUtil.closeKeybord(input_number, getContext());
                clearTextContent();
                mLoginInter.finishView();
            }
        });


        /**
         * 下一步
         * 进入输入密码界面 或 发送验证码并到重置密码界面
         */
        tv_num_next.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickInter != null)
                    clickInter.onClick();
                String phone = input_number.getText().toString();
                if (phone == null || !isEmailValid(phone)) {
                    ll_number.setBackground(getContext().getResources().getDrawable(R.drawable.login_pop_bg));
                    tv_input_number.setVisibility(VISIBLE);
                    input_pwd.setVisibility(INVISIBLE);
                    input_verify.setVisibility(INVISIBLE);
                    input_rePwd.setVisibility(INVISIBLE);
                    input_number.setVisibility(INVISIBLE);
                    tv_num_next.setClickable(false);
                    nextType = NUMBER_NEXT;
                    startEditTime();
//                    ToastUtil.toast(getResources().getString(R.string.isphone));
                    return;
                }

                mPhone = phone;
                if (type == Flag.TYPE_LOGIN) {

                    input_number.clearFocus();
                    rl_pwd_content.setVisibility(VISIBLE);
                    openPwdKeyBoard();
                    input_pwd.setFocusable(true);
                    input_pwd.requestFocus();

                    startFloatAnim(new AnimFactory.FloatListener() {
                        @Override
                        public void floatValueChang(float f) {

                            rl_number_content.setTranslationX(rl_content.getMeasuredHeight() * -f);
                            rl_number_content.setAlpha(1 - f);

                            rl_pwd_content.setTranslationX(rl_content.getMeasuredHeight() * (1 - f));
                            rl_pwd_content.setAlpha(f);
                        }
                    });
                } else {
//                    if (!phone.equals(mPhone)) {
                    if (null != classCut) {
                        classCut.exit = true;
                    }

                    tv_num_next.setClickable(false);
                    num_clear.setVisibility(GONE);
                    pb_num.setVisibility(VISIBLE);
                    input_number.clearFocus();
                    input_number.setFocusableInTouchMode(false);
                    input_number.setFocusable(false);
                    isExit(input_number.getText().toString().trim());


//                        showChoiceDialog(phone);
//                    title_pwd.setRightTextIsShow("<u>" + "<font color='#000000'>忘记密码?</font>" + "</u>", true);
//                    }else {
//                        mPhone = phone;
//                        input_pwd.clearFocus();
//                        rl_rePwd_content.setVisibility(VISIBLE);
//                        openRePwdKeyBoard();
//                        input_rePwd.setFocusable(true);
//                        input_rePwd.requestFocus();
//                        startFloatAnim(new AnimFactory.FloatListener() {
//                            @Override
//                            public void floatValueChang(float f) {
//
////                            openPwdKeyBoard();
//                                rl_number_content.setTranslationX(rl_content.getMeasuredHeight() * -f);
//                                rl_number_content.setAlpha(1 - f);
//
//
//                                rl_rePwd_content.setTranslationX(rl_content.getMeasuredHeight() * (1 - f));
//                                rl_rePwd_content.setAlpha(f);
//
//
////                                rl_verify_content.setTranslationX(rl_content.getMeasuredHeight() * (1 - f));
////                                rl_verify_content.setAlpha(f);
//                            }
//                        });
//                    }
                }
            }
        });

        tv_num_next.setClickable(false);


        /**
         * 下一步
         * 登陆
         */
        tv_pwd_next.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                String pwd = input_pwd.getText().toString();
                if (pwd == null || TextUtils.isEmpty(pwd)) {
                    ll_pwd.setBackground(getContext().getResources().getDrawable(R.drawable.login_pop_bg));
                    tv_input_pwd.setVisibility(VISIBLE);
                    tv_input_pwd.setText(getResources().getString(R.string.password_cannot_be_empty));
                    input_number.setVisibility(INVISIBLE);
                    input_verify.setVisibility(INVISIBLE);
                    input_rePwd.setVisibility(INVISIBLE);
                    input_pwd.setVisibility(INVISIBLE);
                    tv_pwd_next.setClickable(false);
                    nextType = PWD_NEXT;
                    startEditTime();
//                    ToastUtil.toast(getResources().getString(R.string.empty_psw));
                    return;
                }
                if (pwd.length() < 6) {
                    ll_pwd.setBackground(getContext().getResources().getDrawable(R.drawable.login_pop_bg));
                    tv_input_pwd.setVisibility(VISIBLE);
                    tv_input_pwd.setText(getResources().getString(R.string.enter_a_valid_password));
                    input_number.setVisibility(INVISIBLE);
                    input_verify.setVisibility(INVISIBLE);
                    input_rePwd.setVisibility(INVISIBLE);
                    input_pwd.setVisibility(INVISIBLE);
                    tv_pwd_next.setClickable(false);
                    nextType = PWD_NEXT;
                    startEditTime();
                    return;
                }
                mPwd = pwd;

//                closeKeyBoard();
//                rl_pwd_content.setVisibility(GONE);
//                rl_login_content.setVisibility(VISIBLE);
                tv_pwd_next.setClickable(false);

                input_pwd.setFocusable(false);
                input_pwd.setFocusableInTouchMode(false);
                login(LOG_IN);
            }
        });

        tv_pwd_next.setClickable(false);

        input_verify_ll.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                openVerifyKeyBoard();
            }
        });


        /**
         * 返回
         * 返回输入账号界面
         */
        title_pwd.setLeftListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickInter != null)
                    clickInter.onClick();
                input_pwd.clearFocus();

                openNumKeyBoard();

                input_number.setFocusable(true);
                input_number.requestFocus();
                startFloatAnim(new AnimFactory.FloatListener() {
                    @Override
                    public void floatValueChang(float f) {


                        rl_number_content.setTranslationX(-rl_content.getMeasuredHeight() * (1 - f));
                        rl_number_content.setAlpha(f);

                        rl_pwd_content.setTranslationX(rl_content.getMeasuredHeight() * f);
                        rl_pwd_content.setAlpha(1 - f);
                    }
                });

                input_pwd.setText("");
            }
            /**
             * 下一步
             * 忘记密码
             */
        }).setRightTextListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                sendEmail();
            }

        });


        /**
         * 返回
         * 验证界面
         * 返回 输入密码界面
         */
        title_verify.setLeftListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (type == Flag.TYPE_LOGIN) {
                    input_verify.clearFocus();
                    openRePwdKeyBoard();
                    input_number.setFocusable(true);
                    input_number.requestFocus();
                    startFloatAnim(new AnimFactory.FloatListener() {
                        @Override
                        public void floatValueChang(float f) {
                            rl_rePwd_content.setTranslationX(-rl_content.getMeasuredHeight() * (1 - f));
                            rl_rePwd_content.setAlpha(f);
                            rl_verify_content.setTranslationX(rl_content.getMeasuredHeight() * f);
                            rl_verify_content.setAlpha(1 - f);
                        }
                    });
                } else {
                    input_verify.clearFocus();
                    openRePwdKeyBoard();
                    input_rePwd.setFocusable(true);
                    input_rePwd.requestFocus();
                    startFloatAnim(new AnimFactory.FloatListener() {
                        @Override
                        public void floatValueChang(float f) {

                            rl_rePwd_content.setTranslationX(-rl_content.getMeasuredHeight() * (1 - f));
                            rl_rePwd_content.setAlpha(f);

                            rl_verify_content.setTranslationX(rl_content.getMeasuredHeight() * f);
                            rl_verify_content.setAlpha(1 - f);
                        }
                    });
                }

                input_verify.setText("");
            }
            /**
             * 重新发送验证码
             */
        });

        /**
         * 返回
         * 重置密码界面返回输入
         * 手机号码界面
         */
        title_rePwd.setLeftListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (type == Flag.TYPE_LOGIN) {
                    closeKeyBoard();
                    clearTextContent();
                    mLoginInter.finishView();
                } else {
                    input_rePwd.clearFocus();

                    openNumKeyBoard();
                    input_number.setFocusable(true);
                    input_number.requestFocus();
                    startFloatAnim(new AnimFactory.FloatListener() {
                        @Override
                        public void floatValueChang(float f) {

                            //弹入
                            rl_number_content.setTranslationX(-rl_content.getMeasuredHeight() * (1 - f));
                            rl_number_content.setAlpha(f);

                            //弹出
                            rl_rePwd_content.setTranslationX(rl_content.getMeasuredHeight() * f);
                            rl_rePwd_content.setAlpha(1 - f);
                        }
                    });
                }

                input_rePwd.setText("");
            }
        });


        /**
         * 下一步
         * 修改密码并登陆 或 注册并登录
         */

        tv_rePwd_next.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String pwd = input_rePwd.getText().toString();
                if (pwd == null || TextUtils.isEmpty(pwd)) {
                    ll_rePwd.setBackground(getContext().getResources().getDrawable(R.drawable.login_pop_bg));
                    tv_input_rePwd.setVisibility(VISIBLE);
                    tv_input_rePwd.setText(getResources().getString(R.string.password_cannot_be_empty));
                    input_verify.setVisibility(INVISIBLE);
                    input_number.setVisibility(INVISIBLE);
                    input_pwd.setVisibility(INVISIBLE);
                    input_rePwd.setVisibility(INVISIBLE);
                    tv_rePwd_next.setClickable(false);
                    nextType = REPWD_NEXT;
                    startEditTime();
//                    ToastUtil.toast(getResources().getString(R.string.empty_psw));
                    return;
                }
                if (pwd.length() < 6) {
                    ll_rePwd.setBackground(getContext().getResources().getDrawable(R.drawable.login_pop_bg));
                    tv_input_rePwd.setVisibility(VISIBLE);
                    tv_input_rePwd.setText(getResources().getString(R.string.enter_a_valid_password));
                    input_verify.setVisibility(INVISIBLE);
                    input_number.setVisibility(INVISIBLE);
                    input_pwd.setVisibility(INVISIBLE);
                    input_rePwd.setVisibility(INVISIBLE);
                    tv_rePwd_next.setClickable(false);
                    nextType = REPWD_NEXT;
                    startEditTime();
                    return;
                }
                if (!FontImageUtil.isBothLetterAndNum(pwd)) {
                    ll_rePwd.setBackground(getContext().getResources().getDrawable(R.drawable.login_pop_bg));
                    tv_input_rePwd.setVisibility(VISIBLE);
                    tv_input_rePwd.setText(getResources().getString(R.string.wrong_psw_style));
                    input_verify.setVisibility(INVISIBLE);
                    input_number.setVisibility(INVISIBLE);
                    input_pwd.setVisibility(INVISIBLE);
                    input_rePwd.setVisibility(INVISIBLE);
                    tv_rePwd_next.setClickable(false);
                    nextType = REPWD_NEXT;
                    startEditTime();
                    return;
                }
                //设置新密码
                mPwd = pwd;

                input_rePwd.clearFocus();
                rl_verify_content.setVisibility(VISIBLE);
                openVerifyKeyBoard();
                input_verify.setFocusable(true);
                input_verify.requestFocus();

                startFloatAnim(new AnimFactory.FloatListener() {
                    @Override
                    public void floatValueChang(float f) {

                        rl_rePwd_content.setTranslationX(rl_content.getMeasuredHeight() * -f);
                        rl_rePwd_content.setAlpha(1 - f);

                        rl_verify_content.setTranslationX(rl_content.getMeasuredHeight() * (1 - f));
                        rl_verify_content.setAlpha(f);
                    }
                });
            }
        });

        tv_rePwd_next.setClickable(false);


        tv_verify_next.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String nickName = input_verify.getText().toString();

                if (nickName == null || TextUtils.isEmpty(nickName)) {

                    ll_verify.setBackground(getContext().getResources().getDrawable(R.drawable.login_pop_bg));
                    tv_input_verify.setVisibility(VISIBLE);
                    tv_input_verify.setText(getResources().getString(R.string.nicknameempty));
                    input_number.setVisibility(INVISIBLE);
                    input_verify.setVisibility(INVISIBLE);
                    input_rePwd.setVisibility(INVISIBLE);
                    input_pwd.setVisibility(INVISIBLE);
                    tv_input_verify.setClickable(false);
                    nextType = VERIFY_NEXT;
                    startEditTime();

                    return;
                }
                mNicekName = nickName;
//                closeKeyBoard();
//                rl_verify_content.setVisibility(GONE);
//                rl_login_content.setVisibility(VISIBLE);

                input_verify.setFocusable(false);
                input_verify.setFocusableInTouchMode(false);

                tv_verify_next.setClickable(false);
                if (type == Flag.TYPE_LOGIN) {
//                    resetPwd(nickName);
                } else {
                    regist();
                }
            }
        });

        tv_verify_next.setClickable(false);

        num_clear.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                input_number.setText("");
            }
        });

        pwd_clear.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                input_pwd.setText("");
            }
        });

        rePwd_clear.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                input_rePwd.setText("");
            }
        });

        verify_clear.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                input_verify.setText("");
            }
        });


    }

    public interface MLoginInter {
        void finishView();

        void onLogin();
    }

    /**
     * 初始化EditText的Textchange事件
     * 和焦点改变事件
     */
    private void initEditText() {
        input_number.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


                email = input_number.getText().toString();

                if (0 != input_number.getText().toString().length()) {

                    num_clear.setVisibility(View.VISIBLE);

                } else {
                    num_clear.setVisibility(View.GONE);
                }

                if (!StringUtil.isBlank(input_number.getText().toString().trim()) && isEmailValid(input_number.getText().toString())) {
                    tv_num_next.setClickable(true);
                    tv_num_next.setBackground(getResources().getDrawable(R.drawable.main_able_bg));

                } else {
                    tv_num_next.setClickable(false);
                    tv_num_next.setBackground(getResources().getDrawable(R.drawable.main_unable_bg));
                }

//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        isEmail = isEmailValid(email);
//                        Message msg = new Message();
//                        msg.what = 1;
//                        handler.sendMessage(msg);
//                    }
//                }).start();

            }


            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        input_number.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (hasFocus == true) {
                    if (0 != input_number.getText().toString().length()) {
                        num_clear.setVisibility(View.VISIBLE);
                    }
                }
                if (hasFocus == false) {
                    num_clear.setVisibility(View.GONE);
                }
            }
        });

        input_pwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (0 != input_pwd.getText().toString().length()) {
                    pwd_clear.setVisibility(View.VISIBLE);
                } else {
                    pwd_clear.setVisibility(View.GONE);
                }

                if (StringUtil.isBlank(input_pwd.getText().toString()) || !FontImageUtil.isBothLetterAndNum(input_pwd.getText().toString()) || input_pwd.getText().toString().length() < 6) {
                    tv_pwd_next.setClickable(false);
                    tv_pwd_next.setBackground(getResources().getDrawable(R.drawable.main_unable_bg));
                } else {
                    tv_pwd_next.setClickable(true);
                    tv_pwd_next.setBackground(getResources().getDrawable(R.drawable.main_able_bg));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        input_pwd.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (hasFocus == true) {
                    if (0 != input_pwd.getText().toString().length()) {
                        pwd_clear.setVisibility(View.VISIBLE);
                    }
                }
                if (hasFocus == false) {
                    pwd_clear.setVisibility(View.GONE);
                }
            }
        });

        input_rePwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (0 != input_rePwd.getText().toString().length()) {
                    if (hint == true) {
                        rePwd_clear.setVisibility(View.VISIBLE);
                        hint = false;
                        input_rePwd.setTextSize(TypedValue.COMPLEX_UNIT_SP, 28);
                    }

                } else {
                    if (hint == false) {
                        rePwd_clear.setVisibility(View.GONE);
                        hint = true;
                        input_rePwd.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                    }
                }


                if (!FontImageUtil.isBothLetterAndNum(input_rePwd.getText().toString()) || input_rePwd.getText().toString().length() < 6) {
                    if (StringUtil.isBlank(input_rePwd.getText().toString())) {
                        tv_rePwdMessage.setText("");
                    } else {
                        tv_rePwdMessage.setText("Weak");
                        tv_rePwdMessage.setTextColor(getResources().getColor(R.color.red_message));
                    }
                    tv_rePwd_next.setClickable(false);
                    tv_rePwd_next.setBackground(getResources().getDrawable(R.drawable.main_unable_bg));

                } else {
                    tv_rePwd_next.setClickable(true);
                    tv_rePwd_next.setBackground(getResources().getDrawable(R.drawable.main_able_bg));

                    tv_rePwdMessage.setText("");
                    tv_rePwdMessage.setTextColor(getResources().getColor(R.color.sing_in_color));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        input_rePwd.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus == true) {
                    if (0 != input_rePwd.getText().toString().length()) {
                        rePwd_clear.setVisibility(View.VISIBLE);
                    }
                }
                if (hasFocus == false) {
                    rePwd_clear.setVisibility(View.GONE);
                }
            }
        });


        input_verify.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (0 != input_verify.getText().toString().length()) {
                    verify_clear.setVisibility(View.VISIBLE);
                } else {
                    verify_clear.setVisibility(View.GONE);
                }

                if (StringUtil.isBlank(input_verify.getText().toString()) || FontImageUtil.ishaveCharacter(input_verify.getText().toString()) || FontImageUtil.containsEmoji(input_verify.getText().toString())) {
                    if (StringUtil.isBlank(input_verify.getText().toString())) {
                        tv_verifyMessage.setText("");
                    } else {
                        tv_verifyMessage.setText(getResources().getString(R.string.unable));
                        tv_verifyMessage.setTextColor(getResources().getColor(R.color.red_message));
                    }
                    tv_verify_next.setClickable(false);
                    tv_verify_next.setBackground(getResources().getDrawable(R.drawable.main_unable_bg));

                } else {
                    tv_verify_next.setClickable(true);
                    tv_verify_next.setBackground(getResources().getDrawable(R.drawable.main_able_bg));

                    tv_verifyMessage.setText("");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        input_verify.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus == true) {
                    if (0 != input_verify.getText().toString().length()) {
                        verify_clear.setVisibility(View.VISIBLE);
                    }
                }
                if (hasFocus == false) {
                    verify_clear.setVisibility(View.GONE);
                }
            }
        });
    }


    public void startFloatAnim(AnimFactory.FloatListener floatListener) {
        if (valueAnimator != null && valueAnimator.isRunning()) {
            valueAnimator.cancel();
        }
        valueAnimator = AnimFactory.rotationCricleAnim(floatListener, 250);
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

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
        valueAnimator.start();
    }

    /**
     * 判断手机号
     */

    public boolean isEmailValid(String email) {
        boolean isValid = false;
//        CharSequence inputStr = email;
//        //正则表达式
//        String EMAIL_REGEX = "^(www\\.)?\\w+@\\w+(\\.\\w+)+$";
//        Pattern pattern = Pattern.compile(EMAIL_REGEX);
//        Matcher matcher = pattern.matcher(inputStr);
//        if (matcher.matches()) {
//            isValid = true;
//        }
        if (email.contains("@")) {
            isValid = true;
        }
        return isValid;
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    if (type != Flag.TYPE_LOGIN) {
                        if (!StringUtil.isBlank(input_number.getText().toString().trim()) && isEmail) {
//
//                            input_number.setFocusableInTouchMode(false);
//                            input_number.setFocusable(false);
//                            input_number.clearFocus();

                            tv_num_next.setClickable(true);
                            tv_num_next.setBackground(getResources().getDrawable(R.drawable.main_able_bg));
                        } else {
                            tv_num_next.setClickable(false);
                            tv_num_next.setBackground(getResources().getDrawable(R.drawable.main_unable_bg));
                        }
                    } else {
                        if (!StringUtil.isBlank(input_number.getText().toString().trim()) && isEmailValid(input_number.getText().toString())) {
                            tv_num_next.setClickable(true);
                            tv_num_next.setBackground(getResources().getDrawable(R.drawable.main_able_bg));

                        } else {
                            tv_num_next.setClickable(false);
                            tv_num_next.setBackground(getResources().getDrawable(R.drawable.main_unable_bg));
                        }
                    }
                    break;
            }
        }
    };


    //上一次登录类型
    private int mLoginType;

    private DGProgressDialog3 dgProgressDialog3;

    /**
     * 登录
     */
    private void login(final int type) {

        if (null == dgProgressDialog3) {
            dgProgressDialog3 = new DGProgressDialog3(getContext(), true, getResources().getString(R.string.waiting_please));
        }
        dgProgressDialog3.show();

        final LoginUserBody loginUserBody = new LoginUserBody();
        String url;
        if (type == RELOG_IN) {
            url = UrlConstants.DEFAUL_WEB_SITE_RELOGIN + UrlConstants.LOGIN;
        } else {
            url = UrlConstants.DEFAUL_INTERNATIONAL_WEB_SITE + UrlConstants.LOGIN;

            loginUserBody.setLogin(mPhone);//手机号
            loginUserBody.setPassword(mPwd);
            String mtype = android.os.Build.MODEL; // 手机型号
            loginUserBody.setDeviceName(mtype);
            loginUserBody.setAppCode("yuntu");
            loginUserBody.setAppType("2");
//            if (isEmail) {
//                loginUserBody.setRegionCode("international");
//
//            } else {
            loginUserBody.setRegionCode("international");
//            }
        }

        RetrofitFactory.getInstence().API().login(loginUserBody, url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MBaseObserver<LoginReceiveData>((Activity) getContext(), false) {
                    @Override
                    protected void onSuccees(BaseResult<LoginReceiveData> t) throws Exception {

                        LoginReceiveData loginReceiveData = t.getData();
                        if (type != RELOG_IN) {
                            LoginedUser loginedUser;
                            if (LoginedUser.getLoginedUser() != null) {
                                loginedUser = LoginedUser.getLoginedUser();
                            } else {
                                loginedUser = new LoginedUser();
                            }

                            if (loginReceiveData.getToken() != null) {
                                loginedUser.setTokenid(loginReceiveData.getTokenid());
                            }
                            if (!StringUtil.isBlank(loginReceiveData.getJwt())) {
                                loginedUser.setJwt(loginReceiveData.getJwt());

                            }
                            if (!StringUtil.isBlank(loginReceiveData.getWorkspace())) {
                                LoginReceiveData loginData = JsonUtil.Json2T(loginReceiveData.getWorkspace(), LoginReceiveData.class);
                                if (loginReceiveData.getWorkspaceId() != null) {
                                    loginedUser.setWorkspaceid(loginData.getWorkspaceId());
                                }
                                loginedUser.setNickname(loginData.getNickname());
                                loginedUser.setUser_name(loginUserBody.getLogin());
                                loginedUser.setPassword(loginUserBody.getPassword());
                                loginedUser.setLoginData(loginData);
                                loginedUser.setLogined(true);
                                loginedUser.setType(loginData.getType());
//                            loginedUser.setAvatar(loginReceiveData.getWorkspace().getAvatar());
                                loginedUser.setUploadStatus(Flag.UPLOAD_NONE);
                            } else {
                                if (loginReceiveData.getWorkspaceId() != null) {
                                    loginedUser.setWorkspaceid(loginReceiveData.getUserid());
                                }
                                loginedUser.setNickname(loginReceiveData.getNickname());
                                loginedUser.setUser_name(loginUserBody.getLogin());
                                loginedUser.setPassword(loginUserBody.getPassword());
                                loginedUser.setLoginData(loginReceiveData);
                                loginedUser.setLogined(true);
                                loginedUser.setType(loginReceiveData.getType());
                                loginedUser.setAvatar(loginReceiveData.getAvatar());
                                loginedUser.setUploadStatus(Flag.UPLOAD_NONE);
                            }
                            // 同步到本地文件
                            LoginedUser.setLoginedUser(loginedUser);
                            Log.d("LoginUser", loginedUser.toString());

                        }

                        if (null != classCut) {
                            classCut.exit = true;
                        }
                        closeKeyBoard();
                        if (type != RELOG_IN && !StringUtil.isBlank(loginReceiveData.getWorkspace())) {
                            mLoginType=type;
                            login(RELOG_IN);
                        } else {
                            installedAppsCheck(type);


                        }
                    }

                    @Override
                    protected void onCodeError(BaseResult<LoginReceiveData> t) throws Exception {
//                        if (type == LOG_IN||mLoginType==LOG_IN) {
//                            input_pwd.setFocusable(true);
//                            input_pwd.setFocusableInTouchMode(true);
//
//                            rl_pwd_content.setVisibility(VISIBLE);
//                            ll_pwd.setBackground(getContext().getResources().getDrawable(R.drawable.login_pop_bg));
//                            tv_input_pwd.setVisibility(VISIBLE);
//                            tv_input_pwd.setText(t.getMessage());
//                            tv_input_pwd.setClickable(false);
//                            input_number.setVisibility(INVISIBLE);
//                            input_verify.setVisibility(INVISIBLE);
//                            input_rePwd.setVisibility(INVISIBLE);
//                            input_pwd.setVisibility(INVISIBLE);
//                            rl_login_content.setVisibility(INVISIBLE);
//                            nextType = PWD_NEXT;
//                            startEditTime();
//                        } else if (type == REGISTER || type == FORGET_PWD||mLoginType == REGISTER || mLoginType == FORGET_PWD) {
//
//                            ToastUtil.toast(t.getMessage());
//                            closeKeyBoard();
//                            clearTextContent();
//                            mLoginInter.finishView();
//                        }
                        Log.d("LoginUser", t.getMessage());
                        loginCodeError(t.getMessage(),type);




                    }


                    @Override
                    protected void onFailure(Throwable e, boolean isNetWorkError) throws Exception {


                        loginCodeError(e.getMessage(),type);
//                        if (type == LOG_IN||mLoginType==LOG_IN) {
//                            input_pwd.setFocusable(true);
//                            input_pwd.setFocusableInTouchMode(true);
//
//                            rl_pwd_content.setVisibility(VISIBLE);
//                            ll_pwd.setBackground(getContext().getResources().getDrawable(R.drawable.login_pop_bg));
//                            tv_input_pwd.setVisibility(VISIBLE);
//                            tv_input_pwd.setText(e.getMessage());
//                            tv_input_pwd.setClickable(false);
//                            input_number.setVisibility(INVISIBLE);
//                            input_verify.setVisibility(INVISIBLE);
//                            input_rePwd.setVisibility(INVISIBLE);
//                            input_pwd.setVisibility(INVISIBLE);
//                            rl_login_content.setVisibility(INVISIBLE);
//                            nextType = PWD_NEXT;
//                            startEditTime();
//                        } else if (type == REGISTER || type == FORGET_PWD||mLoginType == REGISTER || mLoginType == FORGET_PWD) {
//
//                            ToastUtil.toast(e.getMessage());
//                            closeKeyBoard();
//                            clearTextContent();
//                            mLoginInter.finishView();
//                        }


                    }
                });
            /*loginInfoData.setSource("1");
            loginInfoData.setSystem("2");
            loginInfoData.setApparatus("1");
            loginInfoData.setVersion_number("1.0.1");
            loginInfoData.setAppid("1");*/
    }

    /**
     * 注册
     */
    private void regist() {

        RetrofitInternationalFactory.getInstence().API().registerAlien(new RegisterEmailBody(mPhone, mPwd, mNicekName,"international","yuntu"))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MBaseObserver<Object>((Activity) getContext(), true) {
                    @Override
                    protected void onSuccees(BaseResult<Object> t) throws Exception {
                        if (null == registerUsDialog) {
                            registerUsDialog = RegisterUsDialog.createInstance(getContext());
                        }
                        registerUsDialog.show();
                        registerUsDialog.setType(RegisterUsDialog.RREGISTER);
                        registerUsDialog.setAccount("<u>" + "<font color='#0057FF'>" + mPhone + "</font>" + "</u>");
                        registerUsDialog.setClickLitner(new RegisterUsDialog.ChoiceInterface() {
                            @Override
                            public void close() {
                                registerUsDialog.dismiss();
                                clearTextContent();
                                mLoginInter.finishView();
                            }

                            @Override
                            public void goLogin() {
                                registerUsDialog.dismiss();
                                login(REGISTER);
                            }
                        });
                    }

                    @Override
                    protected void onCodeError(BaseResult<Object> t) throws Exception {
                        input_verify.setFocusable(true);
                        input_verify.setFocusableInTouchMode(true);

                        rl_verify_content.setVisibility(VISIBLE);
                        ll_verify.setBackground(getContext().getResources().getDrawable(R.drawable.login_pop_bg));
                        tv_input_verify.setVisibility(VISIBLE);
                        tv_input_verify.setText(t.getMessage());
                        input_number.setVisibility(INVISIBLE);
                        input_verify.setVisibility(INVISIBLE);
                        input_rePwd.setVisibility(INVISIBLE);
                        input_pwd.setVisibility(INVISIBLE);
                        rl_login_content.setVisibility(INVISIBLE);
                        nextType = VERIFY_NEXT;
                        startEditTime();
                    }

                    @Override
                    protected void onFailure(Throwable e, boolean isNetWorkError) throws Exception {
                        input_verify.setFocusable(true);
                        input_verify.setFocusableInTouchMode(true);

                        rl_verify_content.setVisibility(VISIBLE);
                        ll_verify.setBackground(getContext().getResources().getDrawable(R.drawable.login_pop_bg));
                        tv_input_verify.setVisibility(VISIBLE);
                        tv_input_verify.setText(e.getMessage());
                        input_number.setVisibility(INVISIBLE);
                        input_verify.setVisibility(INVISIBLE);
                        input_rePwd.setVisibility(INVISIBLE);
                        input_pwd.setVisibility(INVISIBLE);
                        rl_login_content.setVisibility(INVISIBLE);
                        nextType = VERIFY_NEXT;
                        startEditTime();
                    }
                });
    }


    private void isExit(String phone) {

        if (UrlConstants.isWorld_Interface) {
            RetrofitInternationalFactory.getInstence().API().userIsExistWorld(new UserIsExistBody(phone, "yuntu", "international"))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new MBaseObserver<Object>((Activity) getContext(), true) {
                        @Override
                        protected void onSuccees(BaseResult<Object> t) throws Exception {
                            input_number.setFocusableInTouchMode(true);
                            input_number.setFocusable(true);
                            input_number.requestFocus();
                            tv_num_next.setClickable(true);
                            //用户存在
                            if (t.getCode() == 20072) {
                                ll_number.setBackground(getContext().getResources().getDrawable(R.drawable.login_pop_bg));
                                tv_input_number.setVisibility(VISIBLE);
                                tv_input_number.setText(t.getMessage());
                                input_pwd.setVisibility(INVISIBLE);
                                input_verify.setVisibility(INVISIBLE);
                                input_rePwd.setVisibility(INVISIBLE);
                                input_number.setVisibility(INVISIBLE);
                                nextType = NUMBER_NEXT_ISEXIT;
                                startEditTime();

//                    tv_num_next.setClickable(false);
//                    tv_num_next.setBackground(getResources().getDrawable(R.drawable.main_unable_bg));

                            }
                            pb_num.setVisibility(GONE);
                        }

                        @Override
                        protected void onCodeError(BaseResult<Object> t) throws Exception {
                            input_number.setFocusableInTouchMode(true);
                            input_number.setFocusable(true);
                            tv_num_next.setClickable(true);
                            //用户不存在
                            if (t.getCode() == 20071) {
//                    tv_num_next.setClickable(true);
//                    tv_num_next.setBackground(getResources().getDrawable(R.drawable.main_able_bg));
//
                                input_number.clearFocus();
                                rl_rePwd_content.setVisibility(VISIBLE);
                                openRePwdKeyBoard();
                                input_rePwd.setFocusable(true);
                                input_rePwd.requestFocus();
                                startFloatAnim(new AnimFactory.FloatListener() {
                                    @Override
                                    public void floatValueChang(float f) {

//                            openPwdKeyBoard();
                                        rl_number_content.setTranslationX(rl_content.getMeasuredHeight() * -f);
                                        rl_number_content.setAlpha(1 - f);

                                        rl_rePwd_content.setTranslationX(rl_content.getMeasuredHeight() * (1 - f));
                                        rl_rePwd_content.setAlpha(f);
                                    }
                                });
                            } else {
                                input_number.requestFocus();
                                ll_number.setBackground(getContext().getResources().getDrawable(R.drawable.login_pop_bg));
                                tv_input_number.setVisibility(VISIBLE);
                                tv_input_number.setText(t.getMessage());
                                input_pwd.setVisibility(INVISIBLE);
                                input_verify.setVisibility(INVISIBLE);
                                input_rePwd.setVisibility(INVISIBLE);
                                input_number.setVisibility(INVISIBLE);
                                nextType = NUMBER_NEXT_ISEXIT;
                                startEditTime();
//                    tv_num_next.setClickable(false);
//                    tv_num_next.setBackground(getResources().getDrawable(R.drawable.main_unable_bg));
                            }
                            pb_num.setVisibility(GONE);
                        }

                        @Override
                        protected void onFailure(Throwable e, boolean isNetWorkError) throws Exception {
                            input_number.setFocusableInTouchMode(true);
                            input_number.setFocusable(true);
                            tv_num_next.setClickable(true);
                            //用户不存在

                            input_number.requestFocus();
                            ll_number.setBackground(getContext().getResources().getDrawable(R.drawable.login_pop_bg));
                            tv_input_number.setVisibility(VISIBLE);
                            tv_input_number.setText(e.getMessage());
                            input_pwd.setVisibility(INVISIBLE);
                            input_verify.setVisibility(INVISIBLE);
                            input_rePwd.setVisibility(INVISIBLE);
                            input_number.setVisibility(INVISIBLE);
                            nextType = NUMBER_NEXT_ISEXIT;
                            startEditTime();
//                    tv_num_next.setClickable(false);
//                    tv_num_next.setBackground(getResources().getDrawable(R.drawable.main_unable_bg));

                            pb_num.setVisibility(GONE);
                        }
                    });
        } else {
            RetrofitFactory.getInstence().API().userIsExist(phone)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new MBaseObserver<Object>((Activity) getContext(), true) {
                        @Override
                        protected void onSuccees(BaseResult<Object> t) throws Exception {
                            input_number.setFocusableInTouchMode(true);
                            input_number.setFocusable(true);
                            input_number.requestFocus();
                            tv_num_next.setClickable(true);
                            //用户存在
                            if (t.getCode() == 20018) {
                                ll_number.setBackground(getContext().getResources().getDrawable(R.drawable.login_pop_bg));
                                tv_input_number.setVisibility(VISIBLE);
                                tv_input_number.setText(t.getMessage());
                                input_pwd.setVisibility(INVISIBLE);
                                input_verify.setVisibility(INVISIBLE);
                                input_rePwd.setVisibility(INVISIBLE);
                                input_number.setVisibility(INVISIBLE);
                                nextType = NUMBER_NEXT_ISEXIT;
                                startEditTime();

                            }
                            pb_num.setVisibility(GONE);
                        }

                        @Override
                        protected void onCodeError(BaseResult<Object> t) throws Exception {
                            input_number.setFocusableInTouchMode(true);
                            input_number.setFocusable(true);
                            tv_num_next.setClickable(true);
                            //用户不存在
                            if (t.getCode() == 20019) {

                                input_number.clearFocus();
                                rl_rePwd_content.setVisibility(VISIBLE);
                                openRePwdKeyBoard();
                                input_rePwd.setFocusable(true);
                                input_rePwd.requestFocus();
                                startFloatAnim(new AnimFactory.FloatListener() {
                                    @Override
                                    public void floatValueChang(float f) {

//                            openPwdKeyBoard();
                                        rl_number_content.setTranslationX(rl_content.getMeasuredHeight() * -f);
                                        rl_number_content.setAlpha(1 - f);

                                        rl_rePwd_content.setTranslationX(rl_content.getMeasuredHeight() * (1 - f));
                                        rl_rePwd_content.setAlpha(f);
                                    }
                                });
                            } else {
                                input_number.requestFocus();
                                ll_number.setBackground(getContext().getResources().getDrawable(R.drawable.login_pop_bg));
                                tv_input_number.setVisibility(VISIBLE);
                                tv_input_number.setText(t.getMessage());
                                input_pwd.setVisibility(INVISIBLE);
                                input_verify.setVisibility(INVISIBLE);
                                input_rePwd.setVisibility(INVISIBLE);
                                input_number.setVisibility(INVISIBLE);
                                nextType = NUMBER_NEXT_ISEXIT;
                                startEditTime();
//                    tv_num_next.setClickable(false);
//                    tv_num_next.setBackground(getResources().getDrawable(R.drawable.main_unable_bg));
                            }
                            pb_num.setVisibility(GONE);
                        }

                        @Override
                        protected void onFailure(Throwable e, boolean isNetWorkError) throws Exception {
                            input_number.setFocusableInTouchMode(true);
                            input_number.setFocusable(true);
                            tv_num_next.setClickable(true);
                            //用户不存在

                            input_number.requestFocus();
                            ll_number.setBackground(getContext().getResources().getDrawable(R.drawable.login_pop_bg));
                            tv_input_number.setVisibility(VISIBLE);
                            tv_input_number.setText(e.getMessage());
                            input_pwd.setVisibility(INVISIBLE);
                            input_verify.setVisibility(INVISIBLE);
                            input_rePwd.setVisibility(INVISIBLE);
                            input_number.setVisibility(INVISIBLE);
                            nextType = NUMBER_NEXT_ISEXIT;
                            startEditTime();
                            pb_num.setVisibility(GONE);
                        }
                    });
        }
    }

    private void installedAppsCheck(final int type) {
        String url = UrlConstants.BASE_INSTALLED_APP + UrlConstants.SETTING_CHECK;
        RetrofitFactory.getInstence().API().settingCheck(url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MBaseObserver<Object>((Activity) getContext(), false) {
                    @Override
                    protected void onSuccees(BaseResult<Object> t) throws Exception {
                        //有服务未初始化
                        if (t.getCode() == 10004) {
                            installedAppsInit(type);
                        }else {
                            dgProgressDialog3.dismiss();

                            rl_pwd_content.setVisibility(GONE);
                            rl_verify_content.setVisibility(GONE);
                            rl_login_content.setVisibility(VISIBLE);

                            startAsyncTime();
                        }
                    }

                    @Override
                    protected void onCodeError(BaseResult<Object> t) throws Exception {
                        //10003 所有服务均已初始化
                        if (t.getCode() != 10003) {
                            installedAppsInit(type);
                        }else {

                            dgProgressDialog3.dismiss();

                            rl_pwd_content.setVisibility(GONE);
                            rl_verify_content.setVisibility(GONE);
                            rl_login_content.setVisibility(VISIBLE);

                            startAsyncTime();

//                            input_pwd.setFocusable(true);
//                            input_pwd.setFocusableInTouchMode(true);
//
//                            rl_pwd_content.setVisibility(VISIBLE);
//                            ll_pwd.setBackground(getContext().getResources().getDrawable(R.drawable.login_pop_bg));
//                            tv_input_pwd.setVisibility(VISIBLE);
//                            tv_input_pwd.setText(t.getMessage());
//                            tv_input_pwd.setClickable(false);
//                            input_number.setVisibility(INVISIBLE);
//                            input_verify.setVisibility(INVISIBLE);
//                            input_rePwd.setVisibility(INVISIBLE);
//                            input_pwd.setVisibility(INVISIBLE);
//                            rl_login_content.setVisibility(INVISIBLE);
//                            nextType = PWD_NEXT;
//                            startEditTime();

//                            loginCodeError(t.getMessage(),type);

                        }
                    }

                    @Override
                    protected void onFailure(Throwable e, boolean isNetWorkError) throws Exception {

                        loginCodeError(e.getMessage(),type);

                    }
                });


    }

    private void installedAppsInit(final int type) {
        String url = UrlConstants.BASE_INSTALLED_APP + UrlConstants.SETTING_INIT;
        RetrofitFactory.getInstence().API().settingInit(url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MBaseObserver<Object>((Activity) getContext(), false) {
                    @Override
                    protected void onSuccees(BaseResult<Object> t) throws Exception {

                        dgProgressDialog3.dismiss();

                        rl_pwd_content.setVisibility(GONE);
                        rl_verify_content.setVisibility(GONE);
                        rl_login_content.setVisibility(VISIBLE);

                        startAsyncTime();
                    }

                    @Override
                    protected void onCodeError(BaseResult<Object> t) throws Exception {
                       loginCodeError(t.getMessage(),type);
                    }

                    @Override
                    protected void onFailure(Throwable e, boolean isNetWorkError) throws Exception {
                        loginCodeError(e.getMessage(),type);
                    }
                });

    }

    /**
     * 发送忘记密码邮件
     */
    private void sendEmail() {
        RetrofitInternationalFactory.getInstence().API().sendEmail(new SendEmailBody(mPhone))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MBaseObserver<Object>((Activity) getContext(), false) {
                    @Override
                    protected void onSuccees(BaseResult<Object> t) throws Exception {
                        if (null == registerUsDialog) {
                            registerUsDialog = RegisterUsDialog.createInstance(getContext());
                        }
                        registerUsDialog.show();
                        registerUsDialog.setAccount("<u>" + "<font color='#0057FF'>" + mPhone + "</font>" + "</u>");
                        registerUsDialog.setClickLitner(new RegisterUsDialog.ChoiceInterface() {
                            @Override
                            public void close() {
                                registerUsDialog.dismiss();
                                clearTextContent();
                                mLoginInter.finishView();
                            }

                            @Override
                            public void goLogin() {

                            }
                        });
                        registerUsDialog.setType(RegisterUsDialog.FORGEST_PWD);
                    }

                    @Override
                    protected void onCodeError(BaseResult<Object> t) throws Exception {
                        ToastUtil.toast(t.getMessage());

                    }

                    @Override
                    protected void onFailure(Throwable e, boolean isNetWorkError) throws Exception {
                        ToastUtil.toast(e.getMessage());

                    }
                });
    }

    public void clearTextContent() {
        input_verify.setText("");
        input_number.setText("");
        input_pwd.setText("");
        input_rePwd.setText("");

    }

    /**
     * 开启倒计时
     */
    public void starttime() {
        i = 60;
        classCut = new ClassCut();
        classCut.start();
    }

    class ClassCut extends Thread implements Runnable {//倒计时逻辑子线程

        private volatile boolean exit = false;

        @Override
        public void run() {
            while (i > 1) {//整个倒计时执行的循环
                if (exit) {
                    break;
                }
                i--;
                mHandler.post(new Runnable() {//通过它在UI主线程中修改显示的剩余时间
                    @Override
                    public void run() {
                        title_verify.setRightTextCanClick("<u>" + "<font color='#000000'>" + String.format("%02d", i) + "s Resend Email</font>" + "</u>", false);
//                            input_phone_right.setClickable(false);
//                            input_phone_right.setText(i + "s");//显示剩余时间
                    }

                });

                try {
                    Thread.sleep(1000);//线程休眠一秒钟     这个就是倒计时的间隔时间
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
            //下面是倒计时结束逻辑
            mHandler.post(new Runnable() {
                @Override
                public void run() {
//                        input_phone_right.setClickable(true);
//                        input_phone_right.setText(getResources().getString(R.string.send_captcha));
                    title_verify.setRightTextCanClick("<u>" + "<font color='#000000'>" + "Resend Email" + "</font>" + "</u>", true);
                }
            });
            i = 60;//修改倒计时剩余时间变量为60秒
        }
    }

    /**
     * 开启倒计时
     */
    public void startAsyncTime() {
        asynTime = 1500;
        asyncCut = new ClassAsyncCut();
        asyncCut.start();
    }

    class ClassAsyncCut extends Thread implements Runnable {//倒计时逻辑子线程

        private volatile boolean exit = false;

        @Override
        public void run() {
            while (asynTime > 0) {//整个倒计时执行的循环
                if (exit) {
                    break;
                }
                asynTime = (asynTime - 1);
                mHandler.post(new Runnable() {//通过它在UI主线程中修改显示的剩余时间
                    @Override
                    public void run() {
                        pb_progressbar.incrementProgressBy(1);
                    }

                });

                try {
                    Thread.sleep(1);//线程休眠
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
            //下面是倒计时结束逻辑
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    SharePreferences preferences = SharePreferences.instance();
                    preferences.putBoolean("isFirstIn", false);

                    Intent intent = new Intent(getContext(), AlbumNewActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    getContext().startActivity(intent);
                    mLoginInter.onLogin();
                }
            });
            asynTime = 1500;
        }
    }

    /**
     * 开启倒计时
     */
    public void startEditTime() {
        errorTime = 1;
        editCut = new ClassEditCut();
        editCut.start();
    }

    class ClassEditCut extends Thread implements Runnable {//倒计时逻辑子线程


        @Override
        public void run() {
            while (errorTime > 0) {//整个倒计时执行的循环
                errorTime--;
                LogUtil.d(errorTime + "");

                try {
                    Thread.sleep(1000);//线程休眠一秒钟     这个就是倒计时的间隔时间
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
            //下面是倒计时结束逻辑
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (nextType == NUMBER_NEXT) {
                        ll_number.setBackgroundColor(getContext().getResources().getColor(R.color.backgroud_new));
                        tv_input_number.setVisibility(INVISIBLE);
                        input_number.setVisibility(VISIBLE);
                        input_pwd.setVisibility(VISIBLE);
                        input_verify.setVisibility(VISIBLE);
                        input_rePwd.setVisibility(VISIBLE);
                        tv_num_next.setClickable(true);
                    } else if (nextType == PWD_NEXT) {
                        ll_pwd.setBackgroundColor(getContext().getResources().getColor(R.color.backgroud_new));
                        tv_input_pwd.setVisibility(INVISIBLE);
                        input_pwd.setVisibility(VISIBLE);
                        input_number.setVisibility(VISIBLE);
                        input_verify.setVisibility(VISIBLE);
                        input_rePwd.setVisibility(VISIBLE);
                        tv_pwd_next.setClickable(true);
                    } else if (nextType == REPWD_NEXT) {
                        ll_rePwd.setBackgroundColor(getContext().getResources().getColor(R.color.backgroud_new));
                        tv_input_rePwd.setVisibility(INVISIBLE);
                        input_rePwd.setVisibility(VISIBLE);
                        input_verify.setVisibility(VISIBLE);
                        input_pwd.setVisibility(VISIBLE);
                        input_number.setVisibility(VISIBLE);
                        tv_rePwd_next.setClickable(true);

                    } else if (nextType == NUMBER_NEXT_ISEXIT) {
                        ll_number.setBackgroundColor(getContext().getResources().getColor(R.color.backgroud_new));
                        tv_input_number.setVisibility(INVISIBLE);
                        input_number.setVisibility(VISIBLE);
                        input_pwd.setVisibility(VISIBLE);
                        input_verify.setVisibility(VISIBLE);
                        input_rePwd.setVisibility(VISIBLE);
                    } else if (nextType == VERIFY_NEXT) {
                        ll_verify.setBackgroundColor(getContext().getResources().getColor(R.color.backgroud_new));
                        tv_input_verify.setVisibility(INVISIBLE);
                        input_rePwd.setVisibility(VISIBLE);
                        input_verify.setVisibility(VISIBLE);
                        input_pwd.setVisibility(VISIBLE);
                        input_number.setVisibility(VISIBLE);
                        tv_verify_next.setClickable(true);
                    }

                }
            });
            errorTime = 1;//修改倒计时剩余时间变量为3秒
        }
    }


    public interface ClickInter {
        void onClick();
    }

    public void setClickInter(MLoginNewView.ClickInter clickInter) {
        this.clickInter = clickInter;
    }


    /**
     * 错误提示
     * @param message 错误信息
     * @param mType 错误操作类型
     */
    private void loginCodeError(String message,int mType){

        if(mType==LOG_IN){
            input_pwd.setFocusable(true);
            input_pwd.setFocusableInTouchMode(true);

            rl_pwd_content.setVisibility(VISIBLE);
            ll_pwd.setBackground(getContext().getResources().getDrawable(R.drawable.login_pop_bg));
            tv_input_pwd.setVisibility(VISIBLE);
            tv_input_pwd.setText(message);
            tv_input_pwd.setClickable(false);
            input_number.setVisibility(INVISIBLE);
            input_verify.setVisibility(INVISIBLE);
            input_rePwd.setVisibility(INVISIBLE);
            input_pwd.setVisibility(INVISIBLE);
            rl_login_content.setVisibility(INVISIBLE);
            nextType = PWD_NEXT;
            startEditTime();
        }else if(mType==RELOG_IN){
            if(mLoginType==LOG_IN){
                input_pwd.setFocusable(true);
                input_pwd.setFocusableInTouchMode(true);

                rl_pwd_content.setVisibility(VISIBLE);
                ll_pwd.setBackground(getContext().getResources().getDrawable(R.drawable.login_pop_bg));
                tv_input_pwd.setVisibility(VISIBLE);
                tv_input_pwd.setText(message);
                tv_input_pwd.setClickable(false);
                input_number.setVisibility(INVISIBLE);
                input_verify.setVisibility(INVISIBLE);
                input_rePwd.setVisibility(INVISIBLE);
                input_pwd.setVisibility(INVISIBLE);
                rl_login_content.setVisibility(INVISIBLE);
                nextType = PWD_NEXT;
                startEditTime();
            }else if(mLoginType == REGISTER || mLoginType == FORGET_PWD){
                ToastUtil.toast(message);
                closeKeyBoard();
                clearTextContent();
                mLoginInter.finishView();
            }
        }else if (mType == REGISTER || mType == FORGET_PWD) {
            ToastUtil.toast(message);
            closeKeyBoard();
            clearTextContent();
            mLoginInter.finishView();
        }

        dgProgressDialog3.dismiss();
    }



}



