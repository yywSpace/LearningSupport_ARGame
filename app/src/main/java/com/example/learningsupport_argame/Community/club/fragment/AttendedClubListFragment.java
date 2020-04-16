package com.example.learningsupport_argame.Community.club.fragment;

import android.app.AlertDialog;
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
import com.example.learningsupport_argame.Community.club.activity.ClubSettingActivity;
import com.example.learningsupport_argame.MainActivity;
import com.example.learningsupport_argame.R;
import com.example.learningsupport_argame.UserManagement.User;
import com.example.learningsupport_argame.UserManagement.UserLab;

import java.util.List;

public class AttendedClubListFragment extends BasicClubListFragment {
    private String TAG = "AttendedClubListFragment";


    public static AttendedClubListFragment newInstance() {
        return new AttendedClubListFragment();
    }


    @Override
    public void onResume() {
        super.onResume();
        //initSearchLayout();
        if (!isSearchList) {
            if (ClubLab.sParticipateClubList != null) {
                mClubList.clear();
                mClubList.addAll(ClubLab.sParticipateClubList);
                mClubListAdapter.notifyDataSetChanged();
                return;
            }
            if (mClubList.size() <= 0)
                new Thread(() -> {
                    while (UserLab.getCurrentUser() == null)
                        ;
                    List<Club> clubs = ClubLab.getParticipateClubList();
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
        mClubListAdapter.setOnClubClickListener((v, club) -> {
            new AlertDialog.Builder(mActivity)
                    .setTitle("进入社团[" + club.getClubName() + "]")
                    .setMessage("是否要进入社团?")
                    .setPositiveButton("确认", (dialog, which) -> {
                        User user = UserLab.getCurrentUser();
                        Intent intent = new Intent(mActivity, MainActivity.class);
                        intent.putExtra("scene", "club");
                        // message:clubName,userName,modName
                        intent.putExtra("scene_args", String.format("%s,%s,%s", club.getClubName(), user.getName(), user.getModName()));
                        startActivity(intent);
                        mActivity.finish();
                    })
                    .setNegativeButton("取消",null)
                    .show();

        });
        mClubListAdapter.setOnSettingClickListener((club, v) -> {
            Club.sCurrentClub = club;
            if (club.getManagerId() == UserLab.getCurrentUser().getId()) {
                Intent intent = new Intent(getContext(), ClubSettingActivity.class);
                startActivity(intent);
                return;
            }
            Intent intent = new Intent(getContext(), ClubInfoActivity.class);
            intent.putExtra("status", "attended");
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
