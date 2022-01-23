package com.bcnetech.hyphoto.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bcnetech.bcnetechlibrary.view.BaseRelativeLayout;
import com.bcnetech.bizcamerlibrary.camera.data.CameraStatus;
import com.bcnetech.hyphoto.R;

/**
 * Created by a1234 on 2017/12/4.
 */

public class RecordSelectLayout extends BaseRelativeLayout {
    private TextView tv_selecttime;
    private SeekBar record_seektime;
    private ImageView pop_select;
    private SelectListener selectListener;
    private int currentTime = 1;

    public RecordSelectLayout(Context context) {
        super(context);
    }

    public RecordSelectLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RecordSelectLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void initView() {
        super.initView();
        inflate(getContext(), R.layout.recodertime_select_layout, this);
        tv_selecttime = (TextView) findViewById(R.id.tv_selecttime);
        record_seektime = (SeekBar) findViewById(R.id.record_seektime);
        pop_select = (ImageView) findViewById(R.id.pop_select);
    }

    @Override
    protected void initData() {
        super.initData();
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                selectListener.onSelect(onClickEd());
            }
        });
    }

    public boolean DisSelect() {
        pop_select.setVisibility(INVISIBLE);
        tv_selecttime.setTextColor(getContext().getResources().getColor(R.color.text_color));
        return false;
    }

    public boolean onClickEd() {
        pop_select.setVisibility(VISIBLE);
        tv_selecttime.setTextColor(getContext().getResources().getColor(R.color.sing_in_color));
        return true;
    }

    public void setCostom() {
        CameraStatus cameraStatus = CameraStatus.getCameraStatus();
        int time = cameraStatus.getCostomRecordTime();
        if (time!=-1) {
            //onClickEd();
            record_seektime.setProgress(time * 100 / CameraStatus.MAXTIME + 1);
        }
    }


    @Override
    protected void onViewClick() {
        super.onViewClick();
        record_seektime.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                currentTime = CameraStatus.MAXTIME * progress / 100;
                if (currentTime==0){
                    currentTime=1;
                }
                tv_selecttime.setText(currentTime + "s");
                selectListener.onSeek(currentTime);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    public interface SelectListener {
        void onSeek(int time);

        void onSelect(boolean isSelect);
    }

    public void setSelectListener(SelectListener selectListener) {
        this.selectListener = selectListener;
    }
}
