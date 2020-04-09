package com.example.learningsupport_argame.Course.list;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.learningsupport_argame.Course.Course;
import com.example.learningsupport_argame.Course.CourseLab;
import com.example.learningsupport_argame.Course.CourseTime;
import com.example.learningsupport_argame.R;

import java.util.List;

public class CourseListAdapter extends BaseAdapter {
    private List<Course> mCourseList;
    private Context mContext;

    public CourseListAdapter(List<Course> data, Context mContext) {
        this.mCourseList = data;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        if (mCourseList != null) {
            return mCourseList.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (mCourseList != null) {
            return mCourseList.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View contentView, ViewGroup parent) {
        ViewHolder holder;
        if (contentView == null) {
            holder = new ViewHolder();
            contentView = LayoutInflater.from(mContext).inflate(R.layout.course_listview_layout, parent, false);
            holder.courseName = contentView.findViewById(R.id.course_name);
            holder.courseTime = contentView.findViewById(R.id.course_time);
            holder.courseClassroom = contentView.findViewById(R.id.course_classroom);
            holder.courseMonitorIcon = contentView.findViewById(R.id.course_monitor_icon);
            holder.delete = contentView.findViewById(R.id.delete);
            contentView.setTag(holder);
        } else {
            holder = (ViewHolder) contentView.getTag();
        }
        Course course = mCourseList.get(position);
        holder.courseName.setText(course.getName());
        CourseTime courseTime = course.getCourseTime();
        holder.courseTime.setText(courseTime.getWeek() + " " + courseTime.getStartTime() + "-" + courseTime.getEndTime() + "节");
        holder.courseClassroom.setText(course.getClassroom());
        if (course.isMonitor())
            holder.courseMonitorIcon.setVisibility(View.VISIBLE);
        else
            holder.courseMonitorIcon.setVisibility(View.INVISIBLE);

        final MyItemLayout finalContentView = (MyItemLayout) contentView;

        holder.delete.setOnClickListener(v -> {
            String courseName = mCourseList.get(position).getName();
            deleteFromDb(courseName);
            mCourseList.remove(position);
            finalContentView.smoothCloseMenu();
            notifyDataSetChanged();
            Toast.makeText(mContext, "已删除", Toast.LENGTH_SHORT).show();
        });
        return contentView;
    }

    public void deleteFromDb(String courseName) {
        new Thread(() -> CourseLab.deleteCourseFromName(courseName)).start();
    }

    private static class ViewHolder {
        TextView courseName;
        TextView courseTime;
        TextView courseClassroom;
        ImageView courseMonitorIcon;
        TextView delete;
    }
}

