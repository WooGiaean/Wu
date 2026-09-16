package com.wjy.personal_blog.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/*
* 此注解用于检查是否当前用户执行相关操作
* 确保用户的唯一性
* */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface autoCheck {
    /**
     * 是否需要登录认证
     */
    boolean requireLogin() default true;

    /**
     * 是否打印日志
     */
    boolean logEnabled() default true;

    /**
     * 日志级别
     */
  //  String logLevel() default "INFO";

    /**
     * 操作描述
     */
    String description() default "";
}
