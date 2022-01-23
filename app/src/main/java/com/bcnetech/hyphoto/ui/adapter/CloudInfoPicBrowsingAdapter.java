package com.bcnetech.hyphoto.ui.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bcnetech.bcnetchhttp.bean.response.CloudPicData;
import com.bcnetech.bcnetechlibrary.util.ContentUtil;
import com.bcnetech.bcnetechlibrary.util.LogUtil;
import com.bcnetech.hyphoto.task.SaveVideoPicTask;
import com.bcnetech.hyphoto.ui.view.photoview.PhotoView;
import com.bcnetech.hyphoto.utils.BitmapUtils;
import com.bcnetech.hyphoto.utils.StringUtil;
import com.bcnetech.hyphoto.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by wenbin on 16/8/17.
 */

public class CloudInfoPicBrowsingAdapter extends PagerAdapter {
    private List<CloudPicData> list;
    private LayoutInflater inflater;
    private Context context;
    private CloseInter closeInter;
    private PhotoView matrixImageView;
    private ImageView video_name;
    private ImageView video_icon;

    private RelativeLayout rl_main;
    public final static int WHITE = 0;
    public final static int BLACK = 1;
    private int type = WHITE;


    public CloudInfoPicBrowsingAdapter(Context context, ArrayList<CloudPicData> list, CloseInter closeInter) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.list = list;
        this.closeInter = closeInter;

    }

    @Override
    public int getCount() {
        return list != null ? list.size() : 0;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    // PagerAdapter只缓存三张要显示的图片，如果滑动的图片超出了缓存的范围，就会调用这个方法，将图片销毁
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((View) object);
    }

    @Override
    public Object instantiateItem(final ViewGroup container, int position) {
        CloudPicData cloudPicData = list.get(position);
        View view = inflater.inflate(R.layout.cloud_pic_browing_item, null);
        final String imgUrl = StringUtil.getSizeUrl(BitmapUtils.getBitmapUrl5(cloudPicData.getFileId(), cloudPicData.getFormat().substring(cloudPicData.getFormat().lastIndexOf("/") + 1)),
                ContentUtil.getScreenWidth(context),
                0);
        LogUtil.d(imgUrl);
        rl_main = (RelativeLayout) view.findViewById(R.id.rl_main);
        matrixImageView = (PhotoView) view.findViewById(R.id.pic_name);
        video_name = (ImageView) view.findViewById(R.id.video_name);
        video_icon = (ImageView) view.findViewById(R.id.video_icon);
//        ImageUtil.EBizImageLoad((ImageView) view.findViewById(R.id.pic_name),imgUrl);
/*
        if(type==BLACK){
            rl_main.setBackgroundColor(context.getResources().getColor(R.color.black));
        }else {
            rl_main.setBackgroundColor(context.getResources().getColor(R.color.backgroud_new));
        }*/
        if (!list.get(position).getFormat().contains("image")) {
//            video_name.setVisibility(View.VISIBLE);
//          final String url = BitmapUtils.getVideoUrl2(cloudPicData.getFileId());
            final String url = BitmapUtils.getDownloadFile(cloudPicData.getFileId());

            video_name.setVisibility(View.VISIBLE);
            SaveVideoPicTask saveVideoPicTask = new SaveVideoPicTask(url, 0, 0, new SaveVideoPicTask.SaveInterface() {
                @Override
                public void saveEnd(Bitmap bitmap) {
                    video_name.setImageBitmap(bitmap);
                    video_icon.bringToFront();
                }
            });
            saveVideoPicTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            video_icon.setVisibility(View.VISIBLE);
            matrixImageView.setVisibility(View.GONE);
//            video_name.setImageData(list.get(position).getImageData());
        } else {
            /*if(type==BLACK){
                matrixImageView.setBackgroundColor(context.getResources().getColor(R.color.black));
            }else {
                if (imgUrl.contains(".png")) {
                    matrixImageView.setBackground(context.getResources().getDrawable(R.drawable.bgbitmap));
                } else {
                    matrixImageView.setBackgroundColor(context.getResources().getColor(R.color.backgroud_new));
                }
            }*/

            video_name.setVisibility(View.GONE);
            matrixImageView.setVisibility(View.VISIBLE);
            video_icon.setVisibility(View.GONE);
          /*  matrixImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(type==WHITE){
                        type=BLACK;
                        matrixImageView.setBackgroundColor(context.getResources().getColor(R.color.black));
                    } else if(type==BLACK){
                        type=WHITE;
                        if (imgUrl.contains(".png")) {
                            matrixImageView.setBackground(context.getResources().getDrawable(R.drawable.bgbitmap));
                        } else {
                            matrixImageView.setBackgroundColor(context.getResources().getColor(R.color.backgroud_new));
                        }
//                        matrixImageView.setBackgroundColor(context.getResources().getColor(R.color.white));
                    }
                    closeInter.switchType(type);
                }
            });*/
            if (imgUrl != null) {

//                String url = BitmapUtils.getBitmapUrl2(cloudPicData.getFileId());
//                LogUtil.d(url);
//                ImageUtil.EBizImageLoad(matrixImageView, StringUtil.getSizeUrl(url, ContentUtil.getScreenWidth(context),
//                        0), ContentUtil.getScreenWidth(context),
//                        0, new ImageLoadingListener() {
//                    @Override
//                    public void onLoadingStarted(String s, View view) {
//                    }
//
//                    @Override
//                    public void onLoadingFailed(String s, View view, FailReason failReason) {
//                    }
//
//                    @Override
//                    public void onLoadingComplete(String s, View view, Bitmap bitmap) {
//
//                    }
//
//                    @Override
//                    public void onLoadingCancelled(String s, View view) {
//                    }
//                });
                Picasso.get().load(imgUrl).into(matrixImageView);
               /* Glide.with(context).load(imgUrl)
                        .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)  .dontAnimate().into(matrixImageView
                );*/
            }
        }
        matrixImageView.setTag(imgUrl);
        container.addView(view);

        return view;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }


    public void setData(List<CloudPicData> list) {
        this.list = list;
    }

    public interface CloseInter {
        void close();

        void switchType(int type);

    }

}
