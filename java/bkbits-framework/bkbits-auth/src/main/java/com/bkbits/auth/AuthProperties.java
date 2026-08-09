package com.bkbits.auth;

import lombok.Data;
import org.noear.solon.annotation.BindProps;
import org.noear.solon.annotation.Configuration;

import java.util.Collections;
import java.util.List;

/**
 * 认证配置属性。
 *
 * <p>对应 app.yml 中 {@code bkbits.auth.*} 前缀的配置，如：</p>
 * <pre>
 * bkbits:
 *   auth:
 *     include:
 *       - /**
 *     exclude:
 *       - /favicon.ico
 * </pre>
 */
@BindProps(prefix = "bkbits.auth")
@Configuration
@Data
public class AuthProperties {

    /**
     * token名称
     */
    private String tokenName = "bkbits-token";

    /**
     * token有效期（单位：秒），默认30天，-1代表永不过期
     */
    private Long timeout = 30 * 24 * 60 * 60L;

    /**
     * token最低活跃频率（单位：秒），超过此时间未访问会被冻结，默认30分钟，-1代表不限制
     */
    private Long tti = 30 * 60L;

    /**
     * 是否允许同一账号多地同时登录（true 允许一起登录，false 新登录挤掉旧登录）
     */
    private boolean concurrent = true;

    /**
     * 多人登录同一账号时，是否共用一个 token（true 共用一个，false 每次登录新建）
     */
    private boolean share = false;

    /**
     * token风格
     */
    private String style = "random-32";

    /**
     * 是否输出操作日志
     */
    private boolean log = false;

    /**
     * 拦截路由
     */
    private List<String> include = Collections.emptyList();

    /**
     * 放行路由
     */
    private List<String> exclude = Collections.emptyList();
}
