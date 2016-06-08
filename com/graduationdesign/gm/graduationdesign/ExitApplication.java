package com.graduationdesign.gm.graduationdesign;

import android.app.Activity;
import android.app.Application;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Administrator on 2016/5/6.
 */
public class ExitApplication extends Application {
    private List<Activity> exitList = new LinkedList<>();
    private static ExitApplication exitInstance = null;
    private ExitApplication() {

    }

    public static ExitApplication getInstance() {
        if (exitInstance == null) {
            synchronized (ExitApplication.class) {
                if (exitInstance == null) {
                    exitInstance = new ExitApplication();
                }
            }
        }
        return exitInstance;
    }

    public void  addActivity(Activity activity) {
        exitList.add(activity);
    }

    public void exit() {
        for (Activity activity :
               exitList ) {
            activity.finish();
        }
    }

}
