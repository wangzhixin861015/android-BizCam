package com.bcnetech.bluetoothlibarary.bluetoothclient;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.os.ParcelUuid;

import com.bcnetech.bluetoothlibarary.bluetoothUtil.BluetoothUtils;
import com.bcnetech.bluetoothlibarary.bluetoothclient.ble.BtBleClient;
import com.bcnetech.bluetoothlibarary.bluetoothclient.bluetoothinterface.IBlueToothProxy;
import com.bcnetech.bluetoothlibarary.bluetoothclient.btrxjava.BTClientObserver;
import com.bcnetech.bluetoothlibarary.bluetoothclient.btrxjava.BTScanObserver;
import com.bcnetech.bluetoothlibarary.bluetoothclient.spp.BtSppClient;
import com.bcnetech.bluetoothlibarary.data.CommendItem;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * author: wsBai
 * date: 2019/1/14
 */
public class BluetoothProxy extends BlueToothClientBase implements IBlueToothProxy {
    //蓝牙搜索回调
    //private BluetoothAdapter.LeScanCallback mScanCallback;
    private ScanCallback mScanCallback;
    private boolean isBle = true;
    private BlueToothClientBase blueToothClient = null;
    private List<BluetoothDevice> mScanLeDeviceList = new ArrayList<>();
    public Observable btBleScanObservable;
    public BTScanObserver btBleScanObserver;
    //当前是否正在搜索蓝牙
    protected boolean isScanning = false;
    private BtObserverCallback btObserverCallback;
    protected ObservableEmitter<Object> scanEmitter;


    public BluetoothProxy() {
    }


    public void setButetoothType(boolean ble, String address) {
        isBle = ble;
        this.blueToothClient = isBle ? new BtBleClient() : new BtSppClient();
        initObserver(address);
    }

    //继承BlueToothClientBase中方法
    @Override
    public boolean connect(String address, ObservableEmitter<Object> emitter) {
        return false;
    }

    @Override
    public void disconnect(String address) {
        if (this.blueToothClient != null)
            this.blueToothClient.disconnect(address);
    }

    @Override
    public boolean isDeviceConnect() {
        if (this.blueToothClient != null) {
            return this.blueToothClient.isDeviceConnect();
        }
        return false;
    }


    @Override
    public boolean isConnecting() {
        if (this.blueToothClient != null) {
            return this.blueToothClient.isConnecting();
        }
        return false;
    }

    @Override
    public void onBTExcetion(String exception) {
        if (this.blueToothClient != null)
            this.blueToothClient.onBTExcetion(exception);
    }

    @Override
    public void write(byte[] value) {
        if (this.blueToothClient != null)
            this.blueToothClient.write(value);
    }

    @Override
    public void Notify() {
        if (this.blueToothClient != null)
            this.blueToothClient.Notify();
    }


    public boolean isScanning() {
        return isScanning;
    }

    public void setScanning(boolean scanning) {
        isScanning = scanning;
    }


    @Override
    public BluetoothDevice getCurrentBluetoothDevice() {
        if (this.blueToothClient != null) {
            return this.blueToothClient.getCurrentBluetoothDevice();
        } else {
            return null;
        }
    }

    @Override
    public void setCurrentBluetoothDevice(BluetoothDevice currentBluetoothDevice) {
        if (this.blueToothClient != null)
            this.blueToothClient.setCurrentBluetoothDevice(currentBluetoothDevice);
    }

    //实现Proxy接口种方法

    @Override
    public boolean isBlueToothOn() {
        return BluetoothAdapter.getDefaultAdapter().enable();
    }

    @Override
    public void search(ObservableEmitter<Object> emitter) {

        if (isScanning) {
            return;
        }
        this.scanEmitter=emitter;

        mScanCallback = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                super.onScanResult(callbackType, result);
                BluetoothUtils.showLogD("onScanResult: " + result.getDevice().getAddress());
                if (null!=btBleScanObserver&&!btBleScanObserver.isDisposed())
                    scanEmitter.onNext(result.getDevice());
            }

            @Override
            public void onBatchScanResults(List<ScanResult> results) {
                super.onBatchScanResults(results);
            }

            @Override
            public void onScanFailed(int errorCode) {
                super.onScanFailed(errorCode);
                if (null!=btBleScanObserver&&!btBleScanObserver.isDisposed()){
                    scanEmitter.onError(new Throwable());
                    stopSearch();
                }
                BluetoothUtils.showLogE("onScanFailed: " + errorCode + "");
            }
        };
        mScanLeDeviceList.clear();
        List<ScanFilter> bleScanFilters = new ArrayList<>();
        bleScanFilters.add(
                new ScanFilter.Builder().setServiceUuid(ParcelUuid.fromString(BtBleClient.serviceUUiD)).build()
        );
        ScanSettings scanSettings = new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_BALANCED).build();
        //mBluetoothAdapter.startLeScan(mScanCallback);

        if(null!=BluetoothAdapter.getDefaultAdapter()&&null!=BluetoothAdapter.getDefaultAdapter().getBluetoothLeScanner()){
            BluetoothAdapter.getDefaultAdapter().getBluetoothLeScanner().startScan(bleScanFilters, scanSettings, mScanCallback);
            isScanning = true;
        }else {
            if (null!=btBleScanObserver&&!btBleScanObserver.isDisposed()){
                scanEmitter.onError(new Exception());
                stopSearch();
            }
        }
        // mBluetoothAdapter.getBluetoothLeScanner().startScan(bleScanFilters, scanSettings, mScanCallback);
    }

    private void initObserver(final String address) {
        //蓝牙数据被观察者
        blueToothClient.blueToothDataObservable = Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(final ObservableEmitter<Object> emitter) throws Exception {
                BluetoothUtils.showLogD("subscribe " + emitter.toString());
                blueToothClient.connect(address, emitter);
            }
        });
        //蓝牙数据观察者
        blueToothClient.btObserver = new BTClientObserver() {
            @Override
            public void onSubscribe(Disposable d) {
                super.onSubscribe(d);
                if(null!=btObserverCallback){
                    btObserverCallback.onSubscribe(d);
                }
            }

            @Override
            public void onNext(CommendItem commendItem) {
                super.onNext(commendItem);
                if(null!=btObserverCallback){
                    btObserverCallback.onNext(commendItem);
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                if(null!=btObserverCallback){
                    btObserverCallback.onError(e);
                }
            }

            @Override
            public void onComplete() {
                super.onComplete();
                if(null!=btObserverCallback){
                    btObserverCallback.onComplete();
                }
            }
        };
        //绑定蓝牙数据观察者对象
        blueToothClient.blueToothDataObservable.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.single())
                .subscribe(blueToothClient.btObserver);
    }


    /**
     * 蓝牙连接页面关闭 或 连接成功后停止扫描
     */
    @Override
    public void stopSearch() {

        if(isScanning){
            isScanning = false;

            if (mScanCallback != null)
                try {
                    // mBluetoothAdapter.stopLeScan(mScanCallback);
                    BluetoothAdapter.getDefaultAdapter().getBluetoothLeScanner().stopScan(mScanCallback);
                    BluetoothAdapter.getDefaultAdapter().getBluetoothLeScanner().flushPendingScanResults(mScanCallback);
                } catch (Exception e) {
                    e.printStackTrace();
                    scanEmitter.onError(e);
                }

            if (btBleScanObserver != null) {
                btBleScanObserver.onComplete();
                btBleScanObserver.dispose();
            }

            mScanCallback = null;
        }

    }

    @Override
    public boolean checkoutDeviceConnect() {
        return blueToothClient.checkoutDeviceConnect();
    }

    public void setBtObserverCallback(BtObserverCallback btObserverCallback) {
        this.btObserverCallback = btObserverCallback;
    }

    public interface BtObserverCallback {
        void onSubscribe(Disposable d);

        void onNext(CommendItem commendItem);

        void onError(Throwable e);

        void onComplete();
    }

}
