package com.gracerun.tool.util;

import com.alibaba.fastjson.JSONObject;
import com.gracerun.tool.exception.ServiceException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Bean工具类，继承自org.springframework.beans.BeanUtils
 *
 * @author dujinglin
 */
public class BeanUtil extends BeanUtils {

    @SuppressWarnings("unchecked")
    public static Map<String, String> toMap(Object object) {
        Assert.notNull(object, "object not emtpy");

        if (object instanceof Map) {
            return (Map<String, String>) object;
        }

        Map<String, String> map = new HashMap<String, String>();
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(object.getClass(), Object.class);
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            for (PropertyDescriptor property : propertyDescriptors) {
                String paramName = property.getName();
                Method getter = property.getReadMethod();
                Object value = getter != null ? getter.invoke(object) : null;
                String valueStr = value == null ? null : value.toString();
                if (StringUtils.hasText(valueStr)) {
                    map.put(paramName, valueStr);
                }
            }
            return map;
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    /**
     * 忽略null值复制对象 空字符串会覆盖，null不会覆盖
     *
     * @param src    提供数据的bean
     * @param target 获取数据的bean
     * @param <M>
     * @return 返回复制后的target对象
     */
    public static <M> M copyPropertiesIgnoreNull(M src, M target) {
        BeanUtils.copyProperties(src, target, getNullPropertyNames(src));
        return target;
    }

    /**
     * 重载属性拷贝方法
     *
     * @param src
     * @param target
     * @param ignoreProperties
     * @return
     */
    public static <M> M copyPropertiesIgnoreNullAndFields(M src, M target, String... ignoreProperties) {
        if (!ObjectUtils.isEmpty(ignoreProperties)) {
            return copyPropertiesIgnoreNullAndFields(src, target, Arrays.asList(ignoreProperties));
        } else {
            return copyPropertiesIgnoreNull(src, target);
        }
    }

    /**
     * 忽略null值复制对象 空字符串会覆盖，null不会覆盖 同时忽略申明的字段
     *
     * @param src    提供数据的bean
     * @param target 获取数据的bean
     * @param <M>
     * @return 返回复制后的target对象
     */
    public static <M> M copyPropertiesIgnoreNullAndFields(M src, M target, List<String> ignoreFileds) {
        List<String> ignores = new ArrayList<String>();
        if (!CollectionUtils.isEmpty(ignoreFileds)) {
            ignores.addAll(ignoreFileds);
        }
        ignores.addAll(Arrays.asList(getNullPropertyNames(src)));
        String[] array = ignores.toArray(new String[ignores.size()]);
        BeanUtils.copyProperties(src, target, array);
        return target;
    }

    /**
     * 获取对象中值为空的属性名称，在复制时加以排除
     *
     * @param source
     * @return
     */
    private static String[] getNullPropertyNames(Object source) {
        BeanWrapper src = new BeanWrapperImpl(source);
        java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<>();
        for (java.beans.PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null || !StringUtils.hasText(srcValue.toString())) {
                emptyNames.add(pd.getName());
            }
        }
        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }

    /**
     * 将所有null字符串字段转换为空字符串
     *
     * @param obj
     */
    public static void parseNullToEmptyString(Object obj) {
        for (Field f : obj.getClass().getDeclaredFields()) {
            f.setAccessible(true);
            if (String.class.isAssignableFrom(f.getType())) {
                String str;
                try {
                    str = (String) f.get(obj);
                } catch (IllegalAccessException e) {
                    throw new ServiceException(e.getMessage());
                }
                if (null != str) {
                    continue;
                }
                try {
                    f.set(obj, "");
                } catch (IllegalAccessException e) {
                    throw new ServiceException(e.getMessage());
                }
            }
        }
    }

    /**
     * 校验Bean中所有属性是否为空
     *
     * @param obj
     * @return
     */
    @Deprecated
    public static boolean checkAllFieldsEmpty(Object obj) {
        for (Field f : obj.getClass().getDeclaredFields()) {
            f.setAccessible(true);
            Object str;
            try {
                str = f.get(obj);
            } catch (IllegalAccessException e) {
                throw new ServiceException(e.getMessage());
            }
            if (!StringUtils.isEmpty(str)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 校验Bean中所有属性
     *
     * @param obj   <p>
     *              需要校验的Bean
     *              </p>
     * @param empty <p>
     *              如果empty字段为true，则校验是否全部为空，如果全部为空，返回true
     *              </p>
     *              <p>
     *              如果empty字段为false，则校验是否全部非空，如果全部非空，返回true
     *              </p>
     * @return
     */
    public static boolean checkAllFields(Object obj, boolean empty) {
        for (Field f : obj.getClass().getDeclaredFields()) {
            f.setAccessible(true);
            Object str;
            try {
                str = f.get(obj);
            } catch (IllegalAccessException e) {
                throw new ServiceException(e.getMessage());
            }
            if (empty ^ StringUtils.isEmpty(str)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 将json中的key-value，设置给对象obj。其中key对应属性，value对应值
     *
     * @param obj
     * @param json
     */
    public static void reflectSetField(Object obj, JSONObject json) {
        if (!json.isEmpty() && obj != null) {
            Class<?> clazz = obj.getClass();
            Method[] methods = clazz.getMethods();
            try {
                for (String fieldName : json.keySet()) {
                    if (!StringUtils.hasText(fieldName)) {
                        continue;
                    }
                    //setfilename
                    String methodName = "set" + fieldName;
                    char[] charArr = methodName.toCharArray();
                    //setFileName
                    charArr[3] -= 32;
                    methodName = String.valueOf(charArr);
                    for (Method method : methods) {
                        if (methodName.equals(method.getName())) {
                            Object value = json.get(fieldName);
                            try {
                                if (value != null) {
                                    method.invoke(obj, value);
                                } else {
                                    method.invoke(obj, new Object[]{null});
                                }
                                break;
                            } catch (IllegalArgumentException e) {
                                //参数错误
                            }
                        }
                    }
                }
            } catch (Exception e) {
                throw new ServiceException(e.getMessage());
            }
        }
    }

}
