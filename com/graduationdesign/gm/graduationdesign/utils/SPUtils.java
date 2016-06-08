package com.graduationdesign.gm.graduationdesign.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Administrator on 2016/5/6.
 */
public class SPUtils {
    private static SharedPreferences getSP(Context context) {
        SharedPreferences profile = context.getSharedPreferences("profile", Context.MODE_PRIVATE);
        return profile;
    }

    /**
     * 向SP中添加数据
     * @param key
     * @param value
     * @param context
     */
    public static void addInfo(String key,String value,Context context) {
        SharedPreferences sp = getSP(context);
        SharedPreferences.Editor edit = sp.edit();
        if (value == null) {
            edit.putString(key, "");
        } else {
            edit.putString(key, value);
        }
        edit.commit();
    }

    /**
     * 返回SP中指定键的数据
     * @param key
     * @param context
     * @return
     */
    public static String getInfo(String key,Context context) {
        SharedPreferences sp = getSP(context);
        String result = null;
        if (key != null) {
            result = sp.getString(key, "");
        }
        return result;
    }

    /**
     * 删除SP中指定键的数据
     * @param key
     * @param context
     */
    public static void removeInfo(String key,Context context) {
        SharedPreferences sp = getSP(context);
        if (key != null) {
            sp.edit().remove(key);
        }
    }
}
