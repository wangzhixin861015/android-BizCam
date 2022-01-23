package com.bcnetech.hyphoto.model;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.IntentFilter;
import android.os.Handler;

import com.bcnetech.bcnetechlibrary.util.LogUtil;
import com.bcnetech.bcnetechlibrary.util.StringUtil;
import com.bcnetech.bcnetechlibrary.util.ThreadPoolUtil;
import com.bcnetech.bluetoothlibarary.bluetoothUtil.BluetoothUtils;
import com.bcnetech.bluetoothlibarary.bluetoothagreement.CommendManage;
import com.bcnetech.bluetoothlibarary.bluetoothclient.BluetoothProxy;
import com.bcnetech.bluetoothlibarary.bluetoothclient.btrxjava.BTScanObserver;
import com.bcnetech.bluetoothlibarary.data.BlueToothItemData;
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
 * 蓝牙连接模块
 *
 * @author wsbai
 * @date 2018/9/26
 */
public class BleConnectModel {
    public static final int STATUSCONNECT = 0;
    public static final int STATUSBTCLOSED = 1;
    public static final int STATUSDISCONNECT = 2;

    //蓝牙列表
    private List<BlueToothItemData> deviceList;
    //蓝牙列表adapter
    //private SurfBlueToothPopAdapter adapter;
    private int CurrentCOBOXVer;
    private int selectPosition = -1;
    private BlueToothBleRunnable blueToothBleRunnable;
    private BleConnectStatus bleConnectStatus;
    private static BleConnectModel bleConnectModel;
    //是否为低功耗蓝牙
    private boolean isBle = true;
    private BluetoothProxy bluetoothProxy;
    //转盘状态
    private int MotorStatus = CommendManage.MOTOROFFLINE;

    public static BleConnectModel getBleConnectModelInstance(BleConnectStatus bleConnectStatus) {
        if (bleConnectModel == null) {
            bleConnectModel = new BleConnectModel();
        }
        if (bleConnectStatus != null)
            bleConnectModel.bleConnectStatus = bleConnectStatus;
        return bleConnectModel;

    }

    public static BleConnectModel getBleConnectModelInstance() {
        if (bleConnectModel == null) {
            bleConnectModel = new BleConnectModel();
        }
        return bleConnectModel;
    }

    public BleConnectModel() {
        if (bluetoothProxy == null)
            bluetoothProxy = new BluetoothProxy();
    }

    public void init() {
        if (deviceList == null)
            deviceList = new ArrayList<>();
        startBlueToothService();
    }

    /**
     * 初始化蓝牙&蓝牙状态监听
     */
    private void startBlueToothService() {
        //当前未手动断开
        if (isCurrentConnect()) {
            if (deviceList != null && !deviceList.isEmpty()) {
                for (int i = 0; i < deviceList.size(); i++) {
                    if (deviceList.get(i).getType() == 2) {
                        selectPosition = i;
                        break;
                    }
                }
                bleConnectStatus.onNotifyList();
                if (bleConnectModel.checkoutDeviceConnect()) {
                    deviceList.get(selectPosition).setType(2);
                    if (bleConnectStatus != null) {
                        bleConnectStatus.onBleConnect(true, deviceList.get(selectPosition).getName());
                    }
                    WriteBleConnection(CommendManage.getInstance().getCurrentVersion());
                }
            }
        }
    }

    /**
     * 检测设备是否以连接
     */
    public boolean checkoutDeviceConnect() {
        if (bluetoothProxy != null) {
            return bluetoothProxy.checkoutDeviceConnect();
        }
        return false;
    }

    /**
     * 当前是否有cobox设备连接
     *
     * @return
     */
    public boolean isCurrentConnect() {
        if (bluetoothProxy != null && bluetoothProxy.isDeviceConnect()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 当前是否正在连接
     *
     * @return
     */
    public boolean isConnecting() {
        if (bluetoothProxy != null && bluetoothProxy.isConnecting()) {
            return true;
        } else {
            return false;
        }
    }

    public BluetoothDevice getCurrentBlueToothDevice() {
        if (bluetoothProxy != null && bluetoothProxy.getCurrentBluetoothDevice() != null) {
            return bluetoothProxy.getCurrentBluetoothDevice();
        }
        return null;
    }


    public boolean isBlueEnable() {
        return BluetoothUtils.isBluetoothEnabled();
    }

    public void disconnectAllDevice() {
        if (bluetoothProxy.getCurrentBluetoothDevice() != null)
            bluetoothProxy.disconnect(bluetoothProxy.getCurrentBluetoothDevice().getAddress());
    }

    public void setDeviceList(List<BlueToothItemData> deviceList) {
        bleConnectModel.deviceList = new ArrayList<>(deviceList);
    }

    public int getSelectPosition() {
        return selectPosition;
    }

    /**
     * 停止扫描
     */
    public void stopSearch() {
        if (bluetoothProxy != null) {
            bluetoothProxy.stopSearch();
        }
    }

    /**
     * 当前是否在搜索蓝牙
     *
     * @return
     */
    public boolean isSearching() {
        if (bluetoothProxy != null) {
            return bluetoothProxy.isScanning();
        }
        return false;
    }

    public void startScanBlueTooth() {
      /*  if (deviceList != null)
            deviceList.clear();*/
        if (bluetoothProxy != null && bluetoothProxy.getCurrentBluetoothDevice() != null) {
            //有连接上的设备，添加连接上设备的item
            BlueToothItemData item = new BlueToothItemData();
            item.setName(bluetoothProxy.getCurrentBluetoothDevice().getName());
            item.setAddress(bluetoothProxy.getCurrentBluetoothDevice().getAddress());
            item.setConnection(false);
            item.setType(2);
            for (int i = 0; i < deviceList.size(); i++) {
                if (deviceList.get(i).getAddress() == item.getAddress()) {
                    return;
                }
            }
            deviceList.add(item);
        }

        bluetoothProxy.btBleScanObservable = Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> emitter) throws Exception {
                bluetoothProxy.search(emitter);
            }
        });
        bluetoothProxy.btBleScanObserver = new BTScanObserver() {
            @Override
            public void onSubscribe(Disposable d) {
                super.onSubscribe(d);
            }

            @Override
            public void onNext(BluetoothDevice device) {
                if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
                    //已配对的设备
                }
                if (deviceList == null) {
                    deviceList = new ArrayList<>();
                } else {
                    for (BlueToothItemData blueToothItemData : deviceList) {
                        if (blueToothItemData.getBleDevice().getAddress() != null && blueToothItemData.getBleDevice().getName() != null && blueToothItemData.getName().equals(device.getName()) && blueToothItemData.getBleDevice().getAddress().equals(device.getAddress())) {
                            return;
                        }
                    }
                }
                if(!StringUtil.isBlank(device.getName())){
                    if ((device.getName().contains(CommendManage.COBOX_NAME) || device.getName().contains(CommendManage.CBEDU_NAME) || device.getName().contains(CommendManage.COLINK_NAME))) {
                        BlueToothItemData item = new BlueToothItemData();
                        item.setName(device.getName());
                        item.setAddress(device.getAddress());
                        item.setConnection(false);
                        item.setType(0);
                        item.setBluetoothDevice(device);
                        deviceList.add(item);
                        bleConnectStatus.onNotifyList();
                    }
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                deviceList.clear();
                bleConnectStatus.onScanError();
            }

            @Override
            public void onComplete() {
                super.onComplete();
            }
        };


        bluetoothProxy.btBleScanObservable.observeOn(AndroidSchedulers.mainThread()).
                subscribeOn(Schedulers.newThread())
                .subscribe(bluetoothProxy.btBleScanObserver);
    }

    /**
     * 直接连接
     */
    private void ConnectBleDevice(final BluetoothDevice bluetoothDevice) {

        if( !StringUtil.isBlank(bluetoothDevice.getName()) && !StringUtil.isBlank(bluetoothDevice.getAddress())) {
            if(bluetoothDevice.getName().contains(CommendManage.COBOX_NAME)||bluetoothDevice.getName().contains(CommendManage.CBEDU_NAME)){
                isBle=false;
            }else {
                isBle=true;
            }        //isBle = true;
            setBtClient(bluetoothDevice.getAddress());
        }else {
            disConnectCurrent();
        }

    }


    public void WriteBleConnection(final byte[] cmd) {
        if (blueToothBleRunnable == null) {
            blueToothBleRunnable = new BlueToothBleRunnable();
        }
        blueToothBleRunnable.cmd = cmd;
        ThreadPoolUtil.execute(blueToothBleRunnable);
    }

    class BlueToothBleRunnable implements Runnable {
        byte[] cmd;

        @Override
        public synchronized void run() {
            sendCommand(cmd);
        }
    }

    /**
     * 获取转盘状态
     *
     * @return
     */
    public int getMotorStatus() {
        return this.MotorStatus;
    }

    private void setMotorStatus(int motorStatus) {
        this.MotorStatus = motorStatus;
    }

    /**
     * 连接蓝牙
     *
     * @param position 下标
     */
    public void choiceDeivce(final int position) {
        if (position < 0) {
            return;
        }
        if (deviceList.get(position).getType() == 0) {
            for (int i = 0; i < deviceList.size(); i++) {
                if (deviceList.get(i).getType() == 1)
                    return;
            }
            selectPosition = position;
            deviceList.get(position).setType(1);
            deviceList.get(position).setConnection(true);
            bleConnectStatus.onNotifyList();
            ConnectBleDevice(deviceList.get(position).getBleDevice());
        }
    }

    /**
     * 断开当前连接
     */
    public void disConnectCurrent() {
        setDeviceDisConnected();
        deviceList.clear();
        startScanBlueTooth();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (bleConnectStatus != null)
                    bleConnectStatus.onBleConnect(false, null);
            }
        }, 600);
    }


    /**
     * 扫码连接蓝牙
     *
     * @param address
     */
    public boolean scanDevice(String address) {
        if (null == deviceList) {
            deviceList = new ArrayList<>();
        }
        for (int i = 0; i < deviceList.size(); i++) {
            if (!StringUtil.isBlank(deviceList.get(i).getName())&&address.equals(deviceList.get(i).getName())) {
                if (selectPosition == i) {
                    bleConnectStatus.onToast(STATUSCONNECT);
                    return true;
                }
                selectPosition = i;
                deviceList.get(i).setType(1);
                deviceList.get(i).setConnection(true);
                bleConnectStatus.onNotifyList();
                ConnectBleDevice(deviceList.get(i).getBleDevice());
                return true;
            }
        }
        return false;
    }

    public List<BlueToothItemData> getDeviceList() {
        return deviceList;
    }

    public void onDestroy() {
        if (!isCurrentConnect()&&!isConnecting()) {
            unBindListener();
            bleConnectModel = null;
            CurrentCOBOXVer = 0;
            deviceList.clear();
            deviceList = null;
            bluetoothProxy.setBtObserverCallback(null);
        }
    }

    /**
     * 断开连接
     */
    private void setDeviceDisConnected() {
        if (bluetoothProxy != null)
            bluetoothProxy.disconnect(null);
        CurrentCOBOXVer = 0;
        if(null!=bleConnectStatus){
            if(null!=deviceList&&deviceList.size()>0&&(selectPosition>0&&selectPosition<deviceList.size())){
                deviceList.get(selectPosition).setType(0);
            }
            bleConnectStatus.onNotifyList();
        }
        selectPosition = -1;
    }

    /**
     * 蓝牙关闭
     */
    public void onBlueToothOff() {
        stopSearch();
        setDeviceDisConnected();
        deviceList.clear();
    }


    public interface BleConnectStatus {
        //蓝牙状态回调（是否打开/关闭蓝牙）
        //  void onBleStatus(boolean isBleOpen);

        //蓝牙连接回调(正常连接或者断开)
        void onBleConnect(boolean isConnected, String deviceName);

        //蓝牙连接错误回调
        void onBleConnectError();

        void onGetDeviceInfo(int useTime);

        void onBleReceive(List<CommendItem> list);

        void onCOBOXVersion(int COBOXVer);

        //转盘状态
        void onMotorStatus(int motorStatus);

        void onNotifyList();

        void onToast(int status);

        void onScanError();

    }

    public void unBindListener() {
        // this.bleConnectStatus = null;
    }


    /**
     * 设置蓝牙客户端类型
     */
    private void setBtClient(String address) {
        bluetoothProxy.setBtObserverCallback(new BluetoothProxy.BtObserverCallback() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(CommendItem commendItem) {
                if (commendItem.getType() == CommendManage.CONNECTED) {
                    LogUtil.d("connect_success");
                    if(null!=deviceList&&deviceList.size()>0){
                        deviceList.get(selectPosition).setType(2);
                        if (bleConnectStatus != null) {
                            bleConnectStatus.onBleConnect(true, deviceList.get(selectPosition).getName());
                        }
                    }
                    bluetoothProxy.stopSearch();
                    return;
                }
                List<CommendItem> list = CommendManage.getInstance().processingResultData(commendItem.getRespons());
                if (null==list||list.size() == 0)
                    return;
                if (list.get(0).getType() == CommendManage.VERSION) {
                    //获取设备蓝牙版本号的回调
                    int CoboxVer = list.get(0).getNum();
                    LogUtil.d("CoboxVer: " + CoboxVer);
                    CurrentCOBOXVer = CoboxVer;
                    if (bleConnectStatus != null)
                        bleConnectStatus.onCOBOXVersion(CurrentCOBOXVer);
                    WriteBleConnection(CommendManage.getInstance().getParamsLand());
                    return;
                } else if (list.get(0).getType() == CommendManage.USETIME_VALUE) {
                    //获取设备使用时间的回调
                    if (bleConnectStatus != null)
                        bleConnectStatus.onGetDeviceInfo(list.get(0).getNum());
                    return;
                } else {
                    //其余回调：获取灯数据
                    if (deviceList != null && !deviceList.isEmpty() && selectPosition != -1) {
                       /* LogUtil.d("connect_success");
                        deviceList.get(selectPosition).setType(2);
                        if (bleConnectStatus != null) {
                            bleConnectStatus.onBleConnect(true, deviceList.get(selectPosition).getName());
                            bleConnectStatus.onBleReceive(list);
                        }*/

                        if (bleConnectStatus != null) {
                            LogUtil.d("onGetLights");
                            bleConnectStatus.onBleReceive(list);
                        }
                        for (CommendItem commendItem1 : list) {
                            if (commendItem1.getType() == CommendManage.MOTOR) {
                                setMotorStatus(commendItem1.getNum());
                                if (bleConnectStatus != null) {
                                    bleConnectStatus.onMotorStatus(MotorStatus);
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onError(Throwable e) {

                if (bleConnectStatus != null) {
                    bleConnectStatus.onBleConnectError();
                }
            }

            @Override
            public void onComplete() {

            }
        });
        bluetoothProxy.setButetoothType(isBle, address);
    }

    public static IntentFilter makeFilter() {

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);//蓝牙状态
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);//指明一个与远程设备建立的低级别（ACL）连接。
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);//指明一个来自于远程设备的低级别（ACL）连接的断开
        return filter;
    }


    private void sendCommand(byte[] cmd) {
        bluetoothProxy.write(cmd);
    }
}
