package com.example.learningsupport_argame.Task.TaskReward;

import java.util.Random;

public class TaskReward {
    private int mId;
    private int mTaskId;
    private RewardItem mRewardItem;
    private int mExp;
    /**
     * 用于发放奖励时设置任务经验值
     */
    private int mGold;


    public TaskReward(int exp, int gold) {
        mExp = exp;
        mGold = gold;
    }

    /**
     * 范围为0-100
     */
    public void randomRewardItem() {
        mRewardItem = new RewardItem();
        // 十分之三的几率可能获得道具
        int randomNum = new Random(System.currentTimeMillis()).nextInt(100);
        if (randomNum <= 10)
            mRewardItem.setRewardItemType(RewardItem.RewardItemType.ITEM_HEALING_POTION);
        else if (randomNum <= 20)
            mRewardItem.setRewardItemType(RewardItem.RewardItemType.ITEM_EXP_POTION);
        else if (randomNum <= 30)
            mRewardItem.setRewardItemType(RewardItem.RewardItemType.ITEM_SPEED_POTION);
        else
            mRewardItem.setRewardItemType(RewardItem.RewardItemType.ITEM_NONE);
        int itemCount = new Random(System.currentTimeMillis()).nextInt(2);
        mRewardItem.setCount(itemCount);
    }

    public RewardItem getRewardItem() {
        return mRewardItem;
    }

    public void setRewardItem(RewardItem rewardItem) {
        mRewardItem = rewardItem;
    }

    public int getExp() {
        return mExp;
    }

    public void setExp(int exp) {
        mExp = exp;
    }

    public int getGold() {
        return mGold;
    }

    public void setGold(int gold) {
        mGold = gold;
    }

    public int getTaskId() {
        return mTaskId;
    }

    public void setTaskId(int taskId) {
        mTaskId = taskId;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }
}




