package com.example.learningsupport_argame.MonitorModel;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import androidx.appcompat.app.AppCompatActivity;

import com.example.learningsupport_argame.FeedbackModel.MissionAccomplishActivity;
import com.example.learningsupport_argame.R;
import com.example.learningsupport_argame.Task.Task;
import com.example.learningsupport_argame.TestActivity;
import com.example.learningsupport_argame.UserManagement.UserLab;

import java.util.List;


/*
    任务时间到达自动执行此Activity
    任务执行后如果不在执行地点,或不在此应用界面则记录相应时间
    任务顺利完成后进入完成界面
*/
// todo 引导用户将此应用加入白名单
public class MonitorActivity extends AppCompatActivity {
    private static String TAG = "MonitorActivity";
    public static Intent sMonitorIntent;
    private ImageView mReturnImageView;
    private TextView mAttentionTime;
    private TextView mPhoneUseCount;
    private TextView mTaskScreenOnTime;
    private TextView mRemainingTime;
    private int mInitialRemainingTime;
    private CountDownView mCountDownView;
    private ImageView mTaskSpeedUpImageView;
    private boolean hasSetTime;
    public static MonitorUIHandler mMonitorHandler;
    public static boolean isActivityOn;
    private MonitorTaskAccomplishService mMonitorTaskAccomplishService;
    private Task mTask;
    private boolean mIsUnBound = false;
    private TextSwitcher mTextSwitcherRight, mTextSwitcherLeft;
    // 诗词切换
    private Handler mHandler;
    private Runnable mRunnable;
    private List<Poetry> mPoetryList;
    private int mCnt = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.monitor_activity_monitor);
        initView();
        // 用户循环切换诗词
        mHandler = new Handler();
        mRunnable = new Runnable() {
            @Override
            public void run() {
                Poetry poetry = mPoetryList.get(mCnt++ % mPoetryList.size());
                mTextSwitcherRight.setText(poetry.getPoetryHead());
                mTextSwitcherLeft.setText(poetry.getPoetryTail() + "｜" + poetry.getPoetryAuthor());
                //要做的事情，这里再次调用此Runnable对象，以实现每两秒实现一次的定时器操作
                mHandler.postDelayed(this, 10000);
            }
        };
        // 诗歌列表
        mPoetryList = PoetryLab.get().getPoetryList();
        // 获取当前任务
        mTask = (Task) getIntent().getSerializableExtra("task");
//        if (mTask == null) {
//            mTask = new Task();
//            mTask.setAccomplishTaskLocation("I do flowers鲜花店,34.819612,114.321369");
//            mTask.setTaskName("任务测试");
//            mTask.setTaskContent("task content");
//            mTask.setTaskStartAt("2019-7-30 16:29");
//            mTask.setTaskEndIn("2019-7-30 16:30");
//        }

        // 打开监督服务
//        mMonitorIntent = new Intent(this, MonitorTaskAccomplishService.class);
//        startService(mMonitorIntent);
//        bindService(mMonitorIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
        mMonitorHandler = new MonitorUIHandler();
    }

    //屏蔽返回键的代码:
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK)
            return true;
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mHandler.postDelayed(mRunnable, 0);
        isActivityOn = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        mHandler.removeCallbacks(mRunnable);
        isActivityOn = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    void initView() {
        mTaskSpeedUpImageView = findViewById(R.id.monitor_task_speed_up_image_view);
        if (UserLab.getCurrentUser().isNextTaskSpeedUp()) {
            mTaskSpeedUpImageView.setVisibility(View.VISIBLE);
            mTaskSpeedUpImageView.setOnClickListener(v ->
                    Toast.makeText(MonitorActivity.this, "任务加速中", Toast.LENGTH_SHORT).show());
        } else {
            mTaskSpeedUpImageView.setVisibility(View.INVISIBLE);
        }

        mReturnImageView = findViewById(R.id.monitor_return);
        mReturnImageView.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_MAIN);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.addCategory(Intent.CATEGORY_HOME);
            startActivity(i);
        });
        mTaskScreenOnTime = findViewById(R.id.monitor_phone_use_time);
        mAttentionTime = findViewById(R.id.monitor_task_attention_time);
        mPhoneUseCount = findViewById(R.id.monitor_phone_use_count);
        mRemainingTime = findViewById(R.id.monitor_task_remaining_time);
        mCountDownView = findViewById(R.id.monitor_count_down_view);
        mCountDownView.setRadius(270);
        mTextSwitcherLeft = findViewById(R.id.monitor_poetry_switcher_left);
        mTextSwitcherRight = findViewById(R.id.monitor_poetry_switcher_right);
        ViewSwitcher.ViewFactory viewFactory = () -> {
            TextView tv = new TextView(MonitorActivity.this);
            tv.setTextSize(15);
            tv.setEms(1);
            return tv;
        };
        mTextSwitcherRight.setFactory(viewFactory);
        mTextSwitcherLeft.setFactory(viewFactory);
    }

    class MonitorUIHandler extends Handler {
        Bundle data;
        MonitorInfo monitorInfo;

        public MonitorUIHandler() {
            monitorInfo = new MonitorInfo();
            monitorInfo.setTaskBeginTime(mTask.getTaskStartAt());
            monitorInfo.setTaskEndTime(mTask.getTaskEndIn());
            monitorInfo.setTaskId(mTask.getTaskId());
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            data = msg.getData();
            mTaskScreenOnTime.setText(TimeUtils.second2Time(data.getInt(MonitorInfo.TASK_SCREEN_ON_TIME)));
            // 总专注时间=息屏时间+专注时间
            mAttentionTime.setText(TimeUtils.second2Time(data.getInt(MonitorInfo.ATTENTION_TIME) + data.getInt(MonitorInfo.TASK_SCREEN_OFF_TIME)));
            mPhoneUseCount.setText(data.getInt(MonitorInfo.PHONE_USE_COUNT) + "");
            //mRemainingTime.setText(MonitorTaskAccomplishService.second2Time(data.getLong(MonitorInfo.TASK_REMANDING_TIME)));
            int remandingTime = data.getInt(MonitorInfo.TASK_REMANDING_TIME);
            if (!hasSetTime) {
                mCountDownView.setInitialSecond(monitorInfo.getTaskTotalTime());
                hasSetTime = true;
            }
            mCountDownView.setCurrentSeconds(remandingTime);
            mCountDownView.setTimeLabel(TimeUtils.second2Time(remandingTime));
            mCountDownView.invalidate();

            if (remandingTime > 0)
                return;

//            unbindService(mServiceConnection);
            if (sMonitorIntent != null) {
                stopService(sMonitorIntent);
                Log.d(TAG, "handleMessage: stopService");
            }

            monitorInfo.setMonitorTaskScreenOnTime(data.getInt(MonitorInfo.TASK_SCREEN_ON_TIME));
            monitorInfo.setMonitorScreenOnAttentionSpan(data.getInt(MonitorInfo.ATTENTION_TIME));
            monitorInfo.setMonitorPhoneUseCount(data.getInt(MonitorInfo.PHONE_USE_COUNT));
            monitorInfo.setTaskOutOfRangeTime(data.getInt(MonitorInfo.TASK_OUT_OF_RANGE_TIME));
            monitorInfo.setMonitorTaskScreenOffTime(data.getInt(MonitorInfo.TASK_SCREEN_OFF_TIME));
            monitorInfo.setTaskDelayTime(data.getInt(MonitorInfo.TASK_DELAY_TIME));
            mCountDownView.setCurrentSeconds(0);
            mCountDownView.setTimeLabel(TimeUtils.second2Time(0));
            mCountDownView.invalidate();
            monitorInfo.setTask(mTask);
            Log.d(TAG, "handleMessage: " + monitorInfo.getTaskId());
            // 保存监督信息
            new Thread(() -> MonitorInfoLab.insertMonitorInfo(monitorInfo)).start();
            finish();
            Intent intent = new Intent(MonitorActivity.this, MissionAccomplishActivity.class);
            intent.putExtra(MonitorInfo.MONITOR_INFO, monitorInfo);
            startActivity(intent);
        }
    }
}
