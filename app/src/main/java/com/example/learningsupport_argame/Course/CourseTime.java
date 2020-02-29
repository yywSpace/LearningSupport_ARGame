package com.example.learningsupport_argame.Course;

import android.widget.TextView;

import org.w3c.dom.Text;

class CourseTime {

    private int mId;
    // 课程ID
    private int mCourseId;
    // 周几的课程
    private String mWeek;
    // 开始的节数
    private int mStartTime;
    // 结束的节数
    private int mEndTime;

    private TextView mTimeTextView;

    public CourseTime() {

    }

    public CourseTime(String week, int startTime, int endTime) {
        mWeek = week;
        mStartTime = startTime;
        mEndTime = endTime;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public int getCourseId() {
        return mCourseId;
    }

    public void setCourseId(int courseId) {
        mCourseId = courseId;
    }

    public String getWeek() {
        return mWeek;
    }

    public void setWeek(String week) {
        mWeek = week;
    }

    public int getStartTime() {
        return mStartTime;
    }

    public void setStartTime(int startTime) {
        mStartTime = startTime;
    }

    public int getEndTime() {
        return mEndTime;
    }

    public void setEndTime(int endTime) {
        mEndTime = endTime;
    }

    public TextView getTimeTextView() {
        return mTimeTextView;
    }

    public void setTimeTextView(TextView timeTextView) {
        mTimeTextView = timeTextView;
    }
}
