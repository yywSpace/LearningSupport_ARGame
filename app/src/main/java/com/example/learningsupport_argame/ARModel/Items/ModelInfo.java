package com.example.learningsupport_argame.ARModel.Items;

import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.Renderable;

public class ModelInfo {
    private Quaternion mModelRotation; // 模型本地旋转角度
    private Vector3 mModelScale; // 模型世界缩放大小
    private Vector3 mModelPosition; // 模型世界坐标
    private Renderable mRenderable;

    public ModelInfo(Quaternion rotation, Vector3 scale, Vector3 position) {
        setModelRotation(rotation);
        setModelPosition(position);
        setModelScale(scale);
    }

    public Quaternion getModelRotation() {
        return mModelRotation;
    }

    public void setModelRotation(Quaternion modelRotation) {
        mModelRotation = modelRotation;
    }

    public Vector3 getModelScale() {
        return mModelScale;
    }

    public void setModelScale(Vector3 modelScale) {
        mModelScale = modelScale;
    }

    public Vector3 getModelPosition() {
        return mModelPosition;
    }

    public void setModelPosition(Vector3 modelPosition) {
        mModelPosition = modelPosition;
    }

    public Renderable getRenderable() {
        return mRenderable;
    }

    public void setRenderable(Renderable renderable) {
        mRenderable = renderable;
    }
}
