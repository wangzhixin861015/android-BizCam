package com.bcnetech.hyphoto.ui.activity.personCenter;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bcnetech.bcnetchhttp.bean.response.LoginedUser;
import com.bcnetech.bcnetchhttp.config.Flag;
import com.bcnetech.bcnetechlibrary.activity.BaseActivity;
import com.bcnetech.bcnetechlibrary.dialog.ChoiceDialog;
import com.bcnetech.bcnetechlibrary.drawable.CircleDefultDrawable;
import com.bcnetech.bcnetechlibrary.drawable.CircleImageDrawable;
import com.bcnetech.bcnetechlibrary.util.EventStatisticsUtil;
import com.bcnetech.bcnetechlibrary.util.ToastUtil;
import com.bcnetech.bcnetechlibrary.util.sharepreference.SharePreferences;
import com.bcnetech.hyphoto.EventCommon;
import com.bcnetech.hyphoto.R;
import com.bcnetech.hyphoto.data.SqlControl.BaseSqlControl;
import com.bcnetech.hyphoto.data.SqlControl.DownloadInfoSqlControl;
import com.bcnetech.hyphoto.data.SqlControl.PresetParmsSqlControl;
import com.bcnetech.hyphoto.model.BleConnectModel;
import com.bcnetech.hyphoto.presenter.BizCamHelpPresenter;
import com.bcnetech.hyphoto.presenter.BizCamUToPresenter;
import com.bcnetech.hyphoto.presenter.PresetLoadManagePresenter;
import com.bcnetech.hyphoto.presenter.UploadManagerPresenter;
import com.bcnetech.hyphoto.receiver.PresetReceiver;
import com.bcnetech.hyphoto.receiver.UploadManagerReceiver;
import com.bcnetech.hyphoto.sql.dao.DownloadInfoData;
import com.bcnetech.hyphoto.sql.dao.PresetParm;
import com.bcnetech.hyphoto.task.manage.UploadManager;
import com.bcnetech.hyphoto.ui.activity.MainActivity;
import com.bcnetech.hyphoto.ui.activity.MainUsActivity;
import com.bcnetech.hyphoto.ui.view.CameraSettingItemView;
import com.bcnetech.hyphoto.ui.view.DrawTextImageView;
import com.bcnetech.hyphoto.ui.view.EditUserView;
import com.bcnetech.hyphoto.utils.BitmapUtils;
import com.bcnetech.hyphoto.utils.FileUtil;
import com.bcnetech.hyphoto.utils.FontImageUtil;
import com.bcnetech.hyphoto.utils.ImageUtil;
import com.bcnetech.hyphoto.utils.PinyUtil;
import com.bcnetech.hyphoto.utils.cache.CacheManager;
import com.bcnetech.bizcamerlibrary.camera.utils.CameraFileUtil;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.util.List;

import static com.bcnetech.bcnetchhttp.bean.response.LoginedUser.getLoginedUser;


/**
 * Created by a1234 on 2017/12/6.
 */
public class UserCenterActivity extends BaseActivity {
    private CameraSettingItemView model_manager;
    private CameraSettingItemView bizcam_manager;
    private CameraSettingItemView upload_manaager;
    private CameraSettingItemView customer_service_manager;
    private CameraSettingItemView account_security_manager;
    private CameraSettingItemView attach_manager;
    private CameraSettingItemView help_manager;
    private CameraSettingItemView feedback_manager;
    private CameraSettingItemView contract_manager;
    private CameraSettingItemView bizcam_u_to;
    private TextView tv_switch_account;
    private DrawTextImageView iv_head;
    private TextView title_edit;
    private ImageView title_close;
    private EditUserView edit_view;
    private LoginedUser loginedUser;
    private TextView tv_head;
    private ChoiceDialog delteDialog;
    private PresetReceiver receiver;
    private PresetParmsSqlControl presetParmsSqlControl;
    private int presetListSize = 0;

    private CameraSettingItemView cache_clear;
    private CameraSettingItemView only_wifi;
    private String CACHEURL = Environment.getExternalStorageDirectory() + Flag.BaseCache;

    private UploadManagerReceiver uploadManagerReceiver;
    private DownloadInfoSqlControl downloadInfoSqlControl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usercenter);
        initView();
        initData();
        onViewClick();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (presetParmsSqlControl != null) {
            presetParmsSqlControl.startQueryBySystem("android");
        }
        if (downloadInfoSqlControl != null) {
            downloadInfoSqlControl.QueryInfoAll();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void initView() {
        model_manager = (CameraSettingItemView) findViewById(R.id.model_manager);
        bizcam_manager = (CameraSettingItemView) findViewById(R.id.bizcam_manager);
        upload_manaager = (CameraSettingItemView) findViewById(R.id.upload_manaager);
        customer_service_manager = (CameraSettingItemView) findViewById(R.id.customer_service_manager);
        account_security_manager = (CameraSettingItemView) findViewById(R.id.account_security_manager);
        attach_manager = (CameraSettingItemView) findViewById(R.id.attach_manager);
        help_manager = (CameraSettingItemView) findViewById(R.id.help_manager);
        feedback_manager = (CameraSettingItemView) findViewById(R.id.feedback_manager);
        contract_manager = (CameraSettingItemView) findViewById(R.id.contract_manager);
        tv_switch_account = (TextView) findViewById(R.id.tv_switch_account);
        title_edit = (TextView) findViewById(R.id.title_edit);
        iv_head = (DrawTextImageView) findViewById(R.id.iv_head);
        title_close = (ImageView) findViewById(R.id.title_close);
        edit_view = (EditUserView) findViewById(R.id.edit_view);
        tv_head = (TextView) findViewById(R.id.tv_head);
        cache_clear = (CameraSettingItemView) findViewById(R.id.cache_clear);
        only_wifi = (CameraSettingItemView) findViewById(R.id.only_wifi);
        bizcam_u_to = findViewById(R.id.bizcam_ut);
        getFileUrl();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != receiver) {
            receiver.unregister(this);
        }
        if (uploadManagerReceiver != null) {
            uploadManagerReceiver.unregister(this);
        }
    }

    @Override
    protected void initData() {
        model_manager.isShowButton(false);
        model_manager.setSetting_name(getResources().getString(R.string.model_manager));
        bizcam_manager.isShowButton(false);
        bizcam_manager.setSetting_name(getResources().getString(R.string.preset_manage));
        upload_manaager.isShowButton(false);
        upload_manaager.setSetting_name(getResources().getString(R.string.upload_manage));
        customer_service_manager.isShowButton(false);
        customer_service_manager.setSetting_name(getResources().getString(R.string.hardware_service));
        account_security_manager.isShowButton(false);
        account_security_manager.setSetting_name(getResources().getString(R.string.change_password));
        attach_manager.isShowButton(false);
        attach_manager.setSetting_name(getResources().getString(R.string.attach_account));
        help_manager.isShowButton(false);
        help_manager.setSetting_name(getResources().getString(R.string.user_help));
        feedback_manager.isShowButton(false);
        feedback_manager.setSetting_name(getResources().getString(R.string.advise_feedback));
        contract_manager.isShowButton(false);
        contract_manager.setSetting_name(getResources().getString(R.string.user_contract));

        bizcam_u_to.setSetting_name(getString(R.string.spyt));
        bizcam_u_to.setSwitchButton(false);

        setUserInfo();
        presetParmsSqlControl = new PresetParmsSqlControl(this);
        receiver = new PresetReceiver() {
            @Override
            public void onGetData(int count) {
                bizcam_manager.setSetting_parm(count + "");
            }
        };
        receiver.register(this);


        downloadInfoSqlControl = new DownloadInfoSqlControl(this);


        uploadManagerReceiver = new UploadManagerReceiver() {
            @Override
            public void onGetData(String type) {

            }

            @Override
            public void onDeleteData(int count) {
                upload_manaager.setSetting_parm(count + "");
            }
        };

        uploadManagerReceiver.register(this);


        cache_clear.setSetting_name(getResources().getString(R.string.clear_cache));
        cache_clear.isShowButton(false);
        only_wifi.setSetting_name(getResources().getString(R.string.only_wifi));
        only_wifi.isShowButton(true);
        only_wifi.setSwitchButton(!loginedUser.isonlywifi());
    }

    @Override
    protected void onViewClick() {
        title_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        iv_head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit_view.showME();
            }
        });
        title_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit_view.showME();
            }
        });
        edit_view.setEditListener(new EditUserView.EditUserListener() {
            @Override
            public void onClose() {
                // edit_view.setVisibility(View.GONE);
                setUserInfo();
            }
        });

        tv_switch_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if (BlueToothLoad.getInstance().blueToothIsConnection()) {
                if (BleConnectModel.getBleConnectModelInstance().isCurrentConnect()) {
                    BleConnectModel.getBleConnectModelInstance().disconnectAllDevice();
                    // BlueToothLoad.getInstance().stopConnect();
                }
                int count = UploadManager.getInstance().getUploadCount();
                if (count > 0) {
                    showDeleteDialog();
                } else {
                    if (Flag.isEnglish) {
                        goMainUS();
                    } else {
                        goMain();
                    }
                }
                CacheManager.DeleteAllCache(UserCenterActivity.this);
            }
        });
        upload_manaager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventStatisticsUtil.event(UserCenterActivity.this, EventCommon.PERSONCENTER_SETTING_UPLOAD_MANAGE_LOGIN);
                UploadManagerPresenter.startAction(UserCenterActivity.this);
            }
        });

        bizcam_manager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventStatisticsUtil.event(UserCenterActivity.this, EventCommon.PERSONCENTER_SETTING_PRESET_MANAGE_LOGIN);
                PresetLoadManagePresenter.startAction(UserCenterActivity.this);
            }
        });
        account_security_manager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /*
                LoginPresenter.startAction(UserCenterActivity.this, Flag.TYPE_CHANGE_PSW);*/
                EventStatisticsUtil.event(UserCenterActivity.this, EventCommon.PERSONCENTER_SETTING_CHANGE_PASSWORD_LOGIN);
                Intent intent = new Intent(UserCenterActivity.this, SettingDetailActivity.class);
                intent.putExtra("type", SettingDetailActivity.TYPECHANGEPSW);
                startActivity(intent);
            }
        });
        feedback_manager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventStatisticsUtil.event(UserCenterActivity.this, EventCommon.PERSONCENTER_SETTING_ADVISE_FEEDBACK_LOGIN);
                Intent intent = new Intent(UserCenterActivity.this, SettingDetailActivity.class);
                intent.putExtra("type", SettingDetailActivity.TYPEADVISE);
                startActivity(intent);
            }
        });
        contract_manager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventStatisticsUtil.event(UserCenterActivity.this, EventCommon.PERSONCENTER_SETTING_USER_AGREEMENT);
                Intent intent = new Intent(UserCenterActivity.this, SettingDetailActivity.class);
                intent.putExtra("type", SettingDetailActivity.TYPEAGREEMENT);
                startActivity(intent);
            }
        });

        bizcam_u_to.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BizCamUToPresenter.startAction(UserCenterActivity.this);
            }
        });


        help_manager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BizCamHelpPresenter.startAction(UserCenterActivity.this);
            }
        });

        presetParmsSqlControl.setListener(new BaseSqlControl.SqlControlListener() {
            @Override
            public void queryListener(Object... parms) {
                List<PresetParm> presetParms = (List<PresetParm>) parms[0];
                presetListSize = presetParms.size();
                bizcam_manager.setSetting_parm(presetListSize + "");
            }

            @Override
            public void insertListener(Object... parms) {
            }

            @Override
            public void deletListener(Object... parms) {
            }

            @Override
            public void upDataListener(Object... parms) {

            }
        });

        downloadInfoSqlControl.setListener(new BaseSqlControl.SqlControlListener() {
            @Override
            public void queryListener(Object... parms) {
                List<DownloadInfoData> downloadInfoDatas = (List<DownloadInfoData>) parms[0];
                upload_manaager.setSetting_parm(downloadInfoDatas.size() + "");
            }

            @Override
            public void insertListener(Object... parms) {

            }

            @Override
            public void deletListener(Object... parms) {

            }

            @Override
            public void upDataListener(Object... parms) {

            }
        });


        cache_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventStatisticsUtil.event(UserCenterActivity.this, EventCommon.PERSONCENTER_SETTING_CLEAR_CACHE);
                cleanFileUrl();
                getFileUrl();

            }
        });
        only_wifi.setCurrentCheckedListener(new CameraSettingItemView.CurrentChecked() {
            @Override
            public void onCurrentCheck(boolean currentcheck) {
                EventStatisticsUtil.event(UserCenterActivity.this, EventCommon.PERSONCENTER_SETTING_ONLY_WIFI);
                loginedUser.setIsonlywifi(!currentcheck);
                LoginedUser.setLoginedUser(loginedUser);
            }
        });

    }


    private void showDeleteDialog() {
        if (delteDialog == null) {
            delteDialog = ChoiceDialog.createInstance(this);
            delteDialog.setAblumTitleBlack(getResources().getString(R.string.upload_info));
            delteDialog.setAblumMessageGray(getResources().getString(R.string.confirm_delete_upload));
            delteDialog.setChoiceCallback(new ChoiceDialog.CardDialogCallback() {
                @Override
                public void onOKClick() {
                    UploadManager.getInstance().dellAllRunTask();
                    delteDialog.dismiss();
                    //goMain();
//                            goMainUS();
                    if (Flag.isEnglish) {
                        goMainUS();
                    } else {
                        goMain();
                    }
                }

                @Override
                public void onCancelClick() {
                    delteDialog.dismiss();
                }

                @Override
                public void onDismiss() {

                }
            });
        }
        delteDialog.show();
    }

    private void goMain() {
        MobclickAgent.onProfileSignIn(getLoginedUser().getUser_name());

        SharePreferences preferences = SharePreferences.instance();
        preferences.putBoolean("isFirstIn", true);
        getLoginedUser().setLogined(false);
        getLoginedUser().quitLogin();
               /*  LoginPresenter.startAction(UserInfoActivity.this,Flag.TYPE_LOGIN);
                finish();*/
        Intent intent = new Intent();
        intent.setClass(UserCenterActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("isToken", true);
        startActivity(intent);
    }

    private void goMainUS() {
        MobclickAgent.onProfileSignIn(getLoginedUser().getUser_name());

        SharePreferences preferences = SharePreferences.instance();
        preferences.putBoolean("isFirstIn", true);
        getLoginedUser().setLogined(false);
        getLoginedUser().quitLogin();
               /*  LoginPresenter.startAction(UserInfoActivity.this,Flag.TYPE_LOGIN);
                finish();*/
        Intent intent = new Intent();
        intent.setClass(UserCenterActivity.this, MainUsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("isToken", true);
        startActivity(intent);
    }

    private void setUserInfo() {
        loginedUser = getLoginedUser();
        if (loginedUser != null) {
            iv_head.showText(false);
            tv_head.setText(loginedUser.getUser_name());
            //存在昵称
            if (loginedUser.getNickname() != null) {
                if (!TextUtils.isEmpty(loginedUser.getNickname())) {
                    tv_head.setText(loginedUser.getNickname());
                    String f = PinyUtil.getSpells(loginedUser.getNickname());
                    iv_head.setImageDrawable(new CircleDefultDrawable(FontImageUtil.setDefaultColor(UserCenterActivity.this, f), loginedUser.getNickname().charAt(0) + ""));
                } else {
                    //没有昵称,使用手机后四位
                    String number = loginedUser.getUser_name();
                    String phonelast = number.substring(number.length() - 4, number.length());
                    tv_head.setText(phonelast);
                    iv_head.setImageDrawable(new CircleDefultDrawable(FontImageUtil.setDefaultColor(UserCenterActivity.this, phonelast), phonelast.charAt(0/*phonelast.length()>0?phonelast.length()-1:0*/) + ""));
                }
            } else {
                String number = loginedUser.getUser_name();
                String nickname = number.substring(number.length() - 4, number.length());
                tv_head.setText(nickname);
                iv_head.setImageDrawable(new CircleDefultDrawable(FontImageUtil.setDefaultColor(UserCenterActivity.this, nickname), nickname.charAt(0) + ""));
            }
            if (loginedUser.getLoginData().getAvatar() != null && !loginedUser.getLoginData().getAvatar().equals("null")) {

                String avatar = BitmapUtils.getBitmapUrl3(loginedUser.getLoginData().getAvatar());
                ImageUtil.EBizImageLoad(avatar, new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String s, View view) {

                    }

                    @Override
                    public void onLoadingFailed(String s, View view, FailReason failReason) {

                    }

                    @Override
                    public void onLoadingComplete(String s, View view, Bitmap bitmap) {

                        if (bitmap != null) {
                            iv_head.setImageDrawable(new CircleImageDrawable(bitmap));
                            iv_head.showText(false);
                            edit_view.setCurrentHeadimg(bitmap);
                        }/*else{
                                defaultHeadImg();
                            }*/
                    }

                    @Override
                    public void onLoadingCancelled(String s, View view) {

                    }
                });
            }
            iv_head.invalidate();
            tv_head.invalidate();
        }
        edit_view.setUserInfo();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        edit_view.onActivityResult(requestCode, resultCode, data);
    }

    private void getFileUrl() {
        File dirFiles = new File(CACHEURL);
        long cachesize = FileUtil.getFolderSize(dirFiles);
        String size = FileUtil.convertFileSize(cachesize);
        cache_clear.setSetting_parm(size != null ? size : "0 B");
        CacheManager.DeleteAllCache(getApplicationContext());

    }

    private void cleanFileUrl() {
        File dirFiles = new File(CACHEURL);
        CameraFileUtil.DeleteFile(dirFiles);
        ToastUtil.toast(getResources().getString(R.string.clear_cache_ok));
    }
}
