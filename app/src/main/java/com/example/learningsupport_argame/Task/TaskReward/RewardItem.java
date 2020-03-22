package com.example.learningsupport_argame.Task.TaskReward;

import androidx.annotation.NonNull;

/**
 * 目前设置每次只能得到一种道具
 */
public class RewardItem {
    private RewardItemType mRewardItemType;
    private int mCount;

    public RewardItem() {

    }

    public RewardItem(RewardItemType rewardItemType, int count) {
        mRewardItemType = rewardItemType;
        mCount = count;
    }

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

    @NonNull
    @Override
    public String toString() {
        return getRewardItemType().toString() + "-" + getCount();
    }

    public enum RewardItemType {
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
        ITEM_SPEED_POTION,
        /**
         * 没有道具
         */
        ITEM_NONE
    }
}
