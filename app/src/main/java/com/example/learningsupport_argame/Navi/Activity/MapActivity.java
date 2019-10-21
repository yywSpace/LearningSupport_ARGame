package com.example.learningsupport_argame.Navi.Activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.UiSettings;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.walknavi.WalkNavigateHelper;
import com.baidu.mapapi.walknavi.adapter.IWEngineInitListener;
import com.baidu.mapapi.walknavi.adapter.IWRoutePlanListener;
import com.baidu.mapapi.walknavi.model.WalkRoutePlanError;
import com.baidu.mapapi.walknavi.params.WalkNaviLaunchParam;
import com.baidu.mapapi.walknavi.params.WalkRouteNodeInfo;
import com.baidu.mapapi.CoordType;
import com.example.learningsupport_argame.Navi.Utils.LocationListener;
import com.example.learningsupport_argame.R;
import com.example.learningsupport_argame.client.ClientLab;
import com.example.learningsupport_argame.client.MessageData;
import com.example.learningsupport_argame.client.UDPClient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class MapActivity extends AppCompatActivity {

    LatLng[] latLng;
    LatLng mUser_latlng;
    LocationListener myLocationListener;
    private static boolean isPermissionRequested = false;
    private MapView mMapView = null;
    private BaiduMap mBaiduMap;
    LocationClient mLocationClient;
    boolean isFirstLoc = true;// 是否首次定位


    // 客户端相关变量
    private static String TAG = "MapActivity";
    private UDPClient mUDPClient;
    private Intent locationService;
    boolean mapFollow = false;
    boolean mapMode = true;
    private FloatingActionButton switchMapModeBtn;
    SendLocationExample mSendLocationExample1;
    SendLocationExample mSendLocationExample2;
    SendLocationExample mSendLocationExample3;
    SendLocationExample mSendLocationExample4;

    public void initUdpClient() {
        locationService = new Intent(this, LocationService.class);
        startService(locationService);
        // 模拟多个用户
        try {
            mSendLocationExample1 = new SendLocationExample("MapWayExample/way1.txt", this);
            mSendLocationExample2 = new SendLocationExample("MapWayExample/way2.txt", this);
            mSendLocationExample3 = new SendLocationExample("MapWayExample/way3.txt", this);
            mSendLocationExample4 = new SendLocationExample("MapWayExample/way4.txt", this);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 无法在主线程发起网络请求，在线程中处理
        new Thread(() -> {

            mUDPClient = ClientLab.getInstance(ClientLab.sPort, ClientLab.sIp, ClientLab.sUserName);
            mUDPClient.Login();
            mUDPClient.setOnReceiveUserList(userListStr -> {
                //name,ipEndPoint,x,y;name1,ipEndPoint1,x1,y1;
                Log.d(TAG, "run: " + userListStr);
                MessageData data = new Gson().fromJson(userListStr, MessageData.class);
                String[] users = data.Message.split(";");
                runOnUiThread(() -> {
                    if (mBaiduMap != null)
                        mBaiduMap.clear();
                });
                for (int i = 0; i < users.length; i++) {
                    String[] userArgs = users[i].split(",");
                    String userName = userArgs[0];
                    Log.d(TAG, "run: " + users[i]);
                    float latitude = Float.parseFloat(userArgs[2]);
                    float longitude = Float.parseFloat(userArgs[3]);

                    // 根据用户名查询用户信息
                    runOnUiThread(() -> {
                        if (mBaiduMap != null) {
                            // 自身图标
                            if (ClientLab.sUserName.equals(userName)) {
                                if(mapFollow == true) {
                                    LatLng center = new LatLng(latitude,
                                            longitude);
                                    //定义地图状态
                                    MapStatus mMapStatus = new MapStatus.Builder()
                                            .target(center)
                                            .build();
                                    //定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
                                    MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
                                    //改变地图状态
                                    mBaiduMap.setMapStatus(mMapStatusUpdate);
                                }
                                // 本人图标
                                addMarker(new LatLng(latitude, longitude), R.drawable.navi_marker_yellow);

                            } else
                                // 其他用户图标
                                addMarker(new LatLng(latitude, longitude), R.drawable.navi_marker_red);
                        }
                    });
                }
            });

        }).start();
    }

    // 地图进入鸟瞰模式
    public void changeMapToAerialViewMode() {
        UiSettings uiSettings = mBaiduMap.getUiSettings();
        uiSettings.setScrollGesturesEnabled(true);
        // 地图旋转
        uiSettings.setRotateGesturesEnabled(false);
        // 地图缩放
        uiSettings.setZoomGesturesEnabled(true);
        // 地图俯视（3D）
        uiSettings.setOverlookingGesturesEnabled(false);

        mapFollow = false;

        mBaiduMap.setMaxAndMinZoomLevel(20, 17);

        mMapView.showZoomControls(false);
    }

    // 地图进入详细且锁定模式
    public void changeMapToLockMode() {
        UiSettings uiSettings = mBaiduMap.getUiSettings();
        // 地图平移
        uiSettings.setScrollGesturesEnabled(false);
        // 地图旋转
        uiSettings.setRotateGesturesEnabled(false);
        // 地图缩放
        uiSettings.setZoomGesturesEnabled(false);
        // 地图俯视（3D）
        uiSettings.setOverlookingGesturesEnabled(false);
        // 地图跟随
        mapFollow = true;
        //
        mMapView.showZoomControls(false);

        //设置俯仰角,和缩放级别
        float overlook = -45.0f;

        MapStatus mapStatus = new MapStatus.Builder(mBaiduMap.getMapStatus()).overlook(overlook).zoom(20).build();

        MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mapStatus);

        mBaiduMap.setMapStatus(mapStatusUpdate);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        SDKInitializer.initialize(getApplicationContext());
        //自4.3.0起，百度地图SDK所有接口均支持百度坐标和国测局坐标，用此方法设置您使用的坐标类型.
        //包括BD09LL和GCJ02两种坐标，默认是BD09LL坐标。
        SDKInitializer.setCoordType(CoordType.BD09LL);

        //  setContentView(R.layout.navi_map_activity);
        //开启前台服务防止应用进入后台gps挂掉
        // startService(new Intent(this, ForegroundService.class));

        requestPermission();

        // 初始化客户端相关变量
        initUdpClient();

        setMapCustomFile(this, "custom_map_config.json");

        setContentView(R.layout.map_map_activity);

        switchMapModeBtn = findViewById(R.id.map_switch_mode);
        switchMapModeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapMode = !mapMode;
                if (mapMode) {
                    changeMapToLockMode();
                }else {
                    changeMapToAerialViewMode();
                }

            }
        });
        //获取地图控件引用
        mMapView = (MapView) findViewById(R.id.bmapView);
        //开启个性化地图
        MapView.setMapCustomEnable(true);
        initViews();

          /*
        地图缩放
         */
//        MapStatus.Builder builder = new MapStatus.Builder();
//        builder.zoom(21.0f);
//        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));


        // startService(new Intent(this, ForegroundService.class));


        // latLng=new LatLng[5];
//        for(int i=0;i<5;i++)
//        {
//            latLng[1]=new LatLng(39.915160800132085,116.40386525193937);
//            latLng[2]=new LatLng(37.915160800132085,115.10386525193937);
//            latLng[3]=new LatLng(41.915160800132085,114.00386525193937);
//            latLng[4]=new LatLng(38.915160800132085,118.40386525193937);
//            latLng[0]=new LatLng(40.915160800132085,117.40386525193937);
//
//
//        }
        //addMarker1(latLng);


        initListener();
        Log.d("MapActivity", "onCreate");

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void initViews() {


        mBaiduMap = mMapView.getMap();

        //设置自定义图标
        //监听地图事件
        BaiduMap.OnMapStatusChangeListener mapStatusChangeListener = new BaiduMap.OnMapStatusChangeListener() {
            View v = View.inflate(MapActivity.this, R.layout.map_imageview_layout, null);
            ImageView imageView = v.findViewById(R.id.map_image_renwu);

            @Override
            public void onMapStatusChangeStart(MapStatus mapStatus) {

            }

            @Override
            public void onMapStatusChangeStart(MapStatus mapStatus, int i) {

            }

            @Override
            public void onMapStatusChange(MapStatus mapStatus) {

                if (mapStatus.zoom == 20) {
                    imageView.setImageResource(R.drawable.map_ic_location_renwu10);
                }
                if (mapStatus.zoom == 19) {
                    imageView.setImageResource(R.drawable.map_ic_location_renwu20);
                }
                if (mapStatus.zoom == 18) {
                    imageView.setImageResource(R.drawable.map_ic_location_renwu50);
                }
                if (mapStatus.zoom == 17) {
                    imageView.setImageResource(R.drawable.map_ic_location_renwu100);
                }
                if (mapStatus.zoom == 16) {
                    imageView.setImageResource(R.drawable.map_ic_location_renwu200);
                }
                if (mapStatus.zoom == 15) {
                    imageView.setImageResource(R.drawable.map_ic_location_renwu500);
                }
                BitmapDescriptor locationMarker = BitmapDescriptorFactory.fromView(v);
                MyLocationConfiguration myLocationConfiguration = new MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL, true, locationMarker, 0, 0);
                mBaiduMap.setMyLocationConfiguration(myLocationConfiguration);
            }

            @Override
            public void onMapStatusChangeFinish(MapStatus mapStatus) {


            }
        };
//        mBaiduMap.setOnMapStatusChangeListener(mapStatusChangeListener);


        //定位初始化
        mLocationClient = new LocationClient(getApplicationContext());

        //通过LocationClientOption设置LocationClient相关参数
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(500);
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);

        //设置locationClientOption
        mLocationClient.setLocOption(option);

        //注册LocationListener监听器
        myLocationListener = new LocationListener();
        myLocationListener.getMap(mMapView, mBaiduMap, isFirstLoc);
//        mLocationClient.registerLocationListener(myLocationListener);


//        LatLng southwestLatLng = new LatLng(40.789925, 116.838326);
//
//        LatLng northeastLatLng = new LatLng(38.740688, 114.647472);

//       // LatLngBounds latLngBounds = new LatLngBounds(southwestLatLng, northeastLatLng);
//        LatLngBounds latLngBounds=new LatLngBounds(southwestLatLng,northeastLatLng);
//
//        mBaiduMap.setMapStatusLimits(latLngBounds);
//        mBaiduMap.setMapStatusLimits(mBaiduMap.getL);


        /**
         * 限制地图显示范围

         */
//        LatLngBounds.Builder builder=new LatLngBounds.Builder();
//        builder.include(new LatLng(myLocationListener.getLatitude(),myLocationListener.getLongitude()));
//        LatLngBounds bounds=builder.build();
//        MapStatusUpdate u=MapStatusUpdateFactory.newLatLngBounds(bounds,10000,10000);
//        mBaiduMap.setMapStatus(u);

        //开启地图定位图层

        //mBaiduMap.setMyLocationEnabled(true);
        mLocationClient.start();
        //mLocationClient.requestLocation();

        changeMapToLockMode();
    }


    @Override
    protected void onResume() {
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
        Log.d("MapActivity的initView被调用", "onResume");
        //requestPermission();
        //initViews();
        super.onResume();
        Log.d("MapActivity1111111", "onResume");

    }

    @Override
    protected void onPause() {
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
        super.onPause();
        Log.d("MapActivity11111111", "onPause");

    }

    @Override
    protected void onDestroy() {
        // 退出时注销用户, 此处有可能bug，mMapView.onDestroy()调用后其后语句无法执行
        mSendLocationExample1.sendLocationThread.interrupt();
        mSendLocationExample2.sendLocationThread.interrupt();
        mSendLocationExample3.sendLocationThread.interrupt();
        mSendLocationExample4.sendLocationThread.interrupt();
        stopService(locationService);
        new Thread(() -> mUDPClient.Logout()).start();


        // 退出时销毁定位
        if (mLocationClient != null) {
            mLocationClient.stop();
        }

        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        mMapView = null;
        //stopService(new Intent(this, ForegroundService.class));
        MapView.setMapCustomEnable(true);
        super.onDestroy();
        Log.d("MapActivity", "onDestroy");
    }

    /**
     * Android6.0之后需要动态申请权限
     */
    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= 23 && !isPermissionRequested) {

            isPermissionRequested = true;

            ArrayList<String> permissionsList = new ArrayList<>();

            String[] permissions = {
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.INTERNET,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.MODIFY_AUDIO_SETTINGS,
                    Manifest.permission.WRITE_SETTINGS,
                    Manifest.permission.ACCESS_WIFI_STATE,
                    Manifest.permission.CHANGE_WIFI_STATE,
                    Manifest.permission.CHANGE_WIFI_MULTICAST_STATE,
                    Manifest.permission.VIBRATE


            };

            for (String perm : permissions) {
                if (PackageManager.PERMISSION_GRANTED != checkSelfPermission(perm)) {
                    permissionsList.add(perm);
                    // 进入到这里代表没有权限.
                }
            }

            if (permissionsList.isEmpty()) {
                return;
            } else {
                requestPermissions(permissionsList.toArray(new String[permissionsList.size()]), 0);
            }
        }
    }

    private void addMarker(LatLng latLng, int makerRes) {
        // 清除地图上的覆盖物
        // mBaiduMap.clear();

        //构建Marker图标
        BitmapDescriptor bitmap = BitmapDescriptorFactory
                .fromResource(makerRes);
        Bundle bundle = new Bundle();
        bundle.putString("id", "id_01");
        //构建MarkerOption，用于在地图上添加Marker
        OverlayOptions option = new MarkerOptions()
                .position(latLng)
//                .animateType(MarkerOptions.MarkerAnimateType.jump)
                .draggable(true)
//                .title("你好,百度")
                .extraInfo(bundle)
                .icon(bitmap);
        //在地图上添加Marker，并显示
        mBaiduMap.addOverlay(option);
    }

    private void initListener() {
        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            //marker被点击时回调的方法
            //若响应点击事件，返回true，否则返回false
            //默认返回false
            @Override
            public boolean onMarkerClick(Marker marker) {
                Bundle extraInfo = marker.getExtraInfo();
//                String id = extraInfo.getString("id");
//                if ("id_01".equals(id)) {
//                   showToast(marker.getTitle());
//                }

                //绘制信息窗
                LatLng latLng = marker.getPosition();
                //用来构造InfoWindow的Button
                Button button = new Button(getApplicationContext());
                button.setBackgroundResource(R.drawable.course_shapebtn1);
//                LinearLayout.LayoutParams params=(LinearLayout.LayoutParams)button.getLayoutParams();
//                params.width=15;
//                params.height=30;
//               button.setLayoutParams(params);
                button.setText("用户1");

                //构造InfoWindow
                //point 描述的位置点
                //-100 InfoWindow相对于point在y轴的偏移量
                InfoWindow mInfoWindow = new InfoWindow(button, latLng, -100);

                //使InfoWindow生效
                mBaiduMap.showInfoWindow(mInfoWindow);


                /////////////////////////////////////////////
                walkNavi(marker.getPosition());
                return false;
            }
        });

        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                addMarker(latLng, R.drawable.navi_marker_yellow);
            }

            @Override
            public boolean onMapPoiClick(MapPoi mapPoi) {
                return false;
            }
        });
    }


    private void walkNavi(final LatLng end) {
        // 获取导航控制类
        // 引擎初始化

        WalkNavigateHelper.getInstance().initNaviEngine(this, new IWEngineInitListener() {

            @Override
            public void engineInitSuccess() {
                //引擎初始化成功的回调
                //Toast.makeText(MapActivity.this,"engineInitSuccess() 被执行", Toast.LENGTH_SHORT).show();
                routeWalkPlanWithParam(end);

            }

            @Override
            public void engineInitFail() {
                //引擎初始化失败的回调
                // Toast.makeText(MapActivity.this,"engineInitFail() 被执行",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void routeWalkPlanWithParam(LatLng end) {
        //发起算路
        //构造WalkNaviLaunchParam
        //起终点位置
        //构造WalkNaviLaunchParam

        //  LatLng mUser_latlng=myLocationListener.getGCJ02NaviLocation();
        LatLng mUser_latlng = myLocationListener.getUserLocation();

        LatLng startPt = new LatLng(29.559010, 106.290216);
        LatLng endPt = new LatLng(29.552061, 106.290216);

        /*构造导航起终点参数对象*/

        WalkRouteNodeInfo walkStartNode = new WalkRouteNodeInfo();
        walkStartNode.setLocation(mUser_latlng);
        WalkRouteNodeInfo walkEndNode = new WalkRouteNodeInfo();
        walkEndNode.setLocation(end);
        WalkNaviLaunchParam walkParam = new WalkNaviLaunchParam().startNodeInfo(walkStartNode).endNodeInfo(walkEndNode);


        //发起算路
        WalkNavigateHelper.getInstance().routePlanWithRouteNode(walkParam, new IWRoutePlanListener() {
            @Override
            public void onRoutePlanStart() {
                //开始算路的回调
                //Toast.makeText(MapActivity.this,"onRoutePlanStart() 被执行",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onRoutePlanSuccess() {
                //算路成功
                //跳转至诱导页面
                //Toast.makeText(MapActivity.this,"onRoutePlanSuccess()被执行",Toast.LENGTH_LONG).show();
                //initViews();
                Intent intent = new Intent(MapActivity.this,
                        YoudaoActivity.class);
                startActivity(intent);
                //MapActivity.this.finish();
                //Toast.makeText(MapActivity.this,"intent()被执行",Toast.LENGTH_LONG).show();
                initViews();
                // Log.d("MapActivity","导航界面结束后调用了InitView方法");


            }

            @Override
            public void onRoutePlanFail(WalkRoutePlanError walkRoutePlanError) {
                //算路失败的回调
                Toast.makeText(MapActivity.this, "目标点太近，不支持导航", Toast.LENGTH_LONG).show();
            }
        });


    }

    /**
     * 将个性化文件写入本地后调用MapView.setCustomMapStylePath加载
     *
     * @param context
     * @param fileName assets目录下自定义样式文件的文件名
     */
    private void setMapCustomFile(Context context, String fileName) {
        InputStream inputStream = null;
        FileOutputStream fileOutputStream = null;
        String moduleName = null;
        try {
            inputStream = context.getAssets().open("customConfigDir/" + fileName);
            byte[] b = new byte[inputStream.available()];
            inputStream.read(b);
            moduleName = context.getFilesDir().getAbsolutePath();
            File file = new File(moduleName + "/" + fileName);
            if (file.exists()) file.delete();
            file.createNewFile();
            fileOutputStream = new FileOutputStream(file);
            //将自定义样式文件写入本地
            fileOutputStream.write(b);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //设置自定义样式文件
        MapView.setCustomMapStylePath(moduleName + "/" + fileName);
    }


}









