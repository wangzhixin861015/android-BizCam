package com.bcnetech.hyphoto.ui.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

import com.bcnetech.bcnetechlibrary.view.BaseRelativeLayout;
import com.bcnetech.hyphoto.R;
import com.bcnetech.hyphoto.data.PresetItem;
import com.bcnetech.hyphoto.ui.adapter.PresetHorizontalListAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yhf on 2017/3/1.
 */
public class PresetBottomView extends BaseRelativeLayout {


    private RecyclerView horizontalListView;
//    private TextView rl_preset;
    private PresetHorizontalListAdapter adapter;
    private List<PresetItem> presetItems;
    private List<Bitmap> recommandBitmaps;
    public PresetBottomView(Context context) {
        super(context);
    }

    public PresetBottomView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PresetBottomView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void initData() {
        super.initData();
        presetItems=new ArrayList<>();

        adapter=new PresetHorizontalListAdapter(getContext(),presetItems);

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

    public void closePresetClick(View.OnClickListener listener){
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

    public void addDataBitmap(List<Bitmap> bitmaps){
        recommandBitmaps.clear();
        recommandBitmaps.addAll(bitmaps);
        adapter.notifyDataSetChanged();
    }

    public void setPresetInterface(PresetHorizontalListAdapter.PresetHolderInterFace interFace){
        adapter.setInterFace(interFace);
    }

}
