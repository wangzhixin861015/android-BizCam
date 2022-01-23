package com.bcnetech.hyphoto.ui.fragment.baselib;

import android.app.Activity;
import android.support.v4.app.Fragment;

/**
 * Created by wenbin on 16/5/9.
 */
public class CallFragment extends Fragment implements CallByActivityListener{
    private CallByFragmentListener callByFragmentListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if(activity instanceof CallByFragmentListener){
            callByFragmentListener = (CallByFragmentListener) activity;
        }
    }

    /**
     * 调用Activity
     *
     * @param command
     * @param data
     */
    protected void callActivity(int command, Object... data) {
        if(null != callByFragmentListener){
            callByFragmentListener.callByFragment(command, data);
        }
    }

    @Override
    public void callByActivity(int command, Object... data) {
        // 子类实现这个方法可以实现被activity调用
    }
}
