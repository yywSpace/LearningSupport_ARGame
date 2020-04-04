package com.example.learningsupport_argame.UserManagement.bag;

public class Item {
    private String mItemName;
    private String mItemDesc;
    private int mCount;
    private int mPrice;

    public Item(String name, String desc, int count, int price) {
        mItemName = name;
        mItemDesc = desc;
        mPrice = price;
        mCount = count;
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

    public int getCount() {
        return mCount;
    }

    public void setCount(int count) {
        mCount = count;
    }

    public int getPrice() {
        return mPrice;
    }

    public void setPrice(int price) {
        mPrice = price;
    }
}
