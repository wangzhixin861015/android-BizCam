package com.bcnetech.bcnetechlibrary.util;

import android.app.Activity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

/**
 * Created by wb on 2016/5/4.
 */
public class ActivityManager {
    private static List<Activity> activityList = new ArrayList<Activity>();

    /**
     * 添加普通Activity
     *
     * @param activity
     */
    public static void addActivity(Activity activity) {
        activityList.add(activity);
    }

    /**
     * 删除普通Activity
     *
     * @param activity
     */
    public static void removeActivity(Activity activity) {
        activityList.remove(activity);
    }

    /**
     * 删除所有
     */
    public static void removeAndFinishAll() {
        for (Activity activity : activityList) {
            activity.finish();
        }
        activityList.clear();
    }

    /**
     * 删除所有处了指定的
     *
     * @param activityNames
     */
    public static void removeAndFinishAllExcludes(String... activityNames) {
        HashSet<String> set = new HashSet<String>();
        for (String name : activityNames) {
            set.add(name);
        }
        Iterator<Activity> iter = activityList.iterator();
        while (iter.hasNext()) {
            Activity a = iter.next();
            if (!set.contains(a.getClass().getSimpleName())) {
                a.finish();
                iter.remove();
            }
        }
    }

    /**
     * 删除指定的Activity
     *
     * @param activityNames
     */
    public static void removeAndFinishIncludes(String... activityNames) {
        HashSet<String> set = new HashSet<String>();
        for (String name : activityNames) {
            set.add(name);
        }
        Iterator<Activity> iter = activityList.iterator();
        while (iter.hasNext()) {
            Activity a = iter.next();
            if (set.contains(a.getClass().getSimpleName())) {
                a.finish();
                iter.remove();
            }
        }
    }
}
