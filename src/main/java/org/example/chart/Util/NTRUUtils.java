package org.example.chart.Util;


import org.bouncycastle.pqc.jcajce.spec.NTRUParameterSpec;

import javax.crypto.Cipher;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

/**
 * ntru相关工具类
 */
public class NTRUUtils {

    private static final String ALGORITHM = "NTRU";
    private static final String PROVIDER = "BC";

    public static KeyPair generateKeyPair() throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM, PROVIDER);
        keyPairGenerator.initialize(NTRUParameterSpec.ntruhps2048509);
        return keyPairGenerator.generateKeyPair();
    }

    public static byte[] encrypt(byte[] plaintext, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM, PROVIDER);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(plaintext);
    }

    public static byte[] decrypt(byte[] ciphertext, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM, PROVIDER);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(ciphertext);
    }

    public static String keyToBase64(Key key) {
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    public static PublicKey base64ToPublicKey(String base64Key) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException {
        byte[] keyBytes = Base64.getDecoder().decode(base64Key);
        java.security.spec.X509EncodedKeySpec keySpec = new java.security.spec.X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM, PROVIDER);
        return keyFactory.generatePublic(keySpec);
    }

    public static PrivateKey base64ToPrivateKey(String base64Key) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException {
        byte[] keyBytes = Base64.getDecoder().decode(base64Key);
        java.security.spec.PKCS8EncodedKeySpec keySpec = new java.security.spec.PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM, PROVIDER);
        return keyFactory.generatePrivate(keySpec);
    }
}