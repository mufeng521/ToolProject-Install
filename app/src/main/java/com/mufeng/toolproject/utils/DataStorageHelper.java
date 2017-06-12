package com.mufeng.toolproject.utils;

import android.content.Context;

import org.apache.http.util.EncodingUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by zhangqing on 2017/3/13.
 */
public class DataStorageHelper {

    public static void writeDatabaseFile(String fileName, String fileContext) {
        try {
            File file = new File(fileName);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            byte[] temBytes = fileContext.getBytes();
            fileOutputStream.write(temBytes);
            fileOutputStream.close();
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    public static void readDatabaseFile(String oldPath, String newPath) {
        File oldFile = new File(oldPath);
        String result = "";

        try {
            if (oldFile.exists()) {
                File newFile = new File(newPath);
                if (!newFile.exists()) {
                    newFile.getParentFile().mkdirs();
                }

                FileInputStream fis = new FileInputStream(oldFile);
                FileOutputStream fos = new FileOutputStream(newPath);

                int length = fis.available(); // 获取文件长度
                byte[] buffer = new byte[length];
                int c;
                while ((c = fis.read(buffer)) > 0) {
                    fos.write(buffer, 0, c);

                    // 将byte数组转换成指定格式的字符串
                    result = EncodingUtils.getString(buffer, "UTF-8");
                    System.out.println("---readDatabaseFile--result--:" + result);
                }
                fos.flush();
                fos.close();
                fis.close();
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }

    }

    /**
     * 向指定文件中写数据
     *
     * @param context  上下文
     * @param fileName 文件名
     * @param content  写入内容
     */
    public static void writeFileData(Context context, String fileName, String content) {
        try {
            FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_WORLD_READABLE + Context.MODE_WORLD_WRITEABLE);
            byte[] buffer = content.getBytes();
            fos.write(buffer);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 打开指定文件，读取其数据，返回字符串对象
     *
     * @param context       本应用的上下文
     * @param otherContext  其他应用的上下文
     * @param fileName      目标文件名称
     * @param otherFileName 资源文件名称
     * @return
     */
    public static String readFileData(Context context, Context otherContext, String fileName, String otherFileName) {
        String result = "";
        try {
            FileInputStream fis = otherContext.openFileInput(otherFileName);
            System.out.println("---readFileData--fis--:" + fis);
            FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_WORLD_READABLE + Context.MODE_WORLD_WRITEABLE);

            int length = fis.available(); // 获取文件长度
            byte[] buffer = new byte[length];
            int c;
            while ((c = fis.read(buffer)) > 0) {
                fos.write(buffer, 0, c);
            }
            fos.flush();// 刷新缓冲区
            fos.close();
            fis.close();
            // 将byte数组转换成指定格式的字符串
            result = EncodingUtils.getString(buffer, "UTF-8");
            System.out.println("---readFileData--result--:" + result);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

}
