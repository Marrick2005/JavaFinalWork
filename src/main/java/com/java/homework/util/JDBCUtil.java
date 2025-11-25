package com.java.homework.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

/**
 * JDBC工具类：获取连接、关闭资源、事务管理
 */
public class JDBCUtil {
    // 线程局部变量：存储当前线程的数据库连接（用于事务）
    private static final ThreadLocal<Connection> THREAD_LOCAL = new ThreadLocal<>();
    private static final Properties PROPS = new Properties();

    // 静态代码块：加载配置文件
    static {
        try (InputStream is = JDBCUtil.class.getClassLoader().getResourceAsStream("db.properties")) {
            PROPS.load(is);
            Class.forName(PROPS.getProperty("jdbc.driver"));
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("JDBC配置加载失败", e);
        }
    }

    /**
     * 获取数据库连接（事务时复用线程内连接）
     */
    public static Connection getConnection() {
        Connection conn = THREAD_LOCAL.get();
        try {
            if (conn == null || conn.isClosed()) {
                conn = DriverManager.getConnection(
                        PROPS.getProperty("jdbc.url"),
                        PROPS.getProperty("jdbc.username"),
                        PROPS.getProperty("jdbc.password")
                );
                THREAD_LOCAL.set(conn);
            }
        } catch (SQLException e) {
            throw new RuntimeException("获取数据库连接失败", e);
        }
        return conn;
    }

    /**
     * 开启事务
     */
    public static void beginTransaction() {
        try {
            Connection conn = getConnection();
            conn.setAutoCommit(false); // 关闭自动提交
        } catch (SQLException e) {
            throw new RuntimeException("开启事务失败", e);
        }
    }

    /**
     * 提交事务
     */
    public static void commitTransaction() {
        try {
            Connection conn = THREAD_LOCAL.get();
            if (conn != null && !conn.isClosed()) {
                conn.commit();
            }
        } catch (SQLException e) {
            throw new RuntimeException("提交事务失败", e);
        } finally {
            closeConnection(); // 释放连接
        }
    }

    /**
     * 回滚事务
     */
    public static void rollbackTransaction() {
        try {
            Connection conn = THREAD_LOCAL.get();
            if (conn != null && !conn.isClosed()) {
                conn.rollback();
            }
        } catch (SQLException e) {
            throw new RuntimeException("回滚事务失败", e);
        } finally {
            closeConnection(); // 释放连接
        }
    }

    /**
     * 关闭连接（从线程局部变量中移除）
     */
    private static void closeConnection() {
        try {
            Connection conn = THREAD_LOCAL.get();
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            THREAD_LOCAL.remove(); // 清除线程局部变量
        }
    }

    /**
     * 关闭资源（ResultSet, PreparedStatement, Connection）
     */
    public static void close(ResultSet rs, PreparedStatement pstmt, Connection conn) {
        try {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
            // 事务连接由commit/rollback关闭，非事务连接直接关闭
            if (conn != null && THREAD_LOCAL.get() == null) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭资源（ResultSet, Statement, Connection）
     */
    public static void close(ResultSet rs, Statement stmt, Connection conn) {
        try {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            // 事务连接由commit/rollback关闭，非事务连接直接关闭
            if (conn != null && THREAD_LOCAL.get() == null) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}