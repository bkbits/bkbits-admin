package com.bkbits.admin.config;

import com.bkbits.core.Result;
import com.github.xiaoymin.knife4j.solon.extension.OpenApiExtensionResolver;
import io.swagger.models.Scheme;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;
import org.noear.solon.docs.DocDocket;

@Configuration
public class DocConfig {
    @Inject
    private OpenApiExtensionResolver openApiExtensionResolver;

    @Bean("adminApi")
    public DocDocket adminApi() {
        return new DocDocket()
                .basicAuth(openApiExtensionResolver.getSetting().getBasic())
                .vendorExtensions(openApiExtensionResolver.buildExtensions())
                .groupName("admin后台接口")
                .schemes(Scheme.HTTP.toValue(), Scheme.HTTPS.toValue())
                .globalResult(Result.class)
                .globalResponseInData(true)
                .apis("com.bkbits");
    }
}
