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
import com.bcnetech.hyphoto.sql.dao.ImageData;
import com.bcnetech.hyphoto.sql.dao.LightRatioData;
import com.bcnetech.hyphoto.sql.dao.PictureProcessingData;
import com.bcnetech.hyphoto.sql.dao.PresetParm;
import com.bcnetech.hyphoto.utils.JsonUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wenbin on 16/11/3.
 */

public class ImageDataSqlControl extends BaseSqlControl {
    private ImageDataAsunvQueryHandler imageDataAsunvQueryHandler;

    public ImageDataSqlControl(Activity activity) {
        super(activity);
        imageDataAsunvQueryHandler = new ImageDataAsunvQueryHandler(activity.getContentResolver());
    }

    /**
     * 插入图片信息
     *
     * @param imageData
     */
    public void insertImg(ImageData imageData) {
        if (imageData == null) {
            return;
        }
        if (imageDataAsunvQueryHandler == null) {
            imageDataAsunvQueryHandler = new ImageDataAsunvQueryHandler(activity.getContentResolver());
        }

        ContentValues values = new ContentValues();
        values.put(Provider.ImageColums.VALUE1, imageData.getType() + "");//图片类型
        values.put(Provider.ImageColums.LIGHT_LOCALURL, imageData.getLocalUrl() + "");
        values.put(Provider.ImageColums.LIGHT_SMALLLOCALURL, imageData.getSmallLocalUrl() + "");
        values.put(Provider.ImageColums.LIGHT_TIME, imageData.getTimeStamp() + "");
        values.put(Provider.ImageColums.CURRENT_POSITION, imageData.getCurrentPosition() + "");
        values.put(Provider.ImageColums.IS_MATTING, imageData.isMatting() + "");
        values.put(Provider.ImageColums.RECODER_TIME, imageData.getRecoderTime() + "");
        if (imageData.getLightRatioData() != null) {
            values.put(Provider.ImageColums.LIGHT_RATIO_DATA, JsonUtil.Object2Json(imageData.getLightRatioData()) + "");
        } else {
            values.put(Provider.ImageColums.LIGHT_RATIO_DATA, "");
        }

        if (imageData.getImageTools() != null && imageData.getImageTools().size() != 0) {
            values.put(Provider.ImageColums.IMAGETOOL, JsonUtil.list2JsonSerial(imageData.getImageTools()) + "");
        } else {
            values.put(Provider.ImageColums.IMAGETOOL, "");
        }
        if (imageData.getImageParts() != null && imageData.getImageParts().size() != 0) {
            values.put(Provider.ImageColums.IMAGEPARMS, JsonUtil.list2JsonSerial(imageData.getImageParts()) + "");
        } else {
            values.put(Provider.ImageColums.IMAGEPARMS, "");
        }

        if (imageData.getPresetParms() != null) {
            values.put(Provider.ImageColums.PRESETPARMS, JsonUtil.Object2Json(imageData.getPresetParms()));
        } else {
            values.put(Provider.ImageColums.PRESETPARMS, "");
        }

        if (imageData.getCameraParm() != null) {
            values.put(Provider.ImageColums.CAMERA_PARM, JsonUtil.Object2Json(imageData.getCameraParm()));
        } else {
            values.put(Provider.ImageColums.CAMERA_PARM, "");
        }
        if (!StringUtil.isBlank(imageData.getValue3())) {
            values.put(Provider.ImageColums.VALUE3, imageData.getValue3());
        }
        if (!StringUtil.isBlank(imageData.getValue5())) {
            values.put(Provider.ImageColums.VALUE5, imageData.getValue5());
        }
        imageDataAsunvQueryHandler.startInsert(INSERT_TOKEN, null, Provider.ImageColums.CONTENT_URI, values);
    }

    //查询数据
    public void startQuery() {
        //查询
        imageDataAsunvQueryHandler.startQuery(QUERY_TOKEN, null, Provider.ImageColums.CONTENT_URI, null, null, null, "_id desc");
    }

    //查询需要上传的数据数据
    public void startUploadQuery() {
        //查询
        imageDataAsunvQueryHandler.startQuery(SEARCH_TOKEN, null, Provider.ImageColums.CONTENT_URI, null, null, null, "_id desc");
    }


    //查询数据
    public void startQueryByIsUpload() {
        //查询
        imageDataAsunvQueryHandler.startQuery(SEARCH_TOKEN, null, Provider.ImageColums.CONTENT_URI, null, Provider.ImageColums.VALUE4 + "= ? or " + Provider.ImageColums.VALUE4 + " = ? ", new String[]{"0", ""}, null);
    }

    /**
     * 删除图片 根据 图片地址
     *
     * @param selectionArgs
     */
    public void startDel(String[] selectionArgs) {
        imageDataAsunvQueryHandler.startDelete(QUERY_TOKEN, null, Provider.ImageColums.CONTENT_URI, Provider.ImageColums.LIGHT_LOCALURL + "=?", selectionArgs);
    }

    /**
     * 更新图片信息 根据图片url
     *
     * @param strs
     * @param imageData
     */
    public void startUpdata(String[] strs, ImageData imageData) {

        ContentValues values = new ContentValues();
        values.put(Provider.ImageColums.LIGHT_LOCALURL, imageData.getLocalUrl() + "");
        values.put(Provider.ImageColums.LIGHT_SMALLLOCALURL, imageData.getSmallLocalUrl() + "");
        values.put(Provider.ImageColums.LIGHT_TIME, imageData.getTimeStamp() + "");
        values.put(Provider.ImageColums.CURRENT_POSITION, imageData.getCurrentPosition() + "");
        values.put(Provider.ImageColums.IS_MATTING, imageData.isMatting() + "");
        values.put(Provider.ImageColums.VALUE1, imageData.getType() + "");
        values.put(Provider.ImageColums.RECODER_TIME, imageData.getRecoderTime() + "");

        if (imageData.getValue2() != null) {
            values.put(Provider.ImageColums.VALUE2, imageData.getValue2() + "");
        } else {
            values.put(Provider.ImageColums.VALUE2, "");
        }
        if (imageData.getLightRatioData() != null) {
            values.put(Provider.ImageColums.LIGHT_RATIO_DATA, JsonUtil.Object2Json(imageData.getLightRatioData()) + "");
        } else {
            values.put(Provider.ImageColums.LIGHT_RATIO_DATA, "");
        }
        if (imageData.getImageTools() != null && imageData.getImageTools().size() != 0) {
            values.put(Provider.ImageColums.IMAGETOOL, JsonUtil.list2JsonSerial(imageData.getImageTools()) + "");
        } else {
            values.put(Provider.ImageColums.IMAGETOOL, "");
        }
        if (imageData.getImageParts() != null && imageData.getImageParts().size() != 0) {
            values.put(Provider.ImageColums.IMAGEPARMS, JsonUtil.list2JsonSerial(imageData.getImageParts()) + "");
        } else {
            values.put(Provider.ImageColums.IMAGEPARMS, "");
        }

        if (imageData.getPresetParms() != null) {
            values.put(Provider.ImageColums.PRESETPARMS, JsonUtil.Object2Json(imageData.getPresetParms()));
        } else {
            values.put(Provider.ImageColums.PRESETPARMS, "");
        }

        if (imageData.getCameraParm() != null) {
            values.put(Provider.ImageColums.CAMERA_PARM, JsonUtil.Object2Json(imageData.getCameraParm()));
        } else {
            values.put(Provider.ImageColums.CAMERA_PARM, "");
        }

        imageDataAsunvQueryHandler.startUpdate(INSERT_TOKEN, null, Provider.ImageColums.CONTENT_URI, values,
                Provider.ImageColums.LIGHT_LOCALURL + "=?", strs);
    }


    /**
     * 更新图片信息 根据图片url
     *
     * @param localUrl
     */
    public void startUpdateByLocalUrl(String localUrl, String fileId, String fileHash) {

        ContentValues values = new ContentValues();
        if (!StringUtil.isBlank(fileId)) {
            values.put(Provider.ImageColums.VALUE3, fileId);
        }

        if (!StringUtil.isBlank(fileHash)) {
            values.put(Provider.ImageColums.VALUE4, fileHash);
        }

        imageDataAsunvQueryHandler.startUpdate(UPDATE_TOKEN, null, Provider.ImageColums.CONTENT_URI, values,
                Provider.ImageColums.LIGHT_LOCALURL + "=? or " + Provider.ImageColums.LIGHT_SMALLLOCALURL + " =?", new String[]{localUrl, localUrl});
    }


    public class ImageDataAsunvQueryHandler extends AsyncQueryHandler {

        public ImageDataAsunvQueryHandler(ContentResolver cr) {
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
            if (cursor == null) return;
            List list = new ArrayList();
            while (cursor.moveToNext()) {
                ImageData imageData = new ImageData();
                imageData.setLocalUrl(cursor.getString(cursor
                        .getColumnIndex(Provider.ImageColums.LIGHT_LOCALURL)));
                imageData.setSmallLocalUrl(cursor.getString(cursor
                        .getColumnIndex(Provider.ImageColums.LIGHT_SMALLLOCALURL)));
                if (!StringUtil.isBlank(cursor.getString(cursor.getColumnIndex(Provider.ImageColums.LIGHT_TIME)))) {
                    imageData.setTimeStamp(Long.valueOf(cursor.getString(cursor
                            .getColumnIndex(Provider.ImageColums.LIGHT_TIME))));
                }
                String currentPosition = cursor.getString(cursor.getColumnIndex(Provider.ImageColums.CURRENT_POSITION));
                if (!StringUtil.isBlank(currentPosition)) {
                    imageData.setCurrentPosition(Integer.valueOf(currentPosition));
                }
                String isMatting = cursor.getString(cursor.getColumnIndex(Provider.ImageColums.IS_MATTING));
                if (!StringUtil.isBlank(isMatting)) {
                    imageData.setMatting(Boolean.valueOf(isMatting));
                }
                String imageType = cursor.getString(cursor.getColumnIndex(Provider.ImageColums.VALUE1));
                String lightRatioData = cursor.getString(cursor.getColumnIndex(Provider.ImageColums.LIGHT_RATIO_DATA));
                String imagetool = cursor.getString(cursor.getColumnIndex(Provider.ImageColums.IMAGETOOL));
                String imageParms = cursor.getString(cursor.getColumnIndex(Provider.ImageColums.IMAGEPARMS));
                String preset = cursor.getString(cursor.getColumnIndex(Provider.ImageColums.PRESETPARMS));
                String cameraParm = cursor.getString(cursor.getColumnIndex(Provider.ImageColums.CAMERA_PARM));
                String recordTime = cursor.getString(cursor.getColumnIndex(Provider.ImageColums.RECODER_TIME));
                String smallParamUrl = cursor.getString(cursor.getColumnIndex(Provider.ImageColums.VALUE2));
                String fileId = cursor.getString(cursor.getColumnIndex(Provider.ImageColums.VALUE3));
                String isUpload = cursor.getString(cursor.getColumnIndex(Provider.ImageColums.VALUE4));
                String title = cursor.getString(cursor.getColumnIndex(Provider.ImageColums.VALUE5));


                if (!StringUtil.isBlank(imageType)) {
                    imageData.setType(Integer.valueOf(imageType));
                }
                if (!StringUtil.isBlank(smallParamUrl)) {
                    imageData.setValue2(smallParamUrl);
                }
                if (!StringUtil.isBlank(fileId)) {
                    imageData.setValue3(fileId);
                }
                if (!StringUtil.isBlank(isUpload)) {
                    imageData.setValue4(isUpload);
                }

                if (!StringUtil.isBlank(title)) {
                    imageData.setValue5(title);
                }

                if (!StringUtil.isBlank(recordTime)) {
                    imageData.setRecoderTime(Integer.parseInt(recordTime));
                }

                if (!StringUtil.isBlank(lightRatioData)) {
                    imageData.setLightRatioData(JsonUtil.Json2T(lightRatioData, LightRatioData.class));
                }

                if (!StringUtil.isBlank(imagetool)) {
                    imageData.setImageTools(JsonUtil.Json2List(imagetool, PictureProcessingData.class));
                }

                if (!StringUtil.isBlank(imageParms)) {
                    imageData.setImageParts(JsonUtil.Json2List(imageParms, PictureProcessingData.class));
                }

                if (!StringUtil.isBlank(preset)) {
                    imageData.setPresetParms(JsonUtil.Json2T(preset, PresetParm.class));
                }

                if (!StringUtil.isBlank(cameraParm)) {
                    imageData.setCameraParm(JsonUtil.Json2T(cameraParm, CameraParm.class));
                }


                list.add(imageData);
            }
            if (listener != null) {
                listener.queryListener(list, token);
            }
        }

        @Override
        protected void onDeleteComplete(int token, Object cookie, int result) {
            super.onDeleteComplete(token, cookie, result);
        }
    }


}
