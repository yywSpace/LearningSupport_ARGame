package com.example.learningsupport_argame.Community;


import com.example.learningsupport_argame.DbUtils;
import com.example.learningsupport_argame.UserManagement.User;

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
                user.setAvatar(DbUtils.Bytes2Bitmap(resultSet.getBytes("user_avatar")));
                user.setPassword(resultSet.getString("user_password"));
                user.setLevel(resultSet.getInt("user_level"));
                user.setBirthday(resultSet.getString("user_birthday"));
                user.setSex(resultSet.getString("user_sex"));
                user.setCity(resultSet.getString("user_city"));
                user.setExp(resultSet.getInt("user_exp"));
                user.setGold(resultSet.getInt("user_gold"));
                friendList.add(user);
            }
        }, "SELECT * FROM user,friend where user.user_id = friend.friend_id AND friend.user_id = ?;", userId);
        return friendList;
    }

    /**
     * 记录当前得到好友数据
     * @return
     */
    public static List<User> getFriendList() {
        return sFriendList;
    }

}
