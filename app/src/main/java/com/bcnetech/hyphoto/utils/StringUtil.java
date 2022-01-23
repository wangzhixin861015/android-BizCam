package com.bcnetech.hyphoto.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bcnetech.bcnetechlibrary.util.LogUtil;
import com.bcnetech.hyphoto.data.GridItem;

import java.text.ParseException;
import java.util.List;

/**
 * Created by wenbin on 16/9/20.
 */

public class StringUtil extends com.bcnetech.bcnetechlibrary.util.StringUtil {

    /***
     * 获取全部图片地址
     * @param mContext
     * @param mGirdList
     * @return
     */
    public static List<GridItem> getLocalPic(Context mContext, List<GridItem> mGirdList) {

        Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        ContentResolver mContentResolver = mContext.getContentResolver();

        // 只查询jpeg和png的图片
        Cursor mCursor = mContentResolver.query(mImageUri, null,
                MediaStore.Images.Media.MIME_TYPE + "=? or "
                        + MediaStore.Images.Media.MIME_TYPE + "=?",
                new String[]{"image/jpeg", "image/png"},
                MediaStore.Images.Media.DATE_MODIFIED);

        // Cursor mCursor = mContentResolver.query(mImageUri, null, null, null, null);
        while (mCursor.moveToNext()) {
            // 获取图片的路径
            String path = "file://" + mCursor.getString(mCursor
                    .getColumnIndex(MediaStore.Images.Media.DATA));
            long times = mCursor.getLong(mCursor
                    .getColumnIndex(MediaStore.Images.Media.DATE_ADDED));
            try {
                GridItem mGridItem = new GridItem(path, paserTimeToYM(times * 1000L));
                mGirdList.add(mGridItem);
            } catch (ParseException e) {

            }


        }
        mCursor.close();

        return mGirdList;
    }


    /**
     * 获取小图规则    code=0  原图    code=1   小图
     *
     * @param url
     * @param width
     * @param height
     * @return
     */
    public static String getSizeUrl(String url, int width, int height) {
        JSONArray jsonArray = new JSONArray();
        ImageSize imageSize = new ImageSize();
        int count=0;
        if (width != 0) {
            imageSize.setW(width);
            count++;
        }
        if (height != 0) {
            imageSize.setH(height);
            count++;
        }

        String urll;
        if(count==0){
            urll = url + "?code=0";
        }
        else{
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("scale", JsonUtil.Object2JsonObject(imageSize));
            jsonArray.add(jsonObject);
            LogUtil.d("   "+jsonArray.toJSONString());
            String size = TestBase64Url.base64UrlEncode(jsonArray.toString().getBytes());

            if(width<1024){
                urll = url + "?style=288@2x&code=1";
            }else {
                urll = url + "?style=512@2x&code=1";
            }
            LogUtil.d(urll);
        }

        return urll;
    }


    public static class ImageSize {

        private int w;
        private int h;

        public int getW() {
            return w;
        }

        public void setW(int w) {
            this.w = w;
        }

        public int getH() {
            return h;
        }

        public void setH(int h) {
            this.h = h;
        }
    }
}
