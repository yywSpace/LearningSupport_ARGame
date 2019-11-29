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

import com.baidu.location.BDLocation;
import com.baidu.mapapi.model.LatLng;
import com.example.learningsupport_argame.Navi.Activity.LocationService;
import com.example.learningsupport_argame.Navi.Utils.MapUtils;
import com.example.learningsupport_argame.R;
import com.example.learningsupport_argame.Task.Task;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 任务开始时创建，完成或失败后销毁
 * 用于向MonitorActivity传递各项信息
 * 手机使用时间为任务期间亮屏时间
 */

public class MonitorTaskAccomplishService extends Service {
    private static final String TAG = MonitorTaskAccomplishService.class.getSimpleName();
    private PowerManager mPowerManager;
    private int mTaskScreenOnTime;
    private int mTaskScreenOffTime;
    private int mAttentionTime;
    private int mPhoneUseCount;
    private boolean mIsOutOfRange = false;
    private int mOutOfRangeTime = 0;
    private int mRemainingTime = -1;
    private String CHANNEL_ID = "change notification";
    private static final int NOTIFICATION_FOREGROUND_ID = 1;
    private NotificationManager mNotificationManager;
    private RemoteViews mRemoteViews;
    private boolean hasAwake;
    private boolean hasSetTask = false;
    private Task mTask;
    private Timer mTimer;

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
        mTimer = new Timer();
        new Thread(() -> {
            mTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (mTask == null && mRemainingTime < 0) {
                        return;
                    }
                    mRemainingTime--;

                    // 判断是否超出任务地点
                    String[] location = mTask.getAccomplishTaskLocation().split(",");
                    float task_lat = Float.parseFloat(location[1]);
                    float task_lng = Float.parseFloat(location[2]);
                    BDLocation bdLocation = LocationService.getCurrentLocation();
                    if (bdLocation != null) {
                        if (distanceRangeIn(200,
                                new LatLng(task_lat, task_lng),
                                new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude()))) {
                            mOutOfRangeTime++;
                            mIsOutOfRange = true;
                        } else {
                            mIsOutOfRange = false;
                        }
                    }

                    if (isScreenOn())
                        mTaskScreenOnTime++;
                    else {
                        hasAwake = false;
                        mTaskScreenOffTime++;
                    }

                    if (isAttention())
                        mAttentionTime++;

                    // 锁屏并打开计作一次手机使用
                    if (isScreenOn() && !hasAwake) {
                        mPhoneUseCount++;
                        hasAwake = true;
                    }

                    // 传输数据
                    Message message = new Message();
                    Bundle data = new Bundle();
                    data.putInt(MonitorInfo.TASK_SCREEN_ON_TIME, mTaskScreenOnTime);
                    data.putInt(MonitorInfo.TASK_SCREEN_OFF_TIME, mTaskScreenOffTime);
                    data.putInt(MonitorInfo.ATTENTION_TIME, mAttentionTime);
                    data.putInt(MonitorInfo.PHONE_USE_COUNT, mPhoneUseCount);
                    data.putInt(MonitorInfo.TASK_REMANDING_TIME, mRemainingTime);
                    data.putInt(MonitorInfo.TASK_OUT_OF_RANGE_TIME, mOutOfRangeTime);
                    message.setData(data);
                    MonitorActivity.mMonitorHandler.sendMessage(message);

                    Notification notification = new Notification.Builder(MonitorTaskAccomplishService.this, CHANNEL_ID)
                            .setSmallIcon(R.drawable.map_task_icon)
                            .setCustomContentView(mRemoteViews)
                            .setOnlyAlertOnce(true)
                            .build();

                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.setAction(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_LAUNCHER);
                    intent.setComponent(new ComponentName(MonitorTaskAccomplishService.this.getPackageName(), MonitorTaskAccomplishService.this.getPackageName() + ".MonitorModel.MonitorActivity"));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                    // 点击跳转到主界面
                    PendingIntent intent_go = PendingIntent.getActivity(getApplicationContext(), 5, intent,
                            PendingIntent.FLAG_UPDATE_CURRENT);
                    mRemoteViews.setOnClickPendingIntent(R.id.monitor_notification, intent_go);

                    mRemoteViews.setTextViewText(R.id.monitor_notification_task_name, mTask.getTaskName());
                    mRemoteViews.setTextViewText(R.id.monitor_notification_task_remaining_time, TimeUtils.second2Time(mRemainingTime));
                    mRemoteViews.setTextViewText(R.id.monitor_notification_task_begin_time, mTask.getTaskStartAt().substring(5));
                    mRemoteViews.setTextViewText(R.id.monitor_notification_task_end_time, mTask.getTaskEndIn().substring(5));
                    mRemoteViews.setTextViewText(R.id.monitor_notification_task_phone_use_time, TimeUtils.second2Time(mTaskScreenOnTime));
                    mRemoteViews.setTextViewText(R.id.monitor_notification_task_phone_use_count, mPhoneUseCount + "");
                    mRemoteViews.setTextViewText(R.id.monitor_notification_task_attention_time, TimeUtils.second2Time(mAttentionTime));
                    startForeground(NOTIFICATION_FOREGROUND_ID, notification);
                }
            }, 0, 1000);
        }).start();

    }

    boolean isAttention() {
        return MonitorActivity.isActivityOn;
    }

    boolean isScreenOn() {
        return mPowerManager.isInteractive();
    }


    public void setTask(Task task) {
        if (!hasSetTask) {
            mTask = task;
            mRemainingTime = (int) TimeUtils.remainingTime(mTask.getTaskStartAt(), mTask.getTaskEndIn(), "yyyy-MM-dd HH:mm");
            hasSetTask = true;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new MonitorBinder();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mTimer.cancel();
        Log.d(TAG, "onDestroy: ");
    }

    /**
     * true 超出，false未超出
     *
     * @param distance 距离任务地点距离
     * @param self     自身经纬度
     * @param task     任务完成地点经纬度
     * @return
     */
    boolean distanceRangeIn(int distance, LatLng self, LatLng task) {
        return MapUtils.distance(self.latitude, self.longitude, task.latitude, task.longitude) >= distance;
    }

    public class MonitorBinder extends Binder {
        public MonitorTaskAccomplishService getService() {
            return MonitorTaskAccomplishService.this;
        }
    }
}
