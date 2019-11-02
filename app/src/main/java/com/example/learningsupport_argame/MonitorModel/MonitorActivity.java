package com.example.learningsupport_argame.MonitorModel;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.KeyEvent;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.learningsupport_argame.R;
import com.example.learningsupport_argame.task.Task;

//import android.support.v7.app.AppCompatActivity;

/*
    任务时间到达自动执行此Activity
    任务执行后如果不在执行地点，或走出执行地点见： MonitorUIHandler 中 case 4
    任务顺利完成后各监督信息见： MonitorUIHandler 中 case 3
*/
// todo 任务信息如何传入此Activity还未决定
// todo 引导用户将此应用加入白名单
public class MonitorActivity extends AppCompatActivity {
    private Intent mMonitorIntent;
    private TextView mSystemTime;
    private TextView mSystemCalender;
    private TextView mAttentionTime;
    private TextView mPhoneUseCount;
    private TextView mTaskScreenOnTime;
    private TextView mRemainingTime;
    private int mInitialRemainingTime;
    private CountDownView mView;
    private boolean hasSetTime;
    public static MonitorUIHandler handler;
    public static boolean isActivityOn;
    private MonitorService mMonitorService;
    private Task mTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.monitor_activity_monitor);
        getSupportActionBar().hide();
        handler = new MonitorUIHandler();
        mSystemTime = findViewById(R.id.monitor_system_time);
        mSystemCalender = findViewById(R.id.monitor_system_calender);
        mTaskScreenOnTime = findViewById(R.id.monitor_phone_use_time);
        mAttentionTime = findViewById(R.id.monitor_task_attention_time);
        mPhoneUseCount = findViewById(R.id.monitor_phone_use_count);
        mRemainingTime = findViewById(R.id.monitor_task_remaining_time);
        mView = findViewById(R.id.monitor_count_down_view);
        mMonitorIntent = new Intent(this, MonitorService.class);
        startService(mMonitorIntent);
        bindService(mMonitorIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
        mTask = new Task();
        mTask.setTaskName("task name");
        mTask.setTaskContent("task content");
        mTask.setTaskStartAt("2019/7/30/16:00");
        mTask.setTaskEndIn("2019/7/30/16:30");
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
        isActivityOn = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        isActivityOn = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    class MonitorUIHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1: // 获取数据
                    Bundle data = msg.getData();
                    mTaskScreenOnTime.setText(data.getString(MonitorInfo.TASK_SCREEN_ON_TIME));
                    mAttentionTime.setText(data.getString(MonitorInfo.ATTENTION_TIME));
                    mPhoneUseCount.setText(data.getString(MonitorInfo.PHONE_USE_COUNT));
                    mRemainingTime.setText(MonitorService.second2Time(data.getLong(MonitorInfo.TASK_REMANDING_TIME)));
                    if (!hasSetTime && data.getLong(MonitorInfo.TASK_REMANDING_TIME) >= 0) {
                        mView.setInitialSecond(data.getLong(MonitorInfo.TASK_REMANDING_TIME));
                        mInitialRemainingTime = (int) data.getLong(MonitorInfo.TASK_REMANDING_TIME);
                        hasSetTime = true;
                    }
                    mView.updateSecond(data.getLong(MonitorInfo.TASK_REMANDING_TIME));
                    mView.invalidate();
                    break;
                case 2: // 获取日期时间
                    Calendar calendar = Calendar.getInstance();
                    int year = calendar.get(Calendar.YEAR);
                    int month = calendar.get(Calendar.MONTH) + 1;
                    int day = calendar.get(Calendar.DAY_OF_MONTH);
                    int hour = calendar.get(Calendar.HOUR_OF_DAY);
                    int minute = calendar.get(Calendar.MINUTE);
                    int second = calendar.get(Calendar.SECOND);
                    mSystemTime.setText(String.format("%02d:%02d:%02d", hour, minute, second));
                    mSystemCalender.setText(year + "/" + month + "/" + day);
                    break;
                case 3: // 任务成功结束
                    stopService(mMonitorIntent);
                    unbindService(mServiceConnection);
                    MonitorInfo monitorInfo = new MonitorInfo();
                    monitorInfo.setTaskBeginTime(mTask.getTaskStartAt());
                    monitorInfo.setTaskEndTime(mTask.getTaskEndIn());
                    monitorInfo.setMonitorTaskScreenOnTime(Integer.parseInt(mTaskScreenOnTime.getText().toString()));
                    monitorInfo.setMonitorScreenOnAttentionSpan(Integer.parseInt(mAttentionTime.getText().toString()));
                    monitorInfo.setMonitorPhoneUseCount(Integer.parseInt(mPhoneUseCount.getText().toString()));

                    // 处理代码
                    break;
                case 4: // 脱离任务地点，或不在
                    stopService(mMonitorIntent);
                    unbindService(mServiceConnection);


                    // 处理代码
                    break;
            }
        }
    }


    ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mMonitorService = ((MonitorService.MonitorBinder) service).getService();
            mMonitorService.setTask(mTask);

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
}
