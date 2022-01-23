package com.example.imageproc;

public class Process {

    public static int JNIAPI_METHOD_VERSION = 0; /* 版本信息 */
    public static int JNIAPI_METHOD_INPAINT = 1; /* 修复 */
    public static int JNIAPI_METHOD_MATTING = 2; /* 抠图 */
    public static int JNIAPI_METHOD_ONEKEY = 3; /* 一键白底 */
    public static int JNIAPI_METHOD_WHITEBALANCE = 4; /* 自动白平衡 */
    public static int JNIAPI_METHOD_RECOMMEND = 5;
    public static int JNIAPI_METHOD_DECOLOR_PREV = 6;
    public static int JNIAPI_METHOD_DECOLOR_1 = 7;
    public static int JNIAPI_METHOD_DECOLOR_2 = 8;
    public static int JNIAPI_METHOD_DECOLOR_3 = 9;
    public static int JNIAPI_METHOD_DECOLOR_4 = 10;
    public static int JNIAPI_METHOD_DECOLOR_5 = 11;
    public static int JNIAPI_METHOD_INTELLIGENT_FILL_BACKGROUND_SRCPRYDOWN = 12;
    public static int JNIAPI_METHOD_AUTOPHOTOGRAPH = 13;
    public static int JNIAPI_METHOD_AUTOPHOTOGRAPH_PROCESS = 14;/* 智拍 */
    public static int JNIAPI_METHOD_FAKE3D = 15; /* fake3d *//* 全维构图 */
    public static int JNIAPI_METHOD_FAKE3D2 = 16; /*YUV2RGB,裁剪坐标*/
    //jin设置cobox版本
    public static int BC_C0MB0X_VERSI0N_C2 = 1;
    //jin设置状态
    public static int USAGE_INIT = 1;
    public static int USAGE_UINIT = 2;
    public static int USAGE_PROCESS = 4;
    public static int USAGE_SETPARAM = 8;
    public static int USAGE_GETPARAM = 10;
    //jni设置平台
    public static int BC_PLATEFORMINFO_ANDROID = 1;

    static {
        //System.loadLibrary("image-proc");
        System.loadLibrary("imgproc");
    }

    public static class AIResult {
        public static final int TARGET_STATUS_ERROR = 0;         /* 错误*/
        public static final int TARGET_STATUS_OK = 1;         /* 正常，可以拍摄 */
        public static final int TARGET_STATUS_AREA_BIG = 2;  /* 目标 过大 */
        public static final int TARGET_STATUS_AREA_SMALL = 3; /* 目标 过小 */
        public static final int TARGET_STATUS_AREA_LEFT = 4;  /* 目标偏左*/
        public static final int TARGET_STATUS_AREA_RIGHT = 5; /* 目标偏右 */
        public static final int TARGET_STATUS_AREA_TOP = 6;   /* 目标偏上 */
        public static final int TARGET_STATUS_AREA_BOTTOM = 7;/* 目标偏下 */
        public static final int TARGET_STATUS_SHIFT = 8;    /* 目标位置偏移 */
        public static final int TARGET_STATUS_FAILED_DETECT_TARGET = 9;    /* 无法检测目标 */
    }

    static public native int[] inpiant(int[] srcImg, int[] maskImg, int w, int h);

    static public native int[] autoWhiteBalance(int[] srcImg, int w, int h, int[] temperatureTint);

    static public native int[] lazySnapping(int[] srcImg, int[] maskImg, int w, int h, int[] maskView);

    static public native Object jniApiMethod(Object param);

    static public native Object byteJniApiMethod(Object param);
}
