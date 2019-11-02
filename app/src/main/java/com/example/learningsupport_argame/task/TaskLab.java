package com.example.learningsupport_argame.task;

import com.example.learningsupport_argame.DbUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TaskLab {
    public static List<Task> getAccomplishTask(String userId) {
        List<Task> tasks = getAllTask(userId);
        List<Task> accomplishTasks = tasks.stream()
                .filter((task -> task.getTaskType().equals("已完成")))
                .collect(Collectors.toList());
        return accomplishTasks;
    }

    public static List<Task> getReleasedTask(String userId) {
        List<Task> tasks = getAllTask(userId);

        List<Task> releasedTasks = tasks.stream()
                .filter((task -> task.getTaskStatus().equals("发布")))
                .collect(Collectors.toList());

        return releasedTasks;
    }

    public static List<Task> getAllTask(String userId) {
        List<Task> tasks = new ArrayList<>();
        Connection connection = DbUtils.getConnection();
        String sql = "SELECT * FROM task WHERE user_id = ?";
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, Integer.parseInt(userId));
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                Task task = new Task();
                task.setUserId(rs.getInt("user_id"));
                task.setTaskName(rs.getString("task_name"));
                task.setTaskContent(rs.getString("task_content"));
                task.setTaskType(rs.getString("task_type"));
                task.setTaskEndIn(rs.getString("task_end_in"));
                task.setTaskStartAt(rs.getString("task_start_at"));
                task.setTaskParticipant(rs.getString("task_participant"));
                task.setTaskStatus(rs.getString("task_status"));
                task.setTaskNotification(rs.getBoolean("task_notification"));
                task.setTaskLocation(rs.getString("task_location"));
                task.setTaskCreateTime(rs.getString("task_create_time"));
                tasks.add(task);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tasks;
    }
}
