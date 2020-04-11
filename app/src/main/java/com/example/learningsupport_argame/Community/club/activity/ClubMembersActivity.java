package com.example.learningsupport_argame.Community.club.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.learningsupport_argame.Community.club.ClubLab;
import com.example.learningsupport_argame.R;
import com.example.learningsupport_argame.UserManagement.ActivityUtil;
import com.example.learningsupport_argame.UserManagement.User;
import com.example.learningsupport_argame.UserManagement.UserLab;
import com.example.learningsupport_argame.UserManagement.UserMessage.FriendMessageActivity;
import com.example.learningsupport_argame.UserManagement.UserMessage.UserMessageActivity;

import java.util.ArrayList;
import java.util.List;

public class ClubMembersActivity extends Activity {
    private static final String TAG = "ClubMembersActivity";
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private FrameLayout mReturnLayout;
    private RecyclerView mRecyclerView;
    private MemberListAdapter mMemberListAdapter;
    private int mClubId;
    private int mManagerId;
    private List<User> mMemberList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.club_member_list_layout);
        ActivityUtil.addActivity(this);

        mSwipeRefreshLayout = findViewById(R.id.members_swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            new Thread(() -> {
                List<User> users = ClubLab.getClubMemberList(mClubId);
                for (int i = 0; i < users.size(); i++) {
                    User user = users.get(i);
                    if (user.getId() == mManagerId) {
                        users.remove(user);
                        users.add(0, user);
                        break;
                    }
                }
                mMemberList.clear();
                mMemberList.addAll(users);
                runOnUiThread(() -> {
                    mMemberListAdapter.notifyDataSetChanged();
                    mSwipeRefreshLayout.setRefreshing(false);
                });
            }).start();
        });
        mReturnLayout = findViewById(R.id.club_members_list_return);
        mReturnLayout.setOnClickListener(v -> finish());
        mClubId = getIntent().getIntExtra("club_id", -1);
        mManagerId = getIntent().getIntExtra("manager_id", -1);
        if (mClubId == -1 || mManagerId == -1) {
            Toast.makeText(this, "运行出错请稍候重试", Toast.LENGTH_SHORT).show();
            finish();
        }
        mMemberListAdapter = new MemberListAdapter(mMemberList);
        mRecyclerView = findViewById(R.id.club_member_list_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mMemberListAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        new Thread(() -> {
            List<User> users = ClubLab.getClubMemberList(mClubId);
            for (int i = 0; i < users.size(); i++) {
                User user = users.get(i);
                if (user.getId() == mManagerId) {
                    users.remove(user);
                    users.add(0, user);
                }
            }
            Log.d(TAG, "onResume: " + users.size());
            mMemberList.clear();
            mMemberList.addAll(users);
            runOnUiThread(() -> {
                mMemberListAdapter.notifyDataSetChanged();
            });
        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityUtil.removeActivity(this);
    }

    class MemberListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private static final int ITEM_NORMAL = 1;
        private static final int ITEM_EMPTY = 0;
        private static final int ITEM_MANAGER = 2;
        private List<User> mMemberList;

        MemberListAdapter(List<User> memberList) {
            mMemberList = memberList;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            Log.d(TAG, "onCreateViewHolder: " + viewType);

            if (viewType == ITEM_EMPTY) {
                View emptyView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_loading_item, parent, false);
                return new RecyclerView.ViewHolder(emptyView) {
                };
            } else if (viewType == ITEM_NORMAL) {
                View view = LayoutInflater.from(ClubMembersActivity.this).inflate(R.layout.club_member_normal_item, parent, false);
                return new NormalMemberViewHolder(view);
            } else {
                View view = LayoutInflater.from(ClubMembersActivity.this).inflate(R.layout.club_member_manager_item, parent, false);
                return new ManagerMemberViewHolder(view);
            }
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof NormalMemberViewHolder) {
                ((NormalMemberViewHolder) holder).bind(mMemberList.get(position));
            }
            if (holder instanceof ManagerMemberViewHolder) {
                ((ManagerMemberViewHolder) holder).bind(mMemberList.get(position));
            }
        }

        @Override
        public int getItemCount() {
            if (mMemberList.size() <= 0)
                return 1;
            return mMemberList.size();
        }

        @Override
        public int getItemViewType(int position) {
            if (mMemberList.size() <= 0)
                return ITEM_EMPTY;
            if (mMemberList.get(position).getId() == mManagerId)
                return ITEM_MANAGER;
            else
                return ITEM_NORMAL;
        }
    }

    class NormalMemberViewHolder extends RecyclerView.ViewHolder {
        private ImageView avatar;
        private TextView name;
        private TextView level;
        private Button operationButton;

        NormalMemberViewHolder(@NonNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.member_list_item_avatar);
            name = itemView.findViewById(R.id.member_list_item_name);
            level = itemView.findViewById(R.id.member_list_item_level);
            operationButton = itemView.findViewById(R.id.member_list_item_operation);
        }

        void bind(User user) {
            itemView.setOnClickListener(v -> {
                if (user.getId() == UserLab.getCurrentUser().getId()) {
                    Intent intent = new Intent(ClubMembersActivity.this, UserMessageActivity.class);
                    ClubMembersActivity.this.startActivity(intent);
                } else {
                    Intent intent = new Intent(ClubMembersActivity.this, FriendMessageActivity.class);
                    intent.putExtra(User.CURRENT_USER_ID, user.getId() + "");
                    ClubMembersActivity.this.startActivity(intent);
                }
            });
            if (UserLab.getCurrentUser().getId() == mManagerId) {
                operationButton.setOnClickListener(v -> {
                    new AlertDialog.Builder(ClubMembersActivity.this)
                            .setTitle("删除成员")
                            .setMessage("确定要删除成员:" + user.getName() + " 吗？")
                            .setPositiveButton("确定", (dialog, which) -> {
                                mMemberList.remove(user);
                                mMemberListAdapter.notifyDataSetChanged();
                                new Thread(() -> ClubLab.deleteClubMember(mClubId, user.getId())).start();
                                Toast.makeText(ClubMembersActivity.this, "删除", Toast.LENGTH_SHORT).show();
                            })
                            .setNegativeButton("取消", null)
                            .show();
                });
            } else {
                operationButton.setVisibility(View.INVISIBLE);
            }
            if (user.getAvatar() != null)
                avatar.setImageBitmap(user.getAvatar());
            else {
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.img_avatar_06);
                avatar.setImageBitmap(bitmap);
            }
            name.setText(user.getName());
            level.setText("Lv." + user.getLevel());
        }
    }

    class ManagerMemberViewHolder extends RecyclerView.ViewHolder {
        private ImageView avatar;
        private TextView name;
        private TextView level;

        public ManagerMemberViewHolder(@NonNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.member_list_item_avatar);
            name = itemView.findViewById(R.id.member_list_item_name);
            level = itemView.findViewById(R.id.member_list_item_level);
        }

        void bind(User user) {
            itemView.setOnClickListener(v -> {
                if (user.getId() == UserLab.getCurrentUser().getId()) {
                    Intent intent = new Intent(ClubMembersActivity.this, UserMessageActivity.class);
                    ClubMembersActivity.this.startActivity(intent);
                } else {
                    Intent intent = new Intent(ClubMembersActivity.this, FriendMessageActivity.class);
                    intent.putExtra(User.CURRENT_USER_ID, user.getId() + "");
                    ClubMembersActivity.this.startActivity(intent);
                }
            });
            if (user.getAvatar() != null)
                avatar.setImageBitmap(user.getAvatar());
            else {
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.img_avatar_06);
                avatar.setImageBitmap(bitmap);
            }
            name.setText(user.getName());
            level.setText("Lv." + user.getLevel());
        }
    }
}