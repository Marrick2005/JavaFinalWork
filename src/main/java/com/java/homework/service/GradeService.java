package com.java.homework.service;

import com.java.homework.entity.Grade;
import com.java.homework.entity.User;

import java.util.List;
import java.util.Map;

/**
 * 成绩服务接口：定义成绩录入、查询、统计等功能
 */
public interface GradeService {
    /**
     * 录入单个学生成绩（教师权限）
     * @param grade 成绩实体（含学生ID、课程ID、分数）
     */
    void addGrade(Grade grade);

    /**
     * 批量录入成绩（选做，教师权限）
     * @param grades 成绩列表
     */
    void batchAddGrade(List<Grade> grades);

    /**
     * 查询学生个人成绩（学生权限）
     * @param studentId 学生ID
     * @return 该学生的所有课程成绩列表（含课程名称关联）
     */
    List<Grade> queryStudentGrades(Integer studentId);

    /**
     * 查询指定课程的班级成绩（教师/管理员权限）
     * @param courseId 课程ID
     * @return 该课程的所有学生成绩列表
     */
    List<Grade> queryCourseGrades(Integer courseId);

    /**
     * 计算指定课程的平均成绩
     * @param courseId 课程ID
     * @return 平均成绩（保留2位小数）
     */
    Double calculateCourseAvgScore(Integer courseId);

    /**
     * 计算指定课程的及格率（60分及以上为及格）
     * @param courseId 课程ID
     * @return 及格率（百分比，保留2位小数）
     */
    Double calculateCoursePassRate(Integer courseId);

    /**
     * 计算指定班级的学分绩点排名
     * @param className 班级名称
     * @return 排名结果（Key：学生实体，Value：学分绩点，已按绩点降序排序）
     */
    Map<User, Double> calculateClassGPARanking(String className);

    /**
     * 查询挂科达到指定次数的学生（管理员权限）
     * @param failThreshold 挂科次数阈值（如2次）
     * @return 挂科次数达标学生列表
     */
    List<User> getStudentsWithFailCount(Integer failThreshold);

    /**
     * 筛选“学霸画像”学生（管理员权限）
     * @return 满足条件的学生列表：所有课程≥85分且修课≥4门
     */
    List<User> getTopStudents();
}