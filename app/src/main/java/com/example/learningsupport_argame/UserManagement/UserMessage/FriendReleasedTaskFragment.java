package com.example.learningsupport_argame.UserManagement.UserMessage;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.learningsupport_argame.R;
import com.example.learningsupport_argame.UserManagement.User;
import com.example.learningsupport_argame.Task.Task;
import com.example.learningsupport_argame.Task.adapter.TaskItemAdapter;
import com.example.learningsupport_argame.Task.TaskLab;
import com.example.learningsupport_argame.UserManagement.achievement.AchievementActivity;

import java.util.ArrayList;
import java.util.List;

public class FriendReleasedTaskFragment extends Fragment {
    private Activity mActivity;
    private static String TAG = "FriendReleasedTaskFragment";

    private String mCurrentUserId;
    private RecyclerView mRecyclerView;
    private TaskItemAdapter mTaskItemAdapter;
    private List<Task> mReleasedTasks;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mActivity = getActivity();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mCurrentUserId = getArguments().getString(User.CURRENT_USER_ID);
        mReleasedTasks = new ArrayList<>();
        View view = inflater.inflate(R.layout.user_management_friend_fragment_release_task, container, false);
        mRecyclerView = view.findViewById(R.id.user_management_released_task_recycler_view);
        mTaskItemAdapter = new TaskItemAdapter(mReleasedTasks, mActivity);
        mRecyclerView.setAdapter(mTaskItemAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mActivity, DividerItemDecoration.VERTICAL));
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        new Thread(() -> {
            List<Task> tasks = TaskLab.getReleasedTask(mCurrentUserId);
            Log.d(TAG, "onResume: " + tasks.size());
            mReleasedTasks.addAll(tasks);
            mActivity.runOnUiThread(() -> {
                mTaskItemAdapter.notifyDataSetChanged();
            });
        }).start();
    }

    public static FriendReleasedTaskFragment getInstance(String userId) {
        FriendReleasedTaskFragment fragment = new FriendReleasedTaskFragment();
        Bundle args = new Bundle();
        args.putString(User.CURRENT_USER_ID, userId);
        fragment.setArguments(args);
        return fragment;
    }
}
