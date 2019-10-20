package com.example.learningsupport_argame.Navi.Utils;

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
import com.baidu.mapapi.model.LatLngBounds;

public class LocationListener extends BDAbstractLocationListener {

    LatLng mUser_latlng;
    double latitude;
    double longitude;
    float radius;
    String coorType;
    int errorCode;
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    boolean isFirstLoc;
    BDLocation userlocation;

    public void getMap(com.baidu.mapapi.map.MapView mapView, com.baidu.mapapi.map.BaiduMap baiduMap, boolean isFirstLoc) {
        mMapView = mapView;
        mBaiduMap = baiduMap;
        this.isFirstLoc = isFirstLoc;

    }

    @Override
    public void onReceiveLocation(BDLocation location) {

        userlocation=location;

        //mapView 销毁后不在处理新接收的位置
        if (location == null || mMapView == null) {
            return;
        }
        MyLocationData locData = new MyLocationData.Builder()
                .accuracy(location.getRadius())
                // 此处设置开发者获取到的方向信息，顺时针0-360
                .direction(location.getDirection()).latitude(location.getLatitude())
                .longitude(location.getLongitude()).build();

        mBaiduMap.setMyLocationData(locData);


        if (isFirstLoc) {
            isFirstLoc = false;
            LatLng ll = new LatLng(location.getLatitude(),
                    location.getLongitude());

             //设置缩放比例,更新地图状态
//            float f = mBaiduMap.getMaxZoomLevel();// 19.0
      //     MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(ll, f - 2);
//            mBaiduMap.animateMapStatus(u);

//            MapStatus.Builder builder = new MapStatus.Builder();
//            builder.zoom(21.0f);
//            mBaiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
           // mBaiduMap.setMaxAndMinZoomLevel(14,21);

            //float f = mBaiduMap.getMaxZoomLevel();// 19.0
//                MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(ll,20);
//             mBaiduMap.animateMapStatus(u);


//            /**
//
//             * 限制地图显示范围
//
//             */
//            LatLngBounds.Builder builder1=new LatLngBounds.Builder();
//            builder1.include(new LatLng(location.getLatitude(),location.getLongitude()));
//            LatLngBounds bounds=builder1.build();
//            MapStatusUpdate u=MapStatusUpdateFactory.newLatLngBounds(bounds,50000,50000);
//            mBaiduMap.setMapStatus(u);



            // LatLng llCentre = new LatLng(location.getLatitude(),location.getLongitude());
            MapStatus.Builder builder = new MapStatus.Builder();
            builder.target(ll )//缩放中心点
                    .zoom(20);//缩放级别
//            mBaiduMap.setMaxAndMinZoomLevel(21,14);
            mBaiduMap.animateMapStatus(MapStatusUpdateFactory
                    .newMapStatus(builder.build()));









        }



//        latitude = location.getLatitude();    //获取纬度信息
//         longitude = location.getLongitude();    //获取经度信息
//         radius = location.getRadius();    //获取定位精度，默认值为0.0f
//
//         coorType = location.getCoorType();
//        //获取经纬度坐标类型，以LocationClientOption中设置过的坐标类型为准
//
//         errorCode = location.getLocType();
//        //获取定位类型、定位错误返回码，具体信息可参照类参考中BDLocation类中的说明

    }

    public double getLatitude()
    {
        return latitude;
    }
    public double getLongitude()
    {
        return longitude;
    }
    public LatLng getUserLocation()
    {

        mUser_latlng = new LatLng(userlocation.getLatitude(), userlocation.getLongitude());
        return mUser_latlng;
    }
    public LatLng getGCJ02NaviLocation()
    {

        LocationClient.getBDLocationInCoorType(userlocation,BDLocation.BDLOCATION_BD09LL_TO_GCJ02);
        mUser_latlng = new LatLng(userlocation.getLatitude(), userlocation.getLongitude());
        return mUser_latlng;
    }
}
