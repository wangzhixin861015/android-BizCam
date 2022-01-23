package com.bcnetech.bcnetechlibrary.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputConnectionWrapper;
import android.widget.EditText;

/**
 * Created by wenbin on 2016/12/26.
 */

public class LimitEditText extends EditText {




    public LimitEditText(Context context) {
        super(context);
    }

    public LimitEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LimitEditText(Context context, AttributeSet attrs, int defStyleAttr) {
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
    /**
     * 输入法
     * @param outAttrs
     * @return
     */
    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        return new mInputConnecttion(super.onCreateInputConnection(outAttrs),
                false);
    }
    class mInputConnecttion extends InputConnectionWrapper implements
            InputConnection {

        public mInputConnecttion(InputConnection target, boolean mutable) {
            super(target, mutable);
        }

        /**
         * 对输入的内容进行拦截
         *
         * @param text
         * @param newCursorPosition
         * @return
         */
        @Override
        public boolean commitText(CharSequence text, int newCursorPosition) {
            // 只能输入汉字
            String str=text.toString();
            String sb;
            for(int i=0;i<str.length();i++) {
                sb=str.charAt(i)+"";
                if (sb.matches("[\u4E00-\u9FA5]") || sb.matches("[a-z]")
                        || sb.matches("[A-Z]") || sb.matches("[0-9]")) {
                    return super.commitText(text, newCursorPosition);
                }
            }
            return false;
        }

        @Override
        public boolean sendKeyEvent(KeyEvent event) {
            return super.sendKeyEvent(event);
        }

        @Override
        public boolean setSelection(int start, int end) {
            return super.setSelection(start, end);
        }



    }
}

