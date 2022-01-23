package com.bcnetech.bluetoothlibarary.bluetoothclient.btrxjava;

import android.bluetooth.BluetoothDevice;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * 蓝牙搜索观察者
 * author: wsBai
 * date: 2019/1/10
 */
public class BTScanObserver implements Observer<BluetoothDevice>,Disposable{

    private Disposable disposable;


    @Override
    public void onSubscribe(Disposable d) {
        disposable=d;
    }

    @Override
    public void onNext(BluetoothDevice bluetoothDevice) {

    }

    @Override
    public void onError(Throwable e) {

    }

    @Override
    public void onComplete() {

    }

    @Override
    public void dispose() {
        disposable.dispose();
    }

    @Override
    public boolean isDisposed() {
        return false;
    }
}
