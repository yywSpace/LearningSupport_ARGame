package com.example.learningsupport_argame.Community.club;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.learningsupport_argame.R;
import com.example.learningsupport_argame.UserManagement.UserLab;

import java.util.List;

public class ClubListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Club> mClubList;
    private Context mContext;
    private View.OnLongClickListener mOnLongClickListener;
    private View.OnClickListener mOnClickListener;
    private OnSettingClickListener mOnSettingClickListener;

    private static final int ITEM_NORMAL = 1;
    private static final int ITEM_EMPTY = 0;

    public ClubListAdapter(List<Club> clubList, Context context) {
        mClubList = clubList;
        mContext = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_EMPTY) {
            View emptyView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_empty_item, parent, false);
            return new RecyclerView.ViewHolder(emptyView) {
            };
        }
        return new ClubListViewHolder(LayoutInflater
                .from(mContext)
                .inflate(R.layout.club_list_recycler_view_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ClubListViewHolder)
            ((ClubListViewHolder) holder).bind(mClubList.get(position));
    }

    @Override
    public int getItemViewType(int position) {
        if (mClubList.size() <= 0)
            return ITEM_EMPTY;
        return ITEM_NORMAL;
    }

    @Override
    public int getItemCount() {
        if (mClubList.size() <= 0)
            return 1;
        return mClubList.size();
    }

    public void setOnLongClickListener(View.OnLongClickListener onLongClickListener) {
        mOnLongClickListener = onLongClickListener;
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        mOnClickListener = onClickListener;
    }


    class ClubListViewHolder extends RecyclerView.ViewHolder {
        private ImageView mClubImage;
        private ImageView mClubSetting;
        private ImageView mClubManagerImage;
        private TextView mClubName;
        private TextView mClubDesc;
        private TextView mClubType;

        private TextView mClubMemberNum;

        ClubListViewHolder(@NonNull View itemView) {
            super(itemView);
            if (mOnLongClickListener != null)
                itemView.setOnLongClickListener(mOnLongClickListener);
            if (mOnClickListener != null)
                itemView.setOnClickListener(mOnClickListener);
            mClubSetting = itemView.findViewById(R.id.club_setting);

            mClubManagerImage = itemView.findViewById(R.id.club_manager_symbol);
            mClubImage = itemView.findViewById(R.id.club_image);
            mClubType = itemView.findViewById(R.id.club_type);
            mClubName = itemView.findViewById(R.id.club_name);
            mClubDesc = itemView.findViewById(R.id.club_desc);
            mClubMemberNum = itemView.findViewById(R.id.club_member_number);
        }

        void bind(Club club) {
            if (club.getCoverBitmap() != null) {
                mClubImage.setImageBitmap(club.getCoverBitmap());
            } else {
                Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.img_avatar_04);
                mClubImage.setImageBitmap(bitmap);
            }
            if (mOnSettingClickListener != null)
                mClubSetting.setOnClickListener(v ->
                        mOnSettingClickListener.onSettingClickListener(club, v));
            if (club.getManagerId() == UserLab.getCurrentUser().getId()) {
                mClubManagerImage.setVisibility(View.VISIBLE);
            } else
                mClubManagerImage.setVisibility(View.INVISIBLE);
            mClubType.setText(club.getClubType());
            mClubName.setText(club.getClubName());
            mClubDesc.setText(club.getClubDesc());
            mClubMemberNum.setText(club.getCurrentMemberNum() + "/" + club.getClubMaxMember());
        }
    }

    public interface OnSettingClickListener {
        void onSettingClickListener(Club club, View v);
    }

    public void setOnSettingClickListener(OnSettingClickListener onSettingClickListener) {
        mOnSettingClickListener = onSettingClickListener;
    }
}
