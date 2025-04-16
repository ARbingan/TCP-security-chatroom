package org.example.chart.Util;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * SM4相关工具类
 */

public class SM4Utils {

    private static final String ALGORITHM = "SM4";
    private static final String PROVIDER = "BC";
    private static final String ECB_TRANSFORMATION = "SM4/ECB/PKCS5Padding";
    private static final String CBC_TRANSFORMATION = "SM4/CBC/PKCS5Padding";

    public static SecretKey generateKey() throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM, PROVIDER);
        keyGenerator.init(128, new SecureRandom());
        return keyGenerator.generateKey();
    }

    public static byte[] encryptECB(byte[] plaintext, SecretKey secretKey) throws Exception {
        Cipher cipher = Cipher.getInstance(ECB_TRANSFORMATION, PROVIDER);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return cipher.doFinal(plaintext);
    }

    public static byte[] decryptECB(byte[] ciphertext, SecretKey secretKey) throws Exception {
        Cipher cipher = Cipher.getInstance(ECB_TRANSFORMATION, PROVIDER);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        return cipher.doFinal(ciphertext);
    }

    public static byte[] encryptCBC(byte[] plaintext, SecretKey secretKey) throws Exception {
        Cipher cipher = Cipher.getInstance(CBC_TRANSFORMATION, PROVIDER);
        byte[] iv = new byte[cipher.getBlockSize()];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(iv);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec);
        byte[] encryptedBytes = cipher.doFinal(plaintext);
        byte[] result = new byte[iv.length + encryptedBytes.length];
        System.arraycopy(iv, 0, result, 0, iv.length);
        System.arraycopy(encryptedBytes, 0, result, iv.length, encryptedBytes.length);
        return result;
    }

    public static byte[] decryptCBC(byte[] ciphertext, SecretKey secretKey) throws Exception {
        Cipher cipher = Cipher.getInstance(CBC_TRANSFORMATION, PROVIDER);
        int blockSize = cipher.getBlockSize();
        byte[] iv = new byte[blockSize];
        System.arraycopy(ciphertext, 0, iv, 0, blockSize);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
        byte[] encryptedBytes = new byte[ciphertext.length - blockSize];
        System.arraycopy(ciphertext, blockSize, encryptedBytes, 0, encryptedBytes.length);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);
        return cipher.doFinal(encryptedBytes);
    }

    public static String keyToBase64(SecretKey secretKey) {
        return Base64.getEncoder().encodeToString(secretKey.getEncoded());
    }

    public static SecretKey base64ToKey(String base64Key) {
        byte[] keyBytes = Base64.getDecoder().decode(base64Key);
        return new SecretKeySpec(keyBytes, ALGORITHM);
    }
}