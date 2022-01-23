package com.bcnetech.hyphoto.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bcnetech.bcnetechlibrary.view.BaseRelativeLayout;
import com.bcnetech.hyphoto.R;
import com.bcnetech.hyphoto.data.WaterMarkData;

/**
 * Created by a1234 on 2017/12/3.
 */

public class FeedBackBottomView extends BaseRelativeLayout {
    private WaterMarkData currentWmData = null;
    RelativeLayout watermark_bottom_content;
    ImageView iv_feedback_image;
    ImageView tv_feedback_add;
    private boolean isAddType = false;
    private boolean isSelect = false;
    private WaterMarkBottomListener waterMarkBottomListener;

    public FeedBackBottomView(Context context) {
        super(context);
    }

    public FeedBackBottomView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FeedBackBottomView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void initView() {
        super.initView();
        inflate(getContext(), R.layout.feedback_bottom_layout, this);
        watermark_bottom_content = (RelativeLayout) findViewById(R.id.watermark_bottom_content);
        iv_feedback_image = (ImageView) findViewById(R.id.iv_watermark_image);
        tv_feedback_add = (ImageView) findViewById(R.id.tv_watermark_add);
    }

    @Override
    protected void initData() {
        super.initData();
    }

    public void setWaterMarkData(WaterMarkData waterMarkData) {
        if (null == waterMarkData) {
            setIsAddType(true);
        } else {
            this.currentWmData = waterMarkData;
            iv_feedback_image.setImageDrawable(waterMarkData.getDrawable());

        }
    }

    public WaterMarkData getCurrentWMD() {
        return this.currentWmData;
    }

    @Override
    protected void onViewClick() {
        watermark_bottom_content.setOnClickListener(new OnClickListener() {
          @Override
          public void onClick(View v) {
              if (isAddType){
                  tv_feedback_add.setVisibility(VISIBLE);
                  waterMarkBottomListener.onClick(null);
              }else{
                  tv_feedback_add.setVisibility(GONE);
                  if (!isSelect) {
                      isSelect = true;
                  }else{
                      isSelect = false;
                  }
                  waterMarkBottomListener.onClick(currentWmData);
              }
          }
      });
    }

    public void setSelect() {
        if (currentWmData != null) {
            if (!isSelect) {
                isSelect = true;
            } else {
                isSelect = false;
            }
        }
    }

    public void setSelect(boolean isSelect) {
        if (currentWmData != null) {
            this.isSelect = isSelect;
            if (!isSelect) {
            } else {
            }
        }
    }

    public void setIsAddType(boolean isAddType) {
        this.isAddType = isAddType;
        if (isAddType) {
            iv_feedback_image.setVisibility(GONE);
            tv_feedback_add.setVisibility(VISIBLE);
        } else {
            iv_feedback_image.setVisibility(VISIBLE);
            tv_feedback_add.setVisibility(GONE);
        }
    }

    public interface WaterMarkBottomListener {
        void onClick(WaterMarkData waterMarkData);
    }

    public void setWaterMarkBottomListener(WaterMarkBottomListener waterMarkBottomListener) {
        this.waterMarkBottomListener = waterMarkBottomListener;
    }
}
