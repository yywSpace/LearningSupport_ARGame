package com.example.learningsupport_argame.ARModel.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.example.learningsupport_argame.ARModel.Items.ModelItem;
import com.google.ar.sceneform.Camera;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.Renderable;
import com.google.ar.sceneform.rendering.ViewRenderable;

public class ARUtils {
    private static final String TAG = "ARUtils";



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

    public static void buildViewRenderable(Context context, ModelItem item, OnRenderableBuildListener renderableBuildListener) {
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

    public static void buildModelRenderable(Context context, ModelItem item, OnRenderableBuildListener renderableBuildListener) {
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

    /**
     * 为bitmap设置新的宽高
     *
     * @param bm        要设置的bitmap
     * @param newWidth  新的宽
     * @param newHeight 新的高
     * @return
     */
    public static Bitmap zoomImage(Bitmap bm, int newWidth, int newHeight) {
        // 获得图片的宽高
        int width = bm.getWidth();
        int height = bm.getHeight();
        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片
        Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
        return newbm;
    }


    public interface OnRenderableBuildListener {
        void onRenderableBuild(Renderable renderable);
    }
}
