package com.example.learningsupport_argame.task.TaskPopWindow;


import android.content.DialogInterface;
import android.os.Handler;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.learningsupport_argame.R;
import com.example.learningsupport_argame.task.Task;
import com.example.learningsupport_argame.task.TaskLab;

import java.util.ArrayList;
import java.util.List;

public class TaskListView {
    private String mCurrentUserId;
    private RecyclerView mTaskRecycleView;
    private TaskItemAdapter mTaskItemAdapter;
    private List<Task> mTaskList;
    private AppCompatActivity mContext;
    private View mView;
    private Handler mHandler;

    public TaskListView(String currentUserId, AppCompatActivity content) {
        mCurrentUserId = currentUserId;
        mContext = content;
        mView = mContext.getLayoutInflater().inflate(R.layout.tasklist_redo_layout, null, false);
        mTaskRecycleView = mView.findViewById(R.id.list_task_redo);
        mHandler = new Handler();
        mTaskList = new ArrayList<>();
        // 获取数据
        new Thread(() -> {
            List<Task> tasks = TaskLab.getAllTask(mCurrentUserId);
            mTaskList.clear();
            mTaskList.addAll(tasks);
            mContext.runOnUiThread(() -> {
                mHandler.post(() -> {
                    mTaskItemAdapter.notifyDataSetChanged();
                });
            });
        }).start();
        mTaskItemAdapter = new TaskItemAdapter(mTaskList, mContext);
        mTaskItemAdapter.setOnRecycleViewItemClick((v, position) -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            CurrentTaskView currentTaskView = new CurrentTaskView(mContext);
            currentTaskView.initCurrentTaskData(mTaskList.get(position));
            builder.setView(currentTaskView.getView());
            builder.setTitle("任务详情");
            builder.setPositiveButton("确定", (dialog, which) -> currentTaskView.setView(null));
            builder.setNegativeButton("取消", (dialog, which) -> currentTaskView.setView(null));
            builder.show();
        });
        mTaskRecycleView.setAdapter(mTaskItemAdapter);
        mTaskRecycleView.setLayoutManager(new LinearLayoutManager(mContext));
        mTaskRecycleView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
    }

    public View getView() {
        return mView;
    }
}