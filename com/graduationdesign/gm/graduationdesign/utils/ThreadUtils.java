package com.graduationdesign.gm.graduationdesign.utils;


import android.os.Handler;
import android.os.Looper;

/**
 * Created by Administrator on 2016/5/6.
 */
public class ThreadUtils {
    public static void runInThread(Runnable runnable) {
        new Thread(runnable).start();
    }

    public static Handler handler = new Handler(Looper.getMainLooper());

    public static void runUIThread(Runnable runnable) {
        handler.post(runnable);
    }
}
