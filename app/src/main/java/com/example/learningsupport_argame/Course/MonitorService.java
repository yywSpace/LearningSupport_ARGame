package com.example.learningsupport_argame.Course;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.util.Log;
import android.widget.RemoteViews;

import com.example.learningsupport_argame.R;

import java.text.SimpleDateFormat;
import java.util.Date;

/**

 * 任务开始时创建，完成或失败后销毁

 * 用于向MonitorActivity传递各项信息

 * 手机使用时间为任务时间

 */

//

public class MonitorService extends Service{

        private static final String TAG = MonitorService.class.getSimpleName();
        private PowerManager mPowerManager;
        private Thread mMonitorThread;
        private int mTaskScreenOnTime;
        private int mAttentionTime;
        private int mPhoneUseCount=-1;
        private long mRemainingTime;
        private String CHANNEL_ID = "change notification";
        private static final int NOTIFICATION_FOREGROUND_ID = 1;
        private NotificationManager mNotificationManager;
        private RemoteViews mRemoteViews;
        private boolean hasAwake;
        private Task mTask;
        private static boolean isActivityOn;

        private ServiceConnection conn;
        Intent intent;
        String CourseOrTask;


    @Override
        public void onCreate() {
            super.onCreate();

            Log.d(TAG, "onCreate: ");

            //Log.d("onCreateService",CourseOrTask);

            isActivityOn=true;
            mRemoteViews = new RemoteViews(getPackageName(), R.layout.monitor_notification_layout);
            mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mNotificationManager.createNotificationChannel(
                    new NotificationChannel(CHANNEL_ID, "channel notification", NotificationManager.IMPORTANCE_DEFAULT));
        }

//        // 设备处于唤醒状态
//            mPowerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
//            mMonitorThread = new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    Log.d(TAG, "run: ");
//                    while (!Thread.interrupted()) {
//                        if(CourseOrTask.equals("Task")) {
//                            if (outOfDistanceRange()) {
//                                Message outOfDistanceMessage = new Message();
//                                outOfDistanceMessage.what = 4;
//                            }
//                        }
//
//                        if (mRemainingTime <= 0) {
//                            Message taskSuccessFinishMessage = new Message();
//                            taskSuccessFinishMessage.what = 3;
//                        }
//                        mRemainingTime--;
//
//                        if (isAttention())
//                            mAttentionTime++;
//
//                        if (isScreenOn())
//                            mTaskScreenOnTime++;
//
//                        else
//                            hasAwake = false;
//
//                        if (isScreenOn() && !hasAwake) {
//                            mPhoneUseCount++;
//                            hasAwake = true;
//                        }
//                        Message message = new Message();
//                        Bundle data = new Bundle();
//                        data.putString(MonitorInfo.TASK_SCREEN_ON_TIME, second2Time(mTaskScreenOnTime));
//                        data.putString(MonitorInfo.ATTENTION_TIME, second2Time(mAttentionTime));
//                        data.putString(MonitorInfo.PHONE_USE_COUNT, mPhoneUseCount + "");
//                        data.putLong(MonitorInfo.TASK_REMANDING_TIME, mRemainingTime);
//                        message.setData(data);
//                        message.what = 1;
//                        MonitorActivity.handler.sendMessage(message);
//                        Message timeUpdateMessage = new Message();
//                        timeUpdateMessage.what = 2;
//                        MonitorActivity.handler.sendMessage(timeUpdateMessage);
//                        try {
//                            Thread.sleep(1000);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//
//                        Notification notification = null;
//                       if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//                           notification = new Notification.Builder(MonitorService.this, CHANNEL_ID)
//                                   .setSmallIcon(R.drawable.ic_launcher_background)
//                                   .setCustomContentView(mRemoteViews)
//                                   .setOnlyAlertOnce(true)
//                                   .build();
//                           Log.d("channel","dssdssdfsaf");
//                       }
//                       else {
////                           NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(MonitorService.this)
////                                   .setContentTitle("活动")
////                                   .setContentText("您有一项新活动")
////                                   .setSmallIcon(R.drawable.ic_launcher_background)
////                                   .setOngoing(true);
////                                  // .setChannelId(CHANNEL_ID);//无效
////                           notification = notificationBuilder.build();
////                          Log.d("channel","asdf");
////                           mNotificationManager.notify(1, notification);
//                       }
//
//
//                        Intent intent = new Intent(Intent.ACTION_MAIN);
//                        intent.setAction(Intent.ACTION_MAIN);
//                        intent.addCategory(Intent.CATEGORY_LAUNCHER);
//                        intent.setComponent(new ComponentName(MonitorService.this.getPackageName(), MonitorService.this.getPackageName() + ".MonitorModel.MonitorActivity"));
//                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
//
//                        // 点击跳转到主界面
//                        PendingIntent intent_go = PendingIntent.getActivity(getApplicationContext(), 5, intent,
//                                PendingIntent.FLAG_UPDATE_CURRENT);
//                        mRemoteViews.setOnClickPendingIntent(R.id.monitor_notification, intent_go);
//                        if(CourseOrTask.equals("Task"))
//                        mRemoteViews.setTextViewText(R.id.monitor_notification_task_name, mTask.getTaskName());
//                        mRemoteViews.setTextViewText(R.id.monitor_notification_task_remaining_time, second2Time(mRemainingTime));
//                        mRemoteViews.setTextViewText(R.id.monitor_notification_task_begin_time, mTask.getTaskStartAt().substring(5));
//                        mRemoteViews.setTextViewText(R.id.monitor_notification_task_end_time, mTask.getTaskEndIn().substring(5));
//                        mRemoteViews.setTextViewText(R.id.monitor_notification_task_phone_use_time, second2Time(mTaskScreenOnTime));
//                        mRemoteViews.setTextViewText(R.id.monitor_notification_task_phone_use_count, mPhoneUseCount + "");
//                        mRemoteViews.setTextViewText(R.id.monitor_notification_task_attention_time, second2Time(mAttentionTime));
//
//                        startForeground(NOTIFICATION_FOREGROUND_ID, notification);
//                    }
//                }
//            });
        }

        boolean isAttention() {
            return MonitorService.isActivityOn;
        }

        boolean isScreenOn() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
                return mPowerManager.isInteractive();
            }
            else
                return false;
        }





        public void setTask(Task task) {
            mTask = task;
            mRemainingTime = remainingTime(mTask.getTaskStartAt(), mTask.getTaskEndIn());

        }
        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {

            Log.d(TAG, "onStartCommand: ");
            this.intent=intent;
            if(intent==null)
            Log.d("空随想d防守打法是否是","；爱的方式法生产");
            else
            CourseOrTask =this.intent.getStringExtra("CourseOrTask");

            // 设备处于唤醒状态
            mPowerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
            mMonitorThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "run: ");
                    while (!Thread.interrupted()) {
                        if(CourseOrTask.equals("Course")) {
                            if (outOfDistanceRange()) {
                                Message outOfDistanceMessage = new Message();
                                outOfDistanceMessage.what = 4;
                            }
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

                        Notification notification = null;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            notification = new Notification.Builder(MonitorService.this, CHANNEL_ID)
                                    .setSmallIcon(R.drawable.ic_launcher_background)
                                    .setCustomContentView(mRemoteViews)
                                    .setOnlyAlertOnce(true)
                                    .build();
                            Log.d("channel","dssdssdfsaf");
                        }
                        else {
//                           NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(MonitorService.this)
//                                   .setContentTitle("活动")
//                                   .setContentText("您有一项新活动")
//                                   .setSmallIcon(R.drawable.ic_launcher_background)
//                                   .setOngoing(true);
//                                  // .setChannelId(CHANNEL_ID);//无效
//                           notification = notificationBuilder.build();
//                          Log.d("channel","asdf");
//                           mNotificationManager.notify(1, notification);

//                            String channel_id = "my_channel_01";
//                            CharSequence name = "channel_name_01";
//                            NotificationChannel notificationChannel = new NotificationChannel(channel_id, name, NotificationManager.IMPORTANCE_HIGH);
//                            NotificationManager nm = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
//                            nm.createNotificationChannel(notificationChannel);
//                            Notification.Builder builder = new Notification.Builder(context);
//                            //NotificationCompat.Builder builder=new NotificationCompat.Builder(context);
//                            builder.setContentTitle(context.getString(R.string.app_name))
//                                    .setContentText("广播一接收")
//                                    .setSmallIcon(R.drawable.ic_launcher_foreground)
//                                    .setAutoCancel(true)
//                                    .setChannelId(channel_id);
//                            Notification notif = builder.build();
//                            nm.notify(1, notif);//id不同，通知会分别显示，id相同，会用同一个通知栏显示
                        }


                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.setAction(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_LAUNCHER);
                        intent.setComponent(new ComponentName(MonitorService.this.getPackageName(), MonitorService.this.getPackageName() + ".Course.MonitorActivity"));
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);

                        // 点击跳转到主界面
                        PendingIntent intent_go = PendingIntent.getActivity(getApplicationContext(), 5, intent,
                                PendingIntent.FLAG_UPDATE_CURRENT);
                        mRemoteViews.setOnClickPendingIntent(R.id.monitor_notification, intent_go);
                        if(CourseOrTask.equals("Task"))
                            mRemoteViews.setTextViewText(R.id.monitor_notification_task_name, mTask.getTaskName());
                        mRemoteViews.setTextViewText(R.id.monitor_notification_task_remaining_time, second2Time(mRemainingTime));
                        mRemoteViews.setTextViewText(R.id.monitor_notification_task_begin_time, mTask.getTaskStartAt().substring(5));
                        mRemoteViews.setTextViewText(R.id.monitor_notification_task_end_time, mTask.getTaskEndIn().substring(5));
                        mRemoteViews.setTextViewText(R.id.monitor_notification_task_phone_use_time, second2Time(mTaskScreenOnTime));
                        mRemoteViews.setTextViewText(R.id.monitor_notification_task_phone_use_count, mPhoneUseCount + "");
                        mRemoteViews.setTextViewText(R.id.monitor_notification_task_attention_time, second2Time(mAttentionTime));

                        MonitorService.this.startForeground(NOTIFICATION_FOREGROUND_ID, notification);
                    }
                }
            });

            // 开启线程
            if (!mMonitorThread.isAlive())

                mMonitorThread.start();
            return super.onStartCommand(intent, flags, startId);
        }


        @Override
        public IBinder onBind(Intent intent) {

            this.intent=intent;
            CourseOrTask =intent.getStringExtra("CourseOrTask");
            return new MonitorBinder();
        }


        @Override
        public void onDestroy() {

            super.onDestroy();
            Log.d("服务onDestry","被执行");
            mMonitorThread.interrupt();
            MonitorService.isActivityOn=false;


        }

        public static String second2Time(long second) {

            long sec = second % 60;
            long minute1 = second / 60;
            long hour = minute1 / 60;
            long minute=minute1-hour*60;

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

