package com.gracerun.log.interceptor;

import com.gracerun.log.constant.Level;
import com.gracerun.log.serializer.LogSerializer;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Getter
@Setter
public class LoggingAttribute {

    /**
     * 日志名称
     */
    private String name;

    /**
     * 方法完整名称
     */
    private String methodIdentification;

    /**
     * 日志接口
     */
    private Logger targetLog;

    /**
     * 序列化参数
     */
    private LogSerializer serializeArgsUsing;

    /**
     * 序列化返回值
     */
    private LogSerializer serializeReturnUsing;

    /**
     * 日志级别
     */
    private Level level;

    /**
     * 单行日志最大长度
     * -1:不限制长度
     */
    int maxLength;

    /**
     * 异常日志属性
     */
    private List<ThrowableLogAttribute> throwableLogAttributes;

    public int getThrowableLogPrintMaxRow(Throwable e) {
        if (!CollectionUtils.isEmpty(throwableLogAttributes)) {
            for (ThrowableLogAttribute attr : throwableLogAttributes) {
                if (attr.throwable.isInstance(e)) {
                    return attr.maxRow;
                }
            }
            return -1;
        } else {
            return -1;
        }
    }

}