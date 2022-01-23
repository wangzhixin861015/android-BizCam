package com.bcnetech.hyphoto.utils;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.bcnetech.bcnetechlibrary.dialog.BaseBlurDialog;
import com.bcnetech.bcnetechlibrary.dialog.ChoiceDialog;
import com.bcnetech.bcnetechlibrary.util.ToastUtil;
import com.bcnetech.hyphoto.R;

/**
 * Created by wenbin on 2017/3/1.
 */

public class PermissionUtil {
    private static final int MY_PERMISSIONS_CAMERA = 1;
    private static final int MY_PERMISSIONS_WRITE = 2;
    private static final int MY_PERMISSIONS_FIND_LOCATION = 3;
    private static final int MY_PERMISSIONS_READ_PHONE_STATE = 4;
    private static final int MY_PERMISSIONS_RECORD = 5;

    public static boolean getCameraPermission(Activity activity) {
        return getPermission(activity, Manifest.permission.CAMERA, MY_PERMISSIONS_CAMERA);
    }

    public static boolean getWritePermmission(Activity activity) {
        return getPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE, MY_PERMISSIONS_WRITE);
    }
/*

    public static boolean getReadPhoneState(Activity activity) {
        return getPermission(activity, Manifest.permission.READ_PHONE_STATE, MY_PERMISSIONS_READ_PHONE_STATE);
    }
*/

    public static boolean getFindLocationPermmission(Activity activity) {
        return getPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION, MY_PERMISSIONS_FIND_LOCATION);
    }

    public static boolean getRecordPermission(Activity activity) {
        return getPermission(activity, Manifest.permission.RECORD_AUDIO, MY_PERMISSIONS_FIND_LOCATION);
    }


    public static void onCameraPermissionsResult(Context context, int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
               // ToastUtil.toast(context.getString(R.string.open_camera_permissions));
            } else {
                //ToastUtil.toast(context.getString(R.string.close_camera_permissions));
            }
        } else if (requestCode == MY_PERMISSIONS_WRITE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
               // ToastUtil.toast(context.getString(R.string.open_save_permissions));
            } else {
               // ToastUtil.toast(context.getString(R.string.close_save_permissions));
            }
        } else if (requestCode == MY_PERMISSIONS_FIND_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                if (checkAppops(context, AppOpsManager.OPSTR_FINE_LOCATION)) {
                  //  ToastUtil.toast(context.getString(R.string.open_location_permissions));
                } else {
                    ToastUtil.toast(context.getString(R.string.please_location_permissions));
                    startAppSettings((Activity) context);
                }

            } else {
                //ToastUtil.toast(context.getString(R.string.close_location_permissions));
            }
        } else if (requestCode == MY_PERMISSIONS_READ_PHONE_STATE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //ToastUtil.toast(context.getString(R.string.open_read_permissions));
            } else {
               // ToastUtil.toast(context.getString(R.string.close_read_permissions));
            }
        } else if (requestCode == MY_PERMISSIONS_RECORD) {

        }
    }

    public static boolean forMIUI(final Activity activity) {
         /*   if (!getWritePermmission(activity)&&isMIUI()) {*/
        if (!checkAppops(activity, AppOpsManager.OPSTR_FINE_LOCATION) && isMIUI()) {
            String per = activity.getResources().getString(R.string.go_set);
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle(per);
            //
            builder.setPositiveButton(activity.getResources().getString(R.string.confirm), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    PermissionUtil.startAppSettings(activity);
                    //activity.finish();
                }
            });
            //
            builder.setNegativeButton(activity.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    //activity.finish();
                      /*  ActivityManager manager = (ActivityManager)WelcomeActivity.this.getSystemService(ACTIVITY_SERVICE); //获取应用程序管理器
                        manager.killBackgroundProcesses(getPackageName()); //强制结束当前应用程序*/

                }
            });
            Dialog noticeDialog = builder.create();
            noticeDialog.show();
            return false;
        }
        return true;
    }

    public static boolean forMIUIRead(final Activity activity) {
        if (!getWritePermmission(activity) && isMIUI()) {
            String per = "商拍需要存储权限,是否去设置";
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle(per);
            //
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    PermissionUtil.startAppSettings(activity);
                    activity.finish();
                }
            });
            //
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    activity.finish();
                      /*  ActivityManager manager = (ActivityManager)WelcomeActivity.this.getSystemService(ACTIVITY_SERVICE); //获取应用程序管理器
                        manager.killBackgroundProcesses(getPackageName()); //强制结束当前应用程序*/

                }
            });
            Dialog noticeDialog = builder.create();
            noticeDialog.show();
            return false;
        }
        return true;
    }


    public static void settingPermisson(final Activity activity) {

            final ChoiceDialog choiceDialog=ChoiceDialog.createInstance(activity);

            choiceDialog.setAblumTitle("权限申请");
            choiceDialog.setAblumMessage("在设置中打开所需权限，以正常使用商拍功能");
            choiceDialog.setOk("设置");
            choiceDialog.setCancel("取消");
            choiceDialog.setChoiceCallback(new BaseBlurDialog.CardDialogCallback() {
                @Override
                public void onOKClick() {
                    choiceDialog.dismiss();
                    PermissionUtil.startAppSettings(activity);
                    activity.finish();
                }

                @Override
                public void onCancelClick() {
                    choiceDialog.dismiss();
                    activity.finish();
                }

                @Override
                public void onDismiss() {

                }
            });
            choiceDialog.show();


    }

    public static boolean onAllPermissionsResult(Context context, int requestCode, String[] permissions, int[] grantResults) {
        boolean permissionOK = true;
        for (int i = 0; i < permissions.length; i++) {
            switch (permissions[i]) {
                case Manifest.permission.CAMERA:
                    String percamera = AppOpsManager.OPSTR_CAMERA;
                    if (grantResults.length > 0 && grantResults[i] == PackageManager.PERMISSION_DENIED || !checkAppops(context, percamera)) {
                        ToastUtil.toast(context.getString(R.string.please_open_camera_permissions));
                        permissionOK = false;
                    }
                    break;
                case Manifest.permission.ACCESS_FINE_LOCATION:
                    String perlocat = AppOpsManager.OPSTR_FINE_LOCATION;
                    if (grantResults.length > 0 && grantResults[i] == PackageManager.PERMISSION_DENIED || !checkAppops(context, perlocat)) {
                        ToastUtil.toast(context.getString(R.string.please_location_permissions));
                        permissionOK = false;
                    }
                    break;
                case Manifest.permission.WRITE_EXTERNAL_STORAGE:
                    String perwrite = AppOpsManager.OPSTR_WRITE_EXTERNAL_STORAGE;
                    if (grantResults.length > 0 && grantResults[i] == PackageManager.PERMISSION_DENIED || !checkAppops(context, perwrite)) {
                        permissionOK = false;
                        ToastUtil.toast(context.getString(R.string.please_open_save_permissions));
                    }
                    break;
            }
        }
        return permissionOK;
    }

    public static boolean checkAllPermission(Activity activity) {
        String[] permissionlist = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,
                    permissionlist,
                    1);
            return false;
        }
        return true;
    }

    /**
     * 检查是否开启权限
     * @param activity 上下文
     * @return true 权限已开启 false 部分权限未开启
     */
    public static boolean checkAllPermissionWelcome(Activity activity) {
        String[] permissionlist = {Manifest.permission.CAMERA,

                Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.ACCESS_FINE_LOCATION};
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                ||ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(activity,
                    permissionlist,
                    1);
            return false;
        }
        return true;
    }

    private static boolean getPermission(Activity activity, String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(activity,
                permission)
                //未打开权限
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    permission)) {
                if (permission.equals(Manifest.permission.ACCESS_FINE_LOCATION) && isMIUI()) {
                    String per = AppOpsManager.OPSTR_FINE_LOCATION;
                    if (!checkAppops(activity, per)) {
                        ToastUtil.toast(activity.getString(R.string.please_location_permissions));
                        startAppSettings(activity);
                    }
                } else {
                    ActivityCompat.requestPermissions(activity,
                            new String[]{permission},
                            requestCode);
                }
            } else {
                if (permission.equals(Manifest.permission.ACCESS_FINE_LOCATION) && isMIUI()) {
                    String per = AppOpsManager.OPSTR_FINE_LOCATION;
                    if (!checkAppops(activity, per)) {
                        ToastUtil.toast(activity.getString(R.string.please_location_permissions));
                        startAppSettings(activity);
                    }
                } else {
                    ActivityCompat.requestPermissions(activity,
                            new String[]{permission},
                            requestCode);
                }

            }
            return false;
        }
        if (permission.equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
            String per = AppOpsManager.OPSTR_FINE_LOCATION;
            return checkAppops(activity, per);
        }
        return true;
    }


    public static boolean isMIUI() {
        try {
            BuildProperties prop = BuildProperties.newInstance();
            String str = prop.getProperty("ro.miui.ui.version.name") + ":" + prop.getProperty("ro.miui.ui.version.code");
            if (Build.MANUFACTURER.contains("Xiaomi")) {
                if (str.contains("V8") || str.contains("V9")) {
                    return true;
                }
            }
        } catch (Exception e) {

        }

        return false;
    }

    public static boolean checkAppops(Context context, String op) {
        if (isMIUI()) { // 只有小米手机才检测
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                AppOpsManager appOpsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
                int checkOp = appOpsManager.checkOp(op, Binder.getCallingUid(), context.getPackageName());
                if (checkOp == AppOpsManager.MODE_ALLOWED) {
                    return true;
                } else {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 打开     App设置界面
     */
    public static void startAppSettings(Activity context) {
        Intent intent = new Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + context.getPackageName()));
        context.startActivity(intent);
    }
}
