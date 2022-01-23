package com.bcnetech.bluetoothlibarary.bluetoothclient.bluetoothinterface;

import io.reactivex.ObservableEmitter;

/**
 * author: wsBai
 * date: 2019/1/14
 */
public interface IBlueToothProxy {
    /**
     * 代理者接口方法
     */
    //搜索蓝牙
    void search(ObservableEmitter<Object> emitter);

    //停止搜索
    void stopSearch();

    //判断蓝牙是否已开启
    boolean isBlueToothOn();

}
