package com.example.learningsupport_argame.task;

import com.example.learningsupport_argame.DbUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TaskLab {
    public static List<Task> getAccomplishTask(String userId) {
        List<Task> accomplishTasks = new ArrayList<>();
        Connection connection = DbUtils.getConnection();
        String sql = "SELECT * FROM task WHERE user_id = ? AND task_type = ?";
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, Integer.parseInt(userId));
            statement.setString(2, "已完成");
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
                accomplishTasks.add(task);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return accomplishTasks;
    }

    public static List<Task> getReleasedTask(String userId) {
        List<Task> releasedTasks = new ArrayList<>();
        Connection connection = DbUtils.getConnection();
        String sql = "SELECT * FROM task WHERE user_id = ? AND task_status = ?";
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, Integer.parseInt(userId));
            statement.setString(2, "发布");
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
                releasedTasks.add(task);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return releasedTasks;

    }

    public static List<Task> getAll(String userId) {
        return new ArrayList<>();

    }
}
