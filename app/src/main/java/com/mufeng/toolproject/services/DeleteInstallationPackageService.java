package com.mufeng.toolproject.services;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;

import com.mufeng.toolproject.utils.Constants;
import com.mufeng.toolproject.utils.FileStorageHelper;
import com.mufeng.toolproject.utils.GmsProperty;

import java.io.File;

/**
 * Created by mufeng on 2017/3/16.
 */
public class DeleteInstallationPackageService extends Service {
    private static final int DELETE_FILE_COMPLETE = 0;
    private static final String TAG = "DeleteInstallationPackageService";
    private String mPackageName;
    private String mFilePath;

    @Override
    public void onCreate() {
        super.onCreate();
        System.out.println(TAG + "onCreate()");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        System.out.println(TAG + "onStartCommand()");
        mPackageName = intent.getStringExtra(Constants.PACKAGE_NAME_KEY);
        if (null != mPackageName) {
            mFilePath = GmsProperty.getStringProperty(this, mPackageName);
            System.out.println("--mFilePath--:"+mFilePath);
        }
        new DeleteFileThread().start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println(TAG + "onDestroy()");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * 子线程用来做一些耗时操作
     */
    public class DeleteFileThread extends Thread {
        @Override
        public void run() {
            super.run();
            if (null != mFilePath) {
                FileStorageHelper.deleteFile(new File(mFilePath));
            }
            mHandler.sendEmptyMessage(DELETE_FILE_COMPLETE);
        }
    }

    /**
     * 消息处理
     */
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DELETE_FILE_COMPLETE:
                    stopSelf();
                    break;
                default:
                    break;
            }
        }
    };
}
