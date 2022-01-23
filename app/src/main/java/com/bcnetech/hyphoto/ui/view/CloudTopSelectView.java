package com.bcnetech.hyphoto.ui.view;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import com.bcnetech.bcnetechlibrary.view.BaseRelativeLayout;
import com.bcnetech.hyphoto.R;
import com.bcnetech.hyphoto.databinding.CloudNewTopSelectViewBinding;

/**
 * author: wsBai
 * date: 2018/12/7
 */
public class CloudTopSelectView extends BaseRelativeLayout {
private CloudNewTopSelectViewBinding cloudNewTopSelectViewBinding;
    public CloudTopSelectView(Context context) {
        super(context);
    }

    public CloudTopSelectView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CloudTopSelectView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void initView() {
        super.initView();
        cloudNewTopSelectViewBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.cloud_new_top_select_view, this, true);
    }

    @Override
    protected void initData() {
        super.initData();
    }

    @Override
    protected void onViewClick() {
    }
    /**
     * 关闭
     * @param listener
     */
    public void close(OnClickListener listener){
        cloudNewTopSelectViewBinding.ivClose.setOnClickListener(listener);
    }

    /**
     * info
     * @param listener
     */
    public void more(OnClickListener listener){
        cloudNewTopSelectViewBinding.ivOther.setOnClickListener(listener);
    }

}
