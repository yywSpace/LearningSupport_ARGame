package com.example.learningsupport_argame.UserManagement.UserMessage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.learningsupport_argame.R;
import com.example.learningsupport_argame.task.Task;

import java.util.List;

public class TaskItemAdapter extends RecyclerView.Adapter<TaskItemAdapter.TaskItemViewHolder> {
    private List<Task> mTasks;
    private Context mContext;

    public TaskItemAdapter(List<Task> accomplishTasks, Context context) {
        mTasks = accomplishTasks;
        mContext = context;
    }

    @NonNull
    @Override
    public TaskItemAdapter.TaskItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_management_task_item, parent, false);
        return new TaskItemAdapter.TaskItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskItemViewHolder holder, int position) {
        holder.bind(mTasks.get(position));
    }


    @Override
    public int getItemCount() {
        return mTasks.size();
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