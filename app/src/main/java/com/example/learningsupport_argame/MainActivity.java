package com.example.learningsupport_argame;


import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.ARModel.SendMessageActivity;
import com.example.learningsupport_argame.Course.CourseMainActivity;
import com.example.learningsupport_argame.FeedbackModel.FeedbackDetailsActivity;
import com.example.learningsupport_argame.Navi.Activity.MapActivity;
import com.example.learningsupport_argame.UserManagement.ActivityUtil;
import com.example.learningsupport_argame.UserManagement.Login.LoginActivity;
import com.example.learningsupport_argame.UserManagement.UserLab;
import com.example.learningsupport_argame.bean.PairInfoBean;
import com.example.learningsupport_argame.community.activity.FriendListDialog;
import com.example.learningsupport_argame.community.activity.FriendList_Main;
import com.example.learningsupport_argame.task.Task;
import com.example.learningsupport_argame.task.TaskLab;
import com.example.learningsupport_argame.task.TaskPopWindow.TaskListPopWindow;
import com.example.learningsupport_argame.task.activity.TaskListActivity;
import com.example.learningsupport_argame.tempararyfile.CurrentTaskFragment;
import com.example.learningsupport_argame.tempararyfile.MultiSelectionSpinner;
import com.example.learningsupport_argame.tempararyfile.TaskListFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class MainActivity extends SendMessageActivity {
    private static String TAG = "MainActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityUtil.addActivity(this);
        SharedPreferences userInfo = getSharedPreferences(LoginActivity.PREFS_NAME, MODE_PRIVATE);
        Toast.makeText(this, userInfo.getString("account", ""), Toast.LENGTH_SHORT).show();
        SendMessage("UIController", "GetUserAccount", userInfo.getString("account", ""));

    }

    public void startFriendActivity() {
        Toast.makeText(this, "startFriendActivity", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, FriendList_Main.class));
    }

    public void startTaskActivity() {
        Toast.makeText(this, "startTaskActivity", Toast.LENGTH_SHORT).show();
//        new TaskListPopWindow(this);
        startActivity(new Intent(this, TaskListActivity.class));


    }

    public void startCourseActivity() {
        Toast.makeText(this, "startCourseActivity", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, CourseMainActivity.class));
    }

    public void startFeedbackActivity() {
        Toast.makeText(this, "startFeedbackActivity", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, FeedbackDetailsActivity.class));
    }

    public void startMapActivity() {
        Toast.makeText(this, "startMapActivity", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, MapActivity.class));
    }

    public void startLoginActivity() {
        Toast.makeText(this, "startFeedbackActivity", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, LoginActivity.class));
    }

    /**
     * 以dialog形式开启好友列表
     */
    public void startFriendDialog(String message) {
        // 从unity传来的，当前给用户发送消息的列表
        String[] nameAndMessage = message.split("\n");
        FriendListDialog fld = new FriendListDialog(MainActivity.this, UserLab.getCurrentUser().getId() + "", nameAndMessage);

        fld.setOnRecycleViewItemClick((view, position) -> {
            view.findViewById(R.id.friend_list_item_avatar_red_point).setVisibility(View.INVISIBLE);
            Log.d(TAG, "startFriendDialog: " + fld.getFriendList().get(position).getName());
            fld.getFriendPopupWindow().dismiss();
            SendMessage("ChatRoomController", "GetFriendName", fld.getFriendList().get(position).getName());
        });
    }

    public void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public void logout() {
        SharedPreferences userInfo = getSharedPreferences(LoginActivity.PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = userInfo.edit();//获取Editor
        editor.clear();
        editor.commit();
    }

}