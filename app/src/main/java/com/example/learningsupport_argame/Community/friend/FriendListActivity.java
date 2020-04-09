package com.example.learningsupport_argame.Community.friend;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.learningsupport_argame.Client.ClientLab;
import com.example.learningsupport_argame.Client.UDPClient;
import com.example.learningsupport_argame.Community.FriendLab;
import com.example.learningsupport_argame.MainActivity;
import com.example.learningsupport_argame.R;
import com.example.learningsupport_argame.UserManagement.ActivityUtil;
import com.example.learningsupport_argame.UserManagement.Login.LoginActivity;
import com.example.learningsupport_argame.UserManagement.User;
import com.example.learningsupport_argame.UserManagement.UserLab;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class FriendListActivity extends AppCompatActivity {
    public static final String IN_CHAT_ROOM_LABEL = "in chat room";
    public static Boolean IN_CHAT_ROOM = false;
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
    private UDPClient mUDPClient;
    private Boolean inChatRoom;
    private FriendItemAdapter.OnRecycleViewItemClick mOnRecycleViewItemClick;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friend_list_activity_layout);
        inChatRoom = getIntent().getBooleanExtra(FriendListActivity.IN_CHAT_ROOM_LABEL, false);
        mOnRecycleViewItemClick = (view, user, position) -> {
            Log.d(TAG, "mOnRecycleViewItemClick: " + inChatRoom);
            if (inChatRoom) {
                Toast.makeText(FriendListActivity.this, "您已经在聊天室中", Toast.LENGTH_SHORT).show();
                return;
            }
            if (user.getOnlineStatus() == 0) {
                Toast.makeText(FriendListActivity.this, "当前用户不在线，请稍后重试", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(FriendListActivity.this, MainActivity.class);
                intent.putExtra("scene", "chat_room");
                //message:otherName,userName,modName
                intent.putExtra("scene_args",
                        user.getName() + "," + UserLab.getCurrentUser().getName() + "," + user.getModName().trim());
                startActivity(intent);
                Toast.makeText(FriendListActivity.this, user.getName() + "," + UserLab.getCurrentUser().getName() + "," + user.getModName(), Toast.LENGTH_SHORT).show();
                finish();
            }
        };
        if (UserLab.getCurrentUser() != null) {
            mCurrentUserID = String.valueOf(UserLab.getCurrentUser().getId());
        } else {
            SharedPreferences userInfo = getSharedPreferences(LoginActivity.PREFS_NAME, MODE_PRIVATE);
            int id = userInfo.getInt("id", 0);
            mCurrentUserID = String.valueOf(id);
        }

        // ActivityUtil.addActivity(this);
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
                mItemAdapter.setOnRecycleViewItemClick(mOnRecycleViewItemClick);
                mFriendsRecyclerView.setAdapter(mItemAdapter);
            } else {
                String name = mSearchBox.getText().toString();
                isSearchList = true;
                new Thread(() -> {
                    mSearchList = UserLab.getUserByFuzzyName(name);
                    initFriendList(mSearchList);
                    Log.d(TAG, "onCreate: " + mSearchList.size());
                    runOnUiThread(() -> {
                        mItemAdapter = new FriendItemAdapter(this, mFriendList, mSearchList);
                        mItemAdapter.setOnRecycleViewItemClick(mOnRecycleViewItemClick);
                        mFriendsRecyclerView.setAdapter(mItemAdapter);
                        mSearchButton.setBackgroundResource(R.drawable.friend_list_pop_window_cross);
                    });
                }).start();
            }
        });
        mItemAdapter = new FriendItemAdapter(this, mFriendList, null);
        mItemAdapter.setOnRecycleViewItemClick(mOnRecycleViewItemClick);
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
                initFriendList(mFriendList);
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
        Log.d(TAG, "onResume: ");

        new Thread(() -> {
            while (UserLab.getCurrentUser() == null) ;
            mUDPClient = ClientLab.getInstance(this, ClientLab.sPort, ClientLab.sIp, ClientLab.sUserName);
            Log.d(TAG, "onResume: mCurrentUserID" + mCurrentUserID);
            List<User> friendList = FriendLab.getFriends(mCurrentUserID);
            mFriendList.clear();
            mFriendList.addAll(friendList);
            initFriendList(mFriendList);
            runOnUiThread(() -> mItemAdapter.notifyDataSetChanged());
            Log.d(TAG, "onResume: mFriendList.size " + mFriendList.size());

        }).start();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState: " + UserLab.getCurrentUser().getId());
        outState.putInt(User.CURRENT_USER_ID, UserLab.getCurrentUser().getId());
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    void initFriendList(List<User> userList) {
        List<String> onlineUser = mUDPClient.onlineUser();
        // 在线的提前
        int indexCount = 0;
        for (int i = 0; i < userList.size(); i++) {
            if (onlineUser.contains(userList.get(i).getName())) {
                userList.get(i).setOnlineStatus(1);
                Collections.swap(userList, indexCount, i);
                indexCount++;
            }
        }
        Map<String, String> messageMap = mUDPClient.getMessageMap();
        // 发送消息的提前
        indexCount = 0;
        for (int i = 0; i < userList.size(); i++) {
            if (messageMap.containsKey(userList.get(i).getName())) {
                // 接收到消息
                userList.get(i).setOnlineStatus(2);
                Collections.swap(userList, indexCount, i);
                indexCount++;
            }
        }
    }
}
