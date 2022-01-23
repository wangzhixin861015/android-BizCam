package com.bcnetech.hyphoto.sql.contentprovider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import com.bcnetech.hyphoto.sql.DatabaseHelper;
import com.bcnetech.hyphoto.sql.Provider;

import java.util.HashMap;

/**
 * Created by wenbin on 16/9/18.
 */

public class ImageUtilContentProvider extends ContentProvider {

    private static HashMap<String, String> sPersonsProjectionMap;

    private static final int PERSONS = 1;
    private static final int PERSONS_ID = 2;

    private static final UriMatcher sUriMatcher;

    private DatabaseHelper mOpenHelper;

    @Override
    public boolean onCreate() {
        mOpenHelper = new DatabaseHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(Provider.ImageColums.TABLE_NAME);
        switch (sUriMatcher.match(uri)) {
            case PERSONS:
                qb.setProjectionMap(sPersonsProjectionMap);
                break;

            case PERSONS_ID:
                qb.setProjectionMap(sPersonsProjectionMap);
                qb.appendWhere(Provider.ImageColums._ID + "=" + uri.getPathSegments().get(1));
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        // If no sort order is specified use the default
        String orderBy;
        if (TextUtils.isEmpty(sortOrder)) {
            orderBy = Provider.ImageColums.DEFAULT_SORT_ORDER;
        } else {
            orderBy = sortOrder;
        }

        // Get the database and run the query
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, orderBy);

        // Tell the cursor what uri to watch, so it knows when its source data changes
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case PERSONS:
                return Provider.CONTENT_TYPE;
            case PERSONS_ID:
                return Provider.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues initialValues) {
        // Validate the requested uri
        if (sUriMatcher.match(uri) != PERSONS) {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        ContentValues values;
        if (initialValues != null) {
            values = new ContentValues(initialValues);
        } else {
            values = new ContentValues();
        }

        // Make sure that the fields are all set
        if (values.containsKey(Provider.ImageColums.LIGHT_LOCALURL) == false) {
            values.put(Provider.ImageColums.LIGHT_LOCALURL, "");
        }

        if (values.containsKey(Provider.ImageColums.LIGHT_SMALLLOCALURL) == false) {
            values.put(Provider.ImageColums.LIGHT_SMALLLOCALURL, "");
        }

        if (values.containsKey(Provider.ImageColums.LIGHT_TIME) == false) {
            values.put(Provider.ImageColums.LIGHT_TIME, "");
        }

        if (values.containsKey(Provider.ImageColums.LIGHT_RATIO_DATA) == false) {
            values.put(Provider.ImageColums.LIGHT_RATIO_DATA, "");
        }

        if (values.containsKey(Provider.ImageColums.IMAGETOOL) == false) {
            values.put(Provider.ImageColums.IMAGETOOL, "");
        }
        if (values.containsKey(Provider.ImageColums.IMAGEPARMS) == false) {
            values.put(Provider.ImageColums.IMAGEPARMS, "");
        }

        if (values.containsKey(Provider.ImageColums.CURRENT_POSITION) == false) {
            values.put(Provider.ImageColums.CURRENT_POSITION, "");
        }

        if (values.containsKey(Provider.ImageColums.IS_MATTING) == false) {
            values.put(Provider.ImageColums.IS_MATTING, "");
        }

        if (values.containsKey(Provider.ImageColums.PRESETPARMS) == false) {
            values.put(Provider.ImageColums.PRESETPARMS, "");
        }

        if (values.containsKey(Provider.ImageColums.CAMERA_PARM) == false) {
            values.put(Provider.ImageColums.CAMERA_PARM, "");
        }

        if (values.containsKey(Provider.ImageColums.RECODER_TIME) == false) {
            values.put(Provider.ImageColums.RECODER_TIME, "");
        }

        if (values.containsKey(Provider.ImageColums.SIZE) == false) {
            values.put(Provider.ImageColums.SIZE, "");
        }

        if (values.containsKey(Provider.ImageColums.VALUE1) == false) {
            values.put(Provider.ImageColums.VALUE1, "");
        }

        if (values.containsKey(Provider.ImageColums.VALUE2) == false) {
            values.put(Provider.ImageColums.VALUE2, "");
        }

        if (values.containsKey(Provider.ImageColums.VALUE3) == false) {
            values.put(Provider.ImageColums.VALUE3, "");
        }

        if (values.containsKey(Provider.ImageColums.VALUE4) == false) {
            values.put(Provider.ImageColums.VALUE4, "");
        }

        if (values.containsKey(Provider.ImageColums.VALUE5) == false) {
            values.put(Provider.ImageColums.VALUE5, "");
        }

        if (values.containsKey(Provider.ImageColums.VALUE6) == false) {
            values.put(Provider.ImageColums.VALUE6, "");
        }

        if (values.containsKey(Provider.ImageColums.VALUE7) == false) {
            values.put(Provider.ImageColums.VALUE7, "");
        }

        if (values.containsKey(Provider.ImageColums.VALUE8) == false) {
            values.put(Provider.ImageColums.VALUE8, "");
        }

        if (values.containsKey(Provider.ImageColums.VALUE9) == false) {
            values.put(Provider.ImageColums.VALUE9, "");
        }

        if (values.containsKey(Provider.ImageColums.VALUE10) == false) {
            values.put(Provider.ImageColums.VALUE10, "");
        }


        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        long rowId = db.insert(Provider.ImageColums.TABLE_NAME, Provider.ImageColums.LIGHT_LOCALURL, values);
        if (rowId > 0) {
            Uri noteUri = ContentUris.withAppendedId(Provider.ImageColums.CONTENT_URI, rowId);
            getContext().getContentResolver().notifyChange(noteUri, null);
            return noteUri;
        }

        throw new SQLException("Failed to insert row into " + uri);
    }

    @Override
    public int delete(Uri uri, String where, String[] whereArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count;
        switch (sUriMatcher.match(uri)) {
            case PERSONS:
                count = db.delete(Provider.ImageColums.TABLE_NAME, where, whereArgs);
                break;

            case PERSONS_ID:
                String noteId = uri.getPathSegments().get(1);
                count = db.delete(Provider.ImageColums.TABLE_NAME, Provider.ImageColums._ID + "=" + noteId
                        + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String where, String[] whereArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count;
        switch (sUriMatcher.match(uri)) {
            case PERSONS:
                count = db.update(Provider.ImageColums.TABLE_NAME, values, where, whereArgs);
                break;

            case PERSONS_ID:
                String noteId = uri.getPathSegments().get(1);
                count = db.update(Provider.ImageColums.TABLE_NAME, values, Provider.ImageColums._ID + "=" + noteId
                        + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }


    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        // 这个地方的persons要和PersonColumns.CONTENT_URI中最后面的一个Segment一致
        sUriMatcher.addURI(Provider.ImageColums.AUTHORITY, "imageutils", PERSONS);
        sUriMatcher.addURI(Provider.ImageColums.AUTHORITY, "imageutils/#", PERSONS_ID);

        sPersonsProjectionMap = new HashMap<String, String>();
        sPersonsProjectionMap.put(Provider.ImageColums._ID, Provider.ImageColums._ID);
        sPersonsProjectionMap.put(Provider.ImageColums.LIGHT_LOCALURL, Provider.ImageColums.LIGHT_LOCALURL);
        sPersonsProjectionMap.put(Provider.ImageColums.LIGHT_SMALLLOCALURL, Provider.ImageColums.LIGHT_SMALLLOCALURL);
        sPersonsProjectionMap.put(Provider.ImageColums.LIGHT_TIME, Provider.ImageColums.LIGHT_TIME);
        sPersonsProjectionMap.put(Provider.ImageColums.LIGHT_RATIO_DATA, Provider.ImageColums.LIGHT_RATIO_DATA);
        sPersonsProjectionMap.put(Provider.ImageColums.IMAGETOOL, Provider.ImageColums.IMAGETOOL);
        sPersonsProjectionMap.put(Provider.ImageColums.IMAGEPARMS, Provider.ImageColums.IMAGEPARMS);
        sPersonsProjectionMap.put(Provider.ImageColums.PRESETPARMS, Provider.ImageColums.PRESETPARMS);
        sPersonsProjectionMap.put(Provider.ImageColums.CURRENT_POSITION, Provider.ImageColums.CURRENT_POSITION);
        sPersonsProjectionMap.put(Provider.ImageColums.IS_MATTING, Provider.ImageColums.IS_MATTING);
        sPersonsProjectionMap.put(Provider.ImageColums.CAMERA_PARM, Provider.ImageColums.CAMERA_PARM);
        sPersonsProjectionMap.put(Provider.ImageColums.RECODER_TIME, Provider.ImageColums.RECODER_TIME);
        sPersonsProjectionMap.put(Provider.ImageColums.SIZE, Provider.ImageColums.SIZE);
        sPersonsProjectionMap.put(Provider.ImageColums.VALUE1, Provider.ImageColums.VALUE1);
        sPersonsProjectionMap.put(Provider.ImageColums.VALUE2, Provider.ImageColums.VALUE2);
        sPersonsProjectionMap.put(Provider.ImageColums.VALUE3, Provider.ImageColums.VALUE3);
        sPersonsProjectionMap.put(Provider.ImageColums.VALUE4, Provider.ImageColums.VALUE4);
        sPersonsProjectionMap.put(Provider.ImageColums.VALUE5, Provider.ImageColums.VALUE5);
        sPersonsProjectionMap.put(Provider.ImageColums.VALUE6, Provider.ImageColums.VALUE6);
        sPersonsProjectionMap.put(Provider.ImageColums.VALUE7, Provider.ImageColums.VALUE7);
        sPersonsProjectionMap.put(Provider.ImageColums.VALUE8, Provider.ImageColums.VALUE8);
        sPersonsProjectionMap.put(Provider.ImageColums.VALUE9, Provider.ImageColums.VALUE9);
        sPersonsProjectionMap.put(Provider.ImageColums.VALUE10, Provider.ImageColums.VALUE10);
    }
}

