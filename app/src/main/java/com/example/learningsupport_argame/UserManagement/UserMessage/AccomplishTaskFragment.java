package com.example.learningsupport_argame.UserManagement.UserMessage;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.learningsupport_argame.task.Task;
import com.example.learningsupport_argame.R;
import com.example.learningsupport_argame.UserManagement.User;
import com.example.learningsupport_argame.task.TaskLab;

import java.util.ArrayList;
import java.util.List;


public class AccomplishTaskFragment extends Fragment {
    private static String TAG = "AccomplishTaskFragment";
    private String mCurrentUserId = "4";
    private TaskItemAdapter mTaskItemAdapter;
    private RecyclerView mRecyclerView;
    private List<Task> mAccomplishTasks;

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
        mTaskItemAdapter = new TaskItemAdapter(mAccomplishTasks);
        mRecyclerView.setAdapter(mTaskItemAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        new Thread(() -> {
            List<Task> tasks = TaskLab.getAccomplishTask("4");
            Log.d(TAG, "onResume: " + tasks.size());
            mAccomplishTasks.addAll(tasks);
            getActivity().runOnUiThread(() -> {
                mTaskItemAdapter.notifyDataSetChanged();
            });
        }).start();
    }

    public static AccomplishTaskFragment getInstance(String userId) {
        AccomplishTaskFragment fragment = new AccomplishTaskFragment();
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
            return new TaskItemAdapter.TaskItemViewHolder(view);
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
