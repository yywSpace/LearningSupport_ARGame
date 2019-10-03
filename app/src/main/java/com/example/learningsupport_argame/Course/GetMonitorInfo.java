package com.example.learningsupport_argame.Course;

import android.database.Cursor;
import android.util.Log;

import org.litepal.LitePal;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class GetMonitorInfo {

    Calendar calendar = Calendar.getInstance();
    int year = calendar.get(Calendar.YEAR);
    int month = calendar.get(Calendar.MONTH) + 1;
    int day = calendar.get(Calendar.DAY_OF_MONTH);
    int week = calendar.get(Calendar.DAY_OF_WEEK);


    public String[] getDayInfo() {

        String[] str = new String[3];

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date currentDate = null;
        try {
            currentDate = dateFormat.parse(year + "-" + month + "-" + day);
        } catch (ParseException e) {
            e.printStackTrace();
            Log.d("GetMonitorInfo", "当前日期获取失败");
        }
        String currentDateStr = dateFormat.format(currentDate);

        long dayUseTimeSum = 0;
        long dayConcentrateTimeSum = 0;
        int dayUseCountSum = 0;

        List<MonitorCourseInfo> monitorCourseInfoList = LitePal.where("monitorDate = ?", currentDateStr).find(MonitorCourseInfo.class);
        for (MonitorCourseInfo monitorCourseInfo : monitorCourseInfoList) {
            dayUseTimeSum += monitorCourseInfo.getUseTime();
            dayConcentrateTimeSum += monitorCourseInfo.getConcentrateTime();
            dayUseCountSum += monitorCourseInfo.getUseCount();
        }
        str[0] = String.valueOf(dayUseTimeSum);
        str[1] = String.valueOf(dayConcentrateTimeSum);
        str[2] = String.valueOf(dayUseCountSum);

        return str;


    }

    public void get() {

        Date startDate = null;
        Date endDate = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date currentDate = null;
        try {
            currentDate = dateFormat.parse(year + "-" + month + "-" + day);
        } catch (ParseException e) {
            e.printStackTrace();
            Log.d("GetMonitorInfo", "当前日期获取失败");
        }

        String stt = dateFormat.format(currentDate);
       // Log.d("GetMonitorInfodafsssfd", stt);


    }

    public String[] getWeekInfo() {

        String str[] = new String[3];
        Date startDate = null;
        Date endDate = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date currentDate = null;
        try {
            currentDate = dateFormat.parse(year + "-" + month + "-" + day);
        } catch (ParseException e) {
            e.printStackTrace();
            Log.d("GetMonitorInfo", "当前日期获取失败");
        }

        String stt = dateFormat.format(currentDate);
        //Log.d("GetMonitorInfodafsssfd", stt);


        switch (week) {
            case 2:
                startDate = currentDate;
                endDate = new Date(currentDate.getTime() + 6 * 24 * 60 * 60);
                break;//一
            case 3:
                startDate = new Date(currentDate.getTime() - 24 * 60 * 60);
                endDate = new Date(currentDate.getTime() + 5 * 24 * 60 * 60);
                break;
            case 4:
                startDate = new Date(currentDate.getTime() - 2 * 24 * 60 * 60);
                endDate = new Date(currentDate.getTime() + 4 * 24 * 60 * 60);
                break;
            case 5:
                startDate = new Date(currentDate.getTime() - 3 * 24 * 60 * 60);
                endDate = new Date(currentDate.getTime() + 3 * 24 * 60 * 60);
                break;
            case 6:
                startDate = new Date(currentDate.getTime() - 4 * 24 * 60 * 60);
                endDate = new Date(currentDate.getTime() + 2 * 24 * 60 * 60);
                break;
            case 7:
                startDate = new Date(currentDate.getTime() - 5 * 24 * 60 * 60);
                endDate = new Date(currentDate.getTime() + 24 * 60 * 60);
                break;//六
            case 1:
                startDate = new Date(currentDate.getTime() - 6 * 24 * 60 * 60);
                endDate = currentDate;
                break;//日
            default:
                break;
        }

        long weekUseTimeSum = 0;
        long weekConcentrateTimeSum = 0;
        int weekUseCountSum = 0;

        String startDateStr = dateFormat.format(startDate);
        String endDateStr = dateFormat.format(endDate);
      //  Log.d("GetMonitorInfodafsssfd", startDateStr);

        List<MonitorCourseInfo> monitorCourseInfoList = LitePal.where("monitorDate >= ? and monitorDate <= ?", startDateStr, endDateStr).find(MonitorCourseInfo.class);
        for (MonitorCourseInfo monitorCourseInfo : monitorCourseInfoList) {
            weekUseTimeSum += monitorCourseInfo.getUseTime();
            weekConcentrateTimeSum += monitorCourseInfo.getConcentrateTime();
            weekUseCountSum += monitorCourseInfo.getUseCount();
        }
//        String content="_____"+monthStr+"___";
//        Cursor cursor = LitePal.findBySQL("select * from MonitorCourseInfo where monitorDate between ? and ?", startDate,endDate);
//        int resultCounts=cursor.getCount();
//        if(resultCounts==0||!cursor.moveToFirst())
//            return null;
//        for(int i=0;i<resultCounts;i++)
//        {
//            monthUseTimeSum += cursor.getInt(cursor.getColumnIndex("usetime"));
//            monthConcentrateTimeSum += cursor.getInt(cursor.getColumnIndex("concentratetime"));//此处列名为小写，否则无法查询到
//            monthUseCountSum += cursor.getInt(cursor.getColumnIndex("usecount"));
//            cursor.moveToNext();
//        }
//        str[0] = String.valueOf(monthUseTimeSum);
//        str[1] = String.valueOf(monthConcentrateTimeSum);
//        str[2] = String.valueOf(monthUseCountSum);

        str[0] = String.valueOf(weekUseTimeSum);
        str[1] = String.valueOf(weekConcentrateTimeSum);
        str[2] = String.valueOf(weekUseCountSum);

        return str;

    }

    public String[] getMonthInfo() {

        String[] str = new String[3];

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date currentDate = null;
        try {
            currentDate = dateFormat.parse(year + "-" + month + "-" + day);
        } catch (ParseException e) {
            e.printStackTrace();
            Log.d("GetMonitorInfo", "当前日期获取失败");
        }
        String currentDateStr = dateFormat.format(currentDate);
        String monthStr = currentDateStr.split("-")[1];

        long monthUseTimeSum = 0;
        long monthConcentrateTimeSum = 0;
        int monthUseCountSum = 0;

//        Cursor cursor = LitePal.findBySQL("select * from news where monitorDate like '_____'?'___'", monthStr);
        String content="_____"+monthStr+"___";
        Cursor cursor = LitePal.findBySQL("select * from MonitorCourseInfo where monitorDate like ?", content);
        int resultCounts=cursor.getCount();
        if(resultCounts==0||!cursor.moveToFirst())
            return null;
        for(int i=0;i<resultCounts;i++)
        {
            monthUseTimeSum += cursor.getInt(cursor.getColumnIndex("usetime"));
            monthConcentrateTimeSum += cursor.getInt(cursor.getColumnIndex("concentratetime"));//此处列名为小写，否则无法查询到
            monthUseCountSum += cursor.getInt(cursor.getColumnIndex("usecount"));
            cursor.moveToNext();
        }
            str[0] = String.valueOf(monthUseTimeSum);
            str[1] = String.valueOf(monthConcentrateTimeSum);
            str[2] = String.valueOf(monthUseCountSum);


//        List<MonitorCourseInfo> monitorCourseInfoList = LitePal.where("monitorDate = ?", currentDateStr).find(MonitorCourseInfo.class);
//        for (MonitorCourseInfo monitorCourseInfo : monitorCourseInfoList) {
//
//        }
        return str;
    }
}
