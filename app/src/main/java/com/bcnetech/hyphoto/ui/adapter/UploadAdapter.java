package com.bcnetech.hyphoto.ui.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.bcnetech.bcnetchhttp.bean.data.DownloadInfoData;
import com.bcnetech.bcnetchhttp.bean.response.Preinstail;
import com.bcnetech.bcnetchhttp.config.Flag;
import com.bcnetech.bcnetechlibrary.util.JsonUtil;
import com.bcnetech.bcnetechlibrary.util.TimeUtil;
import com.bcnetech.hyphoto.R;
import com.bcnetech.hyphoto.data.KeyValue;
import com.bcnetech.hyphoto.data.SqlControl.DownloadInfoSqlControl;
import com.bcnetech.hyphoto.ui.view.swipemenu.SwapWrapperUtils;
import com.bcnetech.hyphoto.ui.view.swipemenu.SwipeMenuBuilder;
import com.bcnetech.hyphoto.ui.view.swipemenu.view.SwipeMenuLayout;
import com.bcnetech.hyphoto.ui.view.swipemenu.view.SwipeMenuView;
import com.bcnetech.hyphoto.utils.ImageUtil;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by yhf on 2017/4/14.
 */
public class UploadAdapter extends RecyclerView.Adapter<UploadAdapter.ViewHolder>{

    private Activity activity;
    private List<DownloadInfoData> list;
    private LayoutInflater inflater;
    private SwipeMenuBuilder swipeMenuBuilder;

    public UploadAdapter(Activity activity, List<DownloadInfoData> list) {
        this.activity = activity;
        this.list = list;
        swipeMenuBuilder = (SwipeMenuBuilder) this.activity;
        this.inflater=LayoutInflater.from(activity);
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //根据数据创建右边的操作view
        SwipeMenuView menuView = swipeMenuBuilder.create();
        //包装用户的item布局
        SwipeMenuLayout swipeMenuLayout = SwapWrapperUtils.wrap(parent, R.layout.upload_item_layout, menuView, new LinearInterpolator(), new LinearInterpolator());
        UploadAdapter.ViewHolder holder=new UploadAdapter.ViewHolder(swipeMenuLayout);
        setListener(parent, holder, viewType);
        return holder;

//        View view = inflater.inflate(R.layout.upload_item_layout, null);
//        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        DownloadInfoData downloadInfoData=list.get(position);
        if(DownloadInfoSqlControl.CLOUD_UPLOAD==downloadInfoData.getType()){
            holder.upload_content_name.setText(activity.getResources().getString(R.string.uploading));
        }else if(DownloadInfoSqlControl.CLOUD_UPLOAD_FAIL==downloadInfoData.getType()){
            holder.upload_content_name.setText(activity.getResources().getString(R.string.upload_fail));
        }
        if(downloadInfoData.getFileType()== Flag.TYPE_VIDEO||downloadInfoData.getFileType()== Flag.TYPE_AI360){
            if(downloadInfoData.getFileType()== Flag.TYPE_VIDEO){
                holder.grid_video.setImageResource(R.drawable.video);
            }else {
                holder.grid_video.setImageResource(R.drawable.ai_360);
            }
            holder.grid_video.setVisibility(View.VISIBLE);

            List<KeyValue> postParms = JsonUtil.Json2List(downloadInfoData.getPostParms(), KeyValue.class);
            String postparmsValue = "";
            for (int i = 0; i < postParms.size(); i++) {
                if (postParms.get(i).getKey().equals("params")) {
                    postparmsValue = postParms.get(i).getValue();
                    break;
                }
            }
            Preinstail preinstail = JsonUtil.Json2T(postparmsValue, Preinstail.class);

            Picasso.get().load(preinstail.getFileName()).resize(ImageUtil.Dp2Px(activity, 70), ImageUtil.Dp2Px(activity, 70)).centerInside().into(holder.upload_item_img);
        }else {
            holder.grid_video.setVisibility(View.INVISIBLE);
            Picasso.get().load(downloadInfoData.getLocalUrl()).resize(ImageUtil.Dp2Px(activity, 70), ImageUtil.Dp2Px(activity, 70)).centerInside().into(holder.upload_item_img);
        }
        holder.upload_content_time.setText(activity.getResources().getString(R.string.upload_time)+ TimeUtil.formatBeiJingTime(downloadInfoData.getUploadTime()) );
//        if(10001==downloadInfoData.getType()){
//            holder.tv_message.setText("重新上传");
//        }else if(10002==downloadInfoData.getType()){
//            holder.tv_message.setText("上传中...");
//        }
//
//        if(10001==downloadInfoData.getType()){
//            holder.ll_reUpload.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    mOnReUploadClickListener.onClick(holder,position);
//                }
//            });
//        }
    }




    public void setData(List<DownloadInfoData> list) {
        this.list = list;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        private View itemView;
        private ImageView upload_item_img;
        private TextView upload_content_name;
        private TextView upload_content_time;
        private ImageView grid_video;
//        private ImageView iv_message;
//        private TextView tv_message;
//        private LinearLayout ll_reUpload;


        public ViewHolder(View view) {
            super(view);
            this.itemView = view;
            upload_item_img= (ImageView) view.findViewById(R.id.upload_item_img);
            upload_content_name= (TextView) view.findViewById(R.id.upload_content_name);
            upload_content_time= (TextView) view.findViewById(R.id.upload_content_time);
            grid_video= (ImageView) view.findViewById(R.id.grid_video);
//            iv_message= (ImageView) view.findViewById(R.id.iv_message);
//            tv_message= (TextView) view.findViewById(R.id.tv_message);
//            ll_reUpload= (LinearLayout) view.findViewById(R.id.ll_reUpload);
        }
    }

    protected void setListener(final ViewGroup parent, final  UploadAdapter.ViewHolder viewHolder, int viewType) {
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


    }

    public OnReUploadClickListener mOnReUploadClickListener;


    public interface OnReUploadClickListener<T> {
        void onClick( RecyclerView.ViewHolder holder, int position);
    }

    public void setReUoloadClickListener(OnReUploadClickListener mOnReUploadClickListener) {
        this.mOnReUploadClickListener = mOnReUploadClickListener;
    }
}
