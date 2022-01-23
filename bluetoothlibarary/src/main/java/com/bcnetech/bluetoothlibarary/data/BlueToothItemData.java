package com.bcnetech.bluetoothlibarary.data;


import android.bluetooth.BluetoothDevice;

import java.io.Serializable;

/**
 * Created by wenbin on 16/6/20.
 */
public class BlueToothItemData implements Serializable {
    private String name;
    private String address;
    private boolean isConnection;
    private BluetoothDevice bleDevice;
    //0未连接 1正在连接 2已连接
    private int type;

    public BlueToothItemData(String name, String address, boolean isConnection, int type) {
        this.name = name;
        this.address = address;
        this.isConnection = isConnection;
        this.type = type;
    }

    public BlueToothItemData(String name, String address, boolean isConnection, int type, BluetoothDevice bleDevice) {
        this.name = name;
        this.address = address;
        this.isConnection = isConnection;
        this.type = type;
        this.bleDevice = bleDevice;
    }


    public BlueToothItemData() {
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isConnection() {
        return isConnection;
    }

    public void setConnection(boolean connection) {
        isConnection = connection;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public BluetoothDevice getBleDevice() {
        return bleDevice;
    }

    public void setBluetoothDevice(BluetoothDevice bleDevice) {
        this.bleDevice = bleDevice;
    }

    @Override
    public String toString() {
        return "BlueToothItemData{" +
                "name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", isConnection=" + isConnection +
                ", bleDevice=" + bleDevice +
                ", type=" + type +
                '}';
    }
}
