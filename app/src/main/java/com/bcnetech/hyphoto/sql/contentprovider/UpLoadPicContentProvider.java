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
 * Created by wenbin on 2017/1/16.
 */

public class UpLoadPicContentProvider extends ContentProvider {
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
        qb.setTables(Provider.UploadPicColums.TABLE_NAME);

        switch (sUriMatcher.match(uri)) {
            case PERSONS:
                qb.setProjectionMap(sPersonsProjectionMap);
                break;

            case PERSONS_ID:
                qb.setProjectionMap(sPersonsProjectionMap);
                qb.appendWhere(Provider.UploadPicColums._ID + "=" + uri.getPathSegments().get(1));
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        // If no sort order is specified use the default
        String orderBy;
        if (TextUtils.isEmpty(sortOrder)) {
            orderBy = Provider.UploadPicColums.DEFAULT_SORT_ORDER;
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

        if (values.containsKey(Provider.UploadPicColums.IMAGEDATA) == false) {
            values.put(Provider.UploadPicColums.IMAGEDATA, "");
        }
        if (values.containsKey(Provider.UploadPicColums.UPLOAD_TIME) == false) {
            values.put(Provider.UploadPicColums.UPLOAD_TIME, "");
        }
        if (values.containsKey(Provider.UploadPicColums.LOCAL_URL) == false) {
            values.put(Provider.UploadPicColums.LOCAL_URL, "");
        }
        if (values.containsKey(Provider.UploadPicColums.VALUE1) == false) {
            values.put(Provider.UploadPicColums.VALUE1, "");
        }
        if (values.containsKey(Provider.UploadPicColums.VALUE2) == false) {
            values.put(Provider.UploadPicColums.VALUE2, "");
        }


        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        long rowId = db.insert(Provider.UploadPicColums.TABLE_NAME, Provider.UploadPicColums.LOCAL_URL, values);
        if (rowId > 0) {
            Uri noteUri = ContentUris.withAppendedId(Provider.UploadPicColums.CONTENT_URI, rowId);
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
                count = db.delete(Provider.UploadPicColums.TABLE_NAME, where, whereArgs);
                break;

            case PERSONS_ID:
                String noteId = uri.getPathSegments().get(1);
                count = db.delete(Provider.UploadPicColums.TABLE_NAME, Provider.UploadPicColums._ID + "=" + noteId
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
                count = db.update(Provider.UploadPicColums.TABLE_NAME, values, where, whereArgs);
                break;

            case PERSONS_ID:
                String noteId = uri.getPathSegments().get(1);
                count = db.update(Provider.UploadPicColums.TABLE_NAME, values, Provider.UploadPicColums._ID + "=" + noteId
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
        sUriMatcher.addURI(Provider.UploadPicColums.AUTHORITY, "uploadmanages", PERSONS);
        sUriMatcher.addURI(Provider.UploadPicColums.AUTHORITY, "uploadmanages/#", PERSONS_ID);

        sPersonsProjectionMap = new HashMap<String, String>();
        sPersonsProjectionMap.put(Provider.UploadPicColums._ID, Provider.UploadPicColums._ID);
        sPersonsProjectionMap.put(Provider.UploadPicColums.IMAGEDATA, Provider.UploadPicColums.IMAGEDATA);
        sPersonsProjectionMap.put(Provider.UploadPicColums.LOCAL_URL, Provider.UploadPicColums.LOCAL_URL);
        sPersonsProjectionMap.put(Provider.UploadPicColums.UPLOAD_TIME, Provider.UploadPicColums.UPLOAD_TIME);
        sPersonsProjectionMap.put(Provider.UploadPicColums.VALUE1, Provider.UploadPicColums.VALUE1);
        sPersonsProjectionMap.put(Provider.UploadPicColums.VALUE2, Provider.UploadPicColums.VALUE2);

    }
}
