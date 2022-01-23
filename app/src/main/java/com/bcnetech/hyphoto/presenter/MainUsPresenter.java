package com.bcnetech.hyphoto.presenter;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.PopupWindow;

import com.bcnetech.bcnetchhttp.RetrofitFactory;
import com.bcnetech.bcnetchhttp.RetrofitUploadFactory;
import com.bcnetech.bcnetchhttp.base.BaseResult;
import com.bcnetech.bcnetchhttp.base.PresetInfoObserver;
import com.bcnetech.bcnetchhttp.bean.request.PresetLoadBody;
import com.bcnetech.bcnetchhttp.bean.response.LoginedUser;
import com.bcnetech.bcnetchhttp.bean.response.Preinstail;
import com.bcnetech.bcnetchhttp.bean.response.PresetDownManageData;
import com.bcnetech.bcnetchhttp.config.Flag;
import com.bcnetech.bcnetechlibrary.util.StringUtil;
import com.bcnetech.hyphoto.data.SqlControl.PresetParmsSqlControl;
import com.bcnetech.hyphoto.model.MarketModel;
import com.bcnetech.hyphoto.presenter.iview.IMainUsView;
import com.bcnetech.hyphoto.receiver.PresetReceiver;
import com.bcnetech.hyphoto.receiver.SynchronizePresetReceiver;
import com.bcnetech.hyphoto.sql.dao.LightRatioData;
import com.bcnetech.hyphoto.sql.dao.PictureProcessingData;
import com.bcnetech.hyphoto.sql.dao.PresetParm;
import com.bcnetech.hyphoto.task.MBaseObserver;
import com.bcnetech.hyphoto.ui.activity.MainUsActivity;
import com.bcnetech.hyphoto.ui.popwindow.MainLoginUsPop;
import com.bcnetech.hyphoto.ui.view.MLoginNewUsView;
import com.bcnetech.hyphoto.utils.BitmapUtils;
import com.bcnetech.bluetoothlibarary.bluetoothagreement.CommendManage;
import com.umeng.analytics.MobclickAgent;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by wenbin on 2017/4/14.
 */

public class MainUsPresenter extends BasePresenter<IMainUsView> {

    private MainLoginUsPop mainLoginPop;
    private MainLoginUsPop mainRegisterPop;
    private SynchronizePresetReceiver synchronizePresetReceiver;
    private int presetParmCount = 3;
    private PresetParmsSqlControl presetParmsSqlControl;
    private PresetDownManageData shareParamLogVOLists;
    private int type;
    private int currentkeyboardHeight = 0;
    private int keyboardAnimHeight = 0;
    private boolean statusHeightChange = false;
    private Queue<PresetDownManageData.ParamList> mRunOnDraw;

    private List<PresetDownManageData.ParamList> paramLists;
    public static void startAction(Activity activity) {
        Intent intent = new Intent(activity, MainUsActivity.class);
        activity.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle bundle) {

    }

    @Override
    public void onPause() {
        super.onPause();
        if (null != mainRegisterPop) {
            if (mainRegisterPop.isShowing()) {
                mainRegisterPop.closeKeyBoard();
            }
        }
        if (null != mainLoginPop) {
            if (mainLoginPop.isShowing()) {
                mainLoginPop.closeKeyBoard();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (null != mainRegisterPop) {
            if (mainRegisterPop.isShowing()) {
                mainRegisterPop.openKeyBoard();
            }
        }
        if (null != mainLoginPop) {
            if (mainLoginPop.isShowing()) {
                mainLoginPop.openKeyBoard();
            }
        }
    }

    @Override
    public void onDestroy() {
        if (type == Flag.TYPE_LOGIN) {
            if (null != mainLoginPop) {
                mainLoginPop.dismiss();
            }

        } else {
            if (null != mainRegisterPop) {
                mainRegisterPop.dismiss();
            }

        }
        if (null != synchronizePresetReceiver) {
            synchronizePresetReceiver.unregister(activity);
        }
    }

    public void initData() {
        shareParamLogVOLists = new PresetDownManageData();
        synchronizePresetReceiver = new SynchronizePresetReceiver() {
            @Override
            public void onGetData() {
                presetParmCount = 3;
                presetParmsSqlControl.deleteAll();
                addPreset();
                addPresetTwo();
                addPresetThree();
                synchronizePreset();
            }
        };
        synchronizePresetReceiver.register(activity);

        mRunOnDraw = new LinkedList<>();
        paramLists=new ArrayList<>();

        presetParmsSqlControl = new PresetParmsSqlControl(activity);
    }

    public void showPop(int type) {
        this.type = type;
        if (type == Flag.TYPE_LOGIN) {
            if (null == mainLoginPop) {

                mainLoginPop = new MainLoginUsPop(activity);
                mainLoginPop.setType(type);
                //setInputHeight(mainLoginPop);
                mainLoginPop.showAtLocation(activity.getWindow().getDecorView(), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                mainLoginPop.setMLoginInter(new MLoginNewUsView.MLoginInter() {
                    @Override
                    public void finishView() {
                        mainLoginPop.dismiss();
                        mainLoginPop = null;
                    }

                    @Override
                    public void onLogin() {
//                        mainLoginPop.dismiss();
//                        LoginedUser loginedUser = LoginedUser.getLoginedUser();
//                        if ((null!=loginedUser.getType()&&loginedUser.getType().equals("4"))) {
//                            Intent intent=new Intent(activity, AlbumActivity.class);
//                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                            activity.startActivity(intent);
//                        }
                        MobclickAgent.onProfileSignIn(LoginedUser.getLoginedUser().getUser_name());
                        SynchronizePresetReceiver.notifyModifyPreset(activity);
//                        activity.finish();
                    }
                });
            } else {
                mainLoginPop.setType(type);
                //setInputHeight(mainLoginPop);
                mainLoginPop.showAtLocation(activity.getWindow().getDecorView(), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                mainLoginPop.openNumKeyBoard();
            }
        } else {
            if (null == mainRegisterPop) {
                mainRegisterPop = new MainLoginUsPop(activity);
                mainRegisterPop.setType(type);
                //setInputHeight(mainRegisterPop);
                mainRegisterPop.showAtLocation(activity.getWindow().getDecorView(), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                mainRegisterPop.setMLoginInter(new MLoginNewUsView.MLoginInter() {
                    @Override
                    public void finishView() {
                        mainRegisterPop.dismiss();
                        mainRegisterPop = null;
                    }

                    @Override
                    public void onLogin() {
                        MobclickAgent.onProfileSignIn(LoginedUser.getLoginedUser().getUser_name());
                        SynchronizePresetReceiver.notifyModifyPreset(activity);
                    }
                });
            } else {
                mainRegisterPop.setType(type);
                //setInputHeight(mainRegisterPop);
                mainRegisterPop.showAtLocation(activity.getWindow().getDecorView(), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                mainRegisterPop.openNumKeyBoard();
            }
        }


    }
    int page=1;

    public void synchronizePreset() {

        RetrofitFactory.getInstence().API().userParamPage(new PresetLoadBody(page + ""))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MBaseObserver<PresetDownManageData>(activity,false) {
                    @Override
                    protected void onSuccees(BaseResult<PresetDownManageData> t) throws Exception {
                        shareParamLogVOLists = t.getData();

                        paramLists.addAll(shareParamLogVOLists.getParamList());
                        if (shareParamLogVOLists.getPage() * shareParamLogVOLists.getPagesize() < shareParamLogVOLists.getTotal()) {
                            page++;
                            synchronizePreset();
                        } else {
                            for (int i = paramLists.size() - 1; i >= 0; i--) {
                                if (!StringUtil.isBlank(paramLists.get(i).getAutherUrl())) {
                                    String url = BitmapUtils.getBitmapUrl(paramLists.get(i).getAutherUrl());
                                    paramLists.get(i).setAutherUrl(url);
                                } else {
                                    paramLists.get(i).setAutherUrl("");
                                }
                                mRunOnDraw.add(paramLists.get(i));
                            }
                            getMarketPresetPams(mRunOnDraw);
                            paramLists.clear();
                            paramLists=null;
                        }
                    }

                    @Override
                    protected void onCodeError(BaseResult<PresetDownManageData> t) throws Exception {

                    }

                    @Override
                    protected void onFailure(Throwable e, boolean isNetWorkError) throws Exception {

                    }
                });

    }

    private synchronized void getMarketPresetPams(final Queue<PresetDownManageData.ParamList> mRunOnDraw) {
        if (mRunOnDraw != null && !mRunOnDraw.isEmpty()) {
            final PresetDownManageData.ParamList param = mRunOnDraw.poll();

            RetrofitUploadFactory.getUPloadInstence().API().getLightData(param.getFileId())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new PresetInfoObserver<Preinstail>(activity,false) {
                        @Override
                        protected void onSuccees(Preinstail t) throws Exception {
                            MarketModel marketModel = new MarketModel();
                            Preinstail preinstail=t;
                            preinstail.setFileId(param.getFileId());

                            preinstail.setCoverId(param.getCoverId());
                            PresetParm presetParm = marketModel.transferImageData(preinstail);

                            presetParm.setShowType("0");
                            presetParm.setPosition(presetParmCount);
                            presetParm.setPresetId(param.getFileId());
                            presetParm.setImageHeight(param.getImageHeight());
                            presetParm.setImageWidth(param.getImageWidth());
                            presetParm.setCategoryId(param.getCategoryId());
                            presetParm.setAutherUrl(param.getAutherUrl());
                            presetParm.setCoverId(param.getCoverId());
                            presetParm.setSize(preinstail.getDataSize());

//                    presetParms.add(presetParm);
                            presetParmCount++;
                            presetParmsSqlControl.startInsert(presetParm);

                            PresetReceiver.notifyModifyPreset(activity, presetParmCount);
                            getMarketPresetPams(mRunOnDraw);
                        }

                        @Override
                        protected void onCodeError(Preinstail t) throws Exception {
                            getMarketPresetPams(mRunOnDraw);
                        }

                        @Override
                        protected void onFailure(Throwable e, boolean isNetWorkError) throws Exception {
                            getMarketPresetPams(mRunOnDraw);
                        }
                    });
        } else {
            activity.finish();
        }

    }

    private void addPreset() {
        PresetParm presetParm = new PresetParm();
        presetParm.setAutherUrl("");
        presetParm.setAuther("BCEASY");
        presetParm.setShowType("0");
        presetParm.setCategoryId("1168231105125");
        presetParm.setDescribe("");
        presetParm.setImageHeight("1080");
        presetParm.setImageWidth("1080");
        presetParm.setEquipment(Build.MODEL + "");

        LightRatioData lightRatioData = new LightRatioData();

        lightRatioData.setBackgroudLight(0);
        lightRatioData.setBottomLight(51);
        lightRatioData.setLeftLight(83);
        lightRatioData.setRightLight(82);
        lightRatioData.setTopLight(0);
        lightRatioData.setNum(0);
        lightRatioData.setVersion("");

        presetParm.setLightRatioData(lightRatioData);
        presetParm.setSystem("android");
        presetParm.setTimeStamp(new Date().getTime());
        presetParm.setTextSrc("http://www.bceasy.com/V1/1.0/file/1168231109340.jpg?code=0");
        presetParm.setName("商拍光影");
        presetParm.setPosition(0);
        presetParm.setPresetId("default");
        presetParm.setCoverId("default");
        presetParm.setSize("0.1 MB");

        PictureProcessingData pictureProcessingData1 = new PictureProcessingData();
        pictureProcessingData1.setType(2002);
        pictureProcessingData1.setNum(0);


        PictureProcessingData pictureProcessingData2 = new PictureProcessingData();
        pictureProcessingData2.setType(2001);
        pictureProcessingData2.setNum(0);

        List<PictureProcessingData> pictureProcessingDatas = new ArrayList<PictureProcessingData>();
        pictureProcessingDatas.add(pictureProcessingData1);
        pictureProcessingDatas.add(pictureProcessingData2);

        presetParm.setPartParmlists(pictureProcessingDatas);

        List<String> labels = new ArrayList<String>();
        labels.add("饮料");
        presetParm.setLabels(labels);
        presetParmsSqlControl.startInsert(presetParm);
    }


    private void addPresetTwo() {
        PresetParm presetParm = new PresetParm();
        presetParm.setAutherUrl("");
        presetParm.setAuther("BCEASY");
        presetParm.setShowType("0");
        presetParm.setCategoryId("1168231105125");
        presetParm.setDescribe("");
        presetParm.setImageHeight("1080");
        presetParm.setImageWidth("1080");
        presetParm.setEquipment(Build.MODEL + "");

        LightRatioData lightRatioData = new LightRatioData();

        lightRatioData.setLeftLight(0);
        lightRatioData.setRightLight(0);
        lightRatioData.setTopLight(100);
        lightRatioData.setBottomLight(70);
        lightRatioData.setMoveLight(100);
        lightRatioData.setBackgroudLight(60);
        lightRatioData.setVersion(CommendManage.VERSION2_1+"");

        presetParm.setLightRatioData(lightRatioData);
        presetParm.setSystem("android");
        presetParm.setTimeStamp(new Date().getTime());
        presetParm.setTextSrc("https://cnfile.bcyun.com/file/1168231109340.jpg?code=0");
        presetParm.setName("Food&Drink");
        presetParm.setPosition(1);
        presetParm.setSize("0.1 MB");
        presetParm.setPresetId("default2");
        presetParm.setCoverId("default2");

        PictureProcessingData pictureProcessingData1 = new PictureProcessingData();
        pictureProcessingData1.setType(2002);
        pictureProcessingData1.setNum(0);


        PictureProcessingData pictureProcessingData2 = new PictureProcessingData();
        pictureProcessingData2.setType(2001);
        pictureProcessingData2.setNum(0);

        List<PictureProcessingData> pictureProcessingDatas = new ArrayList<PictureProcessingData>();
        pictureProcessingDatas.add(pictureProcessingData1);
        pictureProcessingDatas.add(pictureProcessingData2);

        presetParm.setPartParmlists(pictureProcessingDatas);

        List<String> labels = new ArrayList<String>();
        labels.add("Food&Drink");
        presetParm.setLabels(labels);
        presetParmsSqlControl.startInsert(presetParm);
    }

    private void addPresetThree() {
        PresetParm presetParm = new PresetParm();
        presetParm.setAutherUrl("");
        presetParm.setAuther("BCEASY");
        presetParm.setShowType("0");
        presetParm.setCategoryId("1168231105125");
        presetParm.setDescribe("");
        presetParm.setImageHeight("1080");
        presetParm.setImageWidth("1080");
        presetParm.setEquipment(Build.MODEL + "");

        LightRatioData lightRatioData = new LightRatioData();

        lightRatioData.setLeftLight(25);
        lightRatioData.setRightLight(25);
        lightRatioData.setTopLight(30);
        lightRatioData.setBottomLight(25);
        lightRatioData.setMoveLight(100);
        lightRatioData.setBackgroudLight(20);
        lightRatioData.setVersion(CommendManage.VERSION2_1+"");

        presetParm.setLightRatioData(lightRatioData);
        presetParm.setSystem("android");
        presetParm.setTimeStamp(new Date().getTime());
        presetParm.setTextSrc("https://cnfile.bcyun.com/file/1168231109340.jpg?code=0");
        presetParm.setName("Flavoring");
        presetParm.setPosition(2);
        presetParm.setSize("0.1 MB");
        presetParm.setPresetId("default3");
        presetParm.setCoverId("default3");

        PictureProcessingData pictureProcessingData1 = new PictureProcessingData();
        pictureProcessingData1.setType(2002);
        pictureProcessingData1.setNum(0);

        PictureProcessingData pictureProcessingData2 = new PictureProcessingData();
        pictureProcessingData2.setType(2001);
        pictureProcessingData2.setNum(0);

        List<PictureProcessingData> pictureProcessingDatas = new ArrayList<PictureProcessingData>();
        pictureProcessingDatas.add(pictureProcessingData1);
        pictureProcessingDatas.add(pictureProcessingData2);

        presetParm.setPartParmlists(pictureProcessingDatas);

        List<String> labels = new ArrayList<String>();
        labels.add("Flavoring");
        presetParm.setLabels(labels);
        presetParmsSqlControl.startInsert(presetParm);
    }



    int statusBarHeight = 0;

    public void setInputHeight(final PopupWindow popupWindow) {
        final View myLayout = activity.getWindow().getDecorView();
        popupWindow.getContentView().getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect(); // r will be populated with the coordinates of your view that area still visible.
                popupWindow.getContentView().getWindowVisibleDisplayFrame(r);
                int screenHeight = myLayout.getRootView().getHeight();
                int heightDiff = screenHeight - (r.bottom - r.top);
                if (heightDiff > 100) // if more than 100 pixels, its probably a keyboard // get status bar height
                    statusBarHeight = 0;
                try {
                    Class<?> c = Class.forName("com.android.internal.R$dimen");
                    Object obj = c.newInstance();
                    Field field = c.getField("status_bar_height");
                    int x = Integer.parseInt(field.get(obj).toString());
                    statusBarHeight = activity.getResources().getDimensionPixelSize(x);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                int realKeyboardHeight = (heightDiff - statusBarHeight) > 300 ? heightDiff - statusBarHeight : 0;
                if (currentkeyboardHeight != 0)
                    keyboardAnimHeight = currentkeyboardHeight - realKeyboardHeight;
                if (currentkeyboardHeight != realKeyboardHeight)
                    currentkeyboardHeight = realKeyboardHeight;
                Log.d("keyboard height = ", realKeyboardHeight + " " + keyboardAnimHeight);
                if (popupWindow.isShowing() && keyboardAnimHeight != 0 && Math.abs(keyboardAnimHeight) < 300) {
                    showKeyboardAnim(popupWindow);
                }
            }
        });
    }

    private void showKeyboardAnim(PopupWindow popupWindow) {
        if (keyboardAnimHeight > 0) {
            keyboardAnimHeight -= statusBarHeight;
        } else {
            keyboardAnimHeight += statusBarHeight;
        }
        ObjectAnimator translationy = ObjectAnimator.ofFloat(popupWindow.getContentView(), "translationY", 0, keyboardAnimHeight);
        translationy.start();
        keyboardAnimHeight = 0;
    }


}
