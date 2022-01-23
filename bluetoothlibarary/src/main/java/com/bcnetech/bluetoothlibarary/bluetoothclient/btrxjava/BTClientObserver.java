package com.bcnetech.bluetoothlibarary.bluetoothclient.btrxjava;


import com.bcnetech.bluetoothlibarary.bluetoothUtil.BluetoothUtils;
import com.bcnetech.bluetoothlibarary.data.CommendItem;

import io.reactivex.disposables.Disposable;

/**
 * 蓝牙数据订阅者
 * author: wsBai
 * date: 2019/1/2
 */
public class BTClientObserver extends BTObserver {
    private Disposable disposable;

    @Override
    public void onSubscribe(Disposable d) {
        this.disposable = d;
    }

    @Override
    public void onNext(CommendItem commendItem) {

        BluetoothUtils.showLogD("Observer_onNext"+ commendItem.toString());
    }

    @Override
    public void onError(Throwable e) {

    }

    @Override
    public void onComplete() {

    }

    //判断是否订阅
    public boolean getDisposable() {
        return disposable.isDisposed();
    }

    //取消订阅
    public void setDispose() {
        if (this.disposable != null)
            disposable.dispose();
    }

    @Override
    public void dispose() {

    }

    @Override
    public boolean isDisposed() {
        return false;
    }
}
