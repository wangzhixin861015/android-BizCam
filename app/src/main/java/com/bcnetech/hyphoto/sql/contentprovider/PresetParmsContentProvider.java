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
 * Created by wenbin on 16/9/10.
 */

public  class PresetParmsContentProvider extends ContentProvider {
    private static HashMap<String, String> sprogrammersProjectionMap;

    private static final int PROGRAMMERS = 1;
    private static final int PROGRAMMERS_ID = 2;

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
        qb.setTables(Provider.PresetParmsColumns.TABLE_NAME);

        switch (sUriMatcher.match(uri)) {
            case PROGRAMMERS:
                qb.setProjectionMap(sprogrammersProjectionMap);
                break;

            case PROGRAMMERS_ID:
                qb.setProjectionMap(sprogrammersProjectionMap);
                qb.appendWhere(Provider.PresetParmsColumns._ID + "=" + uri.getPathSegments().get(1));
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        // If no sort order is specified use the default
        String orderBy;
        if (TextUtils.isEmpty(sortOrder)) {
            orderBy = Provider.PresetParmsColumns.DEFAULT_SORT_ORDER;
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
            case PROGRAMMERS:
                return Provider.CONTENT_TYPE;
            case PROGRAMMERS_ID:
                return Provider.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues initialValues) {
        // Validate the requested uri
        if (sUriMatcher.match(uri) != PROGRAMMERS) {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        ContentValues values;
        if (initialValues != null) {
            values = new ContentValues(initialValues);
        } else {
            values = new ContentValues();
        }

        // Make sure that the fields are all set
        if (values.containsKey(Provider.PresetParmsColumns.PRESET_ID) == false) {
            values.put(Provider.PresetParmsColumns.PRESET_ID, "");
        }

        if (values.containsKey(Provider.PresetParmsColumns.NAME) == false) {
            values.put(Provider.PresetParmsColumns.NAME, "");
        }

        if (values.containsKey(Provider.PresetParmsColumns.LABEL) == false) {
            values.put(Provider.PresetParmsColumns.LABEL, "");
        }

        if (values.containsKey(Provider.PresetParmsColumns.SHOW_TYPE) == false) {
            values.put(Provider.PresetParmsColumns.SHOW_TYPE, "");
        }

        if (values.containsKey(Provider.PresetParmsColumns.POSITION) == false) {
            values.put(Provider.PresetParmsColumns.POSITION, "");
        }

        if(values.containsKey(Provider.PresetParmsColumns.DOWNLOAD_COUNT) == false){
            values.put(Provider.PresetParmsColumns.DOWNLOAD_COUNT,"");
        }

        if(values.containsKey(Provider.PresetParmsColumns.IMAGE_HEIGHT) == false){
            values.put(Provider.PresetParmsColumns.IMAGE_HEIGHT,"");
        }

        if(values.containsKey(Provider.PresetParmsColumns.IMAGE_WIDTH) == false){
            values.put(Provider.PresetParmsColumns.IMAGE_WIDTH,"");
        }

        if(values.containsKey(Provider.PresetParmsColumns.CATEGORY_ID) == false){
            values.put(Provider.PresetParmsColumns.CATEGORY_ID,"");
        }

        if(values.containsKey(Provider.PresetParmsColumns.SYSTEM) == false){
            values.put(Provider.PresetParmsColumns.SYSTEM,"");
        }

        if (values.containsKey(Provider.PresetParmsColumns.SIZE) == false) {
            values.put(Provider.PresetParmsColumns.SIZE, "");
        }

        if (values.containsKey(Provider.PresetParmsColumns.AUTHER_URL) == false) {
            values.put(Provider.PresetParmsColumns.AUTHER_URL, "");
        }

        if (values.containsKey(Provider.PresetParmsColumns.IMG_DATA_SIZE) == false) {
            values.put(Provider.PresetParmsColumns.IMG_DATA_SIZE, "");
        }

        if (values.containsKey(Provider.PresetParmsColumns.CAMERA_PARM) == false) {
            values.put(Provider.PresetParmsColumns.CAMERA_PARM, "");
        }
        if (values.containsKey(Provider.PresetParmsColumns.TIME_STAMP) == false) {
            values.put(Provider.PresetParmsColumns.TIME_STAMP, "");
        }
        if (values.containsKey(Provider.PresetParmsColumns.TEXT_SRC) == false) {
            values.put(Provider.PresetParmsColumns.TEXT_SRC, "");
        }

        if (values.containsKey(Provider.PresetParmsColumns.AUTHER) == false) {
            values.put(Provider.PresetParmsColumns.AUTHER, "");
        }

        if (values.containsKey(Provider.PresetParmsColumns.AUTHER_ID) == false) {
            values.put(Provider.PresetParmsColumns.AUTHER_ID, "");
        }

        if (values.containsKey(Provider.PresetParmsColumns.DESCRIBE) == false) {
            values.put(Provider.PresetParmsColumns.DESCRIBE, "");
        }

        if (values.containsKey(Provider.PresetParmsColumns.EQUIPMENT) == false) {
            values.put(Provider.PresetParmsColumns.EQUIPMENT, "");
        }

        if (values.containsKey(Provider.PresetParmsColumns.LIGHT_RATIO_DATA) == false) {
            values.put(Provider.PresetParmsColumns.LIGHT_RATIO_DATA, "");
        }

        if (values.containsKey(Provider.PresetParmsColumns.PRAM_LISTS) == false) {
            values.put(Provider.PresetParmsColumns.PRAM_LISTS, "");
        }
        if (values.containsKey(Provider.PresetParmsColumns.PART_PRAM_LISTS) == false) {
            values.put(Provider.PresetParmsColumns.PART_PRAM_LISTS, "");
        }

        if (values.containsKey(Provider.PresetParmsColumns.COVER_ID) == false) {
            values.put(Provider.PresetParmsColumns.COVER_ID, "");
        }


        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        long rowId = db.insert(Provider.PresetParmsColumns.TABLE_NAME, Provider.PresetParmsColumns.NAME, values);
        if (rowId > 0) {
            Uri noteUri = ContentUris.withAppendedId(Provider.PresetParmsColumns.CONTENT_URI, rowId);
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
            case PROGRAMMERS:
                count = db.delete(Provider.PresetParmsColumns.TABLE_NAME, where, whereArgs);
                break;

            case PROGRAMMERS_ID:
                String noteId = uri.getPathSegments().get(1);
                count = db.delete(Provider.PresetParmsColumns.TABLE_NAME, Provider.PresetParmsColumns._ID + "=" + noteId
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
            case PROGRAMMERS:
                count = db.update(Provider.PresetParmsColumns.TABLE_NAME, values, where, whereArgs);
                break;

            case PROGRAMMERS_ID:
                String noteId = uri.getPathSegments().get(1);
                count = db.update(Provider.PresetParmsColumns.TABLE_NAME, values, Provider.PresetParmsColumns._ID + "=" + noteId
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
        sUriMatcher.addURI(Provider.PresetParmsColumns.AUTHORITY, "presetparms", PROGRAMMERS);
        sUriMatcher.addURI(Provider.PresetParmsColumns.AUTHORITY, "presetparms/#", PROGRAMMERS_ID);

        sprogrammersProjectionMap = new HashMap<String, String>();
        sprogrammersProjectionMap.put(Provider.PresetParmsColumns._ID, Provider.PresetParmsColumns._ID);
        sprogrammersProjectionMap.put(Provider.PresetParmsColumns.PRESET_ID, Provider.PresetParmsColumns.PRESET_ID);
        sprogrammersProjectionMap.put(Provider.PresetParmsColumns.NAME, Provider.PresetParmsColumns.NAME);
        sprogrammersProjectionMap.put(Provider.PresetParmsColumns.SIZE, Provider.PresetParmsColumns.SIZE);
        sprogrammersProjectionMap.put(Provider.PresetParmsColumns.AUTHER_URL, Provider.PresetParmsColumns.AUTHER_URL);
        sprogrammersProjectionMap.put(Provider.PresetParmsColumns.IMG_DATA_SIZE, Provider.PresetParmsColumns.IMG_DATA_SIZE);
        sprogrammersProjectionMap.put(Provider.PresetParmsColumns.CAMERA_PARM, Provider.PresetParmsColumns.CAMERA_PARM);
        sprogrammersProjectionMap.put(Provider.PresetParmsColumns.TIME_STAMP, Provider.PresetParmsColumns.TIME_STAMP);
        sprogrammersProjectionMap.put(Provider.PresetParmsColumns.LABEL, Provider.PresetParmsColumns.LABEL);
        sprogrammersProjectionMap.put(Provider.PresetParmsColumns.SHOW_TYPE, Provider.PresetParmsColumns.SHOW_TYPE);
        sprogrammersProjectionMap.put(Provider.PresetParmsColumns.POSITION,Provider.PresetParmsColumns.POSITION);
        sprogrammersProjectionMap.put(Provider.PresetParmsColumns.DOWNLOAD_COUNT,Provider.PresetParmsColumns.DOWNLOAD_COUNT);
        sprogrammersProjectionMap.put(Provider.PresetParmsColumns.IMAGE_HEIGHT,Provider.PresetParmsColumns.IMAGE_HEIGHT);
        sprogrammersProjectionMap.put(Provider.PresetParmsColumns.IMAGE_WIDTH,Provider.PresetParmsColumns.IMAGE_WIDTH);
        sprogrammersProjectionMap.put(Provider.PresetParmsColumns.CATEGORY_ID,Provider.PresetParmsColumns.CATEGORY_ID);
        sprogrammersProjectionMap.put(Provider.PresetParmsColumns.SYSTEM,Provider.PresetParmsColumns.SYSTEM);
        sprogrammersProjectionMap.put(Provider.PresetParmsColumns.TEXT_SRC, Provider.PresetParmsColumns.TEXT_SRC);
        sprogrammersProjectionMap.put(Provider.PresetParmsColumns.AUTHER, Provider.PresetParmsColumns.AUTHER);
        sprogrammersProjectionMap.put(Provider.PresetParmsColumns.AUTHER_ID, Provider.PresetParmsColumns.AUTHER_ID);
        sprogrammersProjectionMap.put(Provider.PresetParmsColumns.DESCRIBE, Provider.PresetParmsColumns.DESCRIBE);
        sprogrammersProjectionMap.put(Provider.PresetParmsColumns.EQUIPMENT, Provider.PresetParmsColumns.EQUIPMENT);
        sprogrammersProjectionMap.put(Provider.PresetParmsColumns.LIGHT_RATIO_DATA, Provider.PresetParmsColumns.LIGHT_RATIO_DATA);
        sprogrammersProjectionMap.put(Provider.PresetParmsColumns.PRAM_LISTS, Provider.PresetParmsColumns.PRAM_LISTS);
        sprogrammersProjectionMap.put(Provider.PresetParmsColumns.PART_PRAM_LISTS, Provider.PresetParmsColumns.PART_PRAM_LISTS);
        sprogrammersProjectionMap.put(Provider.PresetParmsColumns.COVER_ID, Provider.PresetParmsColumns.COVER_ID);

    }

}
