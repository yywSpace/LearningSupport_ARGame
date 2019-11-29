package com.example.learningsupport_argame.Navi.Utils;

import android.util.Log;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;

public class LocationListener extends BDAbstractLocationListener {
    static String TAG = "LocationListener";
    private LatLng mUserLatLng;

    private BaiduMap mBaiDuMap;
    private BDLocation mUserLocation;
    private boolean mMapFollow;
    private boolean isFirstLocation = true;


    public void getMap(BaiduMap baiduMap) {
        mBaiDuMap = baiduMap;
    }

    @Override
    public void onReceiveLocation(BDLocation location) {
        mUserLocation = location;
        Log.d(TAG, "onReceiveLocation: mMapFollow:" + mMapFollow);

        if (mMapFollow == true || isFirstLocation) {
            LatLng center = new LatLng(location.getLatitude(),
                    location.getLongitude());
            //定义地图状态
            MapStatus mMapStatus = new MapStatus.Builder()
                    .target(center)
                    .build();
            //定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
            MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
            //改变地图状态
            mBaiDuMap.setMapStatus(mMapStatusUpdate);
            MyLocationData locData = new MyLocationData.Builder()
                    .latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mBaiDuMap.setMyLocationData(locData);
            isFirstLocation = false;
        }
    }


    public LatLng getUserLocation() {
        if (mUserLocation != null)
            mUserLatLng = new LatLng(mUserLocation.getLatitude(), mUserLocation.getLongitude());
        return mUserLatLng;
    }

    public void setMapFollow(boolean mapFollow) {
        mMapFollow = mapFollow;
    }
}
