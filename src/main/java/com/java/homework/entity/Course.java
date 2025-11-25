package com.java.homework.entity;

/**
 * 课程实体（开课实例）
 */
public class Course {
    private Integer id;
    private String courseName;
    private Integer credits;
    private Integer teacherId; // 关联Users表id
    private String className; // 授课班级
    private String semester; // 开课学期（如2025-2026-1）
    private Integer classSize; // 班级人数

    public Course() {}

    // Getter和Setter
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }
    public Integer getCredits() { return credits; }
    public void setCredits(Integer credits) { this.credits = credits; }
    public Integer getTeacherId() { return teacherId; }
    public void setTeacherId(Integer teacherId) { this.teacherId = teacherId; }
    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }
    public String getSemester() { return semester; }
    public void setSemester(String semester) { this.semester = semester; }
    public Integer getClassSize() { return classSize; }
    public void setClassSize(Integer classSize) { this.classSize = classSize; }

    @Override
    public String toString() {
        return "Course{" +
                "id=" + id +
                ", courseName='" + courseName + '\'' +
                ", credits=" + credits +
                ", teacherId=" + teacherId +
                ", className='" + className + '\'' +
                ", semester='" + semester + '\'' +
                '}';
    }
}