package com.example.learningsupport_argame.UserManagement;

import android.os.Bundle;
import android.widget.ImageView;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import com.example.learningsupport_argame.R;

public class TestActivity extends AppCompatActivity {
    private static String TAG = "TestActivity";
    ImageView mImageView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_management_task_item);

    }


}

