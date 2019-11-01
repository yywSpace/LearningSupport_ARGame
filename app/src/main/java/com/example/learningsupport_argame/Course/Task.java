package com.example.learningsupport_argame.Course;

public class Task {

    private String mTaskName;  //任务名

    private String mTaskContent; //任务内容

    private String mTaskType;  //任务类型：自身任务，好友间任务，社团任务，一般任务

    private String mTaskStatus;  //任务状态：执行中，普通

    private String mTaskNotification;  //任务是否提醒

    private String mTaskParticipant;  //任务参与人员

    private String mTaskLocation;   // 任务地点

    private String mTaskStartAt;  // 任务开始时间

    private String mTaskEndIn;  //任务结束时间





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



    public String getTaskNotification() {

        return mTaskNotification;

    }



    public void setTaskNotification(String taskNotification) {

        mTaskNotification = taskNotification;

    }



    public String getTaskParticipant() {

        return mTaskParticipant;

    }



    public void setTaskParticipant(String taskParticipant) {

        mTaskParticipant = taskParticipant;

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

    public String getTaskLocation() {
        return mTaskLocation;
    }

    public void setTaskLocation(String taskLocation) {
        mTaskLocation = taskLocation;
    }
}
