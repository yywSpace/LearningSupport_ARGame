package com.example.learningsupport_argame.Community.club;

import android.util.Log;

import com.example.learningsupport_argame.DbUtils;
import com.example.learningsupport_argame.Task.Task;
import com.example.learningsupport_argame.UserManagement.User;
import com.example.learningsupport_argame.UserManagement.UserLab;

import java.util.ArrayList;
import java.util.List;

public class ClubLab {
    private static String TAG = "ClubLab";
    public static List<Club> sCreatedClubList;
    public static List<Club> sParticipateClubList;
    public static List<Club> sOtherClubList;

    public static List<Club> getCreatedClubList() {
        sCreatedClubList = getClubWith("" +
                "select c.*, (select count(*) from club_members where club_id = c.id) as current_num " +
                "   from club c WHERE club_manager = ?", UserLab.getCurrentUser().getId());
        return sCreatedClubList;
    }

    public static List<Club> getOtherClubs() {
        sOtherClubList = getClubWith("select c.*, (select count(*) from club_members where club_id = c.id) as current_num " +
                        "from club c where club_manager != ? and id not in (select club_id from club_members where user_id = ?)",
                UserLab.getCurrentUser().getId(),
                UserLab.getCurrentUser().getId());
        return sOtherClubList;
    }

    private static List<Club> getClubWith(String sql, Object... args) {
        List<Club> clubList = new ArrayList<>();
        DbUtils.query(resultSet -> {
            while (resultSet.next()) {
                Club club = new Club();
                club.setId(resultSet.getInt("id"));
                club.setManagerId(resultSet.getInt("club_manager"));
                club.setCoverBitmap(DbUtils.Bytes2Bitmap(resultSet.getBytes("club_cover_image")));
                club.setClubName(resultSet.getString("club_name"));
                club.setClubDesc(resultSet.getString("club_desc"));
                club.setClubMaxMember(resultSet.getInt("club_max_num"));
                club.setCurrentMemberNum(resultSet.getInt("current_num"));
                club.setClubType(resultSet.getString("club_type"));
                clubList.add(club);
            }
        }, sql, args);
        return clubList;
    }

    public static void insert(Club club) {
        DbUtils.update(null, "" +
                        "insert into club(club_manager,club_name,club_desc,club_max_num,club_type,club_cover_image) " +
                        "   values(?,?,?,?,?,?);",
                club.getManagerId(),
                club.getClubName(),
                club.getClubDesc(),
                club.getClubMaxMember(),
                club.getClubType(),
                DbUtils.Bitmap2Bytes(club.getCoverBitmap()));
    }


    public static void update(Club club) {
        DbUtils.update(null, "" +
                        "update club set club_name = ?, club_desc = ?,club_max_num = ?,club_type = ?,club_cover_image = ?" +
                        "   where id = ?",
                club.getClubName(),
                club.getClubDesc(),
                club.getClubMaxMember(),
                club.getClubType(),
                DbUtils.Bitmap2Bytes(club.getCoverBitmap()),
                club.getId());
    }

    public static void delete(Club club) {
        DbUtils.update(null, "" +
                        "delete from club_members where club_id = ?",
                club.getId());
        DbUtils.update(null, "" +
                        "delete from club where id = ?",
                club.getId());
    }

    public static Club getClubById(int id) {
        String sql = "" +
                "select c.*, (select count(*) from club_members where club_id = c.id) as current_num " +
                "   from club c where id = ?;";
        List<Club> clubs = getClubWith(sql, id);
        if (clubs.size() <= 0) {
            return null;
        } else {
            return clubs.get(0);
        }
    }
    public static Club getClubByName(String name) {
        String sql = "" +
                "select c.*, (select count(*) from club_members where club_id = c.id) as current_num " +
                "   from club c where club_name = ?;";
        List<Club> clubs = getClubWith(sql, name);
        if (clubs.size() <= 0) {
            return null;
        } else {
            return clubs.get(0);
        }
    }

    public static void attendClub(Club club) {
        DbUtils.update(null, "" +
                        "insert into club_members values (null, (select id from club where club_name = ? and club_manager = ?) , ?);",
                club.getClubName(),
                club.getManagerId(),
                UserLab.getCurrentUser().getId());
    }

    public static void quitClub(Club club) {
        DbUtils.update(null, "" +
                        "delete from club_members where club_id = ? and user_id = ?",
                club.getId(),
                UserLab.getCurrentUser().getId());
    }

    public static List<Club> getParticipateClubList() {
        sParticipateClubList = getClubWith("" +
                        "select c.*, (select count(*) from club_members where club_id = c.id) as current_num " +
                        "   from club c where id in (select club_id from club_members where user_id = ?)",
                UserLab.getCurrentUser().getId());
        return sParticipateClubList;
    }

    public static List<User> getClubMemberList(int club_id) {
        List<User> clubMemberList = new ArrayList<>();
        String sql = "" +
                "SELECT user_id, user_name, user_level, user_avatar FROM user " +
                "   where user_id in (select cm.user_id from club_members cm where club_id = ?);";
        DbUtils.query(resultSet -> {
            while (resultSet.next()) {
                User user = new User();
                user.setId(resultSet.getInt("user_id"));
                user.setName(resultSet.getString("user_name"));
                user.setLevel(resultSet.getInt("user_level"));
                user.setAvatar(DbUtils.Bytes2Bitmap(resultSet.getBytes("user_avatar")));
                clubMemberList.add(user);
            }
        }, sql, club_id);
        return clubMemberList;
    }

    public static void deleteClubMember(int clubId, int memberId) {
        DbUtils.update(null, "delete from club_members where club_id = ? and user_id = ?;", clubId, memberId);
    }
}
