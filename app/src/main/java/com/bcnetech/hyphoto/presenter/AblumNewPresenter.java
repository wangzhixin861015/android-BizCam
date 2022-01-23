package com.bcnetech.hyphoto.presenter;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.AppOpsManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Camera;
import android.location.LocationManager;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bcnetech.bcnetchhttp.RetrofitFactory;
import com.bcnetech.bcnetchhttp.base.BaseObserver;
import com.bcnetech.bcnetchhttp.base.BaseResult;
import com.bcnetech.bcnetchhttp.bean.request.ModifyScope;
import com.bcnetech.bcnetchhttp.bean.response.CloudPicData;
import com.bcnetech.bcnetchhttp.bean.response.LoginedUser;
import com.bcnetech.bcnetchhttp.config.Flag;
import com.bcnetech.bcnetchhttp.util.RetrofitDownloadManager;
import com.bcnetech.bcnetechlibrary.dialog.ChoiceDialog;
import com.bcnetech.bcnetechlibrary.dialog.ProgressDialog;
import com.bcnetech.bcnetechlibrary.util.AnimFactory;
import com.bcnetech.bcnetechlibrary.util.EventStatisticsUtil;
import com.bcnetech.bcnetechlibrary.util.LogUtil;
import com.bcnetech.bcnetechlibrary.util.StringUtil;
import com.bcnetech.bcnetechlibrary.util.TimeUtil;
import com.bcnetech.bcnetechlibrary.util.ToastUtil;
import com.bcnetech.bcnetechlibrary.util.sharepreference.SharePreferences;
import com.bcnetech.hyphoto.EventCommon;
import com.bcnetech.hyphoto.bizInterface.ItemClickInterface;
import com.bcnetech.hyphoto.data.CloudPicture;
import com.bcnetech.hyphoto.data.GridItem;
import com.bcnetech.hyphoto.data.ShareData;
import com.bcnetech.hyphoto.data.SqlControl.BaseSqlControl;
import com.bcnetech.hyphoto.data.SqlControl.ImageDataSqlControl;
import com.bcnetech.hyphoto.data.UploadBean;
import com.bcnetech.hyphoto.data.UploadCloudData;
import com.bcnetech.hyphoto.model.AblumModel;
import com.bcnetech.hyphoto.model.BleConnectModel;
import com.bcnetech.hyphoto.model.imodel.IAblumModel;
import com.bcnetech.hyphoto.presenter.iview.IAblumNewView;
import com.bcnetech.hyphoto.receiver.AddCloudReceiver;
import com.bcnetech.hyphoto.receiver.AddPicReceiver;
import com.bcnetech.hyphoto.receiver.DownloadPicReceiver;
import com.bcnetech.hyphoto.receiver.HttpChangReceiver;
import com.bcnetech.hyphoto.receiver.UploadAIReceiver;
import com.bcnetech.hyphoto.sql.dao.ImageData;
import com.bcnetech.hyphoto.sql.dao.LightRatioData;
import com.bcnetech.hyphoto.task.MBaseObserver;
import com.bcnetech.hyphoto.task.manage.DownloadListHolder;
import com.bcnetech.hyphoto.task.manage.UploadManager;
import com.bcnetech.hyphoto.ui.activity.AlbumNewActivity;
import com.bcnetech.hyphoto.ui.adapter.PicBrowsingAdapter;
import com.bcnetech.hyphoto.ui.adapter.StickyGridAdapter;
import com.bcnetech.hyphoto.ui.adapter.StickyGridNewAdapter;
import com.bcnetech.hyphoto.ui.adapter.SurfBlueToothPopAdapter;
import com.bcnetech.hyphoto.ui.dialog.RegisterUsDialog;
import com.bcnetech.hyphoto.ui.view.BottomToolView;
import com.bcnetech.hyphoto.ui.view.CoBoxChooseTopView;
import com.bcnetech.hyphoto.ui.view.TitleView;
import com.bcnetech.hyphoto.utils.BitmapUtils;
import com.bcnetech.hyphoto.utils.FileUtil;
import com.bcnetech.hyphoto.utils.Image.FileUpload;
import com.bcnetech.hyphoto.utils.PermissionUtil;
import com.bcnetech.hyphoto.utils.ShareImagesUtil;
import com.bcnetech.bluetoothlibarary.bluetoothbroadcast.BlueToothStatusReceiver;
import com.bcnetech.bluetoothlibarary.data.CommendItem;
import com.bcnetech.hyphoto.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

//import com.bcnetech.bcnetechlibrary.util.okHttp.OkHttpClientManager;
//import com.squareup.okhttp.Request;


/**
 * Created by yhf on 2017/10/16.
 */
public class AblumNewPresenter extends BasePresenter<IAblumNewView> implements IAblumModel {
    public final static int CAMERAACTIVITY = 7;
    private final static int START_TYPE = 0;
    private final static int PHOTO_EDIT_TYPE = 1;
    private final static int ABLUM_TYPE = 2;
    private static final String KEY_LIST_POSITION = "key_list_position";
    private static final String SERIALIZABLELIST = "serialable_list";

    private AblumModel model;
    private StickyGridNewAdapter adapter;
    private SurfBlueToothPopAdapter surfBlueToothPopAdapter;
    private int currentlayoutType = TitleView.FOUR_TYPE;//当前显示类型  一行4个还是 2个
    private int mFirstVisible;//当前gridview位置
    public List<GridItem> mGirdList;
    private int itemClickCount = 0;//当前选中item数量
    private boolean canClick = false;//判断是否能点击
    private ImageDataSqlControl imageDataSqlControl;
    //    private PicBrowsingAdapter picBrowsingAdapter;
    private int isFooterShow = START_TYPE;
    private ChoiceDialog mchoiceDialog;
    private AddPicReceiver addPicReceiver;
    private boolean before1 = false;

    private boolean canClearPic = true;

    private AddCloudReceiver addCloudReceiver;
    private HttpChangReceiver httpChangReceiver;
    private UploadAIReceiver uploadAIReceiver;
    private DownloadPicReceiver downloadPicReceiver;
    private BlueToothStatusReceiver blueToothStatusReceiver;

    private UploadManager uploadManager;
    private ValueAnimator valueAnimator;
    private Queue<CloudPicData> mRunOnDraw;

    private ProgressDialog dgProgressDialog2;
    private ProgressDialog uploadDialog;
    private RegisterUsDialog registerUsDialog;
    private String addPicType;
    //图片
    public final int PHOTO = 1;

    //视频
    public final int CAMERA = 2;

    //多个视频
    public final int CAMERAS = 3;

    //视频和图片
    public final int PHOTO_CAMERA = 4;

    //视频和图片
    public final int PHOTOS = 5;

    //单张
    public final int PHOTO_ONE = 0;

    private int shareType;
    private PopupWindow sharePopupWindow;
    private PopupWindow morePopupWindow;
    private BleConnectModel bleConnectModel;


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(SERIALIZABLELIST, (Serializable) mGirdList);
        outState.putSerializable(KEY_LIST_POSITION, mFirstVisible);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            mGirdList = (List<GridItem>) savedInstanceState.getSerializable(SERIALIZABLELIST);
        }
        if (savedInstanceState != null) {
            mFirstVisible = savedInstanceState.getInt(KEY_LIST_POSITION);
        }
    }

    @Override
    public void onCreate(Bundle bundle) {
        UploadManager.onDestroy();
        initData();
        model = new AblumModel();
        model.attach(this, activity);
        uploadManager = UploadManager.getInstance(activity);
        imageDataSqlControl.startQuery();
        EventBus.getDefault().register(this);
        File file = new File(Flag.APP_CAMERAL);
        if (!file.exists())
            file.mkdirs();
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onRestart() {
        super.onRestart();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mGirdList == null) {
            mGirdList = new ArrayList<>();
        }
        if (bleConnectModel != null) {
            bleConnectModel.stopSearch();
        }
        canClearPic = true;
    }


    @Override
    public void onDestroy() {
        model.dettach();
        if (addPicReceiver != null) {
            addPicReceiver.unregister(activity);
        }
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        if (bleConnectModel != null) {
            bleConnectModel.stopSearch();
        }
        if (httpChangReceiver != null) {
            httpChangReceiver.unregister(activity);
        }

        if (uploadAIReceiver != null) {
            uploadAIReceiver.unregister(activity);
        }

        if (addCloudReceiver != null) {
            addCloudReceiver.unregister(activity);
        }

        if (null != downloadPicReceiver) {
            downloadPicReceiver.unregister(activity);
        }

        if (null != blueToothStatusReceiver) {
            activity.unregisterReceiver(blueToothStatusReceiver);
        }


    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void uploadRefresh(final UploadBean bean) {

        mView.refreshGridView(bean);
    }


    public void  initData() {
        imageDataSqlControl = new ImageDataSqlControl(activity);
        imageDataSqlControl.setListener(new BaseSqlControl.SqlControlListener() {
                                            @Override
                                            public void queryListener(Object... parms) {
                                                mGirdList = model.getGridData(mGirdList, parms);
                                                if ((int) parms[1] == 0) {
                                                    if (mGirdList != null) {

                                                        mView.showBackground(mGirdList);
                                                        itemClickCount = 0;
                                                        adapter.setData(mGirdList);
                                                        adapter.notifyDataSetChanged();
//                                                    picBrowsingAdapter.setData(mGirdList);
//                                                    picBrowsingAdapter.notifyDataSetChanged();
                                                        mView.updataImageUtils(mGirdList);
                                                    }

                                                    if (!StringUtil.isBlank(addPicType) && addPicType.equals("add")) {
                                                        mView.refreshBigImage(mGirdList.get(0), 0, "add");
                                                    } else if (!StringUtil.isBlank(addPicType) && addPicType.equals("refresh")) {
                                                        mView.refreshBigImage(mGirdList.get(mView.getPosition()), mView.getPosition(), "refresh");
                                                    }

                                                    addPicType = "";
                                                } else if ((int) parms[1] == 5) {
                                                    uploadData();
                                                }
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
                                        }

        );

        httpChangReceiver = new HttpChangReceiver() {

            @Override
            public void httpWifiConnection() {

                uploadManager.httpWifiConnection();
            }

            @Override
            public void httpModNetConnection() {
                uploadManager.httpModNetConnection();
            }

            @Override
            public void httpDisConnection() {
                uploadManager.httpDisConnection();
            }
        };

        blueToothStatusReceiver = new BlueToothStatusReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int blueState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                switch (intent.getAction()) {
                    case BluetoothAdapter.ACTION_STATE_CHANGED:
                        LogUtil.d("BlueToothStatus" + blueState);
                        switch (blueState) {
                            case BluetoothAdapter.STATE_TURNING_ON:
                            case BluetoothAdapter.STATE_ON:
                                LogUtil.d("BlueToothStatus" + "STATE_ON");
                                break;
                            case BluetoothAdapter.STATE_TURNING_OFF:
                            case BluetoothAdapter.STATE_OFF:
                                LogUtil.d("BlueToothStatus :" + "STATE_OFF");
                                onBlueToothOff();
                                break;
                        }
                        break;
                    case BluetoothDevice.ACTION_ACL_CONNECTED:
                        break;
                    case BluetoothDevice.ACTION_ACL_DISCONNECTED:
                        if (bleConnectModel != null && bleConnectModel.isCurrentConnect())
                            bleConnectModel.disConnectCurrent();
                        break;
                }

            }
        };

        activity.registerReceiver(blueToothStatusReceiver
                , BleConnectModel.makeFilter());

        httpChangReceiver.register(activity);
        addCloudReceiver = new AddCloudReceiver() {
            @Override
            public void addCloud(List list) {
                final List<UploadCloudData> uploadCloudDataList = list;

                if (uploadCloudDataList != null) {
                    for (int i = 0; i < uploadCloudDataList.size(); i++) {
                        uploadManager.getDownloadInfoSqlControl().insertDownLoadInfo(uploadCloudDataList.get(i).getDownloadInfoData());
//                        uploadManager.runOnDraw(uploadCloudDataList.get(i).getDownloadInfoData());
                    }
                }

                if (uploadManager.getHttpType() == uploadManager.NET) {
                    return;
                } else {
                    //上传
//                    uploadManager.runTask(uploadManager.getmRunOnDraw());
                    uploadManager.getDownloadInfoSqlControl().queryFirstUpload();
                }


            }

            @Override
            public void startUpload() {

            }
        };

        addCloudReceiver.register(activity);

        uploadAIReceiver = new UploadAIReceiver() {
            @Override
            public void onUploadData(String url, String fileId, String fileHash) {
                for (GridItem gridItem : mGirdList) {
                    if (null != gridItem.getImageData() && gridItem.getImageData().getLocalUrl().equals(url)) {
                        gridItem.getImageData().setValue3(fileId);
                        Modify360Scope(fileId);
                        gridItem.getImageData().setValue4(fileHash);
                    }
                }
            }
        };

        uploadAIReceiver.register(activity);

        addPicReceiver = new AddPicReceiver() {
            @Override
            public void onGetData(String type) {

                addPicType = type;
                imageDataSqlControl.startQuery();
                // getPicLocal(Flag.APP_CAMERAL_TYPE);
            }
        };
        addPicReceiver.register(activity);

        downloadPicReceiver = new DownloadPicReceiver() {
            @Override
            public void onGetData(List<CloudPicData> cloudPicDatas) {
                if (mRunOnDraw == null) {
                    mRunOnDraw = new LinkedList<>();
                    for (int i = 0; i < cloudPicDatas.size(); i++) {
                        mRunOnDraw.add(cloudPicDatas.get(i));
                    }
                } else {
                    for (int i = 0; i < cloudPicDatas.size(); i++) {
                        mRunOnDraw.add(cloudPicDatas.get(i));
                    }
                }
                downLoad(mRunOnDraw);

                DownloadListHolder.getInstance().saveCloudPicData(null);
            }
        };

        downloadPicReceiver.register(activity);

//        synchronizePresetReceiver.register(activity);\\

//        uploadPicReceiver = new UploadPicReceiver() {
//            @Override
//            public void addPic(UploadPicData uploadPicData) {
//                Log.d("uploadPic:", uploadPicData.toString());
//            }
//
//        };
//        uploadPicReceiver.register(activity);
    }

    /**
     * 蓝牙关闭时调用方法
     */
    private void onBlueToothOff() {
        if (bleConnectModel != null)
            bleConnectModel.onBlueToothOff();
        mView.setBlueTouchType(CoBoxChooseTopView.BLUE_TOUCH_CLOSE);
        ToastUtil.toast(activity.getResources().getString(R.string.open_bt));

    }

    /**
     * 更改文件属性为公有
     *
     * @param fileId
     */
    private void Modify360Scope(String fileId) {
        ModifyScope modifyScope = new ModifyScope();
        String[] fileIds = {fileId};
        modifyScope.setIds(fileIds);
        modifyScope.setScope("2");
        RetrofitFactory.getInstence().API().bizCamUToUpload(modifyScope)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<Object>(activity, false) {
                    @Override
                    protected void onSuccees(BaseResult<Object> t) throws Exception {

                    }

                    @Override
                    protected void onCodeError(BaseResult<Object> t) throws Exception {

                    }

                    @Override
                    protected void onTokenError(BaseResult<Object> t) throws Exception {

                    }

                    @Override
                    protected void onFailure(Throwable e, boolean isNetWorkError) throws Exception {

                    }
                });
    }

    public BleConnectModel getBleConnectModel() {
        if (this.bleConnectModel == null) {
            bleConnectModel = BleConnectModel.getBleConnectModelInstance();
        }
        return bleConnectModel;
    }

    public void initBleConnect(final boolean isAI360) {
        bleConnectModel = BleConnectModel.getBleConnectModelInstance(new BleConnectModel.BleConnectStatus() {
            @Override
            public void onBleConnect(final boolean isConnected, final String deviceName) {
                if(null!=mView){
                    mView.onConnectBlueTooth(isConnected, deviceName, isAI360);
                }else {
                    ToastUtil.toast(activity.getResources().getString(R.string.out_of_memory));
                }
            }

            @Override
            public void onBleConnectError() {
                if(null!=bleConnectModel){
                    bleConnectModel.disConnectCurrent();
                }
            }

            @Override
            public void onGetDeviceInfo(int useTime) {

            }

            @Override
            public void onBleReceive(List<CommendItem> list) {

            }

            @Override
            public void onCOBOXVersion(int COBOXVer) {

            }

            @Override
            public void onMotorStatus(int isMotorOnline) {

            }

            @Override
            public void onNotifyList() {
                if (surfBlueToothPopAdapter == null)
                    surfBlueToothPopAdapter = new SurfBlueToothPopAdapter(activity);
                surfBlueToothPopAdapter.setData(BleConnectModel.getBleConnectModelInstance().getDeviceList());
                surfBlueToothPopAdapter.notifyDataSetChanged();
            }

            @Override
            public void onToast(int status) {

            }

            @Override
            public void onScanError() {
                if(null!=activity){
                    ToastUtil.toast(activity.getResources().getString(R.string.scan_error));
                }
            }
        });
        bleConnectModel.init();
        if (!bleConnectModel.isCurrentConnect())
            bleConnectModel.startScanBlueTooth();
    }


    public SurfBlueToothPopAdapter getSurfBlueToothPopAdapter() {
        if (surfBlueToothPopAdapter == null)
            surfBlueToothPopAdapter = new SurfBlueToothPopAdapter(activity);
        return surfBlueToothPopAdapter;
    }

    private void downLoad(final Queue<CloudPicData> cloudPicDataQueue) {
        if (cloudPicDataQueue != null && !cloudPicDataQueue.isEmpty()) {
            final CloudPicData cloudPicData = cloudPicDataQueue.poll();
            if (cloudPicData.getFormat().contains("video") || cloudPicData.getFormat().contains("MP4") || cloudPicData.getFormat().contains("mp4")) {
                String url = BitmapUtils.getDownloadFile(cloudPicData.getFileId()) + ".mp4";
                RetrofitDownload(url, cloudPicData.getCoverId(), Flag.APP_CAMERAL, ".mp4", "Video", cloudPicData.getFileId(), "");
//                downVideo(url, Flag.APP_CAMERAL);
            } else if (cloudPicData.getFormat().contains("25d/25d")) {
                String url = BitmapUtils.getDownloadFile(cloudPicData.getFileId()) + ".zip";
                File dirFile = new File(Flag.AI360_PATH);
                if (!dirFile.exists()) {
                    dirFile.mkdirs();
                }
                RetrofitDownload(url, cloudPicData.getCoverId(), Flag.AI360_PATH, ".zip", "Ai360", cloudPicData.getFileId(), cloudPicData.getName());
            } else {
                String downloadFileType = "";
                if (cloudPicData.getFormat().contains("/")) {

                    String temp[] = cloudPicData.getFormat().split("/");
                    if (temp.length > 1) {
                        downloadFileType = temp[temp.length - 1];
                    }
                } else {
                    downloadFileType = cloudPicData.getFormat();
                }

                String url = BitmapUtils.getDownloadFile(cloudPicData.getFileId()) + "." + downloadFileType;

                if (!StringUtil.isBlank(downloadFileType)) {
                    RetrofitDownload(url, null, Flag.APP_CAMERAL, "." + downloadFileType, "Image", cloudPicData.getFileId(), "");
                }
            }
        }
    }

    public ImageDataSqlControl getImageDataSqlControl() {
        return imageDataSqlControl;
    }

    public void initBigImageAdapter(PicBrowsingAdapter.CloseInter closeInter) {
//        picBrowsingAdapter = new PicBrowsingAdapter(activity, null, closeInter);
    }


    public void notifyDataSetChanged() {
//        picBrowsingAdapter.notifyDataSetChanged();
    }


    public void onViewClick(/*final Panel panel*/) {
        adapter.setItemClickInterface(new ItemClickInterface() {
            @Override
            public void itemClick(View view, int position) {
                // panel.setOpen(false, false);
                if (canClick && position >= 0 && !StringUtil.isBlank(mGirdList.get(position).getPath())) {
                    if (mGirdList.get(position).ischeck()) {
                        mGirdList.get(position).setIscheck(false);
                        itemClickCount--;
                        view.findViewById(R.id.grid_item_check).setVisibility(View.INVISIBLE);
                        if (itemClickCount == 0) {
                            mView.setTopSelectCanClick(false);
                            before1 = false;
                            isFooterShow = START_TYPE;
                            mView.outFooterSetting();
                        }
                    } else {
                        mView.setTopSelectCanClick(true);
                        mGirdList.get(position).setIscheck(true);
                        itemClickCount++;
                        view.findViewById(R.id.grid_item_check).setVisibility(View.VISIBLE);

                    }
                    //能否点击分享
                    if (itemClickCount <= 9 && itemClickCount > 0) {
                        mView.setShareCanClick(true);
                    } else {
                        mView.setShareCanClick(false);
                    }
                    if (itemClickCount == 1) {
                        before1 = true;
                        //设置状态,不走动画
                        if (isFooterShow == START_TYPE) {
                            for (int i = 0; i < mGirdList.size(); i++) {
                                if (mGirdList.get(i).ischeck()) {
                                    if (mGirdList.get(i).getImageData().getType() == Flag.TYPE_VIDEO) {

                                        //mView.setFooterType(BottomToolView.PHOTO_EDIT);
                                        mView.inFooterSetting(BottomToolView.PHOTO_EDIT);
                                        isFooterShow = PHOTO_EDIT_TYPE;
                                    } else {
                                        //mView.setFooterType(BottomToolView.ABLUM);
                                        mView.inFooterSetting(BottomToolView.ABLUM);
                                        isFooterShow = ABLUM_TYPE;
                                    }
                                }
                            }
                            //    mView.inFooterSetting();
                            return;
                        }
                        //走动画
                        for (int i = 0; i < mGirdList.size(); i++) {
                            if (mGirdList.get(i).ischeck()) {
                                if (mGirdList.get(i).getImageData().getType() == Flag.TYPE_VIDEO) {

                                    if (isFooterShow != PHOTO_EDIT_TYPE) {
                                        mView.setFooterType(BottomToolView.PHOTO_EDIT);
                                        isFooterShow = PHOTO_EDIT_TYPE;
                                    }
                                    break;
                                } else {
                                    mView.setFooterType(BottomToolView.ABLUM);
                                    isFooterShow = ABLUM_TYPE;
                                    break;
                                }
                            }
                        }


                    } else if (itemClickCount == 2) {
                        if (before1) {
                            mView.setFooterType(BottomToolView.PHOTO_EDIT);
                            isFooterShow = PHOTO_EDIT_TYPE;
                        }
                        before1 = false;
                    }

                } else {
                    mView.initAnim(view.getX() + view.getWidth() / 2, view.getY() + view.getHeight() / 2);
                    showBigPic(position);
                }
            }

            @Override
            public boolean itemLongClick(View view, int position) {

                return true;
            }

            @Override
            public void refreshUpload(View view, int position, StickyGridNewAdapter.HeaderViewHolder headerViewHolder) {


                setUpViewHolder(headerViewHolder);

                imageDataSqlControl.startUploadQuery();
            }

            @Override
            public void headClick(View view, int position, StickyGridNewAdapter.HeaderViewHolder headerViewHolder) {

                if (LoginedUser.getLoginedUser().getUploadStatus() == Flag.UPLOAD_NONE || LoginedUser.getLoginedUser().getUploadStatus() == Flag.UPLOAD_SUCCESS) {
//                    setUpViewHolder(headerViewHolder);
                    showUploadHintDialog();
//                    imageDataSqlControl.startQueryByIsUpload();

                } else if (LoginedUser.getLoginedUser().getUploadStatus() == Flag.UPLOAD_UPLOADING) {
                    //上传中提示
                    showUploadingHintDialog();
                } else if (LoginedUser.getLoginedUser().getUploadStatus() == Flag.UPLOAD_NETCHANGE || LoginedUser.getLoginedUser().getUploadStatus() == Flag.UPLOAD_REUPLOAD) {
                    //网络变化
                    showNetChangeDialog();
                } else {
                    //上传失败提示
                    showUploadErrorDialog();
                }
            }
        });
    }


    public void showBigPic(int position) {
//        clearPic();
        // vibrator.vibrate(30);
        // picBrowsingAdapter.setData(mGirdList);
        mView.showBigImage(position, mGirdList.get(position));
    }


    public void deleImageDialog(final int position) {
        if (mchoiceDialog == null) {
            mchoiceDialog = ChoiceDialog.createInstance(activity);
        }
        mchoiceDialog.setAblumTitle(activity.getResources().getString(R.string.delete));
        String message = activity.getResources().getString(R.string.delete_images_from) + activity.getResources().getString(R.string.your_studio);
        mchoiceDialog.setAblumMessage(message);
        mchoiceDialog.setOk(activity.getResources().getString(R.string.delete));
        mchoiceDialog.setCancel(activity.getResources().getString(R.string.cancel));
        mchoiceDialog.setChoiceCallback(new ChoiceDialog.CardDialogCallback() {
            @Override
            public void onOKClick() {
                dissmissChoiceDialog();
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        EventStatisticsUtil.event(activity, EventCommon.ABLUM_DELETE_CONFIRM);

                        imageDataSqlControl.startDel(model.startDel(mGirdList.get(position)));
                        mGirdList.remove(position);
                        mView.showBackground(mGirdList);
                        Toast.makeText(activity, activity.getResources().getString(R.string.delete_success), Toast.LENGTH_LONG).show();
                        // itemClickCount -= count;
                        isFooterShow = START_TYPE;

                        if (mGirdList.size() == 1) {
                            mView.outTopSelect();
                            LoginedUser loginedUser = LoginedUser.getLoginedUser();
                            loginedUser.setUploadStatus(Flag.UPLOAD_NONE);
                            LoginedUser.setLoginedUser(loginedUser);
                            ((AlbumNewActivity) activity).closeImageUtil();
                        } else {
                            if (position > mGirdList.size() - 2) {
                                mView.refreshBigImage(mGirdList.get(mGirdList.size() - 2), mGirdList.size() - 2, "refresh");
                            } else {
                                mView.refreshBigImage(mGirdList.get(position), position, "refresh");
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void onCancelClick() {
                EventStatisticsUtil.event(activity, EventCommon.ABLUM_DELETE_CANCEL);

                dissmissChoiceDialog();
            }

            @Override
            public void onDismiss() {

            }
        });
        mchoiceDialog.show();
        /*mchoiceDialog.setChoiceInterface(new ChoiceDialog5.ChoiceInterface() {
            @Override
            public void choiceOk() {
                dissmissChoiceDialog();
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        EventStatisticsUtil.event(activity, EventCommon.ABLUM_DELETE_CONFIRM);

                        imageDataSqlControl.startDel(model.startDel(mGirdList.get(position)));
                        mGirdList.remove(position);
                        mView.showBackground(mGirdList);
                        Toast.makeText(activity, activity.getResources().getString(R.string.delete_success), Toast.LENGTH_LONG).show();
                        // itemClickCount -= count;
                        isFooterShow = START_TYPE;

                        if (mGirdList.size() == 1) {
                            mView.outTopSelect();
                            LoginedUser loginedUser=LoginedUser.getLoginedUser();
                            loginedUser.setUploadStatus(Flag.UPLOAD_NONE);
                            LoginedUser.setLoginedUser(loginedUser);
                            ((AlbumNewActivity) activity).closeImageUtil();
                        } else {
                            if (position > mGirdList.size() - 2) {
                                mView.refreshBigImage(mGirdList.get(mGirdList.size() - 2), mGirdList.size() - 2,"refresh");
                            } else {
                                mView.refreshBigImage(mGirdList.get(position), position,"refresh");
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void choiceCencel() {
                EventStatisticsUtil.event(activity, EventCommon.ABLUM_DELETE_CANCEL);

                dissmissChoiceDialog();
            }
        });*/

    }

    public void setUpViewHolder(StickyGridNewAdapter.HeaderViewHolder headerViewHolder) {
        LoginedUser loginedUser = LoginedUser.getLoginedUser();
        headerViewHolder.tv_hint.setText(activity.getResources().getString(R.string.in_the_backup));
        headerViewHolder.ll_upload.setBackground(activity.getDrawable(R.drawable.album_head_bg));
        headerViewHolder.grid_item.setImageResource(R.drawable.oval);
        headerViewHolder.tv_time.setText(activity.getResources().getString(R.string.in_the_backup_num) + String.format("%02d", mGirdList.size() - 1));

        loginedUser.setUploadStatus(Flag.UPLOAD_UPLOADING);
        loginedUser.setUploadTime(TimeUtil.getBeiJingTimeGMT() + "");
        LoginedUser.setLoginedUser(loginedUser);
        headerViewHolder.tv_refresh.setVisibility(View.GONE);
    }

    public void uploadData() {
        RetrofitFactory.getInstence()
                .API()
                .getCloud()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())

                .subscribe(new MBaseObserver<List<CloudPicData>>(activity, false) {
                    @Override
                    protected void onSuccees(BaseResult<List<CloudPicData>> t) throws Exception {
                        List<CloudPicData> cloudPicData = t.getData();
                        List<UploadCloudData> murls = new ArrayList<>();

                        for (int i = mGirdList.size() - 1; i >= 0; i--) {
                            boolean isUpload = false;
                            //未进行过上传操作
                            if (null != mGirdList.get(i).getImageData() &&
                                    StringUtil.isBlank(mGirdList.get(i).getImageData().getValue3())) {
                                murls.add(new UploadCloudData(mGirdList.get(i).getImageData(), ""));
                            } else if (null != mGirdList.get(i).getImageData()) {
                                for (int j = 0; cloudPicData.size() > 0 && j < cloudPicData.size(); j++) {
                                    //web 进行修改
                                    if (mGirdList.get(i).getImageData().getValue3().equals(cloudPicData.get(j).getFileId())) {
                                        if (!StringUtil.isBlank(mGirdList.get(i).getImageData().getValue4()) && !mGirdList.get(i).getImageData().getValue4().equals(cloudPicData.get(j).getSha1()) && mGirdList.get(i).getImageData().getType() != Flag.TYPE_AI360) {
                                            murls.add(new UploadCloudData(mGirdList.get(i).getImageData(), ""));
                                            // 本地进行修改
                                        } else if (!cloudPicData.get(j).getSha1().equals(FileUpload.getFileHash(mGirdList.get(i).getPath().substring(7))) && mGirdList.get(i).getImageData().getType() != Flag.TYPE_AI360) {
                                            murls.add(new UploadCloudData(mGirdList.get(i).getImageData(), ""));
                                        }
                                        isUpload = true;
                                        break;
                                    }
                                }
                            }

                            if (null != mGirdList.get(i).getImageData()
                                    && !StringUtil.isBlank(mGirdList.get(i).getImageData().getValue3())
                                    && !isUpload) {
                                mGirdList.get(i).getImageData().setValue3("");
                                murls.add(new UploadCloudData(mGirdList.get(i).getImageData(), ""));
                            }
                        }
                        if (murls.size() > 0) {
                            AddCloudReceiver.notifyModifyAddPic(activity, murls);
                        } else {
                            mView.refreshGridView(new UploadBean(0, Flag.UPLOAD_SUCCESS, UploadManager.getInstance().getHttpType()));
                        }
                    }

                    @Override
                    protected void onCodeError(BaseResult<List<CloudPicData>> t) throws Exception {
                        mView.refreshGridView(new UploadBean(mGirdList.size() - 1, Flag.UPLOAD_FAIL, UploadManager.getInstance().getHttpType()));

                    }

                    @Override
                    protected void onFailure(Throwable e, boolean isNetWorkError) throws Exception {
                        if (isNetWorkError) {
                            mView.refreshGridView(new UploadBean(mGirdList.size() - 1, Flag.UPLOAD_REUPLOAD, UploadManager.getInstance().getHttpType()));
                        } else {
                            mView.refreshGridView(new UploadBean(mGirdList.size() - 1, Flag.UPLOAD_FAIL, UploadManager.getInstance().getHttpType()));

                        }
                    }
                });
    }

    public void showUploadHintDialog() {

        if (mchoiceDialog == null) {
            mchoiceDialog = ChoiceDialog.createInstance(activity);
        }
        mchoiceDialog.setAblumTitleBlack(activity.getResources().getString(R.string.to_backup));

        String message = activity.getResources().getString(R.string.to_backup_message);
        mchoiceDialog.setAblumMessageGray(message);
        mchoiceDialog.setOk(activity.getResources().getString(R.string.confirm));
        mchoiceDialog.setCancel(activity.getResources().getString(R.string.cancel));
        // mchoiceDialog.setDialogType(mchoiceDialog.TYPE_UPLOAD);
        mchoiceDialog.setChoiceCallback(new ChoiceDialog.CardDialogCallback() {
            @Override
            public void onOKClick() {
                LoginedUser loginedUser = LoginedUser.getLoginedUser();
                loginedUser.setUploadStatus(Flag.UPLOAD_UPLOADING);
                loginedUser.setUploadTime(TimeUtil.getBeiJingTimeGMT() + "");
                LoginedUser.setLoginedUser(loginedUser);

                dissmissChoiceDialog();

                if (UploadManager.getInstance().getHttpType() == UploadManager.WIFI) {
                    mView.refreshGridView(new UploadBean(mGirdList.size() - 1, Flag.UPLOAD_UPLOADING, UploadManager.getInstance().getHttpType()));
                    //开始上传
                } else {
                    mView.refreshGridView(new UploadBean(mGirdList.size() - 1, Flag.UPLOAD_REUPLOAD, UploadManager.getInstance().getHttpType()));
                }
                imageDataSqlControl.startUploadQuery();

            }

            @Override
            public void onCancelClick() {
                dissmissChoiceDialog();
            }

            @Override
            public void onDismiss() {

            }
        });
        mchoiceDialog.show();
       /* mchoiceDialog.setChoiceInterface(new ChoiceDialog5.ChoiceInterface() {
            @Override
            public void choiceOk() {
//                setUpViewHolder(headerViewHolder);

                LoginedUser loginedUser = LoginedUser.getLoginedUser();
                loginedUser.setUploadStatus(Flag.UPLOAD_REUPLOAD);
                LoginedUser.setLoginedUser(loginedUser);

                //开始上传
                if(UploadManager.getInstance().getHttpType()==UploadManager.WIFI){
                    mView.refreshGridView(new UploadBean(mGirdList.size()-1,Flag.UPLOAD_UPLOADING,UploadManager.getInstance().getHttpType()));
                    uploadData();
                }else {
                    mView.refreshGridView(new UploadBean(mGirdList.size()-1,Flag.UPLOAD_REUPLOAD,UploadManager.getInstance().getHttpType()));
                }
                dissmissChoiceDialog();
                mchoiceDialog.setDialogType(mchoiceDialog.TYPE_NULL);
            }

            @Override
            public void choiceCencel() {
                dissmissChoiceDialog();
                mchoiceDialog.setDialogType(mchoiceDialog.TYPE_NULL);
            }
        });*/


    }


    public void showNetChangeDialog() {
        if (null == uploadDialog) {
            uploadDialog = new ProgressDialog(activity, new ProgressDialog.DGProgressListener() {
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
            uploadDialog.show();
            uploadDialog.setType(ProgressDialog.TYPE_UPLOAD);
            uploadDialog.setText(activity.getResources().getString(R.string.to_backup_dialog_message_five),
                    ProgressDialog.TYPE_ABLUM_SELECT);
        } else {
            uploadDialog.show();
        }
    }

    public void showUploadingHintDialog() {
        if (null == registerUsDialog) {
            registerUsDialog = RegisterUsDialog.createInstance(activity);
        }
        registerUsDialog.show();
        mView.setApplyBlur(true);
        registerUsDialog.setAccount("<u>" + "<font color='#0057FF'>" + activity.getString(R.string.web_url) + "</u>");

        registerUsDialog.setAccountListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                Uri content_url = Uri.parse("https://" + activity.getString(R.string.web_url));
                intent.setData(content_url);
                activity.startActivity(intent);
            }
        });
        registerUsDialog.setClickLitner(new RegisterUsDialog.ChoiceInterface() {
            @Override
            public void close() {
//                            registerUsDialog.dismiss();
//                            clearTextContent();
//                            mLoginInter.finishView();
            }

            @Override
            public void goLogin() {

            }
        });
        registerUsDialog.setType(RegisterUsDialog.UPLOAD);
        registerUsDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mView.setApplyBlur(false);
            }
        });
    }

    public void showUploadErrorDialog() {
        if (mchoiceDialog == null) {
            mchoiceDialog = ChoiceDialog.createInstance(activity);
        }
        mchoiceDialog.show();
        if (LoginedUser.getLoginedUser().getUploadStatus() == Flag.UPLOAD_FAIL) {
            mchoiceDialog.setAblumTitle(activity.getResources().getString(R.string.server_error));
            String message = activity.getString(R.string.more) + "<font color='#0057FF'>" + UploadManager.getInstance().getUploadCount() + "</font> " + activity.getString(R.string.not_uploaded);
            mchoiceDialog.setAblumMessageGray(message);
            mchoiceDialog.setOk(activity.getString(R.string.re_upload));
            mchoiceDialog.setCancel(activity.getString(R.string.cancel));
            mchoiceDialog.setChoiceCallback(new ChoiceDialog.CardDialogCallback() {
                @Override
                public void onOKClick() {
                    mView.refreshGridView(new UploadBean(UploadManager.getInstance().getUploadCount(), Flag.UPLOAD_UPLOADING, UploadManager.getInstance().getHttpType()));

                    //刷新且重新上传
                    uploadManager.getDownloadInfoSqlControl().queryUploadFail();
                    dissmissChoiceDialog();
                }

                @Override
                public void onCancelClick() {
                    dissmissChoiceDialog();
                }

                @Override
                public void onDismiss() {

                }
            });
           /* mchoiceDialog.setChoiceInterface(new ChoiceDialog5.ChoiceInterface() {
                @Override
                public void choiceOk() {
                    EventBus.getDefault().post(new UploadBean(UploadManager.getInstance().getUploadCount(), Flag.UPLOAD_UPLOADING, UploadManager.getInstance().getHttpType()));

                    //刷新且重新上传
                    uploadManager.getDownloadInfoSqlControl().queryUploadFail();
                    dissmissChoiceDialog();
                }

                @Override
                public void choiceCencel() {
                    dissmissChoiceDialog();
                }
            });*/
        } else {
            mchoiceDialog.setAblumTitle(activity.getResources().getString(R.string.space_is_full));

            String message = activity.getString(R.string.your_space_is_full) + "<font color='#0057FF'>" + UploadManager.getInstance().getUploadCount() + "</font>" + activity.getString(R.string.file_are_not_uploaded) + "<br/>" +
                    activity.getString(R.string.please_upgrade_the_space_or_go_to) + "<br/>" +
                    "<u>" + "<font color='#0057FF'>" + activity.getString(R.string.web_url) + "/font>" + "</u>" + activity.getString(R.string.to_organize_the_space);
            mchoiceDialog.setAblumMessageGray(message);
            mchoiceDialog.setOk(activity.getString(R.string.re_upload));
            mchoiceDialog.setCancel(activity.getString(R.string.cancel));
            mchoiceDialog.setChoiceCallback(new ChoiceDialog.CardDialogCallback() {
                @Override
                public void onOKClick() {
                    mView.refreshGridView(new UploadBean(UploadManager.getInstance().getUploadCount(), Flag.UPLOAD_UPLOADING, UploadManager.getInstance().getHttpType()));

                    //刷新且重新上传
                    uploadManager.getDownloadInfoSqlControl().queryUploadFail();
                    dissmissChoiceDialog();
                }

                @Override
                public void onCancelClick() {
                    dissmissChoiceDialog();
                }

                @Override
                public void onDismiss() {

                }
            });
            /*mchoiceDialog.setChoiceInterface(new ChoiceDialog5.ChoiceInterface() {
                @Override
                public void choiceOk() {
                    EventBus.getDefault().post(new UploadBean(UploadManager.getInstance().getUploadCount(), Flag.UPLOAD_UPLOADING, UploadManager.getInstance().getHttpType()));

                    //刷新且重新上传
                    uploadManager.getDownloadInfoSqlControl().queryUploadFail();
                    dissmissChoiceDialog();
                }

                @Override
                public void choiceCencel() {
                    dissmissChoiceDialog();
                }
            });*/
        }
    }


    public void onActivityResult(final int requestCode, int resultCode, Intent data) {
        if (requestCode == resultCode && resultCode == Flag.CLOUDFILE) {
            boolean camera = false;
            CloudPicture cloudPicture = (CloudPicture) data.getSerializableExtra("CloudPicture");
            List<UploadCloudData> uploadCloudDataList = new ArrayList<>();

            for (int i = 0; i < mGirdList.size(); i++) {
                if (mGirdList.get(i).ischeck()) {
                    uploadCloudDataList.add(new UploadCloudData(mGirdList.get(i).getImageData(), cloudPicture.getId() + ""));
                }
               /* if (mGirdList.get(i).ischeck()&&mGirdList.get(i).getImageData().getType() != Flag.TYPE_VIDEO) {
                    uploadCloudDataList.add(new UploadCloudData(mGirdList.get(i).getImageData(), cloudPicture.getId() + ""));
                }else if(mGirdList.get(i).ischeck()&&mGirdList.get(i).getImageData().getType() == Flag.TYPE_VIDEO){
                    camera=true;
//                    CostomToastUtil.toast("视频暂时不能上传");
                }*/
            }
            /*if(camera){
                ConfirmDialog.createDialog(activity, "提示", "视频不能上传", "确认", "", new ConfirmDialog.DialogListener() {
                    @Override
                    public void okClick(DialogInterface dialog) {
                        dialog.dismiss();
                    }

                    @Override
                    public void cancleClick(DialogInterface dialog) {

                    }
                });
            }*/
            mView.outTopSelect();
            ToastUtil.toast(activity.getResources().getString(R.string.start_upload));
            AddCloudReceiver.notifyModifyAddPic(activity, uploadCloudDataList);
//            clearPic();
            canClearPic = true;

        }
        //AddPicReceiver.notifyModifyUsername(activity);
    }


    private void dissmissChoiceDialog() {
        if (mchoiceDialog != null) {
            mchoiceDialog.dismiss();
        }
    }


    public void clearPic() {
        if (mGirdList == null) {
            return;
        }
        for (GridItem item : mGirdList) {
            item.setIscheck(false);
        }
        itemClickCount = 0;
        mView.setTopSelectCanClick(false);
        mView.setShareCanClick(false);
        isFooterShow = START_TYPE;
        mView.outFooterSetting();
        adapter.notifyDataSetChanged();
    }


 /*   public String getcurrenturl() {
        if (mGirdList != null && mGirdList.size() != 0) {
            return mGirdList.get(0).getPath();
        } else {
            return null;
        }
    }
*/

    /**
     * 打开相机 权限申请
     */
    public void getCameraPer() {
        PackageManager pm = activity.getPackageManager();
        boolean hasACamera = pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)
                || pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)
                || Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD
                || Camera.getNumberOfCameras() > 0;
        if (hasACamera) {
            if (Build.VERSION.SDK_INT >= 23) {
                if (PermissionUtil.isMIUI()) {
                    if (!PermissionUtil.checkAppops(activity, AppOpsManager.OPSTR_FINE_LOCATION)
                            || !PermissionUtil.checkAppops(activity, AppOpsManager.OPSTR_CAMERA)
                            || !PermissionUtil.checkAppops(activity, AppOpsManager.OPSTR_WRITE_EXTERNAL_STORAGE)
                    ) {
                        boolean permissPass = PermissionUtil.checkAllPermission(activity);
                        if (permissPass) {
                            mView.setPanelClick(false);
                            SurfViewCameraPresenter.startAction(activity
                                   );
                        }
                    } else {
                        mView.setPanelClick(false);
                        SurfViewCameraPresenter.startAction(activity
                                );
                    }
                } else {
                    boolean permissPass = PermissionUtil.checkAllPermission(activity);
                    if (permissPass) {
                        mView.setPanelClick(false);
                        SurfViewCameraPresenter.startAction(activity
                               );
                    }
                }
            } else {
                mView.setPanelClick(false);
                SurfViewCameraPresenter.startAction(activity
                        );
            }
        } else {
            ToastUtil.toast(activity.getResources().getString(R.string.camera_permission));
        }
    }

    /**
     * 判断定位是否打开
     *
     * @return
     */
    public boolean checkGPSIsOpen() {
        LocationManager locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager == null)
            return false;
        return locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER);
    }


    public StickyGridNewAdapter initAdapter(GridView mGridView) {
        SharePreferences sharePreferences = SharePreferences.instance();
        boolean status = sharePreferences.getBoolean("AblumStatus", false);
        if (status) {
            currentlayoutType = TitleView.TWO_TYPE;
        } else {
            currentlayoutType = TitleView.FOUR_TYPE;
        }
        adapter = new StickyGridNewAdapter(activity, mGirdList, mGridView);
        if (currentlayoutType == TitleView.TWO_TYPE) {
            adapter.setType(StickyGridAdapter.TWO_TYPE);
        } else {
            adapter.setType(StickyGridAdapter.FOUR_TYPE);
        }
        return adapter;
    }

    public int getCurrentlayoutType() {
        return currentlayoutType;
    }

    public void setCurrentlayoutType(int currentlayoutType, GridView gridView) {
        mFirstVisible = gridView.getFirstVisiblePosition();
        this.currentlayoutType = currentlayoutType;
        if (currentlayoutType == TitleView.TWO_TYPE) {
            adapter.setType(StickyGridAdapter.TWO_TYPE);
        } else {
            adapter.setType(StickyGridAdapter.FOUR_TYPE);
        }

        mView.setGridViewFirstVisItem(mFirstVisible);

    }


    public int getmFirstVisible() {
        return mFirstVisible;
    }

    public boolean getIsFirstIn() {
        return model.isFirstIn();
    }


    public boolean isCanClick() {
        return canClick;
    }

    public void setCanClick(boolean canClick) {
        if (canClick) {
            for (int i = 0; i < mGirdList.size(); i++) {
                mGirdList.get(i).setType("1");
            }
            adapter.setIsneedHeader(false);
        } else {
            for (int i = 0; i < mGirdList.size(); i++) {
                mGirdList.get(i).setType("0");
            }
            adapter.setIsneedHeader(true);
        }
        adapter.notifyDataSetChanged();
        this.canClick = canClick;
    }

    public int getmGirdListSize() {
        return this.mGirdList.size() - 1;
    }

    @Override
    public void onStop() {
//        if (canClearPic) {
//
//            clearPic();
//        }
    }

    public void deleteAllUpload() {
        UploadManager.getInstance().dellAllRunTask();
    }

    public void startFloatAnim(AnimFactory.FloatListener floatListener) {
        if (valueAnimator != null && valueAnimator.isRunning()) {
            valueAnimator.cancel();
        }
        valueAnimator = AnimFactory.rotationAnim(floatListener);
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

    private boolean photo = false;
    private boolean camera = false;
    private boolean ai360 = false;
    private RetrofitDownloadManager.DownloadListener downloadListener;

    /**
     * 下载图片url自带后缀，视频需要手动添加后缀
     *
     * @param url
     * @param destFileDir
     * @param downloadFileName
     * @param type
     */
    private void RetrofitDownload(String url, final String coverID, String destFileDir, final String downloadFileName, final String type, final String fileId, final String title) {
        downloadListener = new RetrofitDownloadManager.DownloadListener() {
            @Override
            public void onResponse(ResponseBody responseBody, final String savePath) {
                final ImageData imageData = new ImageData();
                final long time = System.currentTimeMillis();
                final LightRatioData lightRatioData = new LightRatioData();
                //未连接蓝牙,光比值为空
                lightRatioData.initData();
                if (type.equals("Image")) {
                    //图片
                    String path2 = FileUtil.copyFile(savePath, Flag.NATIVESDFILE, time + downloadFileName);
                    imageData.setType(Flag.TYPE_PIC);
                    imageData.setLocalUrl("file://" + savePath);
                    imageData.setSmallLocalUrl("file://" + path2);
                    imageData.setCurrentPosition(0);
                    imageData.setTimeStamp(time);
                    imageData.setLightRatioData(lightRatioData);
                    imageDataSqlControl.insertImg(imageData);
                    if (activity != null) {
                        AddPicReceiver.notifyModifyUsername(activity, "");
                        //mView.setAblumImg(result.getSmallLocalUrl());
                        downLoad(mRunOnDraw);
                    }
                } else if (type.equals("Ai360")) {

                    String localUrl = Environment.getExternalStorageDirectory() + Flag.BaseData + "/Panoramas/" + System.currentTimeMillis() + "/";
                    try {
                        FileUtil.UnZipFolder(savePath, localUrl);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    //360度预览
                    imageData.setType(Flag.TYPE_AI360);
                    imageData.setLocalUrl(localUrl);
                    imageData.setCurrentPosition(0);
                    imageData.setTimeStamp(time);
                    imageData.setValue3(fileId);
                    imageData.setLightRatioData(lightRatioData);
                    imageData.setValue5(title);
                    if (coverID != null && !coverID.isEmpty()) {
                        String cover = com.bcnetech.hyphoto.utils.StringUtil.getSizeUrl(BitmapUtils.getBitmapUrl2(coverID), 0, 0);
                        Glide.with(activity).load(cover).asBitmap().into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                String path2 = FileUtil.saveBitmap2(resource,
                                        false, 0, time - 1 + "", false, false, null);//小图
                                imageData.setSmallLocalUrl("file://" + path2);
                                imageDataSqlControl.insertImg(imageData);
                                if (activity != null) {
                                    AddPicReceiver.notifyModifyUsername(activity, "");
                                    File zip = new File(savePath);
                                    if (zip.exists()) {
                                        zip.delete();
                                    }
                                    //mView.setAblumImg(result.getSmallLocalUrl());
                                    downLoad(mRunOnDraw);
                                }
                            }
                        });
                    }
                } else {
                    //视频
                    imageData.setType(Flag.TYPE_VIDEO);
                    imageData.setLocalUrl(savePath);
                    imageData.setCurrentPosition(0);
                    imageData.setTimeStamp(time);
                    imageData.setLightRatioData(lightRatioData);
                    if (coverID != null && !coverID.isEmpty()) {
                        String cover = com.bcnetech.hyphoto.utils.StringUtil.getSizeUrl(BitmapUtils.getBitmapUrl2(coverID), 0, 0);
                        Glide.with(activity).load(cover).asBitmap().into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                String path2 = FileUtil.saveBitmap2(resource,
                                        false, 0, time - 1 + "", false, false, null);//小图
                                imageData.setSmallLocalUrl("file://" + path2);
                                imageDataSqlControl.insertImg(imageData);
                                if (activity != null) {
                                    AddPicReceiver.notifyModifyUsername(activity, "");
                                    //mView.setAblumImg(result.getSmallLocalUrl());
                                    downLoad(mRunOnDraw);
                                }
                            }
                        });
                    } else {
                        //没有封面ID时从视频里获取
                        String path2 = "";
                        try {
                            Bitmap bitmap = getVideoBitmap(savePath);
                            path2 = "file://" + FileUtil.saveBitmaptoNative(bitmap, System.currentTimeMillis() + "", false);
                        } catch (Exception e) {
                        }
                        imageData.setSmallLocalUrl(path2);
                        imageDataSqlControl.insertImg(imageData);
                        if (activity != null) {
                            AddPicReceiver.notifyModifyUsername(activity, "");
                            //mView.setAblumImg(result.getSmallLocalUrl());
                            downLoad(mRunOnDraw);
                        }
                    }
                }
            }

            @Override
            public void onError(Throwable e) {
                ToastUtil.toast(activity.getResources().getString(R.string.download_error));
            }
        };
        RetrofitDownloadManager retrofitDownloadManager = RetrofitDownloadManager.Instance();
        retrofitDownloadManager.setFileInfo(destFileDir, downloadFileName);
        retrofitDownloadManager.download(url);
        retrofitDownloadManager.setDownloadListener(downloadListener);
    }

    //   private void downVideo(String url, String destFileDir, String downloadFileName, final String type) {
    //        OkHttpClientManager.DownloadDelegate downloadDelegate = OkHttpClientManager.getInstance().getDownloadDelegate();
//        downloadDelegate.downloadAsyn(".mp4", url, destFileDir, new OkHttpClientManager.ResultCallback() {
//            @Override
//            public void onError(Request request, Exception e) {
//                ToastUtil.toast(activity.getResources().getString(R.string.download_error));
//            }
//
//            @Override
//            public void onResponse(Object response) {
//                String path = (String) response;
//                final Bitmap coverbitmap = getVideoBitmap(path);
//                final long time = System.currentTimeMillis();
//                String path2 = FileUtil.saveBitmap2(coverbitmap, false, 0, time - 1 + "", false, false, null);//小图
//                ImageData imageData = new ImageData();
//                LightRatioData lightRatioData = new LightRatioData();
//                //未连接蓝牙,光比值为空
//                lightRatioData.initData();
//                //mCurrentPresetParms = null;
//                imageData.setType(Flag.TYPE_VIDEO);
//                imageData.setLocalUrl(path);
//                imageData.setSmallLocalUrl("file://" + path2);
//                imageData.setCurrentPosition(0);
//                imageData.setTimeStamp(time);
//                imageData.setLightRatioData(lightRatioData);
//
//                imageDataSqlControl.insertImg(imageData);
//                String imageDataJson = JsonUtil.Object2Json(imageData);
//                try {
//                    FileUtil.saveImageDataJson(time + "", imageDataJson);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                if (activity != null) {
//                    AddPicReceiver.notifyModifyUsername(activity, "");
//                    //mView.setAblumImg(result.getSmallLocalUrl());
//                    coverbitmap.recycle();
//                    downLoad(mRunOnDraw);
//                }
//            }
//
//        });
//    }
//
    private Bitmap getVideoBitmap(String url) {
        MediaMetadataRetriever media = new MediaMetadataRetriever();
        media.setDataSource(url);
        return media.getFrameAtTime();
    }


    //   private void downPic(String url, String destFileDir, String downloadFileName, final String type) {
//        OkHttpClientManager.DownloadDelegate downloadDelegate = OkHttpClientManager.getInstance().getDownloadDelegate();
//        downloadDelegate.downloadAsyn("", url, destFileDir, new OkHttpClientManager.ResultCallback() {
//            @Override
//            public void onError(Request request, Exception e) {
//                ToastUtil.toast(activity.getResources().getString(R.string.download_error));
//            }
//
//            @Override
//            public void onResponse(Object response) {
//                String path = (String) response;
//
//                final long time = System.currentTimeMillis();
//
//                String path2 = FileUtil.copyFile(path, Flag.NATIVESDFILE, time + "." + type);
//                ImageData imageData = new ImageData();
//                LightRatioData lightRatioData = new LightRatioData();
//                //未连接蓝牙,光比值为空
//                lightRatioData.initData();
//                //mCurrentPresetParms =ic null;
//                imageData.setType(Flag.TYPE_PIC);
//                imageData.setLocalUrl("file://" + path);
//                imageData.setSmallLocalUrl("file://" +path2);
//                imageData.setCurrentPosition(0);
//                imageData.setTimeStamp(time);
//                imageData.setLightRatioData(lightRatioData);
//
//                imageDataSqlControl.insertImg(imageData);
//                String imageDataJson = JsonUtil.Object2Json(imageData);
//                try {
//                    FileUtil.saveImageDataJson(time + "", imageDataJson);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                if (activity != null) {
//                    AddPicReceiver.notifyModifyUsername(activity, "");
//                    //mView.setAblumImg(result.getSmallLocalUrl());
//                    downLoad(mRunOnDraw);
//                }
//            }
//
//        });
//    }

    public void showOtherPopupWindow(View view) {
        if (itemClickCount == 0) {
            if (null == dgProgressDialog2) {
                dgProgressDialog2 = new ProgressDialog(activity, new ProgressDialog.DGProgressListener() {
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
                dgProgressDialog2.setType(ProgressDialog.TYPE_ABLUM_SELECT);
                dgProgressDialog2.setText(activity.getResources().getString(R.string.album_hint),
                        ProgressDialog.TYPE_ABLUM_SELECT);
                dgProgressDialog2.show();
            } else {
                dgProgressDialog2.show();
            }
            return;

        }

        if (null == morePopupWindow) {
            morePopupWindow = showMorePopupWindow(view);
        } else {
            morePopupWindow.showAsDropDown(view);
        }
    }

    private List<ShareData> picPaths;

    public void showSharePopupWindow(View view) {

        if (itemClickCount == 0) {
            if (null == dgProgressDialog2) {
                dgProgressDialog2 = new ProgressDialog(activity, new ProgressDialog.DGProgressListener() {
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
                dgProgressDialog2.show();
                dgProgressDialog2.setType(ProgressDialog.TYPE_ABLUM_SELECT);
                dgProgressDialog2.setText(activity.getResources().getString(R.string.album_hint),
                        ProgressDialog.TYPE_ABLUM_SELECT);
            } else {
                dgProgressDialog2.show();
            }

            return;

        }

        int cameraCount = 0;
        int photoCount = 0;

        if (null == picPaths) {
            picPaths = new ArrayList<>();
        } else {
            picPaths.clear();
        }
        photo = false;
        camera = false;
        ai360 = false;
        for (int i = 0; i < mGirdList.size(); i++) {
            if (mGirdList.get(i).ischeck() && mGirdList.get(i).getImageData().getType() == Flag.TYPE_PIC) {
                ShareData shareData = new ShareData();
                shareData.setUrl(mGirdList.get(i).getPath().substring(7));
                picPaths.add(shareData);
                photoCount++;
                photo = true;
            } else if (mGirdList.get(i).ischeck() && mGirdList.get(i).getImageData().getType() == Flag.TYPE_VIDEO) {
                ShareData shareData = new ShareData();
                shareData.setUrl(mGirdList.get(i).getImageData().getLocalUrl());
                picPaths.add(shareData);
                camera = true;
                cameraCount++;
            } else if (mGirdList.get(i).ischeck() && mGirdList.get(i).getImageData().getType() == Flag.TYPE_AI360) {
                ai360 = true;

                ShareData shareData = new ShareData();
                shareData.setUrl(mGirdList.get(i).getImageData().getValue3());
                shareData.setCover(mGirdList.get(i).getImageData().getSmallLocalUrl().substring(7));
                shareData.setTitle(mGirdList.get(i).getImageData().getValue5());

                picPaths.add(shareData);

            }
        }

        //图片
        if (photoCount <= 9 && camera == false) {
            if (Flag.isEnglish) {
                if (photoCount == 1) {
                    shareType = PHOTO_ONE;
                } else {
                    shareType = PHOTO;
                }
            } else {
                shareType = PHOTO;
            }

            //多图片
        } else if (photoCount > 9 && camera == false) {
            shareType = PHOTOS;
            //单个视频
        } else if (photo == false && cameraCount == 1) {
            shareType = CAMERA;
            //多视频
        } else if (photo == false && cameraCount > 1) {
            shareType = CAMERAS;
            //视频和图片
        } else if (photo && camera) {
            shareType = PHOTO_CAMERA;
        }

        if (null == sharePopupWindow) {
            if (ai360) {

            } else {

            }
            sharePopupWindow = showTipPopupWindow(view);
        } else {
            sharePopupWindow.showAsDropDown(view);
        }
    }


    private PopupWindow showTipPopupWindow(final View anchorView) {

        LinearLayout ll_qq;
        LinearLayout ll_wechat;
        LinearLayout ll_wechat_firend;
        LinearLayout ll_sina;
        LinearLayout ll_qq_zone;
        LinearLayout ll_other;

        LinearLayout ll_twitter;
        LinearLayout ll_facebook;

        LinearLayout ll_cnOne;
        LinearLayout ll_cnTwo;
        LinearLayout ll_us;

        LinearLayout ll_us_other;

        final View contentView = LayoutInflater.from(anchorView.getContext()).inflate(R.layout.share_view_layout, null);
        contentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        // 创建PopupWindow时候指定高宽时showAsDropDown能够自适应
        // 如果设置为wrap_content,showAsDropDown会认为下面空间一直很充足（我以认为这个Google的bug）
        // 备注如果PopupWindow里面有ListView,ScrollView时，一定要动态设置PopupWindow的大小
        final PopupWindow popupWindow = new PopupWindow(contentView,
                ViewGroup.LayoutParams.MATCH_PARENT, contentView.getMeasuredHeight(), false);

        ll_other = (LinearLayout) contentView.findViewById(R.id.ll_other);
        ll_qq_zone = (LinearLayout) contentView.findViewById(R.id.ll_qq_zone);
        ll_qq = (LinearLayout) contentView.findViewById(R.id.ll_qq);
        ll_sina = (LinearLayout) contentView.findViewById(R.id.ll_sina);
        ll_wechat = (LinearLayout) contentView.findViewById(R.id.ll_wechat);
        ll_wechat_firend = (LinearLayout) contentView.findViewById(R.id.ll_wechat_firend);

        ll_cnOne = (LinearLayout) contentView.findViewById(R.id.ll_cnOne);
        ll_cnTwo = (LinearLayout) contentView.findViewById(R.id.ll_cnTwo);
        ll_us = (LinearLayout) contentView.findViewById(R.id.ll_us);
        ll_us_other = (LinearLayout) contentView.findViewById(R.id.ll_us_other);
        ll_twitter = (LinearLayout) contentView.findViewById(R.id.ll_twitter);
        ll_facebook = (LinearLayout) contentView.findViewById(R.id.ll_facebook);

        if (Flag.isEnglish) {
            ll_us.setVisibility(View.VISIBLE);
        } else {
            ll_cnOne.setVisibility(View.VISIBLE);
            ll_cnTwo.setVisibility(View.VISIBLE);
        }

        contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
//
            }
        });


        contentView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // 自动调整箭头的位置
                autoAdjustArrowPos(popupWindow, contentView, anchorView);
                contentView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });

        ll_qq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 360图片和视频图片不能同时分享
                if (ai360 == true && (camera == true || photo == true)) {
                    showChoiceDialog("", activity.getString(R.string.qq), activity.getResources().getString(R.string.photo_360_hint_one));
                    return;
                }
                if (shareType == PHOTO_CAMERA || shareType == CAMERAS) {
                    showChoiceDialog("", activity.getString(R.string.qq), activity.getString(R.string.qq_shareHint));
                    return;
                }
                String fileId = "";
                //360 图片不能分享多个
                if (ai360 == true && picPaths.size() > 1) {
                    showChoiceDialog("", activity.getString(R.string.qq), activity.getResources().getString(R.string.photo_360_hint_two));
                    return;
                } else if (ai360 == true && picPaths.size() == 1) {
                    // 360图片未生成
                    if (StringUtil.isBlank(picPaths.get(0).getUrl())) {
                        showChoiceDialog("", activity.getString(R.string.qq), activity.getResources().getString(R.string.photo_360_hint_three));
                        return;
                    }
                    fileId = picPaths.get(0).getUrl();
                }
                popupWindow.dismiss();
                mView.outTopSelect();
                ShareImagesUtil.onShareQQ(activity, picPaths, photo, fileId);
            }
        });
        ll_wechat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ai360 == true && (camera == true || photo == true)) {
                    showChoiceDialog("", activity.getString(R.string.we_chat), activity.getResources().getString(R.string.photo_360_hint_one));
                    return;
                }
                if (shareType == PHOTO_CAMERA || shareType == CAMERAS) {
                    showChoiceDialog("", activity.getString(R.string.we_chat), activity.getString(R.string.weChat_shareHint));
                    return;
                }
                String fileId = "";
                if (ai360 == true && picPaths.size() > 1) {
                    showChoiceDialog("", activity.getString(R.string.we_chat), activity.getResources().getString(R.string.photo_360_hint_two));
                    return;
                } else if (ai360 == true && picPaths.size() == 1) {
                    if (StringUtil.isBlank(picPaths.get(0).getUrl())) {
                        showChoiceDialog("", activity.getString(R.string.we_chat), activity.getResources().getString(R.string.photo_360_hint_three));
                        return;
                    }
                    fileId = picPaths.get(0).getUrl();

                }
                popupWindow.dismiss();
                mView.outTopSelect();
                ShareImagesUtil.onShareWeChat(activity, picPaths, photo, fileId);
            }
        });
        ll_wechat_firend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ai360 == true && (camera == true || photo == true)) {
                    showChoiceDialog("", activity.getString(R.string.moments), activity.getResources().getString(R.string.photo_360_hint_one));
                    return;
                }
                if (shareType == PHOTO_CAMERA || shareType == CAMERAS || shareType == PHOTOS) {
                    showChoiceDialog("", activity.getString(R.string.moments), activity.getString(R.string.moments_shareHint));
                    return;
                }
                String fileId = "";
                if (ai360 == true && picPaths.size() > 1) {
                    showChoiceDialog("", activity.getString(R.string.moments), activity.getResources().getString(R.string.photo_360_hint_two));
                    return;
                } else if (ai360 == true && picPaths.size() == 1) {
                    if (StringUtil.isBlank(picPaths.get(0).getUrl())) {
                        showChoiceDialog("", activity.getString(R.string.moments), activity.getResources().getString(R.string.photo_360_hint_three));
                        return;
                    }

                    fileId = picPaths.get(0).getUrl();
                }
                popupWindow.dismiss();
                mView.outTopSelect();
                ShareImagesUtil.onShareWeChatFriend(activity, picPaths, photo, fileId);
            }
        });
        ll_sina.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ai360 == true && (camera == true || photo == true)) {
                    showChoiceDialog("", activity.getString(R.string.sina), activity.getResources().getString(R.string.photo_360_hint_one));
                    return;
                }
                if (shareType == PHOTO_CAMERA || shareType == CAMERAS || shareType == PHOTOS) {
                    showChoiceDialog("", activity.getString(R.string.sina), activity.getString(R.string.sina_shareHint));
                    return;
                }
                String fileId = "";
                if (ai360 == true && picPaths.size() > 1) {
                    showChoiceDialog("", activity.getString(R.string.sina), activity.getResources().getString(R.string.photo_360_hint_two));
                    return;
                } else if (ai360 == true && picPaths.size() == 1) {
                    if (StringUtil.isBlank(picPaths.get(0).getUrl())) {
                        showChoiceDialog("", activity.getString(R.string.sina), activity.getResources().getString(R.string.photo_360_hint_three));
                        return;
                    }

                    fileId = picPaths.get(0).getUrl();
                }
                popupWindow.dismiss();
                mView.outTopSelect();
                ShareImagesUtil.onShareSina(activity, picPaths, photo, fileId);
            }
        });
        ll_qq_zone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ai360 == true && (camera == true || photo == true)) {
                    showChoiceDialog("", activity.getString(R.string.qq_zone), activity.getResources().getString(R.string.photo_360_hint_one));
                    return;
                }
                if (shareType == PHOTO_CAMERA || shareType == CAMERAS) {
                    showChoiceDialog("", activity.getString(R.string.qq_zone), activity.getString(R.string.qqZone_shareHint));
                    return;
                }
                String fileId = "";
                if (ai360 == true && picPaths.size() > 1) {
                    showChoiceDialog("", activity.getString(R.string.qq_zone), activity.getResources().getString(R.string.photo_360_hint_two));
                    return;
                } else if (ai360 == true && picPaths.size() == 1) {
                    if (StringUtil.isBlank(picPaths.get(0).getUrl())) {
                        showChoiceDialog("", activity.getString(R.string.qq_zone), activity.getResources().getString(R.string.photo_360_hint_three));
                        return;
                    }

                    fileId = picPaths.get(0).getUrl();
                }
                popupWindow.dismiss();
                mView.outTopSelect();
                ShareImagesUtil.onShareQQZone(activity, picPaths, photo, fileId);
            }
        });
        ll_other.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ai360 == true && (camera == true || photo == true)) {
                    showChoiceDialog("", activity.getString(R.string.share), activity.getResources().getString(R.string.photo_360_hint_one));
                    return;
                }
                String fileId = "";
                if (ai360 == true && picPaths.size() > 1) {
                    showChoiceDialog("", activity.getString(R.string.share), activity.getResources().getString(R.string.photo_360_hint_two));
                    return;
                } else if (ai360 == true && picPaths.size() == 1) {
                    if (StringUtil.isBlank(picPaths.get(0).getUrl())) {
                        showChoiceDialog("", activity.getString(R.string.share), activity.getResources().getString(R.string.photo_360_hint_three));
                        return;
                    }
                    fileId = picPaths.get(0).getUrl();
                }
                if (shareType == PHOTO_CAMERA || shareType == CAMERAS) {
                    showChoiceDialog("", activity.getString(R.string.share), activity.getString(R.string.other_shareHint));
                    return;
                }
                popupWindow.dismiss();
                mView.outTopSelect();
                ShareImagesUtil.onShareOther(activity, picPaths, photo, fileId);
            }
        });

        ll_facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ai360 == true && (camera == true || photo == true)) {
                    showChoiceDialog("", activity.getString(R.string.facebook), activity.getResources().getString(R.string.photo_360_hint_one));
                    return;
                }
                if (shareType == PHOTO_CAMERA || shareType == CAMERAS) {
                    showChoiceDialog("", activity.getString(R.string.facebook), activity.getString(R.string.facebook_shareHint));
                    return;
                }
                String fileId = "";
                if (ai360 == true && picPaths.size() > 1) {
                    showChoiceDialog("", activity.getString(R.string.facebook), activity.getResources().getString(R.string.photo_360_hint_two));
                    return;
                } else if (ai360 == true && picPaths.size() == 1) {
                    if (StringUtil.isBlank(picPaths.get(0).getUrl())) {
                        showChoiceDialog("", activity.getString(R.string.facebook), activity.getResources().getString(R.string.photo_360_hint_three));
                        return;
                    }
                    fileId = picPaths.get(0).getUrl();
                }
                popupWindow.dismiss();
                mView.outTopSelect();
                ShareImagesUtil.onShareFacebook(activity, picPaths, photo, fileId);
            }
        });

        ll_twitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ai360 == true && (camera == true || photo == true)) {
                    showChoiceDialog("", activity.getString(R.string.twitter), activity.getResources().getString(R.string.photo_360_hint_one));
                    return;
                }
                if (shareType == PHOTO_CAMERA || shareType == CAMERAS || shareType == PHOTO || shareType == PHOTOS) {
                    showChoiceDialog("", activity.getString(R.string.twitter), activity.getString(R.string.twitter_shareHint));
                    return;
                }
                String fileId = "";
                if (ai360 == true && picPaths.size() > 1) {
                    showChoiceDialog("", activity.getString(R.string.twitter), activity.getResources().getString(R.string.photo_360_hint_two));
                    return;
                } else if (ai360 == true && picPaths.size() == 1) {

                    if (StringUtil.isBlank(picPaths.get(0).getUrl())) {
                        showChoiceDialog("", activity.getString(R.string.twitter), activity.getResources().getString(R.string.photo_360_hint_three));
                        return;
                    }
                    fileId = picPaths.get(0).getUrl();
                }
                popupWindow.dismiss();
                mView.outTopSelect();
                ShareImagesUtil.onShareTwitter(activity, picPaths, photo, fileId);
            }
        });

        ll_us_other.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ai360 == true && (camera == true || photo == true)) {
                    showChoiceDialog("", activity.getString(R.string.share), activity.getResources().getString(R.string.photo_360_hint_one));
                    return;
                }
                String fileId = "";
                if (ai360 == true && picPaths.size() > 1) {
                    showChoiceDialog("", activity.getString(R.string.share), activity.getResources().getString(R.string.photo_360_hint_two));
                    return;
                } else if (ai360 == true && picPaths.size() == 1) {
                    if (StringUtil.isBlank(picPaths.get(0).getUrl())) {
                        showChoiceDialog("", activity.getString(R.string.share), activity.getResources().getString(R.string.photo_360_hint_three));
                        return;
                    }
                    fileId = picPaths.get(0).getUrl();
                }
                popupWindow.dismiss();
                ShareImagesUtil.onShareOther(activity, picPaths, photo, fileId);
            }
        });


        // 如果不设置PopupWindow的背景，有些版本就会出现一个问题：无论是点击外部区域还是Back键都无法dismiss弹框
        popupWindow.setBackgroundDrawable(new ColorDrawable());

        // setOutsideTouchable设置生效的前提是setTouchable(true)和setFocusable(false)
        popupWindow.setOutsideTouchable(true);

        // 设置为true之后，PopupWindow内容区域 才可以响应点击事件
        popupWindow.setTouchable(true);

        // true时，点击返回键先消失 PopupWindow
        // 但是设置为true时setOutsideTouchable，setTouchable方法就失效了（点击外部不消失，内容区域也不响应事件）
        // false时PopupWindow不处理返回键
        popupWindow.setFocusable(false);
        popupWindow.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;   // 这里面拦截不到返回键
            }
        });
        // 如果希望showAsDropDown方法能够在下面空间不足时自动在anchorView的上面弹出
        // 必须在创建PopupWindow的时候指定高度，不能用wrap_content
        popupWindow.showAsDropDown(anchorView);
        return popupWindow;
    }

    private void showChoiceDialog(String cancel, String title, String hint) {
        if (mchoiceDialog == null) {
            mchoiceDialog = ChoiceDialog.createInstance(activity);
        }
        mchoiceDialog.setChoiceCallback(new ChoiceDialog.CardDialogCallback() {
            @Override
            public void onOKClick() {
                mchoiceDialog.dismiss();
            }

            @Override
            public void onCancelClick() {
                mchoiceDialog.dismiss();
            }

            @Override
            public void onDismiss() {

            }
        });
        mchoiceDialog.setOk(activity.getResources().getString(R.string.confirm));
        mchoiceDialog.setCancel(cancel);
        mchoiceDialog.setTitle(title);
        mchoiceDialog.setHint(hint);
        mchoiceDialog.show();
    }


    private PopupWindow showMorePopupWindow(final View anchorView) {

        LinearLayout ll_delete;
        LinearLayout ll_flashDisk;

        final View contentView = LayoutInflater.from(anchorView.getContext()).inflate(R.layout.more_view_layout, null);
        contentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        // 创建PopupWindow时候指定高宽时showAsDropDown能够自适应
        // 如果设置为wrap_content,showAsDropDown会认为下面空间一直很充足（我以认为这个Google的bug）
        // 备注如果PopupWindow里面有ListView,ScrollView时，一定要动态设置PopupWindow的大小
        final PopupWindow popupWindow = new PopupWindow(contentView,
                ViewGroup.LayoutParams.MATCH_PARENT, contentView.getMeasuredHeight(), false);

        ll_delete = (LinearLayout) contentView.findViewById(R.id.ll_delete);
        ll_flashDisk = (LinearLayout) contentView.findViewById(R.id.ll_flashDisk);

//        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
//            @Override
//            public void onDismiss() {
//                ll_colours.setVisibility(View.INVISIBLE);
//                iv_main.setImageResource(R.drawable.artwork_master);
//                iv_colours.setImageResource(R.drawable.colours);
//                tv_shapeLeft.setImageResource(R.drawable.shape_left);
//                iv_lucency.setImageResource(R.drawable.lucency);
////                onClickListener.onClick(v);
//                select=0;
////                matting_pic.setBackground(MattingPicView.BACKGROUD_TYPE_DISSMISS);
//            }
//        });
        contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
//                ll_colours.setVisibility(View.INVISIBLE);
//                iv_main.setImageResource(R.drawable.artwork_master);
//                iv_colours.setImageResource(R.drawable.colours);
//                tv_shapeLeft.setImageResource(R.drawable.shape_left);
//                iv_lucency.setImageResource(R.drawable.lucency);
////                onClickListener.onClick(v);
//                select=0;
            }
        });

        contentView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // 自动调整箭头的位置
                autoAdjustArrowPos(popupWindow, contentView, anchorView);
                contentView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });

        ll_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mchoiceDialog == null) {
                    mchoiceDialog = ChoiceDialog.createInstance(activity);
                }

                mchoiceDialog.setAblumTitle(activity.getResources().getString(R.string.delete));
                String message = activity.getResources().getString(R.string.delete_images_from) + " <font color=\"#D0021B\">" + itemClickCount + "</font>  ";
                if (itemClickCount == 1) {
                    message += activity.getResources().getString(R.string.your_cloud);
                } else {
                    message += activity.getResources().getString(R.string.your_clouds);
                }
                mchoiceDialog.setAblumMessage(message);
                mchoiceDialog.setOk(activity.getResources().getString(R.string.delete));
                mchoiceDialog.setCancel(activity.getResources().getString(R.string.cancel));
                mchoiceDialog.setChoiceCallback(new ChoiceDialog.CardDialogCallback() {
                    @Override
                    public void onOKClick() {
                        dissmissChoiceDialog();
                        new Handler().post(new Runnable() {
                            @Override
                            public void run() {
                                EventStatisticsUtil.event(activity, EventCommon.ABLUM_DELETE_CONFIRM);

                                List<Integer> list = new ArrayList();
                                int count = 0;
                                for (int i = 0; i < mGirdList.size(); i++) {
                                    if (mGirdList.get(i).ischeck()) {
                                        imageDataSqlControl.startDel(model.startDel(mGirdList.get(i)));
                                        list.add(i);
                                    }
                                }
                                for (int i = list.size() - 1; i >= 0; i--) {
                                    mGirdList.remove((int) list.get(i));
                                    count++;
                                }
                                adapter.notifyDataSetChanged();
                                mView.showBackground(mGirdList);
                                Toast.makeText(activity, activity.getResources().getString(R.string.delete_success), Toast.LENGTH_LONG).show();
                                itemClickCount -= count;
                                isFooterShow = START_TYPE;
                                mView.outTopSelect();

                            }
                        });
                    }

                    @Override
                    public void onCancelClick() {
                        EventStatisticsUtil.event(activity, EventCommon.ABLUM_DELETE_CANCEL);

                        dissmissChoiceDialog();
                    }

                    @Override
                    public void onDismiss() {

                    }
                });
               /* mchoiceDialog.setChoiceInterface(new ChoiceDialog5.ChoiceInterface() {
                    @Override
                    public void choiceOk() {
                        dissmissChoiceDialog();
                        new Handler().post(new Runnable() {
                            @Override
                            public void run() {
                                EventStatisticsUtil.event(activity, EventCommon.ABLUM_DELETE_CONFIRM);

                                List<Integer> list = new ArrayList();
                                int count = 0;
                                for (int i = 0; i < mGirdList.size(); i++) {
                                    if (mGirdList.get(i).ischeck()) {
                                        imageDataSqlControl.startDel(model.startDel(mGirdList.get(i)));
                                        list.add(i);
                                    }
                                }
                                for (int i = list.size() - 1; i >= 0; i--) {
                                    mGirdList.remove((int) list.get(i));
                                    count++;
                                }
                                adapter.notifyDataSetChanged();
                                mView.showBackground(mGirdList);
                                Toast.makeText(activity, activity.getResources().getString(R.string.delete_success), Toast.LENGTH_LONG).show();
                                itemClickCount -= count;
                                isFooterShow = START_TYPE;
                                mView.outTopSelect();

                            }
                        });
                    }

                    @Override
                    public void choiceCencel() {
                        EventStatisticsUtil.event(activity, EventCommon.ABLUM_DELETE_CANCEL);

                        dissmissChoiceDialog();
                    }
                });*/
                popupWindow.dismiss();
                mchoiceDialog.show();
            }
        });

        ll_flashDisk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                // ((AlbumNewActivity) activity).showDialog();
                //showtitleDialog();

                List<File> fileList = new ArrayList<>();
                for (int i = 0; i < mGirdList.size(); i++) {
                    if (mGirdList.get(i).ischeck()) {
                        String localUrl = mGirdList.get(i).getImageData().getLocalUrl();
                        if (!localUrl.contains("mp4")) {
                            localUrl = mGirdList.get(i).getImageData().getSmallLocalUrl();
                            localUrl = localUrl.substring(8);
                        }
                        File f = new File(localUrl);
                        if (((AlbumNewActivity) activity).getcFolder() != null) {
                            fileList.add(f);
                        } else {
                            ToastUtil.toast("未获取到U盘设备信息");
                            break;
                        }
                    }
                }

                if (fileList.size() > 0) {
                    ((AlbumNewActivity) activity).readSDFile(fileList, ((AlbumNewActivity) activity).getcFolder());
                }

            }
        });


        // 如果不设置PopupWindow的背景，有些版本就会出现一个问题：无论是点击外部区域还是Back键都无法dismiss弹框
        popupWindow.setBackgroundDrawable(new ColorDrawable());

        // setOutsideTouchable设置生效的前提是setTouchable(true)和setFocusable(false)
        popupWindow.setOutsideTouchable(true);

        // 设置为true之后，PopupWindow内容区域 才可以响应点击事件
        popupWindow.setTouchable(true);

        // true时，点击返回键先消失 PopupWindow
        // 但是设置为true时setOutsideTouchable，setTouchable方法就失效了（点击外部不消失，内容区域也不响应事件）
        // false时PopupWindow不处理返回键
        popupWindow.setFocusable(false);
        popupWindow.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;   // 这里面拦截不到返回键
            }
        });
        // 如果希望showAsDropDown方法能够在下面空间不足时自动在anchorView的上面弹出
        // 必须在创建PopupWindow的时候指定高度，不能用wrap_content
        popupWindow.showAsDropDown(anchorView);
        return popupWindow;
    }

    private void autoAdjustArrowPos(PopupWindow popupWindow, View contentView, View anchorView) {
//        View upArrow = contentView.findViewById(R.id.up_arrow);
        View downArrow = contentView.findViewById(R.id.down_arrow);

        int pos[] = new int[2];
        contentView.getLocationOnScreen(pos);
        int popLeftPos = pos[0];
        anchorView.getLocationOnScreen(pos);
        int anchorLeftPos = pos[0];
        int arrowLeftMargin = anchorLeftPos - popLeftPos + anchorView.getWidth() / 2 - downArrow.getWidth() / 2;
//        upArrow.setVisibility(popupWindow.isAboveAnchor() ? View.INVISIBLE : View.VISIBLE);
//        downArrow.setVisibility(popupWindow.isAboveAnchor() ? View.VISIBLE : View.INVISIBLE);

//        RelativeLayout.LayoutParams upArrowParams = (RelativeLayout.LayoutParams) upArrow.getLayoutParams();
//        upArrowParams.leftMargin = arrowLeftMargin;
        RelativeLayout.LayoutParams downArrowParams = (RelativeLayout.LayoutParams) downArrow.getLayoutParams();
        downArrowParams.leftMargin = arrowLeftMargin;
        downArrow.setLayoutParams(downArrowParams);
    }


}
