package com.example.learningsupport_argame.ARModel;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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
import com.baidu.mapapi.model.LatLng;
import com.example.learningsupport_argame.ARModel.Items.ModelItem;
import com.example.learningsupport_argame.ARModel.Items.ModelItemType;
import com.example.learningsupport_argame.ARModel.Items.ModelItemsLab;
import com.example.learningsupport_argame.ARModel.Items.ModelInfo;
import com.example.learningsupport_argame.ARModel.Items.ModelInfoLab;
import com.example.learningsupport_argame.ARModel.Utils.ARUtils;
import com.example.learningsupport_argame.ARModel.Utils.DemoUtils;
import com.example.learningsupport_argame.ARModel.Utils.Vector3Utils;
import com.example.learningsupport_argame.R;
import com.example.learningsupport_argame.Client.ClientLab;
import com.example.learningsupport_argame.Client.MessageData;
import com.example.learningsupport_argame.Client.UDPClient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.ar.core.Anchor;
import com.google.ar.core.Pose;
import com.google.ar.core.Session;
import com.google.ar.core.TrackingState;
import com.google.ar.core.exceptions.CameraNotAvailableException;
import com.google.ar.core.exceptions.UnavailableException;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.ArSceneView;
import com.google.ar.sceneform.Camera;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.Renderable;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;


public class ScanModelActivity extends AppCompatActivity {
    private static final String TAG = ScanModelActivity.class.getSimpleName();
    private static final int RC_PERMISSIONS = 0x123;
    private ArSceneView mArSceneView;
    private FloatingActionButton mPutModelButton;
    private boolean installRequested;
    TextView mMessageTextView;

    private MapView mMapView;
    private BaiduMap mBaiDuMap;
    private LocationClient mLocationClient;
    private AnchorNode mCurrentAnchorNode;
    private Renderable mCurrentRenderable;
    private ModelInfo mCurrentModelInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        SDKInitializer.setCoordType(CoordType.BD09LL);

        setContentView(R.layout.ar_activity_scan_model);
        mArSceneView = findViewById(R.id.armodel_ar_scene_view);
        mPutModelButton = findViewById(R.id.armodel_model_put_test);
        mMessageTextView = findViewById(R.id.armodel_node_message);
        // 不检测平面
        mArSceneView.getPlaneRenderer().setEnabled(false);
        mPutModelButton.setOnClickListener(v -> {
            if (mArSceneView == null)
                return;
            //当Frame处于跟踪状态再继续
            if (mArSceneView.getArFrame().getCamera().getTrackingState() != TrackingState.TRACKING) {
                Toast.makeText(ScanModelActivity.this, "NO_TRACKING", Toast.LENGTH_SHORT).show();
                return;
            }
            if (mCurrentAnchorNode == null)
                return;
            if (mCurrentAnchorNode != null)
                mCurrentAnchorNode.getAnchor().detach();
            mCurrentAnchorNode = createAnchorNode(mCurrentModelInfo, mCurrentRenderable);
        });

        initMap();
        initARTask();

        // 申请相机权限
        DemoUtils.requestCameraPermission(this, RC_PERMISSIONS);
    }

    @Override
    protected void onResume() {
        // 获取地图中AR模型数据
        new Thread(() -> {
            ModelInfoLab.getModelInfoList();
            // 等待进入追踪状态
            while (mArSceneView.getArFrame().getCamera().getTrackingState() != TrackingState.TRACKING)
                ;
            ModelInfoLab.mModelInfoList.stream().forEach(modelInfo -> {
                if (modelInfo.isHasVibratorShaken()) {
                    ModelItem ar_item = ModelItemsLab.get()
                            .getItemList()
                            .stream()
                            .filter(item -> item.getItemName().equals(modelInfo.getModelName()))
                            .findFirst().get();
                    if (ar_item.getModelItemType() == ModelItemType.MODEL) {
                        runOnUiThread(() -> ARUtils.buildModelRenderable(this, ar_item, renderable -> {
                            Toast.makeText(this, "buildModelRenderable", Toast.LENGTH_SHORT).show();
                            mCurrentAnchorNode = createAnchorNode(modelInfo, renderable);
                        }));

                    } else if (ar_item.getModelItemType() == ModelItemType.VIEW) {
                        runOnUiThread(() -> ARUtils.buildViewRenderable(this, ar_item, renderable -> {
                            Toast.makeText(this, "buildViewRenderable", Toast.LENGTH_SHORT).show();
                            mCurrentAnchorNode = createAnchorNode(modelInfo, renderable);
                        }));
                    }
                    mCurrentModelInfo = modelInfo;
                }
            });
        }).start();

        mMapView.onResume();
        super.onResume();
        if (mArSceneView == null) {
            return;
        }

        if (mArSceneView.getSession() == null) {
            // If the session wasn't created yet, don't resume rendering.
            // This can happen if ARCore needs to be updated or permissions are not granted yet.
            try {
                Session session = DemoUtils.createArSession(this, installRequested);
                if (session == null) {
                    installRequested = DemoUtils.hasCameraPermission(this);
                    return;
                } else {
                    mArSceneView.setupSession(session);
                }
            } catch (UnavailableException e) {
                DemoUtils.handleSessionException(this, e);
            }
        }
        try {
            mArSceneView.resume();
        } catch (CameraNotAvailableException ex) {
            DemoUtils.displayError(this, "Unable to get camera", ex);
            finish();
            return;
        }
    }

    @Override
    public void onPause() {
        mMapView.onPause();
        super.onPause();
        if (mArSceneView != null) {
            mArSceneView.pause();
        }
    }

    @Override
    public void onDestroy() {
        mLocationClient.stop();
        mBaiDuMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        mMapView = null;
        super.onDestroy();
        if (mArSceneView != null) {
            mArSceneView.destroy();
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] results) {
        if (!DemoUtils.hasCameraPermission(this)) {
            if (!DemoUtils.shouldShowRequestPermissionRationale(this)) {
                // Permission denied with checking "Do not ask again".
                DemoUtils.launchPermissionSettings(this);
            } else {
                Toast.makeText(
                        this, "Camera permission is needed to run this application", Toast.LENGTH_LONG)
                        .show();
            }
            finish();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            // Standard Android full-screen functionality.
            getWindow()
                    .getDecorView()
                    .setSystemUiVisibility(
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }


    void initMap() {
        mMapView = findViewById(R.id.ar_mini_map);
        mMapView.showZoomControls(false);
        mMapView.showScaleControl(false);
        mBaiDuMap = mMapView.getMap();
        mBaiDuMap.setMyLocationEnabled(true);
        mBaiDuMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Toast.makeText(ScanModelActivity.this, "click", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public boolean onMapPoiClick(MapPoi mapPoi) {
                Toast.makeText(ScanModelActivity.this, "click", Toast.LENGTH_SHORT).show();
                finish();
                return false;
            }
        });
        mBaiDuMap.getUiSettings().setAllGesturesEnabled(false);

        // location client
        mLocationClient = new LocationClient(getApplicationContext());
        LocationClientOption option = new LocationClientOption();
        option.setCoorType("bd09ll");
        option.setScanSpan(1000);
        option.setOpenGps(true);

        mLocationClient.setLocOption(option);

        //注册监听函数
        mLocationClient.registerLocationListener(new BDAbstractLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation bdLocation) {
                LatLng cenpt = new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude());
                //定义地图状态
                MapStatus mMapStatus = new MapStatus.Builder()
                        .target(cenpt)
                        .zoom(18)
                        .build();
                //定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
                MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
                //改变地图状态
                mBaiDuMap.setMapStatus(mMapStatusUpdate);
                MyLocationData locData = new MyLocationData.Builder()
                        .latitude(bdLocation.getLatitude())
                        .longitude(bdLocation.getLongitude()).build();
                mBaiDuMap.setMyLocationData(locData);
            }
        });
        mLocationClient.start();

        // 无法在主线程发起网络请求，在线程中处理
        new Thread(() -> {
            UDPClient udpClient = ClientLab.getInstance(ClientLab.sPort, ClientLab.sIp, ClientLab.sUserName);
            udpClient.setOnReceiveUserList(userListStr -> {
                //name,ipEndPoint,x,y;name1,ipEndPoint1,x1,y1;
                MessageData data = new Gson().fromJson(userListStr, MessageData.class);
                String[] users = data.Message.split(";");
                runOnUiThread(() -> {
                    if (mBaiDuMap != null)
                        mBaiDuMap.clear();
                });
                // 添加AR任务图标
                initARTask();

                // 添加用户图标
                for (int i = 0; i < users.length; i++) {
                    String[] userArgs = users[i].split(",");
                    String userName = userArgs[0];
                    float latitude = Float.parseFloat(userArgs[2]);
                    float longitude = Float.parseFloat(userArgs[3]);

                    // 根据用户名查询用户信息
                    runOnUiThread(() -> {
                        if (mBaiDuMap != null) {
                            // 如果不为自身
                            if (!ClientLab.sUserName.equals(userName)) {
                                BitmapDescriptor bitmap = BitmapDescriptorFactory.fromBitmap(ARUtils.zoomImage(
                                        BitmapFactory.decodeResource(getResources(), R.drawable.map_character_other),
                                        90,
                                        90));
                                OverlayOptions overlayOptions = new MarkerOptions()
                                        .position(new LatLng(latitude, longitude))
                                        .draggable(false)
                                        .icon(bitmap);
                                //在地图上添加Marker，并显示
                                mBaiDuMap.addOverlay(overlayOptions);
                            }
                        }
                    });
                }
            });
        }).start();
    }

    void initARTask() {
        List<OverlayOptions> optionsList = new ArrayList<>();
        ModelInfoLab.mModelInfoList.stream().forEach((info) -> {
            BitmapDescriptor bitmap = BitmapDescriptorFactory.fromBitmap(ARUtils.zoomImage(
                    BitmapFactory.decodeResource(getResources(), R.drawable.map_task_icon),
                    90,
                    90));
            //构建MarkerOption，用于在地图上添加Marker
            OverlayOptions overlayOptions = new MarkerOptions()
                    .position(info.getModelLatLng())
                    .draggable(false)
                    .icon(bitmap);
            optionsList.add(overlayOptions);
            Log.d(TAG, "initARTask: " + info.getModelName());
        });
        mBaiDuMap.addOverlays(optionsList);
    }

    AnchorNode createAnchorNode(ModelInfo modelInfo, Renderable renderable) {
        // 清除上一个矛点，放置加载多个
        if (mCurrentAnchorNode != null)
            mCurrentAnchorNode.getAnchor().detach();
        mCurrentRenderable = renderable;

        Camera camera = mArSceneView.getScene().getCamera();
        // 模型放置时相对与相机的坐标与AnchorNode坐标相同，因为node是他的子项
        Vector3 relativePosition = modelInfo.getRelativePosition();
        // 获取相机当前旋转欧拉角
        Vector3 rotation = ARUtils.getCameraEulerRotation(camera);
        // 计算模型沿Y轴旋转相机旋转角度（此时模型在相机同一方向）
        relativePosition = Vector3Utils.rotateAroundY(relativePosition, rotation.y);
        Vector3 transform = Vector3.add(camera.getWorldPosition(), relativePosition);
        Pose pose = Pose.makeTranslation(transform.x, transform.y, transform.z);
        Anchor anchor = mArSceneView.getSession().createAnchor(pose);
        AnchorNode anchorNode = new AnchorNode(anchor);
        anchorNode.setParent(mArSceneView.getScene());
        Node node = new Node();
        node.setLocalScale(modelInfo.getLocalScale());
        // 调整模型自身旋转方向（避免因相机旋转造成的模型自身旋转出错）
        Vector3 nodeRotation = Vector3Utils.quaternion2Euler(modelInfo.getLocalRotation());
        if (nodeRotation.x > 0) {
            nodeRotation.y = 180 - nodeRotation.y;
        }
        // 当前模型自身y轴旋转加上因相机旋转产生的偏移
        nodeRotation = Vector3.add(nodeRotation, new Vector3(0, rotation.y, 0));
        node.setLocalRotation(Quaternion.eulerAngles(nodeRotation));
        node.setRenderable(renderable);
        anchorNode.addChild(node);
        return anchorNode;
    }
}
