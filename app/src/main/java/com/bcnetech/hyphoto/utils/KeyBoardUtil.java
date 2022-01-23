package com.bcnetech.hyphoto.utils;

import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by yhf on 2017/11/20.
 */

public class KeyBoardUtil {

    public KeyBoardUtil() {
    }

    public static void openKeybord(final EditText mEditText, final Context mContext) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                InputMethodManager imm = (InputMethodManager)mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(mEditText, 2);
                imm.toggleSoftInput(2, 1);
            }
        }, 150L);
    }

    public static void openKeybord5L(final EditText mEditText, final Context mContext) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                InputMethodManager imm = (InputMethodManager)mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(mEditText, 2);
                imm.toggleSoftInput(2, 1);
            }
        }, 400L);
    }

    public static void closeKeybord(EditText mEditText, Context mContext) {
        InputMethodManager imm = (InputMethodManager)mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
    }
}
