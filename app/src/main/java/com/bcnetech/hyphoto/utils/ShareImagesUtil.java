package com.bcnetech.hyphoto.utils;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;

import com.bcnetech.bcnetchhttp.config.UrlConstants;
import com.bcnetech.bcnetechlibrary.util.EventStatisticsUtil;
import com.bcnetech.bcnetechlibrary.util.ToastUtil;
import com.bcnetech.hyphoto.EventCommon;
import com.bcnetech.hyphoto.R;
import com.bcnetech.hyphoto.data.ShareData;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yhf on 2017/8/18.
 */
public class ShareImagesUtil {

    /**
     * 分享多图到QQ
     * @param activity
     * @param picPaths
     */
    public static void onShareQQ(Activity activity, List<ShareData> picPaths, boolean photo, String fileId){
        if(!checkPackage("com.tencent.mobileqq",activity)){
            ToastUtil.toast(activity.getResources().getString(R.string.package_unexist));
            return;
        }
        EventStatisticsUtil.event(activity, EventCommon.PERSONCENTER_SETTING_SHARE_QQ);

        if(!StringUtil.isBlank(fileId)){

            File file=new File(picPaths.get(0).getCover());

            UMImage thumb =  new UMImage(activity, file);

            StringBuffer stringBuffer =new StringBuffer();
            stringBuffer.append(UrlConstants.SHARE25DURL);
            stringBuffer.append(fileId);
            stringBuffer.append("&formatType=4");

            UMWeb web = new UMWeb(stringBuffer.toString());
            web.setTitle(picPaths.get(0).getTitle());//标题
            web.setThumb(thumb);  //缩略图
            web.setDescription("25d");//描述

            new ShareAction(activity)
                    .setPlatform(SHARE_MEDIA.QQ)//传入平台
                    .withMedia(web)
                    .share();
//            Intent intent = new Intent();
//            intent.setType("text/plain"); //纯文本
//            intent.setAction(Intent.ACTION_SEND);

//
//            ComponentName comp = new ComponentName("com.tencent.mobileqq",
//                    "com.tencent.mobileqq.activity.JumpActivity");
//            intent.setComponent(comp);
//            activity.startActivity(Intent.createChooser(intent, ""));
            return;
        }

        ArrayList<Uri> imageUris = shareList(picPaths,activity);


        if(imageUris.size() == 0)return;

        try {
            Intent intent = new Intent();
            ComponentName comp = new ComponentName("com.tencent.mobileqq",
                    "com.tencent.mobileqq.activity.JumpActivity");
            intent.setComponent(comp);
            if(photo){
                intent.setAction(Intent.ACTION_SEND_MULTIPLE);
                intent.setType("image/*");
                intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris);
            }else {
                intent.setAction(Intent.ACTION_SEND);
                intent.setType("video/*");
                intent.putExtra(Intent.EXTRA_STREAM, imageUris.get(0));
            }
            activity.startActivity(Intent.createChooser(intent, ""));
        }catch (Exception e){
            Intent intent = new Intent();
            intent.setPackage("com.tencent.mobileqq");
            if(photo){
                intent.setAction(Intent.ACTION_SEND_MULTIPLE);
                intent.setType("image/*");
                intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris);
            }else {
                intent.setAction(Intent.ACTION_SEND);
                intent.setType("video/*");
                intent.putExtra(Intent.EXTRA_STREAM, imageUris.get(0));
            }
            activity.startActivity(Intent.createChooser(intent, ""));
        }
    }

    /**
     * 分享多图到微信
     * @param activity
     * @param picPaths
     */
    public static void onShareWeChat(Activity activity,List<ShareData> picPaths,boolean photo,String fileId){
        if(!checkPackage("com.tencent.mm",activity)){
            ToastUtil.toast(activity.getResources().getString(R.string.package_unexist));
            return;
        }

        EventStatisticsUtil.event(activity, EventCommon.PERSONCENTER_SETTING_SHARE_WECHAT);

        if(!StringUtil.isBlank(fileId)){

            File file=new File(picPaths.get(0).getCover());

            UMImage thumb =  new UMImage(activity, file);

            StringBuffer stringBuffer =new StringBuffer();
            stringBuffer.append(UrlConstants.SHARE25DURL);
            stringBuffer.append(fileId);

            UMWeb web = new UMWeb(stringBuffer.toString());
            web.setTitle(picPaths.get(0).getTitle());//标题
            web.setThumb(thumb);  //缩略图
            web.setDescription("25d");//描述
            stringBuffer.append("&formatType=4");


            new ShareAction(activity)
                    .setPlatform(SHARE_MEDIA.WEIXIN)//传入平台
                    .withMedia(web)
                    .share();


//            Intent intent = new Intent();
//            intent.setType("text/plain"); //纯文本
//            intent.setAction(Intent.ACTION_SEND);
//            StringBuffer stringBuffer =new StringBuffer();
//            stringBuffer.append(UrlConstants.SHARE25DURL);
//            stringBuffer.append(fileId);
//            intent.putExtra(Intent.EXTRA_TEXT, stringBuffer.toString());
//
//            ComponentName comp = new ComponentName("com.tencent.mm",
//                    "com.tencent.mm.ui.tools.ShareImgUI");
//            intent.setComponent(comp);
//
//            activity.startActivity(Intent.createChooser(intent, ""));
            return;
        }
        ArrayList<Uri> imageUris = shareList(picPaths,activity);

        if(imageUris.size() == 0) return;


        try {
            Intent intent = new Intent();
            ComponentName comp = new ComponentName("com.tencent.mm",
                    "com.tencent.mm.ui.tools.ShareImgUI");
            intent.setComponent(comp);
            if(photo){
                intent.setAction(Intent.ACTION_SEND_MULTIPLE);
                intent.setType("image/jpeg");
                intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris);
            }else {
                intent.setAction(Intent.ACTION_SEND);
                intent.setType("video/*");
                intent.putExtra(Intent.EXTRA_STREAM, imageUris.get(0));

            }
            activity.startActivity(Intent.createChooser(intent, ""));
        }catch (Exception e){
            Intent intent = new Intent();
            intent.setPackage("com.tencent.mm");
            if(photo){
                intent.setAction(Intent.ACTION_SEND_MULTIPLE);
                intent.setType("image/*");
                intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris);
            }else {
                intent.setAction(Intent.ACTION_SEND);
                intent.setType("video/*");
                intent.putExtra(Intent.EXTRA_STREAM, imageUris.get(0));
            }
            activity.startActivity(Intent.createChooser(intent, ""));
        }


    }

    /**
     * 分享多图到朋友圈
     * @param activity
     * @param picPaths
     */
    public static void onShareWeChatFriend(Activity activity,List<ShareData> picPaths,boolean photo,String fileId){
        if(!checkPackage("com.tencent.mm",activity)){
            ToastUtil.toast(activity.getResources().getString(R.string.package_unexist));
            return;
        }
        EventStatisticsUtil.event(activity, EventCommon.PERSONCENTER_SETTING_SHARE_WECHAT_CIRCLE);

        if(!StringUtil.isBlank(fileId)){

            File file=new File(picPaths.get(0).getCover());

            UMImage thumb =  new UMImage(activity, file);

            StringBuffer stringBuffer =new StringBuffer();
            stringBuffer.append(UrlConstants.SHARE25DURL);
            stringBuffer.append(fileId);
            stringBuffer.append("&formatType=4");


            UMWeb web = new UMWeb(stringBuffer.toString());
            web.setTitle(picPaths.get(0).getTitle());//标题
            web.setThumb(thumb);  //缩略图
            web.setDescription("25d");//描述

            new ShareAction(activity)
                    .setPlatform(SHARE_MEDIA.WEIXIN_CIRCLE)//传入平台
                    .withMedia(web)
                    .share();

//            Intent intent = new Intent();
//            ComponentName comp = new ComponentName("com.tencent.mm",
//                    "com.tencent.mm.ui.tools.ShareToTimeLineUI");
//            intent.setComponent(comp);
//            intent.setAction(Intent.ACTION_SEND);
//            intent.setType("text/plain");
//
//            StringBuffer stringBuffer =new StringBuffer();
//            stringBuffer.append(UrlConstants.SHARE25DURL);
//            stringBuffer.append(fileId);
//
//            intent.putExtra(Intent.EXTRA_TEXT, stringBuffer.toString());
//            activity.startActivity(Intent.createChooser(intent, ""));

            return;
        }
        ArrayList<Uri> imageUris = shareList(picPaths,activity);

        if(imageUris.size() == 0) return;

        try {
            Intent intent = new Intent();
            ComponentName comp = new ComponentName("com.tencent.mm",
                    "com.tencent.mm.ui.tools.ShareToTimeLineUI");
            intent.setComponent(comp);
            intent.setAction(Intent.ACTION_SEND_MULTIPLE);
            intent.setType("image/*");

            intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris);
            activity.startActivity(Intent.createChooser(intent, ""));
        }catch (Exception e){
            Intent intent = new Intent();
            intent.setPackage("com.tencent.mm");
            intent.setAction(Intent.ACTION_SEND_MULTIPLE);
            intent.setType("image/*");

            intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris);
            activity.startActivity(Intent.createChooser(intent, ""));
        }
    }


    /**
     * 分享多图到新浪微博
     * @param activity
     * @param picPaths
     */
    public static void onShareSina(Activity activity,List<ShareData> picPaths,boolean photo,String fileId){

        if(!checkPackage("com.sina.weibo",activity)){
            ToastUtil.toast(activity.getResources().getString(R.string.package_unexist));
            return;
        }
        EventStatisticsUtil.event(activity, EventCommon.PERSONCENTER_SETTING_SHARE_SINA);

        if(!StringUtil.isBlank(fileId)){

            File file=new File(picPaths.get(0).getCover());

            UMImage thumb =  new UMImage(activity, file);

            StringBuffer stringBuffer =new StringBuffer();
            stringBuffer.append(UrlConstants.SHARE25DURL);
            stringBuffer.append(fileId);
            stringBuffer.append("&formatType=4");


            UMWeb web = new UMWeb(stringBuffer.toString());
            web.setTitle(picPaths.get(0).getTitle());//标题
            web.setThumb(thumb);  //缩略图
            web.setDescription("25d");//描述

            new ShareAction(activity)
                    .setPlatform(SHARE_MEDIA.SINA)//传入平台
                    .withMedia(web)
                    .share();

//            Intent intent = new Intent();
//            intent.setType("image/*"); //纯文本
//            intent.setAction(Intent.ACTION_SEND);
//            StringBuffer stringBuffer =new StringBuffer();
//            stringBuffer.append(UrlConstants.SHARE25DURL);
//            stringBuffer.append(fileId);
//            intent.putExtra(Intent.EXTRA_TEXT, stringBuffer.toString());
//
//            intent.setPackage("com.sina.weibo");
//
//            activity.startActivity(Intent.createChooser(intent, ""));
            return;
        }

        ArrayList<Uri> imageUris = shareList(picPaths,activity);

        if(imageUris.size() == 0) return;

        Intent intent = new Intent();

        intent.setPackage("com.sina.weibo");
        if(photo){
            intent.setAction(Intent.ACTION_SEND_MULTIPLE);
            intent.setType("image/*");
            intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris);
        }else {
            intent.setAction(Intent.ACTION_SEND);
            intent.setType("video/*");
            intent.putExtra(Intent.EXTRA_STREAM, imageUris.get(0));

        }
        activity.startActivity(Intent.createChooser(intent, ""));
    }

    /**
     * 分享多图到QQ空间
     * @param activity
     * @param picPaths
     */
    public static void onShareQQZone(Activity activity,List<ShareData> picPaths,boolean photo,String fileId){
        if(!checkPackage("com.qzone",activity)){
            ToastUtil.toast(activity.getResources().getString(R.string.package_unexist));
            return;
        }

        EventStatisticsUtil.event(activity, EventCommon.PERSONCENTER_SETTING_SHARE_QQ_ZONE);

        if(!StringUtil.isBlank(fileId)){


            File file=new File(picPaths.get(0).getCover());

            UMImage thumb =  new UMImage(activity, file);

            StringBuffer stringBuffer =new StringBuffer();
            stringBuffer.append(UrlConstants.SHARE25DURL);
            stringBuffer.append(fileId);
            stringBuffer.append("&formatType=4");


            UMWeb web = new UMWeb(stringBuffer.toString());
            web.setTitle(picPaths.get(0).getTitle());//标题
            web.setThumb(thumb);  //缩略图
            web.setDescription("25d");//描述


            new ShareAction(activity)
                    .setPlatform(SHARE_MEDIA.QZONE)//传入平台
                    .withMedia(web)
                    .share();


//            Intent intent = new Intent();
//            intent.setType("text/plain"); //纯文本
//            intent.setAction(Intent.ACTION_SEND);
//            StringBuffer stringBuffer =new StringBuffer();
//            stringBuffer.append(UrlConstants.SHARE25DURL);
//            stringBuffer.append(fileId);
//            intent.putExtra(Intent.EXTRA_TEXT, stringBuffer.toString());
//
//            intent.setPackage("com.qzone");
//
//            activity.startActivity(Intent.createChooser(intent, ""));
            return;
        }

        ArrayList<Uri> imageUris = shareList(picPaths,activity);

        if(imageUris.size() == 0) return;


        Intent intent = new Intent();
//        ComponentName comp = new ComponentName("com.qzone",
//                "com.qzonex.module.operation.ui.QZonePublishMoodActivity");
        intent.setPackage("com.qzone");
//        intent.setComponent(comp);
        if(photo){
            intent.setAction(Intent.ACTION_SEND_MULTIPLE);
            intent.setType("image/*");
            intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris);
        }else {
            intent.setAction(Intent.ACTION_SEND);
            intent.setType("video/*");
            intent.putExtra(Intent.EXTRA_STREAM, imageUris.get(0));

        }
        activity.startActivity(intent);
    }

    public static void onShareOther(Activity activity,List<ShareData> picPaths,boolean photo,String fileId){

        if(!StringUtil.isBlank(fileId)){
            Intent intent = new Intent();
            intent.setType("text/plain"); //纯文本
            intent.setAction(Intent.ACTION_SEND);
            StringBuffer stringBuffer =new StringBuffer();
            stringBuffer.append(UrlConstants.SHARE25DURL);
            stringBuffer.append(fileId);
            stringBuffer.append("&formatType=4");

            intent.putExtra(Intent.EXTRA_TEXT, stringBuffer.toString());
            activity.startActivity(Intent.createChooser(intent, ""));
            return;
        }

        ArrayList<Uri> imageUris = shareList(picPaths,activity);

        if(imageUris.size() == 0) return;

        Intent intent = new Intent();
        if(photo){
            if(imageUris.size() > 1){
                intent.setAction(Intent.ACTION_SEND_MULTIPLE);
                intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris);
            }else {
                intent.setAction(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_STREAM, imageUris.get(0));
            }
            intent.setType("image/*");
        }else {
            intent.setAction(Intent.ACTION_SEND);
            intent.setType("video/*");
            intent.putExtra(Intent.EXTRA_STREAM, imageUris.get(0));
        }
        activity.startActivity(Intent.createChooser(intent, ""));
    }

    /**
     * 分享多图到脸书
     * @param activity
     * @param picPaths
     */
    public static void onShareFacebook(Activity activity,List<ShareData> picPaths,boolean photo,String fileId){

        if(!checkPackage("com.facebook.katana",activity)){
            ToastUtil.toast(activity.getResources().getString(R.string.package_unexist));
            return;
        }
        EventStatisticsUtil.event(activity, EventCommon.PERSONCENTER_SETTING_SHARE_SINA);

        if(!StringUtil.isBlank(fileId)){
            Intent intent = new Intent();
            intent.setType("text/plain"); //纯文本
            intent.setAction(Intent.ACTION_SEND);
            StringBuffer stringBuffer =new StringBuffer();
            stringBuffer.append(UrlConstants.SHARE25DURL);
            stringBuffer.append(fileId);
            stringBuffer.append("&formatType=4");

            intent.putExtra(Intent.EXTRA_TEXT, stringBuffer.toString());

            intent.setPackage("com.facebook.katana");

            activity.startActivity(Intent.createChooser(intent, ""));
            return;
        }

        ArrayList<Uri> imageUris = shareList(picPaths,activity);
        if(imageUris.size() == 0) return;

        Intent intent = new Intent();

        intent.setPackage("com.facebook.katana");
        if(photo){
            intent.setAction(Intent.ACTION_SEND_MULTIPLE);
            intent.setType("image/*");
            intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris);
        }else {
            intent.setAction(Intent.ACTION_SEND);
            intent.setType("video/*");
            intent.putExtra(Intent.EXTRA_STREAM, imageUris.get(0));

        }


        activity.startActivity(Intent.createChooser(intent, ""));
    }

    /**
     * 分享多图到推特
     * @param activity
     * @param picPaths
     */
    public static void onShareTwitter(Activity activity,List<ShareData> picPaths,boolean photo,String fileId){
//        getShareApps(activity);
        if(!checkPackage("com.twitter.android",activity)){
            ToastUtil.toast(activity.getResources().getString(R.string.package_unexist));
            return;
        }

        if(!StringUtil.isBlank(fileId)){
            Intent intent = new Intent();
            intent.setType("text/plain"); //纯文本
            intent.setAction(Intent.ACTION_SEND);
            StringBuffer stringBuffer =new StringBuffer();
            stringBuffer.append(UrlConstants.SHARE25DURL);
            stringBuffer.append(fileId);
            stringBuffer.append("&formatType=4");

            intent.putExtra(Intent.EXTRA_TEXT, stringBuffer.toString());

            intent.setPackage("com.twitter.android");

            activity.startActivity(Intent.createChooser(intent, ""));
            return;
        }

        EventStatisticsUtil.event(activity, EventCommon.PERSONCENTER_SETTING_SHARE_SINA);

        ArrayList<Uri> imageUris = shareList(picPaths,activity);

        if(imageUris.size() == 0) return;

        Intent intent = new Intent();

        intent.setPackage("com.twitter.android");

        if(photo){
            intent.setAction(Intent.ACTION_SEND);
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_STREAM, imageUris.get(0));
        }else {
            intent.setAction(Intent.ACTION_SEND);
            intent.setType("video/*");
            intent.putExtra(Intent.EXTRA_STREAM, imageUris.get(0));

        }
        activity.startActivity(Intent.createChooser(intent, ""));
    }

    /**
     * 检测该包名所对应的应用是否存在
     * @param packageName
     * @return
     */
    private static boolean checkPackage(String packageName,Activity activity) {
        if(packageName == null || "".equals(packageName))
            return false;
        try{
            activity.getPackageManager().getApplicationInfo(packageName, PackageManager.GET_UNINSTALLED_PACKAGES);
            return true;
        }catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    private static ArrayList<Uri> shareList(List<ShareData> picPaths, Context context){
        ArrayList<Uri> imageUris = new ArrayList<Uri>();
        for(ShareData path : picPaths){
            File file = new File(path.getUrl());
            if(file.exists()){
                if(Build.VERSION.SDK_INT>=24) {
                    //  使用 FileProvider 会在某些 app 下不支持（在使用FileProvider 方式情况下QQ不能支持图片、视频分享，微信不支持视频分享）
                    Uri fileUri = FileProvider.getUriForFile(context, "com.bcnetech.hyphoto.FileProvider", file);
                    ContentResolver cR = context.getContentResolver();
                    if (fileUri != null && !TextUtils.isEmpty(fileUri.toString())) {
                        String fileType = cR.getType(fileUri);
                        // 使用 MediaStore 的 content:// 而不是自己 FileProvider 提供的uri，不然有些app无法适配
                        if (!TextUtils.isEmpty(fileType)){
                            if (fileType.contains("video/")){
                                fileUri = getVideoContentUri(context, file);
                            }else if (fileType.contains("image/")){
                                fileUri = getImageContentUri(context, file);
                            }else if (fileType.contains("audio/")){
                                fileUri = getAudioContentUri(context, file);
                            }
                        }
                    }
                    imageUris.add(fileUri);
                }else {
                    imageUris.add(Uri.fromFile(file));
                }
            }
        }
        return imageUris;
    }


    /**
     * Gets the content:// URI from the given corresponding path to a file
     *
     * @param context
     * @param imageFile
     * @return content Uri
     */
    public static Uri getImageContentUri(Context context, File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.Images.Media._ID }, MediaStore.Images.Media.DATA + "=? ",
                new String[] { filePath }, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }

    /**
     * Gets the content:// URI from the given corresponding path to a file
     *
     * @param context
     * @param videoFile
     * @return content Uri
     */
    public static Uri getVideoContentUri(Context context, File videoFile) {
        String filePath = videoFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.Video.Media._ID }, MediaStore.Video.Media.DATA + "=? ",
                new String[] { filePath }, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/video/media");
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            if (videoFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Video.Media.DATA, filePath);
                return context.getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }

    /**
     * Gets the content:// URI from the given corresponding path to a file
     *
     * @param context
     * @param audioFile
     * @return content Uri
     */
    public static Uri getAudioContentUri(Context context, File audioFile) {
        String filePath = audioFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.Audio.Media._ID }, MediaStore.Audio.Media.DATA + "=? ",
                new String[] { filePath }, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/audio/media");
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            if (audioFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Audio.Media.DATA, filePath);
                return context.getContentResolver().insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }





}
