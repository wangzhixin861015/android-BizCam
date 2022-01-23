package com.bcnetech.hyphoto.ui.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Camera;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bcnetech.bcnetchhttp.RetrofitInternationalFactory;
import com.bcnetech.bcnetchhttp.base.BaseResult;
import com.bcnetech.bcnetchhttp.bean.request.ChangePasswordBody;
import com.bcnetech.bcnetchhttp.bean.request.ForgetResetBody;
import com.bcnetech.bcnetchhttp.bean.request.SendCodeBody;
import com.bcnetech.bcnetchhttp.bean.response.LoginedUser;
import com.bcnetech.bcnetchhttp.config.Flag;
import com.bcnetech.bcnetechlibrary.util.ToastUtil;
import com.bcnetech.hyphoto.task.MBaseObserver;
import com.bcnetech.hyphoto.utils.FontImageUtil;
import com.bcnetech.hyphoto.utils.StringUtils;
import com.bcnetech.hyphoto.R;

import java.lang.reflect.Method;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by a1234 on 17/4/14.
 */

public class ChangePswView extends ScrollView {
    private RelativeLayout layout_changepsw;
    private EditText inputold;//旧密码
    private EditText inputnew;//新密码
    private EditText inputconfirm;//再次输入新密码
    private SpaceEditText input_captcha;//验证码
    private View input_old_ll, input_new_ll, input_confirm_ll;
    private TextView tv_old, tv_new, tv_confirm;
    private Button chang_confirm_button;
    private int type = Flag.TYPE_CHANGE_PSW;
    private boolean isAnim = false;
    private Handler mhandler;
    private SaveRotate saveRotate;
    private ChangePswInter mLoginInter;
    private ClassCut classCut;
    private Handler mHandler = new Handler();//全局handler
    public static final int COUNTDOWN = 60;
    int i = COUNTDOWN;//倒计时的整个时间数
    private Rotate3d rotate3d;
    private TextView tv_psw_hint, tv_confirm_hint;
    private String newPsw, confirmPsw;
    private boolean haveoldPsw = false;
    private boolean havenewPsw = false;
    private boolean newPswConfirmed = false;
    private boolean havecaptcha = false;
    private Activity actitity;


    public ChangePswView(Context context) {
        super(context);
        initView();
        initData();
        onViewClick();
    }

    public ChangePswView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
        initData();
        onViewClick();
    }

    public ChangePswView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
        initData();
        onViewClick();
    }

    protected void initView() {
        inflate(getContext(), R.layout.change_psw_layout, this);
        layout_changepsw = (RelativeLayout) findViewById(R.id.layout_changepsw);
        inputold =findViewById(R.id.input_old);
        inputnew =  findViewById(R.id.input_new);
        input_captcha = (SpaceEditText) findViewById(R.id.input_captcha);
        inputnew.setTypeface(Typeface.DEFAULT);
        inputnew.setTransformationMethod(new PasswordTransformationMethod());
        inputconfirm = findViewById(R.id.input_confirm);
        inputconfirm.setTypeface(Typeface.DEFAULT);
        inputconfirm.setTransformationMethod(new PasswordTransformationMethod());
        chang_confirm_button = (Button) findViewById(R.id.confirm);
        input_old_ll = (View) findViewById(R.id.input_old_ll);
        input_new_ll = (View) findViewById(R.id.input_new_ll);
        input_confirm_ll = (View) findViewById(R.id.input_confirm_ll);
        tv_old = (TextView) findViewById(R.id.tv_old);
        tv_new = (TextView) findViewById(R.id.tv_new);
        tv_confirm = (TextView) findViewById(R.id.tv_confirm);
        tv_confirm_hint = (TextView) findViewById(R.id.tv_confirm_hint);
        tv_psw_hint = (TextView) findViewById(R.id.tv_psw_hint);
    }

    protected void initData() {
        initEditText();
        haveoldPsw = false;
        havenewPsw = false;
        newPswConfirmed = false;
        havecaptcha = false;
        tv_confirm_hint.setVisibility(INVISIBLE);
        tv_psw_hint.setVisibility(INVISIBLE);
        mhandler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        if (saveRotate != null) {
                            EditTextAnim2(saveRotate.editText, saveRotate.hint, saveRotate.v);
                        }
                        break;
                }
            }
        };

        inputold.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s.toString())) {
                    haveoldPsw = true;
                } else {
                    haveoldPsw = false;
                }
                isCanConfirm();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        inputnew.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() != 0) {
                    newPsw = s.toString();
                    if (s.length() < 6 || s.length() > 30 || !FontImageUtil.isBothLetterAndNum(s.toString())) {
                        tv_psw_hint.setText(getResources().getString(R.string.security_weak));
                        tv_psw_hint.setTextColor(getResources().getColor(R.color.red_us_border));
                        tv_psw_hint.setVisibility(VISIBLE);
                        havenewPsw = false;
                    } else {
                        tv_psw_hint.setText(getResources().getString(R.string.security_pass));
                        tv_psw_hint.setTextColor(Color.BLUE);
                        tv_psw_hint.setVisibility(VISIBLE);
                        havenewPsw = true;
                    }
                    if (!TextUtils.isEmpty(confirmPsw) && !TextUtils.isEmpty(newPsw)) {
                        judgeConfirmPsw(newPsw, confirmPsw);
                    }
                } else {
                    tv_psw_hint.setVisibility(INVISIBLE);
                    havenewPsw = false;
                }
                isCanConfirm();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        inputconfirm.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() != 0) {
                    confirmPsw = s.toString();
                    newPsw = inputnew.getText().toString();
                    judgeConfirmPsw(newPsw, confirmPsw);
                } else {
                    tv_confirm_hint.setVisibility(INVISIBLE);
                    newPswConfirmed = false;
                }
                isCanConfirm();

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    protected void onViewClick() {
        chang_confirm_button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type == Flag.TYPE_CHANGE_PSW) {
                    changepsw();
                } else {
                    findpsew();
                }
            }
        });

        inputconfirm.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            //当键盘弹出隐藏的时候会 调用此方法。
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                //获取当前界面可视部分
                actitity.getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
                //获取屏幕的高度
                int screenHeight = actitity.getWindow().getDecorView().getRootView().getHeight();
                //此处就是用来获取键盘的高度的， 在键盘没有弹出的时候 此高度为0 键盘弹出的时候为一个正数
                int heightDifference = screenHeight - r.bottom - getVirtualBarHeigh();
                Log.d("Keyboard Size", "Size: " + heightDifference);
                mLoginInter.onKeboardShow(heightDifference > 0);
            }

        });

    }

    /**
     * 获取虚拟功能键高度
     */
    public int getVirtualBarHeigh() {
        int vh = 0;
        WindowManager windowManager = (WindowManager) actitity.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        try {
            @SuppressWarnings("rawtypes")
            Class c = Class.forName("android.view.Display");
            @SuppressWarnings("unchecked")
            Method method = c.getMethod("getRealMetrics", DisplayMetrics.class);
            method.invoke(display, dm);
            vh = dm.heightPixels - windowManager.getDefaultDisplay().getHeight();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vh;
    }


    public void setType(int type) {
        clearTextContent();
        switch (type) {
            case Flag.TYPE_FIND_PSW:
                i = 0;
                this.type = Flag.TYPE_FIND_PSW;
                tv_old.setText(getResources().getString(R.string.yanzhengma));
                input_captcha.setVisibility(VISIBLE);
                inputold.setVisibility(GONE);
              /*  inputold.setTypeface(Typeface.DEFAULT);
                inputold.setTransformationMethod(new SingleLineTransformationMethod());*/
                break;
            case Flag.TYPE_CHANGE_PSW:
                i = 0;
                tv_old.setText(getResources().getString(R.string.old_psw));
                this.type = Flag.TYPE_CHANGE_PSW;
                input_captcha.setVisibility(GONE);
                inputold.setVisibility(VISIBLE);
                inputold.setTypeface(Typeface.DEFAULT);
                inputold.setTransformationMethod(new PasswordTransformationMethod());
                break;
        }
    }

    /**
     * 发送验证码,注册信息
     */
    public void sendcode(String number) {
       /* if (Flag.isEnglish) {
            SendEmailCodeTask sendCodeTask = new SendEmailCodeTask((Activity) getContext(), true);
            sendCodeTask.setAsyncTaskSuccessCallback(new AsyncTaskSuccessCallback<RegistData>() {
                @Override
                public void successCallback(Result<RegistData> result) {

//                CostomToastUtil.toast(getResources().getString(R.string.send_ok));

                }
            });
            sendCodeTask.setAsyncTaskFailCallback(new AsyncTaskFailCallback<RegistData>() {
                @Override
                public void failCallback(Result<RegistData> result) {
//                CostomToastUtil.toast(getResources().getString(R.string.send_fail));
                }
            });
            sendCodeTask.setShowProgressDialog(false);
            sendCodeTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, number);
        } else {
            //发送验证码
            SendCodeTask sendCodeTask = new SendCodeTask((Activity) getContext(), true);
            sendCodeTask.setAsyncTaskSuccessCallback(new AsyncTaskSuccessCallback<RegistData>() {
                @Override
                public void successCallback(Result<RegistData> result) {
                    ToastUtil.toast(getResources().getString(R.string.send_ok));
                }
            });
            sendCodeTask.setAsyncTaskFailCallback(new AsyncTaskFailCallback<RegistData>() {
                @Override
                public void failCallback(Result<RegistData> result) {
                    ToastUtil.toast(getResources().getString(R.string.send_fail));
                }
            });
            sendCodeTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, number);
        }*/

        RetrofitInternationalFactory.getInstence().API().sendCode(new SendCodeBody(number))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MBaseObserver<Object>(actitity, true) {
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
    }

    /**
     * 修改密码
     */
    private void changepsw() {
        LoginedUser loginedUser = LoginedUser.getLoginedUser();
        String token = loginedUser.getTokenid();
        String login = loginedUser.getUser_name();
        String oldpsw = inputold.getText().toString();
        String newpsw = inputnew.getText().toString();
        String confirmpsw = inputconfirm.getText().toString();
        if (TextUtils.isEmpty(newpsw) || TextUtils.isEmpty(oldpsw) || TextUtils.isEmpty(confirmpsw)) {
            ToastUtil.toast(getResources().getString(R.string.empty_psw));
            return;
        }
        if (newpsw.equals(oldpsw)) {
            ToastUtil.toast(getResources().getString(R.string.password_equal));
            return;
        }
        if (newpsw.length() < 6) {
            ToastUtil.toast(getResources().getString(R.string.short_psw));
            return;
        }
        if (newpsw.length() > 30) {
            ToastUtil.toast(getResources().getString(R.string.long_psw));
            return;
        }
        if (!FontImageUtil.isBothLetterAndNum(newpsw)) {
            ToastUtil.toast(getResources().getString(R.string.wrong_psw_style));
            return;
        }

        if (!newpsw.equals(confirmpsw)) {
            ToastUtil.toast(getResources().getString(R.string.confirm_error));
            return;
        }
        mLoginInter.onChangePswClick();


        RetrofitInternationalFactory.getInstence().API().changePassword(new ChangePasswordBody(login, oldpsw, newpsw))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MBaseObserver<Object>(actitity, true) {

                    @Override
                    protected void onSuccees(BaseResult<Object> t) throws Exception {
                        mLoginInter.onChangePswResponed(true);
                        ToastUtil.toast(getResources().getString(R.string.change_password_ok));
                    }

                    @Override
                    protected void onCodeError(BaseResult<Object> t) throws Exception {
                        mLoginInter.onChangePswResponed(false);

                        ToastUtil.toast(t.getMessage());
                    }

                    @Override
                    protected void onFailure(Throwable e, boolean isNetWorkError) throws Exception {
                        mLoginInter.onChangePswResponed(false);

                        ToastUtil.toast(e.getMessage());
                    }
                });


//        ChangePwdTask changePwdTask = new ChangePwdTask((Activity) getContext());
//
//        changePwdTask.setAsyncTaskSuccessCallback(new AsyncTaskSuccessCallback<PasswordData>() {
//            @Override
//            public void successCallback(Result<PasswordData> result) {
//                mLoginInter.onChangePswResponed(true);
//                ToastUtil.toast(getResources().getString(R.string.change_password_ok));
//
//                //CoustomToast(getResources().getString(R.string.change_password_ok), true);
//                // ((Activity) getContext()).finish();
//            }
//        });
//        changePwdTask.setAsyncTaskFailCallback(new AsyncTaskFailCallback<PasswordData>() {
//            @Override
//            public void failCallback(Result<PasswordData> result) {
//                mLoginInter.onChangePswResponed(false);
//
//                ToastUtil.toast(result.getMessage());
//                // CoustomToast(result.getMessage(), true);
//            }
//        });
//        changePwdTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, oldpsw, newpsw, login, token);
    }

    /**
     * 重置密码
     */
    private void findpsew() {
        String password = inputnew.getText().toString();
        String confirmpsw = inputconfirm.getText().toString();
        //String code = inputold.getText().toString();
        // String code = input_captcha.getText().toString();
        String code = StringUtils.getTextTrim(input_captcha);
        if (TextUtils.isEmpty(password)) {
            ToastUtil.toast(getResources().getString(R.string.empty_psw));
            return;
        }
        if (password.length() < 6) {
            ToastUtil.toast(getResources().getString(R.string.short_psw));
            return;
        }
        if (password.length() > 30) {
            ToastUtil.toast(getResources().getString(R.string.long_psw));
            return;
        }
        if (!FontImageUtil.isBothLetterAndNum(password)) {
            ToastUtil.toast(getResources().getString(R.string.wrong_psw_style));
            return;
        }
        if (TextUtils.isEmpty(code)) {
            ToastUtil.toast(getResources().getString(R.string.empty_code));
            return;
        }
        if (!password.equals(confirmpsw)) {
            ToastUtil.toast(getResources().getString(R.string.confirm_error));
            return;
        }
        mLoginInter.onResetPswClick();

        RetrofitInternationalFactory.getInstence().API().forgetReset(new ForgetResetBody(LoginedUser.getLoginedUser().getUser_name(), password, code))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MBaseObserver<Object>(actitity, true) {
                    @Override
                    protected void onSuccees(BaseResult<Object> t) throws Exception {
                        ToastUtil.toast(getResources().getString(R.string.reset_psw_ok));

                        mLoginInter.onResetPswResponed(true);
                    }

                    @Override
                    protected void onCodeError(BaseResult<Object> t) throws Exception {
                        mLoginInter.onResetPswResponed(false);

                        ToastUtil.toast(t.getMessage());
                    }

                    @Override
                    protected void onFailure(Throwable e, boolean isNetWorkError) throws Exception {
                        mLoginInter.onResetPswResponed(false);
                        ToastUtil.toast(e.getMessage());
                    }
                });


    }

    /*private void CoustomToast(String msg, boolean isSuccess) {

        Toast toast = new Toast(getContext());
        //设置Toast显示位置，居中，向 X、Y轴偏移量均为0
        toast.setGravity(Gravity.CENTER, 0, 0);
        //获取自定义视图
        View view = LayoutInflater.from(getContext()).inflate(R.layout.pop_toast, null);
        TextView tvMessage = (TextView) view.findViewById(R.id.tv_toast);
        //设置文本
        tvMessage.setText(msg);
        tvMessage.setTextSize(22);
        if (isSuccess) {
            tvMessage.setTextColor(Color.BLUE);
        } else {
            tvMessage.setTextColor(getResources().getColor(R.color.red_us_border));
        }
        //设置视图
        toast.setView(view);
        //设置显示时长
        toast.setDuration(Toast.LENGTH_SHORT);
        //显示
        toast.show();

    }*/

    public void clearTextContent() {
        inputconfirm.setText("");
        inputnew.setText("");
        inputold.setText("");
        input_captcha.setText("");
    }

    //镜像旋转动画
    class Rotate3d extends Animation {
        //开始角度
        private final float mFromDegrees;
        //结束角度
        private final float mToDegrees;
        //中心点
        private final float mCenterX;
        private final float mCenterY;
        private final float mDepthZ;
        //是否需要扭曲
        private final boolean mReverse;
        //摄像头
        private Camera mCamera;

        public Rotate3d(float fromDegrees, float toDegrees,
                        float centerX, float centerY, float depthZ, boolean reverse) {
            mFromDegrees = fromDegrees;
            mToDegrees = toDegrees;
            mCenterX = centerX;
            mCenterY = centerY;
            mDepthZ = depthZ;
            mReverse = reverse;
        }

        @Override
        public void initialize(int width, int height, int parentWidth, int parentHeight) {
            super.initialize(width, height, parentWidth, parentHeight);
            mCamera = new Camera();
        }

        //生成Transformation
        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            final float fromDegrees = mFromDegrees;
            //生成中间角度
            float degrees = fromDegrees + ((mToDegrees - fromDegrees) * interpolatedTime);

            final float centerX = mCenterX;
            final float centerY = mCenterY;
            final Camera camera = mCamera;

            final Matrix matrix = t.getMatrix();

            camera.save();
            if (mReverse) {
                camera.translate(0.0f, 0.0f, mDepthZ * interpolatedTime);
            } else {
                camera.translate(0.0f, 0.0f, mDepthZ * (1.0f - interpolatedTime));
            }
            camera.rotateX(degrees);
            //取得变换后的矩阵
            camera.getMatrix(matrix);
            camera.restore();

            matrix.preTranslate(-centerX, -centerY);
            matrix.postTranslate(centerX, centerY);
        }
    }

    public void setRotateEdit(String hint, EditText editText, View v) {
        EditTextAnim(hint, editText, v);
    }

    private void EditTextAnim2(final EditText editText, final String hint, final View view) {
        rotate3d = new Rotate3d(180, 0, editText.getWidth() / 2, editText.getHeight() / 2, 0, false);
        rotate3d.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                editText.setBackgroundColor(getResources().getColor(R.color.translucent));
                editText.setHintTextColor(getResources().getColor(R.color.gray));
                editText.setClickable(true);
                editText.setEnabled(true);
                editText.setHint(hint);
                editText.setTextSize(28);
                editText.setText("");
                view.setBackgroundColor(getResources().getColor(R.color.backgroud_new));
                isAnim = false;
                mLoginInter.onAniming(isAnim);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        rotate3d.setDuration(300);
        editText.startAnimation(rotate3d);
        rotate3d.setFillAfter(true);
    }

    /**
     * 输入框反转动画
     */
    private void EditTextAnim(final String hint, final EditText editText, final View view) {
        if (!isAnim) {
            Rotate3d rotate3d = new Rotate3d(0, 180, editText.getWidth() / 2, editText.getHeight() / 2, 0, false);
            rotate3d.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    view.setBackground(getResources().getDrawable(R.drawable.red_item_bg));
                    isAnim = true;
                    saveRotate = new SaveRotate();
                    if (editText.getHint() != null) {
                        saveRotate.hint = editText.getHint().toString();
                    }
                    saveRotate.editText = editText;
                    saveRotate.v = view;
                    mLoginInter.onAniming(isAnim);
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    //  v.setBackgroundColor(getResources().getColor(R.color.white));
                    editText.setHintTextColor(getResources().getColor(R.color.blue_tootch_footer_bg));
                    editText.setTextSize(14);
                    editText.setClickable(false);
                    if (hint != null && !TextUtils.isEmpty(hint)) {
                        editText.setHint(hint);
                    }
                    editText.setHintTextColor(getResources().getColor(R.color.blue_tootch_footer_bg));
                    editText.setText("");
                    editText.setEnabled(false);
                    mhandler.sendEmptyMessageDelayed(1, 2000);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            rotate3d.setDuration(300);
            editText.startAnimation(rotate3d);
            // rotate3d.setFillAfter(true);
        }
    }


    /**
     * 开启倒计时
     */
    public void starttime() {
        i = COUNTDOWN;
        classCut = new ClassCut();
        classCut.start();
    }

    public void starttime(int newTime) {
        i = newTime;
        classCut = new ClassCut();
        classCut.start();
    }

    class ClassCut extends Thread implements Runnable {//倒计时逻辑子线程

        @Override
        public void run() {
            while (i > 0) {//整个倒计时执行的循环
                i--;
                mHandler.post(new Runnable() {//通过它在UI主线程中修改显示的剩余时间
                    @Override
                    public void run() {
                        mLoginInter.onCountDown(i);
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
                    mLoginInter.onCountDown(0);
                }
            });
            i = COUNTDOWN;//修改倒计时剩余时间变量为60秒
        }
    }

    class SaveRotate {
        EditText editText;
        String hint;
        View v;
    }

    public interface ChangePswInter {
        void onAniming(boolean isaniming);

        void onCountDown(int time);

        void onKeboardShow(boolean isshow);

        void onChangePswClick();

        void onChangePswResponed(boolean issuccess);

        void onResetPswClick();

        void onResetPswResponed(boolean issuccess);

    }

    public void setMLoginInter(ChangePswInter mLoginInter) {
        this.mLoginInter = mLoginInter;
    }


    public void isCanConfirm() {
        switch (type) {
            case Flag.TYPE_FIND_PSW:
                if (havenewPsw && havecaptcha && newPswConfirmed) {
                    chang_confirm_button.setBackground(getResources().getDrawable(R.drawable.blue_item_bg));
                    chang_confirm_button.setClickable(true);
                } else {
                    chang_confirm_button.setBackground(getResources().getDrawable(R.drawable.gray_item_bg2));
                    chang_confirm_button.setClickable(false);
                }
                break;
            case Flag.TYPE_CHANGE_PSW:
                if (havenewPsw && haveoldPsw && newPswConfirmed) {
                    chang_confirm_button.setBackground(getResources().getDrawable(R.drawable.blue_item_bg));
                    chang_confirm_button.setClickable(true);
                } else {
                    chang_confirm_button.setBackground(getResources().getDrawable(R.drawable.gray_item_bg2));
                    chang_confirm_button.setClickable(false);
                }
                break;
        }
    }

    private void judgeConfirmPsw(String psw1, String psw2) {
        if (!psw1.equals(psw2)) {
            tv_confirm_hint.setVisibility(VISIBLE);
            newPswConfirmed = false;
        } else {
            tv_confirm_hint.setVisibility(INVISIBLE);
            newPswConfirmed = true;
        }
    }

    //输入框的初始化
    private void initEditText() {
        input_captcha.setTextChangeListener(new SpaceEditText.TextChangeListener() {
            @Override
            public void textChange(String text) {
                text = text.replaceAll(" ", "");
                if (!TextUtils.isEmpty(text)) {
                    havecaptcha = true;
                } else {
                    havecaptcha = false;
                }
            }
        });
    }

    public void setActivity(Activity activity) {
        this.actitity = activity;
    }
}
