package com.example.learningsupport_argame.Task;

import android.app.Activity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextClock;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.baidu.mapapi.model.LatLng;
import com.example.learningsupport_argame.Navi.Activity.ShowLocationPopWindow;
import com.example.learningsupport_argame.R;
import com.example.learningsupport_argame.Task.adapter.ParticipantItemAdapter;
import com.example.learningsupport_argame.UserManagement.User;
import com.example.learningsupport_argame.UserManagement.UserLab;

import java.util.ArrayList;
import java.util.List;

public class TaskShowView {
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
    private LatLng mAccomplishTaskLatLng;
    private Activity mActivity;
    private View mView;

    public TaskShowView(Activity activity) {
        mActivity = activity;
        mView = mActivity.getLayoutInflater().inflate(R.layout.task_current_fragment_layout, null, false);
        initView(mView);
    }

    public View getView() {
        return mView;
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
        mMapLocationBtn.setOnClickListener((v) -> {
            if (mAccomplishTaskLatLng != null)
                new ShowLocationPopWindow(mActivity, (float) mAccomplishTaskLatLng.latitude, (float) mAccomplishTaskLatLng.longitude);

        });
        mTaskParticipantRecyclerView = view.findViewById(R.id.task_participant_list);

        mUserList = new ArrayList<>();
        mItemAdapter = new ParticipantItemAdapter(mUserList, mActivity);
        mTaskParticipantRecyclerView.setAdapter(mItemAdapter);

        mTaskParticipantRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        mTaskParticipantRecyclerView.addItemDecoration(new DividerItemDecoration(mActivity, DividerItemDecoration.VERTICAL));

        mTaskStatusFaq.setOnClickListener((v) -> {
            new AlertDialog.Builder(mActivity)
                    .setTitle("任务类型提示")
                    .setMessage("为红色则表示自建任务\n\t\t\t\t\t为绿色则表示社团任务\n\t\t\t\t\t为蓝色则表示好友指定任务\n\t\t\t\t\t为紫色则表示主动的接受任务")
                    .setNegativeButton("取消", null)
                    .show();
        });

        mMapLocationBtn.setOnClickListener((v) -> {
            if (mAccomplishTaskLatLng != null)
                new ShowLocationPopWindow(mActivity, (float) mAccomplishTaskLatLng.latitude, (float) mAccomplishTaskLatLng.longitude);
        });
    }


    public void initData(Task task) {
        if (task == null)
            return;
        String[] location = task.getAccomplishTaskLocation().split(",");
        mTaskStatusTextView.setText(task.getTaskStatus());
        mTaskNameTextView.setText(task.getTaskName());
        mTaskTypeTextView.setText(task.getTaskType());
        mTaskTimeTextClock.setFormat12Hour(task.getTaskStartAt());
        mTaskLocationTextView.setText(location[0]);
        mTaskDescTextView.setText(task.getTaskContent());
        mAccomplishTaskLatLng = new LatLng(Double.parseDouble(location[1]), Double.parseDouble(location[2]));
        // 参与列表
        new Thread(() -> {
            List<User> participants = TaskLab.getParticipant("" + task.getTaskId());
            mUserList.clear();
            mUserList.addAll(participants);
            mActivity.runOnUiThread(() -> {
                mItemAdapter.notifyDataSetChanged();
            });
        }).start();

    }

    public void updateData(){
        new Thread(() -> {
            while (UserLab.getCurrentUser() == null) ;
            String mCurrentUserId = UserLab.getCurrentUser().getId() + "";
            List<Task> tasks = TaskLab.getAllTask(mCurrentUserId);
            mActivity.runOnUiThread(()->{
                initData(tasks.get(0));
            });
        }).start();
    }
}
