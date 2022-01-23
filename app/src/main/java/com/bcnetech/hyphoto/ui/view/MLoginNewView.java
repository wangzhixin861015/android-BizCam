package com.bcnetech.hyphoto.ui.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bcnetech.bcnetchhttp.RetrofitFactory;
import com.bcnetech.bcnetchhttp.RetrofitInternationalFactory;
import com.bcnetech.bcnetchhttp.base.BaseResult;
import com.bcnetech.bcnetchhttp.bean.request.ForgetResetBody;
import com.bcnetech.bcnetchhttp.bean.request.LoginUserBody;
import com.bcnetech.bcnetchhttp.bean.request.RegisterMobileBody;
import com.bcnetech.bcnetchhttp.bean.request.SendCodeBody;
import com.bcnetech.bcnetchhttp.bean.request.UserIsExistBody;
import com.bcnetech.bcnetchhttp.bean.request.ValidateCodeBody;
import com.bcnetech.bcnetchhttp.bean.response.LoginReceiveData;
import com.bcnetech.bcnetchhttp.bean.response.LoginedUser;
import com.bcnetech.bcnetchhttp.config.Flag;
import com.bcnetech.bcnetchhttp.config.UrlConstants;
import com.bcnetech.bcnetechlibrary.dialog.ChoiceDialog;
import com.bcnetech.bcnetechlibrary.dialog.DGProgressDialog3;
import com.bcnetech.bcnetechlibrary.util.AnimFactory;
import com.bcnetech.bcnetechlibrary.util.LogUtil;
import com.bcnetech.bcnetechlibrary.util.StringUtil;
import com.bcnetech.bcnetechlibrary.util.ToastUtil;
import com.bcnetech.bcnetechlibrary.util.sharepreference.SharePreferences;
import com.bcnetech.bcnetechlibrary.view.LimitBackEditText;
import com.bcnetech.hyphoto.task.MBaseObserver;
import com.bcnetech.hyphoto.ui.activity.AlbumNewActivity;
import com.bcnetech.hyphoto.ui.dialog.RegisterUsDialog;
import com.bcnetech.hyphoto.utils.FontImageUtil;
import com.bcnetech.hyphoto.utils.JsonUtil;
import com.bcnetech.hyphoto.utils.KeyBoardUtil;
import com.bcnetech.hyphoto.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


/**
 * Created by yhf on 17/11/20.
 */
public class MLoginNewView extends RelativeLayout {
    private RelativeLayout rl_content;


    public MLoginInter mLoginInter;

    private ValueAnimator valueAnimator;

    private RelativeLayout rl_number_content;
    private TitleView titleView;
    private LimitBackEditText input_number;
    private ImageView num_clear;
    private TextView tv_num_next;
    private TextView tv_message;
    private TextView tv_num;
    private LinearLayout ll_number;
    private TextView tv_input_number;
    private ProgressBar pb_num;

    private RelativeLayout rl_pwd_content;
    private TitleView title_pwd;
    private LimitBackEditText input_pwd;
    private TextView tv_pwd_next;
    private ImageView pwd_clear;
    private LinearLayout ll_pwd;
    private TextView tv_input_pwd;
    private TextView tv_pwd_message;
    private ProgressBar pb_pwd;


    private LimitBackEditText input_verify;
    private TextView tv_input_verify;
    private ImageView verify_clear;
    private TextView tv_verify_next;


    private int type;
    private ChoiceDialog mchoiceDialog;
    private RelativeLayout rl_verify_content;
    private TitleView title_verify;
    private LinearLayout input_verify_ll;
    private LinearLayout ll_verify;
    private ProgressBar pb_verify;


    private RelativeLayout rl_rePwd_content;
    private TitleView title_rePwd;
    private LimitBackEditText input_rePwd;
    private ImageView rePwd_clear;
    private TextView tv_rePwd_next;
    private TextView tv_rePwd;
    private TextView tv_input_rePwd;
    private LinearLayout ll_rePwd;
    private TextView tv_rePwdMessage;

    private RelativeLayout rl_login_content;
    private ProgressBar pb_progressbar;
    private RegisterUsDialog registerUsDialog;

    // private LoginDialog loginDialog;
//    private ProgressDialog dgProgressDialog2;


    private ClassCut classCut;
    private ClassEditCut editCut;
    private ClassAsyncCut asyncCut;
    private Handler mHandler = new Handler();//全局handler
    private int i = 60;//倒计时的整个时间数
    private int errorTime = 1;//错误倒计时
    private int asynTime = 1500;

    //电话
    private String mPhone;
    //密码
    private String mPwd;
    //验证码
    private String mCode;
    //昵称
    private String nickName;

    private String forgetMphone;

    private int nextType;

    private final static int NUMBER_NEXT = 1;
    private final static int NUMBER_NEXT_ISEXIT = 5;
    private final static int PWD_NEXT = 2;
    private final static int REPWD_NEXT = 3;
    private final static int VERIFY_NEXT = 4;
    private final static int VERIFY_NEXT_VALATE = 6;

    private final static int LOG_IN = 1;
    private final static int REGISTER = 2;
    private final static int FORGET_PWD = 3;
    private final static int RELOG_IN = 4;

    private boolean hint = true;
    private boolean rePwdHint = true;
    //    private boolean isEmail = false;
    ClickInter clickInter;


    public MLoginNewView(Context context) {
        super(context);
        initView();
        initData();
        onViewClick();
        initEditText();
    }

    public MLoginNewView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
        initData();
        onViewClick();
        initEditText();
    }

    public MLoginNewView(Context context, AttributeSet attrs, int defStyleAttr) {
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
            title_rePwd.setType(TitleView.MAIN_LOGIN_REPWD);
            tv_num.setText("账号");
//            tv_message.setText("手机号码/子账号");
            tv_num_next.setText(getResources().getString(R.string.next_step));
            tv_rePwd.setText("新密码");
            input_rePwd.setHint(getResources().getString(R.string.enter_a_valid_password));
            title_pwd.setType(TitleView.MAIN_LOGIN_PWD);
            title_rePwd.setType(TitleView.MAIN_LOGIN);

            input_pwd.setTextSize(TypedValue.COMPLEX_UNIT_SP, 28);
            tv_rePwd_next.setText("确定");
            input_rePwd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            if (null != LoginedUser.getLoginedUser()) {
                if (!StringUtil.isBlank(LoginedUser.getLoginedUser().getUser_name())) {
                    if (!StringUtil.isBlank(LoginedUser.getLoginedUser().getUser_name())) {
                        String phone = LoginedUser.getLoginedUser().getUser_name();
                        phone = phone.substring(0, 3) + " " + phone.substring(3);
                        phone = phone.substring(0, 8) + " " + phone.substring(8);
                        input_number.setText(phone);
                        input_number.setSelection(phone.length());
                    }
                }
            }
        } else {
            tv_num.setText("账号");
//            tv_message.setText("账号");
            tv_num_next.setText(getResources().getString(R.string.next_step));
//            tv_rePwd_next.setText(getResources().getString(R.string.set_up));
            tv_rePwd.setText(getResources().getString(R.string.count_name));
            tv_pwd_next.setText(getResources().getString(R.string.next_step));
            title_rePwd.setType(TitleView.MAIN_LOGIN_VERIFY);
            title_pwd.setType(TitleView.MAIN_LOGIN);
            input_pwd.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            input_rePwd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
            input_pwd.setHint(getResources().getString(R.string.enter_a_valid_password));

        }
    }

    protected void initData() {
        titleView.setType(TitleView.MAIN_LOGIN);
        title_verify.setType(TitleView.MAIN_LOGIN_VERIFY);
        KeyBoardUtil.openKeybord(input_number, getContext());
    }

    public void setMLoginInter(MLoginInter mLoginInter) {
        this.mLoginInter = mLoginInter;
    }

    protected void initView() {
        inflate(getContext(), R.layout.mlogin_new_layout, this);
        rl_content = (RelativeLayout) findViewById(R.id.rl_content);
        rl_number_content = (RelativeLayout) findViewById(R.id.rl_number_content);
        titleView = (TitleView) findViewById(R.id.title_layout);
        input_number = findViewById(R.id.input_number);
        num_clear = (ImageView) findViewById(R.id.num_clear);
        tv_num_next = (TextView) findViewById(R.id.tv_num_next);
        tv_num = (TextView) findViewById(R.id.tv_num);
        tv_message = (TextView) findViewById(R.id.tv_message);
        ll_number = (LinearLayout) findViewById(R.id.ll_number);
        tv_input_number = (TextView) findViewById(R.id.tv_input_number);
        pb_num = (ProgressBar) findViewById(R.id.pb_num);

        rl_pwd_content = (RelativeLayout) findViewById(R.id.rl_pwd_content);
        title_pwd = (TitleView) findViewById(R.id.title_pwd);
        input_pwd = findViewById(R.id.input_pwd);
        tv_pwd_next = (TextView) findViewById(R.id.tv_pwd_next);
        pwd_clear = (ImageView) findViewById(R.id.pwd_clear);
        ll_pwd = (LinearLayout) findViewById(R.id.ll_pwd);
        tv_input_pwd = (TextView) findViewById(R.id.tv_input_pwd);
        tv_pwd_message = (TextView) findViewById(R.id.tv_pwd_message);
        pb_pwd = (ProgressBar) findViewById(R.id.pb_pwd);


        tv_verify_next = (TextView) findViewById(R.id.tv_verify_next);
        input_verify = findViewById(R.id.input_verify);
        tv_input_verify = (TextView) findViewById(R.id.tv_input_verify);
        verify_clear = (ImageView) findViewById(R.id.verify_clear);
        rl_verify_content = (RelativeLayout) findViewById(R.id.rl_verify_content);
        title_verify = (TitleView) findViewById(R.id.title_verify);
        input_verify_ll = (LinearLayout) findViewById(R.id.input_verify_ll);
        ll_verify = (LinearLayout) findViewById(R.id.ll_verify);
        ll_verify = (LinearLayout) findViewById(R.id.ll_verify);
        pb_verify = (ProgressBar) findViewById(R.id.pb_verify);

        rl_rePwd_content = (RelativeLayout) findViewById(R.id.rl_rePwd_content);
        title_rePwd = (TitleView) findViewById(R.id.title_rePwd);
        input_rePwd = (LimitBackEditText) findViewById(R.id.input_rePwd);
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
        tv_num_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickInter != null)
                    clickInter.onClick();
                String phone = input_number.getText().toString().replace(" ", "").trim();
                if (StringUtil.isBlank(phone)) {
                    ll_number.setBackground(getContext().getResources().getDrawable(R.drawable.login_pop_bg));
                    tv_input_number.setVisibility(VISIBLE);
                    tv_input_number.setText(getResources().getString(R.string.isphone));
                    input_pwd.setVisibility(INVISIBLE);
                    input_verify.setVisibility(INVISIBLE);
                    input_rePwd.setVisibility(INVISIBLE);
                    input_number.setVisibility(INVISIBLE);
                    tv_num_next.setClickable(false);
                    nextType = NUMBER_NEXT;
                    startEditTime();
                    return;
                }
//

                if (type == Flag.TYPE_LOGIN) {
                    title_pwd.getLeft_img().setImageResource(R.drawable.arrow_back);

                    mPhone = phone;
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


                    title_pwd.getLeft_img().setImageResource(R.drawable.new_close);

                    if (null != classCut) {
                        classCut.exit = true;
                    }

                    tv_num_next.setClickable(false);
                    num_clear.setVisibility(GONE);
                    pb_num.setVisibility(VISIBLE);
                    input_number.clearFocus();
                    input_number.setFocusableInTouchMode(false);
                    input_number.setFocusable(false);
                    isExit(phone);

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

                if (!isPhoneNumberValid(mPhone)) {
                    ll_pwd.setBackground(getContext().getResources().getDrawable(R.drawable.login_pop_bg));
                    tv_input_pwd.setVisibility(VISIBLE);
                    tv_input_pwd.setText(getResources().getString(R.string.isphone));
                    input_number.setVisibility(INVISIBLE);
                    input_verify.setVisibility(INVISIBLE);
                    input_rePwd.setVisibility(INVISIBLE);
                    input_pwd.setVisibility(INVISIBLE);
                    tv_pwd_next.setClickable(false);
                    nextType = PWD_NEXT;
                    startEditTime();
                    return;
                }

                if (pwd == null || TextUtils.isEmpty(pwd)) {
                    ll_pwd.setBackground(getContext().getResources().getDrawable(R.drawable.login_pop_bg));
                    tv_input_pwd.setVisibility(VISIBLE);
                    tv_input_pwd.setText(getResources().getString(R.string.empty_psw));
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
                    tv_input_pwd.setText(getResources().getString(R.string.short_psw));
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
                if (type == Flag.TYPE_LOGIN) {

                    pb_pwd.setVisibility(VISIBLE);
                    pwd_clear.setVisibility(GONE);
                    tv_pwd_next.setClickable(false);

//                    closeKeyBoard();
//                    rl_pwd_content.setVisibility(GONE);
//                    rl_login_content.setVisibility(VISIBLE);
                    input_pwd.setFocusable(false);
                    input_pwd.setFocusableInTouchMode(false);
                    login(LOG_IN);
                } else {
                    input_pwd.clearFocus();
                    rl_rePwd_content.setVisibility(VISIBLE);
                    openRePwdKeyBoard();
                    input_rePwd.setFocusable(true);
                    input_rePwd.requestFocus();

                    startFloatAnim(new AnimFactory.FloatListener() {
                        @Override
                        public void floatValueChang(float f) {
                            rl_pwd_content.setTranslationX(rl_content.getMeasuredHeight() * -f);
                            rl_pwd_content.setAlpha(1 - f);

                            rl_rePwd_content.setTranslationX(rl_content.getMeasuredHeight() * (1 - f));
                            rl_rePwd_content.setAlpha(f);
                        }
                    });
                }


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

                if (type == Flag.TYPE_LOGIN) {
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
                    //注册逻辑
                } else {
                    //邮箱注册

                    //手机号注册
                    KeyBoardUtil.closeKeybord(input_pwd, getContext());
                    classCut.exit = true;
                    clearTextContent();
                    mLoginInter.finishView();


                }

            }
            /**
             * 下一步
             * 忘记密码
             */
        }).setRightTextListener(new OnClickListener() {
            @Override
            public void onClick(View v) {


                if (!StringUtil.isBlank(mPhone) && mPhone.equals(forgetMphone)) {
                    input_pwd.clearFocus();
//                        rl_verify_content.setVisibility(VISIBLE);
                    openVerifyKeyBoard();
                    input_verify.setFocusable(true);
                    input_verify.requestFocus();
                    startFloatAnim(new AnimFactory.FloatListener() {
                        @Override
                        public void floatValueChang(float f) {

                            rl_pwd_content.setTranslationX(rl_content.getMeasuredHeight() * -f);
                            rl_pwd_content.setAlpha(1 - f);

                            rl_verify_content.setTranslationX(rl_content.getMeasuredHeight() * (1 - f));
                            rl_verify_content.setAlpha(f);
                        }
                    });
                } else {

                    if (null != classCut) {
                        classCut.exit = true;
                    }
                    showChoiceDialog(mPhone);
                }


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
                    openPwdKeyBoard();
                    input_pwd.setFocusable(true);
                    input_pwd.requestFocus();

                    startFloatAnim(new AnimFactory.FloatListener() {
                        @Override
                        public void floatValueChang(float f) {

                            rl_pwd_content.setTranslationX(-rl_content.getMeasuredHeight() * (1 - f));
                            rl_pwd_content.setAlpha(f);

                            rl_verify_content.setTranslationX(rl_content.getMeasuredHeight() * f);
                            rl_verify_content.setAlpha(1 - f);
                        }
                    });
                } else {
                    input_verify.clearFocus();

                    openNumKeyBoard();

                    input_number.setFocusable(true);
                    input_number.requestFocus();
                    startFloatAnim(new AnimFactory.FloatListener() {
                        @Override
                        public void floatValueChang(float f) {


                            rl_number_content.setTranslationX(-rl_content.getMeasuredHeight() * (1 - f));
                            rl_number_content.setAlpha(f);

                            rl_verify_content.setTranslationX(rl_content.getMeasuredHeight() * f);
                            rl_verify_content.setAlpha(1 - f);
                        }
                    });
                }

                input_verify.setText("");


                /**
                 * 重新发送验证码
                 */
            }
            /**
             * 重新发送验证码
             */
        }).setRightTextListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                sendcode(mPhone);
                starttime();
            }
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

                    openPwdKeyBoard();
                    input_pwd.setFocusable(true);
                    input_pwd.requestFocus();

                    startFloatAnim(new AnimFactory.FloatListener() {
                        @Override
                        public void floatValueChang(float f) {
                            //弹入
                            rl_pwd_content.setTranslationX(-rl_content.getMeasuredHeight() * (1 - f));
                            rl_pwd_content.setAlpha(f);

                            //弹出
                            rl_rePwd_content.setTranslationX(rl_content.getMeasuredHeight() * f);
                            rl_rePwd_content.setAlpha(1 - f);
                        }
                    });

                    input_rePwd.setText("");
                }
            }
        });


        /**
         * 下一步
         * 修改密码并登陆 或 注册并登录
         */
        tv_rePwd_next.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                input_rePwd.setClickable(false);

                input_rePwd.setFocusable(false);
                input_rePwd.setFocusableInTouchMode(false);
                if (type == Flag.TYPE_LOGIN) {
                    //新密码
                    mPwd = input_rePwd.getText().toString();
                    resetPwd(mCode);
                } else {
                    //昵称
                    nickName = input_rePwd.getText().toString();


                    regist(mCode);

                }
            }
        });

        tv_rePwd_next.setClickable(false);


        num_clear.setOnClickListener(new View.OnClickListener() {
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
     * 确认是否发送验证码
     */
    public void showChoiceDialog(final String phone) {
        if (mchoiceDialog == null) {
            mchoiceDialog = ChoiceDialog.createInstance(getContext());
        }
//        StringBuilder sb = new StringBuilder();
//        for (int i = 0; i < phone.length(); i++) {
//            if (i != 3 && i != 8 && phone.charAt(i) == ' ') {
//                continue;
//            } else {
//                sb.append(phone.charAt(i));
//                if ((sb.length() == 4 || sb.length() == 9) && sb.charAt(sb.length() - 1) != ' ') {
//                    sb.insert(sb.length() - 1, ' ');
//                }
//            }
//        }
        closeKeyBoard();
        mchoiceDialog.isNeedBlur(false);
        mchoiceDialog.show();
        mchoiceDialog.setTitle(getContext().getResources().getString(R.string.send_captcha));
        mchoiceDialog.setCancel(getContext().getResources().getString(R.string.cancel));
        mchoiceDialog.setOk(getContext().getResources().getString(R.string.confirm));
        mchoiceDialog.setMessage(input_number.getText().toString());
        mchoiceDialog.setChoiceCallback(new ChoiceDialog.CardDialogCallback() {
            @Override
            public void onOKClick() {


                dissmissChoiceDialog();
                //发送验证码
                sendcode(phone);
                if (type == Flag.TYPE_LOGIN) {
                    forgetMphone = mPhone;
                    input_pwd.clearFocus();
                    rl_verify_content.setVisibility(VISIBLE);
                    openRePwdKeyBoard();
                    input_verify.setFocusable(true);
                    input_verify.requestFocus();
                    startFloatAnim(new AnimFactory.FloatListener() {
                        @Override
                        public void floatValueChang(float f) {

//                            openPwdKeyBoard();
                            rl_pwd_content.setTranslationX(rl_content.getMeasuredHeight() * -f);
                            rl_pwd_content.setAlpha(1 - f);

                            rl_verify_content.setTranslationX(rl_content.getMeasuredHeight() * (1 - f));
                            rl_verify_content.setAlpha(f);
                        }
                    });

                } else {
                    mPhone = phone;
                    input_number.clearFocus();
                    rl_verify_content.setVisibility(VISIBLE);
                    openVerifyKeyBoard();
                    input_verify.setFocusable(true);
                    input_verify.requestFocus();
                    startFloatAnim(new AnimFactory.FloatListener() {
                        @Override
                        public void floatValueChang(float f) {

                            rl_number_content.setTranslationX(rl_content.getMeasuredHeight() * -f);
                            rl_number_content.setAlpha(1 - f);

                            rl_verify_content.setTranslationX(rl_content.getMeasuredHeight() * (1 - f));
                            rl_verify_content.setAlpha(f);
                        }
                    });
                }

                starttime();
            }

            @Override
            public void onCancelClick() {
                if (type == Flag.TYPE_REGIST) {
                    mPhone = "";
                }
                dissmissChoiceDialog();
            }

            @Override
            public void onDismiss() {

            }
        });
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

                String contents = s.toString();
                int length = contents.length();
                if (length == 4) {
                    if (contents.substring(3).equals(new String(" "))) { // -
                        contents = contents.substring(0, 3);
                        input_number.setText(contents);
                        input_number.setSelection(contents.length());
                    } else { // +
                        contents = contents.substring(0, 3) + " " + contents.substring(3);
                        input_number.setText(contents);
                        input_number.setSelection(contents.length());
                    }
                } else if (length == 9) {
                    if (contents.substring(8).equals(new String(" "))) { // -
                        contents = contents.substring(0, 8);
                        input_number.setText(contents);
                        input_number.setSelection(contents.length());
                    } else {// +
                        contents = contents.substring(0, 8) + " " + contents.substring(8);
                        input_number.setText(contents);
                        input_number.setSelection(contents.length());
                    }
                }

                if (0 != input_number.getText().toString().length()) {
                    if (input_number.getText().toString().equals("请输入正确的手机号")) {
                        num_clear.setVisibility(View.GONE);
                        return;
                    } else {
                        num_clear.setVisibility(View.VISIBLE);
                    }
                } else {
                    num_clear.setVisibility(View.GONE);
                }
//

                if (isPhoneNumberValid(input_number.getText().toString().replace(" ", "").trim())) {
                    tv_num_next.setClickable(true);
                    tv_num_next.setBackground(getResources().getDrawable(R.drawable.main_able_bg));

                } else {
                    tv_num_next.setClickable(false);
                    tv_num_next.setBackground(getResources().getDrawable(R.drawable.main_unable_bg));

                }


            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        input_number.setOnFocusChangeListener(new View.OnFocusChangeListener() {
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
                    if (hint == true) {
                        pwd_clear.setVisibility(View.VISIBLE);
                        hint = false;
                        if (type != Flag.TYPE_LOGIN) {
                            input_pwd.setTextSize(TypedValue.COMPLEX_UNIT_SP, 28);
                        }
                    }

                } else {
                    if (hint == false) {
                        pwd_clear.setVisibility(View.GONE);
                        hint = true;
                        if (type != Flag.TYPE_LOGIN) {
                            input_pwd.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                        }
                    }
                }

                if (type != Flag.TYPE_LOGIN) {
                    if (!FontImageUtil.isBothLetterAndNum(input_pwd.getText().toString()) || input_pwd.getText().toString().length() < 6) {
                        if (StringUtil.isBlank(input_pwd.getText().toString())) {
                            tv_pwd_message.setText("");
                        } else {
                            tv_pwd_message.setText("安全等级弱");
                            tv_pwd_message.setTextColor(getResources().getColor(R.color.red_message));
                        }
                        tv_pwd_next.setClickable(false);
                        tv_pwd_next.setBackground(getResources().getDrawable(R.drawable.main_unable_bg));

                    } else {
                        tv_pwd_next.setClickable(true);
                        tv_pwd_next.setBackground(getResources().getDrawable(R.drawable.main_able_bg));

                        tv_pwd_message.setText("安全等级强");
                        tv_pwd_message.setTextColor(getResources().getColor(R.color.sing_in_color));
                    }
                } else {
                    if (StringUtil.isBlank(input_pwd.getText().toString()) || !FontImageUtil.isBothLetterAndNum(input_pwd.getText().toString()) || input_pwd.getText().toString().length() < 6) {
                        tv_pwd_next.setClickable(false);
                        tv_pwd_next.setBackground(getResources().getDrawable(R.drawable.main_unable_bg));
                    } else {
                        tv_pwd_next.setClickable(true);
                        tv_pwd_next.setBackground(getResources().getDrawable(R.drawable.main_able_bg));
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        input_pwd.setOnFocusChangeListener(new View.OnFocusChangeListener() {
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

                if (type == Flag.TYPE_LOGIN) {
                    if (0 != input_rePwd.getText().toString().length()) {
                        if (rePwdHint == true) {
                            rePwd_clear.setVisibility(View.VISIBLE);
                            rePwdHint = false;
                            input_rePwd.setTextSize(TypedValue.COMPLEX_UNIT_SP, 28);
                        }

                    } else {
                        if (rePwdHint == false) {
                            rePwd_clear.setVisibility(View.GONE);
                            rePwdHint = true;
                            input_rePwd.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                        }
                    }


                    if (!FontImageUtil.isBothLetterAndNum(input_rePwd.getText().toString()) || input_rePwd.getText().toString().length() < 6) {
                        if (StringUtil.isBlank(input_rePwd.getText().toString())) {
                            tv_rePwdMessage.setText("");
                        } else {
                            tv_rePwdMessage.setText("安全等级弱");
                            tv_rePwdMessage.setTextColor(getResources().getColor(R.color.red_message));
                        }
                        tv_rePwd_next.setClickable(false);
                        tv_rePwd_next.setBackground(getResources().getDrawable(R.drawable.main_unable_bg));

                    } else {
                        tv_rePwd_next.setClickable(true);
                        tv_rePwd_next.setBackground(getResources().getDrawable(R.drawable.main_able_bg));

                        tv_rePwdMessage.setText("安全等级强");
                        tv_rePwdMessage.setTextColor(getResources().getColor(R.color.sing_in_color));
                    }
                } else {
                    if (0 != input_rePwd.getText().toString().length()) {
                        if (rePwdHint == true) {
                            rePwd_clear.setVisibility(View.VISIBLE);
                            rePwdHint = false;
                            input_rePwd.setTextSize(TypedValue.COMPLEX_UNIT_SP, 28);
                        }

                    } else {
                        if (rePwdHint == false) {
                            rePwd_clear.setVisibility(View.GONE);
                            rePwdHint = true;
                            input_rePwd.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                        }
                    }


                    if (TextUtils.isEmpty(input_rePwd.getText().toString()) || FontImageUtil.containsEmoji(input_rePwd.getText().toString())) {
                        if (TextUtils.isEmpty(input_rePwd.getText().toString())) {
                            tv_rePwdMessage.setText("");
                        } else {
                            tv_rePwdMessage.setText("字符不可识别");
                            tv_rePwdMessage.setTextColor(getResources().getColor(R.color.red_message));
                        }
                        tv_rePwd_next.setClickable(false);
                        tv_rePwd_next.setBackground(getResources().getDrawable(R.drawable.main_unable_bg));

                    } else {
                        tv_rePwd_next.setClickable(true);
                        tv_rePwd_next.setBackground(getResources().getDrawable(R.drawable.main_able_bg));

                        tv_rePwdMessage.setText("");
                        tv_rePwdMessage.setVisibility(GONE);
                    }
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


                if (input_verify.getText().toString().trim().length() == 6) {
                    pb_verify.setVisibility(VISIBLE);
                    verify_clear.setVisibility(GONE);
                    validateCode(mPhone, input_verify.getText().toString().trim());
                    input_verify.setFocusable(false);
                    input_verify.setFocusableInTouchMode(false);
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

    public boolean isPhoneNumberValid(String phoneNumber) {
        boolean isValid = false;
        CharSequence inputStr = phoneNumber;
        //正则表达式
        String phonenum = "^1\\d{10}$";
//        String phonenum = "^1[34578]\\d{9}$|^\\d{9}$";
        Pattern pattern = Pattern.compile(phonenum);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }


    /**
     * 发送验证码,注册信息
     */
    private void sendcode(String number) {

        RetrofitInternationalFactory.getInstence().API().sendCode(new SendCodeBody(number))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MBaseObserver<Object>((Activity) getContext(), false) {
                    @Override
                    protected void onSuccees(BaseResult<Object> t) throws Exception {
                        ToastUtil.toast(getResources().getString(R.string.send_ok));
                    }

                    @Override
                    protected void onCodeError(BaseResult<Object> t) throws Exception {
                        ToastUtil.toast(getResources().getString(R.string.send_fail));
                    }

                    @Override
                    protected void onFailure(Throwable e, boolean isNetWorkError) throws Exception {
                        ToastUtil.toast(getResources().getString(R.string.send_fail));
                    }
                });

//        //发送验证码
//        SendCodeTask sendCodeTask = new SendCodeTask((Activity) getContext(), true);
//        sendCodeTask.setAsyncTaskSuccessCallback(new AsyncTaskSuccessCallback<RegistData>() {
//            @Override
//            public void successCallback(Result<RegistData> result) {
//
//                ToastUtil.toast(getResources().getString(R.string.send_ok));
//
//            }
//        });
//        sendCodeTask.setAsyncTaskFailCallback(new AsyncTaskFailCallback<RegistData>() {
//            @Override
//            public void failCallback(Result<RegistData> result) {
//                ToastUtil.toast(getResources().getString(R.string.send_fail));
//            }
//        });
//        sendCodeTask.setShowProgressDialog(false);
//        sendCodeTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, number);
    }

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

            /*loginInfoData.setSource("1");
            loginInfoData.setSystem("2");
            loginInfoData.setApparatus("1");
            loginInfoData.setVersion_number("1.0.1");
            loginInfoData.setAppid("1");*/

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
            loginUserBody.setRegionCode("china");
//            }
        }
        Log.d("LoginUser", url);
        RetrofitInternationalFactory.getInstence().API().login(loginUserBody, url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MBaseObserver<LoginReceiveData>((Activity) getContext(), true) {
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

                            if (!StringUtil.isBlank(loginReceiveData.getToken())) {
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
                            mLoginType = type;
                            login(RELOG_IN);

                        } else {

                            installedAppsCheck(type);


//                            rl_rePwd_content.setVisibility(GONE);
//                            rl_pwd_content.setVisibility(GONE);
//
//                            rl_login_content.setVisibility(VISIBLE);
//                            startAsyncTime();
                        }
                    }

                    @Override
                    protected void onFailure(Throwable e, boolean isNetWorkError) throws Exception {
//                        if (type == LOG_IN||mLoginType==LOG_IN) {
//                            input_pwd.setFocusable(true);
//                            input_pwd.setFocusableInTouchMode(true);
//
//                            pb_pwd.setVisibility(GONE);
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
//                            ToastUtil.toast(e.getMessage());
//                            closeKeyBoard();
//                            clearTextContent();
//                            mLoginInter.finishView();
//                        }
                        Log.d("LoginUser", e.getMessage());
                        ToastUtil.toast(e.getMessage());
                        loginCodeError(e.getMessage(), type);
                    }

                    @Override
                    protected void onCodeError(BaseResult<LoginReceiveData> t) throws Exception {
//                        if (type == LOG_IN||mLoginType==LOG_IN) {
//                            input_pwd.setFocusable(true);
//                            input_pwd.setFocusableInTouchMode(true);
//
//                            pb_pwd.setVisibility(GONE);
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
//                            ToastUtil.toast(t.getMessage());
//                            closeKeyBoard();
//                            clearTextContent();
//                            mLoginInter.finishView();
//                        }
                        Log.d("LoginUser", t.getMessage());
                        ToastUtil.toast(t.getMessage());
                        loginCodeError(t.getMessage(), type);
                    }
                });

    }

    /**
     * 注册
     */
    private void regist(String code) {

        RetrofitInternationalFactory.getInstence().API().registerMoble(new RegisterMobileBody(mPhone, mPwd, nickName, code, "china", "yuntu"))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MBaseObserver<Object>((Activity) getContext(), true) {
                    @Override
                    protected void onSuccees(BaseResult<Object> t) throws Exception {
                        login(REGISTER);
                    }

                    @Override
                    protected void onCodeError(BaseResult<Object> t) throws Exception {
                        input_rePwd.setFocusable(true);
                        input_rePwd.setFocusableInTouchMode(true);

                        //loginDialog.error("服务器发生未知错误");
                        rl_rePwd_content.setVisibility(VISIBLE);
                        ll_rePwd.setBackground(getContext().getResources().getDrawable(R.drawable.login_pop_bg));
                        tv_input_rePwd.setVisibility(VISIBLE);
                        tv_input_rePwd.setText(t.getMessage());

                        input_number.setVisibility(INVISIBLE);
                        input_verify.setVisibility(INVISIBLE);
                        input_rePwd.setVisibility(INVISIBLE);
                        input_pwd.setVisibility(INVISIBLE);
                        rl_login_content.setVisibility(INVISIBLE);
                        nextType = REPWD_NEXT;
                        startEditTime();
                    }

                    @Override
                    protected void onFailure(Throwable e, boolean isNetWorkError) throws Exception {
                        input_rePwd.setFocusable(true);
                        input_rePwd.setFocusableInTouchMode(true);

                        //loginDialog.error("服务器发生未知错误");
                        rl_rePwd_content.setVisibility(VISIBLE);
                        ll_rePwd.setBackground(getContext().getResources().getDrawable(R.drawable.login_pop_bg));
                        tv_input_rePwd.setVisibility(VISIBLE);
                        tv_input_rePwd.setText(e.getMessage());

                        input_number.setVisibility(INVISIBLE);
                        input_verify.setVisibility(INVISIBLE);
                        input_rePwd.setVisibility(INVISIBLE);
                        input_pwd.setVisibility(INVISIBLE);
                        rl_login_content.setVisibility(INVISIBLE);
                        nextType = REPWD_NEXT;
                        startEditTime();

                    }
                });


    }


    /**
     * 重置密码
     */
    private void resetPwd(String code) {

        RetrofitInternationalFactory.getInstence().API().forgetReset(new ForgetResetBody(mPhone, mPwd, code))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MBaseObserver<Object>((Activity) getContext(), true) {
                    @Override
                    protected void onSuccees(BaseResult<Object> t) throws Exception {
                        login(FORGET_PWD);
                    }

                    @Override
                    protected void onCodeError(BaseResult<Object> t) throws Exception {
                        input_rePwd.setFocusable(true);
                        input_rePwd.setFocusableInTouchMode(true);

                        rl_rePwd_content.setVisibility(VISIBLE);
                        ll_rePwd.setBackground(getContext().getResources().getDrawable(R.drawable.login_pop_bg));
                        tv_input_rePwd.setVisibility(VISIBLE);
                        tv_input_rePwd.setText(t.getMessage());

                        input_number.setVisibility(INVISIBLE);
                        input_verify.setVisibility(INVISIBLE);
                        input_rePwd.setVisibility(INVISIBLE);
                        input_pwd.setVisibility(INVISIBLE);
                        rl_login_content.setVisibility(INVISIBLE);
                        nextType = REPWD_NEXT;
                        startEditTime();
                    }

                    @Override
                    protected void onFailure(Throwable e, boolean isNetWorkError) throws Exception {
                        input_rePwd.setFocusable(true);
                        input_rePwd.setFocusableInTouchMode(true);

                        rl_rePwd_content.setVisibility(VISIBLE);
                        ll_rePwd.setBackground(getContext().getResources().getDrawable(R.drawable.login_pop_bg));
                        tv_input_rePwd.setVisibility(VISIBLE);
                        tv_input_rePwd.setText(e.getMessage());

                        input_number.setVisibility(INVISIBLE);
                        input_verify.setVisibility(INVISIBLE);
                        input_rePwd.setVisibility(INVISIBLE);
                        input_pwd.setVisibility(INVISIBLE);
                        rl_login_content.setVisibility(INVISIBLE);
                        nextType = REPWD_NEXT;
                        startEditTime();
                    }
                });
    }

    /**
     * 验证用户是否存在
     *
     * @param phone 账号
     */
    private void isExit(final String phone) {

        //新版本
        if (UrlConstants.isWorld_Interface) {
            RetrofitInternationalFactory.getInstence().API().userIsExistWorld(new UserIsExistBody(phone, "yuntu", "china"))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new MBaseObserver<Object>((Activity) getContext(), false) {
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

//                                tv_num_next.setClickable(false);
//                                tv_num_next.setBackground(getResources().getDrawable(R.drawable.main_unable_bg));

                            }
                            pb_num.setVisibility(GONE);
                        }

                        @Override
                        protected void onCodeError(BaseResult<Object> t) throws Exception {
                            input_number.setFocusableInTouchMode(true);
                            input_number.setFocusable(true);
                            input_number.requestFocus();
                            tv_num_next.setClickable(true);

                            //用户不存在
                            if (t.getCode() == 20071) {

                                if (!StringUtil.isBlank(mPhone) && mPhone.equals(phone)) {
                                    input_number.clearFocus();
                                    openVerifyKeyBoard();
                                    input_verify.setFocusable(true);
                                    input_verify.requestFocus();
                                    startFloatAnim(new AnimFactory.FloatListener() {
                                        @Override
                                        public void floatValueChang(float f) {

                                            rl_number_content.setTranslationX(rl_content.getMeasuredHeight() * -f);
                                            rl_number_content.setAlpha(1 - f);

                                            rl_verify_content.setTranslationX(rl_content.getMeasuredHeight() * (1 - f));
                                            rl_verify_content.setAlpha(f);
                                        }
                                    });
                                } else {
                                    if (null != classCut) {
                                        classCut.exit = true;
                                    }
                                    showChoiceDialog(phone);
                                }


                            } else {
                                ll_number.setBackground(getContext().getResources().getDrawable(R.drawable.login_pop_bg));
                                tv_input_number.setVisibility(VISIBLE);
                                tv_input_number.setText(t.getMessage());
                                input_pwd.setVisibility(INVISIBLE);
                                input_verify.setVisibility(INVISIBLE);
                                input_rePwd.setVisibility(INVISIBLE);
                                input_number.setVisibility(INVISIBLE);
                                nextType = NUMBER_NEXT_ISEXIT;
                                startEditTime();
//                                tv_num_next.setClickable(false);
//                                tv_num_next.setBackground(getResources().getDrawable(R.drawable.main_unable_bg));
                            }
                            pb_num.setVisibility(GONE);
                        }

                        @Override
                        protected void onFailure(Throwable e, boolean isNetWorkError) throws Exception {
                            input_number.setFocusableInTouchMode(true);
                            input_number.setFocusable(true);
                            input_number.requestFocus();
                            tv_num_next.setClickable(true);

                            ll_number.setBackground(getContext().getResources().getDrawable(R.drawable.login_pop_bg));
                            tv_input_number.setVisibility(VISIBLE);
                            tv_input_number.setText(e.getMessage());
                            input_pwd.setVisibility(INVISIBLE);
                            input_verify.setVisibility(INVISIBLE);
                            input_rePwd.setVisibility(INVISIBLE);
                            input_number.setVisibility(INVISIBLE);
                            nextType = NUMBER_NEXT_ISEXIT;
                            startEditTime();
//                            tv_num_next.setClickable(false);
//                            tv_num_next.setBackground(getResources().getDrawable(R.drawable.main_unable_bg));
                            pb_num.setVisibility(GONE);
                        }
                    });
        } else {
            RetrofitFactory.getInstence().API().userIsExist(phone)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new MBaseObserver<Object>((Activity) getContext(), false) {
                        @Override
                        protected void onSuccees(BaseResult<Object> t) throws Exception {
                            input_number.setFocusableInTouchMode(true);
                            input_number.setFocusable(true);
                            input_number.requestFocus();
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

                                tv_num_next.setClickable(false);
                                tv_num_next.setBackground(getResources().getDrawable(R.drawable.main_unable_bg));

                            }
                            pb_num.setVisibility(GONE);
                        }

                        @Override
                        protected void onCodeError(BaseResult<Object> t) throws Exception {
                            input_number.setFocusableInTouchMode(true);
                            input_number.setFocusable(true);
                            input_number.requestFocus();
                            //用户不存在
                            if (t.getCode() == 20019) {
                                if (!StringUtil.isBlank(mPhone) && mPhone.equals(phone)) {
                                    input_number.clearFocus();
                                    openVerifyKeyBoard();
                                    input_verify.setFocusable(true);
                                    input_verify.requestFocus();
                                    startFloatAnim(new AnimFactory.FloatListener() {
                                        @Override
                                        public void floatValueChang(float f) {

                                            rl_number_content.setTranslationX(rl_content.getMeasuredHeight() * -f);
                                            rl_number_content.setAlpha(1 - f);

                                            rl_verify_content.setTranslationX(rl_content.getMeasuredHeight() * (1 - f));
                                            rl_verify_content.setAlpha(f);
                                        }
                                    });
                                } else {
                                    if (null != classCut) {
                                        classCut.exit = true;
                                    }
                                    showChoiceDialog(phone);
                                }
                            } else {
                                ll_number.setBackground(getContext().getResources().getDrawable(R.drawable.login_pop_bg));
                                tv_input_number.setVisibility(VISIBLE);
                                tv_input_number.setText(t.getMessage());
                                input_pwd.setVisibility(INVISIBLE);
                                input_verify.setVisibility(INVISIBLE);
                                input_rePwd.setVisibility(INVISIBLE);
                                input_number.setVisibility(INVISIBLE);
                                nextType = NUMBER_NEXT_ISEXIT;
                                startEditTime();
                                tv_num_next.setClickable(false);
                                tv_num_next.setBackground(getResources().getDrawable(R.drawable.main_unable_bg));
                            }
                            pb_num.setVisibility(GONE);
                        }

                        @Override
                        protected void onFailure(Throwable e, boolean isNetWorkError) throws Exception {
                            input_number.setFocusableInTouchMode(true);
                            input_number.setFocusable(true);
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
                            tv_num_next.setClickable(false);
                            tv_num_next.setBackground(getResources().getDrawable(R.drawable.main_unable_bg));
                            pb_num.setVisibility(GONE);
                        }
                    });
        }
    }

    /**
     * 验证验证码
     *
     * @param phone 账号
     * @param code  验证码
     */
    private void validateCode(String phone, String code) {

        RetrofitInternationalFactory.getInstence().API().vaildateCode(new ValidateCodeBody(phone, code))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MBaseObserver<Object>((Activity) getContext(), false) {

                    @Override
                    protected void onSuccees(BaseResult<Object> t) throws Exception {
                        verify_clear.setVisibility(VISIBLE);
                        pb_verify.setVisibility(GONE);

                        String code = input_verify.getText().toString();

                        mCode = code;

                        if (type == Flag.TYPE_LOGIN) {
                            input_verify.clearFocus();
                            rl_rePwd_content.setVisibility(VISIBLE);
                            openRePwdKeyBoard();
                            input_rePwd.setFocusable(true);
                            input_rePwd.requestFocus();
                            startFloatAnim(new AnimFactory.FloatListener() {
                                @Override
                                public void floatValueChang(float f) {

                                    rl_verify_content.setTranslationX(rl_content.getMeasuredHeight() * -f);
                                    rl_verify_content.setAlpha(1 - f);

                                    rl_rePwd_content.setTranslationX(rl_content.getMeasuredHeight() * (1 - f));
                                    rl_rePwd_content.setAlpha(f);
                                }
                            });
                        } else {
                            input_verify.clearFocus();
                            rl_pwd_content.setVisibility(VISIBLE);
                            openPwdKeyBoard();
                            input_pwd.setFocusable(true);
                            input_pwd.requestFocus();
                            startFloatAnim(new AnimFactory.FloatListener() {
                                @Override
                                public void floatValueChang(float f) {

                                    rl_verify_content.setTranslationX(rl_content.getMeasuredHeight() * -f);
                                    rl_verify_content.setAlpha(1 - f);

                                    rl_pwd_content.setTranslationX(rl_content.getMeasuredHeight() * (1 - f));
                                    rl_pwd_content.setAlpha(f);
                                }
                            });
                        }
                    }

                    @Override
                    protected void onCodeError(BaseResult<Object> t) throws Exception {
                        input_verify.setFocusableInTouchMode(true);
                        input_verify.setFocusable(true);
                        input_verify.requestFocus();
                        verify_clear.setVisibility(VISIBLE);
                        tv_verify_next.setClickable(false);
                        tv_verify_next.setBackground(getResources().getDrawable(R.drawable.main_unable_bg));

                        input_verify.setText("");
                        ll_verify.setBackground(getContext().getResources().getDrawable(R.drawable.login_pop_bg));
                        tv_input_verify.setVisibility(VISIBLE);
                        tv_input_verify.setText(t.getMessage());
                        input_pwd.setVisibility(INVISIBLE);
                        input_verify.setVisibility(INVISIBLE);
                        input_rePwd.setVisibility(INVISIBLE);
                        input_number.setVisibility(INVISIBLE);
                        nextType = VERIFY_NEXT_VALATE;
                        startEditTime();


                        pb_verify.setVisibility(GONE);
                    }

                    @Override
                    protected void onFailure(Throwable e, boolean isNetWorkError) throws Exception {
                        input_verify.setFocusableInTouchMode(true);
                        input_verify.setFocusable(true);
                        input_verify.requestFocus();
                        verify_clear.setVisibility(VISIBLE);
                        tv_verify_next.setClickable(false);
                        tv_verify_next.setBackground(getResources().getDrawable(R.drawable.main_unable_bg));

                        input_verify.setText("");
                        ll_verify.setBackground(getContext().getResources().getDrawable(R.drawable.login_pop_bg));
                        tv_input_verify.setVisibility(VISIBLE);
                        tv_input_verify.setText(e.getMessage());
                        input_pwd.setVisibility(INVISIBLE);
                        input_verify.setVisibility(INVISIBLE);
                        input_rePwd.setVisibility(INVISIBLE);
                        input_number.setVisibility(INVISIBLE);
                        nextType = VERIFY_NEXT_VALATE;
                        startEditTime();

                        pb_verify.setVisibility(GONE);
                    }
                });
//
    }


    private void installedAppsCheck(final int type) {
        String url = UrlConstants.BASE_INSTALLED_APP + UrlConstants.SETTING_CHECK;
        RetrofitFactory.getInstence().API().settingCheck(url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MBaseObserver<Object>((Activity) getContext(), false) {
                    @Override
                    protected void onSuccees(BaseResult<Object> t) throws Exception {
                        //有服务初始化
                        if (t.getCode() == 10004) {
                            installedAppsInit(type);
                        } else {
                            dgProgressDialog3.dismiss();

                            rl_rePwd_content.setVisibility(GONE);
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
                        } else {
                            dgProgressDialog3.dismiss();

                            rl_rePwd_content.setVisibility(GONE);
                            rl_pwd_content.setVisibility(GONE);
                            rl_verify_content.setVisibility(GONE);
                            rl_login_content.setVisibility(VISIBLE);

                            startAsyncTime();
                        }
                    }

                    @Override
                    protected void onFailure(Throwable e, boolean isNetWorkError) throws Exception {
                        loginCodeError(e.getMessage(), type);
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

                        rl_rePwd_content.setVisibility(GONE);
                        rl_pwd_content.setVisibility(GONE);
                        rl_verify_content.setVisibility(GONE);
                        rl_login_content.setVisibility(VISIBLE);

                        startAsyncTime();
                    }

                    @Override
                    protected void onCodeError(BaseResult<Object> t) throws Exception {
                        loginCodeError(t.getMessage(), type);
                    }

                    @Override
                    protected void onFailure(Throwable e, boolean isNetWorkError) throws Exception {
                        loginCodeError(e.getMessage(), type);
                    }
                });

    }


    protected void dissmissChoiceDialog() {
        if (mchoiceDialog != null) {
            mchoiceDialog.dismiss();
        }
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
                        title_verify.setRightTextCanClick("<u>" + "<font color='#000000'>" + String.format("%02d", i) + "s后可重新发送</font>" + "</u>", false);
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
                    title_verify.setRightTextCanClick("<u>" + "<font color='#000000'>" + "重新发送" + "</font>" + "</u>", true);
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
                    } else if (nextType == VERIFY_NEXT) {
                        ll_verify.setBackgroundColor(getContext().getResources().getColor(R.color.backgroud_new));
                        tv_input_verify.setVisibility(INVISIBLE);
                        input_rePwd.setVisibility(VISIBLE);
                        input_verify.setVisibility(VISIBLE);
                        input_pwd.setVisibility(VISIBLE);
                        input_number.setVisibility(VISIBLE);
                        tv_verify_next.setClickable(true);
                    } else if (nextType == NUMBER_NEXT_ISEXIT) {
                        ll_number.setBackgroundColor(getContext().getResources().getColor(R.color.backgroud_new));
                        tv_input_number.setVisibility(INVISIBLE);
                        input_number.setVisibility(VISIBLE);
                        input_pwd.setVisibility(VISIBLE);
                        input_verify.setVisibility(VISIBLE);
                        input_rePwd.setVisibility(VISIBLE);

                    } else if (nextType == VERIFY_NEXT_VALATE) {
                        ll_verify.setBackgroundColor(getContext().getResources().getColor(R.color.backgroud_new));
                        tv_input_verify.setVisibility(INVISIBLE);
                        input_rePwd.setVisibility(VISIBLE);
                        input_verify.setVisibility(VISIBLE);
                        input_pwd.setVisibility(VISIBLE);
                        input_number.setVisibility(VISIBLE);
                    }


                }
            });
            errorTime = 1;//修改倒计时剩余时间变量为3秒
        }
    }

    public interface ClickInter {
        void onClick();
    }

    public void setClickInter(ClickInter clickInter) {
        this.clickInter = clickInter;
    }


    /**
     * 错误提示
     *
     * @param message 错误信息
     * @param mType   错误操作类型
     */
    private void loginCodeError(String message, int mType) {
        if (mType == LOG_IN) {
            input_pwd.setFocusable(true);
            input_pwd.setFocusableInTouchMode(true);

            pb_pwd.setVisibility(GONE);
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
        } else if (mType == RELOG_IN) {
            if (mLoginType == LOG_IN) {
                input_pwd.setFocusable(true);
                input_pwd.setFocusableInTouchMode(true);

                pb_pwd.setVisibility(GONE);
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
            } else if (mLoginType == REGISTER || mLoginType == FORGET_PWD) {
                ToastUtil.toast(message);
                closeKeyBoard();
                clearTextContent();
                mLoginInter.finishView();
            }
        } else if (mType == REGISTER || mType == FORGET_PWD) {
            ToastUtil.toast(message);
            closeKeyBoard();
            clearTextContent();
            mLoginInter.finishView();
        }
        dgProgressDialog3.dismiss();
    }
}



