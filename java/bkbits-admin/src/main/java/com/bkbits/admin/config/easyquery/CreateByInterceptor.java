package com.bkbits.admin.config.easyquery;

import com.bkbits.auth.LoginUtil;
import com.bkbits.orm.ICreateBy;
import com.easy.query.core.basic.extension.interceptor.EntityInterceptor;
import com.easy.query.core.expression.sql.builder.EntityInsertExpressionBuilder;
import com.easy.query.core.expression.sql.builder.EntityUpdateExpressionBuilder;
import org.jspecify.annotations.NonNull;
import org.noear.solon.annotation.Component;

import java.time.LocalDateTime;

@Component
public class CreateByInterceptor implements EntityInterceptor {
    @Override
    public void configureInsert(Class<?> entityClass, EntityInsertExpressionBuilder entityInsertExpressionBuilder, Object entity) {
        ICreateBy createBy = (ICreateBy) entity;
        if (createBy.getCreateBy() == null) {
            if (LoginUtil.isLogin()) {
                createBy.setCreateBy(LoginUtil.getLoginUserName());
            } else {
                createBy.setCreateBy("system");
            }
        }

        if (createBy.getCreateTime() == null) {
            createBy.setCreateTime(LocalDateTime.now());
        }
    }

    @Override
    public void configureUpdate(Class<?> entityClass, EntityUpdateExpressionBuilder entityUpdateExpressionBuilder, Object entity) {

    }

    @Override
    public String name() {
        return "CreateBy";
    }

    @Override
    public boolean apply(@NonNull Class<?> entityClass) {
        return ICreateBy.class.isAssignableFrom(entityClass);
    }
}
