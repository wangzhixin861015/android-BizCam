package com.bcnetech.hyphoto.ui.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bcnetech.bcnetechlibrary.view.BaseRelativeLayout;
import com.bcnetech.hyphoto.data.PresetItem;
import com.bcnetech.hyphoto.ui.adapter.PresetHorizontalListAdapter;
import com.bcnetech.hyphoto.ui.adapter.PresetScanListAdapter;
import com.bcnetech.hyphoto.ui.view.scaleview.BaseScaleView;
import com.bcnetech.hyphoto.ui.view.scaleview.HorizontalScaleScrollview;
import com.bcnetech.bluetoothlibarary.bluetoothagreement.CommendManage;
import com.bcnetech.hyphoto.R;

import java.util.List;

/**
 * 相机参数调整界面
 * Created by a1234 on 2017/11/21.
 */

public class CameraParamAdjustView extends BaseRelativeLayout {
    private HorizontalScaleScrollview horizontalscalescrollview;
    private CameraPointView cameraPointView;
    private CameraParamAdjustInter cameraParamAdjustInter;
    private PresetBottomView presetBottomView;
    private PresetScanBottomView presetScanBottomView;
    private RelativeLayout rl_Scale;


    private RecyclerView recycler_lightParam;
    private LinearLayout ll_pro;
    private RecyclerView recycler_cameraParam;
    private View view_line;
    private TextView tv_reduce;
    private TextView tv_add;


    private int type=TYPE_AUTO;

    public static final int TYPE_AUTO=0;

    public static final int TYPE_PRESET=1;

    public static final int TYPE_PRO=2;

    public static final int TYPE_M=3;

    private int connectionType=BLUE_TOUCH_CLOSE;

    //蓝牙关闭
    public static final int BLUE_TOUCH_CLOSE = 1;
    //蓝牙开启
    public static final int BLUE_TOUCH_OPEN = 2;
    //蓝牙连接
    public static final int BLUE_TOUCH_CONNECTION = 3;
    //蓝牙断开
    public static final int BLUE_TOUCH_CONNECTION_ERROR = 4;

    private boolean isCamera2Support=false;

    public CameraParamAdjustView(Context context) {
        super(context);
    }

    public CameraParamAdjustView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CameraParamAdjustView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void initView() {
        super.initView();
        inflate(getContext(), R.layout.camera_adjust_layout, this);
        horizontalscalescrollview = (HorizontalScaleScrollview)findViewById(R.id.horizontalscalescrollview);
        cameraPointView = (CameraPointView)findViewById(R.id.camera_point_view);
        presetBottomView= (PresetBottomView) findViewById(R.id.preset_bottom_view);
        rl_Scale= (RelativeLayout) findViewById(R.id.rl_Scale);
        recycler_lightParam= (RecyclerView) findViewById(R.id.recycler_lightParam);
        ll_pro= (LinearLayout) findViewById(R.id.ll_pro);
        recycler_cameraParam=(RecyclerView) findViewById(R.id.recycler_cameraParam);
        view_line=findViewById(R.id.view_line);
        presetScanBottomView= (PresetScanBottomView) findViewById(R.id.preset_scan_bottom_view);
        tv_reduce= (TextView) findViewById(R.id.tv_reduce);
        tv_add= (TextView) findViewById(R.id.tv_add);
    }

    public boolean isCamera2Support() {
        return isCamera2Support;
    }

    public void setCamera2Support(boolean camera2Support) {
        isCamera2Support = camera2Support;
    }

    @Override
    protected void initData() {
        super.initData();
    }

    @Override
    protected void onViewClick() {
        horizontalscalescrollview.setOnScrollListener(new BaseScaleView.OnScrollListener() {
            @Override
            public void onScaleScroll(int scale) {
                if(null!=cameraParamAdjustInter){
                    cameraParamAdjustInter.onScaleScroll(scale);
                }
            }
        });
    }

    public interface CameraParamAdjustInter{
        void onScaleScroll(int scale);
    }

    public void setCameraParamAdjustInter(CameraParamAdjustInter cameraParamAdjustInter){
        this.cameraParamAdjustInter = cameraParamAdjustInter;
    }

    public void setText(String text){
        cameraPointView.setText(text);
    }

    public int getMax(){
       return horizontalscalescrollview.getMax();
    }

    public void setAddListner(OnClickListener onClickListener){
        tv_add.setOnClickListener(onClickListener);
    }

    public void setReduceListner(OnClickListener onClickListener){
        tv_reduce.setOnClickListener(onClickListener);
    }

    public int getMin(){
        return horizontalscalescrollview.getMin();
    }

    public void reset(){
        horizontalscalescrollview.reset();
    }

    public void setClickShowType(int type){
        if(connectionType==BLUE_TOUCH_CONNECTION){
            if(type==TYPE_PRO){
                rl_Scale.setVisibility(GONE);
                presetBottomView.setVisibility(GONE);
                ll_pro.setVisibility(VISIBLE);
                if(isCamera2Support){
                    recycler_cameraParam.setVisibility(VISIBLE);
                    view_line.setVisibility(VISIBLE);
                }else{
                    recycler_cameraParam.setVisibility(GONE);
                    view_line.setVisibility(GONE);
                }
                recycler_lightParam.setVisibility(VISIBLE);

            }else if(type==TYPE_PRESET){
                ll_pro.setVisibility(GONE);
                rl_Scale.setVisibility(GONE);
                presetBottomView.setVisibility(VISIBLE);
                recycler_cameraParam.setVisibility(GONE);
                recycler_lightParam.setVisibility(VISIBLE);

            }
            else if(type==TYPE_AUTO){
                ll_pro.setVisibility(GONE);
                rl_Scale.setVisibility(VISIBLE);
                presetBottomView.setVisibility(GONE);
                recycler_cameraParam.setVisibility(GONE);
                recycler_lightParam.setVisibility(GONE);

            }
        }else if(connectionType==BLUE_TOUCH_CLOSE||connectionType==BLUE_TOUCH_CONNECTION_ERROR){
            if(type==TYPE_AUTO){
                ll_pro.setVisibility(GONE);
                rl_Scale.setVisibility(VISIBLE);
                presetBottomView.setVisibility(GONE);
//                recycler_cameraParam.setVisibility(GONE);
//                recycler_lightParam.setVisibility(VISIBLE);
            }else  if(type==TYPE_M){

                ll_pro.setVisibility(VISIBLE);
                rl_Scale.setVisibility(GONE);
                presetBottomView.setVisibility(GONE);
                recycler_cameraParam.setVisibility(VISIBLE);
                view_line.setVisibility(GONE);
                recycler_lightParam.setVisibility(GONE);
            }
        }

    }

    public void setConnectionShowType(int type){
        if(type==BLUE_TOUCH_CONNECTION){
            if(CommendManage.getInstance().getVersion()!=CommendManage.VERSION_BOX){
                presetBottomView.setVisibility(VISIBLE);
                rl_Scale.setVisibility(GONE);
            }else{
                presetBottomView.setVisibility(GONE);
                rl_Scale.setVisibility(VISIBLE);
            }
            connectionType=type;
        }else if(type==BLUE_TOUCH_CONNECTION_ERROR||type==BLUE_TOUCH_CLOSE){
            presetBottomView.setVisibility(GONE);
            rl_Scale.setVisibility(VISIBLE);
            ll_pro.setVisibility(GONE);
            connectionType=type;
        }
    }

    public void setConnectionShowTypeCamera2(int type){
        if(type==BLUE_TOUCH_CONNECTION){
            presetBottomView.setVisibility(VISIBLE);
            rl_Scale.setVisibility(GONE);
            ll_pro.setVisibility(GONE);
            connectionType=type;
        }else if(type==BLUE_TOUCH_CONNECTION_ERROR||type==BLUE_TOUCH_CLOSE){
            presetBottomView.setVisibility(GONE);
            rl_Scale.setVisibility(VISIBLE);
            ll_pro.setVisibility(GONE);
            connectionType=type;
        }
    }


    public void setSelect(int position){
        presetBottomView.setSelect(position);
    }

    public void addData(List<PresetItem> presets){
//        presetScanBottomView.setVisibility(GONE);
//        presetBottomView.setVisibility(VISIBLE);
        presetBottomView.addData(presets);
    }

    public void scrollToScale(int scale){
        horizontalscalescrollview.scrollToScale(scale);
    }

    public void addScanData(List<PresetItem> presets){
        presetScanBottomView.setVisibility(VISIBLE);
        presetBottomView.setVisibility(GONE);
        presetScanBottomView.addData(presets);
    }

    public void setPresetInterface(PresetHorizontalListAdapter.PresetHolderInterFace interFace){
        presetBottomView.setPresetInterface(interFace);
    }

    public void setScanInterface(PresetScanListAdapter.PresetHolderInterFace interFace){
        presetScanBottomView.setPresetInterface(interFace);
    }

    public RecyclerView getRecycler_lightParam() {
        return recycler_lightParam;
    }

    public void setRecycler_lightParam(RecyclerView recycler_lightParam) {
        this.recycler_lightParam = recycler_lightParam;
    }

    public RecyclerView getRecycler_cameraParam() {
        return recycler_cameraParam;
    }

    public void setRecycler_cameraParam(RecyclerView recycler_cameraParam) {
        this.recycler_cameraParam = recycler_cameraParam;
    }
}

