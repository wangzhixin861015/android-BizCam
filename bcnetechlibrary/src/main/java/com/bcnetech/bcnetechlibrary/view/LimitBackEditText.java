package com.bcnetech.bcnetechlibrary.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.EditText;

/**
 * Created by wenbin on 2016/12/26.
 */

public class LimitBackEditText extends EditText {




    public LimitBackEditText(Context context) {
        super(context);
    }

    public LimitBackEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LimitBackEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    @Override

    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            return true;
        }else{
            return false;
        }

    }
}

