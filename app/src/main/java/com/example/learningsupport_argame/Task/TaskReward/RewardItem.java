package com.example.learningsupport_argame.Task.TaskReward;

import androidx.annotation.NonNull;

import com.example.learningsupport_argame.UserManagement.bag.Item;

/**
 * 目前设置每次只能得到一种道具
 */
public class RewardItem extends Item {
    private RewardItemType mItemType;

    public RewardItem() {
        super("", "", 1, 0);
    }

    public RewardItem(String name, String desc, RewardItemType rewardItemType, int count, int price) {
        super(name, desc, count, price);
        mItemType = rewardItemType;
        setCount(count);
    }

    public RewardItem(RewardItemType rewardItemType, int count) {
        super("", "", 1, 0);
        mItemType = rewardItemType;
    }

    public RewardItemType getRewardItemType() {
        return mItemType;
    }

    public void setRewardItemType(RewardItemType rewardItemType) {
        mItemType = rewardItemType;
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
