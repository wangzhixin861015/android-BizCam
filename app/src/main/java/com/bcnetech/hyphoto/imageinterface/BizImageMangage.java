package com.bcnetech.hyphoto.imageinterface;

import android.content.Context;
import android.graphics.Bitmap;

import com.bcnetech.bcnetechlibrary.util.ImageUtil;
import com.bcnetech.hyphoto.R;
import com.bcnetech.hyphoto.sql.dao.ImageData;
import com.bcnetech.hyphoto.sql.dao.PictureProcessingData;
import com.bcnetech.hyphoto.utils.StringUtil;
import com.example.imageproc.Process;
import com.example.imageproc.jni.ProcessInt;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import jp.co.cyberagent.android.gpuimage.GPUImageCameraFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageFilter;
import jp.co.cyberagent.android.gpuimage.gpumanage.GPUImageFilterTools;

/**
 * 图片处理
 * Created by wenbin on 16/8/28.
 */

public class BizImageMangage {
    public static final int INIT_DATA = 100;//参数初始值
    public static final int SRC = 9000;//原图
    public static final int SRC_PRESET_PARMS = 9001;//预设参数原图
    public static final int PARMS = 1000;//参数修改

    public static final int BRIGHTNESS = 1001;//亮度
    public static final int CONTRAST = 1002;//对比度
    public static final int HIGHLIGHT = 1003;//高光
    public static final int SHADOW = 1004;//阴影
    public static final int SATURATION = 1005;//饱和度
    public static final int NATURALSATURATION = 1006;//自然饱和度
    public static final int WARMANDCOOLCOLORS = 1007;//冷暖色调
    public static final int SHARPEN = 1008;//锐化
    public static final int DEFINITION = 1009;//清晰度
    public static final int EXPOSURE = 1010;//曝光
    public static final int GAUSSIAN_BLUR = 1011;//高斯模糊
    public static final int BOX_BLUR = 1012;//BOX模糊
    public static final int BILATERAL_BLUR = 1013;//bliateral模糊
    public static final int WHITE_LEVELS = 1014; //白色色阶
    public static final int BLACK_LEVELS = 1015; //黑色色阶
    public static final int WHITE_BALANCE = 1016; //白平衡
    public static final int WHITE_BALANCE_CUSTOM = 1017; //自定义白平衡


    public final static int PAINT_BRUSH = 2001;//画笔
    public final static int PART_PAINT_EXPOSURE = 20011;//画笔曝光
    public final static int PART_PAINT_BRIGHTNESS = 20012;//画笔亮度
    public final static int PART_PAINT_SATURATION = 20013;//画笔饱和度
    public final static int BACKGROUND_REPAIR = 2002;//背景修复
    public final static int ROTATE = 2003;//旋转
    public final static int ATMOSPHERE = 2004;//氛围
    public final static int ATMOSPHERE_IN = 20041;//内部增亮
    public final static int ATMOSPHERE_OUT = 20042;//外部增亮
    public final static int MATTING = 2005;//抠图
    public final static int MATTING_PAINT = 2006;//抠图画笔
    public final static int CROP = 2007;//剪切
    public final static int AUTOCOMPLATE = 20021;//自动修复
    public final static int AUTO_WHITE_BALANCE = 20022;//自动白平衡

    public final static int PRESET_PARMS = 3000;//预设参数

    private static BizImageMangage instance;

    private BizImageMangage() {
    }

    public static BizImageMangage getInstance() {
        if (instance == null) {
            instance = new BizImageMangage();
        }
        return instance;
    }

    /**
     * 根据当前历史记录获取需要的图片
     *
     * @param currentPosition 当前历史次序
     * @param imageData       数据源
     * @param isPartChang     是否覆盖部分修改
     * @param isParamChang    是否覆盖参数修改
     * @return
     */
    public String getUrlFromCurrentPostion(int currentPosition, ImageData imageData, boolean isPartChang, boolean isParamChang) {
        if (currentPosition < 0 || imageData == null) return "";
        if (currentPosition == 0) {
            if (isPartChang) {
                List list = new ArrayList();
                imageData.setImageParts(list);
            }
           /* if (isParamChang) {
                List list;
                if (imageData.getPresetParms() != null && imageData.getPresetParms().getParmlists() != null) {
                    list = imageData.getPresetParms().getParmlists();
                } else {
                    list = new ArrayList();
                }
                imageData.setImageTools(list);
            }*/
            return imageData.getLocalUrl();
        } else {
            if (imageData.getImageTools() != null && imageData.getImageTools().size() != 0) {
                if (isPartChang) {
                    List list = new ArrayList();
                    for (int i = 0; i <= currentPosition - 2; i++) {
                        list.add(imageData.getImageParts().get(i));
                    }
                    imageData.setImageParts(list);
                }

                if (currentPosition == 1) {
                    return imageData.getLocalUrl();
                } else {
                    return imageData.getImageParts().get(currentPosition - 2).getImageUrl();
                }
            } else {
                if (isPartChang) {
                    List list = new ArrayList();
                    for (int i = 0; i <= currentPosition - 1; i++) {
                        list.add(imageData.getImageParts().get(i));
                    }
                    imageData.setImageParts(list);
                }

                return imageData.getImageParts().get(currentPosition - 1).getImageUrl();
            }
        }
    }

    /**
     * 获取参数Filter
     *
     * @param type
     * @return
     */
    public GPUImageFilter getGPUFilterforType(Context context, int type) {
        GPUImageFilter filter = null;
        switch (type) {
            case BRIGHTNESS:
                filter = GPUImageFilterTools.createFilterForType(context, GPUImageFilterTools.FilterType.BRIGHTNESS);
                break;
            case EXPOSURE:
                filter = GPUImageFilterTools.createFilterForType(context, GPUImageFilterTools.FilterType.EXPOSURE);
                break;
            case CONTRAST:
                filter = GPUImageFilterTools.createFilterForType(context, GPUImageFilterTools.FilterType.CONTRAST);
                break;
            case SHARPEN:
                filter = GPUImageFilterTools.createFilterForType(context, GPUImageFilterTools.FilterType.SHARPEN);
                break;
            case SATURATION:
                filter = GPUImageFilterTools.createFilterForType(context, GPUImageFilterTools.FilterType.SATURATION);
                break;
            case DEFINITION:
                filter = GPUImageFilterTools.createFilterForType(context, GPUImageFilterTools.FilterType.DEFINITION);

                break;
            case NATURALSATURATION:
                filter = GPUImageFilterTools.createFilterForType(context, GPUImageFilterTools.FilterType.SATURATION);
                break;
            case WARMANDCOOLCOLORS:
                filter = GPUImageFilterTools.createFilterForType(context, GPUImageFilterTools.FilterType.WHITE_BALANCE);
                break;
            case SHADOW:
                filter = GPUImageFilterTools.createFilterForType(context, GPUImageFilterTools.FilterType.SHADOW_BCNETECH);

                break;
            case HIGHLIGHT:
                filter = GPUImageFilterTools.createFilterForType(context, GPUImageFilterTools.FilterType.HIGHLIGHT_BCNETECH);

                break;

            case PART_PAINT_EXPOSURE:
            case PART_PAINT_BRIGHTNESS:
                filter = GPUImageFilterTools.createFilterForType(context, GPUImageFilterTools.FilterType.PART_EXPOSURE);
                break;
            case PART_PAINT_SATURATION:
                filter = GPUImageFilterTools.createFilterForType(context, GPUImageFilterTools.FilterType.PART_SATURATION);
                break;

            case ATMOSPHERE:
                filter = GPUImageFilterTools.createFilterForType(context, GPUImageFilterTools.FilterType.ATMOS_PHERE);
                break;

            case GAUSSIAN_BLUR:
                filter = GPUImageFilterTools.createFilterForType(context, GPUImageFilterTools.FilterType.GAUSSIAN_BLUR);
                break;

            case BOX_BLUR:
                filter = GPUImageFilterTools.createFilterForType(context, GPUImageFilterTools.FilterType.BOX_BLUR);
                break;
            case BILATERAL_BLUR:
                filter = GPUImageFilterTools.createFilterForType(context, GPUImageFilterTools.FilterType.BILATERAL_BLUR);
                break;
            case WHITE_LEVELS:
                filter = GPUImageFilterTools.createFilterForType(context, GPUImageFilterTools.FilterType.LEVELS_FILTER_MAX);
                break;
            case BLACK_LEVELS:
                filter = GPUImageFilterTools.createFilterForType(context, GPUImageFilterTools.FilterType.LEVELS_FILTER_MIN);
                break;
            case WHITE_BALANCE:
                filter = GPUImageFilterTools.createFilterForType(context, GPUImageFilterTools.FilterType.WHITE_BALANCE_NEW);
                break;
        }
        return filter;
    }

    /**
     * 根据预设参数  获取List<GPUImageFilter>   为了创建GPUImageFilterGroup
     *
     * @param context
     * @param filters
     * @param list
     * @return
     */
    public List<GPUImageFilter> getPresetParms(Context context, List<GPUImageFilter> filters, List<PictureProcessingData> list) {
        if (filters == null) {
            filters = new ArrayList<>();
        }
        filters.clear();
        if (list == null) {
            return filters;
        }
        for (int i = 0; i < list.size(); i++) {
            GPUImageFilter currentFilter = getGPUFilterforType(context, list.get(i).getType());
            GPUImageFilterTools.FilterAdjuster mFilterAdjuster = new GPUImageFilterTools.FilterAdjuster(currentFilter);
            if (list.get(i).getType() == BizImageMangage.WHITE_BALANCE) {
                mFilterAdjuster.adjust(list.get(i).getNum(), list.get(i).getTintNum());
            } else {
                mFilterAdjuster.adjust(list.get(i).getNum());
            }
//            mFilterAdjuster.adjust(list.get(i).getNum());
            if (currentFilter != null) {
                filters.add(currentFilter);
            }
        }
        return filters;
    }

    /**
     * 根据预设参数  获取Camera特制  List<GPUImageFilter>   为了创建GPUImageFilterGroup
     *
     * @param context
     * @param filters
     * @param list
     * @return
     */

    public List<GPUImageFilter> getCameraPresetParms(Context context, List<GPUImageFilter> filters, List<PictureProcessingData> list) {
        if (filters == null) {
            filters = new ArrayList<>();
        }
        filters.clear();
        filters.add(new GPUImageCameraFilter());
        if (list == null) {
            return filters;
        }

        for (int i = 0; i < list.size(); i++) {
            GPUImageFilter currentFilter = getGPUFilterforType(context, list.get(i).getType());
            GPUImageFilterTools.FilterAdjuster mFilterAdjuster = new GPUImageFilterTools.FilterAdjuster(currentFilter);
            mFilterAdjuster.adjust(list.get(i).getNum());
            if (currentFilter != null) {
                filters.add(currentFilter);
            }
        }
        return filters;
    }

    /**
     * 获取某功能对应的显示原图
     *
     * @param imageData
     * @param type
     * @return
     */
    public String getPictureProcessingUrl(ImageData imageData, int type) {
        if (imageData.getImageParts() == null || imageData.getImageParts().size() == 0) {
            return imageData.getLocalUrl();
        } else if (imageData.getImageParts().get(imageData.getImageParts().size() - 1).getType() == type) {
            if (imageData.getImageParts().size() > 1) {
                return imageData.getImageParts().get(imageData.getImageParts().size() - 2).getImageUrl();
            } else {
                return imageData.getLocalUrl();
            }
        } else {
            return imageData.getImageParts().get(imageData.getImageParts().size() - 1).getImageUrl();
        }
    }

    /**
     * 获取某功能对应的显示原图
     *
     * @param imageData
     * @param postion
     * @return
     */
    public String getPictureProcessingNewUrl(ImageData imageData, int postion) {
        if (imageData.getImageParts() == null || imageData.getImageParts().size() == 0 || postion < 0) {
            return imageData.getLocalUrl();
        } else if (postion > imageData.getImageParts().size() - 1) {
            return imageData.getImageParts().get(imageData.getImageParts().size() - 1).getImageUrl();
        } else {
            return imageData.getImageParts().get(postion).getImageUrl();
        }
    }


    /**
     * 获取图片处理队列中的  某次处理
     *
     * @param processingDataList
     * @param type
     * @return
     */
    public int getPicProType(List<PictureProcessingData> processingDataList, int type) {
        if (processingDataList == null) {
            return -1;
        }

        for (int i = 0; i < processingDataList.size(); i++) {
            if (processingDataList.get(i).getType() == type) {
                return i;
            }
        }
        return -1;
    }


    /**
     * 根据类型创建GPUImageFilter
     *
     * @param context
     * @param type
     * @param progress
     * @return
     */
    public GPUImageFilter getFile(Context context, GPUImageFilterTools.FilterType type, int progress) {
        GPUImageFilter mFilter;
        GPUImageFilterTools.FilterAdjuster mFilterAdjuster;
        mFilter = GPUImageFilterTools.createFilterForType(context, type);
        mFilterAdjuster = new GPUImageFilterTools.FilterAdjuster(mFilter);
        mFilterAdjuster.adjust(progress);
        return mFilter;
    }

    /**
     * 图片处理方法
     *
     * @param bitmap 需要处理的图片
     * @param parm   图片处理的配置文件
     * @return 处理后的图片
     */
    public Bitmap ProcessingPic(Bitmap bitmap, PictureProcessingData parm) {
        if (bitmap == null || parm == null) {
            return null;
        }
        return ChoiceMethod(bitmap, parm.getType());
    }

    /**
     * 参数处理
     *
     * @param bitmap 需要处理的图片
     * @param num    数据
     * @param type   类型
     * @return 处理后的图片
     */
    public Bitmap ProcessingPic(Bitmap bitmap, int num, int type) {
        if (bitmap == null) {
            return null;
        }
        return ChoiceMethod(bitmap, type);
    }

    /**
     * 部分处理
     *
     * @param bitmap  需要处理的图片
     * @param maskBit 覆盖的mask图
     * @param type    部分处理的类型
     * @param rect    startx starty w,h
     * @return 处理后的图片
     */
    public Bitmap ProcessingPic(Bitmap bitmap, Bitmap maskBit, int type, int[] rect) {
        if (bitmap == null) {
            return null;
        }
        if (type == BACKGROUND_REPAIR) {
            return inPaintImg(bitmap, maskBit);
        }else if (type == AUTO_WHITE_BALANCE) {
            return autoWhiteBalance(bitmap);
        } else {
            return null;
        }


    }
    public List<Bitmap> ProcessingAutoRepair(Bitmap bitmap,int type) {
        if (bitmap == null) {
            return null;
        }
        return autoRepair(bitmap,type);
    }

    private Bitmap ChoiceMethod(Bitmap bitmap, int type) {
        switch (type) {
            case BRIGHTNESS:
                break;


        }
        return null;
    }

    private Bitmap ChoiceMethodPart(Bitmap src, Bitmap mask, int type, int[] rect) {
        switch (type) {

        }
        return null;
    }

    /**
     * 污点修复
     *
     * @return
     */
    private Bitmap inPaintImg(Bitmap img1, Bitmap img2) {




        ProcessInt jin=new ProcessInt();
        jin.api_method=Process.JNIAPI_METHOD_INPAINT;

        int w = img1.getWidth();
        int h = img1.getHeight();
        jin.srcbuf=new int[w * h];
        img1.getPixels(jin.srcbuf, 0, w, 0, 0, w, h);


        img2 = ImageUtil.resizeImage(img2, w, h);

        jin.inmask=getByte(img2);
        jin.img_width=w;
        jin.img_height=h;


        ProcessInt res= (ProcessInt) Process.jniApiMethod(jin);
        int[] inpaint =res.dstbuf;
        Bitmap bit_sobel = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        bit_sobel.setPixels(inpaint, 0, w, 0, 0, w, h);
        return bit_sobel;
    }

    /**
     * 自动修复
     *
     * @param img
     * @return
     */
    private List<Bitmap> autoRepair(Bitmap img,int type) {
        ProcessInt jin=new ProcessInt();
        jin.api_method=type;

        //Bitmap img= ImageUtil.resizeImage(changBitmap, 300, 300);

        int w = img.getWidth();
        int h = img.getHeight();
       // int[] pix = new int[w * h];

        jin.srcbuf=new int[w * h];
        img.getPixels( jin.srcbuf, 0, w, 0, 0, w, h);

        jin.img_width=w;
        jin.img_height=h;

        ProcessInt res = (ProcessInt) Process.jniApiMethod(jin);



        List<Bitmap> bitmaps=new ArrayList<>();

        if(type==6){
            for(int i=0;i<6;i++){
                Bitmap bit_sobel = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
                bit_sobel.setPixels(res.dstbuf, i*w*h, w, 0, 0, w, h);
                bitmaps.add(bit_sobel);
            }
        }else{
            Bitmap bit_sobel = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            bit_sobel.setPixels(res.dstbuf, 0, w, 0, 0, w, h);
            bitmaps.add(bit_sobel);
        }



        return bitmaps;
    }


    int[] temperatureTint;

    /**
     * 自动白平衡
     *
     * @param img
     * @return
     */
    private Bitmap autoWhiteBalance(Bitmap img) {
        ProcessInt jin=new ProcessInt();
        jin.api_method=Process.JNIAPI_METHOD_WHITEBALANCE;

        int w = img.getWidth();
        int h = img.getHeight();
        jin.srcbuf= new int[w * h];
        img.getPixels(jin.srcbuf, 0, w, 0, 0, w, h);
        jin.img_height=h;
        jin.img_width=w;


        ProcessInt res= (ProcessInt) Process.jniApiMethod(jin);
        int[] inpaint = res.dstbuf;
        temperatureTint=res.wbval;

        Bitmap bit_sobel = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        bit_sobel.setPixels(inpaint, 0, w, 0, 0, w, h);
        return bit_sobel;
    }

    public int[] getTemperatureTint() {
        return temperatureTint;
    }

    private int[] srcPix;

    /**
     * 懒抠图
     *
     * @param src
     * @param mask
     * @param w
     * @param h
     * @return
     */
    public Bitmap lazySnapping(int[] src, Bitmap mask, int w, int h) {


        ProcessInt jin=new ProcessInt();
        jin.api_method=Process.JNIAPI_METHOD_MATTING;
        jin.srcbuf=src;
        jin.img_width=w;
        jin.img_height=h;
        jin.inmask= getByte(mask);

        if (srcPix == null || srcPix.length != mask.getWidth() * mask.getHeight()) {
            srcPix = new int[w * h];
        }


        ProcessInt res= (ProcessInt) Process.jniApiMethod(jin);
        srcPix = res.dstbuf;

        Bitmap newMask = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        newMask.setPixels(srcPix, w*h, w, 0, 0, w, h);
        return newMask;
    }


    public  byte[] getByte(Bitmap bitmap){
        int bytes = bitmap.getByteCount();

        ByteBuffer buf = ByteBuffer.allocate(bytes);
        bitmap.copyPixelsToBuffer(buf);

        return buf.array();
    }

    public int[] getSrcPixBit() {

        return srcPix;
    }

    public void setSrcPix(int[] srcPix) {
        this.srcPix = srcPix;
    }

    public static int bytesToInt(byte[] b) {
        String s = new String(b);
        return Integer.parseInt(s);
    }

    /**
     * 亮度
     */
    private Bitmap Brightness(Bitmap bitmap) {
        return bitmap;
    }

    /**
     * 对比度
     */
    private Bitmap ContrastRatio(Bitmap bitmap) {
        return bitmap;
    }

    /**
     * 高光
     */
    private Bitmap HighLight(Bitmap bitmap) {
        return bitmap;
    }

    /**
     * 阴影
     */
    private Bitmap Shadow(Bitmap bitmap) {
        return bitmap;
    }


    /**
     * 饱和度
     */
    private Bitmap Saturation(Bitmap bitmap) {
        return bitmap;
    }

    /**
     * 自然饱和度
     */
    private Bitmap NaturalSaturation(Bitmap bitmap) {
        return bitmap;
    }

    /**
     * 冷暖色调
     */
    private Bitmap WarmAndCoolColors(Bitmap bitmap) {
        return bitmap;
    }

    /**
     * 锐化
     */
    private Bitmap Sharpening(Bitmap bitmap) {
        return bitmap;
    }


    /**
     * 清晰度
     */
    private Bitmap Definition(Bitmap bitmap) {
        return bitmap;
    }



    public static String getParamsName(int paramType,Context context){
        switch (paramType){
            case BizImageMangage.PARMS:
                return context.getString(R.string.tune_image);
            case BizImageMangage.PAINT_BRUSH:
                return context.getString(R.string.brush);
            case BizImageMangage.BACKGROUND_REPAIR:
                return context.getString(R.string.healing);
            case BizImageMangage.ROTATE:
                return context.getString(R.string.crop_rotate);

            case BizImageMangage.WHITE_BALANCE:
                return context.getString(R.string.white_balance);
            case BizImageMangage.SRC:
                return context.getString(R.string.original_drawing);
            case BizImageMangage.SRC_PRESET_PARMS:
                return context.getString(R.string.original_drawing);
            default:
                return "";


        }
    }

    public static String getCoboxName(String coboxVersion) {

        String name = "";
        if (StringUtil.isBlank(coboxVersion)) {
            return name;
        }

        int version = Integer.valueOf(coboxVersion);

        switch (version) {
            case 4096:
                name = "Cobox S1";
                break;
            case 8192:
                name = "Cobox S2";
                break;
            case 12288:
                //name = "Cobox S3";
                name = "COLINK";
                break;
            case 16384:
                name = "Cobox S4";
                break;
            case 20480:
                name = "Cobox S5";
                break;
            case 24576:
                name = "Cobox P1";
                break;
            case 28672:
                name = "Cobox P2";
                break;
            case 32768:
                name = "Cobox P3";
                break;
            case 36864:
                name = "Cobox P4";
                break;
            case 40960:
                name = "Cobox P5";
                break;
            default:
                name = "CBEDU";
        }
        return name;
    }

    public static String getName(String coboxVersion) {

        String name = "";
        if (StringUtil.isBlank(coboxVersion)) {
            return name;
        }

        int version = Integer.valueOf(coboxVersion);

        switch (version) {
            case 4096:
                name = "S1";
                break;
            case 8192:
                name = "S2";
                break;
            case 12288:
                name = "S3";
                break;
            case 16384:
                name = "S4";
                break;
            case 20480:
                name = "S5";
                break;
            case 24576:
                name = "P1";
                break;
            case 28672:
                name = "P2";
                break;
            case 32768:
                name = "P3";
                break;
            case 36864:
                name = "P4";
                break;
            case 40960:
                name = "P5";
                break;
            default:
                name = "CBEDU";
        }
        return name;
    }
}
