package com.bcnetech.hyphoto.ui.activity.camera;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.PopupWindow;

import com.bcnetech.bcnetchhttp.bean.response.LoginedUser;
import com.bcnetech.bcnetchhttp.config.Flag;
import com.bcnetech.bcnetechlibrary.activity.BaseActivity;
import com.bcnetech.bcnetechlibrary.util.EventStatisticsUtil;
import com.bcnetech.bcnetechlibrary.util.ToastUtil;
import com.bcnetech.hyphoto.EventCommon;
import com.bcnetech.hyphoto.data.SqlControl.CameraSettingData;
import com.bcnetech.hyphoto.databinding.ActivityCamerasettingBinding;
import com.bcnetech.hyphoto.presenter.WaterMarkPresenter;
import com.bcnetech.hyphoto.ui.popwindow.CameraSettingPop;
import com.bcnetech.hyphoto.ui.view.CameraSettingItemView;
import com.bcnetech.hyphoto.ui.view.TitleView;
import com.bcnetech.hyphoto.utils.FileUtil;
import com.bcnetech.bizcamerlibrary.camera.data.CameraStatus;
import com.bcnetech.bizcamerlibrary.camera.utils.CameraFileUtil;
import com.bcnetech.hyphoto.R;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by a1234 on 2017/11/30.
 */

public class CameraSettingActivity extends BaseActivity {
    private ActivityCamerasettingBinding activityCamerasettingBinding;
    private CameraSettingPop cameraSettingPop;
    private ArrayList<CameraSettingData> list;
    private CameraStatus cameraStatus;
    private int currentSelect = Flag.PICSIZE;
    public static final int REQUESE = 11;
    private LoginedUser loginedUser;
    private String CACHEURL = Environment.getExternalStorageDirectory() + Flag.BaseCache;
    private PackageInfo info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
        onViewClick();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void initView() {
        activityCamerasettingBinding = DataBindingUtil.setContentView(this, R.layout.camera_setting_new_layout);
    }

    @Override
    protected void initData() {
        cameraStatus = CameraStatus.getCameraStatus();
        activityCamerasettingBinding.cameraSettingTitle.setType(TitleView.PIC_PARMS_NEW);
        activityCamerasettingBinding.cameraSettingTitle.setRightImgIsShow(false);
        activityCamerasettingBinding.cameraSettingTitle.setTitleText(getResources().getString(R.string.set));
        activityCamerasettingBinding.picSize.setSetting_name(getResources().getString(R.string.pic_size));
        activityCamerasettingBinding.picSize.isShowButton(false);
        activityCamerasettingBinding.picRatio.setSetting_name(getResources().getString(R.string.pic_ratio));
        activityCamerasettingBinding.picRatio.isShowButton(false);
       /* video_size.setSetting_name(getResources().getString(R.string.video_size));
        video_size.isShowButton(false);*/
        activityCamerasettingBinding.videoDuration.setSetting_name(getResources().getString(R.string.video_duration));
        activityCamerasettingBinding.videoDuration.isShowButton(false);
        activityCamerasettingBinding.subline.setSetting_name(getResources().getString(R.string.subline));
        activityCamerasettingBinding.subline.isShowButton(true);
        activityCamerasettingBinding.watermark.setSetting_name(getResources().getString(R.string.watermark));
        activityCamerasettingBinding.watermark.isShowButton(false);
        activityCamerasettingBinding.watermark.setSetting_parm("BizCam");
        activityCamerasettingBinding.blackWhite.setSetting_name(getResources().getString(R.string.blackwhite));
        activityCamerasettingBinding.blackWhite.isShowButton(true);
        activityCamerasettingBinding.compress.setSetting_name(getResources().getString(R.string.compress));
        activityCamerasettingBinding.compress.isShowButton(true);
        activityCamerasettingBinding.cacheClear.setSetting_name(getResources().getString(R.string.clear_cache));
        activityCamerasettingBinding.cacheClear.isShowButton(false);
        activityCamerasettingBinding.onlyWifi.setSetting_name(getResources().getString(R.string.only_wifi));
        activityCamerasettingBinding.onlyWifi.isShowButton(true);
        activityCamerasettingBinding.coboxUpdate.setSetting_name(getResources().getString(R.string.cobox_update));
        activityCamerasettingBinding.coboxUpdate.isShowButton(false);
        setCurrentType(cameraStatus);
        loginedUser = LoginedUser.getLoginedUser();
        getFileUrl();
        getAppVersion();
        activityCamerasettingBinding.onlyWifi.setSwitchButton(!loginedUser.isonlywifi());
    }

    @Override
    protected void onViewClick() {
        activityCamerasettingBinding.cameraSettingTitle.setLeftListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        activityCamerasettingBinding.picSize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setList(Flag.PICSIZE);
                showCameraPop(list);
                cameraSettingPop.isShowSelect(false);
                currentSelect = Flag.PICSIZE;
            }
        });
        activityCamerasettingBinding.picRatio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setList(Flag.PICRATIO);
                showCameraPop(list);
                cameraSettingPop.isShowSelect(false);
                currentSelect = Flag.PICRATIO;
            }
        });
       /* video_size.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setList(Flag.VIDEOSIZE);
                showCameraPop(list);
                currentSelect = Flag.VIDEOSIZE;
            }
        });*/
        activityCamerasettingBinding.videoDuration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setList(Flag.VIDEODURATION);
                showCameraPop(list);
                if (cameraStatus.getCostomRecordTime() > 0) {
                    cameraSettingPop.setCostom();
                }
                if (cameraStatus.getRecordTime() == CameraStatus.Size.RECORD_CUSTOM) {
                    cameraSettingPop.onCoustomSelect();
                }
                cameraSettingPop.isShowSelect(true);
                currentSelect = Flag.VIDEODURATION;
            }
        });
        cameraSettingPop = new CameraSettingPop(this, null);
        cameraSettingPop.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                int type = cameraSettingPop.getSelectType();
                switch (currentSelect) {
                    case Flag.PICSIZE:
                        cameraStatus.setPictureSize(list.get(type).getSize());
                        break;
                    case Flag.PICRATIO:
                        cameraStatus.setPictureRatio(list.get(type).getSize());
                        break;
                    case Flag.VIDEOSIZE:
                        cameraStatus.setPictureSize(list.get(type).getSize());
                        break;
                    case Flag.VIDEODURATION:
                        int customTime = cameraSettingPop.getRecordTime_Custom();
                        cameraStatus.setCostomRecordTime(customTime);
                        if (type != -1) {
                            cameraStatus.setRecordTime(list.get(type).getSize());
                        } else {
                            cameraStatus.setRecordTime(CameraStatus.Size.RECORD_CUSTOM);
                        }
                        break;
                }
                setCurrentType(cameraStatus);
            }
        });

        activityCamerasettingBinding.subline.setCurrentCheckedListener(new CameraSettingItemView.CurrentChecked() {
            @Override
            public void onCurrentCheck(boolean currentcheck) {
                cameraStatus.setSubLineOn(currentcheck);
                CameraStatus.saveToFile(cameraStatus);
            }
        });

        activityCamerasettingBinding.blackWhite.setCurrentCheckedListener(new CameraSettingItemView.CurrentChecked() {
            @Override
            public void onCurrentCheck(boolean currentcheck) {
                cameraStatus.setBlackAndWhite(currentcheck);
                CameraStatus.saveToFile(cameraStatus);
            }
        });

        activityCamerasettingBinding.compress.setCurrentCheckedListener(new CameraSettingItemView.CurrentChecked() {
            @Override
            public void onCurrentCheck(boolean currentcheck) {
                cameraStatus.setPicCompress(currentcheck);
                CameraStatus.saveToFile(cameraStatus);
            }
        });

        activityCamerasettingBinding.watermark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WaterMarkPresenter.startAction(CameraSettingActivity.this,REQUESE,false);
                //startActivityForResult(new Intent(CameraSettingActivity.this, WaterMarkSettingActivity.class), REQUESE);
            }
        });
        activityCamerasettingBinding.cacheClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (mchoiceDialog == null) {
//                    mchoiceDialog = ChoiceDialog3.createInstance(CameraSettingActivity.this);
//                }
//                mchoiceDialog.show();
//                mchoiceDialog.setTitle(getResources().getString(R.string.alert));
//                String message =getResources().getString(R.string.clear_the_cache);
//                mchoiceDialog.setMessage(message);
//                mchoiceDialog.setChoiceInterface(new ChoiceDialog3.ChoiceInterface() {
//                    @Override
//                    public void choiceOk() {
//                        mchoiceDialog.dismiss();
//
//
//                    }
//
//                    @Override
//                    public void choiceCencel() {
//                        mchoiceDialog.dismiss();
//                    }
//                });
                EventStatisticsUtil.event(CameraSettingActivity.this, EventCommon.PERSONCENTER_SETTING_CLEAR_CACHE);
                cleanFileUrl();
                getFileUrl();

            }
        });
        activityCamerasettingBinding.onlyWifi.setCurrentCheckedListener(new CameraSettingItemView.CurrentChecked() {
            @Override
            public void onCurrentCheck(boolean currentcheck) {
                EventStatisticsUtil.event(CameraSettingActivity.this, EventCommon.PERSONCENTER_SETTING_ONLY_WIFI);
                loginedUser.setIsonlywifi(!currentcheck);
                LoginedUser.setLoginedUser(loginedUser);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null)
            return;

        if (requestCode == resultCode && resultCode == REQUESE) {
            cameraStatus = CameraStatus.getCameraStatus();
            if (cameraStatus.getWaterMark().isWaterMarkOn()) {
                activityCamerasettingBinding.watermark.setSetting_parm(cameraStatus.getWaterMark().getWaterMarkStatus().getName());
            } else {
                activityCamerasettingBinding.watermark.setSetting_parm(CameraStatus.Size.WATERMARK_OFF.getName());
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void showCameraPop(ArrayList arrayList) {
        cameraSettingPop.setList(arrayList);
        cameraSettingPop.showPop(this.getWindow().getDecorView());
    }

    private void setList(int type) {
        switch (type) {
            case Flag.PICSIZE:
                list = new ArrayList<>();
                list.add(new CameraSettingData(cameraStatus.getPictureSize() == CameraStatus.Size.LARGE, CameraStatus.Size.LARGE,-1,null));
                list.add(new CameraSettingData(cameraStatus.getPictureSize() == CameraStatus.Size.MIDDLE, CameraStatus.Size.MIDDLE,-1,null));
                list.add(new CameraSettingData(cameraStatus.getPictureSize() == CameraStatus.Size.SMALL, CameraStatus.Size.SMALL,-1,null));
                break;
           /* case Flag.VIDEOSIZE:
                list = new ArrayList<>();
                list.add(new CameraSettingData(cameraStatus.getPictureSize() == CameraStatus.Size.LARGE, CameraStatus.Size.LARGE));
                list.add(new CameraSettingData(cameraStatus.getPictureSize() == CameraStatus.Size.MIDDLE, CameraStatus.Size.MIDDLE));
                list.add(new CameraSettingData(cameraStatus.getPictureSize() == CameraStatus.Size.SMALL, CameraStatus.Size.SMALL));
                break;*/
            case Flag.VIDEODURATION:
                list = new ArrayList<>();
                list.add(new CameraSettingData(cameraStatus.getRecordTime() == CameraStatus.Size.RECORD_9, CameraStatus.Size.RECORD_9,-1,null));
                list.add(new CameraSettingData(cameraStatus.getRecordTime() == CameraStatus.Size.RECORD_30, CameraStatus.Size.RECORD_30,-1,null));
                list.add(new CameraSettingData(cameraStatus.getRecordTime() == CameraStatus.Size.RECORD_60, CameraStatus.Size.RECORD_60,-1,null));
                break;
            case Flag.PICRATIO:
                list = new ArrayList<>();
                list.add(new CameraSettingData(cameraStatus.getPictureRatio() == CameraStatus.Size.TYPE_11, CameraStatus.Size.TYPE_11,-1,null));
                list.add(new CameraSettingData(cameraStatus.getPictureRatio() == CameraStatus.Size.TYPE_34, CameraStatus.Size.TYPE_34,-1,null));
                list.add(new CameraSettingData(cameraStatus.getPictureRatio() == CameraStatus.Size.TYPE_916, CameraStatus.Size.TYPE_916,-1,null));
                break;
        }
    }

    private void setCurrentType(CameraStatus cameraStatus) {
        activityCamerasettingBinding.subline.setSwitchButton(cameraStatus.isSubLineOn());
        activityCamerasettingBinding.blackWhite.setSwitchButton(cameraStatus.isBlackAndWhite());
        activityCamerasettingBinding.compress.setSwitchButton(cameraStatus.isPicCompress());
        activityCamerasettingBinding.picSize.setSetting_parm(cameraStatus.getPictureSize().getName());
        activityCamerasettingBinding.picRatio.setSetting_parm(cameraStatus.getPictureRatio().getName());

        if (cameraStatus.getRecordTime() != CameraStatus.Size.RECORD_CUSTOM) {
            activityCamerasettingBinding.videoDuration.setSetting_parm(cameraStatus.getRecordTime().getName());
        } else {
            activityCamerasettingBinding.videoDuration.setSetting_parm(cameraStatus.getCostomRecordTime() + "s");
        }
        if (cameraStatus.getWaterMark().isWaterMarkOn()) {
            activityCamerasettingBinding.watermark.setSetting_parm(cameraStatus.getWaterMark().getWaterMarkStatus().getName());
        } else {
            activityCamerasettingBinding.watermark.setSetting_parm(CameraStatus.Size.WATERMARK_OFF.getName());
        }
        CameraStatus.saveToFile(cameraStatus);
        // video_size.setSetting_parm(cameraStatus.getPictureSize().getName());
    }

    private void getFileUrl() {
        File dirFiles = new File(CACHEURL);
        long cachesize = FileUtil.getFolderSize(dirFiles);
        String size = FileUtil.convertFileSize(cachesize);
        if (size != null) {
            activityCamerasettingBinding.cacheClear.setSetting_parm(size);
        } else {
            activityCamerasettingBinding.cacheClear.setSetting_parm("0 B");
        }
    }

    private void cleanFileUrl() {
        File dirFiles = new File(CACHEURL);
        CameraFileUtil.DeleteFile(dirFiles);
        ToastUtil.toast(getResources().getString(R.string.clear_cache_ok));
    }

    private void getAppVersion() {
        try {

            PackageManager manager = this.getPackageManager();
            info = manager.getPackageInfo(this.getPackageName(), 0);
            String version = info.versionName + "";
            activityCamerasettingBinding.tvVersion.setText("v " + version);
        } catch (Exception e) {
            e.printStackTrace();
            activityCamerasettingBinding.tvVersion.setText("v 1.0.0");
        }
    }


}
