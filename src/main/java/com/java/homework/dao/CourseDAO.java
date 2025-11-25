package com.java.homework.dao;

import com.java.homework.entity.Course;

import java.util.List;

/**
 * 课程DAO接口
 */
public interface CourseDAO {
    // 根据ID查询
    Course findById(Integer id);
    // 根据教师ID查询（教师查自己的课）
    List<Course> findByTeacherId(Integer teacherId);
    // 新增课程（开课）
    void addCourse(Course course);
    // 删除课程
    void deleteCourse(Integer id);
    // 更新课程
    void updateCourse(Course course);
    // 查询所有课程
    List<Course> listAll();
    // 根据班级和课程名查询（统计用）
    Course findByClassAndName(String className, String courseName);
}