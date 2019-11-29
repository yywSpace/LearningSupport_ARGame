package com.example.learningsupport_argame.ARModel.Items;

import com.baidu.mapapi.model.LatLng;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.Renderable;

public class ModelInfo {
    private int mTaskId;
    private String mModelName;
    private Quaternion mLocalRotation; // 模型本地旋转角度
    private Vector3 mLocalScale; // 模型世界缩放大小
    private Vector3 mRelativePosition; // 模型相对于相机坐标
    private LatLng mModelLatLng;
    private boolean mHasVibratorShaken;
    private Renderable mRenderable;

    public ModelInfo() {

    }

    public ModelInfo(Vector3 relativePosition, Vector3 localScale, Quaternion localRotation) {
        setLocalRotation(localRotation);
        setRelativePosition(relativePosition);
        setLocalScale(localScale);
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

    public Quaternion getLocalRotation() {
        return mLocalRotation;
    }

    public void setLocalRotation(Quaternion localRotation) {
        mLocalRotation = localRotation;
    }

    public Vector3 getLocalScale() {
        return mLocalScale;
    }

    public void setLocalScale(Vector3 localScale) {
        mLocalScale = localScale;
    }

    public String getModelName() {
        return mModelName;
    }

    public void setModelName(String modelName) {
        mModelName = modelName;
    }

    public LatLng getModelLatLng() {
        return mModelLatLng;
    }

    public void setModelLatLng(LatLng modelLatLng) {
        mModelLatLng = modelLatLng;
    }

    public int getTaskId() {
        return mTaskId;
    }

    public void setTaskId(int taskId) {
        mTaskId = taskId;
    }

    @Override
    public String toString() {
        return "ModelInfo{" +
                "mTaskId=" + mTaskId +
                ", mModelName='" + mModelName + '\'' +
                ", mLocalRotation=" + mLocalRotation +
                ", mLocalScale=" + mLocalScale +
                ", mRelativePosition=" + mRelativePosition +
                ", mModelLatLng=" + mModelLatLng +
                ", mRenderable=" + mRenderable +
                '}';
    }

    public boolean isHasVibratorShaken() {
        return mHasVibratorShaken;
    }

    public void setHasVibratorShaken(boolean hasVibratorShaken) {
        mHasVibratorShaken = hasVibratorShaken;
    }
}
