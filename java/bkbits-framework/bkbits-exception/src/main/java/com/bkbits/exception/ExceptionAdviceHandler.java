package com.bkbits.exception;

import com.bkbits.core.Result;
import lombok.AllArgsConstructor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@AllArgsConstructor
public class ExceptionAdviceHandler {
    private final ExceptionAdvice controllerAdvice;
    private final Method method;

    public Result<?> handle(Throwable e) throws Throwable {
        try {
            if (method.getParameterCount() == 0) {
                return (Result<?>) method.invoke(controllerAdvice);
            }
            return (Result<?>) method.invoke(controllerAdvice, e);
        } catch (InvocationTargetException ex) {
            throw ex.getTargetException();
        }
    }

    @Override
    public String toString() {
        return method.getDeclaringClass().getName() + "#" + method.getName();
    }
}
