package com.java.homework.proxy;

import com.java.homework.util.JDBCUtil;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 服务层动态代理工厂：实现日志输出和事务管理
 */
public class ServiceProxyFactory {
    // 日志时间格式
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 为Service实例创建代理对象
     */
    public static Object createProxy(Object targetService) {
        // JDK动态代理：只能代理接口
        return Proxy.newProxyInstance(
                targetService.getClass().getClassLoader(),
                targetService.getClass().getInterfaces(),
                new ServiceInvocationHandler(targetService)
        );
    }

    /**
     * 调用处理器：增强Service方法
     */
    private static class ServiceInvocationHandler implements InvocationHandler {
        private final Object target; // 目标Service实例

        public ServiceInvocationHandler(Object target) {
            this.target = target;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            String methodName = method.getName();
            String operation = getOperationType(methodName); // 解析操作类型
            Object result = null;
            String logMsg = "[" + DATE_FORMAT.format(new Date()) + "] 完成了 [" + operation + "] 操作";

            try {
                // 事务管理：增删改方法开启事务
                if (isModifyMethod(methodName)) {
                    JDBCUtil.beginTransaction();
                }

                // 输出日志
                System.out.println(logMsg);

                // 执行目标方法
                result = method.invoke(target, args);

                // 提交事务
                if (isModifyMethod(methodName)) {
                    JDBCUtil.commitTransaction();
                }
            } catch (Exception e) {
                // 回滚事务
                if (isModifyMethod(methodName)) {
                    JDBCUtil.rollbackTransaction();
                }
                throw e;
            }

            return result;
        }

        /**
         * 根据方法名判断操作类型
         */
        private String getOperationType(String methodName) {
            if (methodName.startsWith("add")) {
                return "新增";
            } else if (methodName.startsWith("update")) {
                return "修改";
            } else if (methodName.startsWith("delete")) {
                return "删除";
            } else {
                return "查询";
            }
        }

        /**
         * 判断是否为增删改方法
         */
        private boolean isModifyMethod(String methodName) {
            return methodName.startsWith("add") || methodName.startsWith("update") || methodName.startsWith("delete");
        }
    }
}