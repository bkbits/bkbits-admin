package com.bkbits.admin.service.impl;

import com.bkbits.admin.service.DeptService;
import com.bkbits.dbo.entity.Dept;
import com.bkbits.util.ValidUtil;
import com.easy.query.api.proxy.client.EasyEntityQuery;
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
                .whereById(ValidUtil.requireString(deptId, "部门编号不能为空"))
                .singleOrNull();
    }

    @Override
    public List<Dept> listByTenantId(String tenantId) {
        return easyEntityQuery.queryable(Dept.class)
                .where(o -> o.tenantId().eq(ValidUtil.requireString(tenantId, "租户编号不能为空")))
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
        ValidUtil.requireNotNull(dept, "部门不能为空");
        ValidUtil.requireString(dept.getDeptId(), "部门编号不能为空");
        easyEntityQuery.updatable(dept)
                .executeRows(1, "更新部门失败");
        return dept;
    }

    @Override
    public void removeById(String deptId) {
        easyEntityQuery.deletable(Dept.class)
                .whereById(ValidUtil.requireString(deptId, "部门编号不能为空"))
                .executeRows(1, "删除部门失败");
    }

}
