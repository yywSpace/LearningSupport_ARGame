package com.example.learningsupport_argame;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.ARModel.UnityPlayerActivity;
import com.example.learningsupport_argame.Course.CourseMainActivity;
import com.example.learningsupport_argame.FeedbackModel.FeedbackDetailsActivity;
import com.example.learningsupport_argame.community.activity.FriendList_Main;
import com.example.learningsupport_argame.task.activity.TaskList_Main;


public class MainActivity extends UnityPlayerActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void startFriendActivity() {
        Toast.makeText(this, "startFriendActivity", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, FriendList_Main.class));
    }

    public void startTaskActivity() {
        Toast.makeText(this, "startTaskActivity", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, TaskList_Main.class));
    }

    public void startCourseActivity() {
        Toast.makeText(this, "startCourseActivity", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, CourseMainActivity.class));
    }

    public void startFeedbackActivity() {
        Toast.makeText(this, "startFeedbackActivity", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, FeedbackDetailsActivity.class));
    }

}
