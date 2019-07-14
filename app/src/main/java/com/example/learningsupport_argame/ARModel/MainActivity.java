package com.example.learningsupport_argame.ARModel;


import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.learningsupport_argame.ARModel.Utils.Utils;
import com.example.learningsupport_argame.R;
import com.google.ar.core.Anchor;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.core.Pose;
import com.google.ar.core.TrackingState;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.HitTestResult;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.ViewRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;


/**
 * 测试用
 */
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private ArFragment mArFragment;
    private boolean hasSetToPanel = false;
    private ViewRenderable mViewRenderable;
    private ModelRenderable mModelRenderable;
    private TextView mNodeMessageTextView;
    private Button mSetToZeroBtn;
    TransformableNode andy;
    ToggleButton mToggleButton;
    Button mChangeXBtn;
    Button mChangeYBtn;
    Button mChangeZBtn;
    float increment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!Utils.checkIsSupportedDeviceOrFinish(this)) {
            return;
        }

        setContentView(R.layout.armodel_activity_ux);
        mArFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.ux_fragment);
        mNodeMessageTextView = findViewById(R.id.node_message);
        mSetToZeroBtn = findViewById(R.id.set_to_zero);

        mToggleButton = findViewById(R.id.switch_pos_neg);
        mChangeXBtn = findViewById(R.id.change_x_button);
        mChangeYBtn = findViewById(R.id.change_y_button);
        mChangeZBtn = findViewById(R.id.change_z_button);
        mToggleButton.setOnClickListener(v -> {
            if (mToggleButton.isChecked())
                increment = .1f;
            else
                increment = -.1f;
            Toast.makeText(this, "" + increment, Toast.LENGTH_SHORT).show();
        });
        mChangeXBtn.setOnClickListener(v -> {
            if (andy != null)
                andy.setWorldPosition(
                        new Vector3(andy.getWorldPosition().x + increment, andy.getWorldPosition().y, andy.getWorldPosition().z));
        });
        mChangeYBtn.setOnClickListener(v -> {
            if (andy != null)
                andy.setWorldPosition(
                        new Vector3(andy.getWorldPosition().x, andy.getWorldPosition().y + increment, andy.getWorldPosition().z));
        });
        mChangeZBtn.setOnClickListener(v -> {
            if (andy != null)
                andy.setWorldPosition(
                        new Vector3(andy.getWorldPosition().x, andy.getWorldPosition().y, andy.getWorldPosition().z + increment));
        });
        ViewRenderable.builder()
                .setView(this, R.layout.armodel_view_renderable_text)
                .build()
                .thenAccept(renderable -> mViewRenderable = renderable)
                .exceptionally(
                        throwable -> {
                            Log.e(TAG, "Unable to load Renderable.", throwable);
                            return null;
                        });


        ModelRenderable.builder()
                // To load as an asset from the 'assets' folder ('src/main/assets/andy.sfb'):
                .setSource(this, Uri.parse("andy.sfb"))

                // Instead, load as a resource from the 'res/raw' folder ('src/main/res/raw/andy.sfb'):
                //.setSource(this, R.raw.andy)

                .build()
                .thenAccept(renderable -> mModelRenderable = renderable)
                .exceptionally(
                        throwable -> {
                            Log.e(TAG, "Unable to load Renderable.", throwable);
                            return null;
                        });
        mSetToZeroBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Vector3 transform = new Vector3(0, -.5f, -1);
                // Pose可理解为带有Rotation和Transform的一个描述对象
                // TODO: 19-7-14 在显示模型时可同时设置 Rotation和Transform, 测试Transform和Rotation, 了解Point和Pose的区别
                Pose pose = Pose.makeTranslation(transform.x, transform.y, transform.z);
                //当Frame处于跟踪状态再继续
                if (mArFragment.getArSceneView().getArFrame().getCamera().getTrackingState() != TrackingState.TRACKING) {
                    Toast.makeText(MainActivity.this, "NO_TRACKING", Toast.LENGTH_SHORT).show();
                    return;
                }
                Anchor anchor = mArFragment.getArSceneView().getSession().createAnchor(pose);
                AnchorNode anchorNode = new AnchorNode(anchor);
                anchorNode.setParent(mArFragment.getArSceneView().getScene());
                //anchorNode.setRenderable(mModelRenderable);
                andy = new TransformableNode(mArFragment.getTransformationSystem());
                andy.setParent(anchorNode);
                andy.setRenderable(mModelRenderable);
                andy.select();
                Toast.makeText(MainActivity.this, anchor.getPose() +":"+ andy.getName(), Toast.LENGTH_SHORT).show();

                andy.setOnTouchListener(new Node.OnTouchListener() {
                    @Override
                    public boolean onTouch(HitTestResult hitTestResult, MotionEvent motionEvent) {
                        mNodeMessageTextView.setText(
                                "Rotation:" + andy.getWorldRotation() + "\n" +
                                        "MaxScale:" + andy.getWorldScale() + "\n" +
                                        "Position:" + andy.getWorldPosition());
                        Log.d(TAG, "Rotation:" + andy.getWorldRotation() + "\n" +
                                "MaxScale:" + andy.getWorldScale() + "\n" +
                                "Position:" + andy.getWorldPosition());
                        return false;
                    }
                });
            }

        });

        mArFragment.setOnTapArPlaneListener(
                (HitResult hitResult, Plane plane, MotionEvent motionEvent) -> {
                    if (mModelRenderable == null || hasSetToPanel == true) {
                        return;
                    }
                    Log.d(TAG, "model renderable");
                    // Create the Anchor.
                    Anchor anchor = hitResult.createAnchor();
                    AnchorNode anchorNode = new AnchorNode(anchor);
                    anchorNode.setParent(mArFragment.getArSceneView().getScene());
                    //anchorNode.setRenderable(mViewRenderable);
                    // Create the transformable andy and add it to the anchor.
                    TransformableNode andy = new TransformableNode(mArFragment.getTransformationSystem());
                    andy.setParent(anchorNode);
                    andy.setRenderable(mModelRenderable);
                    andy.select();

                });
    }


}
