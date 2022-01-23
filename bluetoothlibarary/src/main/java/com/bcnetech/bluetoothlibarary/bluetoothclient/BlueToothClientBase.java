package com.bcnetech.bluetoothlibarary.bluetoothclient;

import android.bluetooth.BluetoothDevice;

import com.bcnetech.bluetoothlibarary.bluetoothUtil.BluetoothUtils;
import com.bcnetech.bluetoothlibarary.bluetoothclient.bluetoothinterface.IBlueToothBase;
import com.bcnetech.bluetoothlibarary.bluetoothclient.btrxjava.BTObserver;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;

/**
 * author: wsBai
 * date: 2019/1/9
 */
public abstract class BlueToothClientBase implements IBlueToothBase {
    protected static String TAG = "BlueToothClient";

    //当前连接的设备
    protected BluetoothDevice currentBluetoothDevice;
    //蓝牙数据被观察者
    public Observable blueToothDataObservable;
    //蓝牙数据观察者
    public BTObserver btObserver;

    protected ObservableEmitter<Object> emitter;

    private boolean isConnection=false;

    @Override
    public abstract boolean connect(String address, ObservableEmitter<Object> emitter);

    @Override
    public abstract void disconnect(String address);

    @Override
    public void write(byte[] value) {
        BluetoothUtils.showLogD("WRITE_HEX: " + BluetoothUtils.byte2HEX(value));
    }

    public Observable getBlueToothDataObservable() {
        return blueToothDataObservable;
    }

    public BTObserver getBtObserver() {
        return btObserver;
    }

    @Override
    public abstract boolean isDeviceConnect();

    @Override
    public abstract boolean isConnecting();

    @Override
    public void Notify() {

    }

    public BlueToothClientBase() {

    }

    public BluetoothDevice getCurrentBluetoothDevice() {
        return currentBluetoothDevice;
    }

    public void setCurrentBluetoothDevice(BluetoothDevice currentBluetoothDevice) {
        this.currentBluetoothDevice = currentBluetoothDevice;
    }

    public void setConnectionIng(boolean connection) {
        isConnection = connection;
    }

    public boolean getConnectionIng() {
        return isConnection;
    }
}
