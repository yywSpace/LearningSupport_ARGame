package com.example.learningsupport_argame.task;

import android.util.Log;

import com.example.learningsupport_argame.DbUtils;
import com.example.learningsupport_argame.UserManagement.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TaskLab {
    private static String TAG = "TaskLab";

    /**
     * 根据userId获取此人已完成任务的列表
     *
     * @param userId
     * @return
     */
    public static List<Task> getAccomplishTask(String userId) {
        List<Task> tasks = getAllTask(userId);
        List<Task> accomplishTasks = tasks.stream()
                .filter((task -> task.getTaskStatus().equals("已完成")))
                .collect(Collectors.toList());
        return accomplishTasks;
    }

    /**
     * 根据userId获取此人已发布任务的列表
     *
     * @param userId
     * @return
     */
    public static List<Task> getReleasedTask(String userId) {
        List<Task> tasks = getAllTask(userId);

        List<Task> releasedTasks = tasks.stream()
                .filter((task -> task.getTaskType().equals("发布")))
                .collect(Collectors.toList());

        return releasedTasks;
    }

    /**
     * 根据userId获取此人所有任务列表
     *
     * @param userId
     * @return
     */
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
                task.setTaskId(rs.getInt("task_id"));
                task.setUserId(rs.getInt("user_id"));
                task.setTaskName(rs.getString("task_name"));
                task.setTaskContent(rs.getString("task_content"));
                task.setTaskType(rs.getString("task_type"));
                task.setTaskEndIn(rs.getString("task_end_in"));
                task.setTaskStartAt(rs.getString("task_start_at"));
                task.setTaskStatus(rs.getString("task_status"));
                task.setTaskNotification(rs.getBoolean("task_notification"));
                task.setAccomplishTaskLocation(rs.getString("task_accomplish_location"));
                task.setTaskCreateTime(rs.getString("task_create_time"));
                // 此处查询消耗时间过长
                // 获取参与人员列表
                // List<User> participant = getParticipant(task.getTaskId() + "");
                // task.setTaskParticipant(participant);
                tasks.add(task);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tasks;
    }

    /**
     * 根据taskId获取参与此任务的人员信息
     *
     * @param taskId
     * @return
     */
    public static List<User> getParticipant(String taskId) {
        List<User> users = new ArrayList<>();
        String sql = "select * from user,task_participant where task_participant.participant_id = user.user_id and task_participant.task_id = ?;";
        Connection connection = DbUtils.getConnection();
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, taskId);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("user_id"));
                user.setAccount(rs.getString("user_account"));
                user.setName(rs.getString("user_name"));
                user.setAvatar(DbUtils.Bytes2Bitmap(rs.getBytes("user_avatar")));
                user.setPassword(rs.getString("user_password"));
                user.setLevel(rs.getString("user_level"));
                user.setBirthday(rs.getString("user_birthday"));
                user.setSex(rs.getString("user_sex"));
                user.setCity(rs.getString("user_city"));
                user.setExp(rs.getInt("user_exp"));
                user.setCredits(rs.getInt("user_credits"));
                users.add(user);
            }
        } catch (SQLException e) {
            Log.e(TAG, "getParticipant: ", e);
            e.printStackTrace();
        }
        return users;
    }

    public static int insertTask(Task task) {
        int row = 0;
        Connection connection = DbUtils.getConnection();
        String sql = "INSERT INTO task (" +
                " user_id, task_name, task_content, task_release_for, " +
                " task_type, task_status, task_notification, task_participant, " +
                " task_accomplish_location,task_start_at, task_end_in, task_create_time " +
                ") VALUE(?,?,?,?,?,?,?,?,?,?,?,?);";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, task.getUserId());
            ps.setString(2, task.getTaskName());
            ps.setString(3, task.getTaskContent());
            ps.setString(4, task.getTaskReleaseFor());
            ps.setString(5, task.getTaskType());
            ps.setString(6, task.getTaskStatus());
            ps.setBoolean(7, false);
            // TODO: 19-11-4 设置选择到的数据
            ps.setString(8, task.getTaskReleaseFor());
            ps.setString(9, task.getAccomplishTaskLocation());
            ps.setString(10, task.getTaskStartAt());
            ps.setString(11, task.getTaskEndIn());
            ps.setString(12, task.getTaskCreateTime());
            row = ps.executeUpdate();
        } catch (SQLException e) {
            Log.e(TAG, "insertTask: ", e);
            e.printStackTrace();
        }
        return row;
    }


}
