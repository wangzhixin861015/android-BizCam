package com.bcnetech.hyphoto.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bcnetech.bcnetechlibrary.util.ContentUtil;
import com.bcnetech.hyphoto.R;
import com.bcnetech.hyphoto.data.SDCardMedia;
import com.bcnetech.hyphoto.utils.FileUtil;
import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<SDCardMedia> mSDCardMedias;
    private LayoutInflater mInflater;

    //保存选中的图片
    private ArrayList<SDCardMedia> mSelectSDCardMedias = new ArrayList<>();
    private OnImageSelectListener mSelectListener;
    private OnItemClickListener mItemClickListener;
    private int mMaxCount;
    private boolean isSingle;
    private int width;

    /**
     * @param maxCount 图片的最大选择数量，小于等于0时，不限数量，isSingle为false时才有用。
     * @param isSingle 是否单选
     */
    public ImageAdapter(Context context, int maxCount, boolean isSingle) {
        mContext = context;
        this.mInflater = LayoutInflater.from(mContext);
        mMaxCount = maxCount;
        this.isSingle = isSingle;
        width = (ContentUtil.getScreenWidth(context) - ContentUtil.dip2px(context, 3)) / 3;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.adapter_images_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final SDCardMedia SDCardMedia = mSDCardMedias.get(position);
        holder.grid_video.setVisibility(View.INVISIBLE);
        Glide.with(mContext).load(new File(SDCardMedia.getThumb_path())).override(width, width).centerCrop().into(holder.grid_item);
        if (SDCardMedia.isVideo() == 1) {
            holder.grid_video.setVisibility(View.VISIBLE);
            holder.grid_video.setImageDrawable(mContext.getResources().getDrawable(R.drawable.video));
        } else {
            if (FileUtil.getImageType(SDCardMedia.getPath()).equals("gif")) {
                holder.grid_video.setVisibility(View.VISIBLE);
                holder.grid_video.setImageDrawable(mContext.getResources().getDrawable(R.drawable.gif));
            }
        }
        setItemSelect(holder, mSelectSDCardMedias.contains(SDCardMedia));
        //点击选中/取消选中图片
        holder.grid_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSelectSDCardMedias.contains(SDCardMedia)) {
                    //如果图片已经选中，就取消选中
                    unSelectImage(SDCardMedia);
                    setItemSelect(holder, false);
                } else if (isSingle) {
                    //如果是单选，就先清空已经选中的图片，再选中当前图片
                    clearImageSelect();
                    selectImage(SDCardMedia);
                    setItemSelect(holder, true);
                } else if (mMaxCount <= 0 || mSelectSDCardMedias.size() < mMaxCount) {
                    //如果不限制图片的选中数量，或者图片的选中数量
                    // 还没有达到最大限制，就直接选中当前图片。
                    selectImage(SDCardMedia);
                    setItemSelect(holder, true);
                }
            }
        });
        holder.grid_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSelectSDCardMedias.contains(SDCardMedia)) {
                    //如果图片已经选中，就取消选中
                    unSelectImage(SDCardMedia);
                    setItemSelect(holder, false);
                } else if (isSingle) {
                    //如果是单选，就先清空已经选中的图片，再选中当前图片
                    clearImageSelect();
                    selectImage(SDCardMedia);
                    setItemSelect(holder, true);
                } else if (mMaxCount <= 0 || mSelectSDCardMedias.size() < mMaxCount) {
                    //如果不限制图片的选中数量，或者图片的选中数量
                    // 还没有达到最大限制，就直接选中当前图片。
                    selectImage(SDCardMedia);
                    setItemSelect(holder, true);
                }
            }
        });

        /*holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                *//*if(mItemClickListener != null){
                    mItemClickListener.OnItemClick(SDCardMedia,holder.getAdapterPosition());
                }*//*
                if (mSelectSDCardMedias.contains(SDCardMedia)) {
                    //如果图片已经选中，就取消选中
                    unSelectImage(SDCardMedia);
                    setItemSelect(holder, false);
                } else if (isSingle) {
                    //如果是单选，就先清空已经选中的图片，再选中当前图片
                    clearImageSelect();
                    selectImage(SDCardMedia);
                    setItemSelect(holder,true);
                } else if (mMaxCount <= 0 || mSelectSDCardMedias.size() < mMaxCount) {
                    //如果不限制图片的选中数量，或者图片的选中数量
                    // 还没有达到最大限制，就直接选中当前图片。
                    selectImage(SDCardMedia);
                    setItemSelect(holder, true);
                }

            }
        });*/
    }

    /**
     * 选中图片
     *
     * @param SDCardMedia
     */
    private void selectImage(SDCardMedia SDCardMedia) {
        mSelectSDCardMedias.add(SDCardMedia);
        if (mSelectListener != null) {
            mSelectListener.OnImageSelect(SDCardMedia, true, mSelectSDCardMedias.size());
        }
    }

    /**
     * 取消选中图片
     *
     * @param SDCardMedia
     */
    private void unSelectImage(SDCardMedia SDCardMedia) {
        mSelectSDCardMedias.remove(SDCardMedia);
        if (mSelectListener != null) {
            mSelectListener.OnImageSelect(SDCardMedia, false, mSelectSDCardMedias.size());
        }
    }

    @Override
    public int getItemCount() {
        return mSDCardMedias == null ? 0 : mSDCardMedias.size();
    }

    public ArrayList<SDCardMedia> getData() {
        return mSDCardMedias;
    }

    public void refresh(ArrayList<SDCardMedia> data) {
        mSDCardMedias = data;
        notifyDataSetChanged();
    }

    /**
     * 设置图片选中和未选中的效果
     */
    private void setItemSelect(ViewHolder holder, boolean isSelect) {
        if (isSelect) {
            holder.grid_item_check.setVisibility(View.VISIBLE);
        } else {
            holder.grid_item_check.setVisibility(View.GONE);
        }
    }

    private void clearImageSelect() {
        if (mSDCardMedias != null && mSelectSDCardMedias.size() == 1) {
            int index = mSDCardMedias.indexOf(mSelectSDCardMedias.get(0));
            if (index != -1) {
                mSelectSDCardMedias.clear();
                notifyItemChanged(index);
            }
        }
    }

    public ArrayList<SDCardMedia> getSelectImages() {
        return mSelectSDCardMedias;
    }

    public void setOnImageSelectListener(OnImageSelectListener listener) {
        this.mSelectListener = listener;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mItemClickListener = listener;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView grid_item;
        ImageView grid_item_check;
        ImageView grid_video;

        public ViewHolder(View itemView) {
            super(itemView);
            grid_item = (ImageView) itemView.findViewById(R.id.grid_item);
            grid_item_check = (ImageView) itemView.findViewById(R.id.grid_item_check);
            grid_video = (ImageView) itemView.findViewById(R.id.grid_video);
        }
    }

    public interface OnImageSelectListener {
        void OnImageSelect(SDCardMedia SDCardMedia, boolean isSelect, int selectCount);
    }

    public interface OnItemClickListener {
        void OnItemClick(SDCardMedia SDCardMedia, int position);
    }
}
