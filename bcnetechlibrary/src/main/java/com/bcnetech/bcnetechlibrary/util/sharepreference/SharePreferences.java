package com.bcnetech.bcnetechlibrary.util.sharepreference;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.bcnetech.bcnetechlibrary.BcnetechAppInstance;
import com.bcnetech.bcnetechlibrary.util.sharepreference.helper.Types;

import java.util.Map;


/**
 * Created by wenbin on 16/6/6.
 */
public class SharePreferences {
    private SharedPreferences prefs;
    private SharedPreferences.Editor prefsEdit;
    private static SharePreferences instance;

    private SharePreferences() {
    }

    public static SharePreferences instance() {
        if(null == instance) {
            instance = new SharePreferences();
            instance.prefs = PreferenceManager.getDefaultSharedPreferences(BcnetechAppInstance.getApplicationContext());
        }

        return instance;
    }



    public boolean getBoolean(String var1, boolean var2) {
        return ((Boolean)this.getSystemProperties(var1, Boolean.valueOf(var2), Types.BOOLEAN)).booleanValue();
    }

    public float getFloat(String var1, float var2) {
        return ((Float)this.getSystemProperties(var1, Float.valueOf(var2), Types.FLOAT)).floatValue();
    }

    public int getInt(String var1, int var2) {
        return ((Integer)this.getSystemProperties(var1, Integer.valueOf(var2), Types.INTEGER)).intValue();
    }

    public long getLong(String var1, long var2) {
        return ((Long)this.getSystemProperties(var1, Long.valueOf(var2), Types.LONG)).longValue();
    }

    public String getString(String var1, String var2) {
        return (String)this.getSystemProperties(var1, var2, Types.STRING);
    }

    public Object getSystemProperties(String var1, Object var2, Types var3) {
        Object var4 = null;
        switch(var3.ordinal()) {
            case 0:
                var4 = Boolean.valueOf(this.prefs.getBoolean(var1, ((Boolean)var2).booleanValue()));
                break;
            case 1:
                var4 = this.prefs.getString(var1, (String)var2);
                break;
            case 2:
                var4 = Integer.valueOf(this.prefs.getInt(var1, ((Integer)var2).intValue()));
                break;
            case 3:
                var4 = Long.valueOf(this.prefs.getLong(var1, ((Long)var2).longValue()));
                break;
            case 4:
                var4 = Float.valueOf(this.prefs.getFloat(var1, ((Float)var2).floatValue()));
                break;
        }

        return var4;
    }
    public Map<String, ?> getAllSystemProperties() {
        return this.prefs.getAll();
    }

    public void putBoolean(String var1, boolean var2) {
        this.saveSystemProperties(var1, Boolean.valueOf(var2), Types.BOOLEAN);
    }

    public void putFloat(String var1, float var2) {
        this.saveSystemProperties(var1, Float.valueOf(var2), Types.FLOAT);
    }

    public void putInt(String var1, int var2) {
        this.saveSystemProperties(var1, Integer.valueOf(var2), Types.INTEGER);
    }

    public void putLong(String var1, long var2) {
        this.saveSystemProperties(var1, Long.valueOf(var2), Types.LONG);
    }

    public void putString(String var1, String var2) {
        this.saveSystemProperties(var1, var2, Types.STRING);
    }

    public void saveSystemProperties(String var1, Object var2, Types var3) {
        if(this.prefsEdit == null) {
            this.initPrefsEdit();
        }

        switch(var3.ordinal()) {
            case 0:
                this.prefsEdit.putBoolean(var1, ((Boolean)var2).booleanValue());
                break;
            case 1:
                this.prefsEdit.putString(var1, (String)var2);
                break;
            case 2:
                this.prefsEdit.putInt(var1, ((Integer)var2).intValue());
                break;
            case 3:
                this.prefsEdit.putLong(var1, ((Long)var2).longValue());
                break;
            case 4:
                this.prefsEdit.putFloat(var1, ((Float)var2).floatValue());
                break;
        }

        this.commitPrefsEdit();
    }

    public void removeSystemProperties(String var1) {
        if(null == this.prefsEdit) {
            this.initPrefsEdit();
        }

        this.prefsEdit.remove(var1);
        this.commitPrefsEdit();
    }

    public SharedPreferences getDefaultSharedPreferences() {
        return this.prefs;
    }

    private synchronized void initPrefsEdit() {
        if(null == this.prefsEdit) {
            this.prefsEdit = this.prefs.edit();
        }

    }

    /**
     * 异步提交
     */
    private void commitPrefsEdit() {
        if(null != this.prefsEdit) {
            this.prefsEdit.apply();
        }

    }

    /**
     * 同步提交
     */
    private void commitPrefsEditCommit() {
        if(null != this.prefsEdit) {
            this.prefsEdit.commit();
        }

    }
}
