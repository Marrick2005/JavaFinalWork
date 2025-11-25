package com.java.homework.dao.impl;

import com.google.gson.reflect.TypeToken;
import com.java.homework.annotation.Component_Marujun;
import com.java.homework.dao.GradeDAO;
import com.java.homework.entity.Grade;
import com.java.homework.util.JSONUtil;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 成绩DAO的JSON文件实现
 */
@Component_Marujun
public class FileGradeDAO implements GradeDAO {
    private static final String FILE_NAME = "grades.json";
    private final AtomicInteger idGenerator = new AtomicInteger(1);

    public FileGradeDAO() {
        List<Grade> grades = JSONUtil.readFromJson(FILE_NAME, new TypeToken<List<Grade>>() {}.getType());
        if (!grades.isEmpty()) {
            int maxId = grades.stream().mapToInt(Grade::getId).max().getAsInt();
            idGenerator.set(maxId + 1);
        }
    }

    @Override
    public List<Grade> findByStudentId(Integer studentId) {
        List<Grade> grades = JSONUtil.readFromJson(FILE_NAME, new TypeToken<List<Grade>>() {}.getType());
        return grades.stream().filter(g -> g.getStudentId().equals(studentId)).toList();
    }

    @Override
    public List<Grade> findByCourseId(Integer courseId) {
        List<Grade> grades = JSONUtil.readFromJson(FILE_NAME, new TypeToken<List<Grade>>() {}.getType());
        return grades.stream().filter(g -> g.getOfferingId().equals(courseId)).toList();
    }

    @Override
    public void addGrade(Grade grade) {
        List<Grade> grades = JSONUtil.readFromJson(FILE_NAME, new TypeToken<List<Grade>>() {}.getType());
        grade.setId(idGenerator.getAndIncrement());
        grades.add(grade);
        JSONUtil.writeToJson(FILE_NAME, grades);
    }

    @Override
    public void batchAddGrade(List<Grade> grades) {
        List<Grade> existingGrades = JSONUtil.readFromJson(FILE_NAME, new TypeToken<List<Grade>>() {}.getType());
        for (Grade grade : grades) {
            grade.setId(idGenerator.getAndIncrement());
            existingGrades.add(grade);
        }
        JSONUtil.writeToJson(FILE_NAME, existingGrades);
    }

    @Override
    public void updateGrade(Grade grade) {
        List<Grade> grades = JSONUtil.readFromJson(FILE_NAME, new TypeToken<List<Grade>>() {}.getType());
        for (int i = 0; i < grades.size(); i++) {
            if (grades.get(i).getId().equals(grade.getId())) {
                grades.set(i, grade);
                break;
            }
        }
        JSONUtil.writeToJson(FILE_NAME, grades);
    }

    @Override
    public Double calculateAvgScore(Integer courseId) {
        List<Grade> grades = findByCourseId(courseId);
        if (grades.isEmpty()) {
            return 0.0;
        }
        int sum = grades.stream().mapToInt(Grade::getScore).sum();
        return (double) sum / grades.size();
    }

    @Override
    public Double calculatePassRate(Integer courseId) {
        List<Grade> grades = findByCourseId(courseId);
        if (grades.isEmpty()) {
            return 0.0;
        }
        long passCount = grades.stream().filter(g -> g.getScore() >= 60).count();
        return (double) passCount / grades.size() * 100;
    }

    @Override
    public Integer countFailedGrades(Integer studentId) {
        List<Grade> grades = findByStudentId(studentId);
        return (int) grades.stream().filter(g -> g.getScore() < 60).count();
    }
}