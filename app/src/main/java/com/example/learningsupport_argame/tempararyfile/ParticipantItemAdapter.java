package com.example.learningsupport_argame.tempararyfile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.learningsupport_argame.R;
import com.example.learningsupport_argame.UserManagement.User;

import java.util.List;

public class ParticipantItemAdapter extends RecyclerView.Adapter<ParticipantItemAdapter.ParticipantItemViewHolder> {
    private List<User> mUserList;
    private Context mContext;

    public ParticipantItemAdapter(List<User> users, Context context) {
        mUserList = users;
        mContext = context;
    }

    @NonNull
    @Override
    public ParticipantItemAdapter.ParticipantItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.task_participant_item, parent, false);
        return new ParticipantItemAdapter.ParticipantItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ParticipantItemAdapter.ParticipantItemViewHolder holder, int position) {
        holder.bind(mUserList.get(position));
    }

    @Override
    public int getItemCount() {
        return mUserList.size();
    }

    class ParticipantItemViewHolder extends RecyclerView.ViewHolder {
        private TextView participantName;
        private TextView participantLevel;

        public ParticipantItemViewHolder(@NonNull View itemView) {
            super(itemView);
            participantName = itemView.findViewById(R.id.task_item_participant_name);
            participantLevel = itemView.findViewById(R.id.task_item_participant_level);
        }

        public void bind(User user) {
            participantName.setText(user.getName());
            participantLevel.setText("Lv." + user.getLevel());
        }
    }
}