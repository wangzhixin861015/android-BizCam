package com.bcnetech.bluetoothlibarary.bluetoothclient.btrxjava;

import com.bcnetech.bluetoothlibarary.data.CommendItem;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * author: wsBai
 * date: 2019/1/10
 */
public class BTObserver implements Observer<CommendItem>, Disposable {

    @Override
    public void onSubscribe(Disposable d) {

    }

    @Override
    public void onNext(CommendItem commendItem) {

    }

    @Override
    public void onError(Throwable e) {

    }

    @Override
    public void onComplete() {

    }

    @Override
    public void dispose() {
        this.dispose();
    }

    @Override
    public boolean isDisposed() {
        return this.isDisposed();
    }
}
