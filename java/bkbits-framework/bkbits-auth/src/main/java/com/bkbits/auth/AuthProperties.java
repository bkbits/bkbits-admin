package com.bkbits.auth;

import lombok.Data;
import org.noear.solon.annotation.BindProps;
import org.noear.solon.annotation.Configuration;

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
     * 拦截路由
     */
    private List<String> include = List.of("/**");

    /**
     * 放行路由
     */
    private List<String> exclude = List.of("/favicon.ico");
}
