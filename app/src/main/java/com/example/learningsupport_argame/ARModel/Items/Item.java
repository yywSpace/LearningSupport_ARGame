package com.example.learningsupport_argame.ARModel.Items;

public class Item {
    private ItemType mItemType;
    private String mItemName;
    private String mItemDesc;
    private int mViewId;
    private String mModelPath;

    public Item(String name, String desc, int id, ItemType itemType) {
        mItemType = itemType;
        mItemName = name;
        mItemDesc = desc;
        mViewId = id;
    }

    public Item(String name, String desc, String path, ItemType itemType) {
        mItemType = itemType;
        mItemName = name;
        mItemDesc = desc;
        mModelPath = path;
    }

    public ItemType getItemType() {
        return mItemType;
    }

    public void setItemType(ItemType itemType) {
        mItemType = itemType;
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
