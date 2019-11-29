package com.example.learningsupport_argame.MonitorModel;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.learningsupport_argame.UserManagement.UserLab;
import com.example.learningsupport_argame.Task.Task;
import com.example.learningsupport_argame.Task.TaskLab;

import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;


/**
 * 监控任务是否开始
 */
public class MonitorTaskStatusService extends Service {
    private static final String TAG = "MonitorTaskStatusService";
    private volatile Thread mTaskStatusThread;
    public static Intent mMonitorTaskStatusServiceIntent;

    @Override
    public void onCreate() {
        super.onCreate();
        new Thread(() -> {
            while (UserLab.getCurrentUser() == null) ;
            TaskLab.getAcceptedTask(UserLab.getCurrentUser().getId() + "");
            Task t = new Task();
            t.setTaskName("之间");
            t.setTaskStartAt("2019-11-27 20:30");
            t.setTaskEndIn("2019-11-27 21:30");
            TaskLab.mAcceptedTaskList.add(t);
        }).start();
        mTaskStatusThread = new Thread(() -> {

            Thread currentThread = Thread.currentThread();
            while (mTaskStatusThread == currentThread) {
                if (TaskLab.mAcceptedTaskList == null) {
                    continue;
                }
                // 一秒检测一次
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Date now = new Date();

                for (Task task : TaskLab.mAcceptedTaskList) {
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    try {
                        Date begin = df.parse(task.getTaskStartAt());
                        Date end = df.parse(task.getTaskEndIn());
                        // 如果begin--now--end(now在begin之后end之前)，任务开始
                        if (now.after(begin) && now.before(end)) {
                            if (!TaskLab.mRunningTaskList.contains(task)) {
                                TaskLab.mRunningTaskList.add(task);
                                Log.d(TAG, "onCreate: " + task.getTaskStartAt() + task.getTaskEndIn());
                            }
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }

        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: ");
        // 开启线程
        if (!mTaskStatusThread.isAlive())
            mTaskStatusThread.start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mTaskStatusThread = null;
    }
}
