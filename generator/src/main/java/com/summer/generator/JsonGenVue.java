package com.summer.generator;


import com.summer.generator.annotation.Translator;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiModelProperty;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.LinkedHashMap;

/**
 * Class生成vueJson
 * 属性必须配置 ApiModelProperty 注解
 *
 * @author xiaojie.zhang
 * @version V1.0.0
 * @since 2020/8/26
 */
public class JsonGenVue {

    public static final Class[] CLASSES = new Class[]{
//            AgentInfoVo.class

    };
    private static final String[] HIDDEN_LIST = new String[]{
//            "settleCard.cnapsCode",
//            "settleCard.bankCode",
//            "customer.agentNo",
//            "customer.comCustomerNo",
//            "customer.status"
    };

    public static void main(String[] args) {
        gen();
    }

    private static void gen() {
        Arrays.asList(CLASSES).forEach(clazz -> {
            Field[] fs = clazz.getDeclaredFields();
            JSONObject objects = new JSONObject(new LinkedHashMap());
            for (Field f : fs) {
                //设置私有属性的访问权限
                f.setAccessible(true);
                f.getName();//获取属性名
                if (f.getName().equals("serialVersionUID")) {
                    continue;
                }
                f.getType().getSimpleName();//获取数据类型的简单名称
                ApiModelProperty apiModelProperty = f.getAnnotation(ApiModelProperty.class);
                if (apiModelProperty == null) {
                    System.out.println(f.getName() + "未配置 ApiModelProperty");
                    continue;
                }
                if (f.getType().getName().indexOf("com.summer") > -1 && !f.getType().isEnum()) {
                    Class child = null;
                    try {
                        child = Class.forName(f.getType().getName()).newInstance().getClass();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Field[] childFields = child.getDeclaredFields();
                    JSONObject childObjects = new JSONObject(new LinkedHashMap());
                    build(childFields, f, childObjects);
                    objects.put(f.getName(), childObjects);
                } else if (f.getType() == java.util.List.class) {
                    if (f.getGenericType() == null) {
                        continue;
                    }
                    if (f.getGenericType() instanceof ParameterizedType) {
                        ParameterizedType pt = (ParameterizedType) f.getGenericType();
                        Class<?> accountPrincipalApproveClazz = (Class<?>) pt.getActualTypeArguments()[0];
                        Class child = null;
                        try {
                            child = accountPrincipalApproveClazz.newInstance().getClass();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Field[] childFields = child.getDeclaredFields();
                        JSONObject childObjects = new JSONObject(new LinkedHashMap());
                        build(childFields, f, childObjects);
                        objects.put(f.getName(), childObjects);
                    }
                } else {
                    Translator translator = f.getAnnotation(Translator.class);
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("cols", 24);
                    jsonObject.put("name", f.getName());
                    jsonObject.put("label", apiModelProperty == null ? "" : apiModelProperty.value());
                    if (Arrays.asList(HIDDEN_LIST).contains(f.getName())) {
                        jsonObject.put("hidden", true);
                    }
                    if (translator != null && Arrays.asList(LABEL_LIST).contains(translator.type().name())) {
                        jsonObject.put("hidden", true);

                        JSONObject jsonObjectLabel = new JSONObject();
                        jsonObjectLabel.put("cols", 24);
                        jsonObjectLabel.put("name", f.getName() + "Label");
                        jsonObjectLabel.put("label", apiModelProperty == null ? "" : apiModelProperty.value());
                        objects.put(f.getName() + "Label", jsonObjectLabel);
                    }
                    objects.put(f.getName(), jsonObject);
                }
            }
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("fieldsConfig", objects);
            System.out.println(jsonObject.toJSONString());
        });
    }

    private static void build(Field[] fields, Field parentField, JSONObject objects) {
        for (Field field : fields) {
            field.setAccessible(true);
            ApiModelProperty property = field.getAnnotation(ApiModelProperty.class);
            Translator translator = field.getAnnotation(Translator.class);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("cols", 24);
            jsonObject.put("name", field.getName());
            jsonObject.put("label", property == null ? "" : property.value());
            if (Arrays.asList(HIDDEN_LIST).contains(parentField.getName() + "." + field.getName())) {
                jsonObject.put("hidden", true);
            }
            if (translator != null && Arrays.asList(LABEL_LIST).contains(translator.type().name())) {
                jsonObject.put("hidden", true);
                JSONObject jsonObjectLabel = new JSONObject();
                jsonObjectLabel.put("cols", 24);
                jsonObjectLabel.put("name", field.getName() + "Label");
                jsonObjectLabel.put("label", property == null ? "" : property.value());
                objects.put(field.getName() + "Label", jsonObjectLabel);
            }
            objects.put(field.getName(), jsonObject);
        }
    }

    private static final String[] LABEL_LIST = new String[]{
            "DICTIONARY",
            "AREA",
            "MCC"
    };

}
