package com.example.learningsupport_argame.ARModel;


import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.learningsupport_argame.ARModel.Items.ModelInfo;
import com.example.learningsupport_argame.ARModel.Items.ModelInfoLab;
import com.example.learningsupport_argame.ARModel.Utils.DemoUtils;
import com.example.learningsupport_argame.ARModel.Utils.LocationSensor;
import com.example.learningsupport_argame.ARModel.Utils.Vector3Utils;
import com.example.learningsupport_argame.MainActivity;
import com.example.learningsupport_argame.R;
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
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.TransformableNode;
import com.google.ar.sceneform.ux.TransformationSystem;

public class ScanModel extends AppCompatActivity  implements View.OnClickListener{
    private static final String TAG = ScanModel.class.getSimpleName();
    private ArSceneView mArSceneView;
    private Snackbar loadingMessageSnackbar = null;
    private FloatingActionButton mPutModelButton;
    private boolean installRequested;
    private ModelRenderable mModelRenderable;
    private ModelInfoLab mModelInfoLab;

    TextView mMessageTextView;
    LocationSensor mLocationSensor;


    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    ImageView menu;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.armodel_activity_scan_model);


        drawerLayout = findViewById(R.id.armodel_activity_na);
        navigationView =  findViewById(R.id.armodel_nav);
        menu=  findViewById(R.id.main_menu);
        View headerView = navigationView.getHeaderView(0);//获取头布局
        menu.setOnClickListener(this);
        navigationView.setNavigationItemSelectedListener(item -> {
            //item.setChecked(true);
            Toast.makeText(ScanModel.this,item.getTitle().toString(), Toast.LENGTH_SHORT).show();
            drawerLayout.closeDrawer(navigationView);
            return true;
        });



        mArSceneView = findViewById(R.id.aomodel_ar_scene_view);
        mPutModelButton = findViewById(R.id.armodel_model_put_test);
        mMessageTextView = findViewById(R.id.armodel_node_message);
        mModelInfoLab = ModelInfoLab.get();
        mLocationSensor = LocationSensor.get(this);
        mPutModelButton.setOnClickListener(v -> {
            if (mArSceneView == null)
                return;
            //Vector3 transform = displayModel(mModelInfoLab.getModelInfoList().get(0));
            //Vector3 transform = new Vector3(0, -.5f, -1);

            Pose pose = displayModel(mModelInfoLab.getModelInfoList().get(0));
            //当Frame处于跟踪状态再继续
            if (mArSceneView.getArFrame().getCamera().getTrackingState() != TrackingState.TRACKING) {
                Toast.makeText(ScanModel.this, "NO_TRACKING", Toast.LENGTH_SHORT).show();
                return;
            }
            Anchor anchor = mArSceneView.getSession().createAnchor(pose);
            AnchorNode anchorNode = new AnchorNode(anchor);
            anchorNode.setParent(mArSceneView.getScene());
            anchorNode.setRenderable(mModelRenderable);
        });

        ModelRenderable.builder()
                .setSource(this, Uri.parse("andy.sfb"))
                .build()
                .thenAccept(renderable -> {
                    mModelRenderable = renderable;
                    Toast.makeText(this, "ModelRenderable build finish", Toast.LENGTH_SHORT).show();
                })
                .exceptionally(
                        throwable -> {
                            Log.e(TAG, "Unable to load Renderable.", throwable);
                            return null;
                        });

        // Set an update listener on the Scene that will hide the loading message once a Plane is
        // detected.
        mArSceneView
                .getScene()
                .addOnUpdateListener(
                        frameTime -> {
                            if (loadingMessageSnackbar == null) {
                                return;
                            }

                            Frame frame = mArSceneView.getArFrame();
                            if (frame == null) {
                                return;
                            }

                            if (frame.getCamera().getTrackingState() != TrackingState.TRACKING) {
                                return;
                            }

                            for (Plane plane : frame.getUpdatedTrackables(Plane.class)) {
                                if (plane.getTrackingState() == TrackingState.TRACKING) {
                                    hideLoadingMessage();
                                }
                            }
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

        if (mArSceneView.getSession() != null) {
            showLoadingMessage();
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

    private void showLoadingMessage() {
        if (loadingMessageSnackbar != null && loadingMessageSnackbar.isShownOrQueued()) {
            return;
        }

        loadingMessageSnackbar =
                Snackbar.make(
                        ScanModel.this.findViewById(android.R.id.content),
                        R.string.armodel_plane_finding,
                        Snackbar.LENGTH_INDEFINITE);
        loadingMessageSnackbar.getView().setBackgroundColor(0xbf323232);
        loadingMessageSnackbar.show();
    }

    private void hideLoadingMessage() {
        if (loadingMessageSnackbar == null) {
            return;
        }

        loadingMessageSnackbar.dismiss();
        loadingMessageSnackbar = null;
    }

    Pose displayModel(ModelInfo modelInfo) {
        //算出当前相机位置到模型位置的向量（向量间减法）
        Vector3 transform = Vector3.forward();//Vector3Utils.sub(modelInfo.getModelPosition(), modelInfo.getCameraPosition());
        float currentDegree = mLocationSensor.getCurrentDegree();
        float originalDegree = modelInfo.getCurrentDegree();
        float rotation = (currentDegree - originalDegree);
        mMessageTextView.setText("CurrentDegree: " + currentDegree +
                "\nOriginalDegree:" + originalDegree +
                "\nrotation:" + rotation);
        Toast.makeText(this, transform + "", Toast.LENGTH_SHORT).show();
        float zRotated = (float) (transform.z * Math.cos(rotation) - transform.x * Math.sin(rotation));
        // 根据深度和方向角计算xz平面上的x的大小
        float xRotated = (float) -(transform.z * Math.sin(rotation) + transform.x * Math.cos(rotation));
        //Pose pose = Pose.makeTranslation(new float[]{xRotated, transform.y, zRotated});
        // 第二个参数指的是，模型的旋转
        Pose pose = new Pose(
                new float[]{xRotated, transform.y, zRotated},
                new float[]{
                        modelInfo.getModelRotation().x,
                        modelInfo.getModelRotation().y,
                        modelInfo.getModelRotation().z,
                        modelInfo.getModelRotation().w
                });
        return pose;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.main_menu://点击菜单，跳出侧滑菜单
                if (drawerLayout.isDrawerOpen(navigationView)){
                    drawerLayout.closeDrawer(navigationView);
                }else{
                    drawerLayout.openDrawer(navigationView);
                }
                break;
        }
    }
}
