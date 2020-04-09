package com.example.learningsupport_argame.Task;


import android.util.Log;

import com.example.learningsupport_argame.DbUtils;
import com.example.learningsupport_argame.UserManagement.User;
import com.example.learningsupport_argame.UserManagement.UserLab;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class TaskLab {
    private static String TAG = "TaskLab";
    public static List<Task> mAcceptedTaskList;
    public static List<Task> mAccomplishTaskList;
    public static List<Task> mCanAcceptedTaskList;
    public static List<Task> mReleasedTaskList;
    public static List<Task> mRunningTaskList = new ArrayList<>();

    public static List<Task> sFriendTaskList;
    public static List<Task> sAllPeopleTaskList;
    public static List<Task> sClubTaskList;

    public static Task getTaskById(int id) {
        String sql = "select * from task where task_id = ?;";
        List<Task> tasks = getTasksWith(sql, id);
        if (tasks.size() <= 0) {
            return null;
        } else {
            return tasks.get(0);
        }
    }

    public static List<Task> getCanAcceptTask() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        // 排除AR发布任务，和已接取任务
        List<Task> tasks = getTasksWith(
                "select * from task where " +
                        "task_id not in (select task_id from task_ar_model)  and " +
                        "task_id not in (select task_id from task_participant where task_participant.participant_id = ?);",
                UserLab.getCurrentUser().getId());
        tasks = tasks.stream().sorted((task1, task2) -> {
            try {
                Date begin = df.parse(task1.getTaskStartAt());
                Date end = df.parse(task2.getTaskEndIn());
                return begin.before(end) ? 1 : -1;
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return 0;
        }).collect(Collectors.toList());
        mCanAcceptedTaskList = tasks;
        return tasks;
    }


    /**
     * 根据userId获取此人已接受任务的列表
     *
     * @param userId
     * @return
     */
    public static List<Task> getAcceptedTask(String userId) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String sql = "select * from task, task_participant where task_participant.participant_id = ? and task.task_id = task_participant.task_id and task_participant.task_accomplish_status = '进行中' order by task.task_id desc ;";
        mAcceptedTaskList = getTasksWith(sql, userId);
        mAcceptedTaskList = mAcceptedTaskList.stream().sorted((task1, task2) -> {
            try {
                Date begin = df.parse(task1.getTaskStartAt());
                Date end = df.parse(task2.getTaskEndIn());
                return begin.before(end) ? 1 : -1;
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return 0;
        }).collect(Collectors.toList());
        return mAcceptedTaskList;
    }

    /**
     * 根据userId获取此人已完成任务的列表
     *
     * @param userId
     * @return
     */
    public static List<Task> getAccomplishTask(String userId) {
        String sql = "select * from task where task_id in " +
                "       (select task_id from task_participant " +
                "           where task_participant.participant_id = ? and task_accomplish_status = '完成')  order by task_id desc ;";

        List<Task> accomplishTasks = getTasksWith(sql, Integer.parseInt(userId));
        mAccomplishTaskList = accomplishTasks;
        return accomplishTasks;
    }

    /**
     * 根据userId获取此人已发布任务的列表
     *
     * @param userId
     * @return
     */
    public static List<Task> getReleasedTask(String userId) {
        List<Task> releasedTasks = getTasksWith(
                "select * from task where user_id = ?  order by task_id desc ",
                userId);
        mReleasedTaskList = releasedTasks;
        return releasedTasks;
    }

    /**
     * 根据userId获取此人所有任务列表
     *
     * @param userId
     * @return
     */
    public static List<Task> getAllTask(String userId) {
        List<Task> tasks = getTasksWith("SELECT * FROM task WHERE user_id = ?", userId);
        return tasks;
    }

    public static List<Task> getTasksWith(String sql, Object... args) {
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
                task.setReleaseClubId(resultSet.getInt("task_release_club"));
                // 此处查询消耗时间过长
                // 获取参与人员列表
                // List<User> participant = getParticipant(task.getTaskId() + "");
                // task.setTaskParticipant(participant);
                tasks.add(task);
            }
        }, sql, args);

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
                        user.setLevel(resultSet.getInt("user_level"));
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
                        " user_id, task_name, task_content," +
                        " task_type, task_status, task_notification, task_participant, " +
                        " task_accomplish_location,task_start_at, task_end_in, task_create_time,task_release_club " +
                        ") VALUE(?,?,?,?,?,?,?,?,?,?,?,?);",
                task.getUserId(),
                task.getTaskName(),
                task.getTaskContent(),
                task.getTaskType(),
                task.getTaskStatus(),
                false,
                task.getTaskType(), // TODO: 19-11-4 设置选择到的数据, 参与者
                task.getAccomplishTaskLocation(),
                task.getTaskStartAt(),
                task.getTaskEndIn(),
                task.getTaskCreateTime(),
                task.getReleaseClubId());
    }

    public static void deleteReleasedTask(Task task) {
        String sql = String.format("delete from task_participant where task_id = (select task_id from task where user_id = %d and task_create_time = '%s'); " +
                "delete from task_ar_model where task_id = (select task_id from task where user_id =  %d and task_create_time = '%s'); " +
                "delete from task where  user_id =  %d and task_create_time = '%s'", task.getUserId(), task.getTaskCreateTime(), task.getUserId(), task.getTaskCreateTime(), task.getUserId(), task.getTaskCreateTime());
        DbUtils.deleteAll(sql.split(";"));
    }


    // 根据user_id和task_create_time更新任务
    public static void updateTask(Task task) {
        String sql = "update task set task_name = ?, task_content= ?,task_type= ?,task_status= ?,task_participant= ?,task_accomplish_location= ?,task_start_at= ?,task_end_in= ? " +
                "where user_id = ? and task_create_time = ?";
        DbUtils.update(null, sql,
                task.getTaskName(),
                task.getTaskContent(),
                task.getTaskType(),
                task.getTaskStatus(),
                task.getTaskType(),
                task.getAccomplishTaskLocation(),
                task.getTaskStartAt(),
                task.getTaskEndIn(),
                task.getUserId(),
                task.getTaskCreateTime());
    }

    // 根据user_id和task_create_time更新任务
    public static void updateTaskParticipantStatus(String taskId, String userId, String status) {
        String sql = "update task_participant set task_accomplish_status = ? where task_id = ? and participant_id = ?";
        DbUtils.update(null, sql,
                status,
                taskId,
                userId
        );
    }

    public static void acceptTask(Task task) {
        Log.d(TAG, "acceptTask: " + task.getTaskName());
        DbUtils.update(null, "insert into task_participant values (null, (select task_id from task where task_name = ? and user_id = ?) , ? , '进行中');",
                task.getTaskName(), task.getUserId(), UserLab.getCurrentUser().getId());
    }

    public static Task getParticipantTask(int taskId, int userId) {
        List<Task> arTasks = getTasksWith("SELECT * FROM task_participant where task_id = ? and participant = ", taskId, userId);
        if (arTasks.size() == 0)
            return null;
        return arTasks.get(0);
    }

    public static Task getARTask(String taskId) {
        if (taskId == null)
            return null;
        List<Task> arTasks = getTasksWith("SELECT * FROM task where task_id = ?", Integer.parseInt(taskId));
        if (arTasks.size() == 0)
            return null;
        return arTasks.get(0);
    }


    public static List<Task> getFriendTask() {
        String sql = "" +
                "select * from task where user_id in (select user_id from friend where friend_id = ?) and task_type = '好友任务' and\n" +
                "                        task_id not in (select task_id from task_ar_model)  and \n" +
                "                        task_id not in (select task_id from task_participant where task_participant.participant_id = ?);";
        List<Task> friendTask = getTasksWith(sql, UserLab.getCurrentUser().getId(), UserLab.getCurrentUser().getId());
        sFriendTaskList = friendTask;
        return friendTask;
    }

    // 获取全体任务
    public static List<Task> getAllPeopleTask() {
        String sql = "" +
                "select * from task " +
                "   where task_id not in (select task_id from task_ar_model) and task_type = '全体任务' and\n" +
                "         task_id not in (select task_id from task_participant where task_participant.participant_id = ?);";
        List<Task> allPeopleTask = getTasksWith(sql, UserLab.getCurrentUser().getId());
        sAllPeopleTaskList = allPeopleTask;
        return allPeopleTask;
    }

    // 获取我参加的社团发布的任务
    public static List<Task> getClubTask() {
        String sql = "" +
                "select * from task " +
                "   where task_release_club " +
                "       in (select club_id from club_members where user_id = ?) and " +
                "       task_type = '社团任务' and\n" +
                "       task_id not in (select task_id from task_ar_model)  and \n" +
                "       task_id not in (select task_id from task_participant where task_participant.participant_id = ?);";
        List<Task> clubTask = getTasksWith(sql, UserLab.getCurrentUser().getId(), UserLab.getCurrentUser().getId());
        sClubTaskList = clubTask;
        return clubTask;
    }

    public static List<Task> getAcceptedTaskByFuzzyName(String name) {
        String sql = String.format("" +
                "select * from task, task_participant" +
                "   where task_participant.participant_id = ? and " +
                "       task.task_id = task_participant.task_id and " +
                "       task_participant.task_accomplish_status = '进行中' and task_name like '%%%s%%';", name);
        return getTasksWith(sql, UserLab.getCurrentUser().getId());
    }

    public static List<Task> getCourseTask() {
        return getTasksWith("select * from task where task_type = '课程'");
    }

}
