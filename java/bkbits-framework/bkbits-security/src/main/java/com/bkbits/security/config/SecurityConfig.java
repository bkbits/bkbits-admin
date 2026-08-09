package com.bkbits.security.config;

import org.noear.solon.Solon;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Init;
import org.noear.solon.security.web.SecurityFilter;
import org.noear.solon.security.web.header.*;

/**
 * 安全配置。
 */
@Configuration
public class SecurityConfig {

    /**
     * 注册全局安全过滤，统一添加常见安全响应头。
     */
    @Init
    public void init() {
        Solon.app().filter(new SecurityFilter(
                new XContentTypeOptionsHeaderHandler(),
                new XXssProtectionHeaderHandler(),
                new CacheControlHeadersHandler()
        ));
    }
}
