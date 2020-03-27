package com.example.learningsupport_argame.Community.club.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.learningsupport_argame.Community.club.Club;
import com.example.learningsupport_argame.Community.club.activity.ClubInfoActivity;
import com.example.learningsupport_argame.Community.club.ClubLab;
import com.example.learningsupport_argame.Community.club.ClubListAdapter;
import com.example.learningsupport_argame.Community.club.activity.ClubSettingActivity;
import com.example.learningsupport_argame.R;
import com.example.learningsupport_argame.UserManagement.UserLab;

import java.util.ArrayList;
import java.util.List;

public class AttendedClubListFragment extends Fragment {
    private Activity mActivity;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ClubListAdapter mClubListAdapter;
    private List<Club> mClubList = new ArrayList<>();


    public static AttendedClubListFragment newInstance() {
        return new AttendedClubListFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = getActivity();
    }

    @Override
    public void onResume() {
        super.onResume();
        new Thread(() -> {
            List<Club> clubs = ClubLab.getParticipateClubList();
            mClubList.clear();
            mClubList.addAll(clubs);
            mActivity.runOnUiThread(() -> {
                mClubListAdapter.notifyDataSetChanged();
            });
        }).start();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.club_my_created_fragment, container, false);
        mClubListAdapter = new ClubListAdapter(mClubList, getContext());
        mClubListAdapter.setOnSettingClickListener((club, v) -> {
            Club.sCurrentClub = club;
            if (club.getManagerId() == UserLab.getCurrentUser().getId()) {
                Intent intent = new Intent(getContext(), ClubSettingActivity.class);
                startActivity(intent);
                return;
            }
            Intent intent = new Intent(getContext(), ClubInfoActivity.class);
            intent.putExtra("status","attended");
            startActivity(intent);
        });
        mRecyclerView = view.findViewById(R.id.club_list_recycler_view);
        mSwipeRefreshLayout = view.findViewById(R.id.club_list_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            new Thread(() -> {
                List<Club> clubs = ClubLab.getParticipateClubList();
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
