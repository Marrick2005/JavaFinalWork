package com.java.homework.dao;

import com.java.homework.entity.User;

import java.util.List;

/**
 * 用户DAO接口
 */
public interface UserDAO {
    // 根据用户名和密码查询（登录用）
    User findByUsernamePassword(String username, String password);
    // 根据ID查询
    User findById(Integer id);
    // 新增用户
    void addUser(User user);
    // 删除用户
    void deleteUser(Integer id);
    // 更新用户
    void updateUser(User user);
    // 查询所有用户（按角色筛选）
    List<User> listAll(String role);
}