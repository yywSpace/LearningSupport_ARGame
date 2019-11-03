package com.example.learningsupport_argame;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.drawerlayout.widget.DrawerLayout;


import com.example.learningsupport_argame.Course.CourseMainActivity;
import com.example.learningsupport_argame.FeedbackModel.FeedbackDetailsActivity;
import com.example.learningsupport_argame.Navi.Activity.MapActivity;
import com.example.learningsupport_argame.UserManagement.UserMessage.UserMessageActivity;
import com.example.learningsupport_argame.community.activity.FriendList_modified;
import com.example.learningsupport_argame.task.activity.TaskListActivity;
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
        headerView.setOnClickListener(v -> {
            Intent intent = new Intent(context, UserMessageActivity.class);
            context.startActivity(intent);
        });
        navigationButton.setOnClickListener(v -> {
            if (drawerLayout.isDrawerOpen(navigationView)) {
                drawerLayout.closeDrawer(navigationView);
            } else {
                drawerLayout.openDrawer(navigationView);
            }
        });

        navigationView.setNavigationItemSelectedListener(item -> {
            Toast.makeText(context, item.getTitle().toString(), Toast.LENGTH_SHORT).show();
            if (item.getItemId() == R.id.navigation_menu_friend)
                context.startActivity(new Intent(context, FriendList_modified.class));
            if (item.getItemId() == R.id.navigation_menu_task)
                context.startActivity(new Intent(context, TaskListActivity.class));
            if (item.getItemId() == R.id.navigation_menu_course)
                context.startActivity(new Intent(context, CourseMainActivity.class));
            if (item.getItemId() == R.id.navigation_menu_feedback)
                context.startActivity(new Intent(context, FeedbackDetailsActivity.class));
            if (item.getItemId() == R.id.navigation_menu_map)
                context.startActivity(new Intent(context, MapActivity.class));
//            if (item.getItemId() == R.id.navigation_menu_put)
//                context.startActivity(new Intent(context, ModelPutActivity.class));
//            if (item.getItemId() == R.id.navigation_menu_social)
//                context.startActivity(new Intent(context, ModelPutActivity.class));
            drawerLayout.closeDrawer(navigationView);
            return true;
        });
    }
}
