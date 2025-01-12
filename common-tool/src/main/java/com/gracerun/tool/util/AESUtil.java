package com.gracerun.tool.util;

import cn.hutool.core.util.HexUtil;
import com.alibaba.fastjson.JSONObject;
import com.gracerun.tool.exception.ServiceException;
import org.springframework.util.Base64Utils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.Charset;
import java.security.SecureRandom;

public class AESUtil {

    private static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

    private static final String TRANSFORMATION = "AES/ECB/PKCS5Padding";

    private static final String ALGORITHM = "AES";

    public static String encrypt(String encryptKey, String content) {
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(encryptKey.getBytes(), ALGORITHM);
            Cipher ecipher = Cipher.getInstance(TRANSFORMATION);
            ecipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            return Base64Utils.encodeToString(ecipher.doFinal(content.getBytes(DEFAULT_CHARSET)));
        } catch (Exception e) {
            throw new ServiceException("加密失败", e);
        }
    }

    public static String encrypt128(String password, String content) {
        try {
            KeyGenerator kgen = KeyGenerator.getInstance(ALGORITHM);
            kgen.init(128, new SecureRandom(password.getBytes(DEFAULT_CHARSET)));
            SecretKey secretKey = kgen.generateKey();
            byte[] enCodeFormat = secretKey.getEncoded();
            SecretKeySpec key = new SecretKeySpec(enCodeFormat, ALGORITHM);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);// 创建密码器
            byte[] byteContent = content.getBytes(DEFAULT_CHARSET);
            cipher.init(Cipher.ENCRYPT_MODE, key);// 初始化
            byte[] result = cipher.doFinal(byteContent);
            String hexString = HexUtil.encodeHexStr(result);
            return hexString; // 加密
        } catch (Exception e) {
            throw new ServiceException("加密失败", e);
        }
    }

    public static String decrypt128(String password, String content) {
        try {
            KeyGenerator kgen = KeyGenerator.getInstance(ALGORITHM);
            kgen.init(128, new SecureRandom(password.getBytes()));
            SecretKey secretKey = kgen.generateKey();
            byte[] enCodeFormat = secretKey.getEncoded();
            SecretKeySpec key = new SecretKeySpec(enCodeFormat, ALGORITHM);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);// 创建密码器
            cipher.init(Cipher.DECRYPT_MODE, key);// 初始化
            byte[] result = cipher.doFinal(HexUtil.decodeHex(content));
            return new String(result, DEFAULT_CHARSET);
        } catch (Exception e) {
            throw new ServiceException("解密失败", e);
        }
    }

    public static String decrypt(String encryptKey, String content) {
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(encryptKey.getBytes(), ALGORITHM);
            Cipher dcipher = Cipher.getInstance(TRANSFORMATION);
            dcipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
            return new String(dcipher.doFinal(Base64Utils.decodeFromString(content)), DEFAULT_CHARSET);
        } catch (Exception e) {
            throw new ServiceException("解密失败", e);
        }
    }

    /**
     * 加密并转为16进制
     *
     * @param object
     * @param key
     * @return
     */
    public static String encryptByte2hex(Object object, String key) {
        try {
            String request = JSONObject.toJSONString(object);
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("ASCII"), ALGORITHM);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(1, skeySpec);
            byte[] encrypted = cipher.doFinal(request.getBytes(DEFAULT_CHARSET));
            return HexUtil.encodeHexStr(encrypted);
        } catch (Exception e) {
            throw new ServiceException("加密失败", e);
        }
    }

    public static String encryptByte2hex(String content, String key) {
        try {
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("ASCII"), ALGORITHM);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(1, skeySpec);
            byte[] encrypted = cipher.doFinal(content.getBytes(DEFAULT_CHARSET));
            return HexUtil.encodeHexStr(encrypted);
        } catch (Exception e) {
            throw new ServiceException("加密失败", e);
        }
    }

    /**
     * 解密并将16进制转为字符串
     *
     * @param str
     * @param key
     * @return
     */
    public static String decryptHex2byte(String str, String key) {
        try {
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("ASCII"), ALGORITHM);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(2, skeySpec);
            byte[] encrypted1 = HexUtil.decodeHex(str);
            byte[] original = cipher.doFinal(encrypted1);
            return new String(original, "UTF-8");
        } catch (Exception e) {
            throw new ServiceException("解密失败", e);
        }
    }

    public static void main(String[] args) throws Exception {
        String message = "abcdefg";
        String password = "1e13df40cdf2496bbcdc5b63b4887e44";

        String encrypt = AESUtil.encrypt(password, message);
        System.out.println("加密结果:" + encrypt);
        String decrypt = AESUtil.decrypt(password, encrypt);
        System.out.println("解密结果:" + decrypt);
    }
}
