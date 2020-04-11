package com.example.learningsupport_argame.Community.club.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Looper;
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

import com.example.learningsupport_argame.Community.club.Club;
import com.example.learningsupport_argame.Community.club.ClubInfoItem;
import com.example.learningsupport_argame.Community.club.ClubLab;
import com.example.learningsupport_argame.R;
import com.example.learningsupport_argame.UserManagement.ActivityUtil;
import com.example.learningsupport_argame.UserManagement.User;
import com.example.learningsupport_argame.UserManagement.UserLab;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ClubInfoActivity extends Activity {
    private FrameLayout mReturnLayout;
    private ClubInfoAdapter mClubInfoAdapter;
    private RecyclerView mRecyclerView;
    private List<ClubInfoItem> mClubInfoItems;
    private Button mAttendButton;
    private Club mClub;
    private String mClubStatus;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.club_attend_actvity_layout);
        ActivityUtil.addActivity(this);
        mClub = Club.sCurrentClub;
        mClubStatus = getIntent().getStringExtra("status");
        mClubInfoItems = new ArrayList<>(Arrays.asList(
                new ClubInfoItem(mClub, ClubInfoItem.ClubInfoItemType.AVATAR),
                new ClubInfoItem(mClub, ClubInfoItem.ClubInfoItemType.MANAGER),
                new ClubInfoItem(mClub, ClubInfoItem.ClubInfoItemType.LABEL),
                new ClubInfoItem(mClub, ClubInfoItem.ClubInfoItemType.DESC),
                new ClubInfoItem(mClub, "查看群成员", ClubInfoItem.ClubInfoItemType.VIEW_MEMBER)
        ));
        mClubInfoAdapter = new ClubInfoAdapter(mClubInfoItems);
        mAttendButton = findViewById(R.id.club_attend_button);
        if (!mClubStatus.equals("attended")) {
            mAttendButton.setOnClickListener(v -> {
                new AlertDialog.Builder(this)
                        .setTitle("加入社团")
                        .setMessage("是否要加入社团-" + mClub.getClubName())
                        .setPositiveButton("确定", (dialog, which) ->
                                new Thread(() -> {
                                    ClubLab.attendClub(mClub);
                                    Looper.prepare();
                                    Toast.makeText(this, "加入社团成功", Toast.LENGTH_SHORT).show();
                                    Looper.loop();
                                }).start())
                        .setNegativeButton("取消", null)
                        .show();
            });
        } else {
            mAttendButton.setText("退出社团");
            mAttendButton.setOnClickListener(v ->
                    new AlertDialog.Builder(this)
                            .setTitle("退出社团")
                            .setMessage("是否要退出社团-" + mClub.getClubName())
                            .setPositiveButton("确定", (dialog, which) ->
                                    new Thread(() -> {
                                        ClubLab.quitClub(mClub);
                                        Looper.prepare();
                                        finish();
                                        Toast.makeText(this, "退出社团成功", Toast.LENGTH_SHORT).show();
                                        Looper.loop();
                                    }).start())
                            .setNegativeButton("取消", null)
                            .show());
        }

        mReturnLayout = findViewById(R.id.club_attend_return);
        mReturnLayout.setOnClickListener(v -> finish());
        mRecyclerView = findViewById(R.id.club_detail_info);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mClubInfoAdapter);
        mRecyclerView.addItemDecoration(new MyItemDecoration());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityUtil.removeActivity(this);
    }

    class ClubInfoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private List<ClubInfoItem> mClubInfoItems;

        public ClubInfoAdapter(List<ClubInfoItem> clubInfoItems) {
            mClubInfoItems = clubInfoItems;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType == ClubInfoItem.ClubInfoItemType.AVATAR.ordinal()) {
                View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.club_info_avatar_item_layout, parent, false);
                return new AvatarViewHolder(view);
            }
            if (viewType == ClubInfoItem.ClubInfoItemType.DESC.ordinal()) {
                View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.club_info_desc_item_layout, parent, false);
                return new ContentViewHolder(view);
            }
            if (viewType == ClubInfoItem.ClubInfoItemType.LABEL.ordinal()) {
                View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.club_info_content_item_layout, parent, false);
                return new ContentViewHolder(view);
            }
            if (viewType == ClubInfoItem.ClubInfoItemType.ATTEND.ordinal()) {
                View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.club_info_avatar_item_layout, parent, false);
                return new AvatarViewHolder(view);
            }
            if (viewType == ClubInfoItem.ClubInfoItemType.VIEW_MEMBER.ordinal()) {
                View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.club_info_view_member_layout, parent, false);
                return new ImageContentViewHolder(view);
            }
            if (viewType == ClubInfoItem.ClubInfoItemType.MANAGER.ordinal()) {
                View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.club_info_content_item_layout, parent, false);
                return new ContentViewHolder(view);
            }
            return null;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof AvatarViewHolder) {
                AvatarViewHolder avatarViewHolder = (AvatarViewHolder) holder;
                avatarViewHolder.bind(mClubInfoItems.get(position));
            }
            if (holder instanceof ContentViewHolder) {
                ContentViewHolder contentViewHolder = (ContentViewHolder) holder;
                contentViewHolder.bind(mClubInfoItems.get(position));
            }
            if (holder instanceof ImageContentViewHolder) {
                ImageContentViewHolder imageContentViewHolder = (ImageContentViewHolder) holder;
                imageContentViewHolder.bind(mClubInfoItems.get(position));
            }
        }

        @Override
        public int getItemCount() {
            return mClubInfoItems.size();
        }

        @Override
        public int getItemViewType(int position) {
            return mClubInfoItems.get(position).getClubInfoItemType().ordinal();
        }
    }

    static class MyItemDecoration extends RecyclerView.ItemDecoration {

        /**
         * @param outRect 边界
         * @param view    recyclerView ItemView
         * @param parent  recyclerView
         * @param state   recycler 内部数据管理
         */
        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            // 设置特定Item底边距
            outRect.bottom = 10;
        }
    }

    class AvatarViewHolder extends RecyclerView.ViewHolder {
        private ImageView avatar;
        private TextView clubMember;
        private TextView clubName;

        AvatarViewHolder(@NonNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.club_image);
            clubMember = itemView.findViewById(R.id.club_member_number);
            clubName = itemView.findViewById(R.id.club_name);
        }

        public void bind(ClubInfoItem infoItem) {
            Club club = infoItem.getClub();
            if (club.getCoverBitmap() != null)
                avatar.setImageBitmap(club.getCoverBitmap());
            else {
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.img_avatar_04);
                avatar.setImageBitmap(bitmap);
            }
            clubMember.setText(club.getCurrentMemberNum() + "/" + club.getClubMaxMember());
            clubName.setText(club.getClubName());
        }
    }

    class ImageContentViewHolder extends RecyclerView.ViewHolder {
        TextView label;

        ImageContentViewHolder(@NonNull View itemView) {
            super(itemView);
            label = itemView.findViewById(R.id.club_info_content_label);

        }

        void bind(ClubInfoItem infoItem) {
            Club club = infoItem.getClub();
            label.setText(infoItem.getClubContent());
            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(ClubInfoActivity.this, ClubMembersActivity.class);
                intent.putExtra("club_id", club.getId());
                intent.putExtra("manager_id", club.getManagerId());
                startActivity(intent);
            });
        }
    }

    class ContentViewHolder extends RecyclerView.ViewHolder {
        TextView label;
        TextView content;

        ContentViewHolder(@NonNull View itemView) {
            super(itemView);
            label = itemView.findViewById(R.id.club_info_content_label);
            content = itemView.findViewById(R.id.club_info_content_detail);
        }

        public void bind(ClubInfoItem infoItem) {
            Club club = infoItem.getClub();
            if (infoItem.getClubInfoItemType().equals(ClubInfoItem.ClubInfoItemType.DESC)) {
                label.setText("社团描述");
                content.setText(club.getClubDesc());
            }
            if (infoItem.getClubInfoItemType().equals(ClubInfoItem.ClubInfoItemType.LABEL)) {
                label.setText("社团标签");
                content.setText(club.getClubType());
            }
            if (infoItem.getClubInfoItemType().equals(ClubInfoItem.ClubInfoItemType.MANAGER)) {
                label.setText("管理员");
                new Thread(() -> {
                    User user = UserLab.getSampleUser(club.getManagerId());
                    runOnUiThread(()-> content.setText(user.getName()));
                }).start();
            }
        }
    }

}
