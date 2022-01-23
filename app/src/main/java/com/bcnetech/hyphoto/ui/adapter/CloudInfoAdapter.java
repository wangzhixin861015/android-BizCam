package com.bcnetech.hyphoto.ui.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bcnetech.bcnetchhttp.bean.response.CloudPicData;
import com.bcnetech.bcnetechlibrary.util.ContentUtil;
import com.bcnetech.hyphoto.task.SaveVideoPicTask;
import com.bcnetech.hyphoto.utils.BitmapUtils;
import com.bcnetech.hyphoto.utils.StringUtil;
import com.bcnetech.hyphoto.R;
import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

/**
 * Created by wenbin on 16/6/30.
 */
public class CloudInfoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<CloudPicData> listdata;
    private Activity activity;
    private LayoutInflater mInflater;
    private int width;
    private int hight;
    private MediaPlayer mediaPlayer;
    private String url;

    //大图
    final int TYPE_1 = 0;
    //小图
    final int TYPE_2 = 1;

    private ItemClickInterface itemClickInterface;
    private Handler mhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            notifyDataSetChanged();
        }
    };


    public CloudInfoAdapter(Activity activity, List<CloudPicData> listdata) {
        this.activity = activity;
        this.listdata = listdata;
        mInflater = LayoutInflater.from(activity);
        width = hight = (ContentUtil.getScreenWidth(activity) - ContentUtil.dip2px(activity, 3)) / 3;
        mediaPlayer = new MediaPlayer();

    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_1) {
            View view = mInflater.inflate(R.layout.grid_item, parent, false);
            return new ViewHolder(view);
        } else {
            View view = mInflater.inflate(R.layout.adapter_images_item, parent, false);
            return new SmallPicViewHolder(view);
        }


    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        CloudPicData cloudPicData = listdata.get(position);
//        int h = calculateHeight(cloudPicData);
        if (holder instanceof ViewHolder) {
            ((ViewHolder) holder).grid_item.setLayoutParams(new RelativeLayout.LayoutParams(width, hight));
            ((ViewHolder) holder).grid_item.getLayoutParams().height = hight;
            ((ViewHolder) holder).grid_item.getLayoutParams().width = width;
            ((ViewHolder) holder).grid_item_check.getLayoutParams().height = hight;
            ((ViewHolder) holder).grid_item_check.getLayoutParams().width = width;
            ((ViewHolder) holder).view.setLayoutParams(new AbsListView.LayoutParams(width, hight));
            url = BitmapUtils.getBitmapUrl6(cloudPicData.getFileId());
            if (cloudPicData.getFormat().contains("video") || cloudPicData.getFormat().contains("mp4")) {
                if (cloudPicData.getCoverId() != null && !cloudPicData.getCoverId().isEmpty()) {
                    String url = BitmapUtils.getBitmapUrl6(cloudPicData.getCoverId());
                    Glide.with(activity).load(StringUtil.getSizeUrl(url, width, hight)).into(((ViewHolder) holder).grid_item);
                } else {
                    url = BitmapUtils.getBitmapUrl6(cloudPicData.getFileId());
                    SaveVideoPicTask saveVideoPicTask = new SaveVideoPicTask(url, width, hight, new SaveVideoPicTask.SaveInterface() {
                        @Override
                        public void saveEnd(Bitmap bitmap) {
                            ((ViewHolder) holder).grid_item.setImageBitmap(bitmap);
                        }
                    });
                    saveVideoPicTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
                ((ViewHolder) holder).grid_video.setVisibility(View.VISIBLE);
                ((ViewHolder) holder).grid_video.setImageResource(R.drawable.video);
            }else if(cloudPicData.getFormat().contains("25d/25d")) {
                String url = BitmapUtils.getBitmapUrl6(cloudPicData.getCoverId());
                Glide.with(activity).load(StringUtil.getSizeUrl(url, width, hight)).into(((ViewHolder) holder).grid_item);
                ((ViewHolder) holder).grid_video.setVisibility(View.VISIBLE);
                ((ViewHolder) holder).grid_video.setImageResource(R.drawable.ai_360);
            } else {
                ((ViewHolder) holder).grid_video.setVisibility(View.GONE);
               /* ImageUtil.EBizImageLoad(((ViewHolder) holder).grid_item, StringUtil.getSizeUrl(url, width, hight), width, hight, new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String s, View view) {
                    }

                    @Override
                    public void onLoadingFailed(String s, View view, FailReason failReason) {
                    }

                    @Override
                    public void onLoadingComplete(String s, View view, Bitmap bitmap) {

                    }

                    @Override
                    public void onLoadingCancelled(String s, View view) {
                    }

                });*/
                Glide.with(activity).load(StringUtil.getSizeUrl(url, width, hight)).into(((ViewHolder) holder).grid_item);
            }
            ((ViewHolder) holder).view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemClickInterface != null) {
                        itemClickInterface.itemClick(v, position);
                    }
                }
            });
        } else if (holder instanceof SmallPicViewHolder) {
            if (cloudPicData.getFormat().contains("video") || cloudPicData.getFormat().contains("mp4")) {
                if (cloudPicData.getCoverId() != null && !cloudPicData.getCoverId().isEmpty()) {
                    String url = BitmapUtils.getBitmapUrl6(cloudPicData.getCoverId());
                    Picasso.get().load(StringUtil.getSizeUrl(url, width, hight)).resize(width, hight).centerInside().into(((SmallPicViewHolder) holder).grid_item);
                } else {
                    url = BitmapUtils.getBitmapUrl6(cloudPicData.getFileId());
                    SaveVideoPicTask saveVideoPicTask = new SaveVideoPicTask(url, width, hight, new SaveVideoPicTask.SaveInterface() {
                        @Override
                        public void saveEnd(Bitmap bitmap) {
                            ((SmallPicViewHolder) holder).grid_item.setImageBitmap(bitmap);
                        }
                    });
                    saveVideoPicTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
                ((SmallPicViewHolder) holder).grid_video.setVisibility(View.VISIBLE);
                ((SmallPicViewHolder) holder).grid_video.setImageResource(R.drawable.video);

            } else if(cloudPicData.getFormat().equals("25d/25d")){
                String url = BitmapUtils.getBitmapUrl6(cloudPicData.getCoverId());
                Glide.with(activity).load(StringUtil.getSizeUrl(url, width, hight)).into(((SmallPicViewHolder) holder).grid_item);
                ((SmallPicViewHolder) holder).grid_video.setVisibility(View.VISIBLE);
                ((SmallPicViewHolder) holder).grid_video.setImageResource(R.drawable.ai_360);

            } else {
                ((SmallPicViewHolder) holder).grid_video.setVisibility(View.GONE);
                String url = BitmapUtils.getBitmapUrl6(cloudPicData.getFileId());
               /* ImageUtil.EBizImageLoad(((SmallPicViewHolder) holder).grid_item, StringUtil.getSizeUrl(url, width, hight), width, hight, new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String s, View view) {
                    }

                    @Override
                    public void onLoadingFailed(String s, View view, FailReason failReason) {
                    }

                    @Override
                    public void onLoadingComplete(String s, View view, Bitmap bitmap) {

                    }

                    @Override
                    public void onLoadingCancelled(String s, View view) {
                    }
                });*/
                Picasso.get().load(StringUtil.getSizeUrl(url, width, hight)).resize(width, hight).centerInside().into(((SmallPicViewHolder) holder).grid_item);
            }
            if (cloudPicData.isClick()) {
                ((SmallPicViewHolder) holder).grid_item_check.setVisibility(View.VISIBLE);
            } else {
                ((SmallPicViewHolder) holder).grid_item_check.setVisibility(View.GONE);
            }
            ((SmallPicViewHolder) holder).view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemClickInterface != null) {
                        itemClickInterface.itemClick(v, position);
                    }
                }
            });
        }


    }

    @Override
    public int getItemViewType(int position) {
        if (listdata.get(position).getType().equals("0")) {
            return TYPE_1;
        } else {
            return TYPE_2;
        }

    }

    @Override
    public int getItemCount() {
        return listdata == null ? 0 : listdata.size();
    }

    public void setData(List list) {
        this.listdata = list;
        this.notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public View view;
        public ImageView grid_item_check;
        public ImageView grid_item;
        public ImageView grid_video;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            grid_item_check = (ImageView) view.findViewById(R.id.grid_item_check);
            grid_item = (ImageView) view.findViewById(R.id.grid_item);
            grid_video = (ImageView) view.findViewById(R.id.grid_video);

        }
    }

    public class SmallPicViewHolder extends RecyclerView.ViewHolder {
        public View view;
        public ImageView grid_item;
        public ImageView grid_item_check;
        public ImageView grid_video;

        public SmallPicViewHolder(View view) {
            super(view);
            this.view = view;
            grid_item_check = (ImageView) view.findViewById(R.id.grid_item_check);
            grid_item = (ImageView) view.findViewById(R.id.grid_item);
            grid_video = (ImageView) view.findViewById(R.id.grid_video);

        }
    }


    public interface ItemClickInterface {
        void itemClick(View view, int position);
    }

    public ItemClickInterface getItemClickInterface() {
        return itemClickInterface;
    }

    public void setItemClickInterface(ItemClickInterface itemClickInterface) {
        this.itemClickInterface = itemClickInterface;
    }


    private Bitmap createVideoThumbnail(String url, int width, int height) {
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        int kind = MediaStore.Video.Thumbnails.MINI_KIND;
        try {
            if (Build.VERSION.SDK_INT >= 14) {
                retriever.setDataSource(url, new HashMap<String, String>());
            } else {
                retriever.setDataSource(url);
            }
            bitmap = retriever.getFrameAtTime();
        } catch (IllegalArgumentException ex) {
            // Assume this is a corrupt video file
        } catch (RuntimeException ex) {
            // Assume this is a corrupt video file.
        } finally {
            try {
                retriever.release();
            } catch (RuntimeException ex) {
                // Ignore failures while cleaning up.
            }
        }
        if (kind == MediaStore.Images.Thumbnails.MICRO_KIND && bitmap != null) {
            bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
                    ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        }
        retriever.release();
        return bitmap;
    }
}
