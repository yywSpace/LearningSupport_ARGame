package com.example.learningsupport_argame.Task.TaskReward;

public class TaskReward {
    private RewardItem mRewardItem;
    private int mExp;
    /**
     * 用于发放奖励时设置任务经验值
     */
    private int mGold;

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
}




