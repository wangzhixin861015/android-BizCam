package com.bcnetech.hyphoto.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;

import com.bcnetech.bcnetchhttp.bean.data.DownloadInfoData;
import com.bcnetech.bcnetchhttp.bean.data.LightThan;
import com.bcnetech.bcnetchhttp.bean.data.ShareGlobalParms;
import com.bcnetech.bcnetchhttp.bean.data.SharePartParms;
import com.bcnetech.bcnetchhttp.bean.response.LoginedUser;
import com.bcnetech.bcnetchhttp.bean.response.Preinstail;
import com.bcnetech.bcnetchhttp.config.Flag;
import com.bcnetech.bcnetchhttp.config.UrlConstants;
import com.bcnetech.bcnetechlibrary.bean.CameraParm;
import com.bcnetech.bcnetechlibrary.util.JsonUtil;
import com.bcnetech.bcnetechlibrary.util.ThreadPoolUtil;
import com.bcnetech.bcnetechlibrary.util.TimeUtil;
import com.bcnetech.hyphoto.data.KeyValue;
import com.bcnetech.hyphoto.data.SqlControl.DownloadInfoSqlControl;
import com.bcnetech.hyphoto.data.UploadCloudData;
import com.bcnetech.hyphoto.imageinterface.BizImageMangage;
import com.bcnetech.hyphoto.sql.dao.ImageData;
import com.bcnetech.hyphoto.sql.dao.LightRatioData;
import com.bcnetech.hyphoto.sql.dao.PictureProcessingData;
import com.bcnetech.hyphoto.sql.dao.PresetParm;
import com.bcnetech.hyphoto.task.manage.UploadListHolder;
import com.bcnetech.hyphoto.utils.FileUtil;
import com.bcnetech.hyphoto.utils.ImageUtil;
import com.bcnetech.hyphoto.utils.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wenbin on 2017/1/16.
 */

public abstract class AddCloudReceiver extends BroadcastReceiver {
    private final static String ACTION_ON = "BizCam_app_AddCloudReceiver_On";
    private final static String CLOUD_DATA = "BizCam_app_AddCloudReceiver_DATA";

    @Override
    public void onReceive(Context context, final Intent intent) {
        String action = intent.getAction();
        if (action.equals(ACTION_ON)) {

            final List<UploadCloudData> list = UploadListHolder.getInstance().getUploadCloudData();

            ThreadPoolUtil.execute(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < list.size(); i++) {
                        list.get(i).setDownloadInfoData(newCloudDownLoadInfo(list.get(i)));
                    }
                    addCloud(list);
                }
            });

            UploadListHolder.getInstance().saveUploadCloudData(null);
        }
    }

    public abstract void addCloud(List list);

    public abstract void startUpload();


    //注册
    public void register(Context context) {
        IntentFilter filterStart = new IntentFilter(ACTION_ON);
        context.registerReceiver(this, filterStart);
    }

    //解绑
    public void unregister(Context context) {
        context.unregisterReceiver(this);
    }

    //通知
    public static void notifyModifyAddPic(Context context, List list) {
        Intent intent = new Intent(ACTION_ON);

        UploadListHolder.getInstance().saveUploadCloudData(list);
//        intent.putExtra(CLOUD_DATA, (Serializable) list);
        context.sendBroadcast(intent);

    }


    private DownloadInfoData newCloudDownLoadInfo(UploadCloudData uploadCloudData) {
        //预设参数
        PresetParm presetParm=EditPresetParm(uploadCloudData.getImageData());

        String jsonParms = JsonUtil.Object2Json(EditShareData(presetParm));

//        String path=uploadCloudData.getImageData().getSmallLocalUrl();



//        FileUpload.fileUpload(path.substring(7));

        DownloadInfoData downloadInfoData = new DownloadInfoData();
        if (uploadCloudData.getImageData().getType() == Flag.TYPE_PIC) {
            downloadInfoData.setFileType(Flag.TYPE_PIC);
            downloadInfoData.setUrl(UrlConstants.PICS);
            downloadInfoData.setLocalUrl(uploadCloudData.getImageData().getSmallLocalUrl());
        } else if(uploadCloudData.getImageData().getType() == Flag.TYPE_AI360){
            downloadInfoData.setFileType(Flag.TYPE_AI360);
            downloadInfoData.setUrl(UrlConstants.PICS);
            String url=Environment.getExternalStorageDirectory()+ Flag.BaseData + "/Panoramas/"+System.currentTimeMillis()+".zip";
            try {
                //生成zip文件
                FileUtil.ZipFolder(uploadCloudData.getImageData().getLocalUrl(), url);
            } catch (Exception e) {
                e.printStackTrace();
            }
            downloadInfoData.setLocalUrl("file://"+url);
        }else {
            downloadInfoData.setFileType(Flag.TYPE_VIDEO);
            downloadInfoData.setUrl(UrlConstants.VIDEOS);
            downloadInfoData.setLocalUrl("file://" + uploadCloudData.getImageData().getLocalUrl());
        }

        List<KeyValue> keyValuesPost = new ArrayList<>();
        keyValuesPost.add(new KeyValue("paramLightPen", uploadCloudData.getImageData().getLightRatioData() != null ? "1" : "0"));
        keyValuesPost.add(new KeyValue("paramGlobal", uploadCloudData.getImageData().getImageTools() != null && uploadCloudData.getImageData().getImageTools().size() != 0 ? "1" : "0"));
        keyValuesPost.add(new KeyValue("paramPreprocess", uploadCloudData.getImageData().getImageParts() != null ? "1" : "0"));
//        keyValuesPost.add(new KeyValue("catalogId", uploadCloudData.getCatalogId()));
        keyValuesPost.add(new KeyValue("scope", "1"));
        keyValuesPost.add(new KeyValue("bizcam", "1"));
        keyValuesPost.add(new KeyValue("paramPreinstall", uploadCloudData.getImageData().getPresetParms() != null ? "1" : "0"));
        keyValuesPost.add(new KeyValue("params", jsonParms));
        keyValuesPost.add(new KeyValue("deviceType", android.os.Build.MODEL));
        //keyValuesPost.add(new KeyValue("name", presetParm.getName()));
        keyValuesPost.add(new KeyValue("system", "android"));
        if(!StringUtil.isBlank(uploadCloudData.getImageData().getValue3())){
            keyValuesPost.add(new KeyValue("replaceId", uploadCloudData.getImageData().getValue3()));
        }

        if(uploadCloudData.getImageData().getLightRatioData() != null ){
            keyValuesPost.add(new KeyValue("coboxName", BizImageMangage.getCoboxName(String.valueOf(uploadCloudData.getImageData().getLightRatioData().getVersion()))));
            keyValuesPost.add(new KeyValue("coboxVersion", uploadCloudData.getImageData().getLightRatioData().getVersion()));
        }


        String postParms = JsonUtil.list2JsonSerial(keyValuesPost);


        List<KeyValue> keyValues = new ArrayList<>();
        if (uploadCloudData.getImageData().getType() == Flag.TYPE_PIC) {
            keyValues.add(new KeyValue("file", uploadCloudData.getImageData().getSmallLocalUrl()));
        } else {
            keyValues.add(new KeyValue("file", "file://" + uploadCloudData.getImageData().getLocalUrl()));
        }
        String jsonFile = JsonUtil.list2JsonSerial(keyValues);
        downloadInfoData.setType(DownloadInfoSqlControl.CLOUD_UPLOAD);
        downloadInfoData.setGetParms("");
        downloadInfoData.setPostParms(postParms);
        downloadInfoData.setPostFileParms(jsonFile);
        downloadInfoData.setUploadTime(TimeUtil.getBeiJingTimeGMT());

        return downloadInfoData;
    }

    private PresetParm EditPresetParm(ImageData imageparms){
        PresetParm parms = new PresetParm();
        String url = imageparms.getSmallLocalUrl();
        parms.setTextSrc(url);
        String devicename = getDeviceName();
        parms.setEquipment(devicename);
        parms.setTimeStamp(imageparms.getTimeStamp());

        LightRatioData data = imageparms.getLightRatioData();
        LoginedUser loginedUser = LoginedUser.getLoginedUser();
        String name = loginedUser.getNickname();
        parms.setAuther(name);
        parms.setLightRatioData(data);
        if(!StringUtil.isBlank(imageparms.getValue5())){
            parms.setName(imageparms.getValue5());
        }else {
            parms.setName("");
        }
        parms.setLabels(new ArrayList<String>());
        parms.setDescribe("");
        parms.setParmlists(imageparms.getImageTools());
        parms.setPartParmlists(imageparms.getImageParts());

        int[] imageWH = ImageUtil.decodeUriAsBitmap(imageparms.getSmallLocalUrl());

        parms.setImageWidth(imageWH[0] + "");
        parms.setImageHeight( imageWH[1] + "");

        parms.setCameraParm(imageparms.getCameraParm());
        return parms;
    }

    private Preinstail EditShareData(PresetParm parms) {
        long timestamp = parms.getTimeStamp();
        String equipment = parms.getEquipment();
        List<PictureProcessingData> mparms = parms.getParmlists();
        List<PictureProcessingData> mpartparms = parms.getPartParmlists();
        for (int i = 0; mparms != null && i < mparms.size(); i++) {
            mparms.get(i).setNum(mparms.get(i).getNum() - 100);
        }

        for (int i = 0;null!=mpartparms && i < mpartparms.size(); i++) {
            mpartparms.get(i).setNum(mpartparms.get(i).getNum() - 100);
        }
        Preinstail shareParmsData = new Preinstail();
//        shareParmsData.setDeviceType(equipment);

        shareParmsData.setDevice(equipment);
        shareParmsData.setImageDate(timestamp);
        shareParmsData.setName(parms.getName());
        shareParmsData.setAutherName(parms.getAuther());
        shareParmsData.setDes(parms.getDescribe());
        shareParmsData.setID(parms.getId());
        shareParmsData.setFileName(parms.getTextSrc());
        int w = Integer.valueOf(parms.getImageWidth());
        int h = Integer.valueOf(parms.getImageHeight());
        shareParmsData.setDataSize(w * h * 4 / 1024 / 1024 + " MB");
        shareParmsData.setSize(w + "x" + h + " px");

        shareParmsData.setLabel("");
        ArrayList<ShareGlobalParms> globallist = new ArrayList<ShareGlobalParms>();
        ArrayList<SharePartParms> partlist = new ArrayList<SharePartParms>();

        LightRatioData lightRatioData = parms.getLightRatioData();
        if(null!=lightRatioData){
            LightThan lightThan = new LightThan();
            lightThan.setLeftValue(lightRatioData.getLeftLight());
            lightThan.setMoveValue2Value(lightRatioData.getTopLight());
            lightThan.setAuxiliaryValue(lightRatioData.getMoveLight());
            lightThan.setRightValue(lightRatioData.getRightLight());
            lightThan.setBackValue(lightRatioData.getBackgroudLight());
            lightThan.setBottomValue(lightRatioData.getBottomLight());
            lightThan.setName(lightRatioData.getName());
            lightThan.setVersion(lightRatioData.getVersion());
            shareParmsData.setLightThan(lightThan);
        }
        shareParmsData.setSystem("android");
        CameraParm cameraParm = parms.getCameraParm();
        if (cameraParm != null) {
            shareParmsData.setCameraParm(cameraParm);
        }

        if (mparms != null) {
            for (int i = 0; i < mparms.size(); i++) {
                ShareGlobalParms shareGlobalParms = new ShareGlobalParms();
                shareGlobalParms.setType(toAnotherName(mparms.get(i).getType()));
                shareGlobalParms.setNum(mparms.get(i).getNum());
                globallist.add(shareGlobalParms);
            }
            shareParmsData.setGlobalArray(globallist);
        }
        if (mpartparms != null) {
            for (int i = 0; i < mpartparms.size(); i++) {
                SharePartParms sharePartParms = new SharePartParms();
                sharePartParms.setType(toAnotherName(mpartparms.get(i).getType()));
                sharePartParms.setImagePath(mpartparms.get(i).getImageUrl());
                partlist.add(sharePartParms);
            }
            shareParmsData.setPartArray(partlist);
        }
        return shareParmsData;
    }


    private int toAnotherName(int type) {
        int newtype = 500;
        switch (type) {
            case BizImageMangage.ATMOSPHERE:
                newtype = 1000;
                break;
            case BizImageMangage.ATMOSPHERE_OUT:
                newtype = 1001;
                break;
            case BizImageMangage.BACKGROUND_REPAIR:
                newtype = 1002;
                break;
            case BizImageMangage.PAINT_BRUSH:
                newtype = 1003;
                break;
            case BizImageMangage.ROTATE:
                newtype = 1004;
                break;
            case BizImageMangage.SRC:
                newtype = 1005;
                break;
            case BizImageMangage.PARMS:
                newtype = 1006;
                break;
            case BizImageMangage.CROP:
                newtype = 2007;
                break;
            case BizImageMangage.HIGHLIGHT:
                newtype = 0;
                break;
            case BizImageMangage.BRIGHTNESS:
                newtype = 1;
                break;
            case BizImageMangage.CONTRAST:
                newtype = 2;
                break;
            case BizImageMangage.SHADOW:
                newtype = 3;
                break;
            case BizImageMangage.NATURALSATURATION:
                newtype = 4;
                break;
            case BizImageMangage.WARMANDCOOLCOLORS:
                newtype = 5;
                break;
            case BizImageMangage.SATURATION:
                newtype = 6;
                break;
            case BizImageMangage.DEFINITION:
                newtype = 7;
                break;
            case BizImageMangage.SHARPEN:
                newtype = 8;
                break;
            case BizImageMangage.EXPOSURE:
                newtype = 9;
                break;
            case BizImageMangage.WHITE_BALANCE:
                newtype = 10;
                break;
            case BizImageMangage.WHITE_LEVELS:
                newtype = 11;
                break;
            case BizImageMangage.BLACK_LEVELS:
                newtype = 12;
                break;
        }

        return newtype;
    }

    /**
     * 获取系统名称
     *
     * @return
     */
    public String getDeviceName() {
        //TelephonyManager tm = (TelephonyManager)getContext().getSystemService(Context.TELEPHONY_SERVICE);
        String mtype = android.os.Build.MODEL; // 手机型号
//

        return mtype;
    }
}
