package com.java.homework.main;

import com.java.homework.container.BeanContainer;
import com.java.homework.service.UserService;

/**
 * 系统主入口
 */
public class Main {
    public static void main(String[] args) {
        // 初始化Bean容器
        BeanContainer container = new BeanContainer("com.java.homework");
        // 获取UserService（通过代理增强）
        UserService userService = (UserService) container.getBean(UserService.class);

        // 启动登录流程
        LoginHandler loginHandler = new LoginHandler(userService);
        loginHandler.start();
    }
}