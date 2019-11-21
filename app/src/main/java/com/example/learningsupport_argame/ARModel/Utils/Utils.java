package com.example.learningsupport_argame.ARModel.Utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.example.learningsupport_argame.ARModel.Items.Item;
import com.google.ar.sceneform.Camera;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.Renderable;
import com.google.ar.sceneform.rendering.ViewRenderable;

public class Utils {
    private static final String TAG = "Utils";

    // dp转像素
    public static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 获取相机当前旋转欧拉角
     *
     * @return
     */
    public static Vector3 getCameraEulerRotation(Camera camera) {
        // 获取当前相机旋转的四元数并转化为欧拉角
        Vector3 rotation = Vector3Utils.quaternion2Euler(camera.getWorldRotation());
        // 通过绕x轴旋转确定绕y轴旋转
        // if x>0 y={90,-90} else y = {180-90,180-(-90)}
        if (rotation.x > 0) {
            rotation.y = 180 - rotation.y;
        }
        return rotation;
    }

    public static void buildViewRenderable(Context context, Item item, OnRenderableBuildListener renderableBuildListener) {
        ViewRenderable.builder()
                .setView(context, item.getViewId())
                .build()
                .thenAccept(renderable -> {
                    if (renderableBuildListener != null)
                        renderableBuildListener.onRenderableBuild(renderable);
                    Toast.makeText(context, "ViewRenderable build finish", Toast.LENGTH_SHORT).show();
                })
                .exceptionally(
                        throwable -> {
                            Log.e(TAG, "Unable to load Renderable.", throwable);
                            return null;
                        });
    }

    public static void buildModelRenderable(Context context, Item item, OnRenderableBuildListener renderableBuildListener) {
        ModelRenderable.builder()
                .setSource(context, Uri.parse(item.getModelPath()))
                .build()
                .thenAccept(renderable -> {
                    if (renderableBuildListener != null)
                        renderableBuildListener.onRenderableBuild(renderable);
                    Toast.makeText(context, "Model renderable build finish", Toast.LENGTH_SHORT).show();
                })
                .exceptionally(
                        throwable -> {
                            Log.e(TAG, "Unable to load Renderable.", throwable);
                            return null;
                        });
    }

    public interface OnRenderableBuildListener {
        void onRenderableBuild(Renderable renderable);
    }
}
