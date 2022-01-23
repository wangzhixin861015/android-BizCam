package com.bcnetech.hyphoto.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.bcnetech.bcnetechlibrary.view.BaseRelativeLayout;
import com.bcnetech.hyphoto.R;

/**
 * Created by yhf on 2017/10/16.
 */

public class CloudInfoTopSelectView extends BaseRelativeLayout {
    private ImageView iv_close;
    private ImageView iv_upload;
    private ImageView iv_delete;

    public CloudInfoTopSelectView(Context context) {
        super(context);
    }

    public CloudInfoTopSelectView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CloudInfoTopSelectView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void initData() {
        super.initData();

    }

    @Override
    protected void initView() {
        super.initView();
        inflate(getContext(), R.layout.cloud_info_top_select_view,this);
        iv_close= (ImageView) findViewById(R.id.iv_close);
        iv_upload= (ImageView) findViewById(R.id.iv_upload);
        iv_delete= (ImageView) findViewById(R.id.iv_delete);
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
     * 下载
     * @param listener
     */
    public void download(OnClickListener listener){
        iv_upload.setOnClickListener(listener);
    }




}
