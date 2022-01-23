package com.bcnetech.hyphoto.data.SqlControl;

import android.app.Activity;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import com.bcnetech.bcnetechlibrary.util.StringUtil;
import com.bcnetech.bcnetechlibrary.util.TimeUtil;
import com.bcnetech.hyphoto.sql.Provider;
import com.bcnetech.hyphoto.sql.dao.ImageData;
import com.bcnetech.hyphoto.sql.dao.UploadPicData;
import com.bcnetech.hyphoto.utils.JsonUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by a1234 on 2017/8/17.
 */

public class UploadPicSqlControl extends BaseSqlControl {
    public final static int CLOUD_UPLOAD = 10000;//上传云图库中；
    public final static int CLOUD_UPLOAD_FAIL = 10001; //上传云图库失败；
    public final static int CLOUD_REUPLOAD = 10002;//重新上传中；

    private UploadPicAsyncQueryHandler uploadPicAsyncQueryHandler;

    public UploadPicSqlControl(Activity activity) {
        super(activity);
        uploadPicAsyncQueryHandler = new UploadPicAsyncQueryHandler(activity.getContentResolver());
    }


    /**
     * 插入上传数据
     *
     * @param uploadPicData
     */
    public void insertUploadPic(UploadPicData uploadPicData) {
        if (uploadPicData == null) {
            return;
        }
        if (uploadPicAsyncQueryHandler == null) {
            uploadPicAsyncQueryHandler = new UploadPicAsyncQueryHandler(activity.getContentResolver());
        }

        ContentValues values = new ContentValues();
        values.put(Provider.UploadPicColums.IMAGEDATA, JsonUtil.Object2Json(uploadPicData.getImagedata()) + "");
        values.put(Provider.UploadPicColums.UPLOAD_TIME, uploadPicData.getUploadTime() + "");
        values.put(Provider.UploadPicColums.LOCAL_URL, uploadPicData.getLocalUrl() + "");
        values.put(Provider.UploadPicColums.VALUE1, uploadPicData.getValue2() + "");
        values.put(Provider.UploadPicColums.VALUE2, uploadPicData.getValue1() + "");
        uploadPicAsyncQueryHandler.startInsert(INSERT_TOKEN, null, Provider.UploadPicColums.CONTENT_URI, values);
    }

    //查询数据
    public void startQuery() {
        //查询
        uploadPicAsyncQueryHandler.startQuery(QUERY_TOKEN, null, Provider.UploadPicColums.CONTENT_URI, null, null, null, "_id desc");
    }

    //查询数据
    public void queryUploadFail() {
        String selectKey = Provider.DownloadInfoColums.TYPE + "=? or " +
                Provider.DownloadInfoColums.TYPE + "=? ";
        String[] selectionArgs = new String[2];
        selectionArgs[0] = CLOUD_UPLOAD_FAIL + "";
        selectionArgs[1] = CLOUD_REUPLOAD + "";

        //查询
        uploadPicAsyncQueryHandler.startQuery(QUERY_TOKEN, null, Provider.DownloadInfoColums.CONTENT_URI, null, selectKey, selectionArgs, "_id asc");
    }


    /**
     * 删除图片 根据 图片地址
     *
     * @param uploadPicData
     */
    public void startDelInfo(UploadPicData uploadPicData) {

        String selectKey = Provider.UploadPicColums.LOCAL_URL + "=? and " +
                Provider.UploadPicColums.IMAGEDATA + "=? and " +
                Provider.UploadPicColums.VALUE2 + "=? and " +
                Provider.UploadPicColums.VALUE1 + "=? and " +
                Provider.UploadPicColums.UPLOAD_TIME + "=?";


        String[] selectionArgs = new String[5];
        selectionArgs[0] = uploadPicData.getLocalUrl() + "";
        selectionArgs[1] = uploadPicData.getValue2() + "";
        selectionArgs[2] = uploadPicData.getValue1() + "";
        selectionArgs[3] = uploadPicData.getImagedata() + "";
        selectionArgs[4] = uploadPicData.getUploadTime() + "";

        uploadPicAsyncQueryHandler.startDelete(QUERY_TOKEN,
                null,
                Provider.DownloadInfoColums.CONTENT_URI,
                selectKey,
                selectionArgs);
    }

    /**
     * 删除图片
     *
     * @param uploadPicData
     */
    public void startDelInfoById(UploadPicData uploadPicData) {

        String selectKey = Provider.DownloadInfoColums._ID + "=? ";


        String[] selectionArgs = new String[1];
        selectionArgs[0] = uploadPicData.getLocalUrl() + "";

        uploadPicAsyncQueryHandler.startDelete(QUERY_TOKEN,
                null,
                Provider.DownloadInfoColums.CONTENT_URI,
                selectKey,
                selectionArgs);
    }

    /**
     * 删除全部
     */
    public void startDelAll() {
        if (uploadPicAsyncQueryHandler == null) {
            uploadPicAsyncQueryHandler = new UploadPicAsyncQueryHandler(activity.getContentResolver());
        }
        uploadPicAsyncQueryHandler.startDelete(DELETE_TOKEN_ALL, null, Provider.DownloadInfoColums.CONTENT_URI, null, null);
    }

    /**
     * 删除重新上传和上传失败的图片
     */
    public void startDellFail() {
        if (uploadPicAsyncQueryHandler == null) {
            uploadPicAsyncQueryHandler = new UploadPicAsyncQueryHandler(activity.getContentResolver());
        }

        String selectKey = Provider.DownloadInfoColums.TYPE + "=? or " +
                Provider.DownloadInfoColums.TYPE + "=? ";
        String[] selectionArgs = new String[2];
        selectionArgs[0] = CLOUD_UPLOAD_FAIL + "";
        selectionArgs[1] = CLOUD_REUPLOAD + "";


        uploadPicAsyncQueryHandler.startDelete(DELETE_TOKEN_ALL, null, Provider.DownloadInfoColums.CONTENT_URI, selectKey, selectionArgs);
    }


    /**
     * 修改图片 根据 图片地址
     *
     * @param uploadPicData
     */
    public void startUpdateInfo(UploadPicData uploadPicData) {

        String selectKey = Provider.DownloadInfoColums.URL + "=? and " +
                Provider.DownloadInfoColums.GET_PARMS + "=? and " +
                Provider.DownloadInfoColums.POST_PARMS + "=? and " +
                Provider.DownloadInfoColums.POST_FILE_PARMS + "=? and " +
                Provider.DownloadInfoColums.UPLOAD_TIME + "=?";

        String[] selectionArgs = new String[5];
        selectionArgs[0] = uploadPicData.getLocalUrl() + "";
        selectionArgs[1] = uploadPicData.getValue2() + "";
        selectionArgs[2] = uploadPicData.getValue1() + "";
        selectionArgs[3] = uploadPicData.getImagedata() + "";
        selectionArgs[4] = uploadPicData.getUploadTime() + "";

        ContentValues contentValues = new ContentValues();
        contentValues.put(Provider.UploadPicColums.UPLOAD_TIME, TimeUtil.getBeiJingTimeGMT() + "");

        uploadPicAsyncQueryHandler.startUpdate(QUERY_TOKEN,
                null,
                Provider.UploadPicColums.CONTENT_URI,
                contentValues,
                selectKey,
                selectionArgs);
    }

    /**
     * 修改图片 根据 ID
     *//*
    public void startUpdateInfoById(DownloadInfoData downloadInfoData) {

        String selectKey = Provider.DownloadInfoColums._ID + "=? ";

        String[] selectionArgs = new String[1];
        selectionArgs[0] = downloadInfoData.getId() + "";

        ContentValues contentValues = new ContentValues();
        contentValues.put(Provider.DownloadInfoColums.UPLOAD_TIME, TimeUtil.getBeiJingTimeGMT() + "");
        contentValues.put(Provider.DownloadInfoColums.TYPE, downloadInfoData.getType());

        uploadPicAsyncQueryHandler.startUpdate(QUERY_TOKEN,
                null,
                Provider.DownloadInfoColums.CONTENT_URI,
                contentValues,
                selectKey,
                selectionArgs);
    }*/


    public class UploadPicAsyncQueryHandler extends AsyncQueryHandler {

        public UploadPicAsyncQueryHandler(ContentResolver cr) {
            super(cr);
        }

        @Override
        protected void onInsertComplete(int token, Object cookie, Uri uri) {
            super.onInsertComplete(token, cookie, uri);
            if (listener != null) {
                listener.insertListener();
            }
        }

        @Override
        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
            super.onQueryComplete(token, cookie, cursor);
            List list = new ArrayList();
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    UploadPicData uploadPicData = new UploadPicData();
                    String imagedata = cursor.getString(cursor
                            .getColumnIndex(Provider.UploadPicColums.IMAGEDATA));
                    if (!StringUtil.isBlank(imagedata)) {
                        uploadPicData.setImagedata(JsonUtil.Json2T(imagedata, ImageData.class));
                    }
                    uploadPicData.setUploadTime(cursor.getLong(cursor
                            .getColumnIndex(Provider.UploadPicColums.UPLOAD_TIME)));
                    uploadPicData.setLocalUrl(cursor.getString(cursor
                            .getColumnIndex(Provider.UploadPicColums.LOCAL_URL)));
                    uploadPicData.setValue1(cursor.getString(cursor.getColumnIndex(Provider.UploadPicColums.VALUE1)));
                    uploadPicData.setValue2(cursor.getString(cursor.getColumnIndex(Provider.UploadPicColums.VALUE2)));
                    list.add(uploadPicData);
                }
            }


            if (listener != null) {

                listener.queryListener(list);


            }
        }

        @Override
        protected void onDeleteComplete(int token, Object cookie, int result) {
            super.onDeleteComplete(token, cookie, result);
            if (listener != null) {
                listener.deletListener();
            }
        }
    }
}
