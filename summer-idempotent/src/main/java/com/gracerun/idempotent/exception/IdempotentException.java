package com.gracerun.idempotent.exception;

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

    public IdempotentException() {
        super();
    }

    public IdempotentException(String message) {
        super(message);
    }


}
