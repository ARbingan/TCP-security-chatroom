package org.example.chart.config;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.pqc.jcajce.spec.NTRUParameterSpec;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import java.security.*;

/**
 *加密库相关配置类
 */

@Configuration
public class CryptoConfig {

    /**
     * 注册 Bouncy Castle 安全提供者
     */
    @Bean
    public BouncyCastleProvider bouncyCastleProvider() {
        BouncyCastleProvider provider = new BouncyCastleProvider();
        Security.addProvider(provider);
        return provider;
    }

    /**
     * NTRU 密钥对生成器
     */
    @Bean
    public KeyPairGenerator ntruKeyPairGenerator(BouncyCastleProvider provider)
            throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException {
        // 获取 NTRU 算法的密钥对生成器，指定使用 Bouncy Castle 作为提供者（provider）
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("NTRU", "BC");
        // 使用推荐的参数集 ntruHPS2048509 初始化密钥对生成器
        keyPairGenerator.initialize(NTRUParameterSpec.ntruhps2048509);
        // 返回初始化后的密钥对生成器实例
        return keyPairGenerator;
    }

    /**
     * SM4 密钥生成器
     */
    @Bean
    public KeyGenerator sm4KeyGenerator(BouncyCastleProvider provider)
            throws NoSuchAlgorithmException, NoSuchProviderException {
        return KeyGenerator.getInstance("SM4", "BC");
    }

    /**
     * SM4 加密/解密器 (ECB 模式)
     */
    @Bean
    public Cipher sm4Cipher(BouncyCastleProvider provider)
            throws NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException {
        return Cipher.getInstance("SM4/ECB/PKCS5Padding", "BC");
    }

    /**
     * SM4 加密/解密器 (CBC 模式，更安全)
     */
    @Bean(name = "sm4CbcCipher")
    public Cipher sm4CbcCipher(BouncyCastleProvider provider)
            throws NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException {
        return Cipher.getInstance("SM4/CBC/PKCS5Padding", "BC");
    }
}