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

        DbUtils.query(resultSet -> {
            while (resultSet.next()) {
                Task task = new Task();
                task.setTaskId(resultSet.getInt("task_id"));
                task.setUserId(resultSet.getInt("user_id"));
                task.setTaskName(resultSet.getString("task_name"));
                task.setTaskContent(resultSet.getString("task_content"));
                task.setTaskType(resultSet.getString("task_type"));
                task.setTaskEndIn(resultSet.getString("task_end_in"));
                task.setTaskStartAt(resultSet.getString("task_start_at"));
                task.setTaskStatus(resultSet.getString("task_status"));
                task.setTaskNotification(resultSet.getBoolean("task_notification"));
                task.setAccomplishTaskLocation(resultSet.getString("task_accomplish_location"));
                task.setTaskCreateTime(resultSet.getString("task_create_time"));
                // 此处查询消耗时间过长
                // 获取参与人员列表
                // List<User> participant = getParticipant(task.getTaskId() + "");
                // task.setTaskParticipant(participant);
                tasks.add(task);
            }
        }, "SELECT * FROM task WHERE user_id = ?", userId);

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

        DbUtils.query(resultSet -> {
                    while (resultSet.next()) {
                        User user = new User();
                        user.setId(resultSet.getInt("user_id"));
                        user.setName(resultSet.getString("user_name"));
                        user.setLevel(resultSet.getString("user_level"));
                        users.add(user);
                    }
                },
                "select user_id, user_name, user_level from user,task_participant " +
                        "where task_participant.participant_id = user.user_id and task_participant.task_id = ?;",
                taskId);
        return users;
    }

    public static void insertTask(Task task) {
        DbUtils.update(null,
                "INSERT INTO task (" +
                        " user_id, task_name, task_content, task_release_for, " +
                        " task_type, task_status, task_notification, task_participant, " +
                        " task_accomplish_location,task_start_at, task_end_in, task_create_time " +
                        ") VALUE(?,?,?,?,?,?,?,?,?,?,?,?);",
                task.getUserId(),
                task.getTaskName(),
                task.getTaskContent(),
                task.getTaskReleaseFor(),
                task.getTaskType(),
                task.getTaskStatus(),
                false,
                task.getTaskReleaseFor(), // TODO: 19-11-4 设置选择到的数据, 参与者
                task.getAccomplishTaskLocation(),
                task.getTaskStartAt(),
                task.getTaskEndIn(),
                task.getTaskCreateTime());
    }
}
