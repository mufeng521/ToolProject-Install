package com.mufeng.toolproject.Activitys;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.mufeng.toolproject.R;
import com.mufeng.toolproject.install.InstallOperationHelper;
import com.mufeng.toolproject.utils.ApkManager;
import com.mufeng.toolproject.utils.Constants;
import com.mufeng.toolproject.utils.FileStorageHelper;
import com.mufeng.toolproject.utils.GmsProperty;
import com.mufeng.toolproject.utils.ZipFileHelper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * 该界面主要功能是把assets目录和res/raw下的文件复制到设备的指定目录
 * Created by mufeng on 2017/3/24.
 */
public class FileStorageActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "FileStorageActivity";
    public static boolean mIsSmartInstall = false;//控制只能自己的app才能执行智能安装
    private static final int UNZIP_FILE_START = 0;
    private static final int UNZIP_FILE_COMPLETE = 1;
    private static final int COPY_RAW_FILE_START = 2;
    private static final int COPY_RAW_FILE_COMPLETE = 3;
    private static final int SILENT_INSTALL_START = 4;
    private static final int SILENT_INSTALL_COMPLETE = 5;
    private static final int SILENT_UNINSTALL_START = 6;
    private static final int SILENT_UNINSTALL_COMPLETE = 7;
    private static final String BASE_PATH = Environment.getExternalStorageDirectory().toString();

    private int mStage;
    private Context mContext;
    private ArrayList<File> mFileList = null;
    private ArrayList<File> mInstallApkFiles = null;
    private File mFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_storage);
        mContext = this;
        initWidget();
    }

    /**
     * 初始化控件
     */
    private void initWidget() {
        findViewById(R.id.assets_btn).setOnClickListener(this);
        findViewById(R.id.raw_btn).setOnClickListener(this);
        findViewById(R.id.slient_install_btn).setOnClickListener(this);
        findViewById(R.id.slient_uninstall_btn).setOnClickListener(this);
        findViewById(R.id.set_smart_btn).setOnClickListener(this);
        findViewById(R.id.smart_install_btn).setOnClickListener(this);
        findViewById(R.id.unroot_slient_install_btn).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.assets_btn:
                mProgressDialog.setMessage("安装包解压中，请稍等...");
                mProgressDialog.show();
                mStage = UNZIP_FILE_START;
                new UpZipFileThread().start();
                break;
            case R.id.raw_btn:
                mProgressDialog.setMessage("raw目录下文件复制中，请稍等...");
                mProgressDialog.show();
                mStage = COPY_RAW_FILE_START;
                new UpZipFileThread().start();
                break;
            case R.id.slient_install_btn:
                if (null != mInstallApkFiles) {
                    mProgressDialog.setMessage("静默安装中，请稍等...");
                    mProgressDialog.show();
                    mStage = SILENT_INSTALL_START;
                    new SilentInstallThread().start();
                } else {
                    Toast.makeText(mContext, "请选择安装文件!", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.slient_uninstall_btn:
                mProgressDialog.setMessage("静默卸载中，请稍等...");
                mProgressDialog.show();
                mStage = SILENT_UNINSTALL_START;
                new SilentInstallThread().start();
                break;
            case R.id.set_smart_btn:
                ApkManager.setSmartInatall(mContext);
                break;
            case R.id.smart_install_btn:
                if (null != mInstallApkFiles) {
                    mIsSmartInstall = true;
                    ApkManager.installApk(mContext, mInstallApkFiles);
                } else {
                    Toast.makeText(mContext, "请选择安装文件!", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.unroot_slient_install_btn:
                Log.d(TAG, "--点击按钮--:");
                if (null != mInstallApkFiles) {
                    InstallOperationHelper.installApk(mContext, mInstallApkFiles);
                }
                break;
        }

    }

    /**
     * 子线程用来做一些耗时操作
     */
    public class UpZipFileThread extends Thread {
        @Override
        public void run() {
            super.run();
            switch (mStage) {
                case UNZIP_FILE_START:
                    upZipFile();
                    mHandler.sendEmptyMessage(UNZIP_FILE_COMPLETE);
                    break;
                case COPY_RAW_FILE_START:
                    mFile = FileStorageHelper.copyFilesFromRaw(mContext, R.raw.doc_test, "doc_test", BASE_PATH + "/" + "mufeng");
                    mHandler.sendEmptyMessage(COPY_RAW_FILE_COMPLETE);
                    break;
            }
        }
    }

    /**
     * 解压安装包
     */
    private void upZipFile() {
        FileStorageHelper.copyFilesFromAssets(this, "install_test.zip", BASE_PATH + "/" + "mufeng");
        mFileList = FileStorageHelper.fileList;
        FileStorageHelper.fileList = new ArrayList<>();
        if (null != mFileList && mFileList.size() != 0) {
            for (File file : mFileList) {
                try {
                    mInstallApkFiles = ZipFileHelper.upZipFile(file, BASE_PATH + "/" + "mufeng");
                    System.out.println("--解压后的文件数目--path--:" + mInstallApkFiles.size());
                    //删除解压完成的压缩文件
                    FileStorageHelper.deleteFile(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 静默安装APK
     */
    public class SilentInstallThread extends Thread {
        @Override
        public void run() {
            super.run();
            switch (mStage) {
                case SILENT_INSTALL_START:
                    for (File file1 : mInstallApkFiles) {
                        System.out.println("--解压后的文件路径--path--:" + file1.getPath());
                        String packageName = ApkManager.getPackageName(mContext, file1);

                        System.out.println("--installApk--packageName--:" + packageName);
                        if (null != packageName) {
                            GmsProperty.setStringProperty(mContext, packageName, file1.getPath());
                        }
                        boolean result = InstallOperationHelper.slientInstall(file1.getPath());
                        Message msg = new Message();
                        Bundle bundle = new Bundle();
                        bundle.putBoolean("result", result);
                        msg.what = SILENT_INSTALL_COMPLETE;
                        msg.setData(bundle);
                        mHandler.sendMessage(msg);
                    }
                    break;
                case SILENT_UNINSTALL_START:
                    boolean result = InstallOperationHelper.slientUninstall(Constants.PACKAGE_NAME);
                    Message msg = new Message();
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("result", result);
                    msg.what = SILENT_UNINSTALL_COMPLETE;
                    msg.setData(bundle);
                    mHandler.sendMessage(msg);
                    break;
            }
        }
    }


    /**
     * 消息处理
     */
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle;
            boolean mResult;
            String content;
            if (null != mProgressDialog && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
            switch (msg.what) {
                case UNZIP_FILE_COMPLETE:
                    if (null != mInstallApkFiles && mInstallApkFiles.size() != 0) {
                        content = "文件解压成功!";
                    } else {
                        content = "文件解压失败!";
                    }
                    Toast.makeText(mContext, content, Toast.LENGTH_SHORT).show();
                    break;
                case COPY_RAW_FILE_COMPLETE:
                    if (null != mFile) {
                        content = "文件复制成功!";
                    } else {
                        content = "文件复制失败!";
                    }
                    Toast.makeText(mContext, content, Toast.LENGTH_SHORT).show();
                    break;
                case SILENT_INSTALL_COMPLETE:
                    bundle = msg.getData();
                    mResult = bundle.getBoolean("result");
                    if (mResult) {
                        content = "静默安装成功!";
                    } else {
                        content = "静默安装失败!";
                    }
                    Toast.makeText(mContext, content, Toast.LENGTH_SHORT).show();
                    break;
                case SILENT_UNINSTALL_COMPLETE:
                    bundle = msg.getData();
                    mResult = bundle.getBoolean("result");
                    if (mResult) {
                        content = "静默卸载成功!";
                    } else {
                        content = "静默卸载失败!";
                    }
                    Toast.makeText(mContext, content, Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };

}
