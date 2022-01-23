/*
 * Copyright (C) 2012 Lightbox
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bcnetech.hyphoto.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;

import com.bcnetech.bcnetchhttp.config.UrlConstants;
import com.bcnetech.bcnetechlibrary.util.ContentUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * BitmapUtils
 *
 * @author panyi
 */
public class BitmapUtils {
    public static final int MAXLENGTH = 2500;
    /**
     * Used to tag logs
     */
    @SuppressWarnings("unused")
    private static final String TAG = "BitmapUtils";

    public static final long MAX_SZIE = 1024 * 512;// 500KB

//    public static Bitmap loadImageByPath(final String imagePath, int reqWidth,
//                                         int reqHeight) {
//        File file = new File(imagePath);
//        if (file.length() < MAX_SZIE) {
//            return getSampledBitmap(imagePath, reqWidth, reqHeight);
//        } else {// 压缩图片
//            return getImageCompress(imagePath);
//        }
//    }

    public static int getOrientation(final String imagePath) {
        int rotate = 0;
        try {
            File imageFile = new File(imagePath);
            ExifInterface exif = new ExifInterface(imageFile.getAbsolutePath());
            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rotate;
    }


    /**
     * bizcan 特有
     *
     * @param bitmapId
     * @return
     */
    public static String getBitmapUrl(String bitmapId) {
        return UrlConstants.DOWNLOAD_PHOTO + bitmapId + ".jpg?code=0";
    }

    /**
     * bizcan 特有
     *
     * @param bitmapId
     * @return
     */
    public static String getBitmapUrl2(String bitmapId) {
        return UrlConstants.DOWNLOAD_PHOTO + bitmapId + ".png";
    }


    /**
     * bizcan 特有
     *
     * @param bitmapId
     * @return
     */
    public static String getBitmapUrl3(String bitmapId) {
        return UrlConstants.DOWNLOAD_PHOTO + bitmapId + ".png?code=0";
    }

    /**
     * bizcan 特有
     *
     * @param bitmapId
     * @return
     */
    public static String getBitmapUrl4(String bitmapId, String type) {
        return UrlConstants.DOWNLOAD_PHOTO + bitmapId + "." + type + "?code=0";
    }

    /**
     * bizcan 特有
     *
     * @param bitmapId
     * @return
     */
    public static String getBitmapUrl5(String bitmapId, String type) {
        return UrlConstants.DOWNLOAD_PHOTO + bitmapId + "." + type;
    }

    /**
     * bizcan 特有
     *
     * @param bitmapId
     * @return
     */
    public static String getBitmapUrl6(String bitmapId) {
        return UrlConstants.DOWNLOAD_PHOTO + bitmapId + ".jpg";
    }


    /**
     * bizcan 特有
     *
     * @param fileId
     * @return
     */
    public static String getDownloadFile(String fileId) {
        return UrlConstants.DOWNLOAD_FILE + fileId;
    }

    public static BitmapSize getBitmapSize(String filePath) {
        Options options = new Options();
        options.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(filePath, options);

        return new BitmapSize(options.outWidth, options.outHeight);
    }

    public static BitmapSize getScaledSize(int originalWidth,
                                           int originalHeight, int numPixels) {
        float ratio = (float) originalWidth / originalHeight;

        int scaledHeight = (int) Math.sqrt((float) numPixels / ratio);
        int scaledWidth = (int) (ratio * Math.sqrt((float) numPixels
                / ratio));

        return new BitmapSize(scaledWidth, scaledHeight);
    }

    public static class BitmapSize {
        public int width;
        public int height;

        public BitmapSize(int width, int height) {
            this.width = width;
            this.height = height;
        }
    }

    public static byte[] bitmapTobytes(Bitmap bitmap) {
        ByteArrayOutputStream a = new ByteArrayOutputStream();
        bitmap.compress(CompressFormat.PNG, 30, a);
        return a.toByteArray();
    }

    public static byte[] bitmapTobytesNoCompress(Bitmap bitmap) {
        ByteArrayOutputStream a = new ByteArrayOutputStream();
        bitmap.compress(CompressFormat.PNG, 100, a);
        return a.toByteArray();
    }

    public static Bitmap genRotateBitmap(byte[] data) {
        Bitmap bMap = BitmapFactory.decodeByteArray(data, 0, data.length);
        // 自定义相机拍照需要旋转90预览支持竖屏
        Matrix matrix = new Matrix();// 矩阵
        matrix.reset();// 设置为单位矩阵
        matrix.postRotate(90);// 旋转90度
        Bitmap bMapRotate = Bitmap.createBitmap(bMap, 0, 0, bMap.getWidth(),
                bMap.getHeight(), matrix, true);
        bMap.recycle();
        bMap = null;
        System.gc();
        return bMapRotate;
    }

    public static Bitmap byteToBitmap(byte[] data) {
        return BitmapFactory.decodeByteArray(data, 0, data.length);
    }

    /**
     * 将view转为bitmap
     *
     * @param view
     * @return
     */
    public static Bitmap getBitmapFromView(View view) {
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(),
                view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null)
            bgDrawable.draw(canvas);
        else
            canvas.drawColor(Color.WHITE);
        view.draw(canvas);
        return returnedBitmap;
    }

    // 按大小缩放
    public static Bitmap getImageCompress(final String srcPath) {
        Options newOpts = new Options();
        // 开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath.substring(7), newOpts);// 此时返回bm为空

        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        // 现在主流手机比较多是1920*1080分辨率，所以高和宽我们设置为
        float hh = 800;// 这里设置高度为1920f
        float ww = 800;// 这里设置宽度为1080f
        // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;// be=1表示不缩放
        if (w >= h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w <= h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;// 设置缩放比例
        // 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        bitmap = BitmapFactory.decodeFile(srcPath.substring(7), newOpts);
        return compressImage(bitmap);// 压缩好比例大小后再进行质量压缩

    }

    // 图片按比例大小压缩
    public static Bitmap compress(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(CompressFormat.JPEG, 100, baos);
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        Options newOpts = new Options();
        // 开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        // 现在主流手机比较多是1920*1080分辨率，所以高和宽我们设置为
        float hh = 800;// 这里设置高度为1920f
        float ww = 600;// 这里设置宽度为1080f
        // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;// be=1表示不缩放
        if (w >= h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w <= h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;// 设置缩放比例
        // 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        isBm = new ByteArrayInputStream(baos.toByteArray());
        bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        return compressImage(bitmap);// 压缩好比例大小后再进行质量压缩
    }

    // 图片按比例大小压缩
    public static Bitmap compress2(Bitmap image, Context context) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(CompressFormat.JPEG, 100, baos);
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        Options newOpts = new Options();
        // 开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        // 现在主流手机比较多是1920*1080分辨率，所以高和宽我们设置为
        //取手机分辨率
        float hh = ContentUtil.getScreenHeight(context);// 这里设置高度为1920f
        float ww = ContentUtil.getScreenWidth(context);// 这里设置宽度为1080f
        // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;// be=1表示不缩放
        if (w <= h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w >= h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;// 设置缩放比例
        // 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        isBm = new ByteArrayInputStream(baos.toByteArray());
        bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        return bitmap;// 压缩好比例大小后再进行质量压缩
    }

    // 图片质量压缩
    public static Bitmap compressImage(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 90;

        while (baos.toByteArray().length / 1024 > 300) { // 循环判断如果压缩后图片是否大于300kb,大于继续压缩

            baos.reset();// 重置baos即清空baos
            image.compress(CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;// 每次都减少10
//			System.out.println("options--->" + options + "    "
//					+ (baos.toByteArray().length / 1024));
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
        return bitmap;
    }

    // 图片质量压缩
    public static Bitmap compressImagePNG(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(CompressFormat.PNG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;

       /* while (baos.toByteArray().length / 1024 > 300) { // 循环判断如果压缩后图片是否大于200kb,大于继续压缩
            if (options<10){
                break;
            }
            baos.reset();// 重置baos即清空baos
            image.compress(CompressFormat.PNG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;// 每次都减少10
//			System.out.println("options--->" + options + "    "
//					+ (baos.toByteArray().length / 1024));
        }*/
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
        BitmapFactory.Options options1 = new BitmapFactory.Options();
        options1.inPreferredConfig = Bitmap.Config.ALPHA_8;
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
        return bitmap;
    }

    // 图片按比例大小压缩
    public static Bitmap compressPNG(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(CompressFormat.PNG, 100, baos);
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        Options newOpts = new Options();
        // 开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        // 现在主流手机比较多是1920*1080分辨率，所以高和宽我们设置为
        float hh = 500;// 这里设置高度为1920f
        float ww = 500;// 这里设置宽度为1080f
        // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;// be=1表示不缩放
        if (w >= h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w <= h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;// 设置缩放比例
        // 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        isBm = new ByteArrayInputStream(baos.toByteArray());
        bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        return bitmap;// 压缩好比例大小后再进行质量压缩
    }


    // 图片按比例大小压缩
    public static Bitmap compressPNGTwo(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(CompressFormat.PNG, 100, baos);
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        Options newOpts = new Options();
        // 开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        // 现在主流手机比较多是1920*1080分辨率，所以高和宽我们设置为
        float hh = 300;// 这里设置高度为1920f
        float ww = 300;// 这里设置宽度为1080f
        // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;// be=1表示不缩放
        if (w >= h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w <= h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;// 设置缩放比例
        // 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        isBm = new ByteArrayInputStream(baos.toByteArray());
        bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        return bitmap;// 压缩好比例大小后再进行质量压缩
    }

    public void printscreen_share(View v, Activity context) {
        View view1 = context.getWindow().getDecorView();
        Display display = context.getWindowManager().getDefaultDisplay();
        view1.layout(0, 0, display.getWidth(), display.getHeight());
        view1.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(view1.getDrawingCache());
    }

    // 图片转为文件
    public static boolean saveBitmap2file(Bitmap bmp, String filepath) {
        CompressFormat format = CompressFormat.PNG;
        int quality = 100;
        OutputStream stream = null;
        try {
            // 判断SDcard状态
            if (!Environment.MEDIA_MOUNTED.equals(Environment
                    .getExternalStorageState())) {
                // 错误提示
                return false;
            }

            // 检查SDcard空间
            File SDCardRoot = Environment.getExternalStorageDirectory();
            if (SDCardRoot.getFreeSpace() < 10000) {
                // 弹出对话框提示用户空间不够
                Log.e("Utils", "存储空间不够");
                return false;
            }

            // 在SDcard创建文件夹及文件
            File bitmapFile = new File(SDCardRoot.getPath() + filepath);
            bitmapFile.getParentFile().mkdirs();// 创建文件夹
            stream = new FileOutputStream(SDCardRoot.getPath() + filepath);// "/sdcard/"
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return bmp.compress(format, quality, stream);
    }

    /**
     * 截屏
     *
     * @param activity
     * @return
     */
    public static Bitmap getScreenViewBitmap(Activity activity) {
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();
        return bitmap;
    }

    /**
     * 一个 View的图像
     *
     * @param view
     * @return
     */
    public static Bitmap getViewBitmap(View view) {
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();
        return bitmap;
    }

    /**
     * 保存Bitmap图片到指定文件
     *
     * @param bm
     */
    public static void saveBitmap(Bitmap bm, String filePath) {
        File f = new File(filePath);
        if (f.exists()) {
            f.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(f);
            bm.compress(CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // System.out.println("保存文件--->" + f.getAbsolutePath());
    }


    /**
     * Resize a bitmap object to fit the passed width and height
     *
     * @param input      The bitmap to be resized
     * @param destWidth  Desired maximum width of the result bitmap
     * @param destHeight Desired maximum height of the result bitmap
     * @return A new resized bitmap
     * @throws OutOfMemoryError if the operation exceeds the available vm memory
     */
    public static Bitmap resizeBitmap(final Bitmap input, int destWidth, int destHeight, int rotation) throws OutOfMemoryError {

        int dstWidth = destWidth;
        int dstHeight = destHeight;
        final int srcWidth = input.getWidth();
        final int srcHeight = input.getHeight();

        if (rotation == 90 || rotation == 270) {
            dstWidth = destHeight;
            dstHeight = destWidth;
        }

        boolean needsResize = false;
        float p;
        if ((srcWidth > dstWidth) || (srcHeight > dstHeight)) {
            needsResize = true;
            if ((srcWidth > srcHeight) && (srcWidth > dstWidth)) {
                p = (float) dstWidth / (float) srcWidth;
                dstHeight = (int) (srcHeight * p);
            } else {
                p = (float) dstHeight / (float) srcHeight;
                dstWidth = (int) (srcWidth * p);
            }
        } else {
            dstWidth = srcWidth;
            dstHeight = srcHeight;
        }

        if (needsResize || rotation != 0) {
            Bitmap output;

            if (rotation == 0) {
                output = Bitmap.createScaledBitmap(input, dstWidth, dstHeight, true);
            } else {
                Matrix matrix = new Matrix();
                matrix.postScale((float) dstWidth / srcWidth, (float) dstHeight / srcHeight);
                matrix.postRotate(rotation);
                output = Bitmap.createBitmap(input, 0, 0, srcWidth, srcHeight, matrix, true);
            }
            return output;
        } else
            return input;
    }

    public static Bitmap rescaleBitmap(Bitmap bitmap) {
        float width = bitmap.getWidth();
        float height = bitmap.getHeight();
        if (width > height) {
            width = MAXLENGTH;
            height = MAXLENGTH * bitmap.getHeight() / bitmap.getWidth();
        } else {
            height = MAXLENGTH;
            width = MAXLENGTH * bitmap.getWidth() / bitmap.getHeight();
        }
        Bitmap newbitmap = Bitmap.createScaledBitmap(bitmap, (int) width, (int) height, true);
        if (newbitmap != null) {
           /* bitmap.recycle();*/
            return newbitmap;
        } else {
            return bitmap;
        }
    }


    /**
     * Resize a bitmap
     *
     * @param input
     * @param destWidth
     * @param destHeight
     * @return
     * @throws OutOfMemoryError
     */
    public static Bitmap resizeBitmap(final Bitmap input, int destWidth, int destHeight) throws OutOfMemoryError {
        return resizeBitmap(input, destWidth, destHeight, 0);
    }

    public static Bitmap getSampledBitmap(String filePath, int reqWidth, int reqHeight) {
        Options options = new Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        int inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inSampleSize = inSampleSize;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath, options);
    }

    /**
     * 把bitmap转换成String
     */
    public static String bitmapToString(Bitmap bm) {
        // Bitmap bm = getSmallBitmap(filePath, reqWidth, reqWidth);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 90, baos);
        byte[] b = baos.toByteArray();

        // bm.recycle();
        bm = null;
        return Base64.encodeToString(b, Base64.DEFAULT);
    }

    /**
     * 把bitmap转换成String
     */
    public static String bitmapToString100(Bitmap bm) {
        // Bitmap bm = getSmallBitmap(filePath, reqWidth, reqWidth);
//        Bitmap bitmap = compress(bm);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(CompressFormat.JPEG, 90, baos);
        byte[] b = baos.toByteArray();

//         bm.recycle();
//        bitmap.recycle();
//        bm = null;
//        bitmap = null;
        return Base64.encodeToString(b, Base64.DEFAULT);
    }

    /**
     * string转成bitmap
     *
     * @param st
     */
    public static Bitmap convertStringToIcon(String st) {
        // OutputStream out;
        Bitmap bitmap = null;
        try {
            // out = new FileOutputStream("/sdcard/aa.jpg");
            byte[] bitmapArray;
            bitmapArray = Base64.decode(st, Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0,
                    bitmapArray.length);
            // bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            return bitmap;
        } catch (Exception e) {
            return null;
        }
    }


    public static int calculateInSampleSize(Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static int dpToPx(Context context, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {

        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();
        Bitmap.Config config =
                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                        : Bitmap.Config.RGB_565;
        Bitmap bitmap = Bitmap.createBitmap(w, h, config);
        //注意，下面三行代码要用到，否则在View或者SurfaceView里的canvas.drawBitmap会看不到图
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        drawable.draw(canvas);

        return bitmap;
    }

    public static Bitmap yuvToBitmap(byte[] data, int width, int height) {
        int frameSize = width * height;
        int[] rgba = new int[frameSize];
        for (int i = 0; i < height; i++)
            for (int j = 0; j < width; j++) {
                int y = (0xff & ((int) data[i * width + j]));
                int u = (0xff & ((int) data[frameSize + (i >> 1) * width + (j & ~1) + 0]));
                int v = (0xff & ((int) data[frameSize + (i >> 1) * width + (j & ~1) + 1]));
                y = y < 16 ? 16 : y;
                int r = Math.round(1.164f * (y - 16) + 1.596f * (v - 128));
                int g = Math.round(1.164f * (y - 16) - 0.813f * (v - 128) - 0.391f * (u - 128));
                int b = Math.round(1.164f * (y - 16) + 2.018f * (u - 128));
                r = r < 0 ? 0 : (r > 255 ? 255 : r);
                g = g < 0 ? 0 : (g > 255 ? 255 : g);
                b = b < 0 ? 0 : (b > 255 ? 255 : b);
                rgba[i * width + j] = 0xff000000 + (b << 16) + (g << 8) + r;
            }
        Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bmp.setPixels(rgba, 0, width, 0, 0, width, height);
        return bmp;
    }
}
