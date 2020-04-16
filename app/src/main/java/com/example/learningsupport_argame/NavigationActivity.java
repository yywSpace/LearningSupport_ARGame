package com.example.learningsupport_argame;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.example.learningsupport_argame.Community.club.fragment.ClubListFragment;
import com.example.learningsupport_argame.Community.friend.FriendListActivity;
import com.example.learningsupport_argame.Course.CourseMainActivity;
import com.example.learningsupport_argame.Course.CourseMainFragment;
import com.example.learningsupport_argame.FeedbackModel.FeedbackDetailsActivity;
import com.example.learningsupport_argame.Navi.Activity.MapActivity;
import com.example.learningsupport_argame.Task.Task;
import com.example.learningsupport_argame.Task.TaskLab;
import com.example.learningsupport_argame.Task.TaskShowView;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

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
        if (savedInstanceState != null) {
            Fragment fragment = getSupportFragmentManager().getFragment(savedInstanceState, "fragment");
            if (fragment instanceof TaskListFragment) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, TaskListFragment.getInstance(1))
                        .commit();
            } else if (fragment instanceof ClubListFragment) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new ClubListFragment())
                        .commit();
            } else if (fragment instanceof CourseMainFragment) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new CourseMainFragment())
                        .commit();
            }

        } else {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragment_container, TaskListFragment.getInstance(1))
                    .commit();
        }
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

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (getSupportFragmentManager().getFragments().size() >= 1) {
            getSupportFragmentManager().putFragment(
                    outState,
                    "fragment",
                    getSupportFragmentManager().getFragments().get(0));
            Log.d(TAG, "onSaveInstanceState: ");
        }
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
        ImageButton announcementImageButton = mHeaderView.findViewById(R.id.navigation_announcement);
        announcementImageButton.setOnClickListener(v -> {
            Toast.makeText(this, "通知", Toast.LENGTH_SHORT).show();
            startAnnouncementDialog(this);
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

    public void startAnnouncementDialog(Activity context) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.announcement_layout, null, false);
        TextSwitcher switcher1 = view.findViewById(R.id.announcement_title_1);
        TextSwitcher switcher2 = view.findViewById(R.id.announcement_title_2);
        switcher1.setOnClickListener(v -> {
            if (TaskLab.sAllPeopleTaskList == null)
                return;
            Optional<Task> optionalTask = TaskLab.sAllPeopleTaskList
                    .stream()
                    .filter(task1 -> {
                        TextView textView = (TextView) switcher1.getCurrentView();
                        return task1.getTaskName().equals(textView.getText().toString());
                    }).findFirst();
            optionalTask.ifPresent(task -> {
                TaskShowView taskShowView = new TaskShowView(NavigationActivity.this);
                taskShowView.initData(task);
                AlertDialog.Builder builder = new AlertDialog.Builder(NavigationActivity.this);
                builder.setView(taskShowView.getView());
                builder.setTitle("任务详情");
                builder.setPositiveButton("确认", null);
                builder.show();
            });

        });
        switcher2.setOnClickListener(v -> {
            if (TaskLab.sAllPeopleTaskList == null)
                return;
            Optional<Task> optionalTask = TaskLab.sAllPeopleTaskList
                    .stream()
                    .filter(task1 -> {
                        TextView textView = (TextView) switcher2.getCurrentView();
                        return task1.getTaskName().equals(textView.getText().toString());
                    }).findFirst();
            optionalTask.ifPresent(task -> {
                TaskShowView taskShowView = new TaskShowView(NavigationActivity.this);
                taskShowView.initData(task);
                AlertDialog.Builder builder = new AlertDialog.Builder(NavigationActivity.this);
                builder.setView(taskShowView.getView());
                builder.setTitle("任务详情");
                builder.setPositiveButton("确认", null);
                builder.show();
            });
        });
        switcher1.setInAnimation(AnimationUtils.loadAnimation(context, R.anim.announcement_in));
        switcher1.setOutAnimation(AnimationUtils.loadAnimation(context, R.anim.announcement_out));
        switcher2.setInAnimation(AnimationUtils.loadAnimation(context, R.anim.announcement_in));
        switcher2.setOutAnimation(AnimationUtils.loadAnimation(context, R.anim.announcement_out));
        ViewSwitcher.ViewFactory factory = () -> {
            TextView t = new TextView(context);
            t.setTextColor(Color.parseColor("#333333"));
            t.setMaxLines(1);
            float textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 8, context.getResources().getDisplayMetrics());
            t.setTextSize(textSize);
            return t;
        };
        switcher1.setFactory(factory);
        switcher2.setFactory(factory);

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (TaskLab.sAllPeopleTaskList == null) {
                    switcher1.setCurrentText("加载中...");
                    switcher2.setCurrentText("加载中...");
                    return;
                }
                List<Task> taskList = new ArrayList<>(TaskLab.sAllPeopleTaskList);
                Random random = new Random(System.currentTimeMillis());
                int ran1 = random.nextInt(taskList.size());
                int ran2 = random.nextInt(taskList.size());
                context.runOnUiThread(() -> {
                    switcher1.setText(taskList.get(ran1).getTaskName());
                    switcher2.setText(taskList.get(ran2).getTaskName());
                });
            }
        }, 0, 3000);

        new Thread(() -> {
            if (TaskLab.sAllPeopleTaskList == null) {
                TaskLab.getAllPeopleTask();
            }
        }).start();

        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setView(view)
                .show();
        ImageButton exitButton = view.findViewById(R.id.announcement_exit_button);
        exitButton.setOnClickListener((v1) -> {
            timer.cancel();
            alertDialog.dismiss();
        });
    }
}
