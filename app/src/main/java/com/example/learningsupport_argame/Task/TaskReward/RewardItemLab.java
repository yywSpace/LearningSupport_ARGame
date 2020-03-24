package com.example.learningsupport_argame.Task.TaskReward;

import java.util.ArrayList;
import java.util.List;

public class RewardItemLab {
    private List<RewardItem> mRewardItemList;
    private static RewardItemLab sRewardItemLab;

    private RewardItemLab() {
        mRewardItemList = new ArrayList<>();
        mRewardItemList.add(new RewardItem("经验增加", "可适量增加经验值(100)", RewardItem.RewardItemType.ITEM_EXP_POTION, 0));
        mRewardItemList.add(new RewardItem("时间加速", "可适当加速任务的执行(x1.3)", RewardItem.RewardItemType.ITEM_SPEED_POTION, 0));
        mRewardItemList.add(new RewardItem("治疗药剂", "治疗用户5点血量(100)", RewardItem.RewardItemType.ITEM_HEALING_POTION, 0));
        mRewardItemList.add(new RewardItem("一堆黄金", "给于用户一百点黄金", RewardItem.RewardItemType.ITEM_GOLD_POTION, 0));
    }

    public List<RewardItem> getRewardItemList() {
        return mRewardItemList;
    }

    public RewardItem getRewardItemByType(RewardItem.RewardItemType rewardItemType, int count) {
        RewardItem ri = mRewardItemList.stream().filter(rewardItem -> rewardItem.getRewardItemType().equals(rewardItemType)).findFirst().get();
        ri.setCount(count);
        return ri;
    }

    public static RewardItemLab get() {
        if (sRewardItemLab == null)
            sRewardItemLab = new RewardItemLab();
        return sRewardItemLab;
    }
}
