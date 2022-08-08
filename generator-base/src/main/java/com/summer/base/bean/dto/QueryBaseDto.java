package com.summer.base.bean.dto;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.yulichang.query.MPJQueryWrapper;
import com.summer.base.annotation.NotAutoCondition;
import com.summer.base.annotation.NotReturn;
import com.summer.base.util.StringUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 查询基础dto
 *
 * @author xiaojie.zhang
 * @version V1.0.0
 * @since 2018/9/12
 */
@Slf4j
@Data
@ApiModel
public class QueryBaseDto implements Serializable {

    /**
     * 页码
     */
    @ApiModelProperty("页码 默认 1")
    private int pageNum = 1;

    /**
     * 页码数量
     */
    @ApiModelProperty("页码数量 默认 10")
    private int pageSize = 10;

    /**
     * 排序方式
     */
    @ApiModelProperty("排序方式")
    private OrderByEnum orderBy;

    enum OrderByEnum {
        ASC,
        DESC
    }

    /**
     * 转换查询装饰器
     *
     * @return
     */
    public QueryWrapper queryWrapper(boolean filterReturn) {
        QueryWrapper queryWrapper = new QueryWrapper();
        Field[] fields = this.getClass().getDeclaredFields();
        if (fields == null || fields.length == 0) {
            return queryWrapper;
        }

        List<String> returnFields = null;
        if (filterReturn) {
            returnFields = new ArrayList<>(fields.length);
        }

        List<String> orderFields = this.orderBy != null ? new ArrayList<>() : null;
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            field.setAccessible(true);
            try {
                String fieldName = StringUtil.humpToLine(field.getName());
                QueryItem queryItem = (QueryItem) field.get(this);
                if (filterReturn) {
                    NotReturn annotation = field.getAnnotation(NotReturn.class);
                    if (Objects.isNull(annotation)) {
                        if (queryItem == null || queryItem.isReturn()) {
                            returnFields.add(fieldName);
                        }
                    }
                }

                if (queryItem != null && queryItem.isOrderBy() && orderFields != null) {
                    orderFields.add(fieldName);
                }

                NotAutoCondition notCondition = field.getAnnotation(NotAutoCondition.class);
                if (queryItem == null || Objects.nonNull(notCondition)) {
                    continue;
                }
                this.build(StringUtil.humpToLine(field.getName()), queryItem, queryWrapper);
            } catch (IllegalAccessException e) {
                log.warn("非法访问 字段 : {}", e);
                throw new RuntimeException(String.format("非法访问 字段 : %s", e.getMessage()));
            }
        }

        if (orderFields != null && orderFields.size() > 0 && this.orderBy != null)
            queryWrapper.orderBy(true, this.orderBy == OrderByEnum.ASC ? true : false, orderFields.toArray());

        return queryWrapper;
    }

    /**
     * 转换查询装饰器
     *
     * @return
     */
    public QueryWrapper queryWrapper() {
        return queryWrapper(false);
    }

    private void build(String propName, QueryItem queryItem, QueryWrapper queryWrapper) {
        if (queryItem.check() && Objects.nonNull(queryWrapper)) {
            if (queryItem.getPropValue() == null || queryItem.getPropValue().length == 0) {
                return;
            }
            switch (queryItem.getComparer()) {
                case EQU:
                    queryWrapper.eq(propName, queryItem.getPropValue()[0]);
                    break;
                case NEQ:
                    queryWrapper.ne(propName, queryItem.getPropValue()[0]);
                    break;
                case IN:
                    queryWrapper.in(propName, queryItem.getPropValue());
                    break;
                case GEQ:
                    queryWrapper.ge(propName, queryItem.getPropValue()[0]);
                    break;
                case GTR:
                    queryWrapper.gt(propName, queryItem.getPropValue()[0]);
                    break;
                case LEQ:
                    queryWrapper.le(propName, queryItem.getPropValue()[0]);
                    break;
                case LSS:
                    queryWrapper.lt(propName, queryItem.getPropValue()[0]);
                    break;
                case INTERVAL:
                    queryWrapper.between(propName, queryItem.getPropValue()[0], queryItem.getPropValue().length > 0 ? queryItem.getPropValue()[1] : null);
                    if (StringUtils.hasText(String.valueOf(queryItem.getPropValue()[0])) && StringUtils.hasText(String.valueOf(queryItem.getPropValue()[1])))
                        queryWrapper.between(propName, queryItem.getPropValue()[0], queryItem.getPropValue().length > 0 ? queryItem.getPropValue()[1] : null);
                    if (StringUtils.hasText(String.valueOf(queryItem.getPropValue()[0])) && !StringUtils.hasText(String.valueOf(queryItem.getPropValue()[1])))
                        queryWrapper.ge(propName, queryItem.getPropValue()[0]);
                    if (!StringUtils.hasText(String.valueOf(queryItem.getPropValue()[0])) && StringUtils.hasText(String.valueOf(queryItem.getPropValue()[1])))
                        queryWrapper.le(propName, queryItem.getPropValue()[1]);
                    break;
                case NOT_INTERVAL:
                    queryWrapper.notBetween(propName, queryItem.getPropValue()[0], queryItem.getPropValue().length > 0 ? queryItem.getPropValue()[1] : null);
                    if (StringUtils.hasText(String.valueOf(queryItem.getPropValue()[0])) && StringUtils.hasText(String.valueOf(queryItem.getPropValue()[1])))
                        queryWrapper.notBetween(propName, queryItem.getPropValue()[0], queryItem.getPropValue().length > 0 ? queryItem.getPropValue()[1] : null);
                    if (StringUtils.hasText(String.valueOf(queryItem.getPropValue()[0])) && !StringUtils.hasText(String.valueOf(queryItem.getPropValue()[1])))
                        queryWrapper.lt(propName, queryItem.getPropValue()[0]);
                    if (!StringUtils.hasText(String.valueOf(queryItem.getPropValue()[0])) && StringUtils.hasText(String.valueOf(queryItem.getPropValue()[1])))
                        queryWrapper.gt(propName, queryItem.getPropValue()[1]);
                    break;
                case LEFT_LIKE:
                    queryWrapper.likeLeft(propName, queryItem.getPropValue()[0]);
                    break;
                case RIGHT_LIKE:
                    queryWrapper.likeRight(propName, queryItem.getPropValue()[0]);
                    break;
                case LIKE:
                    queryWrapper.like(propName, queryItem.getPropValue()[0]);
                    break;
                case NOT_LIKE:
                    queryWrapper.notLike(propName, queryItem.getPropValue()[0]);
                    break;
            }
        }
    }


    /**
     * 转换关联查询装饰器
     *
     * @return
     */
    public MPJQueryWrapper<T> joinWrapper() {
        return joinWrapper(false, null);
    }

    /**
     * 转换关联查询装饰器
     *
     * @return
     */
    public MPJQueryWrapper<T> joinWrapper(String aliasName) {
        return joinWrapper(false, aliasName);
    }

    /**
     * 构建关联查询装饰器
     *
     * @param filterReturn
     * @return
     */
    public MPJQueryWrapper<T> joinWrapper(boolean filterReturn, String aliasName) {
        MPJQueryWrapper wrapper = new MPJQueryWrapper();
        wrapper.setEntityClass(this.getClass());

        Field[] fields = this.getClass().getDeclaredFields();
        if (fields == null || fields.length == 0) {
            return wrapper;
        }

        List<String> returnFields = null;
        if (filterReturn) {
            returnFields = new ArrayList<>(fields.length);
        }

        List<String> orderFields = this.orderBy != null ? new ArrayList<>() : null;
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            field.setAccessible(true);
            try {
                String fieldName = StringUtil.humpToLine(field.getName());
                QueryItem queryItem = (QueryItem) field.get(this);
                if (filterReturn) {
                    NotReturn annotation = field.getAnnotation(NotReturn.class);
                    if (Objects.isNull(annotation)) {
                        if (queryItem == null || queryItem.isReturn()) {
                            returnFields.add(fieldName);
                        }
                    }
                }

                if (queryItem != null && queryItem.isOrderBy() && orderFields != null) {
                    orderFields.add(fieldName);
                }

                NotAutoCondition notCondition = field.getAnnotation(NotAutoCondition.class);
                if (queryItem == null || Objects.nonNull(notCondition)) {
                    continue;
                }

                this.build(StringUtils.hasText(aliasName) ? aliasName + "." + StringUtil.humpToLine(field.getName()) : StringUtil.humpToLine(field.getName()), queryItem, wrapper);
            } catch (IllegalAccessException e) {
                log.warn("非法访问 字段 : {}", e);
                throw new RuntimeException(String.format("非法访问 字段 : %s", e.getMessage()));
            }
        }

        if (orderFields != null && orderFields.size() > 0 && this.orderBy != null)
            wrapper.orderBy(true, this.orderBy == OrderByEnum.ASC ? true : false, orderFields);

        return wrapper;
    }

    /**
     * 构建关联查询参数
     *
     * @param methodName
     * @param queryItem
     * @param queryWrapper
     */
    private void build(String methodName, QueryItem queryItem, MPJQueryWrapper queryWrapper) {
        if (queryItem.check() && Objects.nonNull(queryWrapper)) {
            if (queryItem.getPropValue() == null || queryItem.getPropValue().length == 0) {
                return;
            }

//            SFunction fun = SFunctionUtil.getFuncMethod(this.getClass(), methodName);
            switch (queryItem.getComparer()) {
                case EQU:
                    queryWrapper.eq(methodName, queryItem.getPropValue()[0]);
                    break;
                case NEQ:
                    queryWrapper.ne(methodName, queryItem.getPropValue()[0]);
                    break;
                case IN:
                    queryWrapper.in(methodName, queryItem.getPropValue());
                    break;
                case GEQ:
                    queryWrapper.ge(methodName, queryItem.getPropValue()[0]);
                    break;
                case GTR:
                    queryWrapper.gt(methodName, queryItem.getPropValue()[0]);
                    break;
                case LEQ:
                    queryWrapper.le(methodName, queryItem.getPropValue()[0]);
                    break;
                case LSS:
                    queryWrapper.lt(methodName, queryItem.getPropValue()[0]);
                    break;
                case INTERVAL:
                    queryWrapper.between(methodName, queryItem.getPropValue()[0], queryItem.getPropValue().length > 0 ? queryItem.getPropValue()[1] : null);
                    if (StringUtils.hasText(String.valueOf(queryItem.getPropValue()[0])) && StringUtils.hasText(String.valueOf(queryItem.getPropValue()[1])))
                        queryWrapper.between(methodName, queryItem.getPropValue()[0], queryItem.getPropValue().length > 0 ? queryItem.getPropValue()[1] : null);
                    if (StringUtils.hasText(String.valueOf(queryItem.getPropValue()[0])) && !StringUtils.hasText(String.valueOf(queryItem.getPropValue()[1])))
                        queryWrapper.ge(methodName, queryItem.getPropValue()[0]);
                    if (!StringUtils.hasText(String.valueOf(queryItem.getPropValue()[0])) && StringUtils.hasText(String.valueOf(queryItem.getPropValue()[1])))
                        queryWrapper.le(methodName, queryItem.getPropValue()[1]);
                    break;
                case NOT_INTERVAL:
                    queryWrapper.notBetween(methodName, queryItem.getPropValue()[0], queryItem.getPropValue().length > 0 ? queryItem.getPropValue()[1] : null);
                    if (StringUtils.hasText(String.valueOf(queryItem.getPropValue()[0])) && StringUtils.hasText(String.valueOf(queryItem.getPropValue()[1])))
                        queryWrapper.notBetween(methodName, queryItem.getPropValue()[0], queryItem.getPropValue().length > 0 ? queryItem.getPropValue()[1] : null);
                    if (StringUtils.hasText(String.valueOf(queryItem.getPropValue()[0])) && !StringUtils.hasText(String.valueOf(queryItem.getPropValue()[1])))
                        queryWrapper.lt(methodName, queryItem.getPropValue()[0]);
                    if (!StringUtils.hasText(String.valueOf(queryItem.getPropValue()[0])) && StringUtils.hasText(String.valueOf(queryItem.getPropValue()[1])))
                        queryWrapper.gt(methodName, queryItem.getPropValue()[1]);
                    break;
                case LEFT_LIKE:
                    queryWrapper.likeLeft(methodName, queryItem.getPropValue()[0]);
                    break;
                case RIGHT_LIKE:
                    queryWrapper.likeRight(methodName, queryItem.getPropValue()[0]);
                    break;
                case LIKE:
                    queryWrapper.like(methodName, queryItem.getPropValue()[0]);
                    break;
                case NOT_LIKE:
                    queryWrapper.notLike(methodName, queryItem.getPropValue()[0]);
                    break;
            }
        }
    }

    public boolean checkNull(QueryItem param) {
        if (ObjectUtils.isEmpty(param) || Objects.isNull(param.getPropValue()) || param.getPropValue().length == 0 || !StringUtils.hasText(String.valueOf(param.getPropValue()[0]))) {
            return true;
        }
        return false;
    }
}

