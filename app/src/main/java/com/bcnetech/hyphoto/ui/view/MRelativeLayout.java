package com.bcnetech.hyphoto.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

/**
 * Created by a1234 on 16/10/13.
 */

public class MRelativeLayout extends RelativeLayout {

    public MRelativeLayout(Context context) {
        super(context);
    }

    public MRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
       return true;
    }


}
