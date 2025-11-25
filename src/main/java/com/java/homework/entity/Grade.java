package com.java.homework.entity;

/**
 * 成绩实体
 */
public class Grade {
    private Integer id;
    private Integer studentId; // 关联Users表id
    private Integer offeringId; // 关联Courses表id（开课实例）
    private Integer score; // 成绩（0-100）
    private String courseName; // 课程名称（用于显示）

    public Grade() {}

    // Getter和Setter
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getStudentId() { return studentId; }
    public void setStudentId(Integer studentId) { this.studentId = studentId; }
    public Integer getOfferingId() { return offeringId; }
    public void setOfferingId(Integer offeringId) { this.offeringId = offeringId; }
    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }
    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }

    @Override
    public String toString() {
        return "Grade{" +
                "id=" + id +
                ", studentId=" + studentId +
                ", offeringId=" + offeringId +
                ", score=" + score +
                ", courseName='" + courseName + "'" +
                '}';
    }
}