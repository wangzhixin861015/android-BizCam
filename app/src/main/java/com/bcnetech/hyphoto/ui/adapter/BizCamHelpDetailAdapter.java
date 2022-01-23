package com.bcnetech.hyphoto.ui.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bcnetech.bcnetchhttp.config.Flag;
import com.bcnetech.bcnetechlibrary.BcnetechAppInstance;
import com.bcnetech.bcnetechlibrary.util.ContentUtil;
import com.bcnetech.hyphoto.R;
import com.bcnetech.hyphoto.data.BizCamHelpData;
import com.bcnetech.hyphoto.ui.view.videoplayer.view.MVideoPlayer;

import java.util.List;

import static android.widget.RelativeLayout.CENTER_HORIZONTAL;

/**
 * Created by yhf on 18/12/11.
 */

public class BizCamHelpDetailAdapter extends PagerAdapter {

    private List<BizCamHelpData> list;
    private LayoutInflater inflater;
    private Context context;
    private boolean isFirst=false;
//    private VideoView video_name;
//    private TextView tv_name;
//    private TextView tv_context;

    //记录之前播放的条目下标
    public  int currentPosition = -1;


    public BizCamHelpDetailAdapter(Context context, List<BizCamHelpData> list) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.list = list;
    }

    public boolean isFirst() {
        return isFirst;
    }

    public void setFirst(boolean first) {
        isFirst = first;
    }

    @Override
    public int getCount() {
        return list != null ? (list.size() > 0 ? list.size() : 0) : 0;
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
    public Object instantiateItem( ViewGroup container, final int position) {

        final BizCamHelpData bizCamHelpData = list.get(position);
        View view = LayoutInflater.from(context).inflate(R.layout.bizcam_help_detail_item,container, false);
        final MVideoPlayer video = view.findViewById(R.id.video_view);
        TextView tv_context = view.findViewById(R.id.tv_context);
        TextView tv_name = view.findViewById(R.id.tv_name);
//        final ImageView video_cover = view.findViewById(R.id.video_cover);
//        final ImageView video_state = view.findViewById(R.id.video_state);
        RelativeLayout rl_video = view.findViewById(R.id.rl_video);

        float surfaceViewWidth = ContentUtil.getScreenWidth(context) - ContentUtil.dip2px(context, 74) * 2;
        float surfaceViewHight = surfaceViewWidth * 16 / 9;

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams((int) surfaceViewWidth, (int) surfaceViewHight);
        params.addRule(CENTER_HORIZONTAL);
        rl_video.setLayoutParams(params);

//        video_cover.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String uri;
//                if (list.get(position).getType() == Flag.BIZCAM_HELP_AI_COBOX) {
//                    uri = "android.resource://" + BcnetechAppInstance.getAppProcessName(context) + "/" + R.raw.ai_cobox_batch;
//                    video.setVideoURI(Uri.parse(uri));
//                } else if (list.get(position).getType() == Flag.BIZCAM_HELP_AI_COLINK) {
//                    uri = "android.resource://" + BcnetechAppInstance.getAppProcessName(context) + "/" + R.raw.ai_colink_batch;
//                    video.setVideoURI(Uri.parse(uri));
//                } else if (list.get(position).getType() == Flag.BIZCAM_HELP_PAINT) {
//                    uri = "android.resource://" + BcnetechAppInstance.getAppProcessName(context) + "/" + R.raw.paint_batch;
//                    video.setVideoURI(Uri.parse(uri));
//                } else if (list.get(position).getType() == Flag.BIZCAM_HELP_MATTING) {
//                    uri = "android.resource://" + BcnetechAppInstance.getAppProcessName(context) + "/" + R.raw.matting_batch;
//                    video.setVideoURI(Uri.parse(uri));
//                } else if (list.get(position).getType() == Flag.BIZCAM_HELP_REPAIR) {
//                    uri = "android.resource://" + BcnetechAppInstance.getAppProcessName(context) + "/" + R.raw.repair_batch;
//                    video.setVideoURI(Uri.parse(uri));
//                }


//                video.start();
//            }
//        });
//        video_state.setImageResource(R.drawable.video);

        rl_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                video.stopPlay();
            }
        });
//
//        video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//            @Override
//            public void onPrepared(MediaPlayer mp) {
//                mp.setOnInfoListener(new MediaPlayer.OnInfoListener() {
//                    @Override
//                    public boolean onInfo(MediaPlayer mp, int what, int extra) {
//                        if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START)
//                            video_state.setVisibility(View.GONE);
//                            video_cover.setVisibility(View.GONE);
//                        return true;
//                    }
//                });
//            }
//        });
//
//        video.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//            @Override
//            public void onCompletion(MediaPlayer mp) {
//                mp.start();
//
//            }
//        });

        String uri = "";
        if (bizCamHelpData.getType() == Flag.BIZCAM_HELP_AI_COBOX) {
            uri = "android.resource://" + BcnetechAppInstance.getAppProcessName(context) + "/" + R.raw.ai_cobox_batch;

            tv_name.setText("商拍酷宝-智拍");
            tv_context.setText("蓝牙酷宝 一键出图");
        } else if (bizCamHelpData.getType() == Flag.BIZCAM_HELP_AI_COLINK) {
            uri = "android.resource://" + BcnetechAppInstance.getAppProcessName(context) + "/" + R.raw.ai_colink_batch;

            tv_name.setText("商拍魔盒-智拍");
            tv_context.setText("蓝牙魔盒 一键出图");
        } else if (bizCamHelpData.getType() == Flag.BIZCAM_HELP_PAINT) {
            uri = "android.resource://" + BcnetechAppInstance.getAppProcessName(context) + "/" + R.raw.paint_batch;

            tv_name.setText("曝光饱和调整");
            tv_context.setText("曝光饱和，一步到位");
        } else if (bizCamHelpData.getType() == Flag.BIZCAM_HELP_MATTING) {
            uri = "android.resource://" + BcnetechAppInstance.getAppProcessName(context) + "/" + R.raw.matting_batch;

            tv_name.setText("加减抠图法");
            tv_context.setText("加减抠图，替换背景");
        } else if (bizCamHelpData.getType() == Flag.BIZCAM_HELP_REPAIR) {
            uri = "android.resource://" + BcnetechAppInstance.getAppProcessName(context) + "/" + R.raw.repair_batch;

            tv_name.setText("修复");
            tv_context.setText("消除瑕疵，完美出图");
        }
//        //获取封面
//        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
//        mmr.setDataSource(context, Uri.parse(uri));
//        Bitmap bitmap = mmr.getFrameAtTime(500);//获取第一帧图片
//        video_cover.setImageBitmap(bitmap);
//        mmr.release();//释放资源

        video.setTag(position);
        video.setUri(Uri.parse(uri));
        video.mediaController.setPosition(position);
        video.mediaController.setAdapter(this);

        //设置为初始化状态
        video.initViewDisplay(Uri.parse(uri));
//
//        if(position == currentPosition){
//
//        }

        if(isFirst){
            video.playFirst();
            isFirst=false;
        }

        container.addView(view);
        return view;
    }


    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }


    public void setData(List<BizCamHelpData> list) {
        this.list = list;
    }


    public List<BizCamHelpData> returnlist() {
        return this.list;
    }

    public int getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(int currentPosition) {
        this.currentPosition = currentPosition;
    }
}
