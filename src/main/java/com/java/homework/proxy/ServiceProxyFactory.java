package com.java.homework.proxy;

import com.java.homework.util.JDBCUtil;
import com.java.homework.util.LogUtil;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

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
            String operation = getOperationType(methodName);
            String serviceName = target.getClass().getSimpleName();
            Object result = null;
            long startTime = System.currentTimeMillis();
            
            // 构建详细日志信息
            StringBuilder logBuilder = new StringBuilder();
            logBuilder.append("Service: [").append(serviceName).append("] ");
            logBuilder.append("Method: [").append(methodName).append("] ");
            logBuilder.append("Operation: [").append(operation).append("]");
            
            // 记录参数信息
            if (args != null && args.length > 0) {
                logBuilder.append(" Args: [");
                for (int i = 0; i < args.length; i++) {
                    logBuilder.append(args[i]);
                    if (i < args.length - 1) {
                        logBuilder.append(", ");
                    }
                }
                logBuilder.append("]");
            }
            
            try {
                // 事务管理：增删改方法开启事务
                if (isModifyMethod(methodName)) {
                    JDBCUtil.beginTransaction();
                }
                
                // 执行目标方法
                result = method.invoke(target, args);
                
                // 提交事务
                if (isModifyMethod(methodName)) {
                    JDBCUtil.commitTransaction();
                }
                
                // 记录执行时间和结果
                long endTime = System.currentTimeMillis();
                logBuilder.append(" Time: [").append(endTime - startTime).append("ms]");
                
                // 对于查询操作，可以选择性记录结果数量
                if (!isModifyMethod(methodName) && result instanceof List) {
                    logBuilder.append(" ResultSize: [").append(((List<?>) result).size()).append("]");
                }
                
                // 使用LogUtil输出成功日志
                LogUtil.info(logBuilder.toString() + " Status: [Success]");
                
            } catch (Exception e) {
                // 回滚事务
                if (isModifyMethod(methodName)) {
                    JDBCUtil.rollbackTransaction();
                }
                
                // 使用LogUtil输出错误日志
                LogUtil.error(logBuilder.toString() + " Status: [Error]", e);
                throw e.getCause(); // 抛出原始异常
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