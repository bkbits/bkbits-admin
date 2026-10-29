package com.bkbits.admin.permission;

import com.bkbits.auth.LoginUtil;
import com.bkbits.dbo.entity.Dept;
import com.bkbits.id.IdUtil;
import com.bkbits.orm.ICreateBy;
import com.easy.query.core.expression.parser.core.base.WherePredicate;
import com.easy.query.core.expression.sql.builder.LambdaEntityExpressionBuilder;
import org.jspecify.annotations.NonNull;

public class ParentDeptDataScope implements IDataScope {
    public static final String NAME = "ParentDept";

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
        return "所属上级部门";
    }

    @Override
    public void configure(
            Class<?> entityClass,
            LambdaEntityExpressionBuilder lambdaEntityExpressionBuilder,
            WherePredicate<Object> wherePredicate) {
        wherePredicate.sqlNativeSegment("""
                            {0} in (
                            WITH RECURSIVE {1} AS (
                                SELECT {5} FROM {4} WHERE {5} = {7}
                                UNION ALL
                                SELECT {2}.{5}
                                FROM {4} {2}
                                INNER JOIN {1} {3} ON {2}.{5} = {3}.{6}
                            )
                            SELECT {5} FROM {1}
                        )
                        """,
                (ctx) -> {
                    // 实体表列
                    ctx.columnName("deptId"); // 0
                    // 表或别名
                    ctx.expression("dept_cte_" + IdUtil.nextId()); // 1
                    ctx.expression("d"); // 2
                    ctx.expression("dt"); // 3
                    ctx.expression(
                            wherePredicate.getRuntimeContext()
                                    .getEntityMetadataManager()
                                    .getEntityMetadata(Dept.class)
                                    .getTableName()
                    ); // 4
                    // 子查询列
                    ctx.expression("dept_id");// 5
                    ctx.expression("parent_id");// 6
                    ctx.value(LoginUtil.getLoginUser().getDeptId());// 7
                });
    }
}
