package com.example.learningsupport_argame.UserManagement.UserMessage;

import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.learningsupport_argame.R;
import com.example.learningsupport_argame.UserManagement.UserLab;

public class UserAcceptedTaskActivity extends AppCompatActivity {
    private FrameLayout mReturnFrameLayout;
    private TextView mTaskTitle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_management_task_activity);
        mReturnFrameLayout = findViewById(R.id.user_management_task_return);
        mReturnFrameLayout.setOnClickListener(v -> finish());
        mTaskTitle = findViewById(R.id.toolbar_title);
        mTaskTitle.setText("已接受的任务");
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.user_management_task_container,
                        UserAcceptedTaskFragment.getInstance(UserLab.getCurrentUser().getId() + ""),
                        null)
                .commit();
    }
}
