package com.bcnetech.hyphoto.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bcnetech.bcnetechlibrary.view.BaseRelativeLayout;
import com.bcnetech.hyphoto.R;

/**
 * Created by a1234 on 16/8/29.
 */

public class VideoBottomView extends BaseRelativeLayout {
    private SeekBar video_seekBar;
    private TextView startTime;
    private TextView endTime;
    private ImageView videoButton;
    private VideoBottomListener videoBottomListener;
    //private boolean isPlaying = false;

    public VideoBottomView(Context context) {
        super(context);
    }

    public VideoBottomView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VideoBottomView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void initView() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.video_bottom_layout, null);
        video_seekBar = (SeekBar) view.findViewById(R.id.video_seekBar);
        startTime = (TextView) view.findViewById(R.id.startTime);
        endTime = (TextView) view.findViewById(R.id.endTime);
        videoButton = (ImageView) view.findViewById(R.id.videoButton);
        VideoBottomView.this.addView(view);
    }


    @Override
    protected void initData() {
        super.initData();
    }

    @Override
    protected void onViewClick() {
        super.onViewClick();
        videoButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                videoBottomListener.onPlayClicked();
            }
        });
        video_seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                String currentProgress = ShowTime(progress);
                startTime.setText(currentProgress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                videoBottomListener.onseekbarTracking(true,-1);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                videoBottomListener.onseekbarTracking(false,seekBar.getProgress());
            }
        });
    }

    public void showVideoButton(boolean isshow){
        if (isshow){
            videoButton.setVisibility(VISIBLE);
        }else{
            videoButton.setVisibility(GONE);
        }
    }

    private void setButtonStatus(boolean isPlaying) {
      if (isPlaying) {
          setStatus(true);
      } else {
          setStatus(false);
      }
  }

    private void setStatus(boolean b) {
        if (b) {
            videoButton.setImageDrawable(getResources().getDrawable(R.drawable.play));
        } else {
            videoButton.setImageDrawable(getResources().getDrawable(R.drawable.stop));
        }
    }

    public void setSeekbarMax(int number) {
        video_seekBar.setMax(number);
    }

    public void setSeekbarProgress(int num) {
        video_seekBar.setProgress(num);
    }

    public interface VideoBottomListener {
        void onseekbarTracking(boolean istracking,int progress);

        void onPlayClicked();
    }

    public void setVideoStatus(boolean b) {
        setButtonStatus(b);
    }

    public void setVideoBottomListener(VideoBottomListener videoBottomListener) {
        this.videoBottomListener = videoBottomListener;
    }


    //时间显示函数,我们获得音乐信息的是以毫秒为单位的，把把转换成我们熟悉的00:00格式
    public String ShowTime(double time) {
        time /= 1000;
        time = Math.round(time);
        double minute = time / 60;
        double hour = minute / 60;
        double second = time % 60;
        minute %= 60;
        int min = (int)minute;
        int sec = (int )second;
        return String.format("%02d:%02d", min, sec);
    }

    public void setDuration(long duration) {
        if (duration != 0) {
                endTime.setText(ShowTime(duration));
            }
        }
}