package com.example.learningsupport_argame.Course;

import com.example.learningsupport_argame.DbUtils;
import com.example.learningsupport_argame.Task.Task;
import com.example.learningsupport_argame.UserManagement.User;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class CourseLab {
    private static List<NCourse> getCourseWith(String sql, Object... args) {
        List<NCourse> courses = new ArrayList<>();
        DbUtils.query(resultSet -> {
            if (resultSet.next()) {
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
                courses.add(course);
            }
        }, sql, args);
        return courses;
    }

    public static List<NCourse> getAllCourse(int userId) {
        return getCourseWith("SELECT * FROM course WHERE user_id = ?", userId);
    }

    public static void insertCourse(NCourse course) {
        DbUtils.update(null,
                "INSERT INTO course (`user_id`,`name`, `classroom`, `times`, `teacher`, `start_week`, `end_week`, `week_style`)"
                        + " VALUES (?,?,?,?,?,?,?,?);",
                course.getUserId(),
                course.getName(),
                course.getClassroom(),
                course.getTimes()+"",
                course.getTeacher(),
                course.getStartWeek(),
                course.getEndWeek(),
                course.getWeekStyle());
    }
}
