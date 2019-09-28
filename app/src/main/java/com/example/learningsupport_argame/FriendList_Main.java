package com.example.learningsupport_argame;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.google.android.material.tabs.TabLayout;

import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class FriendList_Main extends AppCompatActivity {

    private TabLayout mTabLayout;
    private Fragment[] mFragmensts;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friendlist_navigation_layout);
        mFragmensts = DataGenerator.getFragments("TabLayout Tab");
        // 初始化导航栏信息
        new NavigationController(this, getWindow().getDecorView());
        initView();

    }

    private void initView() {
        mTabLayout = (TabLayout) findViewById(R.id.bottom_tab_layout);

        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                onTabItemSelected(tab.getPosition());
                // Tab 选中之后，改变各个Tab的状态
                for (int i = 0; i < mTabLayout.getTabCount(); i++) {
                    View view = mTabLayout.getTabAt(i).getCustomView();
                    ImageView icon = (ImageView) view.findViewById(R.id.tab_content_image);
                    TextView text = (TextView) view.findViewById(R.id.tab_content_text);
                    if (i == tab.getPosition()) { // 选中状态
                        icon.setImageResource(DataGenerator.mTabResPressed[i]);

                        text.setText(DataGenerator.mTabTitle[i]);
                        text.setTextColor(getResources().getColor(android.R.color.black));
                    } else {// 未选中状态
                        icon.setImageResource(DataGenerator.mTabRes[i]);
                        text.setText(DataGenerator.mTabTitle[i]);
                        text.setTextColor(getResources().getColor(android.R.color.darker_gray));
                    }
                }
            }


            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        // 提供自定义的布局添加Tab
        for (int i = 0; i < 4; i++) {
            mTabLayout.addTab(mTabLayout.newTab().setText(DataGenerator.mTabTitle[i]).setCustomView(DataGenerator.getTabView(this, i)));
        }
//        for(int i=0;i<4;i++){
//            mTabLayout.getTabAt(i).setText(DataGenerator.mTabTitle[i]);
//        }
//        for(int i=0;i<4;i++){
//            mTabLayout.getTabAt(i).setCustomView(DataGenerator.getTabView(this,i));
//        }


    }

    private void onTabItemSelected(int position) {
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = mFragmensts[0];
                break;
            case 1:
                fragment = mFragmensts[1];
                break;

            case 2:
                fragment = mFragmensts[2];
                break;
            case 3:
                fragment = mFragmensts[3];
                break;
        }
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.home_container, fragment).commit();
        }
    }
}
