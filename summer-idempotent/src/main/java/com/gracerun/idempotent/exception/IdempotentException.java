package com.gracerun.idempotent.exception;

import com.gracerun.tool.bean.ResponseBean;
import lombok.Data;

/**
 * 数据重复异常
 *
 * @author Tom
 * @version 1.0.0
 * @date 2023/6/16
 */
@Data
public class IdempotentException extends RuntimeException {

    private String code;

    public IdempotentException() {
        super();
        this.code = ResponseBean.DB_ERR;
    }

    public IdempotentException(String message) {
        super(message);
        this.code = ResponseBean.DB_ERR;
    }


}
