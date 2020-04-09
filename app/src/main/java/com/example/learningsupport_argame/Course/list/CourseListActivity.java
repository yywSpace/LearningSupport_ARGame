package com.example.learningsupport_argame.Course.list;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.learningsupport_argame.Course.AddCourseActivity;
import com.example.learningsupport_argame.Course.CourseLab;
import com.example.learningsupport_argame.Course.CourseMainActivity;
import com.example.learningsupport_argame.Course.CourseSetting;
import com.example.learningsupport_argame.Course.CourseTime;
import com.example.learningsupport_argame.Course.Course;
import com.example.learningsupport_argame.Course.PopupWindow.PromptAdapter;
import com.example.learningsupport_argame.R;
import com.example.learningsupport_argame.Task.Task;
import com.example.learningsupport_argame.UserManagement.UserLab;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

// TODO: 20-4-9 适配监督 
public class CourseListActivity extends AppCompatActivity {
    private List<Course> mCourseList = new ArrayList<>();
    private List<Course> mAllCourseList = new ArrayList<>();
    private Toolbar mToolbar;
    private MyListView myListView;
    private CourseListAdapter mCourseListAdapter;
    private int mCurrentWeek;
    private int mSchoolWeek;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.course_listview_content);
        // 设置工具栏
        mToolbar = findViewById(R.id.course_list_toolbar);
        setSupportActionBar(mToolbar);
        ActionBar ab = getSupportActionBar();
        ab.setTitle("课程列表");
        ab.setDisplayHomeAsUpEnabled(true);

        mCurrentWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        mSchoolWeek = getCurrentSchoolWeek();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
        }
        initData();
        myListView = findViewById(R.id.my_list);
        mCourseListAdapter = new CourseListAdapter(mCourseList, this);
        myListView.setAdapter(mCourseListAdapter);
        myListView.setOnItemClickListener((parent, view, position, id) -> {
            Course course = (Course) mCourseListAdapter.getItem(position);
            CourseTime courseTime = course.getCourseTime();
            View popView = View.inflate(CourseListActivity.this, R.layout.course_show_dialog_layout, null);
            TextView classroom = popView.findViewById(R.id.show_course_classroom);
            TextView time = popView.findViewById(R.id.show_course_number);
            TextView teacher = popView.findViewById(R.id.show_course_teacher);
            TextView week = popView.findViewById(R.id.show_course_week_number);
            classroom.setText(course.getClassroom());
            time.setText(courseTime.getWeek() + " " + courseTime.getStartTime() + "-" + courseTime.getEndTime() + "节");
            teacher.setText(course.getTeacher());
            week.setText(course.getStartWeek() + "-" + course.getEndWeek() + "周");
            AlertDialog alertDialog = new AlertDialog.Builder(CourseListActivity.this)
                    .setView(popView)
                    .setTitle(course.getName())
                    .setPositiveButton("编辑", (dialog, which) -> {
                        Intent intent = new Intent(CourseListActivity.this, AddCourseActivity.class);
                        intent.putExtra(CourseMainActivity.CURRENT_EDIT_COURSE_NAME, course.getName());
                        startActivity(intent);
                    })
                    .setNegativeButton("取消", null)
                    .create();
            alertDialog.show();
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.course_list_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.course_list_clear_all:
                removeAllCourseLDialog();
                return true;
            case R.id.course_list_all_course:
                getSupportActionBar().setTitle("课程列表");
                mCourseList.clear();
                mCourseList.addAll(mAllCourseList);
                mCourseListAdapter.notifyDataSetChanged();
                return true;
            case R.id.course_list_valid_course:
                getSupportActionBar().setTitle("有效课程");
                List<Course> mValidCourse = getValidCourses(mAllCourseList);
                mCourseList.clear();
                mCourseList.addAll(mValidCourse);
                mCourseListAdapter.notifyDataSetChanged();
                return true;
            case R.id.course_list_monitored_course:
                getSupportActionBar().setTitle("监督课程");
                List<Course> validCourse = getValidCourses(mAllCourseList);
                List<Course> courses = validCourse.stream().filter(Course::isMonitor).collect(Collectors.toList());
                mCourseList.clear();
                mCourseList.addAll(courses);
                mCourseListAdapter.notifyDataSetChanged();
                return true;
            case R.id.course_list_today_course:
                getSupportActionBar().setTitle("当前课程");
                String[] weekArray = {"周一", "周二", "周三", "周四", "周五", "周六", "周日"};
                List<Course> mTodayCourse = new ArrayList<>();
                // 获取课程列表
                mAllCourseList.stream()
                        .filter(course ->
                                course.getStartWeek() <= mSchoolWeek && course.getEndWeek() >= mSchoolWeek)
                        .forEach(course -> {
                            for (CourseTime ct : course.getTimes()) {
                                if (ct.getWeek().equals(weekArray[mCurrentWeek])) {
                                    Course eCourse = new Course(course);
                                    eCourse.setCourseTime(ct);
                                    mTodayCourse.add(eCourse);
                                }
                            }
                        });
                mCourseList.clear();
                mCourseList.addAll(mTodayCourse);
                mCourseListAdapter.notifyDataSetChanged();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @TargetApi(19)
    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    private void initData() {
        new Thread(() -> {
            CourseLab.getAllCourse(UserLab.getCurrentUser().getId());
            // 根据每课的每节构造列表
            for (Course course : CourseLab.sCourseList) {
                for (CourseTime time : course.getTimes()) {
                    Course eCourse = new Course(course);
                    eCourse.setCourseTime(time);
                    mCourseList.add(eCourse);
                }
            }
            mAllCourseList = new ArrayList<>(mCourseList);
            runOnUiThread(() -> mCourseListAdapter.notifyDataSetChanged());
        }).start();
    }

    public void removeAllCourseLDialog() {
        PromptAdapter.Builder builder = new PromptAdapter.Builder(CourseListActivity.this);
        builder.setTitle("提示");
        builder.setContent("确定要删除所有课程信息吗");
        builder.setRight("确定", (dialogInterface, i) -> {
            dialogInterface.dismiss();
            mCourseList.clear();
            mCourseListAdapter.notifyDataSetChanged();
            new Thread(CourseLab::deleteAllCourse).start();
            Toast.makeText(CourseListActivity.this, "课程已全部清空", Toast.LENGTH_SHORT).show();
        });
        builder.setLeft("取消", (dialogInterface, i) -> dialogInterface.dismiss());
        PromptAdapter dialog = builder.create();
        dialog.show();
    }


    List<Course> getValidCourses(List<Course> courseList) {
        List<Course> validCourses = new ArrayList<>();
        courseList.stream()
                .filter(course ->
                        course.getStartWeek() <= mSchoolWeek && course.getEndWeek() >= mSchoolWeek)
                .forEach(course -> {
                    for (CourseTime ct : course.getTimes()) {
                        switch (course.getWeekStyle()) {
                            case "单周":
                                if (mCurrentWeek % 2 != 0) {
                                    Course eCourse = new Course(course);
                                    eCourse.setCourseTime(ct);
                                    validCourses.add(eCourse);
                                }
                                break;
                            case "双周":
                                if (mCurrentWeek % 2 == 0) {
                                    Course eCourse = new Course(course);
                                    eCourse.setCourseTime(ct);
                                    validCourses.add(eCourse);
                                }
                                break;
                            case "单双周":
                                Course eCourse = new Course(course);
                                eCourse.setCourseTime(ct);
                                validCourses.add(eCourse);
                                break;
                        }
                    }
                });
        return validCourses;
    }

    int getCurrentSchoolWeek() {
        String schoolOpenDate = CourseSetting.SCHOOL_OPEN_DATE;
        if (schoolOpenDate == null) {
            return -1;
        } else {
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
            Date schoolOpensDate = null;
            try {
                schoolOpensDate = dateFormat.parse(schoolOpenDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            Calendar currentDate = Calendar.getInstance();
            long currentTime = currentDate.getTime().getTime();
            long schoolOpensTime = schoolOpensDate.getTime();

            int currentWeek = currentDate.get(Calendar.DAY_OF_WEEK);

            int num = (currentWeek == 1 ? 7 : currentWeek - 1);
            long time = currentTime - schoolOpensTime;
            int day = (int) (time / 1000 / 60 / 60 / 24);
            if (time < 0)
                return -1; // 假期
            else {
                int k = (day - (7 - num + 1));
                if (k >= 1) {
                    return ((k / 7) + 2);
                } else {
                    return 1;
                }
            }
        }
    }
}

