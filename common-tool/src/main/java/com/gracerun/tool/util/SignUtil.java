package com.gracerun.tool.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.util.Assert;
import org.springframework.util.Base64Utils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.nio.charset.Charset;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.*;
import java.util.Map.Entry;

@Slf4j
public class SignUtil {

    private static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

    /**
     * 签名算法
     */
    public static final String SIGN_ALGORITHMS = "SHA1WithRSA";

    public static final String RSA = "RSA";

    public static String toSignString(Object object) {
        Map<String, String> params = BeanUtil.toMap(object);
        return toSignString(params, null);
    }

    public static String toSignString(Object object, String key, String... ignores) {
        Map<String, String> params = BeanUtil.toMap(object);
        return toSignString(params, key, ignores);
    }

    public static String toSignString(Map<String, ? extends Object> params) {
        return toSignString(params, null);
    }

    public static String toSignString(Map<String, ? extends Object> params, String key, String... ignores) {
        return toSignString(params, key, true, ignores);
    }

    /**
     * 生成固定格式的签名串 按字母顺序升序排列,使用&拼接 key1=value1&key2=value2....
     *
     * @param params
     * @param key
     * @param ignores
     * @return
     */
    @SuppressWarnings("unchecked")
    public static String toSignString(Map<String, ? extends Object> params, String key, boolean ignoresNullValue, String... ignores) {
        Assert.notEmpty(params, "参数不能为空");

        if (!ObjectUtils.isEmpty(ignores)) {
            for (String s : ignores) {
                params.remove(s);
            }
        }

        StringBuilder signSource = new StringBuilder(params.size() * 20);
        Set<Entry<String, Object>> entrySet = ((Map<String, Object>) params).entrySet();
        ArrayList<Entry<String, Object>> paramNames = new ArrayList<Entry<String, Object>>(entrySet);
        Collections.sort(paramNames, Entry.comparingByKey());
        for (Entry<String, Object> entry : paramNames) {
            String paramName = entry.getKey();
            Object paramValue = entry.getValue();

            if (ignoresNullValue && ObjectUtils.isEmpty(paramValue)) {
                continue;
            }

            if (paramValue instanceof CharSequence) {
                // key=value&
                if (StringUtils.hasText((CharSequence) paramValue)) {
                    signSource.append(paramName).append("=").append(paramValue).append("&");
                } else if (!ignoresNullValue) {
                    signSource.append(paramName).append("=").append("&");
                }
            } else if (paramValue.getClass().isArray()) {
                Object[] objs = (Object[]) paramValue;
                for (Object obj : objs) {
                    if (obj instanceof CharSequence) {
                        // key=value&
                        if (StringUtils.hasText((CharSequence) obj)) {
                            signSource.append(paramName).append("=").append(obj).append("&");
                        } else if (!ignoresNullValue) {
                            signSource.append(paramName).append("=").append("&");
                        }
                    }
                }
            }
        }

        // 去除最后一个&
        if (signSource.length() > 0) {
            signSource.deleteCharAt(signSource.length() - 1);
        }

        if (StringUtils.hasText(key)) {
            signSource.append(key);
        }
        String s = signSource.toString();
        log.debug(s);
        return s;
    }

    /**
     * 参数md5签名
     *
     * @param params  待签名map<String,String[]>
     * @param key     签名key
     * @param ignores 要忽略的参数
     * @return 返回MD5签名值
     */
    @Deprecated
    public static String md5SignArray(Map<String, String[]> params, String key, String... ignores) {
        Assert.notEmpty(params, "参数不能为空");
        Assert.hasText(key, "密钥不能为空");

        String s = toSignString(params, key, ignores);
        String sign = DigestUtils.md5Hex(s).toUpperCase();
        log.info("{}", sign);
        return sign;
    }

    /**
     * 参数md5签名
     *
     * @param params  待签名map<String,String>
     * @param key     签名key
     * @param ignores 要忽略的参数
     * @return 返回MD5签名值
     */
    public static String md5Sign(Map<String, String> params, String key, String... ignores) {
        Assert.notEmpty(params, "参数不能为空");
        Assert.hasText(key, "密钥不能为空");

        String s = toSignString(params, key, ignores);
        String sign = DigestUtils.md5Hex(s).toUpperCase();
        log.info("{}", sign);
        return sign;
    }

    public static boolean md5Verify(Object params, String signature, String key) {
        return md5Verify(params, signature, key, null);
    }

    /**
     * MD5验签
     *
     * @param params
     * @param key
     * @return
     */
    public static boolean md5Verify(Object params, String signature, String key, String... ignores) {
        if (StringUtils.isEmpty(key)) {
            return false;
        }
        if (!StringUtils.hasText(signature)) {
            return false;
        }

        String newMd5Str = md5Sign(params, key, ignores);
        if (signature.equalsIgnoreCase(newMd5Str)) {
            return true;
        }
        return false;
    }

    /**
     * 参数md5签名
     *
     * @param object  待签名对象，空属性不参与签名
     * @param key     签名key
     * @param ignores 要忽略的参数
     * @return 返回MD5签名值
     */
    public static String md5Sign(Object object, String key, String... ignores) {
        String s = toSignString(object, key, ignores);
        String sign = DigestUtils.md5Hex(s).toUpperCase();
        log.info("{}", sign);
        return sign;
    }

    /**
     * 参数rsa签名
     *
     * @param params 待签名map<String,String>
     * @param key    签名key
     * @return 返回rsa签名值
     */
    @Deprecated
    public static String rsaSign(Map<String, String> params, String key) {
        Assert.notEmpty(params, "参数不能为空");
        Assert.hasText(key, "私钥不能为空");

        String signSource = toSignString(params);

        try {
            PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(Base64Utils.decodeFromString(key));

            KeyFactory keyf = KeyFactory.getInstance(RSA);
            PrivateKey priKey = keyf.generatePrivate(priPKCS8);

            java.security.Signature signature = java.security.Signature.getInstance(SIGN_ALGORITHMS);

            signature.initSign(priKey);
            signature.update(signSource.getBytes(DEFAULT_CHARSET));
            byte[] signed = signature.sign();
            return Base64Utils.encodeToString(signed);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * 参数sha1签名
     *
     * @param object  待签名map<String,String>
     * @param key     签名key
     * @param ignores 要忽略的参数
     * @return 返回MD5签名值
     */
    public static String sha1SignLowerCase(Object object, String key, String... ignores) {
        Assert.hasText(key, "密钥不能为空");

        String s = toSignString(object, key, ignores);
        return DigestUtils.sha1Hex(s).toLowerCase();
    }

    public static void main(String[] args) {
        Map<String, String[]> params = new HashMap<String, String[]>();
        params.put("ccc", new String[]{"333"});
        params.put("ddd", new String[]{"444"});
        params.put("aaa", new String[]{"111"});
        params.put("bbb", new String[]{"222"});
        params.put("eee", new String[]{"555"});
        String s = SignUtil.toSignString(params);
        System.out.println(s);

        String md5SignArray = SignUtil.md5SignArray(params, "&key=1234567890", "bbb");
        System.out.println(md5SignArray);
        // aaa=111&ccc=333&ddd=444&eee=555&eee=666&key=1234567890
        // 1F802FAFC46ACE48098762825E3C4F78
        // 1F802FAFC46ACE48098762825E3C4F78

        Map<String, String> params2 = new HashMap<String, String>();
        params2.put("ccc", "333");
        params2.put("ddd", "444");
        params2.put("aaa", "111");
        params2.put("bbb", "222");
        params2.put("eee", "555");

        String md5Sign = SignUtil.md5Sign(params2, "&key=1234567890", "bbb");
        System.out.println(md5Sign);
    }

}
