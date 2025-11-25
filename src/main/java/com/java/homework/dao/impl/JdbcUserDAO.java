package com.java.homework.dao.impl;

import com.java.homework.annotation.Component_Marujun;
import com.java.homework.dao.UserDAO;
import com.java.homework.entity.User;
import com.java.homework.util.JDBCUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户DAO的JDBC实现
 */
@Component_Marujun // 标记为Bean，由容器管理
public class JdbcUserDAO implements UserDAO {
    @Override
    public User findByUsernamePassword(String username, String password) {
        String sql = "SELECT * FROM Users WHERE username = ? AND password = ?";
        Connection conn = JDBCUtil.getConnection();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setRole(rs.getString("role"));
                user.setName(rs.getString("name"));
                user.setClassName(rs.getString("className"));
                user.setMajorName(rs.getString("majorName"));
                user.setCreatedTime(rs.getTimestamp("created_time"));
                return user;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtil.close(rs, pstmt, conn);
        }
        return null;
    }

    @Override
    public User findById(Integer id) {
        String sql = "SELECT * FROM Users WHERE id = ?";
        Connection conn = JDBCUtil.getConnection();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setRole(rs.getString("role"));
                user.setName(rs.getString("name"));
                user.setClassName(rs.getString("className"));
                user.setMajorName(rs.getString("majorName"));
                user.setCreatedTime(rs.getTimestamp("created_time"));
                return user;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtil.close(rs, pstmt, conn);
        }
        return null;
    }

    @Override
    public void addUser(User user) {
        String sql = "INSERT INTO Users (username, password, role, name, className, majorName) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        Connection conn = JDBCUtil.getConnection();
        PreparedStatement pstmt = null;
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getRole());
            pstmt.setString(4, user.getName());
            pstmt.setString(5, user.getClassName());
            pstmt.setString(6, user.getMajorName());
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("新增用户失败", e);
        } finally {
            JDBCUtil.close(null, pstmt, conn);
        }
    }

    @Override
    public void deleteUser(Integer id) {
        String sql = "DELETE FROM Users WHERE id = ?";
        Connection conn = JDBCUtil.getConnection();
        PreparedStatement pstmt = null;
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("删除用户失败", e);
        } finally {
            JDBCUtil.close(null, pstmt, conn);
        }
    }

    @Override
    public void updateUser(User user) {
        String sql = "UPDATE Users SET password = ?, name = ?, className = ?, majorName = ? WHERE id = ?";
        Connection conn = JDBCUtil.getConnection();
        PreparedStatement pstmt = null;
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, user.getPassword());
            pstmt.setString(2, user.getName());
            pstmt.setString(3, user.getClassName());
            pstmt.setString(4, user.getMajorName());
            pstmt.setInt(5, user.getId());
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("更新用户失败", e);
        } finally {
            JDBCUtil.close(null, pstmt, conn);
        }
    }

    @Override
    public List<User> listAll(String role) {
        String sql = "SELECT * FROM Users" + (role != null ? " WHERE role = ?" : "");
        Connection conn = JDBCUtil.getConnection();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<User> users = new ArrayList<>();
        try {
            pstmt = conn.prepareStatement(sql);
            if (role != null) {
                pstmt.setString(1, role);
            }
            rs = pstmt.executeQuery();
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setRole(rs.getString("role"));
                user.setName(rs.getString("name"));
                user.setClassName(rs.getString("className"));
                user.setMajorName(rs.getString("majorName"));
                user.setCreatedTime(rs.getTimestamp("created_time"));
                users.add(user);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtil.close(rs, pstmt, conn);
        }
        return users;
    }
}