package com.example.learningsupport_argame.ARModel;

import android.content.DialogInterface;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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
import com.example.learningsupport_argame.Navi.Activity.LocationService;
import com.example.learningsupport_argame.R;
import com.example.learningsupport_argame.Client.ClientLab;
import com.example.learningsupport_argame.Client.MessageData;
import com.example.learningsupport_argame.Client.UDPClient;
import com.example.learningsupport_argame.Task.Task;
import com.example.learningsupport_argame.Task.TaskLab;
import com.example.learningsupport_argame.Task.fragment.TaskListBasicFragment;
import com.example.learningsupport_argame.UserManagement.UserLab;
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
import com.google.ar.sceneform.HitTestResult;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.Renderable;
import com.google.ar.sceneform.rendering.ViewRenderable;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class ScanModelActivity extends AppCompatActivity {
    private static final String TAG = ScanModelActivity.class.getSimpleName();
    private static final int RC_PERMISSIONS = 0x123;
    private ArSceneView mArSceneView;
    private FloatingActionButton mPutModelButton;
    private FloatingActionButton mTaskAcceptButton;
    private boolean installRequested;
    TextView mMessageTextView;
    private Task mCurrentTask;
    private MapView mMapView;
    private BaiduMap mBaiDuMap;
    private LocationClient mLocationClient;
    private AnchorNode mCurrentAnchorNode;
    private Renderable mCurrentRenderable;
    private ModelInfo mCurrentModelInfo;
    private boolean isFirstLoadModel = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        SDKInitializer.setCoordType(CoordType.BD09LL);

        setContentView(R.layout.ar_activity_scan_model);
        mArSceneView = findViewById(R.id.armodel_ar_scene_view);
        mPutModelButton = findViewById(R.id.armodel_refresh_model);
        mTaskAcceptButton = findViewById(R.id.armodel_task_accept);
        mMessageTextView = findViewById(R.id.armodel_node_message);
        // 不检测平面
        mArSceneView.getPlaneRenderer().setEnabled(false);
        mTaskAcceptButton.setOnClickListener(v -> {
            if (mCurrentTask == null)
                Toast.makeText(this, "此处没有任务可以接取，请到处逛逛吧", Toast.LENGTH_SHORT).show();
            else {
                new AlertDialog.Builder(ScanModelActivity.this)
                        .setTitle("接取任务")
                        .setMessage("您是否要接取此任务呢？")
                        .setPositiveButton("确认", (dialog, which) -> {
                            new Thread(() ->
                            {
                                Task task = TaskLab.getParticipantTask(mCurrentTask.getTaskId(), mCurrentTask.getUserId());
                                if (task == null) {
                                    Looper.prepare();
                                    Toast.makeText(ScanModelActivity.this, "您已经接受过此任务，无法重复接取", Toast.LENGTH_SHORT).show();
                                    Looper.loop();
                                } else
                                    TaskLab.acceptTask(mCurrentTask);
                            }).start();
                        })
                        .setNegativeButton("取消", null)
                        .show();
            }
        });
        mPutModelButton.setOnClickListener(v -> {
            if (mArSceneView == null)
                return;
            //当Frame处于跟踪状态再继续
            if (mArSceneView.getArFrame().getCamera().getTrackingState() != TrackingState.TRACKING) {
                Toast.makeText(ScanModelActivity.this, "NO_TRACKING", Toast.LENGTH_SHORT).show();
                return;
            }
            if (mCurrentAnchorNode == null) {
                Toast.makeText(ScanModelActivity.this, "模型未加载完毕", Toast.LENGTH_SHORT).show();
                return;
            }
            mCurrentAnchorNode.getAnchor().detach();
            mCurrentAnchorNode = createAnchorNode(mCurrentModelInfo, mCurrentRenderable, mCurrentTask);
        });

        // 申请相机权限
        DemoUtils.requestCameraPermission(this, RC_PERMISSIONS);

        // 加载AR模型
        refreshARModel();

        initMap();
        initARTask();
    }

    @Override
    protected void onResume() {
        // 获取地图中AR模型数据
        if (isFirstLoadModel)
            isFirstLoadModel = false;
        else
            refreshARModel();

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

    void refreshARModel() {
        new Thread(() -> {
//            List<ModelInfo> modelInfos = ModelInfoLab.getModelInfoList();
            // 等待进入追踪状态
            while (mArSceneView.getArFrame() == null) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Log.d(TAG, "onCreate:  getArFrame == null");
            }
            while (mArSceneView.getArFrame().getCamera().getTrackingState() != TrackingState.TRACKING) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Log.d(TAG, "onCreate: NOT TRACKING");
            }
            Log.d(TAG, "onCreate: " + ModelInfoLab.mModelInfoList.size());
            ModelInfoLab.mModelInfoList.forEach(modelInfo -> {
                if (LocationService.withinDistance(modelInfo.getModelLatLng(), 100)) {
                    Task task = TaskLab.getTaskById(modelInfo.getTaskId());
                    Optional<ModelItem> itemOptional = ModelItemsLab.get()
                            .getItemList()
                            .stream()
                            .filter(item -> item.getItemName().equals(modelInfo.getModelName()))
                            .findFirst();
                    ModelItem ar_item;
                    if (itemOptional.isPresent()) {
                        ar_item = itemOptional.get();
                        mCurrentTask = task;
                    } else {
                        Looper.prepare();
                        Toast.makeText(ScanModelActivity.this, "无法刷新模型请将手机举起并稍后重试", Toast.LENGTH_SHORT).show();
                        Looper.loop();
                        refreshARModel();
                        return;
                    }
                    if (ar_item.getModelItemType() == ModelItemType.MODEL) {
                        runOnUiThread(() -> ARUtils.buildModelRenderable(this, ar_item, renderable -> {
                            Toast.makeText(this, "buildModelRenderable", Toast.LENGTH_SHORT).show();
                            mCurrentAnchorNode = createAnchorNode(modelInfo, renderable, task);
                            // TODO: 20-3-29 增加交互view
                        }));
                    } else if (ar_item.getModelItemType() == ModelItemType.VIEW) {
                        runOnUiThread(() -> ARUtils.buildViewRenderable(this, task, ar_item, renderable -> {
                            Toast.makeText(this, "buildViewRenderable", Toast.LENGTH_SHORT).show();
                            mCurrentAnchorNode = createAnchorNode(modelInfo, renderable, task);
                        }));
                    }
                    mCurrentModelInfo = modelInfo;
                }
            });
        }).start();
    }

    View initARTaskMessageView() {
        View view = LayoutInflater.from(ScanModelActivity.this).inflate(R.layout.ar_activity_model_interactive_view, null, false);

        return view;
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
    }

    void initARTask() {
        List<OverlayOptions> optionsList = new ArrayList<>();
        Log.d(TAG, "initARTask: " + ModelInfoLab.mModelInfoList);
        if (ModelInfoLab.mModelInfoList == null)
            return;
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

    AnchorNode createAnchorNode(ModelInfo modelInfo, Renderable renderable, Task task) {
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
        InfoNode node = new InfoNode(this, task);
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
        anchorNode.setOnTapListener((hitTestResult, motionEvent) ->
                node.onTap(hitTestResult, motionEvent));
        return anchorNode;
    }
}
