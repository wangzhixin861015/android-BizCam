package com.bcnetech.hyphoto.presenter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.bcnetech.hyphoto.imageinterface.BizImageMangage;
import com.bcnetech.hyphoto.model.PresetLoadManageModel;
import com.bcnetech.hyphoto.model.imodel.IPresetLoadManageModel;
import com.bcnetech.hyphoto.presenter.iview.IPresetLoadManageView;
import com.bcnetech.hyphoto.sql.dao.PresetParm;
import com.bcnetech.hyphoto.ui.activity.personCenter.PresetLoadManageActivity;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by yhf on 2017/3/27.
 */
public class PresetLoadManagePresenter extends BasePresenter<IPresetLoadManageView> implements IPresetLoadManageModel {


    private PresetLoadManageModel model;

    /***
     * @param activity
     */
    public static void startAction(Activity activity) {
        Intent intent = new Intent(activity, PresetLoadManageActivity.class);
        activity.startActivity(intent);

    }

    @Override
    public void onCreate(Bundle bundle) {
        model=new PresetLoadManageModel();
        model.attach(this,activity);
    }

    @Override
    public void onDestroy() {
        model.dettach();
    }


    /**
     * 获取设备名称
     * @return
     */
    public String getDeviceName() {
        String mtype = "android"; // 手机型号
        return mtype;
    }


    public void sortList(List<PresetParm> list){
        Collections.sort(list, new Comparator<PresetParm>(){
            /*
             * int compare(PresetParm lhs, PresetParm rhs) 返回一个基本类型的整型，
             * 返回负数表示：lhs 小于rhs，
             * 返回0 表示：lhs和rhs相等，
             * 返回正数表示：lhs大于rhs。
             */
            @Override
            public int compare(PresetParm lhs, PresetParm rhs) {
                //按照Position降序排列
                if(lhs.getPosition() > rhs.getPosition()){
                    return -1;
                }
                if(lhs.getPosition() == rhs.getPosition()){
                    return 0;
                }
                return 1;
            }
        });
    }

    private static int intoImageDataTools(int type) {
        switch (type) {
            case 1000:
                type = BizImageMangage.ATMOSPHERE;
                break;
            case 1001:
                type = BizImageMangage.ATMOSPHERE_OUT;
                break;
            case 1003:
                type = BizImageMangage.PAINT_BRUSH;
                break;
            case 1002:
                type = BizImageMangage.BACKGROUND_REPAIR;
                break;
            case 1005:
                type = BizImageMangage.ROTATE;
                break;
            case 1:
                type = BizImageMangage.BRIGHTNESS;
                break;
            case 2:
                type = BizImageMangage.CONTRAST;
                break;
            case 7:
                type = BizImageMangage.DEFINITION;
                break;
            case 9:
                type = BizImageMangage.EXPOSURE;
                break;
            case 0:
                type = BizImageMangage.HIGHLIGHT;
                break;
            case 4:
                type = BizImageMangage.NATURALSATURATION;
                break;
            case 6:
                type = BizImageMangage.SATURATION;
                break;
            case 3:
                type = BizImageMangage.SHADOW;
                break;
            case 8:
                type = BizImageMangage.SHARPEN;
                break;
            case 5:
                type = BizImageMangage.WARMANDCOOLCOLORS;
                break;
            case 2007:
                type = BizImageMangage.CROP;
                break;
        }
        return type;
    }




}
