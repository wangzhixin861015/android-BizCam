package com.bcnetech.hyphoto.model;

import android.app.Activity;
import android.text.TextUtils;

import com.bcnetech.bcnetchhttp.RetrofitFactory;
import com.bcnetech.bcnetchhttp.base.BaseResult;
import com.bcnetech.bcnetchhttp.bean.data.LightThan;
import com.bcnetech.bcnetchhttp.bean.data.ShareGlobalParms;
import com.bcnetech.bcnetchhttp.bean.data.SharePartParms;
import com.bcnetech.bcnetchhttp.bean.request.ParamsListBody;
import com.bcnetech.bcnetchhttp.bean.response.LoginedUser;
import com.bcnetech.bcnetchhttp.bean.response.MarketParamsIndexListData;
import com.bcnetech.bcnetchhttp.bean.response.MarketParamsListData;
import com.bcnetech.bcnetchhttp.bean.response.Preinstail;
import com.bcnetech.bcnetechlibrary.util.ContentUtil;
import com.bcnetech.bcnetechlibrary.util.ToastUtil;
import com.bcnetech.hyphoto.imageinterface.BizImageMangage;
import com.bcnetech.hyphoto.sql.dao.LightRatioData;
import com.bcnetech.hyphoto.sql.dao.PictureProcessingData;
import com.bcnetech.hyphoto.sql.dao.PresetParm;
import com.bcnetech.hyphoto.task.MBaseObserver;
import com.bcnetech.hyphoto.utils.BitmapUtils;
import com.bcnetech.hyphoto.utils.ImageUtil;
import com.bcnetech.hyphoto.utils.StringUtil;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by a1234 on 17/2/28.
 */

public class MarketModel {
    private MarketModelInter marketModelInter;

    public PresetParm transferImageData(Preinstail preinstail) {
        PresetParm presetParm = new PresetParm();

        List<ShareGlobalParms> globalArrays = preinstail.getGlobalArray();
        List<SharePartParms> partArrrays = preinstail.getPartArray();
        LightThan lightThan = preinstail.getLightThan();
        List<PictureProcessingData> parmlist = new ArrayList<>();
        List<PictureProcessingData> partlist = new ArrayList<>();
        PictureProcessingData pictureProcessingData;
        if (globalArrays != null && globalArrays.size() != 0) {
            for (int i = 0; i < globalArrays.size(); i++) {
                if(globalArrays.get(i).getType()== BizImageMangage.WHITE_BALANCE){
                    pictureProcessingData = new PictureProcessingData();
                    pictureProcessingData.setType(intoImageDataTools(globalArrays.get(i).getType()));
                    pictureProcessingData.setNum(globalArrays.get(i).getNum()+100);
                    pictureProcessingData.setTintNum(globalArrays.get(i).getTint()+100);
                    parmlist.add(pictureProcessingData);
                }else {
                    pictureProcessingData = new PictureProcessingData();
                    pictureProcessingData.setType(intoImageDataTools(globalArrays.get(i).getType()));
                    pictureProcessingData.setNum(globalArrays.get(i).getNum()+100);
                    parmlist.add(pictureProcessingData);
                }
            }
        }
        if (partArrrays != null && partArrrays.size() != 0) {
            for (int j = 0; j < partArrrays.size(); j++) {
                pictureProcessingData = new PictureProcessingData();
                pictureProcessingData.setType(intoImageDataTools(partArrrays.get(j).getType()));
                pictureProcessingData.setImageData(partArrrays.get(j).getImageData());
                partlist.add(pictureProcessingData);
            }
        }
        if (lightThan != null) {
            LightRatioData lightRatioData = intoImageDataLightRation(lightThan);

            if (lightRatioData != null) {
                presetParm.setLightRatioData(lightRatioData);
            }
        }
        if (preinstail.getDes()!=null&&!TextUtils.isEmpty(preinstail.getDes())){
            presetParm.setDescribe(preinstail.getDes());
        }else{
            presetParm.setDescribe("");
        }

        if (parmlist != null) {
            presetParm.setParmlists(parmlist);
        }
        if (partlist != null) {
            presetParm.setPartParmlists(partlist);
        }
        if (preinstail.getImageDate() != 0) {
            presetParm.setTimeStamp(preinstail.getImageDate());
        }
        if (preinstail.getAutherName() != null && !TextUtils.isEmpty(preinstail.getAutherName())&&preinstail.getAutherName()!="null") {
            presetParm.setAuther(preinstail.getAutherName());
        }else {
            presetParm.setAuther("");
        }
        if (preinstail.getName() != null && !TextUtils.isEmpty(preinstail.getName())&&preinstail.getName()!="null") {
            presetParm.setName(preinstail.getName());
        }else{
            presetParm.setName("");
        }
        if (preinstail.getDevice() != null && !TextUtils.isEmpty(preinstail.getDevice())&&preinstail.getDevice()!="null") {
            presetParm.setEquipment(preinstail.getDevice());
        }else{
            presetParm.setEquipment("");
        }
        if(preinstail.getSystem()!=null&&!TextUtils.isEmpty(preinstail.getSystem())&&preinstail.getSystem()!="null"){
            presetParm.setSystem(preinstail.getSystem());
        }else{
            presetParm.setSystem("");
        }
        if (preinstail.getCameraParm() != null) {
            presetParm.setCameraParm(preinstail.getCameraParm());
        }
        if (preinstail.getLabel() != null && !preinstail.getLabel().isEmpty()) {
            List<String> mlist = stringTolist(preinstail.getLabel());
            if (mlist != null && mlist.size() != 0) {
                presetParm.setLabels(mlist);
            }

        }
        if (preinstail.getCoverId()!=null){
           String url = BitmapUtils.getBitmapUrl(preinstail.getCoverId());
            presetParm.setTextSrc(url);
        }
        if(!StringUtil.isBlank(preinstail.getDataSize())){
            presetParm.setSize(preinstail.getDataSize());
        }
        /** private String autherName;
         private String des;
         private String fileName;
         private String label;
         private String device;
         private String imageDate;
         private String name;**/
        return presetParm;
    }

    /* lightThan.setLeftValue(lightRatioData.getLeftLight());
        lightThan.setMoveValue2Value(lightRatioData.getTopLight());
        lightThan.setAuxiliaryValue(lightRatioData.getMoveLight());
        lightThan.setRightValue(lightRatioData.getRightLight());
        lightThan.setBackValue(lightRatioData.getBackgroudLight());
        lightThan.setBottomValue(lightRatioData.getBottomLight());
        lightThan.setName(lightRatioData.getName());*/
    private static LightRatioData intoImageDataLightRation(LightThan lightThan) {
        LightRatioData lightRatioData = new LightRatioData();
        if (lightThan != null) {
            lightRatioData.setLeftLight(lightThan.getLeftValue());
            lightRatioData.setTopLight(lightThan.getMoveValue2Value());
            lightRatioData.setMoveLight(lightThan.getAuxiliaryValue());
            lightRatioData.setRightLight(lightThan.getRightValue());
            lightRatioData.setBackgroudLight(lightThan.getBackValue());
            lightRatioData.setBottomLight(lightThan.getBottomValue());
            lightRatioData.setName(lightThan.getName());
            lightRatioData.setVersion(lightThan.getVersion());
        }
        return lightRatioData;
    }

    private static int intoImageDataTools(int type) {
        switch (type) {
            case 1000:
                type = BizImageMangage.ATMOSPHERE;
                break;
            case 1001:
                type = BizImageMangage.ATMOSPHERE_OUT;
                break;
            case 1002:
                type = BizImageMangage.BACKGROUND_REPAIR;
                break;
            case 1003:
                type = BizImageMangage.PAINT_BRUSH;
                break;
            case 1004:
                type = BizImageMangage.ROTATE;
                break;
            case 1005:
                type = BizImageMangage.SRC;
                break;
            case 1006:
                type = BizImageMangage.PARMS;
                break;
            case 1008:
            case 10:
                type = BizImageMangage.WHITE_BALANCE;
                break;
            case 2007:
                type = BizImageMangage.CROP;
                break;
            case 0:
                type = BizImageMangage.HIGHLIGHT;
                break;
            case 1:
                type = BizImageMangage.BRIGHTNESS;
                break;
            case 2:
                type = BizImageMangage.CONTRAST;
                break;
            case 3:
                type = BizImageMangage.SHADOW;
                break;
            case 4:
                type = BizImageMangage.NATURALSATURATION;
                break;
            case 5:
                type = BizImageMangage.WARMANDCOOLCOLORS;
                break;
            case 6:
                type = BizImageMangage.SATURATION;
                break;
            case 7:
                type = BizImageMangage.DEFINITION;
                break;
            case 8:
                type = BizImageMangage.SHARPEN;
                break;
            case 9:
                type = BizImageMangage.EXPOSURE;
                break;
            case 11:
                type = BizImageMangage.WHITE_LEVELS;
                break;
            case 12:
                type = BizImageMangage.BLACK_LEVELS;
                break;

        }
        return type;
    }

  /*  public static long dateToStamp(String s) {
        String res;
        long ts = 0;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = simpleDateFormat.parse(s);
            ts = date.getTime();

        } catch (ParseException e) {

        }
        return ts;
    }*/



    /**
     * 分类单项下列表
     * @param activity
     * @param page
     */
    public void setParamsData(Activity activity, int page,String catalogId) {

        String SelectCOBOX = LoginedUser.getLoginedUser().getSelect_market_cobox();

        int pageSize=getPageSize(activity);
        RetrofitFactory.getInstence().API().paramsList(new ParamsListBody(page + "", pageSize+"",catalogId))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MBaseObserver<MarketParamsListData>(activity, false) {
                    @Override
                    protected void onSuccees(BaseResult<MarketParamsListData> t) throws Exception {
                        marketModelInter.onData(t.getData().getList());

                    }

                    @Override
                    protected void onCodeError(BaseResult<MarketParamsListData> t) throws Exception {
                        ToastUtil.toast(t.getMessage());
                    }

                    @Override
                    protected void onFailure(Throwable e, boolean isNetWorkError) throws Exception {
                        ToastUtil.toast(e.getMessage());
                    }
                });




//        marketParamsTask = new MarketParamsTask(activity,true);
//        marketParamsTask.setAsyncTaskSuccessCallback(new AsyncTaskSuccessCallback<List<PresetParmManageItem>>() {
//            @Override
//            public void successCallback(Result<List<PresetParmManageItem>> result) {
//                marketModelInter.onData(result.getValue());
//
//
//            }
//        });
//        marketParamsTask.setAsyncTaskFailCallback(new AsyncTaskFailCallback<List<PresetParmManageItem>>() {
//            @Override
//            public void failCallback(Result<List<PresetParmManageItem>> result) {
//                ToastUtil.toast(result.getMessage());
//            }
//        });
//        marketParamsTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, page,catalogId,SelectCOBOX);
    }

    public interface MarketModelInter {
        void onData(List<MarketParamsListData.PresetParmManageItem> list);
        void onDataIndex(List<MarketParamsIndexListData.PresetParmIndexManageItem> list);
    }

    public void setMarketModelInter(MarketModelInter marketModelInter) {
        this.marketModelInter = marketModelInter;
    }

    private List<String> stringTolist(String s) {
        String[] arr = s.split(",");
        List<String> list = java.util.Arrays.asList(arr);
        return list;
    }

    private int getPageSize(Activity activity){
        int screenHeight=ContentUtil.getScreenHeight2(activity);
        int h=(ContentUtil.getScreenWidth(activity) - ContentUtil.dip2px(activity, 3)) / 3;
        int pageSize=((screenHeight- ImageUtil.Dp2Px(activity,200))/h+1)*3;
        return pageSize;
    }
}