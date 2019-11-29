package com.example.learningsupport_argame;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.example.learningsupport_argame.UserManagement.User;

import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DbUtils {
    private static String TAG = "DbUtils";
    public static String DB_USER = "argame";
    public static String DB_PASSWORD = "1095204049";
    public static String DB_URL = "jdbc:mysql://47.96.152.133:3306/game_learn_ar?characterEncoding=utf-8";
    public static String DB_DRIVER = "com.mysql.jdbc.Driver";

    public static Connection getConnection() {
        Connection connection = null;
        try {
            Class.forName(DB_DRIVER);
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (SQLException | ClassNotFoundException e) {
            Log.e(TAG, "getConnection: ", e);
            e.printStackTrace();
        }
        return connection;
    }

    public static void query(OnSqlQuery onSqlQuery, String sql, Object... args) {
        Connection connection = getConnection();
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement(sql);
            for (int i = 1; i <= args.length; i++)
                statement.setObject(i, args[i - 1]);
            resultSet = statement.executeQuery();
            if (onSqlQuery != null)
                onSqlQuery.onSqlQuery(resultSet);
        } catch (SQLException e) {
            Log.e(TAG, "query: ", e);
            e.printStackTrace();
        } finally {
            close(connection, statement, resultSet);
        }
    }

    public static void deleteAll(String[] sqls) {
        Connection connection = getConnection();
        Statement statement = null;
        try {
            statement = connection.createStatement();
            for (int i = 0; i < sqls.length; i++) {
                statement.addBatch(sqls[i]);// 将所有的SQL语句添加到Statement中
            }
            statement.executeBatch();
        } catch (SQLException e) {
            Log.e(TAG, "deleteAll: ", e);
            e.printStackTrace();
        } finally {
            close(connection, statement);
        }

    }

    public static void update(OnSqlUpdate onSqlUpdate, String sql, Object... args) {
        Connection connection = getConnection();
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(sql);
            for (int i = 1; i <= args.length; i++)
                statement.setObject(i, args[i - 1]);
            int effectRow = statement.executeUpdate();
            if (onSqlUpdate != null)
                onSqlUpdate.onSqlUpdate(effectRow);
        } catch (SQLException e) {
            Log.e(TAG, "update: ", e);
            e.printStackTrace();
        } finally {
            close(connection, statement);
        }
    }


    public static void close(Connection conn, PreparedStatement ps, ResultSet rs) {
        try {
            if (conn != null)
                conn.close();
            if (ps != null)
                ps.close();
            if (rs != null)
                rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void close(Connection conn, PreparedStatement ps) {
        try {
            if (conn != null)
                conn.close();
            if (ps != null)
                ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void close(Connection conn, Statement statement) {
        try {
            if (conn != null)
                conn.close();
            if (statement != null)
                statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static byte[] Bitmap2Bytes(Bitmap bm) {
        if (bm == null)
            return null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    public static Bitmap Bytes2Bitmap(byte[] b) {
        if (b != null && b.length != 0) {
            return BitmapFactory.decodeByteArray(b, 0, b.length);
        } else {
            return null;
        }
    }

    public interface OnSqlQuery {
        void onSqlQuery(ResultSet resultSet) throws SQLException;
    }

    public interface OnSqlUpdate {
        void onSqlUpdate(int effectRow);
    }
}
