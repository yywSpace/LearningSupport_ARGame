package com.example.learningsupport_argame.FeedbackModel;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.learningsupport_argame.MonitorModel.MonitorInfo;
import com.example.learningsupport_argame.MonitorModel.MonitorInfoLab;
import com.example.learningsupport_argame.R;

//import android.support.annotation.Nullable;
//import android.support.v7.app.AppCompatActivity;

// 任务失败时，传入失败原因
public class MissionAccomplishActivity extends AppCompatActivity {
    private TextView mAccomplishResult, mPoints, mExp, mItem, mPhoneUseRate, mPhoneUseCountRate, mAttentionRate;
    private ImageView mAccomplishStateImage;
    private ProgressBar mPhoneUseProgressBar, mAttentionProgressBar, mPhoneUseCountProgressBar;
    private LinearLayout mRatingBarLayout;
    private RatingBar mMissionRateBar;
    private MonitorInfo mMonitorInfo;
    private MonitorInfoLab mMonitorInfoLab;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feedback_activity_accomplish);

        mMonitorInfoLab = MonitorInfoLab.get();

        mAccomplishResult = findViewById(R.id.feedback_accomplish_result);
        mAccomplishStateImage = findViewById(R.id.feedback_accomplish_state_image);

        mPhoneUseRate = findViewById(R.id.feedback_phone_use_rate);
        mPhoneUseCountRate = findViewById(R.id.feedback_phone_use_count_rate);
        mAttentionRate = findViewById(R.id.feedback_attention_rate);
        mPhoneUseProgressBar = findViewById(R.id.feedback_phone_use_progressBar);
        mAttentionProgressBar = findViewById(R.id.feedback_attention_progressBar);
        mPhoneUseCountProgressBar = findViewById(R.id.feedback_phone_use_count_progressBar);

        mPoints = findViewById(R.id.feedback_points);
        mExp = findViewById(R.id.feedback_exp);
        mItem = findViewById(R.id.feedback_item);

        mRatingBarLayout = findViewById(R.id.feedback_rating_bar_layout);
        mMissionRateBar = findViewById(R.id.feedback_mission_rate);
        // 监督信息
        mMonitorInfo = mMonitorInfoLab.getMonitorInfo(0);

        // 完成结果
        mAccomplishResult.setText(R.string.feedback_mission_success);
        // 各监督项
        mPhoneUseProgressBar.setMax((int) mMonitorInfo.getTaskTotalTime());
        mPhoneUseProgressBar.setProgress((int) mMonitorInfo.getMonitorTaskScreenOnTime());
        float phoneUseRate = mMonitorInfo.getMonitorTaskScreenOnTime() / mMonitorInfo.getTaskTotalTime();
        mPhoneUseRate.setText(String.format("%.2f%%", phoneUseRate * 100));

        mAttentionProgressBar.setMax((int) mMonitorInfo.getMonitorTaskScreenOnTime());
        mAttentionProgressBar.setProgress((int) mMonitorInfo.getMonitorScreenOnAttentionSpan());
        float attentionRate = mMonitorInfo.getMonitorScreenOnAttentionSpan() / mMonitorInfo.getMonitorTaskScreenOnTime();
        mAttentionRate.setText(String.format("%.2f%%", attentionRate * 100));

        mPhoneUseCountProgressBar.setMax(100);
        mPhoneUseCountProgressBar.setProgress((int) mMonitorInfo.getMonitorPhoneUseCount());
        float phoneUseCountRate = mMonitorInfo.getMonitorPhoneUseCount() / 100;
        mPhoneUseCountRate.setText(String.format("%.2f%%", phoneUseCountRate * 100));
        // 奖励
        mPoints.setText("100");
        mExp.setText("100");
        mItem.setText("item");

        // 加权平均
        float rate = ((1 - phoneUseRate) * 40 + attentionRate * 50 + phoneUseCountRate * 10) / 100;
        // 评分
        mMissionRateBar.setRating(rate * 5);

        mRatingBarLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MissionAccomplishActivity.this, FeedbackDetailsActivity.class));
            }
        });
    }
}
