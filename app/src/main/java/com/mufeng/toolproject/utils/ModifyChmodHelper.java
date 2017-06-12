package com.mufeng.toolproject.utils;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;

/**
 * Created by zhangqing on 2017/3/13.
 */
public class ModifyChmodHelper {

    /**
     * 修改文件权限
     *
     * @param command
     */
    public static void initChmod(String command) {
        Process process = null;
        DataOutputStream dos = null;
        try {
            process = Runtime.getRuntime().exec("su");
            System.out.println("--initChmod---process--:" + process);
            dos = new DataOutputStream(process.getOutputStream());
            dos.writeBytes(command + "\n");
            dos.writeBytes("exit\n");
            dos.flush();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("--initChmod---catch--:" + process);
        } finally {
            System.out.println("--initChmod---finally--:" + process);
            try {
                if (null != dos) {
                    dos.close();
                }
                process.destroy();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 修改目录、文件权限
     *
     * @param path
     */
    public static void initChmodDir(String path) {
        File file = new File(path);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
        }

        int status = -1;
        Process process = null;
        try {
            process = Runtime.getRuntime().exec("chmod 777 " + file);
//            process = Runtime.getRuntime().exec("chmod 777 " + file.getParentFile());
            status = process.waitFor();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (status == 0) {//chmod succeed
            System.out.println("--initChmodDir--chmod succeed--status--:" + status);
        } else {//chmod failed
            System.out.println("--initChmodDir--chmod failed--status--:" + status);
        }

    }
}
