package com.bcnetech.hyphoto.data.SqlControl;

import android.app.Activity;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;

import com.bcnetech.bcnetchhttp.bean.data.DownloadInfoData;
import com.bcnetech.bcnetechlibrary.util.TimeUtil;
import com.bcnetech.hyphoto.sql.Provider;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wenbin on 2017/1/16.
 */

public class DownloadInfoSqlControl extends BaseSqlControl{

    public final static int CLOUD_UPLOAD=10000;//上传云图库；
    public final static int CLOUD_UPLOAD_FAIL=10001;//上传云图库失败；

//    public final static int CLOUD_REUPLOAD=10006;//重新上传中；
    private DownloadInfoAsunvQueryHandler downloadInfoAsunvQueryHandler;

    public DownloadInfoSqlControl(Activity activity) {
        super(activity);
        downloadInfoAsunvQueryHandler = new DownloadInfoAsunvQueryHandler(activity.getContentResolver());
    }


    /**
     * 插入上传云图库信息
     *
     * @param downloadInfoData
     */
    public void insertDownLoadInfo(DownloadInfoData downloadInfoData) {
        if (downloadInfoData == null) {
            return;
        }
        if (downloadInfoAsunvQueryHandler == null) {
            downloadInfoAsunvQueryHandler = new DownloadInfoAsunvQueryHandler(activity.getContentResolver());
        }

        ContentValues values = new ContentValues();
        values.put(Provider.DownloadInfoColums.TYPE, downloadInfoData.getType() + "");
        values.put(Provider.DownloadInfoColums.URL, downloadInfoData.getUrl() + "");
        values.put(Provider.DownloadInfoColums.GET_PARMS, downloadInfoData.getGetParms() + "");
        values.put(Provider.DownloadInfoColums.POST_PARMS, downloadInfoData.getPostParms() + "");
        values.put(Provider.DownloadInfoColums.POST_FILE_PARMS, downloadInfoData.getPostFileParms() + "");
        values.put(Provider.DownloadInfoColums.UPLOAD_TIME, downloadInfoData.getUploadTime() + "");
        values.put(Provider.DownloadInfoColums.LOCAL_URL, downloadInfoData.getLocalUrl() + "");
        values.put(Provider.DownloadInfoColums.FILE_TYPE, downloadInfoData.getFileType() + "");
        downloadInfoAsunvQueryHandler.startInsert(INSERT_TOKEN, null, Provider.DownloadInfoColums.CONTENT_URI, values);
    }

    //查询数据
    public void QueryInfoAll() {
        //查询
        downloadInfoAsunvQueryHandler.startQuery(QUERY_TOKEN, null, Provider.DownloadInfoColums.CONTENT_URI, null, null, null, "_id asc");
    }

    //查询总数
    public void queryCount() {
        //查询
        downloadInfoAsunvQueryHandler.startQuery(QUERY_COUNT, null, Provider.DownloadInfoColums.CONTENT_URI, null, null, null, "_id asc");
    }

    //查询数据
    public void queryUploadFail() {


        String selectKey = Provider.DownloadInfoColums.TYPE + "=? ";
        String[] selectionArgs = new String[1];
        selectionArgs[0] = CLOUD_UPLOAD_FAIL + "";
//

        //查询
        downloadInfoAsunvQueryHandler.startQuery(QUERY_TOKEN, null, Provider.DownloadInfoColums.CONTENT_URI, null, selectKey, selectionArgs, "_id asc");
    }


    //查询数据
    public void queryFirstUpload() {


        String selectKey = Provider.DownloadInfoColums.TYPE + "=? ";
        String[] selectionArgs = new String[1];
        selectionArgs[0] = CLOUD_UPLOAD + "";


        //查询
        downloadInfoAsunvQueryHandler.startQuery(QUERY_TOKEN, null, Provider.DownloadInfoColums.CONTENT_URI, null, selectKey, selectionArgs, "_id asc");
    }



//    //查询数据
//    public void queryDeleteInfo() {
//        //查询
//        downloadInfoAsunvQueryHandler.startQuery(SEARCH_TOKEN, null, Provider.DownloadInfoColums.CONTENT_URI, null, null, null, "_id asc");
//    }


    /**
     * 删除图片 根据 图片地址
     *
     * @param downloadInfoData
     */
    public void startDelInfo(DownloadInfoData downloadInfoData) {

        String selectKey = Provider.DownloadInfoColums.URL + "=? and " +
                Provider.DownloadInfoColums.GET_PARMS + "=? and " +
                Provider.DownloadInfoColums.POST_PARMS + "=? and " +
                Provider.DownloadInfoColums.POST_FILE_PARMS + "=? and " +
                Provider.DownloadInfoColums.UPLOAD_TIME + "=?";


        String[] selectionArgs = new String[5];
        selectionArgs[0] = downloadInfoData.getUrl() + "";
        selectionArgs[1] = downloadInfoData.getGetParms() + "";
        selectionArgs[2] = downloadInfoData.getPostParms() + "";
        selectionArgs[3] = downloadInfoData.getPostFileParms() + "";
        selectionArgs[4] = downloadInfoData.getUploadTime() + "";

        downloadInfoAsunvQueryHandler.startDelete(QUERY_TOKEN,
                null,
                Provider.DownloadInfoColums.CONTENT_URI,
                selectKey,
                selectionArgs);
    }

    /**
     * 删除图片
     *
     * @param downloadInfoData
     */
    public void startDelInfoById(DownloadInfoData downloadInfoData) {

        String selectKey = Provider.DownloadInfoColums._ID + "=? ";


        String[] selectionArgs = new String[1];
        selectionArgs[0] = downloadInfoData.getId() + "";

        downloadInfoAsunvQueryHandler.startDelete(QUERY_TOKEN,
                null,
                Provider.DownloadInfoColums.CONTENT_URI,
                selectKey,
                selectionArgs);
    }

    /**
     * 删除全部
     */
    public void startDelAll() {
        if (downloadInfoAsunvQueryHandler == null) {
            downloadInfoAsunvQueryHandler = new DownloadInfoAsunvQueryHandler(activity.getContentResolver());
        }
        downloadInfoAsunvQueryHandler.startDelete(DELETE_TOKEN_ALL, null, Provider.DownloadInfoColums.CONTENT_URI, null, null);
    }

    /**
     * 删除重新上传和上传失败的图片
     */
    public void startDellFail() {
        if (downloadInfoAsunvQueryHandler == null) {
            downloadInfoAsunvQueryHandler = new DownloadInfoAsunvQueryHandler(activity.getContentResolver());
        }

        String selectKey = Provider.DownloadInfoColums.TYPE + "=? ";
        String[] selectionArgs = new String[1];
        selectionArgs[0] = CLOUD_UPLOAD_FAIL + "";



        downloadInfoAsunvQueryHandler.startDelete(DELETE_TOKEN_ALL, null, Provider.DownloadInfoColums.CONTENT_URI, selectKey, selectionArgs);
    }


    /**
     * 修改图片 根据 图片地址
     *
     * @param downloadInfoData
     */
    public void startUpdateInfo(DownloadInfoData downloadInfoData) {

        String selectKey = Provider.DownloadInfoColums.URL + "=? and " +
                Provider.DownloadInfoColums.GET_PARMS + "=? and " +
                Provider.DownloadInfoColums.POST_PARMS + "=? and " +
                Provider.DownloadInfoColums.POST_FILE_PARMS + "=? and " +
                Provider.DownloadInfoColums.UPLOAD_TIME + "=?";

        String[] selectionArgs = new String[5];
        selectionArgs[0] = downloadInfoData.getUrl() + "";
        selectionArgs[1] = downloadInfoData.getGetParms() + "";
        selectionArgs[2] = downloadInfoData.getPostParms() + "";
        selectionArgs[3] = downloadInfoData.getPostFileParms() + "";
        selectionArgs[4] = downloadInfoData.getUploadTime() + "";

        ContentValues contentValues = new ContentValues();
        contentValues.put(Provider.DownloadInfoColums.UPLOAD_TIME, TimeUtil.getBeiJingTimeGMT() + "");
        contentValues.put(Provider.DownloadInfoColums.TYPE, downloadInfoData.getType());

        downloadInfoAsunvQueryHandler.startUpdate(QUERY_TOKEN,
                null,
                Provider.DownloadInfoColums.CONTENT_URI,
                contentValues,
                selectKey,
                selectionArgs);
    }

    /**
     * 修改图片 根据 ID
     *
     * @param downloadInfoData
     */
    public void startUpdateInfoById(DownloadInfoData downloadInfoData) {

        String selectKey = Provider.DownloadInfoColums._ID + "=? ";

        String[] selectionArgs = new String[1];
        selectionArgs[0] = downloadInfoData.getId() + "";


        ContentValues contentValues=new ContentValues();
        contentValues.put(Provider.DownloadInfoColums.UPLOAD_TIME, TimeUtil.getBeiJingTimeGMT()+"");
        contentValues.put(Provider.DownloadInfoColums.TYPE,downloadInfoData.getType());
        downloadInfoAsunvQueryHandler.startUpdate(UPDATE_TOKEN,
                null,
                Provider.DownloadInfoColums.CONTENT_URI,
                contentValues,
                selectKey,
                selectionArgs);
    }


    public class DownloadInfoAsunvQueryHandler extends AsyncQueryHandler {

        public DownloadInfoAsunvQueryHandler(ContentResolver cr) {
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
            if(token==BaseSqlControl.QUERY_COUNT){
                if (listener != null) {
                    if(cursor!=null){
                        listener.queryListener(cursor.getCount(),token);
                    }else {
                        listener.queryListener(0,token);
                    }
                }
            }else {
                List list = new ArrayList();
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        DownloadInfoData downloadInfoData = new DownloadInfoData();
                        downloadInfoData.setId(Integer.valueOf(Integer.valueOf(cursor.getString(cursor
                                .getColumnIndex(Provider.DownloadInfoColums._ID)))));
                        downloadInfoData.setType(Integer.valueOf(cursor.getString(cursor
                                .getColumnIndex(Provider.DownloadInfoColums.TYPE))));
                        downloadInfoData.setUrl(cursor.getString(cursor
                                .getColumnIndex(Provider.DownloadInfoColums.URL)));
                        downloadInfoData.setGetParms(cursor.getString(cursor
                                .getColumnIndex(Provider.DownloadInfoColums.GET_PARMS)));
                        downloadInfoData.setPostParms(cursor.getString(cursor
                                .getColumnIndex(Provider.DownloadInfoColums.POST_PARMS)));
                        downloadInfoData.setPostFileParms(cursor.getString(cursor
                                .getColumnIndex(Provider.DownloadInfoColums.POST_FILE_PARMS)));
                        downloadInfoData.setUploadTime(cursor.getLong(cursor
                                .getColumnIndex(Provider.DownloadInfoColums.UPLOAD_TIME)));
                        downloadInfoData.setLocalUrl(cursor.getString(cursor
                                .getColumnIndex(Provider.DownloadInfoColums.LOCAL_URL)));
                        if (TextUtils.isEmpty(cursor.getString(cursor
                                .getColumnIndex(Provider.DownloadInfoColums.FILE_TYPE)))) {
                            downloadInfoData.setFileType(0);
                        } else {
                            downloadInfoData.setFileType(Integer.valueOf(cursor.getString(cursor
                                    .getColumnIndex(Provider.DownloadInfoColums.FILE_TYPE))));
                        }
                        list.add(downloadInfoData);
                    }
                }

                if (listener != null) {

                    listener.queryListener(list,token);

                }
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
