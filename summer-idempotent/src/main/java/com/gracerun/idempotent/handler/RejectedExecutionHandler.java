
package com.gracerun.idempotent.handler;

import com.gracerun.idempotent.exception.IdempotentException;

public interface RejectedExecutionHandler {

    default Object rejectedExecution(Object[] args, IdempotentException e) {
        throw e;
    }

}
