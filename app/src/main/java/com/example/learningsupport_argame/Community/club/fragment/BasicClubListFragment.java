package com.example.learningsupport_argame.Community.club.fragment;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.learningsupport_argame.Community.club.Club;
import com.example.learningsupport_argame.Community.club.ClubListAdapter;

import java.util.ArrayList;
import java.util.List;

public class BasicClubListFragment extends Fragment {
    protected AppCompatActivity mActivity;
    protected RecyclerView mRecyclerView;
    protected SwipeRefreshLayout mSwipeRefreshLayout;
    ClubListAdapter mClubListAdapter;
    List<Club> mClubList = new ArrayList<>();
    List<Club> mSearchList = new ArrayList<>();
    boolean isSearchList;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mActivity = (AppCompatActivity) getActivity();
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
