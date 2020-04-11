package com.example.learningsupport_argame.Navi.Activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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
import com.baidu.mapapi.map.Overlay;
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
import com.example.learningsupport_argame.ARModel.Items.ModelInfoLab;
import com.example.learningsupport_argame.ARModel.ScanModelActivity;
import com.example.learningsupport_argame.ARModel.Utils.ARUtils;
import com.example.learningsupport_argame.Navi.Utils.LocationListener;
import com.example.learningsupport_argame.R;
import com.example.learningsupport_argame.UserManagement.User;
import com.example.learningsupport_argame.UserManagement.UserLab;
import com.example.learningsupport_argame.UserManagement.UserMessage.FriendMessageActivity;
import com.example.learningsupport_argame.Client.ClientLab;
import com.example.learningsupport_argame.Client.MessageData;
import com.example.learningsupport_argame.Client.UDPClient;
import com.example.learningsupport_argame.Task.Task;
import com.example.learningsupport_argame.Task.TaskLab;
import com.example.learningsupport_argame.Task.TaskShowView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MapActivity extends AppCompatActivity {

    private LocationListener myLocationListener;
    private static boolean isPermissionRequested = false;
    private MapView mMapView = null;
    private BaiduMap mBaiduMap;
    private LocationClient mLocationClient;

    // 客户端相关变量
    private static String TAG = "MapActivity";
    private UDPClient mUDPClient;
    /**
     * true:lock
     * false:aerial view
     */
    boolean mapMode = false;
    private String currentPopUserName;
    private String currentPopTaskId;
    private FloatingActionButton switchMapModeBtn;
    private FloatingActionButton goToARBtn;


    public void initUdpClient() {

        // 无法在主线程发起网络请求，在线程中处理
        new Thread(() -> {
            mUDPClient = ClientLab.getInstance(this, ClientLab.sPort, ClientLab.sIp, ClientLab.sUserName);
            mUDPClient.setOnReceiveUserList(userListStr -> {
                Log.d(TAG, "run: " + userListStr);
                String[] users = userListStr.split(";");
                runOnUiThread(() -> {
                    if (mBaiduMap != null)
                        mBaiduMap.clear();
                });
                initARTask();
                List<OverlayOptions> optionsList = new ArrayList<>();

                for (int i = 0; i < users.length; i++) {
                    String[] userArgs = users[i].split(",");
                    String userName = userArgs[0];
                    Log.d(TAG, "run: " + users[i]);
                    float latitude = Float.parseFloat(userArgs[2]);
                    float longitude = Float.parseFloat(userArgs[3]);
                    // 根据用户名查询用户信息
                    if (mBaiduMap != null) {
                        // 如果不为自身
                        if (!UserLab.getCurrentUser().getName().equals(userName)) {
                            OverlayOptions overlayOptions = createOverlay(new LatLng(latitude, longitude), R.drawable.map_character_other, "user_name", userName);
                            optionsList.add(overlayOptions);
                        }
                    }
                }
                List<Overlay> overlayList = mBaiduMap.addOverlays(optionsList);
                overlayList.stream().forEach((overlay) -> {
                    if (overlay.getExtraInfo().get("user_name").equals(currentPopUserName)) {
                        runOnUiThread(() -> showTaskPopWindow((Marker) overlay));
                        return;
                    }
                });

            });

        }).start();
    }


    public void showUserPopWindow(Marker marker, String userName) {
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
            Intent intent = new Intent(this, FriendMessageActivity.class);
            intent.putExtra(User.CURRENT_USER_ID, userName);
            startActivity(intent);
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

    public void showTaskPopWindow(Marker marker) {
        View view = View.inflate(MapActivity.this, R.layout.map_marker_pop_view, null);
        TextView navView = view.findViewById(R.id.map_marker_nav);
        TextView infoView = view.findViewById(R.id.map_marker_user_info);
        infoView.setText("任务详情");
        navView.setOnClickListener(v -> {
            // 开启导航页面
            walkNavi(marker.getPosition());
        });

        infoView.setOnClickListener(v -> {
            TaskShowView taskShowView = new TaskShowView(this);
            new Thread(() -> {
                Task task = TaskLab.getARTask(marker.getExtraInfo().getString("task_id"));
                runOnUiThread(() -> {
                    taskShowView.initData(task);
                });
            }).start();
            new AlertDialog.Builder(this)
                    .setView(taskShowView.getView())
                    .setNegativeButton("取消", null)
                    .show();
        });
        LatLng latLng = marker.getPosition();
        InfoWindow mInfoWindow = new InfoWindow(view, latLng, -100);
        mBaiduMap.showInfoWindow(mInfoWindow);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        SDKInitializer.initialize(getApplicationContext());
        SDKInitializer.setCoordType(CoordType.BD09LL);
        //  setContentView(R.layout.navi_map_activity);
        //开启前台服务防止应用进入后台gps挂掉
        // startService(new Intent(this, ForegroundService.class));

        requestPermission();
        // 初始化客户端相关变量
        setMapCustomFile(this, "custom_map_config.json");
        setContentView(R.layout.map_map_activity);

        //开启个性化地图
        MapView.setMapCustomEnable(true);
        initViews();
        initEvent();
        initARTask();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    private void initViews() {
        //获取地图控件引用
        mMapView = findViewById(R.id.bmapView);
        mMapView.showZoomControls(false);
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMyLocationEnabled(true);        //开启地图定位图层
        mBaiduMap.setMaxAndMinZoomLevel(20, 17);
        mBaiduMap.setMyLocationConfiguration(new MyLocationConfiguration(
                MyLocationConfiguration.LocationMode.FOLLOWING,
                true,
                BitmapDescriptorFactory.fromBitmap(ARUtils.zoomImage(
                        BitmapFactory.decodeResource(getResources(), R.drawable.map_character_self),
                        70,
                        70))));

        // UI设置
        UiSettings uiSettings = mBaiduMap.getUiSettings();
        uiSettings.setScrollGesturesEnabled(true);
        uiSettings.setRotateGesturesEnabled(false);        // 地图旋转
        uiSettings.setZoomGesturesEnabled(true);           // 地图缩放
        uiSettings.setOverlookingGesturesEnabled(false);   // 地图俯视（3D）

        //定位初始化
        mLocationClient = new LocationClient(getApplicationContext());
        //通过LocationClientOption设置LocationClient相关参数
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);          // 打开gps
        option.setCoorType("bd09ll");    // 设置坐标类型
        option.setScanSpan(1000);       // 必须设置1000以上否则只定位一次
        //设置locationClientOption
        mLocationClient.setLocOption(option);
        //注册LocationListener监听器
        myLocationListener = new LocationListener();
        myLocationListener.getMap(mBaiduMap);
        mLocationClient.registerLocationListener(myLocationListener);
        mLocationClient.start();
        goToARBtn = findViewById(R.id.map_goto_ar);
        switchMapModeBtn = findViewById(R.id.map_switch_mode);
    }

    void initEvent() {
        //若响应点击事件，返回true，否则返回false
        mBaiduMap.setOnMarkerClickListener(marker -> {
            Bundle extraInfo = marker.getExtraInfo();
            String key = extraInfo.getString("key");
            if (key.equals("user_name")) {
                String userName = extraInfo.getString("user_name");
                // 记录当前点击的maker所代表的人物名
                currentPopUserName = userName;
                currentPopTaskId = null;
                showUserPopWindow(marker, userName);

            } else if (key.equals("task_id")) {
                String taskId = extraInfo.getString("task_id");
                currentPopTaskId = taskId;
                currentPopUserName = null;
                showTaskPopWindow(marker);
            }

            return false;
        });
        goToARBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, ScanModelActivity.class);
            startActivity(intent);
        });
        switchMapModeBtn.setOnClickListener(v -> {
            // 是否跟随
            mapMode = !mapMode;
            if (mapMode) {
                myLocationListener.setMapFollow(true);
            } else {
                myLocationListener.setMapFollow(false);
            }

            MapStatus mapStatus = new MapStatus.Builder(mBaiduMap.getMapStatus())
                    .target(myLocationListener.getUserLocation())
                    .build();
            MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mapStatus);
            mBaiduMap.setMapStatus(mapStatusUpdate);
        });
    }

    void initARTask() {
        List<OverlayOptions> optionsList = new ArrayList<>();
        Log.d(TAG, "initARTask: " + ModelInfoLab.mModelInfoList);
        if (ModelInfoLab.mModelInfoList == null)
            return;
        ModelInfoLab.mModelInfoList.stream().forEach((info) -> {
            OverlayOptions overlayOptions = createOverlay(info.getModelLatLng(), R.drawable.map_task_icon, "task_id", info.getTaskId() + "");
            optionsList.add(overlayOptions);
        });
        List<Overlay> overlayList = mBaiduMap.addOverlays(optionsList);
        overlayList.stream().forEach((overlay) -> {
            if (overlay.getExtraInfo().get("task_id").equals(currentPopTaskId)) {
                runOnUiThread(() -> showTaskPopWindow((Marker) overlay));
                return;
            }
        });
    }

    @Override
    protected void onResume() {
        new Thread(ModelInfoLab::getModelInfoList).start();
        initUdpClient();
        mMapView.onResume();
        super.onResume();
    }

    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        // 退出时销毁定位
        if (mLocationClient != null) {
            mLocationClient.stop();
        }

        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);

//        stopService(LocationService.mLocationServiceIntent);
        // mMapView.onDestroy();
        super.onDestroy();
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
            if (!permissionsList.isEmpty()) {
                requestPermissions(permissionsList.toArray(new String[permissionsList.size()]), 0);
            }
        }
    }

    private OverlayOptions createOverlay(LatLng latLng, int makerRes, String key, String userName) {
        // 清除地图上的覆盖物
        // mBaiduMap.clear();
        //构建Marker图标
        BitmapDescriptor bitmap = BitmapDescriptorFactory.fromBitmap(ARUtils.zoomImage(
                BitmapFactory.decodeResource(getResources(), makerRes),
                90,
                90));
        Bundle bundle = new Bundle();
        bundle.putString(key, userName);
        bundle.putString("key", key);
        //构建MarkerOption，用于在地图上添加Marker
        OverlayOptions option = new MarkerOptions()
                .position(latLng)
                .draggable(false)
                .extraInfo(bundle)
                .icon(bitmap);
        return option;
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
        LatLng mUserLatLng = myLocationListener.getUserLocation();

        /*构造导航起终点参数对象*/
        Log.d(TAG, "routeWalkPlanWithParam: " + mUserLatLng + ":" + end);
        WalkRouteNodeInfo walkStartNode = new WalkRouteNodeInfo();
        walkStartNode.setLocation(mUserLatLng);
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
                Intent intent = new Intent(MapActivity.this,
                        YoudaoActivity.class);
                startActivity(intent);
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
