package com.example.learningsupport_argame.Community.club.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.learningsupport_argame.Community.club.Club;
import com.example.learningsupport_argame.Community.club.activity.ClubInfoActivity;
import com.example.learningsupport_argame.Community.club.ClubLab;
import com.example.learningsupport_argame.Community.club.ClubListAdapter;
import com.example.learningsupport_argame.R;

import java.util.List;

public class AllClubListFragment extends BasicClubListFragment {
    private String TAG = "AllClubListFragment";

    public static AllClubListFragment newInstance() {
        return new AllClubListFragment();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isSearchList) {
            if (ClubLab.sOtherClubList != null) {
                mClubList.clear();
                mClubList.addAll(ClubLab.sOtherClubList);
                mClubListAdapter.notifyDataSetChanged();
            }
            if (mClubList.size() <= 0)
                new Thread(() -> {
                    List<Club> clubs = ClubLab.getOtherClubs();
                    mClubList.clear();
                    mClubList.addAll(clubs);
                    mActivity.runOnUiThread(() -> {
                        mClubListAdapter.notifyDataSetChanged();
                    });
                }).start();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.club_my_created_fragment, container, false);
        mClubListAdapter = new ClubListAdapter(mClubList, getContext());
        mClubListAdapter.setOnSettingClickListener((club, v) -> {
            Intent intent = new Intent(getContext(), ClubInfoActivity.class);
            intent.putExtra("status", "not attend");
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
                List<Club> clubs = ClubLab.getOtherClubs();
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
}
