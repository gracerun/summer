package com.gracerun.message.bean;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Date;

/**
 * 消息体
 *
 * @author Tom
 * @version 1.0.0
 * @date 12/26/21
 */
@Getter
@Setter
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class GraceMessage {

    /**
     * 业务编号
     */
    private String businessNo;

    /**
     * 业务类型
     */
    private String businessType;

    /**
     * 内容
     */
    private String content;

    /**
     * 下次执行时间
     */
    @JSONField(format = "yyyy-MM-dd HH:mm:ss.SSS")
    private Date nextExecuteTime;

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("businessNo", businessNo)
                .append("businessType", businessType)
                .append("content", content)
                .append("nextExecuteTime", nextExecuteTime)
                .toString();
    }
}