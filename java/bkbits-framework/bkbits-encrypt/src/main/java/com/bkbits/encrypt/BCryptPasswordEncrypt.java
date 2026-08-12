package com.bkbits.encrypt;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.bkbits.util.FileUtil;
import com.bkbits.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;
import org.noear.solon.core.exception.StatusException;

import javax.crypto.Cipher;
import java.io.File;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * 基于 BCrypt 与 RSA 的密码加密实现。
 *
 * <p>BCrypt 负责密码摘要（{@link #hash} / {@link #match}）；RSA 负责密码传输加密
 * （{@link #encrypt} / {@link #decrypt}）。启用 RSA 时按配置路径加载密钥对，
 * 文件缺失则自动生成并保存（PEM 格式）。</p>
 */
@Slf4j
@Component
public class BCryptPasswordEncrypt implements IPasswordEncrypt {
    private static final String X509_PUBLIC_PEM_HEADER = "-----BEGIN PUBLIC KEY-----";
    private static final String X509_PUBLIC_PEM_FOOTER = "-----END PUBLIC KEY-----";
    private static final String X509_PRIVATE_PEM_HEADER = "-----BEGIN PRIVATE KEY-----";
    private static final String X509_PRIVATE_PEM_FOOTER = "-----END PRIVATE KEY-----";

    /**
     * 加密配置属性
     */
    private final BkbitsEncryptProperties bkbitsEncryptProperties;
    /**
     * RSA 公钥
     */
    private PublicKey publicKey;
    /**
     * RSA 私钥
     */
    private PrivateKey privateKey;
    /**
     * 预生成的公钥 PEM 字符串
     */
    private String publicKeyStr;

    /**
     * 构造加密实现；启用 RSA 时加载或生成密钥对。
     *
     * @param properties 加密配置属性
     */
    public BCryptPasswordEncrypt(@Inject BkbitsEncryptProperties properties) {
        this.bkbitsEncryptProperties = properties;
        if (properties.isRsa()) {
            KeyPair keyPair = loadOrSaveKeyPair();
            this.publicKey = keyPair.getPublic();
            this.privateKey = keyPair.getPrivate();
            this.publicKeyStr = toPEM(keyPair.getPublic());
        }
    }

    /**
     * 对密码进行 BCrypt 摘要加密。
     *
     * @param password 密码原文
     * @return 摘要后的密码字符串
     */
    @Override
    public @NonNull String hash(@NonNull String password) {
        return BCrypt.withDefaults()
                .hashToString(
                        bkbitsEncryptProperties.getCost(),
                        password.toCharArray()
                );
    }

    /**
     * 校验密码与 BCrypt 摘要是否匹配。
     *
     * @param password 密码原文
     * @param hash     BCrypt 摘要字符串
     * @return 匹配返回 {@code true}
     */
    @Override
    public boolean match(@NonNull String password, @NonNull String hash) {
        return BCrypt.verifyer().verify(
                password.toCharArray(),
                hash
        ).verified;
    }

    /**
     * 使用 RSA 公钥加密密码并 Base64 编码；未启用 RSA 时原样返回。
     *
     * @param password 密码原文
     * @return 加密后的密码字符串
     */
    @Override
    public @NotNull String encrypt(@NonNull String password) {
        if (!bkbitsEncryptProperties.isRsa()) {
            return password;
        }
        if (publicKey == null) {
            throw new IllegalStateException("RSA公钥未加载");
        }
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            return StringUtil.base64Encode(cipher.doFinal(password.getBytes()));
        } catch (Exception e) {
            throw new IllegalStateException("加密密码失败", e);
        }
    }

    /**
     * 使用 RSA 私钥解密密码；未启用 RSA 时原样返回。
     *
     * @param encodedPassword 加密后的密码字符串
     * @return 密码原文
     */
    @Override
    public @NotNull String decrypt(@NonNull String encodedPassword) {
        if (!bkbitsEncryptProperties.isRsa()) {
            return encodedPassword;
        }
        if (privateKey == null) {
            throw new IllegalStateException("RSA私钥未加载");
        }
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            return new String(cipher.doFinal(StringUtil.base64Decode(encodedPassword)));
        } catch (Exception e) {
            throw new StatusException("密码格式错误，解密失败", 400);
        }
    }

    /**
     * 获取公钥（PEM 格式）。
     *
     * @return 公钥字符串；未启用 RSA 或公钥未加载时返回 {@code null}
     */
    @Override
    public @Nullable String getPublicKey() {
        return publicKeyStr;
    }

    /**
     * 生成 RSA 密钥对（2048 位）。
     *
     * @return RSA 密钥对
     */
    public static KeyPair genRsaKey() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            SecureRandom secureRandom = new SecureRandom();
            secureRandom.setSeed(System.currentTimeMillis());
            keyPairGenerator.initialize(2048, secureRandom);
            return keyPairGenerator.genKeyPair();
        } catch (Exception e) {
            throw new IllegalStateException("生成RSA密钥对失败", e);
        }
    }

    /**
     * 将公钥转换为 PEM 格式字符串。
     *
     * @param publicKey 公钥
     * @return PEM 格式字符串
     */
    public static String toPEM(PublicKey publicKey) {
        return StringUtil.toPEM(publicKey.getEncoded(), X509_PUBLIC_PEM_HEADER, X509_PUBLIC_PEM_FOOTER);
    }

    /**
     * 将私钥转换为 PEM 格式字符串。
     *
     * @param privateKey 私钥
     * @return PEM 格式字符串
     */
    public static String toPEM(PrivateKey privateKey) {
        return StringUtil.toPEM(privateKey.getEncoded(), X509_PRIVATE_PEM_HEADER, X509_PRIVATE_PEM_FOOTER);
    }

    /**
     * 保存公钥到文件（PEM 格式）。
     *
     * @param publicKey 公钥
     * @param path      文件路径
     * @return 保存成功返回 {@code true}
     */
    public static boolean saveToFile(PublicKey publicKey, String path) {
        return saveToFile(publicKey.getEncoded(), path, true);
    }

    /**
     * 保存私钥到文件（PEM 格式）。
     *
     * @param privateKey 私钥
     * @param path       文件路径
     * @return 保存成功返回 {@code true}
     */
    public static boolean saveToFile(PrivateKey privateKey, String path) {
        return saveToFile(privateKey.getEncoded(), path, false);
    }

    /**
     * 从文件加载公钥。
     *
     * @param path 文件路径
     * @return 公钥；加载失败返回 {@code null}
     */
    public static PublicKey loadPublicKey(String path) {
        try {
            byte[] bytes = StringUtil.parsePEM(
                    FileUtil.readUTF8(path),
                    X509_PUBLIC_PEM_HEADER,
                    X509_PUBLIC_PEM_FOOTER);
            return KeyFactory.getInstance("RSA")
                    .generatePublic(new X509EncodedKeySpec(bytes));
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 从文件加载私钥。
     *
     * @param path 文件路径
     * @return 私钥；加载失败返回 {@code null}
     */
    public static PrivateKey loadPrivateKey(String path) {
        try {
            byte[] bytes = StringUtil.parsePEM(
                    FileUtil.readUTF8(path),
                    X509_PRIVATE_PEM_HEADER,
                    X509_PRIVATE_PEM_FOOTER);
            return KeyFactory.getInstance("RSA")
                    .generatePrivate(new PKCS8EncodedKeySpec(bytes));
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 加载或生成 RSA 密钥对（按配置路径；文件缺失时生成并保存）。
     *
     * @return RSA 密钥对
     */
    public KeyPair loadOrSaveKeyPair() {
        String publicKeyPath = bkbitsEncryptProperties.getPublicKey();
        String privateKeyPath = bkbitsEncryptProperties.getPrivateKey();
        if (!new File(publicKeyPath).exists() || !new File(privateKeyPath).exists()) {
            return generatePairToFile();
        }
        PublicKey publicKey = loadPublicKey(publicKeyPath);
        PrivateKey privateKey = loadPrivateKey(privateKeyPath);
        if (publicKey == null || privateKey == null) {
            log.warn("加载RSA密钥对失败: {}, {} 将重新生成公密钥", publicKeyPath, privateKeyPath);
            return generatePairToFile();
        }
        return new KeyPair(publicKey, privateKey);
    }

    /**
     * 生成 RSA 密钥对并保存到配置路径（PEM 格式）。
     *
     * @return 生成的 RSA 密钥对
     */
    private KeyPair generatePairToFile() {
        KeyPair keyPair = genRsaKey();
        if (!saveToFile(keyPair.getPublic(), bkbitsEncryptProperties.getPublicKey())) {
            log.error("保存公钥文件失败: {}", bkbitsEncryptProperties.getPublicKey());
        }
        if (!saveToFile(keyPair.getPrivate(), bkbitsEncryptProperties.getPrivateKey())) {
            log.error("保存私钥文件失败: {}", bkbitsEncryptProperties.getPrivateKey());
        }
        return keyPair;
    }

    /**
     * 保存密钥字节到文件（内部实现）。
     *
     * @param bytes    密钥字节
     * @param path     文件路径
     * @param isPublic 是否为公钥
     * @return 保存成功返回 {@code true}
     */
    private static boolean saveToFile(byte[] bytes, String path, boolean isPublic) {
        try {
            String pem = isPublic
                    ? StringUtil.toPEM(bytes, X509_PUBLIC_PEM_HEADER, X509_PUBLIC_PEM_FOOTER)
                    : StringUtil.toPEM(bytes, X509_PRIVATE_PEM_HEADER, X509_PRIVATE_PEM_FOOTER);
            FileUtil.writeUTF8(path, pem);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
