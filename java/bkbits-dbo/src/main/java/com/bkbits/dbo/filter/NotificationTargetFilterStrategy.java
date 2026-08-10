package com.bkbits.dbo.filter;

import com.bkbits.dbo.constants.BaseConstants;
import com.bkbits.dbo.entity.Dept;
import com.bkbits.dbo.entity.Tenant;
import com.bkbits.dbo.entity.User;
import com.easy.query.core.basic.extension.navigate.NavigateBuilder;
import com.easy.query.core.basic.extension.navigate.NavigateExtraFilterStrategy;
import com.easy.query.core.expression.lambda.SQLActionExpression1;
import com.easy.query.core.expression.parser.core.base.WherePredicate;

import java.util.Objects;

/**
 * 通知目标关联过滤策略
 *
 * <p>{@code Notification.targetId} 为多态外键，指向的目标实体由 {@code Notification.type} 决定：
 * 租户(T)、部门(D)、用户(U)。该策略在导航查询目标通知列表时，按主实体类型附加通知类型过滤条件。</p>
 */
public class NotificationTargetFilterStrategy implements NavigateExtraFilterStrategy {

    @Override
    public SQLActionExpression1<WherePredicate<?>> getPredicateFilterExpression(NavigateBuilder builder) {
        Class<?> entityClass = builder.getNavigateOption().getEntityMetadata().getEntityClass();
        if (Objects.equals(Tenant.class, entityClass)) {
            return o -> o.eq("type", BaseConstants.NOTIFICATION_TYPE_TENANT);
        }
        if (Objects.equals(Dept.class, entityClass)) {
            return o -> o.eq("type", BaseConstants.NOTIFICATION_TYPE_DEPT);
        }
        if (Objects.equals(User.class, entityClass)) {
            return o -> o.eq("type", BaseConstants.NOTIFICATION_TYPE_USER);
        }
        throw new RuntimeException("不支持的通知目标关联实体: " + entityClass.getName());
    }

    @Override
    public SQLActionExpression1<WherePredicate<?>> getPredicateMappingClassFilterExpression(NavigateBuilder builder) {
        return null;
    }
}
