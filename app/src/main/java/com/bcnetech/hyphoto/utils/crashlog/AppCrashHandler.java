package com.bcnetech.hyphoto.utils.crashlog;

import android.os.Environment;
import android.util.Log;

import java.io.File;


public class AppCrashHandler extends AppCrashLog{

	
	private static AppCrashHandler mCrashHandler = null;
	
	private AppCrashHandler(){};
	
	public static AppCrashHandler getInstance() {
		
		if(mCrashHandler == null) {
			mCrashHandler = new AppCrashHandler();
		}
		return mCrashHandler;
	} 
	@Override
	public void initParams() {
		// TODO Auto-generated method stub
		Log.e("************", "initParams");
		CACHE_LOG = Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator+"log";
		
	}

	@Override
	public void sendCrashLogToServer(File file) {
		// TODO Auto-generated method stub
		
		Log.e("************", "sendCrashLogToServer");
	}

}
