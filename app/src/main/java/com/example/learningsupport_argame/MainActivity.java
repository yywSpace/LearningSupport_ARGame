package com.example.learningsupport_argame;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.example.learningsupport_argame.Client.ClientLab;
import com.example.learningsupport_argame.Client.UDPClient;
import com.example.learningsupport_argame.MonitorModel.MonitorTaskStatusService;
import com.example.learningsupport_argame.Navi.Activity.LocationService;
import com.example.learningsupport_argame.UserManagement.Login.LoginActivity;
import com.example.learningsupport_argame.UserManagement.Login.SplashActivity;
import com.example.learningsupport_argame.UserManagement.User;
import com.example.learningsupport_argame.UserManagement.UserLab;
import com.example.learningsupport_argame.UserManagement.UserMessage.UserMessageActivity;
import com.example.learningsupport_argame.Community.friend.FriendListActivity;
import com.example.learningsupport_argame.Task.activity.TaskListActivity;
import com.example.learningsupport_argame.UserManagement.bag.UserBagActivity;
import com.example.learningsupport_argame.UserManagement.ranking.RankingActivity;
import com.unity3d.player.OnUnityExit;
import com.unity3d.player.UnityPlayerOperationActivity;


public class MainActivity extends UnityPlayerOperationActivity {
    private static String TAG = "MainActivity";
    public final static int TASK_ACTIVITY = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FriendListActivity.IN_CHAT_ROOM = true;
        SendMessage("Canvas", "LoadIpEndPoint", ClientLab.sIp + "," + "3002");
        mOnUnityExit = new OnUnityExit() {
            @Override
            public void onUnityExit() {
                Log.d(TAG, "onDestroy: LocationService" + LocationService.mLocationServiceIntent);
                Log.d(TAG, "onDestroy: MonitorTaskStatusService" + MonitorTaskStatusService.mMonitorTaskStatusServiceIntent);
//                startActivity(new Intent(MainActivity.this, TaskListActivity.class));
//                LocationService.mLocationServiceIntent = new Intent(MainActivity.this, LocationService.class);
//                MonitorTaskStatusService.mMonitorTaskStatusServiceIntent = new Intent(MainActivity.this, MonitorTaskStatusService.class);
//                startService(LocationService.mLocationServiceIntent);
//                startService(MonitorTaskStatusService.mMonitorTaskStatusServiceIntent);
            }
        };
    }

    public void startFriendActivity() {
        Toast.makeText(this, "startFriendActivity", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, FriendListActivity.class);
        intent.putExtra(FriendListActivity.IN_CHAT_ROOM_LABEL, true);
        startActivity(intent);
    }

    public void startTaskActivity() {
        Intent intent = new Intent(this, TaskListActivity.class);
        intent.putExtra("task_list_activity_type", 2);
        startActivity(intent);
        Toast.makeText(this, "startTaskActivity", Toast.LENGTH_SHORT).show();
    }

    public void startUserMessageActivity() {
        Toast.makeText(this, "startUserMessageActivity", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, UserMessageActivity.class));
    }

    public void startRankingActivity() {
        Toast.makeText(this, "startUserRankingActivity", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, RankingActivity.class));
    }

    public void startBagActivity() {
        Toast.makeText(this, "startBagActivity", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, UserBagActivity.class));
    }

    public void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

}