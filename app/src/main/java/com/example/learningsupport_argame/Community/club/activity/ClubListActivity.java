package com.example.learningsupport_argame.Community.club.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.learningsupport_argame.Community.club.fragment.AllClubListFragment;
import com.example.learningsupport_argame.Community.club.fragment.AttendedClubListFragment;
import com.example.learningsupport_argame.Community.club.fragment.CreatedClubListFragment;
import com.example.learningsupport_argame.R;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ClubListActivity extends AppCompatActivity {
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private FrameLayout mReturnLayout;
    private FrameLayout mCreateLayout;
    private List<Fragment> mFragmentList;
    private List<String> mTitleList;
    private ImageButton mSearchButton;
    private EditText mSearchBox;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.club_list_activity_layout);

        mSearchBox = findViewById(R.id.club_search_box);
        mSearchButton = findViewById(R.id.club_search_button);
        mReturnLayout = findViewById(R.id.club_list_return);
        mCreateLayout = findViewById(R.id.club_create);
        mCreateLayout.setOnClickListener(v -> {
            startActivity(new Intent(this, ClubCreateActivity.class));
        });
        mReturnLayout.setOnClickListener(v -> finish());
        mFragmentList = new ArrayList<>(Arrays.asList(
                CreatedClubListFragment.newInstance(),
                AttendedClubListFragment.newInstance(),
                AllClubListFragment.newInstance()
        ));
        mTitleList = new ArrayList<>(Arrays.asList("我创建的", "我参加的", "其他社团"));
        mViewPager = findViewById(R.id.club_view_pager);
        mViewPager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
            @Override
            public Fragment getItem(int position) {
                return mFragmentList.get(position);
            }

            @Override
            public int getCount() {
                return mFragmentList.size();
            }

            @Nullable
            @Override
            public CharSequence getPageTitle(int position) {
                return mTitleList.get(position);
            }
        });
        mTabLayout = findViewById(R.id.club_tab_layout);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    public EditText getSearchBox() {
        return mSearchBox;
    }

    public ImageButton getSearchButton() {
        return mSearchButton;
    }


}
