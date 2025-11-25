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
 * 教师处理器：处理教师功能
 */
public class TeacherHandler {
    private final User teacher;
    private final UserService userService;
    private final CourseService courseService;
    private final GradeService gradeService;
    private final Scanner scanner = new Scanner(System.in);

    public TeacherHandler(User teacher, UserService userService) {
        this.teacher = teacher;
        this.userService = userService;
        // 从Bean容器获取其他服务
        BeanContainer container = new BeanContainer("com.java.homework");
        this.courseService = container.getBean(CourseService.class);
        this.gradeService = container.getBean(GradeService.class);
    }

    public void start() {
        while (true) {
            System.out.println("\n===== 教师功能菜单 =====");
            System.out.println("1. 我的课表");
            System.out.println("2. 录入成绩");
            System.out.println("3. 成绩统计");
            System.out.println("4. 退出系统");
            System.out.print("请选择操作：");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1" -> viewCourseSchedule();
                case "2" -> inputGrades();
                case "3" -> gradeStatistics();
                case "4" -> {
                    System.out.println("感谢使用，再见！");
                    return;
                }
                default -> System.out.println("输入错误，请重试！");
            }
        }
    }

    private void viewCourseSchedule() {
        System.out.println("\n===== 我的课表 =====");
        try {
            List<Course> courses = courseService.listTeacherCourses(teacher.getId());
            if (courses.isEmpty()) {
                System.out.println("当前没有教授的课程");
            } else {
                System.out.println("\n课程列表：");
                System.out.println("----------------------------------------------------");
                System.out.printf("%s\t%-15s\t%s\t%s\t%s\t\n", "课程ID", "课程名称", "学分", "班级", "学期");
                System.out.println("----------------------------------------------------");
                for (Course course : courses) {
                    System.out.printf("%d\t%-15s\t%d\t%s\t%s\t\n", 
                            course.getId(), course.getCourseName(), course.getCredits(), 
                            course.getClassName(), course.getSemester());
                }
                System.out.println("----------------------------------------------------");
            }
        } catch (Exception e) {
            System.out.println("获取课表失败：" + e.getMessage());
        }
        System.out.println("\n按回车键返回菜单...");
        scanner.nextLine();
    }

    private void inputGrades() {
        System.out.println("\n===== 录入成绩 =====");
        try {
            // 先显示教师教授的课程列表
            List<Course> courses = courseService.listTeacherCourses(teacher.getId());
            if (courses.isEmpty()) {
                System.out.println("您当前没有教授的课程，无法录入成绩");
                System.out.println("\n按回车键返回菜单...");
                scanner.nextLine();
                return;
            }

            System.out.println("\n请选择要录入成绩的课程：");
            for (Course course : courses) {
                System.out.printf("%d. %s (%s)", course.getId(), course.getCourseName(), course.getClassName());
            }

            System.out.print("\n请输入课程ID：");
            String courseIdStr = scanner.nextLine();
            try {
                Integer courseId = Integer.parseInt(courseIdStr);
                Course selectedCourse = courseService.getCourseById(courseId);
                if (selectedCourse == null || !selectedCourse.getTeacherId().equals(teacher.getId())) {
                    System.out.println("无效的课程ID或您没有权限操作该课程");
                } else {
                    // 显示成绩录入界面
                    System.out.println("\n正在录入课程《" + selectedCourse.getCourseName() + "》的成绩");
                    System.out.println("\n提示：输入学生ID和成绩，输入-1结束录入");
                    
                    while (true) {
                        System.out.print("请输入学生ID（-1结束）：");
                        String studentIdStr = scanner.nextLine();
                        if ("-1".equals(studentIdStr)) {
                            break;
                        }
                        
                        try {
                            Integer studentId = Integer.parseInt(studentIdStr);
                            System.out.print("请输入成绩（0-100）：");
                            String scoreStr = scanner.nextLine();
                            Integer score = Integer.parseInt(scoreStr);
                            
                            if (score < 0 || score > 100) {
                                System.out.println("成绩必须在0-100之间，请重新输入");
                                continue;
                            }
                            
                            // 录入成绩
                            Grade grade = new Grade();
                            grade.setStudentId(studentId);
                            grade.setOfferingId(courseId);
                            grade.setScore(score);
                            grade.setCourseName(selectedCourse.getCourseName());
                            
                            gradeService.addGrade(grade);
                            System.out.println("成绩录入成功！");
                        } catch (NumberFormatException e) {
                            System.out.println("输入格式错误，请输入有效的数字");
                        }
                    }
                }
            } catch (NumberFormatException e) {
                System.out.println("无效的课程ID格式");
            }
        } catch (Exception e) {
            System.out.println("成绩录入失败：" + e.getMessage());
        }
        System.out.println("\n按回车键返回菜单...");
        scanner.nextLine();
    }

    private void gradeStatistics() {
        System.out.println("\n===== 成绩统计 =====");
        try {
            // 先显示教师教授的课程列表
            List<Course> courses = courseService.listTeacherCourses(teacher.getId());
            if (courses.isEmpty()) {
                System.out.println("您当前没有教授的课程，无法进行成绩统计");
                System.out.println("\n按回车键返回菜单...");
                scanner.nextLine();
                return;
            }

            System.out.println("\n请选择要统计的课程：");
            for (Course course : courses) {
                System.out.printf("%d. %s (%s)\n", course.getId(), course.getCourseName(), course.getClassName());
            }

            System.out.print("\n请输入课程ID：");
            String courseIdStr = scanner.nextLine();
            try {
                Integer courseId = Integer.parseInt(courseIdStr);
                Course selectedCourse = courseService.getCourseById(courseId);
                if (selectedCourse == null || !selectedCourse.getTeacherId().equals(teacher.getId())) {
                    System.out.println("无效的课程ID或您没有权限操作该课程");
                } else {
                    // 获取成绩列表
                    List<Grade> grades = gradeService.queryCourseGrades(courseId);
                    if (grades.isEmpty()) {
                        System.out.println("该课程暂无成绩数据");
                    } else {
                        // 计算并显示统计信息
                        Double avgScore = gradeService.calculateCourseAvgScore(courseId);
                        Double passRate = gradeService.calculateCoursePassRate(courseId);
                        
                        System.out.println("\n===== " + selectedCourse.getCourseName() + " 成绩统计 =====");
                        System.out.println("课程名称：" + selectedCourse.getCourseName());
                        System.out.println("班级：" + selectedCourse.getClassName());
                        System.out.println("统计人数：" + grades.size());
                        System.out.println("平均分：" + String.format("%.2f", avgScore));
                        System.out.println("及格率：" + String.format("%.2f%%", passRate));
                        
                        // 显示成绩分布
                        System.out.println("\n成绩分布：");
                        int[] distribution = new int[5]; // 0-59, 60-69, 70-79, 80-89, 90-100
                        for (Grade grade : grades) {
                            if (grade.getScore() < 60) distribution[0]++;
                            else if (grade.getScore() < 70) distribution[1]++;
                            else if (grade.getScore() < 80) distribution[2]++;
                            else if (grade.getScore() < 90) distribution[3]++;
                            else distribution[4]++;
                        }
                        
                        System.out.println("不及格(0-59)：" + distribution[0] + "人");
                        System.out.println("及格(60-69)：" + distribution[1] + "人");
                        System.out.println("中等(70-79)：" + distribution[2] + "人");
                        System.out.println("良好(80-89)：" + distribution[3] + "人");
                        System.out.println("优秀(90-100)：" + distribution[4] + "人");
                    }
                }
            } catch (NumberFormatException e) {
                System.out.println("无效的课程ID格式");
            }
        } catch (Exception e) {
            System.out.println("成绩统计失败：" + e.getMessage());
        }
        System.out.println("\n按回车键返回菜单...");
        scanner.nextLine();
    }
}