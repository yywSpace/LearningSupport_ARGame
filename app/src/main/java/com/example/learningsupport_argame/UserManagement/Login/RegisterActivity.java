package com.example.learningsupport_argame.UserManagement.Login;

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
import com.example.learningsupport_argame.UserManagement.User;
import com.example.learningsupport_argame.UserManagement.UserLab;

public class RegisterActivity extends AppCompatActivity {
    private FrameLayout mReturnButton;
    private TextView mAccountTextLabel;
    private TextView mAccountTextStatus;
    private EditText mAccountEditText;

    private TextView mUserNameTextLabel;
    private TextView mUserNameTextStatus;
    private EditText mUserNameEditText;

    private TextView mPasswordTextLabel;
    private TextView mPasswordTextStatus;
    private EditText mPasswordEditText;


    private TextView mVerifyTextLabel;
    private TextView mVerifyTextStatus;
    private EditText mVerifyEditText;
    private Button mRegisterButton;

    @Override

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_management_activity_register);
        mReturnButton = findViewById(R.id.user_management_register_return);
        mAccountTextLabel = findViewById(R.id.user_management_account_label);
        mAccountTextStatus = findViewById(R.id.user_management_account_status);
        mAccountEditText = findViewById(R.id.user_management_account);

        mUserNameTextLabel = findViewById(R.id.user_management_username_label);
        mUserNameTextStatus = findViewById(R.id.user_management_username_status);
        mUserNameEditText = findViewById(R.id.user_management_username);
        mPasswordTextLabel = findViewById(R.id.user_management_password_label);
        mPasswordTextStatus = findViewById(R.id.user_management_password_status);
        mPasswordEditText = findViewById(R.id.user_management_password);
        mVerifyTextLabel = findViewById(R.id.user_management_confirm_password_label);
        mVerifyTextStatus = findViewById(R.id.user_management_confirm_password_status);
        mVerifyEditText = findViewById(R.id.user_management_confirm_password);
        mRegisterButton = findViewById(R.id.user_management_register_button);

        mReturnButton.setOnClickListener((view) -> {
            finish();
        });

        mRegisterButton.setOnClickListener((view) -> {
            String account = mAccountEditText.getText().toString();
            String username = mUserNameEditText.getText().toString();
            String password = mPasswordEditText.getText().toString();
            String verify = mVerifyEditText.getText().toString();
            boolean status;
            status = changePromptMessage(account.equals(""),
                    "请输入帐号", mAccountTextLabel, mAccountTextStatus);
            status |= changePromptMessage(username.equals(""),
                    "请输入用户名", mUserNameTextLabel, mUserNameTextStatus);
            status |= changePromptMessage(password.equals(""),
                    "请输入密码", mPasswordTextLabel, mPasswordTextStatus);
            status |= changePromptMessage(verify.equals(""),
                    "请再一次输入密码", mVerifyTextLabel, mVerifyTextStatus);
            if (status == true)
                return;


            new Thread(() -> {
                User user = new User(account, username, password);
                UserManagementStatus ums = UserLab.register(user, verify);
                runOnUiThread(() -> {
                    changePromptMessage(ums == UserManagementStatus.REGISTER_ACCOUNT_EXIST,
                            "帐号已经存在", mAccountTextLabel, mAccountTextStatus);
                    changePromptMessage(ums == UserManagementStatus.REGISTER_PASSWORD_DIFFERENT,
                            "两次密码不相同", mPasswordTextLabel, mPasswordTextStatus);
                    if (ums == UserManagementStatus.REGISTER_SUCCESS) {
                        // 进入主页面
                        Toast.makeText(this, "注册成功", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            }).start();
        });

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
