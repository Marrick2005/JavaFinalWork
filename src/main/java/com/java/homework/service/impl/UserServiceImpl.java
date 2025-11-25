package com.java.homework.service.impl;

import com.java.homework.annotation.Autowired_Marujun;
import com.java.homework.annotation.Component_Marujun;
import com.java.homework.dao.UserDAO;
import com.java.homework.entity.User;
import com.java.homework.service.UserService;

import java.util.List;

/**
 * 用户服务实现类：依赖UserDAO，实现用户相关业务逻辑
 */
@Component_Marujun
public class UserServiceImpl implements UserService {
    // 依赖注入UserDAO（支持JDBC/JSON两种实现切换）
    private final UserDAO userDAO;

    // 构造函数注入（标注自定义Autowired注解）
    @Autowired_Marujun
    public UserServiceImpl(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @Override
    public User login(String username, String password) {
        // 调用DAO层验证用户名密码，返回用户实体
        return userDAO.findByUsernamePassword(username, password);
    }

    @Override
    public List<User> listStudents() {
        // 筛选角色为“Student”的用户
        return userDAO.listAll("Student");
    }

    @Override
    public List<User> listTeachers() {
        // 筛选角色为“Teacher”的用户
        return userDAO.listAll("Teacher");
    }

    @Override
    public void addStudent(User student) {
        // 确保学生角色正确，调用DAO新增
        student.setRole("Student");
        userDAO.addUser(student);
    }

    @Override
    public void updateUser(User user) {
        // 调用DAO更新用户信息（如密码、班级）
        userDAO.updateUser(user);
    }

    @Override
    public void deleteUser(Integer id) {
        // 调用DAO删除用户（需确保无关联数据，如成绩）
        userDAO.deleteUser(id);
    }

    @Override
    public User getUserById(Integer id) {
        // 调用DAO根据ID查询用户（用于关联查询）
        return userDAO.findById(id);
    }
}