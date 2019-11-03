package com.example.learningsupport_argame.UserManagement.Login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.learningsupport_argame.R;
import com.example.learningsupport_argame.UserManagement.ActivityUtil;
import com.example.learningsupport_argame.UserManagement.UserLab;

public class ChangePasswordActivity extends AppCompatActivity {
    private FrameLayout mReturnButton;

    private TextView mOldPasswordTextLabel;
    private TextView mOldPasswordTextStatus;
    private TextView mOldPasswordTextEditText;

    private TextView mPasswordTextLabel;
    private TextView mPasswordTextStatus;
    private EditText mPasswordEditText;


    private TextView mVerifyTextLabel;
    private TextView mVerifyTextStatus;
    private EditText mVerifyEditText;

    private Button mChangePasswordButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_management_activity_change_password);
        mReturnButton = findViewById(R.id.user_management_change_password_return);
        mOldPasswordTextLabel = findViewById(R.id.user_management_old_password_label);
        mOldPasswordTextStatus = findViewById(R.id.user_management_old_password_status);
        mOldPasswordTextEditText = findViewById(R.id.user_management_old_password);

        mPasswordTextLabel = findViewById(R.id.user_management_password_label);
        mPasswordTextStatus = findViewById(R.id.user_management_password_status);
        mPasswordEditText = findViewById(R.id.user_management_password);

        mVerifyTextLabel = findViewById(R.id.user_management_confirm_password_label);
        mVerifyTextStatus = findViewById(R.id.user_management_confirm_password_status);
        mVerifyEditText = findViewById(R.id.user_management_confirm_password);

        mChangePasswordButton = findViewById(R.id.user_management_change_password_button);
        mChangePasswordButton.setOnClickListener(v -> {
            String old_password = mOldPasswordTextEditText.getText().toString();
            String password = mPasswordEditText.getText().toString();
            String verify = mVerifyEditText.getText().toString();
            // 数据为空
            boolean status = changePromptMessage(old_password.equals(""),
                    "请输入旧密码", mOldPasswordTextLabel, mOldPasswordTextStatus);
            status |= changePromptMessage(password.equals(""),
                    "请输入新密码", mPasswordTextLabel, mPasswordTextStatus);
            status |= changePromptMessage(verify.equals(""),
                    "请再一次输入密码", mVerifyTextLabel, mVerifyTextStatus);
            if (status == true)
                return;
            new Thread(() -> {
                runOnUiThread(() -> {
                    // 旧密码输入错误
                    boolean s = changePromptMessage(!UserLab.getCurrentUser().getPassword().equals(old_password),
                            "密码错误", mOldPasswordTextLabel, mOldPasswordTextStatus);
                    // 两次密码不同
                    s |= changePromptMessage(!password.equals(verify),
                            "两次密码不相同", mVerifyTextLabel, mVerifyTextStatus);
                    // 修改成功
                    if (s == false) {
                        new Thread(() -> {
                            UserLab.getCurrentUser().setPassword(password);
                            UserLab.updateUser(UserLab.getCurrentUser());
                        }).start();

                        // 清空本地缓存
                        SharedPreferences userInfo = getSharedPreferences(LoginActivity.PREFS_NAME, MODE_PRIVATE);
                        SharedPreferences.Editor editor = userInfo.edit();//获取Editor
                        editor.clear();
                        editor.commit();
                        // 回到登录界面
                        startActivity(new Intent(this, LoginActivity.class));
                        Toast.makeText(this, "修改成功", Toast.LENGTH_SHORT).show();
                    }
                });
            }).start();
        });

        mReturnButton.setOnClickListener(v -> finish());

        ActivityUtil.addActivity(this);

    }

    private boolean changePromptMessage(boolean status, String promptMessage, TextView labelTextView, TextView statusTextView) {
        if (status == true) {
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
}
