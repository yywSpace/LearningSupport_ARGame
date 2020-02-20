package com.example.learningsupport_argame.Course;

public class MonitorCourse {

    public String getmCourseName() {
        return mCourseName;
    }

    public void setmCourseName(String mCourseName) {
        this.mCourseName = mCourseName;
    }

    public String getmCourseContent() {
        return mCourseContent;
    }

    public void setmCourseContent(String mCourseContent) {
        this.mCourseContent = mCourseContent;
    }

    public String getmCourseStartAt() {
        return mCourseStartAt;
    }

    public void setmCourseStartAt(String mCourseStartAt) {
        this.mCourseStartAt = mCourseStartAt;
    }

    public String getmCourseEndIn() {
        return mCourseEndIn;
    }

    public void setmCourseEndIn(String mCourseEndIn) {
        this.mCourseEndIn = mCourseEndIn;
    }

    private String mCourseName;  //任务名

    private String mCourseContent; //任务内容

    private String mCourseStartAt;  // 任务开始时间

    private String mCourseEndIn;  //任务结束时间

    public MonitorCourse() {

    }
}
