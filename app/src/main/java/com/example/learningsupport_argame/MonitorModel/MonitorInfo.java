package com.example.learningsupport_argame.MonitorModel;

public class MonitorInfo {
    public static final String PHONE_TOTAL_TIME = "mMonitorPhoneTotalTime";
    public static final String TASK_SCREEN_ON_TIME = "mMonitorTaskScreenOnTime";
    public static final String ATTENTION_TIME = "mMonitorScreenOnAttentionSpan";
    public static final String PHONE_USE_COUNT = "mMonitorPhoneUseCount";
    public static final String TASK_REMANDING_TIME = "mRemainingTime";


    private int mMonitorLevel; // 监督等级

    private int mMonitorTaskScreenOnTime; //任务过程中手机亮屏时间

    private int mMonitorTaskScreenOffTime; //任务过程中手机非亮屏时间

    private int mMonitorScreenOnAttentionSpan; //亮屏过程中专注时间

    private int mMonitorScreenOnInattentionSpan; //亮屏过程中不专注时间

    private int mMonitorPhoneUseCount;//任务过程手机使用次数

    public int getMonitorLevel() {
        return mMonitorLevel;
    }

    public void setMonitorLevel(int monitorLevel) {
        mMonitorLevel = monitorLevel;
    }

    public int getMonitorTaskScreenOnTime() {
        return mMonitorTaskScreenOnTime;
    }

    public void setMonitorTaskScreenOnTime(int monitorTaskScreenOnTime) {
        mMonitorTaskScreenOnTime = monitorTaskScreenOnTime;
    }

    public int getMonitorTaskScreenOffTime() {
        return mMonitorTaskScreenOffTime;
    }

    public void setMonitorTaskScreenOffTime(int monitorTaskScreenOffTime) {
        mMonitorTaskScreenOffTime = monitorTaskScreenOffTime;
    }

    public int getMonitorScreenOnAttentionSpan() {
        return mMonitorScreenOnAttentionSpan;
    }

    public void setMonitorScreenOnAttentionSpan(int monitorScreenOnAttentionSpan) {
        mMonitorScreenOnAttentionSpan = monitorScreenOnAttentionSpan;
    }

    public int getMonitorScreenOnInattentionSpan() {
        return mMonitorTaskScreenOnTime - mMonitorScreenOnAttentionSpan;
    }

    public void setMonitorScreenOnInattentionSpan(int monitorScreenOnInattentionSpan) {
        mMonitorScreenOnInattentionSpan = monitorScreenOnInattentionSpan;
    }

    public int getMonitorPhoneUseCount() {
        return mMonitorPhoneUseCount;
    }

    public void setMonitorPhoneUseCount(int monitorPhoneUseCount) {
        mMonitorPhoneUseCount = monitorPhoneUseCount;
    }
}
