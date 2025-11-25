package com.java.homework.service;

import com.java.homework.entity.User;

import java.util.List;

/**
 * 用户服务接口：定义用户相关核心功能（登录、用户管理等）
 */
public interface UserService {
    /**
     * 用户登录：根据用户名和密码验证身份
     * @param username 登录用户名
     * @param password 登录密码
     * @return 登录成功返回用户实体，失败返回null
     */
    User login(String username, String password);

    /**
     * 查询所有学生信息
     * @return 学生列表（含本人姓名学生）
     */
    List<User> listStudents();

    /**
     * 查询所有教师信息
     * @return 教师列表
     */
    List<User> listTeachers();

    /**
     * 新增学生
     * @param student 学生实体（需包含姓名、班级、专业等信息）
     */
    void addStudent(User student);

    /**
     * 更新用户信息（支持学生、教师密码/基本信息修改）
     * @param user 待更新的用户实体
     */
    void updateUser(User user);

    /**
     * 删除用户（仅管理员权限）
     * @param id 用户ID
     */
    void deleteUser(Integer id);

    /**
     * 根据ID查询用户（用于关联查询，如教师ID关联教师信息）
     * @param id 用户ID
     * @return 用户实体
     */
    User getUserById(Integer id);
}