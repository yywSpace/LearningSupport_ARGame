package com.example.learningsupport_argame.Course;

import android.widget.TextView;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class CourseTime implements Serializable {
    private boolean isEnable = true;
    // 周几的课程
    private String mWeek;
    // 开始的节数
    private int mStartTime;
    // 结束的节数
    private int mEndTime;

    private TextView mTimeTextView;

    @NonNull
    @Override
    public String toString() {
        return String.format("%s-%d-%d-%b", mWeek, mStartTime, mEndTime, isEnable);
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

    public boolean isEnable() {
        return isEnable;
    }

    public void setEnable(boolean enable) {
        isEnable = enable;
    }
}
