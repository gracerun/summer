package com.gracerun.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.gracerun.repository.SuperEntity;
import lombok.*;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * 业务消息
 *
 * @author minliangqin
 * @date 2023-04-17
 **/
@Data
@Builder
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@TableName("business_msg")
public class BusinessMsg extends SuperEntity<Long> implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 状态
     */
    @NotBlank
    private String status;

    /**
     * 业务编号
     */
    @NotBlank
    private String businessNo;

    /**
     * 名称空间
     */
    @NotBlank
    private String namespace;

    /**
     * 业务类型
     */
    @NotBlank
    private String businessType;

    /**
     * 业务标签
     */
    private String businessTag;

    /**
     * 执行次数
     */
    @NotNull
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
    @NotNull
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

    public void copyFrom(BusinessMsg source) {
        BeanUtil.copyProperties(source, this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
