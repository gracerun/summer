package com.summer.base.bean.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 查询项
 *
 * @author xiaojie.zhang
 * @version V1.0.0
 * @since 2018/9/16
 */
@Slf4j
@Accessors(chain = true)
@Data
@ApiModel
@AllArgsConstructor
@NoArgsConstructor
public class QueryItem implements Serializable {

    /**
     * 比较器
     */
    @ApiModelProperty("比较器")
    private Comparer comparer;
    /**
     * 属性值
     */
    @ApiModelProperty("属性值")
    private Object[] propValue;

    /**
     * 是否返回 - 默认返回
     */
    @ApiModelProperty("是否返回")
    private boolean isReturn = true;

    /**
     * 是否参与排序 - 默认不参与
     */
    @ApiModelProperty("是否参与排序")
    private boolean isOrderBy = false;

    public QueryItem(Comparer comparer, Object[] propValue) {
        this.comparer = comparer;
        this.propValue = propValue;
    }

    /**
     * 校验
     */
    public boolean check() {
        if (Objects.isNull(propValue) || propValue.length == 0 || Objects.isNull(comparer) || !StringUtils.hasText(String.valueOf(propValue[0]))) {
            return false;
        }

        if (this.getComparer() == Comparer.INTERVAL) {
            if (isDateFormat(propValue[0])) {
                propValue[0] = org.apache.commons.lang3.StringUtils.join(String.valueOf(propValue[0]), " 00:00:00");
            }

            if (propValue.length > 1 && isDateFormat(propValue[1])) {
                propValue[1] = org.apache.commons.lang3.StringUtils.join(String.valueOf(propValue[1]), " 23:59:59");
            }
        }
        return true;
    }

    /**
     * 仅日期校验格式，不校验合法性
     *
     * @param object
     * @return
     */
    private static boolean isDateFormat(Object object) {
        String reg = "^\\d{4}(\\-|\\/|\\.)\\d{1,2}\\1\\d{1,2}$";
        Pattern p = Pattern.compile(reg);
        Matcher m = p.matcher(String.valueOf(object));
        return m.matches();
    }

}
