package com.gracerun.tool.util;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import java.security.Key;
import java.security.SecureRandom;

public class DESUtil {

    /**
     * 3DES加密
     *
     * @param data 数据
     * @param key  密钥
     * @return
     */
    public static String encode(String data, String key) {
        try {
            DESedeKeySpec dks = new DESedeKeySpec(key.getBytes());
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DESede");
            Key secretKey = keyFactory.generateSecret(dks);
            Cipher cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, new SecureRandom());
            return Base64.encodeBase64String(cipher.doFinal(data.getBytes("utf-8")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 3DES解密
     *
     * @param key  密钥
     * @param data 密文
     * @return
     */
    public static String decode(String data, String key) {
        try {
            Cipher cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");
            DESedeKeySpec dks = new DESedeKeySpec(key.getBytes());
            SecretKey sk = SecretKeyFactory.getInstance("DESede").generateSecret(dks);
            cipher.init(Cipher.DECRYPT_MODE, sk);
            byte[] result = cipher.doFinal(Base64.decodeBase64(data));
            return new String(result, "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] hexStringToByteArray(String text) {
        if (text == null) {
            return null;
        }
        byte[] result = new byte[text.length() / 2];
        for (int i = 0; i < result.length; ++i) {
            int x = Integer.parseInt(text.substring(i * 2, i * 2 + 2), 16);
            result[i] = x <= 127 ? (byte) x : (byte) (x - 256);
        }
        return result;
    }

    public static String byteArrayToHexString(byte data[]) {
        String result = "";
        for (int i = 0; i < data.length; i++) {
            int v = data[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                result += "0";
            }
            result += hv;
        }
        return result;
    }
}
