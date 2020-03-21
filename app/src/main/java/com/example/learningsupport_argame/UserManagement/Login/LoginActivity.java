package com.example.learningsupport_argame.UserManagement.Login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.learningsupport_argame.Course.CourseMainActivity;
import com.example.learningsupport_argame.FeedbackModel.FeedbackDetailsActivity;
import com.example.learningsupport_argame.FeedbackModel.MissionAccomplishActivity;
import com.example.learningsupport_argame.MonitorModel.MonitorActivity;
import com.example.learningsupport_argame.MonitorModel.MonitorTaskStatusService;
import com.example.learningsupport_argame.Navi.Activity.LocationService;
import com.example.learningsupport_argame.R;
import com.example.learningsupport_argame.Task.activity.TaskListActivity;
import com.example.learningsupport_argame.UserManagement.ActivityUtil;
import com.example.learningsupport_argame.UserManagement.User;
import com.example.learningsupport_argame.UserManagement.UserLab;

// TODO: 20-2-17 各个权限的获取可以全部获取或按需获取
public class LoginActivity extends AppCompatActivity {
    private static String TAG = "LoginActivity";
    public static String PREFS_NAME = "user_info";
    private TextView mAccountTextLabel;
    private TextView mAccountTextStatus;
    private EditText mAccountEditText;
    private TextView mPasswordTextLabel;
    private TextView mPasswordTextStatus;
    private EditText mPasswordEditText;
    private Button mLoginButton;
    private TextView mRegisterTextView;
    // 防止多次点击
    private long lastClickTime = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityUtil.addActivity(this);

        setContentView(R.layout.user_management_activity_login);
        LocationService.mLocationServiceIntent = new Intent(this, LocationService.class);
        MonitorTaskStatusService.mMonitorTaskStatusServiceIntent = new Intent(this, MonitorTaskStatusService.class);
        mAccountTextLabel = findViewById(R.id.user_management_account_label);
        mAccountTextStatus = findViewById(R.id.user_management_account_status);
        mAccountEditText = findViewById(R.id.user_management_account);
        mPasswordTextLabel = findViewById(R.id.user_management_password_label);
        mPasswordTextStatus = findViewById(R.id.user_management_password_status);
        mPasswordEditText = findViewById(R.id.user_management_password);
        mLoginButton = findViewById(R.id.user_management_login);
        mRegisterTextView = findViewById(R.id.user_management_register);

        mLoginButton.setOnClickListener((view) -> {
            String account = mAccountEditText.getText().toString();
            String password = mPasswordEditText.getText().toString();
            boolean status;
            // 未输入帐号
            status = changePromptMessage(account.equals(""),
                    "请输入帐号", mAccountTextLabel, mAccountTextStatus);
            // 未输入密码
            status |= changePromptMessage(password.equals(""),
                    "请输入密码", mPasswordTextLabel, mPasswordTextStatus);

            if (status == true)
                return;

            new Thread(() -> {
                UserManagementStatus ums = UserLab.login(account, password);
                runOnUiThread(() -> {
                    changePromptMessage(ums == UserManagementStatus.LOGIN_ACCOUNT_NOT_EXIST,
                            "帐号不存在", mAccountTextLabel, mAccountTextStatus);
                    changePromptMessage(ums == UserManagementStatus.LOGIN_PASSWORD_ERROR,
                            "密码错误", mPasswordTextLabel, mPasswordTextStatus);
                    if (ums == UserManagementStatus.LOGIN_SUCCESS) {
                        // 登录成功
                        SharedPreferences userInfo = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                        SharedPreferences.Editor editor = userInfo.edit();// 获取Editor
                        // 写入需要保存的数据
                        User user = UserLab.getCurrentUser();
                        editor.putString("account", user.getAccount());
                        editor.putString("password", user.getPassword());
                        editor.commit();//提交修改
                        Toast.makeText(this, "LOGIN_SUCCESS" + UserLab.getCurrentUser().getName(), Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onCreate: " + user.getName());
//                        startService(LocationService.mLocationServiceIntent);
//                        startService(MonitorTaskStatusService.mMonitorTaskStatusServiceIntent);

//                        startActivity(new Intent(LoginActivity.this, MapActivity.class));
//                        startActivity(new Intent(this, TaskListActivity.class));
//                        startActivity(new Intent(this, FeedbackDetailsActivity.class));
                        startActivity(new Intent(this, CourseMainActivity.class));
                        finish();
                    }
                });
            }).start();

        });
        mRegisterTextView.setOnClickListener((view) -> {
            // 防止重复点击
            long now = System.currentTimeMillis();
            if (now - lastClickTime > 1000) {
                lastClickTime = now;
                startActivity(new Intent(this, RegisterActivity.class));
            }
        });

        SharedPreferences userInfo = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String account = userInfo.getString("account", "");
        String password = userInfo.getString("password", "");
        if (account.equals("") || password.equals("")) {
            return;
        }
        new Thread(() -> {
            User user = UserLab.getUser(account);
            UserLab.setCurrentUser(user);
            Log.d(TAG, "onCreate: " + UserLab.getCurrentUser().getId());
            startService(LocationService.mLocationServiceIntent);
            startService(MonitorTaskStatusService.mMonitorTaskStatusServiceIntent);
        }).start();
//        startActivity(new Intent(this, TaskListActivity.class));
//        startActivity(new Intent(LoginActivity.this, MapActivity.class));
//        startActivity(new Intent(this, FeedbackDetailsActivity.class));
        startActivity(new Intent(this, CourseMainActivity.class));
        finish();
    }

    private boolean changePromptMessage(boolean status, String promptMessage, TextView labelTextView, TextView statusTextView) {
        if (status) {
            labelTextView.setTextColor(Color.parseColor("#d81c60"));
            statusTextView.setVisibility(View.VISIBLE);
            statusTextView.setTextColor(Color.parseColor("#d81c60"));
            statusTextView.setText(promptMessage);
        } else {
            if (statusTextView.getVisibility() != View.INVISIBLE)
                statusTextView.setVisibility(View.INVISIBLE);
            labelTextView.setTextColor(Color.BLACK);
        }
        return status;
    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed();
        new AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage("确认要退出吗？")
                .setPositiveButton("确认", (dialog, which) -> {
                    ActivityUtil.destroyAll();
                })
                .setNegativeButton("取消", (dialog, which) -> {

                })
                .create()
                .show();
    }

}
