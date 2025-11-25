package com.java.homework.service;

import com.java.homework.entity.Course;

import java.util.List;

/**
 * 课程服务接口：定义课程管理、开课、课表查询等功能
 */
public interface CourseService {
    /**
     * 开课操作（管理员权限）
     * @param course 课程实体（含课程名、学分、学期、班级等）
     * @param teacherId 任课教师ID（需先验证教师身份）
     */
    void createCourse(Course course, Integer teacherId);

    /**
     * 查询教师个人课表（教师权限）
     * @param teacherId 教师ID
     * @return 该教师教授的所有课程列表
     */
    List<Course> listTeacherCourses(Integer teacherId);

    /**
     * 查询所有课程（管理员权限）
     * @return 系统中所有开课实例列表
     */
    List<Course> listAllCourses();

    /**
     * 根据课程ID查询课程信息（用于成绩关联查询）
     * @param courseId 课程ID
     * @return 课程实体
     */
    Course getCourseById(Integer courseId);

    /**
     * 更新课程信息（如调整教师、班级、学期）
     * @param course 待更新的课程实体
     */
    void updateCourse(Course course);

    /**
     * 删除课程（仅管理员权限，需先确保无关联成绩）
     * @param courseId 课程ID
     */
    void deleteCourse(Integer courseId);

    /**
     * 查询指定班级的课程（用于学生查询班级课程）
     * @param className 班级名称（如“大数据2024-1班”）
     * @return 该班级的所有课程列表
     */
    List<Course> listCoursesByClass(String className);
}