package com.example.learningsupport_argame.Task.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.learningsupport_argame.R;
import com.example.learningsupport_argame.Task.Task;
import com.example.learningsupport_argame.Task.TaskLab;

import java.util.List;

public class TaskCanAcceptListFragment extends TaskListFragment {
    private TextView mTaskTitle;
    private static AppCompatActivity mActivity;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        mTaskItemAdapter.setOnRecycleViewItemClick((v, position) -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            View taskDetailView = getLayoutInflater().inflate(R.layout.task_current_fragment_layout, null);//获取自定义布局
            initView(taskDetailView);
            initData(mTaskList.get(position));
            builder.setView(taskDetailView);
            builder.setTitle("任务详情");
            builder.setPositiveButton("接受", (dialog, which) -> {
                new Thread(() -> {
                    TaskLab.acceptTask(mTaskList.get(position));
                }).start();
            });
            builder.setNegativeButton("取消", null);
            builder.show();
        });
        mTaskTitle = view.findViewById(R.id.taskTitle);
        mTaskTitle.setText("任务列表");
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // 获取数据
        new Thread(() -> {
            List<Task> tasks = TaskLab.getCanAcceptTask();
            Log.d(TAG, "onResume: " + tasks.size());
            mTaskList.clear();
            mTaskList.addAll(tasks);
            getActivity().runOnUiThread(() -> {
                mTaskItemAdapter.notifyDataSetChanged();
            });
        }).start();
    }


    public static TaskCanAcceptListFragment getInstance(AppCompatActivity activity) {
        TaskCanAcceptListFragment fragment = new TaskCanAcceptListFragment();
        mActivity = activity;
//        Bundle args = new Bundle();
//        args.putString(User.CURRENT_USER_ID, userId);
//        fragment.setArguments(args);
        return fragment;
    }

}