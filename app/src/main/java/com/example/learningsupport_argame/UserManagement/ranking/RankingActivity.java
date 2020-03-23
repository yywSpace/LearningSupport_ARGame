package com.example.learningsupport_argame.UserManagement.ranking;


import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.learningsupport_argame.R;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RankingActivity extends AppCompatActivity {
    private ViewPager mRankingViewPager;
    private TabLayout mRankingTabLayout;
    private List<String> mTitles;
    private List<Fragment> mFragments;
    FrameLayout mReturnLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ranking_activity_layout);
        mTitles = new ArrayList<>(Arrays.asList("等级", "任务完成", "任务发布"));
        mFragments = new ArrayList<>(Arrays.asList(
                RankingLevelFragment.getInstance(this),
                RankingAccomplishCountFragment.getInstance(this),
                RankingReleaseCountFragment.getInstance(this)
        ));
        mReturnLayout = findViewById(R.id.ranking_return);
        mReturnLayout.setOnClickListener(v -> finish());
        mRankingViewPager = findViewById(R.id.ranking_view_pager_layout);
        mRankingTabLayout = findViewById(R.id.ranking_tab_layout);
        mRankingViewPager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public int getCount() {
                return mFragments.size();
            }

            @Override
            public Fragment getItem(int position) {
                return mFragments.get(position);
            }

            @Nullable
            @Override
            public CharSequence getPageTitle(int position) {
                return mTitles.get(position);
            }
        });
        mRankingTabLayout.setupWithViewPager(mRankingViewPager);
    }
}
