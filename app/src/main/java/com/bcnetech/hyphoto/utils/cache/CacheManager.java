package com.bcnetech.hyphoto.utils.cache;

import android.content.Context;

import com.bcnetech.bcnetchhttp.bean.response.LoginedUser;
import com.bcnetech.bcnetechlibrary.util.HttpUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

public class CacheManager {

    // wifi缓存时间为1d
    private static long wifi_cache_time = 24 * 60 * 60 * 1000;
    // 其他网络环境为1d
    private static long other_cache_time = 24 * 60 * 60 * 1000;

    /**
     * 保存对象
     *
     * @param ser
     * @param file
     * @throws IOException 用于指定文件名称，不能包含路径分隔符“/”，如果文件不存在，Android会自动创建它。创建的文件保存在/data/data/<package name>/files/目录中。如：/data/data/GH.Test/files/abc.txt
     */
    public static boolean saveObject(Context context, List<?> ser,
                                     String file) {
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        try {
            fos = context.openFileOutput(file, Context.MODE_PRIVATE);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(ser);
            oos.flush();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            StreamUtil.close(fos, oos);
        }
    }

    public static void deleteObject(Context context, File filedata) {
        //String filePath = context.getFilesDir().getPath() + "/" + fileName;
        //File file = new File(filePath);
        if (filedata.exists()) {
            filedata.delete();
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            //递归删除目录中的子目录下
            for (int i=0; i<children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        // 目录此时为空，可以删除
        return dir.delete();
    }

    /**
     * 读取对象
     *
     * @param file
     * @return
     * @throws IOException
     */
    public static List<?> readObject(Context context, String file) {
        if (!isExistDataCache(context, file))
            return null;
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        try {
            fis = context.openFileInput(file);
            ois = new ObjectInputStream(fis);
            return (List<?>) ois.readObject();
        } catch (FileNotFoundException e) {
        } catch (Exception e) {
            e.printStackTrace();
            // 反序列化失败 - 删除缓存文件
            if (e instanceof InvalidClassException) {
                File data = context.getFileStreamPath(file);
                data.delete();
            }
        } finally {
            StreamUtil.close(ois, fis);
        }
        return null;
    }

    /**
     * 判断缓存是否存在
     *
     * @param cachefile
     * @return
     */
    public static boolean isExistDataCache(Context context, String cachefile) {
        if (context == null)
            return false;
        boolean exist = false;
        File data = context.getFileStreamPath(cachefile);
        if (data.exists())
            exist = true;
        return exist;
    }

    /**
     * 判断缓存是否已经失效
     */
    public static boolean isCacheDataFailure(Context context, String cachefile) {
        File data = context.getFileStreamPath(cachefile);
        if (!data.exists()) {

            return false;
        }
        long existTime = System.currentTimeMillis() - data.lastModified();
        boolean failure = false;
        if (HttpUtil.isNetworkAvailable(context, LoginedUser.getLoginedUser().isonlywifi(), false)) {
            failure = existTime > wifi_cache_time;
        } else {
            failure = existTime > other_cache_time;
        }
        if (failure) {
            deleteObject(context, data);
        }
        return failure;
    }

    public static void DeleteCache(Context context, String cachefile) {
        File data = context.getFileStreamPath(cachefile);
        if (data.exists()) {
            deleteObject(context, data);
        }
    }

    public static void DeleteAllCache(Context context){
        File file= context.getFilesDir();
        if (file.exists()){
            deleteDir(file);
        }

    }

}
