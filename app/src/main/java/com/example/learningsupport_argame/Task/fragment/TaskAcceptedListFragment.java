package com.example.learningsupport_argame.Task.fragment;

import android.graphics.Color;
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

import com.example.learningsupport_argame.DbUtils;
import com.example.learningsupport_argame.R;
import com.example.learningsupport_argame.UserManagement.UserLab;
import com.example.learningsupport_argame.Task.Task;
import com.example.learningsupport_argame.Task.TaskLab;

import java.util.List;
import java.util.stream.Collectors;

public class TaskAcceptedListFragment extends TaskListBasicFragment {
    private static AppCompatActivity mActivity;
    private String mCurrentUserId;
    private String mCurrentType = "个人任务";

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
        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            new Thread(() -> {
                mCurrentUserId = UserLab.getCurrentUser().getId() + "";
                List<Task> tasks = TaskLab.getAcceptedTask(mCurrentUserId).stream()
                        .filter(task -> task.getTaskType().equals(mCurrentType))
                        .collect(Collectors.toList());
                Log.d(TAG, "onResume: " + tasks.size());
                mTaskList.clear();
                mTaskList.addAll(tasks);
                mActivity.runOnUiThread(() -> {
                    mTaskItemAdapter.notifyDataSetChanged();
                    mSwipeRefreshLayout.setRefreshing(false);
                });
            }).start();
        });
        mTaskItemAdapter.setOnRecycleViewItemClick((v, position) -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            View taskDetailView = getLayoutInflater().inflate(R.layout.task_current_fragment_layout_22, null);//获取自定义布局
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
        return view;
    }

    private void initListByType() {
        List<Task> tasks = TaskLab.mAcceptedTaskList.stream()
                .filter(task -> {
                    if (mCurrentType.equals("个人任务")) {
                        return task.getTaskType().equals(mCurrentType) ||
                                task.getTaskType().equals("全体任务");
                    }
                    return task.getTaskType().equals(mCurrentType);
                })
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
            while (UserLab.getCurrentUser() == null) ;
            mCurrentUserId = UserLab.getCurrentUser().getId() + "";
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


    public static TaskAcceptedListFragment getInstance(AppCompatActivity activity) {
        TaskAcceptedListFragment fragment = new TaskAcceptedListFragment();
        mActivity = activity;
//        Bundle args = new Bundle();
//        args.putString(User.CURRENT_USER_ID, userId);
//        fragment.setArguments(args);
        return fragment;
    }
}
