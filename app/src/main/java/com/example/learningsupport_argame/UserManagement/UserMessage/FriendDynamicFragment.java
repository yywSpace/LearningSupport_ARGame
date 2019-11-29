package com.example.learningsupport_argame.UserManagement.UserMessage;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.learningsupport_argame.R;
import com.example.learningsupport_argame.UserManagement.User;
import com.example.learningsupport_argame.Task.Task;
import com.example.learningsupport_argame.Task.TaskLab;

import java.util.ArrayList;
import java.util.List;

public class FriendDynamicFragment extends Fragment {
    private String mCurrentUserId;
    private RecyclerView mRecyclerView;
    private List<Task> mTaskList;
    private DynamicTaskItemAdapter mAdapter;


    public static final int ITEM_NORMAL = 1;
    public static final int ITEM_EMPTY = 0;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mTaskList = new ArrayList<>();
        mCurrentUserId = getArguments().getString(User.CURRENT_USER_ID);
        View view = inflater.inflate(R.layout.user_management_friend_fragment_user_dynamic, container, false);
        mRecyclerView = view.findViewById(R.id.user_management_dynamic_recycler_view);
        mAdapter = new DynamicTaskItemAdapter(mTaskList, getContext());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        new Thread(() -> {
            List<Task> tasks = TaskLab.getAllTask(mCurrentUserId);
            mTaskList.addAll(tasks);
            getActivity().runOnUiThread(() -> mAdapter.notifyDataSetChanged());

        }).start();
    }

    public static FriendDynamicFragment getInstance(String userId) {
        FriendDynamicFragment fragment = new FriendDynamicFragment();
        Bundle args = new Bundle();
        args.putString(User.CURRENT_USER_ID, userId);
        fragment.setArguments(args);
        return fragment;
    }

    public class DynamicTaskItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private List<Task> mTasks;
        private Context mContext;

        public DynamicTaskItemAdapter(List<Task> accomplishTasks, Context context) {
            mTasks = accomplishTasks;
            mContext = context;
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

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            //在这里根据不同的viewType进行引入不同的布局
            if (viewType == ITEM_EMPTY) {
                View emptyView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_empty_item, parent, false);
                return new RecyclerView.ViewHolder(emptyView) {
                };
            }

            View view = LayoutInflater.from(mContext).inflate(R.layout.user_management_dynamic_task_item, parent, false);
            return new DynamicTaskItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof DynamicTaskItemViewHolder)
                ((DynamicTaskItemViewHolder) holder).bind(mTasks.get(position));
        }


        @Override
        public int getItemCount() {
            // mTasks.size()为0的话，只引入一个空布局,此时recyclerView的itemCount为1
            if (mTasks.size() == 0) {
                return 1;
            }
            //如果不为0，按正常的流程跑
            return mTasks.size();
        }


        public class DynamicTaskItemViewHolder extends RecyclerView.ViewHolder {
            private TextView dynamicTime;
            private TextView dynamicDesc;
            private TextView taskName;
            private TextView taskTime;
            private TextView taskLocation;

            public DynamicTaskItemViewHolder(@NonNull View itemView) {
                super(itemView);
                dynamicTime = itemView.findViewById(R.id.user_management_dynamic_time);
                dynamicDesc = itemView.findViewById(R.id.user_management_dynamic_desc);
                taskName = itemView.findViewById(R.id.user_management_task_name);
                taskTime = itemView.findViewById(R.id.user_management_task_time);
                taskLocation = itemView.findViewById(R.id.user_management_task_location);
            }

            public void bind(Task task) {
                dynamicTime.setText(task.getTaskCreateTime());
                dynamicDesc.setText(task.getTaskStatus());
                taskName.setText(task.getTaskName());
                taskTime.setText(task.getTaskStartAt() + "-" + task.getTaskEndIn());
                taskLocation.setText(task.getAccomplishTaskLocation().split(",")[0]);
            }
        }
    }

}
