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
import android.widget.Toast;

import com.example.learningsupport_argame.R;
import com.google.ar.core.Anchor;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.HitTestResult;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.ViewRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private ArFragment mArFragment;
    private static final double MIN_OPENGL_VERSION = 3.0;
    private ViewRenderable mViewRenderable;
    private ModelRenderable mModelRenderable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!checkIsSupportedDeviceOrFinish(this)) {
            return;
        }

        setContentView(R.layout.activity_ux);
        mArFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.ux_fragment);

        ViewRenderable.builder()
                .setView(this, R.layout.view_renderable_text)
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


        mArFragment.setOnTapArPlaneListener(
                (HitResult hitResult, Plane plane, MotionEvent motionEvent) -> {
                    if (mModelRenderable == null) {
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
                    andy.setOnTouchListener(new Node.OnTouchListener() {
                        @Override
                        public boolean onTouch(HitTestResult hitTestResult, MotionEvent motionEvent) {
                            Log.d(TAG, "Rotation:" + andy.getWorldRotation() + "\n" +
                                    "MaxScale:" + andy.getWorldScale() + "\n" +
                                    "Position:" + andy.getWorldPosition());
                            return false;
                        }
                    });
                });
    }

    /**
     * Returns false and displays an error message if Sceneform can not run, true if Sceneform can run
     * on this device.
     *
     * <p>Sceneform requires Android N on the device as well as OpenGL 3.0 capabilities.
     *
     * <p>Finishes the activity if Sceneform can not run
     */
    public static boolean checkIsSupportedDeviceOrFinish(final Activity activity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            Log.e(TAG, "Sceneform requires Android N or later");
            Toast.makeText(activity, "Sceneform requires Android N or later", Toast.LENGTH_LONG).show();
            activity.finish();
            return false;
        }
        String openGlVersionString =
                ((ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE))
                        .getDeviceConfigurationInfo()
                        .getGlEsVersion();
        if (Double.parseDouble(openGlVersionString) < MIN_OPENGL_VERSION) {
            Log.e(TAG, "Sceneform requires OpenGL ES 3.0 later");
            Toast.makeText(activity, "Sceneform requires OpenGL ES 3.0 or later", Toast.LENGTH_LONG)
                    .show();
            activity.finish();
            return false;
        }
        return true;
    }
}
