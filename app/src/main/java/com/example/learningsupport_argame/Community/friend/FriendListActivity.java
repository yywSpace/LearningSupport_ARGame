package com.example.learningsupport_argame.Community.friend;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.learningsupport_argame.Community.FriendLab;
import com.example.learningsupport_argame.R;
import com.example.learningsupport_argame.UserManagement.ActivityUtil;
import com.example.learningsupport_argame.UserManagement.User;
import com.example.learningsupport_argame.UserManagement.UserLab;

import java.util.ArrayList;
import java.util.List;

public class FriendListActivity extends AppCompatActivity {
    private static final String TAG = "FriendListActivity";
    private String mCurrentUserID;
    private ImageButton mSearchButton;
    private FrameLayout mReturnLayout;
    private EditText mSearchBox;
    private List<User> mFriendList;
    private List<User> mSearchList;
    private FriendItemAdapter mItemAdapter;
    private SwipeRefreshLayout mRefreshLayout;
    private RecyclerView mFriendsRecyclerView;
    private boolean isSearchList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friend_list_activity_layout);

        ActivityUtil.addActivity(this);
        mCurrentUserID = String.valueOf(UserLab.getCurrentUser().getId());
        // 为增强体验，如果已有数据则提前显示，后续在onResume中继续查询更新数据
        if (FriendLab.getFriendList() != null)
            mFriendList = FriendLab.getFriendList();
        else
            mFriendList = new ArrayList<>();

        mSearchButton = findViewById(R.id.friend_search_button);
        mReturnLayout = findViewById(R.id.friend_list_return);
        mReturnLayout.setOnClickListener(v -> finish());
        mSearchButton.setOnClickListener(v -> {
            // 如果处于搜索结果列表
            if (isSearchList) {
                isSearchList = false;
                mSearchButton.setBackgroundResource(R.drawable.sousuo);
                mItemAdapter = new FriendItemAdapter(this, mFriendList, null);
                mFriendsRecyclerView.setAdapter(mItemAdapter);
            } else {
                String name = mSearchBox.getText().toString();
                isSearchList = true;
                new Thread(() -> {
                    mSearchList = UserLab.getUserByFuzzyName(name);
                    Log.d(TAG, "onCreate: " + mSearchList.size());
                    runOnUiThread(() -> {
                        mItemAdapter = new FriendItemAdapter(this, mFriendList, mSearchList);
                        mFriendsRecyclerView.setAdapter(mItemAdapter);
                        mSearchButton.setBackgroundResource(R.drawable.friend_list_pop_window_cross);
                    });
                }).start();
            }
        });
        mItemAdapter = new FriendItemAdapter(this, mFriendList, null);
        mFriendsRecyclerView = findViewById(R.id.friend_list_recycler_view);
        mRefreshLayout = findViewById(R.id.friend_list_refresh_layout);
        mSearchBox = findViewById(R.id.friend_search_box);
        // 下拉刷新
        mRefreshLayout.setOnRefreshListener(() -> {
            if (isSearchList) {
                mRefreshLayout.setRefreshing(false);
                return;
            }
            new Thread(() -> {
                List<User> friendList = FriendLab.getFriends(mCurrentUserID);
                mFriendList.clear();
                mFriendList.addAll(friendList);
                runOnUiThread(() -> {
                    mItemAdapter.notifyDataSetChanged();
                    mRefreshLayout.setRefreshing(false);
                });
            }).start();
        });
        mFriendsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mFriendsRecyclerView.setAdapter(mItemAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        new Thread(() -> {
            List<User> friendList = FriendLab.getFriends(mCurrentUserID);
            mFriendList.clear();
            mFriendList.addAll(friendList);
            runOnUiThread(() -> mItemAdapter.notifyDataSetChanged());
        }).start();
    }
}
