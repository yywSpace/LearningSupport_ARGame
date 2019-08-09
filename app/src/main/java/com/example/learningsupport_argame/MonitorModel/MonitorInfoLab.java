package com.example.learningsupport_argame.MonitorModel;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class MonitorInfoLab {
    private static String TAG = MonitorInfoLab.class.getSimpleName();
    private static MonitorInfoLab sMMonitorInfoLab;
    private List<MonitorInfo> mMonitorInfoList;

    private MonitorInfoLab() {
        mMonitorInfoList = new ArrayList<>();
        MonitorInfo monitorInfo = new MonitorInfo();
        monitorInfo.setMonitorPhoneUseCount(10);
        monitorInfo.setMonitorScreenOnAttentionSpan(10);
        monitorInfo.setMonitorTaskScreenOnTime(100);
        monitorInfo.setTaskTotalTime(200);
        mMonitorInfoList.add(monitorInfo);
        monitorInfo = new MonitorInfo();
        monitorInfo.setMonitorPhoneUseCount(20);
        monitorInfo.setMonitorScreenOnAttentionSpan(20);
        monitorInfo.setMonitorTaskScreenOnTime(100);
        monitorInfo.setTaskTotalTime(300);
        mMonitorInfoList.add(monitorInfo);
        monitorInfo = new MonitorInfo();
        monitorInfo.setMonitorPhoneUseCount(30);
        monitorInfo.setMonitorScreenOnAttentionSpan(30);
        monitorInfo.setMonitorTaskScreenOnTime(100);
        monitorInfo.setTaskTotalTime(200);
        mMonitorInfoList.add(monitorInfo);
    }


    public static MonitorInfoLab get() {
        if (sMMonitorInfoLab == null)
            sMMonitorInfoLab = new MonitorInfoLab();
        return sMMonitorInfoLab;
    }

    public List<MonitorInfo> getMonitorInfoList() {
        return mMonitorInfoList;
    }

    public MonitorInfo getMonitorInfo(int id) {
        return mMonitorInfoList.get(id);
    }

    public List<MonitorInfo> getMonitorInfoListToday() {
        return mMonitorInfoList.subList(0,1);
    }

    public List<MonitorInfo> getMonitorInfoListWeek() {
        return mMonitorInfoList.subList(0,2);
    }

    public List<MonitorInfo> getMonitorInfoListMonth() {
        return mMonitorInfoList;
    }

}
