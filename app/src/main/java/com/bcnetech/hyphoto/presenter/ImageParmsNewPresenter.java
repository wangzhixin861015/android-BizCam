package com.bcnetech.hyphoto.presenter;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;

import com.bcnetech.bcnetchhttp.config.Flag;
import com.bcnetech.bcnetechlibrary.util.StringUtil;
import com.bcnetech.bcnetechlibrary.util.ToastUtil;
import com.bcnetech.bcnetechlibrary.view.PickerViewV;
import com.bcnetech.hyphoto.data.SqlControl.BaseSqlControl;
import com.bcnetech.hyphoto.data.SqlControl.ImageDataSqlControl;
import com.bcnetech.hyphoto.imageinterface.BizImageMangage;
import com.bcnetech.hyphoto.model.ImageParmsModel;
import com.bcnetech.hyphoto.presenter.iview.IImageParmsNewView;
import com.bcnetech.hyphoto.receiver.AddPicReceiver;
import com.bcnetech.hyphoto.sql.dao.ImageData;
import com.bcnetech.hyphoto.sql.dao.PictureProcessingData;
import com.bcnetech.hyphoto.task.SavePicFinalTask;
import com.bcnetech.hyphoto.ui.activity.photoedit.ImageParmsNewActivity;
import com.bcnetech.hyphoto.ui.view.ImageNewUtilsView;
import com.bcnetech.hyphoto.utils.BitmapUtils;
import com.bcnetech.hyphoto.utils.FileUtil;
import com.bcnetech.hyphoto.utils.ImageUtil;
import com.bcnetech.hyphoto.R;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import jp.co.cyberagent.android.gpuimage.GPUImageFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageFilterGroup;
import jp.co.cyberagent.android.gpuimage.GPUImageWhiteBalanceNewFilter;
import jp.co.cyberagent.android.gpuimage.gpumanage.GPUImageFilterTools;

/**
 * Created by yhf on 2017/10/25.
 */

public class ImageParmsNewPresenter extends BasePresenter<IImageParmsNewView> {
    private final static String CURRENT_IMAGE_DATA = "currentPicDatas";
    private final static String CURRENT_TYPE = "currentType";

    private final static int LIMIT_MAX_DATA = 200;//参数MAX 限制
    private final static int LIMIT_MIN_DATA = 0;//参数MIN 限制

    public final static int LIGHT = 1;
    public final static int COLOR = 2;
    public final static int DETAILS = 3;

    private GPUImageFilter currentFilter, srcFilter;
    private List<PictureProcessingData> currentPicDatas;
    private List<GPUImageFilter> gpuImageFilters;
    private HashMap<Integer, GPUImageFilterTools.FilterAdjuster> adjustMap;
    public int currentPosition;
    private int currentType;
    private int imgType;//图片处理类型 颜色，细节，光效
    private int currentPartParmsPosition;//当前部分修改操作节点；
    private ImageData imageData;
    private String parmName;

    private Bitmap mCurrentBitmap;
    private Bitmap srcBitmap;
    private String url;
    private float currentNum;
    private boolean mFirstWindowFouse, activityFouse;
    private Bitmap tempBit;

    private ImageParmsModel imageParmsModel;
    private Handler handler = new Handler();
    private int color;
    private int orientation;
    private SaveWhitebalanceTask task;
    private PictureProcessingData pictureProcessingData;
    private GPUImageWhiteBalanceNewFilter gpuImageWhiteBalanceNewFilter;
    private GPUImageFilterTools.FilterAdjuster mFilterAdjuster;

    public ImageParmsNewPresenter() {
        imageParmsModel = new ImageParmsModel();
    }

    public static void startAction(Activity activity, int parmsType,int currentPosition, int resultCode) {
        Intent intent = new Intent(activity, ImageParmsNewActivity.class);
        intent.putExtra(Flag.PARMS_TYPE, parmsType);
//        intent.putExtra(Flag.PARMS, imageData);
        intent.putExtra(Flag.CURRENT_PART_POSITION, currentPosition);
        activity.startActivityForResult(intent, resultCode);
    }

    public void autoGpuImageParmsLayout(double withe, double hight) {
        double w = withe;
        double h = hight;

        double strx = 0;
        double stry = 0;
        if (w / h > mCurrentBitmap.getWidth() * 1.0 / mCurrentBitmap.getHeight()) {
            w = h * mCurrentBitmap.getWidth() * 1.0 / mCurrentBitmap.getHeight();
            strx = (withe - w) / 2;
        } else {
            h = w * mCurrentBitmap.getHeight() * 1.0 / mCurrentBitmap.getWidth();
            stry = (hight - h) / 2;
        }

        // 获得图片的宽高
        int width = mCurrentBitmap.getWidth();
        int height = mCurrentBitmap.getHeight();
        // 设置想要的大小
        double newWidth = w;
        double newHeight = h;
        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片
        Bitmap newbm = Bitmap.createBitmap(mCurrentBitmap, 0, 0, width, height, matrix,
                true);

        mView.setGpuImageSize((int) strx, (int) stry, (int) w, (int) h, newbm);
    }

    public List<PickerViewV.Item> initPickerData() {
        switch (imgType) {
            case LIGHT:
                return imageParmsModel.getLightList();
            case COLOR:
                return imageParmsModel.getColorList();
            case DETAILS:
                return imageParmsModel.getDetailsList();
        }
        return null;
    }

    public int getImgType() {
        return imgType;
    }

    public void setImgType(int imgType) {
        this.imgType = imgType;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void pickervChanceType(String name) {
        if (name.equals(activity.getResources().getString(R.string.exposire))) {
            chiceType(name, BizImageMangage.EXPOSURE);
        } else if (name.equals(activity.getResources().getString(R.string.contrast))) {
            chiceType(name, BizImageMangage.CONTRAST);
        } else if (name.equals(activity.getResources().getString(R.string.sharpen))) {
            chiceType(name, BizImageMangage.SHARPEN);
        } else if (name.equals(activity.getResources().getString(R.string.brightness))) {
            chiceType(name, BizImageMangage.BRIGHTNESS);
        } else if (name.equals(activity.getResources().getString(R.string.contrast))) {
            chiceType(name, BizImageMangage.SATURATION);
        } else if (name.equals(activity.getResources().getString(R.string.shadow))) {
            chiceType(name, BizImageMangage.SHADOW);
        } else if (name.equals(activity.getResources().getString(R.string.high_light))) {
            chiceType(name, BizImageMangage.HIGHLIGHT);
        } else if (name.equals(activity.getResources().getString(R.string.vividness))) {
            chiceType(name, BizImageMangage.NATURALSATURATION);
        } else if (name.equals(activity.getResources().getString(R.string.clod_hot))) {
            chiceType(name, BizImageMangage.WARMANDCOOLCOLORS);
        } else if (name.equals(activity.getResources().getString(R.string.defintion))) {
            chiceType(name, BizImageMangage.DEFINITION);
        } else if (name.equals(activity.getResources().getString(R.string.saturation))) {
            chiceType(name, BizImageMangage.SATURATION);
        } else if (name.equals(activity.getResources().getString(R.string.color_temperature))) {
            chiceType(name, BizImageMangage.WHITE_BALANCE);
        } else if (name.equals(activity.getResources().getString(R.string.color_tint))) {
            chiceType(name, BizImageMangage.WHITE_BALANCE);
        } else if (name.equals(activity.getResources().getString(R.string.color_custom))) {
            chiceType(name, BizImageMangage.WHITE_BALANCE);
        }
        parmName = name;
    }

    /**
     * 进入参数调整初始化数据
     */
    public void initCurrentData() {

        gpuImageFilters = new ArrayList<>();
        adjustMap = new HashMap<>();
        //色温
        if (getImgType() == COLOR) {
            pictureProcessingData = new PictureProcessingData(BizImageMangage.WHITE_BALANCE, url);
            gpuImageWhiteBalanceNewFilter = (GPUImageWhiteBalanceNewFilter) BizImageMangage.getInstance().getGPUFilterforType(activity, BizImageMangage.WHITE_BALANCE);
            mFilterAdjuster = new GPUImageFilterTools.FilterAdjuster(gpuImageWhiteBalanceNewFilter);
            gpuImageFilters.add(gpuImageWhiteBalanceNewFilter);
//            currentPicDatas.add(pictureProcessingData);
            adjustMap.put(BizImageMangage.WHITE_BALANCE, mFilterAdjuster);
        }

        if (currentPicDatas == null) {
            currentPicDatas = new ArrayList<>();
            if (null != pictureProcessingData) {
                currentPicDatas.add(pictureProcessingData);
            }
            imageParmsModel.initFilter(gpuImageFilters, adjustMap, currentPicDatas, imageData);
        }
//        else {
//            imageParmsModel.initFilterAgin(gpuImageFilters, adjustMap, currentPicDatas);
//        }
    }

    /**
     * 根据类型获取  图片处理实例
     *
     * @param type
     */
    private void chiceType(String name, int type) {
        if (adjustMap.get(type) == null) {
            imageParmsModel.addFilter(gpuImageFilters, adjustMap, currentPicDatas, type);
            currentPosition = currentPicDatas.size() - 1;
        } else {
            for (int i = 0; i < currentPicDatas.size(); i++) {
                if (currentPicDatas.get(i).getType() == type) {
                    currentPosition = i;
                    break;
                }
            }
        }

        currentType = type;

        if (gpuImageFilters == null || gpuImageFilters.size() == 0) {
            currentFilter = new GPUImageFilter();
        } else {
            currentFilter = new GPUImageFilterGroup(gpuImageFilters);
        }
        mView.setGPUFilter(currentFilter);
        //色温
        if (name.equals(activity.getResources().getString(R.string.color_temperature))) {
            mView.setProgresssee(currentPicDatas.get(currentPosition).getNum());
            titleTextShow(name, currentPicDatas.get(currentPosition).getNum());
        } //着色
        else if (name.equals(activity.getResources().getString(R.string.color_tint))) {
            mView.setProgresssee(currentPicDatas.get(currentPosition).getTintNum());
            titleTextShow(name, currentPicDatas.get(currentPosition).getTintNum());
            //吸管
        } else if (name.equals(activity.getResources().getString(R.string.color_custom))) {
            mView.setTitleText(name);
        } else {
            mView.setProgresssee(currentPicDatas.get(currentPosition).getNum());
            titleTextShow(name, currentPicDatas.get(currentPosition).getNum());
        }

    }


    /**
     * 设置当先数据显示
     * 按-100 -- 100规则
     *
     * @param number
     */
    private void titleTextShow(String name, int number) {
        mView.setTitleText(name + " " + String.valueOf(number - 100));
    }

    /**
     * 设置当先数据显示
     * 按-100 -- 100规则
     *
     * @param
     */
    private void titleTextShow(String name) {
        mView.setTitleText(name);
    }


    public void back() {
        // mView.showLoading();
        if (currentPicDatas != null) {
            if (currentPicDatas.size() != 0) {
                //初始为0
                int currentposition = imageData.getCurrentPosition();
                if (currentposition == 0) {
                    imageData.setCurrentPosition(currentposition + 1);
                } else {
                    //之前做过修改,且未做全局修改
                    if (imageData.getImageTools() == null || imageData.getImageTools().size() == 0) {
                        imageData.setCurrentPosition(currentposition + 1);
                        //  imageparms.setImageTools(imageDatatext.getImageTools());
                    } else {
                        imageData.setCurrentPosition(currentposition);
                        //之前做过修改,且为做全局修改,当前位置不改变
                        // imageData.setImageTools(imageDatatext.getImageTools());
                    }
                }
                imageData.setImageTools(currentPicDatas);
              /*  if (imageData.getCurrentPosition() != 0){
                    //删除已有的小图
                   // DeletePic();
                }
                imageData.setCurrentPosition(imageData.getCurrentPosition() + 1);*/
//                saveImageData(imageData);
                loadSrcBitmap();
            }
        }
       /* Intent intent = new Intent();
       // intent.putExtra(Flag.PARMS, imageData);
        mView.finishView(Flag.IMAGE_PARMS_ACTIVITY,intent);*/
    }


    private void loadSrcBitmap(){
        BitmapUtils.BitmapSize bitmapSize = BitmapUtils.getBitmapSize(imageData.getSmallLocalUrl());
        int orientation=BitmapUtils.getOrientation(imageData.getSmallLocalUrl());
        int min = Math.min(bitmapSize.width, bitmapSize.height);
        int max=800;
        if (min > max) {
            float width ;
            float height;
            if(orientation==90||orientation==270){
                width = bitmapSize.height;
                height = bitmapSize.width;

                if (width > height) {
                    width = max;
                    height = max * bitmapSize.width / bitmapSize.height;
                } else {
                    height = max;
                    width = max * bitmapSize.height / bitmapSize.width;
                }

            }else {
                width = bitmapSize.width;
                height = bitmapSize.height;

                if (width > height) {
                    width = max;
                    height = max * bitmapSize.height / bitmapSize.width;
                } else {
                    height = max;
                    width = max * bitmapSize.width / bitmapSize.height;
                }
            }
            Picasso.get().load(imageData.getLocalUrl()).resize((int) width, (int) height).into(srcTarget);
        }else {
            Picasso.get().load(imageData.getLocalUrl()).into(srcTarget);

        }
    }


    private Target srcTarget=new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            srcBitmap=bitmap;
            saveImageData(imageData);
        }

        @Override
        public void onBitmapFailed(Exception e, Drawable errorDrawable) {

        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    };

    public void saveWhiterbalance() {


        tempBit = mView.getFinalBit();
        Bitmap finalbit = mView.getWhiteBalanceBit();
        task = new SaveWhitebalanceTask(finalbit, tempBit);
        task.execute();
    }

    public GPUImageWhiteBalanceNewFilter getGpuImageWhiteBalanceNewFilter() {
        return gpuImageWhiteBalanceNewFilter;
    }

    public void setGpuImageWhiteBalanceNewFilter(GPUImageWhiteBalanceNewFilter gpuImageWhiteBalanceNewFilter) {
        this.gpuImageWhiteBalanceNewFilter = gpuImageWhiteBalanceNewFilter;
    }

    //保存最终图片和未加滤镜的修改图片
    private class SaveWhitebalanceTask extends AsyncTask<Void, Void, ArrayList> {
        private Bitmap bitmap;//未加滤镜图片
        private Bitmap finalbitmap;//展示用的最终图片

        SaveWhitebalanceTask(Bitmap bitmap, Bitmap finalbitmap) {
            this.bitmap = bitmap;
            this.finalbitmap = finalbitmap;
        }

        protected void onPreExecute() {
            //dialogHelper.showProgressDialog("处理中");
        }

        @Override
        protected ArrayList<String> doInBackground(Void... params) {
            long time = System.currentTimeMillis();
            ArrayList<String> urllist = new ArrayList<>();
            String path1 = null;//未加滤镜图片
            String path2 = null;//最终图片
            String path3 = null; //分享小图
            try {
                urllist = new ArrayList<>();
//                if (imageData.getSmallLocalUrl().endsWith(".png")){
//                    imageData.setMatting(true);
//                }else{
//                    imageData.setMatting(false);
//                }
                path1 = FileUtil.saveBitmap(bitmap, time - 1 + "", imageData.getSmallLocalUrl().contains(".png"));
                path2 = FileUtil.saveBitmaptoNative(finalbitmap, time + 1 + "", imageData.getSmallLocalUrl().contains(".png"));

                path3 = FileUtil.saveBitmaptoShareNative(imageData.getSmallLocalUrl().contains(".png") ? BitmapUtils.compressPNG(finalbitmap) : BitmapUtils.compress(finalbitmap), time + 2 + "", imageData.getSmallLocalUrl().contains(".png"));

                if (StringUtil.isBlank(path1) || StringUtil.isBlank(path2)) {
                    return null;
                }
                path1 = "file://" + path1;
                path2 = "file://" + path2;
                path3 = "file://" + path3;
                urllist.add(path1);
                urllist.add(path2);
                urllist.add(path3);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return urllist;
        }

        @Override
        protected void onPostExecute(ArrayList result) {
            super.onPostExecute(result);
            if (result != null || result.size() == 0) {
                String processurl = (String) result.get(0);
                String finalurl = (String) result.get(1);
                String smallUrl = (String) result.get(2);
                if (processurl != null && finalurl != null) {
                    List<PictureProcessingData> processlist;
                    if (imageData.getImageParts() == null) {
                        processlist = new ArrayList<PictureProcessingData>();
                    } else {
                        processlist = imageData.getImageParts();
                    }
                    processlist.add(new PictureProcessingData(BizImageMangage.WHITE_BALANCE, processurl, smallUrl, ""));

                    if (imageData.getCurrentPosition() == 0) {

                        imageData.setImageTools(new ArrayList<PictureProcessingData>());
                        for (int i = 0; i < imageData.getImageParts().size(); i++) {
                            // DeleteImage(imageDatatext.getImageParts().get(i).getImageUrl().substring("file://".length()));//删除之前小图
                        }
                        imageData.setImageParts(processlist);//设置部分修改
                        imageData.setCurrentPosition(//设置当前位置
                                imageData.getCurrentPosition() +
                                        (processlist.size()));
                    } else {//当前位置不为0
                        if (imageData.getImageTools() != null && imageData.getImageTools().size() != 0) {
                            if (imageData.getCurrentPosition() - 1 > (processlist.size())) {
                            } else {
                                imageData.setCurrentPosition(imageData.getCurrentPosition() + 1);
                            }
                        } else {
                            if (imageData.getCurrentPosition() > (processlist.size())) {
                            } else {
                                imageData.setCurrentPosition(imageData.getCurrentPosition() + 1);

                            }
                            imageData.setImageParts(processlist);
                        }
                        imageData.setImageParts(processlist);

                    }
                    DeleteImage(imageData.getSmallLocalUrl().substring("file://".length()));
                    imageData.setSmallLocalUrl(finalurl);

                    final ImageDataSqlControl imageDataSqlControl = new ImageDataSqlControl(activity);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            imageDataSqlControl.setListener(new BaseSqlControl.SqlControlListener() {
                                @Override
                                public void queryListener(Object... parms) {

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
                            activity.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse(imageData.getSmallLocalUrl())));
                            imageDataSqlControl.startUpdata(new String[]{imageData.getLocalUrl()}, imageData);
                            AddPicReceiver.notifyModifyUsername(activity, "refresh");
                        }
                    });
                }
            } else {
                ToastUtil.toast(activity.getString(R.string.change_fail));
            }
            mView.dissMissSaveLoading();

            mView.finishView(Flag.IMAGE_PARMS_ACTIVITY, null);
        }
    }

    //删除数据库以及文件中的图片
    private boolean DeleteImage(String imgPath) {
        boolean result = false;
        try {
            ContentResolver resolver = activity.getContentResolver();
            Cursor cursor = MediaStore.Images.Media.query(resolver, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Images.Media._ID}, MediaStore.Images.Media.DATA + "=?",
                    new String[]{imgPath}, null);
            if (cursor.moveToFirst()) {
                long id = cursor.getLong(0);
                Uri contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                Uri uri = ContentUris.withAppendedId(contentUri, id);
                int count = activity.getContentResolver().delete(uri, null, null);
                result = count == 1;
            } else {
                File file = new File(imgPath);
                if (file.exists()) {//显示文件不存在,该文件夹内存在两张一样的图片
                    result = file.delete();
                }
            }
        } catch (Exception e) {
            File file = new File(imgPath);
            if (file.exists()) {//显示文件不存在,该文件夹内存在两张一样的图片
                result = file.delete();
            }
        }
        return result;
    }

    public void saveImageData(final ImageData imageData) {
        Bitmap bitmap = mView.getFinalBit();
        Bitmap srcBitmap = mView.getFinalSrcBit();

        if (bitmap != null) {
            //保存最后生成的图片
            SavePicFinalTask savePicFinalTask = new SavePicFinalTask(bitmap, srcBitmap, new SavePicFinalTask.SaveInterface() {
                @Override
                public void saveEnd(String url, String srcUrl) {
                    DeleteImage(imageData.getSmallLocalUrl().substring("file://".length()));
                    if (!StringUtil.isBlank(imageData.getValue2()) && !"null".equals(imageData.getValue2())) {
                        DeleteImage(imageData.getValue2().substring("file://".length()));
                    }
                    imageData.setSmallLocalUrl(url);
                    imageData.setValue2(srcUrl);
                    final ImageDataSqlControl imageDataSqlControl = new ImageDataSqlControl(activity);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            imageDataSqlControl.setListener(new BaseSqlControl.SqlControlListener() {
                                @Override
                                public void queryListener(Object... parms) {

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
                            // Intent intent = new Intent();
                            // intent.putExtra(Flag.PARMS, imageData);
                            mView.dissMissSaveLoading();
                            mView.finishView(Flag.IMAGE_PARMS_ACTIVITY, null);
                            imageDataSqlControl.startUpdata(new String[]{imageData.getLocalUrl()}, imageData);
                            activity.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse(imageData.getSmallLocalUrl())));
                            AddPicReceiver.notifyModifyUsername(activity, "refresh");
                        }
                    });
                }
            }, url.contains(".png"), imageData.isMatting());
            savePicFinalTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }

    }

    public GPUImageFilter getSrcFilter() {
        return srcFilter;
    }

    public GPUImageFilter getCurrentFilter() {
        return currentFilter;
    }


    /**
     * 调整参数
     *
     * @param addNum
     */
    public void setFilterNumber(float addNum) {
        if (parmName.equals(activity.getResources().getString(R.string.color_temperature))) {
            currentNum = currentPicDatas.get(currentPosition).getNum() + addNum;
        } //着色
        else if (parmName.equals(activity.getResources().getString(R.string.color_tint))) {
            currentNum = currentPicDatas.get(currentPosition).getTintNum() + addNum;
        } else {
            currentNum = currentPicDatas.get(currentPosition).getNum() + addNum;
        }
        if (currentNum > LIMIT_MAX_DATA) {
            currentNum = LIMIT_MAX_DATA;
        } else if (currentNum < LIMIT_MIN_DATA) {
            currentNum = LIMIT_MIN_DATA;
        }
        //色温
        if (parmName.equals(activity.getResources().getString(R.string.color_temperature))) {
            adjustMap.get(currentType).adjust((int) currentNum, currentPicDatas.get(currentPosition).getTintNum());

        } //着色
        else if (parmName.equals(activity.getResources().getString(R.string.color_tint))) {
            adjustMap.get(currentType).adjust(currentPicDatas.get(currentPosition).getNum(), (int) currentNum);
        } else {
            adjustMap.get(currentType).adjust((int) currentNum);
        }

        titleTextShow(parmName, (int) currentNum);
        mView.setProgresssee((int) currentNum);
    }

    /**
     * 调整参数
     *
     * @param
     */
    public void refreshCTandTint() {
        //色温 着色
        if (parmName.equals(activity.getResources().getString(R.string.color_temperature)) || parmName.equals(activity.getResources().getString(R.string.color_tint))) {
            adjustMap.get(currentType).adjust(currentPicDatas.get(currentPosition).getNum(), currentPicDatas.get(currentPosition).getTintNum());
        }

    }

    public void setFilterNumber(float temperature, float tint) {
        adjustMap.get(currentType).adjust((int) temperature + 100, (int) tint + 100);
        mView.refresh();
        currentPicDatas.get(currentPosition).setTintNum((int) tint + 100);
        currentPicDatas.get(currentPosition).setNum((int) temperature + 100);

    }

    public void setAutoFilterNumber(float temperature, float tint) {
        adjustMap.get(currentType).adjust((int) temperature + 100, (int) tint + 100);
        mView.refresh();
    }

    public void setAutoNumber(float temperature, float tint) {
        currentPicDatas.get(currentPosition).setTintNum((int) tint + 100);
        currentPicDatas.get(currentPosition).setNum((int) temperature + 100);
    }

    public void setFilterNumberEnd() {
        //色温
        if (parmName.equals(activity.getResources().getString(R.string.color_temperature))) {
            currentPicDatas.get(currentPosition).setNum((int) currentNum);
        } //着色
        else if (parmName.equals(activity.getResources().getString(R.string.color_tint))) {
            currentPicDatas.get(currentPosition).setTintNum((int) currentNum);
        } else {
            currentPicDatas.get(currentPosition).setNum((int) currentNum);
        }
    }

    public List<PictureProcessingData> getCurrentPicDatas() {
        return currentPicDatas;
    }

    public void setCurrentPicDatas(List<PictureProcessingData> currentPicDatas) {
        this.currentPicDatas = currentPicDatas;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(Flag.CURRENT_PART_POSITION, currentPartParmsPosition);
        outState.putSerializable(CURRENT_IMAGE_DATA, (Serializable) currentPicDatas);
        outState.putSerializable(Flag.PARMS, imageData);
        outState.putSerializable(Flag.PARMS_TYPE, imgType);
        outState.putInt(CURRENT_TYPE, currentType);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        imageData = (ImageData) savedInstanceState.getSerializable(Flag.PARMS);
        currentPartParmsPosition = savedInstanceState.getInt(Flag.CURRENT_PART_POSITION);
        currentPicDatas = (List<PictureProcessingData>) savedInstanceState.getSerializable(CURRENT_IMAGE_DATA);
        currentType = savedInstanceState.getInt(CURRENT_TYPE);
        imgType = savedInstanceState.getInt(Flag.PARMS_TYPE);
    }

    @Override
    public void onCreate(Bundle bundle) {
        if(null!=bundle){
            currentPartParmsPosition = bundle.getInt(Flag.CURRENT_PART_POSITION, -1);
            imgType = bundle.getInt(Flag.PARMS_TYPE, LIGHT);
            imageData= (ImageData) bundle.getSerializable(Flag.PARMS);

        }else {
            currentPartParmsPosition = activity.getIntent().getIntExtra(Flag.CURRENT_PART_POSITION, -1);
            imgType = activity.getIntent().getIntExtra(Flag.PARMS_TYPE, LIGHT);
            imageData= ImageNewUtilsView.imageparms;
        }
//        imageData = (ImageData) bundle.getSerializable(Flag.PARMS);
        if (imageData == null) {
            mView.finishView(Flag.NULLCODE, null);
            return;
        }
        if (imageData.getImageTools() == null) {
            imageData.setImageTools(new ArrayList<PictureProcessingData>());
        }
        url = BizImageMangage.getInstance().getUrlFromCurrentPostion(currentPartParmsPosition, imageData, true, true);
        if (url.equals("")) {
            ToastUtil.toast(activity.getResources().getString(R.string.data_error));
            mView.finishView(Flag.NULLCODE, null);
            return;
        }
        activityFouse = false;
        mFirstWindowFouse = true;
        srcFilter = new GPUImageFilter();

    }

    @Override
    public void onResume() {
        super.onResume();
        if (mCurrentBitmap == null) {
            ImageUtil.EBizImageLoad(url, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {
                    mView.showLoading();
                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    mView.hideLoading();
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    orientation = com.bcnetech.bcnetechlibrary.util.ImageUtil.readPictureDegree(url.substring(7));
                    mCurrentBitmap = loadedImage;

                    if (orientation > 0) {
                        Matrix matrix = new Matrix();
                        matrix.setRotate(orientation);
                        // 重新绘制Bitmap
                        Bitmap bitmap = Bitmap.createBitmap(loadedImage, 0, 0, loadedImage.getWidth(), loadedImage.getHeight(), matrix, true);
                        mCurrentBitmap = bitmap;
                    }
                    if (activityFouse) {
                        autoGpuImageParmsLayout(mView.getShowRelWidth(), mView.getShowRelHeight());
                        mFirstWindowFouse = false;
                    }
                    mView.setGpuImageBitmap(mCurrentBitmap);
//                    chiceType(App.getInstance().getResources().getString(R.string.exposire), currentType);
                    mView.hideLoading();
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {
                    mView.hideLoading();
                }
            });
        }

    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        activityFouse = hasFocus;
        if (activityFouse && mFirstWindowFouse) {
            if (mCurrentBitmap != null) {
                autoGpuImageParmsLayout(mView.getShowRelWidth(), mView.getShowRelHeight());
                mFirstWindowFouse = false;
            }
        }
    }

    public Bitmap getmCurrentBitmap() {
        return this.mCurrentBitmap;
    }

    public Bitmap getSrcBitmap() {
        return srcBitmap;
    }

    public void onDestroy() {
        gpuImageFilters = null;
        adjustMap = null;
        currentFilter = null;
        srcFilter = null;
        mCurrentBitmap.recycle();
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

}
