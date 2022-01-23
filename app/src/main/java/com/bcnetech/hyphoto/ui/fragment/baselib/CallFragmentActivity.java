package com.bcnetech.hyphoto.ui.fragment.baselib;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import com.bcnetech.bcnetechlibrary.util.LogUtil;

/**
 * Created by wenbin on 16/5/9.
 */
public class CallFragmentActivity extends FragmentActivity implements
        CallByFragmentListener {



    /**
     * 子类可以调用该方法调用指定的Fragment
     *
     * @param tag
     * @param command
     * @param data
     */
    protected void callFragment(String tag, int command, Object... data) {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
        if (null == fragment) {
            LogUtil.e("Fragment of tag[" + tag
                    + "] command[" + command + "] not exists.");
        } else {
            if(fragment instanceof CallByActivityListener){
                CallByActivityListener callByActivityListener = (CallByActivityListener) fragment;
                callByActivityListener.callByActivity(command, data);
            }else{
                LogUtil.e("Fragment of tag[" + tag
                        + "] not implements the CallByActivityListener.");
            }
        }
    }

    @Override
    public void callByFragment(int command, Object... data) {
        // 如果这个Activity需要被Fragment调用就复写这个类

    }

}
