package com.example.learningsupport_argame.UserManagement.UserMessage;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.learningsupport_argame.DbUtils;
import com.example.learningsupport_argame.R;
import com.example.learningsupport_argame.Task.TaskShowView;
import com.example.learningsupport_argame.Task.fragment.TaskListBasicFragment;
import com.example.learningsupport_argame.UserManagement.User;
import com.example.learningsupport_argame.UserManagement.UserLab;
import com.example.learningsupport_argame.Task.Task;
import com.example.learningsupport_argame.Task.TaskLab;

import java.util.List;
import java.util.stream.Collectors;

public class UserAcceptedTaskFragment extends TaskListBasicFragment {
    private Activity mActivity;
    private String mCurrentType = "个人任务";

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        mCurrentType = "个人任务";
        mTypePersonBtn.setOnClickListener(v -> {
            mCurrentType = "个人任务";
            initListByType();
            mTypePersonBtn.setBackgroundColor(Color.parseColor("#f0f0f0"));
            mTypeGroupBtn.setBackgroundColor(Color.parseColor("#FFFAFAFA"));
            mTypeFriendBtn.setBackgroundColor(Color.parseColor("#FFFAFAFA"));
        });
        mTypeGroupBtn.setOnClickListener(v -> {
            mCurrentType = "社团任务";
            initListByType();
            mTypeGroupBtn.setBackgroundColor(Color.parseColor("#f0f0f0"));
            mTypePersonBtn.setBackgroundColor(Color.parseColor("#FFFAFAFA"));
            mTypeFriendBtn.setBackgroundColor(Color.parseColor("#FFFAFAFA"));
        });
        mTypeFriendBtn.setOnClickListener(v -> {
            mCurrentType = "好友任务";
            initListByType();
            mTypeFriendBtn.setBackgroundColor(Color.parseColor("#f0f0f0"));
            mTypeGroupBtn.setBackgroundColor(Color.parseColor("#FFFAFAFA"));
            mTypePersonBtn.setBackgroundColor(Color.parseColor("#FFFAFAFA"));
        });


        mTaskItemAdapter.setOnRecycleViewItemClick((v, position) -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
            TaskShowView taskShowView = new TaskShowView(mActivity);
            taskShowView.initData(mTaskList.get(position));
            builder.setView(taskShowView.getView());
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

        getSwipeRefreshLayout().setOnRefreshListener(() ->
                new Thread(() -> {
                    List<Task> tasks = TaskLab.getAcceptedTask(mCurrentUserId).stream()
                            .filter(task -> task.getTaskType().equals(mCurrentType))
                            .collect(Collectors.toList());
                    Log.d(TAG, "onResume: " + tasks.size());
                    mTaskList.clear();
                    mTaskList.addAll(tasks);
                    mActivity.runOnUiThread(() -> {
                        mTaskItemAdapter.notifyDataSetChanged();
                        getSwipeRefreshLayout().setRefreshing(false);
                    });
                }).start());
        return view;
    }

    private void initListByType() {
        List<Task> tasks = TaskLab.mAcceptedTaskList.stream()
                .filter(task -> task.getTaskType().equals(mCurrentType))
                .collect(Collectors.toList());
        mTaskList.clear();
        mTaskList.addAll(tasks);
        mTaskItemAdapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        // 获取数据
        new Thread(() -> {
            List<Task> tasks = TaskLab.getAcceptedTask(mCurrentUserId).stream()
                    .filter(task -> task.getTaskType().equals(mCurrentType))
                    .collect(Collectors.toList());
            Log.d(TAG, "onResume: " + tasks.size());
            mTaskList.clear();
            mTaskList.addAll(tasks);
            mActivity.runOnUiThread(() -> {
                mTaskItemAdapter.notifyDataSetChanged();
            });
        }).start();
    }


    public static UserAcceptedTaskFragment getInstance(String userId) {
        UserAcceptedTaskFragment fragment = new UserAcceptedTaskFragment();
        Bundle args = new Bundle();
        args.putString(User.CURRENT_USER_ID, userId);
        fragment.setArguments(args);
        return fragment;
    }
}
