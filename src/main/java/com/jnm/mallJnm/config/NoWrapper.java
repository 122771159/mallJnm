package com.jnm.mallJnm.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义注解，用于标记不需要被GlobalResponseAdvice包装的Controller方法。
 * 主要用于文件下载等直接操作response流的场景。
 */
@Target({ElementType.METHOD}) // 这个注解只能用在方法上
@Retention(RetentionPolicy.RUNTIME) // 运行时保留，这样我们才能通过反射读取到它
public @interface NoWrapper {
}