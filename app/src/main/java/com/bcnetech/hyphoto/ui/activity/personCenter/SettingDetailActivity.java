package com.bcnetech.hyphoto.ui.activity.personCenter;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;

import com.bcnetech.bcnetchhttp.RetrofitFactory;
import com.bcnetech.bcnetchhttp.RetrofitInternationalFactory;
import com.bcnetech.bcnetchhttp.RetrofitUploadFactory;
import com.bcnetech.bcnetchhttp.base.BaseResult;
import com.bcnetech.bcnetchhttp.bean.request.FeedBackBody;
import com.bcnetech.bcnetchhttp.bean.request.FileCheckBody;
import com.bcnetech.bcnetchhttp.bean.request.SendEmailBody;
import com.bcnetech.bcnetchhttp.bean.response.BimageUploadingChunk;
import com.bcnetech.bcnetchhttp.bean.response.LoginedUser;
import com.bcnetech.bcnetchhttp.config.Flag;
import com.bcnetech.bcnetchhttp.config.UrlConstants;
import com.bcnetech.bcnetechlibrary.activity.BaseActivity;
import com.bcnetech.bcnetechlibrary.dialog.DGProgressDialog3;
import com.bcnetech.bcnetechlibrary.util.ThreadPoolUtil;
import com.bcnetech.bcnetechlibrary.util.ToastUtil;
import com.bcnetech.hyphoto.App;
import com.bcnetech.hyphoto.R;
import com.bcnetech.hyphoto.data.RegistData;
import com.bcnetech.hyphoto.data.WaterMarkData;
import com.bcnetech.hyphoto.popwindow.ResetPswEngPop;
import com.bcnetech.hyphoto.popwindow.ResetPswPop;
import com.bcnetech.hyphoto.task.MBaseObserver;
import com.bcnetech.hyphoto.ui.dialog.RegisterUsDialog;
import com.bcnetech.hyphoto.ui.view.ChangePswView;
import com.bcnetech.hyphoto.ui.view.SettingAdviseView;
import com.bcnetech.hyphoto.ui.view.SettingUserAgreementView;
import com.bcnetech.hyphoto.ui.view.TitleView;
import com.bcnetech.hyphoto.utils.AndroidBug5497Workaround;
import com.bcnetech.hyphoto.utils.BitmapUtils;
import com.bcnetech.hyphoto.utils.FileUtil;
import com.bcnetech.hyphoto.utils.Image.FileUpload;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;


/**
 * Created by a1234 on 16/10/11.
 * 设置详细页面
 */

public class SettingDetailActivity extends BaseActivity {
    public final static int TYPEADVISE = 5001;
    public final static int TYPEAGREEMENT = 5002;
    public final static int TYPECHANGEPSW = 5003;
    public final static int TYPERESETPSW = 5004;
    private int type;
    private TitleView titleView;
    private SettingAdviseView settingAdviseView;//意见反馈
    private SettingUserAgreementView settingagreeview;//用户协议
    private ChangePswView changepsw;//修改密码
    boolean isCodeSuccess = false;
    private RegistData registData;
    private boolean setTrue = false;
    private String sendCodeNUm;
    // private int time = 0;
    //private ChoiceDialog4 choiceDialog;
    private ResetPswPop resetPswPop;
    private ResetPswEngPop resetPswEngPop;
    private LinearLayout ll_content;
    private boolean shouldShowPop = false;
    private boolean isKeyboardShow = true;//判断键盘是否弹出
    private RegisterUsDialog registerUsDialog;
    private DGProgressDialog3 dgProgressDialog3;

    // private Bitmap b;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_detail_layout);
        type = this.getIntent().getIntExtra("type", 0);
        AndroidBug5497Workaround.assistActivity(this);
        initView();
        initData();
        onViewClick();
    }

    @Override
    protected void initView() {
        titleView = (TitleView) findViewById(R.id.sett_title_view);
        settingAdviseView = (SettingAdviseView) findViewById(R.id.sett_advise_view);
        settingagreeview = (SettingUserAgreementView) findViewById(R.id.sett_agree_view);
        changepsw = (ChangePswView) findViewById(R.id.sett_changepsw_view);
        ll_content = (LinearLayout) findViewById(R.id.ll_content);
    }

    private void setType(int type) {
        this.type = type;
        switch (type) {
            case TYPEADVISE:
                titleView.setSettingTitle(getResources().getString(R.string.advise_feedback));
                titleView.setType(TitleView.SETTING);
                titleView.setLeftImg(R.drawable.arrow_back);
                settingAdviseView.setVisibility(View.VISIBLE);
                settingagreeview.setVisibility(View.GONE);
                changepsw.setVisibility(View.GONE);
                break;
            case TYPEAGREEMENT:
                titleView.setSettingTitle(getResources().getString(R.string.user_contract));
                titleView.setType(TitleView.SETTING);
                titleView.setLeftImg(R.drawable.arrow_back);
                settingagreeview.setVisibility(View.VISIBLE);
                settingagreeview.loadPage();
                settingAdviseView.setVisibility(View.GONE);
                changepsw.setVisibility(View.GONE);
                break;
            case TYPECHANGEPSW:
                titleView.setSettingTitle(getResources().getString(R.string.change_password));
                titleView.setType(TitleView.SETTING);
                titleView.setLeftImg(R.drawable.arrow_back);
                titleView.setRightTextColor(getResources().getColor(R.color.red_us_border));
                titleView.setRightText(getResources().getString(R.string.find_psw));
            /*    if (time == 0) {

                }*/
                changepsw.setVisibility(View.VISIBLE);
                changepsw.setType(Flag.TYPE_CHANGE_PSW);
                settingagreeview.setVisibility(View.GONE);
                settingAdviseView.setVisibility(View.GONE);
                break;
            case TYPERESETPSW:
                titleView.setSettingTitle(getResources().getString(R.string.find_psw));
                changepsw.setType(Flag.TYPE_FIND_PSW);
                titleView.setType(TitleView.SETTING);
                titleView.setRightTextColor(getResources().getColor(R.color.red_us_border));
                titleView.setLeftImg(R.drawable.arrow_back);
                // titleView.setRightText(time + "s");
                break;
        }
    }

    @Override
    protected void initData() {
        changepsw.setType(Flag.TYPE_CHANGE_PSW);
        changepsw.setActivity(this);
        setType(type);
    }

    @Override
    protected void onViewClick() {
        titleView.setLeftListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        titleView.setRightTextListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                }
                if (isKeyboardShow) {
                    shouldShowPop = true;
                } else {
                    shouldShowPop = false;
                    if (type == TYPECHANGEPSW || type == TYPERESETPSW) {
                        LoginedUser loginedUser = LoginedUser.getLoginedUser();
                        if (!loginedUser.getUser_name().contains("@")) {
                            if (App.getInstance().isend()) {
                                showSendDialog(loginedUser.getUser_name(), null);
                            } else {
                                setType(TYPERESETPSW);
                                changepsw.starttime(App.getInstance().getNewTime());
                                titleView.setRightTextColor(Color.BLACK);
                            }

                        } else {
                            sendMail(loginedUser.getUser_name());
                        }
                    }

                }
              /*  if (type == TYPECHANGEPSW || type == TYPERESETPSW) {
                    if (!Flag.isEnglish) {
                        LoginedUser loginedUser = LoginedUser.getLoginedUser();
                        showSendDialog(loginedUser.getUser_name(), null);
                    } else {
                        sendMail();
                        showSendUSDialog(null);
                    }
                }*/
            }
        });

        changepsw.setMLoginInter(new ChangePswView.ChangePswInter() {
            @Override
            public void onAniming(boolean isaniming) {

            }

            @Override
            public void onCountDown(int nowtime) {
                titleView.setRightTextCanClick(nowtime + "s", false);
                App.getInstance().setNewTime(nowtime);
                if (nowtime == 0) {
                    App.getInstance().setIsend(true);
                    titleView.setRightTextColor(Color.BLUE);
                    titleView.setRightTextCanClick(getResources().getString(R.string.resendyzm), true);
                }
            }

            @Override
            public void onChangePswClick() {

            }

            @Override
            public void onChangePswResponed(boolean issuccess) {
                if (issuccess) {
                    finish();
                } else {
                }
            }

            @Override
            public void onResetPswClick() {
            }

            @Override
            public void onResetPswResponed(boolean issuccess) {
                if (issuccess) {
                    finish();
                } else {
                }

            }

            @Override
            public void onKeboardShow(boolean isshow) {
                SettingDetailActivity.this.isKeyboardShow = isshow;
                //隐藏键盘之后弹出popwinsow
                if (!isshow) {
                    if (shouldShowPop) {
                        if (type == TYPECHANGEPSW || type == TYPERESETPSW) {
                            LoginedUser loginedUser = LoginedUser.getLoginedUser();
                            if (!loginedUser.getUser_name().contains("@")) {
                                if (App.getInstance().isend()) {
                                    showSendDialog(loginedUser.getUser_name(), null);
                                } else {
                                    setType(TYPERESETPSW);
                                    changepsw.starttime(App.getInstance().getNewTime());
                                    titleView.setRightTextColor(Color.BLACK);
                                }
                                //showSendDialog(loginedUser.getUser_name(), null);
                            } else {
                                sendMail(loginedUser.getUser_name());
                            }
                        }
                        shouldShowPop = false;
                    }
                }
            }
        });

        settingAdviseView.onClickFeedback(new SettingAdviseView.Feedbackinter() {
            @Override
            public void onClick(String content, String name, String phone, final List<WaterMarkData> wmlist) {
                if (null == dgProgressDialog3) {
                    dgProgressDialog3 = new DGProgressDialog3(SettingDetailActivity.this, true, getResources().getString(R.string.waiting_please));
                }
                dgProgressDialog3.show();

                mContent = content;
                app_ver = getVersionName();
                mName = name;
                mPhone = phone;
                mImages ="";
                if (wmlist.size() > 1) {
                    if (null == mRunOnDraw) {
                        mRunOnDraw = new LinkedList<>();
                    } else {
                        mRunOnDraw.clear();
                    }
                    ThreadPoolUtil.execute(new Runnable() {
                        @Override
                        public void run() {
                            for (int i = 0; i < wmlist.size(); i++) {
                                if(null==wmlist.get(i)){
                                    continue;
                                }
                                Bitmap bitmap=BitmapFactory.decodeFile(wmlist.get(i).getWatermarkurl().substring(7));
                                String url;
                                try {
                                    url=FileUtil.saveFeedBackBitmap(BitmapUtils.compressImage(bitmap),System.currentTimeMillis()+"");
                                } catch (IOException e) {
                                    url=wmlist.get(i).getWatermarkurl().substring(7);
                                    e.printStackTrace();
                                }
                                mRunOnDraw.add(url);
                            }
                            feedBackImageUplaod(mRunOnDraw.poll());
                        }
                    });
                } else {
                    feedBackContent(mContent, app_ver, mName, mPhone, null);
                }
            }
        });
    }

    private String mContent, app_ver, mName, mPhone, mImages;
    private Queue<String> mRunOnDraw;

    /**
     * 格式化电话号码
     *
     * @param s
     * @return
     */
    private String formattingPhone(String s) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            if (i != 3 && i != 8 && s.charAt(i) == ' ') {
                continue;
            } else {
                sb.append(s.charAt(i));
                if ((sb.length() == 4 || sb.length() == 9) && sb.charAt(sb.length() - 1) != ' ') {
                    sb.insert(sb.length() - 1, ' ');
                }
            }
        }
        return sb.toString();
    }

    private void showSendDialog(final String number, Bitmap b) {
        if (resetPswPop == null) {
            resetPswPop = new ResetPswPop(SettingDetailActivity.this);
        }

        resetPswPop.setResetPswPopListener(SettingDetailActivity.this, new ResetPswPop.ResetPswPopListener() {
            @Override
            public void onConfirm() {
                resetPswPop.dismissPop();
                changepsw.sendcode(number);
                setType(TYPERESETPSW);
                changepsw.starttime();
                App.getInstance().setIsend(false);
                titleView.setRightTextColor(Color.BLACK);
            }

            @Override
            public void onCancel() {
                resetPswPop.dismissPop();
            }
        });

       /* choiceDialog.setChoiceInterface(new ChoiceDialog4.ChoiceInterface() {
            @Override
            public void choiceOk() {
                dissmissChoiceDialog();
                //changepsw.sendcode(number);
                setType(TYPERESETPSW);
                changepsw.starttime();
                //starttime();
            }

            @Override
            public void choiceCencel() {
                dissmissChoiceDialog();
            }
        });*/
        // choiceDialog.show();
        resetPswPop.setTitle(this.getResources().getString(R.string.send_captcha));
        resetPswPop.setCancel(this.getResources().getString(R.string.cancel));
        resetPswPop.setOk(this.getResources().getString(R.string.confirm));
        //resetPswPop.setBackImage(b);
        resetPswPop.setPhone(number);
        //  resetPswPop.showAtLocation(SettingDetailActivity.this.getWindow().getDecorView(), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        resetPswPop.showPop(SettingDetailActivity.this.getWindow().getDecorView().getRootView());
    }

    private void showSendUSDialog(Bitmap b) {
        if (resetPswEngPop == null) {
            resetPswEngPop = new ResetPswEngPop(SettingDetailActivity.this);
        }

        resetPswEngPop.setResetPswPopListener(SettingDetailActivity.this, new ResetPswEngPop.ResetPswEngPopListener() {
            @Override
            public void onResending() {

            }

            @Override
            public void onCancel() {
                resetPswEngPop.dismissPop();
            }

            @Override
            public void onClickMail() {

            }
        });
        resetPswEngPop.showPop(SettingDetailActivity.this.getWindow().getDecorView().getRootView());
    }

    private void sendMail(final String number) {

        RetrofitInternationalFactory.getInstence().API().sendEmail(new SendEmailBody(number))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MBaseObserver<Object>(SettingDetailActivity.this, false) {
                    @Override
                    protected void onSuccees(BaseResult<Object> t) throws Exception {
                        if (null == registerUsDialog) {
                            registerUsDialog = RegisterUsDialog.createInstance(SettingDetailActivity.this);
                        }
                        registerUsDialog.show();
                        registerUsDialog.setAccount("<u>" + "<font color='#0057FF'>" + number + "</font>" + "</u>");
                        registerUsDialog.setClickLitner(new RegisterUsDialog.ChoiceInterface() {
                            @Override
                            public void close() {
                                registerUsDialog.dismiss();
//                        closeKeyBoard();
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

    /*protected void dissmissChoiceDialog() {
        if (choiceDialog != null) {
            choiceDialog.dismiss();
        }
    }*/

    /**
     * 获取应用版本号
     *
     * @return
     * @throws Exception
     */
    private String getVersionName() {
        String version;
        // 获取packagemanager的实例
        PackageManager packageManager = this.getPackageManager();
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        try {
            PackageInfo packInfo = packageManager.getPackageInfo(this.getPackageName(), 0);
            version = packInfo.versionName;
        } catch (Exception e) {
            version = "1";
        }
        return version;

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        settingAdviseView.onActivityResult(requestCode, resultCode, data);
    }

    boolean isFail = false;

    private void feedBackImageUplaod(final String url) {
        FileCheckBody fileCheckBody = FileUpload.fileUploadInfoCheckBody(url, FileUpload.fileUploadCheck(url, ""));
        fileCheckBody.setCode("private");
        fileCheckBody.setScope("1");

        RetrofitUploadFactory.getUPloadInstence().API().fileCheck(fileCheckBody)

                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MBaseObserver<BimageUploadingChunk>(this, false) {
                    @Override
                    protected void onSuccees(final BaseResult<BimageUploadingChunk> checkResult) throws Exception {


                        if (checkResult.getCode() == 20014) {
                            isFail = false;
                            //分片上传
                            for (int i = 0; i < checkResult.getData().getList().size(); i++) {
                                BimageUploadingChunk.BimageUploadingChunkList bimageUploadingChunk = checkResult.getData().getList().get(i);

                                Map<String, String> mapParam = FileUpload.fileUploaInfo(url);
                                mapParam.put("fileId", checkResult.getData().getFileId());
                                mapParam.put("chunkSha1", bimageUploadingChunk.getChunkSha1());
                                mapParam.put("chunkSize", bimageUploadingChunk.getChunkSize());
                                mapParam.put("name", bimageUploadingChunk.getChunkSize());
                                mapParam.put("code", "private");

                                String url = getUrl(mapParam, UrlConstants.DEFAUL_WEB_SITE_UPLOAD, UrlConstants.FILE_UPLOAD);

                                StringBuffer sb = new StringBuffer();
                                sb.append(Flag.FENPIAN);
                                // sb1.append("/");
                                sb.append(mapParam.get("fileName"));
                                sb.append("_data" + (bimageUploadingChunk.getChunkIndex()));

                                File file = new File(sb.toString());
                                MultipartBody.Builder builder = new MultipartBody.Builder();
                                builder.setType(MultipartBody.FORM);

                                builder.addFormDataPart("file", file.getName(), RequestBody.create(MediaType.parse("application/octet-stream"), file));
                                final int finalI = i;
                                RetrofitUploadFactory.getUPloadInstence().API().uploadFile(url, builder.build())
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(new MBaseObserver<Object>(SettingDetailActivity.this, false) {
                                            @Override
                                            protected void onSuccees(final BaseResult<Object> result) throws Exception {
                                                if (!isFail && finalI == checkResult.getData().getList().size() - 1) {
//                                                    updateHead(checkResult.getData().getFileId());
                                                    if (mRunOnDraw != null && !mRunOnDraw.isEmpty()) {
                                                        mImages += checkResult.getData().getFileId() + ",";
                                                        feedBackImageUplaod(mRunOnDraw.poll());
                                                    } else {
                                                        mImages += checkResult.getData().getFileId();
                                                        feedBackContent(mContent, app_ver, mName, mPhone, mImages);
                                                    }
                                                }
                                                if (isFail && finalI == checkResult.getData().getList().size() - 1) {
                                                    ToastUtil.toast(SettingDetailActivity.this.getResources().getString(R.string.upload_fail));
                                                    dgProgressDialog3.dismiss();
                                                }
                                            }

                                            @Override
                                            protected void onCodeError(BaseResult<Object> t) throws Exception {
                                                isFail = true;
                                                if (isFail && finalI == checkResult.getData().getList().size() - 1) {
                                                    ToastUtil.toast(SettingDetailActivity.this.getResources().getString(R.string.upload_fail));
                                                    dgProgressDialog3.dismiss();
                                                }
                                            }

                                            @Override
                                            protected void onFailure(Throwable e, boolean isNetWorkError) throws Exception {
                                                isFail = true;
                                                if (isFail && finalI == checkResult.getData().getList().size() - 1) {
                                                    ToastUtil.toast(SettingDetailActivity.this.getResources().getString(R.string.upload_fail));
                                                    dgProgressDialog3.dismiss();
                                                }
                                            }
                                        });
                            }

                        } else if (checkResult.getCode() == 20018) {
                            if (mRunOnDraw != null && !mRunOnDraw.isEmpty()) {
                                mImages += checkResult.getData().getFileId() + ",";
                                feedBackImageUplaod(mRunOnDraw.poll());
                            } else {
                                mImages += checkResult.getData().getFileId();
                                feedBackContent(mContent, app_ver, mName, mPhone, mImages);
                            }
                        }

                    }

                    @Override
                    protected void onCodeError(BaseResult<BimageUploadingChunk> t) throws Exception {
                        ToastUtil.toast(t.getMessage());
                        dgProgressDialog3.dismiss();
                    }

                    @Override
                    protected void onFailure(Throwable e, boolean isNetWorkError) throws Exception {
                        ToastUtil.toast(e.getMessage());
                        dgProgressDialog3.dismiss();
                    }
                });
    }

    private void feedBackContent(String content, String app_ver, String name, String phone, String images) {
        RetrofitFactory.getInstence().API().feedBack(new FeedBackBody(content, app_ver, name, phone, images))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MBaseObserver<Object>(SettingDetailActivity.this, false) {
                    @Override
                    protected void onSuccees(BaseResult<Object> t) throws Exception {
                        ToastUtil.toast(t.getMessage());
                        finish();
                        dgProgressDialog3.dismiss();
                    }

                    @Override
                    protected void onCodeError(BaseResult<Object> t) throws Exception {
                        ToastUtil.toast(t.getMessage());
                        dgProgressDialog3.dismiss();
                    }

                    @Override
                    protected void onFailure(Throwable e, boolean isNetWorkError) throws Exception {
                        ToastUtil.toast(e.getMessage());
                        dgProgressDialog3.dismiss();
                    }
                });
    }

    public String getUrl(Map<String, String> parms, String host, String url) {
        // 拼接参数到Url后面
        if (null != parms && !parms.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<String, String> e : parms.entrySet()) {
                sb.append(e.getKey()).append("=")
                        .append(URLEncoder.encode(e.getValue().trim()))
                        .append("&");
            }

            sb.deleteCharAt(sb.length() - 1);
            url = url + "?" + sb.toString();
        }

        return url;
    }
}
