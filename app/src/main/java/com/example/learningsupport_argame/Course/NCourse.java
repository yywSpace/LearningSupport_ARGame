package com.example.learningsupport_argame.Course;

import java.util.ArrayList;
import java.util.List;

public class NCourse {
    private int mId;
    private int mUserId;
    private String mName;
    private String mClassroom;
    private List<CourseTime> mTimes = new ArrayList<>();
    private String mTeacher;
    private int mStartWeek;
    private int mEndWeek;
    private String mWeekStyle;

    public NCourse() {

    }

    public NCourse(int userId, String name, String classroom, List<CourseTime> courseTimes, String teacher, int startWeek, int endWeek, String weekStyle) {
        mUserId = userId;
        mName = name;
        mClassroom = classroom;
        mTimes = courseTimes;
        mTeacher = teacher;
        mStartWeek = startWeek;
        mEndWeek = endWeek;
        mWeekStyle = weekStyle;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public int getUserId() {
        return mUserId;
    }

    public void setUserId(int userId) {
        mUserId = userId;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public List<CourseTime> getTimes() {
        return mTimes;
    }

    public void setTimes(List<CourseTime> times) {
        mTimes = times;
    }

    public String getTeacher() {
        return mTeacher;
    }

    public void setTeacher(String teacher) {
        mTeacher = teacher;
    }

    public String getClassroom() {
        return mClassroom;
    }

    public void setClassroom(String classRoom) {
        mClassroom = classRoom;
    }


    public int getStartWeek() {
        return mStartWeek;
    }

    public void setStartWeek(int startWeek) {
        mStartWeek = startWeek;
    }

    public int getEndWeek() {
        return mEndWeek;
    }

    public void setEndWeek(int endWeek) {
        mEndWeek = endWeek;
    }

    public String getWeekStyle() {
        return mWeekStyle;
    }

    public void setWeekStyle(String weekStyle) {
        mWeekStyle = weekStyle;
    }
}
