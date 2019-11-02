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
        mTaskItemAdapter = new TaskItemAdapter(mReleasedTasks);
        mRecyclerView.setAdapter(mTaskItemAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        new Thread(() -> {
            List<Task> tasks = TaskLab.getReleasedTask("4");
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

    class TaskItemAdapter extends RecyclerView.Adapter<TaskItemAdapter.TaskItemViewHolder> {
        private List<Task> mAccomplishTasks;

        public TaskItemAdapter(List<Task> accomplishTasks) {
            mAccomplishTasks = accomplishTasks;
        }

        @NonNull
        @Override
        public TaskItemAdapter.TaskItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.user_management_task_item, parent, false);
            return new TaskItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull TaskItemViewHolder holder, int position) {
            holder.bind(mAccomplishTasks.get(position));
        }


        @Override
        public int getItemCount() {
            return mAccomplishTasks.size();
        }

        public class TaskItemViewHolder extends RecyclerView.ViewHolder {
            private TextView taskName;
            private TextView taskTime;
            private TextView taskLocation;

            public TaskItemViewHolder(@NonNull View itemView) {
                super(itemView);
                taskName = itemView.findViewById(R.id.user_management_task_name);
                taskTime = itemView.findViewById(R.id.user_management_task_time);
                taskLocation = itemView.findViewById(R.id.user_management_task_location);
            }

            public void bind(Task task) {
                taskName.setText(task.getTaskName());
                taskTime.setText(task.getTaskStartAt() + "-" + task.getTaskEndIn());
                taskLocation.setText(task.getTaskLocation());
            }
        }
    }
}
