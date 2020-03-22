package com.example.learningsupport_argame.UserManagement;

import android.util.Log;

import com.example.learningsupport_argame.DbUtils;
import com.example.learningsupport_argame.Task.TaskReward.RewardItem;
import com.example.learningsupport_argame.UserManagement.Login.UserManagementStatus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class UserLab {
    public static String TAG = "UserLab";

    private static User sCurrentUser;

    private UserLab() {

    }

    public static void setCurrentUser(User user) {
        sCurrentUser = user;
    }

    public static User getCurrentUser() {
        return sCurrentUser;
    }


    public static User getUserById(String userId) {
        List<User> users = getUserWith("SELECT * FROM user WHERE user_id = ?", userId);
        if (users.size() == 0)
            return null;
        return users.get(0);
    }

    public static User getUser(String account) {
        List<User> users = getUserWith("SELECT * FROM user WHERE user_account = ?", account);
        if (users.size() == 0)
            return null;
        return users.get(0);
    }

    public static List<User> getUserWith(String sql, Object... args) {
        List<User> users = new ArrayList<>();
        DbUtils.query(resultSet -> {
            if (resultSet.next()) {
                User user = new User();
                user.setId(resultSet.getInt("user_id"));
                user.setAccount(resultSet.getString("user_account"));
                user.setName(resultSet.getString("user_name"));
                user.setAvatar(DbUtils.Bytes2Bitmap(resultSet.getBytes("user_avatar")));
                user.setPassword(resultSet.getString("user_password"));
                user.setLevel(resultSet.getInt("user_level"));
                user.setBirthday(resultSet.getString("user_birthday"));
                user.setSex(resultSet.getString("user_sex"));
                user.setCity(resultSet.getString("user_city"));
                user.setExp(resultSet.getInt("user_exp"));
                user.setGold(resultSet.getInt("user_gold"));
                user.setHp(resultSet.getInt("user_hp"));
                String rewardItemsStr = resultSet.getString("user_reward_items");
                List<RewardItem> rewardItems = new ArrayList<>(3);
                if (rewardItemsStr.equals("")) {
                    rewardItems.addAll(Arrays.asList(
                            new RewardItem(RewardItem.RewardItemType.ITEM_HEALING_POTION, 0),
                            new RewardItem(RewardItem.RewardItemType.ITEM_EXP_POTION, 0),
                            new RewardItem(RewardItem.RewardItemType.ITEM_SPEED_POTION, 0)));
                    user.setRewardItems(rewardItems);
                } else {
                    //[ITEM_HEALING_POTION-0, ITEM_EXP_POTION-0, ITEM_SPEED_POTION-0]
                    String[] rewardItemsArray = rewardItemsStr.substring(1, rewardItemsStr.length() - 1).split(",");
                    for (String item : rewardItemsArray) {
                        Log.d(TAG, "item: " + item);
                        String type = item.split("-")[0];
                        int count = Integer.parseInt(item.split("-")[1]);
                        if (RewardItem.RewardItemType.ITEM_HEALING_POTION.toString().equals(type.trim()))
                            rewardItems.add(new RewardItem(RewardItem.RewardItemType.ITEM_HEALING_POTION, count));
                        else if (RewardItem.RewardItemType.ITEM_EXP_POTION.toString().equals(type.trim()))
                            rewardItems.add(new RewardItem(RewardItem.RewardItemType.ITEM_EXP_POTION, count));
                        else if (RewardItem.RewardItemType.ITEM_SPEED_POTION.toString().equals(type.trim()))
                            rewardItems.add(new RewardItem(RewardItem.RewardItemType.ITEM_SPEED_POTION, count));
                    }
                    Log.d(TAG, "rewardItems: " + rewardItems.toString());
                    user.setRewardItems(rewardItems);
                }
                users.add(user);
            }
        }, sql, args);
        return users;
    }

    public static UserManagementStatus login(String account, String password) {
        User user = getUser(account);
        if (user == null)
            return UserManagementStatus.LOGIN_ACCOUNT_NOT_EXIST;
        if (!user.getPassword().equals(password))
            return UserManagementStatus.LOGIN_PASSWORD_ERROR;
        sCurrentUser = user;
        return UserManagementStatus.LOGIN_SUCCESS;
    }

    public static UserManagementStatus register(User user, String rePassword) {
        if (getUser(user.getAccount()) != null)
            return UserManagementStatus.REGISTER_ACCOUNT_EXIST;
        if (!user.getPassword().equals(rePassword))
            return UserManagementStatus.REGISTER_PASSWORD_DIFFERENT;

        DbUtils.update(null,
                "INSERT INTO user(user_id,user_account,user_name,user_password) VALUE(null,?,?,?)",
                user.getAccount(),
                user.getName(),
                user.getPassword());

        return UserManagementStatus.REGISTER_SUCCESS;
    }

    /**
     * 相同为true
     *
     * @param account
     * @param oldPassword
     * @return
     */
    public static boolean confirmPassword(String account, String oldPassword) {
        User user = getUser(account);
        Log.d(TAG, "confirmPassword: " + account);
        if (user.getPassword().equals(oldPassword))
            return true;
        else return false;
    }

    public static void updateUser(User user) {
        DbUtils.update(null,
                "UPDATE user " +
                        "SET user_name = ?, user_avatar = ?, user_password = ?,user_sex = ?, user_birthday = ?,  user_city= ?, " +
                        "user_hp = ?, user_level = ?, user_exp = ?, user_gold = ?, user_reward_items = ?" +
                        "WHERE user_id = ?",
                user.getName(),
                DbUtils.Bitmap2Bytes(user.getAvatar()),
                user.getPassword(),
                user.getSex(),
                user.getBirthday(),
                user.getCity(),
                user.getHp(),
                user.getLevel(),
                user.getExp(),
                user.getGold(),
                user.getRewardItems().toString(),
                user.getId());
    }

    /**
     * 根据好友Id添加好友
     *
     * @param friendId
     */
    public static void addFriend(String friendId) {
        DbUtils.update(null,
                "INSERT INTO friend VALUE(null, ?, ?);",
                getCurrentUser().getId(),
                Integer.parseInt(friendId));
    }

    public static void deleteFriend(String friendId) {
        DbUtils.update(null,
                "DELETE FROM friend WHERE friend_id = ?",
                Integer.parseInt(friendId));
    }
}
