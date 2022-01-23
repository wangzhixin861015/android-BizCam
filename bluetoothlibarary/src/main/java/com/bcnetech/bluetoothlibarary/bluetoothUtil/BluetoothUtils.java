package com.bcnetech.bluetoothlibarary.bluetoothUtil;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BluetoothUtils {
    public static final String TAG = "BC_BLUETOOTH";
    private static BluetoothAdapter mBluetoothAdapter;
    private static boolean isShow = false;

    private static Handler mHandler;


    private static Handler getHandler() {
        if (mHandler == null) {
            mHandler = new Handler(Looper.getMainLooper());
        }
        return mHandler;
    }

    public static void post(Runnable runnable) {
        getHandler().post(runnable);
    }

    public static boolean isBluetoothEnabled() {
        return getBluetoothState() == BluetoothAdapter.STATE_ON;
    }

    public static int getBluetoothState() {
        BluetoothAdapter adapter = getBluetoothAdapter();
        return adapter != null ? adapter.getState() : 0;
    }

    public static boolean openBluetooth() {
        BluetoothAdapter adapter = getBluetoothAdapter();
        if (adapter != null) {
            return adapter.enable();
        }
        return false;
    }

    public static boolean closeBluetooth() {
        BluetoothAdapter adapter = getBluetoothAdapter();
        if (adapter != null) {
            return adapter.disable();
        }
        return false;
    }

    public static BluetoothAdapter getBluetoothAdapter() {
        if (mBluetoothAdapter == null) {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        }
        return mBluetoothAdapter;
    }

    public static BluetoothDevice getRemoteDevice(String mac) {
        if (!TextUtils.isEmpty(mac)) {
            BluetoothAdapter adapter = getBluetoothAdapter();
            if (adapter != null) {
                return adapter.getRemoteDevice(mac);
            }
        }
        return null;
    }


    public static List<BluetoothDevice> getBondedBluetoothClassicDevices() {
        BluetoothAdapter adapter = getBluetoothAdapter();
        List<BluetoothDevice> devices = new ArrayList<BluetoothDevice>();
        if (adapter != null) {
            Set<BluetoothDevice> sets = adapter.getBondedDevices();
            if (sets != null) {
                devices.addAll(sets);
            }
        }
        return devices;
    }

    public static String byte2HEX(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X ", b));
        }
        return sb.toString();
    }

    public static void showLogD(String message) {
        if (isShow)
            Log.d(TAG, message);
    }

    public static void showLogW(String message) {
        if (isShow)
            Log.w(TAG, message);
    }

    public static void showLogE(String message) {
        if (isShow)
            Log.e(TAG, message);
    }
}
