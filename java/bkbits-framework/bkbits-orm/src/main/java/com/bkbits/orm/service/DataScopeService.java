package com.bkbits.orm.service;

import com.bkbits.orm.IDataScope;

import java.util.Arrays;
import java.util.List;

/**
 * 数据域服务
 *
 */
public interface DataScopeService {
    @FunctionalInterface
    public static interface RunnableException {
        void run() throws Throwable;
    }

    default void beginDataScope(String... dataScopes) {
        beginDataScope(Arrays.asList(dataScopes));
    }

    void beginDataScope(List<String> dataScopes);

    void endDataScope();

    default void execWithDataScope(List<String> dataScopes, RunnableException runnable) {
        beginDataScope(dataScopes);
        try {
            runnable.run();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        } finally {
            endDataScope();
        }
    }

    /**
     * 获取所有启用的数据域
     */
    List<IDataScope> getDataScopes();
}
