package com.gracerun.idempotent.handler;

import com.gracerun.idempotent.exception.IdempotentException;
import com.gracerun.tool.bean.ResponseBean;


public class ResponseSuccess implements RejectedExecutionHandler {
    @Override
    public Object rejectedExecution(Object[] args, IdempotentException e) {
        ResponseBean success = ResponseBean.success();
        success.setMsg("RejectedExecution");
        return success;
    }
}

