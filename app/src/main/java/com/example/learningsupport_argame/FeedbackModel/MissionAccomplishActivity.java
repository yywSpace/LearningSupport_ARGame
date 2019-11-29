package com.example.learningsupport_argame.FeedbackModel;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.learningsupport_argame.MonitorModel.MonitorInfo;
import com.example.learningsupport_argame.R;


// 任务失败时，传入失败原因
public class MissionAccomplishActivity extends AppCompatActivity {
    private String TAG = "MissionAccomplishActivity";
    private TextView mAccomplishResult, mPhoneUseRate, mOutOfRangeRate, mAttentionRate, mPhoneUseCount, mExp, mGold, mItem;
    private ImageView mAccomplishStateImage, mItemImage;
    private ProgressBar mPhoneUseProgressBar, mAttentionProgressBar, mOutOfRangeProgressBar;
    private LinearLayout mRatingBarLayout;
    private RatingBarView mMissionRateBar;
    private MonitorInfo mMonitorInfo;
    private long lastClickTime = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feedback_activity_accomplish);
        // 监督信息
        mMonitorInfo = (MonitorInfo) getIntent().getSerializableExtra(MonitorInfo.MONITOR_INFO);
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
        // 完成结果
        mAccomplishResult.setText(R.string.feedback_mission_success);
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
        // 奖励
        mExp.setText("x100");
        mGold.setText("x100");
        mItem.setText("x100");
        mItemImage.setImageResource(R.drawable.task_reward_potion_red);
        // 加权平均
        float rate = ((1 - phoneUseRate) * 40 + attentionRate * 50 + outOfRangeRate * 10) / 100;
        Log.d(TAG, "phoneUseRate: "+phoneUseRate);
        Log.d(TAG, "attentionRate: "+attentionRate);
        Log.d(TAG, "outOfRangeRate: "+outOfRangeRate);
        Log.d(TAG, "rate: "+rate);
        // 评分
        mMissionRateBar.setRating(rate);
        // 根据任务成功与否设置图片
        boolean taskAccomplishSuccess = false;
        if (taskAccomplishSuccess)
            mAccomplishStateImage.setImageResource(R.drawable.task_accomplish_success);
        else
            mAccomplishStateImage.setImageResource(R.drawable.task_accomplish_failure);

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
}
