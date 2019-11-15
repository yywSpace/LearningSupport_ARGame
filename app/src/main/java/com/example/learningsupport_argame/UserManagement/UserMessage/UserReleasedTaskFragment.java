package com.example.learningsupport_argame.UserManagement.UserMessage;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.example.learningsupport_argame.DbUtils;
import com.example.learningsupport_argame.R;
import com.example.learningsupport_argame.UserManagement.User;
import com.example.learningsupport_argame.UserManagement.UserLab;
import com.example.learningsupport_argame.task.Task;
import com.example.learningsupport_argame.task.TaskLab;
import com.example.learningsupport_argame.tempararyfile.TaskListFragment;

import java.util.List;

public class UserReleasedTaskFragment extends TaskListFragment {
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
                Toast.makeText(getActivity(), "删除或修改，待定", Toast.LENGTH_SHORT).show();
//                new Thread(() -> {
//                    DbUtils.update(null,
//                            "DELETE FROM task WHERE task_id = ?",
//                            mTaskList.get(position).getTaskId());
//                }).start();
            });
            builder.setNegativeButton("取消", null);
            builder.show();
        });
        mTaskTitle = view.findViewById(R.id.taskTitle);
        mTaskTitle.setText("已发布的任务");
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // 获取数据
        new Thread(() -> {
            List<Task> tasks = TaskLab.getReleasedTask(mCurrentUserId);
            Log.d(TAG, "onResume: " + tasks.size());
            mTaskList.clear();
            mTaskList.addAll(tasks);
            getActivity().runOnUiThread(() -> {
                mTaskItemAdapter.notifyDataSetChanged();
            });
        }).start();
    }


    public static UserReleasedTaskFragment getInstance(String userId) {
        UserReleasedTaskFragment fragment = new UserReleasedTaskFragment();
        Bundle args = new Bundle();
        args.putString(User.CURRENT_USER_ID, userId);
        fragment.setArguments(args);
        return fragment;
    }
}
