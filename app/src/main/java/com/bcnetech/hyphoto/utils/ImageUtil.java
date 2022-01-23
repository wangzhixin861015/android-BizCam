package com.bcnetech.hyphoto.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Size;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;

import com.bcnetech.bcnetchhttp.config.Flag;
import com.bcnetech.bcnetechlibrary.util.StringUtil;
import com.bcnetech.hyphoto.R;
import com.bcnetech.hyphoto.sql.dao.ImageData;
import com.bcnetech.bluetoothlibarary.bluetoothagreement.CommendManage;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.squareup.picasso.Picasso;

/**
 * Created by wb on 2016/5/3.
 */
public class ImageUtil {
    public static final String IMAGE_UNSPECIFIED = "image/*";//任意图片类型
    public static final int NONE = 0;
    public static final int PHOTOGRAPH = 1;// 拍照
    public static final int PHOTOZOOM = 2; // 缩放
    public static final int PHOTORESOULT = 3;// 结果
    public static final int PICTURE_HEIGHT = 500;
    public static final int PICTURE_WIDTH = 500;


    public static void CacheImage(String url, View view, int loading, int load_error) {
    }

    /***
     * 网络地址 转换为本地所需要存放的地址
     *
     * @param url
     * @return
     */
    public static String getPicLocalUrl(String url) {
        if (StringUtil.isBlank(url)) {
            return "";
        }
        return url.replaceAll("http://", "").replaceAll("/", "")
                .replaceAll("\\.", "");
    }


    public static void EBizImageLoad(String uri, ImageView imageView, DisplayImageOptions options,
                                     ImageLoadingListener listener, ImageLoadingProgressListener progressListener) {
        ImageLoader.getInstance().displayImage(uri, imageView);

    }

    public static void EBizImageLoad(ImageView imageView, String url) {
        // ImageLoader.getInstance().displayImage(url, imageView);
        Picasso.get().load(url).placeholder(R.color.shape_grey_color).into(imageView);
    }

    public static void EBizImageLoad(View imageView, String url, ImageLoadingListener imageLoadingListener) {
        if (imageView instanceof ImageView) {
            ImageLoader.getInstance().displayImage(url, (ImageView) imageView, imageLoadingListener);
        }
    }

    public static void EBizImageLoad(ImageView imageView, String uri, int w, int h, ImageLoadingListener listener) {
        ImageSize imageSize = new ImageSize(w, h);
        ImageLoader.getInstance().displayImage(uri, new ImageViewAware(imageView), (DisplayImageOptions) null, imageSize, (ImageLoadingListener) listener, (ImageLoadingProgressListener) null);
    }

    public static void EBizImageLoad(String url, ImageLoadingListener listener) {
        ImageLoader.getInstance().loadImage(url, listener);

    }

    public static void EBizImageLoad(String url, int w, int h, ImageLoadingListener listener) {
        ImageSize imageSize = new ImageSize(w, h);
        ImageLoader.getInstance().loadImage(url, imageSize, listener);
    }

    public static int[] decodeUriAsBitmap(String uri) {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();

            /**
             * 最关键在此，把options.inJustDecodeBounds = true;
             * 这里再decodeFile()，返回的bitmap为空，但此时调用options.outHeight时，已经包含了图片的高了
             */
            options.inJustDecodeBounds = true;
            Bitmap bitmap = BitmapFactory.decodeFile(uri.substring(7), options); // 此时返回的bitmap为null
            /**
             *options.outHeight为原始图片的高
             */
            return new int[]{options.outWidth, options.outHeight};
        } catch (Exception e) {
            e.printStackTrace();
            return new int[]{0, 0};
        }
    }

    /***
     * 加载网络图片
     *
     * @param imageView 加载图片放至的位置
     * @param url       图片地址
     * @param resid     加载失败默认图片
     */
    public static void EBizImageLoad(ImageView imageView, String url, int resid) {
        DisplayImageOptions options = getDicplay(resid);
        ImageLoader.getInstance().displayImage(url, imageView, options);

    }

    public static void EBizImageLoad(ImageView imageView, String url, int w, int h, int parmsw, int parmsh) {

    }

    /***
     * 加载网络图片
     *
     * @param imageView 加载图片放至的位置
     * @param url       图片地址
     * @param w         图片缩放的宽
     * @param h         图片缩放的高
     */
    public static void EBizImageLoad(ImageView imageView, String url, int w, int h) {
        ImageSize imageSize = new ImageSize(w, h);
        ImageLoader.getInstance().displayImage(url, imageView, imageSize);
    }

    private static DisplayImageOptions optionsNotChche = null;

    /**
     * 不进行 缓存读取本地图片
     *
     * @param path
     * @param image
     */
    public static void setImageNotChache(String path, ImageView image) {
        if (optionsNotChche == null) {
            optionsNotChche = new DisplayImageOptions.Builder().cacheInMemory(true)
                    .cacheOnDisk(true)
                    .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                    .bitmapConfig(Bitmap.Config.ARGB_8888)
                    .showImageOnLoading(Flag.DEFAULT_AVT)
                    .showImageForEmptyUri(Flag.DEFAULT_AVT)
                    .cacheInMemory(true)
                    .cacheOnDisk(false).considerExifParams(true)
                    .showImageOnFail(Flag.DEFAULT_AVT).build();
        }
        ImageLoader.getInstance().displayImage(path, image, optionsNotChche);
    }


    //使用Bitmap加Matrix来缩放
    public static Bitmap resizeImage(Bitmap bitmap, int w, int h,int orientation) {
        Bitmap BitmapOrg = bitmap;
        int width = BitmapOrg.getWidth();
        int height = BitmapOrg.getHeight();
        int newWidth = w;
        int newHeight = h;

        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        Matrix matrix = new Matrix();
        matrix.postRotate(orientation);
        matrix.postScale(scaleWidth, scaleHeight);
        // if you want to rotate the Bitmap
        // matrix.postRotate(45);
        Bitmap resizedBitmap = Bitmap.createBitmap(BitmapOrg, 0, 0, width,
                height, matrix, true);
        return resizedBitmap;
    }

    //使用Bitmap加Matrix来缩放
    public static Bitmap resizeImage(Bitmap bitmap,int orientation) {
        Bitmap BitmapOrg = bitmap;

        Matrix matrix = new Matrix();
        matrix.postRotate(orientation);
        // if you want to rotate the Bitmap
        // matrix.postRotate(45);
        Bitmap resizedBitmap = Bitmap.createBitmap(BitmapOrg, 0, 0, bitmap.getWidth(),
                bitmap.getHeight(), matrix, true);
        return resizedBitmap;
    }


    /**
     * 因为目前我们只有一套资源文件，全都放在hdpi下面，这样如果是遇到高密度手机， 系统会按照
     * scale = (float) targetDensity / density 把图片放到几倍，这样会使得在高密度手机上经常会发生OOM。
     * <p>
     * 这个方法用来解决在如果密度大于hdpi（240）的手机上，decode资源文件被放大scale，内容浪费的问题。
     *
     * @param resources
     * @param id
     * @return
     */
    public static Bitmap decodeResource(Resources resources, int id) {

        int densityDpi = resources.getDisplayMetrics().densityDpi;
        Bitmap bitmap;
        TypedValue value = new TypedValue();
        resources.openRawResource(id, value);
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inPreferredConfig = Bitmap.Config.ALPHA_8;
        if (densityDpi > DisplayMetrics.DENSITY_HIGH) {
            opts.inTargetDensity = value.density;
            bitmap = BitmapFactory.decodeResource(resources, id, opts);
        } else {
            bitmap = BitmapFactory.decodeResource(resources, id);
        }

        return bitmap;
    }

    /**
     * 定义显示格式
     *
     * @param resId
     * @return
     */
    private static DisplayImageOptions getDicplay(int resId) {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(true).cacheOnDisk(true).considerExifParams(true)
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                .bitmapConfig(Bitmap.Config.ARGB_8888).showImageOnLoading(resId)
                .showImageForEmptyUri(resId).showImageOnFail(resId).build();

        return options;
    }

    /***
     * 高斯模糊
     *
     * @param sentBitmap       原图
     * @param radius           权重
     * @param canReuseInBitmap 是否可重用
     * @return
     */
    public static Bitmap doBlur(Bitmap sentBitmap, int radius,
                                boolean canReuseInBitmap) {
        Bitmap bitmap;
        if (canReuseInBitmap) {
            bitmap = sentBitmap;
        } else {
            bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);
        }

        if (radius < 1) {
            return (null);
        }

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        int[] pix = new int[w * h];
        bitmap.getPixels(pix, 0, w, 0, 0, w, h);

        int wm = w - 1;
        int hm = h - 1;
        int wh = w * h;
        int div = radius + radius + 1;

        int r[] = new int[wh];
        int g[] = new int[wh];
        int b[] = new int[wh];
        int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
        int vmin[] = new int[Math.max(w, h)];

        int divsum = (div + 1) >> 1;
        divsum *= divsum;
        int dv[] = new int[256 * divsum];
        for (i = 0; i < 256 * divsum; i++) {
            dv[i] = (i / divsum);
        }

        yw = yi = 0;

        int[][] stack = new int[div][3];
        int stackpointer;
        int stackstart;
        int[] sir;
        int rbs;
        int r1 = radius + 1;
        int routsum, goutsum, boutsum;
        int rinsum, ginsum, binsum;

        for (y = 0; y < h; y++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            for (i = -radius; i <= radius; i++) {
                p = pix[yi + Math.min(wm, Math.max(i, 0))];
                sir = stack[i + radius];
                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);
                rbs = r1 - Math.abs(i);
                rsum += sir[0] * rbs;
                gsum += sir[1] * rbs;
                bsum += sir[2] * rbs;
                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
            }
            stackpointer = radius;

            for (x = 0; x < w; x++) {

                r[yi] = dv[rsum];
                g[yi] = dv[gsum];
                b[yi] = dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (y == 0) {
                    vmin[x] = Math.min(x + radius + 1, wm);
                }
                p = pix[yw + vmin[x]];

                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[(stackpointer) % div];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi++;
            }
            yw += w;
        }
        for (x = 0; x < w; x++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            yp = -radius * w;
            for (i = -radius; i <= radius; i++) {
                yi = Math.max(0, yp) + x;

                sir = stack[i + radius];

                sir[0] = r[yi];
                sir[1] = g[yi];
                sir[2] = b[yi];

                rbs = r1 - Math.abs(i);

                rsum += r[yi] * rbs;
                gsum += g[yi] * rbs;
                bsum += b[yi] * rbs;

                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }

                if (i < hm) {
                    yp += w;
                }
            }
            yi = x;
            stackpointer = radius;
            for (y = 0; y < h; y++) {
                // Preserve alpha channel: ( 0xff000000 & pix[yi] )
                pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16)
                        | (dv[gsum] << 8) | dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (x == 0) {
                    vmin[y] = Math.min(y + r1, hm) * w;
                }
                p = x + vmin[y];

                sir[0] = r[p];
                sir[1] = g[p];
                sir[2] = b[p];

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[stackpointer];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi += w;
            }
        }

        bitmap.setPixels(pix, 0, w, 0, 0, w, h);

        return (bitmap);
    }

    // dp转ps
    public static int Dp2Px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    /**
     * 从系统相册中选取照片上传
     */
    public static void selectPictureFromAlbum(Activity activity) {
        // 调用系统的相册
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                IMAGE_UNSPECIFIED);

        // 调用剪切功能
        activity.startActivityForResult(intent, PHOTOZOOM);
    }

    /**
     * @param filepath  图片路径
     * @param reqWidth  所需图片压缩尺寸最小宽度
     * @param reqHeight 所需图片压缩尺寸最小高度
     * @return
     */
    public static Bitmap decodeSampledBitmapFromFile(String filepath, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filepath, options);

        options.inSampleSize = calculateInSampleSize(options, reqWidth,
                reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filepath, options);
    }

    public static Bitmap newDecodeSampledBitmapFromFile(String filepath, Bitmap bitmap, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filepath, options);

        int orientation=BitmapUtils.getOrientation(filepath);

        Size size = ImageUtil.newCalculateInSampleSize(options,orientation);
        bitmap = ImageUtil.resizeImage(bitmap,  (size.getWidth()),(size.getHeight()),0);

        return bitmap;
    }

    /**
     * 计算压缩比例值(改进版 by touch_ping)
     * <p>
     * 原版2>4>8...倍压缩
     * 当前2>3>4...倍压缩
     *
     * @param options   解析图片的配置信息
     * @param reqWidth  所需图片压缩尺寸最小宽度O
     * @param reqHeight 所需图片压缩尺寸最小高度
     * @return
     */
    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {

        final int picheight = options.outHeight;
        final int picwidth = options.outWidth;

        int targetheight = picheight;
        int targetwidth = picwidth;
        int inSampleSize = 1;

        if (targetheight > reqHeight || targetwidth > reqWidth) {
            while (targetheight >= reqHeight
                    && targetwidth >= reqWidth) {
                inSampleSize += 1;
                targetheight = picheight / inSampleSize;
                targetwidth = picwidth / inSampleSize;
            }
        }

        Log.d("===", "最终压缩比例:" + inSampleSize + "倍");
        Log.i("===", "新尺寸:" + targetwidth + "*" + targetheight);
        return inSampleSize;
    }

    public static Size newCalculateInSampleSize(BitmapFactory.Options options,int orientation) {


        float width ;
        float height;
        if(orientation==90||orientation==270){
            width = options.outHeight;
            height = options.outWidth;

            if (width > height) {
                width = BitmapUtils.MAXLENGTH;
                height = BitmapUtils.MAXLENGTH * options.outWidth / options.outHeight;
            } else {
                height = BitmapUtils.MAXLENGTH;
                width = BitmapUtils.MAXLENGTH * options.outHeight / options.outWidth;
            }
            return new Size((int)width,(int)height);

        }else {
            width = options.outWidth;
            height = options.outHeight;

            if (width > height) {
                width = BitmapUtils.MAXLENGTH;
                height = BitmapUtils.MAXLENGTH * options.outHeight / options.outWidth;
            } else {
                height = BitmapUtils.MAXLENGTH;
                width = BitmapUtils.MAXLENGTH * options.outWidth / options.outHeight;
            }

            return new Size((int)width,(int)height);
        }

       /* float targetheight = picheight;
        float targetwidth = picwidth;
        float inSampleSize = 1;

        if (targetheight > reqHeight || targetwidth > reqWidth) {
            while (targetheight > reqHeight
            ||targetwidth > reqWidth) {
                inSampleSize += 0.1;
                targetheight = picheight / inSampleSize;
                targetwidth = picwidth / inSampleSize;
            }
        }

        Log.d("===", "最终压缩比例:" + inSampleSize + "倍");
        Log.i("===", "新尺寸:" + targetwidth + "*" + targetheight);
        return inSampleSize;*/
    }


    public static boolean isConnectionBox(ImageData imageData) {
        boolean isConnectionBox = false;
        if (imageData != null && imageData.getLightRatioData() != null) {
            if (imageData.getLightRatioData().getVersion() != null && !imageData.getLightRatioData().getVersion().equals(CommendManage.VERSION_BOX + "")) {
                if (imageData.getLightRatioData().getLeftLight() != -1) {
                    isConnectionBox = true;
                }
            }
        }
        return isConnectionBox;
    }

    public static boolean isConnectionBoxAndMH(ImageData imageData) {
        boolean isConnectionBox = false;
        if (imageData != null && imageData.getLightRatioData() != null) {
            if (imageData.getLightRatioData().getVersion() != null) {
                if (imageData.getLightRatioData().getLeftLight() != -1) {
                    isConnectionBox = true;
                }
            }
        }
        return isConnectionBox;
    }
}
