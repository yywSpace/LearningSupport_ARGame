package com.example.learningsupport_argame.UserManagement.UserMessage;

import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.learningsupport_argame.R;
import com.example.learningsupport_argame.UserManagement.User;
import com.example.learningsupport_argame.UserManagement.UserLab;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FriendMessageActivity extends AppCompatActivity {
    private String mCurrentUserId;
    private TextView mUserName;
    private TextView mUserLevel;
    private ImageView mImageView;

    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private FrameLayout mReturnBtn;
    private List<Fragment> mFragmentList;
    private List<String> mTitleList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_management_activity_friend_message);
        mUserName = findViewById(R.id.user_management_friend_name);
        mUserLevel = findViewById(R.id.user_management_friend_level);
        mImageView = findViewById(R.id.user_management_friend_avatar);
        mViewPager = findViewById(R.id.user_management_friend_view_paper);
        mReturnBtn = findViewById(R.id.user_management_friend_message_return);
        mReturnBtn.setOnClickListener(v -> finish());
        mCurrentUserId = getIntent().getStringExtra(User.CURRENT_USER_ID);

        mTitleList = new ArrayList<>(Arrays.asList("已发布", "已完成", "动态"));
        mFragmentList = new ArrayList<>(Arrays.asList(
                new Fragment[]{
                        ReleasedTaskFragment.getInstance(mCurrentUserId),
                        AccomplishTaskFragment.getInstance(mCurrentUserId),
                        UserDynamicFragment.getInstance(mCurrentUserId)}
        ));
        mTabLayout = findViewById(R.id.user_management_friend_tab_layout);
        mViewPager = findViewById(R.id.user_management_friend_view_paper);
        mViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int i) {
                return mFragmentList.get(i);
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
        mTabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    protected void onResume() {
        super.onResume();
        new Thread(() -> {
            User user = UserLab.getUserById(mCurrentUserId);
            runOnUiThread(() -> {
                mUserName.setText(user.getName());
                mUserLevel.setText("Lv." + user.getLevel());
                mImageView.setImageBitmap(user.getAvatar());
            });
        }).start();
    }
}
