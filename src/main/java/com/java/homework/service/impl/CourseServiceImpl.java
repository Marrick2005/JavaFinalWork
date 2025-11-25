package com.java.homework.service.impl;

import com.java.homework.annotation.Autowired_Marujun;
import com.java.homework.annotation.Component_Marujun;
import com.java.homework.dao.CourseDAO;
import com.java.homework.dao.UserDAO;
import com.java.homework.entity.Course;
import com.java.homework.entity.User;
import com.java.homework.service.CourseService;

import java.util.List;

/**
 * 课程服务实现类：依赖CourseDAO和UserDAO，实现课程管理逻辑
 */
@Component_Marujun
public class CourseServiceImpl implements CourseService {
    private final CourseDAO courseDAO;
    private final UserDAO userDAO;

    // 多依赖构造函数注入
    @Autowired_Marujun
    public CourseServiceImpl(CourseDAO courseDAO, UserDAO userDAO) {
        this.courseDAO = courseDAO;
        this.userDAO = userDAO;
    }

    @Override
    public void createCourse(Course course, Integer teacherId) {
        // 先验证教师身份合法性
        User teacher = userDAO.findById(teacherId);
        if (teacher == null || !"Teacher".equals(teacher.getRole())) {
            throw new RuntimeException("开课失败：教师不存在或角色错误");
        }
        // 设置课程的任课教师ID，调用DAO新增课程
        course.setTeacherId(teacherId);
        courseDAO.addCourse(course);
    }

    @Override
    public List<Course> listTeacherCourses(Integer teacherId) {
        // 调用DAO查询该教师的所有课程
        return courseDAO.findByTeacherId(teacherId);
    }

    @Override
    public List<Course> listAllCourses() {
        // 调用DAO查询所有课程（管理员权限）
        return courseDAO.listAll();
    }

    @Override
    public Course getCourseById(Integer courseId) {
        // 调用DAO根据ID查询课程（用于成绩关联）
        return courseDAO.findById(courseId);
    }

    @Override
    public void updateCourse(Course course) {
        // 调用DAO更新课程信息（如调整学期、班级）
        courseDAO.updateCourse(course);
    }

    @Override
    public void deleteCourse(Integer courseId) {
        // 调用DAO删除课程（实际项目需先校验无关联成绩）
        courseDAO.deleteCourse(courseId);
    }

    @Override
    public List<Course> listCoursesByClass(String className) {
        // 调用DAO查询指定班级的所有课程（需DAO层支持，此处补充逻辑）
        return courseDAO.listAll().stream()
                .filter(course -> className.equals(course.getClassName()))
                .toList();
    }
}