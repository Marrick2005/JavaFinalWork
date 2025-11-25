package com.java.homework.annotation;

import java.lang.annotation.*;

/**
 * 自定义Autowired注解，用于构造函数依赖注入
 */
@Target(ElementType.CONSTRUCTOR) // 作用于构造函数
@Retention(RetentionPolicy.RUNTIME) // 运行时保留
@Documented
public @interface Autowired_Marujun {
}