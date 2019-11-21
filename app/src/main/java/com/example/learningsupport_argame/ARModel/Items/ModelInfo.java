package com.example.learningsupport_argame.ARModel.Items;

import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.Renderable;

public class ModelInfo {
    private Quaternion mRotation; // 模型本地旋转角度
    private Vector3 mScale; // 模型世界缩放大小
    private Vector3 mRelativePosition; // 模型相对于相机坐标
    private Renderable mRenderable;

    public ModelInfo(Vector3 relativePosition, Vector3 scale, Quaternion rotation) {
        setRotation(rotation);
        setRelativePosition(relativePosition);
        setScale(scale);
    }

    public Renderable getRenderable() {
        return mRenderable;
    }

    public void setRenderable(Renderable renderable) {
        mRenderable = renderable;
    }

    public Vector3 getRelativePosition() {
        return mRelativePosition;
    }

    public void setRelativePosition(Vector3 relativePosition) {
        mRelativePosition = relativePosition;
    }

    public Quaternion getRotation() {
        return mRotation;
    }

    public void setRotation(Quaternion rotation) {
        mRotation = rotation;
    }

    public Vector3 getScale() {
        return mScale;
    }

    public void setScale(Vector3 scale) {
        mScale = scale;
    }
}
