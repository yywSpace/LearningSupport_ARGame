package com.example.learningsupport_argame.ARModel;


import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
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
import com.example.learningsupport_argame.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.ar.core.Anchor;
import com.google.ar.core.Frame;
import com.google.ar.core.Plane;
import com.google.ar.core.Pose;
import com.google.ar.core.Session;
import com.google.ar.core.TrackingState;
import com.google.ar.core.exceptions.CameraNotAvailableException;
import com.google.ar.core.exceptions.UnavailableException;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.ArSceneView;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;


public class ScanModelActivity extends AppCompatActivity {
    private static final String TAG = ScanModelActivity.class.getSimpleName();
    private ArSceneView mArSceneView;
    private Snackbar loadingMessageSnackbar = null;
    private FloatingActionButton mPutModelButton;
    private boolean installRequested;
    private ModelRenderable mModelRenderable;
    private ModelInfoLab mModelInfoLab;

    TextView mMessageTextView;
    LocationSensor mLocationSensor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.armodel_activity_scan_model);
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
            Vector3 transform = new Vector3(0, -0.5f, -1);
            transform = Vector3.add(mArSceneView.getScene().getCamera().getWorldPosition(), transform);
            Pose pose = Pose.makeTranslation(transform.x, transform.y, transform.z);
            Anchor anchor = mArSceneView.getSession().createAnchor(pose);
            AnchorNode anchorNode = new AnchorNode(anchor);
            anchorNode.setParent(mArSceneView.getScene());
            Node node = createNode(ModelInfoLab.get().getCurrentModelInfo());
            anchorNode.addChild(node);
        });

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


    Node createNode(ModelInfo modelInfo) {
        Node node = new Node();
        node.setLocalScale(modelInfo.getModelScale());
        node.setLocalPosition(modelInfo.getModelPosition());
        node.setLocalRotation(modelInfo.getModelRotation());
        node.setRenderable(modelInfo.getRenderable());
        return node;
    }
}
