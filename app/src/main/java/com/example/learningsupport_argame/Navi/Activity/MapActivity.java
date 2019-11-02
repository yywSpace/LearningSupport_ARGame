package com.example.learningsupport_argame.Navi.Activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
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
import java.util.ArrayList;

public class MapActivity extends AppCompatActivity {

    private LocationListener myLocationListener;
    private static boolean isPermissionRequested = false;
    private MapView mMapView = null;
    private BaiduMap mBaiduMap;
    private LocationClient mLocationClient;
    private boolean isFirstSwitchMode = true;


    // 客户端相关变量
    private static String TAG = "MapActivity";
    private UDPClient mUDPClient;
    private Intent locationService;
    /**
     * true:lock
     * false:aerial view
     */
    boolean mapMode = false;
    private String currentPopName;
    private FloatingActionButton switchMapModeBtn;
    private FloatingActionButton goToARBtn;
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
                            if (!ClientLab.sUserName.equals(userName)) {
                                Marker marker = addMarker(new LatLng(latitude, longitude), R.drawable.navi_marker_red, userName);
                                // 如果姓名等于当前点击的Marker姓名，则刷新时显示PopWindow
                                if (userName.equals(currentPopName)) {
                                    showPopWindow(marker, userName);
                                }
                            }
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

        MapStatus mapStatus = new MapStatus.Builder(mBaiduMap.getMapStatus()).overlook(90).target(myLocationListener.getUserLocation()).zoom(17).build();

        MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mapStatus);

        mBaiduMap.setMapStatus(mapStatusUpdate);

        myLocationListener.setMapFollow(false);

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
        myLocationListener.setMapFollow(true);

        mMapView.showZoomControls(false);

        //设置俯仰角,和缩放级别
        float overlook = -45.0f;

        MapStatus mapStatus = new MapStatus.Builder(mBaiduMap.getMapStatus()).overlook(overlook).target(myLocationListener.getUserLocation()).zoom(20).build();

        MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mapStatus);

        mBaiduMap.setMapStatus(mapStatusUpdate);
    }

    public void showPopWindow(Marker marker, String userName) {
        Toast.makeText(MapActivity.this, userName, Toast.LENGTH_SHORT).show();
        View view = View.inflate(MapActivity.this, R.layout.map_marker_pop_view, null);
        Toast.makeText(this, userName, Toast.LENGTH_SHORT).show();

        TextView navView = view.findViewById(R.id.map_marker_nav);
        TextView infoView = view.findViewById(R.id.map_marker_user_info);


        navView.setOnClickListener(v -> {
            // 开启导航页面
            walkNavi(marker.getPosition());
        });

        infoView.setOnClickListener(v -> {
            Toast.makeText(this, "UserInfo", Toast.LENGTH_SHORT).show();

        });

        //绘制信息窗
        LatLng latLng = marker.getPosition();

        //构造InfoWindow
        //point 描述的位置点
        //-100 InfoWindow相对于point在y轴的偏移量
        InfoWindow mInfoWindow = new InfoWindow(view, latLng, -100);

        //使InfoWindow生效
        mBaiduMap.showInfoWindow(mInfoWindow);
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


        //获取地图控件引用
        mMapView = findViewById(R.id.bmapView);
        //开启个性化地图
        MapView.setMapCustomEnable(true);
        initViews();

        //若响应点击事件，返回true，否则返回false
        mBaiduMap.setOnMarkerClickListener(marker -> {
            Bundle extraInfo = marker.getExtraInfo();
            String userName = extraInfo.getString("user_name");

            // 记录当前点击的maker所代表的人物名
            currentPopName = userName;
            showPopWindow(marker, userName);

            return false;
        });

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
        mBaiduMap.setOnMapStatusChangeListener(mapStatusChangeListener);

        //定位初始化
        mLocationClient = new LocationClient(getApplicationContext());

        //通过LocationClientOption设置LocationClient相关参数
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        // 必须设置1000以上否则只定位一次
        option.setScanSpan(1000);
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);

        //设置locationClientOption
        mLocationClient.setLocOption(option);

        //注册LocationListener监听器
        myLocationListener = new LocationListener();
        myLocationListener.getMap(mBaiduMap);

        mLocationClient.registerLocationListener(myLocationListener);
        //开启地图定位图层
        mBaiduMap.setMyLocationEnabled(true);

        mLocationClient.start();

        goToARBtn = findViewById(R.id.map_goto_ar);
        goToARBtn.setOnClickListener(v -> {

            Toast.makeText(this, "go to ar", Toast.LENGTH_SHORT).show();

        });
        switchMapModeBtn = findViewById(R.id.map_switch_mode);
        switchMapModeBtn.setOnClickListener(v -> {
            mapMode = !mapMode;
            if (mapMode) {
                changeMapToLockMode();
            } else {
                changeMapToAerialViewMode();
            }

        });
        changeMapToAerialViewMode();
    }


    @Override
    protected void onResume() {
        //在activity执行onResume时执行mMapView. onResume()，实现地图生命周期管理
        mMapView.onResume();
        super.onResume();

    }

    @Override
    protected void onPause() {
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
        super.onPause();

    }

    @Override
    protected void onDestroy() {

        // 退出时注销用户, 此处有可能bug，mMapView.onDestroy()调用后其后语句无法执行
        // 所以就不掉用了
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

        //stopService(new Intent(this, ForegroundService.class));

        // mMapView.onDestroy();
        super.onDestroy();

        Log.d(TAG, "onDestroy: ");

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

    private Marker addMarker(LatLng latLng, int makerRes, String userName) {
        // 清除地图上的覆盖物
        // mBaiduMap.clear();

        //构建Marker图标
        BitmapDescriptor bitmap = BitmapDescriptorFactory
                .fromResource(makerRes);
        Bundle bundle = new Bundle();
        bundle.putString("user_name", userName);
        //构建MarkerOption，用于在地图上添加Marker
        OverlayOptions option = new MarkerOptions()
                .position(latLng)
//                .animateType(MarkerOptions.MarkerAnimateType.jump)
                .draggable(false)
                .extraInfo(bundle)
                .icon(bitmap);
        //在地图上添加Marker，并显示
        Marker marker = (Marker) mBaiduMap.addOverlay(option);
        return marker;
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
        LatLng mUserLatlng = myLocationListener.getUserLocation();

        LatLng startPt = new LatLng(29.559010, 106.290216);
        LatLng endPt = new LatLng(29.552061, 106.290216);

        /*构造导航起终点参数对象*/

        Log.d(TAG, "routeWalkPlanWithParam: " + mUserLatlng + ":" + end);
        WalkRouteNodeInfo walkStartNode = new WalkRouteNodeInfo();
        walkStartNode.setLocation(mUserLatlng);
        WalkRouteNodeInfo walkEndNode = new WalkRouteNodeInfo();
        walkEndNode.setLocation(end);
        WalkNaviLaunchParam walkParam = new WalkNaviLaunchParam()
                .startNodeInfo(walkStartNode)
                .endNodeInfo(walkEndNode);


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
//                initViews();
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









