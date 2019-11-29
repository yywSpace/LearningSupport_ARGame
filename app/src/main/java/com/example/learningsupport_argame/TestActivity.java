package com.example.learningsupport_argame;

import android.graphics.Camera;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Transformation;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import androidx.appcompat.app.AppCompatActivity;

import com.example.learningsupport_argame.MonitorModel.Poetry;
import com.example.learningsupport_argame.MonitorModel.PoetryLab;

import java.util.List;


public class TestActivity extends AppCompatActivity {
    private String TAG = "TestActivity";
    private int mCnt = 0;
    private TextSwitcher mTextSwitcherRight, mTextSwitcherLeft;
    private Handler mHandler;
    private Runnable mRunnable;
    private List<Poetry> mPoetryList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mHandler = new Handler();
        mRunnable = new Runnable() {
            @Override
            public void run() {
                Poetry poetry = mPoetryList.get(mCnt++ % mPoetryList.size());
                mTextSwitcherRight.setText(poetry.getPoetryHead());
                mTextSwitcherLeft.setText(poetry.getPoetryTail() + "   ｜" + poetry.getPoetryAuthor());
                //要做的事情，这里再次调用此Runnable对象，以实现每两秒实现一次的定时器操作
                mHandler.postDelayed(this, 10000);
            }
        };
        // 诗歌列表
        mPoetryList = PoetryLab.get().getPoetryList();
        // 初始化控件
        mTextSwitcherRight = findViewById(R.id.textSwitcherRight);
        mTextSwitcherRight.setFactory(() -> {
            TextView tv = new TextView(TestActivity.this);
            tv.setTextSize(20);
            return tv;
        });
        mTextSwitcherLeft = findViewById(R.id.textSwitcherLeft);
        mTextSwitcherLeft.setFactory(() -> {
            TextView tv = new TextView(TestActivity.this);
            tv.setTextSize(20);
            return tv;
        });
    }

    @Override
    protected void onResume() {
        mHandler.postDelayed(mRunnable, 0);
        super.onResume();
    }

    @Override
    protected void onPause() {
        mHandler.removeCallbacks(mRunnable);
        super.onPause();
    }

}
