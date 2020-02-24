package com.example.learningsupport_argame;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;


import com.example.learningsupport_argame.Course.CourseMainActivity;
import com.example.learningsupport_argame.FeedbackModel.FeedbackDetailsActivity;
import com.example.learningsupport_argame.Navi.Activity.MapActivity;
import com.example.learningsupport_argame.Task.activity.TaskListActivity;
import com.example.learningsupport_argame.UserManagement.User;
import com.example.learningsupport_argame.UserManagement.UserLab;
import com.example.learningsupport_argame.UserManagement.UserMessage.UserMessageActivity;
import com.example.learningsupport_argame.Community.activity.FriendListDialog;
import com.google.android.material.navigation.NavigationView;


public class NavigationController {
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageButton navigationButton;
    private String TAG = "NavigationController";
    private NavigationItem mNavigationItem;
    public NavigationController(AppCompatActivity context, View view,NavigationItem navigationItem) {
        mNavigationItem = navigationItem;
        new Thread(() -> {
            while (UserLab.getCurrentUser() == null) ;
            context.runOnUiThread(() -> {
                initView(view, context);
            });
        }).start();
    }

    void initView(View view, AppCompatActivity context) {
        drawerLayout = view.findViewById(R.id.navigation_drawer);
        navigationView = view.findViewById(R.id.navigation_nav);
        navigationButton = view.findViewById(R.id.navigation_button);
        View headerView = navigationView.getHeaderView(0);//获取头布局
        User user = UserLab.getCurrentUser();
        ImageView avatar = headerView.findViewById(R.id.user_avatar);
        avatar.setImageBitmap(user.getAvatar());
        TextView name = headerView.findViewById(R.id.user_name);
        name.setText(user.getName());
        TextView level = headerView.findViewById(R.id.user_level);
        level.setText("Lv." + user.getLevel());

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
            if (item.getItemId() == R.id.navigation_menu_friend && mNavigationItem != NavigationItem.FRIEND) {
                FriendListDialog fld = new FriendListDialog(context, UserLab.getCurrentUser().getId() + "", null);
//                context.startActivity(new Intent(context, FriendListActivity.class));
            }
            if (item.getItemId() == R.id.navigation_menu_task && mNavigationItem != NavigationItem.TASK)
                context.startActivity(new Intent(context, TaskListActivity.class));
            if (item.getItemId() == R.id.navigation_menu_course && mNavigationItem != NavigationItem.COURSE)
                context.startActivity(new Intent(context, CourseMainActivity.class));
            if (item.getItemId() == R.id.navigation_menu_feedback && mNavigationItem != NavigationItem.FEEDBACK)
                context.startActivity(new Intent(context, FeedbackDetailsActivity.class));
            if (item.getItemId() == R.id.navigation_menu_map && mNavigationItem != NavigationItem.MAP)
                context.startActivity(new Intent(context, MapActivity.class));
//            if (item.getItemId() == R.id.navigation_menu_put)
//                context.startActivity(new Intent(context, ModelPutActivity.class));
//            if (item.getItemId() == R.id.navigation_menu_social)
//                context.startActivity(new Intent(context, ModelPutActivity.class));
            drawerLayout.closeDrawer(navigationView);
            return true;
        });
    }

    public enum NavigationItem {
        FRIEND,
        TASK,
        COURSE,
        FEEDBACK,
        MAP
    }
}
