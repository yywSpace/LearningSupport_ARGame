package com.example.learningsupport_argame.UserManagement;

import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class UserLab {
    private static String TAG = "UserLab";
    private static String DB_USER = "argame";
    private static String DB_PASSWORD = "1095204049";
    private static String DB_URL = "jdbc:mysql://47.96.152.133:3306/game_learn_ar?characterEncoding=utf-8";
    private static String DB_DRIVER = "com.mysql.jdbc.Driver";

    private static User sCurrentUser;

    private UserLab() {

    }

    public static User getCurrentUser() {
        return sCurrentUser;
    }

    public static User getUser(String account) {
        User user = null;
        try {
            //加载驱动
            Class.forName(DB_DRIVER);
            //建立连接
            Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            //查询数据
            String sql = "SELECT * FROM user WHERE user_account = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, account);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                user = new User();
                user.setId(rs.getInt("user_id"));
                user.setAccount(rs.getString("user_account"));
                user.setName(rs.getString("user_name"));
                user.setPassword(rs.getString("user_password"));
                user.setSex(rs.getString("user_sex"));
                user.setCity(rs.getString("user_city"));
                user.setExp(rs.getInt("user_exp"));
                user.setCredits(rs.getInt("user_credits"));
            }
        } catch (ClassNotFoundException | SQLException e) {
            Log.e(TAG, "getUser: ", e);
            e.printStackTrace();
        }
        return user;
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
        try {
            Class.forName(DB_DRIVER);
            Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            String sql = "INSERT INTO user(user_id,user_account,user_name,user_password) VALUE(null,?,?,?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, user.getAccount());
            statement.setString(2, user.getName());
            statement.setString(3, user.getPassword());
            statement.executeUpdate();
        } catch (ClassNotFoundException | SQLException e) {
            Log.e(TAG, "register: ", e);
            e.printStackTrace();
        }
        return UserManagementStatus.REGISTER_SUCCESS;
    }
}
