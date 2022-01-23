package com.bcnetech.hyphoto.utils;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
public class SPUtils
{
    private static volatile SPUtils mInstance;
    private SharedPreferences.Editor editor;
    private SharedPreferences sp;
    public static SPUtils getInstance()
    {
        if (mInstance == null)
            try
            {
                if (mInstance == null)
                    mInstance = new SPUtils();
            }
            finally
            {
            }
        return mInstance;
    }
    public void deleteKey(String paramString)
    {
        this.editor.remove(paramString);
        this.editor.commit();
    }
    public boolean getBoolean(String key, boolean defValue)
    {
        return this.sp.getBoolean(key, true);
    }
    public int getInt(String paramString, int paramInt)
    {
        return this.sp.getInt(paramString, paramInt);
    }
    public String getString(String key, String defValue)
    {
        return this.sp.getString(key, defValue);
    }
    public void initSp(Context paramContext)
    {
        this.sp = paramContext.getSharedPreferences("Config", 0);
        this.editor = this.sp.edit();
    }
    public void putBoolean(String key, boolean Value)
    {
        this.editor.putBoolean(key, Value);
        this.editor.commit();
    }
    public void putInt(String paramString, int paramInt)
    {
        this.editor.putInt(paramString, paramInt);
        this.editor.commit();
    }
    public void putString(String paramString1, String paramString2)
    {
        this.editor.putString(paramString1, paramString2);
        this.editor.commit();
    }
}


