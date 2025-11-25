package com.java.homework.dao.impl;

import com.java.homework.annotation.Component_Marujun;
import com.java.homework.dao.GradeDAO;
import com.java.homework.entity.Grade;
import com.java.homework.util.JDBCUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * 成绩DAO的JDBC实现
 */
@Component_Marujun
public class JdbcGradeDAO implements GradeDAO {
    @Override
    public List<Grade> findByStudentId(Integer studentId) {
        String sql = "SELECT * FROM Grades WHERE studentId = ?";
        Connection conn = JDBCUtil.getConnection();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Grade> grades = new ArrayList<>();
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, studentId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                Grade grade = new Grade();
                grade.setId(rs.getInt("id"));
                grade.setStudentId(rs.getInt("studentId"));
                grade.setOfferingId(rs.getInt("offeringId"));
                grade.setScore(rs.getInt("score"));
                grades.add(grade);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtil.close(rs, pstmt, conn);
        }
        return grades;
    }

    @Override
    public List<Grade> findByCourseId(Integer courseId) {
        String sql = "SELECT * FROM Grades WHERE offeringId = ?";
        Connection conn = JDBCUtil.getConnection();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Grade> grades = new ArrayList<>();
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, courseId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                Grade grade = new Grade();
                grade.setId(rs.getInt("id"));
                grade.setStudentId(rs.getInt("studentId"));
                grade.setOfferingId(rs.getInt("offeringId"));
                grade.setScore(rs.getInt("score"));
                grades.add(grade);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtil.close(rs, pstmt, conn);
        }
        return grades;
    }

    @Override
    public void addGrade(Grade grade) {
        String sql = "INSERT INTO Grades (studentId, offeringId, score) VALUES (?, ?, ?)";
        Connection conn = JDBCUtil.getConnection();
        PreparedStatement pstmt = null;
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, grade.getStudentId());
            pstmt.setInt(2, grade.getOfferingId());
            pstmt.setInt(3, grade.getScore());
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("新增成绩失败", e);
        } finally {
            JDBCUtil.close(null, pstmt, conn);
        }
    }

    @Override
    public void batchAddGrade(List<Grade> grades) {
        String sql = "INSERT INTO Grades (studentId, offeringId, score) VALUES (?, ?, ?)";
        Connection conn = JDBCUtil.getConnection();
        PreparedStatement pstmt = null;
        try {
            pstmt = conn.prepareStatement(sql);
            for (Grade grade : grades) {
                pstmt.setInt(1, grade.getStudentId());
                pstmt.setInt(2, grade.getOfferingId());
                pstmt.setInt(3, grade.getScore());
                pstmt.addBatch(); // 批量添加
            }
            pstmt.executeBatch(); // 执行批量插入
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("批量新增成绩失败", e);
        } finally {
            JDBCUtil.close(null, pstmt, conn);
        }
    }

    @Override
    public void updateGrade(Grade grade) {
        String sql = "UPDATE Grades SET score = ? WHERE id = ?";
        Connection conn = JDBCUtil.getConnection();
        PreparedStatement pstmt = null;
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, grade.getScore());
            pstmt.setInt(2, grade.getId());
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("更新成绩失败", e);
        } finally {
            JDBCUtil.close(null, pstmt, conn);
        }
    }

    @Override
    public Double calculateAvgScore(Integer courseId) {
        String sql = "SELECT AVG(score) AS avg FROM Grades WHERE offeringId = ?";
        Connection conn = JDBCUtil.getConnection();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, courseId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("avg");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtil.close(rs, pstmt, conn);
        }
        return 0.0;
    }

    @Override
    public Double calculatePassRate(Integer courseId) {
        String sql = "SELECT " +
                "COUNT(CASE WHEN score >= 60 THEN 1 END) AS passCount, " +
                "COUNT(*) AS totalCount " +
                "FROM Grades WHERE offeringId = ?";
        Connection conn = JDBCUtil.getConnection();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, courseId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                int passCount = rs.getInt("passCount");
                int totalCount = rs.getInt("totalCount");
                return totalCount == 0 ? 0.0 : (double) passCount / totalCount * 100;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtil.close(rs, pstmt, conn);
        }
        return 0.0;
    }

    @Override
    public Integer countFailedGrades(Integer studentId) {
        String sql = "SELECT COUNT(*) AS failCount FROM Grades WHERE studentId = ? AND score < 60";
        Connection conn = JDBCUtil.getConnection();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, studentId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("failCount");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtil.close(rs, pstmt, conn);
        }
        return 0;
    }
}