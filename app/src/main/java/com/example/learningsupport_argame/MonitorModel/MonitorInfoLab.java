package com.example.learningsupport_argame.MonitorModel;

import android.util.Log;

import com.example.learningsupport_argame.DbUtils;
import com.example.learningsupport_argame.R;
import com.example.learningsupport_argame.UserManagement.User;
import com.example.learningsupport_argame.UserManagement.UserLab;

import java.nio.file.OpenOption;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

public class MonitorInfoLab {
    private static String TAG = MonitorInfoLab.class.getSimpleName();
    private static MonitorInfoLab sMMonitorInfoLab;
    private static List<MonitorInfo> mMonitorInfoList;
    private static SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy/MM/dd");
    private static int mEffectRow;

    private MonitorInfoLab() {


    }

    public static List<MonitorInfo> getAllMonitorInfo(int userId) {
        String sql = "SELECT * FROM monitor_info WHERE user_id = ?;";
        mMonitorInfoList = getMonitorInfoWith(sql, userId);
        return mMonitorInfoList;
    }


    public static MonitorInfoLab get() {
        if (sMMonitorInfoLab == null)
            sMMonitorInfoLab = new MonitorInfoLab();
        return sMMonitorInfoLab;
    }


    public static MonitorInfo getMonitorInfoByTaskId(int id) {
        String sql = "SELECT * FROM monitor_info WHERE task_id = ?;";
        List<MonitorInfo> list = getMonitorInfoWith(sql, id);
        if (list.size() <= 0) {
            return null;
        } else {
            return list.get(0);
        }
    }

    public static boolean testTaskSuccess(MonitorInfo monitorInfo) {
        boolean taskAccomplishSuccess = false;
        // 任务执行中专注时间
        float attentionRate = monitorInfo.getMonitorAttentionTime() / monitorInfo.getTaskTotalTime();
        // 任务执行中超出任务执行地点的时间
        float outOfRangeRate = monitorInfo.getTaskOutOfRangeTime() / monitorInfo.getTaskTotalTime();
        float rate = (attentionRate * 60 + (1 - outOfRangeRate) * 40) / 100;
        // 如果评分地狱0.4则判定此次任务失败
        if (rate >= 0.4) {
            taskAccomplishSuccess = true;
        }
        return taskAccomplishSuccess;
    }

    public static boolean insertMonitorInfo(MonitorInfo monitorInfo) {
        String sql = "INSERT INTO monitor_info (user_id, task_id, task_phone_use_count, task_begin_time, task_end_time, task_out_of_range_time, task_screen_on_time, task_screen_off_time, task_screen_on_attention_time,task_delay_time)" +
                " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);\n";
        DbUtils.update(effectRow -> mEffectRow = effectRow,
                sql,
                UserLab.getCurrentUser().getId(),
                monitorInfo.getTaskId(),
                monitorInfo.getMonitorPhoneUseCount(),
                monitorInfo.getTaskBeginTime(),
                monitorInfo.getTaskEndTime(),
                monitorInfo.getTaskOutOfRangeTime(),
                monitorInfo.getMonitorTaskScreenOnTime(),
                monitorInfo.getMonitorTaskScreenOffTime(),
                monitorInfo.getMonitorScreenOnAttentionSpan(),
                monitorInfo.getTaskDelayTime());
        if (mEffectRow <= 0)
            return false;
        else
            return true;
    }


    public static List<MonitorInfo> getMonitorInfoWith(String sql, Object... args) {
        List<MonitorInfo> monitorInfoList = new ArrayList<>();
        DbUtils.query(resultSet -> {
            if (resultSet.next()) {
                MonitorInfo monitorInfo = new MonitorInfo();
                monitorInfo.setId(resultSet.getInt("info_id"));
                monitorInfo.setTaskId(resultSet.getInt("task_id"));
                monitorInfo.setMonitorPhoneUseCount(resultSet.getInt("task_phone_use_count"));
                monitorInfo.setTaskBeginTime(resultSet.getString("task_begin_time"));
                monitorInfo.setTaskEndTime(resultSet.getString("task_end_time"));
                monitorInfo.setTaskOutOfRangeTime(resultSet.getInt("task_out_of_range_time"));
                monitorInfo.setMonitorTaskScreenOnTime(resultSet.getInt("task_screen_on_time"));
                monitorInfo.setMonitorTaskScreenOffTime(resultSet.getInt("task_screen_off_time"));
                monitorInfo.setMonitorScreenOnAttentionSpan(resultSet.getInt("task_screen_on_attention_time"));
                monitorInfoList.add(monitorInfo);
            }
        }, sql, args);
        return monitorInfoList;
    }

    public List<MonitorInfo> getMonitorInfoListDay(String dataStr) {
        Log.d(TAG, "dataStr: " + dataStr);
        List<MonitorInfo> infoList = new ArrayList<>();
        int dayGiven = Integer.parseInt(dataStr.substring(8, 10));
        Log.d(TAG, "dayGiven: " + dayGiven);
        for (int i = 0; i < mMonitorInfoList.size(); i++) {
            String day = mMonitorInfoList.get(i).getTaskBeginTime().substring(8, 10);
            Log.d(TAG, "day: " + day);

            if (Integer.parseInt(day) == dayGiven)
                infoList.add(mMonitorInfoList.get(i));
        }
        Log.d(TAG, infoList.size() + "");

        return infoList;
    }

    public List<MonitorInfo> getMonitorInfoListWeek(String dataStr) {
        Log.d(TAG, dataStr);
        List<MonitorInfo> infoList = new ArrayList<>();
        String weekRange = getFirstAndLastOfWeek(dataStr);
        String[] range = weekRange.split("-");
        for (int i = 0; i < mMonitorInfoList.size(); i++) {
            String currentTime = mMonitorInfoList.get(i).getTaskBeginTime().substring(0, 10);
            Log.d(TAG, currentTime);
            if (inTheTwoDate(currentTime, range[0], range[1]))
                infoList.add(mMonitorInfoList.get(i));
        }
        Log.d(TAG, infoList.size() + "");

        return infoList;
    }

    /**
     * @return boolean true 表示这个日期在这二个日期之
     * @Description 传入yyyy-MM-dd的String类型的date 2019-06-28
     * @Date 18:13 2019/6/28
     * @Param [m, st, ed] m=判断时间  st开始时间 ed结束日期时间 时间都是yyyy/MM/dd格式
     **/
    public static boolean inTheTwoDate(String m, String st, String ed) {
        int startDay = 0;
        int endDay = 0;
        int mDay = 0;
        try {
            Date dateStart = sDateFormat.parse(st);
            Date datEnd = sDateFormat.parse(ed);
            Date mDate = sDateFormat.parse(m);

            startDay = (int) (dateStart.getTime() / 1000);
            endDay = (int) (datEnd.getTime() / 1000);
            mDay = (int) (mDate.getTime() / 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (startDay <= mDay && mDay <= endDay) {
            return true;
        } else {
            return false;
        }
    }


    public List<MonitorInfo> getMonitorInfoListMonth(String dataStr) {
        List<MonitorInfo> infoList = new ArrayList<>();
        String monthRange = getFirstAndLastOfMonth(dataStr);
        String[] range = monthRange.split("-");
        Log.d(TAG, "monthRange: " + monthRange);
        for (int i = 0; i < mMonitorInfoList.size(); i++) {
            String currentTime = mMonitorInfoList.get(i).getTaskBeginTime().substring(0, 10);
            Log.d(TAG, "currentTime: " + currentTime);
            if (inTheTwoDate(currentTime, range[0], range[1]))
                infoList.add(mMonitorInfoList.get(i));
        }
        Log.d(TAG, infoList.size() + "");

        return infoList;
    }

    /**
     * 获取指定日期所在周的第一天和最后一天,用-连接
     *
     * @param dataStr
     * @return
     * @throws ParseException
     */
    public static String getFirstAndLastOfMonth(String dataStr) {
        Log.d(TAG, "getFirstAndLastOfMonth: " + dataStr);
        //获取当前月第一天：
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(sDateFormat.parse(dataStr));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        c.add(Calendar.MONTH, 0);
        c.set(Calendar.DAY_OF_MONTH, 1);//设置为1号,当前日期既为本月第一天
        String first = sDateFormat.format(c.getTime());

        //获取当前月最后一天
        Calendar ca = Calendar.getInstance();
        try {
            ca.setTime(sDateFormat.parse(dataStr));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        ca.set(Calendar.DAY_OF_MONTH, ca.getActualMaximum(Calendar.DAY_OF_MONTH));
        String last = sDateFormat.format(ca.getTime());
        return first + "-" + last;
    }

    public static String getFirstAndLastOfWeek(String dataStr) {
        Calendar cal = Calendar.getInstance();

        try {
            cal.setTime(sDateFormat.parse(dataStr));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        int d = 0;
        if (cal.get(Calendar.DAY_OF_WEEK) == 1) {
            d = -6;
        } else {
            d = 2 - cal.get(Calendar.DAY_OF_WEEK);
        }
        cal.add(Calendar.DAY_OF_WEEK, d);
        // 所在周开始日期
        String data1 = sDateFormat.format(cal.getTime());
        cal.add(Calendar.DAY_OF_WEEK, 6);
        // 所在周结束日期
        String data2 = sDateFormat.format(cal.getTime());
        return data1 + "-" + data2;
    }

}
