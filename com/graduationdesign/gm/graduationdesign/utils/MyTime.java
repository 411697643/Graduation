package com.graduationdesign.gm.graduationdesign.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2016/5/6.
 */
public class MyTime {
    /**
     * 格式化当前时间
     * 格式为：mm-DD HH:mm:ss
     * @return 时间字符串
     */
    public static String getTime() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd HH:mm:ss");
        return simpleDateFormat.format(date);
    }

    /**
     * 格式化指定时间
     * @param time 时间
     * @return 时间字符串
     */
    public static String getTime(long time) {
        Date date = new Date(time);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd HH:mm:ss");
        return simpleDateFormat.format(date);
    }
}
