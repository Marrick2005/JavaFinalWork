package com.java.homework.main;

import com.java.homework.container.BeanContainer;
import com.java.homework.entity.Course;
import com.java.homework.entity.Grade;
import com.java.homework.entity.User;
import com.java.homework.service.CourseService;
import com.java.homework.service.GradeService;
import com.java.homework.service.UserService;
import java.util.List;
import java.util.Scanner;

/**
 * 学生处理器：处理学生功能
 */
public class StudentHandler {
    private final User student;
    private final UserService userService;
    private final GradeService gradeService;
    private final CourseService courseService;
    private final Scanner scanner = new Scanner(System.in);

    public StudentHandler(User student, UserService userService) {
        this.student = student;
        this.userService = userService;
        // 从Bean容器获取其他服务
        BeanContainer container = new BeanContainer("com.java.homework");
        this.gradeService = container.getBean(GradeService.class);
        this.courseService = container.getBean(CourseService.class);
    }

    public void start() {
        while (true) {
            System.out.println("\n===== 学生功能菜单 =====");
            System.out.println("1. 我的成绩");
            System.out.println("2. GPA查询");
            System.out.println("3. 退出系统");
            System.out.print("请选择操作：");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1" -> viewMyGrades();
                case "2" -> viewGPA();
                case "3" -> {
                    System.out.println("感谢使用，再见！");
                    return;
                }
                default -> System.out.println("输入错误，请重试！");
            }
        }
    }

    private void viewMyGrades() {
        System.out.println("\n===== 我的成绩 =====");
        try {
            List<Grade> grades = gradeService.queryStudentGrades(student.getId());
            if (grades.isEmpty()) {
                System.out.println("当前没有成绩记录");
            } else {
                System.out.println("\n成绩列表：");
                System.out.println("----------------------------------------------------");
                System.out.printf("%-15s\t%s\t%s\t\n", "课程名称", "成绩", "等级");
                System.out.println("----------------------------------------------------");
                
                int totalCredits = 0;
                double weightedScore = 0.0;
                
                for (Grade grade : grades) {
                    // 获取课程信息以计算学分
                    Course course = courseService.getCourseById(grade.getOfferingId());
                    String gradeLevel = calculateGradeLevel(grade.getScore());
                    
                    System.out.printf("%-15s\t%d\t%s\t\n", 
                            grade.getCourseName(), grade.getScore(), gradeLevel);
                    
                    if (course != null && grade.getScore() >= 60) {
                        totalCredits += course.getCredits();
                        weightedScore += grade.getScore() * course.getCredits();
                    }
                }
                System.out.println("----------------------------------------------------");
                
                // 计算平均分（仅计算及格科目）
                if (totalCredits > 0) {
                    double avgScore = weightedScore / totalCredits;
                    System.out.printf("\n加权平均分：%.2f\n", avgScore);
                }
            }
        } catch (Exception e) {
            System.out.println("获取成绩失败：" + e.getMessage());
        }
        System.out.println("\n按回车键返回菜单...");
        scanner.nextLine();
    }

    private void viewGPA() {
        System.out.println("\n===== GPA查询 =====");
        try {
            List<Grade> grades = gradeService.queryStudentGrades(student.getId());
            if (grades.isEmpty()) {
                System.out.println("当前没有成绩记录，无法计算GPA");
            } else {
                double totalPoints = 0.0; // 总学分绩点
                int totalCredits = 0;     // 总学分
                
                System.out.println("\n课程成绩及学分绩点：");
                System.out.println("----------------------------------------------------------------------");
                System.out.printf("%-15s\t%s\t%s\t%s\t%s\t\n", "课程名称", "成绩", "等级", "学分", "学分绩点");
                System.out.println("----------------------------------------------------------------------");
                
                for (Grade grade : grades) {
                    Course course = courseService.getCourseById(grade.getOfferingId());
                    if (course != null) {
                        String gradeLevel = calculateGradeLevel(grade.getScore());
                        double point = calculateGradePoint(grade.getScore());
                        
                        System.out.printf("%-15s\t%d\t%s\t%d\t%.2f\t\n", 
                                grade.getCourseName(), grade.getScore(), gradeLevel, 
                                course.getCredits(), point);
                        
                        // 只有及格科目计入GPA计算
                        if (grade.getScore() >= 60) {
                            totalPoints += point * course.getCredits();
                            totalCredits += course.getCredits();
                        }
                    }
                }
                System.out.println("----------------------------------------------------------------------");
                
                // 计算GPA
                if (totalCredits > 0) {
                    double gpa = totalPoints / totalCredits;
                    System.out.printf("\n总学分：%d\n", totalCredits);
                    System.out.printf("GPA：%.2f\n", gpa);
                    
                    // 添加GPA等级评定
                    String gpaLevel = getGPALevel(gpa);
                    System.out.println("GPA等级：" + gpaLevel);
                } else {
                    System.out.println("\n所有科目均未及格，无法计算GPA");
                }
            }
        } catch (Exception e) {
            System.out.println("GPA计算失败：" + e.getMessage());
        }
        System.out.println("\n按回车键返回菜单...");
        scanner.nextLine();
    }
    
    // 计算成绩等级
    private String calculateGradeLevel(int score) {
        if (score >= 90) return "优秀";
        if (score >= 80) return "良好";
        if (score >= 70) return "中等";
        if (score >= 60) return "及格";
        return "不及格";
    }
    
    // 计算学分绩点（4.0制）
    private double calculateGradePoint(int score) {
        if (score >= 90) return 4.0;
        if (score >= 85) return 3.7;
        if (score >= 82) return 3.3;
        if (score >= 78) return 3.0;
        if (score >= 75) return 2.7;
        if (score >= 72) return 2.3;
        if (score >= 68) return 2.0;
        if (score >= 64) return 1.5;
        if (score >= 60) return 1.0;
        return 0.0;
    }
    
    // 获取GPA等级评定
    private String getGPALevel(double gpa) {
        if (gpa >= 3.8) return "优秀";
        if (gpa >= 3.5) return "良好";
        if (gpa >= 3.0) return "中等";
        if (gpa >= 2.5) return "一般";
        if (gpa >= 2.0) return "及格";
        return "需努力";
    }
}