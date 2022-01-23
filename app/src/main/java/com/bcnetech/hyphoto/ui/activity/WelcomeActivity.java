package com.bcnetech.hyphoto.ui.activity;

/**
 * Created by a1234 on 16/8/19.
 */

import android.Manifest;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NotificationCompat;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.bcnetech.bcnetchhttp.bean.response.LoginedUser;
import com.bcnetech.bcnetchhttp.config.Flag;
import com.bcnetech.bcnetechlibrary.activity.BaseActivity;
import com.bcnetech.bcnetechlibrary.dialog.ChoiceDialog;
import com.bcnetech.bcnetechlibrary.util.HttpUtil;
import com.bcnetech.bcnetechlibrary.util.LogUtil;
import com.bcnetech.bcnetechlibrary.util.PreferenceUtil;
import com.bcnetech.hyphoto.App;
import com.bcnetech.hyphoto.data.VersionData;
import com.bcnetech.hyphoto.presenter.MainPresenter;
import com.bcnetech.hyphoto.presenter.MainUsPresenter;
import com.bcnetech.hyphoto.service.UpdataService;
import com.bcnetech.hyphoto.ui.activity.personCenter.SettingDetailActivity;
import com.bcnetech.hyphoto.utils.DeviceUtils;
import com.bcnetech.hyphoto.utils.FileUtil;
import com.bcnetech.hyphoto.utils.JsonUtil;
import com.bcnetech.hyphoto.utils.LangugeUtil;
import com.bcnetech.hyphoto.utils.PermissionUtil;
import com.bcnetech.hyphoto.R;
import com.bcnetech.hyphoto.utils.SPUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import static com.bcnetech.bcnetchhttp.bean.response.LoginedUser.getLoginedUser;

public class WelcomeActivity extends FragmentActivity {
    private PreferenceUtil preferenceUtil;
    PackageInfo info;
    private int getVersionCode;
    private boolean isFirstIn;
    DisplayMetrics displayMetrics;
    private ImageView log_img;
    private ChoiceDialog choiceDialog;
    private boolean isBindService = false;
    private ServiceConnection serviceConnection;

    //是否进行数据更新操作
    private boolean isUpdateInfo = false;

    //android 8.0以上版本需要设置NotificationChannel
    public static final String sID = "channel_1";
    //名称会显示在系统-设置-应用-通知中
    public static final String sName = "应用更新提示";
    private NotificationManager mManager;



    private boolean showDialog = true;//腾讯应用市场显示隐私说明弹窗 设置为true

    private static final String SP_IS_FIRST_ENTER_APP = "SP_IS_FIRST_ENTER_APP";
    AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        displayMetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        App.getInstance().setRatio(getRatio(displayMetrics.widthPixels, displayMetrics.heightPixels));
        if (!this.isTaskRoot()) {
            Intent intent = getIntent();
            if (intent != null) {
                String action = intent.getAction();
                if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) && Intent.ACTION_MAIN.equals(action)) {
                    finish();
                    return;
                }
            }
        } else {
            setContentView(R.layout.welcome_layout);
            initView();
            initData();
            onViewClick();
            getMainActivity();
        }
    }


    /**
     * 是否是首次进入APP
     */
    public static boolean isFirstEnterApp() {
        return SPUtils.getInstance().getBoolean(SP_IS_FIRST_ENTER_APP, true);
    }

    /**
     * 保存首次进入APP状态
     */
    public static void saveFirstEnterApp() {
        SPUtils.getInstance().putBoolean(SP_IS_FIRST_ENTER_APP, false);
    }


    private void startDialog() {
        dialog = new AlertDialog.Builder(this).create();
        dialog.show();
        //对话框弹出后点击或按返回键不消失;
        dialog.setCancelable(false);

        final Window window = dialog.getWindow();
        if (window != null) {
            window.setContentView(R.layout.dialog_intimate);
            window.setGravity(Gravity.CENTER);
            //  window.setWindowAnimations(com.jm.core.R.style.anim_panel_up_from_bottom);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            //设置属性
            final WindowManager.LayoutParams params = window.getAttributes();
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            params.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            params.dimAmount = 0.5f;
            window.setAttributes(params);
            TextView textView = window.findViewById(R.id.tv_1);
            TextView tvCancel= window.findViewById(R.id.tv_cancel);
            TextView tvAgree= window.findViewById(R.id.tv_agree);
            tvCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startFinish();
                }
            });
            tvAgree.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    enterApp();
                }
            });

            String str = "欢迎使用辉影APP！为了保护您的隐私和使用安全，请您务必仔细阅读我们的《隐私政策》。在确认充分理解并同意后再开始使用此应用。感谢！" ;
            textView.setText(str);

            SpannableStringBuilder ssb = new SpannableStringBuilder();
            ssb.append(str);
            final int start = str.indexOf("《");//第一个出现的位置
            ssb.setSpan(new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    // dialog.cancel();
                    SPUtils.getInstance().putBoolean(SP_IS_FIRST_ENTER_APP, true);
                    Intent intent = new Intent(WelcomeActivity.this, AgreeActivity.class);
                    startActivity(intent);

                }

                @Override

                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setColor(getResources().getColor(R.color.blue_flag_bg));       //设置文件颜色
                    // 去掉下划线
                    ds.setUnderlineText(false);
                }

            }, start, start + 6, 0);


            textView.setMovementMethod(LinkMovementMethod.getInstance());
            textView.setText(ssb, TextView.BufferType.SPANNABLE);
        }
    }

    private void enterApp() {//同意并继续，进入APP

        saveFirstEnterApp();
        dialog.cancel();
        goActivity();

    }

    private void startFinish() {//更改状态，finish APP
        SPUtils.getInstance().putBoolean(SP_IS_FIRST_ENTER_APP, true);
        dialog.cancel();
        finish();

    }

    public static float getRatio(int screenWidth, int screenHeight) {

        float ratioWidth = (float) screenWidth / 1080;
        float ratioHeight = (float) screenHeight / 1920;
        float RATIO = Math.min(ratioWidth, ratioHeight);
        return RATIO; //字体太小也不好看的
    }

    private void getMainActivity() {

    }

    protected void initView() {
        log_img = (ImageView) findViewById(R.id.log_img);
        SPUtils.getInstance().initSp(WelcomeActivity.this);
        if(isFirstEnterApp()){
            startDialog();
        }
    }

    protected void initData() {

        preferenceUtil = new PreferenceUtil(this);
        isFirstIn = preferenceUtil.getBoolean("isFirstIn", true);
        isUpdateInfo = preferenceUtil.getBoolean("isUpdateInfo", false);



        log_img.setImageResource(R.mipmap.logo);



        if (preferenceUtil == null) {
            preferenceUtil = new PreferenceUtil(this);
        }
        PackageManager manager = this.getPackageManager();
        try {
            info = manager.getPackageInfo(this.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (null != info && info.versionCode <= 89 && !isUpdateInfo) {

            preferenceUtil.commitBoolean("isFirstIn", true);
            preferenceUtil.commitBoolean("isUpdateInfo", true);
            isFirstIn = true;

            getLoginedUser().setLogined(false);
            getLoginedUser().quitLogin();
        }
        if(!isFirstEnterApp()){
            goActivity();
        }


    }

    protected void onViewClick() {

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        if (Flag.isEnglish) {
            LangugeUtil.switchLanguage(this, Flag.ENGLISH_US);
        } else {
            LangugeUtil.switchLanguage(this, Flag.CHINAE);
        }
    }


    private void goActivity() {
        App.getInstance().initUm();
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            public void run() {
                LoginedUser loginedUser = getLoginedUser();
                try {
                    loginedUser.setSupportCamera2(DeviceUtils.isCamera2Support(WelcomeActivity.this));
                } catch (Exception e) {
                    loginedUser.setSupportCamera2(false);
                }
                LoginedUser.setLoginedUser(loginedUser);
                if (!isFirstIn) {
                    Intent intent = new Intent(WelcomeActivity.this, AlbumNewActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                } else {
                    if (Flag.isEnglish) {
                        MainUsPresenter.startAction(WelcomeActivity.this);
                    } else {
                        MainPresenter.startAction(WelcomeActivity.this);
                    }
                }
                if (!isBindService) {
                    finish();
                }
            }
        };
        timer.schedule(task, 1000);
    }




    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


}
