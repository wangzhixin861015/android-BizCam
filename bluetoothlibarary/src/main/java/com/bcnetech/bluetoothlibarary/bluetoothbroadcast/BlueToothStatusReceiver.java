package com.bcnetech.bluetoothlibarary.bluetoothbroadcast;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.bcnetech.bluetoothlibarary.bluetoothUtil.BluetoothUtils;

/**
 * author: wsBai
 * date: 2019/1/9
 */
public class BlueToothStatusReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()) {
            case BluetoothAdapter.ACTION_STATE_CHANGED:
                int blueState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                switch (blueState) {
                    case BluetoothAdapter.STATE_TURNING_ON:
                        BluetoothUtils.showLogD("BlueToothStatus " + "onReceive---------STATE_TURNING_ON");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        BluetoothUtils.showLogD("BlueToothStatus " + "onReceive---------STATE_ON");
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        BluetoothUtils.showLogD("BlueToothStatus " + "onReceive---------STATE_TURNING_OFF");
                        break;
                    case BluetoothAdapter.STATE_OFF:
                        BluetoothUtils.showLogD("BlueToothStatus " + "onReceive---------STATE_OFF");
                        break;

                }
                break;
        }

    }
}