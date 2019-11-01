package com.example.learningsupport_argame.UserManagement.UserMessage;

import android.graphics.Bitmap;

import com.example.learningsupport_argame.UserManagement.UserMessage.UserMessageActivity.ItemType;

public class MessageItem {
    private ItemType mItemType;
    private String mItemLabel;
    private String mItemContent;
    private Bitmap mItemImageBitmap;
    private int mItemImageId;

    public MessageItem(String itemLabel, Bitmap itemImage, ItemType itemType) {
        mItemLabel = itemLabel;
        mItemImageBitmap = itemImage;
        mItemType = itemType;
    }

    public MessageItem(String itemLabel, String itemContent, ItemType itemType) {
        mItemLabel = itemLabel;
        mItemContent = itemContent;
        mItemType = itemType;
    }

    public MessageItem(String itemLabel, int itemImageId, ItemType itemType) {
        mItemLabel = itemLabel;
        mItemImageId = itemImageId;
        mItemType = itemType;
    }

    public ItemType getItemType() {
        return mItemType;
    }

    public void setItemType(ItemType itemType) {
        mItemType = itemType;
    }

    public String getItemLabel() {
        return mItemLabel;
    }

    public void setItemLabel(String itemLabel) {
        mItemLabel = itemLabel;
    }

    public String getItemContent() {
        return mItemContent;
    }

    public void setItemContent(String itemContent) {
        mItemContent = itemContent;
    }

    public Bitmap getItemImageBitmap() {
        return mItemImageBitmap;
    }

    public void setItemImageBitmap(Bitmap itemImage) {
        mItemImageBitmap = itemImage;
    }

    public int getItemImageId() {
        return mItemImageId;
    }

    public void setItemImageId(int itemImageId) {
        mItemImageId = itemImageId;
    }
}
