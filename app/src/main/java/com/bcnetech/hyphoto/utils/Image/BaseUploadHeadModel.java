package com.bcnetech.hyphoto.utils.Image;


import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.view.View;

import com.bcnetech.bcnetechlibrary.util.LogUtil;
import com.bcnetech.bcnetechlibrary.util.StringUtil;
import com.bcnetech.bcnetechlibrary.util.ToastUtil;
import com.bcnetech.hyphoto.ui.activity.personCenter.CutActivity;
import com.bcnetech.hyphoto.utils.FileUtil;
import com.bcnetech.hyphoto.R;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * 上传图片逻辑块
 * <p>
 * Created by xuan on 15/9/23.
 */
public abstract class BaseUploadHeadModel {
    /**
     * 去相册
     */
    public static final int REQUEST_CODE_ALBUM = 1;
    /**
     * 去拍照
     */
    public static final int REQUEST_CODE_CAMERA = 2;
    /**
     * 相册返回裁剪
     */
    public static final int REQUEST_CODE_CUT_4_AlBUM = 3;
    /**
     * 拍照返回裁剪
     */
    public static final int REQUEST_CODE_CUT_4_CAMERA = 4;
    /**
     * 自定义裁剪
     **/
    public static final int REQUEST_CODE_CUT_CUSTOM = 5;
    protected Activity mActivity;
    protected Fragment mFragment;
    protected Handler handler;
    public Uri cameraSaveUri;
    private String murls;
    private File file;

    /**
     * 初始化头像上传
     *
     * @param headLayout
     */
    public void initForActivity(View headLayout, final Activity activity, Handler handler) {
        this.mActivity = activity;
        this.mFragment = null;
        this.handler = handler;
        createDir();

        if (null != headLayout) {
            headLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onShowDialog();
                }
            });
        }
    }

    /**
     * 初始化头像上传
     *
     * @param headLayout
     */
    public void initForFragment(View headLayout, final Fragment fragment) {
        this.mActivity = null;
        this.mFragment = fragment;
        createDir();

        if (null != headLayout) {
            headLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onShowDialog();
                }
            });
        }
    }


    /**
     * 处理返回结果
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        // 从相册获取照片，保存大图，并走去截图
        if (REQUEST_CODE_ALBUM == requestCode) {
            gotoCut(data.getData(), REQUEST_CODE_CUT_4_AlBUM, getBigHeadFilename(), REQUEST_CODE_ALBUM);
            return;
        }

        // 从拍照获取图片，保存大图，并走去截图
        if (REQUEST_CODE_CAMERA == requestCode) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    gotoCut(cameraSaveUri, REQUEST_CODE_CUT_4_CAMERA, getBigHeadFilename(), REQUEST_CODE_CAMERA);
                }
            }, 300);

            return;
        }
        // 截图后处理小图
        if (requestCode == REQUEST_CODE_CUT_4_AlBUM
                || requestCode == REQUEST_CODE_CUT_4_CAMERA) {

            Bitmap bigBitmap = null;
            Bitmap smallBitmap = null;
            try {
                // 生成小图保存
                bigBitmap = BitmapFactory.decodeFile(getBigHeadFilename());
                if (null != bigBitmap) {
                    smallBitmap = saveBitmap2FileName(bigBitmap, getSmallHeadFilename(), 100, 100);
                }
            } catch (Exception e) {
                LogUtil.d(e.getMessage(), e);
            }

            if (null != bigBitmap && null != smallBitmap) {
                // 上传头像
                onUploadHead2Server(bigBitmap, smallBitmap);
            } else {
                ToastUtil.toast(mActivity.getResources().getString(R.string.cuthead_fail));
            }

            if (requestCode == REQUEST_CODE_CUT_4_CAMERA) {
                // 如果是拍照来着就删除临时文件
                FileUtil.deleteImage(cameraSaveUri.getPath());
            }
        }

        if (REQUEST_CODE_CUT_CUSTOM == requestCode) {
            String finalurl = data.getStringExtra("mimage").substring(7);

            Bitmap bigBitmap = null;
            try {
                // 生成小图保存
                bigBitmap = BitmapFactory.decodeFile(finalurl);
            } catch (Exception e) {
                LogUtil.d(e.getMessage(), e);
            }

            if (null != bigBitmap) {
                // 上传头像
                murls = finalurl;
                onUploadHead2Server(bigBitmap, null);
            } else {
                ToastUtil.toast(mActivity.getResources().getString(R.string.cuthead_fail));
            }
        }
    }

    protected String getResultUrls() {
        return this.murls;
    }

    protected String getSmallHeadFilename() {
        return onSaveFilePath() + "small.jpg";
    }

    protected String getBigHeadFilename() {
        return onSaveFilePath() + "setScale.jpg";
    }

    protected String getTempFilename() {
        //return onSaveFilePath() + "temp.jpg";
        return onSaveFilePath();
    }

    private void createDir() {
        File saveDirFile = new File(onSaveFilePath());
        if (!saveDirFile.exists()) {
            saveDirFile.mkdirs();
        }

        FileUtil.deleteImage(getBigHeadFilename());
        FileUtil.deleteImage(getSmallHeadFilename());
        FileUtil.deleteImage(getTempFilename());
    }

    /**
     * 保存图片到本地
     *
     * @param fromBitmap
     * @param dest
     * @param newWidth
     * @param newHeight
     */
    private Bitmap saveBitmap2FileName(Bitmap fromBitmap, String dest, int newWidth, int newHeight) {
        Bitmap bitmapSrc = fromBitmap;

        int w = bitmapSrc.getWidth();
        int h = bitmapSrc.getHeight();

        // 若宽高小于指定最大值，不需重新绘制
        Bitmap bitmap = null;
        if (w <= newWidth && h <= newHeight) {
            bitmap = bitmapSrc;
        } else {
            float scale = ((float) newWidth / w) > ((float) newHeight / h) ? ((float) newHeight / h)
                    : ((float) newWidth / w);
            newWidth = (int) (w * scale);
            newHeight = (int) (h * scale);
            if (newWidth <= 0) {
                newWidth = 1;
            }
            if (newHeight <= 0) {
                newHeight = 1;
            }

            Matrix matrix = new Matrix();
            matrix.postScale(scale, scale);
            bitmap = Bitmap.createBitmap(bitmapSrc, 0, 0, w, h, matrix, true);
        }

        OutputStream out = null;
        try {
            File file = new File(dest);
            createParentDirs(file);
            out = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, out);
        } catch (Exception e) {
            LogUtil.d(e.getMessage());
        } finally {
            try {
                out.flush();
                out.close();
            } catch (IOException e) {
                // Ignore
            }
        }

        return bitmap;
    }

    private static void createParentDirs(File file) {
        File parentPath = file.getParentFile();
        if (!parentPath.exists() || !parentPath.isDirectory()) {
            parentPath.mkdirs();
        }
    }

    /**
     * 去相册
     */
    public void gotoAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION);
        startActivityForResult(intent, REQUEST_CODE_ALBUM);
    }

    /**
     * 去相机拍照返回图片
     */
    public void gotoCamera(Context context) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION);
        long time = System.currentTimeMillis();
        file = new File(getTempFilename() + time + ".jpg");
        File parentFile = file.getParentFile();
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }
        if (Build.VERSION.SDK_INT >= 24) {
            cameraSaveUri = FileProvider.getUriForFile(context, "com.bcnetech.hyphoto.FileProvider", file);
        } else {
            cameraSaveUri = (Uri.fromFile(file));
        }

//        cameraSaveUri = Uri.fromFile(file);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraSaveUri);
        startActivityForResult(intent, REQUEST_CODE_CAMERA);
    }

    /**
     * 去截图
     *
     * @param uri
     * @param requestCode
     * @param output
     */
    private void gotoCut(Uri uri, int requestCode, String output, int type) {
        if (null == uri) {
            return;
        }
        String url = getRealFilePath(uri, type);
        if (StringUtil.isBlank(url)) {
            ToastUtil.toast("获取失败");
            return;
        }
        // 如果没有文件夹，先创建之
        File file = new File(output);
        File fileDir = file.getParentFile();
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }

        int outputX = 600;
        int outputY = 600;

        if (requestCode == REQUEST_CODE_CUT_4_AlBUM) {
            outputX = 200;
            outputY = 200;
        }
       /* Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image*//*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", outputX);
        intent.putExtra("outputY", outputY);
        intent.putExtra("scale", true);
        intent.putExtra("scaleUpIfNeeded", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        intent.putExtra("return-data", false);*/
        Intent intent = new Intent(mActivity, CutActivity.class);
        intent.putExtra("murl", url);
        //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivityForResult(intent, REQUEST_CODE_CUT_CUSTOM);

        //startActivityForResult(intent, requestCode);
    }

    private void startActivityForResult(Intent intent, int requestCode) {
        if (null != mActivity) {
            mActivity.startActivityForResult(intent, requestCode);
        } else {
            mFragment.startActivityForResult(intent, requestCode);
        }
    }

    /**
     * 获取相册和拍照uri的实际路径
     *
     * @param uri
     * @return
     */
    public String getRealFilePath(final Uri uri, int type) {
        if (null == uri) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null)
            data = uri.getPath();
        else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {

            if (Build.VERSION.SDK_INT >= 24) {
                if (type == REQUEST_CODE_ALBUM) {
                    Cursor cursor = mActivity.getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
                    if (null != cursor) {
                        if (cursor.moveToFirst()) {
                            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                            if (index > -1) {
                                data = cursor.getString(index);
                            }
                        }
                        cursor.close();
                    }
                } else if (type == REQUEST_CODE_CAMERA) {
                    data = file.getAbsolutePath();
                }
            } else {
                Cursor cursor = mActivity.getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
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


        }
        return data;
    }

    /**
     * 选中图片后上传
     *
     * @param bigBitmap
     * @param smallBitmap
     */
    protected abstract void onUploadHead2Server(Bitmap bigBitmap, Bitmap smallBitmap);

    /**
     * 显示对话框
     */
    protected abstract void onShowDialog();

    /**
     * 拍照选择图片的保存路径
     *
     * @return
     */
    protected abstract String onSaveFilePath();

}