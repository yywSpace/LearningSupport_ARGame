package com.example.learningsupport_argame.Client;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.learningsupport_argame.Community.friend.FriendListActivity;
import com.example.learningsupport_argame.R;

import java.net.SocketException;
import java.net.UnknownHostException;

public class ClientLab {
    public static int sPort = 3000;
    public static String sUserName = "android";
    public static String sIp = "47.96.152.133";//"192.168.1.105";//

    private static UDPClient sUDPClient;

    public static UDPClient getInstance(Context context, int port, String ip, String userName) {
        if (sUDPClient == null) {
            try {
                sUDPClient = new UDPClient(port, ip, userName);
                sUDPClient.setOnReceiveUserChat((name, content) -> {
                    int notificationId = 100;//通知的id
                    final String CHANNEL_ID = "CHAT_CHANNEL_ID";
                    final String CHANNEL_NAME = "CHAT_CHANNEL_NAME";
                    NotificationManager mNotificationManager = (NotificationManager)
                            context.getSystemService(Context.NOTIFICATION_SERVICE);

                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        //只在Android O之上需要渠道
                        NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID,
                                CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
                        //如果这里用IMPORTANCE_NOENE就需要在系统的设置里面开启渠道，
                        //通知才能正常弹出
                        mNotificationManager.createNotificationChannel(notificationChannel);
                    }
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID);
                    builder.setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle("接收到" + name + "发来的信息")
                            .setContentText(String.format("%s:%s", name, content))
                            .setAutoCancel(true);
                    if (!FriendListActivity.IN_CHAT_ROOM) {
                        Intent resultIntent = new Intent(context, FriendListActivity.class);
                        resultIntent.setAction(Intent.ACTION_MAIN);
                        resultIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                        //building the notification
                        builder.setContentIntent(resultPendingIntent);
                    }
                    mNotificationManager.notify(notificationId, builder.build());
                });
            } catch (SocketException | UnknownHostException e) {
                e.printStackTrace();
            }
        }
        return sUDPClient;
    }

    public static UDPClient getClient() {
        return sUDPClient;
    }
}
