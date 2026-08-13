package com.bkbits.admin.service.impl;

import com.bkbits.admin.service.TenantService;
import com.bkbits.dbo.entity.Tenant;
import com.easy.query.api.proxy.client.EasyEntityQuery;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;

import java.util.List;
import java.util.Objects;

@Component
public class TenantServiceImpl implements TenantService {

    @Inject
    EasyEntityQuery easyEntityQuery;

    @Override
    public Tenant add(Tenant tenant) {
        Objects.requireNonNull(tenant, "租户不能为空");
        if (easyEntityQuery.insertable(tenant).executeRows() != 1) {
            throw new IllegalStateException("创建租户失败");
        }
        return tenant;
    }

    @Override
    public Tenant getById(String id) {
        return easyEntityQuery.queryable(Tenant.class)
                .whereById(requireText(id, "租户编号"))
                .singleOrNull();
    }

    @Override
    public List<Tenant> list() {
        return easyEntityQuery.queryable(Tenant.class)
                .orderBy(o -> o.id().asc())
                .toList();
    }

    @Override
    public Tenant update(Tenant tenant) {
        Objects.requireNonNull(tenant, "租户不能为空");
        requireText(tenant.getId(), "租户编号");
        easyEntityQuery.updatable(tenant)
                .executeRows(1, "更新租户失败");
        return tenant;
    }

    @Override
    public void removeById(String id) {
        easyEntityQuery.deletable(Tenant.class)
                .whereById(requireText(id, "租户编号"))
                .executeRows(1, "删除租户失败");
    }

    private String requireText(String value, String name) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(name + "不能为空");
        }
        return value;
    }
}
