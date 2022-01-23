package com.bcnetech.bluetoothlibarary.bluetoothclient.bluetoothinterface;

import io.reactivex.ObservableEmitter;

/**
 * author: wsBai
 * date: 2019/1/9
 */
public interface IBlueToothBase {
    //连接设备
    boolean connect(String address, ObservableEmitter<Object> emitter);

    //断开设备
    void disconnect(String address);

    //写入数据
    void write(byte[] value);

    //判断设备是否连接(当前仅以是否手动断开为标准，设备自动断开以蓝牙广播为准)
    boolean isDeviceConnect();

    //读取数据
    void Notify();

    //蓝牙错误时调用
    void onBTExcetion(String exception);

    //判断当前设备是否连接
   boolean checkoutDeviceConnect();

   //是否正在连接
   boolean isConnecting();


}
