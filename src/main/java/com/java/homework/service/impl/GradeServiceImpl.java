package com.java.homework.service.impl;

import com.java.homework.annotation.Autowired_Marujun;
import com.java.homework.annotation.Component_Marujun;
import com.java.homework.dao.CourseDAO;
import com.java.homework.dao.GradeDAO;
import com.java.homework.dao.UserDAO;
import com.java.homework.entity.Course;
import com.java.homework.entity.Grade;
import com.java.homework.entity.User;
import com.java.homework.service.GradeService;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 成绩服务实现类：依赖GradeDAO、CourseDAO、UserDAO，实现成绩相关逻辑
 */
@Component_Marujun
public class GradeServiceImpl implements GradeService {
    private final GradeDAO gradeDAO;
    private final CourseDAO courseDAO;
    private final UserDAO userDAO;

    // 多依赖构造函数注入
    @Autowired_Marujun
    public GradeServiceImpl(GradeDAO gradeDAO, CourseDAO courseDAO, UserDAO userDAO) {
        this.gradeDAO = gradeDAO;
        this.courseDAO = courseDAO;
        this.userDAO = userDAO;
    }

    @Override
    public void addGrade(Grade grade) {
        // 校验成绩合法性（0-100分）
        if (grade.getScore() < 0 || grade.getScore() > 100) {
            throw new RuntimeException("录入失败：成绩需在0-100分之间");
        }
        // 调用DAO新增成绩
        gradeDAO.addGrade(grade);
    }

    @Override
    public void batchAddGrade(List<Grade> grades) {
        // 批量校验成绩合法性
        boolean hasInvalidScore = grades.stream()
                .anyMatch(grade -> grade.getScore() < 0 || grade.getScore() > 100);
        if (hasInvalidScore) {
            throw new RuntimeException("批量录入失败：存在无效成绩（需在0-100分之间）");
        }
        // 调用DAO批量新增
        gradeDAO.batchAddGrade(grades);
    }

    @Override
    public List<Grade> queryStudentGrades(Integer studentId) {
        // 查询学生成绩，并关联课程名称
        List<Grade> grades = gradeDAO.findByStudentId(studentId);
        grades.forEach(grade -> {
            Course course = courseDAO.findById(grade.getOfferingId());
            if (course != null) {
                grade.setCourseName(course.getCourseName());
            }
        });
        return grades;
    }

    @Override
    public List<Grade> queryCourseGrades(Integer courseId) {
        // 调用DAO查询指定课程的所有成绩
        return gradeDAO.findByCourseId(courseId);
    }

    @Override
    public Double calculateCourseAvgScore(Integer courseId) {
        // 调用DAO计算平均成绩，保留2位小数
        double avg = gradeDAO.calculateAvgScore(courseId);
        return Math.round(avg * 100.0) / 100.0;
    }

    @Override
    public Double calculateCoursePassRate(Integer courseId) {
        // 调用DAO计算及格率，保留2位小数
        double passRate = gradeDAO.calculatePassRate(courseId);
        return Math.round(passRate * 100.0) / 100.0;
    }

    @Override
    public Map<User, Double> calculateClassGPARanking(String className) {
        // 1. 查询该班级所有学生
        List<User> students = userDAO.listAll("Student").stream()
                .filter(student -> className.equals(student.getClassName()))
                .toList();
    
        // 2. 计算每个学生的学分绩点 - 只保留正确的lambda表达式版本
        Map<User, Double> studentGPA = students.stream()
                .collect(Collectors.toMap(
                        student -> student,
                        student -> this.calculateSingleStudentGPA(student.getId())
                ));
    
        // 3. 按绩点降序排序（使用LinkedHashMap保持顺序）
        Map<User, Double> result = new LinkedHashMap<>();
        studentGPA.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .forEach(entry -> result.put(entry.getKey(), entry.getValue()));
        return result;
    }

    /**
     * 计算单个学生的学分绩点（私有工具方法）
     * 公式：绩点 = Σ[(成绩-50)/10 * 学分] / Σ学分
     */
    private Double calculateSingleStudentGPA(Integer studentId) {
        List<Grade> grades = gradeDAO.findByStudentId(studentId);
        if (grades.isEmpty()) {
            return 0.0;
        }

        double totalGPA = 0.0; // 总绩点（绩点*学分累加）
        int totalCredits = 0;  // 总学分

        for (Grade grade : grades) {
            Course course = courseDAO.findById(grade.getOfferingId());
            if (course != null) {
                int credits = course.getCredits();
                totalCredits += credits;
                // 成绩<50分时绩点为0（避免出现负数）
                double gpa = grade.getScore() < 50 ? 0 : (grade.getScore() - 50) / 10.0;
                totalGPA += gpa * credits;
            }
        }

        // 避免除以0，保留2位小数
        return totalCredits == 0 ? 0.0 : Math.round((totalGPA / totalCredits) * 100.0) / 100.0;
    }

    @Override
    public List<User> getStudentsWithFailCount(Integer failThreshold) {
        // 查询所有学生，筛选挂科次数≥阈值的学生
        List<User> students = userDAO.listAll("Student");
        return students.stream()
                .filter(student -> gradeDAO.countFailedGrades(student.getId()) >= failThreshold)
                .toList();
    }

    @Override
    public List<User> getTopStudents() {
        // 筛选“学霸”：所有课程≥85分且修课≥4门
        List<User> students = userDAO.listAll("Student");
        return students.stream()
                .filter(this::isTopStudent)
                .toList();
    }

    /**
     * 判断单个学生是否为“学霸”（私有工具方法）
     */
    private boolean isTopStudent(User student) {
        List<Grade> grades = gradeDAO.findByStudentId(student.getId());
        // 条件1：修课数量≥4门
        if (grades.size() < 4) {
            return false;
        }
        // 条件2：所有课程成绩≥85分
        return grades.stream().allMatch(grade -> grade.getScore() >= 85);
    }
}