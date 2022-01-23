package com.bcnetech.hyphoto.sql;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * 存放跟数据库有关的常量
 */
public class Provider {


    public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.bcnetech.bcnetechlibrary.contentprovider";

    public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.bcnetech.bcnetechlibrary.contentprovider";

//    /**
//     * 跟Person表相关的常量
//     */
//    public static final class PersonColumns implements BaseColumns {
//        //public static final String AUTHORITY = "com.jacp.provider.demo.person";
//        public static final String AUTHORITY = "com.bcnetech.provider.person";
//        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/persons");
//        public static final String TABLE_NAME = "person";
//        public static final String DEFAULT_SORT_ORDER = "_id desc";
//        public static final String NAME = "name";
//        public static final String AGE = "age";
//
//    }


    /**
     * 预设参数
     */
    public static final class PresetParmsColumns implements BaseColumns {
        public static final String AUTHORITY = "com.bcnetech.hyphoto.bcnetechlibrary.contentprovider.presetparm";
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/presetparms");
        public static final String TABLE_NAME = "presetparm";
        public static final String DEFAULT_SORT_ORDER = "_id desc";
        public static final String PRESET_ID = "_PresetId";
        public static final String NAME = "_Name";
        public static final String TEXT_SRC = "_TextSrc";
        public static final String TIME_STAMP = "_TimeStamp";
        public static final String SIZE="_Size";
        public static final String AUTHER_URL="_AutherUrl";
        public static final String IMG_DATA_SIZE="_ImgDataSize";
        public static final String CAMERA_PARM="_CameraParm";
        public static final String AUTHER = "_Auther";
        public static final String AUTHER_ID = "_AutherID";
        public static final String DESCRIBE = "_Describe";
        public static final String EQUIPMENT = "_Equipment";
        public static final String LABEL = "_Label";
        public static final String SHOW_TYPE = "_ShowType";
        public static final String POSITION= "_Position";
        public static final String DOWNLOAD_COUNT="_DownloadCount";
        public static final String IMAGE_HEIGHT="_ImageHeight";
        public static final String IMAGE_WIDTH="_ImageWidth";
        public static final String CATEGORY_ID="_CategoryId";
        public static final String SYSTEM= "_System";

        public static final String LIGHT_RATIO_DATA = "_LeftRatioData";
        public static final String PRAM_LISTS = "_PrimLists";
        public static final String PART_PRAM_LISTS = "_PartPrimLists";
        public static final String COVER_ID="_CoverId";
    }

    /**
     * 光比表
     */
    public static class LightRatioColums implements BaseColumns {
        public static final String AUTHORITY = "com.bcnetech.hyphoto.bcnetechlibrary.contentprovider.lightratio";
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/lightratios");
        public static final String TABLE_NAME = "lightratio";
        public static final String DEFAULT_SORT_ORDER = "_id desc";

        //定义数据表列
        public static final String LIGHT_NAME = "_Name";
        public static final String LIGHT_LEFT = "_Left";
        public static final String LIGHT_RIGHT = "_Right";
        public static final String LIGHT_BOTTOM = "_Bottom";
        public static final String LIGHT_BACKGROUD = "_BackGround";
        public static final String LIGHT_MOVE = "_Move";
        public static final String LIGHT_TOP = "_Top";
        public static final String VERSION= "_Version";
        public static final String LIGHT_LIGHT1 = "_Light1";
        public static final String LIGHT_LIGHT2 = "_Light2";
        public static final String LIGHT_LIGHT3 = "_Light3";
        public static final String LIGHT_LIGHT4 = "_Light4";
        public static final String LIGHT_LIGHT5 = "_Light5";
        public static final String LIGHT_ID = "_LightId";
        public static final String LIGHT_NUM = "_Num";
    }


    public static class ImageColums implements BaseColumns {

        public static final String AUTHORITY = "com.bcnetech.hyphoto.bcnetechlibrary.contentprovider.imageutil";
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/imageutils");
        public static final String TABLE_NAME = "imageutil";
        public static final String DEFAULT_SORT_ORDER = "_id desc";

        //定义数据表列
        public static final String LIGHT_LOCALURL = "_LocalUrl";
        public static final String LIGHT_SMALLLOCALURL = "_SmallLocalUrl";
        public static final String LIGHT_TIME = "_TimeStamp";
        public static final String LIGHT_RATIO_DATA = "_LeftRatioData";
        public static final String IMAGETOOL = "_ImageTool";
        public static final String IMAGEPARMS = "_ImageParts";
        public static final String PRESETPARMS = "_PresetParms";
        public static final String IS_MATTING = "_IsMatting";
        public static final String CURRENT_POSITION="_CurrentPosition";
        public static final String CAMERA_PARM="_CameraParm";
        public static final String RECODER_TIME="_RecoderTime";
        public static final String SIZE="_Size";
        public static final String VALUE1="_Value1";//数据类型
        public static final String VALUE2="_Value2"; //全局调整小图
        public static final String VALUE3="_Value3"; //上传fileId
        public static final String VALUE4="_Value4"; //是否已上传      (0)未上传 (1)正在上传 (2)已上传
        public static final String VALUE5="_Value5";
        public static final String VALUE6="_Value6";
        public static final String VALUE7="_Value7";
        public static final String VALUE8="_Value8";
        public static final String VALUE9="_Value9";
        public static final String VALUE10="_Value10";


    }


    /**
     * 下载上传table
     */
    public static class DownloadInfoColums implements BaseColumns {

        public static final String AUTHORITY = "com.bcnetech.hyphoto.bcnetechlibrary.contentprovider.downloadmanage";
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/downloadmanages");
        public static final String TABLE_NAME = "downloadmanage";
        public static final String DEFAULT_SORT_ORDER = "_id desc";

        //定义数据表列

        public static final String TYPE = "_Type";
        public static final String URL = "_Url";
        public static final String GET_PARMS = "_GetParms";
        public static final String POST_PARMS = "_PostParms";
        public static final String POST_FILE_PARMS = "_PostFileParms";
        public static final String UPLOAD_TIME ="_UploadTime";
        public static final String LOCAL_URL ="_LocalUrl";
        public static final String FILE_TYPE ="_FileType";
    }

    /**
     * 上传图片表
     */
    public static class UploadPicColums implements BaseColumns {

        public static final String AUTHORITY = "com.bcnetech.hyphoto.bcnetechlibrary.contentprovider.uploadmanage";
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/uploadmanages");
        public static final String TABLE_NAME = "upmanage";
        public static final String DEFAULT_SORT_ORDER = "_id desc";

        //定义数据表列
        public static final String IMAGEDATA = "_ImageData";
        public static final String UPLOAD_TIME ="_UploadTime";
        public static final String LOCAL_URL ="_LocalUrl";
        public static final String VALUE1="_Value1";//数据类型
        public static final String VALUE2="_Value2";

    }

}
