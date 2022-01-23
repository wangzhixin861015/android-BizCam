package com.bcnetech.hyphoto.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;

import com.bcnetech.bcnetechlibrary.view.BaseRelativeLayout;
import com.bcnetech.hyphoto.R;

/**
 * Created by yhf on 2017/10/16.
 */

public class ImageNewUtilsTopView extends BaseRelativeLayout {
    private ImageView iv_close;
    private TextView tv_share;
    private TextView iv_upload;
    private ImageView iv_info;
    private ImageView iv_delete;

    public ImageNewUtilsTopView(Context context) {
        super(context);
    }

    public ImageNewUtilsTopView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ImageNewUtilsTopView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void initData() {
        super.initData();
        tv_share.setEnabled(false);
        iv_upload.setEnabled(false);

    }

    @Override
    protected void initView() {
        super.initView();
        inflate(getContext(), R.layout.album_new_top_select_view,this);
        iv_close= (ImageView) findViewById(R.id.iv_close);
        tv_share= (TextView) findViewById(R.id.tv_share);
        tv_share.setVisibility(INVISIBLE);
        iv_upload= (TextView) findViewById(R.id.iv_upload);
        iv_upload.setVisibility(GONE);
        iv_info= (ImageView) findViewById(R.id.iv_info);
        iv_info.setVisibility(VISIBLE);
        iv_delete= (ImageView) findViewById(R.id.iv_delete);
        iv_delete.setVisibility(VISIBLE);
    }

    @Override
    protected void onViewClick() {
        super.onViewClick();
    }

    /**
     * 关闭
     * @param listener
     */
    public void close(OnClickListener listener){
        iv_close.setOnClickListener(listener);
    }

    /**
     * 删除
     * @param listener
     */
    public void delete(OnClickListener listener){
        iv_delete.setOnClickListener(listener);
    }

    /**
     * 详情
     * @param listener
     */
    public void info(OnClickListener listener){
        iv_info.setOnClickListener(listener);
    }

    public void setClickEnabled(boolean enabled){
        iv_info.setEnabled(enabled);
        iv_delete.setEnabled(enabled);
    }




}
