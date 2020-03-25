package com.example.learningsupport_argame.UserManagement.achievement;

import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.learningsupport_argame.R;
import com.example.learningsupport_argame.UserManagement.UserLab;

import java.util.ArrayList;
import java.util.List;

public class AchievementActivity extends AppCompatActivity {
    private FrameLayout mReturnLayout;
    private GridLayout mUserLevelLayout;
    private GridLayout mUserActivityDayLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_achievement_layout);
        mReturnLayout = findViewById(R.id.achievement_return);
        mReturnLayout.setOnClickListener(v -> finish());
        mUserLevelLayout = findViewById(R.id.achievement_user_level_layout);
        mUserActivityDayLayout = findViewById(R.id.achievement_user_active_day_layout);
        int level = UserLab.getCurrentUser().getLevel();
        for (int i = 0; i < mUserLevelLayout.getChildCount(); i++) {
            if (level / 5 == i)
                continue;
            ImageView imageView = (ImageView) mUserLevelLayout.getChildAt(i);
            ColorMatrix cm = new ColorMatrix();
            cm.setSaturation(0); // 设置饱和度
            ColorMatrixColorFilter grayColorFilter = new ColorMatrixColorFilter(cm);
            imageView.setColorFilter(grayColorFilter); // 如果想恢复彩色显示，设置为null即可
        }
        int loginCount = UserLab.getCurrentUser().getLoginCount();
        for (int i = 0; i < mUserActivityDayLayout.getChildCount(); i++) {
            if (i + 1 <= loginCount)
                continue;
            ImageView imageView = (ImageView) mUserActivityDayLayout.getChildAt(i);
            ColorMatrix cm = new ColorMatrix();
            cm.setSaturation(0); // 设置饱和度
            ColorMatrixColorFilter grayColorFilter = new ColorMatrixColorFilter(cm);
            imageView.setColorFilter(grayColorFilter);
        }
    }
}
