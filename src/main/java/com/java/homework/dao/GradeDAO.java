package com.java.homework.dao;

import com.java.homework.entity.Grade;

import java.util.List;

/**
 * 成绩DAO接口
 */
public interface GradeDAO {
    // 根据学生ID查询（学生查自己的成绩）
    List<Grade> findByStudentId(Integer studentId);
    // 根据课程ID查询（教师查班级成绩）
    List<Grade> findByCourseId(Integer courseId);
    // 新增成绩
    void addGrade(Grade grade);
    // 批量新增成绩（选做）
    void batchAddGrade(List<Grade> grades);
    // 更新成绩
    void updateGrade(Grade grade);
    // 计算课程平均成绩
    Double calculateAvgScore(Integer courseId);
    // 计算课程及格率（60分及以上）
    Double calculatePassRate(Integer courseId);
    // 查询学生挂科次数（<60分）
    Integer countFailedGrades(Integer studentId);
}