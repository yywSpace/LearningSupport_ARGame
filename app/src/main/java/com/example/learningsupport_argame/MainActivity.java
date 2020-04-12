package com.example.learningsupport_argame;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import androidx.appcompat.app.AlertDialog;

import com.example.learningsupport_argame.Client.ClientLab;
import com.example.learningsupport_argame.MonitorModel.MonitorTaskStatusService;
import com.example.learningsupport_argame.Navi.Activity.LocationService;
import com.example.learningsupport_argame.Task.Task;
import com.example.learningsupport_argame.Task.TaskLab;
import com.example.learningsupport_argame.Task.activity.TaskListFragmentActivity;
import com.example.learningsupport_argame.Task.fragment.TaskListFragment;
import com.example.learningsupport_argame.UserManagement.UserMessage.UserMessageActivity;
import com.example.learningsupport_argame.Community.friend.FriendListActivity;
import com.example.learningsupport_argame.UserManagement.bag.UserBagActivity;
import com.example.learningsupport_argame.UserManagement.ranking.RankingActivity;
import com.unity3d.player.OnUnityExit;
import com.unity3d.player.UnityPlayerOperationActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends UnityPlayerOperationActivity {
    private static String TAG = "MainActivity";

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

    public void startAnnouncementActivity() {
        startActivity(new Intent(this, AnnouncementActivity.class));
    }

    public void startFriendActivity() {
        Toast.makeText(this, "startFriendActivity", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, FriendListActivity.class);
        intent.putExtra(FriendListActivity.IN_CHAT_ROOM_LABEL, true);
        startActivity(intent);
    }

    public void startTaskActivity() {
        Intent intent = new Intent(this, TaskListFragmentActivity.class);
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