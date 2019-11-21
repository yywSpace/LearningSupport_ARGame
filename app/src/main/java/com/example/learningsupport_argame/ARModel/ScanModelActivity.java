package com.example.learningsupport_argame.ARModel;


import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.learningsupport_argame.ARModel.Items.ModelInfo;
import com.example.learningsupport_argame.ARModel.Items.ModelInfoLab;
import com.example.learningsupport_argame.ARModel.Utils.DemoUtils;
import com.example.learningsupport_argame.ARModel.Utils.LocationSensor;
import com.example.learningsupport_argame.ARModel.Utils.Utils;
import com.example.learningsupport_argame.ARModel.Utils.Vector3Utils;
import com.example.learningsupport_argame.R;
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


public class ScanModelActivity extends AppCompatActivity {
    private static final String TAG = ScanModelActivity.class.getSimpleName();
    private static final int RC_PERMISSIONS = 0x123;
    private ArSceneView mArSceneView;
    private FloatingActionButton mPutModelButton;
    private boolean installRequested;
    private ModelInfoLab mModelInfoLab;

    TextView mMessageTextView;
    LocationSensor mLocationSensor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ar_activity_scan_model);
        mArSceneView = findViewById(R.id.aomodel_ar_scene_view);
        mPutModelButton = findViewById(R.id.armodel_model_put_test);
        mMessageTextView = findViewById(R.id.armodel_node_message);
        mModelInfoLab = ModelInfoLab.get();
        mLocationSensor = LocationSensor.get(this);
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
            createAnchorNode(mModelInfoLab.getCurrentModelInfo());
        });
        // 申请相机权限
        DemoUtils.requestCameraPermission(this, RC_PERMISSIONS);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mLocationSensor.onResume();
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
        super.onPause();
        mLocationSensor.onPause();
        if (mArSceneView != null) {
            mArSceneView.pause();
        }
    }

    @Override
    public void onDestroy() {
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

    // TODO: 19-11-21 有问题，可能是放置时参数设置，也可能是这里 
    AnchorNode createAnchorNode(ModelInfo modelInfo) {
        Camera camera = mArSceneView.getScene().getCamera();
        // 模型放置时相对与相机的坐标与AnchorNode坐标相同，因为node是他的子项
        Vector3 relativePosition = modelInfo.getRelativePosition();
        // 获取相机当前旋转欧拉角
        Vector3 rotation = Utils.getCameraEulerRotation(camera);
        // 计算模型沿Y轴旋转相机旋转角度（此时模型在相机同一方向）
        relativePosition = Vector3Utils.rotateAroundY(relativePosition, rotation.y);
        Vector3 transform = Vector3.add(camera.getWorldPosition(), relativePosition);
        Pose pose = Pose.makeTranslation(transform.x, transform.y, transform.z);
        Anchor anchor = mArSceneView.getSession().createAnchor(pose);
        AnchorNode anchorNode = new AnchorNode(anchor);
        anchorNode.setParent(mArSceneView.getScene());
        Node node = new Node();
        node.setLocalScale(modelInfo.getScale());
        // 调整模型自身旋转方向（避免因相机旋转造成的模型自身旋转出错）
        Vector3 nodeRotation = Vector3Utils.quaternion2Euler(modelInfo.getRotation());
        if (nodeRotation.x > 0) {
            nodeRotation.y = 180 - nodeRotation.y;
        }
        // 当前模型自身y轴旋转加上因相机旋转产生的偏移
        nodeRotation = Vector3.add(nodeRotation, new Vector3(0, rotation.y, 0));
        node.setLocalRotation(Quaternion.eulerAngles(nodeRotation));
        node.setRenderable(modelInfo.getRenderable());
        anchorNode.addChild(node);
        return anchorNode;
    }


}
