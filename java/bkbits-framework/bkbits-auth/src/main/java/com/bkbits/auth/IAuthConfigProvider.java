package com.bkbits.auth;

/**
 * Sa-Token 配置提供者接口。
 *
 * <p>业务模块可实现并注册该接口为 Bean，用于覆盖默认的 Sa-Token 核心配置；
 * 未注册时使用 {@link com.bkbits.auth.config.AuthConfig} 中的默认值。</p>
 */
public interface IAuthConfigProvider {

    /**
     * token 有效期（单位：秒），-1 代表永不过期。
     *
     * @return token 有效期秒数
     */
    int getTimeout();

    /**
     * token 最低活跃频率（单位：秒），token 超过此时间未访问系统会被冻结，-1 代表不限制。
     *
     * @return 最低活跃频率秒数
     */
    int getTTI();

    /**
     * 是否允许同一账号多地同时登录（true 允许一起登录，false 新登录挤掉旧登录）。
     *
     * @return 是否允许并发登录
     */
    boolean isConcurrent();

    /**
     * 多人登录同一账号时，是否共用一个 token（true 共用一个，false 每次登录新建）。
     *
     * @return 是否共享 token
     */
    boolean isShare();

    /**
     * 是否输出操作日志。
     *
     * @return 是否输出日志
     */
    boolean isLog();
}
