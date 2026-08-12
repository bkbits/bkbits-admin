package com.bkbits.admin.service.impl;

import com.bkbits.admin.service.DeptService;
import com.bkbits.dbo.entity.Dept;
import com.easy.query.api.proxy.client.EasyEntityQuery;
import com.easy.query.core.enums.SQLExecuteStrategyEnum;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;

import java.util.List;
import java.util.Objects;

@Component
public class DeptServiceImpl implements DeptService {

    @Inject
    EasyEntityQuery easyEntityQuery;

    @Override
    public Dept add(Dept dept) {
        Objects.requireNonNull(dept, "部门不能为空");
        if (easyEntityQuery.insertable(dept).executeRows() != 1) {
            throw new IllegalStateException("创建部门失败");
        }
        return dept;
    }

    @Override
    public Dept getById(String deptId) {
        return easyEntityQuery.queryable(Dept.class)
                .whereById(requireText(deptId, "部门编号"))
                .singleOrNull();
    }

    @Override
    public List<Dept> listByTenantId(String tenantId) {
        return easyEntityQuery.queryable(Dept.class)
                .where(o -> o.tenantId().eq(requireText(tenantId, "租户编号")))
                .orderBy(o -> {
                    o.parentId().asc();
                    o.sort().asc();
                    o.deptId().asc();
                })
                .toList();
    }

    @Override
    public List<Dept> listByParentId(String parentId) {
        return easyEntityQuery.queryable(Dept.class)
                .where(o -> {
                    if (parentId == null || parentId.isBlank()) {
                        o.parentId().isNull();
                    } else {
                        o.parentId().eq(parentId);
                    }
                })
                .orderBy(o -> {
                    o.sort().asc();
                    o.deptId().asc();
                })
                .toList();
    }

    @Override
    public Dept update(Dept dept) {
        Objects.requireNonNull(dept, "部门不能为空");
        requireText(dept.getDeptId(), "部门编号");
        easyEntityQuery.updatable(dept)
                .setSQLStrategy(SQLExecuteStrategyEnum.ONLY_NOT_NULL_COLUMNS)
                .executeRows(1, "更新部门失败");
        return dept;
    }

    @Override
    public void removeById(String deptId) {
        easyEntityQuery.deletable(Dept.class)
                .whereById(requireText(deptId, "部门编号"))
                .executeRows(1, "删除部门失败");
    }

    private String requireText(String value, String name) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(name + "不能为空");
        }
        return value;
    }
}
