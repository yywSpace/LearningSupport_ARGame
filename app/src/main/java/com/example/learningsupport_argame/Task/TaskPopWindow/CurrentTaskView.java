package com.example.learningsupport_argame.Task.TaskPopWindow;


import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.learningsupport_argame.R;
import com.example.learningsupport_argame.UserManagement.User;
import com.example.learningsupport_argame.Task.adapter.ParticipantItemAdapter;
import com.example.learningsupport_argame.Task.Task;
import com.example.learningsupport_argame.Task.TaskLab;

import java.util.ArrayList;
import java.util.List;

public class CurrentTaskView {
    private View mView;
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

    private AppCompatActivity mContext;
    private Handler mHandler;

    public CurrentTaskView(AppCompatActivity context) {
        mContext = context;
        mView = mContext.getLayoutInflater().inflate(R.layout.task_list_pop_window_tasking_layout, null, false);
        initCurrentTaskView(mView);
        mHandler = new Handler();
    }

    void initCurrentTaskView(View view) {
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
        mItemAdapter = new ParticipantItemAdapter(mUserList, mContext);
        mTaskParticipantRecyclerView.setAdapter(mItemAdapter);

        mTaskParticipantRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mTaskParticipantRecyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));

        mTaskStatusFaq.setOnClickListener((v) -> {
            new AlertDialog.Builder(mContext)
                    .setTitle("任务类型提示")
                    .setMessage("为红色则表示自建任务\n\t\t\t\t\t为绿色则表示社团任务\n\t\t\t\t\t为蓝色则表示好友指定任务\n\t\t\t\t\t为紫色则表示主动的接受任务")
                    .setNegativeButton("取消", null)
                    .show();
        });

        mMapLocationBtn.setOnClickListener((v) -> {
            Toast.makeText(mContext, "地图位置", Toast.LENGTH_SHORT).show();

        });
    }

    public void initCurrentTaskData(Task task) {
        mHandler.post(()->{
            mTaskStatusTextView.setText(task.getTaskStatus());
            mTaskNameTextView.setText(task.getTaskName());
            mTaskTypeTextView.setText(task.getTaskType());
            mTaskTimeTextClock.setFormat12Hour(task.getTaskStartAt());
            mTaskLocationTextView.setText(task.getAccomplishTaskLocation());
            mTaskDescTextView.setText(task.getTaskContent());
        });

        // 参与列表
        new Thread(() -> {
            List<User> participants = TaskLab.getParticipant("" + task.getTaskId());
            mUserList.clear();
            mUserList.addAll(participants);
            mContext.runOnUiThread(() -> {
                mHandler.post(() -> {
                    mItemAdapter.notifyDataSetChanged();

                });
            });
        }).start();
    }

    public View getView() {
        return mView;
    }

    public void setView(View view){
        mView = view;
    }
}

