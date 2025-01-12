package com.gracerun.tool.exception;

import com.gracerun.tool.bean.ResponseBean;
import lombok.Data;

/**
 * 数据库并发更新异常
 *
 * @author Tom
 * @version 1.0.0
 * @date 2023/6/8
 */
@Data
public class ConcurrencyException extends RuntimeException {

    private String code;

    public ConcurrencyException() {
        super();
    }

    public ConcurrencyException(String message) {
        super(message);
        this.code = ResponseBean.DB_ERR;
    }


}
