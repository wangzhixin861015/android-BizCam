package com.bcnetech.bcnetechlibrary.activity;

import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;

import com.bcnetech.bcnetechlibrary.R;
import com.bcnetech.bcnetechlibrary.dialog.ChoiceDialog;
import com.bcnetech.bcnetechlibrary.dialog.DGProgressDialog;
import com.bcnetech.bcnetechlibrary.dialog.DGProgressDialog3;
import com.bcnetech.bcnetechlibrary.util.ActivityManager;
import com.bcnetech.bcnetechlibrary.util.LogUtil;
import com.bcnetech.bcnetechlibrary.util.StringUtil;
import com.bcnetech.bcnetechlibrary.util.ToastUtil;
import com.bcnetech.bcnetechlibrary.util.Watch.Watcher;
import com.github.mjdev.libaums.UsbMassStorageDevice;
import com.github.mjdev.libaums.fs.FileSystem;
import com.github.mjdev.libaums.fs.UsbFile;
import com.github.mjdev.libaums.fs.UsbFileOutputStream;
import com.github.mjdev.libaums.partition.Partition;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by a1234 on 16/8/19.
 */

public abstract class BaseActivity extends FragmentActivity implements Watcher {

    private DGProgressDialog3 transformdialog;
    private DGProgressDialog circledialog;
    private ChoiceDialog choiceDialog;
    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
    private UsbFile cFolder;//当前目录
    private List<UsbFile> usbFiles = new ArrayList<>();
    private UsbMassStorageDevice[] storageDevices;
    private ExecutorService executorService;
    private ProgressDialog dialog_wait;
    protected Animation enterAnimation;
    protected Animation exitAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityManager.addActivity(this);
        View view = getWindow().getDecorView();
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                view.setBackground(null);
            } else {
                getWindow().setBackgroundDrawable(null);
            }
        } catch (Exception e) {
            LogUtil.e(e.toString());
        }
        setStatusBarColor();
        registerReceiver();
        dialog_wait = new ProgressDialog(this);
        dialog_wait.setCancelable(false);
        dialog_wait.setCanceledOnTouchOutside(false);
        executorService = Executors.newCachedThreadPool();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        initGuide();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    protected abstract void initView();

    protected abstract void initData();

    protected abstract void onViewClick();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mUsbReceiver != null) {//有注册就有注销
            unregisterReceiver(mUsbReceiver);
            mUsbReceiver = null;
        }
        ActivityManager.removeActivity(this);
        dissmissDialog();

    }

    /**
     * 修改状态栏颜色
     */
    private void setStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = BaseActivity.this.getWindow();
            //取消设置透明状态栏,使 ContentView 内容不再覆盖状态栏
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            //需要设置这个 flag 才能调用 setStatusBarColor 来设置状态栏颜色
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            //设置状态栏颜色
            window.setStatusBarColor(getResources().getColor(R.color.translucent));

            ViewGroup mContentView = (ViewGroup) this.findViewById(Window.ID_ANDROID_CONTENT);
            View mChildView = mContentView.getChildAt(0);
            if (mChildView != null) {
                //注意不是设置 ContentView 的 FitsSystemWindows, 而是设置 ContentView 的第一个子 View . 预留出系统 View 的空间.
                ViewCompat.setFitsSystemWindows(mChildView, true);
            }
            //修改下部虚拟按键颜色
           /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setNavigationBarColor(Color.parseColor("#1bb5d7"));
            }*/
        }
    }


    public void showDialog() {
        if (circledialog == null) {
            circledialog = new DGProgressDialog(this, getResources().getString(R.string.waiting));
        }
        circledialog.show();
    }

    public void showTransformDialog() {
        if (transformdialog == null) {
            transformdialog = new DGProgressDialog3(this, getResources().getString(R.string.waiting));
        }
        transformdialog.show();
    }

    public void dissmissDialog() {
        if (circledialog != null) {
            circledialog.dismiss();
        }
        if (transformdialog != null) {
            transformdialog.dismiss();
        }

    }

    protected void showChoiceDialog(String title, String message) {
        if (choiceDialog == null) {
            choiceDialog = ChoiceDialog.createInstance(this);
        }
        choiceDialog.setAblumTitleBlack(title);
        choiceDialog.setAblumMessageGray(message);
        choiceDialog.show();
    }

    protected void setChoiceDialogInterface(ChoiceDialog.CardDialogCallback choiceInterface) {
        if (choiceDialog == null) {
            choiceDialog = ChoiceDialog.createInstance(this);
        }
        choiceDialog.setChoiceCallback(choiceInterface);
    }

    protected void showChoiceDialog(String message) {
        if (choiceDialog == null) {
            choiceDialog = ChoiceDialog.createInstance(this);
        }
        choiceDialog.show();
        choiceDialog.setTitle(message);
    }

    protected void dissmissChoiceDialog() {
        if (choiceDialog != null) {
            choiceDialog.dismiss();
        }
    }


    protected Handler getHandler() {
        return new Handler();
    }

    /**
     * 设置文本
     *
     * @param textView
     * @param text
     */
    protected void initTextView(TextView textView, String text) {
        if (StringUtil.isBlank(text)) {
            textView.setVisibility(View.VISIBLE);
            textView.setText(text);
        } else {
            textView.setVisibility(View.GONE);
        }
    }


    /***
     * 观察者接口
     * @param parms
     */
    @Override
    public void updateNotify(Object... parms) {

    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        overridePendingTransition(R.anim.activity_anim_in, R.anim.cache_anim);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.activity_anim_out);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
        overridePendingTransition(R.anim.activity_anim_in, R.anim.cache_anim);
    }


    public static float getRatio(int screenWidth, int screenHeight) {

        float ratioWidth = (float) screenWidth / 1080;
        float ratioHeight = (float) screenHeight / 1920;
        float RATIO = Math.min(ratioWidth, ratioHeight);
        return RATIO; //字体太小也不好看的
    }

    private void registerReceiver() {
        //监听otg插入 拔出
        IntentFilter usbDeviceStateFilter = new IntentFilter();
        usbDeviceStateFilter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        usbDeviceStateFilter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        registerReceiver(mUsbReceiver, usbDeviceStateFilter);
        //注册监听自定义广播
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        registerReceiver(mUsbReceiver, filter);

    }

    private BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                case ACTION_USB_PERMISSION://接受到自定义广播
                    ToastUtil.toast("接收到自定义广播");
                    UsbDevice usbDevice = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {  //允许权限申请
                        if (usbDevice != null) {  //Do something
                            ToastUtil.toast("用户已授权，可以进行读取操作");
                            readDevice(getUsbMass(usbDevice));
                        } else {

                            ToastUtil.toast("未获取到设备信息");
                        }
                    } else {
                        ToastUtil.toast("用户未授权，读取失败");
                    }
                    break;
                case UsbManager.ACTION_USB_DEVICE_ATTACHED://接收到存储设备插入广播
                    UsbDevice device_add = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (device_add != null) {
                        ToastUtil.toast("接收到存储设备插入广播，尝试读取");
                        redDeviceList();
                    }
                    break;
                case UsbManager.ACTION_USB_DEVICE_DETACHED://接收到存储设备拔出广播
                    UsbDevice device_remove = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (device_remove != null) {
                        ToastUtil.toast("接收到存储设备拔出广播");
                        usbFiles.clear();//清除
                        cFolder = null;
                    }
                    break;
            }
        }
    };

    private UsbMassStorageDevice getUsbMass(UsbDevice usbDevice) {
        for (UsbMassStorageDevice device : storageDevices) {
            if (usbDevice.equals(device.getUsbDevice())) {
                return device;
            }
        }
        return null;
    }

    private void readDevice(UsbMassStorageDevice device) {
        try {
            device.init();//初始化
            Partition partition = device.getPartitions().get(0);
            FileSystem currentFs = partition.getFileSystem();
            UsbFile root = currentFs.getRootDirectory();//获取根目录
            String deviceName = currentFs.getVolumeLabel();//获取设备标签
            ToastUtil.toast("正在读取U盘" + deviceName);
            cFolder = root;//设置当前文件对象
            usbFiles.clear();
            for (UsbFile file : cFolder.listFiles()) {
                usbFiles.add(file);
            }
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtil.toast("读取失败，异常：" + e.getMessage());
        }
    }

    public void redDeviceList() {
        //ToastUtil.toast("开始读取设备列表...");
        UsbManager usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        //获取存储设备
        storageDevices = UsbMassStorageDevice.getMassStorageDevices(this);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
        for (UsbMassStorageDevice device : storageDevices) {//可能有几个 一般只有一个 因为大部分手机只有1个otg插口
            if (usbManager.hasPermission(device.getUsbDevice())) {//有就直接读取设备是否有权限
                ToastUtil.toast("检测到有权限，直接读取");
                readDevice(device);
            } else {//没有就去发起意图申请
                ToastUtil.toast("检测到设备，但是没有权限，进行申请");
                usbManager.requestPermission(device.getUsbDevice(), pendingIntent); //该代码执行后，系统弹出一个对话框，
            }
        }
       /* if (storageDevices.length == 0) {
            ToastUtil.toast("未检测到有任何存储设备插入");
        }*/
    }

    public void readSDFile(final List<File> f, final UsbFile folder) {
        dialog_wait.setMessage("正在导出,请耐心等待...");
        dialog_wait.show();

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                UsbFile usbFile = null;
                for (File sdFile : f) {
                    try {
                        if (usbFiles != null && usbFiles.size() > 0) {
                            boolean isExist = false;
                            for (UsbFile uf : usbFiles) {
                                if (uf.getName().equals(sdFile.getName())) {
                                    isExist = true;
                                    //uf.delete();
                                    usbFile = uf;
                                    break;
                                }
                            }
                            if (isExist) {
                                saveSDFile2OTG(usbFile, sdFile);
                            } else {
                                usbFile = folder.createFile(sdFile.getName());
                                if (folder == cFolder) {//如果是在当前目录 就添加到集合中
                                    usbFiles.add(usbFile);
                                }
                                saveSDFile2OTG(usbFile, sdFile);
                            }
                        }

                    } catch (IOException e) {

                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.toast("全部导出到U盘！");
                        dialog_wait.dismiss();
                    }
                });
            }
        });


    }

    private void saveSDFile2OTG(final UsbFile usbFile, final File f) {
        try {//开始写入
            FileInputStream fis = new FileInputStream(f);//读取选择的文件的
            UsbFileOutputStream uos = new UsbFileOutputStream(usbFile);
            redFileStream(uos, fis);
        } catch (final Exception e) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // ToastUtil.toast("写入" + e.getMessage() + "出错,执行删除");
                    try {
                        usbFile.delete();
                    } catch (IOException e1) {
                        // ToastUtil.toast("删除" + usbFile.getName() + "失败");
                    }
                }
            });
        }
    }

    private void redFileStream(OutputStream os, InputStream is) throws IOException {
        /**
         *  写入文件到U盘同理 要获取到UsbFileOutputStream后 通过
         *  f.createNewFile();调用 在U盘中创建文件 然后获取os后
         *  可以通过输出流将需要写入的文件写到流中即可完成写入操作
         */
        int bytesRead = 0;
        byte[] buffer = new byte[1024 * 8];
        while ((bytesRead = is.read(buffer)) != -1) {
            os.write(buffer, 0, bytesRead);
        }
        os.flush();
        os.close();
        is.close();
    }

    public UsbFile getcFolder() {
        return cFolder;
    }

    protected void initGuide() {
        enterAnimation = new AlphaAnimation(0f, 1f);
        enterAnimation.setDuration(300);
        enterAnimation.setFillAfter(true);

        exitAnimation = new AlphaAnimation(1f, 0f);
        exitAnimation.setDuration(300);
        exitAnimation.setFillAfter(true);
    }
}
