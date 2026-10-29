package com.bkbits.logging.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface Log {
    String UNKNOWN = "未分类";
    String INSERT = "新增";
    String UPDATE = "更新";
    String DELETE = "删除";
    String LOGIN = "登录";
    String LOGOUT = "注销";

    String value();

    String module() default "";

    String type() default UNKNOWN;

    boolean args() default false;

    boolean result() default false;

    String remark() default "";
}
