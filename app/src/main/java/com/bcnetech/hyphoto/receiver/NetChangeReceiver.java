package com.bcnetech.hyphoto.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

/**
 * Created by a1234 on 16/12/5.
 * 监听网络变化
 */

public class NetChangeReceiver extends BroadcastReceiver {

    public static final String CONNECTIVITY_CHANGE_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (TextUtils.equals(action, CONNECTIVITY_CHANGE_ACTION)) {

        }

    }

}
