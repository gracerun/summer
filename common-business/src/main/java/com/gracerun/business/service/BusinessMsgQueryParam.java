package com.gracerun.business.service;

import com.gracerun.repository.Query;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

/**
 * @ClassName BusinessMsgQueryParam
 * @Description
 * @Author minliangqin
 * @Date2023/4/17 09:52
 **/
@Data
public class BusinessMsgQueryParam {

    /**
     * 业务编号
     */
    @Query
    private String businessNo;

    /**
     * 名称空间
     */
    @Query
    private String namespace;

    /**
     * 业务类型
     */
    @Query
    private String businessType;

    /**
     * 业务标签
     */
    @Query
    private String businessTag;

    /**
     * 状态
     */
    @Query
    private String status;

    /**
     * 创建时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Query(type = Query.Type.BETWEEN)
    private List<Date> createTime;

    /**
     * 下次执行时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Query(type = Query.Type.LESS_THAN)
    private Date nextExecuteTime;
}
