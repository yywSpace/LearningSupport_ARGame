package com.example.learningsupport_argame.Community.club;

import android.graphics.Bitmap;

import java.io.Serializable;

public class Club {
    // 主要用于解决，activity键无法传递Bitmap的问题
    public static Club sCurrentClub;
    public final static int BASE_CLUB_NUMBER = 3;
    private int mId;
    private int mManagerId;
    private Bitmap mCoverBitmap;
    private String mClubType;
    private String mClubName;
    private String mClubDesc;
    private int mClubMaxMember;
    private int mCurrentMemberNum;

    public Club() {

    }

    public Club(String name, String type, String desc, int maxMember) {
        mClubName = name;
        mClubDesc = desc;
        mClubType = type;
        mClubMaxMember = maxMember;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public String getClubName() {
        return mClubName;
    }

    public void setClubName(String clubName) {
        mClubName = clubName;
    }

    public String getClubDesc() {
        return mClubDesc;
    }

    public void setClubDesc(String clubDesc) {
        mClubDesc = clubDesc;
    }

    public int getClubMaxMember() {
        return mClubMaxMember;
    }

    public void setClubMaxMember(int clubMaxMember) {
        mClubMaxMember = clubMaxMember;
    }

    public int getCurrentMemberNum() {
        return mCurrentMemberNum;
    }

    public void setCurrentMemberNum(int currentMemberNum) {
        mCurrentMemberNum = currentMemberNum;
    }

    public String getClubType() {
        return mClubType;
    }

    public void setClubType(String clubType) {
        mClubType = clubType;
    }

    public int getManagerId() {
        return mManagerId;
    }

    public void setManagerId(int managerId) {
        mManagerId = managerId;
    }

    public Bitmap getCoverBitmap() {
        return mCoverBitmap;
    }

    public void setCoverBitmap(Bitmap coverBitmap) {
        mCoverBitmap = coverBitmap;
    }
}
