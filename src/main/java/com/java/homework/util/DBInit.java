package com.java.homework.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;

/**
 * 数据库初始化：执行创建库、表、初始化数据SQL
 */
public class DBInit {
    public static void init() {
        Connection conn = null;
        Statement stmt = null;
        try {
            // 1. 先连接MySQL默认库，创建java_homework库
            conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/?useSSL=false&serverTimezone=UTC",
                    "root",
                    "root"
            );
            stmt = conn.createStatement();
            stmt.execute("CREATE DATABASE IF NOT EXISTS java_homework CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;");
            stmt.execute("USE java_homework;");

            // 2. 创建Users表
            stmt.execute("DROP TABLE IF EXISTS Users;");
            stmt.execute("CREATE TABLE Users (" +
                    "id INT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID'," +
                    "username VARCHAR(20) NOT NULL UNIQUE COMMENT '登录用户名'," +
                    "password VARCHAR(20) NOT NULL COMMENT '登录密码'," +
                    "role VARCHAR(10) NOT NULL COMMENT '角色：Admin/Teacher/Student'," +
                    "name VARCHAR(20) NOT NULL COMMENT '真实姓名'," +
                    "className VARCHAR(20) COMMENT '班级（仅学生有值）'," +
                    "majorName VARCHAR(20) COMMENT '专业（仅学生有值）'," +
                    "created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'" +
                    ") COMMENT '用户表';");

            // 3. 创建Courses表
            stmt.execute("DROP TABLE IF EXISTS Courses;");
            stmt.execute("CREATE TABLE Courses (" +
                    "id INT PRIMARY KEY AUTO_INCREMENT COMMENT '课程ID'," +
                    "courseName VARCHAR(50) NOT NULL COMMENT '课程名称'," +
                    "credits INT NOT NULL COMMENT '学分'," +
                    "teacherId INT NOT NULL COMMENT '任课教师ID（关联Users表）'," +
                    "className VARCHAR(20) NOT NULL COMMENT '授课班级'," +
                    "semester VARCHAR(20) NOT NULL COMMENT '开课学期'," +
                    "classSize INT NOT NULL COMMENT '班级人数'," +
                    "FOREIGN KEY (teacherId) REFERENCES Users(id)" +
                    ") COMMENT '课程表（开课实例）';");

            // 4. 创建Grades表
            stmt.execute("DROP TABLE IF EXISTS Grades;");
            stmt.execute("CREATE TABLE Grades (" +
                    "id INT PRIMARY KEY AUTO_INCREMENT COMMENT '成绩ID'," +
                    "studentId INT NOT NULL COMMENT '学生ID（关联Users表）'," +
                    "offeringId INT NOT NULL COMMENT '开课实例ID（关联Courses表）'," +
                    "score INT NOT NULL COMMENT '成绩（0-100）'," +
                    "FOREIGN KEY (studentId) REFERENCES Users(id)," +
                    "FOREIGN KEY (offeringId) REFERENCES Courses(id)" +
                    ") COMMENT '成绩表';");

            // 5. 初始化数据（Admin+Teacher+Student+Courses+Grades）
            // 5.1 初始化Admin
            stmt.execute("INSERT INTO Users (username, password, role, name) " +
                    "VALUES ('admin', 'admin123', 'Admin', '教务管理员');");

            // 5.2 初始化5个Teacher
            stmt.execute("INSERT INTO Users (username, password, role, name) " +
                    "VALUES " +
                    "('teacher1', 'tea123', 'Teacher', '张老师')," +
                    "('teacher2', 'tea123', 'Teacher', '李老师')," +
                    "('teacher3', 'tea123', 'Teacher', '王老师')," +
                    "('teacher4', 'tea123', 'Teacher', '刘老师')," +
                    "('teacher5', 'tea123', 'Teacher', '陈老师');");

            // 5.3 初始化100个Student（含马如君）
            stmt.execute("INSERT INTO Users (username, password, role, name, className, majorName) " +
                    "VALUES " +
                    "('2410160121', 'stu123', 'Student', '马如君', '大数据2024-1班', '数据科学与大数据技术')," +
                    "('2410160101', 'stu123', 'Student', '赵一', '大数据2024-1班', '数据科学与大数据技术')," +
                    // 省略中间98条数据（完整项目需补全，此处简化）
                    "('2411160225', 'stu123', 'Student', '萧九九', '计算机2024-2班', '计算机科学与技术');");

            // 5.4 初始化10门课程（含《Java 应用开发》）
            stmt.execute("INSERT INTO Courses (courseName, credits, teacherId, className, semester, classSize) " +
                    "VALUES " +
                    "('Java 应用开发', 3, 2, '大数据2024-1班', '2025-2026-1', 50)," +
                    "('数据库原理', 3, 3, '大数据2024-1班', '2025-2026-1', 50)," +
                    "('操作系统', 3, 4, '大数据2024-2班', '2025-2026-1', 50)," +
                    "('计算机网络', 2, 5, '计算机2024-1班', '2025-2026-1', 50)," +
                    "('数据结构', 3, 6, '计算机2024-1班', '2025-2026-1', 50)," +
                    "('Python编程', 2, 2, '计算机2024-2班', '2025-2026-1', 50)," +
                    "('人工智能', 3, 3, '大数据2024-1班', '2025-2026-1', 50)," +
                    "('Web开发', 2, 4, '大数据2024-2班', '2025-2026-1', 50)," +
                    "('移动开发', 3, 5, '计算机2024-1班', '2025-2026-1', 50)," +
                    "('信息安全', 2, 6, '计算机2024-2班', '2025-2026-1', 50);");

            // 5.5 初始化成绩
            stmt.execute("INSERT INTO Grades (studentId, offeringId, score) " +
                    "VALUES " +
                    "(2, 1, 90), (2, 2, 85), (3, 1, 82), (3, 5, 78), (4, 2, 92)," +
                    // 省略其他成绩数据（完整项目需补全30组）
                    "(31, 4, 83), (32, 4, 75);");

            System.out.println("数据库初始化成功！");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("数据库初始化失败", e);
        } finally {
            JDBCUtil.close(null, stmt, conn);
        }
    }
}