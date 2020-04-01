package com.example.learningsupport_argame.Community.friend;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.learningsupport_argame.Client.ClientLab;
import com.example.learningsupport_argame.MainActivity;
import com.example.learningsupport_argame.R;
import com.example.learningsupport_argame.TestActivity;
import com.example.learningsupport_argame.UserManagement.User;
import com.example.learningsupport_argame.UserManagement.UserLab;
import com.example.learningsupport_argame.UserManagement.UserMessage.FriendMessageActivity;

import java.util.List;
import java.util.Optional;


public class FriendItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static String TAG = "FriendItemAdapter";
    private Context mContext;
    private List<User> mFriendList;
    private List<User> mSearchList;
    private List<User> mOperationList;

    private static final int ITEM_NORMAL = 1;
    private static final int ITEM_EMPTY = 0;

    private OnRecycleViewItemClick mOnRecycleViewItemClick;

    private OnRecycleViewItemLongClick mOnRecycleViewItemLongClick;

    public FriendItemAdapter(Context context, List<User> friendList, List<User> searchList) {
        mFriendList = friendList;
        mSearchList = searchList;
        // 说明此时是搜索列表
        if (searchList == null)
            mOperationList = mFriendList;
        else
            mOperationList = searchList;
        mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //在这里根据不同的viewType进行引入不同的布局
        if (viewType == ITEM_EMPTY) {
            View emptyView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_empty_item, parent, false);
            return new RecyclerView.ViewHolder(emptyView) {
            };
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.friend_list_item, parent, false);
        return new FriendItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof FriendItemViewHolder) {
            User user = mOperationList.get(position);

            holder.itemView.setOnClickListener(v -> {
                if (user.getOnlineStatus() == 0) {
                    Toast.makeText(mContext, "当前用户不在线，请稍后重试", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(mContext, MainActivity.class);
                    intent.putExtra("scene", "chat_room");
                    //message:otherName,userName,modName
                    intent.putExtra("scene_args",
                            user.getName() + "," + UserLab.getCurrentUser().getName() + "," + "modeName");
                    Log.d(TAG, "onBindViewHolder: "+user.getName() + "," + UserLab.getCurrentUser().getName() + "," + "modeName");
                    mContext.startActivity(intent);
                    Toast.makeText(mContext, user.getName() + "," + UserLab.getCurrentUser().getName() + "," + "modeName", Toast.LENGTH_SHORT).show();
                }
            });
            holder.itemView.setOnLongClickListener(v -> {
                PopupMenu popupMenu = new PopupMenu(mContext, holder.itemView);
                popupMenu.inflate(R.menu.friend_list_item_menu);
                popupMenu.setOnMenuItemClickListener(item -> {
                    switch (item.getItemId()) {
                        case R.id.menu_friend_info:
                            Intent intent = new Intent(mContext, FriendMessageActivity.class);
                            intent.putExtra(User.CURRENT_USER_ID, user.getId() + "");
                            mContext.startActivity(intent);
                            break;
                        case R.id.menu_friend_delete:
                            new AlertDialog.Builder(mContext)
                                    .setTitle("删除好友")
                                    .setMessage("确定要删除好友" + user.getName() + "吗?")
                                    .setPositiveButton("确认", (dialog, which) -> {
                                        new Thread(() -> UserLab.deleteFriend(user.getId())).start();
                                        mOperationList.remove(user);
                                        notifyDataSetChanged();
                                    })
                                    .setNegativeButton("取消", null)
                                    .show();
                            break;
                    }
                    return false;
                });
                popupMenu.show();
                return false;
            });

            ((FriendItemViewHolder) holder).bind(user);

            if (user.getOnlineStatus() == 1) {
                // 在线
                holder.itemView
                        .findViewById(R.id.friend_list_item_avatar_red_point)
                        .setBackgroundResource(R.drawable.friend_list_item_green_point);
                TextView message = holder.itemView.findViewById(R.id.friend_item_last_message);
                message.setText("在线");
            } else if (user.getOnlineStatus() == 0) {
                // 不在线
                holder.itemView
                        .findViewById(R.id.friend_list_item_avatar_red_point)
                        .setBackgroundResource(R.drawable.friend_list_item_gray_point);
                TextView message = holder.itemView.findViewById(R.id.friend_item_last_message);
                message.setText("不在线");
            } else {
                // 接收到消息
                holder.itemView
                        .findViewById(R.id.friend_list_item_avatar_red_point)
                        .setBackgroundResource(R.drawable.friend_list_item_red_point);
                TextView message = holder.itemView.findViewById(R.id.friend_item_last_message);
                message.setText(ClientLab.getClient().getMessageMap().get(user.getName()));
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        // 如果没有数据，使用空布局的布局
        if (mOperationList.size() == 0) {
            return ITEM_EMPTY;
        }
        //如果有数据，则使用ITEM的布局
        return ITEM_NORMAL;
    }

    @Override
    public int getItemCount() {
        // size()为0的，只引入一个空布局,此时recyclerView的itemCount为1
        if (mOperationList.size() == 0) {
            return 1;
        }
        return mOperationList.size();
    }

    public void setOnRecycleViewItemClick(OnRecycleViewItemClick onRecycleViewItemClick) {
        mOnRecycleViewItemClick = onRecycleViewItemClick;
    }

    public void setOnRecycleViewItemLongClick(OnRecycleViewItemLongClick onRecycleViewItemLongClick) {
        mOnRecycleViewItemLongClick = onRecycleViewItemLongClick;
    }

    public class FriendItemViewHolder extends RecyclerView.ViewHolder {
        private ImageView friendAvatar;
        private ImageView strangerIcon;
        private TextView friendName;
        private TextView friendLevel;
        private TextView friendMajor;
        private TextView friendLastMessage;
        private ImageView friendInfo;

        public FriendItemViewHolder(View itemView) {
            super(itemView);
            strangerIcon = itemView.findViewById(R.id.friend_list_item_stranger);
            friendAvatar = itemView.findViewById(R.id.friend_list_item_avatar);
            friendName = itemView.findViewById(R.id.friend_list_item_name);
            friendLevel = itemView.findViewById(R.id.friend_list_item_level);
            friendMajor = itemView.findViewById(R.id.friend_list_item_major);
            friendLastMessage = itemView.findViewById(R.id.friend_item_last_message);
            friendInfo = itemView.findViewById(R.id.friend_item_friend_info);
        }

        public void bind(User user) {
            if (user.getAvatar() != null)
                friendAvatar.setImageBitmap(user.getAvatar());
            else {
                Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.img_avatar_03);
                friendAvatar.setImageBitmap(bitmap);
            }
            // 如果当前是搜索列表，则判断此人是否为陌生人
            if (mSearchList != null) {
                Optional<User> userOptional = mFriendList
                        .stream()
                        .filter(u -> u.getId() == user.getId()).findFirst();
                //不在好友列表中，为陌生人
                if (!userOptional.isPresent())
                    strangerIcon.setVisibility(View.VISIBLE);
                else strangerIcon.setVisibility(View.INVISIBLE);
            }

            friendName.setText(user.getName());
            friendLevel.setText("Lv." + user.getLevel());
            friendMajor.setText(user.getLabel());
            friendLastMessage.setText("消息来了");
            friendInfo.setOnClickListener((view) -> {
                Intent intent = new Intent(mContext, FriendMessageActivity.class);
                intent.putExtra(User.CURRENT_USER_ID, user.getId() + "");
                mContext.startActivity(intent);
            });
        }
    }

    public interface OnRecycleViewItemClick {
        void onRecycleViewItemClick(View view, int position);
    }

    public interface OnRecycleViewItemLongClick {
        void onRecycleViewItemLongClick(View view, int position);
    }
}
