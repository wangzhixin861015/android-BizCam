package com.bcnetech.hyphoto.ui.fragment.baselib;
/**
 * Fragment和Activity通讯的接口，Activity实现这个接口，然后它的实例在传递给Fragment之后就可以向上转型了
 * Created by wenbin on 16/5/9.
 */
public interface CallByFragmentListener {
    /**
     * Activity实现这个接口，供Fragment调用
     *
     * @param command
     *            可以表示调用命令
     * @param data
     *            可以传递数据
     */
    public void callByFragment(int command, Object... data);
}
