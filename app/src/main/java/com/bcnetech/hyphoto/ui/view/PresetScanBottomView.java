package com.bcnetech.hyphoto.ui.view;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.bcnetech.bcnetechlibrary.view.BaseRelativeLayout;
import com.bcnetech.hyphoto.data.PresetItem;
import com.bcnetech.hyphoto.ui.adapter.PresetScanListAdapter;
import com.bcnetech.hyphoto.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yhf on 2018/3/12.
 */
public class PresetScanBottomView extends BaseRelativeLayout {


    private RecyclerView horizontalListView;
//    private TextView rl_preset;
    private PresetScanListAdapter adapter;
    private List<PresetItem> presetItems;
    public PresetScanBottomView(Context context) {
        super(context);
    }

    public PresetScanBottomView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PresetScanBottomView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void initData() {
        super.initData();
        presetItems=new ArrayList<>();

        adapter=new PresetScanListAdapter(getContext(),presetItems);

        //设置布局管理器
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        horizontalListView.setLayoutManager(linearLayoutManager);
        horizontalListView.setAdapter(adapter);
    }

    @Override
    protected void initView() {
        super.initView();
        inflate(getContext(), R.layout.prset_bottom_layout,this);
        horizontalListView= (RecyclerView) findViewById(R.id.horizontalListView);
//        rl_preset= (TextView) findViewById(R.id.rl_preset);
    }

    @Override
    protected void onViewClick() {
        super.onViewClick();
    }

    public void closePresetClick(OnClickListener listener){
//        rl_preset.setOnClickListener(listener);
    }

    public void setSelect(int position){
        adapter.setSelectItem(position);
        adapter.notifyDataSetChanged();
    }
    
    public void addData(List<PresetItem> presets){
        presetItems.clear();
        presetItems.addAll(presets);
        adapter.notifyDataSetChanged();
    }

    public void setPresetInterface(PresetScanListAdapter.PresetHolderInterFace interFace){
        adapter.setInterFace(interFace);
    }

}
