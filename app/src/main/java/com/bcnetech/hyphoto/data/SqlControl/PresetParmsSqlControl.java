package com.bcnetech.hyphoto.data.SqlControl;

import android.app.Activity;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import com.bcnetech.bcnetechlibrary.bean.CameraParm;
import com.bcnetech.bcnetechlibrary.util.StringUtil;
import com.bcnetech.hyphoto.sql.Provider;
import com.bcnetech.hyphoto.sql.dao.LightRatioData;
import com.bcnetech.hyphoto.sql.dao.PictureProcessingData;
import com.bcnetech.hyphoto.sql.dao.PresetParm;
import com.bcnetech.hyphoto.utils.JsonUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wenbin on 16/11/3.
 */

public class PresetParmsSqlControl extends BaseSqlControl {

    private PresetParmsAsyncQueryHandler queryHandler;

    public PresetParmsSqlControl(Activity activity) {
        super(activity);

        queryHandler = new PresetParmsAsyncQueryHandler(activity.getContentResolver());
    }


    /**
     * 查询预设参数
     */
    public void startQuery() {

        if (queryHandler == null) {
            queryHandler = new PresetParmsAsyncQueryHandler(activity.getContentResolver());
        }
        //查询
        queryHandler.startQuery(QUERY_TOKEN, null, Provider.PresetParmsColumns.CONTENT_URI, null, null, null, "_id desc");
    }

    /**
     * 查询预设参数
     */
    public void startQueryBySystem() {

        if (queryHandler == null) {
            queryHandler = new PresetParmsAsyncQueryHandler(activity.getContentResolver());
        }
        //查询

        queryHandler.startQuery(QUERY_TOKEN, null, Provider.PresetParmsColumns.CONTENT_URI, null, Provider.PresetParmsColumns.SYSTEM + " = ?", new String[]{"android"}, "_Position desc");


    }

    /**
     * 删除全部
     */
    public void deleteAll() {
        if (queryHandler == null) {
            queryHandler = new PresetParmsAsyncQueryHandler(activity.getContentResolver());
        }
        //删除全部
        queryHandler.startDelete(DELETE_TOKEN_ALL, null, Provider.PresetParmsColumns.CONTENT_URI, null, null);

    }


    /**
     * 查询
     */
    public void startByFileId(String fileId) {

        if (queryHandler == null) {
            queryHandler = new PresetParmsAsyncQueryHandler(activity.getContentResolver());
        }
        //查询
        queryHandler.startQuery(SEARCH_TOKEN, null, Provider.PresetParmsColumns.CONTENT_URI, null, Provider.PresetParmsColumns.PRESET_ID + "= ?", new String[]{fileId}, "_id desc");
    }

    /**
     * 查询系统查询
     */
    public void startQueryBySystem(String system) {

        if (queryHandler == null) {
            queryHandler = new PresetParmsAsyncQueryHandler(activity.getContentResolver());
        }
        //查询
        queryHandler.startQuery(QUERY_TOKEN, null, Provider.PresetParmsColumns.CONTENT_URI, null, Provider.PresetParmsColumns.SYSTEM + "= ?", new String[]{system}, "_Position desc");
    }

    /**
     * 根据 设备名称和是否显示 查询预设参数
     */
    public void startQueryByShowType(String equipment) {

        if (queryHandler == null) {
            queryHandler = new PresetParmsAsyncQueryHandler(activity.getContentResolver());
        }
        //查询
        queryHandler.startQuery(QUERY_TOKEN, null, Provider.PresetParmsColumns.CONTENT_URI, null, Provider.PresetParmsColumns.SHOW_TYPE + "= ? AND " + Provider.PresetParmsColumns.SYSTEM + " like ?", new String[]{"0", "%" + equipment + "%"}, "_Position asc");
    }

    /**
     * 交换位置
     *
     * @param fromPresetParm
     * @param toPresetParm
     */
    public void startUpdate(PresetParm fromPresetParm, PresetParm toPresetParm) {
        if (queryHandler == null) {
            queryHandler = new PresetParmsAsyncQueryHandler(activity.getContentResolver());
        }

        ContentValues fromValues = new ContentValues();
        fromValues.put(Provider.PresetParmsColumns.POSITION, fromPresetParm.getPosition());

        queryHandler.startUpdate(UPDATE_TOKEN, null, Provider.PresetParmsColumns.CONTENT_URI, fromValues, Provider.PresetParmsColumns._ID + "=?", new String[]{fromPresetParm.getId()});

        ContentValues toValues = new ContentValues();
        toValues.put(Provider.PresetParmsColumns.POSITION, toPresetParm.getPosition());
        queryHandler.startUpdate(UPDATE_TOKEN, null, Provider.PresetParmsColumns.CONTENT_URI, toValues, Provider.PresetParmsColumns._ID + "=?", new String[]{toPresetParm.getId()});

    }

    public void getLastOne() {
        if (queryHandler == null) {
            queryHandler = new PresetParmsAsyncQueryHandler(activity.getContentResolver());
        }
        //查询
        queryHandler.startQuery(QUERY_TOKEN, null, Provider.PresetParmsColumns.CONTENT_URI, null, "", new String[]{}, "_Position desc LIMIT 1");
    }

    /**
     * 修改是否显示
     *
     * @param fromPresetParm
     */
    public void startUpdateShowType(PresetParm fromPresetParm) {
        if (queryHandler == null) {
            queryHandler = new PresetParmsAsyncQueryHandler(activity.getContentResolver());
        }

        ContentValues fromValues = new ContentValues();
        fromValues.put(Provider.PresetParmsColumns.SHOW_TYPE, fromPresetParm.getShowType());

        queryHandler.startUpdate(UPDATE_TOKEN, null, Provider.PresetParmsColumns.CONTENT_URI, fromValues, Provider.PresetParmsColumns._ID + "=?", new String[]{fromPresetParm.getId()});
    }

    /**
     * @param id
     */
    public void delData(String id) {
        String selection = Provider.PresetParmsColumns._ID + "=?";
        String[] selectionArgs = new String[]{id};
        queryHandler.startDelete(DELETE_TOKEN, null, Provider.PresetParmsColumns.CONTENT_URI, selection, selectionArgs);
    }

    /**
     * 插入预设参数信息
     *
     * @param presetParm
     */
    public void startInsert(final PresetParm presetParm) {
        if (presetParm == null) {
            return;
        }
        if (queryHandler == null) {
            queryHandler = new PresetParmsAsyncQueryHandler(activity.getContentResolver());
        }


        final ContentValues values = new ContentValues();
        values.put(Provider.PresetParmsColumns.NAME, presetParm.getName() + "");
        values.put(Provider.PresetParmsColumns.LABEL, JsonUtil.list2JsonSerial(presetParm.getLabels()) + "");
        values.put(Provider.PresetParmsColumns.SHOW_TYPE, presetParm.getShowType() + "");
        values.put(Provider.PresetParmsColumns.POSITION, presetParm.getPosition());
        values.put(Provider.PresetParmsColumns.DOWNLOAD_COUNT, presetParm.getDownloadCount());
        values.put(Provider.PresetParmsColumns.PRESET_ID, presetParm.getPresetId() + "");
        values.put(Provider.PresetParmsColumns.IMAGE_WIDTH, presetParm.getImageWidth() + "");
        values.put(Provider.PresetParmsColumns.IMAGE_HEIGHT, presetParm.getImageHeight() + "");
        values.put(Provider.PresetParmsColumns.CATEGORY_ID, presetParm.getCategoryId() + "");
        values.put(Provider.PresetParmsColumns.SYSTEM, presetParm.getSystem() + "");
        values.put(Provider.PresetParmsColumns.TEXT_SRC, presetParm.getTextSrc());
        values.put(Provider.PresetParmsColumns.TIME_STAMP, presetParm.getTimeStamp() + "");
        values.put(Provider.PresetParmsColumns.AUTHER, presetParm.getAuther() + "");
        values.put(Provider.PresetParmsColumns.AUTHER_ID, presetParm.getAutherID() + "");
        values.put(Provider.PresetParmsColumns.AUTHER_URL, presetParm.getAutherUrl() + "");
        values.put(Provider.PresetParmsColumns.DESCRIBE, presetParm.getDescribe() + "");
        values.put(Provider.PresetParmsColumns.EQUIPMENT, presetParm.getEquipment() + "");
        values.put(Provider.PresetParmsColumns.SIZE, presetParm.getSize() + "");

        if (!StringUtil.isBlank(presetParm.getCoverId())) {
            values.put(Provider.PresetParmsColumns.COVER_ID, presetParm.getCoverId() + "");        }
        else{
            values.put(Provider.PresetParmsColumns.COVER_ID, "");
        }

        values.put(Provider.PresetParmsColumns.COVER_ID, presetParm.getCoverId() + "");
        if (presetParm.getLightRatioData() != null) {
            values.put(Provider.PresetParmsColumns.LIGHT_RATIO_DATA, JsonUtil.Object2Json(presetParm.getLightRatioData()));
        }
        if (presetParm.getCameraParm() != null) {
            values.put(Provider.PresetParmsColumns.CAMERA_PARM, JsonUtil.Object2Json(presetParm.getCameraParm()));

        }
        if (presetParm.getParmlists() != null && presetParm.getParmlists().size() != 0) {
            values.put(Provider.PresetParmsColumns.PRAM_LISTS, JsonUtil.list2JsonSerial(presetParm.getParmlists()) + "");
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (presetParm.getPartParmlists() != null && presetParm.getPartParmlists().size() != 0) {
                    values.put(Provider.PresetParmsColumns.PART_PRAM_LISTS, JsonUtil.list2JsonSerial(presetParm.getPartParmlists()) + "");
                }
                queryHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        queryHandler.startInsert(INSERT_TOKEN, null, Provider.PresetParmsColumns.CONTENT_URI, values);

                    }
                });
            }
        }).start();


    }

    public class PresetParmsAsyncQueryHandler extends AsyncQueryHandler {
        public PresetParmsAsyncQueryHandler(ContentResolver cr) {
            super(cr);
        }

        @Override
        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
            super.onQueryComplete(token, cookie, cursor);
            List list = new ArrayList<>();
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    PresetParm presetParm = new PresetParm();
                    if (!StringUtil.isBlank(cursor.getString(cursor
                            .getColumnIndex(Provider.PresetParmsColumns._ID)))) {
                        presetParm.setId(cursor.getString(cursor
                                .getColumnIndex(Provider.PresetParmsColumns._ID)));
                    }

                    if (!StringUtil.isBlank(cursor.getString(cursor
                            .getColumnIndex(Provider.PresetParmsColumns.NAME)))) {
                        presetParm.setName(cursor.getString(cursor
                                .getColumnIndex(Provider.PresetParmsColumns.NAME)));
                    }

                    if (!StringUtil.isBlank(cursor.getString(cursor
                            .getColumnIndex(Provider.PresetParmsColumns.TIME_STAMP)))) {
                        presetParm.setTimeStamp(Long.valueOf(cursor.getString(cursor
                                .getColumnIndex(Provider.PresetParmsColumns.TIME_STAMP))));
                    }
                    if (!StringUtil.isBlank(cursor.getString(cursor
                            .getColumnIndex(Provider.PresetParmsColumns.TEXT_SRC)))) {
                        presetParm.setTextSrc(cursor.getString(cursor
                                .getColumnIndex(Provider.PresetParmsColumns.TEXT_SRC)));
                    }

                    if (!StringUtil.isBlank(cursor.getString(cursor
                            .getColumnIndex(Provider.PresetParmsColumns.AUTHER)))) {
                        presetParm.setAuther(cursor.getString(cursor
                                .getColumnIndex(Provider.PresetParmsColumns.AUTHER)));
                    }

                    if (!StringUtil.isBlank(cursor.getString(cursor
                            .getColumnIndex(Provider.PresetParmsColumns.AUTHER_ID)))) {
                        presetParm.setAutherID(cursor.getString(cursor
                                .getColumnIndex(Provider.PresetParmsColumns.AUTHER_ID)));
                    }

                    if (!StringUtil.isBlank(cursor.getString(cursor
                            .getColumnIndex(Provider.PresetParmsColumns.AUTHER_URL)))) {
                        presetParm.setAutherUrl(cursor.getString(cursor
                                .getColumnIndex(Provider.PresetParmsColumns.AUTHER_URL)));
                    }
                    if (!StringUtil.isBlank(cursor.getString(cursor
                            .getColumnIndex(Provider.PresetParmsColumns.DESCRIBE)))) {
                        presetParm.setDescribe(cursor.getString(cursor
                                .getColumnIndex(Provider.PresetParmsColumns.DESCRIBE)));
                    }

                    if (!StringUtil.isBlank(cursor.getString(cursor
                            .getColumnIndex(Provider.PresetParmsColumns.EQUIPMENT)))) {
                        presetParm.setEquipment(cursor.getString(cursor
                                .getColumnIndex(Provider.PresetParmsColumns.EQUIPMENT)));
                    }

                    if (!StringUtil.isBlank(cursor.getString(cursor
                            .getColumnIndex(Provider.PresetParmsColumns.SHOW_TYPE)))) {
                        presetParm.setShowType(cursor.getString(cursor
                                .getColumnIndex(Provider.PresetParmsColumns.SHOW_TYPE)));
                    }

                    if (!StringUtil.isBlank(cursor.getString(cursor
                            .getColumnIndex(Provider.PresetParmsColumns.DOWNLOAD_COUNT)))) {
                        presetParm.setDownloadCount(cursor.getInt(cursor
                                .getColumnIndex(Provider.PresetParmsColumns.DOWNLOAD_COUNT)));
                    }

                    if (!StringUtil.isBlank(cursor.getString(cursor
                            .getColumnIndex(Provider.PresetParmsColumns.IMAGE_HEIGHT)))) {
                        presetParm.setImageHeight(cursor.getString(cursor
                                .getColumnIndex(Provider.PresetParmsColumns.IMAGE_HEIGHT)));
                    }

                    if (!StringUtil.isBlank(cursor.getString(cursor
                            .getColumnIndex(Provider.PresetParmsColumns.IMAGE_WIDTH)))) {
                        presetParm.setImageWidth(cursor.getString(cursor
                                .getColumnIndex(Provider.PresetParmsColumns.IMAGE_WIDTH)));
                    }

                    if (!StringUtil.isBlank(cursor.getString(cursor
                            .getColumnIndex(Provider.PresetParmsColumns.CATEGORY_ID)))) {
                        presetParm.setCategoryId(cursor.getString(cursor
                                .getColumnIndex(Provider.PresetParmsColumns.CATEGORY_ID)));
                    }

                    if (!StringUtil.isBlank(cursor.getString(cursor
                            .getColumnIndex(Provider.PresetParmsColumns.SYSTEM)))) {
                        presetParm.setSystem(cursor.getString(cursor
                                .getColumnIndex(Provider.PresetParmsColumns.SYSTEM)));
                    }


                    if (!StringUtil.isBlank(cursor.getString(cursor
                            .getColumnIndex(Provider.PresetParmsColumns.POSITION)))) {
                        presetParm.setPosition(cursor.getInt(cursor
                                .getColumnIndex(Provider.PresetParmsColumns.POSITION)));
                    }

                    if (!StringUtil.isBlank(cursor.getString(cursor
                            .getColumnIndex(Provider.PresetParmsColumns.PRESET_ID)))) {
                        presetParm.setPresetId(cursor.getString(cursor
                                .getColumnIndex(Provider.PresetParmsColumns.PRESET_ID)));
                    }

                    if (!StringUtil.isBlank(cursor.getString(cursor.getColumnIndex(Provider.PresetParmsColumns.LABEL)))) {
                        presetParm.setLabels(JsonUtil.Json2List(cursor.getString(cursor.getColumnIndex(Provider.PresetParmsColumns.LABEL))
                                , String.class));
                    }


                    if (!StringUtil.isBlank(cursor.getString(cursor
                            .getColumnIndex(Provider.PresetParmsColumns.LIGHT_RATIO_DATA)))) {
                        presetParm.setLightRatioData(JsonUtil.Json2T(cursor.getString(cursor
                                .getColumnIndex(Provider.PresetParmsColumns.LIGHT_RATIO_DATA)), LightRatioData.class));
                    }

                    if (!StringUtil.isBlank(cursor.getString(cursor
                            .getColumnIndex(Provider.PresetParmsColumns.CAMERA_PARM)))) {
                        presetParm.setCameraParm(JsonUtil.Json2T(cursor.getString(cursor
                                .getColumnIndex(Provider.PresetParmsColumns.CAMERA_PARM)), CameraParm.class));
                    }

                    if (!StringUtil.isBlank(cursor.getString(cursor
                            .getColumnIndex(Provider.PresetParmsColumns.PRAM_LISTS)))) {
                        presetParm.setParmlists(JsonUtil.Json2List(cursor.getString(cursor.getColumnIndex(Provider.PresetParmsColumns.PRAM_LISTS))
                                , PictureProcessingData.class));
                    }
                    if (!StringUtil.isBlank(cursor.getString(cursor
                            .getColumnIndex(Provider.PresetParmsColumns.COVER_ID)))) {
                        presetParm.setCoverId((cursor.getString(cursor
                                .getColumnIndex(Provider.PresetParmsColumns.COVER_ID))));
                    }
                    if (token != QUERY_TOKEN) {
                        if (!StringUtil.isBlank(cursor.getString(cursor
                                .getColumnIndex(Provider.PresetParmsColumns.PART_PRAM_LISTS)))) {
                            presetParm.setPartParmlists(JsonUtil.Json2List(cursor.getString(cursor.getColumnIndex(Provider.PresetParmsColumns.PART_PRAM_LISTS))
                                    , PictureProcessingData.class));
                        }
                    }

                    if (!StringUtil.isBlank(cursor.getString(cursor
                            .getColumnIndex(Provider.PresetParmsColumns.SIZE)))) {
                        presetParm.setSize(cursor.getString(cursor
                                .getColumnIndex(Provider.PresetParmsColumns.SIZE)));
                    }

                    list.add(presetParm);
                }
            }

            if (listener != null) {

                if (token == SEARCH_TOKEN) {
                    if (list.size() == 1) {
                        listener.queryListener(list.get(0), -1, list.size());
                    } else {
                        listener.queryListener(-1, -1, list.size());
                    }

                } else if (token == QUERY_TOKEN) {
                    listener.queryListener(list, list.size(), -1);
                }
            }
        }

        @Override
        protected void onInsertComplete(int token, Object cookie, Uri uri) {
            super.onInsertComplete(token, cookie, uri);
            if (listener != null) {
                listener.insertListener();
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
