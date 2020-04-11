package com.example.learningsupport_argame.Task.activity;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;


import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.learningsupport_argame.DbUtils;
import com.example.learningsupport_argame.R;
import com.example.learningsupport_argame.Task.Task;
import com.example.learningsupport_argame.Task.TaskLab;
import com.example.learningsupport_argame.Task.TaskShowView;
import com.example.learningsupport_argame.Task.adapter.TaskItemAdapter;
import com.example.learningsupport_argame.Task.fragment.TaskListFragment;
import com.example.learningsupport_argame.UserManagement.UserLab;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TaskSearchActivity extends AppCompatActivity {
    private String TAG = "TaskSearchActivity";
    private RecyclerView mRecyclerView;
    private List<Task> mSearchList;
    private TaskItemAdapter mTaskItemAdapter;
    private String mQueryWord;

    @Override
    public void onCreate(Bundle savedInstanceStated) {
        super.onCreate(savedInstanceStated);
        setContentView(R.layout.task_search_activity_layout);
        mRecyclerView = findViewById(R.id.task_search_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mSearchList = new ArrayList<>();
        mTaskItemAdapter = new TaskItemAdapter(mSearchList, this);
        mRecyclerView.setAdapter(mTaskItemAdapter);
        Toolbar toolbar = findViewById(R.id.task_search_toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        // Enable the Up button
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }

        // Get the intent, verify the action and get the query
        Intent intent = getIntent();
        handleIntent(intent);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            SearchRecentSuggestionsLimited suggestions = new SearchRecentSuggestionsLimited(this,
                    TaskSearchSuggestionProvider.AUTHORITY, TaskSearchSuggestionProvider.MODE, 10);
            suggestions.saveRecentQuery(query, null);
            mQueryWord = query;
            getSupportActionBar().setTitle(TaskListFragment.sCurrentListType + "--" + mQueryWord);
            if (TaskListFragment.sCurrentListType.equals("已接受的任务")) {
                mTaskItemAdapter.setOnRecycleViewItemClick((v, position) -> {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    TaskShowView taskShowView = new TaskShowView(this);
                    taskShowView.initData(mSearchList.get(position));
                    builder.setView(taskShowView.getView());
                    builder.setTitle("任务详情");
                    builder.setPositiveButton("删除", (dialog, which) -> {
                        new Thread(() -> {
                            DbUtils.update(null,
                                    "DELETE FROM task_participant WHERE task_id = ? AND participant_id = ?",
                                    mSearchList.get(position).getTaskId(), UserLab.getCurrentUser().getId());
                        }).start();
                    });
                    builder.setNegativeButton("取消", null);
                    builder.show();
                });
                new Thread(() -> {
                    List<Task> taskList = TaskLab.getAcceptedTaskByFuzzyName(mQueryWord);
                    mSearchList.addAll(taskList);
                    runOnUiThread(() -> mTaskItemAdapter.notifyDataSetChanged());
                }).start();
            } else if (TaskListFragment.sCurrentListType.equals("任务列表")) {
                mTaskItemAdapter.setOnRecycleViewItemClick((v, position) -> {
                    TaskShowView taskShowView = new TaskShowView(this);
                    taskShowView.initData(mSearchList.get(position));
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setView(taskShowView.getView());
                    builder.setTitle("任务详情");
                    builder.setPositiveButton("接受", (dialog, which) -> {
                        if (UserLab.getCurrentUser().getHp() < 2) {
                            new AlertDialog.Builder(this)
                                    .setTitle("血量过少")
                                    .setMessage("当前血量过少，无法接受任务。\n您可以使用血量道具或等待血量恢复")
                                    .setPositiveButton("确定", null)
                                    .show();
                            return;
                        }
                        new Thread(() -> {
                            TaskLab.acceptTask(mSearchList.get(position));
                        }).start();
                    });
                    builder.setNegativeButton("取消", null);
                    builder.show();
                });
                new Thread(() -> {
                    List<Task> taskList = new ArrayList<>();
                    taskList.addAll(TaskLab.getFriendTask());
                    taskList.addAll(TaskLab.getAllPeopleTask());
                    taskList.addAll(TaskLab.getClubTask());
                    mSearchList.addAll(taskList.stream().filter(task -> task.getTaskName().contains(mQueryWord)).collect(Collectors.toList()));
                    runOnUiThread(() -> mTaskItemAdapter.notifyDataSetChanged());
                }).start();
            }
            mTaskItemAdapter.notifyDataSetChanged();
        }
    }
}
