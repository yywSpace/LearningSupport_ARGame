package com.example.learningsupport_argame.MonitorModel;

import android.util.Log;

import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

public class MonitorInfoLab {
    private static String TAG = MonitorInfoLab.class.getSimpleName();
    private static MonitorInfoLab sMMonitorInfoLab;
    private List<MonitorInfo> mMonitorInfoList;

    private MonitorInfoLab() {
        mMonitorInfoList = new ArrayList<>();
        MonitorInfo monitorInfo = new MonitorInfo();
        monitorInfo.setTaskBeginTime("2019/08/01/12:00");
        monitorInfo.setTaskEndTime("2019/08/01/12:50");
        monitorInfo.setMonitorPhoneUseCount(10);
        monitorInfo.setMonitorScreenOnAttentionSpan(100);
        monitorInfo.setMonitorTaskScreenOnTime(100);
        mMonitorInfoList.add(monitorInfo);
        monitorInfo = new MonitorInfo();
        monitorInfo.setTaskBeginTime("2019/08/06/1:00");
        monitorInfo.setTaskEndTime("2019/08/06/2:50");
        monitorInfo.setMonitorPhoneUseCount(20);
        monitorInfo.setMonitorScreenOnAttentionSpan(20);
        monitorInfo.setMonitorTaskScreenOnTime(100);
        mMonitorInfoList.add(monitorInfo);
        monitorInfo = new MonitorInfo();
        monitorInfo.setTaskBeginTime("2019/08/07/4:00");
        monitorInfo.setTaskEndTime("2019/08/07/5:30");
        monitorInfo.setMonitorPhoneUseCount(30);
        monitorInfo.setMonitorScreenOnAttentionSpan(100);
        monitorInfo.setMonitorTaskScreenOnTime(100);
        mMonitorInfoList.add(monitorInfo);

        monitorInfo = new MonitorInfo();
        monitorInfo.setTaskBeginTime("2019/07/02/1:00");
        monitorInfo.setTaskEndTime("2019/07/02/2:50");
        monitorInfo.setMonitorPhoneUseCount(20);
        monitorInfo.setMonitorScreenOnAttentionSpan(100);
        monitorInfo.setMonitorTaskScreenOnTime(100);
        mMonitorInfoList.add(monitorInfo);
        monitorInfo = new MonitorInfo();
        monitorInfo.setTaskBeginTime("2019/07/03/4:00");
        monitorInfo.setTaskEndTime("2019/07/03/5:30");
        monitorInfo.setMonitorPhoneUseCount(30);
        monitorInfo.setMonitorScreenOnAttentionSpan(30);
        monitorInfo.setMonitorTaskScreenOnTime(100);
        mMonitorInfoList.add(monitorInfo);
        monitorInfo = new MonitorInfo();
        monitorInfo.setTaskBeginTime("2019/07/05/6:00");
        monitorInfo.setTaskEndTime("2019/07/05/7:30");
        monitorInfo.setMonitorPhoneUseCount(30);
        monitorInfo.setMonitorScreenOnAttentionSpan(30);
        monitorInfo.setMonitorTaskScreenOnTime(100);
        mMonitorInfoList.add(monitorInfo);
    }


    public static MonitorInfoLab get() {
        if (sMMonitorInfoLab == null)
            sMMonitorInfoLab = new MonitorInfoLab();
        return sMMonitorInfoLab;
    }


    public MonitorInfo getMonitorInfo(int id) {
        return mMonitorInfoList.get(id);
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
        Log.d(TAG, infoList.size()+"");

        return infoList;
    }

    /**
     * @return boolean true 表示这个日期在这二个日期之
     * @Description 传入yyyy-MM-dd的String类型的date 2019-06-28
     * @Date 18:13 2019/6/28
     * @Param [m, st, ed] m=判断时间  st开始时间 ed结束日期时间 时间都是yyyy/MM/dd格式
     **/
    public static boolean inTheTwoDate(String m, String st, String ed) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        int startDay = 0;
        int endDay = 0;
        int mDay = 0;
        try {
            Date dateStart = format.parse(st);
            Date datEnd = format.parse(ed);
            Date mDate = format.parse(m);

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
        for (int i = 0; i < mMonitorInfoList.size(); i++) {
            String currentTime = mMonitorInfoList.get(i).getTaskBeginTime().substring(0, 10);
            if (inTheTwoDate(currentTime, range[0], range[1]))
                infoList.add(mMonitorInfoList.get(i));
        }
        return infoList;
    }
    /**
     * 获取指定日期所在周的第一天和最后一天,用-连接
     * @param dataStr
     * @return
     * @throws ParseException
     */
    public static String getFirstAndLastOfMonth(String dataStr){
        //获取当前月第一天：
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(new SimpleDateFormat("yyyy/MM/dd").parse(dataStr));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        c.add(Calendar.MONTH, 0);
        c.set(Calendar.DAY_OF_MONTH,1);//设置为1号,当前日期既为本月第一天
        String first = new SimpleDateFormat("yyyy/MM/dd").format(c.getTime());

        //获取当前月最后一天
        Calendar ca = Calendar.getInstance();
        ca.set(Calendar.DAY_OF_MONTH, ca.getActualMaximum(Calendar.DAY_OF_MONTH));
        String last = new SimpleDateFormat("yyyy/MM/dd").format(ca.getTime());
        return first+"-"+last;
    }

    public static String getFirstAndLastOfWeek(String dataStr) {
        Calendar cal = Calendar.getInstance();

        try {
            cal.setTime(new SimpleDateFormat("yyyy/MM/dd").parse(dataStr));
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
        String data1 = new SimpleDateFormat("yyyy/MM/dd").format(cal.getTime());
        cal.add(Calendar.DAY_OF_WEEK, 6);
        // 所在周结束日期
        String data2 = new SimpleDateFormat("yyyy/MM/dd").format(cal.getTime());
        return data1 + "-" + data2;
    }

}
