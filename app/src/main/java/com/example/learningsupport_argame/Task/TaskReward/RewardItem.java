package com.example.learningsupport_argame.Task.TaskReward;

import androidx.annotation.NonNull;

import com.example.learningsupport_argame.UserManagement.bag.Item;

/**
 * 目前设置每次只能得到一种道具
 */
public class RewardItem extends Item {
    private RewardItemType mItemType;
    private int mCount;

    public RewardItem() {
        super("", "");
    }

    public RewardItem(String name, String desc, RewardItemType rewardItemType, int count) {
        super(name, desc);
        mItemType = rewardItemType;
        mCount = count;
    }

    public RewardItem(RewardItemType rewardItemType, int count) {
        super("", "");
        mItemType = rewardItemType;
        mCount = count;
    }

    public RewardItemType getRewardItemType() {
        return mItemType;
    }

    public void setRewardItemType(RewardItemType rewardItemType) {
        mItemType = rewardItemType;
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
         * 金币
         */
        ITEM_GOLD_POTION,
        /**
         * 没有道具
         */
        ITEM_NONE
    }
}
