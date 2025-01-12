package com.gracerun.tool.util;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

/**
 * @author Tom
 * @version 1.0.0
 * @date 2023/11/4
 */
@Slf4j
public class DateQuietlyUtil {


    public static DateTime parse(CharSequence dateStr, String format) {
        if (!StringUtils.hasText(dateStr)) {
            return null;
        }
        try {
            return DateUtil.parse(dateStr, format);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }
}
