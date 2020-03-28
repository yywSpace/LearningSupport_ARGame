package com.example.learningsupport_argame.Task.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextClock;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.baidu.mapapi.model.LatLng;
import com.example.learningsupport_argame.Navi.Activity.ShowLocationPopWindow;
import com.example.learningsupport_argame.R;
import com.example.learningsupport_argame.Task.Task;
import com.example.learningsupport_argame.Task.TaskLab;
import com.example.learningsupport_argame.Task.TaskShowView;
import com.example.learningsupport_argame.Task.adapter.ParticipantItemAdapter;
import com.example.learningsupport_argame.Task.adapter.TaskItemAdapter;
import com.example.learningsupport_argame.UserManagement.User;
import com.example.learningsupport_argame.UserManagement.UserLab;

import java.util.ArrayList;
import java.util.List;

/**
 * AR任务只能在地图上显示，并不在这里显示
 */
public class TaskListBasicFragment extends Fragment {
    protected static final String TAG = "TaskListBasicFragment";
    private Activity mActivity;
    protected String mCurrentUserId;
    protected Button mTypePersonBtn;
    protected Button mTypeFriendBtn;
    protected Button mTypeGroupBtn;
    SwipeRefreshLayout mSwipeRefreshLayout;
    private String mTaskFragmentType;
    private RecyclerView mTaskRecycleView;
    protected TaskItemAdapter mTaskItemAdapter;
    protected List<Task> mTaskList;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.task_list_basic_layout, container, false);
        mTypePersonBtn = view.findViewById(R.id.tab_type_person);
        mTypeFriendBtn = view.findViewById(R.id.tab_type_friend);
        mTypeGroupBtn = view.findViewById(R.id.tab_type_group);

        mTaskRecycleView = view.findViewById(R.id.list_task_redo);
        mSwipeRefreshLayout = view.findViewById(R.id.task_refresh_layout);
        mTaskList = new ArrayList<>();
        mTaskItemAdapter = new TaskItemAdapter(mTaskList,mActivity);

        mTaskItemAdapter.setOnRecycleViewItemClick((v, position) -> {
            TaskShowView taskShowView = new TaskShowView(mActivity);
            taskShowView.initData(mTaskList.get(position));
            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
            builder.setView(taskShowView.getView());
            builder.setTitle("任务详情");
            builder.setPositiveButton("接受", null);
            builder.setNegativeButton("取消", null);
            builder.show();
        });
        mTaskRecycleView.setAdapter(mTaskItemAdapter);
        mTaskRecycleView.setLayoutManager(new LinearLayoutManager(getContext()));
        mTaskRecycleView.addItemDecoration(new DividerItemDecoration(mActivity, DividerItemDecoration.VERTICAL));
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        while (UserLab.getCurrentUser() == null) ;
        mCurrentUserId = UserLab.getCurrentUser().getId() + "";

    }

    public static TaskListBasicFragment getInstance(String userId, String taskType) {
        TaskListBasicFragment fragment = new TaskListBasicFragment();
        Bundle args = new Bundle();
        args.putString(User.CURRENT_USER_ID, userId);
        args.putString("task_type", taskType);
        fragment.setArguments(args);
        return fragment;
    }


    public SwipeRefreshLayout getSwipeRefreshLayout() {
        return mSwipeRefreshLayout;
    }

    public TaskItemAdapter getTaskItemAdapter() {
        return mTaskItemAdapter;
    }


    public List<Task> getTaskList() {
        return mTaskList;
    }

    public String getTaskFragmentType() {
        return mTaskFragmentType;
    }

    public void setTaskFragmentType(String taskFragmentType) {
        mTaskFragmentType = taskFragmentType;
    }
}
