package com.example.learningsupport_argame.Community.club.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.learningsupport_argame.Community.club.activity.ClubCreateActivity;
import com.example.learningsupport_argame.NavigationActivity;
import com.example.learningsupport_argame.R;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ClubListFragment extends Fragment {
    private AppCompatActivity mActivity;
    private String TAG = "ClubListActivity";
    private Toolbar mToolbar;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private List<BasicClubListFragment> mFragmentList;
    private List<String> mTitleList;
    private List<Integer> mIconList;
    private SearchView mSearchView;
    private MenuItem mSearchMenuItem;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mActivity = (AppCompatActivity) getActivity();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(mActivity).inflate(R.layout.club_list_activity_layout, container, false);
        mToolbar = view.findViewById(R.id.club_list_toolbar);
        mActivity.setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(v -> ((NavigationActivity) mActivity).openNavigation());
        mToolbar.setTitle("我创建的社团");
        mFragmentList = new ArrayList<>(Arrays.asList(
                CreatedClubListFragment.newInstance(),
                AttendedClubListFragment.newInstance(),
                AllClubListFragment.newInstance()
        ));
        mTitleList = new ArrayList<>(Arrays.asList("我创建的", "我参加的", "其他社团"));
        mIconList = new ArrayList<>(Arrays.asList(R.drawable.club_created_icon, R.drawable.club_attended_icon, R.drawable.club_other_icon));
        mViewPager = view.findViewById(R.id.club_view_pager);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                Log.d(TAG, "onPageSelected: " + position);
                switch (position) {
                    case 0:
                        mToolbar.setTitle("我创建的社团");
                        initFragmentSearchListener(0);
                        break;
                    case 1:
                        mToolbar.setTitle("我参加的社团");
                        initFragmentSearchListener(1);
                        break;
                    case 2:
                        mToolbar.setTitle("其他社团");
                        initFragmentSearchListener(2);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mViewPager.setAdapter(new FragmentStatePagerAdapter(getChildFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
            @NonNull
            @Override
            public Fragment getItem(int position) {
                return mFragmentList.get(position);
            }

            @Override
            public int getCount() {
                return mFragmentList.size();
            }

            @Nullable
            @Override
            public CharSequence getPageTitle(int position) {
                return mTitleList.get(position);
            }
        });
        mTabLayout = view.findViewById(R.id.club_tab_layout);
        mTabLayout.setupWithViewPager(mViewPager);
        //设置自定义tab
        for (int i = 0; i < mTabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = mTabLayout.getTabAt(i);
            if (tab != null) {
                View v = LayoutInflater.from(mActivity).inflate(R.layout.club_foot_navigation_item, null, false);
                ImageView icon = v.findViewById(R.id.item_icon);
                TextView title = v.findViewById(R.id.item_title);
                if (i == 0)
                    title.setTextColor(Color.BLACK);
                else title.setTextColor(Color.GRAY);

                icon.setBackgroundResource(mIconList.get(i));
                title.setText(mTitleList.get(i));
                tab.setCustomView(v);
            }
        }
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                View view = tab.getCustomView();
                TextView textView = view.findViewById(R.id.item_title);
                textView.setTextColor(Color.BLACK);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                View view = tab.getCustomView();
                TextView textView = view.findViewById(R.id.item_title);
                textView.setTextColor(Color.GRAY);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.club_list_menu, menu);
        mSearchMenuItem = menu.findItem(R.id.club_search);
        mSearchView = (SearchView) mSearchMenuItem.getActionView();
        mSearchView.setSubmitButtonEnabled(true);
        initFragmentSearchListener(0);
        mToolbar.setTitle("我创建的社团");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.club_add) {
            startActivity(new Intent(mActivity, ClubCreateActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void initFragmentSearchListener(int position) {
        BasicClubListFragment fragment = mFragmentList.get(position);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d(TAG, "onQueryTextSubmit: ");
                String name = mSearchView.getQuery().toString();
                fragment.setInSearchList(true);
                fragment.setSearchList(fragment.getClubList().stream()
                        .filter(club -> club.getClubName().contains(name))
                        .collect(Collectors.toList()));
                fragment.initAdapter(fragment.getSearchList());
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        mSearchMenuItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                Log.d(TAG, "onMenuItemActionCollapse: ");
                fragment.setInSearchList(false);
                fragment.initAdapter(fragment.getClubList());
                return true;
            }
        });
    }
}
