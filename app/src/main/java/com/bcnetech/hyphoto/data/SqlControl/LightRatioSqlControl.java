package com.bcnetech.hyphoto.data.SqlControl;

import android.app.Activity;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import com.bcnetech.bcnetechlibrary.util.StringUtil;
import com.bcnetech.hyphoto.sql.Provider;
import com.bcnetech.hyphoto.sql.dao.LightRatioData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by wenbin on 16/11/3.
 */

public class LightRatioSqlControl extends BaseSqlControl {
    private LightRatioAsyncQueryHandler queryHandler;

    public LightRatioSqlControl(Activity activity) {
        super(activity);
        queryHandler = new LightRatioAsyncQueryHandler(activity.getContentResolver());
    }

    //查询数据
    public void startQuery() {
        if(queryHandler==null){
            queryHandler = new LightRatioAsyncQueryHandler(activity.getContentResolver());
        }
        //查询
        queryHandler.startQuery(QUERY_TOKEN, null, Provider.LightRatioColums.CONTENT_URI, null, null, null, "_LightId desc");
    }

    public void startDel(String[] selectionArgs ){
        if(queryHandler==null){
            queryHandler = new LightRatioAsyncQueryHandler(activity.getContentResolver());
        }
        //查询
        queryHandler.startDelete(QUERY_TOKEN,null, Provider.LightRatioColums.CONTENT_URI,Provider.LightRatioColums._ID +"=?",selectionArgs);

    }

    public void startUpdate(LightRatioData fromLightRatioData,LightRatioData toLightRatioData){
        if (queryHandler == null) {
            queryHandler = new LightRatioAsyncQueryHandler(activity.getContentResolver());
        }

        ContentValues fromValues = new ContentValues();
        fromValues.put(Provider.LightRatioColums.LIGHT_ID, fromLightRatioData.getLightId());

        queryHandler.startUpdate(UPDATE_TOKEN, null, Provider.LightRatioColums.CONTENT_URI, fromValues, Provider.PresetParmsColumns._ID + "=?", new String[]{fromLightRatioData.getId()});

        ContentValues toValues = new ContentValues();
        toValues.put(Provider.LightRatioColums.LIGHT_ID, toLightRatioData.getLightId());
        queryHandler.startUpdate(UPDATE_TOKEN, null, Provider.LightRatioColums.CONTENT_URI, toValues, Provider.PresetParmsColumns._ID + "=?", new String[]{toLightRatioData.getId()});

    }

    public void startInsert(LightRatioData lightRatioData) {
        if (StringUtil.isBlank(lightRatioData.getName())) {
            return;
        }
        ContentValues values = new ContentValues();

        values.put(Provider.LightRatioColums.LIGHT_NAME, lightRatioData.getName());
        values.put(Provider.LightRatioColums.LIGHT_LEFT, lightRatioData.getLeftLight());
        values.put(Provider.LightRatioColums.LIGHT_RIGHT, lightRatioData.getRightLight());
        values.put(Provider.LightRatioColums.LIGHT_BOTTOM, lightRatioData.getBottomLight());
        values.put(Provider.LightRatioColums.LIGHT_BACKGROUD, lightRatioData.getBackgroudLight());
        values.put(Provider.LightRatioColums.LIGHT_MOVE, lightRatioData.getMoveLight());
        values.put(Provider.LightRatioColums.LIGHT_TOP, lightRatioData.getTopLight());
        values.put(Provider.LightRatioColums.LIGHT_LIGHT1, lightRatioData.getLight1());
        values.put(Provider.LightRatioColums.LIGHT_LIGHT2, lightRatioData.getLight2());
        values.put(Provider.LightRatioColums.LIGHT_LIGHT3, lightRatioData.getLight3());
        values.put(Provider.LightRatioColums.LIGHT_LIGHT4, lightRatioData.getLight4());
        values.put(Provider.LightRatioColums.LIGHT_LIGHT5, lightRatioData.getLight5());
        values.put(Provider.LightRatioColums.LIGHT_NUM, lightRatioData.getNum());
        values.put(Provider.LightRatioColums.LIGHT_ID, System.currentTimeMillis()+"");
        values.put(Provider.LightRatioColums.VERSION, lightRatioData.getVersion()+"");
        queryHandler.startInsert(INSERT_TOKEN, null, Provider.LightRatioColums.CONTENT_URI, values);
    }

    public class LightRatioAsyncQueryHandler extends AsyncQueryHandler {
        public LightRatioAsyncQueryHandler(ContentResolver cr) {
            super(cr);
        }

        @Override
        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
            super.onQueryComplete(token, cookie, cursor);
            List list=new ArrayList();
            if (cursor == null) return;

            while (cursor.moveToNext()) {
                LightRatioData lightRatioData = new LightRatioData();
                lightRatioData.setId(cursor.getString(cursor
                        .getColumnIndex(Provider.LightRatioColums._ID)));
                lightRatioData.setLightId(cursor.getString(cursor
                        .getColumnIndex(Provider.LightRatioColums.LIGHT_ID)));
                lightRatioData.setName(cursor.getString(cursor
                        .getColumnIndex(Provider.LightRatioColums.LIGHT_NAME)));
                lightRatioData.setVersion(cursor.getString(cursor
                        .getColumnIndex(Provider.LightRatioColums.VERSION)));
                lightRatioData.setLeftLight(Integer.valueOf(cursor.getString(cursor
                        .getColumnIndex(Provider.LightRatioColums.LIGHT_LEFT))));
                lightRatioData.setRightLight(Integer.valueOf(cursor.getString(cursor
                        .getColumnIndex(Provider.LightRatioColums.LIGHT_RIGHT))));
                lightRatioData.setBottomLight(Integer.valueOf(cursor.getString(cursor
                        .getColumnIndex(Provider.LightRatioColums.LIGHT_BOTTOM))));
                lightRatioData.setBackgroudLight(Integer.valueOf(cursor.getString(cursor
                        .getColumnIndex(Provider.LightRatioColums.LIGHT_BACKGROUD))));
                lightRatioData.setMoveLight(Integer.valueOf(cursor.getString(cursor
                        .getColumnIndex(Provider.LightRatioColums.LIGHT_MOVE))));
                lightRatioData.setTopLight(Integer.valueOf(cursor.getString(cursor
                        .getColumnIndex(Provider.LightRatioColums.LIGHT_TOP))));
                lightRatioData.setLight1(Integer.valueOf(cursor.getString(cursor
                        .getColumnIndex(Provider.LightRatioColums.LIGHT_LIGHT1))));
                lightRatioData.setLight2(Integer.valueOf(cursor.getString(cursor
                        .getColumnIndex(Provider.LightRatioColums.LIGHT_LIGHT2))));
                lightRatioData.setLight3(Integer.valueOf(cursor.getString(cursor
                        .getColumnIndex(Provider.LightRatioColums.LIGHT_LIGHT3))));
                lightRatioData.setLight4(Integer.valueOf(cursor.getString(cursor
                        .getColumnIndex(Provider.LightRatioColums.LIGHT_LIGHT4))));
                lightRatioData.setLight5(Integer.valueOf(cursor.getString(cursor
                        .getColumnIndex(Provider.LightRatioColums.LIGHT_LIGHT5))));
                lightRatioData.setNum(Integer.valueOf(cursor.getString(cursor
                        .getColumnIndex(Provider.LightRatioColums.LIGHT_NUM))));

                list.add(lightRatioData);
            }
            Collections.sort(list, new Comparator<LightRatioData>() {
                @Override
                public int compare(LightRatioData lhs, LightRatioData rhs) {
                    if (lhs.getNum() < rhs.getNum()) {
                        return 1;

                    } else if (lhs.getNum() == rhs.getNum()) {
                        return 0;
                    }
                    return -1;
                }
            });

            if(listener!=null){
                listener.queryListener(list);
            }

        }

        @Override
        protected void onInsertComplete(int token, Object cookie, Uri uri) {
            super.onInsertComplete(token, cookie, uri);
            if(listener!=null){
                listener.insertListener();
            }
        }

        @Override
        protected void onDeleteComplete(int token, Object cookie, int result) {
            super.onDeleteComplete(token, cookie, result);
            if(listener!=null){
                listener.deletListener();
            }
        }
    }



}
