package com.mufeng.toolproject.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by zhangqing on 2017/3/16.
 */
public class GmsProperty {

    /**
     * 设置string型属性值
     *
     * @param context 上下文
     * @param key     属性ID
     * @param value   String 型属性值
     */
    public static void setStringProperty(Context context, String key, String value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.GMS_PROPERTY, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    /**
     * 获取String型属性值
     *
     * @param context 上下文
     * @param key     属性ID
     * @return 返回String 型属性值
     */
    public static String getStringProperty(Context context, String key) {
        return context.getSharedPreferences(Constants.GMS_PROPERTY, 0).getString(key, Constants.DEFAULT_STRING);
    }
}
