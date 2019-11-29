package com.example.learningsupport_argame.UserManagement.UserMessage;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.example.learningsupport_argame.DbUtils;
import com.example.learningsupport_argame.R;
import com.example.learningsupport_argame.UserManagement.User;
import com.example.learningsupport_argame.UserManagement.UserLab;
import com.example.learningsupport_argame.Task.Task;
import com.example.learningsupport_argame.Task.TaskLab;
import com.example.learningsupport_argame.Task.fragment.TaskListFragment;

import java.util.List;

public class UserAccomplishTaskFragment extends TaskListFragment {
    private TextView mTaskTitle;

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
            builder.setPositiveButton("删除", (dialog, which) -> {
                new Thread(() -> {
                    DbUtils.update(null,
                            "DELETE FROM task_participant WHERE task_id = ? AND participant_id = ?",
                            mTaskList.get(position).getTaskId(), UserLab.getCurrentUser().getId());
                }).start();
            });
            builder.setNegativeButton("取消", null);
            builder.show();
        });
        mTaskTitle = view.findViewById(R.id.taskTitle);
        mTaskTitle.setText("已完成的任务");
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // 获取数据
        new Thread(() -> {
            List<Task> tasks = TaskLab.getAccomplishTask(mCurrentUserId);
            Log.d(TAG, "onResume: " + tasks.size());
            mTaskList.clear();
            mTaskList.addAll(tasks);
            getActivity().runOnUiThread(() -> {
                mTaskItemAdapter.notifyDataSetChanged();
            });
        }).start();
    }

    public static UserAccomplishTaskFragment getInstance(String userId) {
        UserAccomplishTaskFragment fragment = new UserAccomplishTaskFragment();
        Bundle args = new Bundle();
        args.putString(User.CURRENT_USER_ID, userId);
        fragment.setArguments(args);
        return fragment;
    }
}
