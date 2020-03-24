package com.example.learningsupport_argame.MonitorModel;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
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
    private Intent mMonitorIntent;
    // 假设一次只能有一个任务开始
    public static boolean alreadyBegan = false;

    @Override
    public void onCreate() {
        super.onCreate();
        new Thread(() -> {
            while (UserLab.getCurrentUser() == null) ;
            TaskLab.getAcceptedTask(UserLab.getCurrentUser().getId() + "");
        }).start();
        mTaskStatusThread = new Thread(() -> {
            Thread currentThread = Thread.currentThread();
            while (mTaskStatusThread == currentThread) {
                if (TaskLab.mAcceptedTaskList == null) {
                    continue;
                }
                // 10秒检测一次, 减轻开销
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                // TODO: 20-1-14 增加消息栏通知功能
                for (Task task : TaskLab.mAcceptedTaskList) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Date now = new Date();
                    try {
                        Date begin = df.parse(task.getTaskStartAt());
                        Date end = df.parse(task.getTaskEndIn());
                        // 如果已经接受的任务开始时间在当前时间之后则任务结束
                        if (end.before(now)) {
                            TaskLab.mAcceptedTaskList.remove(task);
                            Log.d(TAG, "任务结束-" + task.getTaskName());
                            MonitorInfo monitorInfo = MonitorInfoLab.getMonitorInfoByTaskId(task.getTaskId());
                            // 设置此任务为结束状态
                            task.setTaskStatus("已结束");
                            TaskLab.updateTask(task);
                            // 判断是否有监督记录，如果没有则任务失败,否则判断任务是否成功
                            if (monitorInfo != null) {
                                // 设置任务参与者完成状态
                                if (MonitorInfoLab.testTaskSuccess(monitorInfo)) {
                                    TaskLab.updateTaskParticipantStatus(
                                            monitorInfo.getTaskId() + "",
                                            UserLab.getCurrentUser().getId() + "",
                                            "完成");
                                } else {
                                    TaskLab.updateTaskParticipantStatus(
                                            monitorInfo.getTaskId() + "",
                                            UserLab.getCurrentUser().getId() + "",
                                            "失败");
                                    int inattentionTime = (int) ((monitorInfo.getTaskTotalTime() -
                                            monitorInfo.getMonitorAttentionTime()) / 60);
                                    if (inattentionTime >= 30)
                                        UserLab.getCurrentUser().gettingHeart(2);
                                    else
                                        UserLab.getCurrentUser().gettingHeart(1);
                                    UserLab.updateUser(UserLab.getCurrentUser());
                                }
                            } else {
                                TaskLab.updateTaskParticipantStatus(
                                        task.getTaskId() + "",
                                        UserLab.getCurrentUser().getId() + "",
                                        "失败");
                                UserLab.getCurrentUser().gettingHeart(2);
                                UserLab.updateUser(UserLab.getCurrentUser());
                            }
                        }

                        // 如果begin--now--end(now在begin之后end之前)，任务开始
                        if (now.after(begin) && now.before(end)) {
                            if (alreadyBegan)
                                continue;
                            alreadyBegan = true;
                            Log.d(TAG, "任务开始-" + task.getTaskName());
                            if (!TaskLab.mRunningTaskList.contains(task)) {
                                TaskLab.mRunningTaskList.add(task);
                                Log.d(TAG, "onCreate: " + task.getTaskStartAt() + " " + task.getTaskEndIn());
                                Intent monitorIntent = new Intent(this, MonitorActivity.class);
                                monitorIntent.putExtra("task", task);
                                monitorIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                                // 打开监督服务
                                mMonitorIntent = new Intent(this, MonitorTaskAccomplishService.class);
                                mMonitorIntent.putExtra("task", task);
                                String currentTime = df.format(now);
                                mMonitorIntent.putExtra("current_time", currentTime);
                                startService(mMonitorIntent);
                                MonitorActivity.sMonitorIntent = mMonitorIntent;
                                // 打开监督界面
                                getApplication().startActivity(monitorIntent);
                                // 设置任务状态为正执行
                                new Thread(() -> {
                                    task.setTaskStatus("正执行");
                                    TaskLab.updateTask(task);
                                }).start();
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
