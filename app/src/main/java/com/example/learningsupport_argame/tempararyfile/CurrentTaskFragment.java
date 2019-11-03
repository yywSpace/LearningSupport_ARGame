package com.example.learningsupport_argame.tempararyfile;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.learningsupport_argame.R;
import com.example.learningsupport_argame.UserManagement.User;
import com.example.learningsupport_argame.task.Task;
import com.example.learningsupport_argame.task.TaskLab;

import java.util.ArrayList;
import java.util.List;

public class CurrentTaskFragment extends Fragment {
    private String TAG = "CurrentTaskFragment";

    private String mCurrentUserId;
    private ImageButton mTaskStatusFaq;
    private ImageView mMapLocationBtn;
    private TextView mTaskStatusTextView;
    private TextView mTaskNameTextView;
    private TextView mTaskTypeTextView;
    private TextClock mTaskTimeTextClock;
    private TextView mTaskLocationTextView;
    private TextView mTaskDescTextView;
    private RecyclerView mTaskParticipantRecyclerView;
    private ParticipantItemAdapter mItemAdapter;
    private List<User> mUserList;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mCurrentUserId = getArguments().getString(User.CURRENT_USER_ID);
        View view = inflater.inflate(R.layout.task_current_fragment_layout, container, false);
        initView(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // 获取数据
        new Thread(() -> {
            List<Task> tasks = TaskLab.getAllTask(mCurrentUserId);

            getActivity().runOnUiThread(() -> {
                initData(tasks.get(0));
            });
        }).start();
    }

    public void initView(View view) {
        mTaskStatusFaq = view.findViewById(R.id.task_status_faq);
        mTaskStatusTextView = view.findViewById(R.id.task_status);
        mTaskNameTextView = view.findViewById(R.id.task_name);
        mTaskTypeTextView = view.findViewById(R.id.task_type);
        mTaskTimeTextClock = view.findViewById(R.id.task_time);
        mTaskLocationTextView = view.findViewById(R.id.task_location);
        mTaskDescTextView = view.findViewById(R.id.task_desc);
        mMapLocationBtn = view.findViewById(R.id.task_map_location);
        mTaskParticipantRecyclerView = view.findViewById(R.id.task_participant_list);

        mUserList = new ArrayList<>();
        mItemAdapter = new ParticipantItemAdapter(mUserList, getContext());
        mTaskParticipantRecyclerView.setAdapter(mItemAdapter);

        mTaskParticipantRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mTaskParticipantRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));

        mTaskStatusFaq.setOnClickListener((v) -> {
            new AlertDialog.Builder(getContext())
                    .setTitle("任务类型提示")
                    .setMessage("为红色则表示自建任务\n\t\t\t\t\t为绿色则表示社团任务\n\t\t\t\t\t为蓝色则表示好友指定任务\n\t\t\t\t\t为紫色则表示主动的接受任务")
                    .setNegativeButton("取消", null)
                    .show();
        });

        mMapLocationBtn.setOnClickListener((v) -> {
            Toast.makeText(getContext(), "地图位置", Toast.LENGTH_SHORT).show();

        });
    }


    private void initData(Task task) {
        mTaskStatusTextView.setText(task.getTaskStatus());
        mTaskNameTextView.setText(task.getTaskName());
        mTaskTypeTextView.setText(task.getTaskType());
        mTaskTimeTextClock.setFormat12Hour(task.getTaskStartAt());
        mTaskLocationTextView.setText(task.getAccomplishTaskLocation());
        mTaskDescTextView.setText(task.getTaskContent());
        // 参与列表
        new Thread(() -> {
            List<User> participants = TaskLab.getParticipant(""+task.getTaskId());
            mUserList.addAll(participants);
            getActivity().runOnUiThread(()->{
                mItemAdapter.notifyDataSetChanged();
                Log.d(TAG, "initData: " + mUserList.size());
            });
        }).start();

    }

    public static CurrentTaskFragment getInstance(String userId) {
        CurrentTaskFragment fragment = new CurrentTaskFragment();
        Bundle args = new Bundle();
        args.putString(User.CURRENT_USER_ID, userId);
        fragment.setArguments(args);
        return fragment;
    }

}
