package com.example.learningsupport_argame.Navi.Activity;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.example.learningsupport_argame.client.ClientLab;
import com.example.learningsupport_argame.client.UDPClient;

public class LocationService extends Service {
    public static String TAG = "LocationService";
    private LocationClient mLocationClient;
    private UDPClient mUDPClient;
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: ");
        mUDPClient = ClientLab.getInstance(ClientLab.sPort, ClientLab.sIp,ClientLab.sUserName);
        mLocationClient = new LocationClient(getApplicationContext());
        LocationClientOption option = new LocationClientOption();

        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        //可选，设置定位模式，默认高精度
        //LocationMode.Hight_Accuracy：高精度；
        //LocationMode. Battery_Saving：低功耗；
        //LocationMode. Device_Sensors：仅使用设备；

        option.setCoorType("bd09ll");
        //可选，设置返回经纬度坐标类型，默认GCJ02
        //GCJ02：国测局坐标；
        //BD09ll：百度经纬度坐标；
        //BD09：百度墨卡托坐标；
        //海外地区定位，无需设置坐标类型，统一返回WGS84类型坐标

        option.setScanSpan(1000);
        //可选，设置发起定位请求的间隔，int类型，单位ms
        //如果设置为0，则代表单次定位，即仅定位一次，默认为0
        //如果设置非0，需设置1000ms以上才有效

        option.setOpenGps(true);
        //可选，设置是否使用gps，默认false
        //使用高精度和仅用设备两种定位模式的，参数必须设置为true


        option.setEnableSimulateGps(false);
        //可选，设置是否需要过滤GPS仿真结果，默认需要，即参数为false

        mLocationClient.setLocOption(option);
        mLocationClient.registerLocationListener(new BDAbstractLocationListener() {

            @Override
            public void onReceiveLocation(BDLocation bdLocation) {
//                Log.d(TAG, "onReceiveLocation: "+bdLocation.getLatitude());
                new Thread(()->{
                    mUDPClient.Location((float) bdLocation.getLatitude(),(float)bdLocation.getLongitude());
                    mUDPClient.UserList();
                }).start();
            }
        });
        mLocationClient.start();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        mLocationClient.stop();
        super.onDestroy();
    }
}
