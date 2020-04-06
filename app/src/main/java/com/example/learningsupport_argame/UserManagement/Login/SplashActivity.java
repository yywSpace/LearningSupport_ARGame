package com.example.learningsupport_argame.UserManagement.Login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.learningsupport_argame.R;
import com.example.learningsupport_argame.Task.activity.TaskListActivity;

public class SplashActivity extends AppCompatActivity {
    private String TAG = "SplashActivity";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recycler_view_loading_item);
        int type = getIntent().getIntExtra("loading_type", -1);
        new Thread(() -> {
            while (true) {
                Log.d(TAG, "onCreate: " + LoginActivity.sInitFinished);
                if (LoginActivity.sInitFinished) {

                    if (type == 1) {
                        startActivity(new Intent(this, TaskListActivity.class));
                    }
                    LoginActivity.sInitFinished = false;
                    finish();
                    break;
                }
            }
        }).start();
    }
}
