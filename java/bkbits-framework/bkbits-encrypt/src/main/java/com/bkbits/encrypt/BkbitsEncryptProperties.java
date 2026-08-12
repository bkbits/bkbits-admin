package com.bkbits.encrypt;

import lombok.Data;
import org.noear.solon.annotation.BindProps;
import org.noear.solon.annotation.Configuration;

/**
 * 加密模块配置属性。
 *
 * <p>配置前缀 {@code bkbits.encrypt}，示例：</p>
 * <pre>
 * bkbits.encrypt:
 *   cost: 6
 *   rsa: true
 *   publicKey: "./public.pem"
 *   privateKey: "./private.pem"
 * </pre>
 */
@Data
@BindProps(prefix = "bkbits.encrypt")
@Configuration
public class BkbitsEncryptProperties {

    /** 密码加密复杂度 */
    private int cost = 6;

    /** 启用rsa */
    private boolean rsa = true;
    /** 公钥 */
    private String publicKey = "./public.pem";
    /** 私钥 */
    private String privateKey = "./private.pem";
}
