package com.java.homework.container;

import com.java.homework.annotation.Autowired_Marujun;
import com.java.homework.annotation.Component_Marujun;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 自定义Bean容器：扫描Component注解类，实现依赖注入
 */
public class BeanContainer {
    // 存储Bean实例：key=Bean类型，value=Bean实例
    private final Map<Class<?>, Object> beanMap = new HashMap<>();
    // 扫描的基础包名
    private final String basePackage;

    /**
     * 构造函数：指定基础包，初始化容器
     */
    public BeanContainer(String basePackage) {
        this.basePackage = basePackage;
        scanPackages(); // 扫描包并创建Bean
        injectDependencies(); // 依赖注入
    }

    /**
     * 1. 扫描基础包下所有带Component_Marujun注解的类，创建实例并存入beanMap
     */
    private void scanPackages() {
        try {
            // 将包名转为路径（com.java.homework → com/java/homework）
            String packagePath = basePackage.replace(".", "/");
            // 获取类加载器加载的资源
            URL url = Thread.currentThread().getContextClassLoader().getResource(packagePath);
            
            if (url == null) {
                throw new RuntimeException("扫描的包不存在：" + basePackage);
            }
            
            // 修复Windows环境下URL路径解码问题
            String filePath = url.getFile();
            // 正确解码URL路径
            filePath = java.net.URLDecoder.decode(filePath, "UTF-8");
            
            File packageDir = new File(filePath);
            // 递归扫描目录下的.class文件
            List<Class<?>> componentClasses = scanClasses(packageDir, basePackage);

            // 为每个Component类创建实例
            for (Class<?> clazz : componentClasses) {
                if (clazz.isAnnotationPresent(Component_Marujun.class)) {
                    // 检查是否有带Autowired_Marujun注解的构造函数
                    boolean hasAutowiredConstructor = false;
                    for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
                        if (constructor.isAnnotationPresent(Autowired_Marujun.class)) {
                            hasAutowiredConstructor = true;
                            break;
                        }
                    }
                    
                    // 如果有Autowired构造函数，暂时不实例化，先存储Class对象
                    // 如果没有Autowired构造函数，使用无参构造实例化
                    if (!hasAutowiredConstructor) {
                        Object bean = clazz.getDeclaredConstructor().newInstance(); // 无参构造实例化
                        beanMap.put(clazz, bean);
                        // 同时存储接口类型
                        for (Class<?> iface : clazz.getInterfaces()) {
                            beanMap.put(iface, bean);
                        }
                    } else {
                        // 只存储Class对象，等待依赖注入阶段处理
                        beanMap.put(clazz, null);
                        // 同时为接口类型创建映射占位符
                        for (Class<?> iface : clazz.getInterfaces()) {
                            beanMap.put(iface, null);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Bean扫描失败", e);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> clazz) {
        T bean = (T) beanMap.get(clazz);
        if (bean == null) {
            throw new RuntimeException("Bean未找到：" + clazz.getName());
        }
        return bean;
    }

    private void injectDependencies() {
        // 先处理没有依赖的Bean
        boolean progress;
        do {
            progress = false;
            // 直接传递原始beanMap给HashMap构造函数，而不是entrySet()
            for (Map.Entry<Class<?>, Object> entry : new HashMap<Class<?>, Object>(beanMap).entrySet()) {
                Class<?> clazz = entry.getKey();
                if (clazz.isAnnotationPresent(Component_Marujun.class) && entry.getValue() == null) {
                    // 查找带Autowired_Marujun注解的构造函数
                    for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
                        if (constructor.isAnnotationPresent(Autowired_Marujun.class)) {
                            try {
                                // 获取构造函数的参数类型（依赖的Bean类型）
                                Class<?>[] paramTypes = constructor.getParameterTypes();
                                Object[] dependencies = new Object[paramTypes.length];
                                boolean allDependenciesAvailable = true;
                                
                                // 检查所有依赖是否都已可用
                                for (int i = 0; i < paramTypes.length; i++) {
                                    dependencies[i] = beanMap.get(paramTypes[i]);
                                    if (dependencies[i] == null) {
                                        allDependenciesAvailable = false;
                                        break;
                                    }
                                }
                                
                                // 如果所有依赖都可用，创建实例
                                if (allDependenciesAvailable) {
                                    constructor.setAccessible(true); // 允许访问私有构造
                                    Object bean = constructor.newInstance(dependencies);
                                    beanMap.put(clazz, bean);
                                    // 更新接口类型的Bean实例
                                    for (Class<?> iface : clazz.getInterfaces()) {
                                        beanMap.put(iface, bean);
                                    }
                                    progress = true;
                                    break;
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                throw new RuntimeException("依赖注入失败：" + clazz.getName(), e);
                            }
                        }
                    }
                }
            }
        } while (progress); // 直到没有进展为止
        
        // 检查是否还有未处理的Bean（可能存在循环依赖）
        for (Map.Entry<Class<?>, Object> entry : beanMap.entrySet()) {
            if (entry.getValue() == null && entry.getKey().isAnnotationPresent(Component_Marujun.class)) {
                throw new RuntimeException("无法实例化Bean：" + entry.getKey().getName() + "，可能存在循环依赖或缺少依赖");
            }
        }
    }

    /**
     * 递归扫描目录下的所有.class文件，返回Class对象列表
     */
    private List<Class<?>> scanClasses(File dir, String packageName) {
        List<Class<?>> classes = new ArrayList<>();
        if (!dir.exists()) {
            return classes;
        }
        File[] files = dir.listFiles();
        if (files == null) {
            return classes;
        }
        for (File file : files) {
            if (file.isDirectory()) {
                // 子目录：递归扫描，包名拼接
                String subPackage = packageName + "." + file.getName();
                classes.addAll(scanClasses(file, subPackage));
            } else if (file.getName().endsWith(".class")) {
                // .class文件：加载类
                String className = packageName + "." + file.getName().replace(".class", "");
                try {
                    Class<?> clazz = Class.forName(className);
                    classes.add(clazz);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        return classes;
    }
}