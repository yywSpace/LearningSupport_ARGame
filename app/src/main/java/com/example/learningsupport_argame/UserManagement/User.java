package com.example.learningsupport_argame.UserManagement;

import android.graphics.Bitmap;
import android.util.Log;

import com.example.learningsupport_argame.ARModel.Items.ModelItem;
import com.example.learningsupport_argame.Task.TaskReward.RewardItem;
import com.example.learningsupport_argame.Task.TaskReward.TaskReward;
import com.example.learningsupport_argame.UserManagement.bag.UnityItem;

import java.util.ArrayList;
import java.util.List;

public class User {
    public static String CURRENT_USER_ID = "current_user_id";
    public static int BASIC_HP = 5;
    public static int BASIC_EXP = 100;
    private int mId;
    private String mAccount;
    private Bitmap mAvatar;
    private String mName;
    private String mLabel;
    private String mPassword;
    private String mSex;
    private String mBirthday;
    private String mCity;
    private int mLevel;
    private int mHp;
    private String mModName;
    /**
     * 代表当前等级的经验量，当等级提升后将重置
     */
    private int mExp;
    private int mGold;
    private List<RewardItem> mRewardItems;
    private List<ModelItem> mModelItems;
    private List<UnityItem> mUnityItems;
    private int mOnlineStatus;// 0 不在线， 1 在线， 2 接收到消息
    private boolean isNextTaskSpeedUp;
    private String mLastLoginTime;
    private int mLoginCount;
    // 一些排行信息
    private int mReleaseCount;
    private int mAccomplishCount;

    public User() {

    }

    public User(String account, String name, String password) {
        mAccount = account;
        mName = name;
        mPassword = password;
    }

    public void gettingHeart(int damage) {
        mHp -= damage;
        mHp = Math.max(mHp, 0);
    }

    // 返回是否升级成功
    public boolean addReward(TaskReward taskReward) {
        mGold += taskReward.getGold();
        if (taskReward.getRewardItem() != null)
            for (int i = 0; i < mRewardItems.size(); i++) {
                Log.d(UserLab.TAG, "addReward: " + mRewardItems.get(i));
                Log.d(UserLab.TAG, "taskReward: " + taskReward.getRewardItem().toString());
                if (mRewardItems.get(i).getRewardItemType().equals(taskReward.getRewardItem().getRewardItemType())) {
                    mRewardItems.get(i).setCount(mRewardItems.get(i).getCount() + taskReward.getRewardItem().getCount());
                    Log.d(UserLab.TAG, "addReward: " + mRewardItems.get(i).getCount() + "," + taskReward.getRewardItem().getCount());
                    break;
                }
            }
        return levelUp(taskReward.getExp());
    }

    public boolean levelUp(int addedExp) {
        mExp += addedExp;
        // 升级
        int maxExp = User.BASIC_EXP + mLevel * 500;
        if (mExp >= maxExp) {
            mExp = mExp - maxExp;
            mLevel++;
            // 升级血量满
            mHp = User.BASIC_HP + mLevel;
            return true;
        }
        return false;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public String getAccount() {
        return mAccount;
    }

    public void setAccount(String account) {
        mAccount = account;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getPassword() {
        return mPassword;
    }

    public void setPassword(String password) {
        mPassword = password;
    }

    public String getSex() {
        return mSex;
    }

    public void setSex(String sex) {
        mSex = sex;
    }

    public String getBirthday() {
        return mBirthday;
    }

    public void setBirthday(String birthday) {
        mBirthday = birthday;
    }

    public String getCity() {
        return mCity;
    }

    public void setCity(String city) {
        mCity = city;
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

    public Bitmap getAvatar() {
        return mAvatar;
    }

    public void setAvatar(Bitmap avatar) {
        mAvatar = avatar;
    }

    public int getLevel() {
        return mLevel;
    }

    public void setLevel(int level) {
        mLevel = level;
    }

    public int getOnlineStatus() {
        return mOnlineStatus;
    }

    public void setOnlineStatus(int onlineStatus) {
        mOnlineStatus = onlineStatus;
    }

    public List<RewardItem> getRewardItems() {
        return mRewardItems;
    }

    public void setRewardItems(List<RewardItem> rewardItems) {
        mRewardItems = rewardItems;
    }

    public int getHp() {
        return mHp;
    }

    public void setHp(int hp) {
        mHp = hp;
    }

    public int getReleaseCount() {
        return mReleaseCount;
    }

    public void setReleaseCount(int releaseCount) {
        mReleaseCount = releaseCount;
    }

    public int getAccomplishCount() {
        return mAccomplishCount;
    }

    public void setAccomplishCount(int accomplishCount) {
        mAccomplishCount = accomplishCount;
    }

    public boolean isNextTaskSpeedUp() {
        return isNextTaskSpeedUp;
    }

    public void setNextTaskSpeedUp(boolean nextTaskSpeedUp) {
        isNextTaskSpeedUp = nextTaskSpeedUp;
    }

    public String getLastLoginTime() {
        return mLastLoginTime;
    }

    public void setLastLoginTime(String lastLoginTime) {
        mLastLoginTime = lastLoginTime;
    }

    public int getLoginCount() {
        return mLoginCount;
    }

    public void setLoginCount(int loginCount) {
        mLoginCount = loginCount;
    }

    public String getLabel() {
        return mLabel;
    }

    public void setLabel(String label) {
        mLabel = label;
    }

    public List<ModelItem> getModelItems() {
        if (mModelItems == null)
            return new ArrayList<>();
        return mModelItems;
    }

    public void setModelItems(List<ModelItem> modelItems) {
        mModelItems = modelItems;
    }

    public List<UnityItem> getUnityItems() {
        if (mUnityItems == null)
            return new ArrayList<>();
        return mUnityItems;
    }

    public void setUnityItems(List<UnityItem> unityItems) {
        mUnityItems = unityItems;
    }

    public String getModName() {
        return mModName;
    }

    public void setModName(String modName) {
        mModName = modName;
    }
}
