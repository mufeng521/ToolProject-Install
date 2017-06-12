package com.mufeng.toolproject.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.text.TextUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by mufeng on 2017/3/12.
 */
public class FileStorageHelper {
    private static final String SEPARATOR = File.separator;//路径分隔符
    public static ArrayList<File> fileList = new ArrayList<>();
    private static final int BUFFER_SIZE = 1024 * 1024;//1M Byte

    /**
     * 复制assets中的文件到指定目录
     *
     * @param context     上下文
     * @param assetsPath  assets资源路径
     * @param storagePath 目标文件夹的路径
     */
    public static void copyFilesFromAssets(Context context, String assetsPath, String storagePath) {
        String temp = "";
        File targetFile = null;

        if (TextUtils.isEmpty(storagePath)) {
            return;
        } else if (storagePath.endsWith(SEPARATOR)) {
            storagePath = storagePath.substring(0, storagePath.length() - 1);
        }

        if (TextUtils.isEmpty(assetsPath) || assetsPath.equals(SEPARATOR)) {
            assetsPath = "";
        } else if (assetsPath.endsWith(SEPARATOR)) {
            assetsPath = assetsPath.substring(0, assetsPath.length() - 1);
        }

        AssetManager assetManager = context.getAssets();
        try {
            File file = new File(storagePath);
            if (!file.exists()) {//如果文件夹不存在，则创建新的文件夹
                file.mkdirs();
            }

            // 获取assets目录下的所有文件及目录名
            String[] fileNames = assetManager.list(assetsPath);
            if (fileNames.length > 0) {//如果是目录 apk
                for (String fileName : fileNames) {
                    if (!TextUtils.isEmpty(assetsPath)) {
                        temp = assetsPath + SEPARATOR + fileName;//补全assets资源路径
                    }

                    String[] childFileNames = assetManager.list(temp);
                    if (!TextUtils.isEmpty(temp) && childFileNames.length > 0) {//判断是文件还是文件夹：如果是文件夹
                        copyFilesFromAssets(context, temp, storagePath + SEPARATOR + fileName);
                    } else {//如果是文件
                        InputStream inputStream = assetManager.open(temp);
                        targetFile = readInputStream(storagePath + SEPARATOR + fileName, inputStream);
                        fileList.add(targetFile);
                    }
                }
            } else {//如果是文件 doc_test.txt或者apk/app_test.apk
                InputStream inputStream = assetManager.open(assetsPath);
                if (assetsPath.contains(SEPARATOR)) {//apk/app_test.apk
                    assetsPath = assetsPath.substring(assetsPath.lastIndexOf(SEPARATOR), assetsPath.length());
                }
                targetFile = readInputStream(storagePath + SEPARATOR + assetsPath, inputStream);
                fileList.add(targetFile);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 复制res/raw中的文件到指定目录
     *
     * @param context     上下文
     * @param id          资源ID
     * @param fileName    文件名
     * @param storagePath 目标文件夹的路径
     * @return 返回存储在指定目录的文件
     */
    public static File copyFilesFromRaw(Context context, int id, String fileName, String storagePath) {
        InputStream inputStream = context.getResources().openRawResource(id);
        File file = new File(storagePath);
        if (!file.exists()) {//如果文件夹不存在，则创建新的文件夹
            file.mkdirs();
        }
        return readInputStream(storagePath + SEPARATOR + fileName, inputStream);
    }

    /**
     * 读取输入流中的数据写入输出流
     *
     * @param storagePath 目标文件路径
     * @param inputStream 输入流
     * @return 返回存储在指定目录的文件
     */
    public static File readInputStream(String storagePath, InputStream inputStream) {
        File file = new File(storagePath);
        try {
            if (!file.exists()) {
                // 1.建立通道对象
                FileOutputStream fos = new FileOutputStream(file);
                // 2.定义存储空间
                byte[] buffer = new byte[BUFFER_SIZE];
                // 3.开始读文件
                int length = 0;
                while ((length = inputStream.read(buffer)) != -1) {// 循环从输入流读取buffer字节
                    // 将Buffer中的数据写到outputStream对象中
                    fos.write(buffer, 0, length);
                }
                fos.flush();// 刷新缓冲区
                // 4.关闭流
                fos.close();
                inputStream.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    /**
     * 递归删除目录下的所有文件及子目录下的所有文件
     *
     * @param file 将要删除的文件目录
     * @return 如果所有文件都删除成功则返回true, 有一个文件删除失败就停止删除并返回false
     */
    public static boolean deleteFile(File file) {
        boolean success = false;
        if (file.exists()) {
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                for (File file1 : files) {//递归删除文件或目录
                    success = deleteFile(file1);
                }
            } else {
                success = file.delete();
            }
        } else {
            success = false;
        }
        return success;
    }
}
