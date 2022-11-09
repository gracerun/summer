package com.gracerun.summermq.bean;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.Set;

/**
 * 消息查询条件
 *
 * @author Tom
 * @version 1.0.0
 * @date 12/26/21
 */
@Accessors(chain = true)
@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = false)
public class MessageQuery {

    @Getter
    @Setter
    private Set<String> namespaces;

    @Getter
    @Setter
    private Set<String> topicNames;

    /**
     * 状态
     */
    private String status;

    /**
     * 下次执行时间
     */
    private Date nextExecuteTime;

    /**
     * 查询条数
     */
    private int pageSize;

    /**
     * 第几页
     */
    private int pageNum;

}