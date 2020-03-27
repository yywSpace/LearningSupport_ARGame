package com.example.learningsupport_argame.Community.club;

public class ClubInfoItem {
    private Club mClub;
    private String mClubContent;
    private ClubInfoItemType mClubInfoItemType;

    public ClubInfoItem(Club club, ClubInfoItemType type) {
        mClub = club;
        mClubInfoItemType = type;
    }

    public ClubInfoItem(Club club, String clubContent, ClubInfoItemType type) {
        mClub = club;
        mClubContent = clubContent;
        mClubInfoItemType = type;
    }

    public ClubInfoItemType getClubInfoItemType() {
        return mClubInfoItemType;
    }

    public void setClubInfoItemType(ClubInfoItemType clubInfoItemType) {
        mClubInfoItemType = clubInfoItemType;
    }

    public String getClubContent() {
        return mClubContent;
    }

    public void setClubContent(String clubContent) {
        mClubContent = clubContent;
    }


    public Club getClub() {
        return mClub;
    }

    public void setClub(Club club) {
        mClub = club;
    }

    public enum ClubInfoItemType {
        AVATAR,
        MANAGER,
        LABEL,
        DESC,
        ATTEND,
        VIEW_MEMBER
    }
}
