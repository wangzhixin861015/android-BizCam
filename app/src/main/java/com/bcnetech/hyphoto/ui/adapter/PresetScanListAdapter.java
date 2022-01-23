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
import com.bcnetech.hyphoto.data.PresetItem;
import com.bcnetech.hyphoto.utils.BitmapUtils;
import com.bcnetech.hyphoto.utils.FileUtil;
import com.bcnetech.hyphoto.utils.ImageUtil;
import com.bcnetech.hyphoto.utils.StringUtil;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by yhf on 2017/3/2.
 */
public class PresetScanListAdapter extends RecyclerView.Adapter<PresetScanListAdapter.ViewHolder> {


    private Context context;

    private List<PresetItem> presets;
    private LayoutInflater inflater;
    private PresetHolderInterFace interFace;
    private int selectItem = 0;
    private ExecutorService cachedThreadPool;

    //商拍光影
    final int TYPE_1 = 0;
    //智能扫描
    final int TYPE_2 = 1;

    public Context getContext() {
        return context;
    }

    public PresetScanListAdapter(Context context, List<PresetItem> presets) {
        this.context = context;
        this.presets = presets;
        this.inflater=LayoutInflater.from(context);
        cachedThreadPool = Executors.newCachedThreadPool();
    }

    public int getSelectItem() {
        return selectItem;
    }

    public void setSelectItem(int selectItem) {
        this.selectItem = selectItem;
    }

    public PresetHolderInterFace getInterFace() {
        return interFace;
    }

    public void setInterFace(PresetHolderInterFace interFace) {
        this.interFace = interFace;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public List<PresetItem> getPresets() {
        return presets;
    }

    public void setPresets(List<PresetItem> presets) {
        this.presets = presets;
    }


    @Override
    public int getItemCount() {
        return presets==null?0:presets.size();
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PresetScanListAdapter.ViewHolder(inflater.inflate(R.layout.preset_horizonta_scan_list_item,null));
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        String path = presets.get(position).getPath();
        if(StringUtil.isBlank(path)){
            viewHolder.list_item_help.setVisibility(View.GONE);
            viewHolder.list_item_none.setVisibility(View.VISIBLE);
            viewHolder.list_item_scan.setVisibility(View.GONE);
            viewHolder.list_item.setVisibility(View.GONE);
        }else if(path.equals("help")){
            viewHolder.list_item_help.setVisibility(View.VISIBLE);
            viewHolder.list_item_scan.setVisibility(View.GONE);
            viewHolder.list_item_none.setVisibility(View.GONE);
            viewHolder.list_item.setVisibility(View.GONE);
        }else if(path.equals("scan")){
            viewHolder.list_item_help.setVisibility(View.GONE);
            viewHolder.list_item_scan.setVisibility(View.VISIBLE);
            viewHolder.list_item_none.setVisibility(View.GONE);
            viewHolder.list_item.setVisibility(View.GONE);
        }else {
            int w=Integer.valueOf(presets.get(position).getPresetParm().getImageWidth());
            int h=Integer.valueOf(presets.get(position).getPresetParm().getImageHeight());
            viewHolder.list_item_help.setVisibility(View.GONE);
            viewHolder.list_item_none.setVisibility(View.GONE);
            viewHolder.list_item_scan.setVisibility(View.GONE);
            viewHolder.list_item.setVisibility(View.VISIBLE);
            if(!StringUtil.isBlank(presets.get(position).getPresetParm().getCoverId())){
                if(FileUtil.fileIsExists(presets.get(position).getPresetParm().getCoverId())){
                    ImageUtil.EBizImageLoad(viewHolder.list_item,"file://"+ Flag.PRESET_IMAGE_PATH+presets.get(position).getPresetParm().getCoverId()+".jpg");
                }else if(presets.get(position).getPresetParm().getCoverId().equals("default")){
                    Picasso.get().load(R.drawable.preset_default).into(viewHolder.list_item);
                }else if(presets.get(position).getPresetParm().getCoverId().equals("default2")){
                    Picasso.get().load(R.drawable.preset_default_two).into(viewHolder.list_item);
                }else if(presets.get(position).getPresetParm().getCoverId().equals("default3")){
                    Picasso.get().load(R.drawable.preset_default_three).into(viewHolder.list_item);
                }else {
                    if(path.contains("png")){
                        Picasso.get().load(path).into(viewHolder.list_item);
                    }else {
                        String url = StringUtil.getSizeUrl(BitmapUtils.getBitmapUrl6(presets.get(position).getPresetParm().getCoverId()),
                                ImageUtil.Dp2Px(getContext(),120),
                                ImageUtil.Dp2Px(getContext(),120));

                        Picasso.get().load(url).into(viewHolder.list_item);

//                        ImageUtil.EBizImageLoad(viewHolder.list_item,url, new ImageLoadingListener() {
//                            @Override
//                            public void onLoadingStarted(String s, View view) {
//
//                            }
//
//                            @Override
//                            public void onLoadingFailed(String s, View view, FailReason failReason) {
//                                viewHolder.list_item.setBackgroundResource(R.drawable.cobox_choose_bg);
//                            }
//
//                            @Override
//                            public void onLoadingComplete(String s, View view, final Bitmap bitmap) {
//                                cachedThreadPool.execute(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        try {
//                                            FileUtil.saveMyPreset(bitmap,presets.get(position).getPresetParm().getCoverId());
//                                        } catch (IOException e) {
//                                            e.printStackTrace();
//                                        }
//                                    }
//                                });
//
//                            }
//
//                            @Override
//                            public void onLoadingCancelled(String s, View view) {
//
//                            }
//                        });
                    }
//                String url=StringUtil.getSizeUrl(BitmapUtils.getBitmapUrl2(presets.get(position).getPresetParm().getPresetId()),0 ,0);

                }
            }else {
                if(FileUtil.fileIsExists(presets.get(position).getPresetId())){
                    ImageUtil.EBizImageLoad(viewHolder.list_item,"file://"+ Flag.PRESET_IMAGE_PATH+presets.get(position).getPresetParm().getCoverId()+".jpg");
                }else if(presets.get(position).getPresetParm().getId().equals("1")){
                    Picasso.get().load(R.drawable.preset_default).into(viewHolder.list_item);
                }else {
                    if(path.contains("png")){
                        Picasso.get().load(path).into(viewHolder.list_item);
                    }else {
                        String url = StringUtil.getSizeUrl(BitmapUtils.getBitmapUrl6(presets.get(position).getPresetId()),
                                ImageUtil.Dp2Px(getContext(),120),
                                ImageUtil.Dp2Px(getContext(),120));

                        Picasso.get().load(url).into(viewHolder.list_item);
                    }
//                String url=StringUtil.getSizeUrl(BitmapUtils.getBitmapUrl2(presets.get(position).getPresetParm().getPresetId()),0 ,0);

                }
            }
//            ImageUtil.EBizImageLoad(viewHolder.list_item,path);
        }
        if (selectItem == position) {
            viewHolder.list_item_check.setVisibility(View.VISIBLE);
        }else {
            viewHolder.list_item_check.setVisibility(View.GONE);
        }

        viewHolder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                interFace.presetItemClick(position, viewHolder);
            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        View view;
        TextView list_item_none;
        ImageView list_item;
        ImageView list_item_check;
        ImageView list_item_help;
        ImageView list_item_scan;
        public ViewHolder(View itemView) {
            super(itemView);
            view=itemView;
            list_item= (ImageView) view.findViewById(R.id.list_item);
            list_item_check= (ImageView) view.findViewById(R.id.list_item_check);
            list_item_none= (TextView) view.findViewById(R.id.list_item_none);
            list_item_help= (ImageView) view.findViewById(R.id.list_item_help);
            list_item_scan= (ImageView) view.findViewById(R.id.list_item_scan);
        }
    }

    public interface PresetHolderInterFace{
        void presetItemClick(int position, ViewHolder viewHolder);
    }
}
