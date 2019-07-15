package com.example.learningsupport_argame.ARModel;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.learningsupport_argame.ARModel.Items.Item;
import com.example.learningsupport_argame.ARModel.Items.ItemType;
import com.example.learningsupport_argame.ARModel.Items.ItemsAdapter;
import com.example.learningsupport_argame.ARModel.Items.ItemsLab;
import com.example.learningsupport_argame.ARModel.Items.ModelInfo;
import com.example.learningsupport_argame.ARModel.Items.ModelInfoLab;
import com.example.learningsupport_argame.ARModel.Utils.LocationSensor;
import com.example.learningsupport_argame.ARModel.Utils.Utils;
import com.example.learningsupport_argame.R;
import com.google.ar.core.Anchor;
import com.google.ar.core.Frame;
import com.google.ar.core.Plane;
import com.google.ar.core.Pose;
import com.google.ar.core.TrackingState;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.Camera;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.Renderable;
import com.google.ar.sceneform.rendering.ViewRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

import java.util.ArrayList;
import java.util.List;

// TODO: 19-7-14 模型放置，扫描流程
// 假设在放置第一个模型的时候确定手机位置为世界坐标 (0 0 0)
// 放置节点
// 放置时记录当前手机当前经纬度作为原点, 当前手机朝向为初始朝向
// 记录模型所放置模型的参数,记录当前相机位置参数
// 根据当前经纬度，和原点经纬度，当前及初始手机朝向，以及模型参数算出模型距当前位置参数
// TODO: 19-7-14 如何在放置模型时组合模型
// 放置不同模型到屏幕上
// 记录每个模型的世界坐标
// 选择一个主体模型（root）,将其他模型世界坐标转化为局部坐标
// 记录数据
// TODO: 19-7-14 模型移动优化
// 类似Unity3d模型移动方式
// TODO: 19-7-14 点击物品栏模型，显示模型详细信息
// 点击后弹出模型界面，可对模型进行放大缩小，旋转等操作

/**
 * <p>两种放置方法</p>
 * <ol>
 * <li>点击模型，用XYZ按钮确定位置（可放置任意位置）</li>
 * <li>手机扫描出平面后，直接将模型拖动至平面（只能在检测出的平面上）</li>
 * </ol>
 *
 * <p>此类用于放置模型，放置模型时要记录模型的各项信息包括:</p>
 * <ol>
 * <li>手机当前经纬度</li>
 * <li>手机当前朝向</li>
 * <li>模型世界坐标</li>
 * <li>模型世界旋转角度</li>
 * <li>模型世界缩放大小</li>
 * <li>相机世界坐标</li>
 * <li>相机世界旋转角度</li>
 * <li>相机世界缩放大小</li>
 * </ol>
 */
public class PutModel extends AppCompatActivity {
    private static String TAG = PutModel.class.getSimpleName();
    private PopupWindow mItemsPopupWindow;
    private FloatingActionButton mShowModelItemButton;
    private FloatingActionButton mSubmitModelButton;
    private FloatingActionButton mScanModelButton;
    private RecyclerView mItemsRecyclerView;
    private ItemsAdapter mItemsAdapter;
    private boolean hasShowItemsPopupWindow = false;// 是否显示了 PopupWindow
    private List<Item> mItems;
    private List<ModelInfo> mModelInfos;
    private ItemsLab mItemsLab;
    private ModelInfoLab mModelInfoLab;

    private ArFragment mArFragment;
    private TextView mNodeMessageTextView;
    private boolean hasSetToPanel = false;
    private ViewRenderable mViewRenderable;
    private ModelRenderable mModelRenderable;

    private TransformableNode mSelectModel;// 当前选中的model
    private ToggleButton mToggleButton;
    private Button mChangeXBtn;
    private Button mChangeYBtn;
    private Button mChangeZBtn;
    private float increment = .1f;

    LocationSensor mLocationSensor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!Utils.checkIsSupportedDeviceOrFinish(this)) {
            return;
        }
        mLocationSensor = LocationSensor.get(this);

        setContentView(R.layout.armodel_activity_put_model);
        mArFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.armodel_put_fragment);

        mNodeMessageTextView = findViewById(R.id.armodel_node_message);
        mItemsLab = ItemsLab.get();
        mItems = mItemsLab.getItemList();
        // 提交放置的模型
        mModelInfoLab = ModelInfoLab.get();
        mModelInfos = mModelInfoLab.getModelInfoList();
        mSubmitModelButton = findViewById(R.id.armodel_model_submit_button);
        mSubmitModelButton.setOnClickListener(v -> {
            if (mSelectModel == null)
                return;
            Toast.makeText(this, "放置模型成功", Toast.LENGTH_SHORT).show();
            ModelInfo modelInfo = new ModelInfo(
                    mSelectModel.getWorldRotation(),
                    mSelectModel.getWorldScale(),
                    mSelectModel.getWorldPosition(),
                    mLocationSensor.getCurrentDegree());
            modelInfo.setCameraPosition(mArFragment.getArSceneView().getScene().getCamera().getWorldPosition());
            mModelInfos.add(modelInfo);
        });
        mScanModelButton= findViewById(R.id.armodel_model_scan_button);
        mScanModelButton.setOnClickListener(v -> {
            Intent intent = new Intent(PutModel.this,ScanModel.class);
            startActivity(intent);
        });

        // 初始化布局
        mToggleButton = findViewById(R.id.armodel_switch_pos_neg);
        mChangeXBtn = findViewById(R.id.armodel_change_x_button);
        mChangeYBtn = findViewById(R.id.armodel_change_y_button);
        mChangeZBtn = findViewById(R.id.armodel_change_z_button);
        mToggleButton.setOnClickListener(v -> {
            increment = -increment;
            Toast.makeText(this, "" + increment, Toast.LENGTH_SHORT).show();
        });
        mChangeXBtn.setOnClickListener(v -> {
            if (mSelectModel != null)
                mSelectModel.setWorldPosition(
                        new Vector3(mSelectModel.getWorldPosition().x + increment, mSelectModel.getWorldPosition().y, mSelectModel.getWorldPosition().z));
        });
        mChangeYBtn.setOnClickListener(v -> {
            if (mSelectModel != null)
                mSelectModel.setWorldPosition(
                        new Vector3(mSelectModel.getWorldPosition().x, mSelectModel.getWorldPosition().y + increment, mSelectModel.getWorldPosition().z));
        });
        mChangeZBtn.setOnClickListener(v -> {
            if (mSelectModel != null)
                mSelectModel.setWorldPosition(
                        new Vector3(mSelectModel.getWorldPosition().x, mSelectModel.getWorldPosition().y, mSelectModel.getWorldPosition().z + increment));
        });

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
//            if (!hasSetToPanel)
//                return;
//            hasSetToPanel = true;

            // TODO: 19-7-14 找到方法，让Viewrenderable可以从item中读取View ID
            if (item.getItemType() == ItemType.VIEW) {
                ViewRenderable.builder()
                        .setView(this, item.getViewId())
                        .build()
                        .thenAccept(renderable -> {
                            mViewRenderable = renderable;
                            createAnchorNode(mArFragment, mViewRenderable);
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
                            createAnchorNode(mArFragment, mModelRenderable);
                            Toast.makeText(this, "ModelRenderable build finish", Toast.LENGTH_SHORT).show();
                        })
                        .exceptionally(
                                throwable -> {
                                    Log.e(TAG, "Unable to load Renderable.", throwable);
                                    return null;
                                });
            }
            Toast.makeText(PutModel.this, item.getItemName(), Toast.LENGTH_SHORT).show();
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
                        -(mShowModelItemButton.getHeight() / 2 + Utils.dp2px(PutModel.this, 50) / 2));
                hasShowItemsPopupWindow = true;
            } else {
                mItemsPopupWindow.dismiss();
                hasShowItemsPopupWindow = false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mLocationSensor.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mLocationSensor.onPause();
    }

    void createAnchorNode(ArFragment arFragment, Renderable renderable) {

        Toast.makeText(this, "createAnchorNode", Toast.LENGTH_SHORT).show();
        Vector3 transform = new Vector3(0, 0, -1);
        // Pose可理解为带有Rotation和Transform的一个描述对象
        // TODO: 19-7-14 在显示模型时可同时设置 Rotation和Transform, 测试Transform和Rotation, 了解Point和Pose的区别
        Pose pose = Pose.makeTranslation(transform.x, transform.y, transform.z);
        if (arFragment == null) {
            Toast.makeText(PutModel.this, "arFragment == null", Toast.LENGTH_SHORT).show();
            return;
        }
        //当Frame处于跟踪状态再继续
        if (arFragment.getArSceneView().getArFrame().getCamera().getTrackingState() != TrackingState.TRACKING) {
            Toast.makeText(PutModel.this, "NO_TRACKING", Toast.LENGTH_SHORT).show();
            return;
        }
        Anchor anchor = arFragment.getArSceneView().getSession().createAnchor(pose);
        AnchorNode anchorNode = new AnchorNode(anchor);
        anchorNode.setParent(arFragment.getArSceneView().getScene());
        // anchorNode.setRenderable(mModelRenderable);
        // getTransformationSystem().getSelectNode();
        TransformableNode model = new TransformableNode(arFragment.getTransformationSystem());
        model.setParent(anchorNode);
        model.setRenderable(renderable);
        // TODO: 19-7-14 尝试永久让一个模型选中（模型下方会出现光圈）
        model.select();
        Toast.makeText(PutModel.this, anchor.getPose() + "", Toast.LENGTH_SHORT).show();
        model.setOnTapListener((hitTestResult, motionEvent) -> {
            Camera camera = mArFragment.getArSceneView().getScene().getCamera();
            model.select();
            mSelectModel = model;
            mNodeMessageTextView.setText(
                    "Camera\n" +
                            "Position:" + camera.getWorldPosition() + "\n" +
                            "Rotation:" + camera.getWorldRotation() + "n" +
                            "MaxScale:" + camera.getWorldScale() + "\n" +
                            "Position:" + camera.getWorldPosition() + "\n" +
                            "Model\n" +
                            "Rotation:" + model.getWorldRotation() + "n" +
                            "MaxScale:" + model.getWorldScale() + "\n" +
                            "Position:" + model.getWorldPosition());
            Log.d(TAG, "Rotation:" + model.getWorldRotation() + "\n" +
                    "MaxScale:" + model.getWorldScale() + "\n" +
                    "Position:" + model.getWorldPosition());
        });
    }
}

