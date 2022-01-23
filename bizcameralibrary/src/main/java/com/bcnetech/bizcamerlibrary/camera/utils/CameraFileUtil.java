package com.bcnetech.bizcamerlibrary.camera.utils;

import android.graphics.Bitmap;
import android.os.Environment;

import com.bcnetech.bcnetchhttp.config.Flag;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author wsbai
 * @date 2018/9/25
 */
public class CameraFileUtil {
    /**
     * 删除指定文件夹下所有文件
     *   param path 文件夹完整绝对路径
     */
    public static boolean clearFolders(String path) {
        boolean flag = false;
        File file = new File(path);
        if (!file.exists()) {
            return flag;
        }
        if (!file.isDirectory()) {
            return flag;
        }
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + tempList[i]);
            } else {
                temp = new File(path + File.separator + tempList[i]);
            }
            if (temp.isFile()) {
                temp.delete();
            }
            if (temp.isDirectory()) {
                clearFolders(path + "/" + tempList[i]);//先删除文件夹里面的文件
                DeleteFile(new File(path + "/" + tempList[i]));//在删除空的文件夹
                flag = true;
            }
        }
        return flag;
    }

    /**
     * 递归删除文件和文件夹
     *
     * @param file 要删除的根目录
     */
    public static void DeleteFile(File file) {
        if (file.exists() == false) {
            return;
        } else {
            if (file.isFile()) {
                file.delete();
                return;
            }
            if (file.isDirectory()) {
                File[] childFile = file.listFiles();
                if (childFile == null || childFile.length == 0) {
                    file.delete();
                    return;
                }
                for (File f : childFile) {
                    DeleteFile(f);
                }
                file.delete();
            }
        }
    }

    public static File getOutPutData(byte[] data) {

        File dirFile = new File(Environment.getExternalStorageDirectory() + "/Android/data/com.bcnetech.hyphoto/cache/");
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File f = new File(dirFile.getPath() +File.separator+ "IMG_text.jpg");

        try {
            FileOutputStream fos = new FileOutputStream(f);
            fos.write(data);
            fos.close();
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        }

        return f;
    }

    public static void isNeedFile(String url) {

        File dirFile = new File(url);
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }
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
}
