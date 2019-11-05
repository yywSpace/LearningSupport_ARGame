package com.example.learningsupport_argame.community.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.learningsupport_argame.R;
import com.example.learningsupport_argame.UserManagement.User;
import com.example.learningsupport_argame.UserManagement.UserMessage.FriendMessageActivity;

import java.util.List;


public class FriendItemAdapter extends RecyclerView.Adapter<FriendItemAdapter.FriendItemViewHolder> {
    private static String TAG = "FriendItemAdapter";
    private Context mContext;
    private List<User> mFriendList;

    public FriendItemAdapter(Context context, List<User> friendList) {
        mFriendList = friendList;
        mContext = context;
    }

    @Override
    public FriendItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.friend_list_item, parent, false);
        return new FriendItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FriendItemViewHolder holder, int position) {
        holder.bind(mFriendList.get(position));
    }

    @Override
    public int getItemCount() {
        return mFriendList.size();
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
            Log.d(TAG, "bind: " + user.getId());
            friendAvatar.setImageBitmap(user.getAvatar());
            friendName.setText(user.getName());
            friendLevel.setText("Lv." + user.getLevel());
            // friendMajor.setText(user.getName());
            friendLastMessage.setText("消息来了");
            friendInfo.setOnClickListener((view) -> {
                Intent intent = new Intent(mContext, FriendMessageActivity.class);
                intent.putExtra(User.CURRENT_USER_ID, user.getId()+"");
                mContext.startActivity(intent);
            });
        }
    }

}
