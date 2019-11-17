package com.example.learningsupport_argame;

import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import com.example.learningsupport_argame.ARModel.PutModelActivity;
import com.example.learningsupport_argame.Navi.Activity.SelectLocationPopWindow;
import com.example.learningsupport_argame.Navi.Activity.ShowLocationPopWindow;

public class TestActivity extends AppCompatActivity {
    private static String TAG = "TestActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

}

