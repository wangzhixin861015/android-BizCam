package com.bcnetech.hyphoto.presenter;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;

import com.bcnetech.bcnetchhttp.config.Flag;
import com.bcnetech.bcnetechlibrary.util.ContentUtil;
import com.bcnetech.hyphoto.R;
import com.bcnetech.hyphoto.data.WaterMarkData;
import com.bcnetech.hyphoto.presenter.iview.IWaterMarkView;
import com.bcnetech.hyphoto.ui.activity.camera.CameraSettingActivity;
import com.bcnetech.hyphoto.ui.activity.camera.WaterMarkSettingActivity;
import com.bcnetech.hyphoto.ui.view.WaterMarkBottomView;
import com.bcnetech.hyphoto.utils.FileUtil;
import com.bcnetech.hyphoto.utils.ImageUtil;
import com.bcnetech.hyphoto.utils.StringUtil;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * author: wsBai
 * date: 2019/1/21
 */
public class WaterMarkPresenter extends BasePresenter<IWaterMarkView> {
    public static final int MAX_WM_COUNT = 8;
    public static final int TARGETNUM = 60;
    private List<WaterMarkData> wmlist;
    private WaterMarkData wm_bizcam;
    private WaterMarkData wm_plus;
    private Bitmap getbitmap;
    boolean isFromRecord = false;

    public static void startAction(Activity activity, int resultCode, boolean isFromRecord) {
        Intent intent = new Intent(activity, WaterMarkSettingActivity.class);
        if (isFromRecord)
            intent.putExtra("record", true);
        activity.startActivityForResult(intent, resultCode);
    }

    @Override
    public void onCreate(Bundle bundle) {

    }

    public void initWMList() {
        Intent intent;
        intent = new Intent();
        intent.putExtra("change", true);
        activity.setResult(CameraSettingActivity.REQUESE, intent);
        isFromRecord = activity.getIntent().getBooleanExtra("record", false);
        // 图片列表
        wmlist = new ArrayList<>();
        wmlist.add(wm_plus);
        File fileAll = new File(Flag.WATERMARK_PATH);
        File[] files = fileAll.listFiles();
        if (files != null) {
            List fileList = Arrays.asList(files);
            Collections.sort(fileList, new Comparator<File>() {
                @Override
                public int compare(File o1, File o2) {
                    if (o1.isDirectory() && o2.isFile())
                        return -1;
                    if (o1.isFile() && o2.isDirectory())
                        return 1;
                    return -(o1.getName().compareTo(o2.getName()));
                }
            });
            // 将所有的文件存入ArrayList中,并过滤所有图片格式的文件
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                if (checkIsImageFile(file.getPath())) {
                    WaterMarkData waterMarkData = new WaterMarkData(new BitmapDrawable(BitmapFactory.decodeFile(file.getPath())), file.getPath());
                    wmlist.add(waterMarkData);
                }
            }
        }
        wm_bizcam = new WaterMarkData(activity.getResources().getDrawable(R.drawable.wm_bizcam), null);
        wmlist.add(wm_bizcam);
        initSystemTag();
    }

    /**
     * 检查扩展名，得到图片格式的文件
     *
     * @param fName 文件名
     * @return
     */
    private boolean checkIsImageFile(String fName) {
        boolean isImageFile = false;
        // 获取扩展名
        String FileEnd = fName.substring(fName.lastIndexOf(".") + 1,
                fName.length()).toLowerCase();
        if (FileEnd.equals("jpg") || FileEnd.equals("png") || FileEnd.equals("gif")
                || FileEnd.equals("jpeg") || FileEnd.equals("bmp")) {
            isImageFile = true;
        } else {
            isImageFile = false;
        }
        return isImageFile;
    }

    /*
     * 初始化系统tag
     */
    public void initSystemTag() {
        mView.getWMFlowLayout().deleteAllView();
        //示例图片宽
        int DeviceWidth = ContentUtil.getScreenWidth(activity) - ContentUtil.dip2px(activity, 26) * 2;
        if (wmlist.size() > MAX_WM_COUNT) {
            wmlist.remove(0);
        }
        for (int i = 0; i < wmlist.size(); i++) {
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(DeviceWidth / 4 - 30, DeviceWidth / 4 - 30);
            WaterMarkData waterMarkData = wmlist.get(i);
            WaterMarkBottomView waterMarkBottomView = new WaterMarkBottomView(activity);
            waterMarkBottomView.setWaterMarkData(waterMarkData);
            lp.leftMargin = 15;
            lp.rightMargin = 15;
            lp.topMargin = 15;
            lp.bottomMargin = 15;
            mView.getWMFlowLayout().addView(waterMarkBottomView, lp);
        }

        mView.getWMFlowLayout().getChild();
    }

    @Override
    public void onDestroy() {
        wmlist.clear();
        wmlist = null;
        wm_bizcam = null;
        wm_plus = null;
        if (getbitmap != null)
            getbitmap.recycle();
    }

    public List<WaterMarkData> getWmlist() {
        return wmlist;
    }

    public WaterMarkData getWm_bizcam() {
        return wm_bizcam;
    }

    public WaterMarkData getWm_plus() {
        return wm_plus;
    }

    public boolean isFromRecord() {
        return isFromRecord;
    }

    /**
     * 获取相册和拍照uri的实际路径
     *
     * @param uri
     * @return
     */
    public String getRealFilePath(final Uri uri) {
        if (null == uri) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null)
            data = uri.getPath();
        else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = activity.getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;
    }

    public void loadAndCut(final String uri) {
        ImageUtil.EBizImageLoad(uri, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String s, View view) {

            }

            @Override
            public void onLoadingFailed(String s, View view, FailReason failReason) {

            }

            @Override
            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                if (bitmap.getHeight() > ContentUtil.dip2px(activity, TARGETNUM) || bitmap.getWidth() > ContentUtil.dip2px(activity, TARGETNUM))
                    ;
                float ratio = (float) bitmap.getWidth() / bitmap.getHeight();
                double targetWidth, targetHeight;
                if (bitmap.getWidth() > bitmap.getHeight()) {
                    targetWidth = ContentUtil.dip2px(activity, TARGETNUM);
                    targetHeight = targetWidth / ratio;
                } else {
                    targetHeight = ContentUtil.dip2px(activity, TARGETNUM);
                    targetWidth = targetHeight * ratio;
                }
                int bitmaporientation = 0;
                ExifInterface exifInterface = null;
                try {
                    exifInterface = new ExifInterface(uri.substring(7));
                    String orientation = exifInterface.getAttribute(ExifInterface.TAG_ORIENTATION);
                    if (!TextUtils.isEmpty(orientation)) {
                        switch (Integer.parseInt(orientation)) {
                            case ExifInterface.ORIENTATION_ROTATE_90:
                                bitmaporientation = 90;
                                break;
                            case ExifInterface.ORIENTATION_ROTATE_180:
                                bitmaporientation = 180;
                                break;
                            case ExifInterface.ORIENTATION_ROTATE_270:
                                bitmaporientation = 270;
                                break;
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                getbitmap = ImageUtil.resizeImage(bitmap, (int) targetWidth, (int) targetHeight, bitmaporientation);
                saveWm(getbitmap);
            }

            @Override
            public void onLoadingCancelled(String s, View view) {

            }
        });
    }

    private void saveWm(Bitmap bitmap) {
        String path = "";
        String bitmapName = System.currentTimeMillis() + "";
        try {
            path = FileUtil.saveWaterMark(bitmap, bitmapName);
        } catch (IOException e) {

        }
        if (!StringUtil.isBlank(path)) {
            mView.showMyWaterMark(path);
        }
    }


    public void deleteWM(WaterMarkData waterMarkData) {
        FileUtil.deleteImage(waterMarkData.getWatermarkurl());
    }

}
