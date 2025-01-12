package com.gracerun.repository;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * @author damoguyansi
 * @date 2022-01-01
 */
@Slf4j
public class QueryHelpMybatisPlus {

    public static <R, Q> QueryWrapper<R> getPredicate(Q query) {
        QueryWrapper<R> queryWrapper = new QueryWrapper<>();
        if (query == null) {
            return queryWrapper;
        }
        try {
            List<Field> fields = getAllFields(query.getClass(), new ArrayList<>());
            for (Field field : fields) {
                boolean accessible = field.isAccessible();
                field.setAccessible(true);
                Query q = field.getAnnotation(Query.class);
                if (q != null) {
                    String propName = q.propName();
                    String blurry = q.blurry();
                    String attributeName = StrUtil.isBlank(propName) ? field.getName() : propName;
                    attributeName = humpToUnderline(attributeName);
                    Class<?> fieldType = field.getType();
                    Object val = field.get(query);
                    if (ObjectUtil.isNull(val) || "".equals(val)) {
                        continue;
                    }
                    // 模糊多字段
                    if (ObjectUtil.isNotEmpty(blurry)) {
                        String[] blurrys = blurry.split(",");
                        //queryWrapper.or();
                        queryWrapper.and(wrapper -> {
                            for (String blurry1 : blurrys) {
                                String column = humpToUnderline(blurry1);
                                //if(i!=0){
                                wrapper.or();
                                //}
                                fillWrapper(wrapper, q, column, val);
                            }
                        });
                        continue;
                    }
                    fillWrapper(queryWrapper, q, attributeName, val);
                }
                field.setAccessible(accessible);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return queryWrapper;
    }

    private static <R> void fillWrapper(QueryWrapper<R> queryWrapper, Query q, String attributeName, Object val) {
        switch (q.type()) {
            case EQUAL:
                queryWrapper.eq(attributeName, val);
                break;
            case GREATER_THAN:
                queryWrapper.ge(attributeName, val);
                break;
            case LESS_THAN:
                queryWrapper.le(attributeName, val);
                break;
            case LESS_THAN_NQ:
                queryWrapper.lt(attributeName, val);
                break;
            case INNER_LIKE:
                queryWrapper.like(attributeName, val);
                break;
            case LEFT_LIKE:
                queryWrapper.likeLeft(attributeName, val);
                break;
            case RIGHT_LIKE:
                queryWrapper.likeRight(attributeName, val);
                break;
            case IN:
                if (CollUtil.isNotEmpty((Collection<Long>) val)) {
                    queryWrapper.in(attributeName, (Collection<Long>) val);
                }
                break;
            case NOT_IN:
                if (CollUtil.isNotEmpty((Collection<Long>) val)) {
                    queryWrapper.notIn(attributeName, (Collection<Long>) val);
                }
                break;
            case NOT_EQUAL:
                queryWrapper.ne(attributeName, val);
                break;
            case NOT_NULL:
                queryWrapper.isNotNull(attributeName);
                break;
            case IS_NULL:
                queryWrapper.isNull(attributeName);
                break;
            case BETWEEN:
                List<Object> between = new ArrayList<>((List<Object>) val);
                if (CollUtil.isNotEmpty(between)) {
                    queryWrapper.between(attributeName, between.get(0), between.get(1));
                }
                break;
            default:
                break;
        }
    }

    public static List<Field> getAllFields(Class clazz, List<Field> fields) {
        if (clazz != null) {
            fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
            getAllFields(clazz.getSuperclass(), fields);
        }
        return fields;
    }

    /***
     * 驼峰命名转为下划线命名
     *
     * @param para
     *        驼峰命名的字符串
     */
    public static String humpToUnderline(String para) {
        return StrUtil.toUnderlineCase(para);
    }

}
