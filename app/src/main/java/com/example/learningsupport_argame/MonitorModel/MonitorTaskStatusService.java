package com.example.learningsupport_argame.MonitorModel;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.learningsupport_argame.ARModel.Items.ModelInfo;
import com.example.learningsupport_argame.ARModel.Items.ModelInfoLab;
import com.example.learningsupport_argame.UserManagement.UserLab;
import com.example.learningsupport_argame.Task.Task;
import com.example.learningsupport_argame.Task.TaskLab;

import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Optional;


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
                Log.d(TAG, "侦测任务-" + TaskLab.mAcceptedTaskList.size());
                // TODO: 20-1-14 增加消息栏通知功能
                for (Task task : TaskLab.mAcceptedTaskList) {
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    try {
                        Date begin = df.parse(task.getTaskStartAt());
                        Date end = df.parse(task.getTaskEndIn());
                        // 如果已经接受的任务开始结束在当前时间之后则，任务结束
                        if (end.before(now)) {
                            Log.d(TAG, "任务结束-" + task.getTaskName());
                            MonitorInfo monitorInfo = MonitorInfoLab.getMonitorInfoByTaskId(task.getTaskId());
                            // 设置此任务为结束状态
                            task.setTaskStatus("已结束");
                            TaskLab.updateTask(task);
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
                                }
                            } else {
                                TaskLab.updateTaskParticipantStatus(
                                        task.getTaskId() + "",
                                        UserLab.getCurrentUser().getId() + "",
                                        "失败");
                            }

                        }
                        // 如果begin--now--end(now在begin之后end之前)，任务开始
                        if (now.after(begin) && now.before(end)) {
                            Log.d(TAG, "任务开始-" + task.getTaskName());
                            if (!TaskLab.mRunningTaskList.contains(task)) {
                                TaskLab.mRunningTaskList.add(task);
                                Log.d(TAG, "onCreate: " + task.getTaskStartAt() + " " + task.getTaskEndIn());
                                Intent monitorIntent = new Intent(this, MonitorActivity.class);
                                monitorIntent.putExtra("task", task);
                                String currentTime = df.format(now);
                                monitorIntent.putExtra("current_time", currentTime);
                                monitorIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
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
