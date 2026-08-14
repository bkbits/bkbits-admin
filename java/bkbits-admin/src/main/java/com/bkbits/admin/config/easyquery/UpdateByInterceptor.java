package com.bkbits.admin.config.easyquery;

import com.bkbits.auth.LoginUtil;
import com.bkbits.orm.ICreateBy;
import com.bkbits.orm.IUpdateBy;
import com.easy.query.core.basic.extension.interceptor.EntityInterceptor;
import com.easy.query.core.expression.sql.builder.EntityInsertExpressionBuilder;
import com.easy.query.core.expression.sql.builder.EntityUpdateExpressionBuilder;
import org.jspecify.annotations.NonNull;
import org.noear.solon.annotation.Component;

import java.time.LocalDateTime;

@Component
public class UpdateByInterceptor implements EntityInterceptor {
    @Override
    public void configureInsert(Class<?> entityClass, EntityInsertExpressionBuilder entityInsertExpressionBuilder, Object entity) {

    }

    @Override
    public void configureUpdate(Class<?> entityClass, EntityUpdateExpressionBuilder entityUpdateExpressionBuilder, Object entity) {
        IUpdateBy createBy = (IUpdateBy) entity;
        if (createBy.getUpdateBy() == null) {
            if (LoginUtil.isLogin()) {
                createBy.setUpdateBy(LoginUtil.getLoginUserName());
            } else {
                createBy.setUpdateBy("system");
            }
        }

        if (createBy.getUpdateTime() == null) {
            createBy.setUpdateTime(LocalDateTime.now());
        }
    }

    @Override
    public String name() {
        return "UpdateBy";
    }

    @Override
    public boolean apply(@NonNull Class<?> entityClass) {
        return IUpdateBy.class.isAssignableFrom(entityClass);
    }
}
