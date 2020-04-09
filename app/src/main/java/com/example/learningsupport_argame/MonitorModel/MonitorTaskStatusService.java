package com.example.learningsupport_argame.MonitorModel;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.learningsupport_argame.Community.friend.FriendListActivity;
import com.example.learningsupport_argame.Course.Course;
import com.example.learningsupport_argame.Course.CourseLab;
import com.example.learningsupport_argame.Course.CourseMainActivity;
import com.example.learningsupport_argame.Course.CourseSetting;
import com.example.learningsupport_argame.Course.CourseTime;
import com.example.learningsupport_argame.FeedbackModel.MissionAccomplishActivity;
import com.example.learningsupport_argame.R;
import com.example.learningsupport_argame.UserManagement.UserLab;
import com.example.learningsupport_argame.Task.Task;
import com.example.learningsupport_argame.Task.TaskLab;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 监控任务是否开始
 */
public class MonitorTaskStatusService extends Service {
    private static final String TAG = "MonitorTaskStatusService";
    private volatile Thread mTaskStatusThread;
    public static Intent mMonitorTaskStatusServiceIntent;
    private Intent mMonitorIntent;
    // 假设一次只能有一个任务开始
    public static boolean alreadyBegan = false;
    private List<Task> mCourseTask = new ArrayList<>();
    private List<Task> mMonitoredTask = new ArrayList<>();
    private String[] weekArray = {"周一", "周二", "周三", "周四", "周五", "周六", "周日"};
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public void onCreate() {
        super.onCreate();
        new Thread(() -> {
            while (UserLab.getCurrentUser() == null) ;
            // 或如所接受的任务列表
            TaskLab.getAcceptedTask(UserLab.getCurrentUser().getId() + "");
            // 获取今天的年月日
            String today = df.format(new Date());

            // 获取已经加入task数据库的course
            boolean hasInsertCourseTask = false;
            List<Task> oldCourseTask = TaskLab.getCourseTask();
            for (Task task : oldCourseTask) {
                // 如果CorseTask中包含今天日期，则已经插入过
                if (task.getTaskCreateTime().contains(today)) {
                    hasInsertCourseTask = true;
                    break;
                }
            }
            // 如果已经插入过，直接返回
            if (hasInsertCourseTask) {
                return;
            }
            // 获取课程列表,并转换为任务
            CourseLab.getCourseSetting();
            int currentWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
            int schoolWeek = getCurrentWeek();
            CourseLab.getAllCourse(UserLab.getCurrentUser().getId());
            Log.d(TAG, "size: " + CourseLab.sCourseList.size());
            Log.d(TAG, "currentWeek: " + currentWeek);
            Log.d(TAG, "schoolWeek: " + schoolWeek);

            CourseLab.sCourseList.stream()
                    .filter(course ->
                            course.getStartWeek() <= schoolWeek && course.getEndWeek() >= schoolWeek)
                    .forEach(course -> {
                        for (CourseTime ct : course.getTimes()) {
                            if (course.isMonitor()) {
                                if (ct.getWeek().equals(weekArray[currentWeek])) {
                                    Task task = new Task();
                                    task.setTaskName(course.getName());
                                    task.setTaskType("课程");
                                    task.setAccomplishTaskLocation(course.getLocation());
                                    task.setTaskStatus("未开始");
                                    task.setUserId(course.getUserId());
                                    task.setTaskContent(String.format("教师：%s \n教室：%s", course.getTeacher(),
                                            course.getClassroom()));
                                    String startHour = CourseSetting.ALL_COURSE_START_TIME.get(ct.getStartTime());
                                    String endHour = CourseSetting.ALL_COURSE_START_TIME.get(ct.getEndTime()).split(":")[0] + ":" + CourseSetting.COURSE_TIME_SPAN;

                                    String startTime = today + " " + startHour;
                                    String endTime = today + " " + endHour;
                                    task.setTaskCreateTime(startTime);
                                    task.setTaskStartAt(startTime);
                                    task.setTaskEndIn(endTime);
                                    Log.d(TAG, "startTime: " + startTime);
                                    Log.d(TAG, "endTime: " + endTime);
                                    TaskLab.insertTask(task);
                                    // 将构造的CourseTask加入Task数据库并接受
                                    TaskLab.acceptTask(task);
                                }
                            }
                        }
                    });

            // 此处获取task_id，防止第一次加入是id为空导致的错误
            oldCourseTask = TaskLab.getCourseTask();
            for (Task task : oldCourseTask) {
                // 如果CorseTask中包含今天日期，则已经插入过
                if (task.getTaskCreateTime().contains(today)) {
                    mCourseTask.add(task);
                }
            }
        }).start();
        mTaskStatusThread = new Thread(() -> {
            Thread currentThread = Thread.currentThread();
            while (mTaskStatusThread == currentThread) {
                if (TaskLab.mAcceptedTaskList == null) {
                    continue;
                }
                mMonitoredTask.clear();
                mMonitoredTask.addAll(TaskLab.mAcceptedTaskList);
                mMonitoredTask.addAll(mCourseTask);
                // 10秒检测一次, 减轻开销
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                for (int i = 0; i < mMonitoredTask.size(); i++) {
                    boolean isTaskSuccess;
                    Task task = mMonitoredTask.get(i);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Date now = new Date();
                    try {
                        Date begin = df.parse(task.getTaskStartAt());
                        Date end = df.parse(task.getTaskEndIn());
                        // 如果已经接受的任务开始时间在当前时间之后则任务结束
                        if (end.before(now)) {
                            if (alreadyBegan)
                                continue;
                            TaskLab.mAcceptedTaskList.remove(task);
                            Log.d(TAG, "任务结束-" + task.getTaskName());
                            // 查询远端是否含有此任务监督信息
                            MonitorInfo monitorInfo = MonitorInfoLab.getMonitorInfoByTaskId(task.getTaskId());
                            if (monitorInfo == null) {
                                // 查询本地是否存有此任务监督信息
                                SharedPreferences infoPref = getSharedPreferences(MonitorInfo.MONITOR_INFO_PREFS_NAME, MODE_PRIVATE);
                                int task_id = infoPref.getInt(MonitorInfo.MONITOR_TASK_ID, -1);
                                if (task_id == task.getTaskId()) {
                                    monitorInfo = new MonitorInfo();
                                    monitorInfo.setTaskBeginTime(task.getTaskStartAt());
                                    monitorInfo.setTaskEndTime(task.getTaskEndIn());
                                    int taskScreenOnTime = infoPref.getInt(MonitorInfo.TASK_SCREEN_ON_TIME, 0);
                                    int taskScreenOffTime = infoPref.getInt(MonitorInfo.TASK_SCREEN_OFF_TIME, 0);
                                    int attentionTime = infoPref.getInt(MonitorInfo.ATTENTION_TIME, 0);
                                    int phoneUseCount = infoPref.getInt(MonitorInfo.PHONE_USE_COUNT, 0);
                                    int outOfRangeTime = infoPref.getInt(MonitorInfo.TASK_OUT_OF_RANGE_TIME, 0);
                                    monitorInfo.setMonitorTaskScreenOffTime(taskScreenOffTime);
                                    monitorInfo.setMonitorTaskScreenOnTime(taskScreenOnTime);
                                    monitorInfo.setMonitorScreenOnAttentionSpan(attentionTime);
                                    monitorInfo.setMonitorPhoneUseCount(phoneUseCount);
                                    monitorInfo.setTaskOutOfRangeTime(outOfRangeTime);
                                }
                            }
                            // 设置此任务为结束状态
                            task.setTaskStatus("已结束");
                            TaskLab.updateTask(task);
                            // 判断是否有监督记录，如果没有则任务失败,否则判断任务是否成功
                            if (monitorInfo != null) {
                                // 设置任务参与者完成状态
                                if (MonitorInfoLab.testTaskSuccess(monitorInfo)) {
                                    TaskLab.updateTaskParticipantStatus(
                                            monitorInfo.getTaskId() + "",
                                            UserLab.getCurrentUser().getId() + "",
                                            "完成");
                                    isTaskSuccess = true;
                                } else {
                                    TaskLab.updateTaskParticipantStatus(
                                            monitorInfo.getTaskId() + "",
                                            UserLab.getCurrentUser().getId() + "",
                                            "失败");
                                    isTaskSuccess = false;
                                    // 根据失神时间减去血量
                                    int inattentionTime = (int) ((monitorInfo.getTaskTotalTime() -
                                            monitorInfo.getMonitorAttentionTime()) / 60);
                                    if (inattentionTime >= 30)
                                        UserLab.getCurrentUser().gettingHeart(2);
                                    else
                                        UserLab.getCurrentUser().gettingHeart(1);
                                    UserLab.updateUser(UserLab.getCurrentUser());
                                }
                            } else {
                                TaskLab.updateTaskParticipantStatus(
                                        task.getTaskId() + "",
                                        UserLab.getCurrentUser().getId() + "",
                                        "失败");
                                isTaskSuccess = false;
                                monitorInfo = new MonitorInfo();
                                monitorInfo.setTaskBeginTime(task.getTaskStartAt());
                                monitorInfo.setTaskEndTime(task.getTaskEndIn());
                                monitorInfo.setTaskId(task.getTaskId());
                                monitorInfo.setMonitorTaskScreenOnTime(0);
                                monitorInfo.setMonitorScreenOnAttentionSpan(0);
                                monitorInfo.setMonitorPhoneUseCount(0);
                                monitorInfo.setTaskOutOfRangeTime(monitorInfo.getTaskTotalTime());
                                monitorInfo.setTaskDelayTime(monitorInfo.getTaskTotalTime());
                                // 保存
                                MonitorInfoLab.insertMonitorInfo(monitorInfo);
                                UserLab.getCurrentUser().gettingHeart(2);
                                UserLab.updateUser(UserLab.getCurrentUser());
                            }
                            if (!isTaskSuccess) {
                                createNotification(task.getTaskId(),
                                        "任务失败", "任务" + task.getTaskName() + "执行失败，点击查看详细信息", monitorInfo);
                            } else {
                                createNotification(task.getTaskId(),
                                        "任务成功", "任务" + task.getTaskName() + "执行成功，点击查看详细信息", monitorInfo);
                            }
                            TaskLab.mAcceptedTaskList.removeIf(task1 -> task1.getTaskName().equals(task.getTaskName()));
                            mCourseTask.removeIf(task1 -> task1.getTaskName().equals(task.getTaskName()));
                        }

                        // 如果begin--now--end(now在begin之后end之前)，任务开始
                        if (now.after(begin) && now.before(end)) {
                            if (alreadyBegan)
                                continue;
                            alreadyBegan = true;
                            Log.d(TAG, "任务开始-" + task.getTaskName());
                            if (!TaskLab.mRunningTaskList.contains(task)) {
                                TaskLab.mRunningTaskList.add(task);
                                Log.d(TAG, "onCreate: " + task.getTaskStartAt() + " " + task.getTaskEndIn());
                                Intent monitorIntent = new Intent(this, MonitorActivity.class);
                                monitorIntent.putExtra("task", task);
                                monitorIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                                // 打开监督服务
                                mMonitorIntent = new Intent(this, MonitorTaskAccomplishService.class);
                                mMonitorIntent.putExtra("task", task);
                                String currentTime = df.format(now);
                                mMonitorIntent.putExtra("current_time", currentTime);
                                startService(mMonitorIntent);
                                MonitorActivity.sMonitorIntent = mMonitorIntent;
                                // 打开监督界面
                                getApplication().startActivity(monitorIntent);
                                // 设置任务状态为正执行
                                new Thread(() -> {
                                    task.setTaskStatus("正执行");
                                    TaskLab.updateTask(task);
                                }).start();
                            }
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: ");
        // 开启线程
        if (!mTaskStatusThread.isAlive())
            mTaskStatusThread.start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mTaskStatusThread = null;
    }

    void createNotification(int notificationId, String title, String content, MonitorInfo monitorInfo) {
        final String CHANNEL_ID = "CHAT_CHANNEL_ID";
        final String CHANNEL_NAME = "CHAT_CHANNEL_NAME";
        NotificationManager mNotificationManager = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            //只在Android O之上需要渠道
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID,
                    CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            //如果这里用IMPORTANCE_NOENE就需要在系统的设置里面开启渠道，
            //通知才能正常弹出
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID);
        if (title.contains("失败"))
            builder.setSmallIcon(R.drawable.task_accomplish_failure);
        else
            builder.setSmallIcon(R.drawable.task_accomplish_success);
        builder.setContentTitle(title)
                .setContentText(content)
                .setAutoCancel(true);
        Intent resultIntent = new Intent(this, MissionAccomplishActivity.class);
        resultIntent.putExtra(MonitorInfo.MONITOR_INFO, monitorInfo);
        resultIntent.putExtra("is_new_info", false);
        resultIntent.setAction(Intent.ACTION_MAIN);
        resultIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        //building the notification
        builder.setContentIntent(resultPendingIntent);
        mNotificationManager.notify(notificationId, builder.build());
    }

    int getCurrentWeek() {
        String schoolOpenDate = CourseSetting.SCHOOL_OPEN_DATE;
        if (schoolOpenDate == null) {
            return -1;
        } else {
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
            Date schoolOpensDate = null;
            try {
                schoolOpensDate = dateFormat.parse(schoolOpenDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            Calendar currentDate = Calendar.getInstance();
            long currentTime = currentDate.getTime().getTime();
            long schoolOpensTime = schoolOpensDate.getTime();

            int currentWeek = currentDate.get(Calendar.DAY_OF_WEEK);

            int num = (currentWeek == 1 ? 7 : currentWeek - 1);
            long time = currentTime - schoolOpensTime;
            int day = (int) (time / 1000 / 60 / 60 / 24);
            if (time < 0)
                return -1; // 假期
            else {
                int k = (day - (7 - num + 1));
                if (k >= 1) {
                    return ((k / 7) + 2);
                } else {
                    return 1;
                }
            }
        }
    }
}
