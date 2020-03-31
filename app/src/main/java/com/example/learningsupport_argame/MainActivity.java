package com.example.learningsupport_argame;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.ARModel.SendMessageActivity;
import com.example.learningsupport_argame.ARModel.PutModelActivity;
import com.example.learningsupport_argame.ARModel.ScanModelActivity;
import com.example.learningsupport_argame.Course.CourseMainActivity;
import com.example.learningsupport_argame.FeedbackModel.FeedbackDetailsActivity;
import com.example.learningsupport_argame.Navi.Activity.MapActivity;
import com.example.learningsupport_argame.UserManagement.ActivityUtil;
import com.example.learningsupport_argame.UserManagement.Login.LoginActivity;
import com.example.learningsupport_argame.UserManagement.User;
import com.example.learningsupport_argame.UserManagement.UserLab;
import com.example.learningsupport_argame.UserManagement.UserMessage.UserMessageActivity;
import com.example.learningsupport_argame.Community.friend.FriendListActivity;
import com.example.learningsupport_argame.Community.friend.FriendListDialog;
import com.example.learningsupport_argame.Task.activity.TaskListActivity;


public class MainActivity extends SendMessageActivity {//NavigationMainActivity
    private static String TAG = "NavigationMainActivity";
    public final static int TASK_ACTIVITY = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityUtil.addActivity(this);
        SharedPreferences userInfo = getSharedPreferences(LoginActivity.PREFS_NAME, MODE_PRIVATE);
        Toast.makeText(this, userInfo.getString("account", ""), Toast.LENGTH_SHORT).show();
        SendMessage("UIController", "GetUserAccount", userInfo.getString("account", ""));

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TASK_ACTIVITY:
                if (resultCode == RESULT_OK) {
                    // 此时任务列表返回，并开始利用ar放置模型
                    String taskName = data.getStringExtra("task_name");
                    String taskCreatedTime = data.getStringExtra("task_create_time");
                    String taskDesc = data.getStringExtra("task_content");
                    SendMessage("Canvas", "GetTaskInfo", taskName + "," + taskCreatedTime + "," + taskDesc);
                }
                break;

        }
    }

    public void startFriendActivity() {
        Toast.makeText(this, "startFriendActivity", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, FriendListActivity.class));
    }

    public void startTaskActivity() {
        Intent intent = new Intent(this, TaskListActivity.class);
        intent.putExtra(User.CURRENT_USER_ID, UserLab.getCurrentUser().getId() + "");
        startActivityForResult(intent, TASK_ACTIVITY);
        Toast.makeText(this, "startTaskActivity", Toast.LENGTH_SHORT).show();
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

    public void startUserMessageActivity() {
        Toast.makeText(this, "startUserMessageActivity", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, UserMessageActivity.class));
    }

    public void startModelPutActivity() {
        Toast.makeText(this, "startModelPutActivity", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, PutModelActivity.class));
    }

    public void startModelScanActivity() {
        Toast.makeText(this, "startModelScanActivity", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, ScanModelActivity.class));
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
            //SendMessage("ChatRoomController", "GetFriendName", fld.getFriendList().get(position).getName());
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