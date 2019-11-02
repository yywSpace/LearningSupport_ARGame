package com.example.learningsupport_argame.community;


import com.example.learningsupport_argame.bean.PairInfoBean;
import com.example.learningsupport_argame.community.adapter.DBOpenHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DBService {
    private Connection conn = null; //打开数据库对象
    private PreparedStatement ps = null;//操作整合sql语句的对象
    private ResultSet rs = null;//查询结果的集合     //DBService 对象
    public static DBService dbService = null;

    /**
     * 构造方法 私有化     *
     */
    private DBService() {
    }

    /**
     * 获取MySQL数据库单例类对象     *
     */
    public static DBService getDbService() {
        if (dbService == null) {
            dbService = new DBService();
        }
        return dbService;
    }


    public List<PairInfoBean> getAllUserData() {        //结果存放集合
        List<PairInfoBean> list = new ArrayList<PairInfoBean>();        //MySQL 语句
        String sql = "select * from user";        //获取链接数据库对象
        conn = DBOpenHelper.getConn();
        try {
            if (conn != null && (!conn.isClosed())) {
                ps = (PreparedStatement) conn.prepareStatement(sql);
                if (ps != null) {
                    rs = ps.executeQuery();
                    if (rs != null) {
                        while (rs.next()) {
                          PairInfoBean u=new PairInfoBean();
                            u.setPairId(rs.getString("user_id"));
                            u.setPairName(rs.getString("user_name"));
                            u.setPhone(rs.getString("user_phone"));
                           // u.setBrithday(rs.getString("user_brithday"));
                            u.setSex(rs.getString("user_sex"));
                            list.add(u);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        DBOpenHelper.closeAll(conn, ps, rs);//关闭相关操作
        return list;
    }

    public List<PairInfoBean> getUserData(String user_name) {        //结果存放集合
        List<PairInfoBean> list = new ArrayList<PairInfoBean>();        //MySQL 语句
        String sql = "select * from user where user_name=?";        //获取链接数据库对象
        conn = DBOpenHelper.getConn();
        try {
            if (conn != null && (!conn.isClosed())) {

                ps = (PreparedStatement) conn.prepareStatement(sql);
                ps.setString(1, user_name);//第一个参数state 一定要和上面SQL语句字段顺序一致
                if (ps != null) {
                    rs = ps.executeQuery();
                    if (rs != null) {
                        while (rs.next()) {
                            PairInfoBean u=new PairInfoBean();
                            u.setPairId(rs.getString("user_id"));
                            u.setPairName(rs.getString("user_name"));
                            u.setPhone(rs.getString("user_phone"));
                            // u.setBrithday(rs.getString("user_brithday"));
                            u.setSex(rs.getString("user_sex"));
                            list.add(u);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        DBOpenHelper.closeAll(conn, ps, rs);//关闭相关操作
        return list;
    }

    /**
     * 修改数据库中某个对象的状态   改     *
     */
    public int updateUserData(String user_id) {
        int result = -1;
        if (!(user_id==null)) {
            //获取链接数据库对象
            conn = DBOpenHelper.getConn();            //MySQL 语句
            String sql = "update user set user_up=? where user_id=?";
            try {
                boolean closed = conn.isClosed();
                if (conn != null && (!closed)) {
                    ps = (PreparedStatement) conn.prepareStatement(sql);
                    ps.setString(1, "1");//第一个参数state 一定要和上面SQL语句字段顺序一致
                    ps.setString(2, user_id);//第二个参数 phone 一定要和上面SQL语句字段顺序一致
                    result = ps.executeUpdate();//返回1 执行成功
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        DBOpenHelper.closeAll(conn, ps);//关闭相关操作
        return result;
    }

    /**
     * 批量向数据库插入数据   增     *
     */
    public int insertUserData(List<PairInfoBean> list) {
        int result = -1;
        if ((list != null) && (list.size() > 0)) {            //获取链接数据库对象
            conn = DBOpenHelper.getConn();            //MySQL 语句
            String sql = "INSERT INTO user (user_name,user_phone,user_sex,user_id) VALUES (?,?,?,?)";
            try {
                boolean closed = conn.isClosed();
                if ((conn != null) && (!closed)) {
                    for (PairInfoBean user : list) {
                        ps = (PreparedStatement) conn.prepareStatement(sql);
                        String name = user.getPairName();
                        String phone = user.getPhone();
                        String sex= user.getSex();
                        String user_id = user.getPairId();
                        ps.setString(1, name);//第一个参数 name 规则同上
                        ps.setString(2, phone);//第二个参数 phone 规则同上
                        ps.setString(3, sex);//第三个参数 content 规则同上
                        ps.setString(4, user_id);//第四个参数 state 规则同上
                        result = ps.executeUpdate();//返回1 执行成功
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        DBOpenHelper.closeAll(conn, ps);//关闭相关操作
        return result;
    }

//    /**
//     * 删除数据  删     *
//     */
//    public int delUserData(String phone) {
//        int result = -1;
//        if ((!StringUtils.isEmpty(phone)) && (PhoneNumberUtils.isMobileNumber(phone))) {            //获取链接数据库对象
//            conn = DBOpenHelper.getConn();            //MySQL 语句
//            String sql = "delete from user where phone=?";
//            try {
//                boolean closed = conn.isClosed();
//                if ((conn != null) && (!closed)) {
//                    ps = (PreparedStatement) conn.prepareStatement(sql);
//                    ps.setString(1, phone);
//                    result = ps.executeUpdate();//返回1 执行成功
//                }
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        }
//        DBOpenHelper.closeAll(conn, ps);//关闭相关操作
//        return result;
//    }


}
