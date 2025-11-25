package com.java.homework.main;

import com.java.homework.entity.User;
import com.java.homework.service.UserService;
import java.util.Scanner;

/**
 * 登录处理器：处理用户登录逻辑
 */
public class LoginHandler {
    private final UserService userService;
    private final Scanner scanner = new Scanner(System.in);

    public LoginHandler(UserService userService) {
        this.userService = userService;
    }

    public void start() {
        while (true) {
            System.out.println("\n===== 学生课程管理系统 =====");
            System.out.print("请输入用户名：");
            String username = scanner.nextLine();
            System.out.print("请输入密码：");
            String password = scanner.nextLine();

            User user = userService.login(username, password);
            if (user == null) {
                System.out.println("用户名或密码错误，请重试！");
                continue;
            }

            System.out.println("登录成功！欢迎 " + user.getName() + "（" + user.getRole() + "）");
            switch (user.getRole()) {
                case "Admin" -> new AdminHandler(user, userService).start();
                case "Teacher" -> new TeacherHandler(user, userService).start();
                case "Student" -> new StudentHandler(user, userService).start();
                default -> System.out.println("未知角色，登录失败！");
            }
            break; // 登录一次后退出循环，实际可根据需求调整
        }
        scanner.close();
    }
}