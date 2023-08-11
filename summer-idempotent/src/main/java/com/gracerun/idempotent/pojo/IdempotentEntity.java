package com.gracerun.idempotent.pojo;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 幂等实体
 *
 * @author Tom
 * @version 1.0.0
 * @date 2023/6/15
 */
@Builder
@Data
public class IdempotentEntity implements Serializable {

    protected Date createTime;

    private String primaryNo;

    private String primaryType;

}
