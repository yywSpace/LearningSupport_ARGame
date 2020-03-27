package com.example.learningsupport_argame.UserManagement.UserMessage;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.learningsupport_argame.Community.FriendLab;
import com.example.learningsupport_argame.R;
import com.example.learningsupport_argame.UserManagement.User;
import com.example.learningsupport_argame.UserManagement.UserLab;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// TODO: 20-3-26 测试添加好友
public class FriendMessageActivity extends AppCompatActivity {
    public static final String FRIEND_STATUS = "friend_status";
    private String mCurrentUserId;
    private TextView mUserName;
    private TextView mUserLevel;
    private ImageView mImageView;
    private Button mAttentionButton;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private FrameLayout mReturnBtn;
    private List<Fragment> mFragmentList;
    private List<String> mTitleList;
    private static String TAG = "FriendMessageActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_management_activity_friend_message);
        Log.d(TAG, "onCreate: " + mCurrentUserId);
        mUserName = findViewById(R.id.user_management_friend_name);
        mUserLevel = findViewById(R.id.user_management_friend_level);
        mImageView = findViewById(R.id.user_management_friend_avatar);
        mViewPager = findViewById(R.id.user_management_friend_view_paper);
        mAttentionButton = findViewById(R.id.attention_button);
        mReturnBtn = findViewById(R.id.user_management_friend_message_return);
        mReturnBtn.setOnClickListener(v -> finish());
        mCurrentUserId = getIntent().getStringExtra(User.CURRENT_USER_ID);

        mTitleList = new ArrayList<>(Arrays.asList("已发布", "已完成", "动态"));
        mFragmentList = new ArrayList<>(Arrays.asList(
                FriendReleasedTaskFragment.getInstance(mCurrentUserId),
                FriendAccomplishTaskFragment.getInstance(mCurrentUserId),
                FriendDynamicFragment.getInstance(mCurrentUserId)));
        mTabLayout = findViewById(R.id.user_management_friend_tab_layout);
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
            User friend = FriendLab.getFriendById(Integer.parseInt(mCurrentUserId));
            runOnUiThread(()->{
                if (friend != null) {
                    mAttentionButton.setClickable(false);
                    mAttentionButton.setText("已关注");
                } else {
                    mAttentionButton.setClickable(true);
                    mAttentionButton.setOnClickListener(v -> {
                        new Thread(() -> {
                            UserLab.addFriend(mCurrentUserId);
                            runOnUiThread(() -> {
                                mAttentionButton.setClickable(false);
                                mAttentionButton.setText("已关注");
                            });
                        }).start();
                    });
                }
            });

            User user = UserLab.getUserById(mCurrentUserId);
            Log.d(TAG, "onResume: " + user);
            runOnUiThread(() -> {
                mUserName.setText(user.getName());
                mUserLevel.setText("Lv." + user.getLevel());
                if (user.getAvatar() != null)
                    mImageView.setImageBitmap(user.getAvatar());
                else {
                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.img_avatar_05);
                    mImageView.setImageBitmap(bitmap);
                }
            });
        }).start();
    }
}
