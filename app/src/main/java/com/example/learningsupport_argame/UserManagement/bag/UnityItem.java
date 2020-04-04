package com.example.learningsupport_argame.UserManagement.bag;

import androidx.annotation.NonNull;

public class UnityItem extends Item {
    private int mId;
    private String mModName;
    private int mImgRec;

    public UnityItem(int id, String name, String desc, String modName, int imgRec, int price) {
        super(name, desc, 1, price);
        mModName = modName;
        mImgRec = imgRec;
        mId = id;
    }

    @NonNull
    @Override
    public String toString() {
        return String.valueOf(mId);
    }

    public String getModName() {
        return mModName;
    }

    public void setModName(String modName) {
        mModName = modName;
    }

    public int getImgRec() {
        return mImgRec;
    }

    public void setImgRec(int imgRec) {
        mImgRec = imgRec;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }
}
