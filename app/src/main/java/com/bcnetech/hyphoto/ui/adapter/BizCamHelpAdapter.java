package com.bcnetech.hyphoto.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bcnetech.bcnetchhttp.config.Flag;
import com.bcnetech.hyphoto.R;
import com.bcnetech.hyphoto.data.BizCamHelpData;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by yhf on 2017/3/2.
 */
public class BizCamHelpAdapter extends RecyclerView.Adapter<BizCamHelpAdapter.ViewHolder> {


    private Context context;

    private List<BizCamHelpData> bizCamHelpDatas;
    private LayoutInflater inflater;
    private BizCamHelpHolderInterFace interFace;
    private int selectItem = 0;
    private ExecutorService cachedThreadPool;


    public Context getContext() {
        return context;
    }

    public BizCamHelpAdapter(Context context, List<BizCamHelpData> bizCamHelpDatas) {
        this.context = context;
        this.bizCamHelpDatas = bizCamHelpDatas;
        this.inflater=LayoutInflater.from(context);
        cachedThreadPool = Executors.newCachedThreadPool();
    }

    public int getSelectItem() {
        return selectItem;
    }

    public void setSelectItem(int selectItem) {
        this.selectItem = selectItem;
    }

    public BizCamHelpHolderInterFace getInterFace() {
        return interFace;
    }

    public void setInterFace(BizCamHelpHolderInterFace interFace) {
        this.interFace = interFace;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public List<BizCamHelpData> getPresets() {
        return bizCamHelpDatas;
    }

    public void setPresets(List<BizCamHelpData> presets) {
        this.bizCamHelpDatas = presets;
    }


    @Override
    public int getItemCount() {
        return bizCamHelpDatas==null?0:bizCamHelpDatas.size();
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new BizCamHelpAdapter.ViewHolder(inflater.inflate(R.layout.bizcam_help_item,null));
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        int type = bizCamHelpDatas.get(position).getType();
        if(type==Flag.BIZCAM_HELP_AI_COBOX){
            viewHolder.tv_name.setText("智拍-商拍酷宝");
            viewHolder.tv_context.setText("蓝牙酷宝 一键出图");
            viewHolder.iv_image.setImageResource(R.drawable.bizcam_help_ai);
        }else if(type==Flag.BIZCAM_HELP_AI_COLINK){
            viewHolder.tv_name.setText("智拍-商拍魔盒");
            viewHolder.tv_context.setText("蓝牙魔盒 一键出图");
            viewHolder.iv_image.setImageResource(R.drawable.bizcam_help_ai);
        }else if(type==Flag.BIZCAM_HELP_PAINT){
            viewHolder.tv_name.setText("画笔");
            viewHolder.tv_context.setText("点击调整曝光度及饱和度");
            viewHolder.iv_image.setImageResource(R.drawable.bizcam_help_paint);
        }else if(type==Flag.BIZCAM_HELP_MATTING){
            viewHolder.tv_name.setText("抠图");
            viewHolder.tv_context.setText("布尔抠图算法");
            viewHolder.iv_image.setImageResource(R.drawable.bizcam_help_matting);
        }else if(type==Flag.BIZCAM_HELP_REPAIR){
            viewHolder.tv_name.setText("修复");
            viewHolder.tv_context.setText("去除页面杂质");
            viewHolder.iv_image.setImageResource(R.drawable.bizcam_help_reapir);
        }


        viewHolder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                interFace.itemClick(position, viewHolder);
            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        View view;
        TextView tv_name;
        TextView tv_context;
        ImageView iv_image;

        public ViewHolder(View itemView) {
            super(itemView);
            view=itemView;
            tv_name=  view.findViewById(R.id.tv_name);
            tv_context= view.findViewById(R.id.tv_context);
            iv_image= view.findViewById(R.id.iv_image);

        }
    }

    public interface BizCamHelpHolderInterFace{
        void itemClick(int position, ViewHolder viewHolder);
    }
}
