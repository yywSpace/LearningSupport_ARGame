package com.example.learningsupport_argame.MonitorModel;

import com.example.learningsupport_argame.Task.Task;

import java.io.Serializable;

public class MonitorInfo implements Serializable {
    public static final String MONITOR_INFO_PREFS_NAME = "mMonitorInfoPrefsName";
    public static final String MONITOR_TASK_ID = "mMonitorTaskId";
    public static final String MONITOR_INFO = "mMonitorInfo";
    public static final String TASK_SCREEN_ON_TIME = "mMonitorTaskScreenOnTime";
    public static final String TASK_SCREEN_OFF_TIME = "mMonitorTaskScreenOffTime";
    public static final String ATTENTION_TIME = "mMonitorScreenOnAttentionSpan";
    public static final String PHONE_USE_COUNT = "mMonitorPhoneUseCount";
    public static final String TASK_REMANDING_TIME = "mRemainingTime";
    public static final String TASK_OUT_OF_RANGE_TIME = "mTaskOutOfRangeTime";
    public static final String TASK_DELAY_TIME = "mTaskDelayTime";

    private int mId;
    private int mTaskId;
    private String mTaskBeginTime;
    private String mTaskEndTime;
    private int mTaskOutOfRangeTime;
    private float mMonitorTaskScreenOnTime; //任务过程中手机亮屏时间
    private int mMonitorTaskScreenOffTime;
    private float mMonitorScreenOnAttentionSpan;
    private float mMonitorPhoneUseCount;//任务过程手机使用次数
    private int mTaskDelayTime;
    private Task mTask;

    /**
     * 亮屏时间
     *
     * @return
     */
    public float getMonitorTaskScreenOnTime() {
        return mMonitorTaskScreenOnTime;
    }

    public void setMonitorTaskScreenOnTime(float monitorTaskScreenOnTime) {
        mMonitorTaskScreenOnTime = monitorTaskScreenOnTime;
    }

    /**
     * 监督总时间
     *
     * @return
     */
    public int getTaskTotalTime() {
        return (int) TimeUtils.remainingTime(getTaskBeginTime(), getTaskEndTime(), "yyyy-MM-dd HH:mm");
    }

    /**
     * 息屏时间
     *
     * @return
     */
    public int getMonitorTaskScreenOffTime() {
        return mMonitorTaskScreenOffTime;
    }

    public void setMonitorTaskScreenOffTime(int monitorTaskScreenOffTime) {
        mMonitorTaskScreenOffTime = monitorTaskScreenOffTime;
    }

    /**
     * 亮屏过程中专注时间
     *
     * @return
     */
    public float getMonitorScreenOnAttentionSpan() {
        return mMonitorScreenOnAttentionSpan;
    }

    public void setMonitorScreenOnAttentionSpan(int monitorScreenOnAttentionSpan) {
        mMonitorScreenOnAttentionSpan = monitorScreenOnAttentionSpan;
    }

    /**
     * 亮屏过程中非专注时间
     *
     * @return
     */
    public float getMonitorScreenOnInattentionSpan() {
        return mMonitorTaskScreenOnTime - mMonitorScreenOnAttentionSpan;
    }

    /**
     * 任务过程中手机使用次数
     *
     * @return
     */
    public float getMonitorPhoneUseCount() {
        return mMonitorPhoneUseCount;
    }

    public void setMonitorPhoneUseCount(int monitorPhoneUseCount) {
        mMonitorPhoneUseCount = monitorPhoneUseCount;
    }

    /**
     * 任务开始时间
     *
     * @return
     */
    public String getTaskBeginTime() {
        return mTaskBeginTime;
    }

    public void setTaskBeginTime(String taskBeginTime) {
        mTaskBeginTime = taskBeginTime;
    }

    /**
     * 任务结束时间
     *
     * @return
     */
    public String getTaskEndTime() {
        return mTaskEndTime;
    }

    public void setTaskEndTime(String taskEndTime) {
        mTaskEndTime = taskEndTime;
    }

    /**
     * 总专注时间 = 亮屏过程中专注时间+灭屏时间
     *
     * @return
     */
    public float getMonitorAttentionTime() {
        return getMonitorTaskScreenOffTime() + getMonitorScreenOnAttentionSpan();
    }

    /**
     * 任务过程中在任务规定地点之外的时间
     *
     * @return
     */
    public int getTaskOutOfRangeTime() {
        return mTaskOutOfRangeTime;
    }

    public void setTaskOutOfRangeTime(int taskOutOfRangeTime) {
        mTaskOutOfRangeTime = taskOutOfRangeTime;
    }

    public Task getTask() {
        return mTask;
    }

    public void setTask(Task task) {
        mTask = task;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public int getTaskId() {
        return mTaskId;
    }

    public void setTaskId(int taskId) {
        mTaskId = taskId;
    }

    public int getTaskDelayTime() {
        return mTaskDelayTime;
    }

    public void setTaskDelayTime(int taskDelayTime) {
        mTaskDelayTime = taskDelayTime;
    }
}
