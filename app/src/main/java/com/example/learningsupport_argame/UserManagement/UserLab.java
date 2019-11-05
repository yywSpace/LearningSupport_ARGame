package com.example.learningsupport_argame.UserManagement;

import android.util.Log;

import com.example.learningsupport_argame.DbUtils;
import com.example.learningsupport_argame.UserManagement.Login.UserManagementStatus;

import java.util.ArrayList;
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
        List<User> users = new ArrayList<>();
        DbUtils.query(resultSet -> {
            if (resultSet.next()) {
                User user = new User();
                user.setId(resultSet.getInt("user_id"));
                user.setAccount(resultSet.getString("user_account"));
                user.setName(resultSet.getString("user_name"));
                user.setAvatar(DbUtils.Bytes2Bitmap(resultSet.getBytes("user_avatar")));
                user.setPassword(resultSet.getString("user_password"));
                user.setLevel(resultSet.getString("user_level"));
                user.setBirthday(resultSet.getString("user_birthday"));
                user.setSex(resultSet.getString("user_sex"));
                user.setCity(resultSet.getString("user_city"));
                user.setExp(resultSet.getInt("user_exp"));
                user.setCredits(resultSet.getInt("user_credits"));
                users.add(user);
            }
        }, "SELECT * FROM user WHERE user_id = ?", userId);
        if (users.size() == 0)
            return null;
        return users.get(0);

    }

    public static User getUser(String account) {
        List<User> users = new ArrayList<>();
        DbUtils.query(resultSet -> {
            if (resultSet.next()) {
                User user = new User();
                user.setId(resultSet.getInt("user_id"));
                user.setAccount(resultSet.getString("user_account"));
                user.setName(resultSet.getString("user_name"));
                user.setAvatar(DbUtils.Bytes2Bitmap(resultSet.getBytes("user_avatar")));
                user.setPassword(resultSet.getString("user_password"));
                user.setLevel(resultSet.getString("user_level"));
                user.setBirthday(resultSet.getString("user_birthday"));
                user.setSex(resultSet.getString("user_sex"));
                user.setCity(resultSet.getString("user_city"));
                user.setExp(resultSet.getInt("user_exp"));
                user.setCredits(resultSet.getInt("user_credits"));
                users.add(user);
            }
        }, "SELECT * FROM user WHERE user_account = ?", account);
        if (users.size() == 0)
            return null;
        return users.get(0);
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
                        "SET user_name = ?, user_avatar = ?, user_password = ?,user_sex = ?, user_birthday = ?,  user_city= ? " +
                        "WHERE user_id = ?",
                user.getName(),
                DbUtils.Bitmap2Bytes(user.getAvatar()),
                user.getPassword(),
                user.getSex(),
                user.getBirthday(),
                user.getCity(),
                user.getId());
    }
}
