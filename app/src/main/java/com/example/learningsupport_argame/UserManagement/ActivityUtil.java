package com.example.learningsupport_argame.UserManagement;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

// 此方法可能造成内存泄漏，不过此例中界面只有两个，影响可以忽略
public class ActivityUtil {
    private static List<Activity> sActivityList = new ArrayList<>();

    // onCreate 调用，添加Activity
    public static void addActivity(Activity activity) {
        sActivityList.add(activity);
    }
    public static void removeActivity(Activity activity) {
        sActivityList.remove(activity);
    }
    // 销毁所有，退出程序
    public static void destroyAll() {
        for (Activity activity : sActivityList) {
            activity.finish();
        }
    }
}
