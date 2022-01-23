package com.bcnetech.hyphoto.task.manage;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.bcnetech.bcnetchhttp.RetrofitUploadFactory;
import com.bcnetech.bcnetchhttp.base.BaseResult;
import com.bcnetech.bcnetchhttp.bean.data.DownloadInfoData;
import com.bcnetech.bcnetchhttp.bean.data.LightThan;
import com.bcnetech.bcnetchhttp.bean.data.ShareGlobalParms;
import com.bcnetech.bcnetchhttp.bean.data.SharePartParms;
import com.bcnetech.bcnetchhttp.bean.request.FileCheckBody;
import com.bcnetech.bcnetchhttp.bean.response.BimageUploadingChunk;
import com.bcnetech.bcnetchhttp.bean.response.Preinstail;
import com.bcnetech.bcnetchhttp.config.Flag;
import com.bcnetech.bcnetchhttp.config.UrlConstants;
import com.bcnetech.bcnetechlibrary.bean.CameraParm;
import com.bcnetech.bcnetechlibrary.util.ContentUtil;
import com.bcnetech.bcnetechlibrary.util.JsonUtil;
import com.bcnetech.bcnetechlibrary.util.LogUtil;
import com.bcnetech.bcnetechlibrary.util.ThreadPoolUtil;
import com.bcnetech.hyphoto.data.FileInfomation;
import com.bcnetech.hyphoto.data.KeyValue;
import com.bcnetech.hyphoto.data.SqlControl.BaseSqlControl;
import com.bcnetech.hyphoto.data.SqlControl.DownloadInfoSqlControl;
import com.bcnetech.hyphoto.data.SqlControl.ImageDataSqlControl;
import com.bcnetech.hyphoto.data.UploadBean;
import com.bcnetech.hyphoto.imageinterface.BizImageMangage;
import com.bcnetech.hyphoto.receiver.UploadAIReceiver;
import com.bcnetech.hyphoto.receiver.UploadManagerReceiver;
import com.bcnetech.hyphoto.sql.dao.LightRatioData;
import com.bcnetech.hyphoto.sql.dao.PictureProcessingData;
import com.bcnetech.hyphoto.sql.dao.PresetParm;
import com.bcnetech.hyphoto.task.MBaseObserver;
import com.bcnetech.hyphoto.utils.BitmapUtils;
import com.bcnetech.hyphoto.utils.FileUtil;
import com.bcnetech.hyphoto.utils.Image.FileUpload;
import com.bcnetech.hyphoto.utils.StringUtil;
import com.bcnetech.hyphoto.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by yhf on 2017/4/14.
 */
public class UploadManager {

    private Activity activity;
    private Queue<DownloadInfoData> mRunOnDraw;
    private DownloadInfoSqlControl downloadInfoSqlControl;
    private ImageDataSqlControl imageDataSqlControl;


    private int httpType = INIT;
    private boolean showDialog = false;
    private AlertDialog alertDialog;


    //wifi
    public final static int WIFI = 3;
    //移动网络
    public final static int NET = 2;
    //无网络
    public final static int NULL = 1;
    //初始状态
    public final static int INIT = 0;

    private static int connected_type;

    private boolean canRun = true;
    private int count = 0;
    public int number;
    public int sharenumber;
    //分片上传失败数
    public int failBurstCount;

    private PresetParm presetParm;

    private static UploadManager uploadManager = null;

    private UploadListener uploadListener;

    private UploadManager(final Activity activity) {
        this.activity = activity;
        this.downloadInfoSqlControl = new DownloadInfoSqlControl(activity);
        this.imageDataSqlControl = new ImageDataSqlControl(activity);
        this.mRunOnDraw = new LinkedList<>();
        init();
    }

    public static synchronized UploadManager getInstance(Activity activity) {
        if (uploadManager == null) {
            uploadManager = new UploadManager(activity);
        }
        return uploadManager;
    }

    public static synchronized void onDestroy() {
        uploadManager = null;
    }


    public static synchronized UploadManager getInstance() {
        return uploadManager;
    }

    public Queue<DownloadInfoData> getmRunOnDraw() {
        return mRunOnDraw;
    }

    public void setmRunOnDraw(Queue<DownloadInfoData> mRunOnDraw) {
        this.mRunOnDraw = mRunOnDraw;
    }

    public void setConnectedType(int type) {
        connected_type = type;
    }

    public int getConnected_type() {
        return connected_type;
    }

    public int getHttpType() {
        return httpType;
    }

    public DownloadInfoSqlControl getDownloadInfoSqlControl() {
        return downloadInfoSqlControl;
    }


    /**
     * wifi连接
     */
    public void httpWifiConnection() {
        if (httpType == WIFI) {
            return;
        } else if (httpType == NET || httpType == NULL || httpType == INIT) {
            if (uploadManager.getmRunOnDraw().isEmpty()) {
                downloadInfoSqlControl.QueryInfoAll();
            }
        }
        if(httpType == NET || httpType == NULL){
            EventBus.getDefault().post(new UploadBean(count, Flag.UPLOAD_NETCHANGE, WIFI));
        }
        httpType = WIFI;
    }

    /**
     * 移动网络
     */
    public void httpModNetConnection() {
        if (httpType == NET) {
            return;
        }
        httpType = NET;
        if (uploadManager.getmRunOnDraw() != null && !uploadManager.getmRunOnDraw().isEmpty()) {
            while (!uploadManager.getmRunOnDraw().isEmpty()) {
                uploadManager.getmRunOnDraw().poll();
            }
        }
//        EventBus.getDefault().post(new UploadBean(count, Flag.UPLOAD_NETCHANGE, httpType));
        downloadInfoSqlControl.queryCount();
    }

    /**
     * 没有连接
     */
    public void httpDisConnection() {
        if (httpType == NULL) {
            return;
        }
        httpType = NULL;
        if (uploadManager.getmRunOnDraw() != null && !uploadManager.getmRunOnDraw().isEmpty()) {
            while (!uploadManager.getmRunOnDraw().isEmpty()) {
                DownloadInfoData downloadInfoData = uploadManager.getmRunOnDraw().poll();
                downloadInfoData.setType(DownloadInfoSqlControl.CLOUD_UPLOAD_FAIL);
                downloadInfoSqlControl.startUpdateInfoById(downloadInfoData);
            }
        }
//        EventBus.getDefault().post(new UploadBean(count, Flag.UPLOAD_NETCHANGE, httpType));
        downloadInfoSqlControl.queryCount();
    }

    private void init() {
        downloadInfoSqlControl.setListener(new BaseSqlControl.SqlControlListener() {
            @Override
            public void queryListener(Object... parms) {
                if ((int) parms[1] == BaseSqlControl.QUERY_COUNT) {
                    count = (int) parms[0];
                    EventBus.getDefault().post(new UploadBean(count, Flag.UPLOAD_NETCHANGE, httpType));
                } else {

                    List<DownloadInfoData> downloadInfoDatas = (List<DownloadInfoData>) parms[0];
//                if (showDialog) {
//                    showDialog = false;
//                    if (downloadInfoDatas != null && downloadInfoDatas.size() != 0) {
//                        showDialog();
//                        return;
//                    }
//                }
                    for (int i = 0; i < downloadInfoDatas.size(); i++) {
                        DownloadInfoData data = downloadInfoDatas.get(i);
                        data.setType(DownloadInfoSqlControl.CLOUD_UPLOAD);
                        downloadInfoSqlControl.startUpdateInfoById(data);
                        uploadManager.runOnDraw(data);
                    }
                    count = mRunOnDraw.size();

                    UploadManagerReceiver.notifyModifyPreset(activity, "select");
                    //上传
                    if (count > 0) {
                        uploadManager.runTaskByIdNew(uploadManager.getmRunOnDraw());
                    }
                }
            }

            @Override
            public void insertListener(Object... parms) {

            }

            @Override
            public void deletListener(Object... parms) {
                UploadManagerReceiver.deletePreset(activity, count);
                if (count > 0) {
                    LogUtil.d("UPLOAD_UPLOADING deletListener");
                    EventBus.getDefault().post(new UploadBean(count, Flag.UPLOAD_UPLOADING, httpType));
                }
            }

            @Override
            public void upDataListener(Object... parms) {

            }
        });
    }


    public void showDialog() {
        if (alertDialog == null) {
            alertDialog = new AlertDialog.Builder(activity).setTitle(activity.getResources().getString(R.string.upload_info))//设置对话框标题
                    .setMessage(activity.getResources().getString(R.string.upload_message))//设置显示的内容
                    .setPositiveButton(activity.getResources().getString(R.string.confirm), new DialogInterface.OnClickListener() {//添加确定按钮
                        @Override
                        public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件
                            if (uploadManager.getmRunOnDraw().isEmpty()) {
                                downloadInfoSqlControl.QueryInfoAll();
                            } else {
                                //第一次上传
                                uploadManager.runTaskByIdNew(uploadManager.getmRunOnDraw());
                            }
                        }
                    }).setNegativeButton(activity.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {//添加返回按钮
                        @Override
                        public void onClick(DialogInterface dialog, int which) {//响应事件

                        }
                    }).create();//在按键响应事件中显示此对话框
            //alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        }
        alertDialog.show();
    }


    /**
     * 全部上传
     *
     * @param queue
     */
    public synchronized void runTaskByIdNew(final Queue<DownloadInfoData> queue) {
        if (queue != null && !queue.isEmpty()) {
            final DownloadInfoData downloadInfoData = queue.poll();
            File dirFile = new File(downloadInfoData.getLocalUrl().substring(7));
            if (!dirFile.exists()) {
                UploadManagerReceiver.notifyModifyPreset(activity, "upload");
                downloadInfoSqlControl.startDelInfoById(downloadInfoData);
                count--;
                if (count < 0) {
                    count = 0;
                }

                runTaskByIdNew(queue);
                return;
            }
            checkAndUpload(downloadInfoData);
        } else {
            if (count == 0) {
                EventBus.getDefault().post(new UploadBean(count, Flag.UPLOAD_SUCCESS, httpType));
                LogUtil.d("UPLOAD_SUCCESS");

            } else {
                EventBus.getDefault().post(new UploadBean(count, Flag.UPLOAD_FAIL, httpType));
                LogUtil.d("UPLOAD_FAIL");

            }
        }
    }

    public void dellAllRunTask() {
        clearMRunOnDraw();
        count = 0;
        downloadInfoSqlControl.startDelAll();
    }

    public void deleAllFail(final Queue<DownloadInfoData> queue) {
        dellFail(queue);
        downloadInfoSqlControl.startDellFail();
    }

    private List<DownloadInfoData> downloadInfoDatas;

    public void dellFail(final Queue<DownloadInfoData> queue) {
        if (queue != null && !queue.isEmpty()) {
            downloadInfoDatas = new ArrayList<>();
            final DownloadInfoData downloadInfoData = queue.poll();
            if (downloadInfoData.getType() == DownloadInfoSqlControl.CLOUD_UPLOAD_FAIL) {
                downloadInfoDatas.add(downloadInfoData);
            } else {
                count--;
                if (count < 0) {
                    count = 0;
                }
            }
            dellFail(queue);
        } else {
            if (downloadInfoDatas != null) {
                for (int i = 0; i < downloadInfoDatas.size(); i++) {
                    runOnDraw(downloadInfoDatas.get(i));
                }
                downloadInfoDatas = null;
            }
        }
    }

    public int getUploadCount() {
        return count;
    }


    public void setUploadCount(int count) {
        this.count = count;
    }

    private void clearMRunOnDraw() {
        if (!uploadManager.getmRunOnDraw().isEmpty()) {
            uploadManager.getmRunOnDraw().poll();
            clearMRunOnDraw();
        }
    }


    public synchronized void runOnDraw(final DownloadInfoData downloadInfoData) {

        if (mRunOnDraw == null) {
            mRunOnDraw = new LinkedList<DownloadInfoData>();
        }
        mRunOnDraw.add(downloadInfoData);

    }

    /**
     * 云图库上传图片
     *
     * @param path
     * @param coverId
     * @param replaceId
     * @param downloadInfoData
     * @param isPic
     * @param code
     */
    public void uploadPicFile(final String path, final String coverId, final String replaceId, final DownloadInfoData downloadInfoData, final boolean isPic, final String code,String title) {

        //check
        final FileCheckBody fileCheckBody = FileUpload.fileUploadInfoCheckBody(path, FileUpload.fileUploadCheck(path, title));
        fileCheckBody.setCode(code);
        fileCheckBody.setScope("1");
        fileCheckBody.setFileId(coverId);
        if (!TextUtils.isEmpty(replaceId))
            fileCheckBody.setReplaceId(replaceId);

        RetrofitUploadFactory.getUPloadInstence().API().fileCheck(fileCheckBody)

                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MBaseObserver<BimageUploadingChunk>(activity, false) {
                    @Override
                    protected void onSuccees(BaseResult<BimageUploadingChunk> t) throws Exception {
                        //分片上传
                        //部分分片未上传
                        if (t.getCode() == 20014) {
                            number = 0;
                            failBurstCount = 0;
                            String message = JSON.toJSONString(t.getData());
                            final BimageUploadingChunk bimageUploadingChunkList = JSON.parseObject(message, BimageUploadingChunk.class);
                            //
                            io.reactivex.Observable[] observablesArray = getObservableArray(bimageUploadingChunkList, path, code, t.getData().getFileId(), coverId,fileCheckBody.getName());
                            io.reactivex.Observable.mergeArray(observablesArray)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new MBaseObserver<Object>(activity, false) {
                                        @Override
                                        protected void onSuccees(BaseResult<Object> t) throws Exception {

                                            number = number + 1;
                                            if (number == bimageUploadingChunkList.getList().size()) {
                                                //上传图片成功
                                                if (isPic) {
                                                    downloadInfoData.setFileId(bimageUploadingChunkList.getFileId());
                                                    downloadInfoData.setCatalogId(bimageUploadingChunkList.getCatalogId());
                                                }
                                                //上传参数
                                                setFileUpdateInfo(downloadInfoData, replaceId);
                                            }

                                        }

                                        @Override
                                        protected void onCodeError(BaseResult<Object> t) throws Exception {
                                            failBurstCount = failBurstCount + 1;
                                            if (failBurstCount + number == bimageUploadingChunkList.getList().size()) {
                                                downloadInfoData.setType(DownloadInfoSqlControl.CLOUD_UPLOAD_FAIL);
                                                downloadInfoSqlControl.startUpdateInfoById(downloadInfoData);
                                                runTaskByIdNew(uploadManager.getmRunOnDraw());
                                                UploadManagerReceiver.notifyModifyPreset(activity, "select");
                                            }
                                        }

                                        @Override
                                        protected void onFailure(Throwable e, boolean isNetWorkError) throws Exception {
                                            downloadInfoData.setType(DownloadInfoSqlControl.CLOUD_UPLOAD_FAIL);
                                            downloadInfoSqlControl.startUpdateInfoById(downloadInfoData);
                                            runTaskByIdNew(uploadManager.getmRunOnDraw());
                                            UploadManagerReceiver.notifyModifyPreset(activity, "select");
                                        }
                                    });
                            //图片已经上传
                        } else if (t.getCode() == 20018 || t.getCode() == 20009) {
                            String message = JSON.toJSONString(t.getData());
                            FileInfomation bimageUploadingChunk = JSON.parseObject(message, FileInfomation.class);

                            if (isPic) {
                                downloadInfoData.setFileId(bimageUploadingChunk.getFileId());
                                downloadInfoData.setCatalogId(bimageUploadingChunk.getCatalogId());
                            }
                            //上传参数
                            setFileUpdateInfo(downloadInfoData, replaceId);

                        }

                    }

                    @Override
                    protected void onCodeError(BaseResult<BimageUploadingChunk> t) throws Exception {
                        if (t.getCode() == 20008) {
                            downloadInfoData.setType(DownloadInfoSqlControl.CLOUD_UPLOAD_FAIL);
                            downloadInfoSqlControl.startUpdateInfoById(downloadInfoData);
                            runTaskByIdNew(uploadManager.getmRunOnDraw());
                            UploadManagerReceiver.notifyModifyPreset(activity, "select");

                        } else if (t.getCode() == 20022) {
                            downloadInfoData.setType(DownloadInfoSqlControl.CLOUD_UPLOAD_FAIL);
                            downloadInfoSqlControl.startUpdateInfoById(downloadInfoData);

                            while (!uploadManager.getmRunOnDraw().isEmpty()) {
                                DownloadInfoData downloadInfoData = uploadManager.getmRunOnDraw().poll();
                                downloadInfoData.setType(DownloadInfoSqlControl.CLOUD_UPLOAD_FAIL);
                                downloadInfoSqlControl.startUpdateInfoById(downloadInfoData);
                            }
                            EventBus.getDefault().post(new UploadBean(count, Flag.UPLOAD_FULL, httpType));

                        } else if (t.getCode() == 20026) {
                            downloadInfoSqlControl.startDelInfoById(downloadInfoData);
                            UploadManagerReceiver.notifyModifyPreset(activity, "select");
                            runTaskByIdNew(uploadManager.getmRunOnDraw());
                        }
                    }

                    @Override
                    protected void onFailure(Throwable e, boolean isNetWorkError) throws Exception {
                        downloadInfoData.setType(DownloadInfoSqlControl.CLOUD_UPLOAD_FAIL);
                        downloadInfoSqlControl.startUpdateInfoById(downloadInfoData);
                        runTaskByIdNew(uploadManager.getmRunOnDraw());
                        UploadManagerReceiver.notifyModifyPreset(activity, "select");
                    }
                });
    }

    /**
     * 云图库上传视频
     *
     * @param path
     * @param coverIdPath
     * @param coverId
     * @param downloadInfoData
     * @param code
     */
    public void uploadVideoFile(final String path, final String coverIdPath, final String coverId, final DownloadInfoData downloadInfoData, final String code, final String title) {
        //check
        final FileCheckBody fileCheckBody = FileUpload.fileUploadInfoCheckBody(path, FileUpload.fileUploadCheck(path, title));
        fileCheckBody.setCode(code);
        fileCheckBody.setScope("1");
        fileCheckBody.setFileId(coverId);
        RetrofitUploadFactory.getUPloadInstence().API().fileCheck(fileCheckBody)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MBaseObserver<BimageUploadingChunk>(activity, false) {
                    @Override
                    protected void onSuccees(BaseResult<BimageUploadingChunk> t) throws Exception {
                        if (t.getCode() == 20014) {
                            number = 0;
                            failBurstCount = 0;
                            String message = JSON.toJSONString(t.getData());
                            final BimageUploadingChunk bimageUploadingChunkList = JSON.parseObject(message, BimageUploadingChunk.class);
                            io.reactivex.Observable[] observablesArray = getObservableArray(bimageUploadingChunkList, path, code, t.getData().getFileId(), coverId,fileCheckBody.getName());
                            io.reactivex.Observable.mergeArray(observablesArray)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new MBaseObserver<Object>(activity, false) {
                                        @Override
                                        protected void onSuccees(BaseResult<Object> t) throws Exception {


                                            number = number + 1;
                                            if (number == bimageUploadingChunkList.getList().size()) {
//                                      //上传封面
                                                downloadInfoData.setFileId(bimageUploadingChunkList.getFileId());
                                                downloadInfoData.setCatalogId(bimageUploadingChunkList.getCatalogId());
                                                uploadPicFile(coverIdPath, bimageUploadingChunkList.getFileId(), "", downloadInfoData, false, "cover",title);
                                            }

                                        }

                                        @Override
                                        protected void onCodeError(BaseResult<Object> t) throws Exception {
                                            failBurstCount = failBurstCount + 1;
                                            if (failBurstCount + number == bimageUploadingChunkList.getList().size()) {
                                                downloadInfoData.setType(DownloadInfoSqlControl.CLOUD_UPLOAD_FAIL);
                                                downloadInfoSqlControl.startUpdateInfoById(downloadInfoData);
                                                runTaskByIdNew(uploadManager.getmRunOnDraw());
                                                UploadManagerReceiver.notifyModifyPreset(activity, "select");
                                            }
                                        }

                                        @Override
                                        protected void onFailure(Throwable e, boolean isNetWorkError) throws Exception {
                                            downloadInfoData.setType(DownloadInfoSqlControl.CLOUD_UPLOAD_FAIL);
                                            downloadInfoSqlControl.startUpdateInfoById(downloadInfoData);
                                            runTaskByIdNew(uploadManager.getmRunOnDraw());
                                            UploadManagerReceiver.notifyModifyPreset(activity, "select");
                                        }
                                    });
                            //视频已经上传
                        } else if (t.getCode() == 20018 || t.getCode() == 20009)

                        {
                            String message = JSON.toJSONString(t.getData());
                            FileInfomation bimageUploadingChunk = JSON.parseObject(message, FileInfomation.class);
                            if (bimageUploadingChunk != null) {
                                downloadInfoData.setFileId(bimageUploadingChunk.getFileId());
                                downloadInfoData.setCatalogId(bimageUploadingChunk.getCatalogId());
                                uploadPicFile(coverIdPath, downloadInfoData.getFileId(), "", downloadInfoData, false, "cover",title);
                            }
                        }
                    }

                    @Override
                    protected void onCodeError(BaseResult<BimageUploadingChunk> t) throws Exception {
                        if (t.getCode() == 20008) {
                            downloadInfoData.setType(DownloadInfoSqlControl.CLOUD_UPLOAD_FAIL);
                            downloadInfoSqlControl.startUpdateInfoById(downloadInfoData);
                            runTaskByIdNew(uploadManager.getmRunOnDraw());
                            UploadManagerReceiver.notifyModifyPreset(activity, "select");

                        } else if (t.getCode() == 20022) {
                            downloadInfoData.setType(DownloadInfoSqlControl.CLOUD_UPLOAD_FAIL);
                            downloadInfoSqlControl.startUpdateInfoById(downloadInfoData);

                            while (!uploadManager.getmRunOnDraw().isEmpty()) {
                                DownloadInfoData downloadInfoData = uploadManager.getmRunOnDraw().poll();
                                downloadInfoData.setType(DownloadInfoSqlControl.CLOUD_UPLOAD_FAIL);
                                downloadInfoSqlControl.startUpdateInfoById(downloadInfoData);
                            }
                            EventBus.getDefault().post(new UploadBean(count, Flag.UPLOAD_FULL, httpType));

                        } else if (t.getCode() == 20026) {
                            downloadInfoSqlControl.startDelInfoById(downloadInfoData);
                            UploadManagerReceiver.notifyModifyPreset(activity, "select");
                            runTaskByIdNew(uploadManager.getmRunOnDraw());
                        }
                    }

                    @Override
                    protected void onFailure(Throwable e, boolean isNetWorkError) throws Exception {
                        downloadInfoData.setType(DownloadInfoSqlControl.CLOUD_UPLOAD_FAIL);
                        downloadInfoSqlControl.startUpdateInfoById(downloadInfoData);
                        runTaskByIdNew(uploadManager.getmRunOnDraw());
                        UploadManagerReceiver.notifyModifyPreset(activity, "select");
                    }
                });
    }

    /**
     * 获取分片上传observable数组
     *
     * @param bimageUploadingChunkList
     * @param path
     * @param code
     * @param fileId
     * @param coverId
     * @return
     */
    private synchronized io.reactivex.Observable[] getObservableArray(BimageUploadingChunk bimageUploadingChunkList, String path, String code, String fileId, String coverId,String title) {
        io.reactivex.Observable[] observablesArray = new io.reactivex.Observable[bimageUploadingChunkList.getList().size()];
        for (int i = 0; null != bimageUploadingChunkList.getList() && i < bimageUploadingChunkList.getList().size(); i++) {
            BimageUploadingChunk.BimageUploadingChunkList bimageUploadingChunk = bimageUploadingChunkList.getList().get(i);
            Map<String, String> mapParam = FileUpload.fileUploaInfo(path);
            mapParam.put("fileId", fileId);
            mapParam.put("chunkSha1", bimageUploadingChunk.getChunkSha1());
            mapParam.put("chunkSize", bimageUploadingChunk.getChunkSize());
            mapParam.put("code", code);
            mapParam.put("name",title);
            if (!TextUtils.isEmpty(coverId))
                mapParam.put("coverId", coverId);
            String url = getUrl(mapParam, UrlConstants.DEFAUL_WEB_SITE_UPLOAD, UrlConstants.FILE_UPLOAD);
            StringBuffer sb = new StringBuffer();
            sb.append(Flag.FENPIAN);

            sb.append(mapParam.get("fileName"));
            sb.append("_data" + (bimageUploadingChunk.getChunkIndex()));

            File file = new File(sb.toString());
            MultipartBody.Builder builder = new MultipartBody.Builder();
            builder.setType(MultipartBody.FORM);

            builder.addFormDataPart("file", file.getName(), RequestBody.create(MediaType.parse("multipart/form-data"), file));
            io.reactivex.Observable observable = RetrofitUploadFactory.getUPloadInstence().API().uploadFile(url, builder.build());
            observablesArray[i] = observable;
        }

        return observablesArray;
    }

    /**
     * 云图库上传参数
     *
     * @param downloadInfoData
     * @param replaceId
     */
    public void setFileUpdateInfo(final DownloadInfoData downloadInfoData, final String replaceId) {
        HashMap<String, Object> parms = new HashMap<String, Object>();

        List<KeyValue> postList;
        String preset = "";
        if (downloadInfoData != null && !StringUtil.isBlank(downloadInfoData.getPostParms())) {
            postList = JsonUtil.Json2List(downloadInfoData.getPostParms(), KeyValue.class);
            for (int i = 0; i < postList.size(); i++) {
                parms.put(postList.get(i).getKey(), postList.get(i).getValue());
            }
        }

        parms.put("fileId", downloadInfoData.getFileId());
        parms.put("catalogId", downloadInfoData.getCatalogId());
        RetrofitUploadFactory.getUPloadInstence().API().uploadInfo(parms)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MBaseObserver<Object>(activity, false) {
                    @Override
                    protected void onSuccees(BaseResult<Object> t) throws Exception {
                        count--;
                        if (count < 0) {
                            count = 0;
                        }
                        UploadManagerReceiver.notifyModifyPreset(activity, "upload");
                        if(downloadInfoData.getLocalUrl().contains("zip")){
                            List<KeyValue> fileParms = JsonUtil.Json2List(downloadInfoData.getPostFileParms(), KeyValue.class);
                            String url=fileParms.get(0).getValue();
                            imageDataSqlControl.startUpdateByLocalUrl(url.substring(7), downloadInfoData.getFileId(), FileUpload.getFileHash(downloadInfoData.getLocalUrl().substring(7)));
                            UploadAIReceiver.notifyModifyUsername(activity ,url.substring(7),downloadInfoData.getFileId(),FileUpload.getFileHash(downloadInfoData.getLocalUrl().substring(7)));

                        }else {
                            imageDataSqlControl.startUpdateByLocalUrl(downloadInfoData.getLocalUrl(), downloadInfoData.getFileId(), FileUpload.getFileHash(downloadInfoData.getLocalUrl().substring(7)));
                        }

                        if (downloadInfoData.getFileType() == Flag.TYPE_PIC) {
                            if (!StringUtil.isBlank(replaceId)) {
                                final String url = StringUtil.getSizeUrl(BitmapUtils.getBitmapUrl6(replaceId), (ContentUtil.getScreenWidth(activity) - ContentUtil.dip2px(activity, 3)) / 3, (ContentUtil.getScreenWidth(activity) - ContentUtil.dip2px(activity, 3)) / 3);

                                Picasso.get()
                                        .load(url)
                                        .memoryPolicy(MemoryPolicy.NO_CACHE)
                                        .networkPolicy(NetworkPolicy.OFFLINE)
                                        .fetch(new Callback() {
                                            @Override
                                            public void onSuccess() {
                                                //Success: mean disk cache exist -> should do actual fetch
                                                //网络中重新获取一次
                                                Picasso.get()
                                                        .load(url)
                                                        .memoryPolicy(MemoryPolicy.NO_CACHE)
                                                        .networkPolicy(NetworkPolicy.NO_CACHE).fetch();
                                                //刷新内存缓存
                                                Picasso.get().invalidate(url);
                                            }


                                            @Override
                                            public void onError(Exception e) {

                                            }
                                        });
                            }
                        }
                        downloadInfoSqlControl.startDelInfoById(downloadInfoData);
                        runTaskByIdNew(uploadManager.getmRunOnDraw());
                    }

                    @Override
                    protected void onFailure(Throwable e, boolean isNetWorkError) throws Exception {
                        downloadInfoData.setType(DownloadInfoSqlControl.CLOUD_UPLOAD_FAIL);
                        downloadInfoSqlControl.startUpdateInfoById(downloadInfoData);
                        runTaskByIdNew(uploadManager.getmRunOnDraw());
                        UploadManagerReceiver.notifyModifyPreset(activity, "select");
                    }


                    @Override
                    protected void onCodeError(BaseResult<Object> t) throws Exception {
                        downloadInfoData.setType(DownloadInfoSqlControl.CLOUD_UPLOAD_FAIL);
                        downloadInfoSqlControl.startUpdateInfoById(downloadInfoData);
                        runTaskByIdNew(uploadManager.getmRunOnDraw());
                        UploadManagerReceiver.notifyModifyPreset(activity, "select");
                    }
                });

    }

    /**
     * 分享参数：分片上传图片参数文件
     *
     * @param type
     * @param fileId
     * @param localUrl
     * @param presetParm
     * @param isMatting
     * @param smallParamUrl
     * @param paramsName
     */
    public void shareParams(final int type, final String fileId, String localUrl, final PresetParm presetParm, boolean isMatting, String smallParamUrl, String paramsName) {
        setPrinstall(type, fileId, presetParm, presetParm.getTextSrc(), localUrl, isMatting, smallParamUrl);
    }

    private void setPrarms(Preinstail shareParmsData, final int type, final String fileId, String localUrl, boolean isMatting, String smallParamUrl) {
        sharenumber = 0;
        final String code_pic = "cover";
        final String code_param = type == 1 ? "light_data_person" : "light_data_market";
        String parmsJson = JsonUtil.Object2Json(shareParmsData);
        final byte[] byteArray = parmsJson.getBytes();
        final String paramUrl = FileUpload.createFile(byteArray, System.currentTimeMillis() + "");

        final FileCheckBody fileCheckBody = FileUpload.fileUploadInfoCheckBody(paramUrl, FileUpload.fileUploadCheck(paramUrl, ""));
        fileCheckBody.setCode(code_param);
        fileCheckBody.setScope("1");
        if (!StringUtil.isBlank(fileId)) {
            fileCheckBody.setFileId(fileId);
        }
        FileUpload.fileUploaInfo(paramUrl);
        //check接口
        RetrofitUploadFactory.getUPloadInstence().API().fileCheck(fileCheckBody)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MBaseObserver<BimageUploadingChunk>(activity, false) {
                    @Override
                    protected void onSuccees(final BaseResult<BimageUploadingChunk> checkResult) throws Exception {

                        //文件未上传
                        if (checkResult.getCode() == 20014) {
                            String message = JSON.toJSONString(checkResult.getData());
                            final BimageUploadingChunk bimageUploadingChunkList = JSON.parseObject(message, BimageUploadingChunk.class);
                            //分片上传参数文件接口
                            io.reactivex.Observable[] observablesArray = getObservableArray(bimageUploadingChunkList, paramUrl, code_param, checkResult.getData().getFileId(), null,fileCheckBody.getName());
                            io.reactivex.Observable.mergeArray(observablesArray)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new MBaseObserver(activity, false) {
                                        @Override
                                        protected void onSuccees(BaseResult uploadresult) throws Exception {
                                            super.onSuccees(uploadresult);

                                            //分片上传成功
                                            if (uploadresult.isSuccess()) {
                                                sharenumber = sharenumber + 1;
                                                if (sharenumber == bimageUploadingChunkList.getList().size()) {
                                                    //上传参数文件成功
                                                    //分片上传图片文件接口
                                                    sharePic(type, presetParm.getTextSrc(), code_pic, checkResult.getData().getFileId());
                                                }
                                                //分片上传失败
                                            } else {
                                                uploadListener.onUploadFaile();
                                            }
                                        }

                                        @Override
                                        protected void onCodeError(BaseResult t) throws Exception {
                                            super.onCodeError(t);
                                            uploadListener.onUploadFaile();
                                        }

                                        @Override
                                        protected void onTokenError(BaseResult t) throws Exception {
                                            super.onTokenError(t);
                                            uploadListener.onUploadFaile();
                                        }
                                    });
                            //文件已上传
                        } else if (checkResult.getCode() == 20018) {
                            String message = JSON.toJSONString(checkResult.getData());
                            //参数信息
                            final FileInfomation parmInfomation = JSON.parseObject(message, FileInfomation.class);
                            //分片上传图片文件接口
                            sharePic(type, presetParm.getTextSrc(), code_pic, parmInfomation.getFileId());
                        } else {
                            uploadListener.onUploadFaile();
                        }

                    }

                    @Override
                    protected void onCodeError(BaseResult<BimageUploadingChunk> t) throws Exception {
                        uploadListener.onUploadFaile();
                    }

                    @Override
                    protected void onFailure(Throwable e, boolean isNetWorkError) throws Exception {
                        uploadListener.onUploadFaile();
                    }
                });
    }

    /**
     * 分享参数：分片上传图片文件
     *
     * @param type
     * @param path
     * @param code
     * @param fileId
     */
    private void sharePic(final int type, String path, final String code, final String fileId) {
        sharenumber = 0;
        Bitmap bit = BitmapFactory.decodeFile(path.substring(7));
        if (bit != null) {
            try {
                final String bitmappath = FileUtil.saveBitmap3(bit, "test");

                final FileCheckBody fileCheckBody = FileUpload.fileUploadInfoCheckBody(bitmappath, FileUpload.fileUploadCheck(bitmappath, ""));
                fileCheckBody.setCode(code);
                fileCheckBody.setScope("1");
                if (!StringUtil.isBlank(fileId)) {
                    fileCheckBody.setFileId(fileId);
                }
                //分享图片check接口
                RetrofitUploadFactory.getUPloadInstence().API().fileCheck(fileCheckBody)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new MBaseObserver<BimageUploadingChunk>(activity, false) {
                            @Override
                            protected void onSuccees(final BaseResult<BimageUploadingChunk> checkResult) throws Exception {
                                //分享图片未上传
                                if (checkResult.getCode() == 20014) {
                                    String message = JSON.toJSONString(checkResult.getData());
                                    final BimageUploadingChunk bimageUploadingChunkList = JSON.parseObject(message, BimageUploadingChunk.class);
                                    ////分享图片分片上传接口
                                    io.reactivex.Observable[] observablesArray = getObservableArray(bimageUploadingChunkList, bitmappath, code, checkResult.getData().getFileId(), null,fileCheckBody.getName());
                                    io.reactivex.Observable.mergeArray(observablesArray)
                                            .subscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe(new MBaseObserver<Object>(activity, false) {
                                                @Override
                                                protected void onSuccees(BaseResult<Object> uploadResult) throws Exception {

                                                    //分片上传成功
                                                    if (uploadResult.isSuccess()) {
                                                        sharenumber = sharenumber + 1;
                                                        if (sharenumber == bimageUploadingChunkList.getList().size()) {
                                                            //上传图片成功
                                                          /*  if (null != checkResult.getData() && (uploadResult.getCode() == 20017 || uploadResult.getCode() == 20009 || uploadResult.getCode() == 20014)) {
                                                                String messageFile = JSON.toJSONString(checkResult.getData());
                                                                //图片信息
                                                                final FileInfomation fileInfomation = JSON.parseObject(messageFile, FileInfomation.class);
*/

                                                            if (type == 1) {
                                                                presetParm.setPresetId(fileId);
                                                                presetParm.setCategoryId(checkResult.getData().getCatalogId());
                                                                presetParm.setCoverId(checkResult.getData().getFileId());
                                                            }
                                                            HashMap<String, Object> hashMap = uploadParamsMap(fileId, presetParm, checkResult.getData().getCatalogId());
                                                            RetrofitUploadFactory.getUPloadInstence().API().uploadInfo(hashMap)
                                                                    .subscribeOn(Schedulers.io())
                                                                    .observeOn(AndroidSchedulers.mainThread())
                                                                    .subscribe(new MBaseObserver<Object>(activity, false) {
                                                                        @Override
                                                                        protected void onSuccees(BaseResult<Object> t) throws Exception {
                                                                            uploadListener.onUploadSuccess();
                                                                        }

                                                                        @Override
                                                                        protected void onCodeError(BaseResult<Object> t) throws Exception {
                                                                            uploadListener.onUploadFaile();
                                                                        }

                                                                        @Override
                                                                        protected void onFailure(Throwable e, boolean isNetWorkError) throws Exception {
                                                                            uploadListener.onUploadFaile();
                                                                        }
                                                                    });

                                                        }
                                                        //分片上传失败
                                                    } else {
                                                        uploadListener.onUploadFaile();
                                                    }
                                                }

                                                @Override
                                                protected void onCodeError(BaseResult<Object> t) throws Exception {
                                                    uploadListener.onUploadFaile();
                                                }

                                                @Override
                                                protected void onFailure(Throwable e, boolean isNetWorkError) throws Exception {
                                                    uploadListener.onUploadFaile();
                                                }
                                            });
                                } else if (checkResult.getCode() == 20009 || checkResult.getCode() == 20018) {
                                    //图片信息
                                    if (type == 1) {
                                        presetParm.setPresetId(fileId);
                                        presetParm.setCategoryId(checkResult.getData().getCatalogId());
                                        presetParm.setCoverId(checkResult.getData().getFileId());
                                    }
                                    HashMap<String, Object> hashMap = uploadParamsMap(fileId, presetParm, checkResult.getData().getCatalogId());
                                    RetrofitUploadFactory.getUPloadInstence().API().uploadInfo(hashMap)
                                            .subscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe(new MBaseObserver<Object>(activity, false) {
                                                @Override
                                                protected void onSuccees(BaseResult<Object> t) throws Exception {
                                                    uploadListener.onUploadSuccess();
                                                }

                                                @Override
                                                protected void onCodeError(BaseResult<Object> t) throws Exception {
                                                    uploadListener.onUploadFaile();
                                                }

                                                @Override
                                                protected void onFailure(Throwable e, boolean isNetWorkError) throws Exception {
                                                    uploadListener.onUploadFaile();
                                                }
                                            });


                                } else {
                                    uploadListener.onUploadFaile();
                                }

                            }

                            @Override
                            protected void onCodeError(BaseResult<BimageUploadingChunk> t) throws Exception {
                                uploadListener.onUploadFaile();
                            }

                            @Override
                            protected void onFailure(Throwable e, boolean isNetWorkError) throws Exception {
                                uploadListener.onUploadFaile();
                            }
                        });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


    private class PerinstallCallable implements Callable<Bitmap> {
        private String localUrl;

        @Override
        public synchronized Bitmap call() throws Exception {
            return Picasso.get().load(localUrl).get();
        }
    }

    private Bitmap getBitmapAsync(String localUrl) throws Exception {
        PerinstallCallable callable = new PerinstallCallable();
        callable.localUrl = localUrl;
        //1.执行 Callable 方式，需要 FutureTask 实现类的支持，用于接收运算结果。
        FutureTask<Bitmap> result = new FutureTask<>(callable);
        ThreadPoolUtil.execute(result);
        //2.接收线程运算后的结果
        Bitmap bitmap = result.get(3000, TimeUnit.MILLISECONDS);  //FutureTask 可用于 闭锁 类似于CountDownLatch的作用，在所有的线程没有执行完成之后这里是不会执行的
        if (result.isDone())
            result.cancel(false);
        return bitmap;
    }


    private void setPrinstall(final int type, final String fileId, final PresetParm parms, final String url, final String localUrl, final boolean isMatting, final String smallParamUrl) {
        this.presetParm = parms;
        Observable.create(new ObservableOnSubscribe<Preinstail>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Preinstail> e) throws Exception {
                e.onNext(EditShareData(parms, url, localUrl, isMatting, smallParamUrl));
                e.onComplete();
            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Preinstail>() {
                    Disposable disposable;

                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onNext(Preinstail preinstail) {
                        setPrarms(preinstail, type, fileId, localUrl, isMatting, smallParamUrl);
                        disposable.dispose();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }

                });
    }

    private Preinstail EditShareData(PresetParm parms, String url, String localUrl, boolean isMatting, String smallParamUrl) {
        long timestamp = parms.getTimeStamp();
        String equipment = parms.getEquipment();
        List<PictureProcessingData> mparms = new ArrayList<>();
        if (null != parms.getParmlists()) {
            for (int i = 0; i < parms.getParmlists().size(); i++) {
                PictureProcessingData data = new PictureProcessingData();
                data.setType(parms.getParmlists().get(i).getType());
                data.setNum(parms.getParmlists().get(i).getNum() - 100);
                mparms.add(data);
//                mparms.get(i).setNum(parms.getParmlists().get(i).getNum() - 100);
//                mparms.get(i).setType(parms.getParmlists().get(i).getType());
            }
        }

//                parms.getParmlists();
        List<PictureProcessingData> mpartparms = new ArrayList<>();

        if (null != parms.getPartParmlists()) {
            for (int i = 0; i < parms.getPartParmlists().size(); i++) {
                PictureProcessingData data = new PictureProcessingData();
                data.setType(parms.getPartParmlists().get(i).getType());
                data.setNum(parms.getPartParmlists().get(i).getNum() - 100);
                data.setSmallUrl(parms.getPartParmlists().get(i).getSmallUrl());
                data.setImageUrl(parms.getPartParmlists().get(i).getImageUrl());
                mpartparms.add(data);
//                mparms.get(i).setNum(parms.getParmlists().get(i).getNum() - 100);
//                mparms.get(i).setType(parms.getParmlists().get(i).getType());
            }
        }

        Preinstail shareParmsData = new Preinstail();

        shareParmsData.setDevice(equipment);
        shareParmsData.setImageDate(timestamp);
        shareParmsData.setName(parms.getName());
        shareParmsData.setAutherName(parms.getAuther());
        shareParmsData.setDes(parms.getDescribe());
        shareParmsData.setID(parms.getId());
        shareParmsData.setFileName(parms.getTextSrc());
        int w = Integer.valueOf(parms.getImageWidth());
        int h = Integer.valueOf(parms.getImageHeight());
        String size = FileUtil.getFileOrFilesSize(url.substring(7), FileUtil.SIZETYPE_MB);
        shareParmsData.setDataSize(size + " MB");
        parms.setSize(size + " MB");
        shareParmsData.setSize(w + "x" + h + " px");
        char c = ',';
        String Lasbel = listToString(parms.getLabels(), c);
        shareParmsData.setLabel(Lasbel);
        ArrayList<ShareGlobalParms> globallist = new ArrayList<ShareGlobalParms>();
        ArrayList<SharePartParms> partlist = new ArrayList<SharePartParms>();

        LightRatioData lightRatioData = parms.getLightRatioData();
        LightThan lightThan = new LightThan();
        lightThan.setLeftValue(lightRatioData.getLeftLight());
        lightThan.setMoveValue2Value(lightRatioData.getTopLight());
        lightThan.setAuxiliaryValue(lightRatioData.getMoveLight());
        lightThan.setRightValue(lightRatioData.getRightLight());
        lightThan.setBackValue(lightRatioData.getBackgroudLight());
        lightThan.setBottomValue(lightRatioData.getBottomLight());
        lightThan.setName(lightRatioData.getName());
        lightThan.setVersion(lightRatioData.getVersion());
        shareParmsData.setLightThan(lightThan);
        shareParmsData.setSystem("android");
        CameraParm cameraParm = parms.getCameraParm();
        if (cameraParm != null) {
            shareParmsData.setCameraParm(cameraParm);
        }

        if (mparms != null && mparms.size() > 0) {
            for (int i = 0; i < mparms.size(); i++) {
                ShareGlobalParms shareGlobalParms = new ShareGlobalParms();
                shareGlobalParms.setType(toAnotherName(mparms.get(i).getType()));
                shareGlobalParms.setNum(mparms.get(i).getNum());
                globallist.add(shareGlobalParms);
            }
            shareParmsData.setGlobalArray(globallist);
        }

        if (mpartparms != null) {

            //原图
            SharePartParms shareSrcPartParms = new SharePartParms();
            shareSrcPartParms.setType(toAnotherName(BizImageMangage.SRC));
            shareSrcPartParms.setImagePath(url.substring(7));
            try {

                Bitmap myBitmap = getBitmapAsync(localUrl);
                if (localUrl.contains("png")) {
                    if (isMatting) {
                        myBitmap = BitmapUtils.compress(mergeBitmap(myBitmap));
                    } else {
                        myBitmap = BitmapUtils.compress(myBitmap);
                    }
                } else {
                    myBitmap = BitmapUtils.compress(myBitmap);
                }
                String localBase64 = BitmapUtils.bitmapToString100(myBitmap);
                shareSrcPartParms.setImageData(localBase64);
                partlist.add(shareSrcPartParms);

                //本地保存
                mpartparms.add(new PictureProcessingData(BizImageMangage.SRC, shareSrcPartParms.getImagePath(), shareSrcPartParms.getImageData()));

            } catch (Exception e) {
                LogUtil.d(e.getMessage());
            }

            //全局调整结果图
            if (mparms != null && mparms.size() > 0) {
                if (!StringUtil.isBlank(smallParamUrl) && !"null".equals(smallParamUrl)) {
                    SharePartParms shareParamPartParms = new SharePartParms();
                    shareParamPartParms.setType(toAnotherName(BizImageMangage.PARMS));
                    shareParamPartParms.setImagePath(smallParamUrl);
                    try {
                        //   Bitmap myBitmap =  Picasso.get().load(smallParamUrl).get();
                        Bitmap myBitmap = getBitmapAsync(smallParamUrl);
                        String localBase64 = BitmapUtils.bitmapToString100(myBitmap);
                        shareParamPartParms.setImageData(localBase64);
                        partlist.add(shareParamPartParms);

                        //本地保存
                        mpartparms.add(new PictureProcessingData(BizImageMangage.PARMS, shareParamPartParms.getImagePath(), shareParamPartParms.getImageData()));

                    } catch (Exception e) {

                    }
                }

            }

            //步骤图
            for (int i = 0; i < mpartparms.size(); i++) {
                SharePartParms sharePartParms = new SharePartParms();
                if (!StringUtil.isBlank(mpartparms.get(i).getSmallUrl()) && !"null".equals(mpartparms.get(i).getSmallUrl())) {
                    sharePartParms.setType(toAnotherName(mpartparms.get(i).getType()));
                    sharePartParms.setImagePath(mpartparms.get(i).getImageUrl());

                    try {
                        //Bitmap myBitmap = Picasso.get().load(mpartparms.get(i).getSmallUrl()).get();
                        Bitmap myBitmap = getBitmapAsync(mpartparms.get(i).getSmallUrl());
                       /* Bitmap myBitmap = Glide.with(activity)
                                .load(mpartparms.get(i).getSmallUrl())
                                .asBitmap()
                                .fitCenter()
                                .into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                                .get();*/
                        String base64 = BitmapUtils.bitmapToString100(myBitmap);
                        sharePartParms.setImageData(base64);
                        mpartparms.get(i).setImageData(base64);
                        partlist.add(sharePartParms);
                    } catch (Exception e) {

                    }
                }
                shareParmsData.setPartArray(partlist);

                //本地保存
                parms.setPartParmlists(mpartparms);
            }
        } else {
            mpartparms = new ArrayList<>();

            SharePartParms shareSrcPartParms = new SharePartParms();
            shareSrcPartParms.setType(toAnotherName(BizImageMangage.SRC));
            shareSrcPartParms.setImagePath(localUrl);
            try {
                // Bitmap myBitmap = Picasso.get().load(localUrl).get();
                Bitmap myBitmap = getBitmapAsync(localUrl);
                /*Bitmap myBitmap = Glide.with(activity)
                        .load(localUrl)
                        .asBitmap()
                        .fitCenter()
                        .into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                        .get();*/
                if (localUrl.contains("png")) {
                    if (isMatting) {
                        myBitmap = BitmapUtils.compressImage(mergeBitmap(myBitmap));
                    } else {
                        myBitmap = BitmapUtils.compressImage(myBitmap);
                    }
                } else {
                    myBitmap = BitmapUtils.compressImage(myBitmap);
                }
                String localBase64 = BitmapUtils.bitmapToString100(myBitmap);
                shareSrcPartParms.setImageData(localBase64);

                mpartparms.add(new PictureProcessingData(BizImageMangage.SRC, shareSrcPartParms.getImagePath(), shareSrcPartParms.getImageData()));
                partlist.add(shareSrcPartParms);

            } catch (Exception e) {

            }


            //全局调整结果图
            if (mparms != null && mparms.size() > 0) {
                if (!StringUtil.isBlank(smallParamUrl) && !"null".equals(smallParamUrl)) {
                    SharePartParms shareParamPartParms = new SharePartParms();
                    shareParamPartParms.setType(toAnotherName(BizImageMangage.PARMS));
                    shareParamPartParms.setImagePath(smallParamUrl);
                    try {
                        // Bitmap myBitmap = Picasso.get().load(smallParamUrl).get();
                        Bitmap myBitmap = getBitmapAsync(smallParamUrl);
                       /* Bitmap myBitmap = Glide.with(activity)
                                .load(smallParamUrl)
                                .asBitmap()
                                .fitCenter()
                                .into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                                .get();*/

                        String localBase64 = BitmapUtils.bitmapToString100(myBitmap);
                        shareParamPartParms.setImageData(localBase64);
                        partlist.add(shareParamPartParms);

                        //本地保存
                        mpartparms.add(new PictureProcessingData(BizImageMangage.PARMS, shareParamPartParms.getImagePath(), shareParamPartParms.getImageData()));

                    } catch (Exception e) {
                        LogUtil.e(e.toString());
                    }
                }
            }

            shareParmsData.setPartArray(partlist);
            parms.setPartParmlists(mpartparms);
        }
        return shareParmsData;
    }


    public void checkAndUpload(DownloadInfoData downloadInfoData) {
        String path = "";
        String coverIdPath = "";
        if (downloadInfoData.getFileType() == Flag.TYPE_PIC) {
            List<KeyValue> postParms = JsonUtil.Json2List(downloadInfoData.getPostParms(), KeyValue.class);
            String replaceId = "";
            for (int i = 0; i < postParms.size(); i++) {
                if (postParms.get(i).getKey().equals("replaceId")) {
                    replaceId = postParms.get(i).getValue();
//                    final String url = BitmapUtils.getBitmapUrl6(replaceId);
//                    //更新缓存
//                    Picasso.get(activity).invalidate(StringUtil.getSizeUrl(url, (ContentUtil.getScreenWidth(activity) - ContentUtil.dip2px(activity, 3)) / 3, (ContentUtil.getScreenWidth(activity) - ContentUtil.dip2px(activity, 3)) / 3));
//
//                    // Picasso不提供getDownloader()，所以你得用以下方法绕过。
//                    OkHttpDownloader okHttpDownloader = new OkHttpDownloader(activity);
//                    OkHttpClient client = ReflectionHelpers.getField(okHttpDownloader, "client");
//                    picassoDiskCache = client.getCache(); // 拿到disk cache
//                    Picasso picasso = new Picasso.Builder(activity)
//                            .downloader(okHttpDownloader)
//                            .build();
//                    Iterator<String> iterator = picassoDiskCache.urls()
//                    while (iterator.hasNext()) {
//                        String imgUrl= iterator.next();
//                        if (imgUrl.equals(url)) {
//
//                            iterator.remove();
//                            break;
//                        }
//                    }
//
                    break;


                }
            }

            path = downloadInfoData.getLocalUrl().substring(7);
            uploadPicFile(path, "", replaceId, downloadInfoData, true, "storage_sto","");
        } else if (downloadInfoData.getFileType() == Flag.TYPE_VIDEO||downloadInfoData.getFileType() == Flag.TYPE_AI360) {

            List<KeyValue> postParms = JsonUtil.Json2List(downloadInfoData.getPostParms(), KeyValue.class);
            String postparmsValue = "";
            for (int i = 0; i < postParms.size(); i++) {
                if (postParms.get(i).getKey().equals("params")) {
                    postparmsValue = postParms.get(i).getValue();
                    break;
                }
            }
            Preinstail preinstail = JsonUtil.Json2T(postparmsValue, Preinstail.class);

            path = downloadInfoData.getLocalUrl().substring(7);
            if (null != preinstail && !StringUtil.isBlank(preinstail.getFileName())) {
                coverIdPath = preinstail.getFileName().substring(7);
            }
            if(downloadInfoData.getFileType() == Flag.TYPE_AI360){
                uploadVideoFile(path, coverIdPath, "", downloadInfoData, "img_25d",preinstail.getName());
            }else if(downloadInfoData.getFileType() == Flag.TYPE_VIDEO){
                uploadVideoFile(path, coverIdPath, "", downloadInfoData, "storage_sto","");
            }
        }

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

    private int toAnotherName(int type) {
        int newtype = 500;
        switch (type) {

            case BizImageMangage.ATMOSPHERE:
                newtype = 1000;
                break;
            case BizImageMangage.ATMOSPHERE_OUT:
                newtype = 1001;
                break;
            case BizImageMangage.BACKGROUND_REPAIR:
                newtype = 1002;
                break;
            case BizImageMangage.PAINT_BRUSH:
                newtype = 1003;
                break;
            case BizImageMangage.ROTATE:
                newtype = 1004;
                break;
            case BizImageMangage.SRC:
                newtype = 1005;
                break;
            case BizImageMangage.PARMS:
                newtype = 1006;
                break;
            case BizImageMangage.WHITE_BALANCE:
                newtype = 1008;
                break;
            case BizImageMangage.CROP:
                newtype = 2007;
                break;
            case BizImageMangage.HIGHLIGHT:
                newtype = 0;
                break;
            case BizImageMangage.BRIGHTNESS:
                newtype = 1;
                break;
            case BizImageMangage.CONTRAST:
                newtype = 2;
                break;
            case BizImageMangage.SHADOW:
                newtype = 3;
                break;
            case BizImageMangage.NATURALSATURATION:
                newtype = 4;
                break;
            case BizImageMangage.WARMANDCOOLCOLORS:
                newtype = 5;
                break;
            case BizImageMangage.SATURATION:
                newtype = 6;
                break;
            case BizImageMangage.DEFINITION:
                newtype = 7;
                break;
            case BizImageMangage.SHARPEN:
                newtype = 8;
                break;
            case BizImageMangage.EXPOSURE:
                newtype = 9;
                break;
            case BizImageMangage.WHITE_LEVELS:
                newtype = 11;
                break;
            case BizImageMangage.BLACK_LEVELS:
                newtype = 12;
        }

        return newtype;
    }

    private Bitmap mergeBitmap(Bitmap firstBitmap) {
        Paint paint = new Paint();
        paint.setColor(Color.parseColor("#ffffff"));
        Bitmap bitmap = Bitmap.createBitmap(firstBitmap.getWidth(),
                firstBitmap.getHeight(), firstBitmap.getConfig());
        Canvas canvas = new Canvas(bitmap);
        canvas.drawRect(0, 0, firstBitmap.getWidth(), firstBitmap.getHeight(), paint);
        canvas.drawBitmap(firstBitmap, 0, 0, paint);
        return bitmap;
    }

    public String listToString(List list, char separator) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            sb.append(list.get(i));
            if (i < list.size() - 1) {
                sb.append(separator);
            }
        }
        return sb.toString();
    }

    private HashMap<String, Object> uploadParamsMap(String fileId, PresetParm shareParmsData, String catalogId) {
        HashMap<String, Object> parms = new HashMap<String, Object>();
        parms.put("scope", 1);
        parms.put("bizcam", 1);
        int paramGlobal = shareParmsData.getParmlists() == null ? 0 : 1;
        int paramLightPen = shareParmsData.getLightRatioData() != null ? 1 : 0;
        int paramPreprocess = shareParmsData.getPartParmlists() != null ? 1 : 0;

        parms.put("coboxName", BizImageMangage.getCoboxName(String.valueOf(shareParmsData.getLightRatioData().getVersion())));
        parms.put("coboxVersion", shareParmsData.getLightRatioData().getVersion());
        parms.put("system", "android");
        parms.put("deviceType", shareParmsData.getEquipment());
        parms.put("paramGlobal", paramGlobal);//全局参数
        parms.put("paramLightPen", paramLightPen);//光比
        parms.put("paramPreprocess", paramPreprocess);//局部参数
        parms.put("tags", listToString(shareParmsData.getLabels(), ','));//图片标签
        parms.put("name", shareParmsData.getName());//图片名字

        parms.put("paramVersion", "1");
        parms.put("fileId", fileId);
        parms.put("catalogId", catalogId);
        return parms;
    }

    public interface UploadListener {
        void onUploadSuccess();

        void onUploadFaile();

    }

    public void setUploadListener(UploadListener uploadListener) {
        this.uploadListener = uploadListener;
    }
}
