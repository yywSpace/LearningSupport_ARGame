package com.example.learningsupport_argame.UserManagement.UserMessage;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.learningsupport_argame.R;
import com.example.learningsupport_argame.UserManagement.UserLab;

public class UserReleasedTaskActivity extends AppCompatActivity {
    private FrameLayout mReturnFrameLayout;
    private TextView mTaskTitle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_management_task_activity);
        mTaskTitle = findViewById(R.id.toolbar_title);
        mTaskTitle.setText("已发布的任务");
        mReturnFrameLayout = findViewById(R.id.user_management_task_return);
        mReturnFrameLayout.setOnClickListener(v -> finish());
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.user_management_task_container,
                        UserReleasedTaskFragment.getInstance(UserLab.getCurrentUser().getId() + ""),
                        null)
                .commit();
    }
}
