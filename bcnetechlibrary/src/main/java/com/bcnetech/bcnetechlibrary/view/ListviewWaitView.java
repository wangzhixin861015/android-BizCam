package com.bcnetech.bcnetechlibrary.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.bcnetech.bcnetechlibrary.R;

/**
 * Created by wenbin on 16/8/8.
 */

public class ListviewWaitView extends RelativeLayout {
    public ListviewWaitView(Context context) {
        super(context);
        initView();
    }

    public ListviewWaitView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public ListviewWaitView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView(){
        inflate(getContext(), R.layout.listview_wait_view,this);
    }
}
