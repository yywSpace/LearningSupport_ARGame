package com.example.learningsupport_argame.ARModel;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.learningsupport_argame.ARModel.Items.Item;
import com.example.learningsupport_argame.ARModel.Items.ItemType;
import com.example.learningsupport_argame.ARModel.Items.ItemsAdapter;
import com.example.learningsupport_argame.ARModel.Items.ItemsLab;
import com.example.learningsupport_argame.ARModel.Items.ModelInfo;
import com.example.learningsupport_argame.ARModel.Items.ModelInfoLab;
import com.example.learningsupport_argame.ARModel.Utils.DemoUtils;
import com.example.learningsupport_argame.ARModel.Utils.LocationSensor;
import com.example.learningsupport_argame.ARModel.Utils.Utils;
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
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.Renderable;
import com.google.ar.sceneform.rendering.ViewRenderable;

import java.util.List;


public class PutModelActivity extends AppCompatActivity {
    private static String TAG = PutModelActivity.class.getSimpleName();
    private PopupWindow mItemsPopupWindow;
    private ImageView mPutStatusImage;
    private FloatingActionButton mShowModelItemButton;
    private FloatingActionButton mSubmitModelButton;
    private FloatingActionButton mDeleteModelButton;
    private FloatingActionButton mScanModelButton;
    private RecyclerView mItemsRecyclerView;
    private ItemsAdapter mItemsAdapter;
    private boolean hasShowItemsPopupWindow = false;// 是否显示了 PopupWindow
    private List<Item> mItems;
    private List<ModelInfo> mModelInfoList;
    private ItemsLab mItemsLab;
    private ModelInfoLab mModelInfoLab;

    private boolean hasSetToPanel = false;
    private ViewRenderable mViewRenderable;
    private ModelRenderable mModelRenderable;

    private ArSceneView mArSceneView;
    private AnchorNode mSelectNode;// 当前选中的model
    private ModelPutStatus mPutStatus;
    /**
     * 代表当前选中节点的旋转
     */
    private Vector3 mNodeRotation;
    private int SLIDE_EFFECTIVE_DISTANCE = 3;

    LocationSensor mLocationSensor;
    ModelInfo modelInfo;
    private boolean installRequested;
    float startX;
    float startY;
    private ScaleGestureDetector mScaleGestureDetector;
    View.OnTouchListener mScaleOnTouchListener, mSwipeOnTouchListener;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!DemoUtils.checkIsSupportedDeviceOrFinish(this))
            return;

        mLocationSensor = LocationSensor.get(this);

        setContentView(R.layout.armodel_activity_put_model);

        mItemsLab = ItemsLab.get();
        mItems = mItemsLab.getItemList();
        mModelInfoLab = ModelInfoLab.get();
        mModelInfoList = mModelInfoLab.getModelInfoList();
        mNodeRotation = new Vector3();
        mPutStatus = ModelPutStatus.DO_NOT_PUT;
        initView();
        initEvent();

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
            Node node = mSelectNode.getChildren().get(0);
            ModelInfo modelInfo = new ModelInfo(node.getLocalRotation(), node.getLocalScale(), node.getLocalPosition());
            modelInfo.setRenderable(node.getRenderable());
            ModelInfoLab.get().setCurrentModelInfo(modelInfo);
            Toast.makeText(this, "放置模型完毕", Toast.LENGTH_SHORT).show();
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

    }

    void initView() {
        mArSceneView = findViewById(R.id.aomodel_put_ar_scene_view);
        mArSceneView.getPlaneRenderer().setEnabled(false);
        // 提交放置的模型
        mPutStatusImage = findViewById(R.id.armodel_put_status);
        mSubmitModelButton = findViewById(R.id.armodel_model_submit_button);
        mDeleteModelButton = findViewById(R.id.armodel_model_delete_button);
        mScanModelButton = findViewById(R.id.armodel_model_scan_button);

        // 道具相关
        // TODO: 19-7-14  item之间增加间距，采用瀑布流形式（每行数量固定，可上下滑动），长按item显示提示信息，酌情美化
        mShowModelItemButton = findViewById(R.id.armodel_model_show_button);
        View contentView = LayoutInflater.from(this).inflate(R.layout.armodel_popwindow_items, null, false);
        mItemsRecyclerView = contentView.findViewById(R.id.armodel_popwindow_items_recycler_view);
        mItemsAdapter = new ItemsAdapter(this, mItems);
        mItemsRecyclerView.setAdapter(mItemsAdapter);
        mItemsAdapter.setOnModelItemClickListener(item -> {
            mItemsPopupWindow.dismiss();
            hasShowItemsPopupWindow = false;
            // 如果已经放置模型，则返回
            if (hasSetToPanel) {
                Toast.makeText(this, "只能放置一个模型", Toast.LENGTH_SHORT).show();
                return;
            }
            hasSetToPanel = true;

            //当Frame处于跟踪状态再继续
            if (mArSceneView.getArFrame().getCamera().getTrackingState() != TrackingState.TRACKING) {
                Toast.makeText(PutModelActivity.this, "NO_TRACKING", Toast.LENGTH_SHORT).show();
                return;
            }

            if (item.getItemType() == ItemType.VIEW) {
                ViewRenderable.builder()
                        .setView(this, item.getViewId())
                        .build()
                        .thenAccept(renderable -> {
                            mViewRenderable = renderable;
                            createAnchorNode(mArSceneView, mViewRenderable);
                            Toast.makeText(this, "ViewRenderable build finish", Toast.LENGTH_SHORT).show();
                        })
                        .exceptionally(
                                throwable -> {
                                    Log.e(TAG, "Unable to load Renderable.", throwable);
                                    return null;
                                });
            } else if (item.getItemType() == ItemType.MODEL) {
                ModelRenderable.builder()
                        .setSource(this, Uri.parse(item.getModelPath()))
                        .build()
                        .thenAccept(renderable -> {
                            mModelRenderable = renderable;
                            createAnchorNode(mArSceneView, mModelRenderable);
                            Toast.makeText(this, "ModelRenderable build finish", Toast.LENGTH_SHORT).show();
                        })
                        .exceptionally(
                                throwable -> {
                                    Log.e(TAG, "Unable to load Renderable.", throwable);
                                    return null;
                                });
            }
            mPutStatus = ModelPutStatus.LR_BA_MODE;
            mPutStatusImage.setImageResource(R.drawable.friend_list_item_green_point);
        });
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.HORIZONTAL);
        mItemsRecyclerView.setLayoutManager(llm);

        // 设置 PopupWindow 的宽度为比屏幕像素少一些
        mItemsPopupWindow = new PopupWindow(contentView,
                getResources().getDisplayMetrics().widthPixels - (Utils.dp2px(this, 50) + 147 + 20),
                ViewGroup.LayoutParams.WRAP_CONTENT, false);

        mShowModelItemButton.setOnClickListener(v -> {
            // 如果已经显示PopupWindow则销毁，否则创建
            if (!hasShowItemsPopupWindow) {
                mItemsPopupWindow.showAsDropDown(
                        mShowModelItemButton,
                        -(getResources().getDisplayMetrics().widthPixels - (Utils.dp2px(this, 50) + 147)),
                        -(mShowModelItemButton.getHeight() / 2 + Utils.dp2px(PutModelActivity.this, 50) / 2));
                hasShowItemsPopupWindow = true;
            } else {
                mItemsPopupWindow.dismiss();
                hasShowItemsPopupWindow = false;
            }
        });
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
                // 左右-左右移动
                if (Math.abs(offset_x) < SLIDE_EFFECTIVE_DISTANCE)
                    return;
                node.setWorldPosition(new Vector3(location.x - offset_x / 500, location.y, location.z));
            } else {
                // 上下-前后移动
                if (Math.abs(offset_y) < SLIDE_EFFECTIVE_DISTANCE)
                    return;
                node.setWorldPosition(new Vector3(location.x, location.y, location.z - offset_y / 500));
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
        Vector3 transform = new Vector3(0, -0.5f, -1);
        transform = Vector3.add(arSceneView.getScene().getCamera().getWorldPosition(), transform);
        Pose pose = Pose.makeTranslation(transform.x, transform.y, transform.z);
        Anchor anchor = arSceneView.getSession().createAnchor(pose);
        AnchorNode anchorNode = new AnchorNode(anchor);
        Node node = new Node();
        node.setRenderable(renderable);
        anchorNode.setParent(arSceneView.getScene());
        anchorNode.addChild(node);
        mSelectNode = anchorNode;
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

    class ScaleGesture extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            return super.onScaleBegin(detector);
        }

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            return super.onScale(detector);
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            super.onScaleEnd(detector);
        }
    }

}

