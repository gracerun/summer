package com.summer.generator;

import com.summer.generator.annotation.EnumValidator;
import com.summer.generator.utils.StringUtils;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.LinkedHashMap;

/**
 * Class生成vueJson 编辑
 * 属性必须配置 ApiModelProperty 注解
 *
 * @author xiaojie.zhang
 * @version V1.0.0
 * @since 2020/9/17
 */
public class JsonGenVueEdit {

    public static final String packageName = "com.summer";
    public static final Class[] CLASSES = new Class[]{

    };
    private static final String[] HIDDEN_LIST = new String[]{
    };
    private static final String[] EMPTY_LIST = new String[]{
            "businessImgList",
            "rateList"
    };

    public static void main(String[] args) {
        gen();
    }

    private static void gen() {
        Arrays.asList(CLASSES).forEach(clazz -> {
            JSONObject object = new JSONObject(new LinkedHashMap());
            object.put("data", new JSONObject(new LinkedHashMap()));
            JSONObject config = new JSONObject(new LinkedHashMap());
            config.put("fieldsConfig", new JSONObject(new LinkedHashMap()));
            JSONObject separator = new JSONObject(new LinkedHashMap());
            JSONObject separatorIndex = new JSONObject();
            separatorIndex.put("title", "分割线");
            separator.put("separatorIndex", separatorIndex);
            config.put("separator", separator);
            object.put("config", config);
            object.put("rules", new JSONObject(new LinkedHashMap()));

            gen(clazz.getDeclaredFields(), (JSONObject) object.get("data"), (JSONObject) config.get("fieldsConfig"), (JSONObject) object.get("rules"));
            System.out.println(object.toString());
        });
    }

    private static void gen(Field[] fields, JSONObject data, JSONObject fieldsConfig, JSONObject rules) {
        Arrays.asList(fields).forEach(field -> {
            field.setAccessible(true);
            field.getName();
            if (field.getName().equals("serialVersionUID")) {
                return;
            }
            field.getType().getSimpleName();
            if (Arrays.asList(EMPTY_LIST).contains(field.getName())) {
                data.put(field.getName(), new JSONObject(new LinkedHashMap()));
                return;
            }
            if (field.getType().getName().indexOf(packageName) > -1) {
                data.put(field.getName(), new JSONObject(new LinkedHashMap()));
                fieldsConfig.put(field.getName(), new JSONObject(new LinkedHashMap()));
                gen(getChildrenFields(field), (JSONObject) data.get(field.getName()), (JSONObject) fieldsConfig.get(field.getName()), rules);
                return;
            }
            if (field.getType() == java.util.List.class) {
                if (field.getGenericType() == null) {
                    return;
                }
                Type genericType = field.getGenericType();
                if (field.getGenericType() instanceof ParameterizedType) {
                    ParameterizedType pt = (ParameterizedType) genericType;
                    Class<?> accountPrincipalApproveClazz = (Class<?>) pt.getActualTypeArguments()[0];
                    data.put(field.getName(), new JSONObject(new LinkedHashMap()));
                    fieldsConfig.put(field.getName(), new JSONObject(new LinkedHashMap()));
                    gen(getChildrenFields(accountPrincipalApproveClazz), (JSONObject) data.get(field.getName()), (JSONObject) fieldsConfig.get(field.getName()), rules);
                }
                return;
            }
            ApiModelProperty apiModelProperty = field.getAnnotation(ApiModelProperty.class);
            NotNull notNull = field.getAnnotation(NotNull.class);
            NotBlank notBlank = field.getAnnotation(NotBlank.class);
            EnumValidator enumValidator = field.getAnnotation(EnumValidator.class);

            String parentName = null;
            String msg = enumValidator != null ? "请选择%s" : "请填写%s";
            JSONArray objects = new JSONArray();
            JSONObject rule = new JSONObject();
            rule.put("required", notNull != null || notBlank != null ? true : false);
            rule.put("message", String.format(msg, apiModelProperty == null ? "" : apiModelProperty.value()));
            rule.put("trigger", "blur,change");
            objects.add(rule);
            if (rules.get(field.getName()) != null) {
                parentName = field.getDeclaringClass().getName().replace("Dto", "");
                parentName = parentName.substring(parentName.lastIndexOf(".") + 1);
                parentName = toHump(parentName);
                rules.put(parentName + "_" + field.getName(), objects);
            } else {
                rules.put(field.getName(), objects);
            }

            data.put(parentName == null ? field.getName() : parentName + "_" + field.getName(), "");

            JSONObject item = new JSONObject();
            item.put("cols", 12);
            item.put("type", enumValidator == null ? "MypTextInput" : "MypSelect");
            item.put("name", parentName == null ? field.getName() : parentName + "_" + field.getName());
            item.put("label", apiModelProperty == null ? "" : apiModelProperty.value());
            if (enumValidator != null) {
                genEnum(enumValidator, item);
            }
            fieldsConfig.put(parentName == null ? field.getName() : parentName + "_" + field.getName(), item);
        });
    }

    private static Field[] getChildrenFields(Field field) {
        Class child;
        try {
            child = Class.forName(field.getType().getName()).newInstance().getClass();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return child.getDeclaredFields();
    }

    private static Field[] getChildrenFields(Class clazz) {
        Class child;
        try {
            child = clazz.newInstance().getClass();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return child.getDeclaredFields();
    }

    private static String toHump(String string) {
        char[] chars = string.toCharArray();
        chars[0] += 32;
        return new String(chars);
    }

    private static void genEnum(EnumValidator enumValidator, JSONObject item) {
        JSONArray options = new JSONArray();
        Class[] enumClass = enumValidator.target();
        Arrays.asList(enumClass).forEach(clazz -> {
            if (clazz.isEnum()) {
                Enum[] enums = ((Class<Enum>) enumClass[0]).getEnumConstants();
                Arrays.asList(enums).forEach(o -> {
                    Method method;
                    String desc = "";
                    try {
                        method = o.getClass().getMethod("getDesc");
                        desc = (String) method.invoke(o);
                    } catch (Exception e) {
                        System.out.println(o.getClass().getName() + " 未提供getDesc方法");
                    }
                    if (StringUtils.isBlank(desc)) {
                        try {
                            ApiModelProperty property = o.getClass().getField(o.name()).getAnnotation(ApiModelProperty.class);
                            if (property != null) {
                                desc = property.value();
                            }
                        } catch (NoSuchFieldException e) {
                            System.out.println(o.name() + "未配置ApiModelProperty");
                        }
                    }
                    JSONObject option = new JSONObject();
                    option.put("label", desc);
                    option.put("value", o.name());
                    options.add(option);
                });
            }
        });
        item.put("options", options);
    }
}
