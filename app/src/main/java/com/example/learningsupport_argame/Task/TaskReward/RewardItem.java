package com.example.learningsupport_argame.Task.TaskReward;

public class RewardItem {
    private RewardItemType mRewardItemType;
    private int mCount;

    public RewardItemType getRewardItemType() {
        return mRewardItemType;
    }

    public void setRewardItemType(RewardItemType rewardItemType) {
        mRewardItemType = rewardItemType;
    }

    public int getCount() {
        return mCount;
    }

    public void setCount(int count) {
        mCount = count;
    }
}

enum RewardItemType {
    /**
     * 治疗药水
     */
    ITEM_HEALING_POTION,
    /**
     * 经验药水
     */
    ITEM_EXP_POTION,
    /**
     * 加速药水，可适当加速任务完成
     */
    ITEM_SPEED_POTION
}
