package com.bcnetech.hyphoto.ui.view;

import android.content.Context;
import android.util.AttributeSet;

import com.bcnetech.bcnetechlibrary.view.BaseRelativeLayout;
import com.bcnetech.hyphoto.R;

/**
 * 虚线
 * Created by yhf on 2017/3/17.
 */
public class DottedLineView extends BaseRelativeLayout {



    public DottedLineView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DottedLineView(Context context) {
        super(context);
    }

    public DottedLineView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void initView() {
        super.initView();
        inflate(getContext(), R.layout.dotted_line_layout,this);
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
