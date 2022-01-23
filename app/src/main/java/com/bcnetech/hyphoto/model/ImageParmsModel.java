package com.bcnetech.hyphoto.model;

import com.bcnetech.bcnetechlibrary.view.PickerViewV;
import com.bcnetech.hyphoto.App;
import com.bcnetech.hyphoto.imageinterface.BizImageMangage;
import com.bcnetech.hyphoto.sql.dao.ImageData;
import com.bcnetech.hyphoto.sql.dao.PictureProcessingData;
import com.bcnetech.hyphoto.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import jp.co.cyberagent.android.gpuimage.GPUImageFilter;
import jp.co.cyberagent.android.gpuimage.gpumanage.GPUImageFilterTools;

/**
 * Created by wenbin on 2017/2/17.
 */

public class ImageParmsModel {
    private final static int SHADOW_HIGHLIGHT = 0;//高光阴影初始值

    public List getLightList(){
        List list = new ArrayList<>();
        list.add(new PickerViewV.Item(App.getInstance().getResources().getString(R.string.exposire)));
        list.add(new PickerViewV.Item(App.getInstance().getResources().getString(R.string.contrast)));
        list.add(new PickerViewV.Item(App.getInstance().getResources().getString(R.string.brightness)));
        list.add(new PickerViewV.Item(App.getInstance().getResources().getString(R.string.high_light)));
        list.add(new PickerViewV.Item(App.getInstance().getResources().getString(R.string.shadow)));
        return list;
    }

    public List getColorList(){
        List list = new ArrayList<>();
        list.add(new PickerViewV.Item(App.getInstance().getResources().getString(R.string.nature_saturation)));
        list.add(new PickerViewV.Item(App.getInstance().getResources().getString(R.string.saturation)));
        list.add(new PickerViewV.Item(App.getInstance().getResources().getString(R.string.clod_hot)));
        return list;
    }

    public List getDetailsList(){
        List list = new ArrayList<>();
        list.add(new PickerViewV.Item(App.getInstance().getResources().getString(R.string.defintion)));
        list.add(new PickerViewV.Item(App.getInstance().getResources().getString(R.string.sharpen)));
        return list;
    }



    List<PictureProcessingData> imagetools;
    public void initFilter(List<GPUImageFilter> gpuImageFilters,HashMap<Integer,
            GPUImageFilterTools.FilterAdjuster> adjustMap,
                           List<PictureProcessingData> currentPicDatas,
                           ImageData imageData){
        if (imageData.getImageTools() != null && imageData.getImageTools().size() != 0) {
            imagetools = imageData.getImageTools();

        } else if (imageData.getPresetParms() != null && imageData.getPresetParms().getParmlists() != null && imageData.getPresetParms().getParmlists().size() != 0) {
            imagetools = imageData.getPresetParms().getParmlists();
        }
        if (imagetools==null){
            return;
        }
        for (int i = 0; i < imagetools.size(); i++) {
            if(imagetools.get(i).getType()== BizImageMangage.WHITE_BALANCE){
                //白平衡
                addFilterAdjustMap(imagetools.get(i).getType(),gpuImageFilters,adjustMap,imagetools.get(i).getNum(),imagetools.get(i).getTintNum());
            }else {
                addFilterAdjustMap(imagetools.get(i).getType(),gpuImageFilters,adjustMap,imagetools.get(i).getNum());
            }
//            addFilterAdjustMap(imageData.getImageTools().get(i).getType(),gpuImageFilters,adjustMap,imageData.getImageTools().get(i).getNum());
            currentPicDatas.add(new PictureProcessingData(imagetools.get(i)));
        }

    }

    public void initFilterAgin(List<GPUImageFilter> gpuImageFilters,HashMap<Integer,
            GPUImageFilterTools.FilterAdjuster> adjustMap,
                           List<PictureProcessingData> currentPicDatas){
        for (int i = 0; i < currentPicDatas.size(); i++) {
            addFilterAdjustMap(currentPicDatas.get(i).getType(),gpuImageFilters,adjustMap,currentPicDatas.get(i).getNum());
        }

    }



    public void addFilter(List<GPUImageFilter> gpuImageFilters,
                          HashMap<Integer, GPUImageFilterTools.FilterAdjuster> adjustMap,
                          List<PictureProcessingData> currentPicDatas,
                          int type){
        addFilterAdjustMap(type,gpuImageFilters,adjustMap,BizImageMangage.INIT_DATA);
        PictureProcessingData pictureProcessingData = new PictureProcessingData(type);
        currentPicDatas.add(pictureProcessingData);

    }


    private void addFilterAdjustMap(int type,
                                    List<GPUImageFilter> gpuImageFilters,
                                    HashMap<Integer, GPUImageFilterTools.FilterAdjuster> adjustMap,
                                    int adjustData){
        GPUImageFilter mFilter;
        GPUImageFilterTools.FilterAdjuster mFilterAdjuster;
        mFilter = BizImageMangage.getInstance().getGPUFilterforType(App.getInstance(), type);
        gpuImageFilters.add(mFilter);
        mFilterAdjuster = new GPUImageFilterTools.FilterAdjuster(mFilter);
        if(type==BizImageMangage.WHITE_BALANCE){
            mFilterAdjuster.adjust(adjustData,adjustData);
        }else{
            mFilterAdjuster.adjust(adjustData);
        }
        adjustMap.put(type, mFilterAdjuster);
    }

    private void addFilterAdjustMap(int type,
                                    List<GPUImageFilter> gpuImageFilters,
                                    HashMap<Integer, GPUImageFilterTools.FilterAdjuster> adjustMap,
                                    int temperature,int tint){
        GPUImageFilter mFilter;
        GPUImageFilterTools.FilterAdjuster mFilterAdjuster;
        mFilter = BizImageMangage.getInstance().getGPUFilterforType(App.getInstance(), type);
        gpuImageFilters.add(mFilter);
        mFilterAdjuster = new GPUImageFilterTools.FilterAdjuster(mFilter);
        mFilterAdjuster.adjust(temperature,tint);
        adjustMap.put(type, mFilterAdjuster);
    }
}
