package com.java.homework.dao.impl;

import com.google.gson.reflect.TypeToken;
import com.java.homework.annotation.Component_Marujun;
import com.java.homework.dao.UserDAO;
import com.java.homework.entity.User;
import com.java.homework.util.JSONUtil;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 用户DAO的JSON文件实现
 */
@Component_Marujun
public class FileUserDAO implements UserDAO {
    private static final String FILE_NAME = "users.json";
    private final AtomicInteger idGenerator = new AtomicInteger(1); // ID自增器

    // 初始化ID生成器（从现有数据获取最大ID）
    public FileUserDAO() {
        List<User> users = listAll(null);
        if (!users.isEmpty()) {
            int maxId = users.stream().mapToInt(User::getId).max().getAsInt();
            idGenerator.set(maxId + 1);
        }
    }

    @Override
    public User findByUsernamePassword(String username, String password) {
        List<User> users = JSONUtil.readFromJson(FILE_NAME, new TypeToken<List<User>>() {}.getType());
        return users.stream()
                .filter(u -> u.getUsername().equals(username) && u.getPassword().equals(password))
                .findFirst()
                .orElse(null);
    }

    @Override
    public User findById(Integer id) {
        List<User> users = JSONUtil.readFromJson(FILE_NAME, new TypeToken<List<User>>() {}.getType());
        return users.stream()
                .filter(u -> u.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void addUser(User user) {
        List<User> users = JSONUtil.readFromJson(FILE_NAME, new TypeToken<List<User>>() {}.getType());
        user.setId(idGenerator.getAndIncrement()); // 生成新ID
        users.add(user);
        JSONUtil.writeToJson(FILE_NAME, users);
    }

    @Override
    public void deleteUser(Integer id) {
        List<User> users = JSONUtil.readFromJson(FILE_NAME, new TypeToken<List<User>>() {}.getType());
        users.removeIf(u -> u.getId().equals(id));
        JSONUtil.writeToJson(FILE_NAME, users);
    }

    @Override
    public void updateUser(User user) {
        List<User> users = JSONUtil.readFromJson(FILE_NAME, new TypeToken<List<User>>() {}.getType());
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getId().equals(user.getId())) {
                users.set(i, user);
                break;
            }
        }
        JSONUtil.writeToJson(FILE_NAME, users);
    }

    @Override
    public List<User> listAll(String role) {
        List<User> users = JSONUtil.readFromJson(FILE_NAME, new TypeToken<List<User>>() {}.getType());
        if (role == null) {
            return users;
        }
        // 按角色筛选
        return users.stream().filter(u -> u.getRole().equals(role)).toList();
    }

    @Override
    public User findByUsername(String username) {
        List<User> users = JSONUtil.readFromJson(FILE_NAME, new TypeToken<List<User>>() {}.getType());
        return users.stream()
                .filter(u -> u.getUsername().equals(username))
                .findFirst()
                .orElse(null);
    }
}