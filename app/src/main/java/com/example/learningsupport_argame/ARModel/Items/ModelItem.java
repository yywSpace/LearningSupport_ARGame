package com.example.learningsupport_argame.ARModel.Items;

import com.example.learningsupport_argame.UserManagement.bag.Item;

public class ModelItem extends Item {
    private ModelItemType mModelItemType;
    private int mViewId;
    private String mModelPath;

    public ModelItem(String name, String desc, int id, ModelItemType modelItemType) {
        super(name, desc);
        mModelItemType = modelItemType;
        mViewId = id;
    }

    public ModelItem(String name, String desc, String path, ModelItemType modelItemType) {
        super(name, desc);
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
}
