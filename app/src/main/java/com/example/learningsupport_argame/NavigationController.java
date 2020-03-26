package com.example.learningsupport_argame;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;


import com.example.learningsupport_argame.Community.friend.FriendListActivity;
import com.example.learningsupport_argame.Course.CourseMainActivity;
import com.example.learningsupport_argame.FeedbackModel.FeedbackDetailsActivity;
import com.example.learningsupport_argame.Navi.Activity.MapActivity;
import com.example.learningsupport_argame.UserManagement.achievement.AchievementActivity;
import com.example.learningsupport_argame.UserManagement.bag.UserBagActivity;
import com.example.learningsupport_argame.UserManagement.ranking.RankingActivity;
import com.example.learningsupport_argame.Task.activity.TaskListActivity;
import com.example.learningsupport_argame.UserManagement.User;
import com.example.learningsupport_argame.UserManagement.UserLab;
import com.example.learningsupport_argame.UserManagement.UserMessage.UserMessageActivity;
import com.example.learningsupport_argame.UserManagement.shop.ShopActivity;
import com.google.android.material.navigation.NavigationView;

public class NavigationController {
    private boolean hasInitView;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageButton navigationButton;
    private View headerView;
    private String TAG = "NavigationController";
    private NavigationItem mNavigationItem;

    public NavigationController(AppCompatActivity context, View view, NavigationItem navigationItem) {
        mNavigationItem = navigationItem;
        new Thread(() -> {
            while (UserLab.getCurrentUser() == null) ;
            context.runOnUiThread(() -> {
                hasInitView = true;
                initView(view, context);
            });
        }).start();
    }

    @SuppressLint("SetTextI18n")
    private void initView(View view, AppCompatActivity context) {
        drawerLayout = view.findViewById(R.id.navigation_drawer);
        navigationView = view.findViewById(R.id.navigation_nav);
        navigationButton = view.findViewById(R.id.navigation_button);
        headerView = navigationView.getHeaderView(0);//获取头布局
        RelativeLayout userMessageLayout = headerView.findViewById(R.id.navigation_user_message);
        userMessageLayout.setOnClickListener(v -> {
            Intent intent = new Intent(context, UserMessageActivity.class);
            context.startActivity(intent);
        });
        // 刷新任务信息
        refresh();

        // 排行，成就
        ImageButton rankingImageButton = headerView.findViewById(R.id.navigation_ranking);
        rankingImageButton.setOnClickListener(v -> {
            Toast.makeText(context, "排行", Toast.LENGTH_SHORT).show();
            context.startActivity(new Intent(context, RankingActivity.class));
        });
        ImageButton honourImageButton = headerView.findViewById(R.id.navigation_honour);
        honourImageButton.setOnClickListener(v -> {
            Toast.makeText(context, "成就", Toast.LENGTH_SHORT).show();
            context.startActivity(new Intent(context, AchievementActivity.class));
        });
        ImageButton shopImageButton = headerView.findViewById(R.id.navigation_shop);
        shopImageButton.setOnClickListener(v -> {
            Toast.makeText(context, "商店", Toast.LENGTH_SHORT).show();
            context.startActivity(new Intent(context, ShopActivity.class));
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
//                FriendListDialog fld = new FriendListDialog(context, UserLab.getCurrentUser().getId() + "", null);
                Intent intent = new Intent(context, FriendListActivity.class);
                intent.putExtra(User.CURRENT_USER_ID, String.valueOf(UserLab.getCurrentUser().getId()));
                context.startActivity(intent);
            }
            if (item.getItemId() == R.id.navigation_menu_bag && mNavigationItem != NavigationItem.BAG) {
                context.startActivity(new Intent(context, UserBagActivity.class));
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

    // 刷新可能改变的数据
    public void refresh() {
        if (!hasInitView)
            return;

        User user = UserLab.getCurrentUser();
        Log.d(TAG, "refresh: " + user);
        if (user == null)
            return;
        int maxHp = User.BASIC_HP + user.getLevel();
        int hp = user.getHp();
        int maxExp = User.BASIC_EXP + user.getLevel() * 500;
        int exp = user.getExp();
        int level = user.getLevel();

        // 用户信息
        TextView gold = headerView.findViewById(R.id.user_gold);
        gold.setText("金币：" + user.getGold());
        ImageView avatar = headerView.findViewById(R.id.user_avatar);
        avatar.setImageBitmap(user.getAvatar());
        ProgressBar hpProcessBar = headerView.findViewById(R.id.user_hp);
        hpProcessBar.setMax(maxHp);
        hpProcessBar.setProgress(hp);
        ProgressBar expProcessBar = headerView.findViewById(R.id.user_exp);
        expProcessBar.setProgress(exp);
        expProcessBar.setMax(maxExp);
        TextView name = headerView.findViewById(R.id.user_name);
        name.setText(user.getName());
        TextView levelTextView = headerView.findViewById(R.id.user_level);
        levelTextView.setText("Lv." + level);
    }

    public enum NavigationItem {
        FRIEND,
        TASK,
        COURSE,
        FEEDBACK,
        MAP,
        BAG
    }
}
