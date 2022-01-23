package com.bcnetech.bcnetechlibrary.util;

import android.content.Context;

import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;

/**
 * 友盟统计
 * Created by yhf on 17/8/7.
 */
public class EventStatisticsUtil {

    /**
     * 计数事件
     * @param context 指当前的Activity
     * @param eventId 为当前统计的事件ID
     */
    public static void event(Context context, String eventId) {
        MobclickAgent.onEvent(context, eventId);
    }

    /**
     * 计数事件
     * @param context 指当前的Activity
     * @param eventId 为当前统计的事件ID
     * @param map 为当前事件的属性和取值（Key-Value键值对)
     */
    public static void event(Context context, String eventId, HashMap map) {
        MobclickAgent.onEvent(context, eventId, map);
    }

}
