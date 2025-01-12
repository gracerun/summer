package com.gracerun.business.service;

/**
 * @ClassName BusinessMsgDto
 * @Description
 * @Author minliangqin
 * @Date2023/4/17 09:51
 **/

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.ToStringSerializer;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * @author minliangqin
 * @date 2023-04-17
 */
@Data
@Builder
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class BusinessMsgDto implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @JSONField(serializeUsing = ToStringSerializer.class)
    @JsonSerialize(using = com.fasterxml.jackson.databind.ser.std.ToStringSerializer.class)
    private Long id;

    /**
     * 乐观锁
     */
    private Integer optimistic;

    /**
     * 状态
     */
    private String status;

    /**
     * 创建时间
     */
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    /**
     * 最新更新时间
     */
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date lastUpdateTime;

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
     * 业务标签
     */
    private String businessTag;

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
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
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
}
