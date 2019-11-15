package com.example.learningsupport_argame.community.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.text.Layout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.learningsupport_argame.R;
import com.example.learningsupport_argame.UserManagement.User;
import com.example.learningsupport_argame.UserManagement.UserLab;
import com.example.learningsupport_argame.client.ClientLab;
import com.example.learningsupport_argame.client.UDPClient;
import com.example.learningsupport_argame.community.FriendLab;
import com.example.learningsupport_argame.community.adapter.FriendItemAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class FriendListDialog {
    private final String TAG = "FriendListDialog";
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

    private PopupWindow mPopupWindow;
    private ImageView mReturnImageView;

    String[] mUserAndMessages;
    private FriendItemAdapter.OnRecycleViewItemClick mOnRecycleViewItemClick;

    Handler mHandler;

    private UDPClient mUDPClient;

    public FriendListDialog(Activity context, String currentUserID, String[] userAndMessages) {
        mContext = context;
        mCurrentUserID = currentUserID;
        mUserAndMessages = userAndMessages;

        mUDPClient = ClientLab.getInstance(ClientLab.sPort, ClientLab.sIp, ClientLab.sUserName);

        mHandler = new Handler();
        // 为增强体验，如果已有数据则提前显示，后续在onResume中继续查询更新数据
        if (FriendLab.getFriendList() != null)
            mFriendList = FriendLab.getFriendList();
        else
            mFriendList = new ArrayList<>();

        mItemAdapter = new FriendItemAdapter(context, mFriendList, userAndMessages);
        mItemAdapter.setOnRecycleViewItemClick((view, position) -> {
            if (mOnRecycleViewItemClick != null) {
                mOnRecycleViewItemClick.onRecycleViewItemClick(view, position);
            }
        });
        View view = LayoutInflater.from(context).inflate(R.layout.friend_list_pop_window_layout, null, false);
        mFriendsRecyclerView = view.findViewById(R.id.friend_list_recycler_view);
        mFriendsSearch = view.findViewById(R.id.friend_list_search);
        mRefreshLayout = view.findViewById(R.id.friend_list_refresh_layout);
        mReturnImageView = view.findViewById(R.id.friend_list_pop_window_return);
        mReturnImageView.setOnClickListener(v -> {
            mPopupWindow.dismiss();
        });

        // 获取屏幕宽高，以防止pop window 充满真个个屏幕
        Point point = new Point();
        context.getWindowManager().getDefaultDisplay().getSize(point);
        mPopupWindow = new PopupWindow(view, point.x - 100, point.y - 100);
        mPopupWindow.setFocusable(true);//设置pw中的控件能够获取焦点
        mPopupWindow.setOutsideTouchable(true); //设置可以通过点击mPopupWindow外部关闭mPopupWindow
        mPopupWindow.update();//刷新mPopupWindow
        mPopupWindow.showAtLocation(context.getWindow().getDecorView(), Gravity.CENTER, 0, 0);

//        mFriendAlertDialog = new AlertDialog.Builder(context)
//                .setView(view)
//                .setTitle("好友列表")
//                .setNegativeButton("取消", null)
//                .setPositiveButton("确认", null)
//                .show();
        initView();


        new Thread(() -> {
            List<User> friendList = FriendLab.getFriends(mCurrentUserID);
            mFriendList.clear();
            mFriendList.addAll(friendList);
            mHandler.post(() -> {

                // 先将在线的好友放到前方
                List<String> onlineUser = mUDPClient.onlineUser();

                if (onlineUser.size() >= 0) {
                    int indexCount = 0;
                    for (int i = 0; i < mFriendList.size(); i++) {
                        if (onlineUser.contains(mFriendList.get(i).getName())) {
                            mFriendList.get(i).setOnlineStatus(1);
                            Collections.swap(mFriendList, indexCount, i);
                            indexCount++;
                        }
                    }
                }

                if (mUserAndMessages == null) {
                    FriendLab.sFriendList = mFriendList;
                    mItemAdapter.notifyDataSetChanged();
                    return;
                }

                // 再将发送信息的好友，放到列表的前边
                for (String uams : mUserAndMessages) {
                    String[] uam = uams.split(":");
                    Optional<User> optionalUser = mFriendList.stream().filter(user -> user.getName().equals(uam[0])).findFirst();
                    if (optionalUser.isPresent())
                        optionalUser.get().setOnlineStatus(2);
                }

                int indexCount = 0;
                for (int i = 0; i < mFriendList.size(); i++) {
                    if (mFriendList.get(i).getOnlineStatus() == 2) {
                        Collections.swap(mFriendList, indexCount, i);
                        indexCount++;
                    }
                }

                FriendLab.sFriendList = mFriendList;
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
                mHandler.post(() -> {
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

    public PopupWindow getFriendPopupWindow() {
        return mPopupWindow;
    }

    public void setOnRecycleViewItemClick(FriendItemAdapter.OnRecycleViewItemClick onRecycleViewItemClick) {
        mOnRecycleViewItemClick = onRecycleViewItemClick;
    }

    public List<User> getFriendList() {
        return mFriendList;
    }
}