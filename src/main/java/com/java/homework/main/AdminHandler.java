package com.java.homework.main;

import com.java.homework.container.BeanContainer;
import com.java.homework.entity.Course;
import com.java.homework.entity.User;
import com.java.homework.service.CourseService;
import com.java.homework.service.GradeService;
import com.java.homework.service.UserService;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * 管理员处理器：处理管理员功能
 */
public class AdminHandler {
    private final User admin;
    private final UserService userService;
    private final CourseService courseService;
    private final GradeService gradeService;
    private final Scanner scanner = new Scanner(System.in);

    public AdminHandler(User admin, UserService userService) {
        this.admin = admin;
        this.userService = userService;
        // 从Bean容器获取其他服务
        BeanContainer container = new BeanContainer("com.java.homework");
        this.courseService = container.getBean(CourseService.class);
        this.gradeService = container.getBean(GradeService.class);
    }

    public void start() {
        while (true) {
            System.out.println("\n===== 管理员功能菜单 =====");
            System.out.println("1. 学生管理");
            System.out.println("2. 教师管理");
            System.out.println("3. 课程管理");
            System.out.println("4. 开课管理");
            System.out.println("5. 统计分析");
            System.out.println("6. 退出系统");
            System.out.print("请选择操作：");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1" -> manageStudents();
                case "2" -> manageTeachers();
                case "3" -> manageCourses();
                case "4" -> createCourse();
                case "5" -> statisticalAnalysis();
                case "6" -> {
                    System.out.println("感谢使用，再见！");
                    return;
                }
                default -> System.out.println("输入错误，请重试！");
            }
        }
    }

    private void manageStudents() {
        while (true) {
            System.out.println("\n===== 学生管理 =====");
            System.out.println("1. 查看所有学生");
            System.out.println("2. 添加学生");
            System.out.println("3. 修改学生信息");
            System.out.println("4. 删除学生");
            System.out.println("5. 返回上一级");
            System.out.print("请选择操作：");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1" -> {
                    System.out.println("\n所有学生列表：");
                    List<User> students = userService.listStudents();
                    if (students.isEmpty()) {
                        System.out.println("暂无学生信息");
                    } else {
                        for (User student : students) {
                            System.out.println(student.getId() + ". " + student.getName() + "（" + student.getUsername() + "）- " + 
                                             student.getClassName() + " - " + student.getMajorName());
                        }
                    }
                }
                case "2" -> addStudent();
                case "3" -> updateStudent();
                case "4" -> deleteStudent();
                case "5" -> {
                    return;
                }
                default -> System.out.println("输入错误，请重试！");
            }
        }
    }

    private void addStudent() {
        System.out.println("\n===== 添加学生 =====");
        User student = new User();
        
        System.out.print("请输入学生姓名：");
        student.setName(scanner.nextLine());
        
        System.out.print("请输入学号（用户名）：");
        student.setUsername(scanner.nextLine());
        
        System.out.print("请输入初始密码：");
        student.setPassword(scanner.nextLine());
        
        System.out.print("请输入班级：");
        student.setClassName(scanner.nextLine());
        
        System.out.print("请输入专业：");
        student.setMajorName(scanner.nextLine());
        
        student.setRole("Student");
        student.setCreatedTime(new Date());
        
        try {
            userService.addStudent(student);
            System.out.println("\n学生添加成功！");
        } catch (Exception e) {
            System.out.println("\n学生添加失败：" + e.getMessage());
        }
    }

    private void updateStudent() {
        System.out.println("\n===== 修改学生信息 =====");
        List<User> students = userService.listStudents();
        
        if (students.isEmpty()) {
            System.out.println("暂无学生信息");
            return;
        }
        
        // 显示所有学生
        for (User student : students) {
            System.out.println(student.getId() + ". " + student.getName() + "（" + student.getUsername() + "）");
        }
        
        System.out.print("\n请输入要修改的学生ID：");
        try {
            int id = Integer.parseInt(scanner.nextLine());
            User student = userService.getUserById(id);
            
            if (student == null || !"Student".equals(student.getRole())) {
                System.out.println("学生不存在！");
                return;
            }
            
            // 显示当前信息
            System.out.println("\n当前学生信息：");
            System.out.println("姓名：" + student.getName());
            System.out.println("学号：" + student.getUsername());
            System.out.println("班级：" + student.getClassName());
            System.out.println("专业：" + student.getMajorName());
            
            // 更新信息（留空表示不修改）
            System.out.print("\n请输入新姓名（留空不修改）：");
            String name = scanner.nextLine();
            if (!name.isEmpty()) {
                student.setName(name);
            }
            
            System.out.print("请输入新密码（留空不修改）：");
            String password = scanner.nextLine();
            if (!password.isEmpty()) {
                student.setPassword(password);
            }
            
            System.out.print("请输入新班级（留空不修改）：");
            String className = scanner.nextLine();
            if (!className.isEmpty()) {
                student.setClassName(className);
            }
            
            System.out.print("请输入新专业（留空不修改）：");
            String majorName = scanner.nextLine();
            if (!majorName.isEmpty()) {
                student.setMajorName(majorName);
            }
            
            System.out.print("\n确认修改学生信息吗？(y/n)：");
            String confirm = scanner.nextLine();
            
            if ("y".equalsIgnoreCase(confirm)) {
                userService.updateUser(student);
                System.out.println("学生信息修改成功！");
            } else {
                System.out.println("已取消修改操作");
            }
        } catch (NumberFormatException e) {
            System.out.println("输入的ID格式错误！");
        } catch (Exception e) {
            System.out.println("修改失败：" + e.getMessage());
        }
    }

    private void deleteStudent() {
        System.out.println("\n===== 删除学生 =====");
        List<User> students = userService.listStudents();
        
        if (students.isEmpty()) {
            System.out.println("暂无学生信息");
            return;
        }
        
        // 显示所有学生
        for (User student : students) {
            System.out.println(student.getId() + ". " + student.getName() + "（" + student.getUsername() + "）");
        }
        
        System.out.print("\n请输入要删除的学生ID：");
        try {
            int id = Integer.parseInt(scanner.nextLine());
            User student = userService.getUserById(id);
            
            if (student == null || !"Student".equals(student.getRole())) {
                System.out.println("学生不存在！");
                return;
            }
            
            System.out.print("确认删除学生" + student.getName() + "吗？(y/n)：");
            String confirm = scanner.nextLine();
            
            if ("y".equalsIgnoreCase(confirm)) {
                userService.deleteUser(id);
                System.out.println("学生删除成功！");
            } else {
                System.out.println("已取消删除操作");
            }
        } catch (NumberFormatException e) {
            System.out.println("输入的ID格式错误！");
        } catch (Exception e) {
            System.out.println("删除失败：" + e.getMessage());
        }
    }

    private void manageTeachers() {
        while (true) {
            System.out.println("\n===== 教师管理 =====");
            System.out.println("1. 查看所有教师");
            System.out.println("2. 添加教师");
            System.out.println("3. 修改教师信息");
            System.out.println("4. 删除教师");
            System.out.println("5. 返回上一级");
            System.out.print("请选择操作：");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1" -> {
                    System.out.println("\n所有教师列表：");
                    List<User> teachers = userService.listTeachers();
                    if (teachers.isEmpty()) {
                        System.out.println("暂无教师信息");
                    } else {
                        for (User teacher : teachers) {
                            System.out.println(teacher.getId() + ". " + teacher.getName() + "（" + teacher.getUsername() + "）");
                        }
                    }
                }
                case "2" -> addTeacher();
                case "3" -> updateTeacher();
                case "4" -> deleteTeacher();
                case "5" -> {
                    return;
                }
                default -> System.out.println("输入错误，请重试！");
            }
        }
    }

    private void addTeacher() {
        System.out.println("\n===== 添加教师 =====");
        User teacher = new User();
        
        System.out.print("请输入教师姓名：");
        teacher.setName(scanner.nextLine());
        
        System.out.print("请输入工号（用户名）：");
        teacher.setUsername(scanner.nextLine());
        
        System.out.print("请输入初始密码：");
        teacher.setPassword(scanner.nextLine());
        
        teacher.setRole("Teacher");
        teacher.setCreatedTime(new Date());
        
        try {
            // 使用addStudent方法但修改角色为Teacher
            teacher.setRole("Teacher");
            userService.addStudent(teacher); // 注意：这里直接使用现有的addStudent方法，因为底层实现是通用的
            System.out.println("\n教师添加成功！");
        } catch (Exception e) {
            System.out.println("\n教师添加失败：" + e.getMessage());
        }
    }

    private void updateTeacher() {
        System.out.println("\n===== 修改教师信息 =====");
        List<User> teachers = userService.listTeachers();
        
        if (teachers.isEmpty()) {
            System.out.println("暂无教师信息");
            return;
        }
        
        // 显示所有教师
        for (User teacher : teachers) {
            System.out.println(teacher.getId() + ". " + teacher.getName() + "（" + teacher.getUsername() + "）");
        }
        
        System.out.print("\n请输入要修改的教师ID：");
        try {
            int id = Integer.parseInt(scanner.nextLine());
            User teacher = userService.getUserById(id);
            
            if (teacher == null || !"Teacher".equals(teacher.getRole())) {
                System.out.println("教师不存在！");
                return;
            }
            
            // 显示当前信息
            System.out.println("\n当前教师信息：");
            System.out.println("姓名：" + teacher.getName());
            System.out.println("工号：" + teacher.getUsername());
            
            // 更新信息（留空表示不修改）
            System.out.print("\n请输入新姓名（留空不修改）：");
            String name = scanner.nextLine();
            if (!name.isEmpty()) {
                teacher.setName(name);
            }
            
            System.out.print("请输入新密码（留空不修改）：");
            String password = scanner.nextLine();
            if (!password.isEmpty()) {
                teacher.setPassword(password);
            }
            
            System.out.print("\n确认修改教师信息吗？(y/n)：");
            String confirm = scanner.nextLine();
            
            if ("y".equalsIgnoreCase(confirm)) {
                userService.updateUser(teacher);
                System.out.println("教师信息修改成功！");
            } else {
                System.out.println("已取消修改操作");
            }
        } catch (NumberFormatException e) {
            System.out.println("输入的ID格式错误！");
        } catch (Exception e) {
            System.out.println("修改失败：" + e.getMessage());
        }
    }

    private void deleteTeacher() {
        System.out.println("\n===== 删除教师 =====");
        List<User> teachers = userService.listTeachers();
        
        if (teachers.isEmpty()) {
            System.out.println("暂无教师信息");
            return;
        }
        
        // 显示所有教师
        for (User teacher : teachers) {
            System.out.println(teacher.getId() + ". " + teacher.getName() + "（" + teacher.getUsername() + "）");
        }
        
        System.out.print("\n请输入要删除的教师ID：");
        try {
            int id = Integer.parseInt(scanner.nextLine());
            User teacher = userService.getUserById(id);
            
            if (teacher == null || !"Teacher".equals(teacher.getRole())) {
                System.out.println("教师不存在！");
                return;
            }
            
            System.out.print("确认删除教师" + teacher.getName() + "吗？(y/n)：");
            String confirm = scanner.nextLine();
            
            if ("y".equalsIgnoreCase(confirm)) {
                userService.deleteUser(id);
                System.out.println("教师删除成功！");
            } else {
                System.out.println("已取消删除操作");
            }
        } catch (NumberFormatException e) {
            System.out.println("输入的ID格式错误！");
        } catch (Exception e) {
            System.out.println("删除失败：" + e.getMessage());
        }
    }

    private void manageCourses() {
        while (true) {
            System.out.println("\n===== 课程管理 =====");
            System.out.println("1. 查看所有课程");
            System.out.println("2. 修改课程信息");
            System.out.println("3. 删除课程");
            System.out.println("4. 返回上一级");
            System.out.print("请选择操作：");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1" -> viewAllCourses();
                case "2" -> updateCourse();
                case "3" -> deleteCourse();
                case "4" -> {
                    return;
                }
                default -> System.out.println("输入错误，请重试！");
            }
        }
    }

    private void viewAllCourses() {
        System.out.println("\n所有课程列表：");
        try {
            List<Course> courses = courseService.listAllCourses();
            if (courses.isEmpty()) {
                System.out.println("暂无课程信息");
            } else {
                System.out.println("\n课程列表：");
                System.out.println("----------------------------------------------------------------------------------");
                System.out.printf("%s\t%-15s\t%s\t%s\t%s\t%s\t\n", 
                        "课程ID", "课程名称", "学分", "教师ID", "班级", "学期");
                System.out.println("----------------------------------------------------------------------------------");
                for (Course course : courses) {
                    System.out.printf("%d\t%-15s\t%d\t%d\t%s\t%s\t\n", 
                            course.getId(), course.getCourseName(), course.getCredits(),
                            course.getTeacherId(), course.getClassName(), course.getSemester());
                }
                System.out.println("----------------------------------------------------------------------------------");
            }
        } catch (Exception e) {
            System.out.println("获取课程列表失败：" + e.getMessage());
        }
    }

    private void updateCourse() {
        System.out.println("\n===== 修改课程信息 =====");
        try {
            List<Course> courses = courseService.listAllCourses();
            if (courses.isEmpty()) {
                System.out.println("暂无课程信息");
                return;
            }

            // 显示所有课程
            for (Course course : courses) {
                System.out.printf("%d. %s (%s - %s)\n", 
                        course.getId(), course.getCourseName(), course.getClassName(), course.getSemester());
            }

            System.out.print("\n请输入要修改的课程ID：");
            try {
                int courseId = Integer.parseInt(scanner.nextLine());
                Course course = courseService.getCourseById(courseId);
                
                if (course == null) {
                    System.out.println("课程不存在！");
                    return;
                }

                // 显示当前信息
                System.out.println("\n当前课程信息：");
                System.out.println("课程名称：" + course.getCourseName());
                System.out.println("学分：" + course.getCredits());
                System.out.println("教师ID：" + course.getTeacherId());
                System.out.println("班级：" + course.getClassName());
                System.out.println("学期：" + course.getSemester());
                System.out.println("班级人数：" + (course.getClassSize() != null ? course.getClassSize() : "未设置"));

                // 更新信息（留空表示不修改）
                System.out.print("\n请输入新课程名称（留空不修改）：");
                String courseName = scanner.nextLine();
                if (!courseName.isEmpty()) {
                    course.setCourseName(courseName);
                }

                System.out.print("请输入新学分（留空不修改）：");
                String creditsStr = scanner.nextLine();
                if (!creditsStr.isEmpty()) {
                    try {
                        course.setCredits(Integer.parseInt(creditsStr));
                    } catch (NumberFormatException e) {
                        System.out.println("学分格式错误，不更新此字段");
                    }
                }

                System.out.print("请输入新教师ID（留空不修改）：");
                String teacherIdStr = scanner.nextLine();
                if (!teacherIdStr.isEmpty()) {
                    try {
                        course.setTeacherId(Integer.parseInt(teacherIdStr));
                    } catch (NumberFormatException e) {
                        System.out.println("教师ID格式错误，不更新此字段");
                    }
                }

                System.out.print("请输入新班级（留空不修改）：");
                String className = scanner.nextLine();
                if (!className.isEmpty()) {
                    course.setClassName(className);
                }

                System.out.print("请输入新学期（留空不修改，格式如2025-2026-1）：");
                String semester = scanner.nextLine();
                if (!semester.isEmpty()) {
                    course.setSemester(semester);
                }

                System.out.print("请输入新班级人数（留空不修改）：");
                String classSizeStr = scanner.nextLine();
                if (!classSizeStr.isEmpty()) {
                    try {
                        course.setClassSize(Integer.parseInt(classSizeStr));
                    } catch (NumberFormatException e) {
                        System.out.println("班级人数格式错误，不更新此字段");
                    }
                }

                System.out.print("\n确认修改课程信息吗？(y/n)：");
                String confirm = scanner.nextLine();
                
                if ("y".equalsIgnoreCase(confirm)) {
                    courseService.updateCourse(course);
                    System.out.println("课程信息修改成功！");
                } else {
                    System.out.println("已取消修改操作");
                }
            } catch (NumberFormatException e) {
                System.out.println("输入的课程ID格式错误！");
            }
        } catch (Exception e) {
            System.out.println("修改课程信息失败：" + e.getMessage());
        }
    }

    private void deleteCourse() {
        System.out.println("\n===== 删除课程 =====");
        try {
            List<Course> courses = courseService.listAllCourses();
            if (courses.isEmpty()) {
                System.out.println("暂无课程信息");
                return;
            }

            // 显示所有课程
            for (Course course : courses) {
                System.out.printf("%d. %s (%s - %s)\n", 
                        course.getId(), course.getCourseName(), course.getClassName(), course.getSemester());
            }

            System.out.print("\n请输入要删除的课程ID：");
            try {
                int courseId = Integer.parseInt(scanner.nextLine());
                Course course = courseService.getCourseById(courseId);
                
                if (course == null) {
                    System.out.println("课程不存在！");
                    return;
                }

                System.out.print("确认删除课程《" + course.getCourseName() + "》吗？删除后相关成绩数据可能会受到影响。(y/n)：");
                String confirm = scanner.nextLine();
                
                if ("y".equalsIgnoreCase(confirm)) {
                    try {
                        courseService.deleteCourse(courseId);
                        System.out.println("课程删除成功！");
                    } catch (Exception e) {
                        System.out.println("课程删除失败：" + e.getMessage());
                        System.out.println("可能原因：该课程下存在成绩记录，请先清理相关数据");
                    }
                } else {
                    System.out.println("已取消删除操作");
                }
            } catch (NumberFormatException e) {
                System.out.println("输入的课程ID格式错误！");
            }
        } catch (Exception e) {
            System.out.println("删除课程失败：" + e.getMessage());
        }
    }

    private void createCourse() {
        while (true) {
            System.out.println("\n===== 开课管理 =====");
            System.out.println("1. 新增开课");
            System.out.println("2. 返回上一级");
            System.out.print("请选择操作：");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1" -> addNewCourseOffering();
                case "2" -> {
                    return;
                }
                default -> System.out.println("输入错误，请重试！");
            }
        }
    }

    private void addNewCourseOffering() {
        System.out.println("\n===== 新增开课 =====");
        try {
            // 显示可选的教师列表
            List<User> teachers = userService.listTeachers();
            if (teachers.isEmpty()) {
                System.out.println("系统中暂无教师信息，请先添加教师！");
                return;
            }

            System.out.println("\n可选教师列表：");
            for (User teacher : teachers) {
                System.out.println(teacher.getId() + ". " + teacher.getName() + "（" + teacher.getUsername() + "）");
            }

            Course course = new Course();
            
            System.out.print("\n请输入课程名称：");
            course.setCourseName(scanner.nextLine());
            
            System.out.print("请输入学分：");
            try {
                course.setCredits(Integer.parseInt(scanner.nextLine()));
            } catch (NumberFormatException e) {
                System.out.println("学分格式错误，默认设为2");
                course.setCredits(2);
            }
            
            System.out.print("请选择任课教师ID：");
            try {
                course.setTeacherId(Integer.parseInt(scanner.nextLine()));
            } catch (NumberFormatException e) {
                System.out.println("教师ID格式错误，请重新操作");
                return;
            }
            
            System.out.print("请输入授课班级：");
            course.setClassName(scanner.nextLine());
            
            System.out.print("请输入开课学期（格式如2025-2026-1）：");
            course.setSemester(scanner.nextLine());
            
            System.out.print("请输入班级人数：");
            try {
                course.setClassSize(Integer.parseInt(scanner.nextLine()));
            } catch (NumberFormatException e) {
                System.out.println("班级人数格式错误，不设置默认值");
            }

            // 确认信息
            System.out.println("\n开课信息确认：");
            System.out.println("课程名称：" + course.getCourseName());
            System.out.println("学分：" + course.getCredits());
            System.out.println("任课教师ID：" + course.getTeacherId());
            System.out.println("授课班级：" + course.getClassName());
            System.out.println("开课学期：" + course.getSemester());
            System.out.println("班级人数：" + (course.getClassSize() != null ? course.getClassSize() : "未设置"));
            
            System.out.print("\n确认创建该课程吗？(y/n)：");
            String confirm = scanner.nextLine();
            
            if ("y".equalsIgnoreCase(confirm)) {
                try {
                    courseService.createCourse(course, course.getTeacherId());
                    System.out.println("课程创建成功！");
                } catch (Exception e) {
                    System.out.println("课程创建失败：" + e.getMessage());
                }
            } else {
                System.out.println("已取消创建操作");
            }
        } catch (Exception e) {
            System.out.println("新增开课失败：" + e.getMessage());
        }
    }

    private void statisticalAnalysis() {
        while (true) {
            System.out.println("\n===== 统计分析 =====");
            System.out.println("1. 班级GPA排名");
            System.out.println("2. 挂科学生统计");
            System.out.println("3. 学霸画像统计");
            System.out.println("4. 系统用户统计");
            System.out.println("5. 返回上一级");
            System.out.print("请选择操作：");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1" -> classGPARanking();
                case "2" -> failedStudentsStatistics();
                case "3" -> topStudentsAnalysis();
                case "4" -> systemUserStatistics();
                case "5" -> {
                    return;
                }
                default -> System.out.println("输入错误，请重试！");
            }
        }
    }

    private void classGPARanking() {
        System.out.println("\n===== 班级GPA排名 =====");
        try {
            // 获取所有班级（从学生信息中提取）
            List<User> students = userService.listStudents();
            if (students.isEmpty()) {
                System.out.println("暂无学生信息，无法进行班级GPA排名");
                return;
            }

            System.out.println("\n请输入要统计的班级名称：");
            // 显示所有可用班级
            System.out.println("可用班级：");
            for (User student : students) {
                if (student.getClassName() != null) {
                    System.out.println("- " + student.getClassName());
                }
            }

            System.out.print("\n请输入班级名称：");
            String className = scanner.nextLine();

            Map<User, Double> ranking = gradeService.calculateClassGPARanking(className);
            if (ranking.isEmpty()) {
                System.out.println("该班级暂无GPA数据");
            } else {
                System.out.println("\n" + className + " 班级GPA排名：");
                System.out.println("----------------------------------------");
                System.out.printf("%s\t%-10s\t%s\t\n", "排名", "学生姓名", "GPA");
                System.out.println("----------------------------------------");
                
                int rank = 1;
                for (Map.Entry<User, Double> entry : ranking.entrySet()) {
                    User student = entry.getKey();
                    Double gpa = entry.getValue();
                    System.out.printf("%d\t%-10s\t%.2f\t\n", rank++, student.getName(), gpa);
                }
                System.out.println("----------------------------------------");
            }
        } catch (Exception e) {
            System.out.println("班级GPA排名统计失败：" + e.getMessage());
        }
    }

    private void failedStudentsStatistics() {
        System.out.println("\n===== 挂科学生统计 =====");
        try {
            System.out.print("\n请输入挂科次数阈值（如2表示统计挂科2次及以上的学生）：");
            try {
                int threshold = Integer.parseInt(scanner.nextLine());
                
                List<User> failedStudents = gradeService.getStudentsWithFailCount(threshold);
                if (failedStudents.isEmpty()) {
                    System.out.println("\n没有挂科次数达到" + threshold + "次的学生");
                } else {
                    System.out.println("\n挂科次数达到" + threshold + "次的学生列表：");
                    System.out.println("----------------------------------------");
                    System.out.printf("%s\t%-10s\t%s\t%s\t\n", "ID", "学生姓名", "学号", "班级");
                    System.out.println("----------------------------------------");
                    
                    for (User student : failedStudents) {
                        System.out.printf("%d\t%-10s\t%s\t%s\t\n", 
                                student.getId(), student.getName(), student.getUsername(), 
                                (student.getClassName() != null ? student.getClassName() : "未分配"));
                    }
                    System.out.println("----------------------------------------");
                    System.out.println("\n总计：" + failedStudents.size() + "名学生");
                }
            } catch (NumberFormatException e) {
                System.out.println("输入的阈值格式错误，请输入有效的数字");
            }
        } catch (Exception e) {
            System.out.println("挂科学生统计失败：" + e.getMessage());
        }
    }

    private void topStudentsAnalysis() {
        System.out.println("\n===== 学霸画像统计 =====");
        try {
            List<User> topStudents = gradeService.getTopStudents();
            
            if (topStudents.isEmpty()) {
                System.out.println("\n暂无符合学霸标准的学生");
            } else {
                System.out.println("\n学霸画像学生（所有课程≥85分且修课≥4门）：");
                System.out.println("----------------------------------------");
                System.out.printf("%s\t%-10s\t%s\t%s\t%s\t\n", "ID", "学生姓名", "学号", "班级", "专业");
                System.out.println("----------------------------------------");
                
                for (User student : topStudents) {
                    System.out.printf("%d\t%-10s\t%s\t%s\t%s\t\n", 
                            student.getId(), student.getName(), student.getUsername(),
                            (student.getClassName() != null ? student.getClassName() : "未分配"),
                            (student.getMajorName() != null ? student.getMajorName() : "未分配"));
                }
                System.out.println("----------------------------------------");
                System.out.println("\n总计：" + topStudents.size() + "名学生");
            }
        } catch (Exception e) {
            System.out.println("学霸画像统计失败：" + e.getMessage());
        }
    }

    private void systemUserStatistics() {
        System.out.println("\n===== 系统用户统计 =====");
        try {
            List<User> students = userService.listStudents();
            List<User> teachers = userService.listTeachers();
            
            System.out.println("\n系统用户统计信息：");
            System.out.println("----------------------------------------");
            System.out.println("学生总数：" + students.size() + "人");
            System.out.println("教师总数：" + teachers.size() + "人");
            System.out.println("系统总用户数：" + (students.size() + teachers.size() + 1) + "人（含管理员）");
            System.out.println("----------------------------------------");
            
            // 统计班级学生分布
            Map<String, Integer> classDistribution = new java.util.HashMap<>();
            for (User student : students) {
                if (student.getClassName() != null) {
                    classDistribution.put(student.getClassName(), 
                            classDistribution.getOrDefault(student.getClassName(), 0) + 1);
                }
            }
            
            if (!classDistribution.isEmpty()) {
                System.out.println("\n班级学生分布：");
                System.out.println("----------------------------------------");
                for (Map.Entry<String, Integer> entry : classDistribution.entrySet()) {
                    System.out.printf("%-20s：%d人\n", entry.getKey(), entry.getValue());
                }
                System.out.println("----------------------------------------");
            }
        } catch (Exception e) {
            System.out.println("系统用户统计失败：" + e.getMessage());
        }
    }
}