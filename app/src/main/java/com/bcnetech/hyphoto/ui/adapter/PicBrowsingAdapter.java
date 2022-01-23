package com.bcnetech.hyphoto.ui.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.bcnetech.bcnetchhttp.config.Flag;
import com.bcnetech.hyphoto.data.GridItem;
import com.bcnetech.hyphoto.listener.BizFocusViewGestureListener;
import com.bcnetech.hyphoto.ui.view.ScaleImageView;
import com.bcnetech.hyphoto.ui.view.ViewPagerVideoView;
import com.bcnetech.hyphoto.ui.view.ZoomableViewGroup;
import com.bcnetech.hyphoto.utils.ImageUtil;
import com.bcnetech.hyphoto.R;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wenbin on 16/8/17.
 */

public class PicBrowsingAdapter extends PagerAdapter {
    private List<GridItem> list;
    private LayoutInflater inflater;
    private Context context;
    private CloseInter closeInter;
    private ScaleImageView matrixImageView;
    private ViewPagerVideoView video_name;
    private RelativeLayout rl_main;
    public final static int WHITE=0;
    public final static int BLACK=1;
    private int type=WHITE;

    private BizFocusViewGestureListener bizFocusViewGestureListener;
    private ZoomableViewGroup zoomableViewGroup;


    private Bitmap imgbit;

    public PicBrowsingAdapter(Context context, ArrayList<GridItem> list, CloseInter closeInter) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.list = list;
        this.closeInter = closeInter;
    }

    @Override
    public int getCount() {
        return list != null ? (list.size() > 0 ? list.size() - 1 : 0) : 0;
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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public Object instantiateItem(final ViewGroup container, int position) {
        final String imgUrl = list.get(position).getPath();
        View view = inflater.inflate(R.layout.pic_browing_item, null);
        matrixImageView = (ScaleImageView) view.findViewById(R.id.pic_name);
        video_name = (ViewPagerVideoView)view.findViewById(R.id.video_name);
        rl_main= (RelativeLayout) view.findViewById(R.id.rl_main);
        zoomableViewGroup= (ZoomableViewGroup) view.findViewById(R.id.zoomableViewGroup);

        video_name.setVideoInter(new ViewPagerVideoView.VideoInter() {
            @Override
            public void onClose() {
                closeInter.close();
            }
        });





        if(type==BLACK){
            rl_main.setBackgroundColor(context.getResources().getColor(R.color.black));
        }else {
            rl_main.setBackgroundColor(context.getResources().getColor(R.color.backgroud_new));
        }
        if (list.get(position).getImageData().getType() == Flag.TYPE_VIDEO) {
            video_name.setVisibility(View.VISIBLE);
            matrixImageView.setVisibility(View.GONE);
            video_name.setImageData(list.get(position).getImageData());
        } else {
            if(type==BLACK){
                matrixImageView.setBackgroundColor(context.getResources().getColor(R.color.black));
            }else {
                if (imgUrl.contains(".png")) {
                    matrixImageView.setBackground(context.getResources().getDrawable(R.drawable.bgbitmap));
                } else {
                    matrixImageView.setBackgroundColor(context.getResources().getColor(R.color.backgroud_new));
                }
            }


            video_name.setVisibility(View.GONE);
            matrixImageView.setVisibility(View.VISIBLE);


            matrixImageView.setOnClickListener(new View.OnClickListener() {
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
            });
          if (imgUrl != null) {
                  ImageUtil.EBizImageLoad(matrixImageView,imgUrl, new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String s, View view) {

                    }

                    @Override
                    public void onLoadingFailed(String s, View view, FailReason failReason) {

                    }

                    @Override
                    public void onLoadingComplete(String s, View view, Bitmap bitmap) {
//                        if(((String)matrixImageView.getTag()).equals(imgUrl)){
                            imgbit = bitmap;

//                        }

                    }

                    @Override
                    public void onLoadingCancelled(String s, View view) {

                    }
                });
               // Glide.with(context).load(imgUrl).diskCacheStrategy(DiskCacheStrategy.RESULT).override(ContentUtil.getScreenWidth(context),ContentUtil.getScreenHeight(context)).dontAnimate().into(matrixImageView);
            }
        }
        matrixImageView.setTag(imgUrl);
//        matrixImageView.setOnMovingListener(new MatrixImageView.OnMovingListener() {
//            @Override
//            public void startDrag() {
//                closeInter.startDrag();
//            }
//
//            @Override
//            public void stopDrag() {
//                closeInter.stopDrag();
//            }
//        });
//
//        matrixImageView.setZoomImageViewListeenr(new MatrixImageView.ZoomImageViewListener() {
//            @Override
//            public void onSingletap() {
//                if (closeInter != null) {
//                    closeInter.close();
//                }
//            }
//        });
        container.addView(view);

        return view;
    }

    public void resetVideo(){
        video_name.onViewPagerVideoViewGone();
    }


    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }


    public void setData(List<GridItem> list) {
        this.list = list;
    }

    public Bitmap getCurrentBitmap(){
        return this.imgbit;
    }

    public interface CloseInter {
        void close();

        void startDrag();

        void stopDrag();

        void switchType(int type);
    }

    public List<GridItem> returnlist(){
        return this.list;
    }
}
