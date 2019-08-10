package com.example.learningsupport_argame.MonitorModel;

public class MonitorInfo {
    public static final String PHONE_TOTAL_TIME = "mMonitorPhoneTotalTime";
    public static final String TASK_SCREEN_ON_TIME = "mMonitorTaskScreenOnTime";
    public static final String ATTENTION_TIME = "mMonitorScreenOnAttentionSpan";
    public static final String PHONE_USE_COUNT = "mMonitorPhoneUseCount";
    public static final String TASK_REMANDING_TIME = "mRemainingTime";


    // private int mMonitorLevel; // 监督等级
    private String mTaskBeginTime;

    private String mTaskEndTime;

    private float mMonitorTaskScreenOnTime; //任务过程中手机亮屏时间

    //private float mMonitorTaskScreenOffTime; //任务过程中手机非亮屏时间

    private float mMonitorScreenOnAttentionSpan; //亮屏过程中专注时间

    private float mMonitorPhoneUseCount;//任务过程手机使用次数

    public float getMonitorTaskScreenOnTime() {
        return mMonitorTaskScreenOnTime;
    }

    public void setMonitorTaskScreenOnTime(float monitorTaskScreenOnTime) {
        mMonitorTaskScreenOnTime = monitorTaskScreenOnTime;
    }

    public float getTaskTotalTime() {
        return MonitorService.remainingTime(getTaskBeginTime(),getTaskEndTime());
    }



    public float getMonitorTaskScreenOffTime() {
        return getTaskTotalTime() - mMonitorTaskScreenOnTime;
    }


    public float getMonitorScreenOnAttentionSpan() {
        return mMonitorScreenOnAttentionSpan;
    }

    public void setMonitorScreenOnAttentionSpan(int monitorScreenOnAttentionSpan) {
        mMonitorScreenOnAttentionSpan = monitorScreenOnAttentionSpan;
    }

    public float getMonitorScreenOnInattentionSpan() {
        return mMonitorTaskScreenOnTime - mMonitorScreenOnAttentionSpan;
    }


    public float getMonitorPhoneUseCount() {
        return mMonitorPhoneUseCount;
    }

    public void setMonitorPhoneUseCount(int monitorPhoneUseCount) {
        mMonitorPhoneUseCount = monitorPhoneUseCount;
    }

    public String getTaskBeginTime() {
        return mTaskBeginTime;
    }

    public void setTaskBeginTime(String taskBeginTime) {
        mTaskBeginTime = taskBeginTime;
    }

    public String getTaskEndTime() {
        return mTaskEndTime;
    }

    public void setTaskEndTime(String taskEndTime) {
        mTaskEndTime = taskEndTime;
    }
}
