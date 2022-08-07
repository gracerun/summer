package com.summer.mq.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.summer.log.util.NotNullStringStyle;
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
public class MessageBody {

    /**
     * 消息是否持久化
     */
    private Boolean persistent;

    /**
     * 消息ID
     */
    private Long id;

    /**
     * 乐观锁
     */
    private Integer optimistic;

    /**
     * 创建时间
     */
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 最后更新时间
     */
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date lastUpdateTime;

    /**
     * 状态
     */
    private String status;

    /**
     * 业务编号
     */
    private String businessNo;

    /**
     * 名称空间
     */
    private String namespace;

    /**
     * 业务类型
     */
    private String businessType;

    /**
     * 执行次数
     */
    private Integer times;

    /**
     * 内容
     */
    private String content;

    /**
     * 备注
     */
    private String remark;

    /**
     * 下次执行时间
     */
    @JSONField(format = "yyyy-MM-dd HH:mm:ss.SSS")
    private Date nextExecuteTime;

    /**
     * 执行耗时
     */
    private Integer time;

    /**
     * ip地址
     */
    private String ip;

    /**
     * 数据创建时的span_id
     */
    private String createSpanId;

    /**
     * 数据最后一次更新时的span_id
     */
    private String lastUpdateSpanId;

    /**
     * 执行超时时间（单位秒）
     */
    private Integer timeout;

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, NotNullStringStyle.getSytle());
    }
}