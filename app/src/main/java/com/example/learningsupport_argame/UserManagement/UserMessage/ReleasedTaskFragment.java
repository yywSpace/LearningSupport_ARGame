package com.example.learningsupport_argame.UserManagement.UserMessage;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.learningsupport_argame.R;
import com.example.learningsupport_argame.UserManagement.User;
import com.example.learningsupport_argame.task.Task;
import com.example.learningsupport_argame.task.TaskLab;

import java.util.ArrayList;
import java.util.List;

public class ReleasedTaskFragment extends Fragment {
    private static String TAG = "ReleasedTaskFragment";

    private String mCurrentUserId;
    private RecyclerView mRecyclerView;
    private TaskItemAdapter mTaskItemAdapter;
    private List<Task> mReleasedTasks;

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
        mTaskItemAdapter = new TaskItemAdapter(mReleasedTasks, getActivity());
        mRecyclerView.setAdapter(mTaskItemAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        new Thread(() -> {
            List<Task> tasks = TaskLab.getReleasedTask(mCurrentUserId);
            Log.d(TAG, "onResume: " + tasks.size());
            mReleasedTasks.addAll(tasks);
            getActivity().runOnUiThread(() -> {
                mTaskItemAdapter.notifyDataSetChanged();
            });
        }).start();
    }

    public static ReleasedTaskFragment getInstance(String userId) {
        ReleasedTaskFragment fragment = new ReleasedTaskFragment();
        Bundle args = new Bundle();
        args.putString(User.CURRENT_USER_ID, userId);
        fragment.setArguments(args);
        return fragment;
    }
}
