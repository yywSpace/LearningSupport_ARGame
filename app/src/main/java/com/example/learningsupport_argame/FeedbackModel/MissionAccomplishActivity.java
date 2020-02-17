package com.example.learningsupport_argame.FeedbackModel;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.learningsupport_argame.MonitorModel.MonitorInfo;
import com.example.learningsupport_argame.R;
import com.example.learningsupport_argame.Task.TaskLab;
import com.example.learningsupport_argame.UserManagement.UserLab;


// todo 任务失败时，计算失败原因
public class MissionAccomplishActivity extends AppCompatActivity {
    private String TAG = "MissionAccomplishActivity";
    private TextView mAccomplishResult, mPhoneUseRate, mOutOfRangeRate, mAttentionRate, mPhoneUseCount, mExp, mGold, mItem;
    private ImageView mAccomplishStateImage, mItemImage;
    private ProgressBar mPhoneUseProgressBar, mAttentionProgressBar, mOutOfRangeProgressBar;
    private LinearLayout mRatingBarLayout;
    private RatingBarView mMissionRateBar;
    private MonitorInfo mMonitorInfo;
    private long lastClickTime = 0;
    boolean taskAccomplishSuccess = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feedback_activity_accomplish);
        // 监督信息
        mMonitorInfo = (MonitorInfo) getIntent().getSerializableExtra(MonitorInfo.MONITOR_INFO);
        if (mMonitorInfo == null) {
            mMonitorInfo = new MonitorInfo();
            mMonitorInfo.setMonitorTaskScreenOffTime(1);
            mMonitorInfo.setTaskOutOfRangeTime(1);
            mMonitorInfo.setMonitorPhoneUseCount(1);
            mMonitorInfo.setMonitorScreenOnAttentionSpan(1);
            mMonitorInfo.setMonitorTaskScreenOnTime(1);
            mMonitorInfo.setTaskBeginTime("2019-11-27 20:30");
            mMonitorInfo.setTaskEndTime("2019-11-27 21:30");
        }

        initView();
        initData();
    }

    void initView() {
        mExp = findViewById(R.id.feedback_reward_exp);
        mGold = findViewById(R.id.feedback_reward_gold);
        mItem = findViewById(R.id.feedback_reward_item);
        mItemImage = findViewById(R.id.feedback_reward_item_image);
        mAccomplishResult = findViewById(R.id.feedback_accomplish_result);
        mAccomplishStateImage = findViewById(R.id.feedback_accomplish_state_image);
        mPhoneUseCount = findViewById(R.id.feedback_phone_use_count);
        mPhoneUseRate = findViewById(R.id.feedback_phone_use_rate);
        mOutOfRangeRate = findViewById(R.id.feedback_out_of_range_rate);
        mAttentionRate = findViewById(R.id.feedback_attention_rate);
        mPhoneUseProgressBar = findViewById(R.id.feedback_phone_use_progressBar);
        mAttentionProgressBar = findViewById(R.id.feedback_attention_progressBar);
        mOutOfRangeProgressBar = findViewById(R.id.feedback_out_of_range_progressBar);
        mRatingBarLayout = findViewById(R.id.feedback_rating_bar_layout);
        mMissionRateBar = findViewById(R.id.feedback_mission_rate);
    }

    void initData() {
        // 任务执行中手机使用时间
        mPhoneUseProgressBar.setMax(mMonitorInfo.getTaskTotalTime());
        mPhoneUseProgressBar.setProgress((int) mMonitorInfo.getMonitorTaskScreenOnTime());
        float phoneUseRate = mMonitorInfo.getMonitorTaskScreenOnTime() / mMonitorInfo.getTaskTotalTime();
        mPhoneUseRate.setText(String.format("%.2f%%", phoneUseRate * 100));
        // 任务执行中专注时间
        mAttentionProgressBar.setMax(mMonitorInfo.getTaskTotalTime());
        mAttentionProgressBar.setProgress((int) mMonitorInfo.getMonitorAttentionTime());
        float attentionRate = mMonitorInfo.getMonitorAttentionTime() / mMonitorInfo.getTaskTotalTime();
        mAttentionRate.setText(String.format("%.2f%%", attentionRate * 100));
        // 任务执行中超出任务执行地点的时间
        mOutOfRangeProgressBar.setMax(mMonitorInfo.getTaskTotalTime());
        mOutOfRangeProgressBar.setProgress(mMonitorInfo.getTaskOutOfRangeTime());
        float outOfRangeRate = mMonitorInfo.getTaskOutOfRangeTime() / mMonitorInfo.getTaskTotalTime();
        mOutOfRangeRate.setText(String.format("%.2f%%", outOfRangeRate * 100));
        // 任务执行中手机使用次数
        mPhoneUseCount.setText(mMonitorInfo.getMonitorPhoneUseCount() + "次");
        // TODO: 20-2-17 任务奖励的设置与应用
        // 奖励
        mExp.setText("x100");
        mGold.setText("x100");
        mItem.setText("x100");
        mItemImage.setImageResource(R.drawable.task_reward_potion_red);
        // 加权平均
        float rate = (attentionRate * 60 + (1 - outOfRangeRate) * 40) / 100;
        Log.d(TAG, "phoneUseRate: " + phoneUseRate);
        Log.d(TAG, "attentionRate: " + attentionRate);
        Log.d(TAG, "outOfRangeRate: " + outOfRangeRate);
        Log.d(TAG, "rate: " + rate
        );
        // 评分
        mMissionRateBar.setRating(rate);
        // 如果评分地狱0.4则判定此次任务失败
        if (rate >= 0.4) {
            taskAccomplishSuccess = true;
        }

        // 根据任务成功与否设置图片,及进行数据库操作
        if (taskAccomplishSuccess) {
            // 完成结果
            mAccomplishResult.setText(R.string.feedback_mission_success);
            mAccomplishStateImage.setImageResource(R.drawable.task_accomplish_success);

        } else {
            mAccomplishResult.setText(R.string.feedback_mission_failure);
            mAccomplishStateImage.setImageResource(R.drawable.task_accomplish_failure);
        }
        new Thread(() -> {
            // 设置此任务为结束状态
            mMonitorInfo.getTask().setTaskStatus("已结束");
            TaskLab.updateTask(mMonitorInfo.getTask());
            // 设置任务参与者完成状态
            if (taskAccomplishSuccess) {
                TaskLab.updateTaskParticipantStatus(
                        mMonitorInfo.getTask().getTaskId() + "",
                        UserLab.getCurrentUser().getId() + "",
                        "完成");
            } else {
                TaskLab.updateTaskParticipantStatus(
                        mMonitorInfo.getTask().getTaskId() + "",
                        UserLab.getCurrentUser().getId() + "",
                        "失败");
            }
        }).start();
        takeAnimation();
        mRatingBarLayout.setOnClickListener(v -> {
            long now = System.currentTimeMillis();
            if (now - lastClickTime > 1000) {
                lastClickTime = now;
                startActivity(new Intent(
                        MissionAccomplishActivity.this,
                        FeedbackDetailsActivity.class));
            }
        });
    }

    void takeAnimation() {
        // 添加任务结束图片和文字显示的动画
        mAccomplishStateImage.setAlpha(0f);
        mAccomplishStateImage.setScaleX(0);
        mAccomplishStateImage.setScaleY(0);
        mAccomplishStateImage.animate()
                .alpha(1)
                .scaleY(1)
                .scaleX(1)
                .setDuration(500)
                .setInterpolator(new LinearInterpolator());
        mAccomplishResult.setAlpha(0f);
        mAccomplishResult.setScaleX(0);
        mAccomplishResult.setScaleY(0);
        mAccomplishResult.animate()
                .alpha(1)
                .scaleY(1)
                .scaleX(1)
                .setDuration(500)
                .setInterpolator(new LinearInterpolator());
        // 添加进度条动画
    }
}
