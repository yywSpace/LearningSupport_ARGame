package com.example.learningsupport_argame.Task.TaskReward;

import com.example.learningsupport_argame.DbUtils;

public class RewardLab {
    private static String TAG = "RewardLab";

    public static void insert(TaskReward taskReward) {
        DbUtils.update(null,
                "INSERT INTO task_reward(id, task_id, exp, gold, reward_item, item_num) VALUE(null,?,?,?,?,?)",
                taskReward.getTaskId(),
                taskReward.getExp(),
                taskReward.getGold(),
                taskReward.getRewardItem().getRewardItemType().toString(),
                taskReward.getRewardItem().getCount());
    }

    public static void update(TaskReward taskReward) {
        DbUtils.update(null,
                "UPDATE task_reward " +
                        "SET exp = ?, gold = ?, reward_item = ?,item_num = ? " +
                        "WHERE task_id = ?",
                taskReward.getExp(),
                taskReward.getGold(),
                taskReward.getRewardItem().getRewardItemType().toString(),
                taskReward.getRewardItem().getCount(),
                taskReward.getTaskId());
    }

    public static void delete(TaskReward taskReward) {
        DbUtils.update(null,
                "DELETE FROM task_reward WHERE id = ?",
                taskReward.getId());
    }
}
