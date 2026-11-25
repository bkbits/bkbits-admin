package com.bkbits.admin.bootstrap;

import com.bkbits.admin.pojo.DataScopeVO;
import org.noear.solon.annotation.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 菜单数据域注册表。
 *
 * <p>保存 {@link AdminBootstrap} 构建出的「权限标识 -> 可选数据域」映射，供角色授权时查询。</p>
 */
@Component
public class MenuRegistry {

    private volatile Map<String, List<DataScopeVO>> dataScopes = Map.of();

    /**
     * 注册菜单数据域。
     *
     * @param dataScopes 权限标识 -> 可选数据域
     */
    public void register(Map<String, List<DataScopeVO>> dataScopes) {
        Map<String, List<DataScopeVO>> copy = new LinkedHashMap<>();
        dataScopes.forEach((permission, scopes) -> copy.put(permission, List.copyOf(scopes)));
        this.dataScopes = Map.copyOf(copy);
    }

    /**
     * 查询权限标识对应的可选数据域。
     *
     * @param permission 权限标识
     * @return 数据域列表；未配置时返回空列表
     */
    public List<DataScopeVO> listDataScopes(String permission) {
        if (permission == null) {
            return List.of();
        }
        return dataScopes.getOrDefault(permission, List.of());
    }
}
