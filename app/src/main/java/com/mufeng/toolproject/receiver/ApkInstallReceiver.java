package com.mufeng.toolproject.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.mufeng.toolproject.Activitys.FileStorageActivity;
import com.mufeng.toolproject.services.DeleteInstallationPackageService;
import com.mufeng.toolproject.utils.Constants;


/**
 * Created by mufeng on 2017/3/16.
 */
public class ApkInstallReceiver extends BroadcastReceiver {
    private static final String TAG = "ApkInstallReceiver";
    private String mAction;
    private String mPackageName;

    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println(TAG + "--onReceive--");
        if (null != intent) {
            mAction = intent.getAction();
            mPackageName = intent.getData().getSchemeSpecificPart();
            System.out.println(TAG + "-1-onReceive--mPackageName--：" + mPackageName);
            if (mAction.equals("android.intent.action.PACKAGE_ADDED")) {//接收安装广播
                if (mPackageName.equals(Constants.PACKAGE_NAME)) {
                    FileStorageActivity.mIsSmartInstall = false;
                    System.out.println(TAG + "--onReceive--应用安装成功!--");
                    Intent serviceIntent = new Intent(context, DeleteInstallationPackageService.class);
                    serviceIntent.putExtra(Constants.PACKAGE_NAME_KEY, mPackageName);
                    context.startService(serviceIntent);
                }

            } else if (mAction.equals("android.intent.action.PACKAGE_REMOVED")) {//接收卸载广播
                if (mPackageName.equals(Constants.PACKAGE_NAME)) {
                    System.out.println(TAG + "--onReceive--应用卸载成功!--");
                }
            }
        }
    }
}
