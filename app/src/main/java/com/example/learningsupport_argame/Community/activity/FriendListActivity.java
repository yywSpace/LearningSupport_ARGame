package com.example.learningsupport_argame.Community.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.learningsupport_argame.R;
import com.example.learningsupport_argame.UserManagement.ActivityUtil;
import com.example.learningsupport_argame.UserManagement.User;
import com.example.learningsupport_argame.Community.fragment.FriendListFragment;

public class FriendListActivity extends AppCompatActivity {
    private String mCurrentUserID;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friendlist_layout);

        ActivityUtil.addActivity(this);

        mCurrentUserID = getIntent().getStringExtra(User.CURRENT_USER_ID);

        FriendListFragment fragment = FriendListFragment.getInstance(mCurrentUserID);
        getSupportFragmentManager().beginTransaction().replace(R.id.friend_list_fragment_container, fragment).commit();
    }
}
