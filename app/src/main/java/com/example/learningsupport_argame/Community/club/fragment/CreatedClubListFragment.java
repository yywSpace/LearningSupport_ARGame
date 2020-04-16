package com.example.learningsupport_argame.Community.club.fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.learningsupport_argame.Community.club.Club;
import com.example.learningsupport_argame.Community.club.ClubLab;
import com.example.learningsupport_argame.Community.club.ClubListAdapter;
import com.example.learningsupport_argame.Community.club.activity.ClubSettingActivity;
import com.example.learningsupport_argame.MainActivity;
import com.example.learningsupport_argame.R;
import com.example.learningsupport_argame.UserManagement.User;
import com.example.learningsupport_argame.UserManagement.UserLab;

import java.util.List;

public class CreatedClubListFragment extends BasicClubListFragment {
    private String TAG = "CreatedClubListFragment";

    public static CreatedClubListFragment newInstance() {
        return new CreatedClubListFragment();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isSearchList) {
            if (ClubLab.sCreatedClubList != null) {
                mClubList.clear();
                mClubList.addAll(ClubLab.sCreatedClubList);
                mClubListAdapter.notifyDataSetChanged();
                return;
            }
            if (mClubList.size() <= 0) {
                new Thread(() -> {
                    while (UserLab.getCurrentUser() == null)
                        ;
                    List<Club> clubs = ClubLab.getCreatedClubList();
                    mClubList.clear();
                    mClubList.addAll(clubs);
                    mActivity.runOnUiThread(() -> {
                        mClubListAdapter.notifyDataSetChanged();
                    });
                }).start();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.club_my_created_fragment, container, false);
        mClubListAdapter = new ClubListAdapter(mClubList, getContext());
        mClubListAdapter.setOnClubClickListener((v, club) -> {
            new AlertDialog.Builder(mActivity)
                    .setTitle("进入社团[" + club.getClubName() + "]")
                    .setMessage("是否要进入社团?")
                    .setPositiveButton("确认", (dialog, which) -> {
                        User user = UserLab.getCurrentUser();
                        Intent intent = new Intent(mActivity, MainActivity.class);
                        intent.putExtra("scene", "club");
                        intent.putExtra("scene_args", String.format("%s,%s,%s", club.getClubName(), user.getName(), user.getModName()));
                        startActivity(intent);
                        mActivity.finish();
                    })
                    .setNegativeButton("取消", null)
                    .show();
        });
        mClubListAdapter.setOnSettingClickListener((club, v) -> {
            Intent intent = new Intent(getContext(), ClubSettingActivity.class);
            Club.sCurrentClub = club;
            startActivity(intent);
        });

        mRecyclerView = view.findViewById(R.id.club_list_recycler_view);
        mSwipeRefreshLayout = view.findViewById(R.id.club_list_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            if (isSearchList) {
                mSwipeRefreshLayout.setRefreshing(false);
                return;
            }
            new Thread(() -> {
                List<Club> clubs = ClubLab.getCreatedClubList();
                mClubList.clear();
                mClubList.addAll(clubs);
                mActivity.runOnUiThread(() -> {
                    mClubListAdapter.notifyDataSetChanged();
                    mSwipeRefreshLayout.setRefreshing(false);
                });
            }).start();
        });
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mClubListAdapter);
        return view;
    }

    public List<Club> getClubList() {
        return mClubList;
    }

    public void setSearchList(List<Club> searchList) {
        mSearchList = searchList;
    }

    public List<Club> getSearchList() {
        return mSearchList;
    }

    public void setInSearchList(boolean isSearchList) {
        this.isSearchList = isSearchList;
    }

    public void initAdapter(List<Club> clubList) {
        mClubListAdapter = new ClubListAdapter(clubList, mActivity);
        mRecyclerView.setAdapter(mClubListAdapter);
    }
}
