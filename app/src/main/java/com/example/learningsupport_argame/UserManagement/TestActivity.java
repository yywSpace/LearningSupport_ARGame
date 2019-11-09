package com.example.learningsupport_argame.UserManagement;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import com.example.learningsupport_argame.R;
import com.example.learningsupport_argame.task.TaskPopWindow.TaskListPopWindow;

public class TestActivity extends AppCompatActivity {
    private static String TAG = "TestActivity";
    private Button mStartButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mStartButton = findViewById(R.id.start);
        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TaskListPopWindow(TestActivity.this);
            }
        });
    }
}

