package com.bkbits.admin.config.easyquery;

import com.bkbits.generator.IdGenerator;
import com.bkbits.orm.IGenId;
import com.easy.query.core.basic.extension.interceptor.EntityInterceptor;
import com.easy.query.core.expression.sql.builder.EntityInsertExpressionBuilder;
import com.easy.query.core.expression.sql.builder.EntityUpdateExpressionBuilder;
import org.jspecify.annotations.NonNull;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;

@Component
public class GenIdInterceptor implements EntityInterceptor {
    @Inject
    private IdGenerator idGenerator;

    @Override
    public void configureInsert(Class<?> entityClass, EntityInsertExpressionBuilder entityInsertExpressionBuilder, Object entity) {
        IGenId genId = (IGenId) entity;
        if (genId.getId() != null) {
            genId.setId(idGenerator.nextId());
        }
    }

    @Override
    public void configureUpdate(Class<?> entityClass, EntityUpdateExpressionBuilder entityUpdateExpressionBuilder, Object entity) {

    }

    @Override
    public String name() {
        return "GenId";
    }

    @Override
    public boolean apply(@NonNull Class<?> entityClass) {
        return IGenId.class.isAssignableFrom(entityClass);
    }
}
