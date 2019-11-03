package com.example.learningsupport_argame;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DbUtils {
    private static String TAG = "DbUtils";
    private static Connection mConnection;
    public static String DB_USER = "argame";
    public static String DB_PASSWORD = "1095204049";
    public static String DB_URL = "jdbc:mysql://47.96.152.133:3306/game_learn_ar?characterEncoding=utf-8";
    public static String DB_DRIVER = "com.mysql.jdbc.Driver";

    public static Connection getConnection() {

        try {
            if (mConnection == null) {
                Class.forName(DB_DRIVER);
                mConnection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            }
        } catch (SQLException | ClassNotFoundException e) {
            Log.e(TAG, "getConnection: ", e);
            e.printStackTrace();
        }

        return mConnection;
    }


    public static byte[] Bitmap2Bytes(Bitmap bm) {
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
}
