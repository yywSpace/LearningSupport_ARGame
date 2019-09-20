package com.example.learningsupport_argame.Course;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.learningsupport_argame.R;

import org.litepal.LitePal;
import org.litepal.tablemanager.Connector;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import pl.com.salsoft.sqlitestudioremote.SQLiteStudioService;

/*

    任务时间到达自动执行此Activity

    任务执行后如果不在执行地点，或走出执行地点见： MonitorUIHandler 中 case 4

    任务顺利完成后各监督信息见： MonitorUIHandler 中 case 3

*/


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

    Context context;

    Calendar calendar;
    int year;
    int month;
    int day;
    int hour;
    int minute;
    int second;

    String openActivity;
    Intent intent;

    ImageView monitorReturnCoursetable;




    @Override

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.monitor_activity_layout);

        SQLiteStudioService.instance().start(this);

        LitePal.initialize(MonitorActivity.this);

         intent=getIntent();
         openActivity=intent.getStringExtra("openActivity");

        handler = new MonitorUIHandler();

        mSystemTime = findViewById(R.id.monitor_system_time);

        mSystemCalender = findViewById(R.id.monitor_system_calender);

        mTaskScreenOnTime = findViewById(R.id.monitor_phone_use_time);

        mAttentionTime = findViewById(R.id.monitor_task_attention_time);

        mPhoneUseCount = findViewById(R.id.monitor_phone_use_count);

        mRemainingTime = findViewById(R.id.monitor_task_remaining_time);

        mView = findViewById(R.id.monitor_count_down_view);

        monitorReturnCoursetable=findViewById(R.id.monitor_return_coursetable);

        mMonitorIntent = new Intent(this, MonitorService.class);

        judge();

//        getSupportActionBar().hide();


    }

    public void judge()
    {
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH) + 1;
        day = calendar.get(Calendar.DAY_OF_MONTH);
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);
        second = calendar.get(Calendar.SECOND);

        if(openActivity.equals("CourseMainActivity"))
        {
//            Intent intent=getIntent();
            String courseEndTimeStr=intent.getStringExtra("courseEndTimeStr");

            mMonitorIntent.putExtra("CourseOrTask","Course");
           startService(mMonitorIntent);
           bindService(mMonitorIntent, mServiceConnection, Context.BIND_AUTO_CREATE);

            mTask = new Task();
//            mTask.setTaskName("task name");
//            mTask.setTaskContent("task content");
            Log.d("你是猪奥德赛大法师1111",year+"/"+month+"/"+day+"/"+hour+":"+minute);
            Log.d("你是猪奥德赛大法师2222",year+"/"+month+"/"+day+"/"+courseEndTimeStr);

            mTask.setTaskStartAt(year+"/"+month+"/"+day+"/"+hour+":"+minute);

            mTask.setTaskEndIn(year+"/"+month+"/"+day+"/"+courseEndTimeStr);

        }
        if(openActivity.equals(""))
        {
            mMonitorIntent.putExtra("CourseOrTask","Task");
         startService(mMonitorIntent);
          bindService(mMonitorIntent, mServiceConnection, Context.BIND_AUTO_CREATE);

            mTask = new Task();
            mTask.setTaskName("task name");
            mTask.setTaskContent("task content");
            mTask.setTaskStartAt("2019/7/30/16:00");
            mTask.setTaskEndIn("2019/7/30/16:30");
        }

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

            mMonitorService=null;



        }

    };

    @Override

    protected void onDestroy() {

        super.onDestroy();

        SQLiteStudioService.instance().stop();
        // unbindService(mServiceConnection);
//        Log.d("unbindService","被执行");
//        stopService(mMonitorIntent);
//        Log.d("stopService","被执行");

      //stopService(mMonitorIntent);
//        Log.d("stopService","被执行");
//        unbindService(mServiceConnection);
//        Log.d("unbindService","被执行");
//        mMonitorService=null;

    }

    public void monitor_return_coursetable_onClick(View view)
    {

       // stopService(mMonitorIntent);
      // unbindService(mServiceConnection);
//        mMonitorService=null;
//        mServiceConnection=null;

        Log.d("return","被执行");

        String str1=mTaskScreenOnTime.getText().toString();
        String str2=mAttentionTime.getText().toString();
        //long phoneUseSeconds=Integer.valueOf(str1.split(":")[0])*60*60 + Integer.valueOf(str1.split(":")[1])*60 + Integer.valueOf(str1.split(":")[2]);
        //long attentionSeconds=Integer.valueOf(str2.split(":")[0])*60*60 + Integer.valueOf(str2.split(":")[1])*60 + Integer.valueOf(str2.split(":")[2]);
        //int phoneUseCount=Integer.valueOf(mPhoneUseCount.getText().toString());

        int phoneUseSeconds=1;
        int attentionSeconds=1;
        int phoneUseCount=1;

        SQLiteDatabase db = Connector.getDatabase();
        MonitorCourseInfo monitorCourseInfo=new MonitorCourseInfo();
        monitorCourseInfo.setUseTime(phoneUseSeconds);
        monitorCourseInfo.setConcentrateTime(attentionSeconds);
        monitorCourseInfo.setUseCount(phoneUseCount);
        Date currentDate=null;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            currentDate = dateFormat.parse(year + "-" + month + "-" + day);
        } catch (ParseException e) {
            e.printStackTrace();
            Log.d("GetMonitorInfo", "当前日期获取失败");
        }

        String currentDateStr = dateFormat.format(currentDate);
        monitorCourseInfo.setMonitorDate(currentDateStr);
        monitorCourseInfo.save();

//
        GetMonitorInfo getMonitorInfo=new GetMonitorInfo();
        Log.d("dddddddddddDayUsetime",getMonitorInfo.getDayInfo()[0]);
        Log.d("dddddddddddDayContime",getMonitorInfo.getDayInfo()[1]);
        Log.d("dddddddddddDayCount",getMonitorInfo.getDayInfo()[2]);

        GetMonitorInfo getMonitorInfo1=new GetMonitorInfo();
        Log.d("dddddddddddWeekUsetime",getMonitorInfo1.getWeekInfo()[0]);
        Log.d("dddddddddddWeekContime",getMonitorInfo1.getWeekInfo()[1]);
        Log.d("dddddddddddWeekCount",getMonitorInfo1.getWeekInfo()[2]);

        GetMonitorInfo getMonitorInfo2=new GetMonitorInfo();
        Log.d("dddddddddddMonthUsetime",getMonitorInfo2.getMonthInfo()[0]);
        Log.d("dddddddddddMonthContime",getMonitorInfo2.getMonthInfo()[1]);
        Log.d("dddddddddddMonthCount",getMonitorInfo2.getMonthInfo()[2]);


//        Intent intent=new Intent(MonitorActivity.this,CourseMainActivity.class);
//        startActivity(intent);
//        MonitorActivity.this.finish();


//        unbindService(mServiceConnection);


    }


}
