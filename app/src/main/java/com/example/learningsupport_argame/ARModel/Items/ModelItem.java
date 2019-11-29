package com.example.learningsupport_argame.ARModel.Items;

public class ModelItem {
    private ModelItemType mModelItemType;
    private String mItemName;
    private String mItemDesc;
    private int mViewId;
    private String mModelPath;

    public ModelItem(String name, String desc, int id, ModelItemType modelItemType) {
        mModelItemType = modelItemType;
        mItemName = name;
        mItemDesc = desc;
        mViewId = id;
    }

    public ModelItem(String name, String desc, String path, ModelItemType modelItemType) {
        mModelItemType = modelItemType;
        mItemName = name;
        mItemDesc = desc;
        mModelPath = path;
    }

    public ModelItemType getModelItemType() {
        return mModelItemType;
    }

    public void setModelItemType(ModelItemType modelItemType) {
        mModelItemType = modelItemType;
    }

    public String getItemName() {
        return mItemName;
    }

    public void setItemName(String itemName) {
        mItemName = itemName;
    }

    public String getItemDesc() {
        return mItemDesc;
    }

    public void setItemDesc(String itemDesc) {
        mItemDesc = itemDesc;
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
