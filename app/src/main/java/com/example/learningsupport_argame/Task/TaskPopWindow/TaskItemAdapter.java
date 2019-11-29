package com.example.learningsupport_argame.Task.TaskPopWindow;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.learningsupport_argame.R;
import com.example.learningsupport_argame.Task.Task;

import java.util.List;

public class TaskItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static String TAG = "TaskItemAdapter";
    private OnRecycleViewItemClick mOnRecycleViewItemClick;
    private List<Task> mTasks;
    private Context mContext;


    public static final int ITEM_NORMAL = 1;
    public static final int ITEM_EMPTY = 0;

    public TaskItemAdapter(List<Task> accomplishTasks, Context context) {
        mTasks = accomplishTasks;
        mContext = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //在这里根据不同的viewType进行引入不同的布局
        if (viewType == ITEM_EMPTY) {
            View emptyView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_empty_item, parent, false);
            return new RecyclerView.ViewHolder(emptyView) {
            };
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_management_task_item, parent, false);

        return new TaskItemAdapter.TaskItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof TaskItemViewHolder) {
            ((TaskItemViewHolder) holder).bind(mTasks.get(position), position);
        }
    }

    @Override
    public int getItemViewType(int position) {
        // 如果没有数据，使用空布局的布局
        if (mTasks.size() == 0) {
            return ITEM_EMPTY;
        }
        //如果有数据，则使用ITEM的布局
        return ITEM_NORMAL;
    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount: " + mTasks.size());
        // mTasks.size()为0的话，只引入一个空布局,此时recyclerView的itemCount为1
        if (mTasks.size() == 0) {
            return 1;
        }
        //如果不为0，按正常的流程跑
        return mTasks.size();
    }

    public class TaskItemViewHolder extends RecyclerView.ViewHolder {

        private TextView taskName;
        private TextView taskTime;
        private TextView taskLocation;
        private View mItemView;

        public TaskItemViewHolder(@NonNull View itemView) {
            super(itemView);
            mItemView = itemView;
            taskName = itemView.findViewById(R.id.user_management_task_name);
            taskTime = itemView.findViewById(R.id.user_management_task_time);
            taskLocation = itemView.findViewById(R.id.user_management_task_location);

        }

        public void bind(Task task, int position) {
            taskName.setText(task.getTaskName());
            taskTime.setText(task.getTaskStartAt() + "-" + task.getTaskEndIn());
            taskLocation.setText(task.getAccomplishTaskLocation());
            mItemView.setOnClickListener((view) -> {
                if (mOnRecycleViewItemClick != null)
                    mOnRecycleViewItemClick.onRecycleViewItemClick(view, position);
            });
        }
    }

    public void setOnRecycleViewItemClick(OnRecycleViewItemClick onRecycleViewItemClick) {
        mOnRecycleViewItemClick = onRecycleViewItemClick;
    }

    interface OnRecycleViewItemClick {
        void onRecycleViewItemClick(View view, int position);
    }
}