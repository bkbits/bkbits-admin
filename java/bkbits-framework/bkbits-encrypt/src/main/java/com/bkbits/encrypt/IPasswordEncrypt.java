package com.bkbits.encrypt;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 密码加密接口。
 *
 * <p>提供密码摘要（{@link #hash} / {@link #match}）与传输加密
 * （{@link #encrypt} / {@link #decrypt}）两类能力，由具体实现决定算法。</p>
 */
public interface IPasswordEncrypt {

    /**
     * 对密码进行摘要加密。
     *
     * @param password 密码原文
     * @return 摘要后的密码字符串
     */
    @NotNull String hash(@NotNull String password);

    /**
     * 校验密码与摘要是否匹配。
     *
     * @param password 密码原文
     * @param hash     摘要字符串
     * @return 匹配返回 {@code true}
     */
    boolean match(@NotNull String password, @NotNull String hash);

    /**
     * 加密密码（用于传输）。
     *
     * @param password 密码原文
     * @return 加密后的密码字符串
     */
    @NotNull String encrypt(@NotNull String password);

    /**
     * 解密密码。
     *
     * @param encodedPassword 加密后的密码字符串
     * @return 密码原文
     */
    @NotNull String decrypt(@NotNull String encodedPassword);

    /**
     * 获取公钥（PEM 格式）。
     *
     * @return 公钥字符串；未启用或未加载时返回 {@code null}
     */
    @Nullable String getPublicKey();
}
