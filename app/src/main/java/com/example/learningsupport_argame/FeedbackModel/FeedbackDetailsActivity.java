package com.example.learningsupport_argame.FeedbackModel;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.learningsupport_argame.R;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class FeedbackDetailsActivity extends AppCompatActivity {
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private FrameLayout mReturnLayout;
    private List<Fragment> fragments = new ArrayList<>();
    private List<String> titles = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feedback_activity_details);

        mReturnLayout = findViewById(R.id.feedback_return);
        mReturnLayout.setOnClickListener(v -> finish());
        mTabLayout = findViewById(R.id.feedback_details_tabs);
        mViewPager = findViewById(R.id.feedback_details_vp_content);
        fragments.add(RadarChartFragment.newInstance());
        fragments.add(LineChartFragment.newInstance());
        fragments.add(BarChartFragment.newInstance());
        titles.add("RadarChart");
        titles.add("LineChart");
        titles.add("BarChart");

        mViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public int getCount() {
                return fragments.size();
            }

            @Override
            public Fragment getItem(int i) {
                return fragments.get(i);
            }

            @Nullable
            @Override
            public CharSequence getPageTitle(int position) {
                return titles.get(position);
            }

        });
        mTabLayout.setupWithViewPager(mViewPager);
    }
}
