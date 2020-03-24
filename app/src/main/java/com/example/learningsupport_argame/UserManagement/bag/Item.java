package com.example.learningsupport_argame.UserManagement.bag;

public class Item {
    private String mItemName;
    private String mItemDesc;

    public Item(String name, String desc) {
        mItemName = name;
        mItemDesc = desc;
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
}
