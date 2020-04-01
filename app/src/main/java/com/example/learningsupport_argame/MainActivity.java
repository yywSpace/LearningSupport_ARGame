package com.example.learningsupport_argame;


import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.learningsupport_argame.UserManagement.User;
import com.example.learningsupport_argame.UserManagement.UserLab;
import com.example.learningsupport_argame.UserManagement.UserMessage.UserMessageActivity;
import com.example.learningsupport_argame.Community.friend.FriendListActivity;
import com.example.learningsupport_argame.Task.activity.TaskListActivity;
import com.example.learningsupport_argame.UserManagement.ranking.RankingActivity;
import com.unity3d.player.UnityPlayerOperationActivity;


public class MainActivity extends UnityPlayerOperationActivity {
    private static String TAG = "NavigationMainActivity";
    public final static int TASK_ACTIVITY = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void startFriendActivity() {
        Toast.makeText(this, "startFriendActivity", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, FriendListActivity.class));
    }

    public void startTaskActivity() {
        Intent intent = new Intent(this, TaskListActivity.class);
        intent.putExtra(User.CURRENT_USER_ID, UserLab.getCurrentUser().getId() + "");
        startActivityForResult(intent, TASK_ACTIVITY);
        Toast.makeText(this, "startTaskActivity", Toast.LENGTH_SHORT).show();
    }

    public void startUserMessageActivity() {
        Toast.makeText(this, "startUserMessageActivity", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, UserMessageActivity.class));
    }

    public void startUserRankingActivity() {
        Toast.makeText(this, "startUserRankingActivity", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, RankingActivity.class));
    }

    public void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}