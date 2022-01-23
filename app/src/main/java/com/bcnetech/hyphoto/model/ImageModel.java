package com.bcnetech.hyphoto.model;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.bcnetech.hyphoto.R;
import com.bcnetech.hyphoto.data.Folder;
import com.bcnetech.hyphoto.data.SDCardMedia;
import com.bcnetech.hyphoto.utils.FileUtil;
import com.bcnetech.hyphoto.utils.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ImageModel {
    /**
     * 从SDCard加载图片
     *
     * @param context
     * @param callback
     */
    public static void loadImageForSDCard(final Context context, final DataCallback callback) {
        ArrayList<SDCardMedia> SDCardMedias;
        //由于扫描图片是耗时的操作，所以要在子线程处理。
        new Thread(new Runnable() {
            @Override
            public void run() {
                //扫描图片
                Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                ContentResolver mContentResolver = context.getContentResolver();
                Cursor mCursor = mContentResolver.query(mImageUri, new String[]{
                                MediaStore.Images.Media.DATA,
                                MediaStore.Images.Media.DISPLAY_NAME,
                                MediaStore.Images.Media.DATE_ADDED,
                                MediaStore.Images.Media._ID,
                                MediaStore.Images.Thumbnails.DATA
                        },
                        null,
                        null,
                        MediaStore.Images.Media.DATE_ADDED);

                ArrayList<SDCardMedia> SDCardMedias = new ArrayList<>();

                //读取扫描到的图片
                if (mCursor != null) {
                    while (mCursor.moveToNext()) {
                        // 获取图片的路径
                        String path = mCursor.getString(
                                mCursor.getColumnIndex(MediaStore.Images.Media.DATA));
                        String thumbPath = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Thumbnails.DATA));
                        //获取图片名称
                        if (!judgeFileType(FileUtil.getImageType(path))) {
                            continue;
                        }
                        String name = mCursor.getString(
                                mCursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
                        //获取图片时间
                        long time = mCursor.getLong(
                                mCursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED));
                        SDCardMedias.add(new SDCardMedia(path, thumbPath, time, name, 0, null));
                    }
                    mCursor.close();
                }

                //扫描视频
                Uri mImageUri2 = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                ContentResolver mContentResolver2 = context.getContentResolver();
                Cursor mCursor2 = mContentResolver2.query(mImageUri2, new String[]{
                                MediaStore.Video.Media.DATA, MediaStore.Video.Media.DISPLAY_NAME,
                                MediaStore.Video.Media.DURATION,
                                MediaStore.Video.Media.DATE_ADDED,
                                MediaStore.Video.Media._ID},
                        null,
                        null,
                        MediaStore.Video.Media.DATE_ADDED);


                //读取扫描到的视频
                if (mCursor2 != null) {
                    while (mCursor2.moveToNext()) {
                        // 获取视频的路径
                        String video_path = mCursor2.getString(
                                mCursor2.getColumnIndex(MediaStore.Video.Media.DATA));
                        // 获取视频封面图
                        String video_image = mCursor2.getString(
                                mCursor2.getColumnIndex(MediaStore.Video.Media.DATA));
                        //获取视频名称
                        String video_name = mCursor2.getString(
                                mCursor2.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME));
                        //获取视频时间
                        long video_get_time = mCursor2.getLong(
                                mCursor2.getColumnIndex(MediaStore.Video.Media.DATE_ADDED));
                        String video_duration = mCursor2.getString(
                                mCursor2.getColumnIndex(MediaStore.Video.Media.DURATION));
                        SDCardMedias.add(new SDCardMedia(video_path,video_image ,video_get_time, video_name, 1, video_duration));
                    }
                    mCursor2.close();
                }
                Collections.sort(SDCardMedias, new TimeComparator());
                Collections.reverse(SDCardMedias);
                callback.onSuccess(splitFolder(context,SDCardMedias));
            }
        }).start();
    }

    /**
     * 自定义按时间排序
     */

    static class TimeComparator implements Comparator {
        public int compare(Object object1, Object object2) {// 实现接口中的方法
            SDCardMedia p1 = (SDCardMedia) object1; // 强制转换
            SDCardMedia p2 = (SDCardMedia) object2;
            return new Double(p1.getTime()).compareTo(new Double(p2.getTime()));
        }
    }


    /**
     * 把图片按文件夹拆分，第一个文件夹保存所有的图片
     *
     * @param SDCardMedias
     * @return
     */
    private static ArrayList<Folder> splitFolder(Context context,ArrayList<SDCardMedia> SDCardMedias) {
        ArrayList<Folder> folders = new ArrayList<>();
        folders.add(new Folder(context.getResources().getString(R.string.folder_all), SDCardMedias));

        if (SDCardMedias != null && !SDCardMedias.isEmpty()) {
            int size = SDCardMedias.size();
            for (int i = 0; i < size; i++) {
                String path = SDCardMedias.get(i).getPath();
                String name = getFolderName(path);
                if (StringUtils.isNotEmptyString(name)) {
                    Folder folder = getFolder(name, folders);
                    folder.addImage(SDCardMedias.get(i));
                }
            }
        }
        return folders;
    }

    /**
     * 跟着图片路径，获取图片文件夹名称
     *
     * @param path
     * @return
     */
    private static String getFolderName(String path) {
        if (StringUtils.isNotEmptyString(path)) {
            String[] strings = path.split(File.separator);
            if (strings.length >= 2) {
                return strings[strings.length - 2];
            }
        }
        return "";
    }

    private static Folder getFolder(String name, List<Folder> folders) {
        if (!folders.isEmpty()) {
            int size = folders.size();
            for (int i = 0; i < size; i++) {
                Folder folder = folders.get(i);
                if (name.equals(folder.getName())) {
                    return folder;
                }
            }
        }
        Folder newFolder = new Folder(name);
        folders.add(newFolder);
        return newFolder;
    }

    private static boolean judgeFileType(String fileType) {
        if (fileType == null)
            return false;
        if (fileType.equals("jpg") || fileType.equals("png") || fileType.equals("gif") || fileType.equals("bmp")) {
            return true;
        }
        return false;

    }

    public interface DataCallback {
        void onSuccess(ArrayList<Folder> folders);
    }
}
