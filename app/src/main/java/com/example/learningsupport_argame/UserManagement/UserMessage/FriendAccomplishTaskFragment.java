package com.example.learningsupport_argame.UserManagement.UserMessage;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.learningsupport_argame.Task.Task;
import com.example.learningsupport_argame.R;
import com.example.learningsupport_argame.UserManagement.User;
import com.example.learningsupport_argame.Task.adapter.TaskItemAdapter;
import com.example.learningsupport_argame.Task.TaskLab;

import java.util.ArrayList;
import java.util.List;


public class FriendAccomplishTaskFragment extends Fragment {
    private Activity mActivity;
    private static String TAG = "FriendAccomplishTaskFragment";
    private String mCurrentUserId = "4";
    private TaskItemAdapter mTaskItemAdapter;
    private RecyclerView mRecyclerView;
    private List<Task> mAccomplishTasks;

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
        mAccomplishTasks = new ArrayList<>();
        mCurrentUserId = getArguments().getString(User.CURRENT_USER_ID);
        View view = inflater.inflate(R.layout.user_management_friend_fragment_accomplish_task, container, false);
        mRecyclerView = view.findViewById(R.id.user_management_accomplish_task_recycler_view);
        mTaskItemAdapter = new TaskItemAdapter(mAccomplishTasks,mActivity);
        mRecyclerView.setAdapter(mTaskItemAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mActivity, DividerItemDecoration.VERTICAL));

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        new Thread(() -> {
            List<Task> tasks = TaskLab.getAccomplishTask(mCurrentUserId);
            Log.d(TAG, "onResume: " + tasks.size());
            mAccomplishTasks.addAll(tasks);
            mActivity.runOnUiThread(() -> {
                mTaskItemAdapter.notifyDataSetChanged();
            });
        }).start();
    }

    public static FriendAccomplishTaskFragment getInstance(String userId) {
        FriendAccomplishTaskFragment fragment = new FriendAccomplishTaskFragment();
        Bundle args = new Bundle();
        args.putString(User.CURRENT_USER_ID, userId);
        fragment.setArguments(args);
        return fragment;
    }
}
