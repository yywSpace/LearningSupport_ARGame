package com.example.learningsupport_argame.UserManagement;

import android.util.Log;

import com.example.learningsupport_argame.DbUtils;
import com.example.learningsupport_argame.Task.TaskReward.RewardItem;
import com.example.learningsupport_argame.Task.TaskReward.RewardItemLab;
import com.example.learningsupport_argame.UserManagement.Login.UserManagementStatus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class UserLab {
    public static String TAG = "UserLab";
    public static List<User> sUserListWithLevel = new ArrayList<>();
    public static List<User> sUserListWithAccomplishCount = new ArrayList<>();
    public static List<User> sUserListWithReleaseCount = new ArrayList<>();
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
            while (resultSet.next()) {
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
                user.setLastLoginTime(resultSet.getString("user_last_login_time"));
                user.setExp(resultSet.getInt("user_exp"));
                user.setGold(resultSet.getInt("user_gold"));
                user.setHp(resultSet.getInt("user_hp"));
                user.setLoginCount(resultSet.getInt("user_login_count"));
                String rewardItemsStr = resultSet.getString("user_reward_items");
                List<RewardItem> rewardItems = new ArrayList<>(4);
                RewardItemLab rewardItemLab = RewardItemLab.get();
                if (rewardItemsStr.equals("")) {
                    rewardItems.addAll(Arrays.asList(
                            rewardItemLab.getRewardItemByType(RewardItem.RewardItemType.ITEM_HEALING_POTION, 0),
                            rewardItemLab.getRewardItemByType(RewardItem.RewardItemType.ITEM_EXP_POTION, 0),
                            rewardItemLab.getRewardItemByType(RewardItem.RewardItemType.ITEM_SPEED_POTION, 0),
                            rewardItemLab.getRewardItemByType(RewardItem.RewardItemType.ITEM_GOLD_POTION, 0)));
                    user.setRewardItems(rewardItems);
                } else {
                    //[ITEM_HEALING_POTION-0, ITEM_EXP_POTION-0, ITEM_SPEED_POTION-0]
                    String[] rewardItemsArray = rewardItemsStr.substring(1, rewardItemsStr.length() - 1).split(",");
                    for (String item : rewardItemsArray) {
                        Log.d(TAG, "item: " + item);
                        String type = item.split("-")[0];
                        int count = Integer.parseInt(item.split("-")[1]);
                        if (RewardItem.RewardItemType.ITEM_HEALING_POTION.toString().equals(type.trim()))
                            rewardItems.add(rewardItemLab.getRewardItemByType(RewardItem.RewardItemType.ITEM_HEALING_POTION, count));
                        else if (RewardItem.RewardItemType.ITEM_EXP_POTION.toString().equals(type.trim()))
                            rewardItems.add(rewardItemLab.getRewardItemByType(RewardItem.RewardItemType.ITEM_EXP_POTION, count));
                        else if (RewardItem.RewardItemType.ITEM_SPEED_POTION.toString().equals(type.trim()))
                            rewardItems.add(rewardItemLab.getRewardItemByType(RewardItem.RewardItemType.ITEM_SPEED_POTION, count));
                        else if (RewardItem.RewardItemType.ITEM_GOLD_POTION.toString().equals(type.trim()))
                            rewardItems.add(rewardItemLab.getRewardItemByType(RewardItem.RewardItemType.ITEM_GOLD_POTION, count));
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
        return user.getPassword().equals(oldPassword);
    }

    public static void updateUser(User user) {
        DbUtils.update(null,
                "UPDATE user " +
                        "SET user_name = ?, user_avatar = ?, user_password = ?,user_sex = ?, user_birthday = ?,  user_city= ?, " +
                        "user_hp = ?, user_level = ?, user_exp = ?, user_gold = ?, user_reward_items = ?," +
                        "user_last_login_time = ?,user_login_count = ? " +
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
                user.getLastLoginTime(),
                user.getLoginCount(),
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


    public static List<User> getUserListWithLevel() {
        List<User> users = new ArrayList<>();
        String sql = "" +
                "select \n " +
                "   user_id,\n" +
                "   user_name,\n" +
                "   user_avatar,\n" +
                "   user_level\n" +
                "from user order by user_level desc;";
        DbUtils.query(resultSet -> {
            while (resultSet.next()) {
                User user = new User();
                user.setId(resultSet.getInt("user_id"));
                user.setName(resultSet.getString("user_name"));
                user.setAvatar(DbUtils.Bytes2Bitmap(resultSet.getBytes("user_avatar")));
                user.setLevel(resultSet.getInt("user_level"));
                users.add(user);
            }
        }, sql);
        sUserListWithLevel = users;
        return users;
    }

    public static List<User> getUserWithReleaseCount() {
        String sql = "" +
                "select\n" +
                "   user_id as id,\n" +
                "   user_name,\n" +
                "   user_avatar,\n" +
                "   (select count(*)  from task t where t.user_id =  id)  release_count \n" +
                "from user order by release_count desc;";
        List<User> users = new ArrayList<>();
        DbUtils.query(resultSet -> {
            while (resultSet.next()) {
                User user = new User();
                user.setId(resultSet.getInt("id"));
                user.setName(resultSet.getString("user_name"));
                user.setAvatar(DbUtils.Bytes2Bitmap(resultSet.getBytes("user_avatar")));
                user.setReleaseCount(resultSet.getInt("release_count"));
                users.add(user);
            }
        }, sql);
        Log.d(TAG, "getUserWithReleaseCount: " + users.size());
        sUserListWithReleaseCount = users;
        return users;
    }


    /**
     * 查询每个用户的完成任务数量
     *
     * @return
     */
    public static List<User> getUsersWithAccomplishCount() {
        String sql = "" +
                "select \n" +
                "   user_id as id,\n" +
                "   user_name,\n" +
                "   user_avatar,\n" +
                "   (select count(*) from task where task_id in \n" +
                "       (select task_id from task_participant \n" +
                "           where task_participant.participant_id = id and task_participant.task_accomplish_status = '完成'))  accomplish_count " +
                " from user order by accomplish_count desc;";
        List<User> users = new ArrayList<>();
        DbUtils.query(resultSet -> {
            while (resultSet.next()) {
                User user = new User();
                user.setId(resultSet.getInt("id"));
                user.setName(resultSet.getString("user_name"));
                user.setAvatar(DbUtils.Bytes2Bitmap(resultSet.getBytes("user_avatar")));
                user.setAccomplishCount(resultSet.getInt("accomplish_count"));
                users.add(user);
            }
        }, sql);
        Log.d(TAG, "getUsersWithAccomplishCount: " + users.size());
        sUserListWithAccomplishCount = users;
        return users;
    }

}
