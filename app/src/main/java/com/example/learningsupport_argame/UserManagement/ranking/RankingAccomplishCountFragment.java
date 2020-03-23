package com.example.learningsupport_argame.UserManagement.ranking;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.learningsupport_argame.R;
import com.example.learningsupport_argame.Task.adapter.TaskItemAdapter;
import com.example.learningsupport_argame.UserManagement.User;
import com.example.learningsupport_argame.UserManagement.UserLab;

import java.util.ArrayList;
import java.util.List;

import static java.lang.String.*;

public class RankingAccomplishCountFragment extends Fragment {
    private static AppCompatActivity sActivity;
    public static boolean isLoadDataFinish = true;
    private RecyclerView mRankingRecyclerView;
    private RankingAccomplishCountAdapter mRankingAccomplishCountAdapter;
    private List<User> mUserList = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isLoadDataFinish) {
            isLoadDataFinish = false;
            new Thread(() -> {
                mUserList.clear();
                if (UserLab.sUserListWithAccomplishCount.size() <= 0)
                    UserLab.getUsersWithAccomplishCount();
                mUserList.addAll(UserLab.sUserListWithAccomplishCount);
                sActivity.runOnUiThread(() -> {
                    mRankingAccomplishCountAdapter.notifyDataSetChanged();
                    isLoadDataFinish = true;
                });
            }).start();
        }

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.ranking_basic_recycler_view_layout, null, false);
        mRankingRecyclerView = view.findViewById(R.id.ranking_basic_recycler_view);
        mRankingAccomplishCountAdapter = new RankingAccomplishCountAdapter(mUserList);
        mRankingRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRankingRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        mRankingRecyclerView.setAdapter(mRankingAccomplishCountAdapter);
        return view;
    }

    public static RankingAccomplishCountFragment getInstance(AppCompatActivity activity) {
        RankingAccomplishCountFragment fragment = new RankingAccomplishCountFragment();
        sActivity = activity;
        return fragment;
    }

    class RankingAccomplishCountAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private List<User> mUserList;
        static final int ITEM_NORMAL = 1;
        static final int ITEM_EMPTY = 0;

        RankingAccomplishCountAdapter(List<User> userList) {
            mUserList = userList;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType == ITEM_EMPTY) {
                View emptyView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_loading_item, parent, false);
                return new RecyclerView.ViewHolder(emptyView) {
                };
            }
            return new RankingAccomplishCountViewHolder(LayoutInflater.from(getContext())
                    .inflate(R.layout.ranking_level_normal_item, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof RankingAccomplishCountViewHolder) {
                ((RankingAccomplishCountViewHolder) holder).bind(mUserList.get(position), position);
            }
        }

        @Override
        public int getItemCount() {
            if (mUserList.size() == 0) {
                return 1;
            }
            return mUserList.size();
        }

        @Override
        public int getItemViewType(int position) {
            // 如果没有数据，使用空布局的布局
            if (mUserList.size() == 0) {
                return ITEM_EMPTY;
            }
            //如果有数据，则使用ITEM的布局
            return ITEM_NORMAL;
        }
    }

    static class RankingAccomplishCountViewHolder extends RecyclerView.ViewHolder {
        ImageView userAvatar;
        TextView userAccomplishCount;
        TextView userName;
        TextView rankingNum;

        RankingAccomplishCountViewHolder(@NonNull View itemView) {
            super(itemView);
            userAvatar = itemView.findViewById(R.id.ranking_user_avatar);
            userAccomplishCount = itemView.findViewById(R.id.ranking_user_level);
            userName = itemView.findViewById(R.id.ranking_user_name);
            rankingNum = itemView.findViewById(R.id.ranking_number);
        }

        void bind(User user, int position) {
            userAvatar.setImageBitmap(user.getAvatar());
            userAccomplishCount.setText(user.getAccomplishCount() + "个");
            userName.setText(user.getName());
            rankingNum.setText(valueOf(position + 1));
        }
    }
}
