package com.java.homework.annotation;

import java.lang.annotation.*;

/**
 * 自定义Component注解，用于标记需被Bean容器管理的类
 */
@Target(ElementType.TYPE)       // 作用于类
@Retention(RetentionPolicy.RUNTIME) // 运行时保留
@Documented
public @interface Component_Marujun {
}