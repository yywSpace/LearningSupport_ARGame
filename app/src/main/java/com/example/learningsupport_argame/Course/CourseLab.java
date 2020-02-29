package com.example.learningsupport_argame.Course;

import android.util.Log;

import com.example.learningsupport_argame.DbUtils;
import com.example.learningsupport_argame.Task.Task;
import com.example.learningsupport_argame.UserManagement.User;
import com.example.learningsupport_argame.UserManagement.UserLab;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CourseLab {
    public static final String TAG = "CourseLab";

    private static List<NCourse> getCourseWith(String sql, Object... args) {
        List<NCourse> courses = new ArrayList<>();
        DbUtils.query(resultSet -> {
            while (resultSet.next()) {
                NCourse course = new NCourse();
                course.setId(resultSet.getInt("id"));
                course.setUserId(resultSet.getInt("user_id"));
                course.setName(resultSet.getString("name"));
                course.setClassroom(resultSet.getString("classroom"));
                course.setTeacher(resultSet.getString("teacher"));
                course.setStartWeek(resultSet.getInt("start_week"));
                course.setEndWeek(resultSet.getInt("end_week"));
                course.setWeekStyle(resultSet.getString("week_style"));
                String times = resultSet.getString("times");
                Log.d(TAG, "times: " + times);
                String[] timesArray = times.substring(1, times.length() - 1).split(",");
                Arrays.stream(timesArray).forEach(string -> Log.d(TAG, "timesArray: " + string));
                List<CourseTime> courseTimeList = Arrays.stream(timesArray).map((time) -> {
                    CourseTime courseTime = new CourseTime();
                    String[] timeArray = time.split("-");
                    courseTime.setWeek(timeArray[0]);
                    courseTime.setStartTime(Integer.parseInt(timeArray[1]));
                    courseTime.setEndTime(Integer.parseInt(timeArray[2]));
                    return courseTime;
                }).collect(Collectors.toList());
                courseTimeList.forEach(courseTime -> Log.d(TAG, "courseTimeList: " + courseTime));
                course.setTimes(courseTimeList);
                courses.add(course);
            }
        }, sql, args);
        return courses;
    }

    public static List<NCourse> getAllCourse(int userId) {
        return getCourseWith("SELECT * FROM course WHERE user_id = ?", userId);
    }

    public static void insertCourseSetting() {
        DbUtils.update(null,
                "INSERT INTO course_setting (user_id, course_number, course_time_span, all_course_start_times) " +
                        "VALUES (?,?,?,?) ON DUPLICATE KEY UPDATE course_number = ?,course_time_span = ?,all_course_start_times=?;",
                UserLab.getCurrentUser().getId(),
                CourseSetting.COURSE_NUMBER,
                CourseSetting.COURSE_TIME_SPAN,
                CourseSetting.ALL_COURSE_START_TIME.toString(),
                CourseSetting.COURSE_NUMBER,
                CourseSetting.COURSE_TIME_SPAN,
                CourseSetting.ALL_COURSE_START_TIME.toString()
        );
    }

    public static void getCourseSetting() {
        String sql = "SELECT * FROM course_setting WHERE user_id = ?";
        DbUtils.query(resultSet -> {
            if (resultSet.next()) {
                CourseSetting.COURSE_NUMBER = resultSet.getInt("course_number");
                CourseSetting.COURSE_TIME_SPAN = resultSet.getInt("course_time_span");
                String startTimes = resultSet.getString("all_course_start_times");
                String[] startTimesArray = startTimes.substring(1, startTimes.length() - 1).split(",");
                CourseSetting.ALL_COURSE_START_TIME = Arrays.asList(startTimesArray);
            }
        }, sql, UserLab.getCurrentUser().getId());
    }

    public static void deleteAllCourse() {
        DbUtils.update(null,
                "DELETE FROM course WHERE user_id = ?;",
                UserLab.getCurrentUser().getId());
    }

    public static void insertCourse(NCourse course) {
        DbUtils.update(null,
                "INSERT INTO course (`user_id`,`name`, `classroom`, `times`, `teacher`, `start_week`, `end_week`, `week_style`)"
                        + " VALUES (?,?,?,?,?,?,?,?);",
                course.getUserId(),
                course.getName(),
                course.getClassroom(),
                course.getTimes().toString(),
                course.getTeacher(),
                course.getStartWeek(),
                course.getEndWeek(),
                course.getWeekStyle());
    }
}
