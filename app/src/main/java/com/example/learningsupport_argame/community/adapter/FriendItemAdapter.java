package com.example.learningsupport_argame.community.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.learningsupport_argame.R;
import com.example.learningsupport_argame.UserManagement.User;
import com.example.learningsupport_argame.UserManagement.UserLab;
import com.example.learningsupport_argame.UserManagement.UserMessage.FriendMessageActivity;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


public class FriendItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static String TAG = "FriendItemAdapter";
    private Context mContext;
    private List<User> mFriendList;
    private String[] mUserAndMessages;

    public static final int ITEM_NORMAL = 1;
    public static final int ITEM_EMPTY = 0;

    private OnRecycleViewItemClick mOnRecycleViewItemClick;


    public FriendItemAdapter(Context context, List<User> friendList, String[] userAndMessage) {
        mFriendList = friendList;

        mContext = context;
        mUserAndMessages = userAndMessage;
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
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof FriendItemViewHolder) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnRecycleViewItemClick != null) {
                        mOnRecycleViewItemClick.onRecycleViewItemClick(v, position);
                    }
                }
            });

            ((FriendItemViewHolder) holder).bind(mFriendList.get(position));
            if (mUserAndMessages == null)
                return;

            for (String uams : mUserAndMessages) {
                String[] uam = uams.split(":");
                if (uam[0].equals(mFriendList.get(position).getName())) {
                    holder.itemView.findViewById(R.id.friend_list_item_avatar_red_point).setVisibility(View.VISIBLE);
                    TextView message = holder.itemView.findViewById(R.id.friend_item_last_message);
                    message.setText(uam[2]);
                }

            }

        }

    }

    @Override
    public int getItemViewType(int position) {
        // 如果没有数据，使用空布局的布局
        if (mFriendList.size() == 0) {
            return ITEM_EMPTY;
        }
        //如果有数据，则使用ITEM的布局
        return ITEM_NORMAL;
    }

    @Override
    public int getItemCount() {
        // size()为0的，只引入一个空布局,此时recyclerView的itemCount为1
        if (mFriendList.size() == 0) {
            return 1;
        }
        return mFriendList.size();
    }

    public void setOnRecycleViewItemClick(OnRecycleViewItemClick onRecycleViewItemClick) {
        mOnRecycleViewItemClick = onRecycleViewItemClick;
    }

    public class FriendItemViewHolder extends RecyclerView.ViewHolder {
        private ImageView friendAvatar;
        private TextView friendName;
        private TextView friendLevel;
        private TextView friendMajor;
        private TextView friendLastMessage;
        private ImageView friendInfo;

        public FriendItemViewHolder(View itemView) {
            super(itemView);
            friendAvatar = itemView.findViewById(R.id.friend_list_item_avatar);
            friendName = itemView.findViewById(R.id.friend_list_item_name);
            friendLevel = itemView.findViewById(R.id.friend_list_item_level);
            friendMajor = itemView.findViewById(R.id.friend_list_item_major);
            friendLastMessage = itemView.findViewById(R.id.friend_item_last_message);
            friendInfo = itemView.findViewById(R.id.friend_item_friend_info);
        }

        public void bind(User user) {
            friendAvatar.setImageBitmap(user.getAvatar());
            friendName.setText(user.getName());
            friendLevel.setText("Lv." + user.getLevel());
            // friendMajor.setText(user.getName());
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
}
