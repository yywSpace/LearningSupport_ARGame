package com.example.learningsupport_argame.ARModel;

public class Item {
    public Item(String name, String desc) {
        mItemName = name;
        mItemDesc = desc;
    }
    private String mItemName;
    private String mItemDesc;

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
}
