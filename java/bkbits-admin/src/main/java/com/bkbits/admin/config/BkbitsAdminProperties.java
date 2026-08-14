package com.bkbits.admin.config;

import lombok.Data;
import org.noear.solon.annotation.BindProps;
import org.noear.solon.annotation.Configuration;

@Data
@BindProps(prefix = "bkbits.admin")
@Configuration
public class BkbitsAdminProperties {
    /** 使用电话号码登录 */
    private boolean loginByPhone = false;

    /** 使用email登录 */
    private boolean loginByEmail = false;
}
