package com.bcnetech.bluetoothlibarary.bluetoothclient.spp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import com.bcnetech.bluetoothlibarary.bluetoothUtil.BluetoothUtils;
import com.bcnetech.bluetoothlibarary.bluetoothagreement.CommendManage;
import com.bcnetech.bluetoothlibarary.bluetoothclient.BlueToothClientBase;
import com.bcnetech.bluetoothlibarary.data.CommendItem;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import io.reactivex.ObservableEmitter;
import io.reactivex.disposables.CompositeDisposable;

/**
 * 经典蓝牙客户端
 */
public class BtSppClient extends BlueToothClientBase {
    static final UUID SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private BluetoothSocket bluetoothSocket;
    private BluetoothSocket mSocket;
    private OutputStream mOut;
    private InputStream mIn;
    private byte[] writeData;
    private byte[] notifyData;
    protected CommendManage commendManage;
    //一个disposable的容器，可以容纳多个disposable，添加和去除的复杂度为O(1)。
    protected CompositeDisposable mCompositeDisposable;
    //当前类是否被销毁
    private boolean currentStatus = false;

    public BtSppClient() {
        currentStatus = true;
        commendManage = CommendManage.getInstance();
        mCompositeDisposable = new CompositeDisposable();
    }

    /**
     * 循环读取对方数据(若没有数据，则阻塞等待)
     */
    private void Loopread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (currentStatus) {
                    try {
                        CommendItem commendItem = commendManage.getStringByProtocol(mIn);
                        if (commendItem != null) {
                            emitter.onNext(commendItem);
                        }
                    } catch (Exception e) {
                        currentStatus = false;
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }


    private void initDataInputStream(BluetoothDevice bluetoothDevice, BluetoothSocket socket) {
        mSocket = socket;
        try {
            mSocket.connect();
            CommendItem commendItem = new CommendItem();
            commendItem.setType(CommendManage.CONNECTED);
            emitter.onNext(commendItem);
            BluetoothUtils.showLogD("SPP:CONNECTED_SUCCESS");
            setCurrentBluetoothDevice(bluetoothDevice);
            // mListener.onConnect(mSocket.getRemoteDevice());
            //新建蓝牙输出流
            mOut = mSocket.getOutputStream();
            //获取蓝牙输入流
            mIn = mSocket.getInputStream();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Loopread();
            //设置 是否正在连接
            setConnectionIng(false);
            write(CommendManage.getInstance().getCurrentVersion());
        } catch (Throwable e) {
            BluetoothUtils.showLogD(e.toString());
            if (!mSocket.isConnected()) {
//                initDataInputStream(bluetoothDevice, socket);
                this.emitter.onError(new Exception());
            }
        }
    }

    /**
     * 释放监听引用(例如释放对Activity引用，避免内存泄漏)
     */
    public void onDestroy() {
        currentStatus = false;
        if (mCompositeDisposable != null)
            mCompositeDisposable.clear();
        // mListener = null;
    }

    /**
     * 关闭Socket连接
     */
    private void disConnect() {
        try {
            currentStatus = false;
            btObserver.dispose();
            emitter = null;
            mSocket.close();
            setCurrentBluetoothDevice(null);
        } catch (Throwable e) {
        }
    }

    /**
     * 当前设备与指定设备是否连接
     */
    @Override
    public boolean checkoutDeviceConnect() {
        if (getCurrentBluetoothDevice() == null)
            return false;
        if (mSocket != null) {
            BluetoothDevice bluetoothDevice = mSocket.getRemoteDevice();
            if (bluetoothDevice != null) {
                if (bluetoothDevice.getAddress().equals(getCurrentBluetoothDevice().getAddress())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean connect(String address, ObservableEmitter<Object> emitter) {
        this.emitter = emitter;
        //设置为正在连接
        setConnectionIng(true);
        BluetoothUtils.showLogD("Prepare Connect Bluetooth Device");
        try {
            bluetoothSocket = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(address).createRfcommSocketToServiceRecord(SPP_UUID); //明文传输(不安全)，无需配对
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            BluetoothUtils.showLogD("bluetoothSocket build succeed");
            initDataInputStream(BluetoothAdapter.getDefaultAdapter().getRemoteDevice(address), bluetoothSocket);
            return true;
        } catch (Throwable e) {
            BluetoothUtils.showLogE("SPP:CONNECTED_FAILED " + e.toString());
            onBTExcetion(e.toString());
            return false;
        }
    }

    @Override
    public void disconnect(String bluetoothDevice) {
        this.disConnect();
    }

    @Override
    public void write(byte[] value) {
        try {
            mOut.flush();
            mOut.write(value);
            mOut.flush();
            BluetoothUtils.showLogD("Spp_Write: " + BluetoothUtils.byte2HEX(value));
        } catch (Exception e) {
            onBTExcetion(e.toString());
            BluetoothUtils.showLogE("Write_error " + e.toString());
        }
    }

    @Override
    public void Notify() {
        //Loopread();
    }

    @Override
    public boolean isDeviceConnect() {
        return getCurrentBluetoothDevice() == null ? false : true;
    }


    @Override
    public void onBTExcetion(String exception) {
        this.emitter.onError(new Exception());
        BluetoothUtils.showLogD("onBTExcetion: " + exception);
    }

    @Override
    public boolean isConnecting() {
        return getConnectionIng();
    }
}
