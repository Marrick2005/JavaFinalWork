package com.java.homework.dao.impl;

import com.java.homework.annotation.Component_Marujun;
import com.java.homework.dao.CourseDAO;
import com.java.homework.entity.Course;
import com.java.homework.util.JDBCUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * 课程DAO的JDBC实现
 */
@Component_Marujun
public class JdbcCourseDAO implements CourseDAO {
    @Override
    public Course findById(Integer id) {
        String sql = "SELECT * FROM Courses WHERE id = ?";
        Connection conn = JDBCUtil.getConnection();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                Course course = new Course();
                course.setId(rs.getInt("id"));
                course.setCourseName(rs.getString("courseName"));
                course.setCredits(rs.getInt("credits"));
                course.setTeacherId(rs.getInt("teacherId"));
                course.setClassName(rs.getString("className"));
                course.setSemester(rs.getString("semester"));
                course.setClassSize(rs.getInt("classSize"));
                return course;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtil.close(rs, pstmt, conn);
        }
        return null;
    }

    @Override
    public List<Course> findByTeacherId(Integer teacherId) {
        String sql = "SELECT * FROM Courses WHERE teacherId = ?";
        Connection conn = JDBCUtil.getConnection();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Course> courses = new ArrayList<>();
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, teacherId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                Course course = new Course();
                course.setId(rs.getInt("id"));
                course.setCourseName(rs.getString("courseName"));
                course.setCredits(rs.getInt("credits"));
                course.setTeacherId(rs.getInt("teacherId"));
                course.setClassName(rs.getString("className"));
                course.setSemester(rs.getString("semester"));
                course.setClassSize(rs.getInt("classSize"));
                courses.add(course);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtil.close(rs, pstmt, conn);
        }
        return courses;
    }

    @Override
    public void addCourse(Course course) {
        String sql = "INSERT INTO Courses (courseName, credits, teacherId, className, semester, classSize) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        Connection conn = JDBCUtil.getConnection();
        PreparedStatement pstmt = null;
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, course.getCourseName());
            pstmt.setInt(2, course.getCredits());
            pstmt.setInt(3, course.getTeacherId());
            pstmt.setString(4, course.getClassName());
            pstmt.setString(5, course.getSemester());
            pstmt.setInt(6, course.getClassSize());
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("新增课程失败", e);
        } finally {
            JDBCUtil.close(null, pstmt, conn);
        }
    }

    @Override
    public void deleteCourse(Integer id) {
        String sql = "DELETE FROM Courses WHERE id = ?";
        Connection conn = JDBCUtil.getConnection();
        PreparedStatement pstmt = null;
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("删除课程失败", e);
        } finally {
            JDBCUtil.close(null, pstmt, conn);
        }
    }

    @Override
    public void updateCourse(Course course) {
        String sql = "UPDATE Courses SET courseName = ?, credits = ?, teacherId = ?, className = ?, semester = ?, classSize = ? WHERE id = ?";
        Connection conn = JDBCUtil.getConnection();
        PreparedStatement pstmt = null;
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, course.getCourseName());
            pstmt.setInt(2, course.getCredits());
            pstmt.setInt(3, course.getTeacherId());
            pstmt.setString(4, course.getClassName());
            pstmt.setString(5, course.getSemester());
            pstmt.setInt(6, course.getClassSize());
            pstmt.setInt(7, course.getId());
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("更新课程失败", e);
        } finally {
            JDBCUtil.close(null, pstmt, conn);
        }
    }

    @Override
    public List<Course> listAll() {
        String sql = "SELECT * FROM Courses";
        Connection conn = JDBCUtil.getConnection();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Course> courses = new ArrayList<>();
        try {
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                Course course = new Course();
                course.setId(rs.getInt("id"));
                course.setCourseName(rs.getString("courseName"));
                course.setCredits(rs.getInt("credits"));
                course.setTeacherId(rs.getInt("teacherId"));
                course.setClassName(rs.getString("className"));
                course.setSemester(rs.getString("semester"));
                course.setClassSize(rs.getInt("classSize"));
                courses.add(course);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtil.close(rs, pstmt, conn);
        }
        return courses;
    }

    @Override
    public Course findByClassAndName(String className, String courseName) {
        String sql = "SELECT * FROM Courses WHERE className = ? AND courseName = ?";
        Connection conn = JDBCUtil.getConnection();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, className);
            pstmt.setString(2, courseName);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                Course course = new Course();
                course.setId(rs.getInt("id"));
                course.setCourseName(rs.getString("courseName"));
                course.setCredits(rs.getInt("credits"));
                course.setTeacherId(rs.getInt("teacherId"));
                course.setClassName(rs.getString("className"));
                course.setSemester(rs.getString("semester"));
                course.setClassSize(rs.getInt("classSize"));
                return course;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtil.close(rs, pstmt, conn);
        }
        return null;
    }
}