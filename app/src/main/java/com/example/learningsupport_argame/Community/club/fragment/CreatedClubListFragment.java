package com.example.learningsupport_argame.Community.club.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.learningsupport_argame.Community.club.Club;
import com.example.learningsupport_argame.Community.club.ClubLab;
import com.example.learningsupport_argame.Community.club.ClubListAdapter;
import com.example.learningsupport_argame.Community.club.activity.ClubListActivity;
import com.example.learningsupport_argame.Community.club.activity.ClubSettingActivity;
import com.example.learningsupport_argame.Community.friend.FriendItemAdapter;
import com.example.learningsupport_argame.R;
import com.example.learningsupport_argame.UserManagement.UserLab;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CreatedClubListFragment extends Fragment {
    private String TAG = "CreatedClubListFragment";
    private Activity mActivity;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ClubListAdapter mClubListAdapter;
    private List<Club> mClubList = new ArrayList<>();
    private List<Club> mSearchList = new ArrayList<>();
    private ImageButton mSearchButton;
    private EditText mSearchBox;
    private boolean isSearchList;

    public static CreatedClubListFragment newInstance() {
        return new CreatedClubListFragment();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mActivity = getActivity();
        mSearchButton = ((ClubListActivity) mActivity).getSearchButton();
        mSearchBox = ((ClubListActivity) mActivity).getSearchBox();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
        initSearchLayout();
        if (!isSearchList) {
            if (mClubList.size() <= 0) {
                new Thread(() -> {
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

    private void initSearchLayout() {
        mSearchBox.setHint("我创建的社团");
        mSearchBox.setText("");
        mSearchButton.setBackgroundResource(R.drawable.sousuo);
        mSearchButton.setOnClickListener(v -> {
            if (isSearchList) {
                isSearchList = false;
                mSearchButton.setBackgroundResource(R.drawable.sousuo);
                mClubListAdapter = new ClubListAdapter(mClubList, mActivity);
                mRecyclerView.setAdapter(mClubListAdapter);
            } else {
                String name = mSearchBox.getText().toString();
                isSearchList = true;
                mSearchList = mClubList.stream()
                        .filter(club -> club.getClubName().contains(name))
                        .collect(Collectors.toList());
                Log.d(TAG, "onClick: " + mSearchList.size());
                mClubListAdapter = new ClubListAdapter(mSearchList, mActivity);
                mRecyclerView.setAdapter(mClubListAdapter);
                mSearchButton.setBackgroundResource(R.drawable.friend_list_pop_window_cross);
            }
        });
    }
}
