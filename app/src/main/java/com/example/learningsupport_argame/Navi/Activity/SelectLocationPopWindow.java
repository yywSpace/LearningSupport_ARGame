package com.example.learningsupport_argame.Navi.Activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.example.learningsupport_argame.Navi.Utils.MapUtils;
import com.example.learningsupport_argame.R;
import com.mysql.jdbc.log.LogUtils;

public class SelectLocationPopWindow {
    private Context mContext;
    public LocationClient mLocationClient = null;
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private View mView;
    private AlertDialog mAlertDialog;
    private ImageButton mLocationImageButton;
    private LatLng mCurrentLatLng;
    private String mMarkerAddress;
    private LatLng mMarkerLatLng;
    private boolean isFirstLocation = true;
    private OnMarkerSet mOnMarkerSet;

    public SelectLocationPopWindow(Context context) {
        mContext = context;
        SDKInitializer.initialize(mContext);
        SDKInitializer.setCoordType(CoordType.BD09LL);
        View view = LayoutInflater.from(mContext).inflate(R.layout.map_pop_window_layout, null, false);
        mView = view;
        mMapView = view.findViewById(R.id.map_pop_window_map_view);
        // 定位自身位置
        mLocationImageButton = view.findViewById(R.id.map_pop_window_location);
        mLocationImageButton.setOnClickListener(v -> {
            MapStatus mMapStatus = new MapStatus.Builder()
                    .target(mCurrentLatLng)
                    .build();
            MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
            mBaiduMap.setMapStatus(mMapStatusUpdate);
        });
        mMapView.showZoomControls(false);
        mBaiduMap = mMapView.getMap();
        mLocationClient = new LocationClient(mContext);
        mBaiduMap.setMyLocationEnabled(true);

        UiSettings uiSettings = mBaiduMap.getUiSettings();
        uiSettings.setScrollGesturesEnabled(true);
        // 地图旋转
        uiSettings.setRotateGesturesEnabled(false);
        // 地图缩放
        uiSettings.setZoomGesturesEnabled(true);
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
        mLocationClient.registerLocationListener(new BDAbstractLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation bdLocation) {
                mCurrentLatLng = new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude());
                if (isFirstLocation) {
                    // 初始地图缩放,位置
                    MapStatus mMapStatus = new MapStatus.Builder()
                            .zoom(18)
                            .target(mCurrentLatLng)
                            .build();
                    MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
                    mBaiduMap.setMapStatus(mMapStatusUpdate);
                    isFirstLocation = false;
                }
                MyLocationData locData = new MyLocationData.Builder()
                        .latitude(bdLocation.getLatitude())
                        .longitude(bdLocation.getLongitude()).build();
                mBaiduMap.setMyLocationData(locData);
            }
        });

        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mBaiduMap.clear();
                addMarker(latLng, R.drawable.navi_marker_red);
                latLntToAddress(latLng);
            }

            @Override
            public boolean onMapPoiClick(MapPoi mapPoi) {
                mBaiduMap.clear();
                addMarker(mapPoi.getPosition(), R.drawable.navi_marker_red);
                mMarkerAddress = mapPoi.getName();
                return false;
            }
        });
        mLocationClient.start();
    }

    public void showMapDialog() {
        mAlertDialog = new AlertDialog.Builder(mContext)
                .setView(mView)
                .setTitle("选择任务执行地点")
                .setPositiveButton("确认", null)
                .setNegativeButton("取消", null)
                .create();
        mAlertDialog.show();

        mAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener((view) -> {
            if (mMarkerAddress == null || mMarkerLatLng == null) {
                Toast.makeText(mContext, "请选择一个地点", Toast.LENGTH_SHORT).show();
                return;
            }
            if (mOnMarkerSet != null)
                mOnMarkerSet.onMarkerSet(mMarkerAddress, mMarkerLatLng);
            mAlertDialog.dismiss();
        });
    }

    private void addMarker(LatLng latLng, int makerRes) {
        mMarkerLatLng = latLng;
        //构建Marker图标
        BitmapDescriptor bitmap = BitmapDescriptorFactory
                .fromResource(makerRes);
        //构建MarkerOption，用于在地图上添加Marker
        OverlayOptions option = new MarkerOptions()
                .position(latLng)
                .draggable(false)
                .icon(bitmap);
        //在地图上添加Marker，并显示
        mBaiduMap.addOverlay(option);
    }

    private void latLntToAddress(LatLng latLng) {
        MapUtils.latLng2Address(latLng, address -> mMarkerAddress = address);
    }

    public View getView() {
        return mView;
    }

    public void setOnMarkerSet(OnMarkerSet onMarkerSet) {
        mOnMarkerSet = onMarkerSet;
    }

    public interface OnMarkerSet {
        void onMarkerSet(String address, LatLng latLng);
    }

}