package com.example.learningsupport_argame.Navi.Activity;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.model.LatLng;
import com.example.learningsupport_argame.ARModel.Items.ModelInfo;
import com.example.learningsupport_argame.ARModel.Items.ModelInfoLab;
import com.example.learningsupport_argame.ARModel.Utils.VibratorUtil;
import com.example.learningsupport_argame.Navi.Utils.MapUtils;
import com.example.learningsupport_argame.UserManagement.Login.LoginActivity;
import com.example.learningsupport_argame.UserManagement.UserLab;
import com.example.learningsupport_argame.Client.ClientLab;
import com.example.learningsupport_argame.Client.UDPClient;

/**
 * 在login里开启
 */
public class LocationService extends Service {
    public static String TAG = "LocationService";
    private LocationClient mLocationClient;
    private UDPClient mUDPClient;
    private Thread mDetectionARTaskThread;
    private static BDLocation mCurrentLocation;
    public static Intent mLocationServiceIntent;

    @Override
    public void onCreate() {
        super.onCreate();
        mDetectionARTaskThread = new Thread(() -> {
            while (!Thread.interrupted()) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (ModelInfoLab.mModelInfoList != null) {
                    for (ModelInfo info : ModelInfoLab.mModelInfoList) {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (withinDistance(info.getModelLatLng(), 100)) {
                            if (!info.isHasVibratorShaken()) {
                                VibratorUtil.Vibrate(this, 500);
                                info.setHasVibratorShaken(true);
                            }
                        } else
                            info.setHasVibratorShaken(false);
                    }
                }
            }
        });
        new Thread(() -> {
            // 获取AR模型列表
            ModelInfoLab.getModelInfoList();
            if (UserLab.getCurrentUser() == null) {
                SharedPreferences userInfo = getSharedPreferences(LoginActivity.PREFS_NAME, MODE_PRIVATE);
                int id = userInfo.getInt("id", 0);
                UserLab.setCurrentUser(UserLab.getUserById(id + ""));
            }
            Log.d(TAG, "onCreate: " + UserLab.getCurrentUser());
            mUDPClient = ClientLab.getInstance(this, ClientLab.sPort, ClientLab.sIp, UserLab.getCurrentUser().getName());

            mUDPClient.Login();

            mLocationClient = new LocationClient(getApplicationContext());
            LocationClientOption option = new LocationClientOption();
            option.setCoorType("bd09ll");
            // >= 1000 才有效
            option.setScanSpan(1000);
            //使用高精度和仅用设备两种定位模式的，参数必须设置为true
            option.setOpenGps(true);
            mLocationClient.setLocOption(option);
            mLocationClient.registerLocationListener(new BDAbstractLocationListener() {

                @Override
                public void onReceiveLocation(BDLocation bdLocation) {
                    mCurrentLocation = bdLocation;
                    new Thread(() -> {
                        // 发送当前信息
                        mUDPClient.Location((float) bdLocation.getLatitude(), (float) bdLocation.getLongitude());
                        mUDPClient.UserList();
                        Log.d(TAG, "onReceiveLocation: " + bdLocation);
                        // 检测是否到达目标点

                    }).start();
                }
            });
            mLocationClient.start();
        }).start();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand");
        if (!mDetectionARTaskThread.isAlive())
            mDetectionARTaskThread.start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        mDetectionARTaskThread.interrupt();
        if (mLocationClient != null)
            mLocationClient.stop();
        super.onDestroy();
    }

    public static BDLocation getCurrentLocation() {
        return mCurrentLocation;
    }

    public static boolean withinDistance(LatLng modelLatLng, int distanceLimit) {
        if (getCurrentLocation() == null)
            return false;
        return distance(modelLatLng) <= distanceLimit;
    }

    public static int distance(LatLng modelLatLng) {
        return (int) Math.round(
                MapUtils.distance(
                        modelLatLng.latitude,
                        modelLatLng.longitude,
                        getCurrentLocation().getLatitude(),
                        getCurrentLocation().getLongitude()));
    }
}
