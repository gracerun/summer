package com.gracerun.tool.util;

import com.gracerun.tool.exception.ServiceException;
import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Base64Utils;
import org.springframework.util.StringUtils;

import javax.crypto.Cipher;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

/**
 * 说明: rsa加解密工具类
 *
 * @author sck
 * @create 2018-04-18 上午9:05
 */
public abstract class RSAUtil {

    protected static final Logger logger = LoggerFactory.getLogger(RSAUtil.class);

    public static final String KEY_ALGORITHM = "RSA";
    public static final String CIPHER_ALGORITHM = "RSA/ECB/PKCS1Padding";
    public static final String PUBLIC_KEY = "publicKey";
    public static final String PRIVATE_KEY = "privateKey";

    /**
     * 签名算法
     */
    public static final String SHA1WithRSA = "SHA1WithRSA";

    public static final String SHA256withRSA = "SHA256withRSA";

    public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

    public static final String RSA = "RSA";

    /**
     * 证书
     */
    public static final String CERT = "CERT";

    /**
     * 密钥
     */
    public static final String KEY = "KEY";
    /**
     * 证书相关
     */
    public static final String CERT_TYPE = "X.509";
    public static final String PROVIDER = "BC";

    /**
     * RSA密钥长度必须是64的倍数，在512~65536之间。默认是1024，单位：位（bit） 代加密铭文字节长度限制：rsa秘钥长度/8 -11；
     */
    public static final int KEY_SIZE = 1024;

    /**
     * RSA签名
     *
     * @param object     待签名数据
     * @param privateKey 私钥
     * @return 签名值
     */
    public static String sign(Object object, String privateKey, String algorithm) {
        Map<String, String> params = BeanUtil.toMap(object);
        String signSource = SignUtil.toSignString(params);
        return sign(signSource, privateKey, algorithm);
    }

    /**
     * RSA签名
     *
     * @param content    待签名数据
     * @param privateKey 私钥
     * @return 签名值
     */
    public static String sign(String content, String privateKey, String algorithm) {
        try {
            PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(Base64Utils.decodeFromString(privateKey));

            KeyFactory keyf = KeyFactory.getInstance(RSA);
            PrivateKey priKey = keyf.generatePrivate(priPKCS8);

            java.security.Signature signature = java.security.Signature.getInstance(algorithm);

            signature.initSign(priKey);
            signature.update(content.getBytes(DEFAULT_CHARSET));

            byte[] signed = signature.sign();
            return Base64Utils.encodeToString(signed);
        } catch (InvalidKeyException e) {
            throw new ServiceException("InvalidKeyException", e);
        } catch (NoSuchAlgorithmException e) {
            throw new ServiceException("NoSuchAlgorithmException", e);
        } catch (InvalidKeySpecException e) {
            throw new ServiceException("InvalidKeySpecException", e);
        } catch (SignatureException e) {
            throw new ServiceException("SignatureException", e);
        }
    }

    /**
     * RSA验签名检查
     *
     * @param bean    待签名数据
     * @param sign    签名值
     * @param pubCert 分配给开发商公钥
     * @param ignores 签名忽略值
     * @return 布尔值
     */
    public static boolean verify(Object bean, String sign, String pubCert, String algorithm, String keyType, String... ignores) {
        Map<String, String> map = BeanUtil.toMap(bean);
        String content = SignUtil.toSignString(map, null, ignores);
        return verify(content, sign, pubCert, algorithm, keyType);
    }

    /**
     * RSA验签名检查
     *
     * @param content 待签名数据
     * @param sign    签名值
     * @param pubCert 分配给开发商公钥
     * @return 布尔值
     */
    public static boolean verify(String content, String sign, String pubCert, String algorithm, String keyType) {
        try {
            PublicKey publicKey = null;
            if (!StringUtils.hasText(keyType) || CERT.equals(keyType)) {
                CertificateFactory cf = CertificateFactory.getInstance(CERT_TYPE, PROVIDER);
                InputStream in = new ByteArrayInputStream(pubCert.getBytes());
                X509Certificate x509Cert = (X509Certificate) cf.generateCertificate(in);
                publicKey = x509Cert.getPublicKey();
            } else {
                KeyFactory keyFactory = KeyFactory.getInstance(RSA);
                byte[] encodedKey = Base64Utils.decodeFromString(pubCert);
                publicKey = keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));
            }

            java.security.Signature signature = java.security.Signature.getInstance(algorithm);
            signature.initVerify(publicKey);
            signature.update(content.getBytes(DEFAULT_CHARSET));

            return signature.verify(Base64Utils.decodeFromString(sign));
        } catch (InvalidKeyException e) {
            throw new ServiceException(e.getClass().getSimpleName(), e);
        } catch (NoSuchAlgorithmException e) {
            throw new ServiceException(e.getClass().getSimpleName(), e);
        } catch (InvalidKeySpecException e) {
            throw new ServiceException(e.getClass().getSimpleName(), e);
        } catch (SignatureException e) {
            throw new ServiceException(e.getClass().getSimpleName(), e);
        } catch (CertificateException e) {
            throw new ServiceException(e.getClass().getSimpleName(), e);
        } catch (NoSuchProviderException e) {
            throw new ServiceException(e.getClass().getSimpleName(), e);
        }
    }

    /**
     * 生成指定长度的密钥对，若长度小于0则使用默认长度
     *
     * @param keyLength 秘钥长度（单位：位）,必须是64的倍数，在512~65536之间
     * @return
     */
    public static Map<String, Key> generateKeys(int keyLength) {
        try {
            if (keyLength < 0) {
                keyLength = KEY_SIZE;
            }
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KEY_ALGORITHM);
            keyPairGenerator.initialize(keyLength);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
            RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();

            Map<String, Key> keyMap = new HashMap<>();
            keyMap.put(PUBLIC_KEY, publicKey);
            keyMap.put(PRIVATE_KEY, privateKey);
            return keyMap;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 秘钥转化为base64字符串
     *
     * @param key
     * @return
     */
    public static String key2Base64(Key key) {
        return Base64.encodeBase64String(key.getEncoded());
    }

    /**
     * 还原公钥，X509EncodedKeySpec 用于构建公钥的规范
     *
     * @param keyBytes
     * @return
     */
    public static PublicKey restorePublicKey(byte[] keyBytes) {
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(keyBytes);
        try {
            KeyFactory factory = KeyFactory.getInstance(KEY_ALGORITHM);
            PublicKey publicKey = factory.generatePublic(x509EncodedKeySpec);
            return publicKey;
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 还原私钥，PKCS8EncodedKeySpec 用于构建私钥的规范
     *
     * @param keyBytes
     * @return
     */
    public static PrivateKey restorePrivateKey(byte[] keyBytes) {
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(keyBytes);
        try {
            KeyFactory factory = KeyFactory.getInstance(KEY_ALGORITHM);
            PrivateKey privateKey = factory.generatePrivate(pkcs8EncodedKeySpec);
            return privateKey;
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 公钥加密
     *
     * @param publicKey
     * @param data
     * @return
     */
    public static byte[] publicEncrypt(PublicKey publicKey, byte[] data) {
        try {
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            return cipher.doFinal(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    /**
     * 公钥解密
     *
     * @param publicKey
     * @param encodeData
     * @return
     */
    public static String publicDecrypt(PublicKey publicKey, byte[] encodeData) {
        try {
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, publicKey);
            return new String(cipher.doFinal(encodeData));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 私钥加密
     *
     * @param privateKey
     * @param data
     * @return
     */
    public static byte[] privateEncrypt(PrivateKey privateKey, byte[] data) {
        try {
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, privateKey);
            return cipher.doFinal(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 私钥解密
     *
     * @param privateKey
     * @param encodeData
     * @return
     */
    public static String privateDecrypt(PrivateKey privateKey, byte[] encodeData) {
        try {
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            return new String(cipher.doFinal(encodeData));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Certificate readCertificate(String path) throws Exception {
        X509Certificate x509Certificate = null;
        CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
        FileInputStream is = new FileInputStream(path);
        x509Certificate = (X509Certificate) certificateFactory.generateCertificate(is);
        is.close();
        System.out.println("读取Cer证书信息...");
        System.out.println("x509Certificate_SerialNumber_序列号___:" + x509Certificate.getSerialNumber().toString(16));
        System.out.println("x509Certificate_getIssuerDN_发布方标识名___:" + x509Certificate.getIssuerDN());
        System.out.println("x509Certificate_getSubjectDN_主体标识___:" + x509Certificate.getSubjectDN());
        System.out.println("x509Certificate_getSigAlgOID_证书算法OID字符串___:" + x509Certificate.getSigAlgOID());
        System.out.println("x509Certificate_getNotBefore_证书有效期___:" + x509Certificate.getNotAfter());
        System.out.println("x509Certificate_getSigAlgName_签名算法___:" + x509Certificate.getSigAlgName());
        System.out.println("x509Certificate_getVersion_版本号___:" + x509Certificate.getVersion());
        System.out.println("x509Certificate_getPublicKey_公钥___:" + Base64Utils.encodeToString(x509Certificate.getPublicKey().getEncoded()));
        return x509Certificate;
    }

    static {
        try {
            Security.addProvider(new BouncyCastleProvider());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
