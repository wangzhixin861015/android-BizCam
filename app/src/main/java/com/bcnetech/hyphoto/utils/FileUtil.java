package com.bcnetech.hyphoto.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import com.bcnetech.bcnetchhttp.config.Flag;
import com.bcnetech.bcnetechlibrary.util.IOUtil;
import com.bcnetech.bcnetechlibrary.util.LogUtil;
import com.bcnetech.hyphoto.App;
import com.bcnetech.bizcamerlibrary.camera.data.CameraStatus;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.Buffer;
import java.nio.IntBuffer;
import java.text.DecimalFormat;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Created by dd on 16/1/13.
 * 文件读取
 */
public class FileUtil {

    private Context context;
    public static final int SIZETYPE_B = 1;//获取文件大小单位为B的double值
    public static final int SIZETYPE_KB = 2;//获取文件大小单位为KB的double值
    public static final int SIZETYPE_MB = 3;//获取文件大小单位为MB的double值
    public static final int SIZETYPE_GB = 4;//获取文件大小单位为GB的double值

    public static final int SIZETYPE_0 = 1;//获取文件大小单位为B的double值
    public static final int SIZETYPE_00 = 2;//获取文件大小单位为KB的double值

    public FileUtil(Context context) {
        this.context = context;
    }

    //读取模板路径文件
    public String readAsset(String fileName) {
        AssetManager am = context.getAssets();

        String data = "";

        InputStream is = null;
        try {
            is = am.open(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        data = readDataFromInputStream(is);
        try {
            if (is != null) {
                is.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return data;
    }

    private String readDataFromInputStream(InputStream is) {
        BufferedInputStream bis = new BufferedInputStream(is);

        String str = "", s = "";

        int c = 0;
        byte[] buf = new byte[1024];
        while (true) {
            try {
                c = bis.read(buf);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (c == -1)
                break;
            else {
                try {
                    s = new String(buf, 0, c, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                str += s;
            }
        }

        try {
            bis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return str;
    }


    /**
     * 复制单个文件
     *
     * @param oldPath String 原文件路径 如：c:/fqf.txt
     * @return boolean
     */
    public static String copyFile(String oldPath, String url, String name) {
        if (url == null) {
            url = Flag.NATIVESDFILE2;
        }
        try {
            File dirFile = new File(url);
            if (!dirFile.exists()) {
                dirFile.mkdirs();
            }
            int bytesum = 0;
            int byteread = 0;
            if (oldPath.contains("file://")) {
                oldPath = oldPath.substring(7);
            }
            File oldfile = new File(oldPath);
            if (name == null) {
                name = oldfile.getName();
            }
            String newPath = url + name;
            File newfile = new File(newPath);
            if (oldfile.exists()) { //文件存在时
                if (newfile.exists()) {
                    return "exist";
                } else {
                    newfile.createNewFile();
                }

                InputStream inStream = new FileInputStream(oldfile); //读入原文件
                FileOutputStream fs = new FileOutputStream(newfile);
                byte[] buffer = new byte[1444];
                int length;
                while ((byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; //字节数 文件大小
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
            }
            return newPath;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "error";
    }

    /**
     * 保存预设参数
     *
     * @param bmp
     * @param bitName
     * @return
     * @throws IOException
     */
    public static boolean saveMyPreset(Bitmap bmp, String bitName) throws IOException {
        File dirFile = new File(Flag.PRESET_IMAGE_PATH);
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }
        File f = new File(Flag.PRESET_IMAGE_PATH + bitName + ".jpg");
        boolean flag = false;
        f.createNewFile();

        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(f);
            bmp.compress(Bitmap.CompressFormat.JPEG, 70, fOut);
            flag = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            fOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return flag;
    }


    //保存图片文件(弃)
    public static boolean saveMyBitmap(Bitmap bmp, String bitName) throws IOException {
        File dirFile = new File(Flag.APP_CAMERAL);
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }
        File f = new File(Flag.APP_CAMERAL + bitName + ".png");
        boolean flag = false;
        f.createNewFile();
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(f);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            flag = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            fOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 将最终的图片单独保存到文件夹中(/Bizcam)
     *
     * @param bmp
     * @param bitName
     * @return
     * @throws IOException
     */
    public static String saveBitmaptoNative(Bitmap bmp, String bitName, boolean isPNG) throws IOException {
        File dirFile = new File(Flag.NATIVESDFILE);
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }
        // String mbitname = bitName.substring(bitName.lastIndexOf("/")+1,bitName.length());
        File f;
        if (isPNG) {
            f = new File(Flag.NATIVESDFILE + bitName + ".png");
        } else {
            f = new File(Flag.NATIVESDFILE + bitName + ".jpg");
        }

        boolean flag = false;
        f.createNewFile();
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(f);
            if (isPNG) {
                bmp.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            } else {
                bmp.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
            }

            flag = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        try {
            fOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        try {
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        if (isPNG) {
            return Flag.NATIVESDFILE + bitName + ".png";
        } else {
            return Flag.NATIVESDFILE + bitName + ".jpg";
        }

    }

    /**
     * 将最终的视频图片单独保存到文件夹中(/Bizcam)
     *
     * @param bmp
     * @param bitName
     * @return
     * @throws IOException
     */
    public static String saveVideoCover(Bitmap bmp, String bitName) throws IOException {
        File dirFile = new File(Flag.NATIVESDFILE);
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }
        File f;
        f = new File(Flag.NATIVESDFILE + bitName + ".jpg");

        f.createNewFile();
        FileOutputStream fOut = new FileOutputStream(f);
        try {
            fOut = new FileOutputStream(f);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        try {
            fOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        try {
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return Flag.NATIVESDFILE + bitName + ".jpg";

    }

    /**
     * 将最终的图片单独保存到文件夹中(/Bizcam)
     *
     * @param bmp
     * @param bitName
     * @return
     * @throws IOException
     */
    public static String saveBitmaptoShareNative(Bitmap bmp, String bitName, boolean isPNG) throws IOException {
        File dirFile = new File(Flag.SHARE_PATH);
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }
        // String mbitname = bitName.substring(bitName.lastIndexOf("/")+1,bitName.length());
        File f;
        if (isPNG) {
            f = new File(Flag.SHARE_PATH + bitName + ".png");
        } else {
            f = new File(Flag.SHARE_PATH + bitName + ".jpg");
        }

        boolean flag = false;
        f.createNewFile();
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(f);
            if (isPNG) {
                bmp.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            } else {
                bmp.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
            }

            flag = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        try {
            fOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        try {
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        if (isPNG) {
            return Flag.SHARE_PATH + bitName + ".png";
        } else {
            return Flag.SHARE_PATH + bitName + ".jpg";
        }

    }

    /**
     * 保存水印(/WaterMark)
     *
     * @param bmp
     * @param bitName
     * @return
     * @throws IOException
     */
    public static String saveWaterMark(Bitmap bmp, String bitName) throws IOException {
        File dirFile = new File(Flag.WATERMARK_PATH);
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }
        // String mbitname = bitName.substring(bitName.lastIndexOf("/")+1,bitName.length());
        File f;
        f = new File(Flag.WATERMARK_PATH + bitName + ".png");
        boolean flag = false;
        f.createNewFile();
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(f);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            flag = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        try {
            fOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        try {
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return Flag.WATERMARK_PATH + bitName + ".png";
    }

    //保存图片文件(android/data/)
    public static String saveBitmap(Bitmap bmp, String bitName, boolean isPNG) throws IOException {
        File dirFile = new File(Flag.APP_CAMERAL);
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }
        File f;
        if (isPNG) {
            f = new File(Flag.APP_CAMERAL + bitName + ".png");
        } else {
            f = new File(Flag.APP_CAMERAL + bitName + ".jpg");
        }

        boolean flag = false;
        f.createNewFile();
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(f);
            if (isPNG) {
                bmp.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            } else {
                bmp.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
            }

            flag = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        try {
            fOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        try {
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        if (isPNG) {
            return Flag.APP_CAMERAL + bitName + ".png";
        } else {
            return Flag.APP_CAMERAL + bitName + ".jpg";
        }

    }

    //保存图片文件
    public static String saveUploadBitmap(Bitmap bmp, String bitName) throws IOException {
        File dirFile = new File(Flag.UPLOAD_PATH);
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }
        File f = new File(Flag.UPLOAD_PATH + bitName + ".jpg");
        boolean flag = false;
        f.createNewFile();
      /*  FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(f);
            bmp.compress(Bitmap.CompressFormat.JPEG, 97, fOut);
            flag = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            fOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int options = 97;
        bmp.compress(Bitmap.CompressFormat.JPEG, options, baos);
        if (baos.toByteArray().length / 1024 < 100) {
            baos.reset();
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        }
       /* while (baos.toByteArray().length / 1024 > 100) {
            baos.reset();
            options -= 10;
            bmp.compress(Bitmap.CompressFormat.JPEG, options, baos);
        }*/
        try {
            FileOutputStream fos = new FileOutputStream(f);
            fos.write(baos.toByteArray());
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Flag.UPLOAD_PATH + bitName + ".jpg";
    }


    //保存头像图片文件
    public static String saveBitmap2(Bitmap bmp, String bitName) throws IOException {
        File dirFile = new File(Flag.PERSION_IMAGE);
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }
        File f = new File(Flag.PERSION_IMAGE + bitName + ".jpg");
        boolean flag = false;
        f.createNewFile();
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(f);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
            flag = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            fOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Flag.PERSION_IMAGE + bitName + ".jpg";
    }


    //保存图片文件(分享参数)
    public static String saveBitmap3(Bitmap bmp, String bitName) throws IOException {
        File dirFile = new File(Flag.PERSION_IMAGE);
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }
        File f = new File(Flag.PERSION_IMAGE + bitName + ".jpg");
        boolean flag = false;
        f.createNewFile();
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(f);
            bmp.compress(Bitmap.CompressFormat.JPEG, 90, fOut);
            flag = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            fOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Flag.PERSION_IMAGE + bitName + ".jpg";
    }

    //保存图片文件(意见反馈)
    public static String saveFeedBackBitmap(Bitmap bmp, String bitName) throws IOException {
        File dirFile = new File(Flag.FEEDBACK_IMAGE);
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }
        File f = new File(Flag.FEEDBACK_IMAGE + bitName + ".jpg");
        boolean flag = false;
        f.createNewFile();
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(f);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
            flag = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            fOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Flag.FEEDBACK_IMAGE + bitName + ".jpg";
    }

    //图片暂时缓存地址
    public static String cacheSaveBitmap(Bitmap bmp, String bitName, boolean isPNG) throws IOException {
        File dirFile = new File(Flag.LOCAL_IMAGE_PATH);
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }
        File f;
        if (isPNG) {
            f = new File(Flag.LOCAL_IMAGE_PATH + bitName + ".png");
        } else {
            f = new File(Flag.LOCAL_IMAGE_PATH + bitName + ".jpg");
        }

        boolean flag = false;
        f.createNewFile();
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(f);
            if (isPNG) {
                bmp.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            } else {
                bmp.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
            }

            flag = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        try {
            fOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        try {
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        if (isPNG) {
            return Flag.LOCAL_IMAGE_PATH + bitName + ".png";
        } else {
            return Flag.LOCAL_IMAGE_PATH + bitName + ".jpg";
        }
    }

    public static String savecustomBitmap(Bitmap bmp, String saveUrl, String fileName) throws IOException {
        File dirFile = new File(Flag.PERSION_IMAGE);
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }
        File f = new File(saveUrl + fileName);
        boolean flag = false;
        f.createNewFile();
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(f);
            bmp.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
            flag = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            fOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return f.getAbsolutePath();
    }


    public static Bitmap getBitmap(byte[] data) {
        Bitmap croppedImage;

        //获得图片大小
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(data, 0, data.length, options);
        options.inJustDecodeBounds = false;
        Rect r;
        r = new Rect(0, 0, options.outWidth, options.outHeight);
        try {
            croppedImage = decodeRegionCrop(data, r);
        } catch (Exception e) {
            return null;
        }
        return croppedImage;
    }

    /**
     * 存储拍照图片(android/data/)
     *
     * @param bitmapsrc
     * @param isRect
     * @param rotate
     * @param time
     * @return
     */
    public static String saveBitmap(Bitmap bitmapsrc, boolean isRect, int rotate, String time, boolean isPng, boolean iswatermark, Bitmap watermark) {
        String path = "";
        try {
            Matrix matrix = new Matrix();
            matrix.postRotate(rotate);// 这里的rgb就是刚刚转换处理的东东
            if (isRect) {
              /*  if (bitmapsrc.getWidth() > bitmapsrc.getHeight()) {
                    bitmapsrc = Bitmap.createBitmap(bitmapsrc,
                            ((bitmapsrc.getWidth() - bitmapsrc.getHeight()) / 2), 0, bitmapsrc.getHeight(), bitmapsrc.getHeight(), matrix, true);
                } else {
                    bitmapsrc = Bitmap.createBitmap(bitmapsrc,
                            0, (bitmapsrc.getHeight() - bitmapsrc.getWidth()) / 2, bitmapsrc.getWidth(), bitmapsrc.getWidth(), matrix, true);
                }*/
                if (bitmapsrc.getWidth() > bitmapsrc.getHeight()) {
                    bitmapsrc = Bitmap.createBitmap(bitmapsrc,
                            0, 0, bitmapsrc.getHeight(), bitmapsrc.getHeight(), matrix, true);
                } else {
                    bitmapsrc = Bitmap.createBitmap(bitmapsrc,
                            0, 0, bitmapsrc.getWidth(), bitmapsrc.getWidth(), matrix, true);
                }
            } else {
                bitmapsrc = Bitmap.createBitmap(bitmapsrc,
                        0, 0, bitmapsrc.getWidth(), bitmapsrc.getHeight(), matrix, true);
            }
            if (iswatermark) {
                if (watermark != null) {
                    int padding = 0;
                    bitmapsrc = WaterMarkUtil.createWaterMaskRightBottom(bitmapsrc, watermark);
                }
            }//bitmapsrc = Bitmap.createBitmap(bitmapsrc, 0, 0, bitmapsrc.getWidth(), bitmapsrc.getHeight(), matrix, true);
            path = FileUtil.saveBitmap(bitmapsrc, "" + time, isPng);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return path;
    }


    /**
     * 存储拍照图片_New(android/data/)
     *
     * @param bitmapsrc
     * @param rotate
     * @param time
     * @return
     */
    public static String saveBitmapNew(Bitmap bitmapsrc, int rotate, String time, CameraStatus.Size size, boolean isPng, boolean iswatermark, Bitmap watermark, boolean isAiCamera) {
        String path = "";
        try {
            Matrix matrix = new Matrix();
            matrix.postRotate(rotate);// 这里的rgb就是刚刚转换处理的东东
            if (!isAiCamera) {
                switch (size) {
                    case TYPE_11:
                        int x  = (Math.max(bitmapsrc.getWidth(), bitmapsrc.getHeight())-Math.min(bitmapsrc.getWidth(), bitmapsrc.getHeight()))/2;
                        bitmapsrc = Bitmap.createBitmap(bitmapsrc, x, 0, Math.min(bitmapsrc.getWidth(), bitmapsrc.getHeight()), Math.min(bitmapsrc.getWidth(), bitmapsrc.getHeight()), matrix, false);
                        break;
                    case TYPE_34:
                       /* double long_length = Math.max(bitmapsrc.getWidth(), bitmapsrc.getHeight());
                        double width, height;
                        Size msize;
                        if (long_length == bitmapsrc.getWidth()) {
                            height = bitmapsrc.getHeight();
                            long_length *= (double) (0.75f);
                            msize = new Size((int) long_length, (int) height);
                        } else {
                            height = bitmapsrc.getWidth();
                            long_length *= (double) (0.75f);
                            msize = new Size((int) height, (int) long_length);
                        }

                        bitmapsrc = Bitmap.createBitmap(bitmapsrc, 0, 0, msize.getWidth(), msize.getHeight(), matrix, false);
                        break;*/
                    case TYPE_916:
                        bitmapsrc = Bitmap.createBitmap(bitmapsrc, 0, 0, bitmapsrc.getWidth(), bitmapsrc.getHeight(), matrix, false);

                        break;
                }
            } else {
                bitmapsrc = Bitmap.createBitmap(bitmapsrc, 0, 0, bitmapsrc.getWidth(), bitmapsrc.getHeight(), matrix, false);
            }
            if (iswatermark) {
                if (watermark != null) {
                    int padding = 0;
                    bitmapsrc = WaterMarkUtil.createWaterMaskRightBottom(bitmapsrc, watermark);
                }
            }//bitmapsrc = Bitmap.createBitmap(bitmapsrc, 0, 0, bitmapsrc.getWidth(), bitmapsrc.getHeight(), matrix, true);
            path = FileUtil.saveBitmap(bitmapsrc, "" + time, isPng);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return path;
    }

    /**
     * 存储拍照图片_New(/BizCam)
     *
     * @param bitmapsrc
     * @param rotate
     * @param time
     * @return
     */
    public static String saveBitmap2New(Bitmap bitmapsrc, int rotate, String time, CameraStatus.Size size, boolean isPng, boolean iswatermark, Bitmap watermark, boolean isAiCamera) {
        String path = "";
        try {
            Matrix matrix = new Matrix();
            matrix.postRotate(rotate);// 这里的rgb就是刚刚转换处理的东东
            if (!isAiCamera) {
                switch (size) {
                    case TYPE_11:
                        int x  = (Math.max(bitmapsrc.getWidth(), bitmapsrc.getHeight())-Math.min(bitmapsrc.getWidth(), bitmapsrc.getHeight()))/2;
                        bitmapsrc = Bitmap.createBitmap(bitmapsrc, x, 0, Math.min(bitmapsrc.getWidth(), bitmapsrc.getHeight()), Math.min(bitmapsrc.getWidth(), bitmapsrc.getHeight()), matrix, false);
                        break;
                    case TYPE_34:
                       /* double long_length = Math.max(bitmapsrc.getWidth(), bitmapsrc.getHeight());
                        double width, height;
                        Size msize;
                        if (long_length == bitmapsrc.getWidth()) {
                            height = bitmapsrc.getHeight();
                            long_length *= (double) (0.75f);
                            msize = new Size((int) long_length, (int) height);
                        } else {
                            height = bitmapsrc.getWidth();
                            long_length *= (double) (0.75f);
                            msize = new Size((int) height, (int) long_length);
                        }

                        bitmapsrc = Bitmap.createBitmap(bitmapsrc, 0, 0, msize.getWidth(), msize.getHeight(), matrix, false);
                        break;*/
                    case TYPE_916:
                        bitmapsrc = Bitmap.createBitmap(bitmapsrc, 0, 0, bitmapsrc.getWidth(), bitmapsrc.getHeight(), matrix, false);

                        break;
                }
            } else {
                bitmapsrc = Bitmap.createBitmap(bitmapsrc, 0, 0, bitmapsrc.getWidth(), bitmapsrc.getHeight(), matrix, false);
            }
            if (iswatermark) {
                bitmapsrc = WaterMarkUtil.createWaterMaskRightBottom(bitmapsrc, watermark);
            }
            path = FileUtil.saveBitmaptoNative(bitmapsrc, "" + time, isPng);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return path;
    }

    /**
     * 存储拍照图片(/BizCam)
     *
     * @param bitmapsrc
     * @param isRect
     * @param rotate
     * @param time
     * @return
     */
    public static String saveBitmap2(Bitmap bitmapsrc, boolean isRect, int rotate, String time, boolean isPng, boolean iswatermark, Bitmap watermark) {
        String path = "";
        try {
            Matrix matrix = new Matrix();
            matrix.postRotate(rotate);// 这里的rgb就是刚刚转换处理的东东
            if (isRect) {
                /*if (bitmapsrc.getWidth() > bitmapsrc.getHeight()) {
                    bitmapsrc = Bitmap.createBitmap(bitmapsrc,
                            ((bitmapsrc.getWidth() - bitmapsrc.getHeight()) / 2), 0, bitmapsrc.getHeight(), bitmapsrc.getHeight(), matrix, true);
                } else {
                    bitmapsrc = Bitmap.createBitmap(bitmapsrc,
                            0, (bitmapsrc.getHeight() - bitmapsrc.getWidth()) / 2, bitmapsrc.getWidth(), bitmapsrc.getWidth(), matrix, true);
                }*/
                if (bitmapsrc.getWidth() > bitmapsrc.getHeight()) {
                    bitmapsrc = Bitmap.createBitmap(bitmapsrc,
                            0, 0, bitmapsrc.getHeight(), bitmapsrc.getHeight(), matrix, true);
                } else {
                    bitmapsrc = Bitmap.createBitmap(bitmapsrc,
                            0, 0, bitmapsrc.getWidth(), bitmapsrc.getWidth(), matrix, true);
                }
            } else {
                bitmapsrc = Bitmap.createBitmap(bitmapsrc,
                        0, 0, bitmapsrc.getWidth(), bitmapsrc.getHeight(), matrix, true);
            }
            if (iswatermark) {
                bitmapsrc = WaterMarkUtil.createWaterMaskRightBottom(bitmapsrc, watermark);
            }
            path = FileUtil.saveBitmaptoNative(bitmapsrc, "" + time, isPng);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return path;
    }

    /**
     * 存储上传用的小图图片
     *
     * @param bitmapsrc
     * @param isRect
     * @param rotate
     * @param time
     * @return
     */
    public static String saveUploadBitmap(Bitmap bitmapsrc, boolean isRect, int rotate, String time) {
        String path = "";
        try {
            Matrix matrix = new Matrix();
            matrix.postRotate(rotate);// 这里的rgb就是刚刚转换处理的东东
            if (isRect) {
                if (bitmapsrc.getWidth() > bitmapsrc.getHeight()) {
                    bitmapsrc = Bitmap.createBitmap(bitmapsrc,
                            ((bitmapsrc.getWidth() - bitmapsrc.getHeight()) / 2), 0, bitmapsrc.getHeight(), bitmapsrc.getHeight(), matrix, true);
                } else {
                    bitmapsrc = Bitmap.createBitmap(bitmapsrc,
                            0, (bitmapsrc.getHeight() - bitmapsrc.getWidth()) / 2, bitmapsrc.getWidth(), bitmapsrc.getWidth(), matrix, true);
                }
            } else {
                bitmapsrc = Bitmap.createBitmap(bitmapsrc,
                        0, 0, bitmapsrc.getWidth(), bitmapsrc.getHeight(), matrix, true);
            }
            int w = 0;
            int h = 0;
            double ration = 0;
            if (bitmapsrc.getWidth() >= bitmapsrc.getHeight()) {
                ration = (double) bitmapsrc.getWidth() / (double) 600;
                w = 600;
                h = (int) (bitmapsrc.getHeight() / ration);
            } else {
                ration = (double) bitmapsrc.getHeight() / (double) 600;
                h = 600;
                w = (int) (bitmapsrc.getWidth() / ration);
            }
            bitmapsrc = ImageUtil.resizeImage(bitmapsrc, w, h, 0);
            path = FileUtil.saveUploadBitmap(bitmapsrc, "" + time);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return path;
    }

    public static void saveImageDataJson(String url, String fileName, String imageData) throws IOException {
        File dirFile = new File(url);
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }
        File f = new File(url + fileName + ".json");
        f.createNewFile();
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(f);
            fOut.write(imageData.getBytes());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            fOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Bitmap decodeRegionCrop(byte[] data, Rect rect) {

        InputStream is = null;
        System.gc();
        Bitmap croppedImage = null;
        try {
            is = new ByteArrayInputStream(data);
            BitmapRegionDecoder decoder = BitmapRegionDecoder.newInstance(is, false);
            try {
                croppedImage = decoder.decodeRegion(rect, new BitmapFactory.Options());
            } catch (IllegalArgumentException e) {
            }
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            IOUtil.closeStream(is);
        }

        return croppedImage;
    }

    public static boolean deleteImage(String imgPath) {
        ContentResolver resolver = App.getInstance().getContentResolver();
        Cursor cursor = MediaStore.Images.Media.query(resolver, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Images.Media._ID}, MediaStore.Images.Media.DATA + "=?",
                new String[]{imgPath}, null);
        boolean result = false;
        if (cursor.moveToFirst()) {
            long id = cursor.getLong(0);
            Uri contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            Uri uri = ContentUris.withAppendedId(contentUri, id);
            int count = App.getInstance().getContentResolver().delete(uri, null, null);
            result = count == 1;
        } else {
            File file = new File(imgPath);
            result = file.delete();
        }
        return result;
    }


    /*
     *把文件插入到系统图库
     */
    public static void saveImageInCamera(Activity activity, Bitmap bitmap) {
        MediaStore.Images.Media.insertImage(activity.getContentResolver(), bitmap, System.currentTimeMillis() + ".jpg", null);
    }


    /**
     * 获取文件夹数据大小
     */
    public static long getFolderSize(java.io.File file) {

        long size = 0;
        try {
            java.io.File[] fileList = file.listFiles();
            for (int i = 0; i < fileList.length; i++) {
                if (fileList[i].isDirectory()) {
                    size = size + getFolderSize(fileList[i]);

                } else {
                    size = size + fileList[i].length();

                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return size;
    }

    public static String convertFileSize(long size) {
        long kb = 1024;
        long mb = kb * 1024;
        long gb = mb * 1024;

        if (size >= gb) {
            return String.format("%.1f GB", (float) size / gb);
        } else if (size >= mb) {
            float f = (float) size / mb;
            return String.format(f > 100 ? "%.0f MB" : "%.1f MB", f);
        } else if (size >= kb) {
            float f = (float) size / kb;
            return String.format(f > 100 ? "%.0f KB" : "%.1f KB", f);
        } else
            return String.format("%d B", size);
    }


    public static boolean fileIsExists(String name) {
        File dirFile = new File(Flag.PRESET_IMAGE_PATH);
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }
        try {
            File f = new File(Flag.PRESET_IMAGE_PATH + name + ".jpg");
            if (!f.exists()) {
                return false;
            }

        } catch (Exception e) {
            // TODO: handle exception
            return false;
        }
        return true;
    }

    public static String getFileOrFilesSize(String filePath, int sizeType) {
        File file = new File(filePath);
        long blockSize = 0;
        if (file.isFile()) {
            try {
                blockSize = getFileSize(file);
            } catch (Exception e) {
                e.printStackTrace();

            }
        } else if (file.isDirectory()) {
            try {
                blockSize = getFolderSize(file);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return FormetFileSize(blockSize, sizeType);
    }

    /**
     * 获取文件大小
     *
     * @param file
     * @return
     * @throws Exception
     */
    private static long getFileSize(File file) throws Exception {
        long size = 0;
        if (file.exists()) {
            FileInputStream fis = null;
            fis = new FileInputStream(file);
            size = fis.available();
        } else {
            file.createNewFile();

        }
        return size;
    }


    //删除文件夹和文件夹里面的文件
    public static void deleteDir(final String pPath) {
        try {
            File dir = new File(pPath);
            deleteDirWihtFile(dir);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteDirWihtFile(File dir) {
        if (dir == null || !dir.exists() || !dir.isDirectory())
            return;
        for (File file : dir.listFiles()) {
            if (file.isFile())
                file.delete(); // 删除所有文件
            else if (file.isDirectory())
                deleteDirWihtFile(file); // 递规的方式删除文件夹
        }
        dir.delete();// 删除目录本身
    }

    private static String FormetFileSize(long fileS, int sizeType) {
        DecimalFormat df = (DecimalFormat) DecimalFormat.getInstance(Locale.US);
//        DecimalFormat df = new DecimalFormat("#.00");
        df.applyPattern("#0.00");
        String fileSizeLong = "";
        switch (sizeType) {
            case SIZETYPE_B:
                fileSizeLong = df.format((double) fileS);
                break;
            case SIZETYPE_KB:
                fileSizeLong = df.format((double) fileS / 1024);
                break;
            case SIZETYPE_MB:
                fileSizeLong = df.format((double) fileS / 1048576);
                break;
            case SIZETYPE_GB:
                fileSizeLong = df.format((double) fileS / 1073741824);
                break;
            default:
                break;
        }
        return fileSizeLong;
    }

    public static File saveByte2File(byte[] data, String path, String name) {

        File dirFile = new File(path);
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }

        File f = new File(dirFile.getPath() + "/" + name + ".jpg");

        try {
            FileOutputStream fos = new FileOutputStream(f);
            fos.write(data);
            fos.close();
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        }

        return f;
    }


    public static String formatSize(double data, int sizeType) {
        DecimalFormat df = (DecimalFormat) DecimalFormat.getInstance(Locale.US);
        switch (sizeType) {
            case SIZETYPE_0:
                df.applyPattern("#0.0");
                break;
            case SIZETYPE_00:
                df.applyPattern("#0.00");
                break;
        }
        return df.format(data);
    }


    /**
     * 下载图片(android/data/)
     *
     * @param bitmapsrc
     * @param
     * @param time
     * @return
     */
    public static String saveBitmap(Bitmap bitmapsrc, String time, Context context, String type) {
        String path = "";
        try {

            path = FileUtil.saveBitmap(bitmapsrc, "" + time, type);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return path;
    }

    //保存图片文件(android/data/)
    public static String saveBitmap(Bitmap bmp, String bitName, String type) throws IOException {
        File dirFile = new File(Flag.APP_CAMERAL);
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }
        File f = new File(Flag.APP_CAMERAL + bitName + "." + type);


        boolean flag = false;
        f.createNewFile();
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(f);

            flag = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        try {
            fOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        try {
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return Flag.APP_CAMERAL + bitName + "." + type;
    }

    /**
     * 获取文件类型
     *
     * @param filePath
     * @return
     */
    public static String getImageType(String filePath) {
        FileInputStream is = null;
        String value = null;
        try {
            is = new FileInputStream(filePath);
            byte[] b = new byte[3];
            is.read(b, 0, b.length);
            value = bytesToHexString(b);
        } catch (Exception e) {
        } finally {
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                }
            }
        }
        if ("FFD8FF".equals(value)) {
            return "jpg";
        } else if ("FFD8FF".equals(value)) {
            return "jpg";
        } else if ("474946".equals(value)) {
            return "gif";
        } else if ("424D".equals(value)) {
            return "bmp";
        } else if ("89504E".equals(value)) {
            return "png";
        }
        return value;
    }

    private static String bytesToHexString(byte[] src) {
        StringBuilder builder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        String hv;
        for (int i = 0; i < src.length; i++) {
            hv = Integer.toHexString(src[i] & 0xFF).toUpperCase();
            if (hv.length() < 2) {
                builder.append(0);
            }
            builder.append(hv);
        }
        return builder.toString();
    }

    public static IntBuffer getImage(Buffer buffer, int width, int height) {
        IntBuffer intBuffer = ((IntBuffer) buffer);
        int offset1, offset2;
        for (int i = 0; i < height / 2; i++) {
            offset1 = i * width;
            offset2 = (height - i - 1) * width;
            for (int j = 0; j < width; j++) {
                int texturePixel = intBuffer.get(offset1 + j);
                int blue = (texturePixel >> 16) & 0xff;
                int red = (texturePixel << 16) & 0x00ff0000;
                int pixel = (texturePixel & 0xff00ff00) | red | blue;

                int texturePixel2 = intBuffer.get(offset2 + j);
                int blue2 = (texturePixel2 >> 16) & 0xff;
                int red2 = (texturePixel2 << 16) & 0x00ff0000;
                int pixel2 = (texturePixel2 & 0xff00ff00) | red2 | blue2;
                intBuffer.put(offset2 + j, pixel);
                intBuffer.put(offset1 + j, pixel2);
            }
        }
        if (height / 2 != 0) {
            int i = height / 2 + 1;
            offset1 = i * width;
            offset2 = (height - i - 1) * width;
            for (int j = 0; j < width; j++) {
                int texturePixel = intBuffer.get(offset1 + j);
                int blue = (texturePixel >> 16) & 0xff;
                int red = (texturePixel << 16) & 0x00ff0000;
                int pixel = (texturePixel & 0xff00ff00) | red | blue;

                intBuffer.put(offset2 + j, pixel);
            }

        }

        return intBuffer;
    }

    public static String saveBitmaptoNative(String url, Bitmap bmp, String bitName) throws IOException {
        File dirFile = new File(url);
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }
        // String mbitname = bitName.substring(bitName.lastIndexOf("/")+1,bitName.length());
        File f = new File(url + bitName + ".png");
        boolean flag = false;
        f.createNewFile();
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(f);

            bmp.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            flag = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            fOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return url + bitName + ".png";
    }

    public static Bitmap mergeBitmap(Bitmap firstBitmap) {
        Paint paint = new Paint();
        paint.setColor(Color.parseColor("#ffffff"));
        Bitmap bitmap = Bitmap.createBitmap(firstBitmap.getWidth(),
                firstBitmap.getHeight(), firstBitmap.getConfig());
        Canvas canvas = new Canvas(bitmap);
        canvas.drawRect(0, 0, firstBitmap.getWidth(), firstBitmap.getHeight(), paint);
        canvas.drawBitmap(firstBitmap, 0, 0, paint);
        return bitmap;
    }

    public static synchronized void save2Txt(byte[] data, String url) {


        File dirFile = new File(Environment.getExternalStorageDirectory() + url);
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }
        File f = new File(Environment.getExternalStorageDirectory() + url + "/file.txt");
        try {
            f.createNewFile();
        } catch (IOException e) {

        }
        OutputStream out = null;
        try {
            out = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            // 将byte数组写入到文件之中
            out.write(data);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        try {
            out.close();
        } catch (IOException e2) {
            e2.printStackTrace();
        }
    }

    public static synchronized void save2Txts(byte[] data, String url, String title) {

        File dirFile = new File(Environment.getExternalStorageDirectory() + url);
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }
        File f = new File(Environment.getExternalStorageDirectory() + url + "/" + title);
        try {
            f.createNewFile();
        } catch (IOException e) {

        }
        OutputStream out = null;
        try {
            out = new FileOutputStream(f);
            FileDescriptor fd = ((FileOutputStream) out).getFD();
            fd.sync();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            // 将byte数组写入到文件之中
            out.write(data);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        try {
            out.close();
        } catch (IOException e2) {
            e2.printStackTrace();
        }
    }

    public static byte[] getBytesFromFile(File file) throws IOException {

        InputStream is = new FileInputStream(file);

        // 获取文件大小

        long length = file.length();

        if (length > Integer.MAX_VALUE) {

            // 文件太大，无法读取

            throw new IOException("File is to large " + file.getName());

        }

        // 创建一个数据来保存文件数据

        byte[] bytes = new byte[(int) length];

        // 读取数据到byte数组中

        int offset = 0;

        int numRead = 0;

        while (offset < bytes.length

                && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {

            offset += numRead;

        }

        // 确保所有数据均被读取

        if (offset < bytes.length) {

            throw new IOException("Could not completely read file " + file.getName());

        }

        //  Close the input stream and return bytes

        is.close();

        return bytes;

    }


    /**
     * 压缩文件和文件夹
     *
     * @param srcFileString 要压缩的文件或文件夹
     * @param zipFileString 压缩完成的Zip路径
     * @throws Exception
     */
    public static void ZipFolder(String srcFileString, String zipFileString) throws Exception {
        //创建ZIP
        ZipOutputStream outZip = new ZipOutputStream(new FileOutputStream(zipFileString));
        //创建文件
        File file = new File(srcFileString);
        //压缩
        LogUtil.d("---->" + file.getParent() + "===" + file.getAbsolutePath());
        ZipFiles(file.getParent() + File.separator, file.getName(), outZip);
        //完成和关闭
        outZip.finish();
        outZip.close();
    }

    /**
     * 压缩文件
     *
     * @param folderString
     * @param fileString
     * @param zipOutputSteam
     * @throws Exception
     */
    private static void ZipFiles(String folderString, String fileString, ZipOutputStream zipOutputSteam) throws Exception {
        LogUtil.d("folderString:" + folderString + "\n" +
                "fileString:" + fileString + "\n==========================");
        if (zipOutputSteam == null)
            return;
        File file = new File(folderString + fileString);
        if (file.isFile()) {
            ZipEntry zipEntry = new ZipEntry(fileString);
            FileInputStream inputStream = new FileInputStream(file);
            zipOutputSteam.putNextEntry(zipEntry);
            int len;
            byte[] buffer = new byte[4096];
            while ((len = inputStream.read(buffer)) != -1) {
                zipOutputSteam.write(buffer, 0, len);
            }
            zipOutputSteam.closeEntry();
        } else {
            //文件夹
            String fileList[] = file.list();
            //没有子文件和压缩
            if (fileList.length <= 0) {
                ZipEntry zipEntry = new ZipEntry(fileString + File.separator);
                zipOutputSteam.putNextEntry(zipEntry);
                zipOutputSteam.closeEntry();
            }
            //子文件和递归
            for (int i = 0; i < fileList.length; i++) {
                ZipFiles(folderString + fileString + "/", fileList[i], zipOutputSteam);
            }
        }

    }


    /**
     * 解压zip到指定的路径
     *
     * @param zipFileString ZIP的名称
     * @param outPathString 要解压缩路径
     * @throws Exception
     */
    public static void UnZipFolder(String zipFileString, String outPathString) throws Exception {
        ZipInputStream inZip = new ZipInputStream(new FileInputStream(zipFileString));
        ZipEntry zipEntry;
        String szName = "";
        while ((zipEntry = inZip.getNextEntry()) != null) {
            szName = zipEntry.getName();
            if (zipEntry.isDirectory()) {
                //获取部件的文件夹名
                szName = szName.substring(0, szName.length() - 1);
                File folder = new File(outPathString + File.separator + szName);
                folder.mkdirs();
            } else {
                File file = new File(outPathString + File.separator + szName);
                if (!file.exists()) {
                    file.getParentFile().mkdirs();
                    file.createNewFile();
                }
                // 获取文件的输出流
                FileOutputStream out = new FileOutputStream(file);
                int len;
                byte[] buffer = new byte[1024];
                // 读取（字节）字节到缓冲区
                while ((len = inZip.read(buffer)) != -1) {
                    // 从缓冲区（0）位置写入（字节）字节
                    out.write(buffer, 0, len);
                    out.flush();
                }
                out.close();
            }
        }
        inZip.close();
    }

}
