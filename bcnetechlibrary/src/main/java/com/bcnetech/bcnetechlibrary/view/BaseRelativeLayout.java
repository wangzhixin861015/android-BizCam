package com.bcnetech.bcnetechlibrary.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * Created by wb on 2016/5/4.
 */
public abstract class BaseRelativeLayout extends RelativeLayout{
    public BaseRelativeLayout(Context context) {
        super(context);
        initView();
        initData();
        onViewClick();
    }

    public BaseRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
        initData();
        onViewClick();
    }

    public BaseRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
        initData();
        onViewClick();
    }

    protected void initView(){

    }
    protected void initData(){

    }
    protected void onViewClick(){

    }


}