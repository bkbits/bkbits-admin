package com.bkbits.orm.interceptor;

import com.bkbits.id.IdUtil;
import com.bkbits.orm.IGenId;
import com.easy.query.core.basic.extension.interceptor.EntityInterceptor;
import com.easy.query.core.expression.sql.builder.EntityInsertExpressionBuilder;
import com.easy.query.core.expression.sql.builder.EntityUpdateExpressionBuilder;
import org.jspecify.annotations.NonNull;
import org.noear.solon.annotation.Component;

@Component
public class GenIdInterceptor implements EntityInterceptor {

    @Override
    public void configureInsert(Class<?> entityClass, EntityInsertExpressionBuilder entityInsertExpressionBuilder, Object entity) {
        IGenId genId = (IGenId) entity;
        if (genId.getId() == null) {
            genId.setId(IdUtil.nextId());
        }
    }

    @Override
    public void configureUpdate(Class<?> entityClass, EntityUpdateExpressionBuilder entityUpdateExpressionBuilder, Object entity) {

    }

    @Override
    public String name() {
        return getClass().getSimpleName();
    }

    @Override
    public boolean apply(@NonNull Class<?> entityClass) {
        return IGenId.class.isAssignableFrom(entityClass);
    }
}
