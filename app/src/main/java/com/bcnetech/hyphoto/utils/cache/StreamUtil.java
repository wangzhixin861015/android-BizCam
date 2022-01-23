package com.bcnetech.hyphoto.utils.cache;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 相关流工具类
 * <p>
 * Author: JuQiu qiujuer@live.cn
 */
public final class StreamUtil {

    /**
     * 关闭流
     *
     * @param closeables Closeable
     */
    @SuppressWarnings("WeakerAccess")
    public static void close(Closeable... closeables) {
        if (closeables == null || closeables.length == 0)
            return;
        for (Closeable closeable : closeables) {
            if (closeable != null) {
                try {
                    closeable.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 拷贝文件
     * 如果目标文件不存在将会自动创建
     *
     * @param srcFile  原文件
     * @param saveFile 目标文件
     * @return 是否拷贝成功
     */
    public static boolean copyFile(final File srcFile, final File saveFile) {
        File parentFile = saveFile.getParentFile();
        if (!parentFile.exists()) {
            if (!parentFile.mkdirs())
                return false;
        }

        BufferedInputStream inputStream = null;
        BufferedOutputStream outputStream = null;
        try {
            inputStream = new BufferedInputStream(new FileInputStream(srcFile));
            outputStream = new BufferedOutputStream(new FileOutputStream(saveFile));
            byte[] buffer = new byte[1024 * 4];
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
            }
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            close(inputStream, outputStream);
        }
        return true;
    }

    /**
     * 检查手机上是否安装了指定的软件
     * @param context
     * @param packageName
     * @return
     */
    public static boolean isAvilible(Context context, String packageName) {
        final PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
        List<String> packageNames = new ArrayList<>();

        if (packageInfos != null) {
            for (int i = 0; i < packageInfos.size(); i++) {
                String packName = packageInfos.get(i).packageName;
                packageNames.add(packName);
            }
        }
        // 判断packageNames中是否有目标程序的包名，有TRUE，没有FALSE
        return packageNames.contains(packageName);
    }

    public boolean checkApplication(String packageName,Context c) {
        if (packageName == null || "".equals(packageName)) {
            return false;
        }
        try {
            c.getPackageManager().getApplicationInfo(packageName,
                    PackageManager.GET_UNINSTALLED_PACKAGES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
}
