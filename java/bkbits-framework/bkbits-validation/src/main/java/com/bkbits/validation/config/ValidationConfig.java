package com.bkbits.validation.config;

import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.core.handle.Result;
import org.noear.solon.validation.ValidatorManager;

/**
 * 验证配置。
 */
@Configuration
public class ValidationConfig {

    /**
     * 注册统一验证失败处理器：验证不通过时返回 400 与错误信息。
     */
    @Bean
    public void validatorManager() {
        ValidatorManager.setFailureHandler((ctx, ano, result, message) -> {
            ctx.render(Result.failure(400, message));
            return true;
        });
    }
}
