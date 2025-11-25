package com.java.homework.entity;

import java.util.Date;

/**
 * 用户实体：包含Admin/Teacher/Student
 */
public class User {
    private Integer id;
    private String username;
    private String password;
    private String role; // Admin/Teacher/Student
    private String name;
    private String className; // 仅学生有值
    private String majorName; // 仅学生有值
    private Date createdTime;

    // 无参构造（必须，JSON反序列化和反射需要）
    public User() {}

    // Getter和Setter
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }
    public String getMajorName() { return majorName; }
    public void setMajorName(String majorName) { this.majorName = majorName; }
    public Date getCreatedTime() { return createdTime; }
    public void setCreatedTime(Date createdTime) { this.createdTime = createdTime; }

    // toString（方便打印）
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", role='" + role + '\'' +
                ", name='" + name + '\'' +
                ", className='" + className + '\'' +
                ", majorName='" + majorName + '\'' +
                '}';
    }
}