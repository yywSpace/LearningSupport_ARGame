package com.example.learningsupport_argame.ARModel.Items;

import androidx.annotation.NonNull;

import com.example.learningsupport_argame.UserManagement.bag.Item;

public class ModelItem extends Item {
    private int mId;
    private int mImageRes;
    private ModelItemType mModelItemType;
    private int mViewId;
    private String mModelPath;

    public ModelItem(int id, String name, String desc, int viewId, ModelItemType modelItemType) {
        super(name, desc);
        mId = id;
        mModelItemType = modelItemType;
        mViewId = viewId;
    }

    public ModelItem(int id, String name, String desc, String path, int imageRes, ModelItemType modelItemType) {
        super(name, desc);
        mId = id;
        mImageRes = imageRes;
        mModelItemType = modelItemType;
        mModelPath = path;
    }

    public ModelItemType getModelItemType() {
        return mModelItemType;
    }

    public void setModelItemType(ModelItemType modelItemType) {
        mModelItemType = modelItemType;
    }

    public int getViewId() {
        return mViewId;
    }

    public void setViewId(int viewId) {
        mViewId = viewId;
    }

    public String getModelPath() {
        return mModelPath;
    }

    public void setModelPath(String modelPath) {
        mModelPath = modelPath;
    }

    public int getImageRes() {
        return mImageRes;
    }

    public void setImageRes(int imageRes) {
        mImageRes = imageRes;
    }

    @NonNull
    @Override
    public String toString() {
        return String.valueOf(mId);
    }
}
