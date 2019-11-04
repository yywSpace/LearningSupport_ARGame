package com.example.learningsupport_argame.task;

import com.example.learningsupport_argame.UserManagement.User;

import java.util.List;

public class Task {
    private int mTaskId;
    private int mUserId;
    private String mTaskName;  //任务名
    private String mTaskContent; //任务内容
    private String mTaskReleaseFor; // 任务发布的主体
    private String mTaskType;  //任务类型：自身任务，好友间任务，社团任务，一般任务
    private String mTaskStatus;  //任务状态：执行中，未开始，已完成
    private boolean mTaskNotification;  //任务是否提醒
    private List<User> mTaskParticipant;  //任务参与人员
    private String mAccomplishTaskLocation;   // 任务完成所需到达的地点 (地点名称，经纬度)
    private String mTaskStartAt;  // 任务开始时间
    private String mTaskEndIn;  //任务结束时间
    private String mTaskCreateTime; // 任务创建，完成，接取时间

    public Task() {
    }

    public String getTaskName() {
        return mTaskName;
    }

    public void setTaskName(String taskName) {
        mTaskName = taskName;
    }

    public String getTaskContent() {
        return mTaskContent;
    }

    public void setTaskContent(String taskContent) {
        mTaskContent = taskContent;
    }

    public String getTaskType() {
        return mTaskType;
    }

    public void setTaskType(String taskType) {
        mTaskType = taskType;
    }

    public String getTaskStatus() {
        return mTaskStatus;
    }

    public void setTaskStatus(String taskStatus) {
        mTaskStatus = taskStatus;
    }

    public boolean getTaskNotification() {
        return mTaskNotification;
    }

    public void setTaskNotification(boolean taskNotification) {
        mTaskNotification = taskNotification;
    }

    public String getTaskStartAt() {
        return mTaskStartAt;
    }

    public void setTaskStartAt(String taskStartAt) {
        mTaskStartAt = taskStartAt;
    }

    public String getTaskEndIn() {
        return mTaskEndIn;
    }

    public void setTaskEndIn(String taskEndIn) {
        mTaskEndIn = taskEndIn;
    }

    public int getUserId() {
        return mUserId;
    }

    public void setUserId(int userId) {
        mUserId = userId;
    }



    public String getTaskCreateTime() {
        return mTaskCreateTime;
    }

    public void setTaskCreateTime(String taskCreateTime) {
        mTaskCreateTime = taskCreateTime;
    }

    public List<User> getTaskParticipant() {
        return mTaskParticipant;
    }

    public void setTaskParticipant(List<User> taskParticipant) {
        mTaskParticipant = taskParticipant;
    }

    public int getTaskId() {
        return mTaskId;
    }

    public void setTaskId(int taskId) {
        mTaskId = taskId;
    }

    public String getAccomplishTaskLocation() {
        return mAccomplishTaskLocation;
    }

    public void setAccomplishTaskLocation(String accomplishTaskLocation) {
        mAccomplishTaskLocation = accomplishTaskLocation;
    }

    public String getTaskReleaseFor() {
        return mTaskReleaseFor;
    }

    public void setTaskReleaseFor(String taskReleaseFor) {
        mTaskReleaseFor = taskReleaseFor;
    }
}
