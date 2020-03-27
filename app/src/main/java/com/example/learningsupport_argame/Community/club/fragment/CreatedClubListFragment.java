package com.example.learningsupport_argame.Community.club.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.learningsupport_argame.Community.club.Club;
import com.example.learningsupport_argame.Community.club.ClubLab;
import com.example.learningsupport_argame.Community.club.ClubListAdapter;
import com.example.learningsupport_argame.Community.club.activity.ClubSettingActivity;
import com.example.learningsupport_argame.R;

import java.util.ArrayList;
import java.util.List;

public class CreatedClubListFragment extends Fragment {
    private Activity mActivity;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ClubListAdapter mClubListAdapter;
    private List<Club> mClubList = new ArrayList<>();


    public static CreatedClubListFragment newInstance() {
        return new CreatedClubListFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = getActivity();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        new Thread(() -> {
            List<Club> clubs = ClubLab.getCreatedClubList();
            mClubList.clear();
            mClubList.addAll(clubs);
            mActivity.runOnUiThread(() -> {
                mClubListAdapter.notifyDataSetChanged();
            });
        }).start();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.club_my_created_fragment, container, false);
        mClubListAdapter = new ClubListAdapter(mClubList, getContext());
        mClubListAdapter.setOnSettingClickListener((club, v) -> {
            Intent intent = new Intent(getContext(), ClubSettingActivity.class);
            Club.sCurrentClub = club;
            startActivity(intent);
        });
        mRecyclerView = view.findViewById(R.id.club_list_recycler_view);
        mSwipeRefreshLayout = view.findViewById(R.id.club_list_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(() -> {
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

}
