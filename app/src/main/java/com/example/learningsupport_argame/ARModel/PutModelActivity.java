package com.example.learningsupport_argame.ARModel;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.baidu.mapapi.model.LatLng;
import com.example.learningsupport_argame.ARModel.Items.ModelItem;
import com.example.learningsupport_argame.ARModel.Items.ModelItemType;
import com.example.learningsupport_argame.ARModel.Items.ModelItemsAdapter;
import com.example.learningsupport_argame.ARModel.Items.ModelItemsLab;
import com.example.learningsupport_argame.ARModel.Items.ModelInfo;
import com.example.learningsupport_argame.ARModel.Items.ModelInfoLab;
import com.example.learningsupport_argame.ARModel.Utils.ARUtils;
import com.example.learningsupport_argame.ARModel.Utils.DemoUtils;
import com.example.learningsupport_argame.ARModel.Utils.Vector3Utils;
import com.example.learningsupport_argame.Navi.Activity.LocationService;
import com.example.learningsupport_argame.Navi.Utils.MapUtils;
import com.example.learningsupport_argame.R;
import com.example.learningsupport_argame.Task.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.ar.core.Anchor;
import com.google.ar.core.Frame;
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
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.Renderable;
import com.google.ar.sceneform.rendering.ViewRenderable;


public class PutModelActivity extends AppCompatActivity {
    private static final String TAG = PutModelActivity.class.getSimpleName();
    private static final int RC_PERMISSIONS = 0x123;
    private AlertDialog mItemsAlertDialog;
    private ImageView mPutStatusImage;
    private FloatingActionButton mShowModelItemButton;
    private FloatingActionButton mSubmitModelButton;
    private FloatingActionButton mDeleteModelButton;
    private FloatingActionButton mScanModelButton;
    private RecyclerView mItemsRecyclerView;
    private ModelItemsAdapter mModelItemsAdapter;

    private boolean hasSetToPanel = false;
    private ViewRenderable mViewRenderable;
    private ModelRenderable mModelRenderable;

    private ArSceneView mArSceneView;
    private AnchorNode mSelectNode;// 当前选中的model
    private ModelItem mSelectItem;// 当前选中的model
    private ModelPutStatus mPutStatus;
    /**
     * 代表当前选中节点的旋转
     */
    private Vector3 mNodeRotation;
    private int SLIDE_EFFECTIVE_DISTANCE = 3;
    private boolean installRequested;
    private float startX;
    private float startY;
    private ScaleGestureDetector mScaleGestureDetector;
    private View.OnTouchListener mScaleOnTouchListener, mSwipeOnTouchListener;

    private Task mCurrentTask;
    private String mModelAddress = "";
    private LatLng mCurrentLatLng;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!DemoUtils.checkIsSupportedDeviceOrFinish(this))
            return;

        setContentView(R.layout.ar_activity_put_model);
        mNodeRotation = new Vector3();
        mPutStatus = ModelPutStatus.DO_NOT_PUT;
        mCurrentTask = (Task) getIntent().getSerializableExtra("task");
        initView();
        initEvent();
        TextView message = findViewById(R.id.message);
        mArSceneView
                .getScene()
                .addOnUpdateListener(
                        frameTime -> {

                            Frame frame = mArSceneView.getArFrame();

                            if (frame == null) {
                                return;
                            }

                            //当Frame处于跟踪状态再继续
                            if (frame.getCamera().getTrackingState() != TrackingState.TRACKING) {
                                return;
                            }

                            Camera camera = mArSceneView.getScene().getCamera();
                            Vector3 rotation_l = Vector3Utils.quaternion2Euler(camera.getLocalRotation());
                            if (rotation_l.x > 0) {
                                rotation_l.y = 180 - rotation_l.y;
                            }
                            Vector3 rotation_w = Vector3Utils.quaternion2Euler(camera.getWorldRotation());
                            String rotation = "l rotation:" + rotation_l +
                                    "\nw rotation:" + rotation_w +
                                    "\nl position:" + camera.getLocalRotation() +
                                    "\nw position:" + camera.getWorldPosition();
                            message.setText(rotation);
                        });
        // 申请相机权限
        DemoUtils.requestCameraPermission(this, RC_PERMISSIONS);
    }

    @SuppressLint("ClickableViewAccessibility")
    void initEvent() {
        mPutStatusImage.setOnLongClickListener(v -> {
            String popText = "";
            if (mPutStatus == ModelPutStatus.DO_NOT_PUT) {
                popText = "请放置模型";
            }
            if (mPutStatus == ModelPutStatus.LR_BA_MODE) {
                popText = "左右滑动控制模型围绕运动\n上下滑动控制模型前后运动";
            }
            if (mPutStatus == ModelPutStatus.ROTATE_UD_MODE) {
                popText = "左右滑动控制模型旋转\n上下滑动控制模型上下运动";
            }
            if (mPutStatus == ModelPutStatus.SCALE_MODE) {
                popText = "双指缩放";
            }

            View view = PutModelActivity.this.getLayoutInflater().inflate(R.layout.tool_tip_layout, null, false);
            TextView text = view.findViewById(R.id.tooltip_text);
            text.setText(popText);
            view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            int[] location = new int[2];
            mPutStatusImage.getLocationOnScreen(location);
            PopupWindow popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            popupWindow.setFocusable(true);//设置pw中的控件能够获取焦点
            popupWindow.setOutsideTouchable(true); //设置可以通过点击mPopupWindow外部关闭mPopupWindow
            popupWindow.update();
            view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            int popupWidth = view.getMeasuredWidth();
            int popupHeight = view.getMeasuredHeight();
            popupWindow.showAtLocation(mPutStatusImage, Gravity.NO_GRAVITY, location[0] - popupWidth + mPutStatusImage.getWidth(), location[1] + mPutStatusImage.getHeight());
            return true;

        });
        mSubmitModelButton.setOnClickListener(v -> {
            if (mSelectNode == null) {
                Toast.makeText(this, "请放置一个模型", Toast.LENGTH_SHORT).show();
                return;
            }
            // 当用户打开scanModelActivity后将用户放置模型相对于相机状态复现
            // 规定一次只放一个节点
            Node node = mSelectNode.getChildren().get(0);
            // 模型相对相机位置
            Vector3 relativePosition = Vector3.subtract(
                    node.getWorldPosition(),
                    mArSceneView.getScene().getCamera().getWorldPosition());
            // 模型缩放大小
            Vector3 modelScale = node.getLocalScale();
            // 模型绕自身旋转
            Quaternion modelRotation = node.getLocalRotation();
            // 构建模型信息
            ModelInfo modelInfo = new ModelInfo(relativePosition, modelScale, modelRotation);
            modelInfo.setModelName(mSelectItem.getItemName());
            modelInfo.setRenderable(node.getRenderable());
            modelInfo.setModelLatLng(mCurrentLatLng);

            new AlertDialog.Builder(PutModelActivity.this)
                    .setTitle("放置模型")
                    .setMessage(String.format("任务名称：%s\n任务地点：%s\n模型名称：%s\n是否想要放置模型?", mCurrentTask.getTaskName(), mModelAddress, mSelectItem.getItemName()))
                    .setPositiveButton("确定", (dialog, which) -> {
                        new Thread(() -> {
                            ModelInfoLab.insertModelInfo(modelInfo, mCurrentTask);
                            Looper.prepare();
                            Toast.makeText(PutModelActivity.this, "放置模型完毕", Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }).start();
                        setResult(RESULT_OK);
                        finish();
                    })
                    .setNegativeButton("取消", null)
                    .show();
        });

        mScanModelButton.setOnClickListener(v -> {
            Intent intent = new Intent(PutModelActivity.this, ScanModelActivity.class);
            startActivity(intent);
        });

        mDeleteModelButton.setOnClickListener(v -> {
            hasSetToPanel = false;
            if (mSelectNode == null) {
                Toast.makeText(PutModelActivity.this, "请放置一个模型", Toast.LENGTH_SHORT).show();
                return;
            }
            // 删除已存在的模型, 和锚点
            for (int i = 0; i < mSelectNode.getChildren().size(); i++)
                mSelectNode.removeChild(mSelectNode.getChildren().get(i));
            mSelectNode.getAnchor().detach();
            mSelectNode = null;
            mPutStatus = ModelPutStatus.DO_NOT_PUT;
            mPutStatusImage.setImageResource(R.drawable.friend_list_item_gray_point);
        });

        mScaleOnTouchListener = (v, event) -> {
            mScaleGestureDetector.onTouchEvent(event);
            return true;
        };

        mSwipeOnTouchListener = (v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    //获取当前按下的坐标
                    startX = (int) event.getRawX();
                    startY = (int) event.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    //获取移动后的坐标
                    float moveX = (int) event.getRawX();
                    float moveY = (int) event.getRawY();
                    //拿到手指移动距离的大小
                    float move_bigX = moveX - startX;
                    float move_bigY = moveY - startY;
                    // 移动模型
                    if (mSelectNode != null) {
                        for (Node node : mSelectNode.getChildren()) {
                            transformNode(move_bigX, move_bigY, node);
                        }
                    }
                    startX = moveX;
                    startY = moveY;
                    break;
                case MotionEvent.ACTION_UP:
                    mArSceneView.performClick();
                    break;
            }
            return true;//此处一定要返回true，否则监听不生效
        };

        mScaleGestureDetector = new ScaleGestureDetector(getApplicationContext(), new ScaleGestureDetector.SimpleOnScaleGestureListener() {
            @Override
            public boolean onScaleBegin(ScaleGestureDetector detector) {
                return true;
            }

            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                float factor = detector.getScaleFactor();
                Log.i(TAG, "focusX = " + detector.getFocusX());       // 缩放中心，x坐标
                Log.i(TAG, "focusY = " + detector.getFocusY());       // 缩放中心y坐标
                Log.i(TAG, "scale = " + detector.getScaleFactor());   // 缩放因子
                for (Node node : mSelectNode.getChildren()) {
                    Vector3 scale = node.getLocalScale();
                    node.setLocalScale(new Vector3(scale.x * factor, scale.y * factor, scale.z * factor));
                }
                return true;
            }

            @Override
            public void onScaleEnd(ScaleGestureDetector detector) {
            }
        });


        mPutStatusImage.setOnClickListener(v -> {
            Toast.makeText(PutModelActivity.this, mPutStatus.toString(), Toast.LENGTH_SHORT).show();
            if (mPutStatus == ModelPutStatus.DO_NOT_PUT) {
                Toast.makeText(PutModelActivity.this, "请放置一个模型", Toast.LENGTH_SHORT).show();
                return;
            }
            // 绿->红
            if (mPutStatus == ModelPutStatus.LR_BA_MODE) {
                mPutStatus = ModelPutStatus.ROTATE_UD_MODE;
                mPutStatusImage.setImageResource(R.drawable.friend_list_item_red_point);
                return;
            }
            // 红->蓝
            if (mPutStatus == ModelPutStatus.ROTATE_UD_MODE) {
                mArSceneView.setOnTouchListener(mScaleOnTouchListener);
                mPutStatus = ModelPutStatus.SCALE_MODE;
                mPutStatusImage.setImageResource(R.drawable.ar_put_status_blue_point);
                return;
            }
            // 蓝->绿
            if (mPutStatus == ModelPutStatus.SCALE_MODE) {
                mArSceneView.setOnTouchListener(mSwipeOnTouchListener);
                mPutStatus = ModelPutStatus.LR_BA_MODE;
                mPutStatusImage.setImageResource(R.drawable.friend_list_item_green_point);
                return;
            }
        });

        mArSceneView.setOnTouchListener(mSwipeOnTouchListener);

        mShowModelItemButton.setOnClickListener((v) -> {
            View view = LayoutInflater.from(this).inflate(R.layout.ar_item_recycle_layout, null, false);
            mItemsRecyclerView = view.findViewById(R.id.ar_items_recycler_view);
            mModelItemsAdapter = new ModelItemsAdapter(this, ModelItemsLab.get().getItemList());
            mItemsRecyclerView.setAdapter(mModelItemsAdapter);
            mModelItemsAdapter.setOnModelItemClickListener(item -> {
                // 如果已经放置模型，则返回
                if (hasSetToPanel) {
                    Toast.makeText(this, "只能放置一个模型", Toast.LENGTH_SHORT).show();
                    return;
                }

                //当Frame处于跟踪状态再继续
                if (mArSceneView.getArFrame().getCamera().getTrackingState() != TrackingState.TRACKING) {
                    Toast.makeText(PutModelActivity.this, "NO_TRACKING", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (item.getModelItemType() == ModelItemType.VIEW) {
                    ARUtils.buildViewRenderable(this, mCurrentTask, item, renderable -> {
                        mViewRenderable = (ViewRenderable) renderable;
                        createAnchorNode(mArSceneView, mViewRenderable);
                        Toast.makeText(this, "View renderable build finish", Toast.LENGTH_SHORT).show();
                    });

                } else if (item.getModelItemType() == ModelItemType.MODEL) {
                    ARUtils.buildModelRenderable(this, item, renderable -> {
                        mModelRenderable = (ModelRenderable) renderable;
                        createAnchorNode(mArSceneView, mModelRenderable);
                        Toast.makeText(this, "Model renderable build finish", Toast.LENGTH_SHORT).show();
                    });
                }
                // 获取 item
                mSelectItem = item;
                // 获取当前位置
                mCurrentLatLng = new LatLng(
                        LocationService.getCurrentLocation().getLatitude(),
                        LocationService.getCurrentLocation().getLongitude());
                MapUtils.latLng2Address(mCurrentLatLng, address -> mModelAddress = address);
                hasSetToPanel = true;
                mPutStatus = ModelPutStatus.LR_BA_MODE;
                mPutStatusImage.setImageResource(R.drawable.friend_list_item_green_point);
                mItemsAlertDialog.dismiss();
            });
            mItemsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            // 弹出dialog
            mItemsAlertDialog = new AlertDialog.Builder(this)
                    .setTitle("选择模型")
                    .setView(view)
                    .create();
            mItemsAlertDialog.show();
            // 设置dialog宽高
            mItemsAlertDialog.getWindow().setLayout(ARUtils.dp2px(this, 340), ARUtils.dp2px(this, 500));
        });
    }

    void initView() {
        mArSceneView = findViewById(R.id.aomodel_put_ar_scene_view);
        mArSceneView.getPlaneRenderer().setEnabled(false);
        // 提交放置的模型
        mPutStatusImage = findViewById(R.id.armodel_put_status);
        mSubmitModelButton = findViewById(R.id.armodel_model_submit_button);
        mDeleteModelButton = findViewById(R.id.armodel_model_delete_button);
        mScanModelButton = findViewById(R.id.armodel_model_scan_button);
        mShowModelItemButton = findViewById(R.id.armodel_model_show_button);
    }

    /**
     * 根据手指滑动移动模型
     *
     * @param offset_x 滑动时x方向偏移量
     * @param offset_y 滑动时y方向偏移量
     */
    void transformNode(float offset_x, float offset_y, Node node) {
        if (node == null)
            return;
        Vector3 location = node.getWorldPosition();
        if (mPutStatus == ModelPutStatus.DO_NOT_PUT)
            return;
        if (mPutStatus == ModelPutStatus.LR_BA_MODE) {
            if (Math.abs(offset_x) > Math.abs(offset_y)) {
                // 左右-左右围绕移动
                if (Math.abs(offset_x) < SLIDE_EFFECTIVE_DISTANCE)
                    return;
                // 获取当前手机camera坐标
                Vector3 center = mArSceneView.getScene().getCamera().getWorldPosition();
                // 计算模型绕camera旋转一定角度后坐标
                location = Vector3Utils.rotateAroundY(center, location, -offset_x / 10);
                node.setWorldPosition(location);
            } else {
                // 上下-前后移动
                if (Math.abs(offset_y) < SLIDE_EFFECTIVE_DISTANCE)
                    return;
                Vector3 rotation = getCameraEulerRotation();

                // 计算与当前相机在同一方向上的距离向量
                Vector3 distance = Vector3Utils.rotateAroundY(new Vector3(0, 0, -offset_y / 500), rotation.y);
                // 当前模型坐标加上距离向量（此时模型沿相机向前后移动）
                location = Vector3.add(distance, location);
                node.setWorldPosition(location);
            }
        }
        if (mPutStatus == ModelPutStatus.ROTATE_UD_MODE) {
            if (Math.abs(offset_x) > Math.abs(offset_y)) {
                // 左右-旋转
                if (Math.abs(offset_x) < SLIDE_EFFECTIVE_DISTANCE)
                    return;
                Log.d(TAG, "transformNode: " + mNodeRotation);
                mNodeRotation = new Vector3(mNodeRotation.x, mNodeRotation.y + offset_x, mNodeRotation.z);
                node.setLocalRotation(new Quaternion(Quaternion.eulerAngles(mNodeRotation)));
            } else {
                // 上下-上下移动
                if (Math.abs(offset_y) < SLIDE_EFFECTIVE_DISTANCE)
                    return;
                node.setWorldPosition(new Vector3(location.x, location.y + offset_y / 500, location.z));
            }
        }

    }

    void createAnchorNode(ArSceneView arSceneView, Renderable renderable) {
        Toast.makeText(this, "createAnchorNode", Toast.LENGTH_SHORT).show();
        // 放置模型到前方
        Vector3 distance = new Vector3(0, -0.5f, -1);

        // 获取相机当前旋转欧拉角
        Vector3 rotation = getCameraEulerRotation();

        // 计算模型沿Y轴旋转相机旋转角度（此时模型在相机同一方向）
        distance = Vector3Utils.rotateAroundY(distance, rotation.y);

        // camera的local position几乎不会变动，所以此处使用getWorldPosition
        // 相机位置加上沿相机正对方向距离（此时模型在相机前方）
        Vector3 transform = Vector3.add(arSceneView.getScene().getCamera().getWorldPosition(), distance);

        Pose pose = Pose.makeTranslation(transform.x, transform.y, transform.z);
        Anchor anchor = arSceneView.getSession().createAnchor(pose);
        AnchorNode anchorNode = new AnchorNode(anchor);
        Node node = new Node();
        node.setRenderable(renderable);
        anchorNode.setParent(arSceneView.getScene());
        anchorNode.addChild(node);
        mSelectNode = anchorNode;
    }

    /**
     * 获取相机当前旋转欧拉角
     *
     * @return
     */
    Vector3 getCameraEulerRotation() {
        Camera camera = mArSceneView.getScene().getCamera();
        // 获取当前相机旋转的四元数并转化为欧拉角
        Vector3 rotation = Vector3Utils.quaternion2Euler(camera.getWorldRotation());
        // 通过绕x轴旋转确定绕y轴旋转
        // if x>0 y={90,-90} else y = {180-90,180-(-90)}
        if (rotation.x > 0) {
            rotation.y = 180 - rotation.y;
        }
        return rotation;
    }

    @Override
    protected void onResume() {
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
        super.onPause();
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
}