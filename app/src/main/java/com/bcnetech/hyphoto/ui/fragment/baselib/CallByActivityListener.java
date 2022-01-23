package com.bcnetech.hyphoto.ui.fragment.baselib;

/**
 * Fragment实现这个接口，供Activity调用
 * Created by wenbin on 16/5/9.
 */
public interface CallByActivityListener {

    /**
     * Fragment实现这个接口，供Activity调用
     *
     * @param command
     *            可以表示调用命令
     * @param data
     *            可以传递数据
     */
    public void callByActivity(int command, Object... data);

}
