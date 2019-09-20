package com.example.learningsupport_argame.FeedbackModel;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.learningsupport_argame.R;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

//import android.support.annotation.Nullable;
//import android.support.design.widget.TabLayout;
//import android.support.v4.app.Fragment;
//import android.support.v4.app.FragmentPagerAdapter;
//import android.support.v4.view.ViewPager;
//import android.support.v7.app.AppCompatActivity;

public class FeedbackDetailsActivity extends AppCompatActivity {
    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    private List<Fragment> fragments = new ArrayList<>();
    private List<String> titles = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feedback_activity_details);
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
