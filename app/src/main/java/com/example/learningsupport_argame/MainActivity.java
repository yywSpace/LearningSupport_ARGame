package com.example.learningsupport_argame;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import com.example.ARModel.SendMessageActivity;
import com.example.learningsupport_argame.Course.CourseMainActivity;
import com.example.learningsupport_argame.FeedbackModel.FeedbackDetailsActivity;
import com.example.learningsupport_argame.Navi.Activity.MapActivity;
import com.example.learningsupport_argame.UserManagement.ActivityUtil;
import com.example.learningsupport_argame.UserManagement.Login.LoginActivity;
import com.example.learningsupport_argame.community.activity.FriendList_Main;
import com.example.learningsupport_argame.task.activity.TaskListActivity;


public class MainActivity extends SendMessageActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityUtil.addActivity(this);
        SharedPreferences userInfo = getSharedPreferences(LoginActivity.PREFS_NAME, MODE_PRIVATE);
        SendMessage("UIController", "GetUserAccount", userInfo.getString("account", ""));
    }

    public void startFriendActivity() {
        Toast.makeText(this, "startFriendActivity", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, FriendList_Main.class));
    }

    public void startTaskActivity() {
        Toast.makeText(this, "startTaskActivity", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, TaskListActivity.class));
    }

    public void startCourseActivity() {
        Toast.makeText(this, "startCourseActivity", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, CourseMainActivity.class));
    }

    public void startFeedbackActivity() {
        Toast.makeText(this, "startFeedbackActivity", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, FeedbackDetailsActivity.class));
    }

    public void startMapActivity() {
        Toast.makeText(this, "startMapActivity", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, MapActivity.class));
    }

    public void startLoginActivity() {
        Toast.makeText(this, "startFeedbackActivity", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, LoginActivity.class));
    }

    public void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public void logout() {
        SharedPreferences userInfo = getSharedPreferences(LoginActivity.PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = userInfo.edit();//获取Editor
        editor.clear();
        editor.commit();
    }
}
