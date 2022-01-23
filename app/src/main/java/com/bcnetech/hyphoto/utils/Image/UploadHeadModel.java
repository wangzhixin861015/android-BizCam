package com.bcnetech.hyphoto.utils.Image;


import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;

import com.bcnetech.bcnetchhttp.RetrofitInternationalFactory;
import com.bcnetech.bcnetchhttp.RetrofitUploadFactory;
import com.bcnetech.bcnetchhttp.base.BaseResult;
import com.bcnetech.bcnetchhttp.bean.request.FileCheckBody;
import com.bcnetech.bcnetchhttp.bean.request.ResetHeadBody;
import com.bcnetech.bcnetchhttp.bean.response.BimageUploadingChunk;
import com.bcnetech.bcnetchhttp.bean.response.LoginReceiveData;
import com.bcnetech.bcnetchhttp.bean.response.LoginedUser;
import com.bcnetech.bcnetchhttp.config.Flag;
import com.bcnetech.bcnetchhttp.config.UrlConstants;
import com.bcnetech.bcnetechlibrary.util.ToastUtil;
import com.bcnetech.hyphoto.task.MBaseObserver;
import com.bcnetech.hyphoto.ui.popwindow.HeadimgPop;
import com.bcnetech.hyphoto.utils.FileUtil;
import com.bcnetech.hyphoto.R;

import java.io.File;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * 上传个人头像model
 * <p>
 * <p>
 * Created by xuan on 15/9/23.
 */
public class UploadHeadModel extends BaseUploadHeadModel {
    private UploadListener uploadListener;

    @Override
    protected String onSaveFilePath() {
        return Flag.PERSION_IMAGE;
    }

    @Override
    protected void onShowDialog() {
        List<String> itemList = new ArrayList<String>();
        List<View.OnClickListener> onClickListenerList = new ArrayList<View.OnClickListener>();
        FileUtil.deleteImage(getBigHeadFilename());
        FileUtil.deleteImage(getSmallHeadFilename());

        itemList.add(mActivity.getResources().getString(R.string.ablum));
        onClickListenerList.add(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoAlbum();
            }
        });
        itemList.add(mActivity.getResources().getString(R.string.take_photo));
        onClickListenerList.add(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoCamera(mActivity);
            }
        });

        Context context = null;
        if (null != mActivity) {
            context = mActivity;
        } else {
            context = mFragment.getActivity();
        }
        final HeadimgPop headimgPop = new HeadimgPop(mActivity);

        headimgPop.showPop(mActivity.getWindow().getDecorView());
        headimgPop.setHeadimgPopListener(mActivity, new HeadimgPop.HeadimgPopListener() {
            @Override
            public void onTake_photo() {
                gotoCamera(mActivity);
                headimgPop.dismissPop();
            }

            @Override
            public void onSystem_ablum() {
                gotoAlbum();
                headimgPop.dismissPop();
            }
        });
        /*SingleSelectDialogUtil dailog = new SingleSelectDialogUtil.Builder(context)
                .setItemTextAndOnClickListener(itemList.toArray(new String[itemList.size()]), onClickListenerList.toArray(new View.OnClickListener[onClickListenerList.size()]))
                .createInstance();
        dailog.show();*/
    }

    boolean isFail=false;

    @Override
    protected void onUploadHead2Server(Bitmap bigBitmap, Bitmap smallBitmap) {
        final LoginedUser loginedUser = LoginedUser.getLoginedUser();
//        String token = loginedUser.getTokenid();
//        SetHeaderTask setHeaderTask = new SetHeaderTask(mActivity,true);
//        setHeaderTask.setAsyncTaskSuccessCallback(new AsyncTaskSuccessCallback<HttpBaseData>() {
//            @Override
//            public void successCallback(Result<HttpBaseData> result) {
//                final String data = result.getValue().getData().toString();
//                ResetNicknameTask resetNicknameTask = new ResetNicknameTask(mActivity,true);
//                resetNicknameTask.setAsyncTaskSuccessCallback(new AsyncTaskSuccessCallback<RegistData>() {
//                    @Override
//                    public void successCallback(Result<RegistData> result) {
//                        loginedUser.setAvatar(data);
//                        LoginReceiveData loginReceiveData = loginedUser.getLoginData();
//                        loginReceiveData.setAvatar(data);
//                        loginedUser.setLoginData(loginReceiveData);
//                        LoginedUser.saveToFile();
//                        uploadListener.upDataImg(data);
//                        ToastUtil.toast(result.getMessage());
//                    }
//                });
//
//                resetNicknameTask.setAsyncTaskFailCallback(new AsyncTaskFailCallback<RegistData>() {
//                    @Override
//                    public void failCallback(Result<RegistData> result) {
//                        ToastUtil.toast(result.getMessage());
//                    }
//                });
//                resetNicknameTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,null,data);
//            }
//        });
//        setHeaderTask.setAsyncTaskFailCallback(new AsyncTaskFailCallback<HttpBaseData>() {
//            @Override
//            public void failCallback(Result<HttpBaseData> result) {
//                ToastUtil.toast(result.getMessage());
//            }
//        });
//        //setHeaderTask.execute(getBigHeadFilename(), token);
//        setHeaderTask.execute(getResultUrls(), token);


        FileCheckBody fileCheckBody = FileUpload.fileUploadInfoCheckBody(getResultUrls(), FileUpload.fileUploadCheck(getResultUrls(), ""));
        fileCheckBody.setCode("bimage_avatar");
        fileCheckBody.setScope("2");

        RetrofitUploadFactory.getUPloadInstence().API().fileCheck(fileCheckBody)

                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MBaseObserver<BimageUploadingChunk>(mActivity, true) {
                    @Override
                    protected void onSuccees(final BaseResult<BimageUploadingChunk> checkResult) throws Exception {


                        if (checkResult.getCode() == 20014) {
                            isFail=false;
                            //分片上传
                            for (int i = 0; i < checkResult.getData().getList().size(); i++) {
                                BimageUploadingChunk.BimageUploadingChunkList bimageUploadingChunk = checkResult.getData().getList().get(i);

                                Map<String, String> mapParam = FileUpload.fileUploaInfo(getResultUrls());
                                mapParam.put("fileId", checkResult.getData().getFileId());
                                mapParam.put("chunkSha1", bimageUploadingChunk.getChunkSha1());
                                mapParam.put("chunkSize", bimageUploadingChunk.getChunkSize());
                                mapParam.put("name", bimageUploadingChunk.getChunkSize());
                                mapParam.put("code", "bimage_avatar");

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
                                        .subscribe(new MBaseObserver<Object>(mActivity, false) {
                                            @Override
                                            protected void onSuccees(final BaseResult<Object> result) throws Exception {
                                                if (!isFail && finalI == checkResult.getData().getList().size() - 1) {
                                                    updateHead(checkResult.getData().getFileId());
                                                }
                                                if(isFail&&finalI==checkResult.getData().getList().size()-1){
                                                    ToastUtil.toast(mActivity.getResources().getString(R.string.update_fail));
                                                }

                                            }

                                            @Override
                                            protected void onCodeError(BaseResult<Object> t) throws Exception {
                                                isFail=true;
                                            }

                                            @Override
                                            protected void onFailure(Throwable e, boolean isNetWorkError) throws Exception {
                                                isFail=true;
                                            }
                                        });


                            }

                        } else if (checkResult.getCode() == 20018) {
                            updateHead(checkResult.getData().getFileId());
                        }

                    }

                    @Override
                    protected void onCodeError(BaseResult<BimageUploadingChunk> t) throws Exception {
                        ToastUtil.toast(t.getMessage());

                    }

                    @Override
                    protected void onFailure(Throwable e, boolean isNetWorkError) throws Exception {
                        ToastUtil.toast(e.getMessage());

                    }
                });

    }

    private void updateHead(final String fileId) {
        final LoginedUser loginedUser = LoginedUser.getLoginedUser();
        RetrofitInternationalFactory.getInstence().API().resetUserInfoHead(new ResetHeadBody(fileId))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MBaseObserver<Object>(mActivity, false) {
                    @Override
                    protected void onSuccees(BaseResult<Object> resetUserResult) throws Exception {
                        loginedUser.setAvatar(fileId);
                        LoginReceiveData loginReceiveData = loginedUser.getLoginData();
                        loginReceiveData.setAvatar(fileId);
                        loginedUser.setLoginData(loginReceiveData);
                        LoginedUser.saveToFile();
                        uploadListener.upDataImg(fileId);
                        ToastUtil.toast(resetUserResult.getMessage());
                    }

                    @Override
                    protected void onCodeError(BaseResult<Object> resetUserResult) throws Exception {
                        ToastUtil.toast(resetUserResult.getMessage());
                    }

                    @Override
                    protected void onFailure(Throwable e, boolean isNetWorkError) throws Exception {
                        ToastUtil.toast(e.getMessage());
                    }
                });
    }

    public UploadListener getUploadListener() {
        return uploadListener;
    }

    public void setUploadListener(UploadListener uploadListener) {
        this.uploadListener = uploadListener;
    }

    public interface UploadListener {
        void upDataImg(String data);
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