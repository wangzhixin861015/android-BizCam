package com.bcnetech.bluetoothlibarary.bluetoothclient.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.text.TextUtils;

import com.bcnetech.bluetoothlibarary.bluetoothUtil.BluetoothUtils;
import com.bcnetech.bluetoothlibarary.bluetoothagreement.CommendManage;
import com.bcnetech.bluetoothlibarary.bluetoothclient.BlueToothClientBase;
import com.bcnetech.bluetoothlibarary.data.CommendItem;

import java.util.List;
import java.util.UUID;

import io.reactivex.ObservableEmitter;

/**
 * 低功耗蓝牙客户端
 * author: wsBai
 * date: 2019/1/10
 */
public class BtBleClient extends BlueToothClientBase {
    public static final String serviceUUiD = "0000fff0-0000-1000-8000-00805f9b34fb";
    private static final String writeUUiD = "0000fff1-0000-1000-8000-00805f9b34fb";
    private static final String notifyUUiD = "0000fff2-0000-1000-8000-00805f9b34fb";
    private BluetoothGatt mBluetoothGatt;
    private BluetoothGattCallback mGattCallback;
    private Context context;
    private BluetoothGattCharacteristic Write_Characteristic;


    public BtBleClient() {
        mGattCallback = new BluetoothGattCallback() {

            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                super.onConnectionStateChange(gatt, status, newState);
                //连接成功，发送获取版本号指令
                BluetoothUtils.showLogD("onConnectionStateChange: " + newState + " " + status);
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    setConnectionIng(false);
                    //连接成功，设置接收蓝牙回调
                    CommendItem commendItem = new CommendItem();
                    commendItem.setType(CommendManage.CONNECTED);
                    emitter.onNext(commendItem);
                    setCurrentBluetoothDevice(gatt.getDevice());
                    boolean discoverServiceResult = gatt.discoverServices();
                    if (!discoverServiceResult) {
                        onBTExcetion("discoverServices_fail");
                    }

                } else {
                    connect(gatt.getDevice().getAddress(), emitter);
                }
            }

            @Override
            public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                super.onServicesDiscovered(gatt, status);
                BluetoothUtils.showLogD("onServicesDiscovered: " + status);
                /* for (BluetoothGattService service : gatt.getServices()) {
                    String str = service.getUuid().toString();
                    if (str.contains("0000fff0")) {
                        serviceUUiD = service.getUuid();
                        Log.d(TAG, "SERVICEUUID IS :" + serviceUUiD.toString());
                        List<BluetoothGattCharacteristic> gattCharacteristics = service
                                .getCharacteristics();
                        for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                            //读取蓝牙特征值
                            if (BluetoothGattCharacteristic.PROPERTY_WRITE == gattCharacteristic.getProperties() || BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE == gattCharacteristic.getProperties()) {
                                Log.d(TAG, "NotifyUUID IS :" + gattCharacteristic.getUuid().toString());
                                Write = gattCharacteristic;
                            } else *//*if (BluetoothGattCharacteristic.PROPERTY_NOTIFY == gattCharacteristic.getProperties() || BluetoothGattCharacteristic.PROPERTY_READ == gattCharacteristic.getProperties()) *//* {
                                Log.d(TAG, "WriteUUID IS :" + gattCharacteristic.getUuid().toString());
                                Notify = gattCharacteristic;
                            }
                        }
                        break;
                    }

                }
                if (serviceUUiD == null || Notify == null || Write == null) {
                    disconnect(currentBluetoothDevice);
                }*/
                Notify();
                //发送获取cobox版本号指令
            }

            @Override
            public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                super.onCharacteristicRead(gatt, characteristic, status);
            }

            @Override
            public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                super.onCharacteristicWrite(gatt, characteristic, status);
                BluetoothUtils.showLogD("onCharacteristicWrite: " + status);
            }

            @Override
            //蓝牙回调
            public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
                super.onCharacteristicChanged(gatt, characteristic);
                BluetoothUtils.showLogD("NOTIFY_HEX: " + BluetoothUtils.byte2HEX(characteristic.getValue()));
                CommendItem commendItem = new CommendItem();
                commendItem.setRespons(characteristic.getValue());
                emitter.onNext(commendItem);

            }

            @Override
            // 回调报告描述符读操作的结果
            public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
                super.onDescriptorRead(gatt, descriptor, status);
                BluetoothUtils.showLogD("onDescriptorRead " + status);
            }

            @Override
            // 回调报告描述符写操作的结果
            public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
                super.onDescriptorWrite(gatt, descriptor, status);
                BluetoothUtils.showLogD("onDescriptorWrite " + status);
                if (Write_Characteristic == null) {
                    if (mBluetoothGatt.getService(UUID.fromString(serviceUUiD)) != null) {
                        BluetoothGattCharacteristic bluetoothGattCharacteristic = mBluetoothGatt.getService(UUID.fromString(serviceUUiD)).getCharacteristic(UUID.fromString(writeUUiD));
                        Write_Characteristic = bluetoothGattCharacteristic;
                    } else {
                        mBluetoothGatt.discoverServices();
                    }
                }
                write(CommendManage.getInstance().getCurrentVersion());
            }
        };
    }

    public void setContext(Context context) {
        this.context = context;
    }

    /**
     * After using a given BLE device, the app must call this method to ensure resources are
     * released properly.
     */
    public void close() {
        if (mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.disconnect();
        mBluetoothGatt.close();
        Write_Characteristic = null;
        mBluetoothGatt = null;
    }


    @Override
    public boolean connect(String address, ObservableEmitter<Object> emitter) {
        BluetoothUtils.showLogD("Prepare Connect Bluetooth Device");
        setConnectionIng(true);
        this.emitter = emitter;
        if (address == null || TextUtils.isEmpty(address)) {
            BluetoothUtils.showLogW("Device not found.  Unable to connect.");
            onBTExcetion("bluetoothDevice==null");
            return false;
        }
        close();

        //Previously connected device.  Try to reconnect.
        if (currentBluetoothDevice != null && mBluetoothGatt != null
                && address.equals(currentBluetoothDevice.getAddress())) {
            BluetoothUtils.showLogD("Trying to use an existing mBluetoothGatt for connection.");
            if (mBluetoothGatt.connect()) {
                return true;
            } else {
                return false;
            }
        }
        //We want to directly connect to the device, so we are setting the autoConnect
        // parameter to false.
        mBluetoothGatt = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(address).connectGatt(context, false, mGattCallback);
        // mBluetoothGatt = bluetoothDevice.connectGatt(context, false, mGattCallback);
        BluetoothUtils.showLogD("Trying to create a new connection.");

        return true;
    }

    @Override
    public void disconnect(String address) {
        setCurrentBluetoothDevice(null);
        if (mBluetoothGatt != null) {
            mBluetoothGatt.disconnect();
            mBluetoothGatt.close();
        }
        emitter = null;
        Write_Characteristic = null;
    }

    @Override
    public void write(byte[] value) {
        super.write(value);
        if (mBluetoothGatt != null && Write_Characteristic != null) {
            Write_Characteristic.setValue(value);
            boolean b = mBluetoothGatt.writeCharacteristic(Write_Characteristic);
            if (!b) onBTExcetion("writeCharacteristic_Fail");
        }
       /* if (mBluetoothGatt.getService(UUID.fromString(serviceUUiD)) != null) {
            BluetoothGattCharacteristic bluetoothGattCharacteristic = mBluetoothGatt.getService(UUID.fromString(serviceUUiD)).getCharacteristic(UUID.fromString(writeUUiD));
            if (mBluetoothGatt != null && bluetoothGattCharacteristic != null) {
                bluetoothGattCharacteristic.setValue(value);
                boolean b = mBluetoothGatt.writeCharacteristic(bluetoothGattCharacteristic);
                if (!b) onBTExcetion("writeCharacteristic_Fail");
            }
        } else {
            mBluetoothGatt.discoverServices();
            write(CommendManage.getInstance().getCurrentVersion());
            onBTExcetion("getService_Fail");
        }*/
    }

    @Override
    public boolean isDeviceConnect() {
        return getCurrentBluetoothDevice() == null ? false : true;
    }

    @Override
    public boolean checkoutDeviceConnect() {
        if (getCurrentBluetoothDevice() == null)
            return false;
        if (mBluetoothGatt != null) {
            BluetoothDevice bluetoothDevice = mBluetoothGatt.getDevice();
            if (bluetoothDevice != null) {
                if (bluetoothDevice.getAddress().equals(getCurrentBluetoothDevice().getAddress())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void Notify() {
        enableNotification(mBluetoothGatt, UUID.fromString(serviceUUiD), UUID.fromString(notifyUUiD));
       /* if (Write_Characteristic == null) {
            if (mBluetoothGatt.getService(UUID.fromString(serviceUUiD)) != null) {
                BluetoothGattCharacteristic bluetoothGattCharacteristic = mBluetoothGatt.getService(UUID.fromString(serviceUUiD)).getCharacteristic(UUID.fromString(writeUUiD));
                Write_Characteristic = bluetoothGattCharacteristic;
            } else {
                mBluetoothGatt.discoverServices();
                write(CommendManage.getInstance().getCurrentVersion());
            }
        }*/
    }


    @Override
    public void onBTExcetion(String exception) {
        BluetoothUtils.showLogD("onBTExcetion: " + exception);
    }

    /**
     * 开启gatt通知
     *
     * @param gatt
     * @param serviceUUID
     * @param characteristicUUID
     * @return
     */
    private boolean enableNotification(BluetoothGatt gatt, UUID serviceUUID, UUID characteristicUUID) {
        boolean success = false;
        BluetoothGattService service = gatt.getService(serviceUUID);
        if (service != null) {
            BluetoothGattCharacteristic characteristic = findNotifyCharacteristic(service, characteristicUUID);
            if (characteristic != null) {
                success = gatt.setCharacteristicNotification(characteristic, true);
                if (success) { // 来源：http://stackoverflow.com/questions/38045294/oncharacteristicchanged-not-called-with-ble
                    for (BluetoothGattDescriptor dp : characteristic.getDescriptors()) {
                        if (dp != null) {
                            if ((characteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_NOTIFY) != 0) {
                                dp.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                            } else if ((characteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_INDICATE) != 0) {
                                dp.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);
                            }
                            gatt.writeDescriptor(dp);
                        }
                    }
                }
            }
        }
        return success;
    }

    private BluetoothGattCharacteristic findNotifyCharacteristic(BluetoothGattService service, UUID characteristicUUID) {
        BluetoothGattCharacteristic characteristic = null;
        List<BluetoothGattCharacteristic> characteristics = service.getCharacteristics();
        for (BluetoothGattCharacteristic c : characteristics) {
            if ((c.getProperties() & BluetoothGattCharacteristic.PROPERTY_NOTIFY) != 0 && characteristicUUID.equals(c.getUuid())) {
                characteristic = c;
                break;
            }
        }
        if (characteristic != null) return characteristic;
        for (BluetoothGattCharacteristic c : characteristics) {
            if ((c.getProperties() & BluetoothGattCharacteristic.PROPERTY_INDICATE) != 0 && characteristicUUID.equals(c.getUuid())) {
                characteristic = c;
                break;
            }
        }
        return characteristic;
    }


    @Override
    public boolean isConnecting() {
        return getConnectionIng();
    }
}
