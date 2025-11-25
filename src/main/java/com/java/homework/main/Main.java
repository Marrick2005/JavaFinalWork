package com.java.homework.main;

import com.java.homework.container.BeanContainer;
import com.java.homework.service.UserService;
import com.java.homework.util.LogUtil;

/**
 * 系统主入口
 */
public class Main {
    public static void main(String[] args) {
        // 添加关闭钩子，确保程序退出时关闭日志资源
        Runtime.getRuntime().addShutdownHook(new Thread(LogUtil::close));
        
        LogUtil.info("程序启动...");
        
        // 初始化Bean容器
        BeanContainer container = new BeanContainer("com.java.homework");
        // 获取UserService（通过代理增强）
        UserService userService = (UserService) container.getBean(UserService.class);

        // 启动登录流程
        LoginHandler loginHandler = new LoginHandler(userService);
        loginHandler.start();
        
        LogUtil.info("程序结束");
    }
}