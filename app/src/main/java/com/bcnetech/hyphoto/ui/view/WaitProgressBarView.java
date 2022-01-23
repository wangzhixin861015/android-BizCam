package com.bcnetech.hyphoto.ui.view;

import android.content.Context;
import android.util.AttributeSet;

import com.bcnetech.bcnetechlibrary.view.BaseRelativeLayout;
import com.bcnetech.hyphoto.R;

/**
 * Created by wenbin on 2016/12/13.
 */

public class WaitProgressBarView extends BaseRelativeLayout {
    public WaitProgressBarView(Context context) {
        super(context);
    }

    public WaitProgressBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WaitProgressBarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void initView() {
        super.initView();
        inflate(getContext(), R.layout.wait_progressbar_view,this);
    }

    @Override
    protected void initData() {
        super.initData();
    }

    @Override
    protected void onViewClick() {
        super.onViewClick();
    }


}
