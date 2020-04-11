package com.example.learningsupport_argame;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.learningsupport_argame.Community.club.fragment.ClubListFragment;
import com.example.learningsupport_argame.Community.friend.FriendListActivity;
import com.example.learningsupport_argame.Course.CourseMainActivity;
import com.example.learningsupport_argame.Course.CourseMainFragment;
import com.example.learningsupport_argame.FeedbackModel.FeedbackDetailsActivity;
import com.example.learningsupport_argame.Navi.Activity.MapActivity;
import com.example.learningsupport_argame.Task.fragment.TaskListFragment;
import com.example.learningsupport_argame.UserManagement.ActivityUtil;
import com.example.learningsupport_argame.UserManagement.Login.LoginActivity;
import com.example.learningsupport_argame.UserManagement.User;
import com.example.learningsupport_argame.UserManagement.UserLab;
import com.example.learningsupport_argame.UserManagement.UserMessage.UserMessageActivity;
import com.example.learningsupport_argame.UserManagement.achievement.AchievementActivity;
import com.example.learningsupport_argame.UserManagement.bag.UserBagActivity;
import com.example.learningsupport_argame.UserManagement.ranking.RankingActivity;
import com.example.learningsupport_argame.UserManagement.shop.ShopActivity;
import com.google.android.material.navigation.NavigationView;

public class NavigationActivity extends AppCompatActivity {
    private String TAG = "NavigationActivity";
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private View mHeaderView;
    private boolean hasInitView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigation_layout);
        ActivityUtil.addActivity(this);
        initView();
        // 刷新任务信息
        refresh();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container, TaskListFragment.getInstance(1))
                .commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (UserLab.getCurrentUser() == null) {
            SharedPreferences userInfo = getSharedPreferences(LoginActivity.PREFS_NAME, MODE_PRIVATE);
            int id = userInfo.getInt("id", 0);
            Log.d(TAG, "onResume: " + id);
            new Thread(() -> {
                UserLab.setCurrentUser(UserLab.getUserById(id + ""));
                runOnUiThread(this::refresh);
            }).start();
        }
        refresh();
    }

    private void initView() {
        mDrawerLayout = findViewById(R.id.navigation_drawer);
        mNavigationView = findViewById(R.id.navigation_nav);
        mHeaderView = mNavigationView.getHeaderView(0);//获取头布局
        RelativeLayout userMessageLayout = mHeaderView.findViewById(R.id.navigation_user_message);
        userMessageLayout.setOnClickListener(v -> {
            Intent intent = new Intent(this, UserMessageActivity.class);
            startActivity(intent);
        });
        // 排行，成就
        ImageButton rankingImageButton = mHeaderView.findViewById(R.id.navigation_ranking);
        rankingImageButton.setOnClickListener(v -> {
            Toast.makeText(this, "排行", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, RankingActivity.class));
        });
        ImageButton honourImageButton = mHeaderView.findViewById(R.id.navigation_honour);
        honourImageButton.setOnClickListener(v -> {
            Toast.makeText(this, "成就", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, AchievementActivity.class));
        });
        ImageButton shopImageButton = mHeaderView.findViewById(R.id.navigation_shop);
        shopImageButton.setOnClickListener(v -> {
            Toast.makeText(this, "商店", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, ShopActivity.class));
        });

        mNavigationView.setNavigationItemSelectedListener(item -> {
            Toast.makeText(this, item.getTitle().toString(), Toast.LENGTH_SHORT).show();
            if (item.getItemId() == R.id.navigation_menu_friend) {
                Intent intent = new Intent(this, FriendListActivity.class);
                intent.putExtra(FriendListActivity.IN_CHAT_ROOM_LABEL, false);
                intent.putExtra(User.CURRENT_USER_ID, String.valueOf(UserLab.getCurrentUser().getId()));
                startActivity(intent);
            }
            if (item.getItemId() == R.id.navigation_menu_club) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new ClubListFragment())
                        .commit();
            }
            if (item.getItemId() == R.id.navigation_menu_bag) {
                startActivity(new Intent(this, UserBagActivity.class));
            }
            if (item.getItemId() == R.id.navigation_menu_task) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, TaskListFragment.getInstance(1))
                        .commit();
            }
            if (item.getItemId() == R.id.navigation_menu_course) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new CourseMainFragment())
                        .commit();
            }
            if (item.getItemId() == R.id.navigation_menu_feedback)
                startActivity(new Intent(this, FeedbackDetailsActivity.class));
            if (item.getItemId() == R.id.navigation_menu_map)
                startActivity(new Intent(this, MapActivity.class));
            if (item.getItemId() == R.id.navigation_menu_square) {
                User user = UserLab.getCurrentUser();
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("scene", "square");
                // message:userName,modName,squareName
                intent.putExtra("scene_args", String.format("%s,%s,square", user.getName(), user.getModName()));
                startActivity(intent);
            }
            if (item.getItemId() == R.id.navigation_menu_test) {
                startActivity(new Intent(this, TestActivity.class));
            }
            mDrawerLayout.closeDrawer(mNavigationView);
            return true;
        });
        hasInitView = true;
    }

    // 刷新可能改变的数据
    public void refresh() {
        if (!hasInitView)
            return;

        User user = UserLab.getCurrentUser();
        if (user == null)
            return;
        int maxHp = User.BASIC_HP + user.getLevel();
        int hp = user.getHp();
        int maxExp = User.BASIC_EXP + user.getLevel() * 500;
        int exp = user.getExp();
        int level = user.getLevel();

        // 用户信息
        TextView gold = mHeaderView.findViewById(R.id.user_gold);
        gold.setText("金币：" + user.getGold());
        CircleImageView avatar = mHeaderView.findViewById(R.id.user_avatar);
        if (user.getAvatar() == null)
            avatar.setImageDrawable(getResources().getDrawable(R.drawable.img_avatar_02, getTheme()));
        else {
            Drawable drawable = new BitmapDrawable(getResources(), user.getAvatar());
            avatar.setImageDrawable(drawable);
        }
        ProgressBar hpProcessBar = mHeaderView.findViewById(R.id.user_hp);

        hpProcessBar.setMax(maxHp);
        hpProcessBar.setProgress(hp);
        ProgressBar expProcessBar = mHeaderView.findViewById(R.id.user_exp);
        expProcessBar.setProgress(exp);
        expProcessBar.setMax(maxExp);
        TextView name = mHeaderView.findViewById(R.id.user_name);
        name.setText(user.getName());
        TextView levelTextView = mHeaderView.findViewById(R.id.user_level);
        levelTextView.setText("Lv." + level);
    }

    public void openNavigation() {
        if (mDrawerLayout.isDrawerOpen(mNavigationView)) {
            mDrawerLayout.closeDrawer(mNavigationView);
        } else {
            mDrawerLayout.openDrawer(mNavigationView);
        }
    }
}
