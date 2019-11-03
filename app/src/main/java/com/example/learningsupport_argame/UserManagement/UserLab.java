package com.example.learningsupport_argame.UserManagement;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.example.learningsupport_argame.DbUtils;
import com.example.learningsupport_argame.UserManagement.Login.UserManagementStatus;

import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


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
        User user = null;
        try {

            Connection connection = DbUtils.getConnection();
            //
            String sql = "SELECT * FROM user WHERE user_id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, userId);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                user = new User();
                user.setId(rs.getInt("user_id"));
                user.setAccount(rs.getString("user_account"));
                user.setName(rs.getString("user_name"));
                user.setAvatar(DbUtils.Bytes2Bitmap(rs.getBytes("user_avatar")));
                user.setPassword(rs.getString("user_password"));
                user.setLevel(rs.getString("user_level"));
                user.setBirthday(rs.getString("user_birthday"));
                user.setSex(rs.getString("user_sex"));
                user.setCity(rs.getString("user_city"));
                user.setExp(rs.getInt("user_exp"));
                user.setCredits(rs.getInt("user_credits"));
            }
        } catch (SQLException e) {
            Log.e(TAG, "getUser: ", e);
            e.printStackTrace();
        }
        return user;

    }

    public static User getUser(String account) {
        User user = null;
        try {
            //加载驱动
            Class.forName(DbUtils.DB_DRIVER);
            //建立连接
            Connection connection = DriverManager.getConnection(DbUtils.DB_URL, DbUtils.DB_USER, DbUtils.DB_PASSWORD);
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
                user.setAvatar(DbUtils.Bytes2Bitmap(rs.getBytes("user_avatar")));
                user.setPassword(rs.getString("user_password"));
                user.setLevel(rs.getString("user_level"));
                user.setBirthday(rs.getString("user_birthday"));
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
            Class.forName(DbUtils.DB_DRIVER);
            Connection connection = DriverManager.getConnection(DbUtils.DB_URL, DbUtils.DB_USER, DbUtils.DB_PASSWORD);
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
        try {
            Class.forName(DbUtils.DB_DRIVER);
            Connection connection = DriverManager.getConnection(DbUtils.DB_URL, DbUtils.DB_USER, DbUtils.DB_PASSWORD);
            String sql = "UPDATE user " +
                    "SET user_name = ?, user_avatar = ?, user_password = ?,user_sex = ?, user_birthday = ?,  user_city= ? " +
                    "WHERE user_id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, user.getName());
            statement.setBytes(2, DbUtils.Bitmap2Bytes(user.getAvatar()));
            statement.setString(3, user.getPassword());
            statement.setString(4, user.getSex());
            statement.setString(5, user.getBirthday());
            statement.setString(6, user.getCity());
            statement.setInt(7, user.getId());
            statement.executeUpdate();
        } catch (ClassNotFoundException | SQLException e) {
            Log.e(TAG, "updateUser: ", e);
            e.printStackTrace();
        }
    }

}
