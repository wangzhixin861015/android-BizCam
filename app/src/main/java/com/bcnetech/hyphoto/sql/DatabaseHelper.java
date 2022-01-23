package com.bcnetech.hyphoto.sql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.bcnetech.bcnetechlibrary.util.LogUtil;


/**
 * 操作数据库
 *
 * @author jacp
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "jacp_demo.db";
    private static final int DATABASE_VERSION1 = 1;
    private static final int DATABASE_VERSION2 = 6;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION2);
        LogUtil.d("DatabaseHelper ");
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
//        db.execSQL("CREATE TABLE " + Provider.PersonColumns.TABLE_NAME + " ("
//                + Provider.PersonColumns._ID + " INTEGER PRIMARY KEY,"
//                + Provider.PersonColumns.NAME + " TEXT,"
//                + Provider.PersonColumns.AGE + " INTEGER"
//                + ");");
  LogUtil.d("DatabaseHelper onCreate");
        createPresetParmsTable(db);
        createLightRightTable(db);
        createImageDataTable(db);
        createDownloadInfoTable(db);
        createUploadPicTable(db);
    }


    private void createPresetParmsTable(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + Provider.PresetParmsColumns.TABLE_NAME + " ("
                + Provider.PresetParmsColumns._ID + " INTEGER PRIMARY KEY,"
                + Provider.PresetParmsColumns.PRESET_ID + " text, "
                + Provider.PresetParmsColumns.NAME + " text, "
                + Provider.PresetParmsColumns.SIZE + " text, "
                + Provider.PresetParmsColumns.AUTHER_URL + " text, "
                + Provider.PresetParmsColumns.IMG_DATA_SIZE + " text, "
                + Provider.PresetParmsColumns.CAMERA_PARM + " text, "
                + Provider.PresetParmsColumns.TIME_STAMP + " text, "
                + Provider.PresetParmsColumns.LABEL + " text, "
                + Provider.PresetParmsColumns.SHOW_TYPE + " text, "
                + Provider.PresetParmsColumns.POSITION + " INTEGER, "
                + Provider.PresetParmsColumns.DOWNLOAD_COUNT + " INTEGER, "
                + Provider.PresetParmsColumns.IMAGE_HEIGHT + " text, "
                + Provider.PresetParmsColumns.IMAGE_WIDTH + " text, "
                + Provider.PresetParmsColumns.CATEGORY_ID + " text, "
                + Provider.PresetParmsColumns.SYSTEM + " text, "
                + Provider.PresetParmsColumns.TEXT_SRC + " text, "
                + Provider.PresetParmsColumns.AUTHER + " text, "
                + Provider.PresetParmsColumns.AUTHER_ID + " text, "
                + Provider.PresetParmsColumns.DESCRIBE + " text, "
                + Provider.PresetParmsColumns.EQUIPMENT + " text, "
                + Provider.PresetParmsColumns.LIGHT_RATIO_DATA + " text, "
                + Provider.PresetParmsColumns.PART_PRAM_LISTS + " text, "
                + Provider.PresetParmsColumns.PRAM_LISTS + " text,"
                + Provider.PresetParmsColumns.COVER_ID + " text"
                + ");";
        db.execSQL(sql);
    }


    /**
     * 创建光比table
     *
     * @param db
     */
    private void createLightRightTable(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + Provider.LightRatioColums.TABLE_NAME + " ("
                + Provider.LightRatioColums._ID + " INTEGER PRIMARY KEY,"
                + Provider.LightRatioColums.LIGHT_NAME + " text, "
                + Provider.LightRatioColums.LIGHT_LEFT + " text, "
                + Provider.LightRatioColums.LIGHT_RIGHT + " text, "
                + Provider.LightRatioColums.LIGHT_BOTTOM + " text, "
                + Provider.LightRatioColums.LIGHT_BACKGROUD + " text, "
                + Provider.LightRatioColums.LIGHT_MOVE + " text, "
                + Provider.LightRatioColums.LIGHT_TOP + " text, "
                + Provider.LightRatioColums.VERSION + " text, "
                + Provider.LightRatioColums.LIGHT_LIGHT1 + " text, "
                + Provider.LightRatioColums.LIGHT_LIGHT2 + " text, "
                + Provider.LightRatioColums.LIGHT_LIGHT3 + " text, "
                + Provider.LightRatioColums.LIGHT_LIGHT4 + " text, "
                + Provider.LightRatioColums.LIGHT_LIGHT5 + " text, "
                + Provider.LightRatioColums.LIGHT_ID + " text, "
                + Provider.LightRatioColums.LIGHT_NUM + " text"
                + ");";
        db.execSQL(sql);
    }

    /**
     * 创建图片配置信息table
     *
     * @param db
     */
    private void createImageDataTable(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + Provider.ImageColums.TABLE_NAME + " ("
                + Provider.ImageColums._ID + " INTEGER PRIMARY KEY,"
                + Provider.ImageColums.LIGHT_LOCALURL + " text, "
                + Provider.ImageColums.LIGHT_SMALLLOCALURL + " text, "
                + Provider.ImageColums.LIGHT_TIME + " text, "
                + Provider.ImageColums.LIGHT_RATIO_DATA + " text, "
                + Provider.ImageColums.IMAGETOOL + " text, "
                + Provider.ImageColums.IMAGEPARMS + " text, "
                + Provider.ImageColums.IS_MATTING + " text, "
                + Provider.ImageColums.CURRENT_POSITION + " text, "
                + Provider.ImageColums.CAMERA_PARM + " text, "
                + Provider.ImageColums.RECODER_TIME + " text, "
                + Provider.ImageColums.SIZE + " text, "
                + Provider.ImageColums.VALUE1 + " text, "
                + Provider.ImageColums.VALUE2 + " text, "
                + Provider.ImageColums.VALUE3 + " text, "
                + Provider.ImageColums.VALUE4 + " text, "
                + Provider.ImageColums.VALUE5 + " text, "
                + Provider.ImageColums.VALUE6 + " text, "
                + Provider.ImageColums.VALUE7 + " text, "
                + Provider.ImageColums.VALUE8 + " text, "
                + Provider.ImageColums.VALUE9 + " text, "
                + Provider.ImageColums.VALUE10 + " text, "
                + Provider.ImageColums.PRESETPARMS + " text"
                + ");";
        db.execSQL(sql);
    }

    /**
     * 创建图片上传任务table
     *
     * @param db
     */
    private void createDownloadInfoTable(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + Provider.DownloadInfoColums.TABLE_NAME + " ("
                + Provider.DownloadInfoColums._ID + " INTEGER PRIMARY KEY,"
                + Provider.DownloadInfoColums.TYPE + " text, "
                + Provider.DownloadInfoColums.URL + " text, "
                + Provider.DownloadInfoColums.GET_PARMS + " text, "
                + Provider.DownloadInfoColums.POST_PARMS + " text, "
                + Provider.DownloadInfoColums.POST_FILE_PARMS + " text,"
                + Provider.DownloadInfoColums.LOCAL_URL + " text,"
                + Provider.DownloadInfoColums.UPLOAD_TIME + " LONG,"
                + Provider.DownloadInfoColums.FILE_TYPE + " text"
                + ");";
        db.execSQL(sql);
    }

    /**
     * 创建上传服务器图片table
     *
     * @param db
     */
    private void createUploadPicTable(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + Provider.UploadPicColums.TABLE_NAME + " ("
                + Provider.UploadPicColums._ID + " INTEGER PRIMARY KEY,"
                + Provider.UploadPicColums.IMAGEDATA + " text, "
                + Provider.UploadPicColums.LOCAL_URL + " text,"
                + Provider.UploadPicColums.VALUE1 + " text, "
                + Provider.UploadPicColums.VALUE2 + " text, "
                + Provider.UploadPicColums.UPLOAD_TIME + " LONG"
                + ");";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch (newVersion) {
            case DATABASE_VERSION1:
                upTable1(db);
                break;
            case DATABASE_VERSION2:
                upTable2(db);
                break;
        }
    }


    private final static String TEXT_TABLE="texttable";
    public void upTable1(SQLiteDatabase db) {
//        db.execSQL("DROP TABLE IF EXISTS " + Provider.PersonColumns.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Provider.PresetParmsColumns.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Provider.LightRatioColums.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Provider.ImageColums.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Provider.DownloadInfoColums.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Provider.UploadPicColums.TABLE_NAME);
    }


    /**
     * 更新需要更新的表数据
     * @param db
     */
    public void upTable2(SQLiteDatabase db){
        db.execSQL("alter table "+ Provider.PresetParmsColumns.TABLE_NAME +" rename to "+TEXT_TABLE);
        createPresetParmsTable(db);
        db.execSQL("insert into "+Provider.PresetParmsColumns.TABLE_NAME+" select *,\"\" from "+TEXT_TABLE);
        db.execSQL("drop table "+TEXT_TABLE);

    }


}