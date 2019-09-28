package com.example.learningsupport_argame;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.drawerlayout.widget.DrawerLayout;

import com.example.learningsupport_argame.FeedbackModel.FeedbackDetailsActivity;
import com.google.android.material.navigation.NavigationView;


public class NavigationController {
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageButton navigationButton;
    private String TAG = "NavigationController";

    public NavigationController(Context context, View view) {
        drawerLayout = view.findViewById(R.id.navigation_drawer);
        navigationView = view.findViewById(R.id.navigation_nav);
        navigationButton = view.findViewById(R.id.navigation_button);
        View headerView = navigationView.getHeaderView(0);//获取头布局
        navigationButton.setOnClickListener(v -> {
            if (drawerLayout.isDrawerOpen(navigationView)) {
                drawerLayout.closeDrawer(navigationView);
            } else {
                drawerLayout.openDrawer(navigationView);
            }
        });

        navigationView.setNavigationItemSelectedListener(item -> {
            Toast.makeText(context, item.getTitle().toString(), Toast.LENGTH_SHORT).show();
            if (item.getTitle().toString().equals("我的好友"))
                context.startActivity(new Intent(context, FriendList_Main.class));
            else if (item.getTitle().toString().equals("我的任务"))
                context.startActivity(new Intent(context, TaskList_Main.class));
            else if (item.getTitle().toString().equals("我的课程"))
                context.startActivity(new Intent(context, FriendList_Main.class));
            else if (item.getTitle().toString().equals("学习反馈"))
                context.startActivity(new Intent(context, FeedbackDetailsActivity.class));
            else if (item.getTitle().toString().equals("VR模式"))
                context.startActivity(new Intent(context, FriendList_Main.class));
            else if (item.getTitle().toString().equals("广场与社团"))
                context.startActivity(new Intent(context, FriendList_Main.class));
            drawerLayout.closeDrawer(navigationView);
            return true;
        });
    }
}
