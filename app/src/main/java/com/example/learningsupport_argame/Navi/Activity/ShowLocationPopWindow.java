package com.example.learningsupport_argame.Navi.Activity;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.UiSettings;
import com.baidu.mapapi.model.LatLng;
import com.example.learningsupport_argame.R;

public class ShowLocationPopWindow {
    private Context mContext;
    public LocationClient mLocationClient = null;
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private View mView;
    private AlertDialog mAlertDialog;
    private ImageButton mLocationImageButton;

    public ShowLocationPopWindow(Context context, float latitude, float longitude) {
        mContext = context;
        SDKInitializer.initialize(mContext);
        SDKInitializer.setCoordType(CoordType.BD09LL);
        View view = LayoutInflater.from(mContext).inflate(R.layout.map_pop_window_layout, null, false);
        mView = view;
        mMapView = view.findViewById(R.id.map_pop_window_map_view);
        mLocationImageButton = view.findViewById(R.id.map_pop_window_location);
        mLocationImageButton.setVisibility(View.GONE);
        mBaiduMap = mMapView.getMap();
        mLocationClient = new LocationClient(mContext);

        UiSettings uiSettings = mBaiduMap.getUiSettings();
        uiSettings.setScrollGesturesEnabled(false);
        // 地图旋转
        uiSettings.setRotateGesturesEnabled(false);
        // 地图缩放
        uiSettings.setZoomGesturesEnabled(false);
        // 地图俯视（3D）
        uiSettings.setOverlookingGesturesEnabled(false);

        LocationClientOption option = new LocationClientOption();
        option.setCoorType("bd09ll");
        option.setScanSpan(1000);
        option.setOpenGps(true);
        option.setLocationNotify(true);
        option.setIgnoreKillProcess(false);
        option.SetIgnoreCacheException(false);
        option.setWifiCacheTimeOut(5 * 60 * 1000);
        option.setEnableSimulateGps(false);

        mLocationClient.setLocOption(option);

        mLocationClient.start();

        LatLng cenpt = new LatLng(latitude, longitude);
        //定义地图状态
        MapStatus mMapStatus = new MapStatus.Builder()
                .target(cenpt)
                .zoom(18)
                .build();
        //定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
        //改变地图状态
        mBaiduMap.setMapStatus(mMapStatusUpdate);

        addMarker(cenpt, R.drawable.navi_marker_red, "");
        showPopWindow();
    }

    private void showPopWindow() {
        mAlertDialog = new AlertDialog.Builder(mContext)
                .setView(mView)
//                .setTitle("选择任务执行地点")
//                .setPositiveButton("确认", null)
//                .setNegativeButton("取消", null)
                .create();
        mAlertDialog.show();
//        mPopupWindow = new PopupWindow(mView, ViewGroup.LayoutParams.WRAP_CONTENT, 800);
//        mPopupWindow.setFocusable(true);//设置pw中的控件能够获取焦点
//        mPopupWindow.setOutsideTouchable(true); //设置可以通过点击mPopupWindow外部关闭mPopupWindow
//        mPopupWindow.update();//刷新mPopupWindow
//        mPopupWindow.showAtLocation(mMapView, Gravity.CENTER, 0, 0);
    }

    public View getView() {
        return mView;
    }

    private void addMarker(LatLng latLng, int makerRes, String userName) {
        //构建Marker图标
        BitmapDescriptor bitmap = BitmapDescriptorFactory
                .fromResource(makerRes);
        Bundle bundle = new Bundle();
        bundle.putString("user_name", userName);
        //构建MarkerOption，用于在地图上添加Marker
        OverlayOptions option = new MarkerOptions()
                .position(latLng)
                .draggable(false)
                .extraInfo(bundle)
                .icon(bitmap);
        //在地图上添加Marker，并显示
        mBaiduMap.addOverlay(option);
    }
}