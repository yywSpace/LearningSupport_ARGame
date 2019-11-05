package com.example.learningsupport_argame;

import com.example.learningsupport_argame.UserManagement.User;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MainTest {
    public static void main(String[] args) {
        DbUtils.query(resultSet -> {
            User user = new User();
            if (resultSet.next()) {
                user.setId(resultSet.getInt("user_id"));
                user.setAccount(resultSet.getString("user_account"));
                user.setName(resultSet.getString("user_name"));
                user.setPassword(resultSet.getString("user_password"));
                user.setLevel(resultSet.getString("user_level"));
                user.setBirthday(resultSet.getString("user_birthday"));
                user.setSex(resultSet.getString("user_sex"));
                user.setCity(resultSet.getString("user_city"));
                user.setExp(resultSet.getInt("user_exp"));
                user.setCredits(resultSet.getInt("user_credits"));
            }
            System.out.println(user.getName());
        }, "select * from user where user_id = ?", 4);
    }
}
