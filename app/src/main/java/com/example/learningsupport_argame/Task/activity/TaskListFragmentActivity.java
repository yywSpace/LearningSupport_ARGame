package com.example.learningsupport_argame.Task.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.learningsupport_argame.R;
import com.example.learningsupport_argame.Task.fragment.TaskListFragment;

public class TaskListFragmentActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_container_layout);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container, TaskListFragment.getInstance(2))
                .commit();
    }
}
