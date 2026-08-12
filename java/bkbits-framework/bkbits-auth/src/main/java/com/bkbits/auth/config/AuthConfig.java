package com.bkbits.auth.config;

import cn.dev33.satoken.config.SaTokenConfig;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.solon.integration.SaTokenInterceptor;
import cn.dev33.satoken.stp.StpUtil;
import com.bkbits.auth.AuthProperties;
import com.bkbits.auth.IAuthConfigProvider;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;

/**
 * 认证配置。
 *
 * <p>注册 {@link SaTokenInterceptor} 拦截器，支持路径规则拦截与注解鉴权
 * （如 @SaCheckLogin、@SaCheckPermission、@SaIgnore 等）。</p>
 *
 * <p>权限/角色数据由业务模块实现 {@link cn.dev33.satoken.stp.StpInterface}
 * 并注册为 Bean 提供。</p>
 */
@Configuration
public class AuthConfig {

    /**
     * 注册 Sa-Token 核心配置。
     *
     * <p>默认值取自 {@link AuthProperties}（{@code bkbits.auth.*}）；
     * 业务模块实现并注册 {@link IAuthConfigProvider} 时覆盖对应配置项。</p>
     */
    @Bean
    public SaTokenConfig getSaTokenConfigPrimary(
            @Inject AuthProperties authProperties,
            @Inject(required = false) IAuthConfigProvider authConfigProvider
    ) {
        SaTokenConfig config = new SaTokenConfig();
        // token 名称（同时也是 cookie 名称）
        config.setTokenName(authProperties.getTokenName());
        // token 有效期（单位：秒），默认30天，-1代表永不过期
        config.setTimeout(authProperties.getTimeout());
        // token 最低活跃频率（单位：秒），超过此时间未访问会被冻结，默认30分钟，-1代表不限制
        config.setActiveTimeout(authProperties.getTti());
        // 是否允许同一账号多地同时登录（true 允许一起登录，false 新登录挤掉旧登录）
        config.setIsConcurrent(authProperties.isConcurrent());
        // 多人登录同一账号时，是否共用一个 token（true 共用一个，false 每次登录新建）
        config.setIsShare(authProperties.isShare());
        // token 风格
        config.setTokenStyle(authProperties.getStyle());
        // 是否输出操作日志
        config.setIsLog(authProperties.isLog());

        // 业务模块提供 authConfigProvider 时，覆盖默认配置
        if (authConfigProvider != null) {
            config.setTimeout(authConfigProvider.getTimeout());
            config.setActiveTimeout(authConfigProvider.getTTI());
            config.setIsConcurrent(authConfigProvider.isConcurrent());
            config.setIsShare(authConfigProvider.isShare());
            config.setIsLog(authConfigProvider.isLog());
        }
        return config;
    }

    /**
     * 注册 Sa-Token 拦截器。
     *
     * <p>{@code index = -100} 为顺序位（低值优先）。</p>
     */
    @Bean(index = -100)
    public SaTokenInterceptor saTokenInterceptor(@Inject AuthProperties authProperties) {
        SaTokenInterceptor saTokenInterceptor = new SaTokenInterceptor();
        authProperties.getInclude().forEach(saTokenInterceptor::addInclude);
        authProperties.getExclude().forEach(saTokenInterceptor::addExclude);
        saTokenInterceptor.addExclude(
                "/favicon.ico",
                // knife4j 文档资源（参考 solon-openapi2-knife4j 文档）
                "/doc.html",
                "/webjars/**",
                "/img/**",
                "/swagger-resources",
                "/swagger/**"
        );

        return saTokenInterceptor
                // 认证函数：每次请求执行，全局登录校验
                .setAuth(r -> SaRouter.match("/api/**", StpUtil::checkLogin));
    }
}
