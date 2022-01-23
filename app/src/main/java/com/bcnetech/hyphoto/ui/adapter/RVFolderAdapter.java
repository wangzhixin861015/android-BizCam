package com.bcnetech.hyphoto.ui.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bcnetech.bcnetechlibrary.util.ContentUtil;
import com.bcnetech.hyphoto.R;
import com.bcnetech.hyphoto.data.Folder;
import com.bumptech.glide.Glide;

import java.util.ArrayList;


/**
 * 导入界面的封面选择Adapter
 */
public class RVFolderAdapter extends RecyclerView.Adapter<RVFolderAdapter.ViewHolder> {
    private ArrayList<Folder> mFolders;
    private Activity activity;
    private LayoutInflater mInflater;
    private int w;
    private ItemClickListener mItemClickListener;
    private int clickPosition = -1;
    private int brforeposition = -1;


    public RVFolderAdapter(Activity activity) {
        this.activity = activity;
        mInflater = LayoutInflater.from(activity);
        this.w = ContentUtil.getScreenWidth(activity) / 3;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.title_item_folder, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.itemView.setTag(position);
        holder.tv_title.setText(mFolders.get(position).getName());
        Folder folder = mFolders.get(position);
        if (folder.getSDCardMedias().size() > 0) {
            String url = folder.getSDCardMedias().get(0).getThumb_path();
            if (mFolders.get(position).getName().equals(holder.iv_preparm.getTag())) {
            } else {
                Glide.with(activity).load(url).into(holder.iv_preparm);
                holder.iv_check.setTag(mFolders.get(position).getName());
            }

        } else {
            holder.iv_preparm.setImageDrawable(activity.getResources().getDrawable(R.drawable.loading));
        }


        if (clickPosition != -1) {
            if (position == clickPosition) {
                brforeposition = position;
                holder.tv_title.setTextColor(Color.parseColor("#0057FF"));
                holder.iv_check.setVisibility(View.VISIBLE);
            } else {
                holder.tv_title.setTextColor(Color.parseColor("#ADB5C2"));
                holder.iv_check.setVisibility(View.INVISIBLE);
            }
        } else {
            //初始值将第一个设置为点击状态
            if (position == 0) {
                brforeposition = position;
                holder.tv_title.setTextColor(Color.parseColor("#0057FF"));
                holder.iv_check.setVisibility(View.VISIBLE);
            } else {
                holder.tv_title.setTextColor(Color.parseColor("#ADB5C2"));
                holder.iv_check.setVisibility(View.INVISIBLE);
            }
        }
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView iv_preparm;
        public ImageView iv_check;
        public TextView tv_title;

        public ViewHolder(View view) {
            super(view);
            iv_preparm = (ImageView) view.findViewById(R.id.iv_preparm);
            iv_check = (ImageView)view.findViewById(R.id.iv_check);
            tv_title = (TextView) view.findViewById(R.id.tv_title);
            iv_preparm.setVisibility(View.VISIBLE);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            clickPosition = (Integer) itemView.getTag();
            if (mItemClickListener != null) {
                mItemClickListener.OnItemClick(v, (Integer) itemView.getTag());
            }
            // notifyDataSetChanged();
            notifyItemChanged(clickPosition);
            notifyItemChanged(brforeposition);
        }
    }


    public void refresh(ArrayList<Folder> data) {
        mFolders = data;
        notifyDataSetChanged();
    }

    public interface ItemClickListener {
        void OnItemClick(View v, int position);
    }


    public void setOnItemClickListener(ItemClickListener itemClickListener) {
        mItemClickListener = itemClickListener;
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mFolders == null ? 0 : mFolders.size();
    }


}

