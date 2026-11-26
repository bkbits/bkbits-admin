package com.bkbits.auth.config;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.config.SaTokenConfig;
import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.dao.SaTokenDaoForRedisx;
import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.exception.NotRoleException;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.solon.integration.SaTokenInterceptor;
import cn.dev33.satoken.stp.StpUtil;
import com.bkbits.auth.AuthProperties;
import com.bkbits.core.Result;
import org.noear.solon.Solon;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Condition;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;
import org.noear.solon.core.event.AppLoadEndEvent;
import org.noear.redisx.RedisClient;

import java.util.List;

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
     */
    @Bean
    public SaTokenConfig getSaTokenConfigPrimary(
            @Inject AuthProperties authProperties
    ) {
        return buildSaTokenConfig(authProperties);
    }

    /**
     * 注册 sa-token 会话存储；存在 Redis 客户端时使用 Redis 持久化，
     * 未配置缓存模块时回退到默认内存存储。
     */
    @Bean
    @Condition(onBean = RedisClient.class)
    public SaTokenDao saTokenDao(@Inject RedisClient redisClient) {
        return new SaTokenDaoForRedisx(redisClient);
    }

    /**
     * 注册 Sa-Token 拦截器。
     *
     * <p>{@code index = -100} 为顺序位（低值优先）。</p>
     */
    @Bean(index = -100)
    public SaTokenInterceptor saTokenInterceptor(@Inject AuthProperties authProperties) {
        SaTokenConfig config = buildSaTokenConfig(authProperties);
        // sa-token-solon-plugin 自带默认 SaTokenConfig Bean，与本类配置存在初始化顺序竞争，
        // 应用加载完成后再写入一次，确保 bkbits.auth.* 配置真正生效
        SaManager.setConfig(config);
        Solon.context().onEvent(AppLoadEndEvent.class, e -> SaManager.setConfig(config));

        SaTokenInterceptor saTokenInterceptor = new SaTokenInterceptor();

        // 拦截路由为空时默认拦截全部路径，否则 SaRouter.match 匹配不到任何路径，注解鉴权不会执行
        List<String> includes = authProperties.getInclude();
        if (includes == null || includes.isEmpty()) {
            saTokenInterceptor.addInclude("/**");
        } else {
            includes.forEach(saTokenInterceptor::addInclude);
        }
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
                .setAuth(r -> SaRouter.match("/api/**", StpUtil::checkLogin))
                // 异常处理：统一返回 JSON 结构
                .setError(e -> {
                    if (e instanceof NotLoginException) {
                        return Result.fail(401, "未登录或登录已过期");
                    }
                    if (e instanceof NotPermissionException || e instanceof NotRoleException) {
                        return Result.fail(403, "无访问权限");
                    }
                    return Result.fail(500, e.getMessage());
                });
    }

    /**
     * 按 {@link AuthProperties} 构建 Sa-Token 配置。
     *
     * @param authProperties 认证配置属性
     * @return Sa-Token 配置
     */
    private SaTokenConfig buildSaTokenConfig(AuthProperties authProperties) {
        SaTokenConfig config = new SaTokenConfig();
        // token 名称（同时也是 cookie 名称）
        config.setTokenName(authProperties.getTokenName());
        // 设置是否为持久化cookie
        config.setIsLastingCookie(authProperties.isLastingCookie());
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
        return config;
    }
}
