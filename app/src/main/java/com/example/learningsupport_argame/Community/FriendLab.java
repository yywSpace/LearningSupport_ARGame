package com.example.learningsupport_argame.Community;


import com.example.learningsupport_argame.DbUtils;
import com.example.learningsupport_argame.UserManagement.User;
import com.example.learningsupport_argame.UserManagement.UserLab;

import java.util.ArrayList;
import java.util.List;

public class FriendLab {
    private static String TAG = "FriendLab";
    public static List<User> sFriendList;

    public static List<User> getFriends(String userId) {
        List<User> friendList = new ArrayList<>();
        DbUtils.query(resultSet -> {
            while (resultSet.next()) {
                User user = new User();
                user.setId(resultSet.getInt("user_id"));
                user.setAccount(resultSet.getString("user_account"));
                user.setName(resultSet.getString("user_name"));
                user.setLabel(resultSet.getString("user_label"));
                user.setAvatar(DbUtils.Bytes2Bitmap(resultSet.getBytes("user_avatar")));
                user.setPassword(resultSet.getString("user_password"));
                user.setLevel(resultSet.getInt("user_level"));
                user.setBirthday(resultSet.getString("user_birthday"));
                user.setSex(resultSet.getString("user_sex"));
                user.setCity(resultSet.getString("user_city"));
                user.setExp(resultSet.getInt("user_exp"));
                user.setGold(resultSet.getInt("user_gold"));
                String modName = resultSet.getString("user_current_mod_name");
                if (modName == null || modName.equals(""))
                    user.setModName("Mod1");
                else
                    user.setModName(modName);
                friendList.add(user);
            }
        }, "SELECT * FROM user,friend where user.user_id = friend.friend_id AND friend.user_id = ?;", userId);
        return friendList;
    }

    /**
     * 记录当前得到好友数据
     *
     * @return
     */
    public static List<User> getFriendList() {
        return sFriendList;
    }


    // 主要用于测试是否已添加了好友
    public static User getFriendById(int friendId) {
        List<User> friendList = new ArrayList<>();
        String sql = "select * from friend where user_id = ? and friend_id = ?;";
        DbUtils.query(resultSet -> {
            while (resultSet.next()) {
                User user = new User();
                user.setId(resultSet.getInt("user_id"));
                friendList.add(user);
            }
        }, sql, UserLab.getCurrentUser().getId(), friendId);
        if (friendList.size() <= 0)
            return null;
        return friendList.get(0);
    }
}
