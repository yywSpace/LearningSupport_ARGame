package com.example.learningsupport_argame.UserManagement.achievement;

import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.learningsupport_argame.R;
import com.example.learningsupport_argame.UserManagement.UserLab;


public class AchievementActivity extends AppCompatActivity {
    private FrameLayout mReturnLayout;
    private GridLayout mUserLevelLayout;
    private GridLayout mUserAccomplishLayout;
    private GridLayout mUserReleaseLayout;
    private GridLayout mUserActivityDayLayout;
    private int[] mLoginBadgeArray = {1, 3, 7, 15, 30, 60, 70};
    private int[] mLevelBadgeArray = {0, 5, 10, 15, 20, 25};
    private int[] mReleaseBadgeArray = {5, 20, 40};
    private int[] mAccomplishBadgeArray = {5, 20, 40, 60};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_achievement_layout);
        mReturnLayout = findViewById(R.id.achievement_return);
        mReturnLayout.setOnClickListener(v -> finish());
        mUserReleaseLayout = findViewById(R.id.achievement_user_release_layout);
        int releaseCount = UserLab.getCurrentUser().getReleaseCount();
        initBadgeLayout("发布任务的数量达到%d个", "任务发布徽章", releaseCount, mReleaseBadgeArray, mUserReleaseLayout);
        mUserActivityDayLayout = findViewById(R.id.achievement_user_active_day_layout);
        int loginCount = UserLab.getCurrentUser().getLoginCount();
        initBadgeLayout("累计签到时间达到%d天", "签到徽章", loginCount, mLoginBadgeArray, mUserActivityDayLayout);
        mUserLevelLayout = findViewById(R.id.achievement_user_level_layout);
        int level = UserLab.getCurrentUser().getLevel();
        initBadgeLayout("等级达到%d级", "等级徽章", level, mLevelBadgeArray, mUserLevelLayout);
        mUserAccomplishLayout = findViewById(R.id.achievement_user_accomplish_layout);
        int accomplishCount = UserLab.getCurrentUser().getAccomplishCount();
        initBadgeLayout("完成任务的数量达到%d个", "任务完成徽章", accomplishCount, mAccomplishBadgeArray, mUserAccomplishLayout);
    }

    void initBadgeLayout(String badgeDescFormat, String title, int count, int[] badgeLimitArray, GridLayout layout) {
        for (int i = 0; i < badgeLimitArray.length; i++) {
            ImageView imageView = (ImageView) layout.getChildAt(i);
            final int index = i;
            imageView.setOnClickListener(v -> {
                View view = LayoutInflater.from(AchievementActivity.this)
                        .inflate(R.layout.user_achievement_badge_dialog_layout, null, false);
                ImageView badgeIcon = view.findViewById(R.id.achievement_badge_icon);
                TextView badgeDesc = view.findViewById(R.id.achievement_badge_desc);
                TextView badgeProcess = view.findViewById(R.id.achievement_badge_process);
                badgeIcon.setImageDrawable(imageView.getDrawable());
                badgeDesc.setText(String.format(badgeDescFormat, badgeLimitArray[index]));
                if (count >= badgeLimitArray[index])
                    badgeProcess.setText(badgeLimitArray[index] + "/" + badgeLimitArray[index]);
                else
                    badgeProcess.setText(count + "/" + badgeLimitArray[index]);
                AlertDialog alertDialog = new AlertDialog.Builder(this)
                        .setTitle(title)
                        .setView(view)
                        .setIcon(imageView.getDrawable())
                        .create();
                alertDialog.show();
            });

            if (count >= badgeLimitArray[i])
                continue;
            Log.d("123", "initBadgeLayout: " + title + " " + count);
            // 设置颜色为灰色
            ColorMatrix cm = new ColorMatrix();
            cm.setSaturation(0); // 设置饱和度
            ColorMatrixColorFilter grayColorFilter = new ColorMatrixColorFilter(cm);
            imageView.setColorFilter(grayColorFilter);
        }
    }
}
