package com.example.learningsupport_argame.community.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.learningsupport_argame.R;
import com.example.learningsupport_argame.UserManagement.User;
import com.example.learningsupport_argame.UserManagement.UserLab;
import com.example.learningsupport_argame.community.FriendLab;
import com.example.learningsupport_argame.community.adapter.FriendItemAdapter;

import java.util.ArrayList;
import java.util.List;

public class FriendListDialog {
    private String mCurrentUserID;
    private LinearLayout mFriendsSearch;  //外层的搜索框控件
    private SwipeRefreshLayout mRefreshLayout;
    private int mLlSearchHeight; // 搜索框的高度
    private int mScrollY;  //recycler view 滑动的距离
    private boolean isShow = true;  //搜索框是否显示
    private RecyclerView mFriendsRecyclerView;
    private boolean isAnimating;//是否正在进行动画

    private List<User> mFriendList;
    private FriendItemAdapter mItemAdapter;

    private Activity mContext;
    private AlertDialog mFriendAlertDialog;

    public FriendListDialog(Activity context, String currentUserID) {
        mContext = context;
        mCurrentUserID = currentUserID;

        // 为增强体验，如果已有数据则提前显示，后续在onResume中继续查询更新数据
        if(FriendLab.getFriendList() != null)
            mFriendList = FriendLab.getFriendList();
        else
            mFriendList = new ArrayList<>();

        mItemAdapter = new FriendItemAdapter(context, mFriendList);

        View view = LayoutInflater.from(context).inflate(R.layout.friend_list_fragment_layout, null, false);
        mFriendsRecyclerView = view.findViewById(R.id.friend_list_recycler_view);
        mFriendsSearch = view.findViewById(R.id.friend_list_search);
        mRefreshLayout = view.findViewById(R.id.friend_list_refresh_layout);
        mFriendAlertDialog = new AlertDialog.Builder(context)
                .setView(view)
                .setTitle("好友列表")
                .setNegativeButton("取消", null)
                .setPositiveButton("确认", null)
                .create();
        initView();


        new Thread(() -> {
            List<User> friendList = FriendLab.getFriends(mCurrentUserID);
            mFriendList.clear();
            mFriendList.addAll(friendList);
            context.runOnUiThread(() -> {
                mItemAdapter.notifyDataSetChanged();
            });
        }).start();
    }

    void initView() {
        // 下拉刷新
        mRefreshLayout.setOnRefreshListener(() -> {
            if (mRefreshLayout.isRefreshing() == true)
                mRefreshLayout.setRefreshing(false);
            new Thread(() -> {
                List<User> friendList = FriendLab.getFriends(mCurrentUserID);
                mFriendList.clear();
                mFriendList.addAll(friendList);
                mContext.runOnUiThread(() -> {
                    mItemAdapter.notifyDataSetChanged();
                });
            }).start();

        });

        mFriendsRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
//        mFriendsRecyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
        mFriendsRecyclerView.setAdapter(mItemAdapter);
        mFriendsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                //dy是垂直滚动距离，手指上滑动的时候为正，手指下滑的时候为负

                //需要获取llSearch 的高度作为 判断条件   所以布局文件中  llSearch的 visiable属性  不能设置为 gone
                // 设置为  gone之后，llSearch 不进行渲染  获取不到高度
                if (mLlSearchHeight == 0) {
                    mLlSearchHeight = mFriendsSearch.getHeight();
                }
                //记录滑动的距离
                mScrollY += dy;

                if (mScrollY <= 0) {
                    mFriendsSearch.setVisibility(View.INVISIBLE);
                } else {
                    mFriendsSearch.setVisibility(View.VISIBLE);
                }

                if (isAnimating || (mScrollY <= mLlSearchHeight)) {
                    return;
                }

                if (dy < 0) {
                    if (isShow) {
                        return;
                    }

                    ObjectAnimator animator = ObjectAnimator.ofFloat(mFriendsSearch, "translationY", -mLlSearchHeight, 0);
                    animator.setDuration(300);
                    animator.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            isShow = true;
                            isAnimating = false;
                            animation.removeAllListeners();
                        }

                        @Override
                        public void onAnimationStart(Animator animation) {
                            super.onAnimationStart(animation);
                            isAnimating = true;
                        }
                    });
                    animator.start();
                } else {
                    if (!isShow) {
                        return;
                    }
                    ObjectAnimator animator = ObjectAnimator.ofFloat(mFriendsSearch, "translationY", 0, -mLlSearchHeight);
                    animator.setDuration(300);
                    animator.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            isShow = false;
                            isAnimating = false;
                            animation.removeAllListeners();
                        }

                        @Override
                        public void onAnimationStart(Animator animation) {
                            super.onAnimationStart(animation);
                            isAnimating = true;

                        }
                    });
                    animator.start();
                }
            }
        });
    }

    public AlertDialog getFriendAlertDialog() {
        return mFriendAlertDialog;
    }

}

