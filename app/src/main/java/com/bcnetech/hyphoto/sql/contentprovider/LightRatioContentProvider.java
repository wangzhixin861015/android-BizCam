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
 * Created by wenbin on 16/8/4.
 */

public class LightRatioContentProvider extends ContentProvider{

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
        qb.setTables(Provider.LightRatioColums.TABLE_NAME);

        switch (sUriMatcher.match(uri)) {
            case PERSONS:
                qb.setProjectionMap(sPersonsProjectionMap);
                break;

            case PERSONS_ID:
                qb.setProjectionMap(sPersonsProjectionMap);
                qb.appendWhere(Provider.LightRatioColums._ID + "=" + uri.getPathSegments().get(1));
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        // If no sort order is specified use the default
        String orderBy;
        if (TextUtils.isEmpty(sortOrder)) {
            orderBy = Provider.LightRatioColums.DEFAULT_SORT_ORDER;
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
        if (values.containsKey(Provider.LightRatioColums.LIGHT_NAME) == false) {
            values.put(Provider.LightRatioColums.LIGHT_NAME, "");
        }

        if (values.containsKey(Provider.LightRatioColums.LIGHT_LEFT) == false) {
            values.put(Provider.LightRatioColums.LIGHT_LEFT, "");
        }
        if (values.containsKey(Provider.LightRatioColums.LIGHT_RIGHT) == false) {
            values.put(Provider.LightRatioColums.LIGHT_RIGHT, "");
        }
        if (values.containsKey(Provider.LightRatioColums.LIGHT_BOTTOM) == false) {
            values.put(Provider.LightRatioColums.LIGHT_BOTTOM, "");
        }
        if (values.containsKey(Provider.LightRatioColums.LIGHT_BACKGROUD) == false) {
            values.put(Provider.LightRatioColums.LIGHT_BACKGROUD, "");
        }
        if (values.containsKey(Provider.LightRatioColums.LIGHT_MOVE) == false) {
            values.put(Provider.LightRatioColums.LIGHT_MOVE, "");
        }
        if (values.containsKey(Provider.LightRatioColums.LIGHT_LIGHT1) == false) {
            values.put(Provider.LightRatioColums.LIGHT_LIGHT1, "");
        }
        if (values.containsKey(Provider.LightRatioColums.LIGHT_LIGHT2) == false) {
            values.put(Provider.LightRatioColums.LIGHT_LIGHT2, "");
        }
        if (values.containsKey(Provider.LightRatioColums.LIGHT_LIGHT3) == false) {
            values.put(Provider.LightRatioColums.LIGHT_LIGHT3, "");
        }
        if (values.containsKey(Provider.LightRatioColums.LIGHT_LIGHT4) == false) {
            values.put(Provider.LightRatioColums.LIGHT_LIGHT4, "");
        }
        if (values.containsKey(Provider.LightRatioColums.LIGHT_LIGHT5) == false) {
            values.put(Provider.LightRatioColums.LIGHT_LIGHT5, "");
        }

        if (values.containsKey(Provider.LightRatioColums.LIGHT_ID) == false) {
            values.put(Provider.LightRatioColums.LIGHT_ID, "");
        }
        if (values.containsKey(Provider.LightRatioColums.LIGHT_TOP) == false) {
            values.put(Provider.LightRatioColums.LIGHT_TOP, "");
        }

        if (values.containsKey(Provider.LightRatioColums.VERSION) == false) {
            values.put(Provider.LightRatioColums.VERSION, "");
        }

        if (values.containsKey(Provider.LightRatioColums.LIGHT_NUM) == false) {
            values.put(Provider.LightRatioColums.LIGHT_NUM, "");
        }

        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        long rowId = db.insert(Provider.LightRatioColums.TABLE_NAME, Provider.LightRatioColums.LIGHT_NAME, values);
        if (rowId > 0) {
            Uri noteUri = ContentUris.withAppendedId(Provider.LightRatioColums.CONTENT_URI, rowId);
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
                count = db.delete(Provider.LightRatioColums.TABLE_NAME, where, whereArgs);
                break;

            case PERSONS_ID:
                String noteId = uri.getPathSegments().get(1);
                count = db.delete(Provider.LightRatioColums.TABLE_NAME, Provider.LightRatioColums._ID + "=" + noteId
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
                count = db.update(Provider.LightRatioColums.TABLE_NAME, values, where, whereArgs);
                break;

            case PERSONS_ID:
                String noteId = uri.getPathSegments().get(1);
                count = db.update(Provider.LightRatioColums.TABLE_NAME, values, Provider.LightRatioColums._ID + "=" + noteId
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
        sUriMatcher.addURI(Provider.LightRatioColums.AUTHORITY, "lightratios", PERSONS);
        sUriMatcher.addURI(Provider.LightRatioColums.AUTHORITY, "lightratios/#", PERSONS_ID);

        sPersonsProjectionMap = new HashMap<String, String>();
        sPersonsProjectionMap.put(Provider.LightRatioColums._ID, Provider.LightRatioColums._ID);
        sPersonsProjectionMap.put(Provider.LightRatioColums.LIGHT_NAME, Provider.LightRatioColums.LIGHT_NAME);
        sPersonsProjectionMap.put(Provider.LightRatioColums.LIGHT_LEFT, Provider.LightRatioColums.LIGHT_LEFT);
        sPersonsProjectionMap.put(Provider.LightRatioColums.LIGHT_RIGHT, Provider.LightRatioColums.LIGHT_RIGHT);
        sPersonsProjectionMap.put(Provider.LightRatioColums.LIGHT_BOTTOM, Provider.LightRatioColums.LIGHT_BOTTOM);
        sPersonsProjectionMap.put(Provider.LightRatioColums.LIGHT_BACKGROUD, Provider.LightRatioColums.LIGHT_BACKGROUD);
        sPersonsProjectionMap.put(Provider.LightRatioColums.LIGHT_MOVE, Provider.LightRatioColums.LIGHT_MOVE);
        sPersonsProjectionMap.put(Provider.LightRatioColums.LIGHT_TOP, Provider.LightRatioColums.LIGHT_TOP);
        sPersonsProjectionMap.put(Provider.LightRatioColums.LIGHT_LIGHT1, Provider.LightRatioColums.LIGHT_LIGHT1);
        sPersonsProjectionMap.put(Provider.LightRatioColums.LIGHT_LIGHT2, Provider.LightRatioColums.LIGHT_LIGHT2);
        sPersonsProjectionMap.put(Provider.LightRatioColums.LIGHT_LIGHT3, Provider.LightRatioColums.LIGHT_LIGHT3);
        sPersonsProjectionMap.put(Provider.LightRatioColums.LIGHT_LIGHT4, Provider.LightRatioColums.LIGHT_LIGHT4);
        sPersonsProjectionMap.put(Provider.LightRatioColums.LIGHT_LIGHT5, Provider.LightRatioColums.LIGHT_LIGHT5);
        sPersonsProjectionMap.put(Provider.LightRatioColums.LIGHT_ID, Provider.LightRatioColums.LIGHT_ID);
        sPersonsProjectionMap.put(Provider.LightRatioColums.LIGHT_NUM, Provider.LightRatioColums.LIGHT_NUM);
        sPersonsProjectionMap.put(Provider.LightRatioColums.VERSION, Provider.LightRatioColums.VERSION);
    }
}
