package com.example.learningsupport_argame.Task.fragment;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.example.learningsupport_argame.Task.Task;
import com.example.learningsupport_argame.Task.TaskLab;
import com.example.learningsupport_argame.Task.TaskShowView;
import com.example.learningsupport_argame.UserManagement.UserLab;

import java.util.ArrayList;
import java.util.List;

public class TaskCanAcceptListFragment extends TaskListBasicFragment {
    private Activity mActivity;
    private String mCurrentType = "全体任务";

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        mCurrentType = "全体任务";
        mTypePersonBtn.setText("全体任务");
        mTypePersonBtn.setOnClickListener(v -> {
            mCurrentType = "全体任务";
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
                List<Task> tasks = new ArrayList<>();
                if (mCurrentType.equals("好友任务")) {
                    tasks = TaskLab.getFriendTask();
                } else if (mCurrentType.equals("全体任务")) {
                    tasks = TaskLab.getAllPeopleTask();
                } else if (mCurrentType.equals("社团任务")) {
                    tasks = TaskLab.getClubTask();
                }
                mTaskList.clear();
                mTaskList.addAll(tasks);
                mActivity.runOnUiThread(() -> {
                    mTaskItemAdapter.notifyDataSetChanged();
                    mSwipeRefreshLayout.setRefreshing(false);
                });
            }).start();
        });
        mTaskItemAdapter.setOnRecycleViewItemClick((v, position) -> {
            TaskShowView taskShowView = new TaskShowView(mActivity);
            taskShowView.initData(mTaskList.get(position));
            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
            builder.setView(taskShowView.getView());
            builder.setTitle("任务详情");
            builder.setPositiveButton("接受", (dialog, which) -> {
                if (UserLab.getCurrentUser().getHp() < 2) {
                    new AlertDialog.Builder(mActivity)
                            .setTitle("血量过少")
                            .setMessage("当前血量过少，无法接受任务。\n您可以使用血量道具或等待血量恢复")
                            .setPositiveButton("确定", null)
                            .show();
                    return;
                }
                new Thread(() -> {
                    TaskLab.acceptTask(mTaskList.get(position));
                }).start();
            });
            builder.setNegativeButton("取消", null);
            builder.show();
        });
        return view;
    }

    private void initListByType() {
        mTaskList.clear();
        mTaskItemAdapter.notifyDataSetChanged();
        new Thread(() -> {
            List<Task> tasks = new ArrayList<>();
            switch (mCurrentType) {
                case "好友任务":
                    if (TaskLab.sFriendTaskList == null)
                        tasks = TaskLab.getFriendTask();
                    else
                        tasks = TaskLab.sFriendTaskList;
                    break;
                case "全体任务":
                    if (TaskLab.sAllPeopleTaskList == null)
                        tasks = TaskLab.getAllPeopleTask();
                    else
                        tasks = TaskLab.sAllPeopleTaskList;
                    break;
                case "社团任务":
                    if (TaskLab.sClubTaskList == null)
                        tasks = TaskLab.getClubTask();
                    else
                        tasks = TaskLab.sClubTaskList;
                    break;
            }

            mTaskList.addAll(tasks);
            mActivity.runOnUiThread(() -> {
                mTaskItemAdapter.notifyDataSetChanged();
            });
        }).start();
    }

    @Override
    public void onResume() {
        super.onResume();
        // 获取数据
        new Thread(() -> {
            List<Task> tasks = new ArrayList<>();
            if (mCurrentType.equals("好友任务")) {
                tasks = TaskLab.getFriendTask();
            }
            if (mCurrentType.equals("全体任务")) {
                tasks = TaskLab.getAllPeopleTask();
            } else if (mCurrentType.equals("社团任务")) {
                tasks = TaskLab.getClubTask();
            }

            mTaskList.clear();
            mTaskList.addAll(tasks);
            mActivity.runOnUiThread(() -> {
                mTaskItemAdapter.notifyDataSetChanged();
            });
        }).start();
    }


    public static TaskCanAcceptListFragment getInstance() {
        TaskCanAcceptListFragment fragment = new TaskCanAcceptListFragment();
//        Bundle args = new Bundle();
//        args.putString(User.CURRENT_USER_ID, userId);
//        fragment.setArguments(args);
        return fragment;
    }

}