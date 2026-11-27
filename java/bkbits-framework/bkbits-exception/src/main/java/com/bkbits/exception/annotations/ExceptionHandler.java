package com.bkbits.exception.annotations;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ExceptionHandler {
    /**
     * @return 需要捕获的异常类（匹配时按异常类及其父类查找，不支持接口）
     */
    Class<? extends Throwable>[] value();
}
