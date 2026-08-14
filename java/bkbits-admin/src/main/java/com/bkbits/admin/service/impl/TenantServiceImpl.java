package com.bkbits.admin.service.impl;

import com.bkbits.admin.service.TenantService;
import com.bkbits.dbo.entity.Tenant;
import com.bkbits.util.ValidUtil;
import com.easy.query.api.proxy.client.EasyEntityQuery;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;

import java.util.List;

@Component
public class TenantServiceImpl implements TenantService {

    @Inject
    EasyEntityQuery easyEntityQuery;

    @Override
    public Tenant add(Tenant tenant) {
        ValidUtil.requireNotNull(tenant, "租户不能为空");
        if (easyEntityQuery.insertable(tenant).executeRows() != 1) {
            throw new IllegalStateException("创建租户失败");
        }
        return tenant;
    }

    @Override
    public Tenant getById(String id) {
        return easyEntityQuery.queryable(Tenant.class)
                .whereById(ValidUtil.requireString(id, "租户编号不能为空"))
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
        ValidUtil.requireNotNull(tenant, "租户不能为空");
        ValidUtil.requireString(tenant.getId(), "租户编号不能为空");
        easyEntityQuery.updatable(tenant)
                .executeRows(1, "更新租户失败");
        return tenant;
    }

    @Override
    public void removeById(String id) {
        easyEntityQuery.deletable(Tenant.class)
                .whereById(ValidUtil.requireString(id, "租户编号不能为空"))
                .executeRows(1, "删除租户失败");
    }

}
