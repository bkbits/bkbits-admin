package com.bkbits.admin.permission;

import com.easy.query.core.basic.extension.interceptor.PredicateFilterInterceptor;

/**
 * 数据域接口
 */
public interface IDataScope extends PredicateFilterInterceptor {
    /**
     * @return 展示名称
     */
    String display();
}
