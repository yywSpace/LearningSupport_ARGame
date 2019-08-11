package com.example.learningsupport_argame.MonitorModel;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.util.Log;
import android.widget.RemoteViews;

import com.example.learningsupport_argame.R;

import java.util.Date;
import java.text.SimpleDateFormat;

/**
 * 任务开始时创建，完成或失败后销毁
 * 用于向MonitorActivity传递各项信息
 * 手机使用时间为任务时间
 */

// todo 定位代码最后在实现（没有地图环境）

public class MonitorService extends Service {
    private static final String TAG = MonitorService.class.getSimpleName();
    private PowerManager mPowerManager;
    private Thread mMonitorThread;
    private int mTaskScreenOnTime;
    private int mAttentionTime;
    private int mPhoneUseCount;
    private long mRemainingTime;
    private String CHANNEL_ID = "change notification";
    private static final int NOTIFICATION_FOREGROUND_ID = 1;
    private NotificationManager mNotificationManager;
    private RemoteViews mRemoteViews;
    private boolean hasAwake;
    private Task mTask;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: ");
        mRemoteViews = new RemoteViews(getPackageName(), R.layout.monitor_notification_layout);

        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotificationManager.createNotificationChannel(
                new NotificationChannel(CHANNEL_ID, "channel notification", NotificationManager.IMPORTANCE_DEFAULT));

        // 设备处于唤醒状态
        mPowerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mMonitorThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "run: ");
                while (!Thread.interrupted()) {

                    if (outOfDistanceRange()) {
                        Message outOfDistanceMessage = new Message();
                        outOfDistanceMessage.what = 4;
                    }


                    if (mRemainingTime <= 0) {
                        Message taskSuccessFinishMessage = new Message();
                        taskSuccessFinishMessage.what = 3;
                    }
                    mRemainingTime--;


                    if (isAttention())
                        mAttentionTime++;
                    if (isScreenOn())
                        mTaskScreenOnTime++;
                    else
                        hasAwake = false;

                    if (isScreenOn() && !hasAwake) {
                        mPhoneUseCount++;
                        hasAwake = true;
                    }
                    Message message = new Message();
                    Bundle data = new Bundle();
                    data.putString(MonitorInfo.TASK_SCREEN_ON_TIME, second2Time(mTaskScreenOnTime));
                    data.putString(MonitorInfo.ATTENTION_TIME, second2Time(mAttentionTime));
                    data.putString(MonitorInfo.PHONE_USE_COUNT, mPhoneUseCount + "");
                    data.putLong(MonitorInfo.TASK_REMANDING_TIME, mRemainingTime);
                    message.setData(data);
                    message.what = 1;
                    MonitorActivity.handler.sendMessage(message);
                    Message timeUpdateMessage = new Message();
                    timeUpdateMessage.what = 2;
                    MonitorActivity.handler.sendMessage(timeUpdateMessage);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Notification notification = new Notification.Builder(MonitorService.this, CHANNEL_ID)
                            .setSmallIcon(R.drawable.ic_launcher_background)
                            .setCustomContentView(mRemoteViews)
                            .setOnlyAlertOnce(true)
                            .build();

                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.setAction(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_LAUNCHER);
                    intent.setComponent(new ComponentName(MonitorService.this.getPackageName(), MonitorService.this.getPackageName() + ".MonitorModel.MonitorActivity"));

                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                    // 点击跳转到主界面
                    PendingIntent intent_go = PendingIntent.getActivity(getApplicationContext(), 5, intent,
                            PendingIntent.FLAG_UPDATE_CURRENT);
                    mRemoteViews.setOnClickPendingIntent(R.id.monitor_notification, intent_go);


                    mRemoteViews.setTextViewText(R.id.monitor_notification_task_name, mTask.getTaskName());
                    mRemoteViews.setTextViewText(R.id.monitor_notification_task_remaining_time, second2Time(mRemainingTime));
                    mRemoteViews.setTextViewText(R.id.monitor_notification_task_begin_time, mTask.getTaskStartAt().substring(5));
                    mRemoteViews.setTextViewText(R.id.monitor_notification_task_end_time, mTask.getTaskEndIn().substring(5));
                    mRemoteViews.setTextViewText(R.id.monitor_notification_task_phone_use_time, second2Time(mTaskScreenOnTime));
                    mRemoteViews.setTextViewText(R.id.monitor_notification_task_phone_use_count, mPhoneUseCount + "");
                    mRemoteViews.setTextViewText(R.id.monitor_notification_task_attention_time, second2Time(mAttentionTime));

                    startForeground(NOTIFICATION_FOREGROUND_ID, notification);
                }
            }
        });
    }

    boolean isAttention() {
        return MonitorActivity.isActivityOn;
    }

    boolean isScreenOn() {
        return mPowerManager.isInteractive();
    }


    public void setTask(Task task) {
        mTask = task;
        mRemainingTime = remainingTime(mTask.getTaskStartAt(), mTask.getTaskEndIn());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: ");
        // 开启线程
        if (!mMonitorThread.isAlive())
            mMonitorThread.start();


        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new MonitorBinder();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMonitorThread.interrupt();
    }


    public static String second2Time(long second) {
        long sec = second % 60;
        long minute = second / 60;
        long hour = minute / 60;
        return hour + ":" + minute + ":" + sec;

    }

    // 2019/2/12/20:30
    public static long remainingTime(String begin, String end) {

        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd/HH:mm");
        Date d1 = null, d2 = null;
        try {
            d1 = df.parse(begin);
            d2 = df.parse(end);
        } catch (Exception e) {
        }
        long diff = d2.getTime() - d1.getTime();
        long seconds = diff / 1000;
        return seconds;
    }

    boolean outOfDistanceRange() {
        return false;
    }

    public class MonitorBinder extends Binder {
        public MonitorService getService() {
            return MonitorService.this;
        }
    }


}
