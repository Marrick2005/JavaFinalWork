package com.java.homework.dao.impl;

import com.google.gson.reflect.TypeToken;
import com.java.homework.annotation.Component_Marujun;
import com.java.homework.dao.CourseDAO;
import com.java.homework.entity.Course;
import com.java.homework.util.JSONUtil;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 课程DAO的JSON文件实现
 */
@Component_Marujun
public class FileCourseDAO implements CourseDAO {
    private static final String FILE_NAME = "courses.json";
    private final AtomicInteger idGenerator = new AtomicInteger(1);

    public FileCourseDAO() {
        List<Course> courses = listAll();
        if (!courses.isEmpty()) {
            int maxId = courses.stream().mapToInt(Course::getId).max().getAsInt();
            idGenerator.set(maxId + 1);
        }
    }

    @Override
    public Course findById(Integer id) {
        List<Course> courses = JSONUtil.readFromJson(FILE_NAME, new TypeToken<List<Course>>() {}.getType());
        return courses.stream().filter(c -> c.getId().equals(id)).findFirst().orElse(null);
    }

    @Override
    public List<Course> findByTeacherId(Integer teacherId) {
        List<Course> courses = JSONUtil.readFromJson(FILE_NAME, new TypeToken<List<Course>>() {}.getType());
        return courses.stream().filter(c -> c.getTeacherId().equals(teacherId)).toList();
    }

    @Override
    public void addCourse(Course course) {
        List<Course> courses = JSONUtil.readFromJson(FILE_NAME, new TypeToken<List<Course>>() {}.getType());
        course.setId(idGenerator.getAndIncrement());
        courses.add(course);
        JSONUtil.writeToJson(FILE_NAME, courses);
    }

    @Override
    public void deleteCourse(Integer id) {
        List<Course> courses = JSONUtil.readFromJson(FILE_NAME, new TypeToken<List<Course>>() {}.getType());
        courses.removeIf(c -> c.getId().equals(id));
        JSONUtil.writeToJson(FILE_NAME, courses);
    }

    @Override
    public void updateCourse(Course course) {
        List<Course> courses = JSONUtil.readFromJson(FILE_NAME, new TypeToken<List<Course>>() {}.getType());
        for (int i = 0; i < courses.size(); i++) {
            if (courses.get(i).getId().equals(course.getId())) {
                courses.set(i, course);
                break;
            }
        }
        JSONUtil.writeToJson(FILE_NAME, courses);
    }

    @Override
    public List<Course> listAll() {
        return JSONUtil.readFromJson(FILE_NAME, new TypeToken<List<Course>>() {}.getType());
    }

    @Override
    public Course findByClassAndName(String className, String courseName) {
        List<Course> courses = JSONUtil.readFromJson(FILE_NAME, new TypeToken<List<Course>>() {}.getType());
        return courses.stream()
                .filter(c -> c.getClassName().equals(className) && c.getCourseName().equals(courseName))
                .findFirst()
                .orElse(null);
    }
}