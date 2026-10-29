package com.bkbits.admin.permission;

import com.bkbits.auth.LoginUtil;
import com.bkbits.orm.ICreateBy;
import com.easy.query.core.expression.parser.core.base.WherePredicate;
import com.easy.query.core.expression.sql.builder.LambdaEntityExpressionBuilder;
import org.jspecify.annotations.NonNull;

public class OnlyDeptDataScope implements IDataScope {
    public static final String NAME = "OnlyDept";

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public boolean apply(@NonNull Class<?> entityClass) {
        return entityClass.isAssignableFrom(ICreateBy.class);
    }

    @Override
    public String display() {
        return "仅所属部门";
    }

    @Override
    public void configure(
            Class<?> entityClass,
            LambdaEntityExpressionBuilder lambdaEntityExpressionBuilder,
            WherePredicate<Object> wherePredicate) {
        wherePredicate.eq("deptId", LoginUtil.getLoginUser().getDeptId());
    }
}
