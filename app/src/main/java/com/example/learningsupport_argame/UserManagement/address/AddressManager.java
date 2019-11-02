package com.example.learningsupport_argame.UserManagement.address;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class AddressManager {
    private static String TAG = "AddressManager";

    /**
     * 读取assets本地json
     *
     * @param fileName
     * @param context
     * @return
     */
    public static String getJson(String fileName, Context context) {
        //将json数据变成字符串
        StringBuilder stringBuilder = new StringBuilder();
        try {
            //获取assets资源管理器
            AssetManager assetManager = context.getAssets();
            //通过管理器打开文件并读取
            BufferedReader bf = new BufferedReader(new InputStreamReader(
                    assetManager.open(fileName)));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            Log.e(TAG, "getJson: ", e);
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    public static List<Province> getProvince(Context context) {
        String json = getJson("ChinaProvinceCity/china_province_city_2019_5.json", context);
        Log.d(TAG, json);
        Gson gson = new Gson();
        List<Province> provinces = gson.fromJson(json, new TypeToken<List<Province>>() {
        }.getType());
        return provinces;
    }

}
