package com.bkbits.logging.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface Log {
    public static final String UNKNOWN = "未分类";
    public static final String INSERT = "新增";
    public static final String UPDATE = "更新";
    public static final String DELETE = "删除";
    public static final String LOGIN = "登录";
    public static final String LOGOUT = "注销";

    String value();

    String module() default "";

    String type() default UNKNOWN;

    boolean args() default false;

    boolean result() default false;

    String remark() default "";
}
