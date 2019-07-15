package com.example.learningsupport_argame.ARModel.Items;

import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;

public class ModelInfo {
    //private LatLng mCurrentLatLng; // 手机当前经纬度
    private Quaternion mModelRotation; // 模型世界旋转角度
    private Vector3 mModelScale; // 模型世界缩放大小
    private Vector3 mModelPosition; // 模型世界坐标
    private float mCurrentDegree;     // 手机当前朝向
    private Vector3 mCameraPosition; // 相机世界坐标

    public ModelInfo(Quaternion rotation, Vector3 scale, Vector3 position, float currentDegree) {
        setModelRotation(rotation);
        setCurrentDegree(currentDegree);
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

    public float getCurrentDegree() {
        return mCurrentDegree;
    }

    public void setCurrentDegree(float currentDegree) {
        mCurrentDegree = currentDegree;
    }

    public Vector3 getCameraPosition() {
        return mCameraPosition;
    }

    public void setCameraPosition(Vector3 cameraPosition) {
        mCameraPosition = cameraPosition;
    }
}
