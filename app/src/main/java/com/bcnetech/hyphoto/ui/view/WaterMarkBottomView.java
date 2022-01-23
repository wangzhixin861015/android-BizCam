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

public class WaterMarkBottomView extends BaseRelativeLayout {
    private WaterMarkData currentWmData = null;
    RelativeLayout watermark_bottom_content;
    ImageView iv_watermark_select;
    ImageView iv_watermark_image;
    ImageView tv_watermark_add;
    private boolean isAddType = false;
    private boolean isSelect = false;
    private WaterMarkBottomListener waterMarkBottomListener;

    public WaterMarkBottomView(Context context) {
        super(context);
    }

    public WaterMarkBottomView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WaterMarkBottomView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void initView() {
        super.initView();
        inflate(getContext(), R.layout.watermark_bottom_layout, this);
        watermark_bottom_content = (RelativeLayout) findViewById(R.id.watermark_bottom_content);
        iv_watermark_select = (ImageView) findViewById(R.id.iv_watermark_select);
        iv_watermark_image = (ImageView) findViewById(R.id.iv_watermark_image);
        tv_watermark_add = (ImageView) findViewById(R.id.tv_watermark_add);
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
            iv_watermark_image.setImageDrawable(waterMarkData.getDrawable());

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
                if (isAddType) {
                    tv_watermark_add.setVisibility(VISIBLE);
                    waterMarkBottomListener.onselect(null);
                } else {
                    tv_watermark_add.setVisibility(GONE);
                    if (!isSelect) {
                        isSelect = true;
                        iv_watermark_select.setVisibility(VISIBLE);
                    } else {
                        isSelect = false;
                        iv_watermark_select.setVisibility(GONE);
                    }
                    waterMarkBottomListener.onselect(currentWmData);
                }
            }
        });
        watermark_bottom_content.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (isAddType) {
                    tv_watermark_add.setVisibility(VISIBLE);
                } else {
                    tv_watermark_add.setVisibility(GONE);
                    waterMarkBottomListener.onLongClick(currentWmData);
                }
                return true;
            }
        });
    }

    public void setSelect() {
        if (currentWmData != null) {
            if (!isSelect) {
                isSelect = true;
                iv_watermark_select.setVisibility(VISIBLE);
            } else {
                isSelect = false;
                iv_watermark_select.setVisibility(GONE);
            }
        }
    }

    public void setSelect(boolean isSelect) {
        if (currentWmData != null) {
            this.isSelect = isSelect;
            if (!isSelect) {
                iv_watermark_select.setVisibility(GONE);
            } else {
                iv_watermark_select.setVisibility(VISIBLE);
            }
        }
    }

    public void setIsAddType(boolean isAddType) {
        this.isAddType = isAddType;
        if (isAddType) {
            iv_watermark_image.setVisibility(GONE);
            tv_watermark_add.setVisibility(VISIBLE);
        } else {
            iv_watermark_image.setVisibility(VISIBLE);
            tv_watermark_add.setVisibility(GONE);
        }
    }

    public interface WaterMarkBottomListener {
        void onselect(WaterMarkData waterMarkData);

        void onLongClick(WaterMarkData waterMarkData);
    }

    public void setWaterMarkBottomListener(WaterMarkBottomListener waterMarkBottomListener) {
        this.waterMarkBottomListener = waterMarkBottomListener;
    }
}
